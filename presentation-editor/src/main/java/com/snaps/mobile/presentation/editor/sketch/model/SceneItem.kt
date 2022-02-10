package com.snaps.mobile.presentation.editor.sketch.model

import com.snaps.mobile.domain.save.Scene

data class SceneItem(
    val drawIndex: String,
    val templateCode: String,
    val width: Float,
    val height: Float,
    val type: Scene.Type,
    val subType: Scene.SubType,
    val sceneObjects: List<SceneObjectItem>,
    val scaleFactor: Float,
    val midWidth: Float
) {
    var isLockOn: Boolean = false
    val isCartThumbnail: Boolean
        get() = scaleFactor == 1.0f

    fun getDrawWidth(): Int {
        return width.times(scaleFactor).toInt()
    }

    fun getDrawHeight(): Int {
        return height.times(scaleFactor).toInt()
    }

    fun getDrawMidWidth() : Int {
        return midWidth.times(scaleFactor).toInt()
    }

    /**
     * SceneObjectItem 들의 Scale factor 값을 1로 변경.
     */
    fun getDefaultScaleFactor(): List<SceneObjectItem> {
        return sceneObjects
            .map {
                when (it) {
                    is SceneObjectItem.Background.Color -> it.copy(scaleFactor = 1.0f)
                    is SceneObjectItem.Background.Image -> it.copy(scaleFactor = 1.0f)
                    is SceneObjectItem.Image -> it.copy(scaleFactor = 1.0f)
                    is SceneObjectItem.Sticker -> it.copy(scaleFactor = 1.0f)
                    is SceneObjectItem.Text.User -> it.copy(scaleFactor = 1.0f)
                    is SceneObjectItem.Text.Spine -> it.copy(scaleFactor = 1.0f)
                }
            }
    }

    val isEmptyUserImages: Boolean
        get() = sceneObjects.filterIsInstance<SceneObjectItem.Image>()
            .mapNotNull { it.content }
            .isEmpty()

    val isPage: Boolean
        get() = this.subType == Scene.SubType.Page

    val sceneObjectImageCount: Int
        get() = sceneObjects.filterIsInstance<SceneObjectItem.Image>().size

}