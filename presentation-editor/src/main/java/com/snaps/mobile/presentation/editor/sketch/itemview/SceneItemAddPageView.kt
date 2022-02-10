package com.snaps.mobile.presentation.editor.sketch.itemview

import android.view.View
import android.view.ViewParent
import android.widget.Button
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.bumptech.glide.Glide
import com.snaps.mobile.presentation.editor.R
import com.snaps.mobile.presentation.editor.utils.KotlinEpoxyHolder

@EpoxyModelClass
abstract class SceneItemAddPageView : EpoxyModelWithHolder<SceneItemAddPageViewHolder>() {

    override fun getDefaultLayout(): Int = R.layout.item_scene_add_page

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    var onClickAddPage: (() -> Unit)? = null

    override fun bind(holder: SceneItemAddPageViewHolder) {
        with(holder) {
            btnAddPage.setOnClickListener {
                onClickAddPage?.invoke()
            }
        }
    }

    override fun unbind(holder: SceneItemAddPageViewHolder) {
        with(holder) {
            btnAddPage.setOnClickListener(null)
        }
    }
}

class SceneItemAddPageViewHolder(parent: ViewParent) : KotlinEpoxyHolder() {
    val root by bind<View>(R.id.root)
    val glide = Glide.with((parent as View).context)
    val btnAddPage by bind<Button>(R.id.btn_add_page)
}