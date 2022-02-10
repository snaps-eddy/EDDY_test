package com.snaps.mobile.presentation.editor.sketch.model

import android.graphics.Color
import com.snaps.common.android_utils.ApiProvider
import com.snaps.common.android_utils.ResourceProvider
import com.snaps.mobile.domain.save.Scene
import com.snaps.mobile.domain.save.SceneObject
import com.snaps.mobile.presentation.editor.utils.dp
import javax.inject.Inject

class SceneUiItemMapper @Inject constructor(
    resourceProvider: ResourceProvider,
    private val apiProvider: ApiProvider,
) {

    private val drawCoverSceneWidth: Float
    private val drawPageSceneWidth: Float
    private val drawSpreadPageSceneWidth: Float

    init {
        resourceProvider.getScreenWidth().apply {
            drawCoverSceneWidth = this.times(1.68f).minus(65.dp().times(2.0f))
            drawPageSceneWidth = this.div(2.0f).minus(16.dp())
            drawSpreadPageSceneWidth = this.minus(16.dp().times(2.0f))
        }
    }

    fun mapToItem(scene: Scene): SceneItem {
        val scaleFactor = when (scene.type) {
            is Scene.Type.Cover -> drawCoverSceneWidth / scene.width
            Scene.Type.Page -> when (scene.subType) {
                Scene.SubType.Hard -> throw IllegalStateException("Type ${scene.type} is not available using subtype ${scene.subType}")
                Scene.SubType.Page -> drawPageSceneWidth / scene.width
                Scene.SubType.Spread -> drawSpreadPageSceneWidth / scene.width
                Scene.SubType.Blank -> drawPageSceneWidth / scene.width
            }
        }

        return SceneItem(
            drawIndex = scene.drawIndex,
            templateCode = scene.templateCode,
            type = scene.type,
            subType = scene.subType,
            width = scene.width,
            height = scene.height,
            scaleFactor = scaleFactor,
            sceneObjects = scene.sceneObjects.map { mapToItems(it, scaleFactor) },
            midWidth = scene.midWidth
        )
    }

    private fun mapToItems(sceneObject: SceneObject, scaleFactor: Float): SceneObjectItem {
        return when (sceneObject) {
            is SceneObject.Background.Color -> {
                SceneObjectItem.Background.Color(
                    drawIndex = sceneObject.drawIndex,
                    x = sceneObject.x,
                    y = sceneObject.y,
                    width = sceneObject.width,
                    height = sceneObject.height,
                    alpha = sceneObject.alpha,
                    angle = sceneObject.angle,
                    scaleFactor = scaleFactor,
                    bgColor = Color.parseColor(sceneObject.bgColor)
                )
            }
            is SceneObject.Background.Image -> {
                SceneObjectItem.Background.Image(
                    drawIndex = sceneObject.drawIndex,
                    x = sceneObject.x,
                    y = sceneObject.y,
                    width = sceneObject.width,
                    height = sceneObject.height,
                    alpha = sceneObject.alpha,
                    angle = sceneObject.angle,
                    scaleFactor = scaleFactor,
                    middleImagePath = sceneObject.middleImagePath?.run {
                        if (this.isNotBlank()) {
                            apiProvider.newApiBaseUrl.plus(sceneObject.middleImagePath)
                        } else {
                            null
                        }
                    },
                )
            }
            is SceneObject.Image -> {
                SceneObjectItem.Image(
                    drawIndex = sceneObject.drawIndex,
                    x = sceneObject.x,
                    y = sceneObject.y,
                    width = sceneObject.width,
                    height = sceneObject.height,
                    alpha = sceneObject.alpha,
                    angle = sceneObject.angle,
                    scaleFactor = scaleFactor,
                    content = sceneObject.content?.run {
                        ImageContentUiModel(
                            imgSeq = this.imgSeq,
                            year = this.year,
                            thumbnailUri = apiProvider.newApiBaseUrl.plus(this.middleImagePath),
                            originWidth = this.width,
                            originHeight = this.height,
                            isWarningResolution = this.warningResolution,
                            analysisInfo = this.analysisInfo,
                            orientation = this.orientation
                        )
                    },
                    innerImage = sceneObject.innerImage?.run {
                        InnerImageUiModel(
                            x = sceneObject.innerImage?.x ?: 0f,
                            y = sceneObject.innerImage?.y ?: 0f,
                            width = sceneObject.innerImage?.width ?: 0f,
                            height = sceneObject.innerImage?.height ?: 0f,
                            angle = sceneObject.innerImage?.angle ?: 0,
                            alpha = sceneObject.innerImage?.alpha ?: 1.0f
                        )
                    },
                    filter = sceneObject.filter?.run {
                        FilterUiModel(
                            code = this.code,
                            name = this.name,
                            imageUri = this.imageUri
                        )
                    },
                    border = sceneObject.border?.run {
                        BorderUiModel(
                            imageId = this.imageId,
                            imagePath = this.imagePath,
                            maskId = this.maskId,
                            maskPath = if (maskPath.isNotEmpty()) apiProvider.newApiBaseUrl.plus(maskPath) else maskPath,
                            singleAlpha = this.singleAlpha,
                            singleColor= this.singleColor,
                            singleThickness = this.singleThickness,
                            imageOffset = this.imageOffset,
                            type = this.type
                        )
                    }
                )
            }
            is SceneObject.Sticker -> {
                SceneObjectItem.Sticker(
                    drawIndex = sceneObject.drawIndex,
                    x = sceneObject.x,
                    y = sceneObject.y,
                    width = sceneObject.width,
                    height = sceneObject.height,
                    alpha = sceneObject.alpha,
                    scaleFactor = scaleFactor,
                    angle = sceneObject.angle,
                    middleImagePath = apiProvider.newApiBaseUrl.plus(sceneObject.middleImagePath)
                )
            }
            is SceneObject.Text.Spine -> {
                SceneObjectItem.Text.Spine(
                    drawIndex = sceneObject.drawIndex,
                    x = sceneObject.x,
                    y = sceneObject.y,
                    width = sceneObject.width,
                    height = sceneObject.height,
                    alpha = sceneObject.alpha,
                    angle = sceneObject.angle,
                    scaleFactor = scaleFactor,
                    text = sceneObject.text,
                    defaultStyle = sceneObject.defaultStyle,
                    placeholder = sceneObject.placeholder,
                    readOnly = sceneObject.readOnly,
                    name = sceneObject.name
                )
            }
            is SceneObject.Text.User -> {
                SceneObjectItem.Text.User(
                    drawIndex = sceneObject.drawIndex,
                    x = sceneObject.x,
                    y = sceneObject.y,
                    width = sceneObject.width,
                    height = sceneObject.height,
                    alpha = sceneObject.alpha,
                    angle = sceneObject.angle,
                    scaleFactor = scaleFactor,
                    text = sceneObject.text,
                    defaultStyle = sceneObject.defaultStyle,
                    placeholder = sceneObject.placeholder,
                    readOnly = sceneObject.readOnly,
                    name = sceneObject.name
                )
            }
        }
    }
}