package com.snaps.mobile.presentation.editor.sketch.drag

import android.view.DragEvent
import android.view.View

/**
 * Drop 이 가능한 Component는 이 interface를 구현해야한다.
 */
interface SketchDropZone<T> {

    /**
     * Enter 되거나 Exit 시, 호출 됨.
     */
    fun setEnableDrop(enabled: Boolean)

    /**
     * Drop zone 에서 움직이고 있을 때 호출됨.
     */
    fun onMoveInZone(v: View, event: DragEvent)

    /**
     * 아이템을 Drop 했을 때 호출됨.
     */
    fun onDrop(item: T)
}