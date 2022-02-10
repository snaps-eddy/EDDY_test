package com.snaps.mobile.presentation.editor.sketch

import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.lifecycle.*
import com.snaps.common.android_utils.ApiProvider
import com.snaps.common.android_utils.NetworkProvider
import com.snaps.common.android_utils.ResourceProvider
import com.snaps.common.android_utils.SchedulerProvider
import com.snaps.common.utils.constant.Config
import com.snaps.common.utils.log.Dlog
import com.snaps.mobile.domain.app.usecase.IsExpiredConfirmUseMobileDataUseCase
import com.snaps.mobile.domain.app.usecase.PermitMobileDataUseCase
import com.snaps.mobile.domain.asset.RecipeImage
import com.snaps.mobile.domain.asset.usecase.GetAssetSceneBackgroundsUseCase
import com.snaps.mobile.domain.asset.usecase.GetAssetSceneLayoutsUseCase
import com.snaps.mobile.domain.error.Reason
import com.snaps.mobile.domain.product.ProductPolicy
import com.snaps.mobile.domain.product.usecase.GetProductPolicyUseCase
import com.snaps.mobile.domain.project.usecase.CheckEditAfterOrderUseCase
import com.snaps.mobile.domain.project.usecase.GetRecipeImagesUseCase
import com.snaps.mobile.domain.project.usecase.GetSaveUseCase
import com.snaps.mobile.domain.save.Save
import com.snaps.mobile.domain.save.Scene
import com.snaps.mobile.domain.save.SceneObject
import com.snaps.mobile.domain.save.usecase.*
import com.snaps.mobile.presentation.editor.BuildConfig
import com.snaps.mobile.presentation.editor.EditorViewModel
import com.snaps.mobile.presentation.editor.R
import com.snaps.mobile.presentation.editor.RecipeParams
import com.snaps.mobile.presentation.editor.dialog.EditorDialogState
import com.snaps.mobile.presentation.editor.dialog.LoadingAnimStyle
import com.snaps.mobile.presentation.editor.sketch.model.*
import com.snaps.mobile.presentation.editor.utils.SingleLiveEvent
import com.snaps.mobile.presentation.editor.utils.dp
import com.snaps.mobile.presentation.editor.utils.drawBitmapCenter
import com.snaps.mobile.presentation.editor.utils.handleSnaps
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleTransformer
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.Singles
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.toObservable
import javax.inject.Inject

@HiltViewModel
class SketchViewModel @Inject constructor(
    private val getProductPolicy: GetProductPolicyUseCase,
    private val checkEditAfterOrder: CheckEditAfterOrderUseCase,
    private val getSave: GetSaveUseCase,
    private val getRecipeImages: GetRecipeImagesUseCase,
    private val getBackgroundImages: GetAssetSceneBackgroundsUseCase,
    private val getAssetSceneLayouts: GetAssetSceneLayoutsUseCase,
    private val replaceScene: MoveSceneUseCase,
    private val swapUserImageForRecipeImage: SwapUserImageForRecipeImageUseCase,
    private val swapUserImageForUserImage: SwapUserImages,
    private val addUserImageToScene: AddUserImageToSceneUseCase,
    private val addRecipeImageToScene: AddRecipeImageToSceneUseCase,
    private val addPageByUserImage: AddPageByUserImageUseCase,
    private val addPageByRecipeImage: AddPageByRecipeImageUseCase,
    private val addPage: AddPageUseCase,
    private val changeAiLayout: ChangeAiLayoutUseCase,
    private val changeLayout: ChangeLayoutUseCase,
    private val changeBackground: ChangeBackgroundUseCase,
    private val deletePage: DeletePageUseCase,
    private val deleteRecipeImage: DeleteRecipeImageUseCase,
    private val extractUserImage: ExtractUserImageUseCase,
    private val isExpiredConfirmUseMobileData: IsExpiredConfirmUseMobileDataUseCase,
    private val permitMobileData: PermitMobileDataUseCase,
    private val networkProvider: NetworkProvider,
    private val sceneUiItemMapper: SceneUiItemMapper,
    private val resourceProvider: ResourceProvider,
    private val apiProvider: ApiProvider,
    private val compositeDisposable: CompositeDisposable,
    private val schedulerProvider: SchedulerProvider,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _scenes = MutableLiveData<List<SceneItem>>()
    val scenes: LiveData<List<SceneItem>>
        get() = _scenes
    val cartThumbnailSceneItem = MediatorLiveData<SceneItem>().apply {
        addSource(_scenes) { list ->
            val target = list.first()
            value = target.copy(
                scaleFactor = 1.0f,
                sceneObjects = target.getDefaultScaleFactor()
            )
        }
    }

    private var useImageInSketch: Boolean = false

    private val _trayImages = MutableLiveData<List<TrayImageItem>>()
    val trayImages = MediatorLiveData<List<TrayImageItem>>().apply {
        addSource(_trayImages) { list ->
            value = if (useImageInSketch) list else list.filterNot { it.onStage }
        }
    }

    private val _trayBackgroundImages = MutableLiveData<List<TrayBackgroundItem>>()
    val trayBackgroundImages: LiveData<List<TrayBackgroundItem>>
        get() = _trayBackgroundImages

    private val _trayLayouts = MutableLiveData<List<TrayLayoutItem>>()
    val trayLayouts = MediatorLiveData<List<TrayLayoutItem>>().apply {
        addSource(_trayLayouts) { list ->
            value = list?.filter { it.maskCount == lockOnScene?.sceneObjectImageCount }
        }
    }

    private val _viewState = SingleLiveEvent<SketchViewState>()
    val viewState: LiveData<SketchViewState>
        get() = _viewState

    private val _viewEffect = SingleLiveEvent<SketchViewEffect>()
    val viewEffect: LiveData<SketchViewEffect>
        get() = _viewEffect


    private val recipeParams: RecipeParams =
        savedStateHandle.get<RecipeParams>(EditorViewModel.RECIPE_PARAMS) ?: throw IllegalArgumentException("Need Recipe Params")
    val projectCode: String
        get() = recipeParams.projectCode
    val productCode: String
        get() = recipeParams.productCode
    val templateCode: String
        get() = recipeParams.templateCode
    val userNo: String
        get() = recipeParams.userNo
    val deviceId: String
        get() = recipeParams.deviceId
    val language: String
        get() = recipeParams.language

    private var lockOnScene: SceneItem? = null

    private lateinit var productPolicy: ProductPolicy

    init {
        getProductPolicy()
        fetchScenes()
        fetchTrayImages()
        fetchTrayBackgroundImages()
        fetchTrayLayouts()
    }

    private fun getProductPolicy() {
        getProductPolicy.invoke(GetProductPolicyUseCase.Params(recipeParams.productCode, recipeParams.templateCode))
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.ui)
            .subscribe({
                this.productPolicy = it
            }, {
                Dlog.e(it)
            })
    }

    private fun fetchScenes(doOnFinishFetch: (() -> Unit)? = null) {
        Single
            .just(projectCode)
            .compose(fetchSaveTransformer())
            .compose(mapScenesTransformer())
            .observeOn(schedulerProvider.ui)
            .subscribe({
                _scenes.value = it
                doOnFinishFetch?.invoke()
            }, {
                Dlog.e(it)
            })
            .addTo(compositeDisposable)
    }

    private fun fetchScenesAndUpdateLockOn(sceneDrawIndex: String) {
        fetchScenes {
            onSelectScene(sceneDrawIndex, true)
        }
    }

    private fun fetchTrayImages() {
        Single.just(projectCode)
            .compose(fetchTrayTransformer())
            .compose(mapTrayImagesTransformer())
            .observeOn(schedulerProvider.ui)
            .subscribe({
                _trayImages.value = it
            }, {
                Dlog.e(it)
            })
            .addTo(compositeDisposable)
    }

    private fun fetchTrayBackgroundImages() {
        val size = measureTrayItemSize()
        getBackgroundImages
            .invoke(GetAssetSceneBackgroundsUseCase.Params(recipeParams.projectCode, recipeParams.productCode))
            .subscribeOn(schedulerProvider.io)
            .flatMapObservable { it.toObservable() }
            .map {
                TrayBackgroundItem(
                    resourceId = it.id,
                    resourceUri = it.resourceUri,
                    thumbnailUri = apiProvider.newApiBaseUrl.plus(it.thumbnailUri),
                ).apply {
                    this.drawWidth = size
                    this.drawHeight = size
                }
            }
            .toList()
            .observeOn(schedulerProvider.ui)
            .subscribe({
                _trayBackgroundImages.value = it
            }, {
                Dlog.e(it)
            })
            .addTo(compositeDisposable)
    }

    private fun fetchTrayLayouts() {
        val size = measureTrayItemSize()
        getAssetSceneLayouts
            .invoke(
                GetAssetSceneLayoutsUseCase.Params(
                    projectCode = recipeParams.projectCode,
                    productCode = recipeParams.productCode,
                    sceneType = Scene.Type.Page
                )
            )
            .subscribeOn(schedulerProvider.io)
            .flatMapObservable { it.toObservable() }
            .map {
                TrayLayoutItem(
                    resourceId = it.id,
                    thumbnailUri = apiProvider.newApiBaseUrl.plus(it.thumbnailUri),
                    templateScene = it.templateScene,
                    maskCount = it.maskCount
                ).apply {
                    this.drawWidth = size
                    this.drawHeight = size
                }
            }
            .toList()
            .observeOn(schedulerProvider.ui)
            .subscribe({
                _trayLayouts.value = it
            }, {
                Dlog.e(it)
            })
            .addTo(compositeDisposable)
    }

    fun onSelectScene(sceneDrawIndex: String, selected: Boolean) {
        lockOnScene = _scenes.value?.find { it.drawIndex == sceneDrawIndex }
        lockOnScene(selected)
    }

    fun onSceneDropped(from: String, to: String, after: Boolean) {
        replaceScene
            .invoke(MoveSceneUseCase.Params(projectCode, from, to, after))
            .compose(mapScenesTransformer())
            .observeOn(schedulerProvider.ui)
            .subscribe({
                _scenes.value = it
                fetchTrayImages()
            }, {
                Dlog.e(it)
            })
            .addTo(compositeDisposable)
    }

    /**
     * @param target 현재 드래그 하고있는 데이터 (사용자 이미지, 트레이 이미지)
     * @param destination 드롭 하려는 곳의 데이터
     */
    fun onImageDropped(target: ImageMovingData, destination: ImageMovingData) {
        when {
            target.isFromTray && destination.toImageView -> {
                Dlog.d("Case 1 : 트레이 -> 이미지 틀로 이미지가 \"교체\" 되는 경우")
                val targetImgSeq = target.imageId ?: return
                val dstSceneDrawIndex = destination.sceneDrawIndex ?: return
                val dstSceneObjectDrawIndex = destination.sceneObjectDrawIndex ?: return
                swapUserImageForRecipeImage
                    .invoke(
                        SwapUserImageForRecipeImageUseCase.Params(
                            projectCode = projectCode,
                            targetImgSeq = targetImgSeq,
                            dstSceneDrawIndex = dstSceneDrawIndex,
                            dstSceneObjectDrawIndex = dstSceneObjectDrawIndex,
                        )
                    )
                    .subscribeOn(schedulerProvider.io)
                    .observeOn(schedulerProvider.ui)
                    .subscribe({
                        fetchScenesAndUpdateLockOn(dstSceneDrawIndex)
                        fetchTrayImages()
                    }, {
                        Dlog.e(it)
                    })
                    .addTo(compositeDisposable)
            }
            // Case 2 : 트레이 -> Scene으로 이미지가 "추가" 되는 경우
            target.isFromTray && destination.toScene -> {
                Dlog.d("Case 2 : 트레이 -> Scene으로 이미지가 \"추가\" 되는 경우")
                val targetImgSeq = target.imageId ?: return
                val dstSceneDrawIndex = destination.sceneDrawIndex ?: return
                addTrayImageToScene(targetImgSeq, dstSceneDrawIndex)
            }

            // Case 3 : 이미지틀 -> 이미지 틀로 이미지가 "교체" 되는 경우
            target.isFromImageView && destination.toImageView -> {
                Dlog.d("Case 3 : 이미지틀 -> 이미지 틀로 이미지가 \"교체\" 되는 경우 -> 상의 결과, 틀은 유지하고 컨텐츠만 스왑한다.")
                val targetSceneIndex = target.sceneDrawIndex ?: return
                val targetSceneObjectIndex = target.sceneObjectDrawIndex ?: return
                val dstSceneDrawIndex = destination.sceneDrawIndex ?: return
                val dstSceneObjectId = destination.sceneObjectDrawIndex ?: return
                Dlog.d("Case 3 : 같은 Scene에서 움직인 경우 -> ${targetSceneIndex == dstSceneDrawIndex}")
                swapUserImageForUserImage
                    .invoke(
                        SwapUserImages.Params(
                            projectCode = projectCode,
                            targetSceneIndex = targetSceneIndex,
                            targetSceneObjectIndex = targetSceneObjectIndex,
                            destinationSceneIndex = dstSceneDrawIndex,
                            destinationSceneObjectIndex = dstSceneObjectId
                        )
                    )
                    .subscribeOn(schedulerProvider.io)
                    .observeOn(schedulerProvider.ui)
                    .subscribe({
                        fetchScenesAndUpdateLockOn(dstSceneDrawIndex)
                    }, {
                        Dlog.e(it)
                    })
                    .addTo(compositeDisposable)
            }
            // Case 4 : 이미지틀 -> Scene으로 이미지가 "추가" 되는 경우
            target.isFromImageView && destination.toScene -> {
                Dlog.d("Case 4 : 이미지틀 -> Scene으로 이미지가 \"추가\" 되는 경우 $target, $destination")
                val targetImgSeq = target.imageId ?: return
                val targetSceneDrawIndex = target.sceneDrawIndex ?: return
                val dstSceneDrawIndex = destination.sceneDrawIndex ?: return
                showLoadingDialog()
                addUserImageToScene
                    .invoke(
                        AddUserImageToSceneUseCase.Params(
                            projectCode = projectCode,
                            targetImgSeq = targetImgSeq,
                            targetSceneDrawIndex = targetSceneDrawIndex,
                            dstSceneDrawIndex = dstSceneDrawIndex,
                            userNo = userNo,
                            deviceId = deviceId,
                            language = language
                        )
                    )
                    .subscribeOn(schedulerProvider.io)
                    .observeOn(schedulerProvider.ui)
                    .doFinally { hideDialog() }
                    .subscribe({
                        fetchScenesAndUpdateLockOn(dstSceneDrawIndex)
                    }, {
                        it.handleSnaps()?.run {
                            if (this is Reason.OverMaxCount) {
                                showToast(resourceProvider.getString(R.string.can_not_add_photo_n, this.max))
                            }
                        }
                        Dlog.e(it)
                    })
                    .addTo(compositeDisposable)
            }
            else -> {
                Dlog.d("Exception !!!")
                Dlog.d("Target : $target")
                Dlog.d("Destination : $destination")
            }
        }
    }

    /**
     * Case 2 : 트레이 -> Scene으로 이미지가 "추가" 되는 경우
     */
    private fun addTrayImageToScene(targetImgSeq: String, dstSceneDrawIndex: String) {
        showLoadingDialog()
        addRecipeImageToScene
            .invoke(
                AddRecipeImageToSceneUseCase.Params(
                    projectCode = projectCode,
                    targetImgSeq = targetImgSeq,
                    dstSceneDrawIndex = dstSceneDrawIndex,
                    userNo = userNo,
                    deviceId = deviceId,
                    language = language
                )
            )
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.ui)
            .doFinally { hideDialog() }
            .subscribe({
                fetchTrayImages()
                fetchScenesAndUpdateLockOn(dstSceneDrawIndex)
            }, {
                it.handleSnaps()?.run {
                    if (this is Reason.OverMaxCount) {
                        showToast(resourceProvider.getString(R.string.can_not_add_photo_n, this.max))
                    }
                }
                Dlog.e(it)
            })
            .addTo(compositeDisposable)
    }

    /**
     * 스킨 까지 적용하긴 했는데 매직넘버가 너무 많다. 어떻게 정리할까..
     */
    fun onClickAddCart(snapShotStream: Single<Pair<Bitmap, Bitmap>>) {
        when {
            networkProvider.isConnectedMobile() -> {
                isExpiredConfirmUseMobileData
                    .invoke(Unit)
                    .subscribe({ isNeedShowDialog ->
                        if (isNeedShowDialog) {
                            showDialog(
                                EditorDialogState.Choice(
                                    message = resourceProvider.getString(R.string.confirm_background_upload_by_cellular_data),
                                    rightAction = {
                                        permitMobileData
                                            .invoke(PermitMobileDataUseCase.Params(true))
                                            .subscribeOn(schedulerProvider.ui)
                                            .observeOn(schedulerProvider.ui)
                                            .subscribe({
                                                checkEditAfterOrder {
                                                    if (it) showErrorDialogEditAfterOrder() else startUpdloadProjectStream(snapShotStream)
                                                }
                                            }, {
                                                showDialog(
                                                    EditorDialogState.Notice(
                                                        message = it.message ?: ""
                                                    )
                                                )
                                                Dlog.e(it)
                                            })
                                            .addTo(compositeDisposable)
                                    }
                                )
                            )
                        } else {
                            checkEditAfterOrder {
                                if (it) showErrorDialogEditAfterOrder() else startUpdloadProjectStream(snapShotStream)
                            }
                        }
                    }, {
                        showDialog(
                            EditorDialogState.Notice(
                                message = it.message ?: ""
                            )
                        )
                        Dlog.e(it)
                    })
                    .addTo(compositeDisposable)
            }

            networkProvider.isConnectedWifi() -> {
                checkEditAfterOrder {
                    if (it) showErrorDialogEditAfterOrder() else startUpdloadProjectStream(snapShotStream)
                }
            }

            else -> {
                showDialog(
                    EditorDialogState.Choice(
                        message = resourceProvider.getString(R.string.message_http_service_unavailable),
                        leftButtonLabel = resourceProvider.getString(R.string.cancel),
                        rightButtonLabel = resourceProvider.getString(R.string.retry),
                        rightAction = { onClickAddCart(snapShotStream) }
                    )
                )
            }
        }
    }

    private fun showErrorDialogEditAfterOrder() {
        showDialog(
            EditorDialogState.Notice(
                message = resourceProvider.getString(R.string.failed_order_because_already_complated_order),
                buttonLabel = resourceProvider.getString(R.string.confirm)
            )
        )
    }

    private fun checkEditAfterOrder(callBack: (Boolean) -> Unit) {
        if (!Config.isRealServer() && Config.isDevelopVersion()) {
            callBack(false)
            return
        }

        checkEditAfterOrder
            .invoke(
                CheckEditAfterOrderUseCase.Params(
                    deviceId = deviceId,
                    userNo = userNo,
                    projectCode = projectCode,
                )
            )
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.ui)
            .subscribe({
                callBack(it)
            }, {
                showDialog(
                    EditorDialogState.Notice(
                        message = it.message ?: ""
                    )
                )
                Dlog.e(it)
            })
            .addTo(compositeDisposable)
    }

    private fun startUpdloadProjectStream(snapShotStream: Single<Pair<Bitmap, Bitmap>>) {
        snapShotStream
            .map { bitmaps ->
                val skin = bitmaps.first
                val snapshot = bitmaps.second

                Bitmap.createBitmap(snapshot, snapshot.width / 2, 0, snapshot.width / 2, snapshot.height).run {
                    if (this != snapshot) {
                        snapshot.recycle()
                    }
                    val scaledSnapshot = Bitmap.createScaledBitmap(this, (408 * 0.81).toInt(), (408 * 0.81).toInt(), false)
                    this.recycle()
                    val cartThumbnail = Bitmap.createBitmap(480, 480, Bitmap.Config.ARGB_8888)

                    val canvas = Canvas(cartThumbnail)
                    canvas.drawBitmapCenter(scaledSnapshot)
                    canvas.drawBitmapCenter(skin)
                    if (cartThumbnail != scaledSnapshot) {
                        scaledSnapshot.recycle()
                    }
                    cartThumbnail
                }
            }.subscribe({
                val notUsedTrayImageCount = _trayImages.value?.filterNot { trayImage -> trayImage.onStage }?.size ?: 0
                _viewState.value = SketchViewState.AddToCart(it, notUsedTrayImageCount > 0)
            }, {
                Dlog.e(it)
            })
            .addTo(compositeDisposable)
    }

    fun onClickChangeLayout(sceneDrawIndex: String) {
        showLoadingDialog()
        onSelectScene(sceneDrawIndex, true)
        changeAiLayout
            .invoke(
                ChangeAiLayoutUseCase.Params(
                    projectCode = projectCode,
                    productCode = productCode,
                    userNo = userNo,
                    deviceId = deviceId,
                    language = language,
                    currentSceneDrawId = sceneDrawIndex
                )
            )
            .subscribeOn(schedulerProvider.io)
            .compose(mapScenesTransformer())
            .observeOn(schedulerProvider.ui)
            .doFinally { hideDialog() }
            .subscribe({
                _scenes.value = it
                _viewState.value = SketchViewState.LoadingDone
            }, Dlog::e)
    }

    private fun fetchSaveTransformer(): SingleTransformer<String, Save> = SingleTransformer { upstream ->
        upstream
            .observeOn(schedulerProvider.io)
            .flatMap {
                getSave.invoke(it)
            }
    }

    private fun fetchTrayTransformer(): SingleTransformer<String, Pair<Save, List<RecipeImage>>> = SingleTransformer { upstream ->
        upstream
            .observeOn(schedulerProvider.io)
            .flatMap {
                Singles.zip(getSave.invoke(it), getRecipeImages.invoke(it))
            }
    }

    private fun mapScenesTransformer(): SingleTransformer<Save, List<SceneItem>> = SingleTransformer { upstream ->
        upstream
            .observeOn(schedulerProvider.computation)
            .flatMapObservable { it.scenes.toObservable() }
            .map(sceneUiItemMapper::mapToItem)
            .map {
                it.apply {
                    isLockOn = this.drawIndex == lockOnScene?.drawIndex
                }
            }
            .toList()
    }

    private fun mapTrayImagesTransformer(): SingleTransformer<Pair<Save, List<RecipeImage>>, List<TrayImageItem>> = SingleTransformer { upstream ->
        val size = measureTrayItemSize()
        upstream
            .observeOn(schedulerProvider.computation)
            .flatMapObservable { result ->
                val save = result.first
                val recipeImages = result.second
                val attachedImages = save.scenes
                    .flatMap { it.sceneObjects }
                    .filterIsInstance<SceneObject.Image>()
                    .mapNotNull { it.content }

                recipeImages.toObservable()
                    .map { recipeImage ->
                        TrayImageItem(
                            imgSeq = recipeImage.imgSeq,
                            year = recipeImage.year,
                            thumbnailUri = apiProvider.newApiBaseUrl.plus(recipeImage.remoteUri),
                            localId = recipeImage.localId,
                            orientationAngle = recipeImage.orientationAngle
                        ).apply {
                            this.onStage = attachedImages.find { it.imgSeq == this.imgSeq } != null
                            this.drawWidth = size
                            this.drawHeight = size
                        }
                    }
            }
            .toList()
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

    fun onClickUserImage(sceneDrawIndex: String, sceneObjectDrawIndex: String, imgSeq: String) {
        _viewState.postValue(SketchViewState.ImageEdit(sceneDrawIndex, sceneObjectDrawIndex, imgSeq))
    }

    fun onClickGetMorePhotos() {
//        val maxImageCount = productPolicy.maxSelectImageCount // 이것도 도메인으로 옮길까..
//        val availableAddCount = _trayImages.value?.size?.run {
//            min(maxImageCount - this, productPolicy.maxAddMoreImageCount)
//        } ?: 0 // 0 이면 추가 못하도록 ? or 뷰에서 표현
//        _viewState.postValue(SketchViewState.GetMoreRecipeImage(availableAddCount))
        _viewState.postValue(SketchViewState.GetMoreRecipeImage(productPolicy.maxAddMoreImageCount))
    }

    fun onClickChangeCover(templateCode: String) {
        _viewState.postValue(SketchViewState.CoverList(templateCode))
    }

    fun onChangeTrayItem() {
        fetchTrayImages()
    }

    fun onChangeScenes() {
        Dlog.d("Fetch New Scenes !!")
        fetchScenes()
    }

    fun onChangeHideImageInSketch(checked: Boolean) {
        useImageInSketch = checked
        _trayImages.value = _trayImages.value
    }

    fun onClickText(sceneDrawIndex: String, sceneObjectDrawIndex: String) {
        showDialog(EditorDialogState.TextWriter(recipeParams, sceneDrawIndex, sceneObjectDrawIndex))
    }

    fun measureTrayItemSize(): Int {
        val screenWidth = resourceProvider.getScreenWidth()
        val cellCount = 6
        val outerPadding = 8.dp()
        val innerPadding = 4.dp()
        return (screenWidth - (2 * outerPadding) - ((cellCount - 1) * innerPadding)) / cellCount
    }

    fun onScrollChanged(dataIndex: Int) {
        val pageStartIndex = _scenes.value
            ?.firstOrNull { it.subType == Scene.SubType.Page }
            ?.run { _scenes.value?.indexOf(this) } ?: 0
        lockOnScene = _scenes.value?.get(pageStartIndex.coerceAtLeast(dataIndex))
        lockOnScene(true)
    }

    private fun lockOnScene(selected: Boolean) {
        _scenes.value = _scenes.value?.map {
            it.apply {
                isLockOn = this.drawIndex == lockOnScene?.drawIndex && selected
            }
        }
        _trayLayouts.value = _trayLayouts.value
    }

    fun onDropImageToAddPage(target: ImageMovingData, dstSceneDrawIndex: String) {
        val targetImgSeq = target.imageId ?: return
        when {
            target.isFromImageView -> {
                val targetSceneDrawIndex = target.sceneDrawIndex ?: return
                val targetSceneObjectDrawIndex = target.sceneObjectDrawIndex ?: return
                addPageWithUserImage(targetImgSeq, targetSceneDrawIndex, targetSceneObjectDrawIndex, dstSceneDrawIndex)
            }
            target.isFromTray -> {
                addPageWithTrayImage(targetImgSeq, dstSceneDrawIndex)
            }
        }
    }

    private fun addPageWithUserImage(targetImgSeq: String, targetSceneDrawIndex: String, targetSceneObjectDrawIndex: String, dstSceneDrawIndexToNext: String) {
        showLoadingDialog()
        addPageByUserImage
            .invoke(
                AddPageByUserImageUseCase.Params(
                    projectCode = projectCode,
                    deviceId = recipeParams.deviceId,
                    userNo = recipeParams.userNo,
                    language = recipeParams.language,
                    prevSceneDrawIndex = dstSceneDrawIndexToNext,
                    targetImgSeq = targetImgSeq,
                    targetSceneDrawIndex = targetSceneDrawIndex,
                    targetSceneObjectDrawIndex = targetSceneObjectDrawIndex
                )
            )
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.ui)
            .doFinally { hideDialog() }
            .subscribe({
                fetchScenesAndUpdateLockOn(targetSceneDrawIndex)
            }, {
                it.handleSnaps()?.run {
                    if (this is Reason.OverMaxCount) {
                        showToast(resourceProvider.getString(R.string.can_not_add_page_n, this.max))
                    }
                }
                Dlog.e(it)
            })
            .addTo(compositeDisposable)
    }

    private fun addPageWithTrayImage(targetImgSeq: String, dstSceneDrawIndexToNext: String) {
        showLoadingDialog()
        addPageByRecipeImage
            .invoke(
                AddPageByRecipeImageUseCase.Params(
                    projectCode = projectCode,
                    deviceId = recipeParams.deviceId,
                    userNo = recipeParams.userNo,
                    language = recipeParams.language,
                    prevSceneDrawIndex = dstSceneDrawIndexToNext,
                    targetImgSeq = targetImgSeq,
                )
            )
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.ui)
            .doFinally { hideDialog() }
            .subscribe({
                fetchScenes()
                fetchTrayImages()
            }, {
                it.handleSnaps()?.run {
                    if (this is Reason.OverMaxCount) {
                        showToast(resourceProvider.getString(R.string.can_not_add_page_n, this.max))
                    }
                }
                Dlog.e(it)
            })
            .addTo(compositeDisposable)
    }

    fun onImageDropToDelete(target: ImageMovingData) {
        val targetImgSeq = target.imageId ?: return
        when {
            target.isFromImageView -> {
                val targetSceneDrawIndex = target.sceneDrawIndex ?: return
                extractImage(targetImgSeq, targetSceneDrawIndex)
            }
            target.isFromTray -> {
                deleteTrayImage(targetImgSeq)
            }
        }
    }

    private fun extractImage(targetImgSeq: String, targetSceneDrawIndex: String) {
        showLoadingDialog()
        extractUserImage
            .invoke(
                ExtractUserImageUseCase.Params(
                    projectCode = projectCode,
                    targetImgSeq = targetImgSeq,
                    targetSceneDrawIndex = targetSceneDrawIndex,
                    deviceId = deviceId,
                    userNo = userNo,
                    language = language
                )
            )
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.ui)
            .doFinally { hideDialog() }
            .subscribe({
                fetchScenesAndUpdateLockOn(targetSceneDrawIndex)
                fetchTrayImages()
            }, {
                Dlog.e(it)
            })
            .addTo(compositeDisposable)
    }

    private fun deleteTrayImage(targetImgSeq: String) {
        deleteRecipeImage
            .invoke(
                DeleteRecipeImageUseCase.Params(
                    projectCode = projectCode,
                    targetImgSeq = targetImgSeq,
                )
            )
            .subscribeOn(schedulerProvider.computation)
            .observeOn(schedulerProvider.ui)
            .subscribe({
                fetchTrayImages()
            }, {
                Dlog.e(it)
            })
            .addTo(compositeDisposable)
    }

    /**
     * Scene List 하단의 버튼 눌렀을 경우.
     */
    fun onClickAddPage() {
        // Scene 하단에 "페이지 추가하기" 눌렀을 때.
        addPage
            .invoke(AddPageUseCase.Params(projectCode))
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.ui)
            .subscribe({
                fetchScenes()
            }, {
                it.handleSnaps()?.run {
                    if (this is Reason.OverMaxCount) {
                        showToast(resourceProvider.getString(R.string.can_not_add_page_n, this.max))
                    }
                }
                Dlog.e(it)
            })
            .addTo(compositeDisposable)
    }

    fun onClickDeletePage(rightDrawIndex: String) {
        // 현재 포커스 중인 데이터를 날려준다. (혹시 같을 수 있으니)
        lockOnScene = null

        deletePage
            .invoke(DeletePageUseCase.Params(projectCode, rightDrawIndex))
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.ui)
            .subscribe({
                fetchScenes()
                fetchTrayImages()
            }, {
                it.handleSnaps()?.run {
                    if (this is Reason.UnderMinCount) {
                        showToast(resourceProvider.getString(R.string.can_not_delete_page_n, this.min))
                    }
                }
                Dlog.e(it)
            })
            .addTo(compositeDisposable)
    }

    /**
     * Bottom sheet가 열렸을 때 호출. MVI 로 구현할 수 있지만 일단 귀찮다....
     */
    fun onShowBottomSheet(): SceneItem? {
        return lockOnScene
    }

    fun onClickTrayLayout(itemData: TrayLayoutItem) {
        val lockOnSceneDrawIndex = lockOnScene?.drawIndex ?: return
        changeLayout
            .invoke(
                ChangeLayoutUseCase.Params(
                    projectCode = projectCode,
                    dstSceneDrawIndex = lockOnSceneDrawIndex,
                    templateScene = itemData.templateScene
                )
            )
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.ui)
            .subscribe({
                fetchScenes()
            }, {
                Dlog.e(it)
            })
            .addTo(compositeDisposable)
    }

    private fun showLoadingDialog() {
        showDialog(EditorDialogState.Loading(LoadingAnimStyle.Smalll))
    }

    private fun hideDialog() {
        showDialog(EditorDialogState.Hide)
    }

    private fun showToast(message: String) {
        _viewEffect.value = SketchViewEffect.ShowToast(message)
    }

    private fun showDialog(dialog: EditorDialogState) {
        _viewEffect.value = SketchViewEffect.ShowDialog(dialog)
    }

    fun onClickTrayBackground(target: BackgroundMovingData) {
        lockOnScene?.run {
            changeBackground
                .invoke(
                    ChangeBackgroundUseCase.Params(
                        projectCode = projectCode,
                        resourceId = target.resourceId,
                        resourceUri = target.resourceUri,
                        dstSceneDrawIndex = this.drawIndex
                    )
                )
                .subscribeOn(schedulerProvider.io)
                .observeOn(schedulerProvider.ui)
                .subscribe({
                    fetchScenes()
                    fetchTrayImages()
                }, {
                    it.handleSnaps()?.run {
                        if (BuildConfig.DEBUG) {
                            showErrorDialog(this.message)
                        }
                    }
                    Dlog.e(it)
                })
                .addTo(compositeDisposable)
        } ?: Dlog.d("No Locked on scene !")
    }

    private fun showErrorDialog(message: String) {
        showDialog(EditorDialogState.Notice(message = message))
    }

    fun onClickTutorial() {
        _viewState.postValue(SketchViewState.Tutorial)
    }

}
