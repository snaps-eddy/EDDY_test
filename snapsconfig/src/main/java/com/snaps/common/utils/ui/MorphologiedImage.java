package com.snaps.common.utils.ui;

import android.graphics.Rect;

public class MorphologiedImage {

    private int[] mopologiedArray;
    private Rect fitImageEdge;

    public MorphologiedImage(int[] mopologiedArray, Rect fitImageEdge) {
        this.mopologiedArray = mopologiedArray;
        this.fitImageEdge = fitImageEdge;
    }

    public int[] getMopologiedArray() {
        return mopologiedArray;
    }

    public Rect getFitImageEdge() {
        return fitImageEdge;
    }
}
