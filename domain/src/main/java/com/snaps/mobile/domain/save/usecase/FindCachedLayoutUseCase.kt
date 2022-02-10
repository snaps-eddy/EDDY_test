//package com.snaps.mobile.domain.save.usecase
//
//import com.snaps.mobile.domain.project.ProjectRepository
//import com.snaps.mobile.domain.usecase.SingleUseCase
//import io.reactivex.rxjava3.core.Single
//import io.reactivex.rxjava3.kotlin.Singles
//import javax.inject.Inject
//
///**
// * Change Layout을 요청할 사진들이 로컬 캐싱 되어있는지 확인하는 Usecase
// * 캐싱이 되어있지 않으면 Progress를 보여줘야 하기 때문.
// */
//class FindCachedLayoutUseCase @Inject constructor(
//    private val projectRepository: ProjectRepository
//) : SingleUseCase<Boolean, FindCachedLayoutUseCase.Params> {
//
//    override fun invoke(params: Params): Single<Boolean> {
//        return projectRepository.getProject(projectCode = params.projectCode)
//            .flatMap {
//                when (params) {
//                    is Params.ChangeLayout -> {
//                        val thumbnailList = it.getThumbnailsInScene(sceneId = params.sceneDrawIndex)
//                        projectRepository.isExistCachedLayout(thumbnails = thumbnailList)
//                    }
//                    is Params.AddRecipeImageToScene -> {
//                        val thumbnailList = it.getThumbnailsInScene(sceneId = params.dstSceneDrawIndex, withAddImgSeq = params.targetImgSeq)
//                        projectRepository.isExistCachedLayout(thumbnails = thumbnailList)
//                    }
//                    is Params.AddUserImageToScene -> {
//                        val targetThumbnailList = it.getThumbnailsInScene(sceneId = params.dstSceneDrawIndex, withDeleteImgSeq = params.targetImgSeq)
//                        val dstThumbnailList = it.getThumbnailsInScene(sceneId = params.dstSceneDrawIndex, withAddImgSeq = params.targetImgSeq)
//                        Singles.zip(
//                            projectRepository.isExistCachedLayout(thumbnails = targetThumbnailList),
//                            projectRepository.isExistCachedLayout(thumbnails = dstThumbnailList)
//                        ).map { result ->
//                            result.first && result.second
//                        }
//                    }
//                }
//            }
//    }
//
//    sealed class Params {
//        abstract val projectCode: String
//
//        data class ChangeLayout(
//            override val projectCode: String,
//            val sceneDrawIndex: String
//        ) : Params()
//
//        data class AddRecipeImageToScene(
//            override val projectCode: String,
//            val targetImgSeq: String,
//            val dstSceneDrawIndex: String
//        ) : Params()
//
//        data class AddUserImageToScene(
//            override val projectCode: String,
//            val targetImgSeq: String,
//            val targetSceneDrawIndex: String,
//            val dstSceneDrawIndex: String,
//        ) : Params()
//    }
//}