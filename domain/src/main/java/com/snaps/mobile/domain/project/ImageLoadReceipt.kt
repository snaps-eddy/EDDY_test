package com.snaps.mobile.domain.project

import com.snaps.mobile.domain.asset.AnalysisInfo

data class ImageLoadReceipt(
    val imgSeq: String,
    val year: String,
    val localId: String,
    val orientation: Int,
    val width: Float,
    val height: Float,
    val thumbnailRemotePath: String,
    val analysisInfo: AnalysisInfo,
    val exifData: String,
) {
}