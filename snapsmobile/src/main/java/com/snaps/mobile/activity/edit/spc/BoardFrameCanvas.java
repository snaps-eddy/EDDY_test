package com.snaps.mobile.activity.edit.spc;

import android.content.Context;
import android.widget.FrameLayout;

import com.snaps.common.structure.control.SnapsControl;
import com.snaps.common.structure.control.SnapsLayoutControl;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.R;
import com.snaps.mobile.component.ColorBorderView;

import static com.snaps.common.utils.constant.Const_PRODUCT.PRODUCT_PORM_BOARD_FRAME;

public class BoardFrameCanvas extends ThemeBookCanvas {
	private static final String TAG = BoardFrameCanvas.class.getSimpleName();

	public BoardFrameCanvas(Context context) {
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
		try {

			shadowLayer.setBackgroundResource(getShdowType());
		} catch (OutOfMemoryError e) {
			Dlog.e(TAG, e);
		}
	}

	private int getShdowType() {
		String prodCode = Config.getPROD_CODE();

		String type = prodCode.substring(prodCode.length() -2);
		if(type.equals("19") || type.equals("06")) {
			if(prodCode.contains(PRODUCT_PORM_BOARD_FRAME)) {
				return R.drawable.frame_board_white_bold_shadow;
			} else {
				return R.drawable.frame_board_black_bold_shadow;
			}
		} else {
			if(prodCode.contains(PRODUCT_PORM_BOARD_FRAME)) {
				return R.drawable.frame_board_white_thin_shadow;
			} else {
				return R.drawable.frame_board_black_thin_shadow;
			}
		}
	}
	
	@Override
	protected void loadPageLayer() {
		// 사진틀은 만든다..
		for (SnapsControl control : getSnapsPage().getLayoutList()) {
			if (control instanceof SnapsLayoutControl) {

				if (((SnapsLayoutControl) control).bordersinglecolortype.equals(""))
					continue;

				addBorderView(control, pageLayer);

			}
		}

	}

	@Override
	protected void loadBonusLayer() {
	}

	@Override
	protected void initMargin() {

		leftMargin = (int)Const_PRODUCT.BOARD_FRAME_MARGIN_LIST[0];
		topMargin = (int)Const_PRODUCT.BOARD_FRAME_MARGIN_LIST[1];
		rightMargin = (int)Const_PRODUCT.BOARD_FRAME_MARGIN_LIST[2];
		bottomMargin = (int)Const_PRODUCT.BOARD_FRAME_MARGIN_LIST[3];

		if (isThumbnailView()) {
			leftMargin = 0;
			rightMargin = 0;
			topMargin = 0;
			bottomMargin = 0;
		}
	}

	void addBorderView(SnapsControl control, FrameLayout pageLayer) {
		ColorBorderView border = new ColorBorderView(getContext());

		MarginLayoutParams params = new MarginLayoutParams(Integer.parseInt(control.width), Integer.parseInt(control.height));
		border.setLayoutParams(new FrameLayout.LayoutParams(params));

		border.setX(control.getX());
		border.setY(Integer.parseInt(control.y));
		border.setBorderWidth((SnapsLayoutControl) control);

		layoutLayer.addView(border);
	}
}
