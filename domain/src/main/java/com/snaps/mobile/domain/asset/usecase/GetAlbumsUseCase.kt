package com.snaps.mobile.domain.asset.usecase

import com.snaps.mobile.domain.asset.AssetImageAlbum
import com.snaps.mobile.domain.asset.AssetRepository
import com.snaps.mobile.domain.asset.AssetImageType
import com.snaps.mobile.domain.usecase.SingleUseCase
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class GetAlbumsUseCase @Inject constructor(
    private val repository: AssetRepository
) : SingleUseCase<List<AssetImageAlbum>, AssetImageType> {

    override fun invoke(params: AssetImageType): Single<List<AssetImageAlbum>> {
        return repository.getAlbumList(params)
    }

}