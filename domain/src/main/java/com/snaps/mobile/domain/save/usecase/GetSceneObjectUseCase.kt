package com.snaps.mobile.domain.save.usecase

import com.snaps.mobile.domain.project.ProjectRepository
import com.snaps.mobile.domain.save.SceneObject
import com.snaps.mobile.domain.usecase.SingleUseCase
import io.reactivex.rxjava3.core.Single
import java.util.*
import javax.inject.Inject

class GetSceneObjectUseCase @Inject constructor(
    private val projectRepository: ProjectRepository
) : SingleUseCase<SceneObject.Image, GetSceneObjectUseCase.Params> {

    override fun invoke(params: Params): Single<SceneObject.Image> {
        return projectRepository.getProject(projectCode = params.projectCode)
            .map {
                it.findSceneObject(params.sceneDrawIndex, params.sceneObjectDrawIndex)
                    ?: throw NoSuchElementException("Not Found Image object")
            }
    }

    data class Params(
        val projectCode: String,
        val sceneDrawIndex: String,
        val sceneObjectDrawIndex: String,
        val imgSeq: String
    )

}