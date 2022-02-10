package com.snaps.mobile.domain.save

/**
 * defaultStyle=font-family:'나눔스퀘어200';font-size:13px;color:#000000;text-align:center;font-style:none;text-decoration:none;,
 */
data class DefaultStyle(
    val fontFamily: String,
    val fontSize: String,
    val color: String,
    val textAlign: TextAlign,
    val fontStyle: String, // ??
    val textDecoration: String // ??
) {

    constructor() : this("", "", "", TextAlign.Left, "none", "none")

    fun toRawText(): String {
        return "font-family:${this.fontFamily};"
            .plus("font-size:${this.fontSize};")
            .plus("color:${this.color};")
            .plus("text-align:${this.textAlign.raw};")
            .plus("font-style:${this.fontStyle};")
            .plus("text-decoration:${this.textDecoration};")
    }

    val fontSizePx: Int
        get() = fontSize.replace("px", "").toInt()

}