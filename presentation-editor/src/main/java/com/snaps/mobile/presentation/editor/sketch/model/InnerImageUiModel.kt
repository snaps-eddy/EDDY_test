package com.snaps.mobile.presentation.editor.sketch.model

data class InnerImageUiModel(
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
    val angle: Int,
    val alpha: Float,
) {

    val angleCalibrationWidth: Float
        get() = if (angle == 90 || angle == 270) height else width
    val angleCalibrationHeight: Float
        get() = if (angle == 90 || angle == 270) width else height

}