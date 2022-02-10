package com.snaps.mobile.kr

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.snaps.common.android_utils.NetworkChangeMonitor
import com.snaps.common.android_utils.NetworkProvider
import com.snaps.common.utils.log.Dlog
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject
import kotlin.concurrent.timer

// https://stackoverflow.com/questions/36421930/connectivitymanager-connectivity-action-deprecated
// https://developer.android.com/training/basics/network-ops/reading-network-state?hl=ko
class NetworkChangeMonitorImpl @Inject constructor(
    private val connManager: ConnectivityManager,
    private val networkProvider: NetworkProvider,
) : NetworkChangeMonitor {

    private val tag = NetworkChangeMonitorImpl::class.java.simpleName

    private val subscriberCount = AtomicInteger(0)

    @Volatile
    private lateinit var preNetType: NetworkChangeMonitor.NetType

    @Volatile
    private var timer: Timer? = null

    private val subject = PublishSubject.create<Pair<NetworkChangeMonitor.NetType, NetworkChangeMonitor.NetType>>()
    private val listener = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network?) {
            timer?.cancel()
            timer = null
            emit(getNetType())
        }

        override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
            timer?.cancel()
            timer = null
            emit(getNetType())
        }

        override fun onLost(network: Network?) {
            //모바일 네트워크, Wifi 둘다 on일대 wifi를 off하면 연결이 끊어졌다가 모바일 네크워크에 연결된다. 이를 방지하기 위해 타이머 사용
            timer = timer(period = Long.MAX_VALUE, initialDelay = 500) {
                emit(getNetType())
            }
        }

        private fun emit(netType: NetworkChangeMonitor.NetType) {
            if (preNetType != netType) subject.onNext(Pair(preNetType, netType))
            preNetType = netType
        }
    }

    private fun getNetType(): NetworkChangeMonitor.NetType {
        return when {
            networkProvider.isConnectedMobile() -> NetworkChangeMonitor.NetType.CELLUAR
            networkProvider.isConnectedWifi() -> NetworkChangeMonitor.NetType.WIFI
            else -> NetworkChangeMonitor.NetType.NONE
        }
    }

    override fun getObservable(): Observable<Pair<NetworkChangeMonitor.NetType, NetworkChangeMonitor.NetType>> {
        return subject
            .doOnSubscribe {
                if (subscriberCount.get() == 0) {
                    preNetType = getNetType()
                    enable()
                }
                subscriberCount.incrementAndGet()
            }
            .doOnDispose {
                if (subscriberCount.decrementAndGet() == 0) {
                    disable()
                }
            }
            .doOnNext {
                Dlog.d(tag, it.first.toString() + " -> " + it.second)
            }
    }

    private fun enable() {
        val networkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()
        connManager.registerNetworkCallback(networkRequest, listener)
    }

    private fun disable() {
        connManager.unregisterNetworkCallback(listener)
    }
}