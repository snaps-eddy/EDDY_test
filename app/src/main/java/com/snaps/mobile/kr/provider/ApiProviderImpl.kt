package com.snaps.mobile.kr.provider

import android.content.SharedPreferences
import com.snaps.common.android_utils.ApiProvider
import com.snaps.common.utils.constant.Const_VALUE
import com.snaps.common.utils.ui.StringUtil
import com.snaps.mobile.kr.BuildConfig
import java.util.*
import javax.inject.Inject

class ApiProviderImpl @Inject constructor(
    sharedPreference: SharedPreferences
) : ApiProvider {

    private val languageCodeForUrl = sharedPreference.getString(Const_VALUE.KEY_APPLIED_LANGUAGE, Locale.getDefault().language)
        ?.run {
            when (this) {
                Locale.KOREAN.language, Locale.JAPANESE.language, Locale.ENGLISH.language -> this
                else -> Locale.ENGLISH.language
            }
        }
        ?.run { StringUtil.converLanguageCodeToCountryCode(this) }
        ?: ""

    override val newApiBaseUrl: String = "https://".plus(String.format(BuildConfig.BACK_END_NEW_BASE_URL, languageCodeForUrl))


}