package com.snaps.mobile.domain.app

import io.reactivex.rxjava3.core.Single

/**
 * 앱에 필요한 데이터 (lte 사용 여부, 푸시 허용 여부 등 앱 사용에 필요한 데이터)
 */
interface AppPreferenceRepository {

    fun permitMobileData(permission: Boolean): Single<Boolean>
    
    fun getLastConfirmDateUseMobileData(): Single<String>

}