package com.snaps.mobile.data.project

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import com.google.gson.Gson
import com.snaps.mobile.data.SceneMapper
import com.snaps.mobile.data.ai.AiRecommandLayoutCache
import com.snaps.mobile.data.asset.ImageFilterProcessor
import com.snaps.mobile.data.asset.ThumbImageInfo
import com.snaps.mobile.data.asset.ThumbImageMaker
import com.snaps.mobile.data.asset.ThumbImageStorage
import com.snaps.mobile.domain.project.ImageThumbnail
import com.snaps.mobile.domain.project.Project
import com.snaps.mobile.domain.save.Filter
import com.snaps.mobile.domain.save.Save
import com.snaps.mobile.domain.template.ai.LayoutRecommendPage
import io.reactivex.rxjava3.core.Single
import java.io.File
import java.io.IOException
import javax.inject.Inject

/**
 * Gson 유니코드 파싱 문제 때문에 Gson을 주입 받아서 사용하도록.
 */
class LocalProjectDataSource @Inject constructor(
    private val resources: Resources,
    private val context: Context,
    private val aiRecommandLayoutCache: AiRecommandLayoutCache,
    private val imageFilterProcessor: ImageFilterProcessor,
    private val thumbImageMaker: ThumbImageMaker,
    private val thumbImageStorage: ThumbImageStorage,
    private val sceneMapper: SceneMapper,
    private val gson : Gson
) {
    private var project: Project? = null

    fun cache(project: Project): Project {
        this.project = project
        return this.project!!
    }

    fun getProject(): Project? {
        return project
    }

    fun isExistAiRecommandLayout(
        pageId: String,
        thumbnailList: List<ImageThumbnail>,
    ): Boolean {
        return aiRecommandLayoutCache.isExistLayout(
            pageId = pageId,
            thumbnailList = thumbnailList
        )
    }

    fun setAiRecommandLayouts(
        pageId: String,
        layoutRecommendPageList: List<LayoutRecommendPage>
    ) {
        aiRecommandLayoutCache.setTemplateScene(
            pageId = pageId,
            layoutRecommendPageList = layoutRecommendPageList
        )
    }

    fun getAiRecommandLayouts(
        pageId: String,
        thumbnailList: List<ImageThumbnail>,
        layoutCode: String
    ): LayoutRecommendPage? {
        return aiRecommandLayoutCache.getTemplateScene(
            pageId = pageId,
            thumbnailList = thumbnailList,
            layoutCode = layoutCode
        )
    }

    fun createThumbnail(imageUri: String): ThumbImageInfo {
        return thumbImageMaker.create(imageUri = imageUri).let { (bitmap, exif) ->
            thumbImageStorage.save(
                bitmap = bitmap,
                fileNamePrefix = "thumb_image",
                name = imageUri
            )?.let { file ->
                ThumbImageInfo(
                    exifInfo = exif,
                    widthWithoutOt = bitmap.width,
                    heightWithoutOt = bitmap.height,
                    file = file
                )
            } ?: throw IOException("making thumbnail file failed")
        }
    }

    fun createCartThumbnailFile(bitmap: Bitmap): File {
        return thumbImageStorage.save(
            bitmap = bitmap,
            fileNamePrefix = "thumbnailFile",
            name = "thumbnailFile"
        ) ?: throw IOException("write file error [thumbnailFile]")
    }

    fun createMiddleThumbnailFile(bitmap: Bitmap): File {
        return thumbImageStorage.save(
            bitmap = bitmap,
            fileNamePrefix = "middleThumbnailFile",
            name = "middleThumbnailFile"
        ) ?: throw IOException("write file error [middleThumbnailFile]")
    }

    fun createSaveFile(save: Save): File {
        return File(context.externalCacheDir, "save.json").apply {
            if (isFile) delete()
            createNewFile()
            val saveJsonText = gson.toJson(sceneMapper.mapToJson(save))
            writeText(saveJsonText)
        }
    }

    fun getFilteredImage(
        uriText: String,
        filter: Filter
    ): String? {
        return imageFilterProcessor.getFilteredImage(
            uriText = uriText,
            filter = filter
        ) ?: ""
    }

    fun getPreviewFilteredImages(
        uriText: String,
        orientationAngle: Int,
        size: Int
    ): Single<Map<Filter, String?>> {
        return imageFilterProcessor.rxGetPreviewFilteredImages(
            uriText = uriText,
            orientationAngle = orientationAngle,
            size = size
        )
    }

    fun cleanUpStorage() {
        thumbImageStorage.cleanUp()
        imageFilterProcessor.cleanUp()
    }
}