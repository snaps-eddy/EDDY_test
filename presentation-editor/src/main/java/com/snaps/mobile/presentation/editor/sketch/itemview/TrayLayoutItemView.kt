package com.snaps.mobile.presentation.editor.sketch.itemview

import android.view.View
import android.view.ViewParent
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.GridLayoutManager
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.snaps.mobile.presentation.editor.R
import com.snaps.mobile.presentation.editor.sketch.model.TrayLayoutItem
import com.snaps.mobile.presentation.editor.utils.KotlinEpoxyHolder

@EpoxyModelClass
abstract class TrayLayoutItemView : EpoxyModelWithHolder<TrayLayoutItemViewHolder>() {

    override fun getDefaultLayout(): Int = R.layout.item_tray_layout

    @EpoxyAttribute
    lateinit var itemData: TrayLayoutItem

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    var onClickLayout: (() -> Unit)? = null

    override fun bind(holder: TrayLayoutItemViewHolder) {
        with(holder) {
            drawRoot(holder)
            RequestOptions().apply {
                diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                skipMemoryCache(false)
                format(DecodeFormat.PREFER_RGB_565)
                disallowHardwareConfig()
                override(150, 150)
            }.also {
                holder.glide
                    .load(itemData.thumbnailUri)
                    .apply(it)
                    .into(ivThumbnail)
            }
        }
    }

    override fun unbind(holder: TrayLayoutItemViewHolder) {
        with(holder) {
            glide.clear(ivThumbnail)
            ivThumbnail.setOnClickListener(null)
        }
    }

    private fun drawRoot(holder: TrayLayoutItemViewHolder) {
        with(holder) {
            root.apply {
                updateLayoutParams<GridLayoutManager.LayoutParams> {
                    width = itemData.drawWidth
                    height = itemData.drawHeight
                }
            }
            ivThumbnail.apply {
                setOnClickListener {
                    onClickLayout?.invoke()
                }
            }
        }
    }
}


class TrayLayoutItemViewHolder(parent: ViewParent) : KotlinEpoxyHolder() {
    val root by bind<FrameLayout>(R.id.root)
    val ivThumbnail by bind<ImageView>(R.id.tray_layout_thumbnail)
    val glide = Glide.with((parent as View))
}