package com.snaps.common.utils.imageloader;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import androidx.annotation.NonNull;

import com.snaps.common.utils.log.Dlog;

public class BitmapRotateTask extends Thread {
    private static final String TAG = BitmapRotateTask.class.getSimpleName();
    private Bitmap orgBitmap;
    private int rotate = 0;
    private WeakReferenceBitmap weakReferenceBitmap;
    private boolean isRunning = false;

    public BitmapRotateTask(Bitmap orgBitmap, int rotate, @NonNull WeakReferenceBitmap weakReferenceBitmap) {
        this.orgBitmap = orgBitmap;
        this.rotate = rotate;
        this.weakReferenceBitmap = weakReferenceBitmap;
        this.isRunning = true;
        this.setDaemon(true);
    }

    @Override
    public void run() {
        super.run();

        if (orgBitmap == null || orgBitmap.isRecycled()) {
            this.isRunning = false;
            return;
        }

        try {
            Matrix matrix = new Matrix();
            matrix.postRotate(this.rotate);

            if (weakReferenceBitmap != null) {
                if (orgBitmap.getWidth() > 0 && orgBitmap.getHeight() > 0) {
                    weakReferenceBitmap.setBmp(Bitmap.createBitmap(orgBitmap, 0, 0, orgBitmap.getWidth(), orgBitmap.getHeight(), matrix, true));
                }
            }
        } catch (OutOfMemoryError e) {
            Dlog.e(TAG, e);
        }

        this.isRunning = false;
    }

    public boolean isRunning() {
        return isRunning && this.getState() == State.RUNNABLE;
    }
}
