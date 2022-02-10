package com.snaps.mobile.activity.edit.spc;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.snaps.common.spc.SnapsPageCanvas;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.DataTransManager;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.edit.skin.SnapsSkinConstants;
import com.snaps.mobile.activity.edit.skin.SnapsSkinRequestAttribute;
import com.snaps.mobile.activity.edit.skin.SnapsSkinUtil;

import static com.snaps.common.utils.constant.Const_PRODUCT.RECTANGLE_STICKER;
import static com.snaps.common.utils.constant.Const_PRODUCT.ROUND_STICKER;
import static com.snaps.common.utils.constant.Const_PRODUCT.SQUARE_STICKER;

/**
 *
 * com.snaps.kakao.activity.edit.spc
 * SnapsPageCanvas.java
 *
 * @author JaeMyung Park
 * @Date : 2013. 5. 23.
 * @Version : 
 */
public class StickerPageCanvas extends SnapsPageCanvas {
	private static final String TAG = StickerPageCanvas.class.getSimpleName();

	public StickerPageCanvas(Context context) {
		super(context);
	}
	
	public StickerPageCanvas ( Context context, AttributeSet attr ) {
		super( context , attr );
	}

	@Override
	protected void loadShadowLayer() {
		if(!(Const_PRODUCT.isNameStickerProduct() || Const_PRODUCT.isLongPhotoStickerProduct())) {
			try {
				shadowLayer.setBackgroundResource(R.drawable.sticker_shadow);
			} catch (OutOfMemoryError e) {
				Dlog.e(TAG, e);
			}
		}
	}

	@Override
	protected void loadPageLayer() {
		// 책등이나 필요한 부분을 넣어준다.
	}
	
	@Override
	protected void loadBonusLayer() {
		String skinName = getSkinName();
		if( !StringUtil.isEmpty(skinName) ) {
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
		switch (Config.getPROD_CODE()) {
			case ROUND_STICKER:
				return SnapsSkinConstants.STICKER_ROUND;
			case SQUARE_STICKER:
				return SnapsSkinConstants.STICKER_SQUARE;
			case RECTANGLE_STICKER:
				return SnapsSkinConstants.STICKER_RECTANGLE;
			default:
				return "";

		}
	}


	@Override
	protected void initMargin() {
		leftMargin = UIUtil.convertDPtoPX(getContext(), Config.STICKER_MARGIN_LIST[ 0 ]);
		topMargin = UIUtil.convertDPtoPX(getContext(), Config.STICKER_MARGIN_LIST[ 1 ]);
		rightMargin = UIUtil.convertDPtoPX(getContext(), Config.STICKER_MARGIN_LIST[ 2 ]);
		bottomMargin = UIUtil.convertDPtoPX(getContext(), Config.STICKER_MARGIN_LIST[ 3 ]);

		if(Const_PRODUCT.isNameStickerProduct() || Const_PRODUCT.isLongPhotoStickerProduct()) {
			leftMargin = UIUtil.convertDPtoPX(getContext(), 0);
			topMargin = UIUtil.convertDPtoPX(getContext(), 0);
			rightMargin = UIUtil.convertDPtoPX(getContext(), 0);
			bottomMargin = UIUtil.convertDPtoPX(getContext(), 0);
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
