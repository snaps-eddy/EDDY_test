package com.snaps.mobile.domain.save.usecase

import com.snaps.mobile.domain.error.Reason
import com.snaps.mobile.domain.error.SnapsThrowable
import com.snaps.mobile.domain.project.ProjectRepository
import com.snaps.mobile.domain.save.Scene
import com.snaps.mobile.domain.usecase.SingleUseCase
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class AddPageUseCase @Inject constructor(
    private val projectRepository: ProjectRepository
) : SingleUseCase<Boolean, AddPageUseCase.Params> {

    override fun invoke(params: Params): Single<Boolean> {
        return projectRepository.getProject(params.projectCode)
            .doOnSuccess { if (it.isMaxScene()) throw SnapsThrowable(Reason.OverMaxCount(151, it.getPageSceneCount())) }
            .map { project ->
                val lastPage = project.save.scenes.last()
                val lastIndex = project.save.scenes.size
                val emptyLeft = Scene().apply { this.makeEmptyScene(lastIndex, lastPage) } //todo 빈 Scene을 어떻게 구성할지에 따라 해당 부분을 repository에서 받든지 만들던지 해야함.
                val emptyRight = Scene().apply { this.makeEmptyScene(lastIndex, lastPage) }
                project.addScenes(emptyLeft, emptyRight).run {
                    if (this) {
                        true
                    } else {
                        // 151 사이즈 -> 변동 가능성 있음. (페이지 기준. 스프레드 아님)
                        throw SnapsThrowable(Reason.OverMaxCount(151, project.getPageSceneCount()))
                    }
                }
            }
    }

    data class Params(
        val projectCode: String
    )

}