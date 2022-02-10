package com.snaps.mobile.presentation.editor.sketch.itemview

import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.ViewParent
import android.widget.CheckBox
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.core.view.ViewCompat
import androidx.core.view.updateLayoutParams
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.request.RequestOptions
import com.snaps.mobile.presentation.editor.R
import com.snaps.mobile.presentation.editor.sketch.SceneObjectImageBitmapTransformation
import com.snaps.mobile.presentation.editor.sketch.custom.*
import com.snaps.mobile.presentation.editor.sketch.model.ImageMovingData
import com.snaps.mobile.presentation.editor.sketch.model.SceneItem
import com.snaps.mobile.presentation.editor.sketch.model.SceneObjectItem
import com.snaps.mobile.presentation.editor.utils.KotlinEpoxyHolder
import java.security.MessageDigest
import java.util.*

@EpoxyModelClass
abstract class SceneItemSpreadView : EpoxyModelWithHolder<SceneItemSpreadViewHolder>() {

    override fun getDefaultLayout(): Int = R.layout.item_scene_spread

    @EpoxyAttribute
    lateinit var sceneItem: SceneItem

    @EpoxyAttribute
    lateinit var viewIndex: String

    @EpoxyAttribute
    var targetScene: Boolean = false

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    var onImageDrop: ((ImageMovingData, ImageMovingData) -> Unit)? = null

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    var onSceneSelect: ((String, Boolean) -> Unit)? = null

    override fun bind(holder: SceneItemSpreadViewHolder) {
        with(holder) {
            contentsContainer.updateLayoutParams<RelativeLayout.LayoutParams> {
                this.width = sceneItem.getDrawWidth()
                this.height = sceneItem.getDrawHeight()
            }

            focusIndicator.text = viewIndex
            focusIndicator.isChecked = targetScene
            focusIndicator.setOnClickListener {
                onSceneSelect?.invoke(sceneItem.drawIndex, focusIndicator.isChecked)
            }
            objectContainer.removeAllViews()
            sceneItem.sceneObjects.forEach { sceneObject ->
                when (sceneObject) {
                    is SceneObjectItem.Image -> renderImageObject(holder, sceneObject)
                    is SceneObjectItem.Background -> renderBackground(holder, sceneObject)
                    is SceneObjectItem.Text -> renderText(holder, sceneObject)
                    is SceneObjectItem.Sticker -> renderSticker(holder, sceneObject)
                }
            }
        }
    }

    private fun renderBackground(holder: SceneItemSpreadViewHolder, background: SceneObjectItem.Background) {
        with(holder) {
            when (background) {
                is SceneObjectItem.Background.Color -> contentsBackground.setBackgroundColor(background.bgColor)
                is SceneObjectItem.Background.Image -> {
                    val requestOptions = RequestOptions().apply {
                        diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        skipMemoryCache(false)
                        format(DecodeFormat.PREFER_RGB_565)
                        disallowHardwareConfig()
//                        override()  //TODO(배경도 크기 제한해야 하나?)
                    }
                    background.middleImagePath?.apply {
                        glide
                            .asBitmap()
                            .load(this)
                            .apply(requestOptions)
                            .into(contentsBackground)
                    }
                }
            }
        }
    }

    private fun renderImageObject(holder: SceneItemSpreadViewHolder, image: SceneObjectItem.Image) {
        with(holder) {
            val iv = (imageViewPool.poll() ?: SceneObjectImageView(objectContainer.context).apply {
                this.id = ViewCompat.generateViewId()
            }).also {
                objectContainer.addView(it)
            }

            iv.apply {
                updateLayoutParams<FrameLayout.LayoutParams> {
                    x = image.getDrawX()
                    y = image.getDrawY()
                    width = image.getDrawWidth()
                    height = image.getDrawHeight()
                }
                tag = image.drawIndex
            }

            image.content?.run {
                iv.landingItemListener = { fromDropMovingData ->
                    onImageDrop?.invoke(
                        fromDropMovingData, ImageMovingData(this.imgSeq, sceneItem.drawIndex, image.drawIndex)
                    )
                }

                val requestOptions = RequestOptions().apply {
                    if (image.border?.isMask == false) {
                        placeholder(ColorDrawable(Color.argb(153, 218, 218, 218)))
                    }
                    diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    skipMemoryCache(false)
                    format(DecodeFormat.PREFER_RGB_565)
                    disallowHardwareConfig()
                    transform(getImageObjectTransformation(glide, image))
                    override(image.getDownSampleSize(false))
                }

                iv.alpha = image.alpha
                glide
                    .asBitmap()
                    .load(thumbnailUri)
                    .apply(requestOptions)
                    .into(SketchImageTarget.Default(iv, image))

                iv.isHapticFeedbackEnabled = true
                iv.setOnLongClickListener { v ->
                    v.performHapticFeedback(
                        HapticFeedbackConstants.LONG_PRESS,
                        HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
                    )
//                    val clipData = UserImageMovingData(
//                        imageId = this.imgSeq,
//                        sceneDrawIndex = sceneItem.drawIndex,
//                        sceneObjectDrawIndex = image.drawIndex
//                    )
//                    val item = ClipData.Item(Gson().toJson(clipData))
//                    val dragData = ClipData(
//                        image.drawIndex,
//                        arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN),
//                        item
//                    )
//
//                    val shadow = SceneObjectImageViewShadowBuilder(v)
//                    ViewCompat.startDragAndDrop(v, dragData, shadow, shadow, 0)
                }
            }
        }
    }

    private fun getImageObjectTransformation(
        glideReq: RequestManager,
        image: SceneObjectItem.Image
    ): MultiTransformation<Bitmap> {
        return mutableListOf<BitmapTransformation>().apply {
            add(SceneObjectImageBitmapTransformation.Rotate(image.uiAngle, image.isValidRatio()))
            image.border?.let {
                if (image.border.isMask) add(SceneObjectImageBitmapTransformation.Mask(glideReq, image))
                else if (image.border.isSingleColor) add(SceneObjectImageBitmapTransformation.Frame(image))
            }
        }.let {
            MultiTransformation(it)
        }
    }

    private fun renderText(holder: SceneItemSpreadViewHolder, text: SceneObjectItem.Text) {
        with(holder) {
            val textImageView = textImageViewPool.poll() ?: SceneObjectTextView(objectContainer.context).apply {
                this.id = ViewCompat.generateViewId()
            }
            objectContainer.addView(textImageView)
            textImageView.apply {
                updateLayoutParams<FrameLayout.LayoutParams> {
                    x = text.getDrawX()
                    y = text.getDrawY()
                    width = text.getDrawWidth()
                    height = text.getDrawHeight()
                }
            }

            //TODO:: 미구현
        }
    }

    private fun renderSticker(holder: SceneItemSpreadViewHolder, sticker: SceneObjectItem.Sticker) {
        with(holder) {
            val stickerImageView = stickerImageViewPool.poll() ?: SceneObjectStickerView(objectContainer.context).apply {
                this.id = ViewCompat.generateViewId()
            }
            objectContainer.addView(stickerImageView)
            stickerImageView.apply {
                updateLayoutParams<FrameLayout.LayoutParams> {
                    x = sticker.getDrawX()
                    y = sticker.getDrawY()
                    width = sticker.getDrawWidth()
                    height = sticker.getDrawHeight()
                    rotation = sticker.angle.toFloat()
                }
            }
            val requestOptions = RequestOptions().apply {
                diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                skipMemoryCache(false)
                format(DecodeFormat.PREFER_RGB_565)
                disallowHardwareConfig()
                override(sticker.getDrawWidth(), sticker.getDrawHeight())
            }

            stickerImageView.alpha = sticker.alpha
            glide
                .asBitmap()
                .load(sticker.middleImagePath)
                .apply(requestOptions)
                .into(stickerImageView)
        }
    }

    override fun bind(holder: SceneItemSpreadViewHolder, previouslyBoundModel: EpoxyModel<*>) {
        val previous = previouslyBoundModel as SceneItemSpreadView
        if (previous.targetScene == targetScene) {
            super.bind(holder, previouslyBoundModel)
        } else {
            holder.focusIndicator.isChecked = targetScene
        }
    }

    override fun unbind(holder: SceneItemSpreadViewHolder) {
        with(holder) {
            focusIndicator.setOnClickListener(null)
        }
    }
}

class SceneItemSpreadViewHolder(parent: ViewParent) : KotlinEpoxyHolder() {
    val sceneContainer by bind<SceneUIContainer>(R.id.scene_container)
    val contentsContainer by bind<RelativeLayout>(R.id.contents_container)
    val objectContainer by bind<FrameLayout>(R.id.object_container)
    val contentsBackground by bind<ImageView>(R.id.contents_background)
    val focusIndicator by bind<CheckBox>(R.id.focus_indicator)
    val pageDividerSkin by bind<ImageView>(R.id.page_divider_skin)
    val glide = Glide.with((parent as View).context)
    val dropIndicator by bind<View>(R.id.drop_indicator)

    val imageViewPool: Queue<SceneObjectImageView> = LinkedList()
    val textImageViewPool: Queue<SceneObjectTextView> = LinkedList()
    val stickerImageViewPool: Queue<SceneObjectStickerView> = LinkedList()
}