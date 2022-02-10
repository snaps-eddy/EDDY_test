package com.snaps.mobile.presentation.editor.utils

import android.content.res.Resources
import android.view.View

fun Float.toPx(): Int = (this / Resources.getSystem().displayMetrics.density).toInt()

fun Int.dp(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

fun Int.toScaled(scaleFactor: Float): Int = (this * scaleFactor).toInt() // floor or up or round

fun Int.centerOfX(view: View): Float = (view.width - this) / 2.0f

fun Int.centerOfY(view: View): Float = (view.height - this) / 2.0f

fun Int.widthScaleFactor(factor : Float): Float = (Resources.getSystem().displayMetrics.widthPixels / this.toFloat()) * factor

fun Int.applyScale(scaleFactor: Float): Int = (this * scaleFactor).toInt()

fun Int.perItemInDevice() : Int = Resources.getSystem().displayMetrics.heightPixels / this