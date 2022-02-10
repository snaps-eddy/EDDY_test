package com.snaps.mobile.data.product

data class TemplatePriceInfoDto(
    val countryCompanyCode: String = "",
    val productCode: String = "",
    val sellPrice: Float = 0.0f,
    val orgPrice: Float = 0.0f,
    val priceNo: Int = 0,
    val quantityStartRange: Int,
    val quantityEndRange: Int,
    val pageAddPrice: Float = 0.0f,
    val orgPageAddPrice: Float = 0.0f,
    val discountRate: Float = 0.0f,
    val formPrice: Float = 0.0f,
    val formOrgPrice: Float = 0.0f,
    val framePrice: Float = 0.0f,
    val screenPrice: Float = 0.0f,
    val frameOrgPrice: Float = 0.0f,
    val screenOrgPrice: Float = 0.0f
)