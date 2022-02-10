package com.snaps.mobile.domain.asset

interface AssetImageAlbum {

    val id: String
    val type: AssetImageType
    val name: String
    val thumbnail: String
    val photoCounts: Int

}