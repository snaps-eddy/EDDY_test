package com.snaps.mobile.data.app

import android.content.SharedPreferences
import io.reactivex.rxjava3.core.Single
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class LocalAppPreferenceDataSource @Inject constructor(
    private val pref: SharedPreferences
) {

    fun permitMobileData(permission: Boolean): Single<Boolean> {
        return Single
            .fromCallable {
                pref.edit().run {
                    if (permission) {
                        val date = Date(System.currentTimeMillis())
                        val strCurYear = SimpleDateFormat("yyyy", Locale.getDefault()).format(date)
                        val strCurMonth = SimpleDateFormat("MM", Locale.getDefault()).format(date)
                        val strCurDay = SimpleDateFormat("dd", Locale.getDefault()).format(date)
                        putString(KEY_DEVICE_DATE_ACCEPT_USE_MOBILE_DATA, strCurYear + strCurMonth + strCurDay)
                    } else {
                        remove(KEY_DEVICE_DATE_ACCEPT_USE_MOBILE_DATA)
                    }
                    commit() // apply 는 비동기이기 때문에 메소드 콜백이 좀 더 빠르긴 한데..
                }
            }
            .onErrorReturn { false }
    }

    fun getLastConfirmDateUseMobileData(): Single<String> {
        return Single
            .fromCallable {
                pref.getString(KEY_DEVICE_DATE_ACCEPT_USE_MOBILE_DATA, null) ?: ""
            }
            .onErrorReturn { "" }
    }

    companion object {
        //        device.date.accept_use_mobile_data
        const val KEY_DEVICE_DATE_ACCEPT_USE_MOBILE_DATA = "setting_value_use_cellular_confirm_date"
    }

}