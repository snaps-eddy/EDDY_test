package com.snaps.mobile.data.save

import com.google.gson.annotations.SerializedName

data class SaveToJson(
    @SerializedName("info")
    val info: SaveInfoToJson,
    @SerializedName("scene")
    val scene: List<SceneToJson>
) {
    
}