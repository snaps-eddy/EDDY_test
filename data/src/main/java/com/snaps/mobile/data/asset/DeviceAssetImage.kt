package com.snaps.mobile.data.asset

import com.snaps.mobile.domain.asset.AssetImage
import com.snaps.mobile.domain.asset.AssetImageType

class DeviceAssetImage(
    override val id: String,
    override val type: AssetImageType = AssetImageType.Device,
    override val thumbnailUri: String,
    override val milliseconds: Long,
    override val width: Float,
    override val height: Float,
    override val orientation: Int,
) : AssetImage,
    Comparable<DeviceAssetImage> {
    override fun compareTo(other: DeviceAssetImage): Int = when {
        milliseconds < other.milliseconds -> 1
        milliseconds > other.milliseconds -> -1
        else -> 0
    }
}

