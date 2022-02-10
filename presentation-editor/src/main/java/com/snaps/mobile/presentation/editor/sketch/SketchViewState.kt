package com.snaps.mobile.presentation.editor.sketch

import android.graphics.Bitmap

sealed class SketchViewState {

    object LoadingDone : SketchViewState()

    class GetMoreRecipeImage(val maxAddMoreCount: Int) : SketchViewState()
    class AddToCart(val cartThumbnail: Bitmap, val hasNotUseTrayImage: Boolean) : SketchViewState()
    class ImageEdit(
        val sceneDrawIndex: String,
        val sceneObjectDrawIndex: String,
        val imgSeq: String,
        val tag: String = "ImageEditFragment"
    ) : SketchViewState()

    class CoverList(val coverTemplateCode: String) : SketchViewState()

    object Tutorial : SketchViewState()

}