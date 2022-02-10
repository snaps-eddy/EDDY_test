package com.snaps.mobile.domain.save.usecase

import com.snaps.mobile.domain.project.ProjectRepository
import com.snaps.mobile.domain.save.Filter
import com.snaps.mobile.domain.usecase.SingleUseCase
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class GetFilteredImageUriUseCase @Inject constructor(
    private val projectRepository: ProjectRepository
) : SingleUseCase<String, GetFilteredImageUriUseCase.Params> {

    override fun invoke(params: Params): Single<String> {
        return projectRepository.getFilteredImage(
            uriText = params.imageUri,
            filter = params.filter
        )
    }

    data class Params(
        val imageUri: String,
        val filter: Filter,
    )
}