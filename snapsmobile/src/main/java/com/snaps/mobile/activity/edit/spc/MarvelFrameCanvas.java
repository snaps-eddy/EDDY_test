package com.snaps.mobile.activity.edit.spc;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.snaps.common.spc.SnapsFrameLayout;
import com.snaps.common.structure.control.SnapsControl;
import com.snaps.common.structure.control.SnapsLayoutControl;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.component.ColorBorderView;
import com.snaps.mobile.component.CombinedFrameShadow;

public class MarvelFrameCanvas extends ThemeBookCanvas {
	private static final String TAG = MarvelFrameCanvas.class.getSimpleName();

	public MarvelFrameCanvas(Context context) {
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
            int pxW = Integer.parseInt(getSnapsPage().info.F_PAGE_PIXEL_WIDTH);
            int mmW = Integer.parseInt(getSnapsPage().info.F_PAGE_MM_WIDTH);
            int shadowContentSize = pxW / mmW * Const_PRODUCT.MARVEL_FRAME_SHADOW_CONTENT_SIZE;

            int[] newShadowOnlySize = new int[4];
            for( int i = 0; i < newShadowOnlySize.length; ++i )
                newShadowOnlySize[i] = (int)( Const_PRODUCT.MARVEL_FRAME_OUTER_SHADOW_SIZE[i] * (float)pxW / (float)mmW );

            ViewGroup.LayoutParams params = shadowLayer.getLayoutParams();
            shadowLayer.addView( new CombinedFrameShadow(getContext(), Integer.parseInt(getSnapsPage().info.F_PAGE_MM_WIDTH), Integer.parseInt(getSnapsPage().info.F_PAGE_MM_HEIGHT), params.width, params.height, shadowContentSize, newShadowOnlySize, new String[]{"frame_mable_center", "frame_mable_left", "frame_mable_top", "frame_mable_right", "frame_mable_bottom"}) );
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
        int pxW = Integer.parseInt(getSnapsPage().info.F_PAGE_PIXEL_WIDTH);
        int mmW = Integer.parseInt(getSnapsPage().info.F_PAGE_MM_WIDTH);

        leftMargin = (int)( (float) pxW / (float) mmW * Const_PRODUCT.MARVEL_FRAME_OUTER_SHADOW_SIZE[0] );
        topMargin = (int)( (float) pxW / (float) mmW * Const_PRODUCT.MARVEL_FRAME_OUTER_SHADOW_SIZE[1] );
        rightMargin = (int)( (float) pxW / (float) mmW * Const_PRODUCT.MARVEL_FRAME_OUTER_SHADOW_SIZE[2] );
        bottomMargin = (int)( (float) pxW / (float) mmW * Const_PRODUCT.MARVEL_FRAME_OUTER_SHADOW_SIZE[3] );

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
		param.width = pageLayer.getLayoutParams().width + rightMargin;
		param.height = pageLayer.getLayoutParams().height + topMargin;
		shadow.addRound(param.width, param.height);
		shadow.setBackgrounColor("#000000");

		return shadow;
	}

	void addBorderView(SnapsControl control, FrameLayout pageLayer) {
		ColorBorderView border = new ColorBorderView(getContext());

		ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(Integer.parseInt(control.width), Integer.parseInt(control.height));
		border.setLayoutParams(new FrameLayout.LayoutParams(params));

		border.setX(control.getX());
		border.setY(Integer.parseInt(control.y));
		border.setBorderWidth((SnapsLayoutControl) control);

		layoutLayer.addView(border);
	}
}
