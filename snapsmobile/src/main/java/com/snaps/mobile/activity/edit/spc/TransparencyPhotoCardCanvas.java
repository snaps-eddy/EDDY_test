package com.snaps.mobile.activity.edit.spc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import androidx.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.request.transition.Transition;
import com.snaps.common.data.img.BPoint;
import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.data.model.SnapsCommonResultListener;
import com.snaps.common.spc.SnapsFrameLayout;
import com.snaps.common.spc.SnapsPageCanvas;
import com.snaps.common.spc.view.CustomImageView;
import com.snaps.common.spc.view.ImageLoadView;
import com.snaps.common.spc.view.SnapsImageView;
import com.snaps.common.structure.SnapsHandler;
import com.snaps.common.structure.control.SnapsBgControl;
import com.snaps.common.structure.control.SnapsClipartControl;
import com.snaps.common.structure.control.SnapsControl;
import com.snaps.common.structure.control.SnapsLayoutControl;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.ISnapsHandler;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.constant.Const_VALUES;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.imageloader.CropUtil;
import com.snaps.common.utils.imageloader.ImageLoader;
import com.snaps.common.utils.imageloader.SnapsCustomTargets;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.BitmapUtil;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.edit.skin.SnapsSkinConstants;
import com.snaps.mobile.activity.edit.skin.SnapsSkinRequestAttribute;
import com.snaps.mobile.activity.edit.skin.SnapsSkinUtil;
import com.snaps.mobile.activity.themebook.PhotobookCommonUtils;
import com.snaps.mobile.component.MaskImageView;
import com.snaps.mobile.utils.custom_layouts.AFrameLayoutParams;
import com.snaps.mobile.utils.custom_layouts.ARelativeLayoutParams;
import com.snaps.mobile.utils.smart_snaps.SmartSnapsManager;
import com.snaps.mobile.utils.ui.SnapsImageViewTarget;
import com.snaps.mobile.utils.ui.SnapsImageViewTargetParams;

import java.io.IOException;

import font.FTextView;

import static com.snaps.common.utils.imageloader.ImageLoader.DEFAULT_BITMAP_CONFIG;
import static com.snaps.common.utils.imageloader.ImageLoader.MAX_DOWN_SAMPLE_RATIO;

/**
 * Created by ifunbae on 2016. 9. 23..
 */

public class TransparencyPhotoCardCanvas extends SnapsPageCanvas implements ISnapsHandler {
    private static final String TAG = TransparencyPhotoCardCanvas.class.getSimpleName();
    private boolean isApplyTransparencyBG = false;

    private SnapsHandler snapsHandler = null;

    public TransparencyPhotoCardCanvas(Context context) {
        super(context);
    }

    public TransparencyPhotoCardCanvas(Context context, AttributeSet attr) {
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
    protected void loadShadowLayer() { }

    @Override
    public void setBgColor(int color) {
        color = 0xFFEEEEEE;
        super.setBgColor(color);
    }

    @Override
    protected void loadPageLayer() {
        SnapsImageView bgView = new SnapsImageView(getContext());
       LayoutParams layout = new LayoutParams(this.width, this.height);
        layout.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
        bgView.setScaleType(ImageView.ScaleType.FIT_XY);
        bgView.setLayoutParams(new AFrameLayoutParams(layout));
        bgView.setBackgroundResource(R.drawable.transparency_image);
        shadowLayer.addView(bgView);
    }

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

        LayoutParams layout = new LayoutParams(this.getLayoutParams());

        this.width = page.getWidth();
        this.height = Integer.parseInt(page.height);

        initMargin();

        layout.width = this.width + leftMargin + rightMargin;
        layout.height = this.height + topMargin + bottomMargin;

        edWidth = layout.width;
        edHeight = layout.height;

        this.setLayoutParams(new ARelativeLayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        // Shadow 초기화.
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

        LayoutParams baseLayout = new LayoutParams(this.width, this.height);
        shadowLayer2 = new FrameLayout(this.getContext());
        MarginLayoutParams shadowParams = new ARelativeLayoutParams(baseLayout);
        shadowLayer2.setLayoutParams(shadowParams);
        this.addView(shadowLayer2);

        bonusLayer = new FrameLayout(this.getContext());
        bonusLayer.setLayoutParams(new ARelativeLayoutParams(shadowlayout));
        this.addView(bonusLayer);

        // bgLayer 초기화.

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

//        setBackgroundColorIfSmartSnapsSearching();
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

        if (mHandler != null)
            mHandler.removeMessages(MSG_LOAD_TRANSPARENCY_PHOTO_CARD_BITMAP);

        super.onDestroyCanvas();
    }

    @Override
    protected void loadImage(String url, ImageView imageView, final int loadType, final int rotate, MyPhotoSelectImageData imgData, SnapsLayoutControl layoutControl) {
        int totalRotate = (rotate == -1 ? 0 : rotate) % 360;

        final String URL = url;
        SnapsImageViewTarget.ImageSearchCompletedListener imageSearchCompletedListener = null;
        if(Const_PRODUCT.isTransparencyPhotoCardProduct()) {
            imageSearchCompletedListener = new SnapsImageViewTarget.ImageSearchCompletedListener() {
                @Override
                public void onfinished() {
                    if(isRealPagerView()) {
                        imageMultiply(true);
                    }
                }
            };
        }
        SnapsImageViewTargetParams snapsImageViewTargetParams = new SnapsImageViewTargetParams.Builder().setSnapsPage(_snapsPage).setLayoutControl(layoutControl).setView(imageView)
                .setImageData(imgData).setRotate(totalRotate).setUri(url).setRealPagerView(isRealPagerView()).setLoadType(loadType).create();
        SnapsImageViewTarget bitmapImageViewTarget = new SnapsImageViewTarget(getContext(), snapsImageViewTargetParams, imageSearchCompletedListener) {
            @Override
            public void onResourceReady(Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                super.onResourceReady(resource, transition);

                try {
                    if (isRealPagerView()) {
                        calculateImageCoordinate(resource, view, loadType, URL, rotate);
                    }
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                } finally {
                    if (view != null) { //bitmap이 완전히 그려지는 지 체크하기 위해 post..
                        view.post(new Runnable() {
                            @Override
                            public void run() {
                                subImageLoadCheckCount();
                            }
                        });
                    } else {
                        subImageLoadCheckCount();
                    }
                }

                isLoadedShadowLayer = true;
                // Shadow 설정.
                loadShadowLayer();
            }
        };

        if (isRealPagerView()) {
            if (imgData != null){
                addSmartSnapsAnimationViewTargetListener(imgData, bitmapImageViewTarget);
            }
        }
        else {
            if (SmartSnapsManager.isFirstSmartAreaSearching()) {
                if (imgData != null) {
                    addSmartSnapsAnimationThumbViewTargetListener(imgData, bitmapImageViewTarget);
                }
            }
        }

        ImageLoader.asyncDisplayImage(mContext, imgData, URL, bitmapImageViewTarget, getRequestImageSize());
    }

    public void imageMultiply(final boolean sleep) {
        if (mHandler != null)
            mHandler.sendEmptyMessageDelayed(MSG_LOAD_TRANSPARENCY_PHOTO_CARD_BITMAP, 500);
    }

    private Bitmap getOriginalImage() {
        View view = getPageContainer();
        Bitmap orgBmp = null;
        try {
            orgBmp = getInSampledBitmap(getOrgWidth(), getOrgHeight(), 1);
            Canvas cvs = new Canvas(orgBmp);
            view.draw(cvs);
        }catch (Exception e) {
            Dlog.e(TAG, e);
        }
        return orgBmp;
    }

    private void loadTransparencyImage(final SnapsCommonResultListener<Bitmap> listener) {
        ImageLoader.with(getContext()).load(R.drawable.transparency_image).override(getOrgWidth(), getOrgHeight()).into(new SnapsCustomTargets<BitmapDrawable>(){
            @Override
            public void onResourceReady(BitmapDrawable resource, Transition<? super BitmapDrawable> transition) {
                if (resource == null || !BitmapUtil.isUseAbleBitmap(resource.getBitmap())) {
                    if (listener != null) {
                        listener.onResult(null);
                    }
                } else {
                    if (listener != null) {
                        listener.onResult(CropUtil.getInSampledBitmapCopy(resource.getBitmap(), Bitmap.Config.ARGB_8888));
                    }
                }
            }
        });
    }

    private void setDrawLayout(Canvas canvas,FrameLayout frameLayout) {
        for(int i = 0 ; i < frameLayout.getChildCount(); i++) {
            View layerView = frameLayout.getChildAt(i);
            int x = 0;
            int y = 0;
            boolean isPng = false;
            boolean isSkip = false;
            View imageView = null;
            if(layerView instanceof SnapsImageView) {
                SnapsImageView snapsImageView = (SnapsImageView)layerView;
                SnapsBgControl bg = (SnapsBgControl)snapsImageView.getSnapsControl();
                if(bg != null && !TextUtils.isEmpty(bg.resourceURL)) {
                    isPng = bg.isPng();
                    x = bg.getIntX();
                    y = bg.getIntY();
                    imageView = snapsImageView;
                } else {
                    isSkip = true;
                }
            } else if(layerView instanceof CustomImageView) {
                MaskImageView maskImageView = (MaskImageView)((CustomImageView) layerView).getImageView();
                SnapsLayoutControl layoutControl = (SnapsLayoutControl)maskImageView.getSnapsControl();
                if(layoutControl.imgData != null && !TextUtils.isEmpty(layoutControl.imgData.PATH)) {
                    isPng = layoutControl.imgData.PATH.contains(".png");
                    x = ((CustomImageView) layerView).getLayoutControl().getIntX();
                    y = ((CustomImageView) layerView).getLayoutControl().getIntY();
                    imageView = layerView;
                } else {
                    isSkip = true;
                }
            } else if(layerView instanceof ImageLoadView) {
                ImageLoadView imageLoadView = (ImageLoadView)layerView;
                SnapsLayoutControl layoutControl = (SnapsLayoutControl)((ImageLoadView) layerView).getSnapsControl();
                if(layoutControl != null && !TextUtils.isEmpty(layoutControl.imagePath)) {
                    isPng = ((SnapsLayoutControl) ((ImageLoadView) layerView).getSnapsControl()).imagePath.contains(".png");
                    x = imageLoadView.getSnapsControl().getIntX();
                    y = imageLoadView.getSnapsControl().getIntY();
                    imageView = imageLoadView;
                } else {
                    isSkip =true;
                }
            }
            if(!isSkip && isPng) {
                    whiteBg(x, y, canvas, imageView);
            }
        }
    }

    private void whiteBg(int x,int y, Canvas canvas, View imageView) {
        Bitmap imageBitmap = null;
        Bitmap whiteBitmap = null;
        try {
            imageBitmap = getInSampledBitmap(imageView.getWidth(), imageView.getHeight(), 1);
            whiteBitmap = getInSampledBitmap(imageView.getWidth(), imageView.getHeight(), 1);
            Canvas canvas1 = new Canvas(imageBitmap);
            imageView.draw(canvas1);
            Canvas canvas2 = new Canvas(whiteBitmap);
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.parseColor("#ffffffff"));
            Rect rect = new Rect();
            rect.set(0,0,whiteBitmap.getWidth(),whiteBitmap.getHeight());
            canvas2.drawRect(rect,paint);
            Bitmap result = getEffectBlandedBitmap(whiteBitmap, PorterDuff.Mode.SRC_IN, 100, imageBitmap, 1, null);
            canvas.drawBitmap(result, x,y, null);
        }catch (Exception e) {
            Dlog.e(TAG, e);
        }

    }
    private void alphaWhiteBg(int x,int y,int width, int height, Canvas canvas) {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor("#1affffff"));
        Rect rect = new Rect();
        rect.set(x,y,x+width,y+height);
        canvas.drawRect(rect, paint);
    }

    public Bitmap getInSampledBitmap(int width, int height, int samplingRatio) throws IOException {
        Bitmap imgBitmap = null;

        try {
            if (width < 1 || height < 1) return null;

            imgBitmap = Bitmap.createBitmap(width, height, DEFAULT_BITMAP_CONFIG);
        } catch (OutOfMemoryError e) {
            samplingRatio *= 2;

            //무한 루프를 방지하기 위해 재 시도 횟수에 제한을 둔다.
            if (samplingRatio <= MAX_DOWN_SAMPLE_RATIO) {
                return getInSampledBitmap(width/samplingRatio, height/samplingRatio, samplingRatio);
            } else {
                return null;
            }
        }
        return imgBitmap;
    }

    private Bitmap getEffectBlandedBitmap(Bitmap filterDrawableRes,
                                          PorterDuff.Mode blandMode,
                                          int alphaPer,
                                          Bitmap originalImage,
                                          int sampleRat,View view) {

        if(originalImage == null || originalImage.isRecycled()) return null;

        try {
            MaskImageView imageView = (MaskImageView) view;
            Bitmap.Config rgbConfig =  Bitmap.Config.ARGB_8888 ;
            Bitmap orgBlendRes =  CropUtil.getInSampledBitmapCopy(filterDrawableRes, rgbConfig);
            Bitmap scaleBitmap = null;
            if(view == null) {
                scaleBitmap = Bitmap.createScaledBitmap(orgBlendRes, originalImage.getWidth(), originalImage.getHeight(), false);
            } else {
                scaleBitmap = Bitmap.createScaledBitmap(orgBlendRes, orgBlendRes.getWidth(), orgBlendRes.getHeight(), false);
            }
            if(scaleBitmap != orgBlendRes) {
                if(orgBlendRes != null && !orgBlendRes.isRecycled()) {
                    orgBlendRes.recycle();
                    orgBlendRes = null;
                }
            }
            orgBlendRes = scaleBitmap;

            Paint p = new Paint();
            p.setAlpha(convertAlphaPer(alphaPer));
            p.setXfermode(new PorterDuffXfermode(blandMode));


            Canvas c = new Canvas();

            if(view == null) {
                p.setShader(new BitmapShader(orgBlendRes, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
                c.setBitmap(originalImage);
                c.drawBitmap(originalImage, 0, 0, null);
                c.drawRect(0, 0, originalImage.getWidth(), originalImage.getHeight(), p);
                return originalImage;
            } else {
                p.setShader(new BitmapShader(originalImage, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
                c.setBitmap(filterDrawableRes);
                c.drawBitmap(filterDrawableRes, 0, 0, null);
                c.drawRect(imageView.getSnapsControl().getIntX(), imageView.getSnapsControl().getIntY(), imageView.getSnapsControl().getIntX() + imageView.getSnapsControl().getIntWidth(), imageView.getSnapsControl().getIntY() + imageView.getSnapsControl().getIntHeight(), p);
                Bitmap converted = Bitmap.createBitmap(filterDrawableRes, imageView.getSnapsControl().getIntX(), imageView.getSnapsControl().getIntY(),  imageView.getSnapsControl().getIntWidth(),  imageView.getSnapsControl().getIntHeight());
                return converted;
            }

        } catch (OutOfMemoryError e) {
            Dlog.e(TAG, e);
            sampleRat *= 2;
            if (sampleRat <= MAX_DOWN_SAMPLE_RATIO)
                return getEffectBlandedBitmap(filterDrawableRes, blandMode, alphaPer, originalImage, sampleRat, view);
            else
                return null;
        }
    }

    private int convertAlphaPer(int per) {
        return (int)(255 *  (per / 100.f));
    }

    private void requestAddTransImage() throws Exception {
        if (shadowLayer2 != null)
            shadowLayer2.removeAllViews();

        loadTransparencyImage(new SnapsCommonResultListener<Bitmap>() {
            @Override
            public void onResult(final Bitmap bgBitmap) {
                if (!BitmapUtil.isUseAbleBitmap(bgBitmap))
                    return;
                        drawTransLayouts(bgBitmap);

                        drawMultiplyImage(bgBitmap);
                    }
        });
    }

    private void drawTransLayouts(Bitmap bgBitmap) {
        Canvas cvs = new Canvas(bgBitmap);
        setDrawLayout(cvs, bgLayer);
        setDrawLayout(cvs,layoutLayer);
        setDrawLayout(cvs,controlLayer);
    }

    private void drawMultiplyImage(Bitmap bgBitmap) {
        Bitmap bmCopied = getOriginalImage();
        if (BitmapUtil.isUseAbleBitmap(bmCopied)) {
            Bitmap multiplyBitmap = getEffectBlandedBitmap(bgBitmap, PorterDuff.Mode.MULTIPLY, 100, bmCopied, 1, null);
            if (BitmapUtil.isUseAbleBitmap(multiplyBitmap)) {
                final SnapsImageView bgView = new SnapsImageView(getContext());
                FrameLayout.LayoutParams layout = new FrameLayout.LayoutParams(new LayoutParams(TransparencyPhotoCardCanvas.this.width, TransparencyPhotoCardCanvas.this.height));
                bgView.setLayoutParams(layout);
                bgView.setImageBitmap(multiplyBitmap);
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (shadowLayer2 != null)
                            shadowLayer2.addView(bgView);
                    }
                });
            }
        }
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);

        try {
            switch (msg.what) {
                case MSG_LOAD_TRANSPARENCY_PHOTO_CARD_BITMAP:
                    requestAddTransImage();
                    break;
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }
}
