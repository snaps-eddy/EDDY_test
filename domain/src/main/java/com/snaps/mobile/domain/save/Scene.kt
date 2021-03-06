package com.snaps.mobile.domain.save

import com.snaps.common.utils.log.Dlog
import com.snaps.mobile.domain.error.SnapsThrowable
import com.snaps.mobile.domain.generateDrawIndex
import com.snaps.mobile.domain.project.ImageThumbnail
import com.snaps.mobile.domain.template.TemplateScene
import com.snaps.mobile.domain.template.TemplateSceneObject
import java.util.*

data class Scene(
    var drawIndex: String,
    var width: Float,
    var type: Type,
    var subType: SubType,
    var sceneObjects: MutableList<SceneObject>,
    var printCount: Int,
    var height: Float,
    var templateCode: String,
    var hiddenIdx: Int,
    var side: String?,
    var layoutCode: String,
    var layoutType: String,
    var initialMillimeterWidth: Int,
    var initialMillimeterHeight: Int,
    var year: String,
    var month: String,
    var midWidth: Float,
    var defaultTemplateCode: String,
    var defaultLayoutCode: String,
    var defaultStickerIdList: List<String>,
    var defaultBackgroundId: String,
) {

    constructor() : this(
        drawIndex = 0.generateDrawIndex(),
        width = 0f,
        type = Type.Page,
        subType = SubType.Page,
        sceneObjects = mutableListOf(),
        printCount = 0,
        height = 0f,
        templateCode = "",
        hiddenIdx = 0,
        side = null,
        layoutCode = "",
        layoutType = "",
        initialMillimeterWidth = 0,
        initialMillimeterHeight = 0,
        year = "",
        month = "",
        midWidth = 0f,
        defaultTemplateCode = "",
        defaultLayoutCode = "",
        defaultStickerIdList = mutableListOf(),
        defaultBackgroundId = "",
    )

    fun applySceneTemplate(sceneIndex: Int, templateScene: TemplateScene) {
        drawIndex = sceneIndex.generateDrawIndex()
        copyPropertyFrom(templateScene)
        sceneObjects = applySceneObjectTemplate(templateScene.sceneObjects).toMutableList()
    }

    private fun applySceneObjectTemplate(templateSceneObjects: List<TemplateSceneObject>): List<SceneObject> {
        return templateSceneObjects
            .mapIndexed { index, templateSceneObject ->
                when (templateSceneObject) {
                    is TemplateSceneObject.Background.Color -> SceneObject.Background.Color().copyFromTemplate(index, templateSceneObject)
                    is TemplateSceneObject.Background.Image -> SceneObject.Background.Image().copyFromTemplate(index, templateSceneObject)
                    is TemplateSceneObject.Image -> SceneObject.Image().copyFromTemplate(index, templateSceneObject)
                    is TemplateSceneObject.Sticker -> SceneObject.Sticker().copyFromTemplate(index, templateSceneObject)
                    is TemplateSceneObject.Text.Spine -> SceneObject.Text.Spine().copyFromTemplate(index, templateSceneObject)
                    is TemplateSceneObject.Text.User -> SceneObject.Text.User().copyFromTemplate(index, templateSceneObject)
                }
            }
    }

    fun changeTemplate(templateScene: TemplateScene, imageList: Queue<ImageThumbnail?>) {
        copyPropertyFrom(templateScene)
        sceneObjects = changeSceneObjectTemplate(templateScene.sceneObjects).toMutableList().apply {
            filterIsInstance<SceneObject.Image>()
                .map { sceneObjectImage ->
                    imageList.poll()?.let {
                        sceneObjectImage.insertImage(it)
                    }
                }
        }
    }

    private fun changeSceneObjectTemplate(templateSceneObjects: List<TemplateSceneObject>): List<SceneObject> {
        return templateSceneObjects
            .map { templateSceneObject ->
                when (templateSceneObject) {
                    is TemplateSceneObject.Background.Color -> SceneObject.Background.Color().changeTemplate(templateSceneObject)
                    is TemplateSceneObject.Background.Image -> SceneObject.Background.Image().changeTemplate(templateSceneObject)
                    is TemplateSceneObject.Image -> SceneObject.Image().changeTemplate(templateSceneObject)
                    is TemplateSceneObject.Sticker -> SceneObject.Sticker().changeTemplate(templateSceneObject)
                    is TemplateSceneObject.Text.Spine -> SceneObject.Text.Spine().changeTemplate(templateSceneObject)
                    is TemplateSceneObject.Text.User -> SceneObject.Text.User().changeTemplate(templateSceneObject)
                }
            }
    }

    fun changeTemplate(templateScene: TemplateScene) {
        copyPropertyFrom(templateScene)
        val imageContents = sceneObjects.filterIsInstance<SceneObject.Image>()
            .mapNotNull { it.content }
            .run {
                LinkedList(this)
            }

        sceneObjects = changeSceneObjectTemplate(templateScene.sceneObjects).toMutableList().apply {
            filterIsInstance<SceneObject.Image>()
                .map { sceneObjectImage ->
                    imageContents.poll()?.let {
                        sceneObjectImage.changeImageContent(it)
                    }
                    imageContents
                }
        }
    }

    /**
     * Scene ????????? ?????? ????????? ??????
     * DefaultTemplateCode ??? ????????? TemplateCode.
     * templateCode??? ????????? ?????? TemplateCode ??????.
     */
    fun copyPropertyFrom(templateScene: TemplateScene) {
        width = templateScene.width
        height = templateScene.height
        type = templateScene.type
        subType = templateScene.subType
        printCount = templateScene.printCount
        templateCode = templateScene.templateCode
        hiddenIdx = templateScene.hiddenIdx
        side = templateScene.side
        layoutCode = templateScene.layoutCode
        layoutType = templateScene.layoutType
        initialMillimeterWidth = templateScene.initialMillimeterWidth
        initialMillimeterHeight = templateScene.initialMillimeterHeight
        year = templateScene.year
        month = templateScene.month
        defaultTemplateCode = templateScene.defaultTemplateCode
        defaultLayoutCode = templateScene.defaultLayoutCode
        defaultStickerIdList = templateScene.defaultStickerIdList
        defaultBackgroundId = templateScene.defaultBackgroundId
    }

    /**
     * ?????? Scene ?????? ?????? Image??? ????????? ?????? ????????? ???????????? data??? ?????????.
     * ????????? ?????? ??????. ????????? ??? Scene??? ?????? ????????? ?????? ?????? ?????? ???????????????.
     */
    fun makeEmptyScene(sceneIndex: Int, cloneScene: Scene) {
        drawIndex = sceneIndex.generateDrawIndex()
        templateCode = "045021000000" //??? ????????? ??????
        copySceneProperty(cloneScene)

        cloneScene.sceneObjects
            .filterIsInstance<SceneObject.Background>()
            .firstOrNull()
            ?.let {
                sceneObjects.clear()
                it.drawIndex = 0.generateDrawIndex()
                when (it) {
                    is SceneObject.Background.Color -> sceneObjects.add(it.apply { bgColor = "#ffffff" })
                    is SceneObject.Background.Image -> sceneObjects.add(it.convertToColor("#ffffff"))
                }
            }
    }

    private fun copySceneProperty(cloneScene: Scene) {
        width = cloneScene.width
        height = cloneScene.height
        type = cloneScene.type
        subType = cloneScene.subType
        printCount = 0
        hiddenIdx = 0
        side = null
        layoutCode = cloneScene.layoutCode
        layoutType = cloneScene.layoutType
        initialMillimeterWidth = cloneScene.initialMillimeterWidth
        initialMillimeterHeight = cloneScene.initialMillimeterHeight
        year = cloneScene.year
        month = cloneScene.month
        defaultTemplateCode = cloneScene.defaultTemplateCode
        defaultLayoutCode = cloneScene.defaultLayoutCode
        defaultStickerIdList = cloneScene.defaultStickerIdList
        defaultBackgroundId = cloneScene.defaultBackgroundId
    }

    fun changeBackground(resourceId: String, resourceUri: String) {
        sceneObjects.filterIsInstance<SceneObject.Background>()
            .firstOrNull()
            ?.apply {
                when (this) {
                    is SceneObject.Background.Color -> {
                        val index = sceneObjects.indexOf(this)
                        sceneObjects[index] = this.convertToImage(resourceId, resourceUri)
                    }
                    is SceneObject.Background.Image -> {
                        this.resourceId = resourceId
                        this.middleImagePath = resourceUri
                    }
                }
            }
            ?: throw SnapsThrowable("No Background Scene Object. TemplateCode : $templateCode \n LayoutCode : $layoutCode")
    }

    fun getImageContents(): List<ImageContent> {
        return sceneObjects.filterIsInstance<SceneObject.Image>().mapNotNull { it.content }
    }

    fun getTextContents(): List<String> {
        return sceneObjects.filterIsInstance<SceneObject.Text>().map { it.text }
    }

    fun copySceneObjectsFrom(templateSceneObjects: List<TemplateSceneObject>) {
        sceneObjects = changeSceneObjectTemplate(templateSceneObjects).toMutableList()
    }

    fun fillImageContents(imageContents: LinkedList<ImageContent>) {
        sceneObjects.filterIsInstance<SceneObject.Image>()
            .forEach { sceneObjectImage ->
                imageContents.poll()?.let {
                    sceneObjectImage.changeImageContent(it)
                }
            }
    }

    fun fillTitleText(projectName: String) {
        sceneObjects.filterIsInstance<SceneObject.Text>()
            .map {
                Dlog.d(it)
                it
            }
            .filter { it.name == "title" || it is SceneObject.Text.Spine }
            .forEach { sceneObjectText ->
                sceneObjectText.text = projectName
            }
    }

    /**
     * LayoutTemplate ?????? Image, Text ??? ????????????. (Background ??????)
     */
    fun applyLayout(layoutTemplate: TemplateScene) {
        this.layoutCode = layoutTemplate.layoutCode
        // 1. ???????????? ????????? ???????????? ???????????????.
        val existsImageContents = sceneObjects
            .filterIsInstance<SceneObject.Image>()
            .mapNotNull { it.content }
            .map { it.copy() }
            .run { LinkedList(this) }

        // 2. SceneObject Text??? SceneObject Image ?????? ?????????.
        val textObjects = sceneObjects.filterIsInstance<SceneObject.Text>()
        sceneObjects.removeAll(textObjects)

        val imageObjects = sceneObjects.filterIsInstance<SceneObject.Image>()
        sceneObjects.removeAll(imageObjects)

        // 3. Image??? ??????, ????????? Content??? ????????? Image ??? ?????????.
        layoutTemplate.sceneObjects.forEachIndexed { index, templateSceneObject ->
            when (templateSceneObject) {
                is TemplateSceneObject.Image -> {
                    SceneObject.Image().copyFromTemplate(index, templateSceneObject).apply {
                        existsImageContents.poll()?.let {
                            this.changeImageContent(it)
                        }
                    }.also {
                        sceneObjects.add(it)
                    }
                }
                is TemplateSceneObject.Text.Spine -> {
                    SceneObject.Text.Spine().copyFromTemplate(index, templateSceneObject)
                        .also {
                            sceneObjects.add(it)
                        }
                }
                is TemplateSceneObject.Text.User -> {
                    SceneObject.Text.User().copyFromTemplate(index, templateSceneObject)
                        .also {
                            sceneObjects.add(it)
                        }
                }
                else -> {
                }
            }
        }
    }

    /**
     * Ai ?????? ??????????????? ????????? ????????? ?????? ??? ???????????? ?????? ??????????????????.
     */
    fun applyAiRecommendLayout(layoutTemplate: TemplateScene, imageList: Queue<ImageThumbnail?>) {
        // 1. Layout ?????? ??????.
        this.layoutCode = layoutTemplate.layoutCode
        // 2. SceneObject Text??? SceneObject Image ?????? ?????????.
        val textObjects = sceneObjects.filterIsInstance<SceneObject.Text>()
        sceneObjects.removeAll(textObjects)
        val imageObjects = sceneObjects.filterIsInstance<SceneObject.Image>()
        sceneObjects.removeAll(imageObjects)

        // 2. Image??? ??????, ????????? Content??? ????????? Image ??? ?????????.
        layoutTemplate.sceneObjects.forEachIndexed { index, templateSceneObject ->
            when (templateSceneObject) {
                is TemplateSceneObject.Image -> {
                    SceneObject.Image().copyFromTemplate(index, templateSceneObject).apply {
                        imageList.poll()?.let {
                            this.insertImage(it)
                        }
                    }.also {
                        sceneObjects.add(it)
                    }
                }
                is TemplateSceneObject.Text.Spine -> {
                    SceneObject.Text.Spine().copyFromTemplate(index, templateSceneObject)
                        .also {
                            sceneObjects.add(it)
                        }
                }
                is TemplateSceneObject.Text.User -> {
                    SceneObject.Text.User().copyFromTemplate(index, templateSceneObject)
                        .also {
                            sceneObjects.add(it)
                        }
                }
                else -> {
                }
            }
        }
    }

    sealed class Type(val raw: String) {
        data class Cover(
            val isSoft: Boolean = true,
            val isLeather: Boolean = false
        ) : Type("cover")

        object Page : Type("page")
    }

    sealed class SubType(val raw: String) {
        object Hard : SubType("hard")
        object Spread : SubType("spread")
        object Page : SubType("page")
        object Blank : SubType("blank")
    }
}