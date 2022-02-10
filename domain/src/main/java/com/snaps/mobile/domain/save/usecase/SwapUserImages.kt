package com.snaps.mobile.domain.save.usecase

import com.snaps.mobile.domain.project.ProjectRepository
import com.snaps.mobile.domain.save.Save
import com.snaps.mobile.domain.usecase.SingleUseCase
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class SwapUserImages @Inject constructor(
    private val projectRepository: ProjectRepository,
) : SingleUseCase<Save, SwapUserImages.Params> {

    override fun invoke(params: Params): Single<Save> {
        return projectRepository.getProject(params.projectCode)
            .map { project ->
                project.swapImage(
                    params.targetSceneIndex,
                    params.targetSceneObjectIndex,
                    params.destinationSceneIndex,
                    params.destinationSceneObjectIndex
                )
            }
    }

    data class Params(
        val projectCode: String,
        val targetSceneIndex: String,
        val targetSceneObjectIndex: String,
        val destinationSceneIndex: String,
        val destinationSceneObjectIndex: String,
    )

}