package com.snaps.mobile.data.product

import com.snaps.mobile.data.project.NetworkErrorLog
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class RemoteProductDataSource @Inject constructor(
    private val productApi: ProductApi,
    private val networkErrorLog: NetworkErrorLog,
) {

    fun getProductInfos(productCode: String, templateCode: String): Single<ResponseGetProductInfo> {
        return productApi
            .getMultiInfos(productCode, templateCode)
            .doOnError {
                networkErrorLog.write(
                    "getMultiInfos", it,
                    hashMapOf("productCode" to productCode, "templateCode" to templateCode)
                )
            }
    }
}