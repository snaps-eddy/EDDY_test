package com.snaps.mobile.domain.asset

import com.snaps.mobile.domain.product.ProductAspect
import com.snaps.mobile.domain.save.Scene
import com.snaps.mobile.domain.template.TemplateScene
import io.reactivex.rxjava3.core.Single

interface AssetRepository {

    fun getHEIFImageCount(assetImageType: AssetImageType): Single<Int>

    fun getAlbumList(assetImageType: AssetImageType): Single<List<AssetImageAlbum>>

    fun getAlbumDetails(albumId: String, assetType: AssetImageType): Single<List<List<AssetImage>>>

    fun getBackgroundImages(productCode: String, aspect: ProductAspect): Single<List<AssetSceneBackground>>

    fun getCoverCatalog(productCode: String, aspect: ProductAspect): Single<List<AssetSceneCoverTemplate>>

    fun getSceneLayouts(productCode: String, type: Scene.Type, aspect: ProductAspect): Single<List<AssetSceneLayout>>

    /**
     * 임시.. 아직 어떻게 할지 모르겠다. Repository 에서 받아올지
     * 아니면 Editor 에서 받은 json 파일을 믿고 파싱을 할지 ..
     */
    fun parseTemplateScene(jsonContents: String): Single<TemplateScene>
}