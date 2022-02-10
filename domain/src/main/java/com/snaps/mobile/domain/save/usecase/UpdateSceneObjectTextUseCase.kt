package com.snaps.mobile.domain.save.usecase

import com.snaps.mobile.domain.project.ProjectRepository
import com.snaps.mobile.domain.save.TextAlign
import com.snaps.mobile.domain.usecase.SingleUseCase
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class UpdateSceneObjectTextUseCase @Inject constructor(
    private val projectRepository: ProjectRepository
) : SingleUseCase<Unit, UpdateSceneObjectTextUseCase.Params> {

    override fun invoke(params: Params): Single<Unit> {
        return projectRepository.getProject(params.projectCode)
            .map { project ->
                project.updateSceneObjectText(
                    sceneObjectDrawIndex = params.sceneObjectDrawIndex,
                    text = params.text,
                    textAlign = params.textAlign,
                    hexColor = params.hexColor
                )
            }
    }

    data class Params(
        val projectCode: String,
        val sceneObjectDrawIndex: String,
        val text: String,
        val textAlign: TextAlign,
        val hexColor: String
    )

}