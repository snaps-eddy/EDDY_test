package com.snaps.mobile.domain.project.usecase

import com.snaps.mobile.domain.project.ProjectRepository
import com.snaps.mobile.domain.usecase.SingleUseCase
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class GetProjectNameUseCase @Inject constructor(
    private val projectRepository: ProjectRepository
) : SingleUseCase<String, String> {

    override fun invoke(params: String): Single<String> {
        return projectRepository.getProject(params)
            .map {
                it.getProjectName()
            }
    }
}