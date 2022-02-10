package com.snaps.mobile.domain.project.usecase

import android.graphics.Bitmap
import com.snaps.mobile.domain.project.Project
import com.snaps.mobile.domain.project.ProjectRepository
import com.snaps.mobile.domain.usecase.SingleUseCase
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class UploadSaveUseCase @Inject constructor(
    private val projectRepository: ProjectRepository,
) : SingleUseCase<Project, UploadSaveUseCase.Params> {

    override fun invoke(params: Params): Single<Project> {
        return projectRepository.getProject(projectCode = params.projectCode)
            .map {
                it.measureSpineNo()
                it.measurePagesAddCount()
                it.updateSaveInfo()
                it.setAffxName(params.appVesion)
            }
            .flatMap { project ->
                projectRepository.uploadSave(
                    projectCode = project.code,
                    save = project.save,
                    thumbnailList = project.getImageThumbnail(),
                    projectOption = project.projectOption,
                    cartThumbnail = params.cartThumbnail,
                    deviceId = params.deviceId,
                    userNo = params.userNo,
                ).map {
                    project
                }
            }
    }

    data class Params(
        val deviceId: String,
        val userNo: String,
        val appVesion: String,
        val projectCode: String,
        val cartThumbnail: Bitmap
    )
}