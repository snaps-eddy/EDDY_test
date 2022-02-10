package com.snaps.mobile.activity.edit.spc;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.snaps.common.spc.SnapsPageCanvas;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.activity.edit.skin.SnapsSkinConstants;
import com.snaps.mobile.activity.edit.skin.SnapsSkinRequestAttribute;
import com.snaps.mobile.activity.edit.skin.SnapsSkinUtil;

/**
 * com.snaps.kakao.activity.edit.spc
 * SnapsPageCanvas.java
 *
 * @author JaeMyung Park
 * @Date : 2013. 5. 23.
 * @Version :
 */
public class MiniBannerCanvas extends SnapsPageCanvas {
    private static final String TAG = MiniBannerCanvas.class.getSimpleName();
    public final static String HOLDER = "386002";
    public final static String NOT_HOLDER = "386001";

    public MiniBannerCanvas(Context context) {
        super(context);
    }

    public MiniBannerCanvas(Context context, AttributeSet attr) {
        super(context, attr);
    }

    @Override
    protected void loadShadowLayer() {
    }

    @Override
    protected void loadPageLayer() {
    }

    @Override
    protected void loadBonusLayer() {
        String skinName = getSkinName();
        if (!StringUtil.isEmpty(skinName)) {
            try {
                SnapsSkinUtil.loadSkinImage(new SnapsSkinRequestAttribute.Builder()
                        .setContext(getContext())
                        .setResourceFileName(skinName)
                        .setSkinBackgroundView(bonusLayer)
                        .create());
            } catch (Exception e) {
                Dlog.e(TAG, e);
            }
        }
    }

    private String getSkinName() {
        String productCode = Config.getPROD_CODE();
        String frameType = Config.getFRAME_TYPE();

        if (productCode.equals(Const_PRODUCT.PRODUCT_MINI_BANNER_BASIC)) {
            if (frameType.equals(HOLDER)) return SnapsSkinConstants.MINI_BANNER_HOLDER_BASIC;
            if (frameType.equals(NOT_HOLDER)) return SnapsSkinConstants.MINI_BANNER_NOT_HOLDER_BASIC;
        }

        if (productCode.equals(Const_PRODUCT.PRODUCT_MINI_BANNER_CLEAR)) {
            if (frameType.equals(HOLDER)) return SnapsSkinConstants.MINI_BANNER_HOLDER_CLEAR;
            if (frameType.equals(NOT_HOLDER)) return SnapsSkinConstants.MINI_BANNER_NOT_HOLDER_CLEAR;
        }

        if (productCode.equals(Const_PRODUCT.PRODUCT_MINI_BANNER_CANVAS)) {
            if (frameType.equals(HOLDER)) return SnapsSkinConstants.MINI_BANNER_HOLDER_CANVAS;
            if (frameType.equals(NOT_HOLDER)) return SnapsSkinConstants.MINI_BANNER_NOT_HOLDER_CANVAS;
        }

        return "";
    }


    @Override
    protected void initMargin() {

        leftMargin = 45;
        topMargin = 90;
        rightMargin = 45;
        bottomMargin = 90;

    }

    @Override
    public void onDestroyCanvas() {
        if (shadowLayer != null) {
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
