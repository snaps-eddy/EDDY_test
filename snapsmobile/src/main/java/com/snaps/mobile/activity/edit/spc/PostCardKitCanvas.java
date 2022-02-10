package com.snaps.mobile.activity.edit.spc;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.snaps.common.spc.SnapsPageCanvas;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.activity.edit.skin.SnapsSkinConstants;
import com.snaps.mobile.activity.edit.skin.SnapsSkinRequestAttribute;
import com.snaps.mobile.activity.edit.skin.SnapsSkinUtil;

public class PostCardKitCanvas extends SnapsPageCanvas {
	private static final String TAG = PostCardKitCanvas.class.getSimpleName();

	public PostCardKitCanvas(Context context) {
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
        try {
			SnapsSkinUtil.loadSkinImage(new SnapsSkinRequestAttribute.Builder()
					.setContext(getContext())
					.setResourceFileName(SnapsSkinConstants.POST_CARD_FILE_NAME)
					.setSkinBackgroundView(bonusLayer).create());

			bonusLayer.setAlpha( 0.7f );
			containerLayer.setScaleX( 0.815f );
			containerLayer.setScaleY( 0.817f );
        } catch (OutOfMemoryError | Exception e) {
			Dlog.e(TAG, e);
        }
	}

	@Override
	protected void initMargin() {
		leftMargin = Const_PRODUCT.POST_CARD_MARGIN_LIST[0];
		topMargin = Const_PRODUCT.POST_CARD_MARGIN_LIST[1];
		rightMargin = Const_PRODUCT.POST_CARD_MARGIN_LIST[2];
		bottomMargin = Const_PRODUCT.POST_CARD_MARGIN_LIST[3];

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
