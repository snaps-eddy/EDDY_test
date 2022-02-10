package com.snaps.mobile.presentation.editor.sketch

import com.airbnb.epoxy.TypedEpoxyController
import com.snaps.mobile.presentation.editor.sketch.itemview.trayLayoutItemView
import com.snaps.mobile.presentation.editor.sketch.model.TrayLayoutItem

class TrayLayoutController(
    private val callbacks: CallbacksListener
) : TypedEpoxyController<List<TrayLayoutItem>>() {

    override fun buildModels(data: List<TrayLayoutItem>?) {
        data?.forEach { itemData ->
            trayLayoutItemView {
                id(itemData.resourceId)
                itemData(itemData)
                onClickLayout { callbacks.onClickLayout(itemData) }
            }
        }
    }

    interface CallbacksListener {
        fun onClickLayout(itemData: TrayLayoutItem)
    }
}