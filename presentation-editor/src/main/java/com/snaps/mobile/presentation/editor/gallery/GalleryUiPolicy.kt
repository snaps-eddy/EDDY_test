package com.snaps.mobile.presentation.editor.gallery

import javax.inject.Inject

class GalleryUiPolicy @Inject constructor(
    private val minSelectCount: Int,
    private val maxSelectCount: Int,
) {
    fun isNotAvailableSelectImage(currentCount: Int): Boolean {
        return !isAvailableSelectImage(currentCount)
    }

    fun isAvailableSelectImage(currentCount: Int): Boolean {
        return currentCount < maxSelectCount
    }

    fun isNotEnoughImageCount(currentCount: Int): Boolean {
        return (currentCount >= minSelectCount).not()
    }

    fun isOverImageCount(currentCount: Int): Boolean {
        return currentCount > maxSelectCount
    }

    fun getMinSelectCount() = minSelectCount
    fun getMaxSelectCount() = maxSelectCount
}