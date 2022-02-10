package com.snaps.mobile.activity.edit.spc;

import android.content.Context;
import android.widget.RelativeLayout;

import com.snaps.common.spc.SnapsFrameLayout;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.activity.edit.skin.SnapsSkinConstants;
import com.snaps.mobile.activity.edit.skin.SnapsSkinRequestAttribute;
import com.snaps.mobile.activity.edit.skin.SnapsSkinUtil;

public class PhotoMugCupCanvas extends ThemeBookCanvas {
    private static final String TAG = PhotoMugCupCanvas.class.getSimpleName();

	public PhotoMugCupCanvas(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

    /**
     * 배경색 변경
     * @param color
     */
    @Override
    public void setBgColor(int color) {
        color = 0xFFEEEEEE;
        super.setBgColor(color);
    }

	@Override
	protected void loadShadowLayer() {
        SnapsFrameLayout shadow = new SnapsFrameLayout(getContext());
        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(pageLayer.getLayoutParams());
        shadow.setLayoutParams( param );
        try {
            SnapsSkinUtil.loadSkinImage(new SnapsSkinRequestAttribute.Builder()
                    .setContext(getContext())
                    .setResourceFileName(SnapsSkinConstants.MUG_CUP_FILE_NAME)
                    .setSkinBackgroundView(shadow).create());
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        shadowLayer.addView( shadow );
	}

	@Override
	protected void loadPageLayer() {
        containerLayer.setScaleX( 0.95f );
        containerLayer.setScaleY( 0.925f );
	}

    @Override
	protected void loadBonusLayer() {

    }

    @Override
    protected void initMargin() {
        leftMargin = Const_PRODUCT.PHOTO_MUG_MARGIN_LIST[0];
        rightMargin = Const_PRODUCT.PHOTO_MUG_MARGIN_LIST[2];
        topMargin = Const_PRODUCT.PHOTO_MUG_MARGIN_LIST[1];
        bottomMargin = Const_PRODUCT.PHOTO_MUG_MARGIN_LIST[3];

        if( isThumbnailView() ) {
            leftMargin = 0;
            rightMargin = 0;
            topMargin = 0;
            bottomMargin = 0;
        }
    }
}
