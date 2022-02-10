package com.snaps.mobile.data.ai

data class LayoutRecommendResponseDto2 (
    val statusCode: Int,
    val status: String?,
    val message: String?,
    val errorMessage: String?,
    val errorCode: String?,
    val appType: String?,
    val bookTitle: String?,
    val projCode: String?,
    val transId: String?,
    val deviceId: String?,
    val userNo: String?,
    val language: String?,
    val bookSize: Int,
    val imagesLength: Int,
    val themeType: Int,
    val analysisType: String?,
    val sceneLength: Int,
    val scene: List<Scene>?,
) {
    data class Scene (
        val index: Int,
        val type: String?,
        val layoutType: String?,
        val pagesLength: Int?,
        val pages: List<Pages>?,
    ) {
        data class Pages (
            val type: String?,
            val imagesLength: Int,
            val images: List<Images>?,
            val multiform: List<Multiform>?,
            val layoutsLength: Int,
            val layouts: List<Layouts>?,
            val backgrounds: List<Backgrounds>?

        ) {
            data class Multiform (
                val code: String?,
                val id: String?,
                val data: String?,
            )

            data class Layouts (
                val index: Int,
                val order: List<String>?,
                val id: String?,
                val url: String?,
                val data: String?,
            )

            data class Backgrounds (
                val index: Int,
                val id: String?,
                val code: String?,
                val url: String?,
                val data: String?,
                val bgColor: String?,
                val type: String?,
            )

            data class Images (
                val index: Int,
                val uuid: String?,
                val imageKey: String?,
                val location: String?,
                val thumbW: Float,
                val thumbH: Float,
                val oripqW: Float,
                val oripqH: Float,
                val ot: Int,
                val fd_thum: FdThum?,
//                val exifDate: String?,
//                val imageOriFile: String?,
//                val exceptType: String?,
//                val clear: String?,
//                val absPath: String?,
//                val sysDate: String?,
//                val theme: String?,
//                val snapsImageThumbFile: String?,
//                val gps: String?,
            )

            data class FdThum(
                val fn: Int = 0,
                val h: Float = 0f,
                val w: Float = 0f,
                val x: Float = 0f,
                val xw: Float = 0f,
                val y: Float = 0f,
                val yh: Float = 0f
            )
        }
    }
}