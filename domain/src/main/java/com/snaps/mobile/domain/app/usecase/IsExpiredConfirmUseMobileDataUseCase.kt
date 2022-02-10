package com.snaps.mobile.domain.app.usecase

import com.snaps.mobile.domain.app.AppPreferenceRepository
import com.snaps.mobile.domain.getStringDate
import com.snaps.mobile.domain.usecase.SingleUseCase
import io.reactivex.rxjava3.core.Single
import java.util.*
import javax.inject.Inject

class IsExpiredConfirmUseMobileDataUseCase @Inject constructor(
    private val appPreferenceRepository: AppPreferenceRepository
) : SingleUseCase<Boolean, Unit> {

    override fun invoke(params: Unit): Single<Boolean> {
        return appPreferenceRepository.getLastConfirmDateUseMobileData()
            .map { lastDate ->
                val today = Date(System.currentTimeMillis()).getStringDate()
                today != lastDate
            }
    }

}