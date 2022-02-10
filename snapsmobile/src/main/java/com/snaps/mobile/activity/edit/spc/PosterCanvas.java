package com.snaps.mobile.activity.edit.spc;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.snaps.common.spc.SnapsPageCanvas;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.activity.edit.skin.SnapsSkinConstants;
import com.snaps.mobile.activity.edit.skin.SnapsSkinRequestAttribute;
import com.snaps.mobile.activity.edit.skin.SnapsSkinUtil;

import static com.snaps.common.utils.constant.Const_PRODUCT.ACCORDION_CARD_MINI;
import static com.snaps.common.utils.constant.Const_PRODUCT.ACCORDION_CARD_NORMAL;

public class PosterCanvas extends SnapsPageCanvas {

	public PosterCanvas(Context context) {
		super(context);
	}

	public PosterCanvas(Context context, AttributeSet attr) {
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
//		String skinName = getSkinName();
//		if( !StringUtil.isEmpty(skinName) ) {
//			try {
//				SnapsSkinUtil.loadSkinImage(new SnapsSkinRequestAttribute.Builder()
//						.setContext(getContext())
//						.setResourceFileName(skinName)
//						.setSkinBackgroundView(bonusLayer)
//						.create());
//			} catch (Exception e) {
//				Dlog.e(TAG, e);
//			}
//		}
	}

//	private String getSkinName() {
//		switch (Config.getPROD_CODE()) {
//			case ACCORDION_CARD_NORMAL:
//				return SnapsSkinConstants.ACCORDION_CARD_NOMAL_CUT;
//			case ACCORDION_CARD_MINI:
//				return SnapsSkinConstants.ACCORDION_CARD_MINI_NOMAL;
//			default:
//				return "";
//
//		}
//	}

	@Override
	protected void initMargin() {
//		leftMargin = UIUtil.convertDPtoPX(getContext(), Config.ACCORDION_CARD_MARGIN_LIST[ 0 ]);
//		topMargin = UIUtil.convertDPtoPX(getContext(), Config.ACCORDION_CARD_MARGIN_LIST[ 1 ]);
//		rightMargin = UIUtil.convertDPtoPX(getContext(), Config.ACCORDION_CARD_MARGIN_LIST[ 2 ]);
//		bottomMargin = UIUtil.convertDPtoPX(getContext(), Config.ACCORDION_CARD_MARGIN_LIST[ 3 ]);
//
//		if (isThumbnailView()) {
//			leftMargin = 0;
//			rightMargin = 0;
//			topMargin = 0;
//			bottomMargin = 0;
//		}
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