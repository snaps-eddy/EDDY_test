package com.snaps.mobile.activity.edit.spc;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.snaps.common.data.img.BPoint;
import com.snaps.common.spc.SnapsPageCanvas;
import com.snaps.common.utils.ui.UIUtil;

public class SloganCanvas extends SnapsPageCanvas {

	public SloganCanvas(Context context) {
		super(context);
	}

	public SloganCanvas(Context context, AttributeSet attr) {
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
	protected BPoint getCanvasOffsetPoint() {
		int x = 0, y = 0;
		if(isLandscapeMode()) {
			y = UIUtil.convertDPtoPX(mContext, 48);
			x = UIUtil.convertDPtoPX(mContext, 112);
		} else {
			y = UIUtil.convertDPtoPX(mContext, 48);
		}

		return new BPoint(x, y);
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