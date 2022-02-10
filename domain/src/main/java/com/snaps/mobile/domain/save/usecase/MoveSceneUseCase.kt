package com.snaps.mobile.domain.save.usecase

import com.snaps.mobile.domain.project.ProjectRepository
import com.snaps.mobile.domain.save.Save
import com.snaps.mobile.domain.usecase.SingleUseCase
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class MoveSceneUseCase @Inject constructor(
    private val projectRepository: ProjectRepository
) : SingleUseCase<Save, MoveSceneUseCase.Params> {

    override fun invoke(params: Params): Single<Save> {
        return projectRepository.getProject(projectCode = params.projectCode)
            .map {
                it.moveScene(params.fromDrawIndex, params.toDrawIndex, params.after)
            }
    }

    data class Params(
        val projectCode: String,
        val fromDrawIndex: String,
        val toDrawIndex: String,
        val after: Boolean
    )

}