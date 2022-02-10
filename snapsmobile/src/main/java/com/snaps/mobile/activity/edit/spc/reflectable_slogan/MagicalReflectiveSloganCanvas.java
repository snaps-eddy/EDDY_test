package com.snaps.mobile.activity.edit.spc.reflectable_slogan;

import android.content.Context;

import com.snaps.common.utils.constant.Const_VALUES;
import com.snaps.mobile.activity.edit.skin.SnapsSkinConstants;

public class MagicalReflectiveSloganCanvas extends BaseReflectableSloganCanvas {

    private static final String TAG = MagicalReflectiveSloganCanvas.class.getSimpleName();

    public MagicalReflectiveSloganCanvas(Context context) {
        super(context);
    }

    @Override
    public String getGradientSkinPath(String gradientColorCode) {

        if (gradientColorCode == null) {
            return null;
        }

        switch (gradientColorCode) {
            case Const_VALUES.COLOR_CODE_RED_GRADIENT: //red
                return SnapsSkinConstants.SLOGAN_MAGICAL_REFLECTIVES_TEXTURE_RED;

            case Const_VALUES.COLOR_CODE_BLUE_GRADIENT: // blue
                return SnapsSkinConstants.SLOGAN_MAGICAL_REFLECTIVES_TEXTURE_BLUE;

            case Const_VALUES.COLOR_CODE_GREEN_GRADIENT: // green
                return SnapsSkinConstants.SLOGAN_MAGICAL_REFLECTIVES_TEXTURE_GREEN;

            case Const_VALUES.COLOR_CODE_YELLOW_GRADIENT:
                return SnapsSkinConstants.SLOGAN_MAGICAL_REFLECTIVES_TEXTURE_YELLOW;
        }
        return null;
    }

}
