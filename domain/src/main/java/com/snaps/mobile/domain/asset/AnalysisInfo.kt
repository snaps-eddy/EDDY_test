package com.snaps.mobile.domain.asset

import android.graphics.Rect
import android.graphics.RectF
import androidx.exifinterface.media.ExifInterface
import android.util.Size
import android.util.SizeF
import com.snaps.common.utils.log.Dlog
import kotlin.math.max
import kotlin.math.min

data class AnalysisInfo(
    private val img: Img
) {
    constructor() : this(Img(Img.FdThum(), Img.Meta()))

    data class Img(
        val fd_thum: FdThum,
        val meta: Meta
    ) {
        data class FdThum(
            val fn: Int = 0,
            val h: Float = 0f,
            val w: Float = 0f,
            val x: Float = 0f,
            val xw: Float = 0f,
            val y: Float = 0f,
            val yh: Float = 0f
        )

        data class Meta(
            val dt: String = "",
            val ot: Int = 0,
            val w: Float = 0f,
            val h: Float = 0f,
            val th: Float = 0f,
            val tw: Float = 0f,
        )
    }

    fun getFdThum(): Img.FdThum {
        return img.fd_thum.copy()
    }

    val isAnalysis: Boolean
        get() = img.meta.th.toInt() > 0 && img.meta.tw.toInt() > 0

    val outputRaw: String
        get() = "${img.fd_thum.x.toInt()}," +
                "${img.fd_thum.y.toInt()}," +
                "${img.fd_thum.xw.toInt() - img.fd_thum.x.toInt()}," +
                "${img.fd_thum.yh.toInt() - img.fd_thum.y.toInt()}," +
                "${img.meta.tw.toInt()}," +
                "${img.meta.th.toInt()}," +
                "${img.fd_thum.fn}"

    val searchedAreaRect
        get() = RectF(img.fd_thum.x, img.fd_thum.y, img.fd_thum.xw, img.fd_thum.yh)

    private val uploadThumbnailSize
        get() = SizeF(img.meta.tw, img.meta.th)

    /**
     * fd 정보는 orientation을 뺀 정보이지만
     * fdbox에 대한 정보는 orientation 적용된 위치로 기록되어있다.
     */
    fun getCalibratedOrientationThumbnailSize(orientationAngle: Int): SizeF {
        return if (orientationAngle == 90 || orientationAngle == 270) {
            SizeF(uploadThumbnailSize.height, uploadThumbnailSize.width)
        } else {
            uploadThumbnailSize
        }
    }

    val noFdInfo: Boolean
        get() = !hasFdInfo

    val hasFdInfo: Boolean
        get() = img.fd_thum.w.toInt() > 0 && img.fd_thum.h.toInt() > 0
}





