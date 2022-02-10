package com.snaps.mobile.domain.asset.usecase

import com.snaps.mobile.domain.asset.AssetImageType
import com.snaps.mobile.domain.asset.AssetRepository
import com.snaps.mobile.domain.usecase.SingleUseCase
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class GetHEIFImageCountUseCase @Inject constructor(
    private val repository: AssetRepository
) : SingleUseCase<Int, AssetImageType> {

    override fun invoke(params: AssetImageType): Single<Int> {
        return repository.getHEIFImageCount(params)
    }
}