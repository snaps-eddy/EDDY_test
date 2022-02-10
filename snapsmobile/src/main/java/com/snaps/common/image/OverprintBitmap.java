package com.snaps.common.image;

import android.graphics.Bitmap;

import com.snaps.common.structure.control.SnapsControl;

public class OverprintBitmap {

    private Bitmap bitmap;
    private SnapsControl snapsControl;

    public OverprintBitmap(Bitmap bitmap, SnapsControl snapsControl) {
        this.bitmap = bitmap;
        this.snapsControl = snapsControl;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public SnapsControl getSnapsControl() {
        return snapsControl;
    }
}
