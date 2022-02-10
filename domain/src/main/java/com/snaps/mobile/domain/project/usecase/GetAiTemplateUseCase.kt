package com.snaps.mobile.domain.project.usecase

import com.snaps.mobile.domain.project.ProjectRepository
import com.snaps.mobile.domain.template.Template
import com.snaps.mobile.domain.usecase.SingleUseCase
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class GetAiTemplateUseCase @Inject constructor(
    private val projectRepository: ProjectRepository,
) : SingleUseCase<Template, GetAiTemplateUseCase.Params> {

    override fun invoke(params: Params): Single<Template> {
        return projectRepository.getProject(projectCode = params.projectCode)
            .flatMap { project ->
                projectRepository.getAiTemplate(
                    deviceId = params.deviceId,
                    userNo = params.userNo,
                    projectCode = params.projectCode,
                    language = params.language,
                    productInfo = project.productInfo,
                    thumbnailList = project.getImageThumbnail()
                ).map { aiTemplate ->
                    project.setTemplateCode(aiTemplate.templateCode)
                    project.loadAiTemplate(aiTemplate.template, aiTemplate.imageKeyList, aiTemplate.bookTitle)
                    aiTemplate.template
                }
            }
    }

    data class Params(
        val projectCode: String,
        val userNo: String,
        val deviceId: String,
        val language: String,
    )
}