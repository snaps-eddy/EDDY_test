package com.snaps.mobile.presentation.editor.gallery.albumdetail

import android.view.View
import android.view.ViewParent
import android.widget.ImageButton
import android.widget.ImageView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.airbnb.epoxy.preload.Preloadable
import com.bumptech.glide.Glide
import com.snaps.mobile.presentation.editor.R
import com.snaps.mobile.presentation.editor.utils.KotlinEpoxyHolder
import com.snaps.mobile.presentation.editor.utils.loadImage

@EpoxyModelClass
abstract class SelectImageBucketItemView : EpoxyModelWithHolder<SelectImageBucketItemViewHolder>() {

    override fun getDefaultLayout(): Int = R.layout.item_select_image

    @EpoxyAttribute
    lateinit var uri: String

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var deleteClickListener: View.OnClickListener

    override fun bind(holder: SelectImageBucketItemViewHolder) {
        with(holder) {
            glide.loadImage(uri, true).into(ivPhoto)
            btnDelete.setOnClickListener {
                deleteClickListener.onClick(it)
            }
        }
    }

    override fun unbind(holder: SelectImageBucketItemViewHolder) {
        with(holder) {
            glide.clear(ivPhoto)
            btnDelete.setOnClickListener(null)
        }
    }
}

class SelectImageBucketItemViewHolder(parent: ViewParent) : KotlinEpoxyHolder(), Preloadable {
    val ivPhoto by bind<ImageView>(R.id.iv_photo)
    val btnDelete by bind<ImageButton>(R.id.btn_delete)
    val glide = Glide.with((parent as View).context)
    override val viewsToPreload by lazy { listOf(ivPhoto) }
}