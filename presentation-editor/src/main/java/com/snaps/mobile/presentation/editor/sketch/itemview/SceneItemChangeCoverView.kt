package com.snaps.mobile.presentation.editor.sketch.itemview

import android.view.ViewParent
import android.widget.Button
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.snaps.mobile.presentation.editor.R
import com.snaps.mobile.presentation.editor.utils.KotlinEpoxyHolder

@EpoxyModelClass
abstract class SceneItemChangeCoverView : EpoxyModelWithHolder<SceneItemChangeCoverViewHolder>() {

    override fun getDefaultLayout(): Int = R.layout.item_decoration_change_cover

    @EpoxyAttribute
    lateinit var coverTemplateCode: String

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    var onClickChangeCover: ((String) -> Unit)? = null

    override fun bind(holder: SceneItemChangeCoverViewHolder) {
        with(holder) {
            btnChangeCover.setOnClickListener { onClickChangeCover?.invoke(coverTemplateCode) }
        }
    }

}

class SceneItemChangeCoverViewHolder(parent: ViewParent) : KotlinEpoxyHolder() {
    val btnChangeCover by bind<Button>(R.id.btn_change_cover)
}