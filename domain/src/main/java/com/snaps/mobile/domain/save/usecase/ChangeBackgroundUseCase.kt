package com.snaps.mobile.domain.save.usecase

import com.snaps.mobile.domain.project.ProjectRepository
import com.snaps.mobile.domain.usecase.SingleUseCase
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class ChangeBackgroundUseCase @Inject constructor(
    private val projectRepository: ProjectRepository
) : SingleUseCase<Boolean, ChangeBackgroundUseCase.Params> {

    override fun invoke(params: Params): Single<Boolean> {
        return projectRepository.getProject(params.projectCode)
            .map { project ->
                project.changeSceneBackground(params.dstSceneDrawIndex, params.resourceId, params.resourceUri)
                true
            }
    }

    data class Params(
        val projectCode: String,
        val resourceId: String,
        val resourceUri: String,
        val dstSceneDrawIndex: String
    )

}