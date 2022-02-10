package com.snaps.mobile.data.asset

import com.google.gson.Gson
import com.snaps.common.utils.log.Dlog
import com.snaps.mobile.data.SceneMapper
import com.snaps.mobile.data.template.TemplateSceneDto
import com.snaps.mobile.domain.asset.*
import com.snaps.mobile.domain.product.ProductAspect
import com.snaps.mobile.domain.save.Scene
import com.snaps.mobile.domain.template.TemplateScene
import com.snaps.mobile.domain.template.TemplateSceneObject
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.kotlin.toObservable
import javax.inject.Inject

class AssetRepositoryImpl @Inject constructor(
    private val deviceDataSource: DeviceDataSource,
    private val snapsResourceRemote: SnapsResourceRemoteDataSource,
    private val gson: Gson,
    private val sceneMapper: SceneMapper
) : AssetRepository {

    private val tag = AssetRepositoryImpl::class.java.simpleName

    override fun getHEIFImageCount(assetImageType: AssetImageType): Single<Int> {
        when (assetImageType) {
            AssetImageType.Device -> {
                return Single.fromCallable {
                    val time1 = System.currentTimeMillis()
                    deviceDataSource.getHEIFImageCount()
                        .also {
                            val time2 = System.currentTimeMillis()
                            val time = time2 - time1
                            Dlog.d(tag, "isExistHEIFImage() : $time millisecond")
                        }
                }
            }
            AssetImageType.External.Google -> {
                throw IllegalStateException("Google service not supported.")
            }
            AssetImageType.External.Facebook -> {
                throw IllegalStateException("Facebook service not supported.")
            }
            else -> {
                throw IllegalStateException("Unknown type.")
            }
        }
    }

    override fun getAlbumList(assetImageType: AssetImageType): Single<List<AssetImageAlbum>> {
        when (assetImageType) {
            AssetImageType.Device -> {
                return Single.fromCallable {
                    val time1 = System.currentTimeMillis()
                    deviceDataSource.getAlbumList()
                        .also {
                            val time2 = System.currentTimeMillis()
                            val time = time2 - time1
                            Dlog.d(tag, "getAlbumList() : $time millisecond")
                        }
                }
            }
            AssetImageType.External.Google -> {
                throw IllegalStateException("Google service not supported.")
            }
            AssetImageType.External.Facebook -> {
                throw IllegalStateException("Facebook service not supported.")
            }
            else -> {
                throw IllegalStateException("Unknown type.")
            }
        }
    }

    override fun getAlbumDetails(albumId: String, assetType: AssetImageType): Single<List<List<AssetImage>>> {
        when (assetType) {
            AssetImageType.Device -> {
                val time1 = System.currentTimeMillis()
                return Single.fromCallable {
                    deviceDataSource.getAlbumDetailsGroupByDay(albumId)
                        .also {
                            val time2 = System.currentTimeMillis()
                            val time = time2 - time1
                            Dlog.d(tag, "getAlbumDetails() : $time millisecond")
                        }
                }
            }
            AssetImageType.External.Google -> {
                throw IllegalStateException("Google service not supported.")
            }
            AssetImageType.External.Facebook -> {
                throw IllegalStateException("Facebook service not supported.")
            }
            else -> {
                throw IllegalStateException("Unknown type.")
            }
        }
    }

    override fun getBackgroundImages(productCode: String, aspect: ProductAspect): Single<List<AssetSceneBackground>> {
        return snapsResourceRemote.getBackgroundImages(productCode, aspect)
            .flatMapObservable { it.toObservable() }
            .filter { !it.templateCode.isNullOrBlank() }  //혹시나 해서 java에서 kotlin쪽으로 데이터를 넣어줘서 null이 될수도 있다.
            .doOnNext {
                if (it.thumbnailImageUrl.isNullOrBlank()) Dlog.e(tag, "[BackgroundImages] thumbnailImageUrl is null. code:${it.templateCode}")
                if (it.middleSizeImageUrl.isNullOrBlank()) Dlog.e(tag, "[BackgroundImages] middleSizeImageUrl is null. code:${it.templateCode}")
            }
            .filter { !it.thumbnailImageUrl.isNullOrBlank() && !it.middleSizeImageUrl.isNullOrBlank() }
            .map {
                AssetSceneBackground(
                    id = it.templateId,
                    code = it.templateCode,
                    resourceUri = it.middleSizeImageUrl ?: "",// 없을 경우에는 ?
                    thumbnailUri = it.thumbnailImageUrl ?: "", // 없을 경우에는 ?
                )
            }
            .toList()
    }

    override fun getCoverCatalog(productCode: String, aspect: ProductAspect): Single<List<AssetSceneCoverTemplate>> {
        return snapsResourceRemote.getCoverCatalog(productCode, aspect)
            .flatMapObservable { it.toObservable() }
            .filter { !it.templateCode.isNullOrBlank() }  //혹시나 해서 java에서 kotlin쪽으로 데이터를 넣어줘서 null이 될수도 있다.
            .doOnNext {
                if (it.jsonContents.isNullOrBlank()) Dlog.e(tag, "[Cover] jsonContents isNullOrBlank. code:${it.templateCode}")
                if (it.middleSizeImageUrl.isNullOrBlank()) Dlog.e(tag, "[Cover] middleSizeImageUrl isNullOrBlank. code:${it.templateCode}")
            }
            .filter { !it.jsonContents.isNullOrBlank() && !it.middleSizeImageUrl.isNullOrBlank() } // 임시로 막아둠.
            .map {
                Pair(it, sceneMapper.mapToModel(gson.fromJson(it.jsonContents, TemplateSceneDto::class.java)))
            }
            .doOnNext {
                val (snapsResourceDto, templateScene) = it
                if (templateScene.sceneObjects.filterIsInstance<TemplateSceneObject.Image>().count() != 1) {
                    buildString {
                        append("[Cover] There is not 1 image object.\n")
                        append("code:${snapsResourceDto.templateCode}\n")
                        append("middleSizeImageUrl:http://www.snaps.com/${snapsResourceDto.middleSizeImageUrl}\n")
                        append("${snapsResourceDto.jsonContents}")
                    }.let { log -> Dlog.e(tag, log) }
                }
            }
            .filter {
                val (_, templateScene) = it
                templateScene.sceneObjects.filterIsInstance<TemplateSceneObject.Image>().count() == 1
            }
            .map {
                val (snapsResourceDto, templateScene) = it
                AssetSceneCoverTemplate(
                    coverThumbnailUri = snapsResourceDto.middleSizeImageUrl ?: "",
                    templateCode = snapsResourceDto.templateCode,
                    templateId = snapsResourceDto.templateCode,
                    templateScene = templateScene
                )
            }
            .toList()
    }

    override fun getSceneLayouts(productCode: String, type: Scene.Type, aspect: ProductAspect): Single<List<AssetSceneLayout>> {
        return snapsResourceRemote.getLayouts(productCode, type, aspect)
            .flatMapObservable { it.toObservable() }
            .filter { !it.templateCode.isNullOrBlank() }  //혹시나 해서 java에서 kotlin쪽으로 데이터를 넣어줘서 null이 될수도 있다.
            .doOnNext {
                if (it.jsonContents.isNullOrBlank()) Dlog.e(tag, "[Layout] jsonContents isNullOrBlank. code:${it.templateCode}")
                if (it.middleSizeImageUrl.isNullOrBlank()) Dlog.e(tag, "[Layout] middleSizeImageUrl isNullOrBlank. code:${it.templateCode}")
            }
            .filter { !it.jsonContents.isNullOrBlank() && !it.middleSizeImageUrl.isNullOrBlank() } // 임시로 막아둠.
            .map {
                Pair(it, sceneMapper.mapToModel(gson.fromJson(it.jsonContents, TemplateSceneDto::class.java)))
            }
            .doOnNext {
                val (snapsResourceDto, templateScene) = it
                val imgaeObjectCount = templateScene.sceneObjects.filterIsInstance<TemplateSceneObject.Image>().count()
                if (imgaeObjectCount != snapsResourceDto.maskCount) {
                    buildString {
                        append("[Layout] The maskCount and the number of image objects do not match.\n")
                        append("maskCount:${snapsResourceDto.maskCount}  !=  imgaeObjectCount:${imgaeObjectCount}\n")
                        append("code:${snapsResourceDto.templateCode}\n")
                        append("middleSizeImageUrl:http://www.snaps.com/${snapsResourceDto.middleSizeImageUrl}\n")
                        append("${snapsResourceDto.jsonContents}")
                    }.let { log -> Dlog.e(tag, log) }
                }
            }
            .filter {
                val (snapsResourceDto, templateScene) = it
                templateScene.sceneObjects.filterIsInstance<TemplateSceneObject.Image>().count() == snapsResourceDto.maskCount
            }
            .map {
                val (snapsResourceDto, templateScene) = it
                AssetSceneLayout(
                    id = snapsResourceDto.templateId,
                    code = snapsResourceDto.templateCode,
                    thumbnailUri = snapsResourceDto.middleSizeImageUrl ?: "", // 없을 경우에는 ?
                    templateScene = templateScene,
                    maskCount = snapsResourceDto.maskCount
                )
            }
            .toList()
    }

    override fun parseTemplateScene(jsonContents: String): Single<TemplateScene> {
        return Single.fromCallable {
            val templateSceneDto = gson.fromJson(jsonContents, TemplateSceneDto::class.java)
            sceneMapper.mapToModel(templateSceneDto)
        }
    }

}