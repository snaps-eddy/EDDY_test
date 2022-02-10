package com.snaps.mobile.presentation.editor.sketch.custom

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.HorizontalScrollView
import androidx.viewpager.widget.ViewPager
import com.snaps.common.utils.log.Dlog
import kotlin.math.abs

@SuppressLint("ClickableViewAccessibility")
class CoverHorizontalScrollView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : HorizontalScrollView(context, attrs, defStyleAttr) {

    private val gesture = GestureDetector(context, SnapGesture())
    private var currentPage: Int = 2
    private val pageCounts = 3
    private var onPageChange: ((CoverPageSide) -> Unit)? = null

    init {
        setOnTouchListener { v, event ->
            if (gesture.onTouchEvent(event)) {
                true
            } else if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
                val featureWidth: Int = v.measuredWidth
                val child = getChildAt(0) ?: throw IllegalStateException("No Child !")
                val maxScroll = child.measuredWidth - featureWidth
                currentPage = (scrollX / (maxScroll / pageCounts.toFloat())).toInt()
                val step = featureWidth / pageCounts
                val scrollTo: Int = currentPage * step
                smoothScrollTo(scrollTo, 0)
                onPageChanged(currentPage)
                true
            } else {
                false
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        post {
            this.scrollTo(currentPage * measuredWidth / pageCounts, 0)
        }
    }

    inner class SnapGesture : GestureDetector.SimpleOnGestureListener() {
        override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
            if (e1 == null || e2 == null) {
                return false
            }
            if (e1.x - e2.x > 3 && abs(velocityX) > 8000) {
                val featureWidth = measuredWidth
                currentPage = kotlin.run {
                    if (currentPage < pageCounts - 1) {
                        currentPage + 1
                    } else {
                        pageCounts - 1
                    }
                }
                val step = featureWidth / pageCounts
                smoothScrollTo(currentPage * step, 0)
                onPageChanged(currentPage)
                return true
            } else if (e2.x - e1.x > 3 && abs(velocityX) > 8000) {
                val featureWidth = measuredWidth
                val step = featureWidth / pageCounts
                currentPage = if (currentPage > 0) currentPage - 1 else 0
                smoothScrollTo(currentPage * step, 0)
                onPageChanged(currentPage)
                return true
            }
            return false
        }
    }

    private fun onPageChanged(page: Int) {
        val pageSide = when (page) {
            0 -> {
                CoverPageSide.Back
            }
            1 -> {
                CoverPageSide.Spine
            }
            2 -> {
                CoverPageSide.Front
            }
            else -> {
                CoverPageSide.Front
            }
        }
        onPageChange?.invoke(pageSide)
    }

    fun setOnPageChangeListener(listener: ((CoverPageSide) -> Unit)?) {
        this.onPageChange = listener
        this.onPageChanged(currentPage)
    }

    sealed class CoverPageSide {
        object Back : CoverPageSide()
        object Spine : CoverPageSide()
        object Front : CoverPageSide()
    }

}