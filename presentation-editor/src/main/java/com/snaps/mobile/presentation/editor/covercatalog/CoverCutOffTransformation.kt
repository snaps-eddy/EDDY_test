package com.snaps.mobile.presentation.editor.covercatalog

import android.graphics.Bitmap
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import java.security.MessageDigest

class CoverCutOffTransformation(val range: CutRange) : BitmapTransformation() {

    override fun transform(pool: BitmapPool, toTransform: Bitmap, outWidth: Int, outHeight: Int): Bitmap {
        when (range) {
            CutRange.StartToHalf -> {
                return Bitmap.createBitmap(
                    toTransform,
                    0,
                    0,
                    toTransform.width / 2,
                    toTransform.height,
                )
            }
            CutRange.HalfToEnd -> {
                return Bitmap.createBitmap(
                    toTransform,
                    toTransform.width / 2,
                    0,
                    toTransform.height,
                    toTransform.height,
                )
            }
        }
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(ID.toByteArray(CHARSET))
    }

    override fun hashCode(): Int {
        return ID.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return other is CoverCutOffTransformation
    }

    override fun toString(): String {
        return "CutOffFrontPageTransformation"
    }

    companion object {
        const val ID = "com.snaps.mobile.presentation.editor.covercatalog"
    }

    sealed class CutRange {
        object StartToHalf : CutRange()
        object HalfToEnd : CutRange()
    }

}