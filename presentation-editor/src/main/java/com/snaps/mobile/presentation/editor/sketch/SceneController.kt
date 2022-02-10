package com.snaps.mobile.presentation.editor.sketch

import android.content.ClipData
import android.graphics.Bitmap
import android.graphics.Rect
import android.widget.RelativeLayout
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.IdUtils
import com.airbnb.epoxy.TypedEpoxyController
import com.snaps.mobile.domain.save.Scene
import com.snaps.mobile.presentation.editor.sketch.itemview.*
import com.snaps.mobile.presentation.editor.sketch.model.ImageMovingData
import com.snaps.mobile.presentation.editor.sketch.model.SceneItem
import com.snaps.mobile.presentation.editor.utils.dp

class SceneController constructor(
    private val callbacks: CallbacksListener
) : TypedEpoxyController<List<SceneItem>>() {

    override fun buildModels(data: List<SceneItem>?) {
        var viewIndex = 1
        data?.forEachIndexed { index, sceneItem ->
            when (sceneItem.type) {
                is Scene.Type.Cover -> {
                    sceneItemCoverView {
                        id(sceneItem.drawIndex)
                        sceneItem(sceneItem)
                        onImageDrop(callbacks::onDropImage)
                        onClickUserImage(callbacks::onClickUserImage)
                        onClickUserText(callbacks::onClickUserText)
                        onClickChangeTitle(callbacks::onClickChangeTitle)
                        spanSizeOverride { totalSpanCount, _, _ -> totalSpanCount }
                    }
                    sceneItemChangeCoverView {
                        id(modelCountBuiltSoFar)
                        spanSizeOverride { totalSpanCount, _, _ -> totalSpanCount }
                        coverTemplateCode(sceneItem.templateCode)
                        onClickChangeCover(callbacks::onClickChangeCover)
                    }
                }
                Scene.Type.Page -> {
                    when (sceneItem.subType) {
                        Scene.SubType.Blank -> {
                            val side = getSide(index)
                            sceneItemPageBlankView {
                                id(sceneItem.drawIndex)
                                sceneDrawIndex(sceneItem.drawIndex)
                                sceneObjectItems(sceneItem.sceneObjects)
                                sceneWidth(sceneItem.getDrawWidth())
                                sceneHeight(sceneItem.getDrawHeight())
                                side(side)
                                spanSizeOverride { _, _, _ -> 1 }
                            }
                        }

                        Scene.SubType.Page -> {
                            val side = getSide(index)
                            sceneItemPageView {
                                id(sceneItem.drawIndex)
                                val viewIndexText = viewIndex++.toString()
                                pageIndex(viewIndexText)
                                dataIndex(index)
                                sceneDrawIndex(sceneItem.drawIndex)
                                sceneWidth(sceneItem.getDrawWidth())
                                sceneHeight(sceneItem.getDrawHeight())
                                sceneObjectItems(sceneItem.sceneObjects)
                                lockOn(sceneItem.isLockOn)
                                templateCode(sceneItem.templateCode)
                                side(side)
                                onDropScene(callbacks::onDropScene)
                                onDropImage(callbacks::onDropImage)
                                onSelectScene(callbacks::onSelectScene)
                                onClickChangeLayout(callbacks::onClickChangeLayout)
                                onClickUserImage(callbacks::onClickUserImage)
                                onClickUserText(callbacks::onClickUserText)
                                onStartDragImage(callbacks::onStartDragImage)
                                onStartDragScene(callbacks::onStartDragScene)
                                spanSizeOverride { _, _, _ -> 1 }
                                val prevData = data[index - 1]
                                if (side.isRight && prevData.isPage) { // 첫번째 페이지는 속지와 한쌍이므로 삭제할 수 없다.
                                    prevSceneImagesEmpty(prevData.isEmptyUserImages)
                                    onClickDelete {
                                        callbacks.onClickDeletePage(sceneItem.drawIndex)
                                    }
                                }
                            }
                            if (side == ScenePageSide.Right) {
                                sceneItemAddPageZoneView {
                                    id(modelCountBuiltSoFar)
                                    val tempDataIndex = index + 1
                                    dataIndex(if (index == data.lastIndex) index - 1 else tempDataIndex)
                                    onDropImage { movingData ->
                                        callbacks.onDropImageToAddPage(movingData, sceneItem.drawIndex)
                                    }
                                    spanSizeOverride { totalSpanCount, _, _ -> totalSpanCount }
                                }
                            }
                        }
                        else -> {
                        }
//                        Scene.SubType.Spread -> {
//                            sceneItemSpreadView {
//                                id(sceneItem.drawIndex)
//                                val viewIndexText = "${viewIndex++}, ${viewIndex++}"
//                                viewIndex(viewIndexText)
//                                sceneItem(sceneItem)
//                                targetScene(sceneItem.isLockOn)
//                                onImageDrop(callbacks::onDropImage)
//                                onSceneSelect(callbacks::onSelectScene)
//                                spanSizeOverride { totalSpanCount, _, _ -> totalSpanCount }
//                            }
//                        }
//                        Scene.SubType.Hard -> {
//                        }
                    }
                }
            }
        }
        sceneItemAddPageView {
            id(modelCountBuiltSoFar)
            spanSizeOverride { totalSpanCount, _, _ -> totalSpanCount }
            onClickAddPage(callbacks::onClickAddPage)
        }
    }

    private fun getSide(scenePageIndex: Int): ScenePageSide {
        return if (scenePageIndex % 2 == 0) {
            ScenePageSide.Right
        } else {
            ScenePageSide.Left
        }
    }

    fun getSceneItemAt(viewPosition: Int): Int {
        if (viewPosition == -1) {
            return 0
        }
        return when (val atModel = adapter.getModelAtPosition(viewPosition)) {
            is SceneItemPageView -> atModel.dataIndex
            is SceneItemChangeCoverView -> 2
            is SceneItemAddPageZoneView -> atModel.dataIndex
            else -> 0
        }
    }

    fun findViewPosition(sceneItem: SceneItem): Int {
        val modelId = IdUtils.hashString64Bit(sceneItem.drawIndex)
        return adapter.getModelById(modelId).run {
            adapter.getModelPosition(this as EpoxyModel<*>)
        }
    }

    sealed class ScenePageSide(
        val pageDividerSkinVisible: Boolean,
        val addRule: Int,
        val removeRule: Int,
        val margins: Rect
    ) {
        object Left : ScenePageSide(true, RelativeLayout.ALIGN_PARENT_END, RelativeLayout.ALIGN_PARENT_START, Rect(16.dp(), 12.dp(), 0, 0))
        object Right : ScenePageSide(false, RelativeLayout.ALIGN_PARENT_START, RelativeLayout.ALIGN_PARENT_END, Rect(0, 12.dp(), 16.dp(), 0))

        val isLeft: Boolean
            get() = this == Left

        val isRight: Boolean
            get() = !isLeft
    }

    interface CallbacksListener {

        fun onClickUserText(sceneDrawIndex: String, sceneObjectDrawIndex: String)

        fun onDropScene(from: String, to: String, after: Boolean)

        fun onDropImage(from: ImageMovingData, to: ImageMovingData)

        fun onDropImageToAddPage(from: ImageMovingData, prevSceneDrawIndex: String)

        fun onSelectScene(sceneDrawIndex: String, selected: Boolean)

        fun onClickChangeLayout(sceneDrawIndex: String)

        fun onClickChangeCover(templateCode: String)

        fun onClickUserImage(sceneDrawIndex: String, sceneObjectDrawIndex: String, imgSeq: String)

        fun onStartDragScene(snapshot: Bitmap, sceneDrawIndex: String, clipData: ClipData)

        fun onStartDragImage(snapshot: Bitmap, movingData: ImageMovingData, clipData: ClipData)

        fun onClickAddPage()

        fun onClickDeletePage(rightDrawIndex: String)

        fun onClickChangeTitle(viewId: Int)

    }
}

