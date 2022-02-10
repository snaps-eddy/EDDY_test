package com.snaps.common.android_utils

import androidx.annotation.StringRes

interface ResourceProvider {

    fun getDimensions(resId: Int): Float

    fun getScreenWidth(): Int

    fun getString(@StringRes resId: Int): String

    fun getString(@StringRes resId: Int, vararg args : Any): String

}