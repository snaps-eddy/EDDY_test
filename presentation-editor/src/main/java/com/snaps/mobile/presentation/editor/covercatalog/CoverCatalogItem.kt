package com.snaps.mobile.presentation.editor.covercatalog

import com.snaps.mobile.domain.template.TemplateScene

data class CoverCatalogItem(
    val coverThumbnailUri: String,
    val templateId: String,
    val templateCode: String,
    val templateScene: TemplateScene?,
    val isSelected: Boolean = false
)