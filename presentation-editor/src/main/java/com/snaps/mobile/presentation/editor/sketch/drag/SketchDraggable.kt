package com.snaps.mobile.presentation.editor.sketch.drag

interface SketchDraggable {

    fun isNotEqualView(targetSceneId: String): Boolean
    fun onDragEnded()

}