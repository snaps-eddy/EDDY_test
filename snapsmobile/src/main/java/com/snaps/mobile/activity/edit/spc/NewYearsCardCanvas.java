package com.snaps.mobile.activity.edit.spc;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.edit.skin.SnapsSkinConstants;
import com.snaps.mobile.activity.edit.skin.SnapsSkinRequestAttribute;
import com.snaps.mobile.activity.edit.skin.SnapsSkinUtil;
import com.snaps.mobile.activity.themebook.PhotobookCommonUtils;

/**
 * Created by kimduckwon on 2017. 11. 9..
 */

public class NewYearsCardCanvas extends ThemeBookCanvas{
    private static final String TAG = NewYearsCardCanvas.class.getSimpleName();
    public NewYearsCardCanvas(Context context) {
        super(context);
    }

    @Override
    protected void setScaleValue() {
        boolean isPortraitScreen = PhotobookCommonUtils.isPortraitScreenProduct();

        int screenWidth = UIUtil.getScreenWidth(mContext) - UIUtil.convertDPtoPX(mContext,101);
        int screenHeight = UIUtil.getScreenWidth(mContext) - (int) getResources().getDimension(R.dimen.home_title_bar_height) - UIUtil.convertDPtoPX(mContext,101);

        //썸네일을 확대해서 생성한다.
        if (isScaledThumbnailMakeMode()) {
            screenWidth = SCALE_THUMBNAIL_MAX_OFFSET;
        }

        float ratioCanvasWH = this.width / (float) this.height;
        int fixedCanvasWidth = (int) (screenWidth / ratioCanvasWH);

        mScaleX = screenWidth / (float) this.width;
        mScaleY = fixedCanvasWidth / (float) this.height;

        if(!isPortraitScreen && (this.height * mScaleY) > screenHeight) {
            mScaleY = screenHeight / (float) this.height;
            mScaleX = mScaleY;
        }

        if (isPreview()) {
            setScaleX(mScaleX);
            setScaleY(mScaleY);
        }
    }
    @Override
    protected void loadShadowLayer() {
    }

    @Override
    protected void loadPageLayer() {
    }

    @Override
    protected void loadBonusLayer() {
        try {
            if(isRealPagerView()) {
                containerLayer.setScaleX(0.90f);
                containerLayer.setScaleY(0.90f);
            }
        } catch (OutOfMemoryError | Exception e) {
            Dlog.e(TAG, e);
        }
    }

    @Override
    protected void initMargin() {
        leftMargin = Const_PRODUCT.NEW_YEARS_CARD_MARGIN_LIST[0];
        topMargin = Const_PRODUCT.NEW_YEARS_CARD_MARGIN_LIST[1];
        rightMargin = Const_PRODUCT.NEW_YEARS_CARD_MARGIN_LIST[2];
        bottomMargin = Const_PRODUCT.NEW_YEARS_CARD_MARGIN_LIST[3];

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
