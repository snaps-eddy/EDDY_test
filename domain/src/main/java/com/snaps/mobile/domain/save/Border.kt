package com.snaps.mobile.domain.save

data class Border(
    val imageId: String,
    val imagePath: String,
    val maskId: String,
    val maskPath: String,
    val singleAlpha: Float,
    val singleColor: String,
    val singleThickness: Int,
    val imageOffset: String,
    val type: String
)
