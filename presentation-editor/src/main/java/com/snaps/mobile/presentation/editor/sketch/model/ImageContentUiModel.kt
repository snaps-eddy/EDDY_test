package com.snaps.mobile.presentation.editor.sketch.model

import com.snaps.mobile.domain.asset.AnalysisInfo
import com.snaps.common.android_utils.toOTAngle

data class ImageContentUiModel(
    val imgSeq: String,
    val year: String,
    val thumbnailUri: String,
    val originWidth: Float,
    val originHeight: Float,
    val isWarningResolution: Boolean,
    val analysisInfo: AnalysisInfo,
    val orientation: Int,
) {
    val orientationAngle: Int
        get() = orientation.toOTAngle()
}