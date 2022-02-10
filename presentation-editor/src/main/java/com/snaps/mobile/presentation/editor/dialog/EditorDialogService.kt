package com.snaps.mobile.presentation.editor.dialog

import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import com.snaps.mobile.presentation.editor.EditorViewModel.Companion.RECIPE_PARAMS
import com.snaps.mobile.presentation.editor.textwritter.TextWriterFragment
import com.snaps.mobile.presentation.editor.textwritter.TextWriterViewModel
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class EditorDialogService @Inject constructor(
    private val anchorActivity: AppCompatActivity
) {

    private val vm: EditorDialogViewModel by anchorActivity.viewModels()

    fun showDialog(state: EditorDialogState) {
        hideDialog()
        vm.updateState(state)
        when (state) {
            is EditorDialogState.Choice -> {
                ChoiceDialogFragment().show(anchorActivity.supportFragmentManager, TAG_DIALOG)
            }
            is EditorDialogState.Notice -> {
                NoticeDialogFragment().show(anchorActivity.supportFragmentManager, TAG_DIALOG)
            }
            is EditorDialogState.Loading -> {
                LoadingDialogFragment().show(anchorActivity.supportFragmentManager, TAG_DIALOG)
            }
            is EditorDialogState.Progress -> {
                ProgressDialogFragment().show(anchorActivity.supportFragmentManager, TAG_DIALOG)
            }
            is EditorDialogState.TextWriter -> {
                TextWriterFragment()
                    .apply {
                        arguments = bundleOf(
                            RECIPE_PARAMS to state.recipeParams,
                            TextWriterViewModel.KEY_SCENE_OBJECT_TEXT_DRAW_INDEX to state.sceneObjectDrawIndex,
                        )
                    }.run {
                        show(anchorActivity.supportFragmentManager, TAG_DIALOG)
                    }
            }
            EditorDialogState.Hide -> {
                hideDialog()
            }
        }
    }

    fun hideDialog() {
        (anchorActivity.supportFragmentManager.findFragmentByTag(TAG_DIALOG) as? BaseEditorDialogFragment<*>)?.dismiss()
    }

    companion object {
        const val TAG_DIALOG = "Editor Activity dialog"
    }
}