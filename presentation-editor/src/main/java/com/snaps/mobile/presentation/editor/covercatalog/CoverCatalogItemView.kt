package com.snaps.mobile.presentation.editor.covercatalog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewParent
import android.widget.CheckBox
import android.widget.ImageView
import androidx.core.view.isVisible
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.snaps.mobile.presentation.editor.R
import com.snaps.mobile.presentation.editor.utils.KotlinEpoxyHolder

@EpoxyModelClass
abstract class CoverCatalogItemView : EpoxyModelWithHolder<CoverCatalogItemViewHolder>() {

    override fun getDefaultLayout(): Int = R.layout.item_cover_catalog

    @EpoxyAttribute
    lateinit var coverThumbnailUri: String

    @EpoxyAttribute
    var selected: Boolean = false

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var onCheckCover: ((Boolean) -> Unit)

    override fun bind(holder: CoverCatalogItemViewHolder) {
        drawAll(holder)
    }

    override fun bind(holder: CoverCatalogItemViewHolder, previouslyBoundModel: EpoxyModel<*>) {
        val prevModel = previouslyBoundModel as CoverCatalogItemView
        if (selected != prevModel.selected) {
            setSelection(holder, selected)
            return
        }
        drawAll(holder)
    }

    override fun unbind(holder: CoverCatalogItemViewHolder) {
        with(holder) {
            selectIndicator.setOnCheckedChangeListener(null)
            coverImage.setOnClickListener(null)
            glide.clear(coverImage)
        }
    }

    private fun drawAll(holder: CoverCatalogItemViewHolder) {
        with(holder) {
            attachImage(this)

            drawSelectComponents(this)

            setSelection(this, selected)
        }
    }

    private fun attachImage(holder: CoverCatalogItemViewHolder) {
        with(holder) {
            val requestOptions = RequestOptions().apply {
                placeholder(ColorDrawable(Color.argb(153, 218, 218, 218)))
                diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                format(DecodeFormat.PREFER_RGB_565)
                disallowHardwareConfig()
                override(400) // 사진 선명하게 보이려면 override 옵션을 아예 삭제.
                transform(CoverCutOffTransformation(CoverCutOffTransformation.CutRange.HalfToEnd))
            }

            glide
                .load(coverThumbnailUri)
                .apply(requestOptions)
                .into(coverImage)
        }
    }

    private fun drawSelectComponents(holder: CoverCatalogItemViewHolder) {
        with(holder) {
            coverImage.setOnClickListener {
                selectIndicator.toggle()
                onCheckCover.invoke(selectIndicator.isChecked)
            }
            selectIndicator.setOnClickListener {
                onCheckCover.invoke(selectIndicator.isChecked)
            }
        }
    }

    private fun setSelection(holder: CoverCatalogItemViewHolder, isSelect: Boolean) {
        with(holder) {
            selectBox.isVisible = isSelect
            selectIndicator.isChecked = isSelect
        }
    }

}

class CoverCatalogItemViewHolder(parent: ViewParent) : KotlinEpoxyHolder() {
    val glide = Glide.with((parent as View).context)
    val coverImage by bind<ImageView>(R.id.iv_cover)
    val selectBox by bind<View>(R.id.view_select_box)
    val selectIndicator by bind<CheckBox>(R.id.cb_select_indicator)

}