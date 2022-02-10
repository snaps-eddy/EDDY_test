package com.snaps.mobile.domain.project

import com.snaps.mobile.domain.asset.AnalysisInfo
import com.snaps.mobile.domain.asset.AssetImageType
import com.snaps.common.android_utils.toOTAngle

data class ImageThumbnail(
    val imgSeq: String,
    val year: String,
    val localId: String,
    val type: AssetImageType,
    val thumbnailUri: String,
    // 섬네일의 width, height 가 아닌 원본의 width height 값이다.
    val originWidth: Float,
    val originHeight: Float,
    val thumbnailRemotePath: String,
    val analysisInfo: AnalysisInfo,
    val orientation: Int,
    val date: String,
) : Comparable<ImageThumbnail> {
    val outputImageSequence: String
        get() {
            return "${year}/$imgSeq"
        }

    val orientationAngle: Int
        get() = orientation.toOTAngle()

    override fun compareTo(other: ImageThumbnail): Int = outputImageSequence.compareTo(other.outputImageSequence)
}