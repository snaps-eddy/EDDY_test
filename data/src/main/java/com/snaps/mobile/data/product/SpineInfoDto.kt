package com.snaps.mobile.data.product

import com.google.gson.annotations.SerializedName

data class SpineInfoDto (
    @SerializedName("version")
    val version: String,
    @SerializedName("papers")
    val papers: List<Paper>,
) {
    data class Paper (
        @SerializedName("code")
        val code: String,
        @SerializedName("millimeter")
        val millimeter: String,
        @SerializedName("mo_maxpage")
        val mobileMaxpage: String,
        @SerializedName("spine")
        val spine: List<Spine>,
    ) {
        data class Spine (
            @SerializedName("page_min")
            val pageMin: String,
            @SerializedName("millimeter")
            val millimeter: String?, //160007은 왜 이게 없는 거야
            @SerializedName("thickness")
            val thickness: String,
            @SerializedName("number")
            val number: String,
            @SerializedName("page_max")
            val pageMax: String,
        )
    }
}

