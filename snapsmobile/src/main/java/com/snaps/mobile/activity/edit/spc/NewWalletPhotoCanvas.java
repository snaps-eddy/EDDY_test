package com.snaps.mobile.activity.edit.spc;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.snaps.common.data.img.BPoint;
import com.snaps.common.spc.SnapsFrameLayout;
import com.snaps.common.spc.SnapsPageCanvas;
import com.snaps.common.spc.view.ImageLoadView;
import com.snaps.common.structure.control.SnapsClipartControl;
import com.snaps.common.structure.control.SnapsControl;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.constant.Const_VALUES;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.edit.skin.SnapsSkinConstants;
import com.snaps.mobile.activity.edit.skin.SnapsSkinRequestAttribute;
import com.snaps.mobile.activity.edit.skin.SnapsSkinUtil;
import com.snaps.mobile.activity.themebook.PhotobookCommonUtils;
import com.snaps.mobile.utils.custom_layouts.ARelativeLayoutParams;

import errorhandle.logger.Logg;
import font.FTextView;

/**
 * Created by ifunbae on 2016. 9. 23..
 */

public class NewWalletPhotoCanvas extends SnapsPageCanvas {
    private static final String TAG = NewWalletPhotoCanvas.class.getSimpleName();

    public NewWalletPhotoCanvas(Context context) {
        super(context);
    }

    public NewWalletPhotoCanvas(Context context, AttributeSet attr) {
        super(context, attr);
    }

    @Override
    protected void setScaleValue() {
        if (isPreview()) {
            setScaleX(mScaleX);
            setScaleY(mScaleY);
        }
    }

    private void calculatorScaleValue() {
        boolean isPortraitScreen = PhotobookCommonUtils.isPortraitScreenProduct();

        int screenWidth = UIUtil.getScreenWidth(mContext) - (int) getResources().getDimension(R.dimen.snaps_photo_card_preview_margin);
        int screenHeight = UIUtil.getScreenWidth(mContext) - (int) getResources().getDimension(R.dimen.home_title_bar_height) - (int) getResources().getDimension(R.dimen.snaps_photo_card_preview_margin);

        //???????????? ???????????? ????????????.
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
    }

    @Override
    protected void loadShadowLayer() {
        try {
        } catch (OutOfMemoryError e) {
            Dlog.e(TAG, e);
        }
    }

    @Override
    public void setBgColor(int color) {
        color = 0xFFEEEEEE;
        super.setBgColor(color);
    }

    @Override
    protected void loadPageLayer() {}

    @Override
    protected void loadBonusLayer() {
        ImageView skinView = new ImageView(getContext());
        LayoutParams param = new LayoutParams(pageLayer.getLayoutParams());
        param.width = pageLayer.getLayoutParams().width + rightMargin + leftMargin;
        param.height = pageLayer.getLayoutParams().height + topMargin + bottomMargin;
        skinView.setLayoutParams( param );
        skinView.setClickable(false);
        skinView.setFocusable(false);

        if (isThumbnailView()) {
            skinView.setBackgroundResource(R.drawable.shape_photo_card_thumbnail_bg);
        } else {
            try {
                SnapsSkinUtil.loadSkinImage(new SnapsSkinRequestAttribute.Builder()
                        .setContext(getContext())
                        .setResourceFileName(SnapsSkinConstants.PHOTO_CARD_SKIN_FILE_NAME)
                        .setSkinBackgroundView(skinView).create());
            } catch (Exception e) {
                Dlog.e(TAG, e);
            }
        }

        bonusLayer.addView( skinView );
    }

    @Override
    protected void initMargin() {
        leftMargin = Const_PRODUCT.PHOTO_CARD_MARGIN_LIST[0];
        topMargin = Const_PRODUCT.PHOTO_CARD_MARGIN_LIST[1];
        rightMargin = Const_PRODUCT.PHOTO_CARD_MARGIN_LIST[2];
        bottomMargin = Const_PRODUCT.PHOTO_CARD_MARGIN_LIST[3];

        if( isThumbnailView() ) {
            leftMargin = 0;
            rightMargin = 0;
            topMargin = 0;
            bottomMargin = 0;
        }
    }

    @Override
    public void setSnapsPage(SnapsPage page, int number, boolean isBg, String previewBgColor) {
        this._snapsPage = page;
        this._page = number;
        // SnapsPageCanvas??? ????????? ????????? ??????.
        removeItems(this);

        LayoutParams layout = new LayoutParams(this.getLayoutParams());

        this.width = page.getWidth();
        this.height = Integer.parseInt(page.height);

        initMargin();

        layout.width = this.width + leftMargin + rightMargin;
        layout.height = this.height + topMargin + bottomMargin;

        edWidth = layout.width;
        edHeight = layout.height;

        this.setLayoutParams(new ARelativeLayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        // Shadow ?????????.
        LayoutParams shadowlayout = new LayoutParams(layout.width, layout.height);
        shadowLayer = new FrameLayout(this.getContext());
        shadowLayer.setLayoutParams(new ARelativeLayoutParams(shadowlayout));
        this.addView(shadowLayer);

        MarginLayoutParams containerlayout = new MarginLayoutParams(this.width, this.height);
        containerlayout.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);

        containerLayer = new SnapsFrameLayout(this.getContext());
        ARelativeLayoutParams params = new ARelativeLayoutParams(containerlayout);
        containerLayer.setLayout( params );
        this.addView(containerLayer);

        bonusLayer = new FrameLayout(this.getContext());
        bonusLayer.setLayoutParams(new ARelativeLayoutParams(shadowlayout));
        this.addView(bonusLayer);

        // bgLayer ?????????.
        LayoutParams baseLayout = new LayoutParams(this.width, this.height);

        bgLayer = new FrameLayout(this.getContext());
        bgLayer.setLayoutParams(new ARelativeLayoutParams(baseLayout));

        if (isBg || previewBgColor != null)
            containerLayer.addView(bgLayer);

        // layoutLayer ?????????.
        layoutLayer = new FrameLayout(this.getContext());
        layoutLayer.setLayoutParams(new ARelativeLayoutParams(baseLayout));
        containerLayer.addView(layoutLayer);

        // controllLayer ?????????. ppppoint
        controlLayer = new FrameLayout(this.getContext());
        controlLayer.setLayoutParams(new ARelativeLayoutParams(baseLayout));
        containerLayer.addView(controlLayer);


        // formLayer ?????????.
        formLayer = new FrameLayout(this.getContext());
        formLayer.setLayoutParams(new ARelativeLayoutParams(baseLayout));
        containerLayer.addView(formLayer);

        // pageLayer ?????????.
        pageLayer = new FrameLayout(this.getContext());
        pageLayer.setLayoutParams(new ARelativeLayoutParams(baseLayout));
        containerLayer.addView(pageLayer);

		/*
		 * ?????? ?????? ??????. if( Config.PROD_CODE.equalsIgnoreCase(
		 * Config.PRODUCT_STICKER ) ) { this.setBackgroundColor( Color.argb(
		 * 255, 24, 162, 235 ) ); }
		 */
        //????????? ?????? ?????? ?????? ?????? ??????
        initImageLoadCheckTask();

        // Back Ground ??????.
        loadBgLayer(previewBgColor); //????????? ????????? ????????? ????????? ?????? BG ??? ????????????.

        requestLoadAllLayerWithDelay(DELAY_TIME_FOR_LOAD_IMG_LAYER);
    }

    @Override
    protected void loadAllLayers() {
        if (isSuspendedLayerLoad()) {
            hideProgressOnCanvas();
            return;
        }
        // Layout ??????
        loadLayoutLayer();

        // Control ??????.
        loadControlLayer();

        // textView??? 2?????? addView?????? ????????? loadControlLayer()??? ????????????.
        // loadControlLayer2();

        // Form ??????.
        loadFormLayer();

        // Page ????????? ??????.
        loadPageLayer();

        // ?????? Layer ??????.
        loadBonusLayer();

        // ????????? ?????? ?????? ??????.
        setPinchZoomScaleLimit(_snapsPage);

        setScaleValue();

        // ????????? ????????? ?????? ????????? ??? ?????? ?????? ????????????.
        imageLoadCheck();
    }

    @Override
    protected void loadControlLayer() {
        for (SnapsControl control : _snapsPage.getClipartControlList()) {

            switch (control._controlType) {
                case SnapsControl.CONTROLTYPE_IMAGE:
                    // ?????????
                    break;

                case SnapsControl.CONTROLTYPE_STICKER: // ?????????..
                    ImageLoadView view = new ImageLoadView(this.getContext(), (SnapsClipartControl) control);
                    view.setSnapsControl(control);

                    String url = SnapsAPI.DOMAIN(false) + ((SnapsClipartControl) control).resourceURL;
                    loadImage(url, view, Const_VALUES.SELECT_SNAPS, 0, null);

                    // angleclip??????
                    if (!control.angle.isEmpty()) {
                        view.setRotation(Float.parseFloat(control.angle));
                    }
                    SnapsClipartControl clipart = (SnapsClipartControl) control;
                    Dlog.d("loadControlLayer() clipart.alpha:" + clipart.alpha);
                    float alpha = Float.parseFloat(clipart.alpha);
                    view.setAlpha(alpha);

                    controlLayer.addView(view);

                    break;

                case SnapsControl.CONTROLTYPE_BALLOON:
                    // ?????????.
                    break;
            }

        }

        for (SnapsControl control : _snapsPage.getTextControlList()) {

            switch (control._controlType) {
                case SnapsControl.CONTROLTYPE_IMAGE:
                    // ?????????
                    break;

                case SnapsControl.CONTROLTYPE_STICKER: // ?????????..
                    ImageLoadView view = new ImageLoadView(this.getContext(), (SnapsClipartControl) control);
                    view.setSnapsControl(control);

                    String url = SnapsAPI.DOMAIN() + ((SnapsClipartControl) control).resourceURL;
                    loadImage(url, view, Const_VALUES.SELECT_SNAPS, 0, null);

                    // angleclip??????
                    if (!control.angle.isEmpty()) {
                        view.setRotation(Float.parseFloat(control.angle));
                    }
                    SnapsClipartControl clipart = (SnapsClipartControl) control;
                    Dlog.d("loadControlLayer() clipart.alpha:" + clipart.alpha);
                    float alpha = Float.parseFloat(clipart.alpha);
                    view.setAlpha(alpha);

                    controlLayer.addView(view);

                    break;

                case SnapsControl.CONTROLTYPE_BALLOON:
                    // ?????????.
                    break;

                case SnapsControl.CONTROLTYPE_TEXT:
                    setMutableTextControl(control);
                    break;
            }

        }
    }

    //(?????? ???????????? ???????????? ????????????) ?????? ????????? ?????? ?????? ??????.
    private void setSpannableAppliedText(FTextView textView) {
        if (textView == null) return;

        String desc = textView.getText().toString();
        if (desc == null || !desc.contains("(")) return;

        String[] arDesc = desc.split("\\(");
        textView.setText(arDesc[0]);
        arDesc[1] = "\n(" + arDesc[1];

        final SpannableStringBuilder sp = new SpannableStringBuilder(arDesc[1]);
        sp.setSpan(new RelativeSizeSpan(0.8f), 0, arDesc[1].length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.append(sp);
    }

    @Override
    protected BPoint getCanvasOffsetPoint() {
        int x = 0, y = 0;
        if(isLandscapeMode()) {
            y = UIUtil.convertDPtoPX(mContext, 48);
            x = UIUtil.convertDPtoPX(mContext, 148);
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
