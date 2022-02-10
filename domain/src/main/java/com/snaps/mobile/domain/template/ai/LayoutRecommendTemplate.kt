package com.snaps.mobile.domain.template.ai

import com.snaps.mobile.domain.template.Template

data class LayoutRecommendTemplate (
    val template: Template,
    val bookTitle: String = "",
    val themeType: Int = 0,
    val templateCode: String,
    val imageKeyList: List<String> = listOf()
)