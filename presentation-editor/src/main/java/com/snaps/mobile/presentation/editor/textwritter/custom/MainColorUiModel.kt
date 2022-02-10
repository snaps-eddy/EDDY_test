package com.snaps.mobile.presentation.editor.textwritter.custom

import android.graphics.Color

data class MainColorUiModel(
    val hexColor: String,
    val subColors: List<SubColorUiModel>
) {

    val intColor: Int
        get() = Color.parseColor(hexColor)


}