package com.snaps.mobile.activity.edit.spc;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;


import com.snaps.common.spc.SnapsPageCanvas;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.activity.edit.skin.SnapsSkinConstants;
import com.snaps.mobile.activity.edit.skin.SnapsSkinRequestAttribute;
import com.snaps.mobile.activity.edit.skin.SnapsSkinUtil;

import static com.snaps.common.utils.constant.Const_PRODUCT.PIN_BACK_BUTTON_CIRCLE_32X32;
import static com.snaps.common.utils.constant.Const_PRODUCT.PIN_BACK_BUTTON_CIRCLE_38X38;
import static com.snaps.common.utils.constant.Const_PRODUCT.PIN_BACK_BUTTON_CIRCLE_44X44;
import static com.snaps.common.utils.constant.Const_PRODUCT.PIN_BACK_BUTTON_CIRCLE_58X58;
import static com.snaps.common.utils.constant.Const_PRODUCT.PIN_BACK_BUTTON_CIRCLE_75X75;
import static com.snaps.common.utils.constant.Const_PRODUCT.PIN_BACK_BUTTON_HEART_57X52;
import static com.snaps.common.utils.constant.Const_PRODUCT.PIN_BACK_BUTTON_SQUARE_37X37;
import static com.snaps.common.utils.constant.Const_PRODUCT.PIN_BACK_BUTTON_SQUARE_50X50;
import static com.snaps.common.utils.constant.Const_PRODUCT.MIRROR_BACK_BUTTON_CIRCLE_58X58;
import static com.snaps.common.utils.constant.Const_PRODUCT.MIRROR_BACK_BUTTON_CIRCLE_75X75;
import static com.snaps.common.utils.constant.Const_PRODUCT.MAGNET_BACK_BUTTON_CIRCLE_32X32;
import static com.snaps.common.utils.constant.Const_PRODUCT.MAGNET_BACK_BUTTON_CIRCLE_38X38;
import static com.snaps.common.utils.constant.Const_PRODUCT.MAGNET_BACK_BUTTON_CIRCLE_44X44;
import static com.snaps.common.utils.constant.Const_PRODUCT.MAGNET_BACK_BUTTON_CIRCLE_58X58;
import static com.snaps.common.utils.constant.Const_PRODUCT.MAGNET_BACK_BUTTON_HEART_57X52;
import static com.snaps.common.utils.constant.Const_PRODUCT.MAGNET_BACK_BUTTON_SQUARE_37X37;
import static com.snaps.common.utils.constant.Const_PRODUCT.MAGNET_BACK_BUTTON_SQUARE_50X50;

public class ButtonsCanvas extends SnapsPageCanvas {
    private static final String TAG = ButtonsCanvas.class.getSimpleName();

    public ButtonsCanvas(Context context) {
        super(context);
    }

    public ButtonsCanvas(Context context, AttributeSet attr) {
        super(context, attr);
    }


    @Override
    public void setSnapsPage(SnapsPage page, int number, boolean isBg, String previewBgColor) {
        super.setSnapsPage(page, number, isBg, previewBgColor);
    }

    @Override
    public void setBgColor(int color) {
        color = 0xFFEEEEEE;
        super.setBgColor(color);
    }

    @Override
    protected void loadShadowLayer() {

    }

    @Override
    protected void loadPageLayer() {

    }

    @Override
    protected void loadBonusLayer() {
        try {
            if (getSnapsPage().type.equalsIgnoreCase("page")) {
                ImageView skin = new ImageView(getContext());
                LayoutParams param = new LayoutParams(bonusLayer.getLayoutParams());
                param.width = pageLayer.getLayoutParams().width + rightMargin + leftMargin;
                param.height = pageLayer.getLayoutParams().height + topMargin + bottomMargin;
                skin.setLayoutParams( param );

                SnapsSkinUtil.loadSkinImage(new SnapsSkinRequestAttribute.Builder()
                        .setContext(getContext())
                        .setResourceFileName(getSkinName())
                        .setSkinBackgroundView(skin).create());
                bonusLayer.addView( skin );
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private String getSkinName() {
        switch (Config.getPROD_CODE()) {
            case PIN_BACK_BUTTON_CIRCLE_32X32:
            case MAGNET_BACK_BUTTON_CIRCLE_32X32:
                return SnapsSkinConstants.PHOTO_BUTTON_CIRCLE_32;

            case PIN_BACK_BUTTON_CIRCLE_38X38:
            case MAGNET_BACK_BUTTON_CIRCLE_38X38:
                return SnapsSkinConstants.PHOTO_BUTTON_CIRCLE_38;

            case PIN_BACK_BUTTON_CIRCLE_44X44:
            case MAGNET_BACK_BUTTON_CIRCLE_44X44:
                return SnapsSkinConstants.PHOTO_BUTTON_CIRCLE_44;

            case PIN_BACK_BUTTON_CIRCLE_58X58:
            case MAGNET_BACK_BUTTON_CIRCLE_58X58:
            case MIRROR_BACK_BUTTON_CIRCLE_58X58:
                return SnapsSkinConstants.PHOTO_BUTTON_CIRCLE_58;

            case PIN_BACK_BUTTON_CIRCLE_75X75:
            case MIRROR_BACK_BUTTON_CIRCLE_75X75:
                return SnapsSkinConstants.PHOTO_BUTTON_CIRCLE_75;

            case PIN_BACK_BUTTON_HEART_57X52:
            case MAGNET_BACK_BUTTON_HEART_57X52:
                return SnapsSkinConstants.PHOTO_BUTTON_HEART_57;

            case PIN_BACK_BUTTON_SQUARE_37X37:
            case MAGNET_BACK_BUTTON_SQUARE_37X37:
                return SnapsSkinConstants.PHOTO_BUTTON_SQUARE_37;

            case PIN_BACK_BUTTON_SQUARE_50X50:
            case MAGNET_BACK_BUTTON_SQUARE_50X50:
                return SnapsSkinConstants.PHOTO_BUTTON_SQUARE_50;

            default:
                return "";
        }
    }

    @Override
    protected void initMargin() {
        switch (Config.getPROD_CODE()) {
            case PIN_BACK_BUTTON_CIRCLE_32X32:
            case MAGNET_BACK_BUTTON_CIRCLE_32X32:
                leftMargin = 68;
                topMargin = 68;
                rightMargin = 68;
                bottomMargin = 68;
                break;

            case PIN_BACK_BUTTON_CIRCLE_38X38:
            case MAGNET_BACK_BUTTON_CIRCLE_38X38:
                leftMargin = 60;
                topMargin = 60;
                rightMargin = 60;
                bottomMargin = 60;
                break;

            case PIN_BACK_BUTTON_CIRCLE_44X44:
            case MAGNET_BACK_BUTTON_CIRCLE_44X44:
                leftMargin = 52;
                topMargin = 52;
                rightMargin = 52;
                bottomMargin = 52;
                break;

            case PIN_BACK_BUTTON_CIRCLE_58X58:
            case MAGNET_BACK_BUTTON_CIRCLE_58X58:
            case MIRROR_BACK_BUTTON_CIRCLE_58X58:
                leftMargin = 40;
                topMargin = 40;
                rightMargin = 40;
                bottomMargin = 40;
                break;

            case PIN_BACK_BUTTON_CIRCLE_75X75:
            case MIRROR_BACK_BUTTON_CIRCLE_75X75:
                leftMargin = 30;
                topMargin = 30;
                rightMargin = 30;
                bottomMargin = 30;
                break;

            case PIN_BACK_BUTTON_HEART_57X52:
            case MAGNET_BACK_BUTTON_HEART_57X52:
                leftMargin = 45;
                topMargin = 45;
                rightMargin = 45;
                bottomMargin = 45;
                break;

            case PIN_BACK_BUTTON_SQUARE_37X37:
            case MAGNET_BACK_BUTTON_SQUARE_37X37:
                leftMargin = 50;
                topMargin = 50;
                rightMargin = 50;
                bottomMargin = 50;
                break;

            case PIN_BACK_BUTTON_SQUARE_50X50:
            case MAGNET_BACK_BUTTON_SQUARE_50X50:
                leftMargin = 45;
                topMargin = 45;
                rightMargin = 45;
                bottomMargin = 45;
                break;
        }

        //bug fix
        //실제 상품의 테두리가 안쪽으로 말려서 만들어지므로 여백을 주면 안됨
        leftMargin = 0;
        topMargin = 0;
        rightMargin = 0;
        bottomMargin = 0;
    }

    @Override
    public void onDestroyCanvas() {
        if(bonusLayer != null) {
            Drawable d = bonusLayer.getBackground();
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
