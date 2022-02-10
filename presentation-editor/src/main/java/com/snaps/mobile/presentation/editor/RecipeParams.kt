package com.snaps.mobile.presentation.editor

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RecipeParams(
    val isFromCart: Boolean,
    val projectCode: String,
    val productCode: String,
    val templateCode: String,
    val userNo: String,
    val userName: String,
    val deviceId: String,
    val rawText: String, // 원본 Json
    val language: String,
    val appVersion: String,
    val tutorialUrl: String,
) : Parcelable {
}