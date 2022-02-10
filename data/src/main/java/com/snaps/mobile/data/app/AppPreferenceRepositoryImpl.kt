package com.snaps.mobile.data.app

import com.snaps.mobile.domain.app.AppPreferenceRepository
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class AppPreferenceRepositoryImpl @Inject constructor(
    private val localDataSource: LocalAppPreferenceDataSource
) : AppPreferenceRepository {

    override fun permitMobileData(permission: Boolean): Single<Boolean> {
        return localDataSource.permitMobileData(permission)
    }

    override fun getLastConfirmDateUseMobileData(): Single<String> {
        return localDataSource.getLastConfirmDateUseMobileData()
    }

}