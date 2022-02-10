package com.snaps.mobile.domain.save

sealed class TextAlign(val raw: String) {
    object Left : TextAlign("left")
    object Center : TextAlign("center")
    object Right : TextAlign("right")
}