package com.snaps.mobile.domain.save

import java.text.SimpleDateFormat
import java.util.*

data class SaveInfo(
    var locationSearch: String = "",
    var userAgent: String = "",
    var regDate: String = "",
    var saveCount: String = ""
) {
    companion object {
        const val OS = "ANDROID"
    }

    fun increasingNumberOfEdits() {
        saveCount = "${saveCount}1"
    }

    fun updateSaveDate() {
        regDate = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.KOREA).format(Date())
    }

    fun isAndroid(): Boolean {
        if (userAgent.isNullOrBlank()) return false
        return userAgent.startsWith(OS)
    }

    fun getAppVersion(): String {
        if (userAgent.isNullOrBlank()) return ""
        return userAgent.substringAfterLast("/", "")
    }
}