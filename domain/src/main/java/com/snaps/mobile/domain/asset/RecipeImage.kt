package com.snaps.mobile.domain.asset

import com.snaps.common.android_utils.toOTAngle

/**
 * 용도.
 * 1. Media store or SNS Image 데이터로 부터 만든다.
 * 2. Thumbnail upload 전에 상태와 upload 후 상태를 가질 수 있다.
 * 3. 업로드 후에는 편집기 사진 트레이에 보여지는 테이터.
 * 4. 사진 트레이에서 스케치로 옮겨진 후에는 사용되지 않는다.
 */
data class RecipeImage(
    val imgSeq: String?,
    val year: String?,
    val localId: String,
    val type: AssetImageType,
    val localUri: String,
    val remoteUri: String,
    val width: Float,
    val height: Float,
    val orientation: Int
) {
    val outputImageSequence: String
        get() {
            return "${year}/$imgSeq"
        }

    val orientationAngle: Int
        get() = orientation.toOTAngle()
}