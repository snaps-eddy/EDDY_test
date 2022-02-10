package com.snaps.mobile.domain.project.usecase

import com.snaps.common.android_utils.SchedulerProvider
import com.snaps.mobile.domain.asset.RecipeImage
import com.snaps.mobile.domain.project.ProjectRepository
import com.snaps.mobile.domain.usecase.FlowableUseCase
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.kotlin.toFlowable
import javax.inject.Inject

class UploadThumbnailsUseCase @Inject constructor(
    private val projectRepository: ProjectRepository,
    private val schedulerProvider: SchedulerProvider
) : FlowableUseCase<Int, UploadThumbnailsUseCase.Params> {

    override fun invoke(params: Params): Flowable<Int> {
        return projectRepository.getProject(projectCode = params.projectCode)
            .flatMapPublisher { project ->
                val recipeImageList = project.getRecipeImageNotUpload()
                val recipeImageCount = recipeImageList.size
                recipeImageList.toFlowable()
                    .parallel(3)
                    .runOn(schedulerProvider.upload)
                    .flatMap { recipeImage ->
                        projectRepository
                            .uploadThumbnail(
                                deviceId = params.deviceId,
                                userNo = params.userNo,
                                projectCode = params.projectCode,
                                recipeImage = recipeImage,
                                enableFaceFinder = params.enableFaceFinder
                            )
                            .toFlowable()
                    }
                    .sequential()
                    .observeOn(schedulerProvider.io)
                    .map { receipt ->
                        project.updateRecipeImage(receipt)
                        recipeImageCount
                    }
            }
    }

    data class Params(
        val projectCode: String,
        val userNo: String,
        val deviceId: String,
        val enableFaceFinder: Boolean
    )

}