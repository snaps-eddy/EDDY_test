package com.snaps.mobile.data.asset

import android.content.ContentResolver
import android.database.Cursor
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.snaps.common.R

import com.snaps.common.android_utils.ResourceProvider
import com.snaps.common.android_utils.SchedulerProvider
import com.snaps.mobile.domain.asset.AssetImageAlbum
import com.snaps.mobile.domain.asset.AssetImageType
import java.io.File
import java.lang.StringBuilder
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap
import io.reactivex.rxjava3.kotlin.toObservable
import io.reactivex.rxjava3.core.Observable

class DeviceDataSource @Inject constructor(
    private val resourceProvider: ResourceProvider,
    private val contentResolver: ContentResolver,
    private val schedulerProvider: SchedulerProvider,
) {
    private val tag = DeviceDataSource::class.java.simpleName
    private val uriExternal: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

    private data class AlbumInfo(
        val bucketId: Long,
        var recentId: Long,
        val name: String,
        var recentTime: Long,
        var photoCounts: Int,
    )

    private var minValidDateLong: Long = 0
    private var maxValidDateLong: Long = 0

    private fun initDateCorrection() {
        Calendar.getInstance().apply {
            set(1970, 1, 1, 0, 0, 0)
            minValidDateLong = timeInMillis //그냥 0대입 하면 되는데 혹시 변경에 대비
        }

        Calendar.getInstance().apply {
            add(Calendar.MINUTE, 60)
            maxValidDateLong = timeInMillis
        }
    }

    private fun fixInvalidDate(time: Long): Long {
        return when (time) {
            in minValidDateLong..maxValidDateLong -> time
            else -> (time * 1000).let { time1000 ->
                when (time1000) {
                    in minValidDateLong..maxValidDateLong -> time1000
                    else -> 0
                }
            }
        }
    }

    private fun getImageTime(dateTaken: Long, dateAdded: Long): Long {
        if (dateTaken == 0L) return fixInvalidDate(dateAdded)
        return fixInvalidDate(dateTaken).let {
            if (it > 0) it else fixInvalidDate(dateAdded)
        }
    }

    private fun isAllowedImageFormat(mimeType: String): Boolean {
        if (mimeType.endsWith("/jpeg")) return true
        if (mimeType.endsWith("/jpg")) return true
        if (mimeType.endsWith("/png")) return true
        if (mimeType.endsWith("/jpeg", true)) return true
        if (mimeType.endsWith("/jpg", true)) return true
        if (mimeType.endsWith("/png", true)) return true
        return false
    }

    fun getHEIFImageCount(): Int {
        var count = 0
        contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            arrayOf(MediaStore.Images.Media.MIME_TYPE),
            null,
            null,
            null
        )?.use { cursor ->
            cursor.moveToFirst()
            val idxMimeType = cursor.getColumnIndex(MediaStore.Images.Media.MIME_TYPE)
            do {
                if (cursor.getString(idxMimeType).endsWith("/heic")) count++
            } while (cursor.moveToNext())
        }
        return count
    }

    fun getAlbumList(): List<AssetImageAlbum> {
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.MIME_TYPE,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media.DATE_TAKEN,
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.WIDTH,
            MediaStore.Images.Media.HEIGHT,
        )

        return contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            null
        )?.use { cursor ->
            if (cursor.count == 0) return listOf<DeviceAssetImageAlbum>()

            initDateCorrection()

            cursor.moveToFirst()
            val idxId = cursor.getColumnIndex(MediaStore.Images.Media._ID)
            val idxMimeType = cursor.getColumnIndex(MediaStore.Images.Media.MIME_TYPE)
            val idxDateTaken = cursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN)
            val idxDateAdded = cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED)
            val idxBucketId = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID)
            val idxBucketDisplayName = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
            val idxWidth = cursor.getColumnIndex(MediaStore.Images.Media.WIDTH)
            val idxHeight = cursor.getColumnIndex(MediaStore.Images.Media.HEIGHT)

            val albumInfoMap: HashMap<Long, AlbumInfo> = hashMapOf()
            do {
                if (!isAllowedImageFormat(cursor.getString(idxMimeType))) continue
                if (cursor.getInt(idxWidth) < 1 || cursor.getInt(idxHeight) < 1) continue

                val id = cursor.getLong(idxId)
                val imageTime = getImageTime(
                    dateTaken = if (idxDateTaken == -1) 0 else cursor.getLong(idxDateTaken),
                    dateAdded = if (idxDateAdded == -1) 0 else cursor.getLong(idxDateAdded)
                )

                cursor.getLong(idxBucketId).let { bucketId ->
                    albumInfoMap[bucketId]?.apply {
                        photoCounts++
                        if (recentTime < imageTime) {
                            recentTime = imageTime
                            recentId = id
                        }
                    } ?: run {
                        albumInfoMap[bucketId] = AlbumInfo(
                            bucketId = bucketId,
                            recentId = id,
                            name = cursor.getString(idxBucketDisplayName) ?: "0",  //사진이 /sdcard 폴더에 있으면 폴더 이름이 null
                            recentTime = imageTime,
                            photoCounts = 1
                        )
                    }
                }
            } while (cursor.moveToNext())

            if (albumInfoMap.size == 0) {
                listOf<DeviceAssetImageAlbum>()
            } else {
                createDeviceAssetImageAlbum(albumInfoMap)
            }
        } ?: listOf()
    }


    private fun moveToFrontOfList(list: MutableList<AlbumInfo>, name: String) {
        list.find { it.name.equals(name, true) }?.let {
            list.remove(it)
            list.add(0, it)
        }
    }

    private fun createDeviceAssetImageAlbum(albumInfoMap: HashMap<Long, AlbumInfo>): List<DeviceAssetImageAlbum> {
        return albumInfoMap.values.sortedBy { it.name }.toMutableList().apply {
            moveToFrontOfList(this, "download")
            moveToFrontOfList(this, "screenshots")
            moveToFrontOfList(this, "camera")

            add(0,
                AlbumInfo(
                    bucketId = Long.MAX_VALUE,
                    recentId = maxByOrNull { it.recentTime }?.recentId ?: 0,
                    name = resourceProvider.getString(R.string.phone_all_photos),
                    recentTime = maxByOrNull { it.recentTime }?.recentTime ?: 0,
                    photoCounts = map { it.photoCounts }.reduce { sum, count -> sum + count }
                )
            )
        }.map {
            DeviceAssetImageAlbum(
                id = it.bucketId.toString(),
                name = it.name,
                thumbnail = Uri.withAppendedPath(uriExternal, it.recentId.toString()).toString(),
                photoCounts = it.photoCounts
            )
        }
    }

    fun getAlbumDetailsGroupByDay(albumId: String): List<List<DeviceAssetImage>> {
        val result = mutableListOf<List<DeviceAssetImage>>()
        var subList = mutableListOf<DeviceAssetImage>()
        var preDayTime = Long.MIN_VALUE
        val dayUnit = 1000 * 60 * 60 * 24

//        return listOf(getAlbumDetails(albumId))

        //forEach가 아닌 fold로 구현 할 수 있을 것도 같은데... groupBy로 하면 되는데 내부적으로 map을 사용하므로 속도, 정렬의 문제가 생기...
        getAlbumDetails(albumId).forEach {
            val dayTime = it.milliseconds / dayUnit
            if (dayTime != preDayTime) {
                preDayTime = dayTime
                subList = mutableListOf()
                result.add(subList)
            }
            subList.add(it)
        }

        return result
    }

    private fun getAlbumDetails(albumId: String): List<DeviceAssetImage> {
        return contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.MIME_TYPE,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.DATE_TAKEN,
                MediaStore.Images.Media.WIDTH,
                MediaStore.Images.Media.HEIGHT,
                MediaStore.Images.Media.ORIENTATION,
            ),
            when (albumId) {
                Long.MAX_VALUE.toString() -> null
                else -> "${MediaStore.Images.Media.BUCKET_ID} ==?"
            },
            when (albumId) {
                Long.MAX_VALUE.toString() -> null
                else -> arrayOf(albumId)
            },
            null
        )?.use { cursor ->
            initDateCorrection()

            val uriExternalText = uriExternal.toString()

            cursor.moveToFirst()
            val idxId = cursor.getColumnIndex(MediaStore.Images.Media._ID)
            val idxMimeType = cursor.getColumnIndex(MediaStore.Images.Media.MIME_TYPE)
            val idxDateTaken = cursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN)
            val idxDateAdded = cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED)
            val idxWidth = cursor.getColumnIndex(MediaStore.Images.Media.WIDTH)
            val idxHeight = cursor.getColumnIndex(MediaStore.Images.Media.HEIGHT)
            val idxOrientation = cursor.getColumnIndex(MediaStore.Images.Media.ORIENTATION)

            val isCorrectPredictUri = cursor.getLong(idxId).toString().let {
                Uri.withAppendedPath(uriExternal, it).toString() == "$uriExternalText/$it"
            }

            val deviceAssetImageList: MutableList<DeviceAssetImage> = mutableListOf()
            do {
                if (!isAllowedImageFormat(cursor.getString(idxMimeType))) continue
                if (cursor.getInt(idxWidth) < 1 || cursor.getInt(idxHeight) < 1) continue

                val idxText = cursor.getLong(idxId).toString()
                val deviceAssetImage = DeviceAssetImage(
                    id = idxText,
                    type = AssetImageType.Device,
                    width = cursor.getInt(idxWidth).toFloat(),
                    height = cursor.getInt(idxHeight).toFloat(),
                    thumbnailUri = when (isCorrectPredictUri) {
                        true -> "$uriExternalText/$idxText"
                        false -> Uri.withAppendedPath(uriExternal, idxText).toString()
                    },
                    milliseconds = run {
                        val dateTaken = if (idxDateTaken == -1) 0 else cursor.getLong(idxDateTaken)
                        val dateAdded = if (idxDateAdded == -1) 0 else cursor.getLong(idxDateAdded)
                        getImageTime(dateTaken = dateTaken, dateAdded = dateAdded)
                    },
                    orientation = when (if (idxOrientation == -1) 0 else cursor.getInt(idxOrientation)) {
                        0 -> ExifInterface.ORIENTATION_NORMAL
                        90 -> ExifInterface.ORIENTATION_ROTATE_90
                        180 -> ExifInterface.ORIENTATION_ROTATE_180
                        270 -> ExifInterface.ORIENTATION_ROTATE_270
                        else -> ExifInterface.ORIENTATION_UNDEFINED
                    },
                )
                deviceAssetImageList.add(deviceAssetImage)
            } while (cursor.moveToNext())

            deviceAssetImageList.sorted()
        } ?: listOf()
    }

    data class ImageInfo(
        val localPath: String = "/no_name",
        val dateAdded: Long = 0L
    )

    fun getImageInfos(idList: List<String>): HashMap<String, ImageInfo> {
        initDateCorrection()
        return HashMap<String, ImageInfo>().apply {
            idList.asSequence().windowed(32, 32, true).map {
                getImageInfosInternal(it)
            }.map {
                putAll(it)
            }
        }
    }

    fun rxGetImageInfos(idList: List<String>): HashMap<String, ImageInfo> {
        initDateCorrection()
        return HashMap<String, ImageInfo>().apply {
            idList.windowed(32, 32, true).toObservable()
                .subscribeOn(schedulerProvider.io)
                .flatMap(
                    {
                        Observable.fromCallable {
                            getImageInfosInternal(it)
                        }.subscribeOn(schedulerProvider.io)
                    },
                    false,
                    3
                )
                .blockingForEach {
                    putAll(it)
                }
        }
    }

    private fun createQuestionMarks(idList: List<String>): String {
        return idList.fold(StringBuilder("?")) { sb, _ -> sb.append(", ?") }.toString()
    }

    private fun createItemInfoProjection(): Array<String> {
        return when (Build.VERSION.SDK_INT) {
            in Build.VERSION_CODES.BASE..Build.VERSION_CODES.P -> arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATA,
            )
            else -> arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.RELATIVE_PATH,
            )
        }
    }

    private fun getImageInfosInternal(idList: List<String>): HashMap<String, ImageInfo> {
        return HashMap<String, ImageInfo>().apply {
            contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                createItemInfoProjection(),
                buildString {
                    append(MediaStore.Images.Media._ID)
                    append(" IN ")
                    append("(${createQuestionMarks(idList)})")
                },
                idList.toTypedArray(),
                null
            )?.use { cursor ->
                cursor.moveToFirst()
                val idColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                val dataAddedColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED)
                do {
                    ImageInfo(
                        localPath = when (Build.VERSION.SDK_INT) {
                            in Build.VERSION_CODES.BASE..Build.VERSION_CODES.P -> getLocalPathVerP(cursor)
                            else -> getLocalPath(cursor)
                        },
                        dateAdded = fixInvalidDate(cursor.getLong(dataAddedColumnIndex))
                    ).let {
                        put(cursor.getLong(idColumnIndex).toString(), it)
                    }
                } while (cursor.moveToNext())
            }
        }
    }

    private fun getLocalPathVerP(cursor: Cursor): String {
        return cursor.getColumnIndex(MediaStore.Images.Media.DATA).let { index ->
            if (index == -1) "" else cursor.getString(index).substringBeforeLast(File.separator, "")
        }.let {
            it + File.separator + cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME))
        }
    }

    private fun getLocalPath(cursor: Cursor): String {
        return cursor.getColumnIndex(MediaStore.Images.Media.RELATIVE_PATH).let { index ->
            if (index == -1) "" else cursor.getString(index)
        }.let {
            it + File.separator + cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME))
        }
    }
}


