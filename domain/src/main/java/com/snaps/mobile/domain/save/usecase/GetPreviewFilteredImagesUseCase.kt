package com.snaps.mobile.domain.save.usecase

import com.snaps.mobile.domain.project.ProjectRepository
import com.snaps.mobile.domain.save.Filter
import com.snaps.mobile.domain.usecase.SingleUseCase
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class GetPreviewFilteredImagesUseCase @Inject constructor(
    private val projectRepository: ProjectRepository
) : SingleUseCase<Map<Filter, String?>, GetPreviewFilteredImagesUseCase.Params> {

    override fun invoke(params: Params): Single<Map<Filter, String?>> {
        return projectRepository.getPreviewFilteredImages(
            uriText = params.imageUri,
            orientationAngle = params.orientationAngle,
            size = params.size
        )
    }

    data class Params(
        val imageUri: String,
        val orientationAngle: Int,
        val size: Int
    )
}