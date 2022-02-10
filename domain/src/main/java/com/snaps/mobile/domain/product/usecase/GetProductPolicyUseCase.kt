package com.snaps.mobile.domain.product.usecase

import com.snaps.mobile.domain.product.ProductPolicy
import com.snaps.mobile.domain.product.ProductRepository
import com.snaps.mobile.domain.usecase.SingleUseCase
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class GetProductPolicyUseCase @Inject constructor(
    private val productRepository: ProductRepository
) : SingleUseCase<ProductPolicy, GetProductPolicyUseCase.Params> {


    override fun invoke(params: Params): Single<ProductPolicy> {
        return productRepository.getProductPolicy(params.productCode, params.templateCode)
    }

    data class Params(
        val productCode: String,
        val templateCode: String,
    )

}