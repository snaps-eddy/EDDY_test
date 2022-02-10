package com.snaps.common.android_utils

import androidx.exifinterface.media.ExifInterface

fun Int.toOTAngle(): Int = when (this) {
    ExifInterface.ORIENTATION_NORMAL -> 0
    ExifInterface.ORIENTATION_ROTATE_90 -> 90
    ExifInterface.ORIENTATION_ROTATE_180 -> 180
    ExifInterface.ORIENTATION_ROTATE_270 -> 270
    //혹시 몰라서 아래 값도 처리...인데...
    ExifInterface.ORIENTATION_TRANSPOSE -> 90
    ExifInterface.ORIENTATION_FLIP_VERTICAL -> 180
    ExifInterface.ORIENTATION_TRANSVERSE -> 270
    else -> 0
}