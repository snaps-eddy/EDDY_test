package com.snaps.mobile.presentation.editor.utils

import android.graphics.Bitmap
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import com.snaps.mobile.presentation.editor.R

fun RequestManager.loadImage(url: String, isPreloading: Boolean): RequestBuilder<Bitmap> {
    val options = RequestOptions
        .diskCacheStrategyOf(DiskCacheStrategy.AUTOMATIC)
        .override(400, 400)
        .signature(ObjectKey(url.plus(if (isPreloading) "_preloading" else "_not_preloading")))

    return asBitmap()
        .thumbnail(0.1f)
        .placeholder(R.drawable.placeholder)
        .apply(options)
        .load(url)
}