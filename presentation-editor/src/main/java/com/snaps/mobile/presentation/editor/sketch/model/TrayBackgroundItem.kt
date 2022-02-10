package com.snaps.mobile.presentation.editor.sketch.model

data class TrayBackgroundItem(
    val resourceId: String,
    val resourceUri: String,
    val thumbnailUri: String
) {

    var drawWidth: Int = 0
    var drawHeight: Int = 0

}