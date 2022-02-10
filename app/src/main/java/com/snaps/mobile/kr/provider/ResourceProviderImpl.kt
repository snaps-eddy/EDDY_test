package com.snaps.mobile.kr.provider

import android.content.res.Resources
import androidx.annotation.StringRes
import com.snaps.common.android_utils.ResourceProvider
import javax.inject.Inject

class ResourceProviderImpl @Inject constructor(
    private val resources: Resources
) : ResourceProvider {

    override fun getDimensions(resId: Int): Float {
        return resources.getDimension(resId)
    }

    override fun getScreenWidth(): Int {
        return resources.displayMetrics.widthPixels
    }

    override fun getString(@StringRes resId: Int): String {
        return resources.getString(resId)
    }

    override fun getString(resId: Int, vararg args: Any): String {
        return resources.getString(resId, *args)
    }

}