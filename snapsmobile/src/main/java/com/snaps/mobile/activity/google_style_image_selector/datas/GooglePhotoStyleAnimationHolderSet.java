package com.snaps.mobile.activity.google_style_image_selector.datas;

/**
 * Created by ysjeong on 2017. 1. 11..
 */

public class GooglePhotoStyleAnimationHolderSet {
    private GooglePhotoStyleAnimationHolder startAnimationHolder = null;
    private GooglePhotoStyleAnimationHolder targetAnimationHolder = null;
    private int row = -1;

    public GooglePhotoStyleAnimationHolderSet(GooglePhotoStyleAnimationHolder startAnimationHolder, GooglePhotoStyleAnimationHolder targetAnimationHolder, int row) {
        this.startAnimationHolder = startAnimationHolder;
        this.targetAnimationHolder = targetAnimationHolder;
        this.row = row;
    }

    public GooglePhotoStyleAnimationHolder getStartAnimationHolder() {
        return startAnimationHolder;
    }

    public void setStartAnimationHolder(GooglePhotoStyleAnimationHolder startAnimationHolder) {
        this.startAnimationHolder = startAnimationHolder;
    }

    public GooglePhotoStyleAnimationHolder getTargetAnimationHolder() {
        return targetAnimationHolder;
    }

    public void setTargetAnimationHolder(GooglePhotoStyleAnimationHolder targetAnimationHolder) {
        this.targetAnimationHolder = targetAnimationHolder;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }
}
