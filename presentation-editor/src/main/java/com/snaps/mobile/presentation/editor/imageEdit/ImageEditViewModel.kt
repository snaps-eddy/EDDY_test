package com.snaps.mobile.presentation.editor.imageEdit

import androidx.lifecycle.*
import com.google.android.datatransport.runtime.scheduling.persistence.SQLiteEventStore_Factory
import com.snaps.common.android_utils.ApiProvider
import com.snaps.common.android_utils.ResourceProvider
import com.snaps.common.android_utils.SchedulerProvider
import com.snaps.common.utils.log.Dlog
import com.snaps.mobile.domain.asset.AnalysisInfo
import com.snaps.mobile.domain.save.Filter
import com.snaps.mobile.domain.save.InnerImage
import com.snaps.mobile.domain.save.usecase.GetFilteredImageUriUseCase
import com.snaps.mobile.domain.save.usecase.GetPreviewFilteredImagesUseCase
import com.snaps.mobile.domain.save.usecase.GetSceneObjectImagesUseCase
import com.snaps.mobile.domain.save.usecase.UpdateUserImageEditing
import com.snaps.mobile.presentation.editor.EditorViewModel
import com.snaps.mobile.presentation.editor.R
import com.snaps.mobile.presentation.editor.RecipeParams
import com.snaps.mobile.presentation.editor.imageEdit.model.FilterImageItemUiModel
import com.snaps.mobile.presentation.editor.utils.dp
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.toObservable
import javax.inject.Inject

@HiltViewModel
class ImageEditViewModel @Inject constructor(
    private val getSceneObjectImagesUseCase: GetSceneObjectImagesUseCase,
    private val updateUserImageEditing: UpdateUserImageEditing,
    private val getFilteredImageUriUseCase: GetFilteredImageUriUseCase,
    private val getPreviewFilteredImages: GetPreviewFilteredImagesUseCase,
    private val schedulerProvider: SchedulerProvider,
    private val compositeDisposable: CompositeDisposable,
    private val resourceProvider: ResourceProvider,
    private val apiProvider: ApiProvider,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val imageEditParamsList = mutableListOf<ImageEditParams>()

    private val _photoTotatCount = MutableLiveData<Int>()
    val photoTotatCount: LiveData<Int>
        get() = _photoTotatCount

    private val _photoCurrentIndex = MutableLiveData<Int>()
    val photoCurrentIndex: LiveData<Int>
        get() = _photoCurrentIndex

    private val recipeParams: RecipeParams =
        savedStateHandle.get<RecipeParams>(EditorViewModel.RECIPE_PARAMS) ?: throw IllegalArgumentException("Recipe Param Missing")

    private val sceneDrawObjectIndex: String =
        savedStateHandle.get<String>(KEY_SCENE_OBJECT_DRAW_INDEX) ?: throw IllegalArgumentException("Scene Object Draw Index Missing")

    private val projectCode: String
        get() = recipeParams.projectCode

    private val _imageEditParams = MutableLiveData<ImageEditParams>()
    val imageEditParams: LiveData<ImageEditParams>
        get() = _imageEditParams

    private val _currentImageIndex = MutableLiveData<Int>()
    private val _totalImageCount = MutableLiveData<Int>()
    val titleText = MediatorLiveData<String>().apply {
        addSource(_currentImageIndex) {
            value = "$it/${_totalImageCount.value ?: 0}"
        }
        addSource(_totalImageCount) {
            value = "${_currentImageIndex.value}/${it}"
        }
    }

    private val _isApplied = MutableLiveData<Boolean>()
    val isApplied: LiveData<Boolean>
        get() = _isApplied

    private val _filteredImageUri = MutableLiveData<String>()
    val filteredImageUri: LiveData<String>
        get() = _filteredImageUri

    private val _filterPreviews = MutableLiveData<List<FilterImageItemUiModel>>()
    val filterPreviews: LiveData<List<FilterImageItemUiModel>>
        get() = _filterPreviews

    private var imageIndex = 0

    init {
        getSceneObjectImagesUseCase
            .invoke(
                GetSceneObjectImagesUseCase.Params(projectCode)
            )
            .map { list ->
                list.filter { item -> item.sceneObjectImage.content != null && item.sceneObjectImage.innerImage != null }
            }
            .flattenAsObservable { it }
            .map(::mapToImageEditParams)
            .map {
                //TODO:땜방
                it.apply {
                    if (it.edit.angle == 90 || it.edit.angle == 270) {
                        //https://stackoverflow.com/questions/45377802/swap-function-in-kotlin
                        it.edit.width = it.edit.height.also { edit.height = edit.width }
                        it.editOrg.width = it.editOrg.height.also { editOrg.height = editOrg.width }
                    }
                }
            }
            .toList()
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.ui)
            .subscribe(
                {
                    imageEditParamsList.addAll(it)
                    imageIndex = imageEditParamsList.indexOfFirst { item -> item.drawIndex == sceneDrawObjectIndex }
                    _currentImageIndex.postValue(imageIndex + 1)
                    _totalImageCount.postValue(imageEditParamsList.size)
                    _imageEditParams.postValue(imageEditParamsList[imageIndex])
                    getPreviewImages()
                }, {
                    Dlog.e(it)
                }
            )
            .addTo(compositeDisposable)
    }

    private fun mapToImageEditParams(item: GetSceneObjectImagesUseCase.Result): ImageEditParams {
        val imageContent = item.sceneObjectImage.content ?: throw IllegalStateException("content is null")
        val innerImage = item.sceneObjectImage.innerImage ?: throw IllegalStateException("innerImage is null")
        val filter = item.sceneObjectImage.filter ?: throw IllegalStateException("filter is null")

        item.sceneObjectImage.content?.analysisInfo
        val edit = ImageEditParams.Edit(
            x = innerImage.x,
            y = innerImage.y,
            width = innerImage.width,
            height = innerImage.height,
            angle = innerImage.angle,
            filter = filter
        )
        return ImageEditParams(
            editOrg = edit,
            edit = edit.copy(),
            drawIndex = item.sceneObjectImage.drawIndex,
            imageUri = apiProvider.newApiBaseUrl.plus(imageContent.middleImagePath),
            frameWidth = item.sceneObjectImage.width,
            frameHeight = item.sceneObjectImage.height,
            alpha = innerImage.alpha,
            orientationAngle = imageContent.orientationAngle,
            originWidth = imageContent.width,
            originHeight = imageContent.height,
            availableMaxWidth = item.availableMaxWidth,
            availableMaxHeight = item.availableMaxHeight
        )
    }

    private fun applyFilter(filter: Filter) {
        val imageEditParams = imageEditParamsList[imageIndex]
        getFilteredImageUriUseCase
            .invoke(
                GetFilteredImageUriUseCase.Params(
                    imageUri = imageEditParams.imageUri,
                    filter = filter
                )
            )
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.ui)
            .subscribe(
                {
                    val appliedFilter = Filter.fromCode(filter.code, it)
                    imageEditParams.edit.filter = appliedFilter
                    updateImageEditParams(imageEditParams)
                    _filteredImageUri.postValue(it)
                    updatePreviewListApplied(appliedFilter)
                }, {
                }
            )
            .addTo(compositeDisposable)
    }

    fun updateImageEditParams(imageEditParams: ImageEditParams) {
        val index = imageEditParamsList.indexOfFirst { it.drawIndex == imageEditParams.drawIndex }
        imageEditParamsList[index] = imageEditParams
    }

    fun isFirstPhoto(): Boolean {
        return imageIndex == 0
    }

    fun isLastPhoto(): Boolean {
        return imageIndex == imageEditParamsList.size - 1
    }

    fun showPreviousPhoto(): Boolean {
        showPhoto(false)
        getPreviewImages()
        return true
    }

    fun showNextPhoto(): Boolean {
        showPhoto(true)
        getPreviewImages()
        return true
    }

    private fun showPhoto(isNext: Boolean): Boolean {
        when (isNext) {
            true -> if (imageIndex == imageEditParamsList.size - 1) return false
            false -> if (imageIndex == 0) return false
        }
        imageIndex = run {
            val move = if (isNext) 1 else -1
            (imageIndex + move) % imageEditParamsList.size
        }
        _imageEditParams.postValue(imageEditParamsList[imageIndex])
        _currentImageIndex.postValue(imageIndex + 1)
        return true
    }

    fun applyPhotoEdits() {
        imageEditParamsList
            .filter { it.isModify() }
            .toObservable()
            .map {
                it.apply {
                    if (it.edit.angle == 90 || it.edit.angle == 270) {
                        //https://stackoverflow.com/questions/45377802/swap-function-in-kotlin
                        it.edit.width = it.edit.height.also { edit.height = edit.width }
                    }
                }
            }
            .flatMapSingle {
                updateUserImageEditing
                    .invoke(
                        UpdateUserImageEditing.Params(
                            projectCode = projectCode,
                            sceneObjectDrawIndex = it.drawIndex,
                            innerImage = InnerImage(
                                x = it.edit.x,
                                y = it.edit.y,
                                width = it.edit.width,
                                height = it.edit.height,
                                angle = it.edit.angle,
                                alpha = it.alpha
                            ),
                            filter = it.edit.filter
                        )
                    )
            }
            .toList()
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.ui)
            .subscribe({
                _isApplied.postValue(true)
            }, {
                Dlog.e(it)
            })
            .addTo(compositeDisposable)
    }

    fun onSelectFilter(filter: Filter) {
        applyFilter(filter)
    }

    private fun getPreviewImages() {
        imageEditParamsList[imageIndex].let { editParams ->
            getPreviewFilteredImages
                .invoke(
                    GetPreviewFilteredImagesUseCase.Params(
                        imageUri = editParams.imageUri,
                        orientationAngle = editParams.orientationAngle,
                        size = 44.dp() // View 에서 보여질 사이즈
                    )
                )
                .subscribeOn(schedulerProvider.io)
                .flatMapObservable { result ->
                    result.entries.toObservable()
                        .map {
                            val filter = it.key
                            val filteredImageUri = it.value
                            FilterImageItemUiModel(
                                filter = filter,
                                filteredImageUri = filteredImageUri ?: "",
                                filterName = translateFilterName(filter),
                            ).apply {
                                this.isApplied = this.filter.code == editParams.edit.filter.code
                            }
                        }
                }
                .toList()
                .map { list ->
                    //필터 순서 맞추기
                    listOf(
                        Filter.None(),
                        Filter.GrayScale(), Filter.Sephia(), Filter.Sharpen(),
                        Filter.Vintage(), Filter.Warm(), Filter.Amerald(),
                        Filter.OldLight(), Filter.Aurora(), Filter.Winter()
                    ).forEach { filter ->
                        list.firstOrNull { it.filter == filter }?.let {
                            list.remove(it)
                            list.add(0, it)
                        }
                    }
                    list.reversed()
                }
                .observeOn(schedulerProvider.ui)
                .subscribe({
                    _filterPreviews.postValue(it)
                }, {
                    Dlog.e(it)
                })
                .addTo(compositeDisposable)
        }
    }

    fun updatePreviewListApplied(appliedFilter: Filter) {
        _filterPreviews.value = _filterPreviews.value?.map {
            it.apply {
                isApplied = this.filter.code == appliedFilter.code
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

    companion object {
        const val KEY_SCENE_DRAW_INDEX = "key scene draw index"
        const val KEY_SCENE_OBJECT_DRAW_INDEX = "key scene object draw index"
        const val KEY_IMG_SEQ = "key img seq"
    }

    fun translateFilterName(filter: Filter): String {
        return resourceProvider.getString(
            when (filter) {
                is Filter.None -> R.string.original
                is Filter.Amerald -> R.string.emerald
                is Filter.Aurora -> R.string.aurora
                is Filter.GrayScale -> R.string.black_and_white
                is Filter.OldLight -> R.string.faded
                is Filter.Sephia -> R.string.sepia
                is Filter.Sharpen -> R.string.vivid
                is Filter.Vintage -> R.string.vintage
                is Filter.Warm -> R.string.warmth
                is Filter.Winter -> R.string.the_winter
            }
        )
    }
}