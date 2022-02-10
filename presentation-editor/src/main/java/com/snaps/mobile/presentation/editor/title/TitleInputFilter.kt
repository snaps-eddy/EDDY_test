package com.snaps.mobile.presentation.editor.title

import android.text.InputFilter
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextUtils
import androidx.core.text.buildSpannedString
import java.util.regex.Pattern

class TitleInputFilter : InputFilter {

//    private val whiteCharRegex = "^[ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z0-9]*$"
//    private val whiteCharPattern = Pattern.compile(whiteCharRegex)

    override fun filter(source: CharSequence, start: Int, end: Int, dest: Spanned?, dstart: Int, dend: Int): CharSequence {
//        val matcher = whiteCharPattern.matcher(source)
//        return if (matcher.matches()) source else ""

        return if (dest.isNullOrEmpty() && source.length > 1) removeDisallowedCharacters(source) else filterSource(source)
    }

    //추후 PC에서 편집한 내용중에 허용되지 않은 문자가 있는 경우 해당 문자만 삭제하는 기능
    private fun removeDisallowedCharacters(source: CharSequence): CharSequence {
        return SpannableStringBuilder().apply {
            source.forEach { char -> if (isAllowedCharacter(char)) append(char) }
            subSequence(0, length)
        }
    }

    private fun filterSource(source: CharSequence): CharSequence {
        return source.find { isDisallowedCharacter(it) }?.let { "" } ?: source
    }

    private fun isDisallowedCharacter(char: Char): Boolean {
        return isAllowedCharacter(char).not()
    }

    private fun isAllowedCharacter(char: Char): Boolean {
        val isEng = char.code in 65..90 || char.code in 97..122
        val isOtherLetter = Character.getType(char) == Character.OTHER_LETTER.toInt()  // https://www.compart.com/en/unicode/category
        val isNumber = char.code in 48..57
        val isEtc = char.code in 33..47 || char.code in 58..64 || char.code in 91..96 || char.code in 123..126
        val isSpace = char.code == 32
        return isEng || isOtherLetter || isNumber || isEtc || isSpace
    }
}