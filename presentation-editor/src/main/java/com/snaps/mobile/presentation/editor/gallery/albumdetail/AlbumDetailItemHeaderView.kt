package com.snaps.mobile.presentation.editor.gallery.albumdetail

import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import android.widget.CheckBox
import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.airbnb.epoxy.VisibilityState
import com.snaps.common.utils.log.Dlog
import com.snaps.mobile.presentation.editor.R
import com.snaps.mobile.presentation.editor.utils.KotlinEpoxyHolder

@EpoxyModelClass
abstract class AlbumDetailItemHeaderView : EpoxyModelWithHolder<AlbumDetailItemHeaderViewHolder>() {

    override fun getDefaultLayout(): Int = R.layout.item_album_detail_list_header

    @EpoxyAttribute
    var groupChecked: Boolean = false

    @EpoxyAttribute
    var position: Int = 0

    @EpoxyAttribute
    lateinit var children: List<AlbumDetailItem>

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var checkClickListener: ((View, Boolean) -> Unit)

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var onChildSelect: ((Boolean) -> Unit)

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    var visibleInRecycleView: Boolean = false

    override fun bind(holder: AlbumDetailItemHeaderViewHolder) {
        with(holder) {
            tvDateTitle.text = children.first().date.getDateTitle()
            cbSelectDate.isChecked = groupChecked
            root.setOnClickListener {
                cbSelectDate.toggle()
                checkClickListener.invoke(it, cbSelectDate.isChecked)
            }
        }
    }

    override fun bind(holder: AlbumDetailItemHeaderViewHolder, payloads: MutableList<Any>) {
        payloads.forEach {
            when (it) {
                AlbumDetailListAdapter.PAYLOAD_UPDATE_SELECTION -> renderSelection(holder)
            }
        }
    }

    private fun renderSelection(holder: AlbumDetailItemHeaderViewHolder) {
        holder.cbSelectDate.isChecked = groupChecked
    }

    override fun unbind(holder: AlbumDetailItemHeaderViewHolder) {
        with(holder) {
            tvDateTitle.text = null
            root.setOnClickListener(null)
        }
    }

    override fun onVisibilityStateChanged(visibilityState: Int, holder: AlbumDetailItemHeaderViewHolder) {
        visibleInRecycleView = visibilityState != VisibilityState.INVISIBLE
    }
}

class AlbumDetailItemHeaderViewHolder(parent: ViewParent) : KotlinEpoxyHolder() {
    val root by bind<ViewGroup>(R.id.root)
    val cbSelectDate by bind<CheckBox>(R.id.cb_select_date)
    val tvDateTitle by bind<TextView>(R.id.tv_date_title)
}