package com.snaps.mobile.presentation.editor.dialog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.snaps.common.android_utils.ResourceProvider
import com.snaps.common.android_utils.SchedulerProvider
import com.snaps.common.utils.log.Dlog
import com.snaps.mobile.presentation.editor.R
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import javax.inject.Inject

@HiltViewModel
class EditorDialogViewModel @Inject constructor(
    private val resourceProvider: ResourceProvider,
    private val compositeDisposable: CompositeDisposable,
    private val schedulerProvider: SchedulerProvider
) : ViewModel() {

    private var currentDialogState: EditorDialogState? = null

    private val _message = MutableLiveData<CharSequence>()
    val message: LiveData<CharSequence>
        get() = _message

    private val _leftButtonLabel = MutableLiveData(resourceProvider.getString(R.string.cancel))
    val leftButtonLabel: LiveData<String>
        get() = _leftButtonLabel

    private val _rightButtonLabel = MutableLiveData(resourceProvider.getString(R.string.confirm))
    val rightButtonLabel: LiveData<String>
        get() = _rightButtonLabel

    private val _singleButtonLabel = MutableLiveData(resourceProvider.getString(R.string.confirm))
    val singleButtonLabel: LiveData<String>
        get() = _singleButtonLabel

    private val _progress = MutableLiveData(0)
    val progress: LiveData<Int>
        get() = _progress

    private var leftAction: (() -> Unit)? = null
    private var rightAction: (() -> Unit)? = null
    private var singleAction: (() -> Unit)? = null

    private val _loadingAnimStyle = MutableLiveData<LoadingAnimStyle>(LoadingAnimStyle.Smalll)
    val loadingAnimStyle: LiveData<LoadingAnimStyle>
        get() = _loadingAnimStyle

    fun updateState(state: EditorDialogState) {
        currentDialogState = state
        when (state) {
            is EditorDialogState.Choice -> {
                _message.postValue(state.spannableString ?: state.message)
                _leftButtonLabel.postValue(state.leftButtonLabel ?: resourceProvider.getString(R.string.cancel))
                _rightButtonLabel.postValue(state.rightButtonLabel ?: resourceProvider.getString(R.string.confirm))
                leftAction = state.leftAction
                rightAction = state.rightAction
            }
            is EditorDialogState.Notice -> {
                _message.postValue(state.message)
                _singleButtonLabel.postValue(state.buttonLabel ?: resourceProvider.getString(R.string.confirm))
                singleAction = state.buttonAction
            }
            is EditorDialogState.Loading -> {
                _loadingAnimStyle.postValue(state.style)
            }
            is EditorDialogState.Progress -> {
                _message.postValue(resourceProvider.getString(R.string.uploading_project))
                state.progressStream
                    .observeOn(schedulerProvider.ui)
                    .subscribe({
                        _progress.postValue(it)
                    }, Dlog::e)
                    .addTo(compositeDisposable)
            }
            else -> {
                _message.postValue("")
                _leftButtonLabel.postValue("")
                _rightButtonLabel.postValue("")
                leftAction = null
                rightAction = null
            }
        }
    }

    fun onClickLeftButton() {
        leftAction?.invoke()
    }

    fun onClickRightButton() {
        rightAction?.invoke()
    }

    fun onclickSingleButton() {
        singleAction?.invoke()
    }

    fun onDismissView() {
        TODO("Not yet implemented")
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}