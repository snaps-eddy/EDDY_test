package com.snaps.mobile.domain.save.usecase

import com.snaps.mobile.domain.project.ProjectRepository
import com.snaps.mobile.domain.usecase.SingleUseCase
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class ExtractUserImageUseCase @Inject constructor(
    private val projectRepository: ProjectRepository
) : SingleUseCase<Boolean, ExtractUserImageUseCase.Params> {

    override fun invoke(params: Params): Single<Boolean> {
        return projectRepository.getProject(params.projectCode)
            .flatMap { project ->
                // 중복코드 -> 이전에 사진을 지우고, 새로 레이아웃을 받는 코드. AddUserImageToSceneUseCase
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
        val targetImgSeq: String,
        val targetSceneDrawIndex: String,
        val deviceId: String,
        val userNo: String,
        val language: String
    )

}