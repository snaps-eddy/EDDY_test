package com.snaps.mobile.domain.save.usecase

import com.snaps.mobile.domain.project.ProjectRepository
import com.snaps.mobile.domain.usecase.SingleUseCase
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class SwapUserImageForRecipeImageUseCase @Inject constructor(
    private val projectRepository: ProjectRepository
) : SingleUseCase<Boolean, SwapUserImageForRecipeImageUseCase.Params> {

    override fun invoke(params: Params): Single<Boolean> {
        return projectRepository.getProject(params.projectCode)
            .map {
                it.replaceUserImage(
                    targetImgSeq = params.targetImgSeq,
                    dstSceneDrawIndex = params.dstSceneDrawIndex,
                    dstSceneObjectDrawIndex = params.dstSceneObjectDrawIndex,
                )
                true
            }
    }

    data class Params(
        val projectCode: String,
        val targetImgSeq: String,
        val dstSceneDrawIndex: String,
        val dstSceneObjectDrawIndex: String
    )

}