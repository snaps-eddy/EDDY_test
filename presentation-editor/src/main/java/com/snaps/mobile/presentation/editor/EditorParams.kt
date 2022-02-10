package com.snaps.mobile.presentation.editor

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class EditorParams(
    val projectCode: String?,
    val productCode: String?,
    val templateCode: String?,
    val userNo: String,
    val userName: String,
    val deviceId: String,
    val glossType: String?,
    val paperCode: String?,
    val projectCount: String?,
    val rawText: String, // 원본 Json
    val appVersion: String,
    val language: String,
    val ipAddress: String,
    val ISP: String,
    val tutorialUrl: String,
) : Parcelable {
}