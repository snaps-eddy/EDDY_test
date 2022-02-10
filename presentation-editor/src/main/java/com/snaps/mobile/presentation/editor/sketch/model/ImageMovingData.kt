package com.snaps.mobile.presentation.editor.sketch.model

data class ImageMovingData(
    val imageId: String?,
    val sceneDrawIndex: String? = null,
    val sceneObjectDrawIndex: String? = null,
    val isOnSketch: Boolean = true
) {

    /**
     * About target
     */
    val isFromTray: Boolean
        get() = imageId != null && sceneDrawIndex == null && sceneObjectDrawIndex == null

    val isFromImageView: Boolean
        get() = imageId != null && sceneDrawIndex != null && sceneObjectDrawIndex != null

    /**
     * About destination
     */
    val toImageView: Boolean
        get() = sceneDrawIndex != null && sceneObjectDrawIndex != null

    val toScene: Boolean
        get() = sceneDrawIndex != null && sceneObjectDrawIndex == null

    val isNotOnSketch: Boolean
        get() = !isOnSketch

}