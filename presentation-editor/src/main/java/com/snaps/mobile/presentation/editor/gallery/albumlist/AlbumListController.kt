package com.snaps.mobile.presentation.editor.gallery.albumlist

import com.airbnb.epoxy.TypedEpoxyController

class AlbumListController constructor(
    private val callbackListener: AdapterCallbacksListener
) : TypedEpoxyController<List<AlbumListItem>>() {

    override fun buildModels(data: List<AlbumListItem>?) {
        data?.forEachIndexed { index, itemData ->
            albumListItemView {
                id(itemData.id.plus(index))
                thumbnailUri(itemData.thumbnailUri)
                albumName(itemData.name)
                imageCount(itemData.imageCount.toString())
                spanSizeOverride { totalSpanCount, position, itemCount -> 1 }
                onClickAlbum { model, parentView, clickedView, position ->
                    callbackListener.onClickAlbum(itemData)
                }
            }
        }
    }

    interface AdapterCallbacksListener {
        fun onClickAlbum(album: AlbumListItem)
    }
}