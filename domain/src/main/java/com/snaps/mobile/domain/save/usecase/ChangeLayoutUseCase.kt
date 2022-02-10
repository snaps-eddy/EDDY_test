package com.snaps.mobile.domain.save.usecase

import com.snaps.mobile.domain.project.ProjectRepository
import com.snaps.mobile.domain.template.TemplateScene
import com.snaps.mobile.domain.usecase.SingleUseCase
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class ChangeLayoutUseCase @Inject constructor(
    private val projectRepository: ProjectRepository
) : SingleUseCase<Boolean, ChangeLayoutUseCase.Params> {

    override fun invoke(params: Params): Single<Boolean> {
        return projectRepository.getProject(params.projectCode)
            .map { project ->
                project.changeSceneLayout(params.dstSceneDrawIndex, params.templateScene)
                true
            }
    }

    data class Params(
        val projectCode: String,
        val dstSceneDrawIndex: String,
        val templateScene: TemplateScene,
    )

}