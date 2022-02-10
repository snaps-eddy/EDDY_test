package com.snaps.mobile.activity.edit.spc;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.snaps.common.spc.SnapsPageCanvas;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.activity.edit.skin.SnapsSkinConstants;
import com.snaps.mobile.activity.edit.skin.SnapsSkinRequestAttribute;
import com.snaps.mobile.activity.edit.skin.SnapsSkinUtil;

public class WoodBlockCanvas extends SnapsPageCanvas {
	private static final String TAG = WoodBlockCanvas.class.getSimpleName();
	public WoodBlockCanvas(Context context) {
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
			if (getSnapsPage().type.equalsIgnoreCase("page")) {
				ImageView skin = new ImageView(getContext());
				RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(bonusLayer.getLayoutParams());
				param.width = pageLayer.getLayoutParams().width + rightMargin + leftMargin;
				param.height = pageLayer.getLayoutParams().height + topMargin + bottomMargin;
				skin.setLayoutParams( param );

				SnapsSkinUtil.loadSkinImage(new SnapsSkinRequestAttribute.Builder()
						.setContext(getContext())
						.setResourceFileName(SnapsSkinConstants.WOOD_BLOCK_SKIN_FILE_NAME)
						.setSkinBackgroundView(skin).create());

				bonusLayer.addView( skin );
			}
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
	}

	@Override
	protected void initMargin() {
		leftMargin = Const_PRODUCT.WOOD_BLOCK_MARGIN_LIST[0];
		topMargin = Const_PRODUCT.WOOD_BLOCK_MARGIN_LIST[1];
		rightMargin = Const_PRODUCT.WOOD_BLOCK_MARGIN_LIST[2];
		bottomMargin = Const_PRODUCT.WOOD_BLOCK_MARGIN_LIST[3];

		if (isThumbnailView()) {
			leftMargin = 0;
			rightMargin = 0;
			topMargin = 0;
			bottomMargin = 0;
		}
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
