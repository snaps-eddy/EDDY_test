package com.snaps.mobile.data.template

import com.google.gson.annotations.SerializedName
import com.snaps.mobile.data.save.FormStyleToJson
import com.snaps.mobile.data.save.SceneToJson

// https://proandroiddev.com/most-elegant-way-of-using-gson-kotlin-with-default-values-and-null-safety-b6216ac5328c
// https://proandroiddev.com/safe-parsing-kotlin-data-classes-with-gson-4d560fe3cdd2
data class TemplateSceneDto(
    @SerializedName("@type")
    val type: String = "",
    @SerializedName("@subType")
    val subType: String = "",
    @SerializedName("@side")
    val side: String = "",
    @SerializedName("@width")
    val width: Float = 0.0f,
    @SerializedName("@height")
    val height: Float = 0.0f,
    @SerializedName("@templateCode")
    val templateCode: String = "",
    @SerializedName("@layoutCode")
    val layoutCode: String = "",
    @SerializedName("@layoutType")
    val layoutType: String = "",
    @SerializedName("@hiddenIdx")
    val hiddenIdx: Int = 0,
    @SerializedName("@printCount")
    val printCount: Int = 0,
    @SerializedName("@initialMillimeterWidth")
    val initialMillimeterWidth: Int = 0,
    @SerializedName("@initialMillimeterHeight")
    val initialMillimeterHeight: Int = 0,
    @SerializedName("@year")
    val year: String = "",
    @SerializedName("@month")
    val month: String = "",
    @SerializedName("@midWidth")
    val midWidth: String = "",
    @SerializedName("defaultInfo")
    val defaultInfo: DefaultInfo = DefaultInfo(),
//    @SerializedName("formStyle")
//    val formStyle: TemplateSceneFormStyleDto,
    @SerializedName("object")
    val sceneObjects: List<TemplateSceneObjectDto> = listOf(),
) {
    data class DefaultInfo(
        @SerializedName("@templateCode")
        val templateCode: String = "",
        @SerializedName("@layoutCode")
        val layoutCode: String = "",
        @SerializedName("@stickerIdList")
        val stickerIdList: List<String> = mutableListOf(),
        @SerializedName("@backgroundId")
        val backgroundId: String = ""
    )
}