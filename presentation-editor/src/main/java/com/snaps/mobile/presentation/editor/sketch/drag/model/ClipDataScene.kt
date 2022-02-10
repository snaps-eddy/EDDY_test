package com.snaps.mobile.presentation.editor.sketch.drag.model

import androidx.core.view.ViewCompat
import com.snaps.mobile.presentation.editor.sketch.custom.SceneUIContainer
import com.snaps.mobile.presentation.editor.sketch.drag.shadow.SceneShadowBuilder

class ClipDataScene(
    val sceneId: String,
    val shadow: SceneShadowBuilder,
) : SceneUIContainer.DropItem {

    override val type: SceneUIContainer.DropItem.Type = SceneUIContainer.DropItem.Type.Scene(sceneId)

    override fun onEnterSceneContainer() {
        shadow.apply {
            isOnScene = true
        }.run {
            ViewCompat.updateDragShadow(this.view, this)
        }
    }

    override fun onExitSceneContainer() {
        shadow.apply {
            isOnScene = false
        }.run {
            ViewCompat.updateDragShadow(this.view, this)
        }
    }

    override fun isNotEqualView(targetSceneId: String): Boolean {
        return sceneId != targetSceneId
    }

    override fun onDragEnded() {
        shadow.onDragEnded()
    }
}