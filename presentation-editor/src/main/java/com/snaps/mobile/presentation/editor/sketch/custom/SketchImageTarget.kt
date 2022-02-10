package com.snaps.mobile.presentation.editor.sketch.custom

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.transition.Transition
import com.snaps.common.utils.constant.Config
import com.snaps.mobile.presentation.editor.sketch.model.SceneObjectItem

sealed class SketchImageTarget(view: SceneObjectImageView, val data: SceneObjectItem.Image) : CustomViewTarget<SceneObjectImageView, Bitmap>(view) {

    class Default(view: SceneObjectImageView, data: SceneObjectItem.Image) : SketchImageTarget(view, data) {
        override fun onLoadFailed(errorDrawable: Drawable?) {
            view.onLoadFailed()
            view.showWarningResoulution(false)
        }

        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
            if (Config.isDevelopVersion()) {
                view.setSource(view.drawFDBox(resource, data))
            } else {
                view.setSource(resource)
            }
            view.showWarningResoulution(data.content?.isWarningResolution ?: false)
            view.applyPaperFull(data)
        }

        override fun onResourceCleared(placeholder: Drawable?) {
            view.onResourceCleared(placeholder)
            view.showWarningResoulution(false)
        }
    }

    class NoWarningResolution(view: SceneObjectImageView, data: SceneObjectItem.Image) : SketchImageTarget(view, data) {
        override fun onLoadFailed(errorDrawable: Drawable?) {
            view.onLoadFailed()
        }

        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
            view.setSource(resource)
            view.applyPaperFull(data)
        }

        override fun onResourceCleared(placeholder: Drawable?) {
            view.onResourceCleared(placeholder)
        }
    }
}