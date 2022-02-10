package com.snaps.mobile.domain.asset.usecase

import com.snaps.mobile.domain.asset.RecipeImage
import com.snaps.mobile.domain.project.ProjectRepository
import com.snaps.mobile.domain.usecase.SingleUseCase
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class AddRecipeImagesUseCase @Inject constructor(
    private val projectRepository: ProjectRepository,
) : SingleUseCase<Int, AddRecipeImagesUseCase.Params> {

    override fun invoke(params: Params): Single<Int> {
        return projectRepository.getProject(projectCode = params.projectCode)
            .map {
                it.putRecipeImage(params.images)
            }
    }

    data class Params(
        val projectCode: String,
        val images: List<RecipeImage>
    )

}