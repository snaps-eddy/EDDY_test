package com.snaps.mobile.data.template

import com.google.gson.annotations.SerializedName

data class TemplateInnerImageDto(
    @SerializedName("@x")
    val x: Float = 0.0f,
    @SerializedName("@y")
    val y: Float = 0.0f,
    @SerializedName("@alpha")
    val alpha: Float = 1.0f,
    @SerializedName("@angle")
    val angle: Int = 0,
    @SerializedName("@height")
    val height: Float = 0.0f,
    @SerializedName("@width")
    val width: Float = 0.0f,
)