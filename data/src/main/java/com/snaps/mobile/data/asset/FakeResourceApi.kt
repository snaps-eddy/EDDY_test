package com.snaps.mobile.data.asset

import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET

interface FakeResourceApi {

    //    https://picsum.photos/v2/list
    @GET("/v2/list")
    fun getBackgroundImages(): Single<List<AssetBackgroundImageDto>>

}