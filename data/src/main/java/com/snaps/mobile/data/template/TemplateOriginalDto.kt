package com.snaps.mobile.data.template

import com.google.gson.annotations.SerializedName

data class TemplateOriginalDto(
    @SerializedName("@width")
    val width: Float = 0.0f,
    @SerializedName("@height")
    val height: Float = 0.0f,
    @SerializedName("@date")
    val date: String? = null,
    @SerializedName("@imageSequence")
    val imageSequence: String? = null,
    @SerializedName("@middleImagePath")
    val middleImagePath: String? = null,
    @SerializedName("@orientation")
    val orientation: String? = null,        //TODO AI에 등록된 템플릿이 문자열로 되어 있음
    @SerializedName("@resourceId")
    val resourceId: String? = null,
) {
    val imgSeq: String?
        get() {
            if (imageSequence.isNullOrBlank()) return ""
            return imageSequence.split("/")[1]
        }

    val year: String?
        get() {
            if (imageSequence.isNullOrBlank()) return ""
            return imageSequence.split("/")[0]
            return ""
        }
}