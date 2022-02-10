package com.snaps.mobile.domain.save

data class InnerImage(
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
    val angle: Int = 0,
    val alpha: Float = 1.0f
) {
}