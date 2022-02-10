package com.snaps.mobile.domain.asset.usecase

import com.snaps.common.utils.log.Dlog
import com.snaps.mobile.domain.asset.RecipeImage
import com.snaps.mobile.domain.project.ProjectRepository
import com.snaps.mobile.domain.project.usecase.UploadThumbnailsUseCase
import com.snaps.mobile.domain.usecase.SingleUseCase
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class AddMoreRecipeImagesUseCase @Inject constructor(
    private val projectRepository: ProjectRepository,
    private val uploadThumbnail: UploadThumbnailsUseCase,
) : SingleUseCase<Int, AddMoreRecipeImagesUseCase.Params> {

    override fun invoke(params: Params): Single<Int> {
        return projectRepository.getProject(projectCode = params.projectCode)
            .map {
                it.addMoreRecipeImage(params.images)
            }
            .flatMapPublisher {
                uploadThumbnail(
                    UploadThumbnailsUseCase.Params(
                        projectCode = params.projectCode,
                        userNo = params.userNo,
                        deviceId = params.deviceId,
                        enableFaceFinder = true
                    )
                )
            }
            .toList()
            .map {
                /**
                 * 리턴 값 받아서 사용하는 곳 없음. 임시로
                 */
                Dlog.d("it ! $it")
//                it.first()
                0
            }
    }

    data class Params(
        val projectCode: String,
        val images: List<RecipeImage>,
        val userNo: String,
        val deviceId: String,
    )

}