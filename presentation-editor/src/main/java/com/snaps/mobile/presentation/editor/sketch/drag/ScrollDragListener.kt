package com.snaps.mobile.presentation.editor.sketch.drag

import android.view.DragEvent
import android.view.View
import com.snaps.common.utils.log.Dlog

class ScrollDragListener(
    private val isNeedDelay: Boolean,
    private val onScroll: (() -> Unit)?
) : View.OnDragListener {

    var enteredTime: Long = 0L

    override fun onDrag(v: View, event: DragEvent): Boolean {
        return when (event.action) {
            DragEvent.ACTION_DRAG_STARTED -> {
                true
            }

            DragEvent.ACTION_DRAG_ENTERED -> {
                enteredTime = System.currentTimeMillis()
                true
            }

            DragEvent.ACTION_DRAG_LOCATION -> {
                val interval = System.currentTimeMillis() - enteredTime
                if (isNeedDelay) {
                    if (interval > 1000) {
                        onScroll?.invoke()
                    }
                } else {
                    onScroll?.invoke()
                }
                true
            }

            DragEvent.ACTION_DRAG_EXITED -> {
                enteredTime = 0L
                true
            }

            DragEvent.ACTION_DROP -> {
                enteredTime = 0L
                true
            }

            DragEvent.ACTION_DRAG_ENDED -> {
                enteredTime = 0L
                true
            }

            else -> {
                enteredTime = 0L
                Dlog.e("Unknown action type received by OnDragListener.")
                false
            }
        }
    }
}