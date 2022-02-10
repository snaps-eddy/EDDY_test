package com.snaps.mobile.domain.project.usecase

import com.snaps.mobile.domain.project.ProjectRepository
import com.snaps.mobile.domain.usecase.SingleUseCase
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class CheckEditAfterOrderUseCase @Inject constructor(
    private val projectRepository: ProjectRepository,
) : SingleUseCase<Boolean, CheckEditAfterOrderUseCase.Params> {

    override fun invoke(params: Params): Single<Boolean> {
        return projectRepository.getProject(projectCode = params.projectCode)
            .flatMap {
                projectRepository.isAfterOrderEdit(
                    deviceId = params.deviceId,
                    userNo = params.userNo,
                    projectCode = params.projectCode
                )
            }
    }

    data class Params(
        val deviceId: String,
        val userNo: String,
        val projectCode: String,
    )
}