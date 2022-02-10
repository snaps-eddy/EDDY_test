package com.snaps.mobile.data

import android.graphics.Matrix
import android.graphics.RectF
import com.snaps.common.utils.constant.Config
import com.snaps.common.utils.log.Dlog
import com.snaps.mobile.data.save.SaveInfoToJson
import com.snaps.mobile.data.save.SaveToJson
import com.snaps.mobile.data.save.SceneObjectToJson
import com.snaps.mobile.data.save.SceneToJson
import com.snaps.mobile.data.template.TemplateDto
import com.snaps.mobile.data.template.TemplateSceneDto
import com.snaps.mobile.data.template.TemplateSceneObjectDto
import com.snaps.mobile.domain.asset.AnalysisInfo
import com.snaps.mobile.domain.generateDrawIndex
import com.snaps.mobile.domain.save.*
import com.snaps.mobile.domain.template.Template
import com.snaps.mobile.domain.template.TemplateScene
import com.snaps.mobile.domain.template.TemplateSceneObject
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.absoluteValue

@Singleton
class SceneMapper @Inject constructor() {
    private lateinit var saveInfo: SaveInfo

    /**
     * SaveJson -> Save
     */
    fun mapToModel(dto: SaveToJson): Save {
        saveInfo = mapToSaveInfo(dto.info)
        return Save(
            scenes = dto.scene.mapIndexed(::mapToScene).toMutableList(),
            info = mapToSaveInfo(dto.info)
        )
    }

    private fun mapToScene(index: Int, dto: SceneToJson): Scene {
        return Scene(
            drawIndex = index.generateDrawIndex(),
            width = dto.width,
            height = dto.height,
            type = mapToType(dto.type),
            subType = mapToSubType(dto.subType),
            printCount = dto.printCount,
            templateCode = dto.templateCode,
            hiddenIdx = dto.hiddenIdx,
            side = dto.side,
            layoutCode = dto.layoutCode,
            layoutType = dto.layoutType,
            initialMillimeterWidth = dto.initialMillimeterWidth,
            initialMillimeterHeight = dto.initialMillimeterHeight,
            year = dto.year,
            month = dto.month,
            midWidth = dto.midWidth,
            sceneObjects = dto.sceneObjects.mapIndexed(::mapToObject).toMutableList(),
            defaultTemplateCode = dto.defaultInfo.templateCode,
            defaultLayoutCode = dto.defaultInfo.layoutCode,
            defaultStickerIdList = dto.defaultInfo.stickerIdList,
            defaultBackgroundId = dto.defaultInfo.backgroundId,
        )
    }

    private fun mapToObject(index: Int, dto: SceneObjectToJson): SceneObject {
        return when (dto.type) {
            "background" -> {
                when (dto.subType) {
                    "color" -> {
                        val sss = SceneObject.Background.Color(
                            drawIndex = index.generateDrawIndex(),
                            x = dto.x,
                            y = dto.y,
                            width = dto.width,
                            height = dto.height,
                            angle = dto.angle,
                            type = dto.type,
                            subType = dto.subType,
                            name = dto.name,
                            source = dto.source,
//                            usedType = dto.usedType,
                            alpha = dto.alpha,
                            overPrint = dto.overPrint,
                            whitePrint = dto.whitePrint,
                            textileColor = dto.textileColor,
                            fillColor = dto.fillColor,
                            isBigFile = dto.isBigFile,
                            fixedSize = dto.fixedSize,
                            readOnly = dto.readOnly,
                            bgColor = dto.bgColor ?: throw IllegalStateException("Scene Object -> Background need Bg Color")
                        )
                        sss
                    }
                    "image" -> {
                        SceneObject.Background.Image(
                            drawIndex = index.generateDrawIndex(),
                            x = dto.x,
                            y = dto.y,
                            width = dto.width,
                            height = dto.height,
                            angle = dto.angle,
                            type = dto.type,
                            subType = dto.subType,
                            name = dto.name ?: "",
                            source = dto.source,
//                            usedType = dto.usedType,
                            alpha = dto.alpha,
                            overPrint = dto.overPrint,
                            whitePrint = dto.whitePrint,
                            textileColor = dto.textileColor,
                            fillColor = dto.fillColor,
                            isBigFile = dto.isBigFile,
                            fixedSize = dto.fixedSize,
                            readOnly = dto.readOnly,
                            resourceId = dto.resourceId,
                            middleImagePath = dto.middleImagePath,
                        )
                    }
                    else -> throw IllegalStateException("${dto.subType} is Not defiend ${dto.type}} ")
                }
            }
            "image" -> {
                SceneObject.Image(
                    drawIndex = index.generateDrawIndex(),
                    x = dto.x,
                    y = dto.y,
                    width = dto.width,
                    height = dto.height,
                    angle = dto.angle,
                    type = dto.type,
                    subType = dto.subType,
                    name = dto.name,
                    source = dto.source,
//                    usedType = dto.usedType,
                    alpha = dto.alpha,
                    overPrint = dto.overPrint,
                    whitePrint = dto.whitePrint,
                    textileColor = dto.textileColor,
                    fillColor = dto.fillColor,
                    isBigFile = dto.isBigFile,
                    fixedSize = dto.fixedSize,
                    readOnly = dto.readOnly,
                    content = dto.original?.let {
                        if (it.middleImagePath.isNullOrBlank()) {
                            null
                        } else {
                            ImageContent(
                                imgSeq = dto.original?.getImgSeq() ?: "",
                                year = dto.original?.getYear() ?: "",
                                middleImagePath = dto.original?.middleImagePath ?: "",
                                width = dto.original?.width ?: 0f,
                                height = dto.original?.height ?: 0f,
                                analysisInfo = createAnalysisInfoFromText(dto.analysis?.fd),
                                orientation = dto.original?.orientation?.toInt() ?: 0,
                                date = dto.original?.date ?: ""
                            )
                        }
                    },
                    innerImage = dto.innerImage?.let {
                        //버전 체크 땜방
                        if (saveInfo.isAndroid() && (saveInfo.getAppVersion() == "3.5.75" || saveInfo.getAppVersion() == "3.5.76")) {
                            InnerImage(
                                x = it.x, //it.x
                                y = it.y, // it.y
                                width = it.width,
                                height = it.height,
                                angle = it.angle,
                                alpha = it.alpha
                            )
                        } else {
                            if (it.angle.absoluteValue == 90 || it.angle.absoluteValue == 270) {
                                val originRect = RectF(it.x, it.y, it.x + it.width, it.y + it.height)
                                val originRotateRect = convertRenderImagePosition(originRect, it.angle.toFloat())
                                InnerImage(
                                    x = originRotateRect.left, //it.x
                                    y = originRotateRect.top, // it.y
                                    width = it.width,
                                    height = it.height,
                                    angle = it.angle,
                                    alpha = it.alpha
                                )
                            } else {
                                InnerImage(
                                    x = it.x, //it.x
                                    y = it.y, // it.y
                                    width = it.width,
                                    height = it.height,
                                    angle = it.angle,
                                    alpha = it.alpha
                                )
                            }
                        }
                    },
                    filter = dto.filter?.let {
                        Filter.fromCode(it.code)
                    } ?: Filter.None(),
                    border = dto.border?.let {
                        Border(
                            imageId = it.imageId,
                            imagePath = it.imagePath,
                            maskId = it.maskId,
                            maskPath = it.maskPath,
                            singleAlpha = it.singleAlpha,
                            singleColor = it.singleColor,
                            singleThickness = it.singleThickness,
                            imageOffset = it.imageOffset,
                            type = it.type
                        )
                    }
                )
            }
            "text" -> {
                when (dto.subType) {
                    "spine" -> {
                        SceneObject.Text.Spine(
                            drawIndex = index.generateDrawIndex(),
                            x = dto.x,
                            y = dto.y,
                            width = dto.width,
                            height = dto.height,
                            angle = dto.angle,
                            type = dto.type,
                            subType = dto.subType,
                            name = dto.name ?: "",
                            source = dto.source,
//                            usedType = dto.usedType,
                            alpha = dto.alpha,
                            overPrint = dto.overPrint,
                            whitePrint = dto.whitePrint,
                            textileColor = dto.textileColor,
                            fillColor = dto.fillColor,
                            isBigFile = dto.isBigFile,
                            fixedSize = dto.fixedSize,
                            readOnly = dto.readOnly,
//                            textDrawableHeight = dto.textDrawableHeight ?: "",
                            wordWrap = dto.wordWrap ?: false,
                            placeholder = dto.placeholder ?: "",
                            defaultText = dto.defaultText ?: "",
                            defaultStyle = parseDefaultStyle(dto.defaultStyle),
                            text = dto.textContent ?: "",
                        )
                    }
                    "" -> {
                        SceneObject.Text.User(
                            drawIndex = index.generateDrawIndex(),
                            x = dto.x,
                            y = dto.y,
                            width = dto.width,
                            height = dto.height,
                            angle = dto.angle,
                            type = dto.type,
                            subType = dto.subType,
                            name = dto.name ?: "",
                            source = dto.source,
//                            usedType = dto.usedType,
                            alpha = dto.alpha,
                            overPrint = dto.overPrint,
                            whitePrint = dto.whitePrint,
                            textileColor = dto.textileColor,
                            fillColor = dto.fillColor,
                            isBigFile = dto.isBigFile,
                            fixedSize = dto.fixedSize,
                            readOnly = dto.readOnly,
//                            textDrawableHeight = dto.textDrawableHeight ?: "",
                            wordWrap = dto.wordWrap ?: false,
                            placeholder = dto.placeholder ?: "",
                            defaultText = dto.defaultText ?: "",
                            defaultStyle = parseDefaultStyle(dto.defaultStyle),
                            text = dto.textContent ?: "",
                        )
                    }
                    else -> throw IllegalStateException("${dto.subType} is Not defiend ${dto.type}} ")
                }
            }
            "sticker" -> {
                SceneObject.Sticker(
                    drawIndex = index.generateDrawIndex(),
                    x = dto.x,
                    y = dto.y,
                    width = dto.width,
                    height = dto.height,
                    angle = dto.angle,
                    type = dto.type,
                    subType = dto.subType,
                    name = dto.name ?: "",
                    source = dto.source,
//                    usedType = dto.usedType,
                    alpha = dto.alpha,
                    overPrint = dto.overPrint,
                    whitePrint = dto.whitePrint,
                    textileColor = dto.textileColor,
                    fillColor = dto.fillColor,
                    isBigFile = dto.isBigFile,
                    fixedSize = dto.fixedSize,
                    readOnly = dto.readOnly,
                    resourceId = dto.resourceId,
                    middleImagePath = dto.middleImagePath,
                )
            }
            else -> throw IllegalStateException("Not defined scene object type : ${dto.type}")
        }
    }

    private fun createAnalysisInfoFromText(text: String?): AnalysisInfo {
        if (text.isNullOrBlank()) {
            return AnalysisInfo()
        }
        //TODO 문제가 있는 코드인데 일단 넣자
        try {
            val data = text.trim().split(",")
            val fd = AnalysisInfo.Img.FdThum(
                x = data[0].toFloat(),
                y = data[1].toFloat(),
                w = data[2].toFloat(),
                h = data[3].toFloat(),
                xw = data[0].toFloat() + data[2].toFloat(),
                yh = data[1].toFloat() + data[3].toFloat(),
                fn = if (data.size == 7) data[6].toInt() else 0
            )
            val meta = AnalysisInfo.Img.Meta(
                tw = data[4].toFloat(),
                th = data[5].toFloat(),
            )
            return AnalysisInfo(AnalysisInfo.Img(fd, meta))
        } catch (e: Exception) {
            return AnalysisInfo()
        }
    }

    private fun mapToSaveInfo(dto: SaveInfoToJson): SaveInfo {
        return SaveInfo(
            locationSearch = dto.locationSearch,
            userAgent = dto.userAgent,
            regDate = dto.regDate,
            saveCount = dto.saveCount
        )
    }

    /**
     * TemplateDto -> Template
     */
    fun mapToModel(dto: TemplateDto): Template {
        return Template(
            dto.scenes.map { sceneTemplateDto ->
                mapToModel(sceneTemplateDto)
            }.toMutableList()
        )
    }

    fun mapToModel(dto: TemplateSceneDto): TemplateScene {
        return TemplateScene(
            type = mapToType(dto.type),
            subType = mapToSubType(dto.subType), // Default 값을 뭘 할지는 나중에 정하자.
            width = dto.width,
            height = dto.height,
            templateCode = dto.templateCode,
            printCount = dto.printCount,
            hiddenIdx = dto.hiddenIdx,
            side = dto.side,
            layoutCode = dto.layoutCode,
            layoutType = dto.layoutType,
            initialMillimeterWidth = dto.initialMillimeterWidth,
            initialMillimeterHeight = dto.initialMillimeterHeight,
            year = dto.year,
            month = dto.month,
            sceneObjects = dto.sceneObjects.map(::mapToObject).toMutableList(),
            defaultTemplateCode = dto.defaultInfo.templateCode,
            defaultLayoutCode = dto.defaultInfo.layoutCode,
            defaultStickerIdList = dto.defaultInfo.stickerIdList,
            defaultBackgroundId = dto.defaultInfo.backgroundId,
        )
    }


    private fun mapToType(raw: String): Scene.Type {
        return when (raw) {
            "cover" -> Scene.Type.Cover()
            "page" -> Scene.Type.Page
            else -> throw IllegalArgumentException("Not defined type : $raw")
        }
    }

    private fun mapToSubType(raw: String): Scene.SubType {
        return when (raw) {
            "hard" -> Scene.SubType.Hard
            "spread" -> Scene.SubType.Spread
            "page" -> Scene.SubType.Page
            "blank" -> Scene.SubType.Blank
            "" -> Scene.SubType.Spread // 없을 경우 뭘 넣어야 할지..
            else -> throw IllegalArgumentException("Not defined sub type : $raw")
        }
    }

    private fun mapToObject(dto: TemplateSceneObjectDto): TemplateSceneObject {
        return when (dto.type) {
            "background" -> {
                when (dto.subType) {
                    "color" -> {
                        TemplateSceneObject.Background.Color(
                            x = dto.x,
                            y = dto.y,
                            width = dto.width,
                            height = dto.height,
                            angle = dto.angle,
                            type = dto.type,
                            subType = dto.subType,
                            name = dto.name ?: "",
                            source = dto.source,
//                            usedType = dto.usedType,
                            alpha = dto.alpha,
                            overPrint = dto.overPrint,
                            whitePrint = dto.whitePrint,
                            textileColor = dto.textileColor,
                            fillColor = dto.fillColor,
                            isBigFile = dto.isBigFile,
                            fixedSize = dto.fixedSize,
                            readOnly = dto.readOnly,
                            bgColor = dto.bgColor ?: throw IllegalStateException("Scene Object -> Background need Bg Color")
                        )
                    }
                    "image" -> {
                        TemplateSceneObject.Background.Image(
                            x = dto.x,
                            y = dto.y,
                            width = dto.width,
                            height = dto.height,
                            angle = dto.angle,
                            type = dto.type,
                            subType = dto.subType,
                            name = dto.name ?: "",
                            source = dto.source,
//                            usedType = dto.usedType,
                            alpha = dto.alpha,
                            overPrint = dto.overPrint,
                            whitePrint = dto.whitePrint,
                            textileColor = dto.textileColor,
                            fillColor = dto.fillColor,
                            isBigFile = dto.isBigFile,
                            fixedSize = dto.fixedSize,
                            readOnly = dto.readOnly,
                            resourceId = dto.resourceId ?: throw IllegalStateException("Scene Object -> Background need resourceId"),
                            middleImagePath = dto.middleImagePath ?: throw IllegalStateException("Scene Object -> Background need middleImagePath")
                        )
                    }
                    else -> throw IllegalStateException("${dto.subType} is Not defiend ${dto.type}} ")
                }
            }
            "image" -> {
                TemplateSceneObject.Image(
                    x = dto.x,
                    y = dto.y,
                    width = dto.width,
                    height = dto.height,
                    angle = dto.angle,
                    type = dto.type,
                    subType = dto.subType,
                    name = dto.name ?: "",
                    source = dto.source,
//                    usedType = dto.usedType,
                    alpha = dto.alpha,
                    overPrint = dto.overPrint,
                    whitePrint = dto.whitePrint,
                    textileColor = dto.textileColor,
                    fillColor = dto.fillColor,
                    isBigFile = dto.isBigFile,
                    fixedSize = dto.fixedSize,
                    readOnly = dto.readOnly,
                    original = TemplateSceneObject.Image.Original(
                        imgSeq = dto.original?.imgSeq ?: "",
                        year = dto.original?.year ?: "",
                        middleImagePath = dto.original?.middleImagePath ?: "",
                        width = dto.original?.width ?: 0f,
                        height = dto.original?.height ?: 0f,
                        analysisInfo = dto.analysis?.fd ?: "",
//                        orientation = dto.original?.orientation?.toInt() ?: 0, // orientation 이 "" 인 경우가 있나?
                        orientation = if (dto.original?.orientation.isNullOrBlank()) 0 else dto.original?.orientation?.toInt() ?: 0,
                        date = dto.original?.date ?: ""
                    ),
                    innerImage = dto.innerImage?.let {
                        TemplateSceneObject.Image.InnerImage(
                            x = it.x,
                            y = it.y,
                            width = it.width,
                            height = it.height,
                            angle = it.angle,
                            alpha = it.alpha
                        )
                    },
                    filter = dto.filter?.let {
                        Filter.fromCode(it.code)
                    } ?: Filter.None(),
                    border = dto.border?.let {
                        TemplateSceneObject.Image.Border(
                            imageId = it.imageId ?: "",
                            imagePath = it.imagePath ?: "",
                            maskId = it.maskId ?: "",
                            maskPath = it.maskPath ?: "",
                            singleAlpha = it.singleAlpha,
                            singleColor = it.singleColor ?: "",
                            singleThickness = it.singleThickness,
                            imageOffset = it.imageOffset ?: "",
                            type = it.type ?: "",
                        )
                    }
                )
            }
            "text" -> {
                when (dto.subType) {
                    "spine" -> {
                        TemplateSceneObject.Text.Spine(
                            x = dto.x,
                            y = dto.y,
                            width = dto.width,
                            height = dto.height,
                            angle = dto.angle,
                            type = dto.type,
                            subType = dto.subType,
                            name = dto.name ?: "",
                            source = dto.source,
//                            usedType = dto.usedType,
                            alpha = dto.alpha,
                            overPrint = dto.overPrint,
                            whitePrint = dto.whitePrint,
                            textileColor = dto.textileColor,
                            fillColor = dto.fillColor,
                            isBigFile = dto.isBigFile,
                            fixedSize = dto.fixedSize,
                            readOnly = dto.readOnly,
//                            textDrawableHeight = dto.textDrawableHeight ?: "",
                            wordWrap = dto.wordWrap ?: false,
                            placeholder = dto.placeholder ?: "",
                            defaultText = dto.defaultText ?: "",
                            defaultStyle = parseDefaultStyle(dto.defaultStyle),
                            text = dto.userText ?: ""
                        )
                    }
                    "text", "" -> {
                        TemplateSceneObject.Text.User(
                            x = dto.x,
                            y = dto.y,
                            width = dto.width,
                            height = dto.height,
                            angle = dto.angle,
                            type = dto.type,
                            subType = dto.subType,
                            name = dto.name ?: "",
                            source = dto.source,
//                            usedType = dto.usedType,
                            alpha = dto.alpha,
                            overPrint = dto.overPrint,
                            whitePrint = dto.whitePrint,
                            textileColor = dto.textileColor,
                            fillColor = dto.fillColor,
                            isBigFile = dto.isBigFile,
                            fixedSize = dto.fixedSize,
                            readOnly = dto.readOnly,
//                            textDrawableHeight = dto.textDrawableHeight ?: "",
                            wordWrap = dto.wordWrap ?: false,
                            placeholder = dto.placeholder ?: "",
                            defaultText = dto.defaultText ?: "",
                            defaultStyle = parseDefaultStyle(dto.defaultStyle),
                            text = dto.userText ?: ""
                        )
                    }
                    else -> throw IllegalStateException("${dto.subType} is Not defiend ${dto.type}} ")
                }
            }
            "sticker" -> {
                TemplateSceneObject.Sticker(
                    x = dto.x,
                    y = dto.y,
                    width = dto.width,
                    height = dto.height,
                    angle = dto.angle,
                    type = dto.type,
                    subType = dto.subType,
                    name = dto.name ?: "",
                    source = dto.source,
//                    usedType = dto.usedType,
                    alpha = dto.alpha,
                    overPrint = dto.overPrint,
                    whitePrint = dto.whitePrint,
                    textileColor = dto.textileColor,
                    fillColor = dto.fillColor,
                    isBigFile = dto.isBigFile,
                    fixedSize = dto.fixedSize,
                    readOnly = dto.readOnly,
                    resourceId = dto.resourceId ?: throw IllegalStateException("Scene Object -> Sticker need resourceId"),
                    middleImagePath = dto.middleImagePath ?: throw IllegalStateException("Scene Object -> Sticker need middleImagePath")
                )
            }
            else -> throw IllegalStateException("Not defined scene object type : ${dto.type}")
        }
    }

    /**
     * Save -> SaveToJson
     */
    fun mapToJson(save: Save): SaveToJson {
        return SaveToJson(
            info = mapToSaveInfoJson(save.info),
            scene = save.scenes.map(::mapToSceneJson)
        )
    }

    private fun mapToSaveInfoJson(saveInfo: SaveInfo): SaveInfoToJson {
        return SaveInfoToJson(
            locationSearch = saveInfo.locationSearch,
            userAgent = saveInfo.userAgent,
            regDate = saveInfo.regDate,
            saveCount = saveInfo.saveCount
        )
    }

    private fun mapToSceneJson(scene: Scene): SceneToJson {
        return SceneToJson(
            type = scene.type.raw,
            subType = scene.subType.raw,
            side = scene.side ?: "",
            width = scene.width,
            height = scene.height,
            templateCode = scene.templateCode,
            layoutCode = scene.layoutCode,
            layoutType = scene.layoutType,
            hiddenIdx = scene.hiddenIdx,
            printCount = scene.printCount,
            initialMillimeterWidth = scene.initialMillimeterWidth,
            initialMillimeterHeight = scene.initialMillimeterHeight,
            year = scene.year,
            month = scene.month,
            midWidth = scene.midWidth,
//            formStyle = listOf(),
            sceneObjects = scene.sceneObjects.map(::mapToSceneObjectJson),
            defaultInfo = SceneToJson.DefaultInfo(
                templateCode = scene.defaultTemplateCode,
                layoutCode = scene.defaultLayoutCode,
                stickerIdList = scene.defaultStickerIdList,
                backgroundId = scene.defaultBackgroundId,
            ),
        )
    }

    private fun mapToSceneObjectJson(sceneObject: SceneObject): SceneObjectToJson {
        val baseSceneObject = SceneObjectToJson(
            type = sceneObject.type,
            subType = sceneObject.subType,
            name = sceneObject.name,
            source = sceneObject.source,
//            usedType = sceneObject.usedType,
            x = sceneObject.x,
            y = sceneObject.y,
            width = sceneObject.width,
            height = sceneObject.height,
            angle = sceneObject.angle,
            alpha = sceneObject.alpha,
            overPrint = sceneObject.overPrint,
            whitePrint = sceneObject.whitePrint,
            textileColor = sceneObject.textileColor,
            fillColor = sceneObject.fillColor,
            isBigFile = sceneObject.isBigFile,
            fixedSize = sceneObject.fixedSize,
            readOnly = sceneObject.readOnly,
        )

        /**
         * For Background
         */

        when (sceneObject) {
            is SceneObject.Background.Color ->
                baseSceneObject.apply {
                    resourceId = ""
                    middleImagePath = ""
                    imageSequence = ""
                    bgColor = sceneObject.bgColor
                }
            is SceneObject.Background.Image -> baseSceneObject.apply {
                resourceId = sceneObject.resourceId
                middleImagePath = sceneObject.middleImagePath
                imageSequence = ""
                bgColor = ""
            }
            is SceneObject.Image -> baseSceneObject.apply {
                border = sceneObject.border?.run {
                    SceneObjectToJson.BorderToJson(
                        type = this.type,
                        singleColor = this.singleColor,
                        singleThickness = this.singleThickness,
                        singleAlpha = this.singleAlpha,
                        imageId = this.imageId,
                        imagePath = this.imagePath,
                        imageOffset = this.imageOffset,
                        maskId = this.maskId,
                        maskPath = this.maskPath
                    )
                }
                innerImage = sceneObject.innerImage?.run {
                    if (this.angle.absoluteValue == 90 || this.angle.absoluteValue == 270) {
                        val originRect = RectF(this.x, this.y, this.x + this.height, this.y + this.width)
                        val originRotateRect = convertRenderImagePosition(originRect, -this.angle.toFloat())
                        SceneObjectToJson.InnerImageToJson(
                            x = originRotateRect.left,
                            y = originRotateRect.top,
                            width = this.width,
                            height = this.height,
                            angle = this.angle,
                            alpha = this.alpha,
                        )
                    } else {
                        SceneObjectToJson.InnerImageToJson(
                            x = this.x,
                            y = this.y,
                            width = this.width,
                            height = this.height,
                            angle = this.angle,
                            alpha = this.alpha,
                        )
                    }
                }
                filter = sceneObject.filter?.run {
                    SceneObjectToJson.FilterToJson(
                        code = this.code,
                        name = this.name,
                    )
                }
                original = sceneObject.content?.run {
                    SceneObjectToJson.OriginalToJson(
                        width = this.width,
                        height = this.height,
                    ).also {
                        it.resourceId = "" //todo
                        it.imageSequence = this.outputImageSequence
                        it.middleImagePath = this.middleImagePath
                        it.orientation = this.orientation.toString()
                        it.date = this.date
                    }
                }

                analysis = sceneObject.content?.analysisInfo?.run {
                    SceneObjectToJson.AnalysisToJson(
                        fd = this.outputRaw
                    )
                }
            }
            is SceneObject.Sticker -> baseSceneObject.apply {
                resourceId = sceneObject.resourceId
                middleImagePath = sceneObject.middleImagePath
                imageSequence = "" //todo
                original = SceneObjectToJson.OriginalToJson(
                    width = 0f, //todo
                    height = 0f, //todo
                )
            }
            is SceneObject.Text.Spine -> baseSceneObject.apply {
//                textDrawableHeight = sceneObject.textDrawableHeight
                wordWrap = sceneObject.wordWrap
                placeholder = sceneObject.placeholder
                defaultText = sceneObject.defaultText
                defaultStyle = sceneObject.defaultStyle.toRawText()
                textContent = sceneObject.text

            }
            is SceneObject.Text.User -> baseSceneObject.apply {
//                textDrawableHeight = sceneObject.textDrawableHeight
                wordWrap = sceneObject.wordWrap
                placeholder = sceneObject.placeholder
                defaultText = sceneObject.defaultText
                defaultStyle = sceneObject.defaultStyle.toRawText()
                textContent = sceneObject.text
            }
        }

        return baseSceneObject
    }

    private fun parseDefaultStyle(rawString: String?): DefaultStyle {
        if (rawString.isNullOrBlank()) {
            return DefaultStyle()
        }

        var fontFamily = ""
        var fontSize = ""
        var color = ""
        var textAlign: TextAlign = TextAlign.Left
        var fontStyle = "none"
        var textDecoration = "none"

        rawString
            .split(";")
            .filter { it.isNotBlank() }
            .map { it.split(":") }
            .forEach {
                if (it.size == 2) {
                    when (it[0]) {
                        "font-family" -> {
                            fontFamily = it[1]
                        }
                        "font-size" -> {
                            fontSize = it[1]
                        }
                        "color" -> {
                            color = it[1]
                        }
                        "text-align" -> {
                            textAlign = when (it[1]) {
                                "left" -> TextAlign.Left
                                "center" -> TextAlign.Center
                                "right" -> TextAlign.Right
                                else -> TextAlign.Left
                            }
                        }
                        "font-style" -> {
                            fontStyle = it[1]
                        }
                        "text-decoration" -> {
                            textDecoration = it[1]
                        }
                    }
                }
            }

        return DefaultStyle(
            fontFamily = fontFamily,
            fontSize = fontSize,
            color = color,
            textAlign = textAlign,
            fontStyle = fontStyle,
            textDecoration = textDecoration
        )
    }

    private fun convertInnerImagePostionRenderToAndroid(dto: SceneObjectToJson): InnerImage? {
        //버전 체크 땜방
        if (saveInfo.isAndroid() && (saveInfo.getAppVersion() == "3.5.75" || saveInfo.getAppVersion() == "3.5.76")) {
            return dto.innerImage?.let {
                InnerImage(
                    x = it.x, //it.x
                    y = it.y, // it.y
                    width = it.width,
                    height = it.height,
                    angle = it.angle,
                    alpha = it.alpha
                )
            }
        }

        return dto.innerImage?.let {
            if (it.angle.absoluteValue == 90 || it.angle.absoluteValue == 270) {
                val originRect = RectF(it.x, it.y, it.x + it.width, it.y + it.height)
                val originRotateRect = convertRenderImagePosition(originRect, it.angle.toFloat())

                //값 보정
                val fixX = if (originRotateRect.left >= 1) 0f else originRotateRect.left
                val fixY = if (originRotateRect.top >= 1) 0f else originRotateRect.top
                val fixWidth = if (it.width - fixX < dto.height) dto.height else it.width  //주의! 회전된 경우와 비교
                val fixHeight = if (it.height - fixY < dto.width) dto.width else it.height //주의! 회전된 경우와 비교
                if (Config.isDevelopVersion()) {
                    if (fixX != originRotateRect.left) Dlog.e("fixX != originRotateRect.left -->  $fixX != ${originRotateRect.left}")
                    if (fixY != originRotateRect.top) Dlog.e("fixY != originRotateRect.top -->  $fixY != ${originRotateRect.top}")
                    if (fixWidth != it.width) Dlog.e("fixWidth != it.width -->  $fixWidth != ${it.width}")
                    if (fixHeight != it.height) Dlog.e("fixHeight != it.height -->  $fixHeight != ${it.height}")
                }

                InnerImage(
                    x = fixX,
                    y = fixY,
                    width = fixWidth,
                    height = fixHeight,
                    angle = it.angle,
                    alpha = it.alpha
                )
            } else {
                InnerImage(
                    x = it.x, //it.x
                    y = it.y, // it.y
                    width = it.width,
                    height = it.height,
                    angle = it.angle,
                    alpha = it.alpha
                )
            }
        }
    }

    private fun convertInnerImagePostionAndroidToRender(sceneObject: SceneObject.Image): SceneObjectToJson.InnerImageToJson? {
        return sceneObject.innerImage?.run {
            if (this.angle.absoluteValue == 90 || this.angle.absoluteValue == 270) {
                val originRect = RectF(this.x, this.y, this.x + this.width, this.y + this.height)
                val originRotateRect = convertRenderImagePosition(originRect, -this.angle.toFloat())

                //값 보정
                val fixX = if (originRotateRect.left >= 1) 0f else originRotateRect.left
                val fixY = if (originRotateRect.top >= 1) 0f else originRotateRect.top
                val fixWidth = if (width - fixX < sceneObject.height) sceneObject.height else width  //주의! 회전된 경우와 비교
                val fixHeight = if (height - fixY < sceneObject.width) sceneObject.width else height //주의! 회전된 경우와 비교
                if (Config.isDevelopVersion()) {
                    if (fixX != originRotateRect.left) Dlog.e("fixX != originRotateRect.left -->  $fixX != ${originRotateRect.left}")
                    if (fixY != originRotateRect.top) Dlog.e("fixY != originRotateRect.top -->  $fixY != ${originRotateRect.top}")
                    if (fixWidth != width) Dlog.e("fixWidth != it.width -->  $fixWidth != $width")
                    if (fixHeight != height) Dlog.e("fixHeight != it.height -->  $fixHeight != $height")
                }

                SceneObjectToJson.InnerImageToJson(
                    x = fixX,
                    y = fixY,
                    width = fixWidth,
                    height = fixHeight,
                    angle = this.angle,
                    alpha = this.alpha,
                )
            } else {
                SceneObjectToJson.InnerImageToJson(
                    x = this.x,
                    y = this.y,
                    width = this.width,
                    height = this.height,
                    angle = this.angle,
                    alpha = this.alpha,
                )
            }
        }
    }

    private fun convertRenderImagePosition(originRect: RectF, angle: Float): RectF {
        return RectF(originRect).also {
            Matrix().apply {
                setRotate(angle, it.centerX(), it.centerY())
                mapRect(it)
            }
        }
    }
}