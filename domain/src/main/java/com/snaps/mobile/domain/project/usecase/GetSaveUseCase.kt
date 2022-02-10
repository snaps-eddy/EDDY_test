package com.snaps.mobile.domain.project.usecase

import com.snaps.mobile.domain.project.ProjectRepository
import com.snaps.mobile.domain.save.Save
import com.snaps.mobile.domain.usecase.SingleUseCase
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class GetSaveUseCase @Inject constructor(
    private val projectRepository: ProjectRepository,
) : SingleUseCase<Save, String> {

    override fun invoke(params: String): Single<Save> {
        return projectRepository.getProject(projectCode = params)
            .map { it.setPhotoBookCoverType() }
            .map { it.adjustPhotoBookHardCoverSpineThickness() }
            .map { it.checkWarningResolutions().save }
    }
}