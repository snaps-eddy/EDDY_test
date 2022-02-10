package com.snaps.common.android_utils

import io.reactivex.rxjava3.core.Observable

interface NetworkChangeMonitor {
    enum class NetType {
        NONE,
        WIFI,
        CELLUAR
    }

    fun getObservable(): Observable<Pair<NetType, NetType>>
}