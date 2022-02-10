package com.snaps.mobile.domain.project

data class ImageOriginal(
    val imgSeq: String,
    val year: String,
) {
    val outputImageSequence: String
        get() {
            return "${year}/$imgSeq"
        }
}