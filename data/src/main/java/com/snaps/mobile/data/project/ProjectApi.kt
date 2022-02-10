package com.snaps.mobile.data.project

import com.snaps.mobile.data.ai.LayoutRecommendResponseDto
import com.snaps.mobile.data.ai.LayoutRecommendResponseDto2
import com.snaps.mobile.data.save.SaveToJson
import io.reactivex.rxjava3.core.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ProjectApi {

    @Headers(
        "X-SNAPS-CHANNEL: ANDROID",
        "X-SNAPS-VERSION: 1",
        "X-SNAPS-OS-VERSION: 1",
        "X-SNAPS-DEVICE: 1",
        "X-SNAPS-DEVICE-TOKEN: 1",
        "X-SNAPS-DEVICE-UUID: 1"
    )
    @POST("v1/project")
    fun createProject(
        @Header("X-SNAPS-VERSION") appVer: String,
        @Header("X-SNAPS-OS-VERSION") osVer: String,
        @Header("X-SNAPS-DEVICE-UUID") deviceId: String,
        @Header("X-SNAPS-USER-NO") userNo: String,
    ): Single<ProjectOptionDto>

    @Headers(
        "X-SNAPS-CHANNEL: ANDROID",
        "X-SNAPS-VERSION: 1",
        "X-SNAPS-OS-VERSION: 1",
        "X-SNAPS-DEVICE: 1",
        "X-SNAPS-DEVICE-TOKEN: 1"
    )
    @GET("v1/project/{projectCode}")
    fun getProjectOption(
        @Path("projectCode") projectCode: String,
        @Header("X-SNAPS-VERSION") appVer: String,
        @Header("X-SNAPS-OS-VERSION") osVer: String,
        @Header("X-SNAPS-DEVICE-UUID") deviceId: String,
        @Header("X-SNAPS-USER-NO") userNo: String,
    ): Single<ProjectOptionDto>

    @Headers(
        "X-SNAPS-CHANNEL: ANDROID",
        "X-SNAPS-VERSION: 1",
        "X-SNAPS-OS-VERSION: 1",
        "X-SNAPS-DEVICE: 1",
        "X-SNAPS-DEVICE-TOKEN: 1"
    )
    @GET("v1/project/{projectCode}/json")
    fun getSave(
        @Path("projectCode") projectCode: String,
        @Header("X-SNAPS-VERSION") appVer: String,
        @Header("X-SNAPS-OS-VERSION") osVer: String,
        @Header("X-SNAPS-DEVICE-UUID") deviceId: String,
        @Header("X-SNAPS-USER-NO") userNo: String,
    ): Single<SaveToJson>

    @Headers(
        "X-SNAPS-CHANNEL: ANDROID",
        "X-SNAPS-VERSION: 1",
        "X-SNAPS-OS-VERSION: 1",
        "X-SNAPS-DEVICE: 1",
        "X-SNAPS-DEVICE-TOKEN: 1"
    )
    @Multipart
    @POST("v1/project/{projectCode}/thumbnailFile")
    fun uploadThumbImage(
        @Path("projectCode") projectCode: String,
        @Header("X-SNAPS-VERSION") appVer: String,
        @Header("X-SNAPS-OS-VERSION") osVer: String,
        @Header("X-SNAPS-DEVICE-UUID") deviceId: String,
        @Header("X-SNAPS-USER-NO") userNo: String,
        @Part file: MultipartBody.Part,
        @Part("analysisYN") analysisYN: RequestBody,
        @Part("orientation") orientation: RequestBody
    ): Single<UploadThumbImageResponseDto>


    @Headers(
        "X-SNAPS-CHANNEL: ANDROID",
        "X-SNAPS-VERSION: 1",
        "X-SNAPS-OS-VERSION: 1",
        "X-SNAPS-DEVICE: 1",
        "X-SNAPS-DEVICE-TOKEN: 1"
    )
    @Multipart
    @POST("v1/project/{projectCode}/originalFile")
    fun uploadOriginalImage(
        @Path("projectCode") projectCode: String,
        @Header("X-SNAPS-VERSION") appVer: String,
        @Header("X-SNAPS-OS-VERSION") osVer: String,
        @Header("X-SNAPS-DEVICE-UUID") deviceId: String,
        @Header("X-SNAPS-USER-NO") userNo: String,
        @Part file: MultipartBody.Part,
        @Part("imageWidth") imageWidth: RequestBody,
        @Part("imageHeight") imageHeight: RequestBody,
        @Part("imageYear") imageYear: RequestBody,
        @Part("imageSequence") imageSequence: RequestBody
    ): Single<UploadOriginalImageResponseDto>

    @Headers(
        "X-SNAPS-CHANNEL: ANDROID",
        "X-SNAPS-VERSION: 1",
        "X-SNAPS-OS-VERSION: 1",
        "X-SNAPS-DEVICE: 1",
        "X-SNAPS-DEVICE-TOKEN: 1",
    )
    @POST("v1/project/{projectCode}/isAfterOrderEdit")
    fun getIsAfterOrderEdit(
        @Path("projectCode") projectCode: String,
        @Header("X-SNAPS-VERSION") appVer: String,
        @Header("X-SNAPS-OS-VERSION") osVer: String,
        @Header("X-SNAPS-DEVICE-UUID") deviceId: String,
        @Header("X-SNAPS-USER-NO") userNo: String
    ): Single<GetIsAfterOrderEditResponseDto>

    @Headers(
        "X-SNAPS-CHANNEL: ANDROID",
        "X-SNAPS-VERSION: 1",
        "X-SNAPS-OS-VERSION: 1",
        "X-SNAPS-DEVICE: 1",
        "X-SNAPS-DEVICE-TOKEN: 1",
    )
    @Multipart
    @POST("v1/project/{projectCode}")
    fun uploadProject(
        @Path("projectCode") projectCode: String,
        @Header("X-SNAPS-VERSION") appVer: String,
        @Header("X-SNAPS-OS-VERSION") osVer: String,
        @Header("X-SNAPS-DEVICE-UUID") deviceId: String,
        @Header("X-SNAPS-USER-NO") userNo: String,
        @Part("productCode") productCode: RequestBody,
        @Part("templateCode") templateCode: RequestBody,
        @Part("projectName") projectName: RequestBody,
        @Part("pageAddCount") pageAddCount: RequestBody,
        @Part("productType") productType: RequestBody,
        @Part("finishStatus") finishStatus: RequestBody,
        @Part("affxName") affxName: RequestBody,
        @Part("glossyType") glossyType: RequestBody,
        @Part("paperCode") paperCode: RequestBody,
        @Part("frameCode") frameCode: RequestBody,
        @Part("frameType") frameType: RequestBody,
        @Part("coatingYN") coatingYN: RequestBody,
        @Part("backType") backType: RequestBody,
        @Part("quantity") quantity: RequestBody,
        @Part("spineNo") spineNo: RequestBody,
        @Part("spineVersion") spineVersion: RequestBody,
        @Part("usePhotoCount") usePhotoCount: RequestBody,
        @Part("calendarStartDate") calendarStartDate: RequestBody,
        @Part("calendarEndDate") calendarEndDate: RequestBody,
        @Part saveFile: MultipartBody.Part,
        @Part middleThumbnailFile: MultipartBody.Part,
        @Part thumbnailFile: MultipartBody.Part,
        @Part imageYearList: List<MultipartBody.Part>,
        @Part imageSequenceList: List<MultipartBody.Part>,
        @Part("convertJsonYN") convertJsonYN: RequestBody
    ): Single<UploadProjectResponseDto>


    @Headers(
        "X-SNAPS-CHANNEL: ANDROID",
        "X-SNAPS-VERSION: 1",
        "X-SNAPS-OS-VERSION: 1",
        "X-SNAPS-DEVICE: 1",
        "X-SNAPS-DEVICE-TOKEN: 1",
        "Content-Type: application/json"
    )
    @POST("/v1/recommend/layout/new")
    fun getAiTemplate(
        @Header("X-SNAPS-VERSION") appVer: String,
        @Header("X-SNAPS-OS-VERSION") osVer: String,
        @Header("X-SNAPS-DEVICE-UUID") deviceId: String,
        @Header("X-SNAPS-USER-NO") userNo: String,
        @Body jsonString: RequestBody
    ): Single<LayoutRecommendResponseDto2>


    @Headers(
        "X-SNAPS-CHANNEL: ANDROID",
        "X-SNAPS-VERSION: 1",
        "X-SNAPS-OS-VERSION: 1",
        "X-SNAPS-DEVICE: 1",
        "X-SNAPS-DEVICE-TOKEN: 1",
        "Content-Type: application/json"
    )
    @POST("/v1/recommend/layout/new/page")
    fun getAiRecommendLayout(
        @Header("X-SNAPS-VERSION") appVer: String,
        @Header("X-SNAPS-OS-VERSION") osVer: String,
        @Header("X-SNAPS-DEVICE-UUID") deviceId: String,
        @Header("X-SNAPS-USER-NO") userNo: String,
        @Body jsonString: RequestBody
    ): Single<LayoutRecommendResponseDto2>
}