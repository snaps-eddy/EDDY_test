package com.snaps.mobile.domain.save.usecase

import com.snaps.mobile.domain.error.Reason
import com.snaps.mobile.domain.error.SnapsThrowable
import com.snaps.mobile.domain.project.ProjectRepository
import com.snaps.mobile.domain.usecase.SingleUseCase
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class DeletePageUseCase @Inject constructor(
    private val projectRepository: ProjectRepository
) : SingleUseCase<Boolean, DeletePageUseCase.Params> {

    override fun invoke(params: Params): Single<Boolean> {
        return projectRepository.getProject(params.projectCode)
            .map { project ->
                if (project.isMinScene()) throw SnapsThrowable(Reason.UnderMinCount(21, project.getPageSceneCount()))
                project.deleteScenes(params.rightSceneDrawIndex)
            }
    }

    data class Params(
        val projectCode: String,
        val rightSceneDrawIndex: String
    )

}