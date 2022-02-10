package com.snaps.mobile.presentation.editor.sketch.itemview

import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewParent
import android.widget.*
import androidx.core.view.ViewCompat
import androidx.core.view.children
import androidx.core.view.updateLayoutParams
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.snaps.mobile.presentation.editor.R
import com.snaps.mobile.presentation.editor.sketch.SceneController
import com.snaps.mobile.presentation.editor.sketch.SceneObjectImageBitmapTransformation
import com.snaps.mobile.presentation.editor.sketch.custom.*
import com.snaps.mobile.presentation.editor.sketch.model.SceneObjectItem
import com.snaps.mobile.presentation.editor.utils.KotlinEpoxyHolder
import java.security.MessageDigest
import java.util.*

@EpoxyModelClass
abstract class SceneItemPageBlankView : EpoxyModelWithHolder<SceneItemPageBlankViewHolder>() {

    override fun getDefaultLayout(): Int = R.layout.item_scene_page_blank

    @EpoxyAttribute
    var sceneWidth: Int = 0

    @EpoxyAttribute
    var sceneHeight: Int = 0

    @EpoxyAttribute
    lateinit var side: SceneController.ScenePageSide

    @EpoxyAttribute
    lateinit var sceneObjectItems: List<SceneObjectItem>

    @EpoxyAttribute
    lateinit var sceneDrawIndex: String

    override fun bind(holder: SceneItemPageBlankViewHolder) {
        with(holder) {
            drawAll(this)
        }
    }

    override fun unbind(holder: SceneItemPageBlankViewHolder) {
        with(holder) {
            objectContainer.children
                .forEach {
                    when (it) {
                        is SceneObjectImageView -> {
                            glide.clear(it.sourceView)
                            it.tag = null
                            imageViewPool.add(it)
                        }
                        is SceneObjectTextView -> {
                            glide.clear(it.textImageView)
                            textImageViewPool.add(it)
                        }
                    }
                }
            glide.clear(contentsBackground)
        }
    }

    private fun drawAll(holder: SceneItemPageBlankViewHolder) {
        with(holder) {
            drawSceneContainer(this)

            drawBottomTool(this)

            drawSceneObjects(this)
        }
    }

    private fun drawSceneContainer(holder: SceneItemPageBlankViewHolder) {
        with(holder) {
            sceneContainer.apply {
                this.tag = sceneDrawIndex
                this.updateLayoutParams<RelativeLayout.LayoutParams> {
                    this.width = sceneWidth
                    this.height = sceneHeight
                    this.setMargins(side.margins.left, side.margins.top, side.margins.right, side.margins.bottom)
                    this.removeRule(side.removeRule)
                    this.addRule(side.addRule)
                }
            }
        }
    }

    private fun drawBottomTool(holder: SceneItemPageBlankViewHolder) {
        with(holder) {
            focusIndicator.text = focusIndicator.context.getString(R.string.inner_paper)
            layoutChanger.visibility = View.INVISIBLE
            movePageButton.visibility = View.INVISIBLE
            focusIndicator.isEnabled = false
        }
    }

    private fun drawSceneObjects(holder: SceneItemPageBlankViewHolder) {
        with(holder) {
            objectContainer.removeAllViews()
            sceneObjectItems.forEach {
                when (it) {
                    is SceneObjectItem.Background -> renderBackground(this, it)
                    is SceneObjectItem.Image -> renderImageObject(this, it)
//                    is SceneObjectItem.Text -> renderText(this, it)  //기획팀 요청 사항(2021.10.08): 다국어 문제로 표시 안함
                    is SceneObjectItem.Sticker -> renderSticker(this, it)
                }
            }
        }
    }

    private fun renderBackground(holder: SceneItemPageBlankViewHolder, background: SceneObjectItem.Background) {
        with(holder) {
            when (background) {
                is SceneObjectItem.Background.Color -> contentsBackground.setBackgroundColor(background.bgColor)
                is SceneObjectItem.Background.Image -> {
                    val requestOptions = RequestOptions().apply {
                        diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        skipMemoryCache(false)
                        format(DecodeFormat.PREFER_RGB_565)
                        disallowHardwareConfig()
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

    private fun renderImageObject(holder: SceneItemPageBlankViewHolder, image: SceneObjectItem.Image) {
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
            fillImage(this, iv, image)
        }
    }

    private fun fillImage(holder: SceneItemPageBlankViewHolder, iv: SceneObjectImageView, image: SceneObjectItem.Image) {
        with(holder) {
            iv.tag = image.drawIndex
            image.content?.run {
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
                    .load(image.filter?.imageUri ?: thumbnailUri)
                    .apply(requestOptions)
                    .transition(BitmapTransitionOptions.withCrossFade(1000))
                    .into(SketchImageTarget.Default(iv, image))
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

    private fun renderText(holder: SceneItemPageBlankViewHolder, text: SceneObjectItem.Text) {
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
            if (text.text.isBlank()) {
//                textImageView.setBackgroundColor(Color.BLACK)
            } else {
                val scale = if (text.defaultStyle.fontSizePx >= 12) 1 else 2
                val requestOptions = RequestOptions().apply {
                    diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    skipMemoryCache(false)
                    format(DecodeFormat.PREFER_RGB_565)
                    disallowHardwareConfig()
                    if (scale != 1) transform(SceneObjectImageBitmapTransformation.Scale(1f / scale))
                }

                val requestUrl = textImageView.createRequestUrl(
                    text = text.text,
                    isSpine = false,
                    style = text.defaultStyle.toRawText(),
                    width = text.width,
                    height = text.height
                )
                glide
                    .asBitmap()
                    .load(requestUrl)
                    .apply(requestOptions)
                    .override(text.getDrawWidth() * scale, text.getDrawHeight() * scale)
                    .into(TextToImageTarget.Default(textImageView, text))
            }
        }
    }

    private fun renderSticker(holder: SceneItemPageBlankViewHolder, sticker: SceneObjectItem.Sticker) {
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

}

class SceneItemPageBlankViewHolder(parent: ViewParent) : KotlinEpoxyHolder() {
    val sceneContainer by bind<SceneUIContainer>(R.id.scene_container)
    val objectContainer by bind<FrameLayout>(R.id.object_container)
    val contentsBackground by bind<ImageView>(R.id.contents_background)
    val focusIndicator by bind<CheckBox>(R.id.focus_indicator)
    val glide = Glide.with((parent as View).context)
    val layoutChanger by bind<View>(R.id.btn_layout_change)
    val movePageButton by bind<Button>(R.id.btn_drag)

    val imageViewPool: Queue<SceneObjectImageView> = LinkedList()
    val textImageViewPool: Queue<SceneObjectTextView> = LinkedList()
    val stickerImageViewPool: Queue<SceneObjectStickerView> = LinkedList()
}

