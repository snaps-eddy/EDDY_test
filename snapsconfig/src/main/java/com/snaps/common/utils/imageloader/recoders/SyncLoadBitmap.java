package com.snaps.common.utils.imageloader.recoders;

import android.graphics.Bitmap;

import com.snaps.common.utils.log.Dlog;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by ysjeong on 2017. 12. 26..
 */

public class SyncLoadBitmap {
    private static final String TAG = SyncLoadBitmap.class.getSimpleName();
    private Bitmap bitmap = null;
    private Object locker = new Object();
    private AtomicBoolean isDownloading = new AtomicBoolean(false);

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Object getLocker() {
        return locker;
    }

    public void setLocker(Object locker) {
        this.locker = locker;
    }

    public boolean isDownloading() {
        return isDownloading.get();
    }

    public void startDownloading() {
        isDownloading.set(true);
    }

    public void finishDownload() {
        isDownloading.set(false);
        synchronized (getLocker()) {
            getLocker().notify();
        }
    }

    public void waitIfDownloading() {
        if (isDownloading()) {
            synchronized (getLocker()){
                if (isDownloading()) {
                    try {
                        getLocker().wait();
                    } catch (InterruptedException e) {
                        Dlog.e(TAG, e);
                    }
                }
            }
        }
    }
}
