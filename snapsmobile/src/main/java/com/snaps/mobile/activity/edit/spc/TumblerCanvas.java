package com.snaps.mobile.activity.edit.spc;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.edit.skin.SnapsSkinConstants;
import com.snaps.mobile.activity.edit.skin.SnapsSkinRequestAttribute;
import com.snaps.mobile.activity.edit.skin.SnapsSkinUtil;

public class TumblerCanvas extends ThemeBookCanvas {
    private static final String TAG = TumblerCanvas.class.getSimpleName();

	public TumblerCanvas(Context context) {
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

	}

	@Override
	protected void loadBonusLayer() {

		String productCode = null;
		
		try {
			productCode = getSnapsPage().info.F_PROD_CODE;
		} catch (NullPointerException e) {
            Dlog.e(TAG, e);
		}
		
		try {
			if(productCode != null && productCode.length() > 0)
				//bonusLayer.addView();
                makeTumblerSkin(productCode);
		} catch (OutOfMemoryError e) {
            Dlog.e(TAG, e);
		}
	}

	@Override
	protected void initMargin() {
        leftMargin = Const_PRODUCT.TUMBLER_MARGIN_LIST[0];
        topMargin = Const_PRODUCT.TUMBLER_MARGIN_LIST[1];
        rightMargin = Const_PRODUCT.TUMBLER_MARGIN_LIST[2];
        bottomMargin = Const_PRODUCT.TUMBLER_MARGIN_LIST[3];

        if( isThumbnailView() ) {
            leftMargin = 0;
            rightMargin = 0;
            topMargin = 0;
            bottomMargin = 0;
        }
	}
	
//	View makeTumblerSkin(String productCode) {
//		ViewGroup skinView = new FrameLayout(getContext());
//		skinView.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
//
//		boolean bPremium = productCode.equals(Const_PRODUCT.PRODUCT_TUMBLR_GRADE);
//        String skinName = bPremium ? SnapsSkinConstants.TUMBLER_12OZ_FILE_NAME : SnapsSkinConstants.TUMBLER_11OZ_FILE_NAME;
//
//        try {
//            SnapsSkinUtil.loadSkinImage(new SnapsSkinRequestAttribute.Builder()
//                    .setContext(getContext())
//                    .setResourceFileName(skinName)
//                    .setSkinBackgroundView(skinView).create());
//        } catch (Exception e) {
//            Dlog.e(TAG, e);
//        }
//
//        if( bPremium ) {
//            int[] margins = new int[]{ -24, 20, -42, 0 };
//            int height = 150;
//            skinView.addView( new TumblerTextView(getContext(), getContext().getString(R.string.area_covered_by_cap), margins, height) );
//        }
//
//		return skinView;
//	}

    void makeTumblerSkin(String productCode) {
//		ViewGroup skinView = new FrameLayout(getContext());
//		skinView.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        boolean bPremium = productCode.equals(Const_PRODUCT.PRODUCT_TUMBLR_GRADE);
        String skinName = bPremium ? SnapsSkinConstants.TUMBLER_12OZ_FILE_NAME : SnapsSkinConstants.TUMBLER_11OZ_FILE_NAME;

        try {
            SnapsSkinUtil.loadSkinImage(new SnapsSkinRequestAttribute.Builder()
                    .setContext(getContext())
                    .setResourceFileName(skinName)
                    .setSkinBackgroundView(bonusLayer).create());
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        if( bPremium ) {
            int[] margins = new int[]{ -24, 20, -42, 0 };
            int height = 150;
            bonusLayer.addView( new TumblerTextView(getContext(), getContext().getString(R.string.area_covered_by_cap), margins, height) );
        }
    }


    private class TumblerTextView extends View {
        private String text;
        private int[] margins;
        private int height;

        public TumblerTextView(Context context, String text, int[] margins, int height ) {
            super(context);
            this.text = text;
            this.margins = margins;
            this.height = height;
        }

        @Override
        public void draw(Canvas canvas) {
            super.draw(canvas);

            Path path = new Path();
            RectF rect = new RectF();
            rect.left = margins[0];
            rect.right = getWidth() - margins[2];
            rect.top = margins[1];
            rect.bottom = height - margins[3];
            path.addArc( rect, 180, 180 );

            Paint paint = new Paint();
            paint.setColor( Color.WHITE );
            paint.setTextAlign( Paint.Align.CENTER );
            paint.setAntiAlias( true );
            paint.setTextSize( 15f );
            canvas.drawTextOnPath( text, path, margins[0], margins[1], paint );
        }
    }
}
