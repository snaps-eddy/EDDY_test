package com.snaps.mobile.data.asset

import com.snaps.mobile.data.util.ExifUtil
import java.io.File

data class ThumbImageInfo(
    val file: File,
    val exifInfo: ExifUtil.Info,
    val widthWithoutOt: Int,
    val heightWithoutOt: Int,
)