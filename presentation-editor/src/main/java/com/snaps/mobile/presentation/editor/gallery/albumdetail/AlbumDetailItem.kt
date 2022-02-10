package com.snaps.mobile.presentation.editor.gallery.albumdetail

import com.snaps.mobile.domain.asset.AssetImageType
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

data class AlbumDetailItem(
    val id: String,
    val type: AssetImageType,
    val thumbnailUri: String,
    val date: Date,
    val width: Float,
    val height: Float,
    val orientation: Int
) {
    var selected: Boolean = false

//    var selectIndex: Int = 0

//    fun setIndexInfo(indexIn: Int): AlbumDetailItem {
//        if (indexIn > -1) {
//            this.selected = true
//            this.selectIndex = indexIn + 1
//        } else {
//            this.selected = false
//            this.selectIndex = indexIn
//        }
//        return this
//    }

    data class Date(
        val toDays: Long,
        val titleFormat: SimpleDateFormat,
        val fullTitleFormat: SimpleDateFormat,
        val todayText: String,
        val yesterdayText: String,
    ) : Comparable<Date> {

        var milliseconds: Long = 0L
        var nowMilliseconds: Long = 0L

        override fun compareTo(other: Date): Int {
            return this.toDays.compareTo(other.toDays)
        }

        fun getDateTitle(): String {
            val nowDays = TimeUnit.MILLISECONDS.toDays(nowMilliseconds)
            val nowYear = Calendar.getInstance().apply {
                timeInMillis = nowMilliseconds
            }.run {
                this.get(Calendar.YEAR)
            }
            val toYear = Calendar.getInstance().apply {
                timeInMillis = milliseconds
            }.run {
                this.get(Calendar.YEAR)
            }
            return when (nowDays - toDays) {
                0L -> todayText
                1L -> yesterdayText
                else -> {
                    if (nowYear == toYear) {
                        titleFormat.format(milliseconds)
                    } else {
                        fullTitleFormat.format(milliseconds)
                    }

                }
            }
        }
    }

    override fun toString(): String {
        return super.toString().plus("selected : $selected")
    }
}