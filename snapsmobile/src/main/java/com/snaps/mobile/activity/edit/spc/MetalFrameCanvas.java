package com.snaps.mobile.activity.edit.spc;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.snaps.common.spc.SnapsFrameLayout;
import com.snaps.common.structure.control.SnapsControl;
import com.snaps.common.structure.control.SnapsLayoutControl;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.R;
import com.snaps.mobile.component.ColorBorderView;
import com.snaps.mobile.component.CombinedFrameShadow;

public class MetalFrameCanvas extends ThemeBookCanvas {
	private static final String TAG = MetalFrameCanvas.class.getSimpleName();
	public MetalFrameCanvas(Context context) {
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
		getShdowType();
	}

	private void getShdowType() {
		if(TextUtils.isEmpty(Config.getBACK_TYPE())) {
			int pxW = Integer.parseInt(getSnapsPage().info.F_PAGE_PIXEL_WIDTH);
			int mmW = Integer.parseInt(getSnapsPage().info.F_PAGE_MM_WIDTH);
			int shadowContentSize = pxW / mmW * Const_PRODUCT.METAL_FRAME_SHADOW_CONTENT_SIZE;

			int[] newShadowOnlySize = new int[4];
			for( int i = 0; i < newShadowOnlySize.length; ++i )
				newShadowOnlySize[i] = (int)( Const_PRODUCT.METAL_FRAME_OUTER_SHADOW_SIZE[i] * (float)pxW / (float)mmW );

			ViewGroup.LayoutParams params = shadowLayer.getLayoutParams();
			shadowLayer.addView( new CombinedFrameShadow(getContext(), Integer.parseInt(getSnapsPage().info.F_PAGE_MM_WIDTH), Integer.parseInt(getSnapsPage().info.F_PAGE_MM_HEIGHT), params.width, params.height, shadowContentSize, newShadowOnlySize, new String[]{"frame_metal_desk_bold_center", "frame_metal_desk_bold_left", "frame_metal_desk_bold_top", "frame_metal_desk_bold_right", "frame_metal_desk_bold_bottom"}) );
		} else {
			String prodCode = Config.getPROD_CODE();
			String type = prodCode.substring(prodCode.length() -2);
			int resId = 0;
			if(type.equals("02") || type.equals("03") || type.equals("04")) {
					resId =R.drawable.frame_metal_wall_bold_shadow;
			} else {
					resId = R.drawable.frame_metal_wall_thin_shadow;
			}
			shadowLayer.setBackgroundResource(resId);
		}
	}

	@Override
	protected void loadPageLayer() {
		// 사진틀은 만든다..
	}
	@Override
	protected void loadBonusLayer() {

        if ( !getSnapsPage().info.F_FRAME_TYPE.equals(Const_PRODUCT.FRAME_TYPE_BRUSH) ) return;

        try {
            SnapsFrameLayout frameLayout = new SnapsFrameLayout( getContext() );
            ResourceSelector.setPatternResource( frameLayout, "frame_metal_pattern_brush" );
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT );
            params.setMargins( leftMargin, topMargin, rightMargin, bottomMargin );
            frameLayout.setLayoutParams( params );
            bonusLayer.addView( frameLayout );
        } catch (OutOfMemoryError e) {
			Dlog.e(TAG, e);
        }


	}

	@Override
	protected void initMargin() {

		int[]margin = getMargin();


        leftMargin = margin[0];
        topMargin = margin[1];
        rightMargin = margin[2];
        bottomMargin = margin[3];

		if (isThumbnailView()) {
			leftMargin = 0;
			rightMargin = 0;
			topMargin = 0;
			bottomMargin = 0;
		}
	}

	private int[] getMargin() {
		int[] margin = new int[4];
		if(TextUtils.isEmpty(Config.getBACK_TYPE())) {
			int pxW = Integer.parseInt(getSnapsPage().info.F_PAGE_PIXEL_WIDTH);
			int mmW = Integer.parseInt(getSnapsPage().info.F_PAGE_MM_WIDTH);

			margin[0]= (int)( (float) pxW / (float) mmW * Const_PRODUCT.METAL_FRAME_OUTER_SHADOW_SIZE[0] );
			margin[1] = (int)( (float) pxW / (float) mmW * Const_PRODUCT.METAL_FRAME_OUTER_SHADOW_SIZE[1] );
			margin[2] = (int)( (float) pxW / (float) mmW * Const_PRODUCT.METAL_FRAME_OUTER_SHADOW_SIZE[2] );
			margin[3] = (int)( (float) pxW / (float) mmW * Const_PRODUCT.METAL_FRAME_OUTER_SHADOW_SIZE[3] );
		} else {
			margin[0]=32;
			margin[1] =0;
			margin[2] = 32;
			margin[3] = 44;
		}

		return margin;
	}

	View makeBrush() {
		View v = new View(getContext());
		RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(pageLayer.getLayoutParams());
		param.width = pageLayer.getLayoutParams().width;
		param.height = pageLayer.getLayoutParams().height;
		v.setLayoutParams(param);
		v.setBackgroundResource(R.drawable.metal_silver);
		return v;
	}

	View makeShadow() {
		SnapsFrameLayout shadow = new SnapsFrameLayout(getContext());
		RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(pageLayer.getLayoutParams());
		param.width = pageLayer.getLayoutParams().width + leftMargin + rightMargin;
		param.height = pageLayer.getLayoutParams().height + topMargin + bottomMargin;
		shadow.setLayout(param);
		shadow.addRound(param.width, param.height);
		shadow.addBorder("#cacaca", "#cccccc", "#969696", 2);

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
