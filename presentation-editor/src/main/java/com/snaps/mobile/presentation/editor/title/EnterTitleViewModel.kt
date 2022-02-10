package com.snaps.mobile.presentation.editor.title

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.snaps.common.android_utils.SchedulerProvider
import com.snaps.common.utils.log.Dlog
import com.snaps.mobile.domain.project.usecase.GetProjectNameUseCase
import com.snaps.mobile.domain.project.usecase.UpdateProjectNameUseCase
import com.snaps.mobile.presentation.editor.EditorViewModel
import com.snaps.mobile.presentation.editor.RecipeParams
import com.snaps.mobile.presentation.editor.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import javax.inject.Inject

@HiltViewModel
class EnterTitleViewModel @Inject constructor(
    private val getProjectName: GetProjectNameUseCase,
    private val updateProjectName: UpdateProjectNameUseCase,
    private val compositeDisposable: CompositeDisposable,
    private val schedulerProvider: SchedulerProvider,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _next = SingleLiveEvent<Unit>()
    val next: LiveData<Unit>
        get() = _next

    private val _close = SingleLiveEvent<Unit>()
    val close: LiveData<Unit>
        get() = _close

    private val _currentProjectName = MutableLiveData<String>()
    val currentProjectName: LiveData<String>
        get() = _currentProjectName

    private val recipeParams: RecipeParams =
        savedStateHandle.get<RecipeParams>(EditorViewModel.RECIPE_PARAMS) ?: throw IllegalArgumentException("Need Recipe Params")
    private val isCreateProcess: Boolean =
        savedStateHandle.get<Boolean>(KEY_IS_CREATE_PROCESS) ?: false

    init {
        getProjectName
            .invoke(recipeParams.projectCode)
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.ui)
            .subscribe({
                _currentProjectName.value = it
            }, {
                Dlog.e(it)
            })
            .addTo(compositeDisposable)
    }

    fun onClickNext(title: String) {
        setTitle(title)
    }

    private fun setTitle(willTitle: String) {
        updateProjectName
            .invoke(
                UpdateProjectNameUseCase.Params(
                    projectCode = recipeParams.projectCode,
                    willTitle = willTitle
                )
            )
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.ui)
            .subscribe({
                Dlog.d(it)
                if (isCreateProcess) {
                    _next.value = Unit
                } else {
                    _close.value = Unit
                }
            }, {
                Dlog.e(it)
            })
            .addTo(compositeDisposable)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

    companion object {
        const val KEY_IS_CREATE_PROCESS = "key is create process"
    }

}