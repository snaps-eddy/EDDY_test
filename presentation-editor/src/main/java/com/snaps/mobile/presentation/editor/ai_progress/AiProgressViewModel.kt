package com.snaps.mobile.presentation.editor.ai_progress

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.snaps.common.android_utils.NetworkChangeMonitor
import com.snaps.common.android_utils.ResourceProvider
import com.snaps.common.android_utils.SchedulerProvider
import com.snaps.common.utils.constant.Config
import com.snaps.common.utils.log.Dlog
import com.snaps.mobile.domain.project.usecase.GetAiTemplateUseCase
import com.snaps.mobile.domain.project.usecase.GetProjectNameUseCase
import com.snaps.mobile.domain.project.usecase.GetRecipeImagesUseCase
import com.snaps.mobile.domain.project.usecase.UploadThumbnailsUseCase
import com.snaps.mobile.presentation.editor.EditorNavigation
import com.snaps.mobile.presentation.editor.EditorViewModel.Companion.RECIPE_PARAMS
import com.snaps.mobile.presentation.editor.R
import com.snaps.mobile.presentation.editor.RecipeParams
import com.snaps.mobile.presentation.editor.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.kotlin.Observables
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.toObservable
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@HiltViewModel
class AiProgressViewModel @Inject constructor(
    private val uploadThumbnails: UploadThumbnailsUseCase,
    private val getAiTemplate: GetAiTemplateUseCase,
    private val compositeDisposable: CompositeDisposable,
    private val schedulerProvider: SchedulerProvider,
    private val getRecipeImages: GetRecipeImagesUseCase,
    private val resourceProvider: ResourceProvider,
    private val getProjectName: GetProjectNameUseCase,
//    private val networkChangeMonitor: NetworkChangeMonitor,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val tag = AiProgressViewModel::class.java.simpleName

    private val _navigation = SingleLiveEvent<EditorNavigation>()
    val navigation: LiveData<EditorNavigation>
        get() = _navigation

    private val _currentProjectName = MutableLiveData<String>()
    val currentProjectName: LiveData<String>
        get() = _currentProjectName

    private val _taskProgress = MutableLiveData<Int>()
    val taskProgress: LiveData<Int>
        get() = _taskProgress

    private val _finishProgress = SingleLiveEvent<Boolean>()
    val finishProgress: LiveData<Boolean>
        get() = _finishProgress

    private val _slideImage = MutableLiveData<Pair<String, Long>>()
    val slideImage: LiveData<Pair<String, Long>>
        get() = _slideImage

    private val _productDesc = MutableLiveData<String>()
    val productDesc: LiveData<String>
        get() = _productDesc

    private val _userName = MutableLiveData<Pair<String, Long>>()
    val userName: LiveData<Pair<String, Long>>
        get() = _userName

    private val _playAnimation = MutableLiveData<Boolean>()
    val playAnimation: LiveData<Boolean>
        get() = _playAnimation

    private val _networkError = MutableLiveData<Pair<Boolean, String>>()
    val networkError: LiveData<Pair<Boolean, String>>
        get() = _networkError

    private val progressSubject = PublishSubject.create<Int>()

    private val recipeParams: RecipeParams = savedStateHandle.get<RecipeParams>(RECIPE_PARAMS) ?: throw IllegalArgumentException("Need Recipe Params")

    private var totalImageCount = Int.MIN_VALUE
    private var uploadImageCount = 1
    private var uploadThumbnailsDisposable: Disposable? = null

    //Debug
    private val lapTimeList = mutableListOf<Long>()
    private val _lapTimeMsg = MutableLiveData<String>()
    val lapTimeMsg: LiveData<String>
        get() = _lapTimeMsg

    init {
        getProjectName.invoke(recipeParams.projectCode)
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.ui)
            .subscribe({
                _currentProjectName.value = it
            }, {
                Dlog.e(it)
            })
            .addTo(compositeDisposable)

        startRecipeImageAnimation()

        startProductDescAnimation()

        startUserNameAnimation(recipeParams.userName)

//        networkChangeMonitor.getObservable()
//            .subscribeOn(schedulerProvider.io)
//            .observeOn(schedulerProvider.ui)
//            .subscribe {
//            }
//            .addTo(compositeDisposable)

        lapTimeList.add(System.currentTimeMillis())
        uploadImages()

        progressSubject
            .observeOn(schedulerProvider.ui)
            .subscribe {
                _taskProgress.value = it.toInt()
            }
            .addTo(compositeDisposable)

        if (Config.isDevelopVersion()) {
            startTimerForDebug()
        }
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

    fun pauseUpload() {
        _playAnimation.value = false
        uploadThumbnailsDisposable?.apply { if (!isDisposed) dispose() }
    }

    fun resumeUpload() {
        _playAnimation.value = true
        uploadThumbnailsDisposable?.apply { if (!isDisposed) dispose() }
        uploadImages()
    }

    private fun uploadImages() {
        uploadThumbnailsDisposable = uploadThumbnails
            .invoke(
                UploadThumbnailsUseCase.Params(
                    projectCode = recipeParams.projectCode,
                    userNo = recipeParams.userNo,
                    deviceId = recipeParams.deviceId,
                    enableFaceFinder = true
                )
            )
            .observeOn(schedulerProvider.io)
            .doOnNext {
                totalImageCount = if (totalImageCount == Int.MIN_VALUE) it else totalImageCount
                progressSubject.onNext((uploadImageCount++ * 95.0f / totalImageCount).toInt())
            }
            .toList()
            .flatMap {
                lapTimeList.add(System.currentTimeMillis())
                getAiTemplate.invoke(
                    GetAiTemplateUseCase.Params(
                        projectCode = recipeParams.projectCode,
                        userNo = recipeParams.userNo,
                        deviceId = recipeParams.deviceId,
                        language = recipeParams.language
                    )
                )
            }
            .doOnSuccess {
                progressSubject.onNext(100)
            }
            .observeOn(schedulerProvider.ui)
            .subscribe({
                _finishProgress.value = true
            }, {
                Dlog.e(it)
                _networkError.value = Pair(true, it.message.toString())
                _playAnimation.value = false
            })
            .addTo(compositeDisposable)
    }

    private fun startRecipeImageAnimation() {
        Observable.zip(
            getRecipeImages.invoke(recipeParams.projectCode).flattenAsObservable { it },
            Observable.interval(0, 4000L, TimeUnit.MILLISECONDS),
            { first, _ -> first })
            .map {
                Pair(it.localUri, 4000L)
            }
            .repeat()
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.ui)
            .subscribe {
                _slideImage.value = it
            }
            .addTo(compositeDisposable)
    }

    private fun startProductDescAnimation() {
        val descMsgList = listOf(
            resourceProvider.getString(R.string.smart_analysis_book_making_desc_b),
            resourceProvider.getString(R.string.smart_analysis_book_making_desc_c),
            resourceProvider.getString(R.string.smart_analysis_book_making_desc_d),
            resourceProvider.getString(R.string.smart_analysis_book_making_desc_e),
            resourceProvider.getString(R.string.smart_analysis_book_making_desc_f)
        )

        Observables.zip(descMsgList.toObservable(), Observable.interval(5500L, 5500L, TimeUnit.MILLISECONDS))
            .map { it.first }
            .repeat()
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.ui)
            .subscribe {
                _productDesc.value = it
            }
            .addTo(compositeDisposable)
    }

    private fun startUserNameAnimation(name: String) {
        Observable.just(name)
            .delay(1000L, TimeUnit.MILLISECONDS)
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.ui)
            .subscribe {
                _userName.value = Pair(it, 4000L)
            }
            .addTo(compositeDisposable)
    }

    //Debug
    private fun startTimerForDebug() {
        Observable.interval(1000, TimeUnit.MILLISECONDS)
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.ui)
            .subscribe {
                if (_networkError.value == null || !_networkError.value!!.first) {
                    val sb = StringBuilder().append(getFormattedTimeHtml(it * 1000))
                    if (lapTimeList.size > 1) {
                        sb.append("   ")
                        sb.append(getFormattedTimeHtml(lapTimeList[1] - lapTimeList[0]))
                    }
                    _lapTimeMsg.value = sb.toString()
                }
            }
            .addTo(compositeDisposable)
    }

    private fun getFormattedTimeHtml(totalMilliSecond: Long): String {
        return String.format("%d:%02d", totalMilliSecond / 1000 / 60, totalMilliSecond / 1000 % 60)
    }
}
