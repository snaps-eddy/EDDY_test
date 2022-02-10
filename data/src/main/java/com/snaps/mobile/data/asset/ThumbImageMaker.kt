package com.snaps.mobile.data.asset

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.snaps.mobile.data.util.ExifUtil
import java.io.File
import java.io.IOException
import java.security.MessageDigest
import javax.inject.Inject

class ThumbImageMaker @Inject constructor(
    private val context: Context,
    private val exifUtil: ExifUtil,
) {
    fun create(imageUri: String): Pair<Bitmap, ExifUtil.Info> {
        return exifUtil.getInfo(imageUri).let { exifInfo ->
            createBitmapWithoutOt(
                uriText = imageUri,
                orientationAngle = exifInfo.orientationAngle
            ).let {
                Pair(it, exifInfo)
            }
        }
    }

    private fun createBitmapWithoutOt(
        uriText: String,
        orientationAngle: Int
    ): Bitmap {
        return Glide.with(context)
            .asBitmap()
            .load(uriText)
            .skipMemoryCache(false)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .disallowHardwareConfig()
            .fitCenter()
            .override(800, 800).apply {
                if (orientationAngle != 0) transform(RotateTransformation(orientationAngle * -1))
            }
            .submit()
            .get()
    }

    // https://futurestud.io/tutorials/glide-how-to-rotate-images
    internal class RotateTransformation(
        private val rotateRotationAngle: Int
    ) : BitmapTransformation() {
        override fun transform(
            pool: BitmapPool,
            toTransform: Bitmap,
            outWidth: Int,
            outHeight: Int
        ): Bitmap {
            return Bitmap.createBitmap(
                toTransform,
                0,
                0,
                toTransform.width,
                toTransform.height,
                Matrix().apply { postRotate(rotateRotationAngle.toFloat()) },
                true
            )
        }

        override fun updateDiskCacheKey(messageDigest: MessageDigest) {
            messageDigest.update("rotate$rotateRotationAngle".toByteArray())
        }
    }
}