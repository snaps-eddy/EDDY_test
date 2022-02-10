package com.snaps.mobile.data.project

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.util.Base64
import com.google.gson.Gson
import com.snaps.common.utils.constant.Config
import com.snaps.common.utils.log.Dlog
import com.snaps.mobile.data.ai.LayoutRecommendPageRequestDto
import com.snaps.mobile.data.ai.LayoutRecommendRequestDto
import com.snaps.mobile.data.ai.LayoutRecommendResponseDto2
import com.snaps.mobile.data.asset.ThumbImageInfo
import com.snaps.mobile.data.save.SaveToJson
import com.snaps.mobile.data.util.NETWORK
import com.snaps.mobile.data.util.applyRetryPolicy
import com.snaps.mobile.domain.asset.AnalysisInfo
import com.snaps.mobile.domain.asset.RecipeImage
import com.snaps.mobile.domain.project.ImageLoadReceipt
import com.snaps.mobile.domain.project.ImageThumbnail
import com.snaps.mobile.domain.project.ProjectOption
import io.reactivex.rxjava3.core.Single
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.BufferedSink
import okio.source
import java.io.File
import java.io.IOException
import javax.inject.Inject

/**
 * Gson 유니코드 파싱 문제 때문에 Gson을 주입 받아서 사용하도록.
 */
//https://velog.io/@dev_thk28/Android-Retrofit2-Multipart%EC%82%AC%EC%9A%A9%ED%95%98%EA%B8%B0-Java
class RemoteProjectDataSource @Inject constructor(
    private val contentResolver: ContentResolver,
    private val projectApi: ProjectApi,
    private val context: Context,
    private val networkErrorLog: NetworkErrorLog,
    private val gson: Gson
) {
    private val tag = RemoteProjectDataSource::class.java.simpleName
    private val isWriteSaveJonFile: Boolean = true
    private val isDevelopVersion = Config.isDevelopVersion()

    fun getNewProjectOption(
        appVer: String,
        osVer: String,
        deviceId: String,
        userNo: String
    ): Single<ProjectOptionDto> {
        return projectApi.createProject(
            appVer = appVer,
            osVer = osVer,
            deviceId = deviceId,
            userNo = userNo
        ).doOnError {
            networkErrorLog.write("createProject", it)
        }
    }

    fun getProjectOption(
        appVer: String,
        osVer: String,
        deviceId: String,
        userNo: String,
        projectCode: String
    ): Single<ProjectOptionDto> {
        return projectApi.getProjectOption(
            appVer = appVer,
            osVer = osVer,
            projectCode = projectCode,
            deviceId = deviceId,
            userNo = userNo
        ).doOnError {
            networkErrorLog.write("getProjectOption", it, hashMapOf("projectCode" to projectCode))
        }.doOnSuccess {
            Dlog.d("Response : $it")
        }
    }

    fun getSave(
        appVer: String,
        osVer: String,
        deviceId: String,
        userNo: String,
        projectCode: String
    ): Single<SaveToJson> {
        return projectApi.getSave(
            appVer = appVer,
            osVer = osVer,
            projectCode = projectCode,
            deviceId = deviceId,
            userNo = userNo
        ).doOnSuccess {
            if (isDevelopVersion && isWriteSaveJonFile) {
                File(context.externalCacheDir, "save.json").writeText(
                    gson.newBuilder().setPrettyPrinting().create().toJson(it)
                )
            }
        }.doOnError {
            networkErrorLog.write("getSave", it, hashMapOf("projectCode" to projectCode))
        }
    }

    private fun createFormData(
        name: String,
        file: File
    ): MultipartBody.Part {
        return MultipartBody.Part.createFormData(
            name = name,
            filename = file.name,
            body = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        )
    }

    private fun createFormData(
        name: String,
        imageUri: String
    ): MultipartBody.Part {
        val filename = Base64.encodeToString(
            imageUri.substring(0, imageUri.length.coerceAtMost(64)).toByteArray(),
            Base64.NO_WRAP or Base64.NO_PADDING
        )
        val body = object : RequestBody() {
            val uri = Uri.parse(imageUri)
            override fun contentType(): MediaType? {
                return contentResolver.getType(uri)?.toMediaType()
            }

            override fun writeTo(sink: BufferedSink) {
                contentResolver.openInputStream(uri)?.use { sink.writeAll(it.source()) } ?: throw IOException("Could not open $uri")
            }
        }
        return MultipartBody.Part.createFormData(
            name = name,
            filename = filename,
            body = body
        )
    }

    private val mediaTypeText = "text/plain".toMediaTypeOrNull()
    private fun String.toReqBody(): RequestBody = toRequestBody(mediaTypeText)
    private fun Int.toReqBody(): RequestBody = toString().toRequestBody(mediaTypeText)

    fun uploadThumbnail(
        appVer: String,
        osVer: String,
        deviceId: String,
        userNo: String,
        projectCode: String,
        recipeImage: RecipeImage,
        thumbImageInfo: ThumbImageInfo,
        enableFaceFinder: Boolean
    ): Single<ImageLoadReceipt> {
        return projectApi.uploadThumbImage(
            appVer = appVer,
            osVer = osVer,
            deviceId = deviceId,
            userNo = userNo,
            projectCode = projectCode,
            file = createFormData("file", thumbImageInfo.file),
            analysisYN = (if (enableFaceFinder) "Y" else "N").toReqBody(),
            orientation = thumbImageInfo.exifInfo.orientation.toReqBody()
        ).compose(
            applyRetryPolicy(NETWORK)
        ).map {
            Dlog.d(tag, "uploadThumbnail(): upload path -> " + it.middleImagePath)

            //스크린 샷 이미지가 가로, 세로가 반대로 저장되는 경우 대응
            val isCorrectRatio = when {
                thumbImageInfo.widthWithoutOt > thumbImageInfo.heightWithoutOt -> recipeImage.width > recipeImage.height
                thumbImageInfo.widthWithoutOt < thumbImageInfo.heightWithoutOt -> recipeImage.width < recipeImage.height
                else -> true
            }

            val analysisInfo = it.analysisInfo?.let { analysisInfo ->
                gson.fromJson(analysisInfo, AnalysisInfo::class.java)
            } ?: AnalysisInfo()

            ImageLoadReceipt(
                year = it.imageYear ?: throw RuntimeException("imageYear is null"),
                imgSeq = it.imageSequence ?: throw RuntimeException("imageSequence is null"),
                orientation = thumbImageInfo.exifInfo.orientation,
                localId = recipeImage.localId,
                width = if (isCorrectRatio) recipeImage.width else recipeImage.height,
                height = if (isCorrectRatio) recipeImage.height else recipeImage.width,
                thumbnailRemotePath = it.middleImagePath ?: throw RuntimeException("middleImagePath is null"),
                analysisInfo = analysisInfo,
                exifData = thumbImageInfo.exifInfo.exifDate
            )
        }.doOnError {
            networkErrorLog.write("uploadThumbImage", it, hashMapOf("projectCode" to projectCode))
        }
    }

    fun uploadOriginalImage(
        appVer: String,
        osVer: String,
        deviceId: String,
        userNo: String,
        projectCode: String,
        imageThumbnail: ImageThumbnail
    ): Single<ImageThumbnail> {
        return projectApi.uploadOriginalImage(
            appVer = appVer,
            osVer = osVer,
            deviceId = deviceId,
            userNo = userNo,
            projectCode = projectCode,
            file = createFormData("file", imageThumbnail.thumbnailUri),
            imageWidth = imageThumbnail.originWidth.toInt().toReqBody(),
            imageHeight = imageThumbnail.originHeight.toInt().toReqBody(),
            imageYear = imageThumbnail.year.toReqBody(),
            imageSequence = imageThumbnail.imgSeq.toReqBody()
        ).map {
            imageThumbnail
        }.compose(
            applyRetryPolicy(NETWORK)
        ).doOnError {
            networkErrorLog.write("uploadOriginalImage", it, hashMapOf("projectCode" to projectCode))
        }
    }

    // Edit after order 이건데...
    fun isAfterOrderEdit(
        appVer: String,
        osVer: String,
        deviceId: String,
        userNo: String,
        projectCode: String
    ) : Single<Boolean> {
        return projectApi.getIsAfterOrderEdit(
            appVer = appVer,
            osVer = osVer,
            deviceId = deviceId,
            userNo = userNo,
            projectCode = projectCode
        ).map {
            it.status?.let { status ->
                status == "500"
            } ?: false
        }
    }


    fun uploadSave(
        appVer: String,
        osVer: String,
        deviceId: String,
        userNo: String,
        projectCode: String,
        thumbnailList: List<ImageThumbnail>,
        projectOption: ProjectOption,
        thumbnailFile: File,
        middleThumbnailFile: File,
        saveFile: File
    ): Single<UploadProjectResponseDto> {
        val imageYearList: MutableList<MultipartBody.Part> = mutableListOf()
        val imageSequenceList: MutableList<MultipartBody.Part> = mutableListOf()
        thumbnailList.forEach { item ->
            imageYearList.add(MultipartBody.Part.createFormData("imageYear", item.year))
            imageSequenceList.add(MultipartBody.Part.createFormData("imageSequence", item.imgSeq))
        }
        Dlog.d(tag, "uploadSave() projectOption\n" + projectOption.toPrettyString())
        return with(projectOption) {
            projectApi.uploadProject(
                appVer = appVer,
                osVer = osVer,
                deviceId = deviceId,
                userNo = userNo,
                projectCode = projectCode,
                productCode = productCode.toReqBody(),
                templateCode = templateCode.toReqBody(),
                projectName = projectName.toReqBody(),
                pageAddCount = pageAddCount.toReqBody(),
                productType = productType.toReqBody(),
                finishStatus = finishStatus.toReqBody(),
                affxName = affxName.toReqBody(),
                glossyType = glossyType.toReqBody(),
                paperCode = paperCode.toReqBody(),
                frameCode = frameCode.toReqBody(),
                frameType = frameType.toReqBody(),
                coatingYN = coatingYN.toReqBody(),
                backType = backType.toReqBody(),
                quantity = quantity.toReqBody(),
                spineNo = spineNo.toReqBody(),
                spineVersion = spineVersion.toReqBody(),
                usePhotoCount = thumbnailList.size.toReqBody(),
                calendarStartDate = calendarStartDate.toReqBody(),
                calendarEndDate = calendarEndDate.toReqBody(),
                saveFile = createFormData("saveFile", saveFile),
                middleThumbnailFile = createFormData("middleThumbnailFile", middleThumbnailFile),
                thumbnailFile = createFormData("thumbnailFile", thumbnailFile),
                imageYearList = imageYearList,
                imageSequenceList = imageSequenceList,
                convertJsonYN = convertJsonYN.toReqBody()
            ).compose(
                applyRetryPolicy(NETWORK)
            ).map {
                Dlog.d(tag, "Upload() Response\n" + it.toPrettyString())
                it
            }.doOnError {
                networkErrorLog.write("uploadProject", it, hashMapOf("projectCode" to projectCode))
            }
        }
    }

    fun getAiTemplate(
        appVer: String,
        osVer: String,
        deviceId: String,
        userNo: String,
        request: LayoutRecommendRequestDto
    ): Single<LayoutRecommendResponseDto2> {
        return projectApi.getAiTemplate(
            appVer = appVer,
            osVer = osVer,
            deviceId = deviceId,
            userNo = userNo,
            jsonString = gson.toJson(request).toRequestBody()
        ).doOnError {
            networkErrorLog.write("getAiTemplate", it)
        }
    }

    fun getAiRecommendLayoutList(
        appVer: String,
        osVer: String,
        deviceId: String,
        userNo: String,
        request: LayoutRecommendPageRequestDto
    ): Single<LayoutRecommendResponseDto2> {
        return projectApi.getAiRecommendLayout(
            appVer = appVer,
            osVer = osVer,
            deviceId = deviceId,
            userNo = userNo,
            jsonString = gson.toJson(request).toRequestBody()
        ).doOnError {
            networkErrorLog.write("getAiRecommendLayout", it)
        }
    }
}