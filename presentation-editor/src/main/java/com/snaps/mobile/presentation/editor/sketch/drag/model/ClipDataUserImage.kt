package com.snaps.mobile.presentation.editor.sketch.drag.model

import androidx.core.view.ViewCompat
import com.snaps.common.utils.log.Dlog
import com.snaps.mobile.presentation.editor.sketch.custom.SceneObjectImageView
import com.snaps.mobile.presentation.editor.sketch.custom.SceneUIContainer
import com.snaps.mobile.presentation.editor.sketch.drag.BottomImageDeleteDragListener
import com.snaps.mobile.presentation.editor.sketch.drag.TrayImageDeleteDragListener
import com.snaps.mobile.presentation.editor.sketch.drag.shadow.UserImageShadowBuilder
import com.snaps.mobile.presentation.editor.sketch.itemview.SceneItemAddPageZoneView
import com.snaps.mobile.presentation.editor.sketch.model.ImageMovingData

class ClipDataUserImage(
    val shadow: UserImageShadowBuilder,
    override val imageMovingData: ImageMovingData
) : SceneUIContainer.DropItem,
    SceneObjectImageView.OnDroppable,
    SceneItemAddPageZoneView.DropItem,
    BottomImageDeleteDragListener.DropItem,
    TrayImageDeleteDragListener.DropItem {

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

    override fun isNotEqualView(targetSceneId: String): Boolean {
        return imageMovingData.sceneDrawIndex != targetSceneId && imageMovingData.sceneObjectDrawIndex != targetSceneId// 원래 sceneObjectDrawIndex로 했으나, 이러면 현재 포함된 Scene에 Indicator가 생김.
    }

    override fun onDragEnded() {
//        shadow.onDragEnded()
    }

    override fun onEnterAddPageZone() {
        shadow.apply { isOnAddPage = true }.run { ViewCompat.updateDragShadow(this.view, this) }
    }

    override fun onExitAddPageZone() {
        shadow.apply { isOnAddPage = false }.run { ViewCompat.updateDragShadow(this.view, this) }
    }

    override fun onEnterTrayDeleteZone() {
        shadow.apply { isOnTray = true }.run { ViewCompat.updateDragShadow(this.view, this) }
    }

    override fun onExitTrayDeleteZone() {
        shadow.apply { isOnTray = false }.run { ViewCompat.updateDragShadow(this.view, this) }
    }

    /**
     * BottomImageDeleteDragListener.DropItem Implementation
     */
    override fun onEnterBottomDeleteZone() {
        ViewCompat.updateDragShadow(shadow.view, shadow)
    }

    override fun onExitBottomDeleteZone() {
        ViewCompat.updateDragShadow(shadow.view, shadow)
    }

    override fun isAvailableDelete(): Boolean {
        return true
    }
}