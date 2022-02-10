package com.snaps.mobile.presentation.editor.sketch

import com.snaps.mobile.presentation.editor.dialog.EditorDialogState

sealed class SketchViewEffect {

    class ShowToast(val message: String) : SketchViewEffect()

    class ShowDialog(val dialog: EditorDialogState) : SketchViewEffect()

}