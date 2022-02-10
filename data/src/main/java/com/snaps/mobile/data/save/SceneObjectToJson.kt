package com.snaps.mobile.data.save

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SceneObjectToJson(
    @SerializedName("@type")
    val type: String,
    @SerializedName("@subType")
    val subType: String,
    @SerializedName("@name")
    val name: String,
    @SerializedName("@source")
    val source: String,
//    @SerializedName("@usedType")
//    val usedType: String,
    @SerializedName("@x")
    val x: Float,
    @SerializedName("@y")
    val y: Float,
    @SerializedName("@width")
    val width: Float,
    @SerializedName("@height")
    val height: Float,
    @SerializedName("@angle")
    val angle: Int,
    @SerializedName("@alpha")
    val alpha: Float,
    @SerializedName("@overPrint")
    val overPrint: Boolean,
    @SerializedName("@whitePrint")
    val whitePrint: Boolean,
    @SerializedName("@textileColor")
    val textileColor: String,
    @SerializedName("@fillColor")
    val fillColor: String,
    @SerializedName("@isBigFile")
    val isBigFile: Boolean,
    @SerializedName("@fixedSize")
    val fixedSize: Boolean,
    @SerializedName("@readOnly")
    val readOnly: Boolean,
) {

    /**
     * For Background
     */
    @Expose
    @SerializedName("@resourceId")
    var resourceId: String? = null

    @Expose
    @SerializedName("@middleImagePath")
    var middleImagePath: String? = null

    @Expose
    @SerializedName("@imageSequence")
    var imageSequence: String? = null

    @Expose
    @SerializedName("@bgColor")
    var bgColor: String? = null

    /**
     * For Image
     */
    @Expose
    @SerializedName("border")
    var border: BorderToJson? = null

    @Expose
    @SerializedName("innerImage")
    var innerImage: InnerImageToJson? = null

    @Expose
    @SerializedName("filter")
    var filter: FilterToJson? = null

    @Expose
    @SerializedName("original")
    var original: OriginalToJson? = null

    @Expose
    @SerializedName("analysis")
    var analysis: AnalysisToJson? = null

    /**
     * For Text
     */
//    @Expose
//    @SerializedName("@textDrawableHeight")
//    var textDrawableHeight: String? = null

    @Expose
    @SerializedName("@wordWrap")
    var wordWrap: Boolean? = null

    @Expose
    @SerializedName("@placeholder")
    var placeholder: String? = null

    @Expose
    @SerializedName("@defaultText")
    var defaultText: String? = null

    @Expose
    @SerializedName("@defaultStyle")
    var defaultStyle: String? = null

    @Expose
    @SerializedName("_")
    var textContent: String? = null

    data class BorderToJson(
        @SerializedName("@type")
        val type: String,
        @SerializedName("@singleColor")
        val singleColor: String,
        @SerializedName("@singleThickness")
        val singleThickness: Int,
        @SerializedName("@singleAlpha")
        val singleAlpha: Float,
        @SerializedName("@imageId")
        val imageId: String,
        @SerializedName("@imagePath")
        val imagePath: String,
        @SerializedName("@imageOffset")
        val imageOffset: String,
        @SerializedName("@maskId")
        val maskId: String,
        @SerializedName("@maskPath")
        val maskPath: String,
    )

    data class InnerImageToJson(
        @SerializedName("@x")
        val x: Float,
        @SerializedName("@y")
        val y: Float,
        @SerializedName("@width")
        val width: Float,
        @SerializedName("@height")
        val height: Float,
        @SerializedName("@angle")
        val angle: Int,
        @SerializedName("@alpha")
        val alpha: Float
    )

    data class FilterToJson(
        @SerializedName("@code")
        val code: String,
        @SerializedName("@name")
        val name: String
    )

    data class OriginalToJson(
        @SerializedName("@width")
        val width: Float,
        @SerializedName("@height")
        val height: Float,
    ) {
        @Expose
        @SerializedName("@resourceId")
        var resourceId: String? = null

        @Expose
        @SerializedName("@middleImagePath")
        var middleImagePath: String? = null

        @Expose
        @SerializedName("@imageSequence")
        var imageSequence: String? = null

        @Expose
        @SerializedName("@orientation")
        var orientation: String? = null

        @Expose
        @SerializedName("@date")
        var date: String? = null

        fun getImgSeq(): String? {
            return imageSequence?.run {
                val split = this.split("/")
                split[1]
            }
        }

        fun getYear(): String? {
            return imageSequence?.run {
                val split = this.split("/")
                split[0]
            }
        }
    }


    data class AnalysisToJson(
        @SerializedName("@fd")
        val fd: String
    )
}