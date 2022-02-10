package com.snaps.mobile.presentation.editor.sketch.custom

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.transition.Transition
import com.snaps.mobile.presentation.editor.sketch.model.SceneObjectItem

sealed class TextToImageTarget(view: SceneObjectTextView, val data: SceneObjectItem.Text) : CustomViewTarget<SceneObjectTextView, Bitmap>(view) {

    class Default(view: SceneObjectTextView, data: SceneObjectItem.Text) : TextToImageTarget(view, data) {
        override fun onLoadFailed(errorDrawable: Drawable?) {
            view.showOutline()
        }

        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
            view.setSource(resource, data)

            if (!data.readOnly && data.text.isBlank() && data.placeholder.isNotEmpty()) {
                view.showOutline()
            } else {
                view.hideOutline()
            }
        }

        override fun onResourceCleared(placeholder: Drawable?) {
             view.showOutline()
        }
    }

    class Thumbnail(view: SceneObjectTextView, data: SceneObjectItem.Text) : TextToImageTarget(view, data) {
        override fun onLoadFailed(errorDrawable: Drawable?) {
            view.hideOutline()
        }

        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
            view.setSource(resource, data)
            view.hideOutline()
        }

        override fun onResourceCleared(placeholder: Drawable?) {
            view.hideOutline()
        }
    }

}