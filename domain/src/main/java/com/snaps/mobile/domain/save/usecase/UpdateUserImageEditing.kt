package com.snaps.mobile.domain.save.usecase

import com.snaps.mobile.domain.project.ProjectRepository
import com.snaps.mobile.domain.save.Filter
import com.snaps.mobile.domain.save.InnerImage
import com.snaps.mobile.domain.save.SceneObject
import com.snaps.mobile.domain.usecase.SingleUseCase
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class UpdateUserImageEditing @Inject constructor(
    private val projectRepository: ProjectRepository
) : SingleUseCase<Boolean, UpdateUserImageEditing.Params> {

    override fun invoke(params: Params): Single<Boolean> {
        return projectRepository.getProject(projectCode = params.projectCode)
            .map { project ->
                val sceneObject: SceneObject? = project.findSceneObject(params.sceneObjectDrawIndex)
                if (sceneObject != null && sceneObject is SceneObject.Image) {
                    sceneObject.innerImage = params.innerImage
                    sceneObject.filter = params.filter
                }
                true
            }
    }

    data class Params(
        val projectCode: String,
        val sceneObjectDrawIndex: String,
        val innerImage: InnerImage,
        val filter: Filter
    )
}