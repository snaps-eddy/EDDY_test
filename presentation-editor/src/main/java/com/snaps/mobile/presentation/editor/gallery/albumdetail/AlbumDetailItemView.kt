package com.snaps.mobile.presentation.editor.gallery.albumdetail

import android.view.View
import android.view.ViewParent
import android.widget.CheckBox
import android.widget.ImageView
import androidx.core.view.isVisible
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.airbnb.epoxy.VisibilityState
import com.airbnb.epoxy.preload.Preloadable
import com.bumptech.glide.Glide
import com.jakewharton.rxbinding4.view.clicks
import com.snaps.common.utils.log.Dlog
import com.snaps.mobile.presentation.editor.R
import com.snaps.mobile.presentation.editor.gallery.albumdetail.AlbumDetailListAdapter.Companion.PAYLOAD_UPDATE_SELECTION
import com.snaps.mobile.presentation.editor.utils.KotlinEpoxyHolder
import com.snaps.mobile.presentation.editor.utils.loadImage

@EpoxyModelClass
abstract class AlbumDetailItemView : EpoxyModelWithHolder<AlbumDetailItemViewHolder>() {

    override fun getDefaultLayout(): Int = R.layout.item_album_detail_list

    @EpoxyAttribute
    lateinit var uri: String

    @EpoxyAttribute
    var itemChecked: Boolean = false

    @EpoxyAttribute
    var position: Int = 0

    @EpoxyAttribute
    var parentKey: Long = 0L

    @EpoxyAttribute
    lateinit var detailItem: AlbumDetailItem

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var clickListener: View.OnClickListener

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var selectListener: (Boolean) -> Unit

    private var viewVisibleState: Boolean = true

    override fun bind(holder: AlbumDetailItemViewHolder) {
        with(holder) {
            glide.loadImage(uri, true).into(photo)

            renderSelection(holder)

            /**
             * 이미지 클릭과 셀렉트 박스 클릭의 행동이 다르지만
             * 첫번째 나가는 버전에는 셀렉트 기능으로 나간다.
             */
            photo.setOnClickListener {
//                clickListener.onClick(it)
//                selectListener.invoke(selectBox.isChecked)
                selectBox.performClick() // Ben : selectListener.invoke(selectBox.isChecked) 호출하면 뭔가 타이밍 이슈가 생기는 것 같음
            }

            selectBox.setOnClickListener {
                selectListener.invoke(selectBox.isChecked)
            }
        }
    }

    override fun bind(holder: AlbumDetailItemViewHolder, payloads: MutableList<Any>) {
        payloads.forEach {
            when (it) {
                PAYLOAD_UPDATE_SELECTION -> renderSelection(holder)
            }
        }
    }

    private fun renderSelection(holder: AlbumDetailItemViewHolder) {
        with(holder) {
            viewSelectionForeground.isVisible = itemChecked
            selectBox.isChecked = itemChecked
        }
    }

    override fun onVisibilityChanged(
        percentVisibleHeight: Float,
        percentVisibleWidth: Float,
        visibleHeight: Int,
        visibleWidth: Int,
        holder: AlbumDetailItemViewHolder
    ) {
        viewVisibleState = percentVisibleHeight > 0.0f
    }

    fun isVisibleInRecycleView(): Boolean = viewVisibleState

    override fun unbind(holder: AlbumDetailItemViewHolder) {
        with(holder) {
            glide.clear(photo)
            photo.setImageDrawable(null)
            photo.setOnClickListener(null)
            selectBox.setOnClickListener(null)
        }
    }
}

class AlbumDetailItemViewHolder(parent: ViewParent) : KotlinEpoxyHolder(), Preloadable {
    val photo by bind<ImageView>(R.id.iv_photo)
    val viewSelectionForeground by bind<View>(R.id.view_selection_foreground)
    val selectBox by bind<CheckBox>(R.id.cb_selected)
    val glide = Glide.with((parent as View).context)
    override val viewsToPreload by lazy { listOf(photo) }
}