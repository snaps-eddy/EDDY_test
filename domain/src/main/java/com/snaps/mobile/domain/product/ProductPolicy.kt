package com.snaps.mobile.domain.product

data class ProductPolicy(
    val productCode: String,
    val templateCode: String,
    val minSelectImageCount: Int = 21,
    val maxSelectImageCount: Int = 700,
    val maxAddMoreImageCount: Int = 100
) {
}
