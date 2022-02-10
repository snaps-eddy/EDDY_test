package com.snaps.mobile.domain

val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
fun Int.generateDrawIndex(): String {
    return (1..10)
        .map { allowedChars.random() }
        .joinToString("")
        .plus(this)
}