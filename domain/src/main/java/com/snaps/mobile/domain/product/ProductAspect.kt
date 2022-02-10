package com.snaps.mobile.domain.product

sealed class ProductAspect(val code: String) {

    object Horizontal : ProductAspect(widthDirectionCode)
    object Vertical : ProductAspect(heightDirectionCode)
    object Square : ProductAspect(squareDirectionCode)

    companion object {
        private const val widthDirectionCode: String = "194001"
        private const val heightDirectionCode: String = "194002"
        private const val squareDirectionCode: String = "194003"
    }
}