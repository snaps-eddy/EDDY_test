package com.snaps.mobile.data.asset

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.snaps.common.utils.log.Dlog
import com.snaps.mobile.domain.save.Filter
import java.io.File
import java.io.IOException
import java.math.BigInteger
import java.security.MessageDigest
import java.util.*
import javax.inject.Inject

class ImageFilterFileStorage @Inject constructor(
    private val context: Context,
) {
    private val tag = ImageFilterFileStorage::class.java.simpleName
    private val imageDirName = "filtered_image"
    private val imageMaxFileCount = 256
    private val imageDeleteFileAmount = imageMaxFileCount / 10
    private val previewImageDirName = "preview_filtered_image"

    fun cleanUp() {
        File(context.externalCacheDir, imageDirName).apply {
            if (isDirectory) mangeStorageSpace(this)
        }
        File(context.externalCacheDir, previewImageDirName).apply {
            if (isDirectory) deleteRecursively()
        }
    }

    private fun mangeStorageSpace(dir: File) {
        dir.listFiles()?.let {
            if (it.size < imageMaxFileCount) return
            Arrays.sort(it) { o1, o2 ->
                o1.lastModified().compareTo(o2.lastModified())
            }
            for (i in 0 until imageDeleteFileAmount) {
                it[i].delete()
            }
        }
    }

    fun getFileUri(
        uriText: String,
        filterName: String
    ) = getFileUri(uriText, dirName = imageDirName, filterName = filterName)

    fun getPreviewFileUri(
        uriText: String,
        filterName: String
    ) = getFileUri(uriText, dirName = previewImageDirName, filterName = filterName)

    private fun getFileUri(
        uriText: String,
        dirName: String,
        filterName: String
    ): String? {
        return File(context.externalCacheDir, dirName).let { dir ->
            File(dir, createFileName(uriText, filterName)).let {
                if (it.isFile && it.length() > 0) Uri.fromFile(it).toString() else null
            }
        }
    }

    private fun createFileName(
        uriText: String,
        filterName: String
    ): String {
        return Uri.parse(uriText).let { uri ->
            safeLet(uri.path, uri.lastPathSegment) { path, lastPath ->
                path.substring(0, path.length - lastPath.length).md5().let {
                    String.format("%s_%s_%s", it, filterName, lastPath)
                }
            }
        } ?: uriText.plus(filterName)
    }

    //https://stackoverflow.com/questions/35513636/multiple-variable-let-in-kotlin
    inline fun <T1 : Any, T2 : Any, R : Any> safeLet(p1: T1?, p2: T2?, block: (T1, T2) -> R?): R? {
        return if (p1 != null && p2 != null) block(p1, p2) else null
    }

    private fun String.md5() = MessageDigest.getInstance("MD5").digest(toByteArray()).let {
        BigInteger(1, it).toString(16).padStart(32, '0')
    }

    fun savePriview(
        uriText: String,
        bitmap: Bitmap,
        filterName: String
    ) = save(uriText = uriText, bitmap = bitmap, quality = 80, dirName = previewImageDirName, filterName = filterName)

    fun save(
        uriText: String,
        bitmap: Bitmap,
        filterName: String
    ) = save(uriText = uriText, bitmap = bitmap, quality = 90, dirName = imageDirName, filterName = filterName)

    private fun save(
        uriText: String,
        bitmap: Bitmap,
        quality: Int,
        dirName: String,
        filterName: String
    ): String? {
        return File(context.externalCacheDir, dirName).let { dir ->
            if (!dir.isDirectory) dir.mkdir()
            File(dir, createFileName(uriText, filterName)).let {
                when (saveFile(bitmap, it, quality)) {
                    true -> Uri.fromFile(it).toString()
                    false -> null
                }
            }
        }
    }

    private fun saveFile(bitmap: Bitmap, file: File, quality: Int): Boolean {
        return file.outputStream().buffered().use { out ->
            when (bitmap.hasAlpha()) {
                true -> bitmap.compress(Bitmap.CompressFormat.PNG, quality, out)
                false -> bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)
            }
        }
    }
}