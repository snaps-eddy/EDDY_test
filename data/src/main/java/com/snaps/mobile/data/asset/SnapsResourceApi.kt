package com.snaps.mobile.data.asset

import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface SnapsResourceApi {

    @Headers(
        "X-SNAPS-CHANNEL: ANDROID",
        "X-SNAPS-VERSION: 1",
        "X-SNAPS-OS-VERSION: 1",
        "X-SNAPS-DEVICE: 1",
        "X-SNAPS-DEVICE-TOKEN: 1",
        "X-SNAPS-DEVICE-UUID: 1"
    )
    @GET("/v1/resource/{resourceType}")
    fun getResources(
        @Path("resourceType") resourceType: String,
        @Query("productCode") productCode: String,
        @Query("pageType") pageType: String? = null,
        @Query("itemType") itemType: String? = null,
        @Query("subType") subType: String? = null,
        @Query("directionType") directionType: String? = null,
    ): Single<GetSnapsResourceResponse>
}