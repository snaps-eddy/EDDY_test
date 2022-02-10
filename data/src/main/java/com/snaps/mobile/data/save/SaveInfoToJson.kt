package com.snaps.mobile.data.save

import com.google.gson.annotations.SerializedName

data class SaveInfoToJson(
    @SerializedName("@locationSearch")
    val locationSearch: String,
    @SerializedName("@userAgent")
    val userAgent: String,
    @SerializedName("@regDate")
    val regDate: String,
    @SerializedName("@saveCount")
    val saveCount: String
)