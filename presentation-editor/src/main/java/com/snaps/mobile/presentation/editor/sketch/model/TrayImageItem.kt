package com.snaps.mobile.presentation.editor.sketch.model

data class TrayImageItem(
    val imgSeq: String?,
    val year: String?,
    val localId: String,
    val thumbnailUri: String,
    val orientationAngle: Int
) {

    var onStage: Boolean = false
    var isUploading = imgSeq.isNullOrBlank() && year.isNullOrBlank()
    var drawWidth: Int = 0
    var drawHeight: Int = 0

}
