package com.snaps.mobile.activity.edit.spc;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.snaps.common.spc.SnapsPageCanvas;
import com.snaps.common.structure.control.SnapsControl;
import com.snaps.common.structure.control.SnapsLayoutControl;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.component.CombinedFrameShadow;
import com.snaps.mobile.component.WoodFrameBorderView;
import com.snaps.mobile.component.WoodFrameView;

public class WoodFrameCanvas extends SnapsPageCanvas {
	private static final String TAG = WoodFrameCanvas.class.getSimpleName();
	public WoodFrameCanvas(Context context) {
		super(context);
	}

    @Override
    public void setBgColor(int color) {
        color = 0xFFEEEEEE;
        super.setBgColor(color);
    }


	@Override
	protected void loadShadowLayer() {
        int pxW = Integer.parseInt(getSnapsPage().info.F_PAGE_PIXEL_WIDTH);
        int mmW = Integer.parseInt(getSnapsPage().info.F_PAGE_MM_WIDTH);
        int shadowContentSize = pxW / mmW * Const_PRODUCT.WOOD_FRAME_SHADOW_CONTENT_SIZE;

        int[] newShadowOnlySize = new int[4];
        for( int i = 0; i < newShadowOnlySize.length; ++i )
            newShadowOnlySize[i] = (int)( Const_PRODUCT.WOOD_FRAME_OUTER_SHADOW_SIZE[i] * (float)pxW / (float)mmW );

        ViewGroup.LayoutParams params = shadowLayer.getLayoutParams();
        shadowLayer.addView( new CombinedFrameShadow(getContext(), Integer.parseInt(getSnapsPage().info.F_PAGE_MM_WIDTH), Integer.parseInt(getSnapsPage().info.F_PAGE_MM_HEIGHT), params.width, params.height, shadowContentSize, newShadowOnlySize, new String[]{"frame_wood_shadow_center", "frame_wood_shadow_left", "", "frame_wood_shadow_right", "frame_wood_shadow_bottom"}) );
	}

	@Override
	protected void loadPageLayer() {
		try {
			// 매트 그리기.. 매트일때만 그린다.
			if (getSnapsPage().info.F_FRAME_TYPE.equals(Const_PRODUCT.FRAME_TYPE_MET)) {
				int pxW = Integer.parseInt(getSnapsPage().info.F_PAGE_PIXEL_WIDTH);
				int mmW = Integer.parseInt(getSnapsPage().info.F_PAGE_MM_WIDTH);
		
				float borderW = pxW / mmW * 2;
		
				for (SnapsControl control : getSnapsPage().getLayoutList()) {
					if (control instanceof SnapsLayoutControl) {
		
						if (control.regName.equals("") || control.regName.equals("background"))
							continue;
		
						addBorderView(control, pageLayer, borderW);
		
					}
				}
			}
		} catch (OutOfMemoryError e) {
			Dlog.e(TAG, e);
		}
	}

	@Override
	protected void loadBonusLayer() {
		try {
			// 프레임 그리기
            int pxW = Integer.parseInt(getSnapsPage().info.F_PAGE_PIXEL_WIDTH);
            int mmW = Integer.parseInt(getSnapsPage().info.F_PAGE_MM_WIDTH);
            int frameSize = pxW / mmW * Const_PRODUCT.WOOD_FRAME_BORDER_PADDING[0];
            String[] images = new String[4];
            if (getSnapsPage().info.F_FRAME_ID.equals("045014000203")) { //월넛..
                images[0] = "frame_wood_brown_mid_left";
                images[1] = "frame_wood_brown_top_center";
                images[2] = "frame_wood_brown_mid_right";
                images[3] = "frame_wood_brown_bottom_center";
            }
            else if (!getSnapsPage().info.F_FRAME_ID.equals("045014000171")) { // 블랙원목액자..
                images[0] = "frame_wood_black_mid_left";
                images[1] = "frame_wood_black_top_center";
                images[2] = "frame_wood_black_mid_right";
                images[3] = "frame_wood_black_bottom_center";
            }
            else {
                images[0] = "frame_wood_natural_mid_left";
                images[1] = "frame_wood_natural_top_center";
                images[2] = "frame_wood_natural_mid_right";
                images[3] = "frame_wood_natural_bottom_center";
            }

            int frameLeftMargin = (int)( pxW / mmW * Const_PRODUCT.WOOD_FRAME_OUTER_SHADOW_SIZE[0] );
            int frameTopMargin = (int)( pxW / mmW * Const_PRODUCT.WOOD_FRAME_OUTER_SHADOW_SIZE[1] );
            int frameRightMargin = (int)( pxW / mmW * Const_PRODUCT.WOOD_FRAME_OUTER_SHADOW_SIZE[2] );
            int frameBottomMargin = (int)( pxW / mmW * Const_PRODUCT.WOOD_FRAME_OUTER_SHADOW_SIZE[3] );

            ViewGroup.LayoutParams params = bonusLayer.getLayoutParams();
			WoodFrameView woodFrameView = new WoodFrameView(getContext(), new int[]{frameLeftMargin, frameTopMargin, frameRightMargin, frameBottomMargin},
					params.width - frameLeftMargin - frameRightMargin, params.height - frameTopMargin - frameBottomMargin, frameSize, images );

            bonusLayer.addView( woodFrameView );

			// 프레임 가이드 그리기(빨간선)
			if (getSnapsPage().info.F_FRAME_TYPE.equals(Const_PRODUCT.FRAME_TYPE_WOOD))
				woodFrameView.makeWoodFrameGuide();
		} catch (OutOfMemoryError e) {
			Dlog.e(TAG, e);
		}
	}

	@Override
	protected void initMargin() {
        int pxW = Integer.parseInt(getSnapsPage().info.F_PAGE_PIXEL_WIDTH);
        int mmW = Integer.parseInt(getSnapsPage().info.F_PAGE_MM_WIDTH);

        leftMargin = (int)( (float) pxW / (float) mmW * (Const_PRODUCT.WOOD_FRAME_BORDER_PADDING[0] - Const_PRODUCT.WOOD_FRAME_BORDER_PADDING[1] + Const_PRODUCT.WOOD_FRAME_OUTER_SHADOW_SIZE[0]) );
        topMargin = (int)( (float) pxW / (float) mmW * (Const_PRODUCT.WOOD_FRAME_BORDER_PADDING[0] - Const_PRODUCT.WOOD_FRAME_BORDER_PADDING[1] + Const_PRODUCT.WOOD_FRAME_OUTER_SHADOW_SIZE[1]) );
        rightMargin = (int)( (float) pxW / (float) mmW * (Const_PRODUCT.WOOD_FRAME_BORDER_PADDING[0] - Const_PRODUCT.WOOD_FRAME_BORDER_PADDING[1] + Const_PRODUCT.WOOD_FRAME_OUTER_SHADOW_SIZE[2]) );
        bottomMargin = (int)( (float) pxW / (float) mmW * (Const_PRODUCT.WOOD_FRAME_BORDER_PADDING[0] - Const_PRODUCT.WOOD_FRAME_BORDER_PADDING[1] + Const_PRODUCT.WOOD_FRAME_OUTER_SHADOW_SIZE[3]) );

		if (isThumbnailView()) {
			leftMargin = 0;
			rightMargin = 0;
			topMargin = 0;
			bottomMargin = 0;
		}
	}

	/***
	 * 메트를 추가하는 함수.
	 * 
	 * @param control
	 * @param pageLayer
	 * @param borderW
	 */
	void addBorderView(SnapsControl control, FrameLayout pageLayer, float borderW) {
		WoodFrameBorderView border = new WoodFrameBorderView(getContext());
		// 오차로 인해 +2 적용
		ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(Integer.parseInt(control.width) + 2, Integer.parseInt(control.height) + 2);
		border.setLayoutParams(new FrameLayout.LayoutParams(params));

		border.setX(control.getX() - 1);
		border.setY(Integer.parseInt(control.y) - 1);
		border.setBorderWidth((int) borderW);

		pageLayer.addView(border);

	}

	@Override
	public void onDestroyCanvas() {
		super.onDestroyCanvas();		
	}
}
