package com.snaps.mobile.domain.asset.usecase

import com.snaps.mobile.domain.asset.AssetImage
import com.snaps.mobile.domain.asset.AssetRepository
import com.snaps.mobile.domain.asset.AssetImageType
import com.snaps.mobile.domain.usecase.SingleUseCase
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class GetAlbumImagesUseCase @Inject constructor(
    private val repository: AssetRepository
) : SingleUseCase<List<List<AssetImage>>, GetAlbumImagesUseCase.Params> {

    override fun invoke(params: Params): Single<List<List<AssetImage>>> {
        return repository.getAlbumDetails(albumId = params.albumId, assetType = params.assetType)
    }

    data class Params(
        val albumId: String,
        val assetType: AssetImageType
    )

}