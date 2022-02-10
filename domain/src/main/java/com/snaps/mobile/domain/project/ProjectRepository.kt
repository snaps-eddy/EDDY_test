package com.snaps.mobile.domain.project

import android.graphics.Bitmap
import com.snaps.mobile.domain.asset.RecipeImage
import com.snaps.mobile.domain.product.ProductInfo
import com.snaps.mobile.domain.save.Filter
import com.snaps.mobile.domain.save.Save
import com.snaps.mobile.domain.template.ai.LayoutRecommendPage
import com.snaps.mobile.domain.template.ai.LayoutRecommendTemplate
import io.reactivex.rxjava3.core.Single

interface ProjectRepository {

    fun getNewProjectOption(
        deviceId: String,
        userNo: String,
    ): Single<ProjectOption>

    fun getProjectOption(
        projectCode: String,
        deviceId: String,
        userNo: String
    ): Single<ProjectOption>

    fun getSave(
        deviceId: String,
        userNo: String,
        projectCode: String
    ): Single<Save>

    fun getProject(
        projectCode: String
    ): Single<Project>

    fun getAiTemplate(
        deviceId: String,
        userNo: String,
        projectCode: String,
        language: String,
        productInfo: ProductInfo,
        thumbnailList: List<ImageThumbnail>
    ): Single<LayoutRecommendTemplate>

    fun getAiRecommendLayout(
        deviceId: String,
        userNo: String,
        language: String,
        projectCode: String,
        productInfo: ProductInfo,
        layoutCode: String = "",
        recommendCount: Int,
        pageId: String = "",
        thumbnailList: List<ImageThumbnail>
    ): Single<LayoutRecommendPage>

    fun cleanUpStorage()

    fun uploadThumbnail(
        deviceId: String,
        userNo: String,
        projectCode: String,
        recipeImage: RecipeImage,
        enableFaceFinder: Boolean
    ): Single<ImageLoadReceipt>

    fun uploadOriginalImage(
        deviceId: String,
        userNo: String,
        projectCode: String,
        imageThumbnail: ImageThumbnail
    ): Single<ImageThumbnail>

    fun uploadRemainingImages(
        deviceId: String,
        userNo: String,
        project: Project
    ): Single<Project>

    fun isAfterOrderEdit(
        projectCode: String,
        deviceId: String,
        userNo: String,
    ): Single<Boolean>

    fun uploadSave(
        projectCode: String,
        save: Save,
        thumbnailList: List<ImageThumbnail>,
        projectOption: ProjectOption,
        deviceId: String,
        userNo: String,
        cartThumbnail: Bitmap,
    ): Single<Boolean>

    fun update(project: Project): Single<Project>

    fun isExistCachedLayout(
        pageId: String = "",
        thumbnails: List<ImageThumbnail>
    ): Single<Boolean>

    fun getFilteredImage(
        uriText: String,
        filter: Filter
    ): Single<String>

    fun getPreviewFilteredImages(
        uriText: String,
        orientationAngle: Int,
        size: Int
    ): Single<Map<Filter, String?>>
}