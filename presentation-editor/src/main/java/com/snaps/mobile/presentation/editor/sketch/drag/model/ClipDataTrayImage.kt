package com.snaps.mobile.presentation.editor.sketch.drag.model

import androidx.core.view.ViewCompat
import com.snaps.mobile.presentation.editor.sketch.custom.SceneObjectImageView
import com.snaps.mobile.presentation.editor.sketch.custom.SceneUIContainer
import com.snaps.mobile.presentation.editor.sketch.drag.BottomImageDeleteDragListener
import com.snaps.mobile.presentation.editor.sketch.drag.shadow.TrayPhotoShadowBuilder
import com.snaps.mobile.presentation.editor.sketch.itemview.SceneItemAddPageZoneView
import com.snaps.mobile.presentation.editor.sketch.model.ImageMovingData

class ClipDataTrayImage(
    val shadow: TrayPhotoShadowBuilder,
    override val imageMovingData: ImageMovingData,
    val isOnSketch: Boolean,
) : SceneUIContainer.DropItem,
    SceneObjectImageView.OnDroppable,
    SceneItemAddPageZoneView.DropItem,
    BottomImageDeleteDragListener.DropItem {

    override val type: SceneUIContainer.DropItem.Type = SceneUIContainer.DropItem.Type.Image(imageMovingData)

    override fun onEnterSceneContainer() {
        shadow.apply { isOnScene = true }.run { ViewCompat.updateDragShadow(this.view, this) }
    }

    override fun onExitSceneContainer() {
        shadow.apply { isOnScene = false }.run { ViewCompat.updateDragShadow(this.view, this) }
    }

    override fun onEnterSceneObjectImageView() {
        shadow.apply { isOnImage = true }.run { ViewCompat.updateDragShadow(this.view, this) }
    }

    override fun onExitImageSceneObjectImageView() {
        shadow.apply { isOnImage = false }.run { ViewCompat.updateDragShadow(this.view, this) }
    }

    override fun onEnterAddPageZone() {
        shadow.apply { isOnAddPage = true }.run { ViewCompat.updateDragShadow(this.view, this) }
    }

    override fun onExitAddPageZone() {
        shadow.apply { isOnAddPage = false }.run { ViewCompat.updateDragShadow(this.view, this) }
    }

    override fun onEnterBottomDeleteZone() {
        ViewCompat.updateDragShadow(shadow.view, shadow)
    }

    override fun onExitBottomDeleteZone() {
        ViewCompat.updateDragShadow(shadow.view, shadow)
    }

    override fun isAvailableDelete(): Boolean {
        return isOnSketch.not()
    }

    override fun isNotEqualView(targetSceneId: String): Boolean {
        return imageMovingData.sceneDrawIndex != targetSceneId && imageMovingData.sceneObjectDrawIndex != targetSceneId// 원래 sceneObjectDrawIndex로 했으나, 이러면 현재 포함된 Scene에 Indicator가 생김.
    }

    override fun onDragEnded() {
    }
}