package com.snaps.mobile.presentation.editor.sketch.drag

import android.content.ClipDescription
import android.view.DragEvent
import android.view.HapticFeedbackConstants
import android.view.View
import com.snaps.common.utils.log.Dlog
import com.snaps.mobile.presentation.editor.sketch.model.ImageMovingData

class TrayImageDeleteDragListener(
    private val dropZone: SketchDropZone<DropItem>
) : View.OnDragListener {

    override fun onDrag(v: View, event: DragEvent): Boolean {
        if (event.localState !is DropItem) {
            return false
        }

        val dropItem = event.localState as DropItem

        return when (event.action) {
            DragEvent.ACTION_DRAG_STARTED -> {
                event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)
            }

            DragEvent.ACTION_DRAG_ENTERED -> {
                v.performHapticFeedback(
                    HapticFeedbackConstants.KEYBOARD_TAP,
                    HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING,
                )
                dropItem.onEnterTrayDeleteZone()
                dropZone.setEnableDrop(true)
                true
            }

            DragEvent.ACTION_DRAG_LOCATION -> {
                dropZone.onMoveInZone(v, event)
                true
            }

            DragEvent.ACTION_DRAG_EXITED -> {
                dropItem.onExitTrayDeleteZone()
                dropZone.setEnableDrop(false)
                true
            }

            DragEvent.ACTION_DROP -> {
                dropZone.onDrop(dropItem)
                dropZone.setEnableDrop(false)
                true
            }

            DragEvent.ACTION_DRAG_ENDED -> {
                dropItem.onDragEnded()
                dropZone.setEnableDrop(false)
                true
            }

            else -> {
                Dlog.e("Unknown action type received by OnDragListener.")
                dropItem.onDragEnded()
                false
            }
        }
    }

    interface DropItem : SketchDraggable {

        val imageMovingData: ImageMovingData

        fun onEnterTrayDeleteZone()
        fun onExitTrayDeleteZone()

    }

}