package com.snaps.mobile.presentation.editor.dialog

import androidx.annotation.RawRes
import com.snaps.mobile.presentation.editor.R
import com.snaps.mobile.presentation.editor.utils.dp

sealed class LoadingAnimStyle(
    val width: Int,
    val height: Int,
    @RawRes val resouce: Int,
    val isDimBackground: Boolean,
) {

    object Smalll : LoadingAnimStyle(width = 26.dp(), height = 26.dp(), R.raw.lottie_loading, false)

//    object Large : LoadingAnimStyle(width = 200.dp(), height = 200.dp(), R.raw.lottie_loading_photobook, true)

}