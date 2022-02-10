package com.snaps.mobile.data.asset

import android.content.Context
import android.graphics.Bitmap
import android.util.Base64
import com.snaps.common.utils.log.Dlog
import java.io.File
import java.io.IOException
import java.util.*
import javax.inject.Inject

class ThumbImageStorage @Inject constructor(
    private val context: Context,
) {
    private val tag = ThumbImageStorage::class.java.simpleName

    private val dirName = "thumb_image"
    private val limitFileNameLength = 64

    fun cleanUp() {
        File(context.externalCacheDir, dirName).apply {
            if (isDirectory) deleteRecursively()
            mkdir()
        }
    }

    fun save(bitmap: Bitmap, fileNamePrefix: String, name: String): File? {
        return File(context.externalCacheDir, dirName).apply {
            if (!isDirectory) mkdir()
        }.let { dir ->
            createFileName(
                fileNamePrefix = fileNamePrefix,
                hasAlpha = bitmap.hasAlpha(),
                name = name
            ).let { fileName ->
                save(
                    bitmap = bitmap,
                    format = if (bitmap.hasAlpha()) Bitmap.CompressFormat.PNG else Bitmap.CompressFormat.JPEG,
                    file = File(dir, fileName)
                )
            }
        }
    }

    private fun createFileName(
        fileNamePrefix: String = "",
        hasAlpha: Boolean,
        name: String
    ): String {
        return name.reversed().let {
            it.substring(0, name.length.coerceAtMost(limitFileNameLength))
        }.let {
            Base64.encodeToString(it.toByteArray(), Base64.NO_WRAP or Base64.NO_PADDING)
        }.let { newFileName ->
            String.format("%s_%s_%s.%s", fileNamePrefix, newFileName, createRendomText(), if (hasAlpha) "png" else "jpg")
        }
    }

    private fun createRendomText() = "${System.currentTimeMillis()}_${Random().nextInt()}"

    private fun save(bitmap: Bitmap, format: Bitmap.CompressFormat, file: File): File? {
        try {
            file.outputStream().buffered().use { out ->
                bitmap.compress(format, 90, out)
            }
        } catch (e: IOException) {
            Dlog.e(tag, e)
            return null
        }
        return file
    }
}