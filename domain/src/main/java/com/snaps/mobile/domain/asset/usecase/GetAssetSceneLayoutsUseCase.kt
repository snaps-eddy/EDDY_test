package com.snaps.mobile.domain.asset.usecase

import com.snaps.mobile.domain.asset.AssetRepository
import com.snaps.mobile.domain.asset.AssetSceneLayout
import com.snaps.mobile.domain.project.ProjectRepository
import com.snaps.mobile.domain.save.Scene
import com.snaps.mobile.domain.usecase.SingleUseCase
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class GetAssetSceneLayoutsUseCase @Inject constructor(
    private val projectRepository: ProjectRepository,
    private val assetRepository: AssetRepository
) : SingleUseCase<List<AssetSceneLayout>, GetAssetSceneLayoutsUseCase.Params> {

    override fun invoke(params: Params): Single<List<AssetSceneLayout>> {
        return projectRepository.getProject(params.projectCode)
            .flatMap {
                assetRepository.getSceneLayouts(params.productCode, params.sceneType, it.productInfo.getProductAspect())
            }
    }

    data class Params(
        val projectCode: String,
        val productCode: String,
        val sceneType: Scene.Type
    )

}