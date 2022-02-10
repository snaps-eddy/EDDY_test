package com.snaps.mobile.presentation.editor.sketch.itemview

import android.content.ClipData
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.ViewParent
import android.widget.*
import androidx.core.view.ViewCompat
import androidx.core.view.children
import androidx.core.view.isVisible
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
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.snaps.mobile.presentation.editor.R
import com.snaps.mobile.presentation.editor.covercatalog.CoverCutOffTransformation
import com.snaps.mobile.presentation.editor.sketch.SceneController
import com.snaps.mobile.presentation.editor.sketch.SceneObjectImageBitmapTransformation
import com.snaps.mobile.presentation.editor.sketch.custom.*
import com.snaps.mobile.presentation.editor.sketch.model.ImageMovingData
import com.snaps.mobile.presentation.editor.sketch.model.SceneObjectItem
import com.snaps.mobile.presentation.editor.utils.KotlinEpoxyHolder
import java.security.MessageDigest
import java.util.*

@EpoxyModelClass
abstract class SceneItemPageView : EpoxyModelWithHolder<SceneItemPageViewHolder>() {

    override fun getDefaultLayout(): Int = R.layout.item_scene_page

    @EpoxyAttribute
    lateinit var side: SceneController.ScenePageSide

    @EpoxyAttribute
    lateinit var sceneDrawIndex: String

    @EpoxyAttribute
    lateinit var sceneObjectItems: List<SceneObjectItem>

    @EpoxyAttribute
    lateinit var pageIndex: String

    @EpoxyAttribute
    var prevSceneImagesEmpty: Boolean = false

    @EpoxyAttribute
    lateinit var templateCode: String

    @EpoxyAttribute
    var dataIndex: Int = 0

    @EpoxyAttribute
    var sceneWidth: Int = 0

    @EpoxyAttribute
    var sceneHeight: Int = 0

    @EpoxyAttribute
    var lockOn: Boolean = false

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    var onDropScene: ((String, String, Boolean) -> Unit)? = null

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    var onDropImage: ((ImageMovingData, ImageMovingData) -> Unit)? = null

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    var onSelectScene: ((String, Boolean) -> Unit)? = null

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    var onClickChangeLayout: ((String) -> Unit)? = null

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    var onClickUserImage: ((String, String, String) -> Unit)? = null

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    var onClickUserText: ((String, String) -> Unit)? = null

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    var onStartDragImage: ((Bitmap, ImageMovingData, ClipData) -> Unit)? = null

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    var onStartDragScene: ((Bitmap, String, ClipData) -> Unit)? = null

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    var onClickDelete: (() -> Unit)? = null

    override fun bind(holder: SceneItemPageViewHolder) {
        println("Page Index : $pageIndex")
        drawAll(holder)
    }

    override fun bind(holder: SceneItemPageViewHolder, previouslyBoundModel: EpoxyModel<*>) {
        val previous = previouslyBoundModel as SceneItemPageView
        if (sceneDrawIndex != previous.sceneDrawIndex) {
            drawAll(holder)
            return
        }

        if (templateCode != previous.templateCode) {
            drawAll(holder)
            return
        }

        if (sceneObjectItems.hashCode() != previous.sceneObjectItems.hashCode()) {
            // 걍 매번 그리자 일단..
            drawAll(holder)
            return
        }

        if (prevSceneImagesEmpty != previous.prevSceneImagesEmpty) {
            drawDeletePageButton(holder)
        }

        if (lockOn != previous.lockOn) {
            holder.focusIndicator.isChecked = lockOn
        }

        if (pageIndex != previous.pageIndex) {
            drawFocusIndicator(holder)
        }

        if (sceneWidth != previous.sceneWidth || sceneHeight != previous.sceneHeight || side.hashCode() != previous.side.hashCode()) {
            drawSceneContainer(holder)
        }
    }

    override fun unbind(holder: SceneItemPageViewHolder) {
        with(holder) {
            objectContainer.children
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
                            it.setOnClickListener(null)
                            stickerImageViewPool.add(it)
                        }
                    }
                }
            glide.clear(contentsBackground)
            focusIndicator.setOnClickListener(null)
            layoutChanger.setOnClickListener(null)
            btnDelete.setOnClickListener(null)
        }
    }

    private fun drawAll(holder: SceneItemPageViewHolder) {
        with(holder) {
            drawSceneContainer(this)

            drawFocusIndicator(this)

            drawSceneObjects(this)

            drawDeletePageButton(this)

            setClickListers(this)
        }
    }

    private fun drawSceneContainer(holder: SceneItemPageViewHolder) {
        with(holder) {
            sceneContainer.apply {
                this.tag = sceneDrawIndex
                this.leftDropSceneIndicator = holder.leftDropIndicator
                this.rightDropSceneIndicator = if (side.isLeft) holder.leftDropIndicator else holder.rightDropIndicator
                this.dropImageIndicator = holder.dropImageIndicator
                this.landingSceneListener = { sceneId, after ->
                    // 드롭된 Scene 뒤에 넣으려면 true, 앞에다 넣으려면 false
                    onDropScene?.invoke(sceneId, sceneDrawIndex, if (side.isLeft) false else after)
                }
                this.landingImageListener = {
                    onDropImage?.invoke(it, ImageMovingData(null, sceneDrawIndex, null))
                }
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

    private fun drawFocusIndicator(holder: SceneItemPageViewHolder) {
        with(holder) {
            focusIndicator.text = pageIndex
            focusIndicator.isChecked = lockOn
            focusIndicator.setOnClickListener {
                if (focusIndicator.isChecked) {
                    onSelectScene?.invoke(sceneDrawIndex, focusIndicator.isChecked)
                } else {
                    focusIndicator.isChecked = true
                }
            }
        }
    }

    private fun drawDeletePageButton(holder: SceneItemPageViewHolder) {
        with(holder) {
            btnDelete.isVisible = side.isRight && isUserImagesEmpty() && prevSceneImagesEmpty
            btnDelete.setOnClickListener { onClickDelete?.invoke() }
        }
    }

    private fun setClickListers(holder: SceneItemPageViewHolder) {
        with(holder) {
            layoutChanger.setOnClickListener {
                onClickChangeLayout?.invoke(sceneDrawIndex)
            }

            movePageButton.setOnLongClickListener { v ->
                v.performHapticFeedback(
                    HapticFeedbackConstants.LONG_PRESS,
                    HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING,
                )
                val clipData = ClipData(ClipData.newPlainText(sceneDrawIndex, sceneDrawIndex))
                val snapshot = Bitmap.createBitmap(sceneContainer.measuredWidth, sceneContainer.measuredHeight, Bitmap.Config.ARGB_8888)
                Canvas(snapshot).run {
                    sceneContainer.draw(this)
                }
                onSelectScene?.invoke(sceneDrawIndex, true)
                onStartDragScene?.invoke(snapshot, sceneDrawIndex, clipData)
                true
            }
        }
    }

    private fun drawSceneObjects(holder: SceneItemPageViewHolder) {
        with(holder) {
            objectContainer.removeAllViews()
            sceneObjectItems.forEach {
                when (it) {
                    is SceneObjectItem.Background -> renderBackground(this, it)
                    is SceneObjectItem.Image -> renderImageObject(this, it)
                    is SceneObjectItem.Sticker -> renderSticker(this, it)
                    is SceneObjectItem.Text -> renderText(this, it)
                }
            }
        }
    }

    private fun renderBackground(holder: SceneItemPageViewHolder, background: SceneObjectItem.Background) {
        with(holder) {
            when (background) {
                is SceneObjectItem.Background.Color -> contentsBackground.setBackgroundColor(background.bgColor)
                is SceneObjectItem.Background.Image -> {
                    val requestOptions = RequestOptions().apply {
                        diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        skipMemoryCache(false)
                        format(DecodeFormat.PREFER_RGB_565)
                        disallowHardwareConfig()
                        override(400) // 사진 선명하게 보이려면 override 옵션을 아예 삭제.
                        transform(CoverCutOffTransformation(CoverCutOffTransformation.CutRange.StartToHalf))
                    }
                    background.middleImagePath?.apply {
                        glide
                            .load(this)
                            .apply(requestOptions)
                            .into(contentsBackground)
                    }
                }
            }
        }
    }

    private fun renderImageObject(holder: SceneItemPageViewHolder, image: SceneObjectItem.Image) {
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

            attachImage(this, iv, image)
        }
    }

    private fun attachImage(holder: SceneItemPageViewHolder, iv: SceneObjectImageView, image: SceneObjectItem.Image) {
        with(holder) {
            iv.tag = image.drawIndex
            image.content?.run {
                iv.landingItemListener = { fromDropData ->
                    onDropImage?.invoke(
                        fromDropData, ImageMovingData(this.imgSeq, sceneDrawIndex, image.drawIndex)
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
                    .load(image.filter?.imageUri ?: thumbnailUri)
                    .apply(requestOptions)
                    .transition(BitmapTransitionOptions.withCrossFade(1000))
                    .into(SketchImageTarget.Default(iv, image))

                iv.isHapticFeedbackEnabled = true
                iv.setOnLongClickListener { v ->
                    v.performHapticFeedback(
                        HapticFeedbackConstants.LONG_PRESS,
                        HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
                    )
                    val userImageMovingData = ImageMovingData(this.imgSeq, sceneDrawIndex, image.drawIndex)
                    val clipData = ClipData(ClipData.newPlainText(image.drawIndex, this.imgSeq))
                    val snapshot = Bitmap.createBitmap(image.getDrawWidth(), image.getDrawHeight(), Bitmap.Config.ARGB_8888)
                    Canvas(snapshot).run {
                        v.draw(this)
                    }
                    onSelectScene?.invoke(sceneDrawIndex, true)
                    onStartDragImage?.invoke(snapshot, userImageMovingData, clipData)
                    true
                }
                iv.setOnClickListener {
                    onClickUserImage?.invoke(sceneDrawIndex, image.drawIndex, imgSeq)
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

    private fun renderText(holder: SceneItemPageViewHolder, text: SceneObjectItem.Text) {
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

            if (!text.readOnly) {
                textImageView.setOnClickListener {
                    onClickUserText?.invoke(sceneDrawIndex, text.drawIndex)
                }
            }

//            val showText = if (text.text.isBlank()) text.placeholder else text.text  //기획팀 요청 사항(2021.10.08): 플레이스 홀더가 한글로 되어있어 일본 서비스에 문제가 되니 표시 하지마!!
            val showText = if (text.text.isBlank()) "" else text.text
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
                    .into(TextToImageTarget.Default(textImageView, text))
            }
        }
    }

    private fun renderSticker(holder: SceneItemPageViewHolder, sticker: SceneObjectItem.Sticker) {
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

    private fun isUserImagesEmpty(): Boolean {
        return sceneObjectItems.filterIsInstance<SceneObjectItem.Image>()
            .mapNotNull { it.content }
            .isEmpty()
    }
}

class SceneItemPageViewHolder(parent: ViewParent) : KotlinEpoxyHolder() {
    val sceneContainer by bind<SceneUIContainer>(R.id.scene_container)
    val objectContainer by bind<FrameLayout>(R.id.object_container)
    val contentsBackground by bind<ImageView>(R.id.contents_background)
    val focusIndicator by bind<CheckBox>(R.id.focus_indicator)
    val pageDividerSkin by bind<ImageView>(R.id.page_divider_skin)
    val glide = Glide.with((parent as View).context)
    val leftDropIndicator by bind<View>(R.id.left_drop_indicator)
    val rightDropIndicator by bind<View>(R.id.right_drop_indicator)
    val dropImageIndicator by bind<View>(R.id.drop_image_indicator)
    val layoutChanger by bind<View>(R.id.btn_layout_change)
    val movePageButton by bind<Button>(R.id.btn_drag)
    val btnDelete by bind<Button>(R.id.btn_delete_page)

    val imageViewPool: Queue<SceneObjectImageView> = LinkedList()
    val textImageViewPool: Queue<SceneObjectTextView> = LinkedList()
    val stickerImageViewPool: Queue<SceneObjectStickerView> = LinkedList()
}