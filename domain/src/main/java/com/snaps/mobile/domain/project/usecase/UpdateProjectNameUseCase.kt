package com.snaps.mobile.domain.project.usecase

import com.snaps.mobile.domain.project.ProjectRepository
import com.snaps.mobile.domain.usecase.SingleUseCase
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class UpdateProjectNameUseCase @Inject constructor(
    private val projectRepository: ProjectRepository
) : SingleUseCase<Boolean, UpdateProjectNameUseCase.Params> {

    override fun invoke(params: Params): Single<Boolean> {
        return projectRepository.getProject(params.projectCode)
            .map {
                it.setProjectName(params.willTitle)
            }
    }

    data class Params(
        val projectCode: String,
        val willTitle: String
    )

}