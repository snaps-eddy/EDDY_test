package com.snaps.mobile.presentation.editor.sketch.itemview

import android.content.ClipDescription
import android.view.DragEvent
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.ViewParent
import android.widget.FrameLayout
import androidx.core.view.isVisible
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.snaps.common.utils.log.Dlog
import com.snaps.mobile.presentation.editor.R
import com.snaps.mobile.presentation.editor.sketch.drag.SketchDraggable
import com.snaps.mobile.presentation.editor.sketch.model.ImageMovingData
import com.snaps.mobile.presentation.editor.utils.KotlinEpoxyHolder

/**
 * 페이지 중간 중간에 섞여있는 Scene 추가 Zone
 */
@EpoxyModelClass
abstract class SceneItemAddPageZoneView : EpoxyModelWithHolder<SceneItemAddPageZoneViewHolder>() {

    @EpoxyAttribute
    var dataIndex: Int = 0

    @EpoxyAttribute
    lateinit var prevSceneDrawIndex: String

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    var onDropImage: ((ImageMovingData) -> Unit)? = null

    override fun getDefaultLayout(): Int = R.layout.item_scene_add_page_zone

    override fun bind(holder: SceneItemAddPageZoneViewHolder) {
        holder.root.tag = "AddPageDropZone"
        holder.root.setOnDragListener { v, event ->
            if (event.localState !is DropItem) {
                return@setOnDragListener false
            }

            val dropItem = event.localState as DropItem

            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)
                }
                DragEvent.ACTION_DRAG_ENTERED -> {
                    v.performHapticFeedback(
                        HapticFeedbackConstants.KEYBOARD_TAP,
                        HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING,
                    )
                    dropItem.onEnterAddPageZone()
                    holder.dropIndicator.isVisible = true
                    true
                }

                DragEvent.ACTION_DRAG_LOCATION ->
                    true

                DragEvent.ACTION_DRAG_EXITED -> {
                    dropItem.onExitAddPageZone()
                    holder.dropIndicator.isVisible = false
                    true
                }

                DragEvent.ACTION_DROP -> {
                    onDropImage(dropItem)
                    holder.dropIndicator.isVisible = false
                    true
                }

                DragEvent.ACTION_DRAG_ENDED -> {
                    holder.dropIndicator.isVisible = false
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

    override fun unbind(holder: SceneItemAddPageZoneViewHolder) {
        holder.root.setOnDragListener(null)
    }

    private fun onDropImage(dropItem: DropItem) {
        onDropImage?.invoke(dropItem.imageMovingData)
    }

    interface DropItem : SketchDraggable {

        val imageMovingData: ImageMovingData
        fun onEnterAddPageZone()
        fun onExitAddPageZone()

    }
}

class SceneItemAddPageZoneViewHolder(parent: ViewParent) : KotlinEpoxyHolder() {
    val root by bind<FrameLayout>(R.id.root)
    val dropIndicator by bind<View>(R.id.view_drop_indicator)

}

