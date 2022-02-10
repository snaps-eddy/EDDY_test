package com.snaps.mobile.presentation.editor.sketch.drag.model

import androidx.core.view.ViewCompat
import com.snaps.mobile.presentation.editor.sketch.custom.SceneUIContainer
import com.snaps.mobile.presentation.editor.sketch.drag.shadow.TrayBackgroundShadowBuilder
import com.snaps.mobile.presentation.editor.sketch.model.BackgroundMovingData

class ClipDataTrayBackground(
    val shadow: TrayBackgroundShadowBuilder,
    val movingData: BackgroundMovingData,
) : SceneUIContainer.DropItem {

    override val type: SceneUIContainer.DropItem.Type = SceneUIContainer.DropItem.Type.Background(movingData)

    override fun onEnterSceneContainer() {
        return shadow.apply { isOnScene = true }.run { ViewCompat.updateDragShadow(this.view, this) }
    }

    override fun onExitSceneContainer() {
        return shadow.apply { isOnScene = false }.run { ViewCompat.updateDragShadow(this.view, this) }
    }

    override fun isNotEqualView(targetSceneId: String): Boolean {
        return true
    }

    override fun onDragEnded() {

    }

}