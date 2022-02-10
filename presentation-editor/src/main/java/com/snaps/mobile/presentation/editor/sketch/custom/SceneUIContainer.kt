package com.snaps.mobile.presentation.editor.sketch.custom

import android.content.ClipDescription
import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import android.content.res.TypedArray
import android.graphics.Point
import android.graphics.Rect
import android.os.Vibrator
import android.util.AttributeSet
import android.view.DragEvent
import android.view.HapticFeedbackConstants
import android.view.View
import android.widget.RelativeLayout
import androidx.core.content.res.use
import androidx.core.view.isVisible
import com.snaps.common.utils.log.Dlog
import com.snaps.mobile.presentation.editor.sketch.drag.SketchDraggable
import com.snaps.mobile.presentation.editor.sketch.model.BackgroundMovingData
import com.snaps.mobile.presentation.editor.sketch.model.ImageMovingData
import kotlin.math.roundToInt

class SceneUIContainer @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : RelativeLayout(context, attrs) {

    var landingSceneListener: ((String, Boolean) -> Unit)? = null
    var landingImageListener: ((ImageMovingData) -> Unit)? = null
    var landingBackgroundListener: ((BackgroundMovingData) -> Unit)? = null
    var leftDropSceneIndicator: View? = null
    var rightDropSceneIndicator: View? = null
    var dropImageIndicator: View? = null

    init {
        setOnDragListener { v, event ->
            if (event.localState !is DropItem) {
                return@setOnDragListener false
            }

            val dropItem = event.localState as DropItem

            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)
                }
                DragEvent.ACTION_DRAG_ENTERED -> {
                    if (dropItem.isNotEqualView(v.tag as String)) {
                        v.performHapticFeedback(
                            HapticFeedbackConstants.KEYBOARD_TAP,
                            HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING,
                        )
                        dropItem.onEnterSceneContainer()
                        /**
                         * 보통의 경우, Entered 되었을 때 뷰 업데이트 해주면 되지만
                         * Scene의 경우 좌, 우 구분해야 하기 때문에 DragEvent.ACTION_DRAG_LOCATION 에서 실시간으로 처리한다.
                         */

                        when (dropItem.type) {
                            is DropItem.Type.Background -> showImageDropIndicator()
                            is DropItem.Type.Image -> showImageDropIndicator()
                            else -> {
                            }
                        }
                    }
                    true
                }

                DragEvent.ACTION_DRAG_LOCATION -> {
                    if (dropItem.isNotEqualView(v.tag as String) && dropItem.type is DropItem.Type.Scene) {
                        val dropItemPosition = getDropItemPosition(v, event)
                        showSceneDropIndicator(dropItemPosition)
                    }
                    true
                }

                DragEvent.ACTION_DRAG_EXITED -> {
                    dropItem.onExitSceneContainer()
                    hideAllDropIndicator()
                    true
                }

                DragEvent.ACTION_DROP -> {
                    if (dropItem.isNotEqualView(v.tag as String)) {
                        val dropItemPosition = getDropItemPosition(v, event)
                        onLandingItem(dropItem, dropItemPosition)
                        hideAllDropIndicator()
                        true

                    } else {
                        false
                    }
                }

                DragEvent.ACTION_DRAG_ENDED -> {
                    hideAllDropIndicator()
                    dropItem.onDragEnded()
                    true
                }

                else -> {
                    Dlog.e("Unknown action type received by OnDragListener.")
                    dropItem.onDragEnded()
                    false
                }
            }
        }
    }

    private fun showSceneDropIndicator(dropItemPosition: Point) {
        if (isMoveToSceneAfter(dropItemPosition)) {
            leftDropSceneIndicator?.isVisible = false
            rightDropSceneIndicator?.isVisible = true
        } else {
            leftDropSceneIndicator?.isVisible = true
            rightDropSceneIndicator?.isVisible = false
        }
    }

    private fun showImageDropIndicator() {
        dropImageIndicator?.isVisible = true
    }

    private fun hideAllDropIndicator() {
        leftDropSceneIndicator?.isVisible = false
        rightDropSceneIndicator?.isVisible = false
        dropImageIndicator?.isVisible = false
    }

    private fun onLandingItem(data: DropItem, lastPosition: Point) {
        when (val type = data.type) {
            is DropItem.Type.Image -> {
                landingImageListener?.invoke(type.imageMovingData)
            }
            is DropItem.Type.Scene -> {
                landingSceneListener?.invoke(type.sceneId, isMoveToSceneAfter(lastPosition))
            }
            is DropItem.Type.Background -> {
                landingBackgroundListener?.invoke(type.movingData)
            }
        }
    }

    private fun getDropItemPosition(item: View, dragEvent: DragEvent): Point {
        val rect = Rect().apply {
            item.getLocalVisibleRect(this)
        }
        return Point(rect.left + dragEvent.x.roundToInt(), rect.top + dragEvent.y.roundToInt())
    }

    private fun isMoveToSceneAfter(point: Point): Boolean {
        val containerWidth = this.measuredWidth
        val positionInPercentage = point.x / containerWidth.toFloat()
        return positionInPercentage > 0.333f
    }

    /**
     * inlines
     */
    private fun Context.getStyledAttributes(attributeSet: AttributeSet?, styleArray: IntArray, block: TypedArray.() -> Unit) {
        return this.obtainStyledAttributes(attributeSet, styleArray, 0, 0).use(block)
    }

    interface DropItem : SketchDraggable {

        fun onEnterSceneContainer()
        fun onExitSceneContainer()

        val type: Type

        sealed class Type {
            class Image(val imageMovingData: ImageMovingData) : Type()
            class Scene(val sceneId: String) : Type()
            class Background(val movingData: BackgroundMovingData) : Type()
        }
    }
}