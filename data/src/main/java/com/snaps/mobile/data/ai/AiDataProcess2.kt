package com.snaps.mobile.data.ai

import android.graphics.Color
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.snaps.common.utils.constant.Config
import com.snaps.common.utils.log.Dlog
import com.snaps.mobile.data.SceneMapper
import com.snaps.mobile.data.asset.DeviceDataSource
import com.snaps.mobile.data.template.TemplateDto
import com.snaps.mobile.data.template.TemplateSceneDto
import com.snaps.mobile.data.template.TemplateSceneObjectDto
import com.snaps.mobile.data.util.ExifUtil
import com.snaps.mobile.domain.asset.AnalysisInfo
import com.snaps.mobile.domain.error.SnapsThrowable
import com.snaps.mobile.domain.product.ProductInfo
import com.snaps.mobile.domain.project.ImageThumbnail
import com.snaps.mobile.domain.save.Scene
import com.snaps.mobile.domain.template.TemplateSceneObject
import com.snaps.mobile.domain.template.ai.LayoutRecommendPage
import com.snaps.mobile.domain.template.ai.LayoutRecommendTemplate
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * Gson 유니코드 파싱 문제 때문에 Gson을 주입 받아서 사용하도록.
 */
class AiDataProcess2 @Inject constructor(
    private val sceneMapper: SceneMapper,
    private val deviceDataSource: DeviceDataSource,
    private val exifUtil: ExifUtil,
    private val aiDataLog: AiDataLog2,
    private val gson: Gson
) {
    private val tag = AiDataProcess2::class.java.simpleName
    private val isDevelopVersion = Config.isDevelopVersion()

    companion object {
        const val SCENE_TYPE_COVER = "cover"
        const val LAYOUT_TYPE_SMART = "smart"
        const val LAYOUT_TYPE_IDEA = "idea"
        const val SCENE_OBJECT_TYPE_IMAGE = "image"
        const val SCENE_OBJECT_TYPE_TEXT = "text"
        const val SCENE_OBJECT_TYPE_BACKGROUND = "background"
        const val SCENE_OBJECT_SUBTYPE_IMAGE = "image"
        const val SCENE_OBJECT_SUBTYPE_COLOR = "color"
    }

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

    private fun convertAIExifDateFormat(exifDate: String): String {
        if (exifDate.isEmpty()) return exifDate
        return try {
            SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.KOREA).parse(exifDate)?.let {
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA).format(it)
            } ?: exifDate
        } catch (e: Exception) {
            exifDate
        }
    }

    private data class SceneDtoIncludeImageKeys(
        val tmplateSceneDto: TemplateSceneDto,
        val imageKeyList: List<String>
    )

    ///////////////////////////////////////

    fun createRequestPhotoBook(
        deviceId: String,
        userNo: String,
        projectCode: String,
        language: String,
        productInfo: ProductInfo,
        thumbnailList: List<ImageThumbnail>
    ): LayoutRecommendRequestDto {
        return createRequestPhotoBookImages(thumbnailList).let {
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

    private fun createRequestPhotoBookImages(
        thumbnailList: List<ImageThumbnail>
    ): List<LayoutRecommendRequestDto.Images> {
        val imageInfoMap = deviceDataSource.rxGetImageInfos(thumbnailList.map { it.localId })

        return thumbnailList.mapIndexed { index, imageThumbnail ->
            val (localPath, timeStamp) = imageInfoMap[imageThumbnail.localId]?.let {
                it.localPath to it.dateAdded
            } ?: run {
                "/no_name" to System.currentTimeMillis()
            }
            val fdThum = imageThumbnail.analysisInfo.getFdThum().let {
                LayoutRecommendRequestDto.Images.Fdthum(
                    w = it.w.toInt(),
                    h = it.h.toInt(),
                    x = it.x.toInt(),
                    y = it.y.toInt(),
                    xw = it.xw.toInt(),
                    yh = it.yh.toInt(),
                    fn = it.fn,
                )
            }
            LayoutRecommendRequestDto.Images(
                index = index,
                uuid = imageThumbnail.outputImageSequence,
                imageKey = imageThumbnail.outputImageSequence,
                imageOriFile = localPath.substringAfterLast("/", "no_name"),
                absPath = localPath,
                oripqW = imageThumbnail.originWidth.toInt(),
                oripqH = imageThumbnail.originHeight.toInt(),
                ot = imageThumbnail.orientation,
                exifDate = convertAIExifDateFormat(imageThumbnail.date),
                sysDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA).format(Date(timeStamp)),
                gps = exifUtil.getInfo(imageThumbnail.thumbnailUri).gps,
                snapsImageThumbFile = imageThumbnail.thumbnailRemotePath,
                fd_thum = fdThum
            )
        }
    }

    private enum class ThemeTemplateCode(val themeType: Int, val templateCode: String) {
        ETC(0, "045021028571"),
        TRAVEL(1, "045021028567"),
        COUPLE(2, "045021028568"),
        FAMILY(3, "045021028569"),
        BABY(4, "045021028570"),
    }

    fun createPhotoBookTemplate(
        productInfo: ProductInfo,
        thumbnailList: List<ImageThumbnail>,
        response: LayoutRecommendResponseDto2
    ): LayoutRecommendTemplate {
        if (isDevelopVersion) aiDataLog.writeAiResponse("book", response)

        val templateCode = enumValues<ThemeTemplateCode>().find {
            it.themeType == response.themeType
        }?.templateCode ?: ThemeTemplateCode.ETC.templateCode

        return createTemplateSceneDtoWithImageKeysList(response).let { list ->
            LayoutRecommendTemplate(
                bookTitle = response.bookTitle ?: "",
                themeType = response.themeType,
                templateCode = templateCode,
                imageKeyList = list.flatMap { it.imageKeyList },
                template = sceneMapper.mapToModel(TemplateDto(list.map { it.tmplateSceneDto })),
            )
        }.also { layoutRecommendTemplate ->
            if (isDevelopVersion) {
                aiDataLog.logLayoutRecommendTemplate(
                    tag = tag,
                    thumbnailList = thumbnailList,
                    layoutRecommendTemplate = layoutRecommendTemplate
                )
            }
            checkErrorLayoutRecommendTemplate(
                productInfo = productInfo,
                layoutRecommendTemplate = layoutRecommendTemplate
            )
        }
    }

    //유효성 검사들
    //서버 데이터가 이상하게 오는 경우가 있음
    private fun checkErrorLayoutRecommendTemplate(
        productInfo: ProductInfo,
        layoutRecommendTemplate: LayoutRecommendTemplate
    ) {
        with(layoutRecommendTemplate.template) {
            //cover page 검사
            if (scenes.count { it.type is Scene.Type.Cover } != 1) {
                throw SnapsThrowable("The template has no cover.")
            }
            scenes.find { it.type is Scene.Type.Cover }?.let {
                val imageObjectList = it.sceneObjects.filterIsInstance<TemplateSceneObject.Image>()
                if (imageObjectList.size != 1) {
                    throw SnapsThrowable("The number of image objects on the cover is not 1.")
                }
                imageObjectList.forEach { imgObj ->
                    if (imgObj.width < 1 || imgObj.height < 1) {
                        throw SnapsThrowable("The image object size is not valid.")
                    }
                }
            }

            //blank 페이지 검사
            if (scenes.count { it.type is Scene.Type.Page && it.subType is Scene.SubType.Blank } != 1) {
                throw SnapsThrowable("The template has no blank page.")
            }
            scenes.find { it.type is Scene.Type.Page && it.subType is Scene.SubType.Blank }?.let {
                if (it.sceneObjects.filterIsInstance<TemplateSceneObject.Image>().count() != 0) {
                    throw SnapsThrowable("The blank page has an image object.")
                }
            }

            //내지 페이지에 이미지 오브젝트가 정상인지 검사
            scenes.filter { it.type is Scene.Type.Page && it.subType is Scene.SubType.Page }.forEach {
                val imageObjectList = it.sceneObjects.filterIsInstance<TemplateSceneObject.Image>()
                if (imageObjectList.isEmpty()) {
                    throw SnapsThrowable("There is no image object on the inner page.")
                }
                imageObjectList.forEach { imgObj ->
                    if (imgObj.width < 1 || imgObj.height < 1) {
                        throw SnapsThrowable("The image object size is not valid.")
                    }
                }
            }

            //최소, 최대 페이지 검사
            scenes.count { it.type is Scene.Type.Page && it.subType is Scene.SubType.Page }.let {
                val min = productInfo.baseQuantity * 2 + 1
                val max = productInfo.maxQuantity * 2 + 1
                if (it < min) throw SnapsThrowable("Less than the minimum page. [${it}]")
                else if (it > max) throw SnapsThrowable("Maximum pages exceeded. [${it}]")
            }

            //추천 이미지 개수와 이미지 오브젝트 갯수가 일치하는지 검사
            val totalObjectImageCount = scenes.fold(0) { total, scenes ->
                total + scenes.sceneObjects.count { it.type == SCENE_OBJECT_TYPE_IMAGE }
            }
            if (totalObjectImageCount == 0) {
                throw SnapsThrowable("There is no image object in the template.")
            } else if (totalObjectImageCount != layoutRecommendTemplate.imageKeyList.size) {
                throw SnapsThrowable("Image object and image key do not match.")
            }
        }
    }

    private fun createTemplateSceneDtoWithImageKeysList(
        response: LayoutRecommendResponseDto2
    ): List<SceneDtoIncludeImageKeys> {
        return response.scene?.flatMap { scene ->
            scene.pages?.map { page ->
                createTemplateSceneDtoWithImageKeys(scene, page)
            } ?: throw SnapsThrowable("scene.pages is null or empty --> scene.index:${scene.index}")
        } ?: listOf()
    }

    private fun createTemplateSceneDtoWithImageKeys(
        scene: LayoutRecommendResponseDto2.Scene,
        page: LayoutRecommendResponseDto2.Scene.Pages
    ): SceneDtoIncludeImageKeys {
        return page.multiform?.let { multiformList ->
            if (multiformList.isEmpty()) throw SnapsThrowable("multiform is emepty --> scene.index:${scene.index}")

            //TODO::땜방!! 아놔!!
            val layoutType = scene.layoutType?.let { layoutType ->
                page.type?.let { type ->
                    if (layoutType == "blank" && type == "right") "page" else layoutType
                }
            }

            try {
                gson.fromJson(multiformList.first().data, TemplateSceneDto::class.java).copy(
                    type = scene.type ?: throw SnapsThrowable("scene.type is null --> scene.index:${scene.index}"),
                    subType = layoutType
                        ?: throw SnapsThrowable("scene.layoutType is null --> scene.index:${scene.index}"),
                    layoutType = if (scene.type == SCENE_TYPE_COVER) LAYOUT_TYPE_IDEA else LAYOUT_TYPE_SMART
                )
            } catch (e: JsonSyntaxException) {
                if (isDevelopVersion) aiDataLog.logPasringMultiform(tag, scene.index, multiformList.first(), e)
                throw e
            }
        }?.let {
            if (it.sceneObjects.count { obj -> obj.type == SCENE_OBJECT_TYPE_BACKGROUND } != 1) {
                throw SnapsThrowable("multiform does not have a background object --> scene.index:${scene.index}")
            }
            SceneDtoIncludeImageKeys(
                tmplateSceneDto = mergeImageAndTextAndBackground(scene.index, it, page),
                imageKeyList = page.layouts?.firstOrNull()?.order ?: listOf()
            )
        } ?: throw SnapsThrowable("multiform is null --> scene.index:${scene.index}")
    }


    private fun mergeImageAndTextAndBackground(
        sceneIndex: Int,
        baseTemplateSceneDto: TemplateSceneDto,
        page: LayoutRecommendResponseDto2.Scene.Pages
    ): TemplateSceneDto {
        var workTemplateSceneDto = baseTemplateSceneDto.copy()
        page.layouts?.let { layoutList ->
            layoutList.firstOrNull()?.id?.let { layoutId ->
                val jsonContent = layoutList.first().data
                if (!jsonContent.isNullOrEmpty()) {
                    workTemplateSceneDto = mergeImageAndText(
                        sceneIndex = sceneIndex,
                        templateSceneDto = workTemplateSceneDto,
                        layoutId = layoutId,
                        layoutTemplateSceneDto = gson.fromJson(jsonContent, TemplateSceneDto::class.java)
                    )
                }
            } ?: Dlog.w(tag, "page.layouts is null or empty --> scene.index:${sceneIndex}")
        } ?: Dlog.w(tag, "page.layouts is null --> scene.index:${sceneIndex}")

        page.backgrounds?.let { backgroundList ->
            backgroundList.firstOrNull()?.let {
                workTemplateSceneDto = mergeBackground(
                    sceneIndex = sceneIndex,
                    templateSceneDto = workTemplateSceneDto,
                    background = it
                )
            } ?: Dlog.w(tag, "page.backgrounds is null or empty --> scene.index:${sceneIndex}")
        } ?: Dlog.w(tag, "page.backgrounds is null --> scene.index:${sceneIndex}")

        return workTemplateSceneDto
    }

    private fun mergeImageAndText(
        sceneIndex: Int,
        templateSceneDto: TemplateSceneDto,
        layoutId: String,
        layoutTemplateSceneDto: TemplateSceneDto
    ): TemplateSceneDto {
        val targetSceneObject = listOf(SCENE_OBJECT_TYPE_IMAGE, SCENE_OBJECT_TYPE_TEXT)
        return templateSceneDto.sceneObjects.filterNot { targetSceneObject.contains(it.type) }.let { target ->
            layoutTemplateSceneDto.sceneObjects.filter { targetSceneObject.contains(it.type) }.let { source ->
                if (isDevelopVersion) {
                    Dlog.d(tag, "applyLayouts() remove Count:${templateSceneDto.sceneObjects.size - target.size} --> scene.index:${sceneIndex}")
                    Dlog.d(tag, "applyLayouts() add Count:${source.size}: ${source.map { it.type }} --> scene.index:${sceneIndex}")
                }
                templateSceneDto.copy(
                    layoutCode = layoutId,
                    sceneObjects = target.toMutableList().apply { addAll(source) }
                )
            }
        }
    }

    private fun mergeBackground(
        sceneIndex: Int,
        templateSceneDto: TemplateSceneDto,
        background: LayoutRecommendResponseDto2.Scene.Pages.Backgrounds
    ): TemplateSceneDto {
        return run {
            val orgBgSceneObject = templateSceneDto.sceneObjects
                .firstOrNull { it.type == SCENE_OBJECT_TYPE_BACKGROUND }
                ?: throw SnapsThrowable("background is not exist --> scene.index:${sceneIndex}")

            checkBackground(sceneIndex, background)
            when (orgBgSceneObject.subType) {
                SCENE_OBJECT_SUBTYPE_IMAGE -> mergeBackgroundOrgTypeImage(orgBgSceneObject, background)
                SCENE_OBJECT_SUBTYPE_COLOR -> mergeBackgroundOrgTypeColor(orgBgSceneObject, background)
                else -> throw SnapsThrowable("background subType is not defined:${orgBgSceneObject.subType} --> scene.index:${sceneIndex}")
            }
        }.let {
            templateSceneDto.sceneObjects.filterNot { it.type == SCENE_OBJECT_TYPE_BACKGROUND }.toMutableList().apply {
                add(0, it)
            }
        }.let {
            templateSceneDto.copy(sceneObjects = it)
        }
    }

    private fun checkBackground(
        sceneIndex: Int,
        background: LayoutRecommendResponseDto2.Scene.Pages.Backgrounds
    ) {
        try {
            background.type?.let { bgType ->
                if (bgType == SCENE_OBJECT_SUBTYPE_IMAGE) {
                    if (background.id.isNullOrEmpty()) throw SnapsThrowable("background.id is null or empty --> scene.index:${sceneIndex}")
                    if (background.url.isNullOrEmpty()) throw SnapsThrowable("background.url is null or empty --> scene.index:${sceneIndex}")
                } else if (bgType == SCENE_OBJECT_SUBTYPE_IMAGE) {
                    if (background.bgColor.isNullOrEmpty()) throw SnapsThrowable("background.bgColor is null or empty --> scene.index:${sceneIndex}")
                    if (!background.bgColor.startsWith("#")) throw SnapsThrowable("background.bgColor is invalid:${background.bgColor} --> scene.index:${sceneIndex}")
                    Color.parseColor(background.bgColor)
                }
            }
        } catch (e: Exception) {
            Dlog.e(tag, background.toString())
            throw e
        }
    }

    private fun mergeBackgroundOrgTypeImage(
        bgSceneObject: TemplateSceneObjectDto,
        background: LayoutRecommendResponseDto2.Scene.Pages.Backgrounds
    ): TemplateSceneObjectDto {
        return background.type?.let { bgType ->
            when (bgType) {
                SCENE_OBJECT_SUBTYPE_IMAGE -> bgSceneObject.copy(
                    resourceId = background.id ?: throw SnapsThrowable("background.id is null"),
                    middleImagePath = background.url ?: throw SnapsThrowable("background.url is null")
                )
                SCENE_OBJECT_SUBTYPE_COLOR -> bgSceneObject.copy(
                    subType = SCENE_OBJECT_SUBTYPE_COLOR,
                    bgColor = background.bgColor ?: throw SnapsThrowable("background.bgColor is null"),
                    resourceId = "",
                    middleImagePath = ""
                )
                else -> throw SnapsThrowable("background.type is invalid:$bgType")
            }
        } ?: throw SnapsThrowable("background.type is null")
    }

    private fun mergeBackgroundOrgTypeColor(
        bgSceneObject: TemplateSceneObjectDto,
        background: LayoutRecommendResponseDto2.Scene.Pages.Backgrounds
    ): TemplateSceneObjectDto {
        return background.type?.let { bgType ->
            when (bgType) {
                SCENE_OBJECT_SUBTYPE_IMAGE -> bgSceneObject.copy(
                    subType = SCENE_OBJECT_SUBTYPE_IMAGE,
                    bgColor = "",
                    resourceId = background.id ?: throw SnapsThrowable("background.id is null"),
                    middleImagePath = background.url ?: throw SnapsThrowable("background.url is null"),
                )
                SCENE_OBJECT_SUBTYPE_COLOR -> bgSceneObject.copy(
                    bgColor = background.bgColor ?: throw SnapsThrowable("background.bgColor is null"),
                )
                else -> throw SnapsThrowable("background.type is invalid:$bgType")
            }
        } ?: throw SnapsThrowable("background.type is null")
    }

//////////////////////////////////////////////////////////////////////

    fun createRequestRecommendLayouts(
        language: String,
        productInfo: ProductInfo,
        deviceId: String,
        userNo: String,
        projectCode: String,
        recommendCount: Int,
        thumbnailList: List<ImageThumbnail>
    ): LayoutRecommendPageRequestDto {
        return createRequestRecommendLayoutsImages(thumbnailList).let {
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

    private fun createRequestRecommendLayoutsImages(
        thumbnailList: List<ImageThumbnail>
    ): List<LayoutRecommendPageRequestDto.Images> {
        val sysDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA).format(Date(System.currentTimeMillis()))

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
                exifDate = convertAIExifDateFormat(imageThumbnail.date),
                sysDate = sysDate,
                gps = null,
                snapsImageThumbFile = imageThumbnail.thumbnailRemotePath,
                fd_thum = createImagesFdthum(imageThumbnail.analysisInfo),
            )
        }
    }

    private fun createImagesFdthum(analysisInfo: AnalysisInfo): LayoutRecommendPageRequestDto.Images.Fdthum? {
        if (!analysisInfo.isAnalysis) return null

        return with(analysisInfo.getFdThum()) {
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
    }

    fun createRecommendLayoutList(
        response: LayoutRecommendResponseDto2
    ): List<LayoutRecommendPage> {
        if (isDevelopVersion) aiDataLog.writeAiResponse("page", response)

        return response.scene?.firstOrNull()?.let { scene ->
            scene.pages?.firstOrNull()?.let { page ->
                createLayoutRecommendPage(page)
            } ?: run {
                Dlog.w(tag, "scene is null or empty")
                listOf()
            }
        } ?: run {
            Dlog.w(tag, "scene.pages is null or empty")
            listOf()
        }
    }

    private fun createLayoutRecommendPage(
        page: LayoutRecommendResponseDto2.Scene.Pages
    ): List<LayoutRecommendPage> {
        return page.layouts?.mapIndexed { index, layout ->
            if (layout.order.isNullOrEmpty()) throw SnapsThrowable("layout.order is null or empty")
            if (layout.id.isNullOrEmpty()) throw SnapsThrowable("layout.id is null or empty")
            LayoutRecommendPage(
                imageKeyList = layout.order,
                analysisInfoMap = createAnalysisInfos(page.images),
                scene = gson.fromJson(layout.data, TemplateSceneDto::class.java).copy(layoutCode = layout.id).let {
                    TemplateDto(listOf(it))
                }.let {
                    sceneMapper.mapToModel(it).scenes.first()
                }
            ).also { layoutRecommendPage ->
                if (isDevelopVersion) {
                    layoutRecommendPage.scene.sceneObjects.filterIsInstance<TemplateSceneObject.Image>().count().let { count ->
                        Dlog.d(tag, "LayoutRecommendPage [$index] image frame count: $count")
                    }
                    Dlog.d(tag, "LayoutRecommendPage [$index] imageKeyList: ${layoutRecommendPage.imageKeyList}")
                }
            }
        } ?: listOf()
    }

    private fun createAnalysisInfos(
        imageList: List<LayoutRecommendResponseDto2.Scene.Pages.Images>?
    ): Map<String, AnalysisInfo> {
        return imageList?.mapNotNull { image ->
            image.imageKey?.let { imageKey ->
                createAnalysisInfo(image)?.let { analysisInfo -> imageKey to analysisInfo }
            }
        }?.toMap() ?: hashMapOf()
    }

    private fun createAnalysisInfo(
        image: LayoutRecommendResponseDto2.Scene.Pages.Images
    ): AnalysisInfo? {
        return image.fd_thum?.let {
            val fdThum = AnalysisInfo.Img.FdThum(
                fn = image.fd_thum.fn,
                h = it.h,
                w = it.w,
                x = it.x,
                xw = it.xw,
                y = it.y,
                yh = it.yh
            )
            val meta = AnalysisInfo.Img.Meta(
                dt = "",
                ot = image.ot,
                w = image.oripqW,
                h = image.oripqH,
                th = image.thumbH,
                tw = image.thumbW
            )
            AnalysisInfo(img = AnalysisInfo.Img(fd_thum = fdThum, meta = meta))
        }
    }
}