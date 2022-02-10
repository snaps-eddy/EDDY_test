package com.snaps.mobile.presentation.editor.sketch

import android.content.ClipData
import android.graphics.Bitmap
import com.airbnb.epoxy.TypedEpoxyController
import com.snaps.mobile.presentation.editor.sketch.itemview.trayImageItemView
import com.snaps.mobile.presentation.editor.sketch.model.TrayImageItem
import com.snaps.mobile.presentation.editor.sketch.model.ImageMovingData

class TrayImageController(
    private val callbacks: CallbacksListener
) : TypedEpoxyController<List<TrayImageItem>>() {

    override fun buildModels(data: List<TrayImageItem>?) {
        data?.forEachIndexed { index, image ->
            trayImageItemView {
                id(image.imgSeq)
                drawWidth(image.drawWidth)
                drawHeight(image.drawHeight)
                imgSeq(image.imgSeq)
                photoUrl(image.thumbnailUri)
                onStage(image.onStage)
                orientationAngle(image.orientationAngle)
                onStartDragImage { snapShot ->
                    val userImageMovingData = ImageMovingData(imageId = image.imgSeq, isOnSketch = image.onStage)
                    val clipData = ClipData(ClipData.newPlainText(null, image.imgSeq))
                    callbacks.onStartDragTrayImage(snapShot, userImageMovingData, clipData)
                }
                spanSizeOverride { _, _, _ -> 1 }
            }
        }
    }

    interface CallbacksListener {

        fun onStartDragTrayImage(snapshot: Bitmap, movingData: ImageMovingData, clipData: ClipData)

    }

}