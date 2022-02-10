package com.snaps.mobile.presentation.editor.sketch

import android.graphics.*
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.snaps.common.utils.constant.Config
import com.snaps.common.utils.log.Dlog
import com.snaps.mobile.presentation.editor.sketch.model.SceneObjectItem
import java.security.MessageDigest

class SceneObjectImageBitmapTransformation {
    companion object {
        const val id = "com.snaps.mobile.presentation.editor.sketch.SceneObjectImageBitmapTransformation"
    }

    class Scale(
        private val scale: Float
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
                outWidth,
                outHeight,
                Matrix().apply {
                    postScale(scale, scale)
                },
                true
            )
        }

        fun getId(): String {
            return buildString {
                append(id)
                append("Scale")
                append(scale)
            }
        }

        override fun equals(o: Any?) = if (o is Scale) getId() == o.getId() else false
        override fun hashCode() = getId().hashCode()
        override fun updateDiskCacheKey(messageDigest: MessageDigest) = messageDigest.update(getId().toByteArray())
    }

    class Rotate(
        private val angle: Int,
        private val isValidRatio: Boolean
    ) : BitmapTransformation() {

        override fun transform(pool: BitmapPool, toTransform: Bitmap, outWidth: Int, outHeight: Int): Bitmap {
            if (angle == 0) return toTransform
            return Bitmap.createBitmap(
                toTransform,
                0,
                0,
                toTransform.width,
                toTransform.height,
                Matrix().apply { postRotate(angle.toFloat()) },
                true
            ).apply {
                if (Config.isDevelopVersion()) {
                    if (!isValidRatio) {
                        Canvas(this).apply {
                            val paint = Paint().apply {
                                color = Color.MAGENTA
                                strokeWidth = 20f
                            }
                            drawLine(0f, 0f, width.toFloat(), height.toFloat(), paint)
                            drawLine(width.toFloat(), 0f, 0f, height.toFloat(), paint)
                        }
                    }
                }
            }
        }

        fun getId(): String {
            return buildString {
                append(Companion.id)
                append("Rotate")
                append(angle)
            }
        }

        override fun equals(o: Any?) = if (o is Rotate) getId() == o.getId() else false
        override fun hashCode() = getId().hashCode()
        override fun updateDiskCacheKey(messageDigest: MessageDigest) = messageDigest.update(getId().toByteArray())
    }

    class Mask(
        private val glideReq: RequestManager,
        private val image: SceneObjectItem.Image
    ) : BitmapTransformation() {

        override fun transform(pool: BitmapPool, toTransform: Bitmap, outWidth: Int, outHeight: Int): Bitmap {
            val maskBitmap = image.border?.let {
                if (it.maskPath.isNotEmpty()) loadImage(it.maskPath) else null
            } ?: return toTransform

            val visibleRect = Utils().getVisibleRect(
                image = image,
                toTransformWidth = toTransform.width,
                toTransformHeight = toTransform.height,
                outWidth = outWidth,
                outHeight = outHeight
            ) ?: return toTransform

            val resultBitmap = Bitmap.createBitmap(toTransform.width, toTransform.height, Bitmap.Config.ARGB_8888)
            resultBitmap.apply {
                setHasAlpha(true)
                Canvas(this).apply {
                    drawBitmap(
                        maskBitmap,
                        Rect(0, 0, maskBitmap.width, maskBitmap.height),
                        visibleRect,
                        null
                    )
                    drawBitmap(
                        toTransform,
                        0f, 0f,
                        Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN) }
                    )
                }
            }
            return resultBitmap
        }

        private fun loadImage(maskPath: String): Bitmap? {
            return try {
                glideReq
                    .asBitmap()
                    .load(maskPath)
                    .skipMemoryCache(true) //주의!! 반드시 true
                    .diskCacheStrategy(DiskCacheStrategy.NONE)  //주의!! 반드시 NONE
                    .submit()
                    .get()
            } catch (e: Exception) {
                Dlog.e(e)
                null
            }
        }

        fun getId(): String {
            return buildString {
                append(id)
                append("Mask")
                image.border?.let {
                    append(it.maskPath)
                } ?: "borderisnull"
                image.innerImage?.let {
                    append(it.x.toInt())
                    append(it.y.toInt())
                    append(it.width.toInt())
                    append(it.height.toInt())
                    append(image.uiAngle)
                } ?: "innerImageisnull"
            }
        }

        override fun equals(o: Any?) = if (o is Mask) getId() == o.getId() else false
        override fun hashCode() = getId().hashCode()
        override fun updateDiskCacheKey(messageDigest: MessageDigest) = messageDigest.update(getId().toByteArray())
    }

    class Frame(
        private val image: SceneObjectItem.Image
    ) : BitmapTransformation() {

        override fun transform(pool: BitmapPool, toTransform: Bitmap, outWidth: Int, outHeight: Int): Bitmap {
            val (frameThickness, frameColor, frameAlpha) = image.border?.let {
                Utils().getScale(image)?.let { scale ->
                    Triple(
                        (it.singleThickness * scale * image.scaleFactor).toInt(),
                        Color.parseColor(it.singleColor),
                        (it.singleAlpha * 255).toInt()
                    )
                }
            } ?: return toTransform

            val visibleRect = Utils().getVisibleRect(
                image = image,
                toTransformWidth = toTransform.width,
                toTransformHeight = toTransform.height,
                outWidth = outWidth,
                outHeight = outHeight
            ) ?: return toTransform

            val resultBitmap = Bitmap.createBitmap(toTransform.width, toTransform.height, Bitmap.Config.ARGB_8888)
            resultBitmap.apply {
                setHasAlpha(true)
                Canvas(this).apply {
                    drawBitmap(toTransform, 0f, 0f, null)

                    val paint = Paint().apply {
                        color = frameColor
                        style = Paint.Style.STROKE
                        alpha = frameAlpha
                    }
                    for (i in 0..frameThickness) {
                        drawRect(
                            visibleRect.left + i.toFloat(),
                            visibleRect.top + i.toFloat(),
                            visibleRect.right - i.toFloat(),
                            visibleRect.bottom - i.toFloat(),
                            paint
                        )
                    }
                }
            }
            return resultBitmap
        }

        fun getId(): String {
            return buildString {
                append(id)
                append("Frame")
                image.border?.let {
                    append(it.singleAlpha)
                    append(it.singleColor)
                    append(it.singleThickness)
                } ?: "borderisnull"
                image.innerImage?.let {
                    append(it.x.toInt())
                    append(it.y.toInt())
                    append(it.width.toInt())
                    append(it.height.toInt())
                    append(image.uiAngle)
                } ?: "innerImageisnull"
            }
        }

        override fun equals(o: Any?) = if (o is Frame) getId() == o.getId() else false
        override fun hashCode() = getId().hashCode()
        override fun updateDiskCacheKey(messageDigest: MessageDigest) = messageDigest.update(getId().toByteArray())
    }

    private class Utils {
        fun getScale(image: SceneObjectItem.Image): Float? {
            return image.innerImage?.let { innerImage ->
                image.content?.run {
                    when (orientationAngle) {
                        90, 270 -> image.width / innerImage.height
                        else -> image.width / innerImage.width
                    }
                }
            }
        }

        fun getVisibleRect(
            image: SceneObjectItem.Image,
            toTransformWidth: Int,
            toTransformHeight: Int,
            outWidth: Int,
            outHeight: Int
        ): Rect? {
            val innerImage = image.innerImage ?: return null
            val scalePosition = when (image.uiAngle) {
                90, 270 -> innerImage.width / toTransformHeight
                else -> innerImage.width / toTransformWidth
            }
            val scaleSize = image.content?.run {
                when (orientationAngle) {
                    90, 270 -> image.width / innerImage.height
                    else -> image.width / innerImage.width
                }
            } ?: return null
            val left = innerImage.x * -1 / scalePosition
            val top = innerImage.y * -1 / scalePosition
            return Rect(
                left.toInt(),
                top.toInt(),
                (left + outWidth * scaleSize).toInt(),
                (top + outHeight * scaleSize).toInt()
            )
        }
    }
}