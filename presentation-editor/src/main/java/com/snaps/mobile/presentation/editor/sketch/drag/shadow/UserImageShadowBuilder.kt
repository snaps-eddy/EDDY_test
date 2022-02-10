package com.snaps.mobile.presentation.editor.sketch.drag.shadow

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Point
import android.view.View
import androidx.core.content.ContextCompat
import com.snaps.mobile.presentation.editor.R
import com.snaps.mobile.presentation.editor.sketch.drag.model.ClipDataUserImage
import com.snaps.mobile.presentation.editor.utils.dp

class UserImageShadowBuilder(
    v: View,
    val snapshot: Bitmap
) : View.DragShadowBuilder(v) {

    var isOnImage = false
    var isOnScene = false
    var isOnAddPage = false
    var isOnTray = false

    private val icReplace = ContextCompat.getDrawable(v.context, R.drawable.ic_photo_replace)
    private val icAdd = ContextCompat.getDrawable(v.context, R.drawable.ic_photo_plus)
    private val icAddPage = ContextCompat.getDrawable(v.context, R.drawable.ic_photo_page_plus)
    private val icExtract = ContextCompat.getDrawable(v.context, R.drawable.ic_photo_extract)

    override fun onProvideShadowMetrics(size: Point, touch: Point) {
        val width: Int = snapshot.width + 12.dp()
        val height: Int = snapshot.height + 12.dp()
        size.set(width, height)
        touch.set(width / 2, height)
    }

    override fun onDrawShadow(canvas: Canvas) {
        when {
            isOnImage -> {
                canvas.save()
                canvas.translate(12.dp().toFloat(), 12.dp().toFloat())
                canvas.drawBitmap(snapshot, 0.0f, 0.0f, null)
                canvas.restore()
                icReplace?.run {
                    setBounds(0, 0, 24.dp(), 24.dp())
                    this.draw(canvas)
                }
            }
            isOnScene -> {
                canvas.save()
                canvas.translate(12.dp().toFloat(), 12.dp().toFloat())
                canvas.drawBitmap(snapshot, 0.0f, 0.0f, null)
                canvas.restore()
                icAdd?.run {
                    setBounds(0, 0, 24.dp(), 24.dp())
                    this.draw(canvas)
                }
            }
            isOnAddPage -> {
                canvas.save()
                canvas.translate(12.dp().toFloat(), 12.dp().toFloat())
                canvas.drawBitmap(snapshot, 0.0f, 0.0f, null)
                canvas.restore()
                icAddPage?.run {
                    setBounds(0, 0, 24.dp(), 24.dp())
                    this.draw(canvas)
                }
            }
            isOnTray -> {
                canvas.save()
                canvas.translate(12.dp().toFloat(), 12.dp().toFloat())
                canvas.drawBitmap(snapshot, 0.0f, 0.0f, null)
                canvas.restore()
                icExtract?.run {
                    setBounds(0, 0, 24.dp(), 24.dp())
                    this.draw(canvas)
                }
            }
            else -> {
                canvas.save()
                canvas.translate(12.dp().toFloat(), 12.dp().toFloat())
                canvas.drawBitmap(snapshot, 0.0f, 0.0f, null)
                canvas.restore()
            }
        }
    }

//    override fun getOriginalView(): View {
//        return view
//    }
//
//    override fun onDragEnded() {
//        if (!snapshot.isRecycled) {
//            snapshot.recycle()
//        }
//    }

}