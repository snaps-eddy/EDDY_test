package com.snaps.mobile.presentation.editor.sketch.itemview

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
import com.snaps.mobile.presentation.editor.sketch.model.TrayBackgroundItem
import com.snaps.mobile.presentation.editor.utils.KotlinEpoxyHolder

@EpoxyModelClass
abstract class TrayBackgroundItemView : EpoxyModelWithHolder<TrayBackgroundItemViewHolder>() {

    override fun getDefaultLayout(): Int = R.layout.item_tray_background

    @EpoxyAttribute
    lateinit var itemData: TrayBackgroundItem

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    var onClickImage: (() -> Unit)? = null

    override fun bind(holder: TrayBackgroundItemViewHolder) {
        with(holder) {
            drawRoot(holder)
            RequestOptions().apply {
                placeholder(ColorDrawable(Color.argb(153, 218, 218, 218)))
                diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                skipMemoryCache(false)
                format(DecodeFormat.PREFER_RGB_565)
                disallowHardwareConfig()
                override(150, 150)
            }.also {
                holder.glide
                    .load(itemData.thumbnailUri)
                    .apply(it)
                    .into(imageView)
            }
        }
    }

    override fun unbind(holder: TrayBackgroundItemViewHolder) {
        with(holder) {
            glide.clear(imageView)
            imageView.setOnClickListener(null)
        }
    }

    private fun drawRoot(holder: TrayBackgroundItemViewHolder) {
        with(holder) {
            root.apply {
                updateLayoutParams<GridLayoutManager.LayoutParams> {
                    width = itemData.drawWidth
                    height = itemData.drawHeight
                }
            }
            imageView.setOnClickListener {
                onClickImage?.invoke()
            }
        }
    }


}

class TrayBackgroundItemViewHolder(parent: ViewParent) : KotlinEpoxyHolder() {
    val root by bind<FrameLayout>(R.id.root)
    val imageView by bind<ImageView>(R.id.tray_background)
    val glide = Glide.with((parent as View))
}