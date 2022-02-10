package com.snaps.mobile.activity.edit.spc;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.snaps.common.spc.SnapsPageCanvas;

/**
 * Created by ifunbae on 2016. 9. 23..
 */

public class IdentifyPhotoCanvas extends SnapsPageCanvas {

    public IdentifyPhotoCanvas(Context context) {
        super(context);
    }

    @Override
    protected void loadShadowLayer() {
    }

    @Override
    protected void loadPageLayer() {
    }

    @Override
    protected void loadBonusLayer() {
    }

    @Override
    protected void initMargin() {
    }

    @Override
    public void onDestroyCanvas() {
        if(shadowLayer != null) {
            Drawable d = shadowLayer.getBackground();
            if (d != null) {
                try {
                    d.setCallback(null);
                } catch (Exception ignore) {
                }
            }
        }
        super.onDestroyCanvas();
    }
}
