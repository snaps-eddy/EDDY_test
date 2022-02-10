package com.snaps.mobile.presentation.editor.textwritter.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import androidx.core.view.isVisible
import com.snaps.mobile.presentation.editor.databinding.ItemColorParentBinding

class MainColorItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    private val binding = ItemColorParentBinding.inflate(LayoutInflater.from(context), this, true)
    private lateinit var _uiModel: MainColorUiModel
    val uiModel: MainColorUiModel
        get() = _uiModel

    var onClickMainColor: ((MainColorUiModel) -> Unit)? = null

    fun updateModel(model: MainColorUiModel) {
        this.setOnClickListener(null)
        _uiModel = model
        this.setOnClickListener { onClickMainColor?.invoke(uiModel) }
        renderView()
    }

    private fun renderView() {
        binding.viewBg.setBackgroundColor(uiModel.intColor)
    }

    fun mark(check: Boolean = true) {
        binding.viewSelector.isVisible = check
        binding.viewFooter.isVisible = !check
    }

    fun unMark() {
        mark(false)
    }

}