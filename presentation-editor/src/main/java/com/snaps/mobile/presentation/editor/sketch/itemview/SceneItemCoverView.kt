package com.snaps.mobile.presentation.editor.sketch.itemview

import android.view.View
import android.view.ViewParent
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.bumptech.glide.Glide
import com.snaps.mobile.presentation.editor.R
import com.snaps.mobile.presentation.editor.sketch.custom.CoverHorizontalScrollView
import com.snaps.mobile.presentation.editor.sketch.custom.SceneCoverContentView
import com.snaps.mobile.presentation.editor.sketch.model.SceneItem
import com.snaps.mobile.presentation.editor.sketch.model.ImageMovingData
import com.snaps.mobile.presentation.editor.utils.KotlinEpoxyHolder

@EpoxyModelClass
abstract class SceneItemCoverView : EpoxyModelWithHolder<SceneItemCoverViewHolder>() {

    override fun getDefaultLayout(): Int = R.layout.item_scene_cover

    @EpoxyAttribute
    lateinit var sceneItem: SceneItem

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    var onImageDrop: ((ImageMovingData, ImageMovingData) -> Unit)? = null

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    var onClickUserImage: ((String, String, String) -> Unit)? = null

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    var onClickUserText: ((String, String) -> Unit)? = null

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    var onClickChangeTitle: ((Int) -> Unit)? = null

    override fun bind(holder: SceneItemCoverViewHolder) {
        with(holder) {
            coverContentView.isForCartThumbnail = false
            coverContentView.onImageDrop = onImageDrop
            coverContentView.onClickUserImage = onClickUserImage
            coverContentView.onClickText = onClickChangeTitle
            coverContentView.onClickUserText = onClickUserText
            coverContentView.setData(sceneItem)
            scroll.setOnPageChangeListener(coverContentView::setCurrentCoverPage)
        }
    }

    override fun unbind(holder: SceneItemCoverViewHolder) {
        with(holder) {
            coverContentView.unbind()
            scroll.setOnPageChangeListener(null)
        }
    }
}

class SceneItemCoverViewHolder(parent: ViewParent) : KotlinEpoxyHolder() {
    val glide = Glide.with((parent as View).context)
    val scroll by bind<CoverHorizontalScrollView>(R.id.scroll)
    val coverContentView by bind<SceneCoverContentView>(R.id.cover_content)
}