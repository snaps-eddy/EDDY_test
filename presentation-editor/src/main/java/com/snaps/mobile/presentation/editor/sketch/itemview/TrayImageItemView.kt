package com.snaps.mobile.presentation.editor.sketch.itemview

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.ColorDrawable
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.GridLayoutManager
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.request.RequestOptions
import com.snaps.mobile.presentation.editor.R
import com.snaps.mobile.presentation.editor.utils.KotlinEpoxyHolder
import com.snaps.mobile.presentation.editor.utils.dp
import java.security.MessageDigest

@EpoxyModelClass
abstract class TrayImageItemView : EpoxyModelWithHolder<TrayImageItemViewHolder>() {

    override fun getDefaultLayout(): Int = R.layout.item_tray_image

    @EpoxyAttribute
    lateinit var photoUrl: String

    @EpoxyAttribute
    lateinit var imgSeq: String

    @EpoxyAttribute
    var orientationAngle: Int = 0

    @EpoxyAttribute
    var onStage: Boolean = false

    @EpoxyAttribute
    var drawWidth: Int = 0

    @EpoxyAttribute
    var drawHeight: Int = 0

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    var onStartDragImage: ((Bitmap) -> Unit)? = null

    override fun bind(holder: TrayImageItemViewHolder) {
        drawAll(holder)
    }

    override fun bind(holder: TrayImageItemViewHolder, previouslyBoundModel: EpoxyModel<*>) {
        val previousModel = previouslyBoundModel as TrayImageItemView
        if (previousModel.imgSeq != imgSeq) {
            drawAll(holder)
            return
        }

        if (previousModel.onStage != onStage || previousModel.drawWidth != drawWidth) {
            drawRoot(holder)
            drawTrayPhoto(holder)
        }

        if (previousModel.photoUrl != photoUrl) {
            drawTrayPhoto(holder).also {
                fillImage(holder)
            }
        }
    }

    override fun unbind(holder: TrayImageItemViewHolder) {
        with(holder) {
            glide.clear(trayPhoto)
        }
    }

    private fun drawAll(holder: TrayImageItemViewHolder) {
        with(holder) {
            drawRoot(this)
            drawTrayPhoto(this).also {
                fillImage(this)
            }
        }
    }

    private fun drawRoot(holder: TrayImageItemViewHolder): ViewGroup {
        return with(holder) {
            root.apply {
                val padding = if (onStage) 8.dp() else 0
                root.updatePadding(padding, padding, padding, padding)
                root.updateLayoutParams<GridLayoutManager.LayoutParams> {
                    width = drawWidth
                    height = drawHeight
                }
            }
        }
    }

    private fun drawTrayPhoto(holder: TrayImageItemViewHolder): ImageView {
        return with(holder) {
            trayPhoto.apply {
                isHapticFeedbackEnabled = true
                setOnLongClickListener { v ->
                    v.performHapticFeedback(
                        HapticFeedbackConstants.LONG_PRESS,
                        HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
                    )
                    val snapshot = Bitmap.createBitmap(v.measuredWidth, v.measuredHeight, Bitmap.Config.ARGB_8888)
                    Canvas(snapshot).run {
                        v.draw(this)
                    }
                    onStartDragImage?.invoke(snapshot)
                    true
                }
            }
        }
    }

    private fun fillImage(holder: TrayImageItemViewHolder) {
        with(holder) {
            RequestOptions().apply {
                placeholder(ColorDrawable(Color.argb(153, 218, 218, 218)))
                diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                skipMemoryCache(false)
                format(DecodeFormat.PREFER_RGB_565)
                disallowHardwareConfig()
                transform(RotateTransformation(orientationAngle))
                override(150, 150)
            }.also {
                holder.glide
                    .load(photoUrl)
                    .apply(it)
                    .into(trayPhoto)
            }
        }
    }

    private class RotateTransformation(
        private val angle: Int
    ) : BitmapTransformation() {
        private val id = "TrayImageItemView.RotateTransformation".toByteArray()

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
            )
        }

        override fun equals(o: Any?) = o is RotateTransformation
        override fun hashCode() = id.hashCode()
        override fun updateDiskCacheKey(messageDigest: MessageDigest) = messageDigest.update("$id$angle".toByteArray())
    }
}

class TrayImageItemViewHolder(parent: ViewParent) : KotlinEpoxyHolder() {
    val root by bind<FrameLayout>(R.id.root)
    val trayPhoto by bind<ImageView>(R.id.tray_photo)
    val glide = Glide.with((parent as View))
}