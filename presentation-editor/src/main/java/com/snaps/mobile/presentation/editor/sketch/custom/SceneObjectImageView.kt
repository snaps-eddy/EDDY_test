package com.snaps.mobile.presentation.editor.sketch.custom

import android.content.ClipDescription
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.DragEvent
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.view.isVisible
import com.snaps.common.utils.imageloader.CropUtil
import com.snaps.common.utils.log.Dlog
import com.snaps.mobile.presentation.editor.databinding.ViewSceneObjectImageBinding
import com.snaps.mobile.presentation.editor.sketch.drag.SketchDraggable
import com.snaps.mobile.presentation.editor.sketch.model.ImageMovingData
import com.snaps.mobile.presentation.editor.sketch.model.SceneObjectItem
import com.snaps.mobile.presentation.editor.utils.dp
import kotlin.math.roundToInt

class SceneObjectImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    var landingItemListener: ((ImageMovingData) -> Unit)? = null

    private val binding = ViewSceneObjectImageBinding.inflate(LayoutInflater.from(context), this, true)

    val sourceView: ImageView
        get() = binding.source

    init {
        binding.viewDropZone
            .setOnDragListener { v, event ->
                if (event.localState !is OnDroppable) {
                    return@setOnDragListener false
                }

                val dropItem = event.localState as OnDroppable

                when (event.action) {
                    DragEvent.ACTION_DRAG_STARTED -> {
                        event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)
                    }
                    DragEvent.ACTION_DRAG_ENTERED -> {
                        if (dropItem.isNotEqualView(this.tag as String)) {
                            v.performHapticFeedback(
                                HapticFeedbackConstants.KEYBOARD_TAP,
                                HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING,
                            )
                            dropItem.onEnterSceneObjectImageView()
                            setVisbileDropIndicator(true)
                        }
                        true
                    }

                    DragEvent.ACTION_DRAG_LOCATION ->
                        true

                    DragEvent.ACTION_DRAG_EXITED -> {
//                        ViewCompat.updateDragShadow(dropItem.getView(), dropItem.onExitImageSceneObjectImageView())
                        dropItem.onExitImageSceneObjectImageView()
                        setVisbileDropIndicator(false)
                        true
                    }

                    DragEvent.ACTION_DROP -> {
                        if (dropItem.isNotEqualView(this.tag as String)) {
                            onLandingItem(dropItem)
                            setVisbileDropIndicator(false)
                            true
                        } else {
                            false
                        }
                    }

                    DragEvent.ACTION_DRAG_ENDED -> {
                        setVisbileDropIndicator(false)
                        true
                    }

                    else -> {
                        Dlog.e("Unknown action type received by OnDragListener.")
                        false
                    }
                }
            }
    }

    fun setSource(resource: Bitmap) {
        binding.source.setImageBitmap(resource)
        binding.background.isVisible = false
    }

    fun onLoadFailed() {
        binding.background.isVisible = true
    }

    fun onResourceCleared(placeholder: Drawable?) {
        binding.source.setImageDrawable(placeholder)
        binding.background.isVisible = true
    }

    private fun setVisbileDropIndicator(isShow: Boolean) {
        binding.dropIndicator.isVisible = isShow
    }

    private fun onLandingItem(data: OnDroppable) {
        landingItemListener?.invoke(data.imageMovingData)
    }

    fun showWarningResoulution(notMatchResolution: Boolean) {
        binding.resolutionErrorIcon.isVisible = notMatchResolution
    }

    fun applyPaperFull(sceneObject: SceneObjectItem.Image) {
        val innerImage = sceneObject.innerImage ?: return

        with(binding) {
            val dWidth = source.drawable.intrinsicWidth
            val dHeight = source.drawable.intrinsicHeight

            /**
             * 이 값은 정확하게 소수점까지 맞진 않는다. 다만 계산 공식을 domain으로 옮겨놨고
             * 추후 계산이 필요한 부분은 domain 에서 처리한다.
             */

            val innerWidth = innerImage.angleCalibrationWidth
            val innerHeight = innerImage.angleCalibrationHeight

            val scaledWidth = (innerWidth * sceneObject.scaleFactor).roundToInt()
            val scaledHeight = (innerHeight * sceneObject.scaleFactor).roundToInt()

            val offsetX = innerImage.x * sceneObject.scaleFactor
            val offsetY = innerImage.y * sceneObject.scaleFactor

            source.scaleType = ImageView.ScaleType.MATRIX
            source.imageMatrix = Matrix().apply {
                setScale(scaledWidth / dWidth.toFloat(), scaledHeight / dHeight.toFloat())
                postTranslate(offsetX, offsetY)
            }
        }
    }

    /**
     * @ForDevelop
     */
    fun drawFDBox(original: Bitmap, sceneObject: SceneObjectItem.Image): Bitmap {
        return sceneObject.content?.analysisInfo?.let { info ->
            val copiedBitmap = CropUtil.getInSampledBitmapCopy(original, Bitmap.Config.ARGB_8888)

            val canvas = Canvas(copiedBitmap)
            val calibrationSize = info.getCalibratedOrientationThumbnailSize(sceneObject.content.orientationAngle)

            val ratioWidth = copiedBitmap.width / calibrationSize.width
            val ratioHeight = copiedBitmap.height / calibrationSize.height

            val fdArea = info.searchedAreaRect

            val left = fdArea.left * ratioWidth
            val right = fdArea.right * ratioWidth
            val top = fdArea.top * ratioHeight
            val bottom = fdArea.bottom * ratioHeight

            val fillPaint = Paint().apply {
                color = if (sceneObject.content.orientationAngle != 0) {
                    Color.argb(130, 255, 255, 153)
                } else {
                    Color.argb(130, 0, 200, 0)
                }
                style = Paint.Style.FILL_AND_STROKE
            }

            val outlinePaint = Paint().apply {
                color = if (sceneObject.content.orientationAngle != 0) {
                    Color.argb(255, 255, 255, 0)
                } else {
                    Color.argb(255, 0, 255, 0)
                }
                style = Paint.Style.STROKE
                strokeWidth = 1.dp().toFloat()
            }
            canvas.drawRect(left, top, right, bottom, fillPaint)
            canvas.drawRect(left, top, right, bottom, outlinePaint)
            copiedBitmap
        } ?: original
    }

    interface OnDroppable : SketchDraggable {

        val imageMovingData: ImageMovingData

        fun onEnterSceneObjectImageView()
        fun onExitImageSceneObjectImageView()

    }
}
