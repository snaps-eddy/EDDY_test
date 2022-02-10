package com.snaps.mobile.activity.edit.spc;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.request.transition.Transition;
import com.snaps.common.customui.RotateImageView;
import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.spc.SnapsFrameLayout;
import com.snaps.common.spc.SnapsPageCanvas;
import com.snaps.common.spc.view.CustomImageView;
import com.snaps.common.spc.view.SnapsImageView;
import com.snaps.common.structure.control.SnapsLayoutControl;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.imageloader.CropUtil;
import com.snaps.common.utils.imageloader.ImageLoader;
import com.snaps.common.utils.imageloader.SnapsCustomTargets;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.BitmapUtil;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.activity.edit.skin.SnapsSkinConstants;
import com.snaps.mobile.activity.edit.skin.SnapsSkinRequestAttribute;
import com.snaps.mobile.activity.edit.skin.SnapsSkinUtil;
import com.snaps.mobile.component.MaskImageView;
import com.snaps.mobile.utils.custom_layouts.ARelativeLayoutParams;
import com.snaps.mobile.utils.smart_snaps.SmartSnapsManager;
import com.snaps.mobile.utils.ui.SnapsImageViewTarget;
import com.snaps.mobile.utils.ui.SnapsImageViewTargetParams;

import java.io.IOException;

import static com.snaps.common.utils.constant.Const_PRODUCT.DIY_STICKER_A4;
import static com.snaps.common.utils.constant.Const_PRODUCT.DIY_STICKER_A5;
import static com.snaps.common.utils.constant.Const_PRODUCT.DIY_STICKER_A6;
import static com.snaps.common.utils.imageloader.ImageLoader.DEFAULT_BITMAP_CONFIG;
import static com.snaps.common.utils.imageloader.ImageLoader.MAX_DOWN_SAMPLE_RATIO;

/**
 * com.snaps.kakao.activity.edit.spc
 * SnapsPageCanvas.java
 *
 * @author JaeMyung Park
 * @Date : 2013. 5. 23.
 * @Version :
 */
public class DIYStickerPageCanvas extends SnapsPageCanvas {
    private static final String TAG = DIYStickerPageCanvas.class.getSimpleName();

    public DIYStickerPageCanvas(Context context) {
        super(context);
    }

    public DIYStickerPageCanvas(Context context, AttributeSet attr) {
        super(context, attr);
    }

    @Override
    protected void loadShadowLayer() {
        try {
            shadowLayer.setBackgroundResource(getShadowName());
        } catch (OutOfMemoryError e) {
            Dlog.e(TAG, e);
        } catch (PackageManager.NameNotFoundException e) {
            Dlog.e(TAG, e);
        }
    }

    @Override
    protected void loadPageLayer() {
    }

    @Override
    public void setBgColor(int color) {
        color = 0xFFEEEEEE;
        super.setBgColor(color);
    }

    @Override
    protected void loadBonusLayer() {
        String skinName = getSkinName();
        if (!StringUtil.isEmpty(skinName)) {
            try {
                SnapsSkinUtil.loadSkinImage(new SnapsSkinRequestAttribute.Builder()
                        .setContext(getContext())
                        .setResourceFileName(skinName)
                        .setSkinBackgroundView(bonusLayer)
                        .create());
            } catch (Exception e) {
                Dlog.e(TAG, e);
            }
        }
    }

    private String getSkinName() {
        switch (Config.getPROD_CODE()) {
            case DIY_STICKER_A4:
                if (Config.getFRAME_TYPE().equals("389000"))
                    return SnapsSkinConstants.STICKER_DIY_A4;
                return SnapsSkinConstants.STICKER_DIY_A4_LINE;
            case DIY_STICKER_A5:
                if (Config.getFRAME_TYPE().equals("389000"))
                    return SnapsSkinConstants.STICKER_DIY_A5;
                return SnapsSkinConstants.STICKER_DIY_A5_LINE;
            case DIY_STICKER_A6:
                if (Config.getFRAME_TYPE().equals("389000"))
                    return SnapsSkinConstants.STICKER_DIY_A6;
                return SnapsSkinConstants.STICKER_DIY_A6_LINE;
            default:
                return "";

        }
    }

    private int getShadowName() throws PackageManager.NameNotFoundException {
        String size = "";
        String paperCode = "";
        switch (Config.getPROD_CODE()) {
            case DIY_STICKER_A4:
                size = "a4";
                break;
            case DIY_STICKER_A5:
                size = "a5";
                break;
            case DIY_STICKER_A6:
                size = "a6";
                break;

        }
        switch (Config.getPAPER_CODE()) {
            case "160026":
            case "160027":
                paperCode = "white";
                break;
            case "160025":
                paperCode = "craft";
                break;
            case "160023":
            case "160024":
                paperCode = "clear";
                break;
        }
        Context resContext = getContext().createPackageContext(getContext().getPackageName(), 0);
        Resources res = resContext.getResources();
        int id = res.getIdentifier("m_skin_fancy_diy_sticker_" + size + "_standard_" + paperCode, "drawable", getContext().getPackageName());
        if (id == 0) {
            return 0;
        } else
            return id;
    }


    @Override
    protected void initMargin() {
        switch (Config.getPROD_CODE()) {
            case DIY_STICKER_A4:
                leftMargin = 8;
                topMargin = 8;
                rightMargin = 8;
                bottomMargin = 40;
                break;
            case DIY_STICKER_A5:
                leftMargin = 12;
                topMargin = 11;
                rightMargin = 11;
                bottomMargin = 57;
                break;
            case DIY_STICKER_A6:
                leftMargin = 16;
                topMargin = 16;
                rightMargin = 16;
                bottomMargin = 79;
                break;
            default:
                leftMargin = 0;
                topMargin = 0;
                rightMargin = 0;
                bottomMargin = 0;
                break;
        }

    }

    private int count = 0;

    @Override
    protected void loadImage(String url, ImageView imageView, final int loadType, final int rotate, MyPhotoSelectImageData imgData, SnapsLayoutControl layoutControl) {
        int totalRotate = (rotate == -1 ? 0 : rotate) % 360;

        final String URL = url;
        SnapsImageViewTarget.ImageSearchCompletedListener imageSearchCompletedListener = () -> {
            count++;
            int controlcount = _snapsPage.getLayoutList().size();
            if (count == 1 || count == controlcount) {
                if (mHandler != null)
                    mHandler.sendEmptyMessageDelayed(MSG_LOAD_DIY_STICKER_BITMAP, 100);
            }
        };

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
                        view.post(() -> subImageLoadCheckCount());
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
            if (imgData != null) {
                addSmartSnapsAnimationViewTargetListener(imgData, bitmapImageViewTarget);
            }
        } else {
            if (SmartSnapsManager.isFirstSmartAreaSearching()) {
                if (imgData != null) {
                    addSmartSnapsAnimationThumbViewTargetListener(imgData, bitmapImageViewTarget);
                }
            }
        }

        ImageLoader.asyncDisplayImage(mContext, imgData, URL, bitmapImageViewTarget, getRequestImageSize());
    }

    private void drawImage() {
        try {
            drawMultiplyImage();
        } catch (PackageManager.NameNotFoundException e) {
            Dlog.e(TAG, e);
        }
    }

    private Bitmap getOutLineOriginalImage() {
        setHidenButton();
        View view = getPageContainer();

        Bitmap orgBmp = null;
        try {
            orgBmp = getInSampledBitmap(getOrgWidth(), getOrgHeight(), 1);
            Canvas cvs = new Canvas(orgBmp);
            view.draw(cvs);
            setShownButton();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        return orgBmp;
    }

    private Bitmap getOriginalImage() {
        View view = getPageContainer();
        Bitmap orgBmp = null;
        try {
            orgBmp = getInSampledBitmap(getOrgWidth(), getOrgHeight(), 1);
            Canvas cvs = new Canvas(orgBmp);
            view.draw(cvs);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        return orgBmp;
    }

    public void setHidenButton() {
        for (int i = 0; i < layoutLayer.getChildCount(); i++) {
            View layerView = layoutLayer.getChildAt(i);
            if (layerView instanceof RotateImageView) {
                layerView.setVisibility(View.INVISIBLE);
            } else if (layerView instanceof CustomImageView) {
                if (((CustomImageView) layerView).getLayoutControl().imgData == null) {
                    layerView.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    public void setShownButton() {
        for (int i = 0; i < layoutLayer.getChildCount(); i++) {
            View layerView = layoutLayer.getChildAt(i);
            if (layerView instanceof RotateImageView) {
                layerView.setVisibility(View.VISIBLE);
            } else if (layerView instanceof CustomImageView) {
                if (((CustomImageView) layerView).getLayoutControl().imgData == null) {
                    layerView.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void drawOutLineImage(final Bitmap oriBitmapArray) {
        final Bitmap newBitmap = getOutLineOriginalImage();
        int lineColor = Color.parseColor("#ff00fd");
        UIUtil.drawKnifeOutLine(newBitmap, newBitmap.getWidth(), getMaskSize(), lineColor, outLineBitmapArray -> {
            int[] bitmapIntArray = UIUtil.getIntArray(oriBitmapArray);
            for (int i = 0; i < bitmapIntArray.length; i++) {
                if (outLineBitmapArray[i] == lineColor) {
                    bitmapIntArray[i] = lineColor;
                }
            }
            Bitmap result = UIUtil.intArrayToBitmap(bitmapIntArray, width, height);
            loadImage(result);
        });
    }

    private int getMaskSize() {
        switch (Config.getPROD_CODE()) {
            case DIY_STICKER_A4:
                return 5;
            case DIY_STICKER_A5:
                return 7;
            case DIY_STICKER_A6:
                return 7;
            default:
                return 0;

        }
    }

    private void drawMultiplyImage() throws PackageManager.NameNotFoundException {

        int bgName = getShadowName();

        ImageLoader.with(getContext()).load(bgName).override(getOrgWidth(), getOrgHeight()).into(new SnapsCustomTargets<BitmapDrawable>() {
            @Override
            public void onResourceReady(BitmapDrawable shadow, Transition<? super BitmapDrawable> transition) {
                if (shadow != null && BitmapUtil.isUseAbleBitmap(shadow.getBitmap())) {
                    Bitmap bgBitmap = CropUtil.getInSampledBitmapCopy(shadow.getBitmap(), Bitmap.Config.ARGB_8888);
                    Bitmap bmCopied = getOriginalImage();
                    if (BitmapUtil.isUseAbleBitmap(bmCopied)) {
                        Bitmap multiplyBitmap = getEffectBlandedBitmap(bgBitmap, PorterDuff.Mode.MULTIPLY, 100, bmCopied, 1, null);

                        if (_snapsPage.getImageCountOnPage() < 1 || Config.getFRAME_TYPE().equals("389000")) {
                            loadImage(multiplyBitmap);
                        } else {
                            drawOutLineImage(multiplyBitmap);
                        }

                    }
                }
            }
        });

    }

    private void loadImage(Bitmap bitmap) {
        if (BitmapUtil.isUseAbleBitmap(bitmap)) {
            final SnapsImageView bgView = new SnapsImageView(getContext());
            FrameLayout.LayoutParams layout = new FrameLayout.LayoutParams(new LayoutParams(DIYStickerPageCanvas.this.width, DIYStickerPageCanvas.this.height));
            bgView.setLayoutParams(layout);
            bgView.setImageBitmap(bitmap);
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (multiFlyLayer != null)
                        multiFlyLayer.removeAllViews();
                    multiFlyLayer.addView(bgView);
                }
            });
        }
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
                return getInSampledBitmap(width / samplingRatio, height / samplingRatio, samplingRatio);
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
                                          int sampleRat, View view) {

        if (originalImage == null || originalImage.isRecycled()) return null;

        try {
            MaskImageView imageView = (MaskImageView) view;
            Bitmap.Config rgbConfig = Bitmap.Config.ARGB_8888;
            Bitmap orgBlendRes = CropUtil.getInSampledBitmapCopy(filterDrawableRes, rgbConfig);
            Bitmap scaleBitmap = null;
            if (view == null) {
                scaleBitmap = Bitmap.createScaledBitmap(orgBlendRes, originalImage.getWidth(), originalImage.getHeight(), false);
            } else {
                scaleBitmap = Bitmap.createScaledBitmap(orgBlendRes, orgBlendRes.getWidth(), orgBlendRes.getHeight(), false);
            }
            if (scaleBitmap != orgBlendRes) {
                if (orgBlendRes != null && !orgBlendRes.isRecycled()) {
                    orgBlendRes.recycle();
                    orgBlendRes = null;
                }
            }
            orgBlendRes = scaleBitmap;

            Paint p = new Paint();
            p.setAlpha(convertAlphaPer(alphaPer));
            p.setXfermode(new PorterDuffXfermode(blandMode));


            Canvas c = new Canvas();

            if (view == null) {
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
                Bitmap converted = Bitmap.createBitmap(filterDrawableRes, imageView.getSnapsControl().getIntX(), imageView.getSnapsControl().getIntY(), imageView.getSnapsControl().getIntWidth(), imageView.getSnapsControl().getIntHeight());
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
        return (int) (255 * (per / 100.f));
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

        this.setLayoutParams(new ARelativeLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        // Shadow 초기화.
        LayoutParams shadowlayout = new LayoutParams(layout.width, layout.height);
        shadowLayer = new FrameLayout(this.getContext());
        shadowLayer.setLayoutParams(new ARelativeLayoutParams(shadowlayout));
        this.addView(shadowLayer);

        MarginLayoutParams containerlayout = new MarginLayoutParams(this.width, this.height);
        containerlayout.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);

        containerLayer = new SnapsFrameLayout(this.getContext());
        ARelativeLayoutParams params = new ARelativeLayoutParams(containerlayout);
        containerLayer.setLayout(params);
        this.addView(containerLayer);

        LayoutParams baseLayout = new LayoutParams(this.width, this.height);
        multiFlyLayer = new FrameLayout(this.getContext());
        MarginLayoutParams shadowParams = new ARelativeLayoutParams(baseLayout);
        shadowParams.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
        multiFlyLayer.setLayoutParams(shadowParams);
        this.addView(multiFlyLayer);

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

        LayoutParams baseLayout2 = new LayoutParams(this.width, this.height);
        buttonLayer = new FrameLayout(this.getContext());
        MarginLayoutParams buttonParams = new ARelativeLayoutParams(baseLayout2);
        buttonParams.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
        buttonLayer.setLayoutParams(buttonParams);
        this.addView(buttonLayer);


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
    public void handleMessage(Message msg) {
        super.handleMessage(msg);

        try {
            switch (msg.what) {
                case MSG_LOAD_DIY_STICKER_BITMAP:
                    drawImage();
                    break;
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    @Override
    public void onDestroyCanvas() {
        if (shadowLayer != null) {
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
