package com.snaps.mobile.presentation.editor.sketch.drag.shadow

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Point
import android.view.View
import androidx.core.content.ContextCompat
import com.snaps.mobile.presentation.editor.R
import com.snaps.mobile.presentation.editor.sketch.drag.model.ClipDataUserImage

class TrayBackgroundShadowBuilder(
    v: View,
    val snapshot: Bitmap
) : View.DragShadowBuilder(v) {

    private val offset: Float = v.context.resources.getDimension(R.dimen.drag_shadow_icon_size).div(2)
    private var scale: Float = 2.0f

    var isOnScene = false

    private val icReplace = ContextCompat.getDrawable(v.context, R.drawable.ic_photo_replace)

    override fun onProvideShadowMetrics(size: Point, touch: Point) {
        val width: Int = snapshot.width.times(scale).plus(offset).toInt()
        val height: Int = snapshot.height.times(scale).plus(offset).toInt()
        size.set(width, height)
        touch.set(width.div(2).plus(offset).toInt(), height.div(2).plus(offset).toInt())
    }

    override fun onDrawShadow(canvas: Canvas) {
        when {
            isOnScene -> {
                canvas.save()
                canvas.scale(scale, scale)
                canvas.translate(offset.div(2), offset.div(2))
                canvas.drawBitmap(snapshot, 0.0f, 0.0f, null)
                canvas.restore()
                icReplace?.apply {
                    setBounds(0, 0, offset.times(2).toInt(), offset.times(2).toInt())
                }?.also {
                    it.draw(canvas)
                }
            }
            else -> {
                canvas.save()
                canvas.scale(scale, scale)
                canvas.translate(offset.div(2), offset.div(2))
                canvas.drawBitmap(snapshot, 0.0f, 0.0f, null)
                canvas.restore()
            }
        }
    }
}