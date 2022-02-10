package com.snaps.mobile.kr.provider

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.snaps.common.android_utils.NetworkProvider
import javax.inject.Inject

class NetworkProviderImpl @Inject constructor(
    private val connManager: ConnectivityManager
) : NetworkProvider {

    override fun isConnectedWifi(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connManager.activeNetwork
            connManager.getNetworkCapabilities(activeNetwork)?.run {
                this.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
            } ?: false

        } else {
            connManager.allNetworks.mapNotNull { connManager.getNetworkInfo(it) }
                .filter { it.isConnected && it.type == ConnectivityManager.TYPE_WIFI }
                .count() > 0
        }
    }

    override fun isConnectedMobile(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connManager.activeNetwork
            connManager.getNetworkCapabilities(activeNetwork)?.run {
                this.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
            } ?: false

        } else {
            connManager.allNetworks.mapNotNull { connManager.getNetworkInfo(it) }
                .filter { it.isConnected && it.type == ConnectivityManager.TYPE_MOBILE }
                .count() > 0 && !isConnectedWifi()
        }
    }

    override fun hasAnyConnection(): Boolean {
        return isConnectedWifi() || isConnectedMobile()
    }
}