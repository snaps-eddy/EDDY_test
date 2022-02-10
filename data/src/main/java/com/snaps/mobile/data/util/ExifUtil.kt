package com.snaps.mobile.data.util

import android.content.ContentResolver
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import com.snaps.common.utils.log.Dlog
import com.snaps.common.android_utils.toOTAngle
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class ExifUtil @Inject constructor(
    private val contentResolver: ContentResolver,
) {
    private val tag = ExifUtil::class.java.simpleName

    data class Info(
        val orientation: Int = 0,
        val latitude: Double = 0.0,
        val longitude: Double = 0.0,
        val dateTime: Long = 0
    ) {
        val gps: String?
            get() {
                if (latitude == 0.0 || longitude == 0.0) return null
                return "${latitude},$longitude"
            }

        val exifDate: String
            get() {
                if (dateTime == 0L) return ""
                return SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.KOREA).format(Date(dateTime))
            }

        val orientationAngle: Int
            get() = orientation.toOTAngle()
    }

    //GPS 시간은 왜 단일 포맷이 없는지...
    private val gpsDateFormatList: List<SimpleDateFormat> = listOf(
        SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.KOREA),  //이게 경우의 수가 제일 많다.
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA),
        SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.KOREA),
        SimpleDateFormat("yyyy:MM:dd:HH:mm:ss", Locale.KOREA),
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.KOREA)
    )

    fun getInfo(uriText: String): Info {
        return try {
            contentResolver.openInputStream(Uri.parse(uriText))?.use { inputStream ->
                inputStream?.let {
                    ExifInterface(it)
                }.let {
                    Info(
                        orientation = getOrientation(it),
                        latitude = getLatitude(it),
                        longitude = getLongitude(it),
                        dateTime = getDateTimeLong(it)
                    )
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Info()
        } ?: Info()
    }

    private fun getDateTimeLong(exifInterface: ExifInterface): Long {
        return exifInterface.getAttribute(ExifInterface.TAG_DATETIME)?.let { dateTimeString ->
            if (dateTimeString.isEmpty()) {
                0L
            } else {
                gpsDateFormatList.firstOrNull {
                    try {
                        it.parse(dateTimeString)
                        true
                    } catch (e: Exception) {
                        false
                    }
                }?.run {
                    parse(dateTimeString).time
                } ?: 0L
            }
        } ?: 0L

//        val dateTimeString = exifInterface.getAttribute(ExifInterface.TAG_DATETIME) ?: return 0
//        if (dateTimeString.isEmpty()) return 0
//
//        for (sf in gpsDateFormatList) {
//            try {
//                return sf.parse(dateTimeString).time
//            } catch (e: Exception) {
//                continue
//            }
//        }
//        return 0
    }

    private fun getOrientation(exifInterface: ExifInterface): Int {
        return try {
            exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL).let {
                when (it) {
                    ExifInterface.ORIENTATION_UNDEFINED -> ExifInterface.ORIENTATION_UNDEFINED
                    ExifInterface.ORIENTATION_NORMAL -> ExifInterface.ORIENTATION_NORMAL
                    ExifInterface.ORIENTATION_ROTATE_90 -> ExifInterface.ORIENTATION_ROTATE_90
                    ExifInterface.ORIENTATION_ROTATE_180 -> ExifInterface.ORIENTATION_ROTATE_180
                    ExifInterface.ORIENTATION_ROTATE_270 -> ExifInterface.ORIENTATION_ROTATE_270
                    //음...
                    ExifInterface.ORIENTATION_TRANSPOSE -> ExifInterface.ORIENTATION_ROTATE_90
                    ExifInterface.ORIENTATION_FLIP_VERTICAL -> ExifInterface.ORIENTATION_ROTATE_180
                    ExifInterface.ORIENTATION_TRANSVERSE -> ExifInterface.ORIENTATION_ROTATE_270
                    else -> ExifInterface.ORIENTATION_UNDEFINED //32380 대응 (unknown)
                }
            }
        } catch (e: Exception) {
            Dlog.e(tag, e)
            ExifInterface.ORIENTATION_UNDEFINED
        }
    }

    private fun getLatitude(exifInterface: ExifInterface): Double {
        return exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE)?.let { latitudeText ->
            exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF)?.let { latitudeRefText ->
                try {
                    convertRationalLatLonToDouble(
                        latitudeText,
                        latitudeRefText
                    )
                } catch (e: Exception) {
                    0.0
                }
            }
        } ?: 0.0
    }

    private fun getLongitude(exifInterface: ExifInterface): Double {
        return exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE)?.let { longitudeText ->
            exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF)?.let { longitudeRefText ->
                try {
                    convertRationalLatLonToDouble(
                        longitudeText,
                        longitudeRefText
                    )
                } catch (e: Exception) {
                    0.0
                }
            }
        } ?: 0.0
    }


    //https://android.googlesource.com/platform/frameworks/base/+/android-cts-4.4_r1/media/java/android/media/ExifInterface.java
    //도분초 단위를 도단위로 변경한다.
    private fun convertRationalLatLonToDouble(rationalString: String, ref: String): Double {
        return rationalString.split(",").map {
            it.split("/").let { list ->
                list[0].trim().toDouble() / list[1].trim().toDouble()
            }
        }.toList().let {
            it[0] + it[1] / 60.0 + it[2] / 3600.0 * if (ref == "S" || ref == "W") -1 else 1
        }

//        val parts = rationalString.split(",")
//
//        val degrees = parts[0].split("/").let {
//            it[0].trim().toDouble() / it[1].trim().toDouble()
//        }
//
//        val minutes = parts[1].split("/").let {
//            it[0].trim().toDouble() / it[1].trim().toDouble()
//        }
//
//        val seconds = parts[2].split("/").let {
//            it[0].trim().toDouble() / it[1].trim().toDouble()
//        }
//
//        return degrees + minutes / 60.0 + seconds / 3600.0 * if (ref == "S" || ref == "W") -1 else 1
    }
}