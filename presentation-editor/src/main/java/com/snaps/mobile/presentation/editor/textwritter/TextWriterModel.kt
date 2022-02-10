package com.snaps.mobile.presentation.editor.textwritter

import com.snaps.mobile.domain.save.TextAlign
import kotlin.math.max
import kotlin.math.min

data class TextWriterModel(
    val text: String,
    val align: TextAlign,
    val color: String,
    val sizePx: Int
) {

    val viewFontSizeDp: Float
        get() = min(36, max(13, 150)).toFloat()
}