package com.snaps.mobile.activity.edit.spc;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.snaps.common.spc.SnapsFrameLayout;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.activity.edit.skin.SnapsSkinConstants;
import com.snaps.mobile.activity.edit.skin.SnapsSkinRequestAttribute;
import com.snaps.mobile.activity.edit.skin.SnapsSkinUtil;

public class MousePadCanvas extends ThemeBookCanvas {
	private static final String TAG = MousePadCanvas.class.getSimpleName();

	public MousePadCanvas(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
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
		// TODO Auto-generated method stub

	}

	@Override
	protected void loadBonusLayer() {
        ImageView skinView = new ImageView(getContext());
        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(pageLayer.getLayoutParams());
        param.width = pageLayer.getLayoutParams().width + rightMargin + leftMargin;
        param.height = pageLayer.getLayoutParams().height + topMargin + bottomMargin;
        skinView.setLayoutParams( param );

		try {
			SnapsSkinUtil.loadSkinImage(new SnapsSkinRequestAttribute.Builder()
					.setContext(getContext())
					.setResourceFileName(SnapsSkinConstants.MOUSE_PAD_FILE_NAME)
					.setSkinBackgroundView(skinView).create());
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}

        bonusLayer.addView( skinView );
	}

	@Override
	protected void initMargin() {
		leftMargin = Const_PRODUCT.MOUSE_PAD_MARGIN_LIST[0];
		topMargin = Const_PRODUCT.MOUSE_PAD_MARGIN_LIST[1];
		rightMargin = Const_PRODUCT.MOUSE_PAD_MARGIN_LIST[2];
		bottomMargin = Const_PRODUCT.MOUSE_PAD_MARGIN_LIST[3];

		if (isThumbnailView()) {
			leftMargin = 0;
			rightMargin = 0;
			topMargin = 0;
			bottomMargin = 0;
		}
	}

	View makeShadow() {
		SnapsFrameLayout shadow = new SnapsFrameLayout(getContext());
		RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(pageLayer.getLayoutParams());
		param.width = pageLayer.getLayoutParams().width + rightMargin + leftMargin;
		param.height = pageLayer.getLayoutParams().height + topMargin + bottomMargin;
		shadow.addRound(param.width, param.height);
		shadow.setBackgrounColor("#000000");

		return shadow;
	}

}
