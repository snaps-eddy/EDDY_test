package com.snaps.mobile.domain.asset

import com.snaps.mobile.domain.template.TemplateScene

data class AssetSceneLayout(
    val id: String,
    val code: String,
    val thumbnailUri: String,
    val templateScene: TemplateScene,
    val maskCount: Int
)