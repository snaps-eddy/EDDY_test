package com.snaps.mobile.domain.template

import com.snaps.common.android_utils.toOTAngle
import com.snaps.mobile.domain.save.DefaultStyle
import com.snaps.mobile.domain.save.Filter

sealed class TemplateSceneObject {

    abstract val x: Float
    abstract val y: Float
    abstract val width: Float
    abstract val height: Float
    abstract val angle: Int
    abstract val type: String
    abstract val subType: String
    abstract val name: String
    abstract val source: String
//    abstract val usedType: String
    abstract val alpha: Float
    abstract val overPrint: Boolean
    abstract val whitePrint: Boolean
    abstract val textileColor: String
    abstract val fillColor: String
    abstract val isBigFile: Boolean
    abstract val fixedSize: Boolean
    abstract val readOnly: Boolean

    data class Image(
        override val x: Float,
        override val y: Float,
        override val width: Float,
        override val height: Float,
        override val angle: Int,
        override val type: String,
        override val subType: String,
        override val name: String,
        override val source: String,
//        override val usedType: String,
        override val alpha: Float,
        override val overPrint: Boolean,
        override val whitePrint: Boolean,
        override val textileColor: String,
        override val fillColor: String,
        override val isBigFile: Boolean,
        override val fixedSize: Boolean,
        override val readOnly: Boolean,
        val innerImage: InnerImage?,
        val original: Original?,
        val filter: Filter?,
        val border: Border?,
    ) : TemplateSceneObject() {

        data class Original(
            val imgSeq: String,
            val year: String,
            val middleImagePath: String,
            val width: Float,
            val height: Float,
            val analysisInfo: String,
            val orientation: Int,
            val date: String,
        ) {
            val orientationAngle: Int
                get() = orientation.toOTAngle()
        }

        data class InnerImage(
            val x: Float,
            val y: Float,
            val width: Float,
            val height: Float,
            val angle: Int,
            val alpha: Float,
        )

        data class Border(
            val imageId: String,
            val imagePath: String,
            val maskId: String,
            val maskPath: String,
            val singleAlpha: Float,
            val singleColor: String,
            val singleThickness: Int,
            val imageOffset: String,
            val type: String
        )
    }

    sealed class Background : TemplateSceneObject() {
        data class Color(
            override val x: Float,
            override val y: Float,
            override val width: Float,
            override val height: Float,
            override val angle: Int,
            override val type: String,
            override val subType: String,
            override val name: String,
            override val source: String,
//            override val usedType: String,
            override val alpha: Float,
            override val overPrint: Boolean,
            override val whitePrint: Boolean,
            override val textileColor: String,
            override val fillColor: String,
            override val isBigFile: Boolean,
            override val fixedSize: Boolean,
            override val readOnly: Boolean,
            val bgColor: String
        ) : Background()

        data class Image(
            override val x: Float,
            override val y: Float,
            override val width: Float,
            override val height: Float,
            override val angle: Int,
            override val type: String,
            override val subType: String,
            override val name: String,
            override val source: String,
//            override val usedType: String,
            override val alpha: Float,
            override val overPrint: Boolean,
            override val whitePrint: Boolean,
            override val textileColor: String,
            override val fillColor: String,
            override val isBigFile: Boolean,
            override val fixedSize: Boolean,
            override val readOnly: Boolean,
            val resourceId: String,
            val middleImagePath: String,
        ) : Background()

    }

    sealed class Text : TemplateSceneObject() {

//        abstract val textDrawableHeight: String
        abstract val wordWrap: Boolean
        abstract val placeholder: String
        abstract val defaultText: String
        abstract val defaultStyle: DefaultStyle
        abstract val text: String

        data class User(
            override val x: Float,
            override val y: Float,
            override val width: Float,
            override val height: Float,
            override val angle: Int,
            override val type: String,
            override val subType: String,
            override val name: String,
            override val source: String,
//            override val usedType: String,
            override val alpha: Float,
            override val overPrint: Boolean,
            override val whitePrint: Boolean,
            override val textileColor: String,
            override val fillColor: String,
            override val isBigFile: Boolean,
            override val fixedSize: Boolean,
            override val readOnly: Boolean,
//            override val textDrawableHeight: String,
            override val wordWrap: Boolean,
            override val placeholder: String,
            override val defaultText: String,
            override val defaultStyle: DefaultStyle,
            override val text: String,
        ) : Text()

        data class Spine(
            override val x: Float,
            override val y: Float,
            override val width: Float,
            override val height: Float,
            override val angle: Int,
            override val type: String,
            override val subType: String,
            override val name: String,
            override val source: String,
//            override val usedType: String,
            override val alpha: Float,
            override val overPrint: Boolean,
            override val whitePrint: Boolean,
            override val textileColor: String,
            override val fillColor: String,
            override val isBigFile: Boolean,
            override val fixedSize: Boolean,
            override val readOnly: Boolean,
//            override val textDrawableHeight: String,
            override val wordWrap: Boolean,
            override val placeholder: String,
            override val defaultText: String,
            override val defaultStyle: DefaultStyle,
            override val text: String,
        ) : Text()
    }

    data class Sticker(
        override val x: Float,
        override val y: Float,
        override val width: Float,
        override val height: Float,
        override val angle: Int,
        override val type: String,
        override val subType: String,
        override val name: String,
        override val source: String,
//        override val usedType: String,
        override val alpha: Float,
        override val overPrint: Boolean,
        override val whitePrint: Boolean,
        override val textileColor: String,
        override val fillColor: String,
        override val isBigFile: Boolean,
        override val fixedSize: Boolean,
        override val readOnly: Boolean,
        val resourceId: String,
        val middleImagePath: String,
    ) : TemplateSceneObject()
}