package com.snaps.mobile.presentation.editor

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.*

private const val EMPTY_STRING = ""
private const val FIRST_SYMBOL = 0

fun spannable(func: () -> SpannableString) = func()

private fun span(s: CharSequence, o: Any) = getNewSpannableString(s).apply {
    setSpan(o, FIRST_SYMBOL, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
}

private fun getNewSpannableString(charSequence: CharSequence): SpannableString {
    return if (charSequence is String) {
        SpannableString(charSequence)
    } else {
        charSequence as? SpannableString ?: SpannableString(EMPTY_STRING)
    }
}

operator fun SpannableString.plus(s: CharSequence) = SpannableString(TextUtils.concat(this, "", s))

fun CharSequence.makeSpannableString() = span(this, Spanned.SPAN_COMPOSING)
fun CharSequence.makeBold() = span(this, StyleSpan(Typeface.BOLD))
fun CharSequence.makeItalic() = span(this, StyleSpan(Typeface.ITALIC))
fun CharSequence.makeUnderline() = span(this, UnderlineSpan())
fun CharSequence.makeStrike() = span(this, StrikethroughSpan())
fun CharSequence.makeSuperscript() = span(this, SuperscriptSpan())
fun CharSequence.makeSubscript() = span(this, SubscriptSpan())
fun CharSequence.makeAbsSize(dp: Int) = span(this, AbsoluteSizeSpan(dp))
fun CharSequence.makeAnotherSize(size: Float) = span(this, RelativeSizeSpan(size))
fun CharSequence.makeAnotherColor(color: Int) = span(this, ForegroundColorSpan(color))
fun CharSequence.makeAnotherBackground(color: Int) = span(this, BackgroundColorSpan(color))
fun CharSequence.makeUrl(url: String) = span(this, URLSpan(url))