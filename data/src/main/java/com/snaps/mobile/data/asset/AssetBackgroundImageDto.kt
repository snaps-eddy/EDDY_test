package com.snaps.mobile.data.asset

import com.google.gson.annotations.SerializedName
import com.snaps.common.utils.log.Dlog

data class AssetBackgroundImageDto(
    @SerializedName("id") val id: String,
    @SerializedName("author") val author: String,
    @SerializedName("width") val width: Int,
    @SerializedName("height") val height: Int,
    @SerializedName("url") val url: String,
    @SerializedName("download_url") val downloadUrl: String,
) {

    val downscaleImageUri: String
        get() {
            val splitResult = downloadUrl.split("/").toMutableList()
            val size = splitResult.size
            splitResult[1] = "/"
            splitResult[size - 2] = "200"
            splitResult[size - 1] = "200"
            return splitResult.joinToString("/", "", "")
        }

}