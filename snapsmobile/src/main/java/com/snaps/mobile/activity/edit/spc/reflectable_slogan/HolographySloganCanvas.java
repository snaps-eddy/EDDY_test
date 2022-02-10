package com.snaps.mobile.activity.edit.spc.reflectable_slogan;

import android.content.Context;

import com.snaps.mobile.activity.edit.skin.SnapsSkinConstants;

public class HolographySloganCanvas extends BaseReflectableSloganCanvas {

    private static final String TAG = HolographySloganCanvas.class.getSimpleName();

    public HolographySloganCanvas(Context context) {
        super(context);
    }

    @Override
    public String getGradientSkinPath(String gradientColorCode) {
        return SnapsSkinConstants.SLOGAN_HOLOGRAPHY_TEXTURE_;
    }
}
