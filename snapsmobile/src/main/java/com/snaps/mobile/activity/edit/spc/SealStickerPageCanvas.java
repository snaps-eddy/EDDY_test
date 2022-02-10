package com.snaps.mobile.activity.edit.spc;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.snaps.common.customui.RotateImageView;
import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.spc.SnapsFrameLayout;
import com.snaps.common.spc.SnapsPageCanvas;
import com.snaps.common.spc.view.CustomImageView;
import com.snaps.common.spc.view.ImageLoadView;
import com.snaps.common.spc.view.SnapsImageView;
import com.snaps.common.structure.control.SnapsBgControl;
import com.snaps.common.structure.control.SnapsControl;
import com.snaps.common.structure.control.SnapsLayoutControl;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.text.SnapsTextToImageAttribute;
import com.snaps.common.text.SnapsTextToImageView;
import com.snaps.common.text.SnapsTextToImageView;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.imageloader.ImageLoader;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.BitmapUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.common.products.single_page_product.SealStickerProductEditor;
import com.snaps.mobile.activity.edit.skin.SnapsSkinConstants;
import com.snaps.mobile.activity.edit.spc.base.SceneCapturable;
import com.snaps.mobile.activity.edit.thumbnail_skin.SnapsThumbNailSkinConstants;
import com.snaps.mobile.component.ColorBorderView;
import com.snaps.mobile.component.MaskImageView;
import com.snaps.mobile.tutorial.SnapsTutorialAttribute;
import com.snaps.mobile.tutorial.SnapsTutorialConstants;
import com.snaps.mobile.tutorial.new_tooltip_tutorial.SnapsTutorialUtil;
import com.snaps.mobile.utils.custom_layouts.ARelativeLayoutParams;
import com.snaps.mobile.utils.ui.SnapsImageViewTarget;
import com.snaps.mobile.utils.ui.SnapsImageViewTargetParams;

import java.net.URL;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import androidx.annotation.Nullable;

/**
 * R.layout.activity_seal_sticker
 */
public class SealStickerPageCanvas extends SnapsPageCanvas implements SceneCapturable {

    private boolean isCartThumbnail;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    public SealStickerPageCanvas(Context context) {
        super(context);
    }

    public SealStickerPageCanvas(Context context, boolean isCartThumbnail) {
        super(context);
        this.isCartThumbnail = isCartThumbnail;
    }

    @Override
    protected void loadShadowLayer() {
    }

    @Override
    protected void loadPageLayer() {
        //Nothing
    }

    @Override
    protected void loadLayoutLayer() {
        super.loadLayoutLayer();

        //저해상도 아이콘 표시 안함
        //정상 처리하려면 이미지 합성 등등에서 복잡한 처리를 해줘야하는 처리하기 힘듬
        int count = containerLayer.getChildCount();
        for (int i = 0; i < count; i++) {
            View view = containerLayer.getChildAt(i);
            if (view instanceof ImageView && SnapsPageCanvas.TAG_IMAGEVIEW_NO_PRINT_IMAGE_ALERT.equals(view.getTag())) {
                view.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    protected void loadControlLayer() {
        super.loadControlLayer();

        if (!isCartThumbnail) {
            setDisableButtonActionBGImage(); //배경으로 사용되는 사진틀 플러스 버튼 및 터치 이벤트 막기
        }

        for (int i = 0; i < layoutLayer.getChildCount(); i++) {
            View view = layoutLayer.getChildAt(i);
            if (view instanceof CustomImageView) {
                CustomImageView customImageView = (CustomImageView) view;
                if (customImageView.getLayoutControl().imgData == null) {
                    view.setAlpha(isCartThumbnail ? 0 : 0.25f);
                }
            }
        }

        if (!isCartThumbnail) {
            for (int i = 0; i < buttonLayer.getChildCount(); i++) {
                View view = buttonLayer.getChildAt(i);
                if (view instanceof RotateImageView) {
                    //+버튼이 너무 크게 나와서 축소
                    view.setScaleX(0.75f);
                    view.setScaleY(0.75f);
                }
            }
        }
    }


    private void setDisableButtonActionBGImage() {
        CustomImageView backgroundCustomImageView = null;  //바탕에 깔려있는 사진틀
        int count = layoutLayer.getChildCount();
        for (int i = 0; i < count; i++) {
            View view = layoutLayer.getChildAt(i);
            if (view instanceof CustomImageView) {
                CustomImageView customImageView = (CustomImageView) view;
                SnapsLayoutControl layoutControl = customImageView.getLayoutControl();
                if (layoutControl != null && layoutControl.isForBackground()) {
                    backgroundCustomImageView = customImageView;
                }
            }
        }

        if (backgroundCustomImageView != null) {
            MaskImageView maskImageView = (MaskImageView) backgroundCustomImageView.getImageView();
            maskImageView.setClickable(false);  // 클릭 이벤트 막기

            count = buttonLayer.getChildCount();
            for (int i = 0; i < count; i++) {
                View view = buttonLayer.getChildAt(i);
                if (view instanceof RotateImageView) {
                    RotateImageView rotateImageView = (RotateImageView) view;
                    if (rotateImageView.getTag().equals(backgroundCustomImageView.getLayoutControl())) {
                        rotateImageView.setVisibility(View.GONE); // + 버튼 안보이게 하기
                    }
                }
            }
        }
    }

    @Override
    protected void loadBonusLayer() {
        try {
            if (getSnapsPage().type.equalsIgnoreCase("page")) {
                ImageView skin = new ImageView(getContext());
                LayoutParams param = new LayoutParams(bonusLayer.getLayoutParams());
                param.width = bonusLayer.getLayoutParams().width;
                param.height = bonusLayer.getLayoutParams().height;
                skin.setLayoutParams(param);

                bonusLayer.addView(skin);
                loadPaperCoatingCutLineSkins(skin);
            }
        } catch (Exception e) {
            Dlog.e(e);
        }
    }


    @Override
    protected void loadAllLayers() {
        super.loadAllLayers();

        if (!isCartThumbnail && !SealStickerProductEditor.isShownToolTip) {
            SealStickerProductEditor.isShownToolTip = true;
            SnapsTutorialUtil.showTooltipAlways((Activity) mContext, new SnapsTutorialAttribute.Builder().setViewPosition(
                    SnapsTutorialConstants.eTUTORIAL_VIEW_POSITION.ACCORDION_CARD)
                    .setText(getContext().getString(R.string.qr_code_only_printed_first_page))
                    .setTargetView(layoutLayer)
                    .setLeftMargin((int) (layoutLayer.getMeasuredWidth() * 0.78f))
                    .create());
        }

        //Ben
        //이미지 로딩 완료는 체크 가능하나 텍스트 이미지는 안되서 추가한 개구멍에 연결
        for (int i = 0; i < controlLayer.getChildCount(); i++) {
            View view = controlLayer.getChildAt(i);
            if (view instanceof SnapsTextToImageView) {
                SnapsTextToImageView snapsTextToImageView = (SnapsTextToImageView) view;
                snapsTextToImageView.setListener(() -> {
                    if (!isCompositingImages) {
                        compositeImages();
                    }
                });

            }
        }
    }

    @Override
    protected void initMargin() {
        leftMargin = 12;
        topMargin = 12;
        rightMargin = 12;
        bottomMargin = 12;
    }

    @Override
    public void setBgColor(int color) {
        color = 0xFFEEEEEE;
        super.setBgColor(color);
    }

    @Override
    public void setSnapsPage(SnapsPage page, int number, boolean isBg, String previewBgColor) {
        this._snapsPage = page;
        this._page = number;
        // SnapsPageCanvas를 하나만 사용할 경우.
        removeItems(this);

        LayoutParams layout = new LayoutParams(this.getLayoutParams());

        this.width = page.getWidth();
        this.height = page.getHeight();

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
        MarginLayoutParams multiflyParams = new ARelativeLayoutParams(baseLayout);
        multiflyParams.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
        multiFlyLayer.setLayoutParams(multiflyParams);
        this.addView(multiFlyLayer);

        bonusLayer = new FrameLayout(this.getContext());
        bonusLayer.setLayoutParams(shadowlayout);
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

        if (isCartThumbnail) {
            buttonLayer = null;
        } else {
            LayoutParams baseLayout2 = new LayoutParams(this.width, this.height);
            buttonLayer = new FrameLayout(this.getContext());
            MarginLayoutParams buttonParams = new ARelativeLayoutParams(baseLayout2);
            buttonParams.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
            buttonLayer.setLayoutParams(buttonParams);
            this.addView(buttonLayer);
        }
        //이미지 로딩 완료 체크 객체 생성
        initImageLoadCheckTask();

        // Back Ground 설정.
        loadBgLayer(previewBgColor); //형태는 갖추고 있어야 하니까 우선 BG 만 로딩한다.

        requestLoadAllLayerWithDelay(DELAY_TIME_FOR_LOAD_IMG_LAYER);
    }

    public void setHidenButton() {
    }

    public void setShownButton() {
    }

    /**
     * Todo : TextToImageView 도 다 그려지는 시점을 알아야 한다.  <-- 개구멍 뚫어서... (Ben)
     */
    @Override
    public void onFinishImageLoad() {
        super.onFinishImageLoad();
        compositeImages();
    }

    private volatile boolean isCompositingImages = false;

    private void compositeImages() {
        if (SealStickerProductEditor.isTransparentPaper) {
            isCompositingImages = true;
            Disposable disposable = Single.fromCallable(this::hideTextViewOutLineAndPlaceHolder)
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .flatMap(result ->
                            Single.fromCallable(this::makeTranslucentEffect).subscribeOn(Schedulers.io())
                    )
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe((bitmap) -> {
                        multiFlyLayer.setBackground(new BitmapDrawable(getResources(), bitmap));
                        isCompositingImages = false;
                    }, throwable -> {
                        Dlog.d("Error !! " + throwable);
                        Dlog.e(throwable);
                        isCompositingImages = false;
                    });
            compositeDisposable.add(disposable);
        }
    }

    private boolean hideTextViewOutLineAndPlaceHolder() {
        // 이런 걸 부모에서 제공해줘야지 ...
        if (isCartThumbnail) {
            int size = controlLayer.getChildCount();
            for (int i = 0; i < size; i++) {
                View view = controlLayer.getChildAt(i);
                if (view instanceof SnapsTextToImageView) {
                    SnapsTextToImageView textView = (SnapsTextToImageView) view;
                    textView.setVisibleOutLine(false);
                    textView.setVisiblePlaceHolder(false);  // 아 이거 UI 스레드
                }
            }
        }
        return true;
    }

    /**
     * @Marko 여기서 부터 시작.
     */
    private Bitmap makeTranslucentEffect() throws ExecutionException, InterruptedException {
        int pageWidth = getPageContainer().getWidth();
        int pageHeight = getPageContainer().getHeight();

        Bitmap gridBackground = Glide.with(getContext())
                .asBitmap()
                .load(R.drawable.transparency_image)
                .skipMemoryCache(false)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .override(pageWidth, pageHeight)
                .submit()
                .get();

        Bitmap viewCaptureBitmap = BitmapUtil.getInSampledBitmap(pageWidth, pageHeight, 1);
        if (viewCaptureBitmap == null) {
            throw new NullPointerException("can not create bitmap.");
        }
        Canvas viewCaptureCanvas = new Canvas(viewCaptureBitmap);

        drawSceneObjects(viewCaptureCanvas, layoutLayer);
        drawSceneObjects(viewCaptureCanvas, controlLayer);

        Rect gridBackgroundRect = new Rect(0, 0, controlLayer.getWidth(), controlLayer.getHeight());

        Paint paint = new Paint();
        paint.setFilterBitmap(true);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY));
        viewCaptureCanvas.drawBitmap(gridBackground, gridBackgroundRect, gridBackgroundRect, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER));
        viewCaptureCanvas.drawBitmap(gridBackground, gridBackgroundRect, gridBackgroundRect, paint);
        return viewCaptureBitmap;
    }

    private void drawSceneObjects(Canvas canvas, ViewGroup viewLayer) {
        int childCount = viewLayer.getChildCount();
        Paint paint = new Paint();
        paint.setFilterBitmap(true);
        for (int i = 0; i < childCount; i++) {
            View layerView = viewLayer.getChildAt(i);
            int x = 0;
            int y = 0;
            View targetView = null;

            if (layerView instanceof SnapsImageView) {
                SnapsImageView snapsImageView = (SnapsImageView) layerView;
                SnapsBgControl bg = (SnapsBgControl) snapsImageView.getSnapsControl();
                if (bg != null && !TextUtils.isEmpty(bg.resourceURL)) {
                    x = bg.getIntX();
                    y = bg.getIntY();
                    targetView = snapsImageView;
                }
            } else if (layerView instanceof CustomImageView) {
                MaskImageView maskImageView = (MaskImageView) ((CustomImageView) layerView).getImageView();
                SnapsLayoutControl layoutControl = (SnapsLayoutControl) maskImageView.getSnapsControl();
                if (layoutControl.imgData != null && !TextUtils.isEmpty(layoutControl.imgData.PATH)) {
                    x = ((CustomImageView) layerView).getLayoutControl().getIntX();
                    y = ((CustomImageView) layerView).getLayoutControl().getIntY();
                    targetView = layerView;
                }
            } else if (layerView instanceof ImageLoadView) {
                ImageLoadView imageLoadView = (ImageLoadView) layerView;
                SnapsLayoutControl layoutControl = (SnapsLayoutControl) ((ImageLoadView) layerView).getSnapsControl();
                if (layoutControl != null && !TextUtils.isEmpty(layoutControl.imagePath)) {
                    x = imageLoadView.getSnapsControl().getIntX();
                    y = imageLoadView.getSnapsControl().getIntY();
                    targetView = imageLoadView;
                }
            } else if (layerView instanceof SnapsTextToImageView) {
                SnapsTextToImageView imageLoadView = (SnapsTextToImageView) layerView;
                SnapsTextToImageAttribute textToImageAttribute = ((SnapsTextToImageView) layerView).getAttribute();

                x = textToImageAttribute.getSnapsTextControl().getIntX();
                y = textToImageAttribute.getSnapsTextControl().getIntY();
                targetView = imageLoadView;
            }

            if (targetView != null) {
                captureSceneObjectView(x, y, canvas, targetView, paint);
            }
        }
    }

    private void captureSceneObjectView(int x, int y, Canvas canvas, View imageView, Paint paint) {
        Bitmap captured = BitmapUtil.getInSampledBitmap(imageView.getWidth(), imageView.getHeight(), 1);
        if (captured == null) {
            throw new NullPointerException("can not create bitmap.");
        }
        Canvas capturedCanvas = new Canvas(captured);
        imageView.draw(capturedCanvas);
        canvas.drawBitmap(captured, x, y, paint);
    }

    @Override
    public Bitmap getThumbnailBitmap() {
        hideTextViewOutLineAndPlaceHolder();
        FrameLayout targetFrameLayout = SealStickerProductEditor.isTransparentPaper ? multiFlyLayer : containerLayer;

        int thumbWidth = targetFrameLayout.getWidth() - leftMargin - rightMargin;
        int thumbHeight = targetFrameLayout.getHeight() - topMargin - bottomMargin;
        Bitmap captureBitmap = Bitmap.createBitmap(thumbWidth, thumbHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(captureBitmap);
        targetFrameLayout.draw(canvas);
        bonusLayer.draw(canvas);

        return captureBitmap;
    }

    private void loadPaperCoatingCutLineSkins(ImageView imageView) {
        final int width = bonusLayer.getLayoutParams().width;
        final int height = bonusLayer.getLayoutParams().height;
        final Rect marginRect = new Rect(leftMargin, topMargin, width - rightMargin, height - bottomMargin);
        final String path = SnapsAPI.DOMAIN() + SnapsSkinConstants.SNAPS_SKIN_RESOURCE_URL;

        ComposeBitmapTransformation composeBitmapTrans = new ComposeBitmapTransformation(getContext());

        if (SealStickerProductEditor.glossyType.equals("S")) {
            composeBitmapTrans.add(path + SnapsSkinConstants.SEAL_STICKER_COATING_SPARKLE, marginRect);
        } else if (SealStickerProductEditor.glossyType.equals("A")) {
            composeBitmapTrans.add(path + SnapsSkinConstants.SEAL_STICKER_COATING_AURORA, marginRect);
        }

        if (!isCartThumbnail) {
            composeBitmapTrans.add(SnapsAPI.DOMAIN() + SealStickerProductEditor.sceneCutUrl, marginRect);
            composeBitmapTrans.add(path + SnapsSkinConstants.SEAL_STICKER_BASE_3, 0, 0, width, height);
        }

        Glide.with(getContext())
                .asBitmap()
                .load(Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888))
                .skipMemoryCache(false)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .transform(composeBitmapTrans)
                .into(imageView);
    }

    static class ComposeBitmapTransformation extends BitmapTransformation {
        private static final String ID = "com.snaps.mobile.activity.edit.spc.ComposeBitmapTransformation";
        private static final byte[] ID_BYTES = ID.getBytes(CHARSET);

        private Context context;
        private final List<String> urlList;
        private final List<Rect> rectList;

        ComposeBitmapTransformation(Context context) {
            this.context = context;
            this.urlList = new ArrayList<>();
            this.rectList = new ArrayList<>();
        }

        public void add(String url, Rect rect) {
            urlList.add(url);
            rectList.add(rect);
        }

        public void add(String url, int left, int top, int right, int bottom) {
            urlList.add(url);
            rectList.add(new Rect(left, top, right, bottom));
        }

        @Override
        protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
            Paint paint = new Paint();
            paint.setFilterBitmap(true);

            Rect sourceRect = new Rect();
            Rect targetRect = new Rect();

            Canvas canvas = new Canvas(toTransform);
            for (int i = 0; i < urlList.size(); i++) {
                Bitmap bitmap = loadImage(urlList.get(i));
                sourceRect.set(0, 0, bitmap.getWidth(), bitmap.getHeight());
                targetRect.set(rectList.get(i));
                canvas.drawBitmap(bitmap, sourceRect, targetRect, paint);
            }
            return toTransform;
        }

        private Bitmap loadImage(String requestURL) {
            try {
                return Glide.with(context)
                        .asBitmap()
                        .load(requestURL)
                        .skipMemoryCache(false)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .submit()
                        .get();
            } catch (Exception e) {
                Dlog.e(e);
            }
            return null;
        }

        @Override
        public void updateDiskCacheKey(MessageDigest messageDigest) {
            messageDigest.update(ID_BYTES);
        }
    }

}

