package com.snaps.mobile.presentation.editor.textwritter

import androidx.lifecycle.SavedStateHandle
import com.snaps.common.android_utils.SchedulerProvider
import com.snaps.common.utils.log.Dlog
import com.snaps.common.utils.ui.StringUtil
import com.snaps.mobile.domain.save.TextAlign
import com.snaps.mobile.domain.save.usecase.GetSceneObjectTextUseCase
import com.snaps.mobile.domain.save.usecase.UpdateSceneObjectTextUseCase
import com.snaps.mobile.presentation.editor.EditorViewModel
import com.snaps.mobile.presentation.editor.RecipeParams
import com.snaps.mobile.presentation.editor.mvi.BaseEditorViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import javax.inject.Inject

@HiltViewModel
class TextWriterViewModel @Inject constructor(
    private val getSceneObjectText: GetSceneObjectTextUseCase,
    private val updateSceneObjectText: UpdateSceneObjectTextUseCase,
    private val schedulerProvider: SchedulerProvider,
    private val compositeDisposable: CompositeDisposable,
    savedStateHandle: SavedStateHandle,
) : BaseEditorViewModel<TextWriterContract.Event, TextWriterContract.State, TextWriterContract.Effect>() {

    private val recipeParams: RecipeParams =
        savedStateHandle.get<RecipeParams>(EditorViewModel.RECIPE_PARAMS) ?: throw IllegalArgumentException("Recipe Param Missing")

    private val sceneDrawObjectIndex: String =
        savedStateHandle.get<String>(KEY_SCENE_OBJECT_TEXT_DRAW_INDEX) ?: throw IllegalArgumentException("Scene Object Draw Index Missing")

    private val projectCode: String
        get() = recipeParams.projectCode

    private lateinit var textModel: TextWriterModel
    private lateinit var defaultTextModel: TextWriterModel

    init {
        getSceneObjectText
            .invoke(GetSceneObjectTextUseCase.Params(projectCode, sceneDrawObjectIndex))
            .map {
                TextWriterModel(
                    text = it.text.replace("<br>".toRegex(), "\n"),
                    color = it.defaultStyle.color,
                    align = it.defaultStyle.textAlign,
                    sizePx = it.defaultStyle.fontSizePx
                )
            }
            .subscribe({
                textModel = it
                defaultTextModel = it
                setState {
                    copy(
                        uiState = TextWriterContract.TextWriterState.InitText(
                            text = textModel.text,
                            color = textModel.color,
                            align = textModel.align,
                            fontSize = textModel.viewFontSizeDp
                        )
                    )
                }
            }, {
            })
            .addTo(compositeDisposable)

    }

    override fun createInitialState(): TextWriterContract.State {
        return TextWriterContract.State(
            TextWriterContract.TextWriterState.Idle
        )
    }

    override fun handleEvent(event: TextWriterContract.Event) {
        when (event) {
            is TextWriterContract.Event.OnClickBack -> {
                textModel = textModel.copy(text = event.currentText)
                willCloseView()
            }
            is TextWriterContract.Event.OnClickTextColor -> {
                setState { copy(uiState = if (event.isColorPickerOpend) TextWriterContract.TextWriterState.HideColorPicker else TextWriterContract.TextWriterState.ShowColorPicker) }
            }
            TextWriterContract.Event.OnClickEdtiText -> {
                setState { copy(uiState = TextWriterContract.TextWriterState.HideColorPicker) }
            }
            is TextWriterContract.Event.OnCheckAlign -> {
                updateAlign(event.textAlign)
            }
            is TextWriterContract.Event.OnChangeTextColor -> {
                updateTextColor(event.hexColor)
            }
            is TextWriterContract.Event.OnClickConfirm -> {
                val writtenText = StringUtil.trimOnlySuffix(event.text)
                val filterString = StringUtil.getFilterString(writtenText)

                if (writtenText.length != filterString.length) {
                    // Something found emoji !!!
                    return
                }
                textModel = textModel.copy(text = filterString.replace("\n".toRegex(), "<br>"))
                applyTextChanges()
            }
            TextWriterContract.Event.OnClickDefaultTextColor -> {
                updateTextColorToDefault()
            }
            TextWriterContract.Event.OnClickDialogConfirm -> {
                setEffect { TextWriterContract.Effect.Close }
            }
        }
    }

    private fun updateAlign(align: TextAlign) {
        textModel = textModel.copy(align = align)
        setState { copy(uiState = TextWriterContract.TextWriterState.UpdateTextAlign(align)) }
    }

    private fun updateTextColor(hexColor: String) {
        textModel = textModel.copy(color = hexColor)
        setState { copy(uiState = TextWriterContract.TextWriterState.UpdateTextColor(hexColor)) }
    }

    private fun updateTextColorToDefault() {
        textModel = textModel.copy(color = defaultTextModel.color)
        setState { copy(uiState = TextWriterContract.TextWriterState.UpdateDefaultTextColor(textModel.color)) }
    }

    private fun willCloseView() {
        if (defaultTextModel == textModel) {
            setEffect { TextWriterContract.Effect.Close }
        } else {
            setEffect { TextWriterContract.Effect.ConfirmHasChanges }
            // 입력하신 내용을 초기화하고\n이전페이지로 이동합니다.
            // 취소 // 확인
        }
    }

    private fun applyTextChanges() {
        updateSceneObjectText
            .invoke(
                UpdateSceneObjectTextUseCase.Params(
                    projectCode = projectCode,
                    sceneObjectDrawIndex = sceneDrawObjectIndex,
                    text = textModel.text,
                    textAlign = textModel.align,
                    hexColor = textModel.color
                )
            )
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.ui)
            .subscribe({
                setEffect { TextWriterContract.Effect.SaveAndClose }
            }, Dlog::e)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

    companion object {
        const val KEY_SCENE_OBJECT_TEXT_DRAW_INDEX = "key sceneobject text draw index"
    }
}