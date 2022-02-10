package com.snaps.mobile.domain.template

import com.snaps.mobile.domain.save.Scene

data class TemplateScene(
    val width: Float,
    val type: Scene.Type,
    val subType: Scene.SubType,
    val sceneObjects: List<TemplateSceneObject>,
    val printCount: Int,
    val height: Float,
    val templateCode: String,
    val hiddenIdx: Int,
    val side: String?,
    val layoutCode: String,
    val layoutType: String,
    val initialMillimeterWidth: Int,
    val initialMillimeterHeight: Int,
    val year: String,
    val month: String,
    val defaultTemplateCode: String,
    val defaultLayoutCode: String,
    val defaultStickerIdList: List<String>,
    val defaultBackgroundId: String,
)