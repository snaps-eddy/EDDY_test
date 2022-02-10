package com.snaps.mobile.data.ai

data class LayoutRecommendPageRequestDto (
    val appType: String,
    val bookSize: Int = 2, // 포토북 사이즈 [0:5*7, 1:6x6, 2:8*8, 3:8*10, 4:10*10, 5:12*12, 6:A4]
    val userNo : Int = 1,
    val deviceId: String = "",
    val language: String = "ko",
    val projCode: String = "",
    val nRcmd: Int = 1,
    val singlePage: Int = 0, // 단일 페이지 추천 여부  0 : 사용 안함 1 : 단일 페이지로 추천
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
        val exifDate: String?,
        val sysDate: String?,
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