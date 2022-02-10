package com.snaps.mobile.data.product

import android.content.res.Resources
import com.google.gson.Gson
import com.snaps.common.utils.log.Dlog
import com.snaps.mobile.data.R
import com.snaps.mobile.domain.product.ProductPolicy
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

/**
 * Gson 유니코드 파싱 문제 때문에 Gson을 주입 받아서 사용하도록.
 */
class LocalProductDataSource @Inject constructor(
    private val productPolicyFactory: ProductPolicyFactory,
    private val resources: Resources,
    private val gson : Gson
) {

    private val productMap: MutableSet<ResponseGetProductInfo> = mutableSetOf()

    fun cacheProduct(response: ResponseGetProductInfo) {
        productMap.add(response)
    }

    fun getProduct(productCode: String, templateCode: String): ResponseGetProductInfo? {
        return productMap.firstOrNull() {
            it.productInfo.productCode == productCode && it.templateInfo.templateCode == templateCode
        }
    }

    fun getProductPolicy(productCode: String, templateCode: String): Single<ProductPolicy> {
        return Single.just(productPolicyFactory.findPolicy(productCode, templateCode))
    }

    fun getRawSpineInfo(): Single<SpineInfoDto> {
        return Single
            .fromCallable {
                resources.openRawResource(R.raw.spine_info)
                    .bufferedReader()
                    .use { it.readText() }
            }
            .map {
                gson.fromJson(it, SpineInfoDto::class.java)
            }
    }

}