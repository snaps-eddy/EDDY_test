package com.snaps.mobile.presentation.editor.gallery.albumlist

import com.snaps.mobile.domain.asset.AssetImageType

data class AlbumListItem(
    val id: String,
    val albumImageType: AssetImageType,
    val thumbnailUri: String,
    val name: String,
    val imageCount: Int
)