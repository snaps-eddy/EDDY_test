package com.snaps.mobile.data.asset

import javax.inject.Inject

class SnapsResourceLocalDataSource @Inject constructor(
) {

    private var layoutList: GetSnapsResourceResponse? = null

    fun getLayouts(): GetSnapsResourceResponse? {
        return layoutList
    }

    fun cacheLayouts(layoutList: GetSnapsResourceResponse) {
        this.layoutList = layoutList.clone() as GetSnapsResourceResponse
    }

}