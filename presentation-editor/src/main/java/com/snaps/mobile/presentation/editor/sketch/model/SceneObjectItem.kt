package com.snaps.mobile.presentation.editor.sketch.model

import com.snaps.mobile.domain.save.DefaultStyle
import com.snaps.mobile.presentation.editor.utils.toScaled

sealed class SceneObjectItem {

    abstract val drawIndex: String
    abstract val x: Float
    abstract val y: Float
    abstract val width: Float
    abstract val height: Float
    abstract val alpha: Float
    abstract val angle: Int
    abstract val scaleFactor: Float

    fun getDrawX(): Float = x * scaleFactor

    fun getDrawY(): Float = y * scaleFactor

    fun getDrawWidth(): Int = width.toInt().toScaled(scaleFactor)

    fun getDrawHeight(): Int = height.toInt().toScaled(scaleFactor)

    private fun Int.toScaleUp(scaleFactor: Float): Int {
        return this.times(scaleFactor).toInt()
    }

    data class Image(
        override val drawIndex: String,
        override val x: Float,
        override val y: Float,
        override val width: Float,
        override val height: Float,
        override val alpha: Float,
        override val angle: Int,
        override val scaleFactor: Float,
        val content: ImageContentUiModel?,
        val innerImage: InnerImageUiModel?,
        val filter: FilterUiModel?,
        val border: BorderUiModel?
    ) : SceneObjectItem() {

        val uiAngle: Int = (innerImage?.angle ?: 0)

        fun getDownSampleSize(isCartThumbnail: Boolean): Int {
            return if (isCartThumbnail) {
                // 섬네일뷰라고 가정.
                // 150은 이전 레거시 코드에서 정해놓은 매직 넘버
                150

            } else {
//            getDrawWidth().coerceAtLeast(getDrawHeight()).times(0.5f).toInt()
                440
            }
        }

        fun isValidRatio(): Boolean {
            if (content == null || innerImage == null) return true

            if (content.originWidth > content.originHeight) {
                if (innerImage.width < innerImage.height) return false
            } else if (content.originWidth < content.originHeight) {
                if (innerImage.width > innerImage.height) return false
            }
            return true
        }
    }

    sealed class Background : SceneObjectItem() {
        data class Color(
            override val drawIndex: String,
            override val x: Float,
            override val y: Float,
            override val width: Float,
            override val height: Float,
            override val alpha: Float,
            override val angle: Int,
            override val scaleFactor: Float,
            val bgColor: Int
        ) : Background()

        data class Image(
            override val drawIndex: String,
            override val x: Float,
            override val y: Float,
            override val width: Float,
            override val height: Float,
            override val alpha: Float,
            override val angle: Int,
            override val scaleFactor: Float,
            val middleImagePath: String?,
        ) : Background()
    }

    data class Sticker(
        override val drawIndex: String,
        override val x: Float,
        override val y: Float,
        override val width: Float,
        override val height: Float,
        override val alpha: Float,
        override val angle: Int,
        override val scaleFactor: Float,
        val middleImagePath: String,
    ) : SceneObjectItem()

    sealed class Text : SceneObjectItem() {

        abstract val text: String
        abstract val defaultStyle: DefaultStyle
        abstract val placeholder: String
        abstract val readOnly: Boolean
        abstract val name: String

        data class User(
            override val drawIndex: String,
            override val x: Float,
            override val y: Float,
            override val width: Float,
            override val height: Float,
            override val alpha: Float,
            override val angle: Int,
            override val scaleFactor: Float,
            override val text: String,
            override val defaultStyle: DefaultStyle,
            override val placeholder: String,
            override val readOnly: Boolean,
            override val name: String,
        ) : Text()

        data class Spine(
            override val drawIndex: String,
            override val x: Float,
            override val y: Float,
            override val width: Float,
            override val height: Float,
            override val alpha: Float,
            override val angle: Int,
            override val scaleFactor: Float,
            override val text: String,
            override val defaultStyle: DefaultStyle,
            override val placeholder: String,
            override val readOnly: Boolean,
            override val name: String,
        ) : Text()
    }
}