package com.snaps.mobile.presentation.editor.sketch.custom

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.widget.FrameLayout
import com.bumptech.glide.Glide
import com.snaps.common.android_utils.SchedulerProvider
import com.snaps.mobile.presentation.editor.sketch.model.SceneItem
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.kotlin.Singles
import javax.inject.Inject

@AndroidEntryPoint
class CartThumbnailView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    @Inject
    lateinit var schedulerProvider: SchedulerProvider

    private val contentView = SceneCoverContentView(context).apply {
        layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        this.hideBottomController()
        this.isForCartThumbnail = true
    }

    init {
        this.addView(contentView)
    }

    fun takeSnapShot(): Single<Pair<Bitmap, Bitmap>> {
        return Singles
            .zip(
                Single.fromCallable {
                    val testUrl = "https://www.snaps.com/Upload/Data1/mobile/cartSkin/photobook_sq_soft.png"
                    Glide.with(this)
                        .asBitmap()
                        .load(testUrl)
                        .override(480)
                        .skipMemoryCache(false)
                        .submit()
                        .get()
                }.subscribeOn(schedulerProvider.io),
                Single.fromCallable {
                    contentView.takeSnapShot()
                }.subscribeOn(schedulerProvider.ui)
            )
            .observeOn(schedulerProvider.ui)
    }

    fun setData(data: SceneItem) {
        if (contentView.isEqualData(data)) {
            return
        }
        contentView.setData(data)
    }

    fun unbind() {
        contentView.unbind()
    }

}
