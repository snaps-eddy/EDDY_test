package com.snaps.mobile.data.product

import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface ProductApi {

    @Headers(
        "X-SNAPS-CHANNEL: ANDROID",
        "X-SNAPS-VERSION: 1",
        "X-SNAPS-OS-VERSION: 1",
        "X-SNAPS-DEVICE: 1",
        "X-SNAPS-DEVICE-TOKEN: 1",
        "X-SNAPS-DEVICE-UUID: 1"
    )
    @GET("v1/product/{productCode}/productInfoPrice/{templateCode}")
    fun getMultiInfos(@Path("productCode") productCode: String, @Path("templateCode") templateCode: String): Single<ResponseGetProductInfo>

}