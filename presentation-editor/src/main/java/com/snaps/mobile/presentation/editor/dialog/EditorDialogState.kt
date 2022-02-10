package com.snaps.mobile.presentation.editor.dialog

import android.text.SpannableString
import com.snaps.mobile.presentation.editor.RecipeParams
import io.reactivex.rxjava3.core.Observable

sealed class EditorDialogState {

    object Hide : EditorDialogState()

    data class Loading(
        val style: LoadingAnimStyle
    ) : EditorDialogState()

    class Progress(val progressStream: Observable<Int>) : EditorDialogState()

    class Notice(
        val message: String,
        val buttonLabel: String? = null,
        val buttonAction: (() -> Unit)? = null,
    ) : EditorDialogState()

    class Choice(
        val message: CharSequence,
        val leftAction: (() -> Unit)? = null,
        val leftButtonLabel: String? = null,
        val rightAction: (() -> Unit)? = null,
        val rightButtonLabel: String? = null,
    ) : EditorDialogState() {
        var spannableString: SpannableString? = null
    }

    class TextWriter(
        val recipeParams: RecipeParams,
        val sceneDrawIndex: String,
        val sceneObjectDrawIndex: String,
    ) : EditorDialogState()
}