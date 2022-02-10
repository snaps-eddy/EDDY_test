package com.snaps.mobile.presentation.editor.sketch

import android.content.ClipData
import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.addCallback
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import com.google.android.material.button.MaterialButton
import com.snaps.common.utils.log.Dlog
import com.snaps.mobile.presentation.editor.EditorViewModel
import com.snaps.mobile.presentation.editor.R
import com.snaps.mobile.presentation.editor.covercatalog.CoverCatalogFragment
import com.snaps.mobile.presentation.editor.databinding.FragmentSketchBinding
import com.snaps.mobile.presentation.editor.databinding.ToastFillWidthCustomBinding
import com.snaps.mobile.presentation.editor.dialog.EditorDialogService
import com.snaps.mobile.presentation.editor.dialog.EditorDialogState
import com.snaps.mobile.presentation.editor.gallery.GalleryFragment
import com.snaps.mobile.presentation.editor.imageEdit.ImageEditFragment
import com.snaps.mobile.presentation.editor.sketch.custom.BottomSheetGroup
import com.snaps.mobile.presentation.editor.sketch.custom.DeleteImageZoneView
import com.snaps.mobile.presentation.editor.sketch.drag.ScrollDragListener
import com.snaps.mobile.presentation.editor.sketch.drag.model.ClipDataScene
import com.snaps.mobile.presentation.editor.sketch.drag.model.ClipDataTrayImage
import com.snaps.mobile.presentation.editor.sketch.drag.model.ClipDataUserImage
import com.snaps.mobile.presentation.editor.sketch.drag.shadow.SceneShadowBuilder
import com.snaps.mobile.presentation.editor.sketch.drag.shadow.TrayPhotoShadowBuilder
import com.snaps.mobile.presentation.editor.sketch.drag.shadow.UserImageShadowBuilder
import com.snaps.mobile.presentation.editor.sketch.model.BackgroundMovingData
import com.snaps.mobile.presentation.editor.sketch.model.ImageMovingData
import com.snaps.mobile.presentation.editor.sketch.model.SceneItem
import com.snaps.mobile.presentation.editor.sketch.model.TrayLayoutItem
import com.snaps.mobile.presentation.editor.textwritter.TextWriterFragment
import com.snaps.mobile.presentation.editor.title.EnterTitleFragment
import com.snaps.mobile.presentation.editor.utils.dp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class SketchFragment : Fragment() {

    private var _binding: FragmentSketchBinding? = null
    private val binding get() = _binding!!

    private val vm by viewModels<SketchViewModel>()
    private val activityVm by activityViewModels<EditorViewModel>()

    private val firstVisiblePosition = MutableLiveData<Int>()
    private val _userImageDragging = MutableLiveData<Boolean>()
    private val _trayImageDragging = MutableLiveData<Boolean>()

    private val normalConstraintSet = ConstraintSet()
    private val openBottomSheetConstraintSet = ConstraintSet()
    private val startDragUserImageConstraintSet = ConstraintSet()

    @Inject
    lateinit var editorDialogService: EditorDialogService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(this, true) {
            if (isExistOpendBottomSheet()) {
                closeAllBottomSheet()
            } else {
                activityVm.confirmFinishWithoutSave()
            }
        }
        setFragmentResultListener(GalleryFragment.KEY_REQUEST_FRGMENT) { key, bundle ->
            val hasChanged = bundle.getBoolean(GalleryFragment.KEY_TRAY_ITEM_CHANGED)
            if (hasChanged) {
                vm.onChangeTrayItem()
            }
        }

        setFragmentResultListener("ImageEditFragment") { _, bundle ->
            val result = bundle.getBoolean(ImageEditFragment.KEY_SCENE_USER_IMAGE_CHANGED)
            Dlog.d(result)
            vm.onChangeScenes()
        }

        setFragmentResultListener("CoverCatalogFragment") { _, bundle ->
            val result = bundle.getBoolean(CoverCatalogFragment.KEY_COVER_TEMPLATE_CHANGED)
            Dlog.d(result)
            vm.onChangeScenes()
        }

        setFragmentResultListener("EnterTitleFragment") { _, bundle ->
            val result = bundle.getBoolean(EnterTitleFragment.KEY_TITLE_CHANGED)
            Dlog.d(result)
            vm.onChangeScenes()
        }

        setFragmentResultListener("TextWriterFragment") { _, bundle ->
            val result = bundle.getBoolean(TextWriterFragment.KEY_SCENE_USER_TEXT_CHANGED)
            if (result) vm.onChangeScenes()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSketchBinding.inflate(inflater, container, false)
        // 본래 Apply 안에 있던 코드인데, IDE가 버벅돼서 밖으로 뺌.
        binding.rvScenes.apply {
            setHasFixedSize(true)
            setController(sceneController)
            layoutManager = GridLayoutManager(context, 2)
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    if (newState != RecyclerView.SCROLL_STATE_SETTLING) {
                        // 스크롤이 멈추었을 때만 동작하도록
                        onScrollChanged()
                    }
                }
            })
        }

        binding.btnAddCart.setOnClickListener {
            vm.onClickAddCart(binding.cartThumbnail.takeSnapShot())
        }

        binding.btnBack.setOnClickListener {
            activityVm.confirmFinishWithoutSave()
        }
        binding.zoneScrollTop.setOnDragListener(ScrollDragListener(true) {
            binding.rvScenes.scrollBy(0, -100)
        })

        binding.zoneScrollBottom.setOnDragListener(ScrollDragListener(true) {
            binding.rvScenes.scrollBy(0, 100)
        })

        binding.viewDeleteZone.setOnScrollDragListener(ScrollDragListener(true) {
            binding.rvScenes.scrollBy(0, 100)
        })

        binding.viewDeleteZone.landingImageListener = { movingData ->
            vm.onImageDropToDelete(movingData)
        }

        binding.viewBottomSheetGroup.apply {
            setTrayPhotoController(trayPhotoController)
            setOnImageDropAtTrayPhoto { vm.onImageDropToDelete(it) }
            setGetMoreListener { vm.onClickGetMorePhotos() }
            setSwitchListener { vm.onChangeHideImageInSketch(it) }
            setTrayBackgroundController(trayBackgroundController)
            setTrayLAyoutController(trayLayoutController)
            setOnTraySwipeBottom(::closeAllBottomSheet)
        }

        binding.bottomToggleGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            when (checkedId) {
                binding.btnPhotoTray.id -> binding.btnPhotoTray as MaterialButton
                binding.btnLayoutTray.id -> binding.btnLayoutTray as MaterialButton
                binding.btnBgTray.id -> binding.btnBgTray as MaterialButton
                else -> null
            }?.apply {
                if (isChecked) {
                    iconTintMode = PorterDuff.Mode.ADD
                    setIconTintResource(android.R.color.transparent)
                    setTextColor(ContextCompat.getColor(context, R.color.bittersweet))
                } else {
                    iconTintMode = PorterDuff.Mode.SRC_IN
                    setIconTintResource(R.color.cod_gray)
                    setTextColor(ContextCompat.getColor(context, R.color.cod_gray))
                }
            }

            when (checkedId) {
                binding.btnPhotoTray.id -> BottomSheetGroup.TrayType.Photo
                binding.btnLayoutTray.id -> BottomSheetGroup.TrayType.Layout
                binding.btnBgTray.id -> BottomSheetGroup.TrayType.Background
                else -> null
            }?.run {
                showBottomSheet(this, isChecked)
            }
        }

        _userImageDragging.observe(viewLifecycleOwner, {
            if (isExistOpendBottomSheet()) {

            } else {
                binding.viewDeleteZone.isVisible = it
                binding.bottomToggleGroup.isVisible = !it
            }
        })

        _trayImageDragging.observe(viewLifecycleOwner, {
            binding.viewDeleteZone.isVisible = it
            binding.bottomToggleGroup.isVisible = !it
        })

        binding.root.setOnDragListener { v, event ->
            if (event.action == DragEvent.ACTION_DRAG_ENDED) {
                _userImageDragging.postValue(false)
                _trayImageDragging.postValue(false)
            }
            true
        }

        with(binding) {
            normalConstraintSet.clone(root)
            openBottomSheetConstraintSet.clone(root)
            openBottomSheetConstraintSet.apply {
                connect(viewBottomSheetGroup.id, ConstraintSet.BOTTOM, guideBottomGroup.id, ConstraintSet.TOP)
                clear(viewBottomSheetGroup.id, ConstraintSet.TOP)
                connect(zoneScrollBottom.id, ConstraintSet.BOTTOM, viewBottomSheetGroup.id, ConstraintSet.TOP)
                connect(rvScenes.id, ConstraintSet.BOTTOM, viewBottomSheetGroup.id, ConstraintSet.TOP)
            }
            startDragUserImageConstraintSet.clone(root)
        }

        binding.btnTutorial.setOnClickListener {
            vm.onClickTutorial()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm.scenes.observe(viewLifecycleOwner, {
            val isAddEmptyScene = run {
                val preSceneItemCount = sceneController.currentData?.size ?: 0
                val currentSceneItemCount = it.size
                if (preSceneItemCount != 0 && currentSceneItemCount > 1 && currentSceneItemCount > preSceneItemCount) {
                    val lastSceneItem = it.last()
                    if (lastSceneItem.isPage) {
                        lastSceneItem.sceneObjectImageCount == 0 && it[it.size - 2].sceneObjectImageCount == 0
                    } else {
                        lastSceneItem.sceneObjectImageCount == 0
                    }
                } else {
                    false
                }
            }

            sceneController.setData(it)

            if (isAddEmptyScene) {
                lifecycleScope.launch {
                    delay(50)
                    val count = sceneController.adapter.itemCount
                    //이건 페이지 추가 버튼의 잔상이 생기고
                    binding.rvScenes.smoothScrollToPosition(count - 1)

                    //이건 페이지 추가 버튼의 잔상이 생기지 않치만 스크롤이 부자연스럽고
//                    binding.rvScenes.scrollToPosition(count - 1)
//                    binding.rvScenes.smoothScrollBy(0, -1)  //요건 포커스 이동을 위함 꼼수
                }
            }
        })

        vm.trayImages.observe(viewLifecycleOwner, {
            trayPhotoController.setData(it)
        })

        vm.viewState.observe(viewLifecycleOwner, { viewState ->
            when (viewState) {
                SketchViewState.LoadingDone -> {
                    editorDialogService.hideDialog()
                }
                is SketchViewState.GetMoreRecipeImage -> {
                    activityVm.showGetMoreRecipeImages(viewState.maxAddMoreCount)
                }
                is SketchViewState.CoverList -> {
                    activityVm.showCoverChange(viewState.coverTemplateCode)
                }
                is SketchViewState.AddToCart -> {
                    activityVm.addToCart(viewState.cartThumbnail, viewState.hasNotUseTrayImage)
                }
                is SketchViewState.ImageEdit -> {
                    activityVm.showAdjustImageView(
                        viewState.sceneDrawIndex,
                        viewState.sceneObjectDrawIndex,
                        viewState.imgSeq
                    )
                }
//                is SketchViewState.TextWriter -> {
//                    closeAllBottomSheet()
//                    activityVm.showTextWriter(
//                        sceneDrawIndex = viewState.sceneDrawIndex,
//                        sceneObjectDrawIndex = viewState.sceneObjectDrawIndex
//                    )
//                }
                SketchViewState.Tutorial -> {
                    activityVm.showTutorial()
                }
            }
        })

        vm.cartThumbnailSceneItem.observe(viewLifecycleOwner, {
            binding.cartThumbnail.setData(data = it)
        })

        firstVisiblePosition.observe(viewLifecycleOwner, {
            vm.onScrollChanged(it)
        })

        vm.trayBackgroundImages.observe(viewLifecycleOwner, {
            trayBackgroundController.setData(it)
        })

        vm.trayLayouts.observe(viewLifecycleOwner, {
            trayLayoutController.setData(it)
        })

        vm.viewEffect.observe(viewLifecycleOwner, { viewEffect ->
            when (viewEffect) {
                is SketchViewEffect.ShowToast -> {
                    context?.let {
                        Toast(it).apply {
                            this.view = ToastFillWidthCustomBinding.inflate(LayoutInflater.from(it), null, false).apply {
                                tvMsg.text = viewEffect.message
                            }.root
                            setGravity(Gravity.TOP or Gravity.FILL_HORIZONTAL, 0, 0)
                            duration = Toast.LENGTH_SHORT
                        }.show()
                    }
                }
                is SketchViewEffect.ShowDialog -> {
                    if (viewEffect.dialog is EditorDialogState.TextWriter) closeAllBottomSheet()
                    editorDialogService.showDialog(viewEffect.dialog)
                }
            }
        })
    }

    private fun isExistOpendBottomSheet(): Boolean {
        return binding.bottomToggleGroup.checkedButtonId != View.NO_ID
    }

    private fun closeAllBottomSheet() {
        binding.bottomToggleGroup.clearChecked()
    }

    /**
     * Scene recyclerview 스크롤 될 때마다 호출 됨.
     * (정확히는 스크롤 하려고 드래깅 시작 할 때, 드래깅 끝내고 혹은 Settling 끝내고 스크롤이 완전히 멈출 때만 호출된다)
     */
    private fun onScrollChanged() {
        val firstIndex = (binding.rvScenes.layoutManager as GridLayoutManager).findFirstCompletelyVisibleItemPosition()
        val dataIndex = sceneController.getSceneItemAt(firstIndex)
        firstVisiblePosition.postValue(dataIndex)
    }

    private fun showBottomSheet(trayType: BottomSheetGroup.TrayType, isShow: Boolean) {
        val transition = ChangeBounds().apply {
            excludeTarget(binding.rvScenes, true)
        }

        TransitionManager.beginDelayedTransition(binding.root, transition)

        if (isShow) {
            binding.viewBottomSheetGroup.changeSheetContent(trayType)
            openBottomSheetConstraintSet.applyTo(binding.root)
            vm.onShowBottomSheet()?.run {
                lifecycleScope.launch {
                    val position = sceneController.findViewPosition(this@run)
                    delay(50)
                    (binding.rvScenes.layoutManager as GridLayoutManager).scrollToPositionWithOffset(position, 55.dp())
                }
            }

        } else {
            normalConstraintSet.applyTo(binding.root)
        }
    }

    /**
     * Scene Controller와 Tray Controller의 Callback 모음
     */
    private val sceneController = SceneController(object : SceneController.CallbacksListener {

        override fun onDropScene(from: String, to: String, after: Boolean) {
            vm.onSceneDropped(from, to, after)
        }

        override fun onDropImage(from: ImageMovingData, to: ImageMovingData) {
            vm.onImageDropped(from, to)
        }

        override fun onDropImageToAddPage(from: ImageMovingData, prevSceneDrawIndex: String) {
            vm.onDropImageToAddPage(from, prevSceneDrawIndex)
        }

        override fun onSelectScene(sceneDrawIndex: String, selected: Boolean) {
            vm.onSelectScene(sceneDrawIndex, selected)
        }

        override fun onClickChangeLayout(sceneDrawIndex: String) {
            vm.onClickChangeLayout(sceneDrawIndex)
        }

        override fun onClickChangeCover(templateCode: String) {
            vm.onClickChangeCover(templateCode)
        }

        override fun onClickUserImage(sceneDrawIndex: String, sceneObjectDrawIndex: String, imgSeq: String) {
            vm.onClickUserImage(sceneDrawIndex, sceneObjectDrawIndex, imgSeq)
        }

        override fun onClickUserText(sceneDrawIndex: String, sceneObjectDrawIndex: String) {
            vm.onClickText(sceneDrawIndex, sceneObjectDrawIndex)
        }

        /**
         * Scene 롱클릭하여 움직이기 시작할 때 호출/
         */
        override fun onStartDragScene(snapshot: Bitmap, sceneDrawIndex: String, clipData: ClipData) {
            val dragShadow = SceneShadowBuilder(binding.root, snapshot)
            val clipDataScene = ClipDataScene(
                sceneDrawIndex,
                dragShadow,
            )
            ViewCompat.startDragAndDrop(binding.root, clipData, dragShadow, clipDataScene, 0)
        }

        /**
         * Scene 안에 이미지 롱클릭하여 움직이기 시작할 때 호출.
         */
        override fun onStartDragImage(snapshot: Bitmap, movingData: ImageMovingData, clipData: ClipData) {
            binding.viewDeleteZone.setIconState(DeleteImageZoneView.IconState.MoveToTray)
            binding.viewDeleteZone.setEnableScroll(true)
            _userImageDragging.postValue(true)
            val dragShadow = UserImageShadowBuilder(binding.root, snapshot)
            val clipDataUserImage = ClipDataUserImage(
                dragShadow,
                movingData
            )
            ViewCompat.startDragAndDrop(binding.root, clipData, dragShadow, clipDataUserImage, 0)
        }

        override fun onClickAddPage() {
            vm.onClickAddPage()
        }

        override fun onClickDeletePage(rightDrawIndex: String) {
            vm.onClickDeletePage(rightDrawIndex)
        }

        override fun onClickChangeTitle(viewId: Int) {
            activityVm.showModifyTitle()
        }
    })

    private val trayPhotoController = TrayImageController(object : TrayImageController.CallbacksListener {
        override fun onStartDragTrayImage(snapshot: Bitmap, movingData: ImageMovingData, clipData: ClipData) {
            val deleteState = if (movingData.isNotOnSketch) DeleteImageZoneView.IconState.DeleteFromTray else DeleteImageZoneView.IconState.Disable
            binding.viewDeleteZone.setIconState(deleteState)
            binding.viewDeleteZone.setEnableScroll(false)
            _trayImageDragging.postValue(true)
            val dragShadow = TrayPhotoShadowBuilder(binding.root, snapshot)
            val clipDataUserImage = ClipDataTrayImage(
                dragShadow,
                movingData,
                movingData.isOnSketch
            )
            ViewCompat.startDragAndDrop(binding.root, clipData, dragShadow, clipDataUserImage, 0)
        }
    })

    private val trayBackgroundController = TrayBackgroundController(object : TrayBackgroundController.CallbacksListener {
//        override fun onStartDragTrayBackground(snapshot: Bitmap, movingData: BackgroundMovingData, clipData: ClipData) {
//            val dragShadow = TrayBackgroundShadowBuilder(binding.root, snapshot)
//            val clipMovingData = ClipDataTrayBackground(
//                dragShadow,
//                movingData
//            )
//            ViewCompat.startDragAndDrop(binding.root, clipData, dragShadow, clipMovingData, 0)
//        }

        override fun onClickTrayBackground(movingData: BackgroundMovingData) {
            vm.onClickTrayBackground(movingData)
        }
    })

    private val trayLayoutController = TrayLayoutController(object : TrayLayoutController.CallbacksListener {
        override fun onClickLayout(itemData: TrayLayoutItem) {
            vm.onClickTrayLayout(itemData)
        }
    })

    override fun onDestroyView() {
        super.onDestroyView()
        with(binding) {
            rvScenes.adapter = null
            cartThumbnail.unbind()
        }
        _binding = null
    }

}