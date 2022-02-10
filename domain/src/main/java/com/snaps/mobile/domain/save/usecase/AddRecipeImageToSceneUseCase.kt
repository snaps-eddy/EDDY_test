package com.snaps.mobile.domain.save.usecase

import com.snaps.mobile.domain.error.Reason
import com.snaps.mobile.domain.error.SnapsThrowable
import com.snaps.mobile.domain.project.ProjectRepository
import com.snaps.mobile.domain.usecase.SingleUseCase
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class AddRecipeImageToSceneUseCase @Inject constructor(
    private val projectRepository: ProjectRepository
) : SingleUseCase<Boolean, AddRecipeImageToSceneUseCase.Params> {

    override fun invoke(params: Params): Single<Boolean> {
        return projectRepository.getProject(params.projectCode)
            .flatMap { project ->
                val existUserImages = project.getUserImagesIn(params.dstSceneDrawIndex).toMutableList()
                if (existUserImages.size > 4) {
                    //@Rule Scene에 추가할 수 있는 이미지 갯수는 최대 5장이다.
                    throw SnapsThrowable(Reason.OverMaxCount(5, existUserImages.size))
                }
                val willAddUserImage = project.findUserImage(params.targetImgSeq)
                    ?: throw SnapsThrowable("Not found Target Image. Check image sequence or not uploaded.")

                existUserImages.add(willAddUserImage)

                projectRepository.getAiRecommendLayout(
                    deviceId = params.deviceId,
                    userNo = params.userNo,
                    language = params.language,
                    projectCode = params.projectCode,
                    productInfo = project.productInfo,
                    recommendCount = 5,
                    layoutCode = project.getSceneLayoutCode(params.dstSceneDrawIndex),
                    thumbnailList = existUserImages,
                ).map {
                    project.applyAiRecommendLayout(params.dstSceneDrawIndex, it.scene, it.imageKeyList)
                    true
                }
            }
    }

    data class Params(
        val projectCode: String,
        val targetImgSeq: String,
        val dstSceneDrawIndex: String,
        val userNo: String,
        val deviceId: String,
        val language: String,
    )

}