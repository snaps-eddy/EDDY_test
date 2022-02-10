package com.snaps.mobile.activity.edit.spc;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

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

public class PhotoCardCanvas extends SnapsPageCanvas {
    private static final String TAG = PhotoCardCanvas.class.getSimpleName();

    public PhotoCardCanvas(Context context) {
        super(context);
    }

    public PhotoCardCanvas(Context context, AttributeSet attr) {
        super(context, attr);
    }

    @Override
    protected void setScaleValue() {
        boolean isPortraitScreen = PhotobookCommonUtils.isPortraitScreenProduct();

        int screenWidth = UIUtil.getScreenWidth(mContext) - (int) getResources().getDimension(R.dimen.snaps_photo_card_preview_margin);
        int screenHeight = UIUtil.getScreenWidth(mContext) - (int) getResources().getDimension(R.dimen.home_title_bar_height) - (int) getResources().getDimension(R.dimen.snaps_photo_card_preview_margin);

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
    protected void loadShadowLayer() {}

    @Override
    public void setBgColor(int color) {
        color = 0xFFEEEEEE;
        super.setBgColor(color);
    }

    @Override
    protected void loadPageLayer() {}

    @Override
    protected void loadBonusLayer() {
        if (isThumbnailView()) {
            ImageView skinView = new ImageView(getContext());
            LayoutParams param = new LayoutParams(pageLayer.getLayoutParams());
            param.width = pageLayer.getLayoutParams().width + rightMargin + leftMargin;
            param.height = pageLayer.getLayoutParams().height + topMargin + bottomMargin;
            skinView.setLayoutParams( param );
            skinView.setClickable(false);
            skinView.setFocusable(false);

            skinView.setBackgroundResource(R.drawable.shape_photo_card_thumbnail_bg);

            bonusLayer.addView( skinView );
        } else {
            ImageView guideLineView = new ImageView(getContext());
            RelativeLayout.LayoutParams guideLineViewParam = new RelativeLayout.LayoutParams(pageLayer.getLayoutParams());
            guideLineViewParam.width = pageLayer.getLayoutParams().width + rightMargin + leftMargin;
            guideLineViewParam.height = pageLayer.getLayoutParams().height + topMargin + bottomMargin;
            guideLineView.setLayoutParams( guideLineViewParam );
            guideLineView.setClickable(false);
            guideLineView.setFocusable(false);

            try {
                SnapsSkinUtil.loadSkinImage(new SnapsSkinRequestAttribute.Builder()
                        .setContext(getContext())
                        .setResourceFileName(SnapsSkinConstants.PHOTO_CARD_GUIDE_LINE_FILE_NAME)
                        .setSkinBackgroundView(guideLineView).create());
            } catch (Exception e) {
                Dlog.e(TAG, e);
            }

            bonusLayer.addView( guideLineView );
        }
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
        // SnapsPageCanvas를 하나만 사용할 경우.
        removeItems(this);

        RelativeLayout.LayoutParams layout = new RelativeLayout.LayoutParams(this.getLayoutParams());

        this.width = page.getWidth();
        this.height = Integer.parseInt(page.height);

        initMargin();

        layout.width = this.width + leftMargin + rightMargin;
        layout.height = this.height + topMargin + bottomMargin;

        edWidth = layout.width;
        edHeight = layout.height;

        this.setLayoutParams(new ARelativeLayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        // Shadow 초기화.
        RelativeLayout.LayoutParams shadowlayout = new RelativeLayout.LayoutParams(layout.width, layout.height);
        shadowLayer = new FrameLayout(this.getContext());
        shadowLayer.setLayoutParams(new ARelativeLayoutParams(shadowlayout));
        this.addView(shadowLayer);

        ViewGroup.MarginLayoutParams containerlayout = new ViewGroup.MarginLayoutParams(this.width, this.height);
        containerlayout.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);

        containerLayer = new SnapsFrameLayout(this.getContext());
        ARelativeLayoutParams params = new ARelativeLayoutParams(containerlayout);
        containerLayer.setLayout( params );
        this.addView(containerLayer);

        bonusLayer = new FrameLayout(this.getContext());
        bonusLayer.setLayoutParams(new ARelativeLayoutParams(shadowlayout));
        this.addView(bonusLayer);

        // bgLayer 초기화.
        RelativeLayout.LayoutParams baseLayout = new RelativeLayout.LayoutParams(this.width, this.height);

        bgLayer = new FrameLayout(this.getContext());
        bgLayer.setLayoutParams(new ARelativeLayoutParams(baseLayout));

        if (isBg || previewBgColor != null)
            containerLayer.addView(bgLayer);

        // layoutLayer 초기화.
        layoutLayer = new FrameLayout(this.getContext());
        layoutLayer.setLayoutParams(new ARelativeLayoutParams(baseLayout));
        containerLayer.addView(layoutLayer);

        // controllLayer 초기화. ppppoint
        controlLayer = new FrameLayout(this.getContext());
        controlLayer.setLayoutParams(new ARelativeLayoutParams(baseLayout));
        containerLayer.addView(controlLayer);

        // formLayer 초기화.
        formLayer = new FrameLayout(this.getContext());
        formLayer.setLayoutParams(new ARelativeLayoutParams(baseLayout));
        containerLayer.addView(formLayer);

        // pageLayer 초기화.
        pageLayer = new FrameLayout(this.getContext());
        pageLayer.setLayoutParams(new ARelativeLayoutParams(baseLayout));
        containerLayer.addView(pageLayer);

		/*
		 * 임의 색상 적용. if( Config.PROD_CODE.equalsIgnoreCase(
		 * Config.PRODUCT_STICKER ) ) { this.setBackgroundColor( Color.argb(
		 * 255, 24, 162, 235 ) ); }
		 */
        //이미지 로딩 완료 체크 객체 생성
        initImageLoadCheckTask();

        // Back Ground 설정.
        loadBgLayer(previewBgColor); //형태는 갖추고 있어야 하니까 우선 BG 만 로딩한다.

        requestLoadAllLayerWithDelay(DELAY_TIME_FOR_LOAD_IMG_LAYER);
    }

    @Override
    protected void loadAllLayers() {
        if (isSuspendedLayerLoad()) {
            hideProgressOnCanvas();
            return;
        }
        // Layout 설정
        loadLayoutLayer();

        // Control 설정.
        loadControlLayer();

        // Form 설정.
        loadFormLayer();

        // Page 이미지 설정.
        loadPageLayer();

        // 추가 Layer 설정.
        loadBonusLayer();

        // 이미지 로드 완료 설정.
        setPinchZoomScaleLimit(_snapsPage);

        setScaleValue();

        // 이미지 로딩이 완료 되었는 지 체크 하기 시작한다.
        imageLoadCheck();
    }

    @Override
    protected void loadControlLayer() {
        for (SnapsControl control : _snapsPage.getClipartControlList()) {

            switch (control._controlType) {
                case SnapsControl.CONTROLTYPE_IMAGE:
                    // 이미지
                    break;

                case SnapsControl.CONTROLTYPE_STICKER: // 스티커..
                    ImageLoadView view = new ImageLoadView(this.getContext(), (SnapsClipartControl) control);
                    view.setSnapsControl(control);

                    String url = SnapsAPI.DOMAIN(false) + ((SnapsClipartControl) control).resourceURL;
                    loadImage(url, view, Const_VALUES.SELECT_SNAPS, 0, null);

                    // angleclip적용
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
                    // 말풍선.
                    break;
            }

        }

        for (SnapsControl control : _snapsPage.getTextControlList()) {

            switch (control._controlType) {
                case SnapsControl.CONTROLTYPE_IMAGE:
                    // 이미지
                    break;

                case SnapsControl.CONTROLTYPE_STICKER: // 스티커..
                    ImageLoadView view = new ImageLoadView(this.getContext(), (SnapsClipartControl) control);
                    view.setSnapsControl(control);

                    String url = SnapsAPI.DOMAIN() + ((SnapsClipartControl) control).resourceURL;
                    loadImage(url, view, Const_VALUES.SELECT_SNAPS, 0, null);

                    // angleclip적용
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
                    // 말풍선.
                    break;

                case SnapsControl.CONTROLTYPE_TEXT:
                    setMutableTextControl(control);
                    break;
            }

        }
    }

    //(해당 텍스트는 출력되지 않습니다) 부분 글씨를 작게 처리 한다.
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
