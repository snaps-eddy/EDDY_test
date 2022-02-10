package com.snaps.mobile.presentation.editor.textwritter.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import androidx.core.view.isVisible
import com.snaps.mobile.presentation.editor.databinding.ItemColorChildBinding

class SubColorItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    private val binding = ItemColorChildBinding.inflate(LayoutInflater.from(context), this, true)

    private lateinit var _uiModel: SubColorUiModel
    val uiModel: SubColorUiModel
        get() = _uiModel

    var onClickSubColor: ((SubColorUiModel) -> Unit)? = null

    fun updateModel(model: SubColorUiModel) {
        this.setOnClickListener(null)
        this._uiModel = model
        this.setOnClickListener { onClickSubColor?.invoke(uiModel) }
        renderView()
    }

    private fun renderView() {
        binding.viewBg.setBackgroundColor(uiModel.intColor)
    }

    fun mark(check: Boolean = true) {
        binding.viewSelector.isVisible = check
    }

    fun unMark() {
        mark(false)
    }

}