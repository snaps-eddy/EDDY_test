package com.snaps.mobile.presentation.editor

import android.graphics.Bitmap
import android.text.SpannableString
import android.text.Spanned
import android.text.style.RelativeSizeSpan
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.snaps.common.android_utils.ResourceProvider
import com.snaps.common.android_utils.SchedulerProvider
import com.snaps.common.utils.log.Dlog
import com.snaps.mobile.domain.project.usecase.LoadProjectUseCase
import com.snaps.mobile.domain.project.usecase.UploadLeftImagesUseCase
import com.snaps.mobile.domain.project.usecase.UploadSaveUseCase
import com.snaps.mobile.presentation.editor.dialog.EditorDialogState
import com.snaps.mobile.presentation.editor.utils.SingleLiveEvent
import com.snaps.mobile.presentation.editor.utils.handleSnaps
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.subjects.PublishSubject
import javax.inject.Inject

@HiltViewModel
class EditorViewModel @Inject constructor(
    private val loadProject: LoadProjectUseCase,
    private val uploadLeftImages: UploadLeftImagesUseCase,
    private val uploadSave: UploadSaveUseCase,
    private val resourceProvider: ResourceProvider,
    private val schedulerProvider: SchedulerProvider,
    private val compositeDisposable: CompositeDisposable,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private lateinit var recipeParams: RecipeParams

    private val _navigation = SingleLiveEvent<EditorNavigation>()
    val navigation: LiveData<EditorNavigation>
        get() = _navigation

    private val progressSubject = PublishSubject.create<Int>()
    private var hasNotUseTrayImage: Boolean = false

    init {
        savedStateHandle.get<EditorParams>(PRODUCT_EDIT_PARAM)?.run {
            loadProject(this)
        }

        progressSubject.observeOn(schedulerProvider.ui)
            .subscribe(
                {
                    if (it == 100) {
                        val titleMessage = resourceProvider.getString(R.string.done_save_project)
                        val message = buildString {
                            append(titleMessage)
                            if (hasNotUseTrayImage) append("\n\n").append(resourceProvider.getString(R.string.warning_remainning_tray_image))
                        }

                        Dlog.d(Dlog.UI_MACRO, "DIALOG_GO_CART")
                        _navigation.postValue(EditorNavigation.Dialog(
                            EditorDialogState.Choice(
                                message = message,
                                leftButtonLabel = resourceProvider.getString(R.string.photoprint_edit_contiue),
                                rightButtonLabel = resourceProvider.getString(R.string.go_to_cart),
                                rightAction = { _navigation.value = EditorNavigation.Finish(goToCart = true, isFromCart = recipeParams.isFromCart) },
                            ).apply {
                                if (hasNotUseTrayImage) {
                                    spannableString = SpannableString(message).apply {
                                        setSpan(RelativeSizeSpan(1.2f), 0, titleMessage.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                                        setSpan(RelativeSizeSpan(0.5f), titleMessage.length, titleMessage.length + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                                    }
                                }
                            }
                        ))
                    }
                }, {
                    Dlog.e(it)
                }
            )
            .addTo(compositeDisposable)
    }

    private fun loadProject(editorParams: EditorParams) {
        with(editorParams) {
            checkNotNull(this.productCode)
            checkNotNull(this.templateCode)

            loadProject
                .invoke(
                    LoadProjectUseCase.Params(
                        projectCode = this.projectCode,
                        productCode = this.productCode,
                        templateCode = this.templateCode,
                        userNo = this.userNo,
                        deviceId = this.deviceId,
                        locationSearch = this.rawText,
                        glossType = this.glossType,
                        paperCode = this.paperCode,
                        projectCount = this.projectCount?.toInt() ?: -1,
                        appVersion = this.appVersion,
                        ipAddress = this.ipAddress,
                        ISP = this.ISP,
                    )
                )
                .subscribeOn(schedulerProvider.io)
                .observeOn(schedulerProvider.ui)
                .subscribe({ project ->
                    recipeParams = RecipeParams(
                        isFromCart = !this.projectCode.isNullOrBlank(),
                        productCode = this.productCode,
                        projectCode = project.code,
                        templateCode = this.templateCode,
                        deviceId = this.deviceId,
                        userNo = this.userNo,
                        rawText = this.rawText,
                        userName = this.userName,
                        language = this.language,
                        appVersion = this.appVersion,
                        tutorialUrl = this.tutorialUrl
                    )

                    if (this.projectCode == null) {
                        navigateTo(EditorNavigation.Gallery(recipeParams))
                    } else {
                        navigateTo(EditorNavigation.Sketch(recipeParams))
                    }

                }, {
                    it.handleSnaps()?.run {
                        _navigation.postValue(
                            EditorNavigation.Dialog(
                                EditorDialogState.Choice(
                                    message = resourceProvider.getString(R.string.message_http_service_unavailable),
                                    leftAction = {
                                        _navigation.value =
                                            EditorNavigation.Finish(
                                                goToCart = true,
                                                isFromCart = editorParams.productCode != null && editorParams.productCode.isNotEmpty()
                                            )
                                    },
                                    rightButtonLabel = resourceProvider.getString(R.string.retry),
                                    rightAction = { loadProject(editorParams) }
                                ))
                        )
                    }
                    Dlog.e(it)
                })
                .addTo(compositeDisposable)
        }
    }

    fun onCompleteGalleryProcess() {
        navigateTo(EditorNavigation.Title(recipeParams, true))
    }

    fun onCompleteTitleProcess() {
        navigateTo(EditorNavigation.AiProgress(recipeParams))
    }

    fun onCompleteAiProgressProcess() {
        navigateTo(EditorNavigation.Sketch(recipeParams))
    }

    fun finishEditorAndGoToCart() {
        navigateTo(EditorNavigation.Finish(goToCart = true, isFromCart = recipeParams.isFromCart))
    }

    private fun navigateTo(destination: EditorNavigation) {
        _navigation.value = destination
    }

    fun addToCart(cartThumbnail: Bitmap, hasNotUseTrayImage: Boolean) {
        this.hasNotUseTrayImage = hasNotUseTrayImage
        var uploadCountIndex = 1
        _navigation.postValue(
            EditorNavigation.Dialog(
                EditorDialogState.Progress(progressSubject)
            )
        )

        uploadLeftImages
            .invoke(
                UploadLeftImagesUseCase.Params(
                    deviceId = recipeParams.deviceId,
                    userNo = recipeParams.userNo,
                    projectCode = recipeParams.projectCode,
                )
            )
            .doOnNext {
                progressSubject.onNext((uploadCountIndex++ * UPLOAD_LEFT_IMAGE_TASK_WEIGHT / it).toInt())
            }
            .doOnComplete {
                // 업로드할 이미지가 없는 경우...
                progressSubject.onNext(UPLOAD_LEFT_IMAGE_TASK_WEIGHT.toInt())
            }
            .toList()
            .toObservable()
            .flatMap {
                uploadSave.invoke(
                    UploadSaveUseCase.Params(
                        deviceId = recipeParams.deviceId,
                        userNo = recipeParams.userNo,
                        appVesion = recipeParams.appVersion,
                        projectCode = recipeParams.projectCode,
                        cartThumbnail = cartThumbnail
                    )
                ).toObservable()
            }
            .doOnNext {
                progressSubject.onNext(100)
            }
            .observeOn(schedulerProvider.ui)
            .subscribe({
                Dlog.d("Complete ! $it")
            }, {
                _navigation.postValue(
                    EditorNavigation.Dialog(
                        EditorDialogState.Notice(it.message ?: it.toString())
                    )
                )
            })
            .addTo(compositeDisposable)
    }

    fun confirmFinishWithoutSave() {
        _navigation.postValue(EditorNavigation.Dialog(
            EditorDialogState.Choice(
                resourceProvider.getString(R.string.message_back_previous_page_without_save),
                rightButtonLabel = resourceProvider.getString(R.string.quit_editor),
                rightAction = {
                    _navigation.value = EditorNavigation.Finish(goToCart = true, isFromCart = recipeParams.isFromCart)
                }
            )
        ))
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

    fun showAdjustImageView(sceneDrawIndex: String, sceneObjectDrawIndex: String, imgSeq: String) {
        EditorNavigation.ImageEdit(
            recipeParams,
            sceneDrawIndex,
            sceneObjectDrawIndex,
            imgSeq
        ).let(_navigation::postValue)
    }

    fun showGetMoreRecipeImages(maxAddMoreCount: Int) {
        EditorNavigation.ModalGallery(recipeParams, maxAddMoreCount)
            .let(_navigation::postValue)
    }

    fun showCoverChange(templateCode: String) {
        EditorNavigation.CoverCatalog(recipeParams, templateCode)
            .let(_navigation::postValue)
    }

    fun showModifyTitle() {
        EditorNavigation.Title(recipeParams, false)
            .let(_navigation::postValue)
    }

    fun showTutorial() {
        EditorNavigation.Tutorial(recipeParams)
            .let(_navigation::postValue)
    }

    companion object {
        const val PRODUCT_EDIT_PARAM = "product_edit_parameter"
        const val RECIPE_PARAMS = "recipe params"

        const val UPLOAD_LEFT_IMAGE_TASK_WEIGHT = 95.0f
    }

}