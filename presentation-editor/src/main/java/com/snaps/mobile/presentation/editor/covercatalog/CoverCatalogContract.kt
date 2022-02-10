package com.snaps.mobile.presentation.editor.covercatalog

import com.snaps.mobile.presentation.editor.mvi.UiEffect
import com.snaps.mobile.presentation.editor.mvi.UiEvent
import com.snaps.mobile.presentation.editor.mvi.UiState

class CoverCatalogContract {

    sealed class Event : UiEvent {
        class OnSelectCover(val item: CoverCatalogItem, val isChecked: Boolean) : Event()
        object OnClickClose : Event()
        object OnClickConfirm : Event()
    }

    data class State(
        val uiState: CoverCatalogListState
    ) : UiState

    sealed class CoverCatalogListState {
        object Idle : CoverCatalogListState()
        object Loading : CoverCatalogListState()
        data class Success(val coverCatalogItems: List<CoverCatalogItem>) : CoverCatalogListState()
        data class Refresh(val coverCatalogItems: List<CoverCatalogItem>) : CoverCatalogListState()
    }

    sealed class Effect : UiEffect {
        object Close : Effect()
        object SaveAndClose : Effect()
    }

}