package com.snaps.mobile.presentation.editor.covercatalog

import androidx.lifecycle.SavedStateHandle
import com.snaps.common.android_utils.ApiProvider
import com.snaps.common.android_utils.SchedulerProvider
import com.snaps.common.utils.log.Dlog
import com.snaps.mobile.domain.asset.usecase.GetAssetSceneCoversUseCase
import com.snaps.mobile.domain.save.usecase.ChangeCoverUseCase
import com.snaps.mobile.presentation.editor.EditorViewModel
import com.snaps.mobile.presentation.editor.RecipeParams
import com.snaps.mobile.presentation.editor.mvi.BaseEditorViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.toObservable
import javax.inject.Inject

@HiltViewModel
class CoverCatalogViewModel @Inject constructor(
    private val getCoverList: GetAssetSceneCoversUseCase,
    private val changeCover: ChangeCoverUseCase,
    private val schedulerProvider: SchedulerProvider,
    private val apiProvider: ApiProvider,
    private val compositeDisposable: CompositeDisposable,
    savedStateHandle: SavedStateHandle,
) : BaseEditorViewModel<CoverCatalogContract.Event, CoverCatalogContract.State, CoverCatalogContract.Effect>() {

    private val recipeParams: RecipeParams =
        savedStateHandle.get<RecipeParams>(EditorViewModel.RECIPE_PARAMS) ?: throw IllegalArgumentException("Need Recipe Params")

    private val currentCoverTemplateCode: String =
        savedStateHandle.get<String>(KEY_CURRENT_COVER_TEMPLATE_CODE) ?: throw IllegalArgumentException("Need Current Cover Template Code.")

    private var coverCatalogItems: List<CoverCatalogItem> = listOf()
    private var selectedCover: CoverCatalogItem? = null

    init {
        selectedCover = CoverCatalogItem(
            coverThumbnailUri = "",
            templateId = "",
            templateCode = currentCoverTemplateCode,
            templateScene = null,
        )
        Dlog.d("Selected template Code : ${selectedCover?.templateCode}")
        fetchCoverList()
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

    private fun fetchCoverList() {

        setState { copy(uiState = CoverCatalogContract.CoverCatalogListState.Loading) }

        getCoverList.invoke(GetAssetSceneCoversUseCase.Params(recipeParams.projectCode, recipeParams.productCode))
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.ui)
            .flatMap { catalogs ->
                catalogs.toObservable()
                    .map {
                        CoverCatalogItem(
                            coverThumbnailUri = apiProvider.newApiBaseUrl.plus(it.coverThumbnailUri),
                            templateCode = it.templateCode,
                            templateId = it.templateCode,
                            templateScene = it.templateScene,
                            isSelected = it.templateCode == selectedCover?.templateCode
                        )
                    }
                    .toList()
            }
            .subscribe({
                coverCatalogItems = it
                setState { copy(uiState = CoverCatalogContract.CoverCatalogListState.Success(coverCatalogItems = coverCatalogItems)) }
            }, {
                Dlog.e(it)
            })
            .addTo(compositeDisposable)
    }

    private fun updateCoverList() {
        setState {
            copy(
                uiState = CoverCatalogContract.CoverCatalogListState.Refresh(
                    coverCatalogItems = coverCatalogItems.map {
                        it.copy(isSelected = it.coverThumbnailUri == selectedCover?.coverThumbnailUri)
                    })
            )
        }
    }

    companion object {
        const val KEY_CURRENT_COVER_TEMPLATE_CODE = "key current cover template code"
    }

    override fun createInitialState(): CoverCatalogContract.State {
        return CoverCatalogContract.State(
            CoverCatalogContract.CoverCatalogListState.Idle
        )
    }

    override fun handleEvent(event: CoverCatalogContract.Event) {
        when (event) {
            is CoverCatalogContract.Event.OnSelectCover -> selectCover(event.item, event.isChecked)
            CoverCatalogContract.Event.OnClickConfirm -> changeCover()
            CoverCatalogContract.Event.OnClickClose -> setCloseUiState()
        }
    }

    private fun selectCover(item: CoverCatalogItem, isChecked: Boolean) {
        selectedCover = if (isChecked) item else null
        updateCoverList()
    }

    private fun changeCover() {
        val selectedCoverTemplateCode = selectedCover?.templateCode
        val selectedCoverTemplateScene = selectedCover?.templateScene

        if (selectedCoverTemplateCode.isNullOrBlank() || selectedCoverTemplateScene == null) {
            setCloseUiState()
            return
        }

        changeCover
            .invoke(
                ChangeCoverUseCase.Params(
                    projectCode = recipeParams.projectCode,
                    sceneTemplateCode = selectedCoverTemplateCode,
                    templateScene = selectedCoverTemplateScene
                )
            )
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.ui)
            .subscribe({
                setEffect { CoverCatalogContract.Effect.SaveAndClose }
            }, {
                Dlog.e(it)
            })
            .addTo(compositeDisposable)
    }

    private fun setCloseUiState() {
        setEffect { CoverCatalogContract.Effect.Close }
    }
}