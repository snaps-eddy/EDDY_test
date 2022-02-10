package com.snaps.mobile.domain.save.usecase

import com.snaps.mobile.domain.project.ProjectRepository
import com.snaps.mobile.domain.template.TemplateScene
import com.snaps.mobile.domain.usecase.SingleUseCase
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class ChangeCoverUseCase @Inject constructor(
    private val projectRepository: ProjectRepository
) : SingleUseCase<Boolean, ChangeCoverUseCase.Params> {

    override fun invoke(params: Params): Single<Boolean> {
        return projectRepository.getProject(params.projectCode)
            .map { project ->
                // 변경 할 TemplateScene 에는 TemplateCode 값이 비어있다.
                val templateScene = params.templateScene.copy(templateCode = params.sceneTemplateCode)
                project.changeCoverSceneTemplate(templateScene)
                true
            }
    }

    data class Params(
        val projectCode: String,
        val sceneTemplateCode: String,
        val templateScene: TemplateScene,
    )

}