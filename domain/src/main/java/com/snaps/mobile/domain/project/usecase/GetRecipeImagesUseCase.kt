package com.snaps.mobile.domain.project.usecase

import com.snaps.mobile.domain.asset.RecipeImage
import com.snaps.mobile.domain.project.ProjectRepository
import com.snaps.mobile.domain.usecase.SingleUseCase
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class GetRecipeImagesUseCase @Inject constructor(
    private val projectRepository: ProjectRepository
) : SingleUseCase<List<RecipeImage>, String> {

    override fun invoke(params: String): Single<List<RecipeImage>> {
        return projectRepository.getProject(projectCode = params)
            .map {
                it.getRecipeImage().distinct()
            }
    }

}