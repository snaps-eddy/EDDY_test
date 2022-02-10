package com.snaps.mobile.presentation.editor

import com.snaps.mobile.presentation.editor.dialog.EditorDialogState

sealed class EditorNavigation(val tag: String) {

    class Gallery(val recipeParams: RecipeParams) :
        EditorNavigation("Gallery")

    class ModalGallery(val recipeParams: RecipeParams, val maxAddMoreCount: Int) :
        EditorNavigation("ModalGallery")

    class Title(val recipeParams: RecipeParams, val isCreateProcess: Boolean) :
        EditorNavigation("Title")

    class AiProgress(val recipeParams: RecipeParams) :
        EditorNavigation("AiProgress")

    class Sketch(val recipeParams: RecipeParams) :
        EditorNavigation("Sketch")

    class ImageEdit(
        val recipeParams: RecipeParams,
        val sceneDrawIndex: String,
        val sceneObjectDrawIndex: String,
        val imgSeq: String
    ) : EditorNavigation("ImageEdit")

    class CoverCatalog(
        val recipeParams: RecipeParams,
        val coverTemplateCode: String
    ) : EditorNavigation("CoverListFragment")

    class Tutorial(
        val recipeParams: RecipeParams
    ) : EditorNavigation("Tutorial")

    class Finish(
        val goToCart: Boolean,
        val isFromCart: Boolean
    ) : EditorNavigation("Finish Editor")

    class Dialog(val dialogState: EditorDialogState) : EditorNavigation("Show Dialog")
}