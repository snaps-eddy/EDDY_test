package com.snaps.mobile.presentation.editor.tutorial

import com.snaps.mobile.presentation.editor.mvi.UiEffect
import com.snaps.mobile.presentation.editor.mvi.UiEvent
import com.snaps.mobile.presentation.editor.mvi.UiState

class TutorialContract {

    sealed class Event : UiEvent {}

    data class State(
        val uiState: TutorialState
    ) : UiState

    sealed class TutorialState {
        object Idle : TutorialState()
    }

    sealed class Effect : UiEffect {
        class LoadUrl(val url: String) : Effect()
        object Close : Effect()
    }

}