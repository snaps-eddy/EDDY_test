package com.snaps.mobile.presentation.editor.gallery

import android.net.Uri
import androidx.lifecycle.*
import com.snaps.common.android_utils.NetworkProvider
import com.snaps.common.android_utils.ResourceProvider
import com.snaps.common.android_utils.SchedulerProvider
import com.snaps.common.utils.log.Dlog
import com.snaps.mobile.domain.app.usecase.IsExpiredConfirmUseMobileDataUseCase
import com.snaps.mobile.domain.app.usecase.PermitMobileDataUseCase
import com.snaps.mobile.domain.asset.AssetImageType
import com.snaps.mobile.domain.asset.RecipeImage
import com.snaps.mobile.domain.asset.usecase.*
import com.snaps.mobile.domain.product.usecase.GetProductPolicyUseCase
import com.snaps.mobile.presentation.editor.EditorViewModel
import com.snaps.mobile.presentation.editor.R
import com.snaps.mobile.presentation.editor.RecipeParams
import com.snaps.mobile.presentation.editor.dialog.EditorDialogState
import com.snaps.mobile.presentation.editor.dialog.LoadingAnimStyle
import com.snaps.mobile.presentation.editor.gallery.albumdetail.AlbumDetailItem
import com.snaps.mobile.presentation.editor.gallery.albumlist.AlbumListItem
import com.snaps.mobile.presentation.editor.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.toObservable
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val getProductPolicy: GetProductPolicyUseCase,
    private val getHEIFImageCount: GetHEIFImageCountUseCase,
    private val getAlbums: GetAlbumsUseCase,
    private val getAlbumImages: GetAlbumImagesUseCase,
    private val addRecipeImages: AddRecipeImagesUseCase,
    private val addMoreRecipeImages: AddMoreRecipeImagesUseCase,
    private val permitMobileData: PermitMobileDataUseCase,
    private val isExpiredConfirmUseMobileData: IsExpiredConfirmUseMobileDataUseCase,
    private val resourceProvider: ResourceProvider,
    private val networkProvider: NetworkProvider,
    private val schedulerProvider: SchedulerProvider,
    private val compositeDisposable: CompositeDisposable,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _navigation = SingleLiveEvent<GalleryNavigation>()
    val navigation: LiveData<GalleryNavigation>
        get() = _navigation

    private val _viewEffect = SingleLiveEvent<GalleryViewEffect>()
    val viewEffect: LiveData<GalleryViewEffect>
        get() = _viewEffect

    private val _albumList = MutableLiveData<List<AlbumListItem>>()
    val albumList: LiveData<List<AlbumListItem>>
        get() = _albumList

    private val _currentAlbum = MutableLiveData<AlbumListItem>()
    val currentAlbumName = MediatorLiveData<String>().apply {
        addSource(_currentAlbum) {
            value = it.name
        }
    }

    private val _albumImages: MutableList<List<AlbumDetailItem>> = mutableListOf()

    private val liveAlbumImages = MutableLiveData<AlbumImageSet>()
    val albumImages: LiveData<AlbumImageSet>
        get() = liveAlbumImages

    private val _imageSelection: MutableSet<AlbumDetailItem> = mutableSetOf()
    private val liveImageSelection = MutableLiveData<Set<AlbumDetailItem>>()
    val imageSelection = MediatorLiveData<List<AlbumDetailItem>>().apply {
        addSource(liveImageSelection) {
            value = it.toList()
        }
    }
    val imageSelectionCount = MediatorLiveData<Int>().apply {
        addSource(liveImageSelection) {
            value = it.size
        }
    }

    private val recipeParams: RecipeParams =
        savedStateHandle.get<RecipeParams>(EditorViewModel.RECIPE_PARAMS) ?: throw IllegalArgumentException("Need Recipe Params")

    private val home: GalleryHome = savedStateHandle.get<GalleryHome>(KEY_HOME) ?: GalleryHome.AlbumList

    private lateinit var uiPolicy: GalleryUiPolicy

    init {
        getProductPolicy.invoke(GetProductPolicyUseCase.Params(recipeParams.productCode, recipeParams.templateCode))
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.ui)
            .subscribe({ productPolicy ->
                when (home) {
                    GalleryHome.AlbumList -> {
                        uiPolicy = GalleryUiPolicy(
                            minSelectCount = productPolicy.minSelectImageCount,
                            maxSelectCount = productPolicy.maxSelectImageCount,
                        )
                        prepareHomeAsAlbumList()
//                        prepareHomeAsHEIFImageCount()  //이거 하면 prepareHomeAsAlbumList()이게 약간 느려짐
                    }
                    is GalleryHome.AlbumDetails -> {
                        uiPolicy = GalleryUiPolicy(
                            minSelectCount = 0,
                            maxSelectCount = home.maxAddMoreCount,
                        )
                        prepareHomeAsAlbumDetailList()
                    }
                }
                Dlog.d(productPolicy)
            }, {
                Dlog.e(it)
            })

        permitMobileData
            .invoke(PermitMobileDataUseCase.Params(false))
            .subscribeOn(schedulerProvider.ui)
            .observeOn(schedulerProvider.ui)
            .subscribe({
                //성공하던 말던.
            }, {
                Dlog.e(it)
            })
            .addTo(compositeDisposable)
    }

    private fun prepareHomeAsHEIFImageCount() {
        getHEIFImageCount.invoke(AssetImageType.Device)
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.ui)
            .subscribe({
                Dlog.i("HEIFImageCount", "HEIF Image Count: $it")
            }, {
                Dlog.e(it)
            })
            .addTo(compositeDisposable)
    }

    private fun prepareHomeAsAlbumList() {
        getAlbums.invoke(AssetImageType.Device)
            .subscribeOn(schedulerProvider.io)
            .flatMapObservable { it.toObservable() }
            .map {
                AlbumListItem(
                    id = it.id,
                    albumImageType = it.type,
                    thumbnailUri = it.thumbnail,
                    name = it.name,
                    imageCount = it.photoCounts,
                )
            }
            .toList()
            .observeOn(schedulerProvider.ui)
            .subscribe({
                _albumList.value = it
                _navigation.value = GalleryNavigation.AlbumList
            }, {
                Dlog.e(it)
            })
            .addTo(compositeDisposable)
    }

    private fun prepareHomeAsAlbumDetailList() {
        getAlbums.invoke(AssetImageType.Device)
            .subscribeOn(schedulerProvider.io)
            .flatMapObservable { it.toObservable() }
            .map {
                AlbumListItem(
                    id = it.id,
                    albumImageType = it.type,
                    thumbnailUri = it.thumbnail,
                    name = it.name,
                    imageCount = it.photoCounts,
                )
            }
            .toList()
            .observeOn(schedulerProvider.ui)
            .subscribe({
                _albumList.value = it
                findAllImageAlbum()?.run {
                    getAlbumImages(this)
                    _navigation.value = GalleryNavigation.AlbumDetailListAsHome
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

    fun onChangeAlbum(newAlbum: AlbumListItem) {
        getAlbumImages(newAlbum)
    }

    fun onClickAlbum(album: AlbumListItem) {
        getAlbumImages(album)
        _navigation.value = GalleryNavigation.AlbumDetailList
    }

    private fun getAlbumImages(album: AlbumListItem) {
        _albumImages.clear()
        _currentAlbum.value = album
        notifyAlbumImagesChanged()

        val locale = when (recipeParams.language) {
            "ko" -> Locale.KOREA
            "ja" -> Locale.JAPAN
            else -> Locale.ENGLISH
        }
        val dateTitleFormatText = resourceProvider.getString(R.string.date_format_gallery_header)
        val fullDateTitleFormatText = resourceProvider.getString(R.string.date_format_gallery_header_year)
        val dateTitleFormat = SimpleDateFormat(dateTitleFormatText, locale)
        val fullDateTitleFormat = SimpleDateFormat(fullDateTitleFormatText, locale)
        val currentTime = System.currentTimeMillis()

        val todayText = resourceProvider.getString(R.string.today)
        val yesterdayText = resourceProvider.getString(R.string.yesterday)
        val imageSelectionIds = _imageSelection.map { it.id }.toHashSet()
        getAlbumImages.invoke(GetAlbumImagesUseCase.Params(albumId = album.id, assetType = album.albumImageType))
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.computation)
            .map { result ->
                result.map { details ->
                    details.map {
                        AlbumDetailItem.Date(
                            TimeUnit.MILLISECONDS.toDays(it.milliseconds),
                            dateTitleFormat,
                            fullDateTitleFormat,
                            todayText,
                            yesterdayText
                        ).apply {
                            milliseconds = it.milliseconds
                            nowMilliseconds = currentTime
                        }.let { albumDetailItemDate ->
                            AlbumDetailItem(
                                id = it.id,
                                type = it.type,
                                thumbnailUri = it.thumbnailUri,
                                date = albumDetailItemDate,
                                width = it.width,
                                height = it.height,
                                orientation = it.orientation
                            ).apply {
                                selected = imageSelectionIds.contains(id)
                            }
                        }
                    }
                }
            }
            .observeOn(schedulerProvider.ui)
            .subscribe({
                _albumImages.clear()
                _albumImages.addAll(it)
                notifyAlbumImagesChanged()
            }, {
                Dlog.e(it)
            })
    }

    private fun notifyAlbumImagesChanged() {
        liveAlbumImages.value = _albumImages
    }

    private fun notifyImageSelectionChanged() {
        liveImageSelection.value = _imageSelection
    }

    fun onClickImage(image: AlbumDetailItem) {
//        currentDetailImage = imageItem
//        _navigation.value = GalleryNavigation.ToImageDetail
    }

    fun onSelectImage(willSelect: Boolean, item: AlbumDetailItem, callback: (AlbumDetailItem) -> Unit) {
//        val maxCount = productPolicy.maxImageCount
//        if (willSelect && _imageSelection.size >= maxCount) {
        if (willSelect && uiPolicy.isNotAvailableSelectImage(_imageSelection.size)) {
            item.selected = false
            callback.invoke(item)
            String.format(resourceProvider.getString(R.string.can_not_add_more_photo_n), uiPolicy.getMaxSelectCount()).let { msg ->
                _viewEffect.postValue(GalleryViewEffect.ShowToast(msg))
            }
            return
        }

        addSelection(item).run {
            item.selected = this
            callback.invoke(item)
            notifyImageSelectionChanged()
        }
    }

    private fun addSelection(item: AlbumDetailItem): Boolean {
        return _imageSelection.add(item).run {
            if (!this) {
                _imageSelection.remove(item)
            }
            this
        }
    }

    fun onSelectGroup(willSelect: Boolean, groupItems: List<AlbumDetailItem>, callback: (List<AlbumDetailItem>) -> Unit) {
        val isAdd = groupItems.none { it.selected }
        groupItems.toObservable()
            .subscribeOn(schedulerProvider.computation)
            .map {
                it.selected = isAdd && uiPolicy.isAvailableSelectImage(_imageSelection.size)
                if (isAdd && it.selected) _imageSelection.add(it) else _imageSelection.remove(it)
                it
            }.toList()
            .observeOn(schedulerProvider.ui)
            .subscribe({
                if (isAdd && it.firstOrNull { item -> !item.selected } != null) {
                    String.format(resourceProvider.getString(R.string.can_not_add_more_photo_n), uiPolicy.getMaxSelectCount()).let { msg ->
                        _viewEffect.postValue(GalleryViewEffect.ShowToast(msg))
                    }
                }
                notifyImageSelectionChanged()
                callback(it)
            }, Dlog::e)
            .addTo(compositeDisposable)
    }

//    fun onSelectGroup(willSelect: Boolean, groupItems: List<AlbumDetailItem>, callback: (List<AlbumDetailItem>) -> Unit) {
//        if (willSelect && uiPolicy.isAvailableSelectImage(_imageSelection.size)) {
//            groupItems.toObservable()
//                .map {
//                    val isAvaialbeSelect = uiPolicy.isAvailableSelectImage(_imageSelection.size)
//                    if (isAvaialbeSelect) {
//                        _imageSelection.add(it)
//                        it.apply { this.selected = true }
//                    } else {
//                        it
//                    }
//                }
//        } else {
//            groupItems.toObservable()
//                .map {
//                    _imageSelection.remove(it)
//                    it.apply { this.selected = false }
//                }
//        }.run {
//            this.subscribeOn(schedulerProvider.computation)
//                .toList()
//                .observeOn(schedulerProvider.ui)
//                .subscribe({
//                    notifyImageSelectionChanged()
//                    if (willSelect && it.firstOrNull { item -> !item.selected } != null) {
//                        String.format(resourceProvider.getString(R.string.can_not_add_more_photo_n), uiPolicy.getMaxSelectCount()).let { msg ->
//                            _viewEffect.postValue(GalleryViewEffect.ShowToast(msg))
//                        }
//                    }
//                    callback.invoke(it)
//                }, Dlog::e)
//                .addTo(compositeDisposable)
//        }
//    }

    fun onLongClickAddBucket() {
        when (home) {
            GalleryHome.AlbumList -> {
                addBucket(true)
            }
            else -> {
            }
        }
    }

    fun onClickAddBucket() {
        val currentImageCount = _imageSelection.size
        when {
            uiPolicy.isNotEnoughImageCount(currentImageCount) -> {
                String.format(resourceProvider.getString(R.string.not_enough_select_photo_n), uiPolicy.getMinSelectCount()).let {
                    _viewEffect.postValue(GalleryViewEffect.ShowToast(it))
                }
            }
//            uiPolicy.isOverImageCount(currentImageCount) -> {
//                _viewEffect.postValue(GalleryViewEffect.ShowToast("최대 700장까지 추가 가능합니다."))
//            }

            networkProvider.isConnectedMobile() -> {
                isExpiredConfirmUseMobileData
                    .invoke(Unit)
                    .observeOn(schedulerProvider.ui)
                    .subscribe({
                        if (it) {
                            showWarningUseMobileDataDialog()
                        } else {
                            addBucket()
                        }
                    }, {
                        _navigation.postValue(
                            GalleryNavigation.Dialog(
                                EditorDialogState.Notice(
                                    message = it.message ?: ""
                                )
                            )
                        )
                        Dlog.e(it)
                    })
                    .addTo(compositeDisposable)
            }
            networkProvider.isConnectedWifi() -> {
                addBucket()
            }
            else -> {
                _navigation.postValue(
                    GalleryNavigation.Dialog(
                        EditorDialogState.Choice(
                            message = resourceProvider.getString(R.string.message_http_service_unavailable),
                            leftButtonLabel = resourceProvider.getString(R.string.cancel),
                            rightButtonLabel = resourceProvider.getString(R.string.retry),
                            rightAction = { onClickAddBucket() }
                        )
                    )
                )
            }
        }
    }

    private fun addBucket(randomSelect: Boolean = false) {
        val toUploadList = if (randomSelect) {
            _albumImages
                .flatten()
                .shuffled()
                .map {
                    RecipeImage(
                        imgSeq = null,
                        year = null,
                        localId = it.id,
                        type = it.type,
                        localUri = it.thumbnailUri,
                        remoteUri = "",
                        width = it.width,
                        height = it.height,
                        orientation = it.orientation
                    )
                }
                .subList(0, 40)
        } else {
            _imageSelection.map {
                RecipeImage(
                    imgSeq = null,
                    year = null,
                    localId = it.id,
                    type = it.type,
                    localUri = it.thumbnailUri,
                    remoteUri = "",
                    width = it.width,
                    height = it.height,
                    orientation = it.orientation
                )
            }
        }

        when (home) {
            GalleryHome.AlbumList -> {
                if (toUploadList.isEmpty()) {
                    _navigation.postValue(GalleryNavigation.Complete)
                    return
                }
                addRecipeImages.invoke(AddRecipeImagesUseCase.Params(projectCode = recipeParams.projectCode, images = toUploadList))
                    .subscribeOn(schedulerProvider.computation)
                    .observeOn(schedulerProvider.ui)
                    .subscribe({ result ->
                        _navigation.postValue(GalleryNavigation.Complete)
                    }, {
                        Dlog.e(it)
                    })
                    .addTo(compositeDisposable)
            }
            is GalleryHome.AlbumDetails -> {
                if (toUploadList.isEmpty()) {
                    _navigation.postValue(GalleryNavigation.Close(false))
                    return
                }
                _navigation.postValue(GalleryNavigation.Dialog(EditorDialogState.Loading(LoadingAnimStyle.Smalll)))
                addMoreRecipeImages
                    .invoke(
                        AddMoreRecipeImagesUseCase.Params(
                            projectCode = recipeParams.projectCode,
                            images = toUploadList,
                            userNo = recipeParams.userNo,
                            deviceId = recipeParams.deviceId
                        )
                    )
                    .subscribeOn(schedulerProvider.io)
                    .observeOn(schedulerProvider.ui)
                    .subscribe({
                        _navigation.postValue(GalleryNavigation.Close(true))
                    }, {
                        Dlog.e(it)
                    })
            }

        }
    }

    fun onChangeMediaStore(selfChange: Boolean, uri: Uri?) {
        Dlog.d("Media store changed : $selfChange uris : $uri")
    }

    private fun findAllImageAlbum(): AlbumListItem? {
        return _albumList.value?.find { it.id == Long.MAX_VALUE.toString() }
    }

    private fun showWarningUseMobileDataDialog() {
        _navigation.value = GalleryNavigation.Dialog(
            EditorDialogState.Choice(
                message = resourceProvider.getString(R.string.confirm_background_upload_by_cellular_data),
                rightAction = {
                    permitMobileData
                        .invoke(PermitMobileDataUseCase.Params(true))
                        .subscribeOn(schedulerProvider.ui)
                        .observeOn(schedulerProvider.ui)
                        .subscribe({
                            //성공하던 말던.
                            addBucket()
                        }, {
                            Dlog.e(it)
                        })
                        .addTo(compositeDisposable)
                }
            )
        )
    }

    fun onClickDeleteTrayItem(image: AlbumDetailItem, callback: (AlbumDetailItem) -> Unit) {
        onSelectImage(false, image, callback)
    }

    companion object {
        const val KEY_HOME = "KEY_HOME"
    }
}