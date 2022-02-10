package com.snaps.mobile.data.project

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import com.snaps.common.utils.log.Dlog
import com.snaps.mobile.data.SceneMapper
import com.snaps.mobile.data.ai.AiDataProcess2
import com.snaps.mobile.data.util.SERVICE_TEMPOLARY_UNAVAILABLE
import com.snaps.mobile.data.util.applyRetryPolicy
import com.snaps.mobile.data.util.handleHttpError
import com.snaps.mobile.domain.asset.RecipeImage
import com.snaps.mobile.domain.product.ProductInfo
import com.snaps.mobile.domain.project.*
import com.snaps.mobile.domain.save.Filter
import com.snaps.mobile.domain.save.Save
import com.snaps.mobile.domain.template.ai.LayoutRecommendPage
import com.snaps.mobile.domain.template.ai.LayoutRecommendTemplate
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.kotlin.Singles
import io.reactivex.rxjava3.kotlin.toObservable
import javax.inject.Inject


class ProjectRepositoryImpl @Inject constructor(
    private val context: Context,
    private val remote: RemoteProjectDataSource,
    private val local: LocalProjectDataSource,
    private val aiDataProcess: AiDataProcess2,
    private val sceneMapper: SceneMapper
) : ProjectRepository {
    private val tag = ProjectRepositoryImpl::class.java.simpleName

    private var appVer = ""
    private fun getAppVer(): String {
        if (appVer.isEmpty()) {
            try {
                appVer = context.packageManager.getPackageInfo(context.packageName, 0).versionName
            } catch (e: PackageManager.NameNotFoundException) {
                Dlog.e(tag, e)
            }
        }
        return appVer
    }

    override fun getNewProjectOption(
        deviceId: String,
        userNo: String
    ): Single<ProjectOption> {
        return remote.getNewProjectOption(
            appVer = getAppVer(),
            osVer = Build.VERSION.RELEASE,
            deviceId = deviceId,
            userNo = userNo
        ).map {
            ProjectOption(it.projectCode)
        }
    }

    override fun getProjectOption(
        projectCode: String,
        deviceId: String,
        userNo: String,
    ): Single<ProjectOption> {
        return remote
            .getProjectOption(
                appVer = getAppVer(),
                osVer = Build.VERSION.RELEASE,
                deviceId = deviceId,
                userNo = userNo,
                projectCode = projectCode,
            )
            .compose(applyRetryPolicy(SERVICE_TEMPOLARY_UNAVAILABLE))
            .map {
                ProjectOption(
                    projectCode = it.projectCode,
                    productCode = it.productCode ?: "",
                    templateCode = it.templateCode ?: "",
                    projectName = it.projectName ?: "",
                    quantity = it.quantity,
                    paperCode = it.paperCode ?: "",
                    glossyType = it.glossyType ?: "",
                    frameCode = it.frameCode ?: "",
                )
            }
            .compose(handleHttpError())
    }

    override fun getSave(
        deviceId: String,
        userNo: String,
        projectCode: String
    ): Single<Save> {
        return remote.getSave(
            appVer = getAppVer(),
            osVer = Build.VERSION.RELEASE,
            deviceId = deviceId,
            userNo = userNo,
            projectCode = projectCode
        ).map {
            Dlog.d(Dlog.UI_MACRO, "LOAD_COMPLEATE_TEMPLEATE")
            sceneMapper.mapToModel(it)
        }
    }

    override fun getProject(projectCode: String): Single<Project> {
        return Single.just(local.getProject())
    }

    override fun getAiTemplate(
        deviceId: String,
        userNo: String,
        projectCode: String,
        language: String,
        productInfo: ProductInfo,
        thumbnailList: List<ImageThumbnail>
    ): Single<LayoutRecommendTemplate> {
        return Single.fromCallable {
            aiDataProcess.createRequestPhotoBook(
                deviceId = deviceId,
                userNo = userNo,
                projectCode = projectCode,
                language = language,
                productInfo = productInfo,
                thumbnailList = thumbnailList
            )
        }.flatMap { request ->
            remote.getAiTemplate(
                appVer = getAppVer(),
                osVer = Build.VERSION.RELEASE,
                deviceId = deviceId,
                userNo = userNo,
                request = request
            )
        }.map {
            aiDataProcess.createPhotoBookTemplate(
                productInfo = productInfo,
                thumbnailList = thumbnailList,
                response = it
            )
        }
    }

    override fun getAiRecommendLayout(
        deviceId: String,
        userNo: String,
        language: String,
        projectCode: String,
        productInfo: ProductInfo,
        layoutCode: String,
        recommendCount: Int,
        pageId: String,
        thumbnailList: List<ImageThumbnail>
    ): Single<LayoutRecommendPage> {
        return Single.fromCallable {
            local.isExistAiRecommandLayout(
                pageId = pageId,
                thumbnailList = thumbnailList,
            )
        }.flatMap { isExist ->
            if (isExist) {
                Single.fromCallable {
                    local.getAiRecommandLayouts(
                        pageId = pageId,
                        thumbnailList = thumbnailList,
                        layoutCode = layoutCode
                    )
                }
            } else {
                val request = aiDataProcess.createRequestRecommendLayouts(
                    deviceId = deviceId,
                    userNo = userNo,
                    projectCode = projectCode,
                    productInfo = productInfo,
                    language = language,
                    recommendCount = recommendCount,
                    thumbnailList = thumbnailList
                )
                remote.getAiRecommendLayoutList(
                    appVer = getAppVer(),
                    osVer = Build.VERSION.RELEASE,
                    deviceId = deviceId,
                    userNo = userNo,
                    request = request
                ).map {
                    aiDataProcess.createRecommendLayoutList(it)
                }.map {
                    local.setAiRecommandLayouts(
                        pageId = pageId,
                        layoutRecommendPageList = it
                    )
                    local.getAiRecommandLayouts(
                        pageId = pageId,
                        thumbnailList = thumbnailList,
                        layoutCode = layoutCode
                    )
                }
            }
        }
    }

    override fun cleanUpStorage() {
        local.cleanUpStorage()
    }

    override fun uploadThumbnail(
        deviceId: String,
        userNo: String,
        projectCode: String,
        recipeImage: RecipeImage,
        enableFaceFinder: Boolean,
    ): Single<ImageLoadReceipt> {
        return Single.fromCallable {
            local.createThumbnail(imageUri = recipeImage.localUri)
        }.flatMap { thumbImageInfo ->
            remote.uploadThumbnail(
                appVer = getAppVer(),
                osVer = Build.VERSION.RELEASE,
                deviceId = deviceId,
                userNo = userNo,
                projectCode = projectCode,
                recipeImage = recipeImage,
                thumbImageInfo = thumbImageInfo,
                enableFaceFinder = enableFaceFinder
            ).map {
                thumbImageInfo.file.delete()
                it
            }
        }
    }

    override fun uploadOriginalImage(
        deviceId: String,
        userNo: String,
        projectCode: String,
        imageThumbnail: ImageThumbnail
    ): Single<ImageThumbnail> {
        return remote.uploadOriginalImage(
            appVer = getAppVer(),
            osVer = Build.VERSION.RELEASE,
            deviceId = deviceId,
            userNo = userNo,
            projectCode = projectCode,
            imageThumbnail = imageThumbnail
        )
    }

    override fun uploadRemainingImages(
        deviceId: String,
        userNo: String,
        project: Project
    ): Single<Project> {
        return project.getImageThumbnail().toObservable()
            .flatMapSingle {
                remote.uploadOriginalImage(
                    appVer = getAppVer(),
                    osVer = Build.VERSION.RELEASE,
                    deviceId = deviceId,
                    userNo = userNo,
                    projectCode = project.code,
                    imageThumbnail = it
                )
            }.toList().map {
                project
            }
    }

    override fun isAfterOrderEdit(
        projectCode: String,
        deviceId: String,
        userNo: String,
    ): Single<Boolean> {
        return remote.isAfterOrderEdit(
            appVer = getAppVer(),
            osVer = Build.VERSION.RELEASE,
            deviceId = deviceId,
            userNo = userNo,
            projectCode = projectCode
        )
    }

    override fun uploadSave(
        projectCode: String,
        save: Save,
        thumbnailList: List<ImageThumbnail>,
        projectOption: ProjectOption,
        deviceId: String,
        userNo: String,
        cartThumbnail: Bitmap,
    ): Single<Boolean> {
        return Singles.zip(
            Single.fromCallable { local.createCartThumbnailFile(bitmap = cartThumbnail) },
            Single.fromCallable { local.createMiddleThumbnailFile(bitmap = cartThumbnail) },
            Single.fromCallable { local.createSaveFile(save = save) }
        ).flatMap { files ->
            remote.uploadSave(
                appVer = getAppVer(),
                osVer = Build.VERSION.RELEASE,
                projectCode = projectCode,
                thumbnailList = thumbnailList,
                projectOption = projectOption,
                deviceId = deviceId,
                userNo = userNo,
                thumbnailFile = files.first,
                middleThumbnailFile = files.second,
                saveFile = files.third
            ).map {
                Dlog.d(Dlog.CS_TOOL_TOY, "PROJECT_CODE:$projectCode")
                files.first.delete()
                files.second.delete()
                files.third.delete()
                it
            }
        }.map {
            it != null
        }
    }

    override fun update(project: Project): Single<Project> {
        return Single.just(local.cache(project))
    }

    override fun isExistCachedLayout(
        pageId: String,
        thumbnails: List<ImageThumbnail>
    ): Single<Boolean> {
        return Single.just(
            local.isExistAiRecommandLayout(
                pageId = pageId,
                thumbnailList = thumbnails
            )
        )
    }

    override fun getFilteredImage(
        uriText: String,
        filter: Filter
    ): Single<String> {
        return Single.fromCallable {
            local.getFilteredImage(
                uriText = uriText,
                filter = filter
            )
        }
    }

    override fun getPreviewFilteredImages(
        uriText: String,
        orientationAngle: Int,
        size: Int
    ): Single<Map<Filter, String?>> {
        return local.getPreviewFilteredImages(
            uriText = uriText,
            orientationAngle = orientationAngle,
            size = size
        )
    }
}