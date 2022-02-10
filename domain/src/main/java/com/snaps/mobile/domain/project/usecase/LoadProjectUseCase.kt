package com.snaps.mobile.domain.project.usecase

import android.os.Build
import com.snaps.common.android_utils.ApiProvider
import com.snaps.mobile.domain.product.ProductRepository
import com.snaps.mobile.domain.project.Project
import com.snaps.mobile.domain.project.ProjectRepository
import com.snaps.mobile.domain.save.Filter
import com.snaps.mobile.domain.save.Save
import com.snaps.mobile.domain.save.SaveInfo
import com.snaps.mobile.domain.save.SceneObject
import com.snaps.mobile.domain.save.usecase.GetFilteredImageUriUseCase
import com.snaps.mobile.domain.usecase.SingleUseCase
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.kotlin.toObservable
import javax.inject.Inject

class LoadProjectUseCase @Inject constructor(
    private val projectRepository: ProjectRepository,
    private val productRepository: ProductRepository,
    private val getFilteredImageUriUseCase: GetFilteredImageUriUseCase,
    private val apiProvider: ApiProvider
) : SingleUseCase<Project, LoadProjectUseCase.Params> {

    override fun invoke(params: Params): Single<Project> {
        projectRepository.cleanUpStorage()
        val stream = params.projectCode?.run {
            projectRepository.getProjectOption(
                this,
                deviceId = params.deviceId,
                userNo = params.userNo
            ).flatMap { projectOption ->
                projectRepository.getSave(
                    deviceId = params.deviceId,
                    userNo = params.userNo,
                    projectCode = projectOption.projectCode
                ).map { save ->
                    // 존재하는 프로젝트 생성 시, saveInfo 는 건들지 않는다.
                    Project(projectOption.projectCode)
                        .setSave(save)
                        .setProjectOption(projectOption)
                }.flatMap { project ->
                    getFilteredImageUri(project).map {
                        project
                    }
                }
            }
        } ?: projectRepository.getNewProjectOption(
            deviceId = params.deviceId,
            userNo = params.userNo
        ).map {
            it.apply {
                // 최소 생성시, 프론트에서 전달받은 파라미터로 projectOption 을 채워준다.
                this.productCode = params.productCode
                this.templateCode = params.templateCode
                this.glossyType = params.glossType ?: throw IllegalArgumentException("Need Gloss Type!")
                this.paperCode = params.paperCode ?: throw IllegalArgumentException("Need Paper Code!")
                this.quantity = if (params.projectCount == -1) throw IllegalArgumentException("Need Paper Code!") else params.projectCount
            }
        }.map {
            val save = Save(info = SaveInfo())
            Project(it.projectCode)
                .setProjectOption(it)
                .setSave(save)
        }

        return stream
            .flatMap { project ->
                productRepository.getInfos(productCode = params.productCode, templateCode = params.templateCode)
                    .map {
                        project.setInfos(it)
                        project.setUserAgent(getUserAgent(params.appVersion, params.ipAddress, params.ISP))
                        project.setlocationSearch(params.getClearLocationSearch())
                        project.checkWarningResolutions()
                    }
                    .flatMap {
                        productRepository.getSpineInfo()
                            .map { spineInfo ->
                                it.setSpineInfo(spineInfo)
                            }
                    }
                    .flatMap {
                        projectRepository.update(it)
                    }
            }
    }

    private fun getFilteredImageUri(project: Project): Single<List<String>> {
        return project.save.scenes
            .map { scene ->
                scene.sceneObjects
                    .filterIsInstance<SceneObject.Image>()
                    .filter { it.content != null }
                    .filter { it.filter != null && it.filter !is Filter.None }
            }
            .flatten()
            .toObservable()
            .flatMapSingle { objImg ->
                getFilteredImageUriUseCase.invoke(
                    GetFilteredImageUriUseCase.Params(
                        imageUri = apiProvider.newApiBaseUrl.plus(objImg.content!!.middleImagePath),
                        filter = objImg.filter ?: throw IllegalArgumentException("filter is null")
                    )
                ).doOnSuccess {
                    objImg.filter = Filter.fromCode(objImg.filter!!.code, it)
                }
            }.toList()
    }

    private fun getUserAgent(appVersion: String, ipAddress: String, ISP: String): String {
        return StringBuilder()
            .append(SaveInfo.OS).append("_").append(Build.VERSION.RELEASE)
            .append(" [")
            .append("SDK:").append(Build.VERSION.SDK_INT).append(", ")
            .append("brand:").append(Build.BRAND).append(", ")
            .append("device:").append(Build.DEVICE).append(", ")
            .append("product:").append(Build.PRODUCT).append(", ")
            .append("model:").append(Build.MODEL).append(", ")
            .append("IP:").append(ipAddress).append(", ")
            .append("ISP:").append(ISP)
            .append("] ")
            .append("/")
            .append(appVersion)
            .toString()
    }

    data class Params(
        val projectCode: String?,
        val productCode: String,
        val templateCode: String,
        val userNo: String,
        val deviceId: String,
        val locationSearch: String,
        val glossType: String?, // 최초 프로젝트 생성 시에만 프론트에서 전달받는다.
        val paperCode: String?, // 최초 프로젝트 생성 시에만 프론트에서 전달받는다.
        val projectCount: Int, // 최초 프로젝트 생성 시에만 프론트에서 전달받는다.
        val appVersion: String,
        val ipAddress: String,
        val ISP: String,
    ) {
        fun getClearLocationSearch(): String {
            return locationSearch.trim().let {
                if (it.startsWith("{")) it.substring(1) else it
            }.let {
                if (it.endsWith("}")) it.substring(0, it.length - 1) else it
            }
        }
    }
}