package com.snaps.mobile.domain.asset

interface AssetImage {

    val id: String
    val type: AssetImageType
    val thumbnailUri: String
    val milliseconds: Long
    val width: Float
    val height: Float
    val orientation: Int

}