package com.snaps.mobile.data.product

import com.google.gson.annotations.SerializedName

data class ResponseGetProductInfo(
    @SerializedName("productInfo")
    val productInfo: ProductInfoDto,
    @SerializedName("templateInfo")
    val templateInfo: TemplateInfoDto,
    @SerializedName("editorProductPrice")
    val templatePriceInfo: List<TemplatePriceInfoDto>,
)