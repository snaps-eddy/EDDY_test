package com.snaps.mobile.presentation.editor.tutorial

import androidx.lifecycle.SavedStateHandle
import com.snaps.common.utils.log.Dlog
import com.snaps.mobile.presentation.editor.EditorViewModel
import com.snaps.mobile.presentation.editor.RecipeParams
import com.snaps.mobile.presentation.editor.mvi.BaseEditorViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TutorialViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : BaseEditorViewModel<TutorialContract.Event, TutorialContract.State, TutorialContract.Effect>(), TutorialJavascriptInterface.Callback {

    private val recipeParams: RecipeParams =
        savedStateHandle.get<RecipeParams>(EditorViewModel.RECIPE_PARAMS) ?: throw IllegalArgumentException("Recipe Param Missing")

    init {
        setEffect { TutorialContract.Effect.LoadUrl(recipeParams.tutorialUrl) }
    }

    override fun createInitialState(): TutorialContract.State {
        return TutorialContract.State(TutorialContract.TutorialState.Idle)
    }

    override fun handleEvent(event: TutorialContract.Event) {
    }

    override fun onCallClose() {
        Dlog.d("On Call Javascript: -> Close ")
        setEffect { TutorialContract.Effect.Close }
    }

}