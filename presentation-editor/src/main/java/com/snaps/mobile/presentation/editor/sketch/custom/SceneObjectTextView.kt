package com.snaps.mobile.presentation.editor.sketch.custom

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.snaps.common.utils.log.Dlog
import com.snaps.mobile.presentation.editor.databinding.ViewSceneObjectTextBinding
import com.snaps.mobile.presentation.editor.sketch.model.SceneObjectItem
import java.net.URLEncoder

class SceneObjectTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ConstraintLayout(context, attrs, defStyle) {

    private val binding = ViewSceneObjectTextBinding.inflate(LayoutInflater.from(context), this, true)

    val textImageView: ImageView
        get() = binding.textimage

    fun hideOutline() {
        binding.outline.isVisible = false
    }

    fun showOutline() {
        binding.outline.isVisible = true
    }

    fun createRequestUrl(
        text: String,
        isSpine: Boolean,
        width: Float,
        height: Float,
        style: String,
        zoom: Int = 1,
    ): String {
        val divTagWithText = getDivStyleTextWithTextControl(
            textContents = text,
            style = style
        )
        val encodedDivTagWithText = URLEncoder.encode(divTagWithText, "utf-8").replace("+", "%20")

        val urlTextToImageDomain = "https://text3.snaps.com"
        val textToImageSpine = "spineimage"
        val textToImage = "textimageAny"

        return StringBuilder()
            .append(urlTextToImageDomain)
            .append("/")
            .append(if (isSpine) textToImageSpine else textToImage)
            .append("/").append(width.toInt() * zoom)
            .append("/").append(height.toInt() * zoom)
            .append("/")
            .append(encodedDivTagWithText)
            .append("/end")
            .append("/snapsTextImageForAndroid.png")
            .toString()
            .apply {
                Dlog.d(this)
            }
    }

    private fun getDivStyleTextWithTextControl(
        textContents: String,
        style: String,
    ): String {
        return StringBuilder()
            .append("<div style=\"$style\">")
            .append(convertTextContentsForTextServer(textContents))
            .append("</div>")
            .toString()
    }

//    private fun getDivStyleTextWithTextControl(
//        textContents: String,
//        fontFamily: String,
//        fontSize: Int,
//        fontColor: String,
//        textAlign: String,
//        italic: Boolean,
//        underLine: Boolean,
//    ): String {
//        val divStyle = run {
//            val color = if (fontColor.startsWith("#")) fontColor else "#$fontColor"
//            val fontStyle = if (italic) "italic" else "none"
//            val textDecoration = if (underLine) "underline" else "none"
//            StringBuilder()
//                .append("font-family:").append("'$fontFamily'").append(";")
//                .append("font-size:").append(fontSize).append("px;")
//                .append("color:").append(color).append(";")
//                .append("text-align:").append(textAlign).append(";")
//                .append("font-style:").append(fontStyle).append(";")
//                .append("text-decoration:").append(textDecoration).append(";")
//        }
//
//        return StringBuilder()
//            .append("<div style=\"$divStyle\"")
//            .append(convertTextContentsForTextServer(textContents))
//            .append("</div>")
//            .toString()
//    }

    /**
     * 원래 소스에서 <,> replace 후 <br> 삽입이라 순서 변경함.
     */
    private fun convertTextContentsForTextServer(textContents: String): String {
        return removeDisallowedCharacters(textContents)
//            .replace("<".toRegex(), "&lt;")
//            .replace(">".toRegex(), "&gt;")
            .replace("\n".toRegex(), "<br>")
    }

    private fun removeDisallowedCharacters(text: String): String {
        //이모지 제거

        val happyText = text.let {
            //https://stackoverflow.com/questions/49510006/remove-and-other-such-emojis-images-signs-from-java-strings
//            val emojisCharacterFilter = "[^\\p{L}\\p{M}\\p{N}\\p{P}\\p{Z}\\p{Cf}\\p{Cs}\\s]".toRegex()  <- 이거는 <가 없어짐..
            val emojisCharacterFilter =
                "[\uD83C\uDF00-\uD83D\uDDFF]|[\uD83D\uDE00-\uD83D\uDE4F]|[\uD83D\uDE80-\uD83D\uDEFF]|[\u2600-\u26FF]|[\u2700-\u27BF]".toRegex()
            it.replace(emojisCharacterFilter, "")
        }

        //xml 에 적합하지 않은 문자열 제거 (json 으로 변경되었는데 인데 왠지 처리 안하면 집에 가서 잠이 안올거 같으니 넣자)
        //https://stackoverflow.com/questions/4237625/removing-invalid-xml-characters-from-a-string-in-java
        val veryHappyText = happyText.let {
            val xml10pattern = "[^" + "\u0009\r\n" + "\u0020-\uD7FF" + "\uE000-\uFFFD" + "\ud800\udc00-\udbff\udfff" + "]".toRegex()
            val xml11pattern = "[^" + "\u0001-\uD7FF" + "\uE000-\uFFFD" + "\ud800\udc00-\udbff\udfff" + "]+".toRegex()
            it.replace(xml10pattern, "")
                .replace(xml11pattern, "")
        }

        //정체를 알수 없는 것인데 처리 안하면 집에 가서 잠이 더 안올거 같으니 넣자
        val veryVeryHappyText = veryHappyText.let {
            it.replace("\\p{So}+".toRegex(), "")
                .replace("[\ud800\udc00-\udbff\udfff]".toRegex(), "")
                .replace("\ufe0f".toRegex(), "")
        }

        return veryVeryHappyText.let {
            it.replace("(Sticker)", "")
                .replace("(Image)", "")
                .replace("\u2028", "\n")
        }
    }


    /**
     * Glide로 부터 Bitmap 전달.
     */
    fun setSource(resource: Bitmap, data: SceneObjectItem.Text) {
        textImageView.setImageBitmap(resource)
    }
}