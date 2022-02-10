package com.snaps.mobile.data.template

import com.google.gson.annotations.SerializedName

data class TemplateSceneObjectDto(
    @SerializedName("@type")
    val type: String = "",
    @SerializedName("@subType")
    val subType: String = "",
    @SerializedName("@name")
    val name: String = "",
    @SerializedName("@source")
    val source: String = "",
//    @SerializedName("@usedtype")
//    val usedType: String = "",
    @SerializedName("@x")
    val x: Float = 0.0f,
    @SerializedName("@y")
    val y: Float = 0.0f,
    @SerializedName("@width")
    val width: Float = 0.0f,
    @SerializedName("@height")
    val height: Float = 0.0f,
    @SerializedName("@angle")
    val angle: Int = 0,
    @SerializedName("@alpha")
    val alpha: Float = 1.0f,
    @SerializedName("@overPrint")
    val overPrint: Boolean = false,
    @SerializedName("@whitePrint")
    val whitePrint: Boolean = false,
    @SerializedName("@textileColor")
    val textileColor: String = "",
    @SerializedName("@fillColor")
    val fillColor: String = "",
    @SerializedName("@isBigFile")
    val isBigFile: Boolean = false,
    @SerializedName("@fixedSize")
    val fixedSize: Boolean = false,
    @SerializedName("@readOnly")
    val readOnly: Boolean = false,

    // Optionals
    @SerializedName("@resourceType")
    val resourceType: String? = null,
    @SerializedName("@resourceId")
    val resourceId: String? = null,
    @SerializedName("@middleImagePath")
    val middleImagePath: String? = null,
    @SerializedName("@imageSequence")
    val imageSequence: String? = null,
    @SerializedName("@bgColor")
    val bgColor: String? = null,
    @SerializedName("@defaultText")
    val defaultText: String? = null,
    @SerializedName("@defaultStyle")
    val defaultStyle: String? = null,
    @SerializedName("@placeholder")
    val placeholder: String? = null,
    @SerializedName("@wordWrap")
    val wordWrap: Boolean? = null,
    @SerializedName("_")
    val userText: String? = null,
    @SerializedName("@lineHeight")
    val lineHeight: Int? = null,
//    @SerializedName("@textDrawableHeight")
//    val textDrawableHeight: String? = null,

    @SerializedName("original")
    val original: TemplateOriginalDto? = null,
    @SerializedName("innerImage")
    val innerImage: TemplateInnerImageDto? = null,
    @SerializedName("border")
    val border: TemplateBorderDto? = null,
    @SerializedName("filter")
    val filter: TemplateFilterDto? = null,
    @SerializedName("analysis")
    val analysis: TemplateAnalysisDto? = null,
)