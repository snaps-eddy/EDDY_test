package com.snaps.mobile.domain.app.usecase

import com.snaps.mobile.domain.app.AppPreferenceRepository
import com.snaps.mobile.domain.usecase.SingleUseCase
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class PermitMobileDataUseCase @Inject constructor(
    private val appPreferenceRepository: AppPreferenceRepository
) : SingleUseCase<Boolean, PermitMobileDataUseCase.Params> {

    override fun invoke(params: Params): Single<Boolean> {
        return appPreferenceRepository.permitMobileData(params.permission)
    }

    data class Params(
        val permission: Boolean
    )

}