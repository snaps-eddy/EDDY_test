package com.snaps.mobile.domain.save.usecase

import com.snaps.mobile.domain.error.Reason
import com.snaps.mobile.domain.error.SnapsThrowable
import com.snaps.mobile.domain.project.ProjectRepository
import com.snaps.mobile.domain.usecase.SingleUseCase
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class AddPageByRecipeImageUseCase @Inject constructor(
    private val projectRepository: ProjectRepository
) : SingleUseCase<Boolean, AddPageByRecipeImageUseCase.Params> {

    override fun invoke(params: Params): Single<Boolean> {
        return projectRepository.getProject(params.projectCode)
            .doOnSuccess { if (it.isMaxScene()) throw SnapsThrowable(Reason.OverMaxCount(151, it.getPageSceneCount())) }
            .flatMap { project ->
                project.findThumbnail(params.targetImgSeq)?.run {
                    projectRepository.getAiRecommendLayout(
                        deviceId = params.deviceId,
                        userNo = params.userNo,
                        language = params.language,
                        projectCode = project.code,
                        productInfo = project.productInfo,
                        recommendCount = 5,
                        thumbnailList = listOf(this),
                    ).map {
                        project.addPairScene(it.scene, it.imageKeyList, params.prevSceneDrawIndex)
                        true
                    }
                } ?: throw IllegalArgumentException("Image Thumbnail not found")
            }
    }

    data class Params(
        val projectCode: String,
        val deviceId: String,
        val userNo: String,
        val prevSceneDrawIndex: String,
        val targetImgSeq: String,
        val language: String
    )
}