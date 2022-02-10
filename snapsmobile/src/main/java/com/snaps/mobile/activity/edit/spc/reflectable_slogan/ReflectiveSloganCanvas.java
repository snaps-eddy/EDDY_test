package com.snaps.mobile.activity.edit.spc.reflectable_slogan;

import android.content.Context;

import com.snaps.mobile.activity.edit.skin.SnapsSkinConstants;

public class ReflectiveSloganCanvas extends BaseReflectableSloganCanvas {

    private static final String TAG = ReflectiveSloganCanvas.class.getSimpleName();

    public ReflectiveSloganCanvas(Context context) {
        super(context);
    }

    @Override
    public String getGradientSkinPath(String gradientColorCode) {
        return SnapsSkinConstants.SLOGAN_REFLECTIVES_TEXTURE;
    }

}
