package com.snaps.mobile.presentation.editor.sketch.model

data class BorderUiModel(
    val imageId: String,
    val imagePath: String,
    val maskId: String,
    val maskPath: String,
    val singleAlpha: Float,
    val singleColor: String,
    val singleThickness: Int,
    val imageOffset: String,
    val type: String,
) {
    val isSingleColor: Boolean
        get() {
            return type == "singleColor" && singleColor.isNullOrBlank().not() && singleThickness > 0 && singleAlpha > 0
        }

    val isBorderImage: Boolean
        get() {
            return type == "borderImage"
        }

    val isMask: Boolean
        get() {
            return type == "mask" && maskPath.isNullOrBlank().not()
        }
}
