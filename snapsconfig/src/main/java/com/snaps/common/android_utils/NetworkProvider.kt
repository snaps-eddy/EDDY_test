package com.snaps.common.android_utils

interface NetworkProvider {

    fun isConnectedWifi(): Boolean

    fun isConnectedMobile(): Boolean

    fun hasAnyConnection(): Boolean

}