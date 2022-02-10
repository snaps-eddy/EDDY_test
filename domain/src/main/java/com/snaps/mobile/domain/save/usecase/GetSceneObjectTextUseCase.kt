package com.snaps.mobile.domain.save.usecase

import com.snaps.mobile.domain.project.ProjectRepository
import com.snaps.mobile.domain.save.SceneObject
import com.snaps.mobile.domain.usecase.SingleUseCase
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class GetSceneObjectTextUseCase @Inject constructor(
    private val projectRepository: ProjectRepository
): SingleUseCase<SceneObject.Text, GetSceneObjectTextUseCase.Params> {


    override fun invoke(params: Params): Single<SceneObject.Text> {
        return projectRepository.getProject(params.projectCode)
            .map {
                it.findSceneObject<SceneObject.Text>(params.sceneDrawIndex)
            }
    }

    data class Params(
        val projectCode : String,
        val sceneDrawIndex : String,
    )

}