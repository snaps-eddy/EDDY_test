package com.snaps.mobile.data.template

import com.google.gson.annotations.SerializedName

data class TemplateBorderDto(

    @SerializedName("@imageId")
    val imageId: String? = null,
    @SerializedName("@imagePath")
    val imagePath: String? = null,
    @SerializedName("@maskId")
    val maskId: String? = null,
    @SerializedName("@maskPath")
    val maskPath: String? = null,
    @SerializedName("@singleAlpha")
    val singleAlpha: Float = 0.0f,
    @SerializedName("@singleColor")
    val singleColor: String? = null,
    @SerializedName("@singleThickness")
    val singleThickness: Int = 0,
    @SerializedName("@type")
    val type: String? = null,
    @SerializedName("@imageOffset")
    val imageOffset: String? = null,
)