package com.snaps.mobile.domain.asset.usecase

import com.snaps.mobile.domain.asset.AssetRepository
import com.snaps.mobile.domain.asset.AssetSceneCoverTemplate
import com.snaps.mobile.domain.project.ProjectRepository
import com.snaps.mobile.domain.usecase.SingleUseCase
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class GetAssetSceneCoversUseCase @Inject constructor(
    private val projectRepository: ProjectRepository,
    private val assetRepository: AssetRepository
) : SingleUseCase<List<AssetSceneCoverTemplate>, GetAssetSceneCoversUseCase.Params> {

    override fun invoke(params: Params): Single<List<AssetSceneCoverTemplate>> {
        return projectRepository.getProject(params.projectCode)
            .flatMap {
                assetRepository.getCoverCatalog(params.productCode, it.productInfo.getProductAspect())
            }
    }

    data class Params(
        val projectCode: String,
        val productCode: String
    )

}