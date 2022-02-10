package com.snaps.mobile.presentation.editor.sketch

import com.airbnb.epoxy.TypedEpoxyController
import com.snaps.mobile.presentation.editor.sketch.itemview.trayBackgroundItemView
import com.snaps.mobile.presentation.editor.sketch.model.BackgroundMovingData
import com.snaps.mobile.presentation.editor.sketch.model.TrayBackgroundItem

class TrayBackgroundController(
    val callbacks: CallbacksListener
) : TypedEpoxyController<List<TrayBackgroundItem>>() {

    override fun buildModels(data: List<TrayBackgroundItem>?) {
        data?.forEachIndexed { index, background ->
            trayBackgroundItemView {
                id(background.resourceId)
                itemData(background)
                onClickImage {
                    val movingData = BackgroundMovingData(
                        resourceId = background.resourceId,
                        resourceUri = background.resourceUri
                    )
                    callbacks.onClickTrayBackground(movingData)
                }
            }
        }
    }

    interface CallbacksListener {
        fun onClickTrayBackground(movingData: BackgroundMovingData)
    }

}