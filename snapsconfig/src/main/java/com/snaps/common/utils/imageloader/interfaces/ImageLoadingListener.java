package com.snaps.common.utils.imageloader.interfaces;

import android.graphics.Bitmap;

/**
 * Created by ifunbae on 2017. 1. 20..
 */

public interface ImageLoadingListener {
//    void onLoadingStarted();
//
//    void onLoadingFailed(@Nullable Drawable errorDrawable);

    void onLoadingComplete(Bitmap resource);

//    void onLoadingCancelled();
}

