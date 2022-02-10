package com.snaps.mobile.activity.edit.spc;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.snaps.common.spc.SnapsPageCanvas;
import com.snaps.common.spc.view.SnapsImageView;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.constant.SnapsProductInfoManager;
import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.edit.skin.SnapsSkinConstants;
import com.snaps.mobile.activity.edit.skin.SnapsSkinRequestAttribute;
import com.snaps.mobile.activity.edit.skin.SnapsSkinUtil;

public class FabricPosterCanvas extends SnapsPageCanvas {

    private static final String TAG = FabricPosterCanvas.class.getSimpleName();

    public FabricPosterCanvas(Context context) {
        super(context);
    }

    @Override
    public void setSnapsPage(SnapsPage page, int number, boolean isBg, String previewBgColor) {
        super.setSnapsPage(page, number, isBg, previewBgColor);
    }

    @Override
    protected void loadBonusLayer() {
        FrameLayout.LayoutParams layout = new FrameLayout.LayoutParams(width, height);
        SnapsImageView iv_saftyzone = new SnapsImageView(getContext());
        iv_saftyzone.setLayoutParams(layout);

        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        String pdCode = productInfoManager.getPROD_CODE();

        String skinImageName;

        if (pdCode.equals(Const_PRODUCT.FABRIC_POSTER_A1)) {
            skinImageName = SnapsSkinConstants.FABRIC_POSTER_SAFETY_ZONE_A1;

        } else if (pdCode.equals(Const_PRODUCT.FABRIC_POSTER_A2)) {
            skinImageName = SnapsSkinConstants.FABRIC_POSTER_SAFETY_ZONE_A2;

        } else {
            skinImageName = SnapsSkinConstants.FABRIC_POSTER_SAFETY_ZONE_A3;
        }

        try {
            SnapsSkinUtil.loadSkinImage(new SnapsSkinRequestAttribute.Builder()
                    .setContext(getContext())
                    .setResourceFileName(skinImageName)
                    .setSkinBackgroundView(iv_saftyzone).create());

        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        bonusLayer.removeAllViews();
        bonusLayer.addView(iv_saftyzone);
    }

    @Override
    protected void loadShadowLayer() {

    }

    @Override
    protected void loadPageLayer() {

    }

    @Override
    protected void initMargin() {

    }
}
