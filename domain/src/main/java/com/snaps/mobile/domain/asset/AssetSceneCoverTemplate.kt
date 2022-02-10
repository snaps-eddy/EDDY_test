package com.snaps.mobile.domain.asset

import com.snaps.mobile.domain.template.TemplateScene

/**
 * 실제 커버 템플릿 내용이 아닌, 커버 변경 리스트에서 보여줄 축약된 모델.
 * 커버 이미지 썸네일, 템플릿 url, 템플릿 코드) <- list
 */
data class AssetSceneCoverTemplate(
    val coverThumbnailUri: String,
    val templateId: String,
    val templateCode: String,
    val templateScene: TemplateScene
) {
}