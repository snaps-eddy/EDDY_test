package com.snaps.mobile.presentation.editor.textwritter

import com.snaps.mobile.domain.save.TextAlign
import com.snaps.mobile.presentation.editor.mvi.UiEffect
import com.snaps.mobile.presentation.editor.mvi.UiEvent
import com.snaps.mobile.presentation.editor.mvi.UiState

class TextWriterContract {

    sealed class Event : UiEvent {
        //        object OnClickClose : Event()
        class OnClickBack(val currentText: String) : Event()
        class OnClickTextColor(val isColorPickerOpend: Boolean) : Event()
        object OnClickEdtiText : Event()
        object OnClickDefaultTextColor : Event()
        object OnClickDialogConfirm : Event()

        class OnCheckAlign(val textAlign: TextAlign) : Event()
        class OnChangeTextColor(val hexColor: String) : Event()
        class OnClickConfirm(val text: String) : Event()
    }

    data class State(
        val uiState: TextWriterState
    ) : UiState

    sealed class TextWriterState {
        object Idle : TextWriterState()
        object ShowColorPicker : TextWriterState()
        object HideColorPicker : TextWriterState()

        /**
         * 전달받은 데이터로 최초 화면 생성 시 셋팅 화면.
         */
        class InitText(
            val text: String,
            val color: String,
            val align: TextAlign,
            val fontSize: Float
        ) : TextWriterState()

        /**
         * 컬러 픽커의 색상 선택 후 상태
         */
        class UpdateTextColor(val color: String) : TextWriterState()

        /**
         * Text align 선택 후 상태
         */
        class UpdateTextAlign(val align: TextAlign) : TextWriterState()

        /**
         * 컬러 픽커의 "기본색" 선택 후 상태
         */
        class UpdateDefaultTextColor(val color: String) : TextWriterState()
    }

    sealed class Effect : UiEffect {
        object Close : Effect()
        object SaveAndClose : Effect()
        object ConfirmHasChanges : Effect()
    }

}