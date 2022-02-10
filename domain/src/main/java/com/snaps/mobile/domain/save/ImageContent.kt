package com.snaps.mobile.domain.save

import com.snaps.mobile.domain.asset.AnalysisInfo
import com.snaps.common.android_utils.toOTAngle

data class ImageContent(
    val imgSeq: String,
    val year: String,
    val middleImagePath: String,
    val width: Float,
    val height: Float,
    val analysisInfo: AnalysisInfo,
    val orientation: Int,       //TODO 이게 현 시점에 문제가 될???
    val date: String
) {

    var warningResolution: Boolean = false

    val outputImageSequence: String
        get() {
            return "${year}/$imgSeq"
        }

    val orientationAngle: Int
        get() = orientation.toOTAngle()
}