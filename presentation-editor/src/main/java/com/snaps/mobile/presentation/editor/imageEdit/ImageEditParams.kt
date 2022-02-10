package com.snaps.mobile.presentation.editor.imageEdit

import com.snaps.mobile.domain.asset.AnalysisInfo
import com.snaps.mobile.domain.save.Filter

data class ImageEditParams(
    val editOrg: Edit = Edit(),
    val edit: Edit = Edit(),
    val drawIndex: String = "",
    val imageUri: String = "",
    val frameWidth: Float = 0f,
    val frameHeight: Float = 0f,
    val alpha: Float = 1f,
    val orientationAngle: Int = 0,
    val originWidth: Float = 0f,
    val originHeight: Float = 0f,
    val availableMaxWidth: Float = Float.MAX_VALUE,
    val availableMaxHeight: Float = Float.MAX_VALUE
) {
    data class Edit(
        var x: Float = 0f,
        var y: Float = 0f,
        var width: Float = 0f,
        var height: Float = 0f,
        var angle: Int = 0,
        var filter: Filter = Filter.None(),
    )

    fun isValid(): Boolean {
        if (drawIndex.isEmpty()) return false
        if (imageUri.isEmpty()) return false
        if (frameWidth <= 0) return false
        if (frameHeight <= 0) return false
        if (originWidth <= 0) return false
        if (originHeight <= 0) return false
        return true
    }

    fun isModify(): Boolean {
        return edit != editOrg
    }
}