package com.snaps.mobile.data.ai

import com.google.gson.Gson
import com.snaps.common.utils.constant.Config
import com.snaps.mobile.data.SceneMapper
import com.snaps.mobile.data.asset.DeviceDataSource
import com.snaps.mobile.data.template.TemplateDto
import com.snaps.mobile.data.template.TemplateSceneDto
import com.snaps.mobile.data.template.TemplateSceneObjectDto
import com.snaps.mobile.data.util.ExifUtil
import com.snaps.mobile.domain.asset.AnalysisInfo
import com.snaps.mobile.domain.product.ProductInfo
import com.snaps.mobile.domain.project.ImageThumbnail
import com.snaps.mobile.domain.template.ai.LayoutRecommendPage
import com.snaps.mobile.domain.template.ai.LayoutRecommendTemplate
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

/**
 * Gson 유니코드 파싱 문제 때문에 Gson을 주입 받아서 사용하도록.
 */
class AiDataProcess @Inject constructor(
    private val sceneMapper: SceneMapper,
    private val deviceDataSource: DeviceDataSource,
    private val exifUtil: ExifUtil,
    private val aiDataLog: AiDataLog,
    private val gson: Gson
) {
    private val isDevelopVersion = Config.isDevelopVersion()

    private fun getBookSize(productInfo: ProductInfo): Int {
        return when (productInfo.getPhotoBookSize()) {
            ProductInfo.PhotoBookSize.Size5X7 -> 0
            ProductInfo.PhotoBookSize.Size6X6 -> 1
            ProductInfo.PhotoBookSize.Size8X8 -> 2
            ProductInfo.PhotoBookSize.Size8X10 -> 3
            ProductInfo.PhotoBookSize.Size10X10 -> 4
            ProductInfo.PhotoBookSize.SizeA4 -> 5
            ProductInfo.PhotoBookSize.None -> 0
        }
    }

    private enum class ThemeTemplateCode(val themeType: Int, val templateCode: String) {
        ETC(0, "045021028571"),
        TRAVEL(1, "045021028567"),
        COUPLE(2, "045021028568"),
        FAMILY(3, "045021028569"),
        BABY(4, "045021028570"),
    }

    ///////////////////////////////////////

    fun createAiTemplateRequestDto(
        deviceId: String,
        userNo: String,
        projectCode: String,
        language: String,
        productInfo: ProductInfo,
        thumbnailList: List<ImageThumbnail>
    ): LayoutRecommendRequestDto {
        return mapToLayoutRecommendRequestDtoImages(thumbnailList).let {
            LayoutRecommendRequestDto(
                appType = "android",
                bookSize = getBookSize(productInfo),
                rcmdCover = if (productInfo.isLeatherCover()) 0 else 1,
                userNo = userNo.toInt(),
                deviceId = deviceId,
                language = language,
                projCode = projectCode,
                nRcmd = 1,
                transId = SimpleDateFormat("yyyyMMddHHmmss", Locale.KOREA).format(Date()),
                imagesLength = it.size,
                images = it
            )
        }
    }

    private fun mapToLayoutRecommendRequestDtoImages(
        thumbnailList: List<ImageThumbnail>
    ): List<LayoutRecommendRequestDto.Images> {
        val sf = SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.KOREA)
        val imageInfoMap = deviceDataSource.getImageInfos(thumbnailList.map { it.localId })

        return thumbnailList.mapIndexed { index, imageThumbnail ->
            val imageInfo = imageInfoMap[imageThumbnail.localId]
            val localPath = imageInfo?.localPath ?: "/no_name"
            val fileName = localPath.lastIndexOf("/").let { pathIndex ->
                if (pathIndex == -1) "no_name" else localPath.substring(pathIndex + 1)
            }
            val fdThum = with(imageThumbnail.analysisInfo.getFdThum()) {
                LayoutRecommendRequestDto.Images.Fdthum(
                    w = w.toInt(),
                    h = h.toInt(),
                    x = x.toInt(),
                    y = y.toInt(),
                    xw = xw.toInt(),
                    yh = yh.toInt(),
                    fn = fn,
                )
            }
            LayoutRecommendRequestDto.Images(
                index = index,
                uuid = imageThumbnail.outputImageSequence,
                imageKey = imageThumbnail.outputImageSequence,
                imageOriFile = fileName,
                absPath = localPath,
                oripqW = imageThumbnail.originWidth.toInt(),
                oripqH = imageThumbnail.originHeight.toInt(),
                ot = imageThumbnail.orientation,
                exifDate = imageThumbnail.date,
                sysDate = sf.format(Date(imageInfo?.dateAdded ?: System.currentTimeMillis())),
                gps = exifUtil.getInfo(imageThumbnail.thumbnailUri).gps,
                snapsImageThumbFile = imageThumbnail.thumbnailRemotePath,
                fd_thum = fdThum
            )
        }
    }

    fun createLayoutRecommendTemplate(
        response: LayoutRecommendResponseDto
    ): LayoutRecommendTemplate {
        if (isDevelopVersion) aiDataLog.writeAiResponse("book", response)

        return mapToSceneDtoIncludeImageKeysList(response).let { list ->
            LayoutRecommendTemplate(
                bookTitle = response.bookTitle ?: "",
                themeType = response.themeType,
                templateCode = getThemeTemplateCode(response).templateCode,
                imageKeyList = list.map { it.imageKeyList }.toList().flatten(),
                template = sceneMapper.mapToModel(
                    TemplateDto(
                        list.map { it.tmplateSceneDto }.toList()
                    )
                ),
            )
        }
    }

    private fun getThemeTemplateCode(
        response: LayoutRecommendResponseDto
    ) = enumValues<ThemeTemplateCode>().find { it.themeType == response.themeType }?.let { it } ?: ThemeTemplateCode.ETC

    private fun mapMultiformToSceneDtoIncludeImageKeysList(
        response: LayoutRecommendResponseDto
    ): List<SceneDtoIncludeImageKeys> {
        return response.scene?.mapNotNull { scene ->
            scene.multiform?.let { parsingSceneMultiform(it) }
        }?.toList() ?: listOf()
    }

    private fun mapPagesToSceneDtoIncludeImageKeysList(
        response: LayoutRecommendResponseDto
    ): List<SceneDtoIncludeImageKeys> {
        return response.scene?.mapNotNull { scene ->
            scene.pages?.mapNotNull { parsingScenePage(it).firstOrNull() } ?: listOf()
        }?.flatten() ?: listOf()
    }

    private fun createBlankScene(
        list: List<SceneDtoIncludeImageKeys>
    ): SceneDtoIncludeImageKeys {
        return list.last().run {
            copy(
                tmplateSceneDto = tmplateSceneDto.copy(
                    type = "page",
                    subType = "blank",
                    templateCode = "",
                    layoutType = "smart",
                    sceneObjects = listOf()
                ),
                imageKeyList = listOf()
            )
        }
    }

    private fun mapToSceneDtoIncludeImageKeysList(
        response: LayoutRecommendResponseDto
    ): List<SceneDtoIncludeImageKeys> {
        return mutableListOf<SceneDtoIncludeImageKeys>().apply {
            addAll(mapMultiformToSceneDtoIncludeImageKeysList(response))
            addAll(mapPagesToSceneDtoIncludeImageKeysList(response))

            //추후 AI에서 blank 페이지를 내려줄 경우 대비
            find { it.tmplateSceneDto.subType == "blank" } ?: run {
                add(1, createBlankScene(this))
            }

            if (isDevelopVersion) {
                aiDataLog.writeAiResponseKeyList("book", map { it.imageKeyList }.toList())
            }
        }
    }

    fun createLayoutRecommendPageList(
        response: LayoutRecommendResponseDto
    ): List<LayoutRecommendPage> {
        if (isDevelopVersion) aiDataLog.writeAiResponse("page", response)

        return response.scene?.map { scene ->
            scene.pages?.map { page ->
                parsingScenePage(page).map {
                    LayoutRecommendPage(
                        scene = TemplateDto(listOf(it.tmplateSceneDto)).let { templateDto ->
                            sceneMapper.mapToModel(templateDto).scenes.first()
                        },
                        imageKeyList = it.imageKeyList,
                        analysisInfoMap = createAnalysisInfos(page.images)
                    )
                }
            }?.flatten() ?: listOf()
        }?.flatten() ?: listOf()
    }

    private fun createAnalysisInfos(
        imageList: List<LayoutRecommendResponseDto.Scene.Pages.Images>?
    ): Map<String, AnalysisInfo> {
        return imageList?.mapNotNull { image ->
            image.imageKey?.let {
                createAnalysisInfo(image)?.let {
                    Pair(image.imageKey, it)
                }
            }
        }?.map {
            it.first to it.second
        }?.toMap() ?: hashMapOf()
    }

    private fun createAnalysisInfo(
        image: LayoutRecommendResponseDto.Scene.Pages.Images
    ): AnalysisInfo? {
        return image.fd_thum?.let {
            AnalysisInfo(
                AnalysisInfo.Img(
                    fd_thum = AnalysisInfo.Img.FdThum(
                        fn = it.fn,
                        h = it.h,
                        w = it.w,
                        x = it.x,
                        xw = it.xw,
                        y = it.y,
                        yh = it.yh
                    ),
                    meta = AnalysisInfo.Img.Meta(
                        dt = "",
                        ot = image.ot,
                        w = image.oripqW,
                        h = image.oripqH,
                        th = image.thumbH,
                        tw = image.thumbW
                    )
                )
            )
        }
    }

    private data class SceneDtoIncludeImageKeys(
        val tmplateSceneDto: TemplateSceneDto,
        val imageKeyList: List<String>
    )

    private data class BackgroundImageInfo(
        val code: String = "",
        val url: String = "",
    )

    private fun mapToBackgroundImageInfoList(
        multiform: LayoutRecommendResponseDto.Scene.Multiform
    ): List<BackgroundImageInfo> {
        return multiform.backgrounds?.mapNotNull { background ->
            if (background.url != null && background.code != null) {
                BackgroundImageInfo(code = background.code, url = background.url)
            } else {
                null
            }
        } ?: listOf()
    }

    private fun mapToTemplateSceneObjectDtoList(
        multiform: LayoutRecommendResponseDto.Scene.Multiform,
        templateSceneDto: TemplateSceneDto
    ): List<TemplateSceneObjectDto> {
        return mutableListOf<TemplateSceneObjectDto>().apply {
            add(
                createBackgroundObject(
                    index = 0,
                    width = templateSceneDto.width,
                    height = templateSceneDto.height,
                    backgroundImageInfoList = mapToBackgroundImageInfoList(multiform)
                )
            )
            addAll(templateSceneDto.sceneObjects)
        }
    }

    private fun parsingSceneMultiform(
        multiform: LayoutRecommendResponseDto.Scene.Multiform
    ): SceneDtoIncludeImageKeys? {
        return multiform.layouts?.firstOrNull()?.let { layout ->
            gson.fromJson(layout.data, TemplateSceneDto::class.java).let { templateSceneDto ->
                SceneDtoIncludeImageKeys(
                    tmplateSceneDto = templateSceneDto.copy(
                        layoutType = "idea",
                        layoutCode = layout.code ?: "", //TODO:: 이 값을 넣는것이 맞는지 확인 필요
                        sceneObjects = mapToTemplateSceneObjectDtoList(multiform, templateSceneDto)
                    ),
                    imageKeyList = layout.order ?: listOf()
                )
            }
        }
    }

    private fun mapToBackgroundImageInfoList(
        page: LayoutRecommendResponseDto.Scene.Pages
    ): List<BackgroundImageInfo> {
        return page.backgrounds?.mapNotNull { background ->
            if (background.url != null && background.code != null) {
                BackgroundImageInfo(code = background.code, url = background.url)
            } else {
                null
            }
        } ?: listOf()
    }

    private fun mapToTemplateSceneObjectDtoList(
        layoutIndex: Int,
        page: LayoutRecommendResponseDto.Scene.Pages,
        templateSceneDto: TemplateSceneDto
    ): List<TemplateSceneObjectDto> {
        return mutableListOf<TemplateSceneObjectDto>().apply {
            add(
                createBackgroundObject(
                    index = layoutIndex,
                    width = templateSceneDto.width,
                    height = templateSceneDto.height,
                    backgroundImageInfoList = mapToBackgroundImageInfoList(page)
                )
            )
            addAll(templateSceneDto.sceneObjects)
        }
    }

    private fun parsingScenePage(
        page: LayoutRecommendResponseDto.Scene.Pages
    ): List<SceneDtoIncludeImageKeys> {
        return page.layouts?.mapIndexed { layoutIndex, layout ->
            gson.fromJson(layout.data, TemplateSceneDto::class.java).let { templateSceneDto ->
                SceneDtoIncludeImageKeys(
                    tmplateSceneDto = templateSceneDto.copy(
                        layoutType = "smart",
                        layoutCode = layout.code ?: "",  //TODO:: 이 값을 넣는것이 맞는지 확인 필요
                        sceneObjects = mapToTemplateSceneObjectDtoList(layoutIndex, page, templateSceneDto)
                    ),
                    imageKeyList = layout.order ?: listOf()
                )
            }
        } ?: listOf()
    }

    private fun createBackgroundObject(
        index: Int,
        width: Float,
        height: Float,
        backgroundImageInfoList: List<BackgroundImageInfo>
    ): TemplateSceneObjectDto {
        return when {
            backgroundImageInfoList.isEmpty() -> null
            index < backgroundImageInfoList.size -> backgroundImageInfoList[index]
            else -> backgroundImageInfoList[backgroundImageInfoList.size - 1]
        }.let { backgroundImageInfo ->
            when (backgroundImageInfo) {
                null -> createBackgroundColorObject(
                    width = width,
                    height = height,
                )
                else -> createBackgroundImageObject(
                    width = width,
                    height = height,
                    code = backgroundImageInfo.code,
                    middleImagePath = backgroundImageInfo.url,
                )
            }
        }
    }

    private fun createBackgroundColorObject(
        width: Float,
        height: Float
    ): TemplateSceneObjectDto {
        return TemplateSceneObjectDto(
            type = "background",
            subType = "color",
            name = "",
            source = "",
            x = 0.0f,
            y = 0.0f,
            width = width,
            height = height,
            angle = 0,
            alpha = 1.0f,
            overPrint = false,
            whitePrint = false,
            bgColor = "#ffffff",
            middleImagePath = "",
        )
    }

    private fun createBackgroundImageObject(
        width: Float,
        height: Float,
        code: String,
        middleImagePath: String
    ): TemplateSceneObjectDto {
        return TemplateSceneObjectDto(
            type = "background",
            subType = "image",
            name = "",
            x = 0.0f,
            y = 0.0f,
            width = width,
            height = height,
            angle = 0,
            alpha = 1.0f,
            overPrint = false,
            whitePrint = false,
            bgColor = "",
//            usedType = "resource",
            source = "template",
            resourceId = code,
            middleImagePath = middleImagePath,
        )
    }


    private fun mapToImagesFdthum(analysisInfo: AnalysisInfo): LayoutRecommendPageRequestDto.Images.Fdthum? {
        return if (analysisInfo.isAnalysis) {
            with(analysisInfo.getFdThum()) {
                LayoutRecommendPageRequestDto.Images.Fdthum(
                    w = w.toInt(),
                    h = h.toInt(),
                    x = x.toInt(),
                    y = y.toInt(),
                    xw = xw.toInt(),
                    yh = yh.toInt(),
                    fn = fn,
                )
            }
        } else {
            null
        }
    }

    private fun mapToLayoutRecommendLayoutRequestDtoImages(
        thumbnailList: List<ImageThumbnail>
    ): List<LayoutRecommendPageRequestDto.Images> {
        val sysDate = SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.KOREA).format(Date(System.currentTimeMillis()))

        return thumbnailList.mapIndexed { index, imageThumbnail ->
            LayoutRecommendPageRequestDto.Images(
                index = index,
                uuid = imageThumbnail.outputImageSequence,
                imageKey = imageThumbnail.outputImageSequence,
                imageOriFile = "no_name",
                absPath = "/no_name",
                oripqW = imageThumbnail.originWidth.toInt(),
                oripqH = imageThumbnail.originHeight.toInt(),
                ot = imageThumbnail.orientation,
                exifDate = imageThumbnail.date,
                sysDate = sysDate,
                gps = null,
                snapsImageThumbFile = imageThumbnail.thumbnailRemotePath,
                fd_thum = mapToImagesFdthum(imageThumbnail.analysisInfo),
            )
        }
    }

    fun createAiRecommendLayoutRequestDto(
        language: String,
        productInfo: ProductInfo,
        deviceId: String,
        userNo: String,
        projectCode: String,
        recommendCount: Int,
        thumbnailList: List<ImageThumbnail>
    ): LayoutRecommendPageRequestDto {
        return mapToLayoutRecommendLayoutRequestDtoImages(thumbnailList).let {
            LayoutRecommendPageRequestDto(
                appType = "android",
                bookSize = getBookSize(productInfo),
                userNo = userNo.toInt(),
                deviceId = deviceId,
                language = language,
                projCode = projectCode,
                nRcmd = recommendCount,
                transId = SimpleDateFormat("yyyyMMddHHmmss", Locale.KOREA).format(Date()),
                imagesLength = it.size,
                images = it
            )
        }
    }
}