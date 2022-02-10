package com.snaps.mobile.domain.save.usecase

import com.snaps.mobile.domain.project.ProjectRepository
import com.snaps.mobile.domain.save.Filter
import com.snaps.mobile.domain.save.Save
import com.snaps.mobile.domain.save.Scene
import com.snaps.mobile.domain.save.SceneObject
import com.snaps.mobile.domain.usecase.SingleUseCase
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class GetSceneObjectImagesUseCase @Inject constructor(
    private val projectRepository: ProjectRepository
) : SingleUseCase<List<GetSceneObjectImagesUseCase.Result>, GetSceneObjectImagesUseCase.Params> {

    override fun invoke(params: Params): Single<List<Result>> {
        return projectRepository.getProject(projectCode = params.projectCode)
            .map { project ->
                val coverSizeInfo = project.productInfo.getCoverSizeInfo()
                val pageSizeInfo = project.productInfo.getPageSizeInfo()

                val resultList = mutableListOf<Result>()

                project.save.scenes.forEach { scene ->
                    // 해당 씬에 맞는 mm, px 을 가져온다.
                    val sceneSizeInfo = when (scene.type) {
                        is Scene.Type.Cover -> coverSizeInfo
                        Scene.Type.Page -> pageSizeInfo
                    }
                    // 가로 세로 같은 비율로 하나만 구한다.
                    val sceneRatio = if (sceneSizeInfo == null) {
                        Save.mmPPC.toFloat() //mmPPCRatio를 1로 만들어버림
                    } else {
                        sceneSizeInfo.pxWidth / sceneSizeInfo.mmWidth.toFloat()
                    }

                    // 현재 씬의 비율에 맞는 PPC를 구한다.
                    val mmPPCRatio = Save.mmPPC / sceneRatio

                    val sceneImageList = mutableListOf<Result>()
                    scene.sceneObjects.filterIsInstance<SceneObject.Image>().forEach { image ->
                        image.content?.run {
                            val result = Result(
                                sceneObjectImage = image,
                                availableMaxWidth = width.div(mmPPCRatio),
                                availableMaxHeight = height.div(mmPPCRatio)
                            )
                            sceneImageList.add(result)
                        }
                    }

                    if (params.isSort) {
                        sceneImageList.sortBy { with(it.sceneObjectImage) { y * width + x } }
                    }
                    resultList.addAll(sceneImageList)
                }
                resultList
            }
    }

    data class Params(
        val projectCode: String,
        val isSort: Boolean = false
    )

    data class Result(
        val sceneObjectImage: SceneObject.Image,
        val availableMaxWidth: Float,
        val availableMaxHeight: Float
    )
}