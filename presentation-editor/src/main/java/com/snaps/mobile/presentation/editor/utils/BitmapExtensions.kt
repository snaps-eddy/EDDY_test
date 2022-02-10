package com.snaps.mobile.presentation.editor.utils

import android.graphics.Bitmap
import android.graphics.Canvas

fun Canvas.drawBitmapCenter(source: Bitmap) {
    val centerX = (this.width - source.width) / 2.0f
    val centerY = (this.height - source.height) / 2.0f
    this.drawBitmap(source, centerX, centerY, null)
}