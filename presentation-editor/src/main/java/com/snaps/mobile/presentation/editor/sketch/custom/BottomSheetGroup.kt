package com.snaps.mobile.presentation.editor.sketch.custom

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import com.snaps.mobile.presentation.editor.databinding.ViewBottomSheetGroupBinding
import com.snaps.mobile.presentation.editor.sketch.TrayBackgroundController
import com.snaps.mobile.presentation.editor.sketch.TrayImageController
import com.snaps.mobile.presentation.editor.sketch.TrayLayoutController
import com.snaps.mobile.presentation.editor.sketch.drag.SketchDropZone
import com.snaps.mobile.presentation.editor.sketch.drag.TrayImageDeleteDragListener
import com.snaps.mobile.presentation.editor.sketch.model.ImageMovingData
import com.snaps.mobile.presentation.editor.utils.GridSpacingItemDecoration
import com.snaps.mobile.presentation.editor.utils.dp

class BottomSheetGroup @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : FrameLayout(context, attrs) {

    private val binding = ViewBottomSheetGroupBinding.inflate(LayoutInflater.from(context), this, true)

    fun setTrayPhotoController(trayController: TrayImageController) {
        with(binding.bottomSheetPhotoTray) {
            rvPhotoThumbnails.setController(trayController)
            rvPhotoThumbnails.layoutManager = GridLayoutManager(context, 6)
            rvPhotoThumbnails.addItemDecoration(GridSpacingItemDecoration(4.dp(), false))
            this.root.setOnDragListener { _, _ ->
                // Bottom sheet 가 올라와있을 경우, 그 뒤에 있는 스케치 drop 리스너가 작동하지 못하도록
                // drag event를 여기서 소비한다.
                true
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun setOnTraySwipeTop(callback: () -> Unit) {
        binding.root.setOnTouchListener(object : OnSwipeTouchListener(context) {
            override fun onSwipeTop() {
                callback.invoke()
            }
        })
    }

    @SuppressLint("ClickableViewAccessibility")
    fun setOnTraySwipeBottom(callback: () -> Unit) {
        binding.root.setOnTouchListener(object : OnSwipeTouchListener(context) {
            override fun onSwipeBottom() {
                callback.invoke()
            }
        })
    }

    fun setOnImageDropAtTrayPhoto(callback: (ImageMovingData) -> Unit) {
        binding.bottomSheetPhotoTray.viewDeleteDropZone.setOnDragListener(TrayImageDeleteDragListener(object :
            SketchDropZone<TrayImageDeleteDragListener.DropItem> {
            override fun setEnableDrop(enabled: Boolean) {
            }

            override fun onDrop(item: TrayImageDeleteDragListener.DropItem) {
                callback.invoke(item.imageMovingData)
            }

            override fun onMoveInZone(v: View, event: DragEvent) {
            }
        }))
    }

    /**
     * 포토트레이에 "사진 가져오기" 버튼 Click Listener
     */
    fun setGetMoreListener(getMoreListener: () -> Unit) {
        binding.bottomSheetPhotoTray.tvGetMorePhoto.setOnClickListener {
            getMoreListener.invoke()
        }
    }

    /**
     * 포토트레이에 "사용" Switch Change Listener
     */
    fun setSwitchListener(switchListener: (Boolean) -> Unit) {
        binding.bottomSheetPhotoTray.switchHideImageInSketch.setOnCheckedChangeListener { _, isChecked ->
            switchListener.invoke(isChecked)
        }
    }

    /**
     * 백그라운드 트레이 셋팅.
     */
    fun setTrayBackgroundController(trayBackgroundController: TrayBackgroundController) {
        with(binding.bottomSheetBackgroundTray) {
            rvBackgrounds.setController(trayBackgroundController)
            rvBackgrounds.layoutManager = GridLayoutManager(context, 6)
            rvBackgrounds.addItemDecoration(GridSpacingItemDecoration(4.dp(), false))
            this.root.setOnDragListener { _, _ ->
                // Bottom sheet 가 올라와있을 경우, 그 뒤에 있는 스케치 drop 리스너가 작동하지 못하도록
                // drag event를 여기서 소비한다.
                true
            }
        }
    }

    /**
     * Layout Tray 셋팅
     */
    fun setTrayLAyoutController(trayLayoutController: TrayLayoutController) {
        with(binding.bottomSheetLayoutTray) {
            rvLayouts.setController(trayLayoutController)
            rvLayouts.layoutManager = GridLayoutManager(context, 6)
            rvLayouts.addItemDecoration(GridSpacingItemDecoration(4.dp(), false))
            this.root.setOnDragListener { _, _ ->
                // Bottom sheet 가 올라와있을 경우, 그 뒤에 있는 스케치 drop 리스너가 작동하지 못하도록
                // drag event를 여기서 소비한다.
                true
            }
        }
    }

    fun changeSheetContent(trayType: TrayType) {
        when (trayType) {
            TrayType.Photo -> {
                binding.bottomSheetPhotoTray.root.isVisible = true
                binding.bottomSheetLayoutTray.root.isVisible = false
                binding.bottomSheetBackgroundTray.root.isVisible = false
            }
            TrayType.Layout -> {
                binding.bottomSheetPhotoTray.root.isVisible = false
                binding.bottomSheetLayoutTray.root.isVisible = true
                binding.bottomSheetBackgroundTray.root.isVisible = false
            }
            TrayType.Background -> {
                binding.bottomSheetPhotoTray.root.isVisible = false
                binding.bottomSheetLayoutTray.root.isVisible = false
                binding.bottomSheetBackgroundTray.root.isVisible = true
            }
        }
    }

    private fun findTrayView(trayType: TrayType): View = when (trayType) {
        TrayType.Photo -> binding.bottomSheetPhotoTray.root
        TrayType.Layout -> binding.bottomSheetLayoutTray.root
        TrayType.Background -> binding.bottomSheetBackgroundTray.root
    }

    sealed class TrayType {
        object Photo : TrayType()
        object Layout : TrayType()
        object Background : TrayType()
    }

}