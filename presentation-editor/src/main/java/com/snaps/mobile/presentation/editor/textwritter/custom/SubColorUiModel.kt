package com.snaps.mobile.presentation.editor.textwritter.custom

import android.graphics.Color

data class SubColorUiModel(
    val hexColor: String
) {

    val intColor: Int
        get() = Color.parseColor(hexColor)

}