package com.snaps.common.utils.imageloader;

import android.graphics.Bitmap;

import java.lang.ref.WeakReference;

public class WeakReferenceBitmap {
    private WeakReference<Bitmap> bmp;

    public void setBmp(Bitmap bmp) {
        this.bmp = new WeakReference<>(bmp);
    }

    public Bitmap getBmp() {
        return bmp != null ? bmp.get() : null;
    }
}
