package com.snaps.mobile.activity.edit.spc;

import android.content.Context;
import android.widget.FrameLayout;

import com.snaps.common.structure.control.SnapsControl;
import com.snaps.common.structure.control.SnapsLayoutControl;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.R;
import com.snaps.mobile.component.ColorBorderView;

public class PremiumAcrylFrameCanvas extends ThemeBookCanvas {
	private static final String TAG = PremiumAcrylFrameCanvas.class.getSimpleName();
	public PremiumAcrylFrameCanvas(Context context) {
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
            shadowLayer.setBackgroundResource(R.drawable.frame_acrylic_shadow);
		} catch (OutOfMemoryError e) {
			Dlog.e(TAG, e);
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
		leftMargin = (int)Const_PRODUCT.ACRYL_FRAME_MARGIN_LIST[0];
        topMargin = (int)Const_PRODUCT.ACRYL_FRAME_MARGIN_LIST[1];
        rightMargin = (int)Const_PRODUCT.ACRYL_FRAME_MARGIN_LIST[2];
        bottomMargin = (int)Const_PRODUCT.ACRYL_FRAME_MARGIN_LIST[3];

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
