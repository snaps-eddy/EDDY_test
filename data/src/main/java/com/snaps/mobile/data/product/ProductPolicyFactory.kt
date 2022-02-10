package com.snaps.mobile.data.product

import com.snaps.common.utils.log.Dlog
import com.snaps.mobile.domain.product.ProductPolicy
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductPolicyFactory @Inject constructor(

) {

    fun findPolicy(productCode: String, templateCode: String): ProductPolicy {
        Dlog.d("productCode : $productCode, templateCode : $templateCode")
        return ProductPolicy(productCode, templateCode)
    }

}