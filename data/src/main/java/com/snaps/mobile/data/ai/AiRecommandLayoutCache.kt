package com.snaps.mobile.data.ai

import com.snaps.mobile.domain.error.SnapsThrowable
import com.snaps.mobile.domain.project.ImageThumbnail
import com.snaps.mobile.domain.template.TemplateScene
import com.snaps.mobile.domain.template.TemplateSceneObject
import com.snaps.mobile.domain.template.ai.LayoutRecommendPage
import javax.inject.Inject

class AiRecommandLayoutCache @Inject constructor(
) {
    private val templateCacheMap: HashMap<String, List<CacheData>> = hashMapOf()

    private data class CacheData(
        val templateScene: TemplateScene,
        val imageSequenceList: List<String> = listOf(),
    )

    fun isExistLayout(
        pageId: String,
        thumbnailList: List<ImageThumbnail>,
    ) = createKeyByImageThumbnail(pageId, thumbnailList).let { templateCacheMap.containsKey(it) }

    fun setTemplateScene(
        pageId: String,
        layoutRecommendPageList: List<LayoutRecommendPage>
    ) {
        createKeyByImageSequence(pageId, layoutRecommendPageList.first().imageKeyList).let { key ->
            layoutRecommendPageList.forEach {
                if (it.imageKeyList.size != it.scene.sceneObjects.filterIsInstance<TemplateSceneObject.Image>().count()) {
                    throw SnapsThrowable("Image object and image key do not match.")
                }
            }
            templateCacheMap[key] = layoutRecommendPageList.map {
                CacheData(
                    templateScene = it.scene,
                    imageSequenceList = it.imageKeyList
                )
            }
        }
    }

    fun getTemplateScene(
        pageId: String,
        thumbnailList: List<ImageThumbnail>,
        layoutCode: String
    ): LayoutRecommendPage? {
        return createKeyByImageThumbnail(pageId, thumbnailList).let { key ->
            templateCacheMap[key]?.let {
                getTemplateScene(layoutCode = layoutCode, list = it)
            }
        }
    }

    private fun getTemplateScene(
        layoutCode: String,
        list: List<CacheData>
    ): LayoutRecommendPage {
        return when {
            layoutCode.isEmpty() -> list[0]
            else -> nextCacheData(list = list, layoutCode = layoutCode)
        }.let {
            LayoutRecommendPage(
                scene = it.templateScene,
                imageKeyList = it.imageSequenceList,
            )
        }
    }

    private fun nextCacheData(
        list: List<CacheData>,
        layoutCode: String
    ): CacheData {
        return list.indexOfFirst {
            it.templateScene.layoutCode == layoutCode
        }.let { index ->
            if (index == -1) list[0] else list[(index + 1) % list.size]
        }
    }

    private fun createKeyByImageThumbnail(
        pageId: String,
        thumbnailList: List<ImageThumbnail>
    ) = createKeyByImageSequence(
        pageId = pageId,
        imageSequenceList = thumbnailList.map { it.outputImageSequence }
    )

    private fun createKeyByImageSequence(
        pageId: String,
        imageSequenceList: List<String>
    ) = imageSequenceList.sorted().fold(StringBuilder(pageId)) { sb, seq -> sb.append(seq) }.toString()
}