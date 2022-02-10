package com.snaps.mobile.presentation.editor.sketch.custom

import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.core.view.*
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.request.RequestOptions
import com.snaps.mobile.domain.save.Scene
import com.snaps.mobile.presentation.editor.databinding.ViewSceneContentBinding
import com.snaps.mobile.presentation.editor.sketch.SceneObjectImageBitmapTransformation
import com.snaps.mobile.presentation.editor.sketch.itemview.SceneItemPageView
import com.snaps.mobile.presentation.editor.sketch.model.ImageMovingData
import com.snaps.mobile.presentation.editor.sketch.model.SceneItem
import com.snaps.mobile.presentation.editor.sketch.model.SceneObjectItem
import java.security.MessageDigest
import java.util.*

class SceneCoverContentView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val glide = Glide.with(context)
    private val imageViewPool: Queue<SceneObjectImageView> = LinkedList()
    private val textImageViewPool: Queue<SceneObjectTextView> = LinkedList()
    private val stickerImageViewPool: Queue<SceneObjectStickerView> = LinkedList()
    private val binding = ViewSceneContentBinding.inflate(LayoutInflater.from(context), this, true)

    var onImageDrop: ((ImageMovingData, ImageMovingData) -> Unit)? = null
    var onClickUserImage: ((String, String, String) -> Unit)? = null

    //커버 텍스트 중, 제목과 동기화 되어있는 텍스트의 클릭 처리
    var onClickText: ((Int) -> Unit)? = null

    //커버 텍스트 중, 사용자기 직접 입력 가능한 텍스트 클릭의 처리.
    var onClickUserText: ((String, String) -> Unit)? = null

    lateinit var sceneItem: SceneItem

    var isForCartThumbnail: Boolean = false

    fun setData(data: SceneItem) {
        sceneItem = data
        with(binding) {
            contentsContainer.updateLayoutParams<RelativeLayout.LayoutParams> {
                this.width = sceneItem.getDrawWidth()
                this.height = sceneItem.getDrawHeight()
                if (isForCartThumbnail) {
                    updateMarginsRelative(0, 0, 0, 0)
                }
            }
            objectContainer.removeAllViews()
            sceneItem.sceneObjects.forEach {
                when (it) {
                    is SceneObjectItem.Background -> renderBackground(it)
                    is SceneObjectItem.Image -> renderImageObject(it)
                    is SceneObjectItem.Sticker -> renderSticker(it)
                    is SceneObjectItem.Text -> renderText(it)
                }
            }

            if (!isForCartThumbnail) {
                if (sceneItem.type is Scene.Type.Cover) {
                    val cover = sceneItem.type as Scene.Type.Cover
                    qrCode.visibility = if (cover.isLeather) View.INVISIBLE else View.VISIBLE
                }
            }
            guideCenter.updateLayoutParams {
                this.width = sceneItem.getDrawMidWidth()
            }
        }
    }

    fun hideBottomController() {
        binding.bottomController.isVisible = false
    }

    fun takeSnapShot(): Bitmap {
        return Bitmap.createBitmap(sceneItem.getDrawWidth(), sceneItem.getDrawHeight(), Bitmap.Config.ARGB_8888).apply {
            val canvas = Canvas(this)
            binding.contentsContainer.draw(canvas)
        }
    }

    fun unbind() {
        binding.objectContainer.children
            .forEach {
                when (it) {
                    is SceneObjectImageView -> {
                        glide.clear(it.sourceView)
                        it.setOnLongClickListener(null)
                        it.setOnClickListener(null)
                        it.tag = null
                        imageViewPool.add(it)

                    }
                    is SceneObjectTextView -> {
                        glide.clear(it.textImageView)
                        it.setOnClickListener(null)
                        textImageViewPool.add(it)
                    }
                    is SceneObjectStickerView -> {
                        glide.clear(it)
                        stickerImageViewPool.add(it)
                    }
                }
            }
        glide.clear(binding.contentsBackground)
    }

    private fun renderBackground(background: SceneObjectItem.Background) {
        when (background) {
            is SceneObjectItem.Background.Color -> {
                binding.contentsBackground.setBackgroundColor(background.bgColor)
                glide.clear(binding.contentsBackground)
            }
            is SceneObjectItem.Background.Image -> {
                binding.contentsBackground.scaleType = ImageView.ScaleType.CENTER_CROP
                val requestOptions = RequestOptions().apply {
                    diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    skipMemoryCache(false)
                    format(DecodeFormat.PREFER_RGB_565)
                    disallowHardwareConfig()
                    override(400)
                }
                background.middleImagePath?.apply {
                    glide
                        .asBitmap()
                        .load(this)
                        .apply(requestOptions)
                        .into(binding.contentsBackground)
                }
            }
        }
    }

    private fun renderImageObject(image: SceneObjectItem.Image) {
        with(binding) {
            val iv = (imageViewPool.poll() ?: SceneObjectImageView(objectContainer.context).apply {
                this.id = ViewCompat.generateViewId()
            }).also {
                objectContainer.addView(it)
            }

            iv.apply {
                updateLayoutParams<LayoutParams> {
                    x = image.getDrawX()
                    y = image.getDrawY()
                    width = image.getDrawWidth()
                    height = image.getDrawHeight()
                    rotation = image.angle.toFloat()
                }
                tag = image.drawIndex
            }

            image.content?.run {
                iv.landingItemListener = { dropMovingData ->
                    onImageDrop?.invoke(
                        dropMovingData, ImageMovingData(
                            this.imgSeq, sceneItem.drawIndex, image.drawIndex
                        )
                    )
                }

                iv.setOnClickListener {
                    onClickUserImage?.invoke(sceneItem.drawIndex, image.drawIndex, this.imgSeq)
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
                    override(image.getDownSampleSize(isForCartThumbnail))
                }

                iv.alpha = image.alpha
                // Glide 는 작은 쪽 기준으로 Aspect를 유지한다.
                glide
                    .asBitmap()
                    .load(image.filter?.imageUri ?: thumbnailUri)
                    .apply(requestOptions)
                    .into(if (isForCartThumbnail) SketchImageTarget.NoWarningResolution(iv, image) else SketchImageTarget.Default(iv, image))

                if (sceneItem.isCartThumbnail) {
                    return
                }
            }
        }
    }

    private fun getImageObjectTransformation(
        glideReq: RequestManager,
        image: SceneObjectItem.Image
    ): MultiTransformation<Bitmap> {
        return mutableListOf<BitmapTransformation>().apply {
            add(SceneObjectImageBitmapTransformation.Rotate(image.uiAngle, image.isValidRatio() || isForCartThumbnail))
            image.border?.let {
                if (image.border.isMask) add(SceneObjectImageBitmapTransformation.Mask(glideReq, image))
                else if (image.border.isSingleColor) add(SceneObjectImageBitmapTransformation.Frame(image))
            }
        }.let {
            MultiTransformation(it)
        }
    }

    private fun renderSticker(sticker: SceneObjectItem.Sticker) {
        with(binding) {
            val stickerImageView = stickerImageViewPool.poll() ?: SceneObjectStickerView(objectContainer.context).apply {
                this.id = ViewCompat.generateViewId()
            }

            objectContainer.addView(stickerImageView)
            stickerImageView.apply {
                updateLayoutParams<LayoutParams> {
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

    private fun renderText(text: SceneObjectItem.Text) {
        with(binding) {
            val textImageView = textImageViewPool.poll() ?: SceneObjectTextView(objectContainer.context).apply {
                this.id = ViewCompat.generateViewId()
            }
            objectContainer.addView(textImageView)
            textImageView.apply {
                updateLayoutParams<LayoutParams> {
                    x = text.getDrawX()
                    y = text.getDrawY()
                    width = text.getDrawWidth()
                    height = text.getDrawHeight()
                    rotation = text.angle.toFloat()
                }
            }

            if (!text.readOnly) {
                textImageView.setOnClickListener {
                    if (text is SceneObjectItem.Text.Spine || (text is SceneObjectItem.Text.User && text.name == "title")) {
                        onClickText?.invoke(it.id)
                    } else if (text is SceneObjectItem.Text.User) {
                        onClickUserText?.invoke(sceneItem.drawIndex, text.drawIndex)
                    }
                }
            }


            val showText = when (isForCartThumbnail) {
                true -> text.text
                else -> {
//                    if (text.text.isBlank()) text.placeholder else text.text  //기획팀 요청 사항(2021.10.08): 플레이스 홀더가 한글로 되어있어 일본 서비스에 문제가 되니 표시 하지마!!
                    if (text.text.isBlank()) "" else text.text
                }
            }
            if (showText.isBlank()) {
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
                    text = showText,
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
                    .into(if (sceneItem.isCartThumbnail) TextToImageTarget.Thumbnail(textImageView, text) else TextToImageTarget.Default(textImageView, text))
            }
        }
    }

    fun setCurrentCoverPage(side: CoverHorizontalScrollView.CoverPageSide) {
        when (side) {
            CoverHorizontalScrollView.CoverPageSide.Back -> {
                binding.backpageIndicator.isEnabled = true
                binding.spineIndicator.isEnabled = false
                binding.frontpageIndicator.isEnabled = false
            }
            CoverHorizontalScrollView.CoverPageSide.Spine -> {
                binding.backpageIndicator.isEnabled = false
                binding.spineIndicator.isEnabled = true
                binding.frontpageIndicator.isEnabled = false
            }
            CoverHorizontalScrollView.CoverPageSide.Front -> {
                binding.backpageIndicator.isEnabled = false
                binding.spineIndicator.isEnabled = false
                binding.frontpageIndicator.isEnabled = true
            }
        }
    }

    fun isEqualData(data: SceneItem): Boolean {
        return this::sceneItem.isInitialized && data.hashCode() == sceneItem.hashCode()
    }
}