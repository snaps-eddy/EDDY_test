package com.snaps.mobile.domain.product

import io.reactivex.rxjava3.core.Single

interface ProductRepository {

    fun getInfos(productCode: String, templateCode: String): Single<MergeInfos>

    fun getProductPolicy(productCode: String, templateCode: String): Single<ProductPolicy>

    fun getSpineInfo(): Single<SpineInfo>
}