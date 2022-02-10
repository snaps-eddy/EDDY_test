package com.snaps.mobile.presentation.editor

import io.kotest.core.spec.style.BehaviorSpec
import org.assertj.core.api.Assertions.assertThat
import java.util.regex.Pattern

class EmojiRemoveRegexTest : BehaviorSpec({

    given("Hi") {
        val source = "하이!@#$%^&*()_+|\u2700"

        `when`("Go") {
//            val emojiRegex = "^[\\uD83C-\\uDBFF\\uDC00-\\uDFFF]+"
            val emojiRegex = "[\u2700-\u27BF]|[\uE000-\uF8FF]|\uD83C[\uDC00-\uDFFF]|\uD83D[\uDC00-\uDFFF]|[\u2011-\u26FF]|\uD83E[\uDD10-\uDDFF]"
            val matcher = Pattern.compile(emojiRegex).matcher(source)
            val result = matcher.replaceAll("")

            then("Then") {
                println(source)
                assertThat(result).isEqualTo("하이!@#$%^&*()_+|")
            }
        }
    }
})