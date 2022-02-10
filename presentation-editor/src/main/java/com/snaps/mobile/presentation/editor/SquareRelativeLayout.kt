package com.snaps.mobile.presentation.editor

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout
import com.snaps.common.utils.log.Dlog

class SquareRelativeLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        Dlog.d("widthMeasureSpec $widthMeasureSpec")
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }
}