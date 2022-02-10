package com.snaps.mobile.presentation.editor.gallery.albumlist

import android.view.View
import android.view.ViewParent
import android.widget.ImageView
import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.bumptech.glide.Glide
import com.snaps.mobile.presentation.editor.R
import com.snaps.mobile.presentation.editor.utils.KotlinEpoxyHolder

@EpoxyModelClass
abstract class AlbumListItemView : EpoxyModelWithHolder<AlbumListItemViewHolder>() {

    override fun getDefaultLayout(): Int = R.layout.item_album_list

    @EpoxyAttribute
    lateinit var thumbnailUri: String

    @EpoxyAttribute
    lateinit var albumName: String

    @EpoxyAttribute
    lateinit var imageCount: String

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var onClickAlbum: View.OnClickListener

    override fun bind(holder: AlbumListItemViewHolder) {
        with(holder) {
            glide.load(thumbnailUri)
                .override(400, 400)
                .into(ivAlbumThumbnail)
            tvAlbumName.text = albumName
            tvSourceCount.text = imageCount
            ivAlbumThumbnail.setOnClickListener(onClickAlbum)
        }
    }

    override fun unbind(holder: AlbumListItemViewHolder) {
        with(holder) {
            glide.clear(ivAlbumThumbnail)
            ivAlbumThumbnail.setOnClickListener(null)
        }
    }
}

class AlbumListItemViewHolder(parent: ViewParent) : KotlinEpoxyHolder() {
    val ivAlbumThumbnail by bind<ImageView>(R.id.iv_album_thumbnail)
    val tvAlbumName by bind<TextView>(R.id.tv_album_name)
    val tvSourceCount by bind<TextView>(R.id.tv_source_count)

    val glide = Glide.with((parent as View).context)
}