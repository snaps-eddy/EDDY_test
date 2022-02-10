package com.snaps.mobile.presentation.editor.imageEdit.itemview

import android.graphics.Color
import android.view.View
import android.view.ViewParent
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.bumptech.glide.Glide
import com.snaps.mobile.presentation.editor.R
import com.snaps.mobile.presentation.editor.utils.KotlinEpoxyHolder
import com.snaps.mobile.presentation.editor.utils.dp

@EpoxyModelClass
abstract class FilterImageItemView : EpoxyModelWithHolder<FilterImageItemViewHolder>() {

    @EpoxyAttribute
    lateinit var translatedFilterName: String

    @EpoxyAttribute
    lateinit var filterUri: String

    @EpoxyAttribute
    var applied: Boolean = false

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    var onSelect: (() -> Unit)? = null

    override fun getDefaultLayout(): Int = R.layout.item_filter_image

    override fun bind(holder: FilterImageItemViewHolder) {
        with(holder) {
            if (applied) {
                filterName.setTextColor(Color.parseColor("#FF6060"))
                selectIndicator.isVisible = true
            } else {
                filterName.setTextColor(Color.WHITE)
                selectIndicator.isVisible = false
            }
            filterName.text = translatedFilterName
            glide.load(filterUri)
                .override(44.dp())
                .into(ivFilterImage)

            ivFilterImage.setOnClickListener {
                onSelect?.invoke()
            }
        }
    }

    override fun unbind(holder: FilterImageItemViewHolder) {
        holder.glide.clear(holder.ivFilterImage)
    }
}

class FilterImageItemViewHolder(parent: ViewParent) : KotlinEpoxyHolder() {
    val glide = Glide.with((parent as View).context)
    val filterName by bind<TextView>(R.id.tv_filter_name)
    val ivFilterImage by bind<ImageView>(R.id.iv_filter_image)
    val selectIndicator by bind<View>(R.id.view_select_indicator)
}