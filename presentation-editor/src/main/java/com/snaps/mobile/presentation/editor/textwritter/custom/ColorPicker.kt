package com.snaps.mobile.presentation.editor.textwritter.custom

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.snaps.common.android_utils.ResourceProvider
import com.snaps.mobile.presentation.editor.utils.dp
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ColorPicker @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    @Inject
    lateinit var resourceProvider: ResourceProvider

    private val mainColorViews: MutableList<MainColorItemView> = mutableListOf()
    private val subColorViews: MutableList<SubColorItemView> = mutableListOf()

    private val baseColorBgView: View? = null
    private var baseColorSelector: View? = null
    private val baseFontColor = ""

    var onSelectHexColor: ((String) -> Unit)? = null

    companion object {
        val colorMap = listOf(
            MainColorUiModel("#ffffff", listOf("#ffffff", "#f3f3f3", "#cccccc", "#999999", "#666666", "#000000").map { SubColorUiModel(it) }),
            MainColorUiModel("#980000", listOf("#980000", "#dd7e6b", "#cc4125", "#a61c00", "#85200c", "#5b0f00").map { SubColorUiModel(it) }),
            MainColorUiModel("#ff0000", listOf("#ff0000", "#ea9999", "#e06666", "#cc0000", "#990000", "#660000").map { SubColorUiModel(it) }),
            MainColorUiModel("#ff9900", listOf("#ff9900", "#f9cb9c", "#f6b26b", "#e69138", "#b45f06", "#783f04").map { SubColorUiModel(it) }),
            MainColorUiModel("#ffff00", listOf("#ffff00", "#ffe599", "#ffd966", "#f1c232", "#bf9000", "#7f6000").map { SubColorUiModel(it) }),
            MainColorUiModel("#00ff00", listOf("#00ff00", "#b6d7a8", "#93c47d", "#6aa84f", "#38761d", "#274e13").map { SubColorUiModel(it) }),
            MainColorUiModel("#00ffff", listOf("#00ffff", "#a2c4c9", "#76a5af", "#45818e", "#134f5c", "#0c343d").map { SubColorUiModel(it) }),
            MainColorUiModel("#4a86e8", listOf("#4a86e8", "#a4c2f4", "#6d9eeb", "#3c78d8", "#1155cc", "#1c4587").map { SubColorUiModel(it) }),
            MainColorUiModel("#0000ff", listOf("#0000ff", "#9fc5e8", "#6fa8dc", "#3d85c6", "#0b5394", "#073763").map { SubColorUiModel(it) }),
            MainColorUiModel("#9900ff", listOf("#9900ff", "#b4a7d6", "#8e7cc3", "#674ea7", "#351c75", "#20124d").map { SubColorUiModel(it) }),
            MainColorUiModel("#ff00ff", listOf("#ff00ff", "#d5a6bd", "#c27ba0", "#a64d79", "#741b47", "#4c1130").map { SubColorUiModel(it) })
        )
    }

    init {
        this.orientation = VERTICAL
        this.gravity = Gravity.CENTER_VERTICAL

        val parentColorLayout = LinearLayout(getContext())
        val parentColorLayoutParam = LayoutParams(LayoutParams.MATCH_PARENT, getMainColorLayoutHeight())
        parentColorLayoutParam.leftMargin = 21.dp()
        parentColorLayoutParam.rightMargin = 20.dp()
        parentColorLayout.layoutParams = parentColorLayoutParam
        parentColorLayout.orientation = HORIZONTAL
        this.addView(parentColorLayout)

        val childColorLayout = LinearLayout(getContext())
        val childColorLayoutParams = LayoutParams(LayoutParams.MATCH_PARENT, getSubColorLayoutHeight())
        childColorLayoutParams.topMargin = 11.dp()
        childColorLayoutParams.leftMargin = 16.dp()
        childColorLayoutParams.rightMargin = 16.dp()
        childColorLayout.layoutParams = childColorLayoutParams
        childColorLayout.orientation = HORIZONTAL
        this.addView(childColorLayout)

        colorMap.forEach { mainColorUiModel ->
            MainColorItemView(context)
                .apply {
                    layoutParams = LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f)
                    this.onClickMainColor = { setCurrentMainColor(mainColorUiModel) }
                    updateModel(mainColorUiModel)
                }
                .also {
                    mainColorViews.add(it)
                    parentColorLayout.addView(it)
                }
        }

        colorMap.first().subColors.forEachIndexed { index, subColorUiModel ->
            SubColorItemView(context)
                .apply {
                    layoutParams = LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f).also { if (index > 0) it.leftMargin = 2.dp() }
                    this.onClickSubColor = { setCurrentSubColor(it) }
                    updateModel(subColorUiModel)
                }
                .also {
                    subColorViews.add(it)
                    childColorLayout.addView(it)
                }
        }

        setCurrentMainColor(colorMap.first())
    }

    private fun getMainColorLayoutHeight(): Int {
        val mainColorSize = colorMap.size
        val screenWidth: Int = resourceProvider.getScreenWidth() - 41.dp()
        return screenWidth / mainColorSize + 13.dp()
    }

    private fun getSubColorLayoutHeight(): Int {
        val subColorSize = colorMap.first().subColors.size
        val screenWidth: Int = resourceProvider.getScreenWidth() - 32.dp()
        return screenWidth / subColorSize
    }

    private fun setCurrentMainColor(mainColor: MainColorUiModel) {
        updateMainColorCheckView(mainColor)
    }

    private fun setCurrentSubColor(subColor: SubColorUiModel) {
        subColorViews
            .forEach {
                if (it.uiModel.intColor == subColor.intColor) {
                    it.mark()
                    onSelectHexColor?.invoke(it.uiModel.hexColor)
                } else {
                    it.unMark()
                }
            }
    }

    private fun updateMainColorCheckView(mainColor: MainColorUiModel) {
        mainColorViews.forEach {
            if (it.uiModel.intColor == mainColor.intColor) {
                it.mark()
            } else {
                it.unMark()
            }
        }
        updateSubColorsView(mainColor.subColors)
    }

    private fun updateSubColorsView(subColors: List<SubColorUiModel>) {
        subColorViews.zip(subColors)
            .forEachIndexed { index, pair ->
                pair.first.updateModel(pair.second)
                if (index == 0) {
                    setCurrentSubColor(pair.first.uiModel)
                }
            }
    }

    fun clearSelection() {
        subColorViews.forEach {
            it.unMark()
        }
    }
}

