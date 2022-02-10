package com.snaps.mobile.domain.save

import android.util.SizeF
import com.snaps.mobile.domain.asset.AnalysisInfo
import com.snaps.mobile.domain.generateDrawIndex
import com.snaps.mobile.domain.project.ImageThumbnail
import com.snaps.mobile.domain.template.TemplateSceneObject

sealed class SceneObject {

    abstract var drawIndex: String
    abstract var x: Float
    abstract var y: Float
    abstract var width: Float
    abstract var height: Float
    abstract var angle: Int
    abstract var type: String
    abstract var subType: String
    abstract var name: String
    abstract var source: String
//    abstract var usedType: String
    abstract var alpha: Float
    abstract var overPrint: Boolean
    abstract var whitePrint: Boolean
    abstract var textileColor: String
    abstract var fillColor: String
    abstract var isBigFile: Boolean
    abstract var fixedSize: Boolean
    abstract var readOnly: Boolean

    data class Image(
        override var drawIndex: String,
        override var x: Float,
        override var y: Float,
        override var width: Float,
        override var height: Float,
        override var angle: Int,
        override var type: String,
        override var subType: String,
        override var name: String,
        override var source: String,
//        override var usedType: String,
        override var alpha: Float,
        override var overPrint: Boolean,
        override var whitePrint: Boolean,
        override var textileColor: String,
        override var fillColor: String,
        override var isBigFile: Boolean,
        override var fixedSize: Boolean,
        override var readOnly: Boolean,
        var content: ImageContent?,
        var innerImage: InnerImage?,
        var filter: Filter?,
        var border: Border?
    ) : SceneObject() {

        constructor() : this(
            drawIndex = 0.generateDrawIndex(),
            x = 0f,
            y = 0f,
            width = 0f,
            height = 0f,
            angle = 0,
            type = "0",
            subType = "0",
            name = "0",
            source = "0",
//            usedType = "",
            alpha = 0.0f,
            overPrint = false,
            whitePrint = false,
            textileColor = "",
            fillColor = "",
            isBigFile = false,
            fixedSize = false,
            readOnly = false,
            content = null,
            innerImage = null,
            filter = null,
            border = null
        )

        fun copyFromTemplate(index: Int, templateSceneObject: TemplateSceneObject.Image): Image {
            drawIndex = index.generateDrawIndex()
            return changeTemplate(templateSceneObject)
        }

        /**
         * filter 와 image content 정보만 유지하고
         * 새로운 Template 정보로 변경하는 로직.
         * 사용자 편집정보도 초기화된다.
         */
        fun changeMeasurement(new: TemplateSceneObject.Image) {
            x = new.x
            y = new.y
            width = new.width
            height = new.height
            angle = new.angle
            type = new.type
            subType = new.subType
            name = new.name
            source = new.source
//            usedType = new.usedType
            alpha = new.alpha
            overPrint = new.overPrint
            whitePrint = new.whitePrint
            textileColor = new.textileColor
            fillColor = new.fillColor
            isBigFile = new.isBigFile
            fixedSize = new.fixedSize
            readOnly = new.readOnly
            // content , filter 유지
            measurePaperFullImageSize()
                .run {
                    InnerImage(
                        x = this.x,
                        y = this.y,
                        width = this.width,
                        height = this.height
                    )
                }.also {
                    this.innerImage = it
                }
        }

        fun changeTemplate(templateSceneObject: TemplateSceneObject.Image): Image {
            x = templateSceneObject.x
            y = templateSceneObject.y
            width = templateSceneObject.width
            height = templateSceneObject.height
            angle = templateSceneObject.angle
            type = templateSceneObject.type
            subType = templateSceneObject.subType
            name = templateSceneObject.name
            source = templateSceneObject.source
//            usedType = templateSceneObject.usedType
            alpha = templateSceneObject.alpha
            overPrint = templateSceneObject.overPrint
            whitePrint = templateSceneObject.whitePrint
            textileColor = templateSceneObject.textileColor
            fillColor = templateSceneObject.fillColor
            isBigFile = templateSceneObject.isBigFile
            fixedSize = templateSceneObject.fixedSize
            readOnly = templateSceneObject.readOnly
            content = templateSceneObject.original?.let {
                if (templateSceneObject.original.middleImagePath.isEmpty()) {
                    null
                } else {
                    ImageContent(
                        imgSeq = it.imgSeq,
                        year = it.year,
                        middleImagePath = it.middleImagePath,
                        width = it.width,
                        height = it.height,
                        analysisInfo = AnalysisInfo(
                            AnalysisInfo.Img(
                                AnalysisInfo.Img.FdThum(),
                                AnalysisInfo.Img.Meta()
                            )
                        ),
                        orientation = it.orientation,
                        date = it.date,
                    )
                }
            }
            innerImage = templateSceneObject.innerImage?.let {
                InnerImage(
                    x = it.x,
                    y = it.y,
                    width = it.width,
                    height = it.height,
                    angle = it.angle,
                    alpha = it.alpha
                )
            }
            filter = templateSceneObject.filter?.let {
                Filter.fromCode(it.code)
            } ?: Filter.None()
            border = templateSceneObject.border?.let {
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
            return this
        }

        fun insertImage(thumbnail: ImageThumbnail) {
            changeImageContent(
                ImageContent(
                    imgSeq = thumbnail.imgSeq,
                    year = thumbnail.year,
                    middleImagePath = thumbnail.thumbnailRemotePath,
                    width = thumbnail.originWidth,
                    height = thumbnail.originHeight,
                    analysisInfo = thumbnail.analysisInfo,
                    orientation = thumbnail.orientation,
                    date = thumbnail.date,
                )
            )
        }

        fun changeImageContent(content: ImageContent) {
            this.content = content
            this.filter = Filter.None() // -> 일단 필터 유지하지 않는다.
            measurePaperFullImageSize().run {
                /**
                 * @Makro
                 * 일단 사진 스왑할 경우, 이전에 Edit 정보는 유지하지 않는다.
                 * 필터나 이런것들만 유지할 수도 있다.
                 */
                InnerImage(
                    x = this.x,
                    y = this.y,
                    width = this.width,
                    height = this.height,
                    angle = content.orientationAngle
                )
            }.also {
                this.innerImage = it
            }
        }

        private fun measurePaperFullImageSize(): XyWidthHeight {
            val content = this.content ?: return XyWidthHeight()
            val orientationAngle = content.orientationAngle
            val contentWidth = if (orientationAngle == 90 || orientationAngle == 270) content.height else content.width
            val contentHeight = if (orientationAngle == 90 || orientationAngle == 270) content.width else content.height

            val tempWidth = contentWidth / width
            val tempHeight = contentHeight / height

            val scaledWidth: Float
            val scaledHeight: Float

            when {
                tempWidth == tempHeight -> {
                    scaledWidth = width
                    scaledHeight = height
                }
                tempWidth > tempHeight -> {
                    scaledWidth = contentWidth * height / contentHeight
                    scaledHeight = height
                }
                else -> {
                    scaledWidth = width
                    scaledHeight = contentHeight * width / contentWidth
                }
            }

            var x = (width - scaledWidth) * 0.5f
            var y = (height - scaledHeight) * 0.5f

            if (content.analysisInfo.hasFdInfo) {
                val fdOffset = getFdOffset(orientationAngle, SizeF(scaledWidth, scaledHeight))
                x += fdOffset.first
                y += fdOffset.second
            }

            val recordWidth = if (orientationAngle == 90 || orientationAngle == 270) scaledHeight else scaledWidth
            val recordHeight = if (orientationAngle == 90 || orientationAngle == 270) scaledWidth else scaledHeight

            return XyWidthHeight(x, y, recordWidth, recordHeight)
        }

        private fun getFdOffset(orientationAngle: Int, imageContentSize: SizeF): Pair<Float, Float> {
            return content?.let { contentInfo ->
                val thumbnailSize = contentInfo.analysisInfo.getCalibratedOrientationThumbnailSize(orientationAngle)
                val ratio = imageContentSize.width / thumbnailSize.width

                val dCenterX = thumbnailSize.width * 0.5f
                val fdCenterX = contentInfo.analysisInfo.searchedAreaRect.centerX()
                val dCenterY = thumbnailSize.height * 0.5f
                val fdCenterY = contentInfo.analysisInfo.searchedAreaRect.centerY()

                val willMoveX = dCenterX.minus(fdCenterX) * ratio
                val willMoveY = dCenterY.minus(fdCenterY) * ratio

                val availableMoveHorizontal = imageContentSize.width.minus(width) * 0.5f - 1
                val availableMoveVertical = imageContentSize.height.minus(height) * 0.5f - 1

                val returnX = when {
                    willMoveX > 0 -> {
                        //fd 좌표가 center 기준 왼쪽에 있다는 뜻
                        if (availableMoveHorizontal > willMoveX) {
                            willMoveX
                        } else {
                            availableMoveHorizontal
                        }
                    }
                    willMoveX < 0 -> {
                        //fd 좌표가 center 기준 오른쪽 있다는 뜻
                        if (willMoveX > -availableMoveHorizontal) {
                            willMoveX
                        } else {
                            -availableMoveHorizontal
                        }

                    }
                    else -> {
                        //fd 좌표가 center와 같다.
                        0f
                    }
                }

                val returnY = when {
                    willMoveY > 0 -> {
                        //fd 좌표가 center 기준 위쪽 있다는 뜻
                        if (availableMoveVertical > willMoveY) {
                            willMoveY
                        } else {
                            availableMoveVertical
                        }
                    }
                    willMoveY < 0 -> {
                        //fd 좌표가 center 기준 아래쪽 있다는 뜻
                        if (willMoveY > -availableMoveVertical) {
                            willMoveY
                        } else {
                            -availableMoveVertical
                        }
                    }
                    else -> {
                        //fd 좌표가 center와 같다.
                        0f
                    }
                }

                when {
                    imageContentSize.width == width -> {
                        Pair(0f, returnY)
                    }
                    imageContentSize.height == height -> {
                        Pair(returnX, 0f)
                    }
                    else -> {
                        Pair(returnX, returnY)
                    }
                }
            } ?: Pair(0f, 0f)
        }
    }

    sealed class Background : SceneObject() {
        data class Color(
            override var drawIndex: String,
            override var x: Float,
            override var y: Float,
            override var width: Float,
            override var height: Float,
            override var angle: Int,
            override var type: String,
            override var subType: String,
            override var name: String,
            override var source: String,
//            override var usedType: String,
            override var alpha: Float,
            override var overPrint: Boolean,
            override var whitePrint: Boolean,
            override var textileColor: String,
            override var fillColor: String,
            override var isBigFile: Boolean,
            override var fixedSize: Boolean,
            override var readOnly: Boolean,
            var bgColor: String
        ) : Background() {

            fun copyFromTemplate(index: Int, templateSceneObject: TemplateSceneObject.Background.Color): Color {
                drawIndex = index.generateDrawIndex()
                return changeTemplate(templateSceneObject)
            }

            fun changeTemplate(templateSceneObject: TemplateSceneObject.Background.Color): Color {
                x = templateSceneObject.x
                y = templateSceneObject.y
                width = templateSceneObject.width
                height = templateSceneObject.height
                angle = templateSceneObject.angle
                type = templateSceneObject.type
                subType = templateSceneObject.subType
                name = templateSceneObject.name
                source = templateSceneObject.source
//                usedType = templateSceneObject.usedType
                alpha = templateSceneObject.alpha
                overPrint = templateSceneObject.overPrint
                whitePrint = templateSceneObject.whitePrint
                textileColor = templateSceneObject.textileColor
                fillColor = templateSceneObject.fillColor
                isBigFile = templateSceneObject.isBigFile
                fixedSize = templateSceneObject.fixedSize
                readOnly = templateSceneObject.readOnly
                bgColor = templateSceneObject.bgColor
                return this
            }

            /**
             * Color 타입의 background를 Image 타입으로 해준다.
             */
            fun convertToImage(resourceId: String, backgroundResourceUri: String): Image {
                return Image(
                    drawIndex = this.drawIndex,
                    x = this.x,
                    y = this.y,
                    width = this.width,
                    height = this.height,
                    angle = this.angle,
                    type = this.type,
                    subType = "image",
                    name = this.name,
                    source = this.source,
//                    usedType = this.usedType,
                    alpha = this.alpha,
                    overPrint = this.overPrint,
                    whitePrint = this.whitePrint,
                    textileColor = this.textileColor,
                    fillColor = this.fillColor,
                    isBigFile = this.isBigFile,
                    fixedSize = this.fixedSize,
                    readOnly = this.readOnly,
                    resourceId = resourceId,
                    middleImagePath = backgroundResourceUri
                )
            }

            constructor() : this(
                drawIndex = 0.generateDrawIndex(),
                x = 0f,
                y = 0f,
                width = 0f,
                height = 0f,
                angle = 0,
                type = "0",
                subType = "0",
                name = "0",
                source = "0",
//                usedType = "",
                alpha = 0.0f,
                overPrint = false,
                whitePrint = false,
                textileColor = "",
                fillColor = "",
                isBigFile = false,
                fixedSize = false,
                readOnly = false,
                bgColor = "0"
            )
        }

        data class Image(
            override var drawIndex: String,
            override var x: Float,
            override var y: Float,
            override var width: Float,
            override var height: Float,
            override var angle: Int,
            override var type: String,
            override var subType: String,
            override var name: String,
            override var source: String,
//            override var usedType: String,
            override var alpha: Float,
            override var overPrint: Boolean,
            override var whitePrint: Boolean,
            override var textileColor: String,
            override var fillColor: String,
            override var isBigFile: Boolean,
            override var fixedSize: Boolean,
            override var readOnly: Boolean,
            var resourceId: String?,
            var middleImagePath: String?,
        ) : Background() {

            fun copyFromTemplate(index: Int, templateSceneObject: TemplateSceneObject.Background.Image): Image {
                drawIndex = index.generateDrawIndex()
                return changeTemplate(templateSceneObject)
            }

            fun changeTemplate(templateSceneObject: TemplateSceneObject.Background.Image): Image {
                x = templateSceneObject.x
                y = templateSceneObject.y
                width = templateSceneObject.width
                height = templateSceneObject.height
                angle = templateSceneObject.angle
                type = templateSceneObject.type
                subType = templateSceneObject.subType
                name = templateSceneObject.name
                source = templateSceneObject.source
//                usedType = templateSceneObject.usedType
                alpha = templateSceneObject.alpha
                overPrint = templateSceneObject.overPrint
                whitePrint = templateSceneObject.whitePrint
                textileColor = templateSceneObject.textileColor
                fillColor = templateSceneObject.fillColor
                isBigFile = templateSceneObject.isBigFile
                fixedSize = templateSceneObject.fixedSize
                readOnly = templateSceneObject.readOnly
                resourceId = templateSceneObject.resourceId.run {
                    if (this.isNotBlank()) {
                        this
                    } else {
                        null
                    }
                }
                middleImagePath = templateSceneObject.middleImagePath.run {
                    if (this.isNotBlank()) {
                        this
                    } else {
                        null
                    }
                }
                return this
            }

            /**
             * Image 타입의 background를 color 타입으로 해준다.
             */
            fun convertToColor(color: String): Color {
                return Color(
                    drawIndex = this.drawIndex,
                    x = this.x,
                    y = this.y,
                    width = this.width,
                    height = this.height,
                    angle = this.angle,
                    type = this.type,
                    subType = "color",
                    name = this.name,
                    source = this.source,
//                    usedType = this.usedType,
                    alpha = this.alpha,
                    overPrint = this.overPrint,
                    whitePrint = this.whitePrint,
                    textileColor = this.textileColor,
                    fillColor = this.fillColor,
                    isBigFile = this.isBigFile,
                    fixedSize = this.fixedSize,
                    readOnly = this.readOnly,
                    bgColor = color
                )
            }

            constructor() : this(
                drawIndex = 0.generateDrawIndex(),
                x = 0f,
                y = 0f,
                width = 0f,
                height = 0f,
                angle = 0,
                type = "0",
                subType = "0",
                name = "0",
                source = "0",
//                usedType = "",
                alpha = 0.0f,
                overPrint = false,
                whitePrint = false,
                textileColor = "",
                fillColor = "",
                isBigFile = false,
                fixedSize = false,
                readOnly = false,
                resourceId = null,
                middleImagePath = null,
            )
        }

    }

    sealed class Text : SceneObject() {

        //        abstract var textDrawableHeight: String
        abstract var wordWrap: Boolean
        abstract var placeholder: String
        abstract var defaultText: String
        abstract var defaultStyle: DefaultStyle
        abstract var text: String

        data class User(
            override var drawIndex: String,
            override var x: Float,
            override var y: Float,
            override var width: Float,
            override var height: Float,
            override var angle: Int,
            override var type: String,
            override var subType: String,
            override var name: String,
            override var source: String,
//            override var usedType: String,
            override var alpha: Float,
            override var overPrint: Boolean,
            override var whitePrint: Boolean,
            override var textileColor: String,
            override var fillColor: String,
            override var isBigFile: Boolean,
            override var fixedSize: Boolean,
            override var readOnly: Boolean,
//            override var textDrawableHeight: String,
            override var wordWrap: Boolean,
            override var placeholder: String,
            override var defaultText: String,
            override var defaultStyle: DefaultStyle,
            override var text: String,
        ) : Text() {

            fun copyFromTemplate(index: Int, templateSceneObject: TemplateSceneObject.Text.User): User {
                drawIndex = index.generateDrawIndex()
                return changeTemplate(templateSceneObject)
            }

            fun changeTemplate(templateSceneObject: TemplateSceneObject.Text.User): User {
                x = templateSceneObject.x
                y = templateSceneObject.y
                width = templateSceneObject.width
                height = templateSceneObject.height
                angle = templateSceneObject.angle
                type = templateSceneObject.type
                subType = templateSceneObject.subType
                name = templateSceneObject.name
                source = templateSceneObject.source
//                usedType = templateSceneObject.usedType
                alpha = templateSceneObject.alpha
                overPrint = templateSceneObject.overPrint
                whitePrint = templateSceneObject.whitePrint
                textileColor = templateSceneObject.textileColor
                fillColor = templateSceneObject.fillColor
                isBigFile = templateSceneObject.isBigFile
                fixedSize = templateSceneObject.fixedSize
                readOnly = templateSceneObject.readOnly
//                textDrawableHeight = templateSceneObject.textDrawableHeight
                wordWrap = templateSceneObject.wordWrap
                placeholder = templateSceneObject.placeholder
                defaultText = templateSceneObject.defaultText
                defaultStyle = templateSceneObject.defaultStyle
                text = templateSceneObject.text
                return this
            }

            constructor() : this(
                drawIndex = 0.generateDrawIndex(),
                x = 0f,
                y = 0f,
                width = 0f,
                height = 0f,
                angle = 0,
                type = "0",
                subType = "0",
                name = "0",
                source = "0",
//                usedType = "",
                alpha = 0.0f,
                overPrint = false,
                whitePrint = false,
                textileColor = "",
                fillColor = "",
                isBigFile = false,
                fixedSize = false,
                readOnly = false,
//                textDrawableHeight= "",
                wordWrap = false,
                placeholder = "",
                defaultText = "",
                defaultStyle = DefaultStyle(),
                text = "",
            )
        }

        data class Spine(
            override var drawIndex: String,
            override var x: Float,
            override var y: Float,
            override var width: Float,
            override var height: Float,
            override var angle: Int,
            override var type: String,
            override var subType: String,
            override var name: String,
            override var source: String,
//            override var usedType: String,
            override var alpha: Float,
            override var overPrint: Boolean,
            override var whitePrint: Boolean,
            override var textileColor: String,
            override var fillColor: String,
            override var isBigFile: Boolean,
            override var fixedSize: Boolean,
            override var readOnly: Boolean,
//            override var textDrawableHeight: String,
            override var wordWrap: Boolean,
            override var placeholder: String,
            override var defaultText: String,
            override var defaultStyle: DefaultStyle,
            override var text: String,
        ) : Text() {
            fun copyFromTemplate(index: Int, templateSceneObject: TemplateSceneObject.Text.Spine): Spine {
                drawIndex = index.generateDrawIndex()
                return changeTemplate(templateSceneObject)
            }

            fun changeTemplate(templateSceneObject: TemplateSceneObject.Text.Spine): Spine {
                x = templateSceneObject.x
                y = templateSceneObject.y
                width = templateSceneObject.width
                height = templateSceneObject.height
                angle = templateSceneObject.angle
                type = templateSceneObject.type
                subType = templateSceneObject.subType
                name = templateSceneObject.name
                source = templateSceneObject.source
//                usedType = templateSceneObject.usedType
                alpha = templateSceneObject.alpha
                overPrint = templateSceneObject.overPrint
                whitePrint = templateSceneObject.whitePrint
                textileColor = templateSceneObject.textileColor
                fillColor = templateSceneObject.fillColor
                isBigFile = templateSceneObject.isBigFile
                fixedSize = templateSceneObject.fixedSize
                readOnly = templateSceneObject.readOnly
//                textDrawableHeight = templateSceneObject.textDrawableHeight
                wordWrap = templateSceneObject.wordWrap
                placeholder = templateSceneObject.placeholder
                defaultText = templateSceneObject.defaultText
                defaultStyle = templateSceneObject.defaultStyle
                text = templateSceneObject.text
                return this
            }

            constructor() : this(
                drawIndex = 0.generateDrawIndex(),
                x = 0f,
                y = 0f,
                width = 0f,
                height = 0f,
                angle = 0,
                type = "0",
                subType = "0",
                name = "0",
                source = "0",
//                usedType = "",
                alpha = 0.0f,
                overPrint = false,
                whitePrint = false,
                textileColor = "",
                fillColor = "",
                isBigFile = false,
                fixedSize = false,
                readOnly = false,
//                textDrawableHeight = "",
                wordWrap = false,
                placeholder = "",
                defaultText = "",
                defaultStyle = DefaultStyle(),
                text = "",
            )
        }
    }

    data class Sticker(
        override var drawIndex: String,
        override var x: Float,
        override var y: Float,
        override var width: Float,
        override var height: Float,
        override var angle: Int,
        override var type: String,
        override var subType: String,
        override var name: String,
        override var source: String,
//        override var usedType: String,
        override var alpha: Float,
        override var overPrint: Boolean,
        override var whitePrint: Boolean,
        override var textileColor: String,
        override var fillColor: String,
        override var isBigFile: Boolean,
        override var fixedSize: Boolean,
        override var readOnly: Boolean,
        var resourceId: String?,
        var middleImagePath: String?,
    ) : SceneObject() {

        fun copyFromTemplate(index: Int, templateSceneObject: TemplateSceneObject.Sticker): Sticker {
            drawIndex = index.generateDrawIndex()
            return changeTemplate(templateSceneObject)
        }

        fun changeTemplate(templateSceneObject: TemplateSceneObject.Sticker): Sticker {
            x = templateSceneObject.x
            y = templateSceneObject.y
            width = templateSceneObject.width
            height = templateSceneObject.height
            angle = templateSceneObject.angle
            type = templateSceneObject.type
            subType = templateSceneObject.subType
            name = templateSceneObject.name
            source = templateSceneObject.source
//            usedType = templateSceneObject.usedType
            alpha = templateSceneObject.alpha
            overPrint = templateSceneObject.overPrint
            whitePrint = templateSceneObject.whitePrint
            textileColor = templateSceneObject.textileColor
            fillColor = templateSceneObject.fillColor
            isBigFile = templateSceneObject.isBigFile
            fixedSize = templateSceneObject.fixedSize
            readOnly = templateSceneObject.readOnly
            resourceId = templateSceneObject.resourceId.run {
                if (this.isNotBlank()) {
                    this
                } else {
                    null
                }
            }
            middleImagePath = templateSceneObject.middleImagePath.run {
                if (this.isNotBlank()) {
                    this
                } else {
                    null
                }
            }
            return this
        }

        constructor() : this(
            drawIndex = 0.generateDrawIndex(),
            x = 0f,
            y = 0f,
            width = 0f,
            height = 0f,
            angle = 0,
            type = "0",
            subType = "0",
            name = "0",
            source = "0",
//            usedType = "",
            alpha = 0.0f,
            overPrint = false,
            whitePrint = false,
            textileColor = "",
            fillColor = "",
            isBigFile = false,
            fixedSize = false,
            readOnly = false,
            resourceId = "0",
            middleImagePath = "",
        )
    }
}

