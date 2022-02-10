package com.snaps.mobile.presentation.editor.sketch.drag.shadow

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Point
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.snaps.mobile.presentation.editor.R
import com.snaps.mobile.presentation.editor.utils.dp

class SceneShadowBuilder(
    v: View,
    val snapshot: Bitmap
) : View.DragShadowBuilder(v) {

    var isOnScene = false
    private val icReplace = ContextCompat.getDrawable(v.context, R.drawable.ic_photo_replace)
    private val icPageMove = ContextCompat.getDrawable(v.context, R.drawable.ic_page_move)

    override fun onProvideShadowMetrics(size: Point, touch: Point) {
        val width: Int = snapshot.width + 12.dp()
        val height: Int = snapshot.height + 12.dp()
        size.set(width, height)
        touch.set(width / 2, height)
    }

    override fun onDrawShadow(canvas: Canvas) {
        if (isOnScene) {
            canvas.save()
            canvas.translate(12.dp().toFloat(), 12.dp().toFloat())
            canvas.drawBitmap(snapshot, 0.0f, 0.0f, null)
            canvas.restore()
            icReplace?.run {
                setBounds(0, 0, 24.dp(), 24.dp())
                this.draw(canvas)
            }
        } else {
            canvas.save()
            canvas.translate(12.dp().toFloat(), 12.dp().toFloat())
            canvas.drawBitmap(snapshot, 0.0f, 0.0f, null)
            canvas.restore()
            icPageMove?.run {
                setBounds(0, 0, 24.dp(), 24.dp())
                this.draw(canvas)
            }
        }
    }

    fun onDragEnded() {
        if (!snapshot.isRecycled) {
            snapshot.recycle()
        }
    }
}