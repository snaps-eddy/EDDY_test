package com.snaps.mobile.domain.project.usecase

import com.snaps.common.android_utils.SchedulerProvider
import com.snaps.mobile.domain.project.ProjectRepository
import com.snaps.mobile.domain.usecase.FlowableUseCase
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.kotlin.toFlowable
import javax.inject.Inject

class UploadLeftImagesUseCase @Inject constructor(
    private val projectRepository: ProjectRepository,
    private val schedulerProvider: SchedulerProvider
) : FlowableUseCase<Int, UploadLeftImagesUseCase.Params> {

    override fun invoke(params: Params): Flowable<Int> {
        return projectRepository.getProject(projectCode = params.projectCode)
            .flatMapPublisher { project ->
                val willUploadImages = project.getWillUploadImageList()
                val willUploadImageCount = willUploadImages.size

                willUploadImages.toFlowable()
                    .parallel(3)
                    .runOn(schedulerProvider.upload)
                    .flatMap { target ->
                        projectRepository.uploadOriginalImage(
                            projectCode = params.projectCode,
                            deviceId = params.deviceId,
                            userNo = params.userNo,
                            imageThumbnail = target
                        ).toFlowable()
                    }
                    .sequential()
                    .observeOn(schedulerProvider.io)
                    .map {
                        project.updateUploadedImage(it)
                        willUploadImageCount
                    }
            }
    }

    data class Params(
        var deviceId: String,
        var userNo: String,
        val projectCode: String,
    )
}