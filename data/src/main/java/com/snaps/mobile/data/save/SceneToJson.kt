package com.snaps.mobile.data.save

import com.google.gson.annotations.SerializedName

data class SceneToJson(
    @SerializedName("@type")
    val type: String,
    @SerializedName("@subType")
    val subType: String,
    @SerializedName("@side")
    val side: String,
    @SerializedName("@width")
    val width: Float,
    @SerializedName("@height")
    val height: Float,
    @SerializedName("@templateCode")
    val templateCode: String,
    @SerializedName("@layoutCode")
    val layoutCode: String,
    @SerializedName("@layoutType")
    val layoutType: String,
    @SerializedName("@hiddenIdx")
    val hiddenIdx: Int,
    @SerializedName("@printCount")
    val printCount: Int,
    @SerializedName("@initialMillimeterWidth")
    val initialMillimeterWidth: Int,
    @SerializedName("@initialMillimeterHeight")
    val initialMillimeterHeight: Int,
    @SerializedName("@year")
    val year: String,
    @SerializedName("@month")
    val month: String,
    @SerializedName("@midWidth")
    val midWidth: Float,
    @SerializedName("defaultInfo")
    val defaultInfo: DefaultInfo,
//    @SerializedName("formStyle")
//    val formStyle: FormStyleToJson,
    @SerializedName("object")
    val sceneObjects: List<SceneObjectToJson>,
) {

//    data class DefaultInfo(
//        @SerializedName("@templateCode")
//        val templateCode: String,
//        @SerializedName("@stickerCodes")
//        val stickerCodes: String,
//        @SerializedName("@backgroundCodes")
//        val backgroundCodes: String
//    )

    data class DefaultInfo(
        @SerializedName("@templateCode")
        val templateCode: String,
        @SerializedName("@layoutCode")
        val layoutCode: String,
        @SerializedName("@stickerIdList")
        val stickerIdList: List<String>,
        @SerializedName("@backgroundId")
        val backgroundId: String
    )

}