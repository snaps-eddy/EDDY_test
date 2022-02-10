package com.snaps.mobile.domain.save.usecase

import com.snaps.mobile.domain.error.Reason
import com.snaps.mobile.domain.error.SnapsThrowable
import com.snaps.mobile.domain.project.ProjectRepository
import com.snaps.mobile.domain.usecase.SingleUseCase
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class AddPageByUserImageUseCase @Inject constructor(
    private val projectRepository: ProjectRepository
) : SingleUseCase<Boolean, AddPageByUserImageUseCase.Params> {

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
                        project
                    }
                } ?: throw IllegalArgumentException("Image Thumbnail not found")
            }
            .flatMap { project ->
                // 중복코드 in AddUserImageToSceneUseCase
                val existUserImages = project.getUserImagesIn(params.targetSceneDrawIndex).toMutableList()
                val willExtractUserImage = project.findUserImage(params.targetImgSeq)
                    ?: throw IllegalStateException("Not found Target Image. Check image sequence or not uploaded.")

                existUserImages.remove(willExtractUserImage)

                if (existUserImages.isEmpty()) {
                    project.extractUserImage(params.targetSceneDrawIndex)
                    Single.just(false)

                } else {
                    projectRepository.getAiRecommendLayout(
                        deviceId = params.deviceId,
                        userNo = params.userNo,
                        language = params.language,
                        projectCode = params.projectCode,
                        productInfo = project.productInfo,
                        recommendCount = 5,
                        layoutCode = project.getSceneLayoutCode(params.targetSceneDrawIndex),
                        thumbnailList = existUserImages,
                    ).map {
                        project.applyAiRecommendLayout(params.targetSceneDrawIndex, it.scene, it.imageKeyList)
                        true

                    }
                }
            }
    }

    data class Params(
        val projectCode: String,
        val deviceId: String,
        val userNo: String,
        val language: String,
        val prevSceneDrawIndex: String,
        val targetImgSeq: String,
        val targetSceneDrawIndex: String,
        val targetSceneObjectDrawIndex: String
    )

}