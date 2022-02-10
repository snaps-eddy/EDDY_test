package com.snaps.mobile.data.asset

import com.snaps.mobile.domain.asset.AssetImageAlbum
import com.snaps.mobile.domain.asset.AssetImageType

data class DeviceAssetImageAlbum(
    override val id: String,
    override val type: AssetImageType = AssetImageType.Device,
    override val name: String,
    override val thumbnail: String,
    override val photoCounts: Int
) : AssetImageAlbum {
}