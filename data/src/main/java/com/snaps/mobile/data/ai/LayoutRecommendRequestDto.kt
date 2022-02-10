package com.snaps.mobile.data.ai

import com.google.gson.annotations.Expose

// https://snaps1.atlassian.net/wiki/spaces/SR/pages/1223065609/01.+Snaps+Web+-+Data+Web
data class LayoutRecommendRequestDto(
    val appType: String,
    val bookSize: Int = 2, // 포토북 사이즈 [0:5*7, 1:6x6, 2:8*8, 3:8*10, 4:10*10, 5:12*12, 6:A4]
    val rcmdCover: Int = 1, // 0: 추천 안함(레더커버)  1: 추천함 (default)
    val userNo : Int = 1,
    val deviceId: String = "",
    val language: String = "ko",
    val projCode: String = "",
    val nRcmd: Int = 1,
    val transId: String = "",
    val imagesLength: Int = 0,
    val images: List<Images>
) {
    data class Images (
        val index: Int,
        val uuid: String,
        val imageKey: String,
        val imageOriFile: String,
        val absPath: String ,
        val oripqW: Int,
        val oripqH: Int,
        val ot: Int,
        @Expose
        val exifDate: String?,
        val sysDate: String,
        @Expose
        val gps: String?,
        val snapsImageThumbFile: String,
        val fd_thum: Fdthum?
    ) {
        data class Fdthum (
            val w: Int,
            val h: Int,
            val x: Int,
            val y: Int,
            val xw: Int,
            val yh: Int,
            val fn: Int,
        )
    }
}