package com.snaps.mobile.domain.asset

sealed class AssetImageType {

    object Device : AssetImageType()

    sealed class External : AssetImageType() {
        object Google : External()
        object Facebook : External()
    }

    object Remote : AssetImageType()

}