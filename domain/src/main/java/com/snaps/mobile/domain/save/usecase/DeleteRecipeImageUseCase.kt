package com.snaps.mobile.domain.save.usecase

import com.snaps.mobile.domain.project.ProjectRepository
import com.snaps.mobile.domain.usecase.SingleUseCase
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class DeleteRecipeImageUseCase @Inject constructor(
    private val projectRepository: ProjectRepository
) : SingleUseCase<Boolean, DeleteRecipeImageUseCase.Params> {

    override fun invoke(params: Params): Single<Boolean> {
        return projectRepository.getProject(params.projectCode)
            .map { it.deleteRecipeImage(params.targetImgSeq) }
    }

    data class Params(
        val projectCode: String,
        val targetImgSeq: String
    )

}