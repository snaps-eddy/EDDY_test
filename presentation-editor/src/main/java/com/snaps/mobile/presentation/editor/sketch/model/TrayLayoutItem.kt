package com.snaps.mobile.presentation.editor.sketch.model

import com.snaps.mobile.domain.template.TemplateScene

/**
 * @param maskCount = 사진 x장 들어가는 템플릿을 의미.
 */
data class TrayLayoutItem(
    val resourceId: String,
    val thumbnailUri: String,
    val templateScene: TemplateScene,
    val maskCount: Int
) {

    var drawWidth: Int = 0
    var drawHeight: Int = 0

}