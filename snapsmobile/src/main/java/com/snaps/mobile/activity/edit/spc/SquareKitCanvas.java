package com.snaps.mobile.activity.edit.spc;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.snaps.common.spc.SnapsPageCanvas;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.activity.edit.skin.SnapsSkinConstants;
import com.snaps.mobile.activity.edit.skin.SnapsSkinRequestAttribute;
import com.snaps.mobile.activity.edit.skin.SnapsSkinUtil;
import com.snaps.mobile.utils.custom_layouts.ARelativeLayoutParams;

public class SquareKitCanvas extends SnapsPageCanvas {
	private static final String TAG = SquareKitCanvas.class.getSimpleName();

	public SquareKitCanvas(Context context) {
		super(context);
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
			String fileName = is4X4Size() ? SnapsSkinConstants.SQUARE_4X4_FILE_NAME : SnapsSkinConstants.SQUARE_5X5_FILE_NAME;

			SnapsSkinUtil.loadSkinImage(new SnapsSkinRequestAttribute.Builder()
					.setContext(getContext())
					.setResourceFileName(fileName)
					.setSkinBackgroundView(bonusLayer).create());

			ARelativeLayoutParams params = (ARelativeLayoutParams) bonusLayer.getLayoutParams();
			params.leftMargin = 10;
			params.topMargin = 10;
			bonusLayer.setAlpha(.4f);
			bonusLayer.setLayoutParams(params);
		} catch (OutOfMemoryError | Exception e) {
			Dlog.e(TAG, e);
        }
	}

	private boolean is4X4Size() {
        return "00800900180001".equalsIgnoreCase( Config.getPROD_CODE() );
    }

	@Override
	protected void initMargin() {
		leftMargin = Const_PRODUCT.SQUARE_MARGIN_LIST[0];
		topMargin = Const_PRODUCT.SQUARE_MARGIN_LIST[1];
		rightMargin = Const_PRODUCT.SQUARE_MARGIN_LIST[2];
		bottomMargin = Const_PRODUCT.SQUARE_MARGIN_LIST[3];

		if (isThumbnailView()) {
			leftMargin = 0;
			rightMargin = 0;
			topMargin = 0;
			bottomMargin = 0;
		}
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
