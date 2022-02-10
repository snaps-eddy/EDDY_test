package com.snaps.mobile.domain.template.ai

import com.snaps.mobile.domain.asset.AnalysisInfo
import com.snaps.mobile.domain.template.Template
import com.snaps.mobile.domain.template.TemplateScene


data class LayoutRecommendPage (
    val scene: TemplateScene,
    val imageKeyList: List<String> = listOf(),
    val analysisInfoMap: Map<String, AnalysisInfo> = HashMap()
)