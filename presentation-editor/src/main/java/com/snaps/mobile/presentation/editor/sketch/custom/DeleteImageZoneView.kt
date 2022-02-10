package com.snaps.mobile.presentation.editor.sketch.custom

import android.content.Context
import android.util.AttributeSet
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AnticipateOvershootInterpolator
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import com.snaps.mobile.presentation.editor.R
import com.snaps.mobile.presentation.editor.databinding.ViewImageDeleteBinding
import com.snaps.mobile.presentation.editor.sketch.drag.BottomImageDeleteDragListener
import com.snaps.mobile.presentation.editor.sketch.drag.ScrollDragListener
import com.snaps.mobile.presentation.editor.sketch.drag.SketchDropZone
import com.snaps.mobile.presentation.editor.sketch.model.ImageMovingData

class DeleteImageZoneView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : FrameLayout(context, attrs), SketchDropZone<BottomImageDeleteDragListener.DropItem> {

    private val binding = ViewImageDeleteBinding.inflate(LayoutInflater.from(context), this, true)
    var landingImageListener: ((ImageMovingData) -> Unit)? = null

    private val deactiveConstraintSet = ConstraintSet()
    private val activeConstraintSet = ConstraintSet()
    private val activeTrans = ChangeBounds().apply {
        this.duration = 100
        this.interpolator = AccelerateDecelerateInterpolator()
    }

    private val deactiveTrans = ChangeBounds().apply {
        this.duration = 100
        this.interpolator = AnticipateOvershootInterpolator()
    }

    init {
        deactiveConstraintSet.clone(binding.root)
        activeConstraintSet.clone(context, R.layout.view_image_delete_over)

        binding.viewDeleteDropZone.tag = "Delete Image Zone View"
        binding.viewDeleteDropZone.setOnDragListener(BottomImageDeleteDragListener(this))
    }

    private fun activeDropIndicator() {
        TransitionManager.beginDelayedTransition(binding.root, activeTrans)
        activeConstraintSet.applyTo(binding.root)
    }


    private fun deactiveDropIndicator() {
        TransitionManager.beginDelayedTransition(binding.root, deactiveTrans)
        deactiveConstraintSet.applyTo(binding.root)
    }

    fun setIconState(iconState : IconState){
        when(iconState){
            IconState.DeleteFromTray -> binding.ivIndicator.setImageResource(R.drawable.tray_image_delete_idle)
            IconState.Disable -> binding.ivIndicator.setImageResource(R.drawable.tray_image_delete_idle_disable)
            IconState.MoveToTray -> binding.ivIndicator.setImageResource(R.drawable.user_image_extract_idle)
        }
    }

    /**
     * Delete 영역 좌, 우에 있는 영역 스크롤 가능 여부
     */
    fun setEnableScroll(enabled: Boolean) {
        // disable일 경우 스크롤 되는걸 막기 위해.
        binding.background.isEnabled = enabled
    }

    /**
     * Drop Zone implementation
     */
    override fun setEnableDrop(enabled: Boolean) {
        if (enabled) {
            activeDropIndicator()
        } else {
            deactiveDropIndicator()
        }
    }

    override fun onMoveInZone(v: View, event: DragEvent) {
        // do nothing
    }

    override fun onDrop(item: BottomImageDeleteDragListener.DropItem) {
        landingImageListener?.invoke(item.imageMovingData)
    }

    fun setOnScrollDragListener(scrollDragListener: ScrollDragListener) {
        binding.background.setOnDragListener(scrollDragListener)
    }

    sealed class IconState {
        object MoveToTray : IconState()
        object DeleteFromTray : IconState()
        object Disable : IconState()
    }

}