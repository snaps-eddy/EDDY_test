package com.snaps.mobile.domain.save.usecase

import com.snaps.mobile.domain.project.ProjectRepository
import com.snaps.mobile.domain.save.Save
import com.snaps.mobile.domain.usecase.SingleUseCase
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class ChangeAiLayoutUseCase @Inject constructor(
    private val projectRepository: ProjectRepository
) : SingleUseCase<Save, ChangeAiLayoutUseCase.Params> {

    override fun invoke(params: Params): Single<Save> {
        return projectRepository.getProject(projectCode = params.projectCode)
            .flatMap { project ->
                projectRepository.getAiRecommendLayout(
                    deviceId = params.deviceId,
                    userNo = params.userNo,
                    language = params.language,
                    projectCode = params.projectCode,
                    productInfo = project.productInfo,
                    recommendCount = 5,
                    layoutCode = project.getSceneLayoutCode(params.currentSceneDrawId),
                    thumbnailList = project.getThumbnailsInScene(sceneId = params.currentSceneDrawId),
                ).map {
                    if (it.analysisInfoMap.isNotEmpty()) project.updateAnalysisInfo(it.analysisInfoMap)
                    project.applyAiRecommendLayout(params.currentSceneDrawId, it.scene, it.imageKeyList)
                }
            }
    }

    data class Params(
        val projectCode: String,
        val productCode: String,
        val userNo: String,
        val deviceId: String,
        val currentSceneDrawId: String,
        val language: String
    )
}