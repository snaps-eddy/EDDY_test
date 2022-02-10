package com.snaps.mobile.data.project

data class UploadThumbImageResponseDto(
    val imageYear: String?,
    val imageSequence: String?,
    val analysisInfo: String?,
    val exifDate: String?,
    val fileName: String?,
    val imageWidth: String?,
    val imageHeight: String?,
    val smallImagePath: String?,
    val middleImagePath: String?,
    val largeImagePath: String?,
    val originalFileName: String?,
    val projectCode: String?
)