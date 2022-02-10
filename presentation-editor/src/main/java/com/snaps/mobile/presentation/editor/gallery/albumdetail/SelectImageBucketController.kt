package com.snaps.mobile.presentation.editor.gallery.albumdetail

import com.airbnb.epoxy.TypedEpoxyController

class SelectImageBucketController(
    private val callback: AdapterCallbacksListener
) : TypedEpoxyController<List<AlbumDetailItem>>() {

    override fun buildModels(data: List<AlbumDetailItem>?) {
        data?.forEach {
            selectImageBucketItemView {
                id(it.id)
                uri(it.thumbnailUri)
                deleteClickListener { model, parentView, clickedView, position ->
                    callback.onClickDeleteTrayImage(it)
                }
            }
        }
    }

    interface AdapterCallbacksListener {
        fun onClickDeleteTrayImage(image: AlbumDetailItem)
    }

}