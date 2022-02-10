package com.snaps.mobile.presentation.editor.gallery

/**
 * Vm 에서 View에게 전달하는 Single event
 */
sealed class GalleryViewEffect {

    class ShowToast(val message: String) : GalleryViewEffect()

}