package com.snaps.mobile.activity.edit.spc;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.os.Looper;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.request.transition.Transition;
import com.snaps.common.customui.RotateImageView;
import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.http.APIConnection;
import com.snaps.common.http.AreaPriceEntity;
import com.snaps.common.spc.SnapsPageCanvas;
import com.snaps.common.spc.view.CustomImageView;
import com.snaps.common.spc.view.SnapsImageView;
import com.snaps.common.spc.view.SnapsMovableImageView;
import com.snaps.common.structure.SnapsProductOption;
import com.snaps.common.structure.SnapsTemplateManager;
import com.snaps.common.structure.SnapsTemplatePrice;
import com.snaps.common.structure.control.SnapsLayoutControl;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.image.ResolutionUtil;
import com.snaps.common.utils.imageloader.ImageLoader;
import com.snaps.common.utils.imageloader.recoders.AdjustableCropInfo;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.BitmapUtil;
import com.snaps.common.utils.ui.DynamicProductDimensions;
import com.snaps.common.utils.ui.DynamicProductImageSizeConverter;
import com.snaps.common.utils.ui.HelperInfo;
import com.snaps.common.utils.ui.HelperMaker;
import com.snaps.common.utils.ui.ImageEdge;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.common.utils.ui.SnapsLanguageUtil;
import com.snaps.common.utils.ui.SwordMan;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.common.products.single_page_product.AcrylicStandEditor;
import com.snaps.mobile.tutorial.SnapsTutorialAttribute;
import com.snaps.mobile.tutorial.SnapsTutorialConstants;
import com.snaps.mobile.tutorial.new_tooltip_tutorial.SnapsTutorialUtil;
import com.snaps.mobile.utils.custom_layouts.ARelativeLayoutParams;
import com.snaps.mobile.utils.shimmer_animation.ShimmerAnimationManager;
import com.snaps.mobile.utils.ui.SnapsImageViewTarget;

import font.FProgressDialog;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class AcrylicStandCanvas extends SnapsPageCanvas implements View.OnTouchListener {
    private static final String TAG = AcrylicStandCanvas.class.getSimpleName();

    private final int IMAGE_HEIGHT;
    private final int IMAGE_WIDTH;
    private final int ZOOM_LEVEL;
    private final int MARGIN;
    private final int KNIFE_LINE;
    private final int IMAGEVIEW_TOP_MARGIN;
    private final int IMAGEVIEW_LEFT_MARGIN;
    private final int IMAGEVIEW_RIGHT_MARGIN;
    private final int IMAGEVIEW_BOTTOM_MARGIN;
    private final int STICK_WIDTH;
    private final int STICK_HEIGHT;
    private final int HELPER_MIN_WIDTH;

    private SwordMan swordMan = new SwordMan();
    private HelperMaker helperMaker = new HelperMaker();
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private SnapsImageView knifeLineImageView = null;
    private SnapsImageView shineEffectImageView = null;
    private Bitmap layoutLayerCaptureBitmap;

    private ImageEdge lastImageEdge;
    private HelperInfo lastHelperInfo;

    private SnapsMovableImageView helperView;
    private SnapsMovableImageView stickView;

    private boolean isHelperMoveable = false;

    private volatile FProgressDialog fProgressDialog;
    private MessageUtil.PriceToast priceToast;
    private UsageGuideToolTip usageGuideToolTip;

    public AcrylicStandCanvas(Context context) {
        super(context);
        SnapsProductOption snapsProductOption = SnapsTemplateManager.getInstance().getSnapsTemplate().getProductOption();

        int zoomLevel = 6;
        int knifeLine = 6;
        int margin = 9;
        int stickWidth = 42;
        int stickHeight = 9;
        int helperMinWidth = 60;
        int userSelectedMMHeight = 200;
        int userSelectedMMWidth = 200;

        try {
            zoomLevel = (int) Float.parseFloat(snapsProductOption.get(SnapsProductOption.KEY_ZOOM_LEVEL));
            knifeLine = (int) Float.parseFloat(snapsProductOption.get(SnapsProductOption.KEY_KNIFE_LINE_PX));
            margin = (int) Float.parseFloat(snapsProductOption.get(SnapsProductOption.KEY_MARGIN_PX));
            stickWidth = (int) Float.parseFloat(snapsProductOption.get(SnapsProductOption.KEY_STICK_WIDTH_PX));
            stickHeight = (int) Float.parseFloat(snapsProductOption.get(SnapsProductOption.KEY_STICK_HEIGHT_PX));
            helperMinWidth = (int) Float.parseFloat(snapsProductOption.get(SnapsProductOption.KEY_HELPER_MIN_WIDTH_PX));
            userSelectedMMHeight = (int) Float.parseFloat(snapsProductOption.get(SnapsProductOption.KEY_USER_SELECTED_MM_HEIGHT));
            userSelectedMMWidth = (int) Float.parseFloat(snapsProductOption.get(SnapsProductOption.KEY_USER_SELECTED_MM_WIDTH));
        } catch (NumberFormatException e) {
            Dlog.e(TAG, e);
        }

        ZOOM_LEVEL = zoomLevel;
        MARGIN = margin;
        KNIFE_LINE = knifeLine;
        STICK_WIDTH = stickWidth;
        STICK_HEIGHT = stickHeight;
        HELPER_MIN_WIDTH = helperMinWidth;
        IMAGE_HEIGHT = userSelectedMMHeight * zoomLevel;
        IMAGE_WIDTH = userSelectedMMWidth * zoomLevel;

        IMAGEVIEW_TOP_MARGIN = KNIFE_LINE + MARGIN;
        IMAGEVIEW_LEFT_MARGIN = IMAGEVIEW_TOP_MARGIN;
        IMAGEVIEW_RIGHT_MARGIN = IMAGEVIEW_TOP_MARGIN;
        IMAGEVIEW_BOTTOM_MARGIN = IMAGEVIEW_TOP_MARGIN + STICK_HEIGHT;

        fProgressDialog = new FProgressDialog(mContext, false);
        fProgressDialog.setCancelable(false);

        usageGuideToolTip = new UsageGuideToolTip();
        priceToast = new MessageUtil.PriceToast();
    }

    @Override
    public void setSnapsPage(SnapsPage page, int number, boolean isBg, String previewBgColor) {
        super.setSnapsPage(page, number, isBg, previewBgColor);
        LayoutParams baseLayout = new LayoutParams(this.width, this.height);
        multiFlyLayer = new FrameLayout(this.getContext());
        MarginLayoutParams shadowParams = new ARelativeLayoutParams(baseLayout);
        shadowParams.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
        multiFlyLayer.setLayoutParams(shadowParams);
        this.addView(multiFlyLayer, this.getChildCount() - 2);

        effectLayer.setShimmer(ShimmerAnimationManager.getInstance().getAcrylicShimmer());

        this.setOnTouchListener(this);
    }

    @Override
    protected void loadControlLayer() {
        super.loadControlLayer();

        for (int i = 0; i < layoutLayer.getChildCount(); i++) {
            View view = layoutLayer.getChildAt(i);
            if (view instanceof RotateImageView) {
                SnapsTutorialUtil.showTooltip((Activity) mContext, new SnapsTutorialAttribute.Builder().setViewPosition(
                        SnapsTutorialConstants.eTUTORIAL_VIEW_POSITION.TOP)
                        .setText(getContext().getString(R.string.touch_upload_png_photo_file))
                        .setTutorialId(SnapsTutorialConstants.eTUTORIAL_ID.TUTORIAL_ID_TOOLTIP_NAME_ACRYLIC_STAND_TOUCH_UPLOAD_PNG_FILE)
                        .setTargetView(view)
                        .create());
            }
        }

        FrameLayout.LayoutParams layout = new FrameLayout.LayoutParams(width, height);
        knifeLineImageView = new SnapsImageView(getContext());
        knifeLineImageView.setLayoutParams(layout);

        shineEffectImageView = new SnapsImageView(getContext());
        shineEffectImageView.setLayoutParams(layout);

        int size = controlLayer.getChildCount();
        for (int i = 0; i < size; i++) {
            View view = controlLayer.getChildAt(i);
            if (view instanceof SnapsMovableImageView) {
                SnapsMovableImageView movableView = (SnapsMovableImageView) view;

                if ("helper".equalsIgnoreCase(movableView.getSnapsControl().getSnsproperty())) {
                    helperView = movableView;
                    helperView.setTag("helper");
                    helperView.setOnLongClickListener(v -> false);
                    helperView.setBackgroundColor(Color.parseColor("#e8625a"));
                    helperView.setVisibility(View.GONE);
                }

                if ("stick".equalsIgnoreCase(movableView.getSnapsControl().getSnsproperty())) {
                    stickView = movableView;
                    stickView.setTag("stick");
                    stickView.setOnLongClickListener(v -> false);
                    stickView.setVisibility(View.GONE);
                }
            }
        }

        int count = layoutLayer.getChildCount();
        for (int i = 0; i < count; i++) {
            View view = layoutLayer.getChildAt(i);
            if (view instanceof RotateImageView) {
                //확대되서 +버튼이 너무 크게 나와서 축소
                float scale = 1.0f / getCurrentScaleFactor();
                view.setScaleX(scale);
                view.setScaleY(scale);
                break;
            }
        }
    }

    @Override
    protected void loadImage(String url, ImageView imageView, final int loadType, final int rotate, MyPhotoSelectImageData imgData, SnapsLayoutControl layoutControl) {
        SnapsImageViewTarget bitmapImageViewTarget = new SnapsImageViewTarget(getContext(), imageView) {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                super.onResourceReady(resource, transition);
                if (Looper.myLooper() == Looper.getMainLooper() && view != null) {
                    view.post(() -> subImageLoadCheckCount());
                } else {
                    subImageLoadCheckCount();
                }
                getEdgeRect(resource, imageView, layoutControl, imgData);
                isLoadedShadowLayer = true;
            }
        };
        ImageLoader.asyncDisplayImageCenterInside(mContext, imgData, url, bitmapImageViewTarget, IMAGE_WIDTH, IMAGE_HEIGHT);

        fProgressDialog.show();
    }

    private void getEdgeRect(Bitmap bitmap, ImageView imageView, SnapsLayoutControl layoutControl, MyPhotoSelectImageData imgData) {
        Rect imageRect = swordMan.extractImageRect(bitmap);

        int bWidth = Math.abs(imageRect.width()) + 1;
        int bHeight = Math.abs(imageRect.height()) + 1;

        DynamicProductImageSizeConverter converter = new DynamicProductImageSizeConverter();
        DynamicProductDimensions dimensions = converter.getFitImageDimensions(bWidth, bHeight, IMAGE_WIDTH, IMAGE_HEIGHT);

        ViewGroup.LayoutParams params = imageView.getLayoutParams();
        params.width = dimensions.getWidth();
        params.height = dimensions.getHeight();
        layoutControl.width = String.valueOf(dimensions.getWidth());
        layoutControl.height = String.valueOf(dimensions.getHeight());

        AdjustableCropInfo.CropImageRect clipRect = new AdjustableCropInfo.CropImageRect();
        clipRect.width = dimensions.getWidth();
        clipRect.height = dimensions.getHeight();

        AdjustableCropInfo.CropImageRect imgRect = new AdjustableCropInfo.CropImageRect();
        imgRect.width = bitmap.getWidth() * dimensions.getPostScaleFactor();
        imgRect.height = bitmap.getHeight() * dimensions.getPostScaleFactor();
        imgRect.scaleX = 1.0f;
        imgRect.scaleY = 1.0f;

        float rectCenterX = (imageRect.centerX() + 1) * dimensions.getPostScaleFactor();
        float rectCenterY = (imageRect.centerY() + 1) * dimensions.getPostScaleFactor();

        float bitmapCenterX = bitmap.getWidth() * dimensions.getPostScaleFactor() / 2;
        float bitmapCenterY = bitmap.getHeight() * dimensions.getPostScaleFactor() / 2;

        imgRect.movedX = -(rectCenterX - bitmapCenterX);
        imgRect.movedY = -(rectCenterY - bitmapCenterY);

        AdjustableCropInfo adjustableCropInfo = imgData.ADJ_CROP_INFO;
        adjustableCropInfo.setClipRect(clipRect);
        adjustableCropInfo.setImgRect(imgRect);
        imgData.isAdjustableCropMode = true;

        Matrix matrix = new Matrix();
        matrix.postTranslate(-imageRect.left, -imageRect.top);
        matrix.postScale(dimensions.getPostScaleFactor(), dimensions.getPostScaleFactor());
        imageView.setImageMatrix(matrix);

        modifySceneSize(dimensions.getWidth() + IMAGEVIEW_LEFT_MARGIN + IMAGEVIEW_RIGHT_MARGIN, dimensions.getHeight() + IMAGEVIEW_TOP_MARGIN + IMAGEVIEW_BOTTOM_MARGIN, layoutControl);
        mHandler.sendEmptyMessageDelayed(MSG_LOAD_DIY_STICKER_BITMAP, 100);
    }

    private void modifySceneSize(int mWidth, int mHeight, SnapsLayoutControl layoutControl) {

        RelativeLayout.LayoutParams layout = new RelativeLayout.LayoutParams(this.getLayoutParams());
        initMargin();

        layout.width = mWidth + leftMargin + rightMargin;
        layout.height = mHeight + topMargin + bottomMargin;

        getSnapsPage().info.F_PAGE_MM_WIDTH = String.valueOf(Math.round(layout.width / (float) ZOOM_LEVEL));
        getSnapsPage().info.F_PAGE_MM_HEIGHT = String.valueOf(Math.round(layout.height / (float) ZOOM_LEVEL));

        getSnapsPage().info.F_PAGE_PIXEL_WIDTH = String.valueOf(layout.width);
        getSnapsPage().info.F_PAGE_PIXEL_HEIGHT = String.valueOf(layout.height);

        RelativeLayout.LayoutParams shadowlayout = new RelativeLayout.LayoutParams(layout.width, layout.height);
        shadowLayer.setLayoutParams(shadowlayout);

        multiFlyLayer.setLayoutParams(shadowlayout);

        ViewGroup.MarginLayoutParams containerlayout = new ViewGroup.MarginLayoutParams(mWidth, mHeight);
        containerlayout.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
        containerLayer.setLayout(new RelativeLayout.LayoutParams(containerlayout));

        bonusLayer.setLayoutParams(new ARelativeLayoutParams(shadowlayout));

        // bgLayer 사이즈 변경.
        FrameLayout.LayoutParams baseLayout = new FrameLayout.LayoutParams(mWidth, mHeight);
        bgLayer.setLayoutParams(baseLayout);

        // layoutLayer 사이즈 변경.
        layoutLayer.setLayoutParams(baseLayout);

        // controllLayer 사이즈 변경.
        controlLayer.setLayoutParams(baseLayout);

        // formLayer 사이즈 변경.
        formLayer.setLayoutParams(baseLayout);

        // pageLayer 사이즈 변경.
        pageLayer.setLayoutParams(baseLayout);

        // effectLayer 사이즈 변경.
        effectLayer.setLayoutParams(baseLayout);

        int pixelWidth = Math.min(width, mWidth);
        int pixelHeight = Math.min(height, mHeight);

        //변경된 데이터 저장
        _snapsPage.width = String.valueOf(pixelWidth);
        _snapsPage.height = String.valueOf(pixelHeight);

        checkNoPrintImage((float) pixelWidth / (float) ZOOM_LEVEL, pixelWidth, layoutControl);
    }

    private void checkNoPrintImage(float pageMMWidth, int pagePixelWidth, SnapsLayoutControl layoutControl) {
        // 인쇄가능 여부 검사
        try {
            ResolutionUtil.isEnableResolution(pageMMWidth, pagePixelWidth, layoutControl);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        //인쇄가능 여부 UI 표시
        //SnapsPageCanvas 안쪽에 Alert 이미지 그리는 부분이 있어서 아래와 같이 처리
        if (layoutControl.isNoPrintImage) {
            int count = containerLayer.getChildCount();
            for (int i = 0; i < count; i++) {
                View view = containerLayer.getChildAt(i);
                if (view instanceof ImageView && SnapsPageCanvas.TAG_IMAGEVIEW_NO_PRINT_IMAGE_ALERT.equals(view.getTag())) {
                    ((ImageView) view).setImageResource(R.drawable.alert_01);
                    break;
                }
            }
        }
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);

        try {
            if (msg.what == MSG_LOAD_DIY_STICKER_BITMAP) {
                //이미지 클릭시 변경|편집|삭제 팝업 메뉴 동작 안하게 수정
                for (int i = 0; i < layoutLayer.getChildCount(); i++) {
                    View view = layoutLayer.getChildAt(i);
                    if (view instanceof CustomImageView) {
                        CustomImageView customImageView = (CustomImageView) view;
                        customImageView.setIsPreview(true);
                        break;
                    }
                }

                initLocation(true);

                Bitmap capturedImage = captureTargetImage();
                extractImageOutline(capturedImage);

            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    @Override
    public void onDestroyCanvas() {
        if (bonusLayer != null) {
            Drawable d = bonusLayer.getBackground();
            if (d != null) {
                try {
                    d.setCallback(null);
                } catch (Exception ignore) {
                }
            }
        }

        compositeDisposable.clear();

        usageGuideToolTip.cancel();

        priceToast.cancel();

        if (effectLayer != null) {
            effectLayer.stopShimmer();
        }

        super.onDestroyCanvas();
    }

    private Bitmap captureTargetImage() {
        if (layoutLayerCaptureBitmap == null) {
            layoutLayerCaptureBitmap = BitmapUtil.getInSampledBitmap(width, height, 1);
        } else {
            layoutLayerCaptureBitmap.eraseColor(Color.TRANSPARENT);
        }

        Canvas cvs = new Canvas(layoutLayerCaptureBitmap);
        layoutLayer.draw(cvs);
        return layoutLayerCaptureBitmap;
    }

    private void extractImageOutline(final Bitmap oriBitmapArray) {
        Disposable disposable = this.swordMan.getKnifeLine(oriBitmapArray, ZOOM_LEVEL * 2)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(imageEdges -> {

                    ImageEdge imageEdge = imageEdges.get(0);
                    HelperInfo helperInfo;

                    int helperX = helperView.getSnapsControl().getIntX();
                    int helperY = helperView.getSnapsControl().getIntY();

                    if (helperX == 0 && helperY == 0) {
                        helperInfo = helperMaker.makeHelper(imageEdge, width, HELPER_MIN_WIDTH);
                    } else {
//                        helperX += helperView.getSnapsControl().getIntWidth() / 2;
                        helperInfo = helperMaker.restoreHelper(imageEdge, helperX, helperY, width, helperView.getSnapsControl().getIntWidth());
                    }

                    drawHelper(imageEdge, helperInfo);

                    lastImageEdge = imageEdge;
                    lastHelperInfo = helperInfo;

                    initHoverView();

                    //튜토리얼
                    showTutorial();
                });
        compositeDisposable.add(disposable);
    }

    private void initHoverView() {

        isHelperMoveable = lastHelperInfo.getHelperWidth() < lastImageEdge.getFitImageRect().width();

        helperView.setX(lastHelperInfo.getLeftMatchPoint().x);
        helperView.setY(lastHelperInfo.getLowestCenterPoint().y);
        helperView.setWidth(lastHelperInfo.getHelperWidth());
        helperView.setHeight(STICK_HEIGHT);

        stickView.setX(lastHelperInfo.getLowestCenterPoint().x - STICK_WIDTH / 2);
        stickView.setY(lastHelperInfo.getLowestCenterPoint().y);
        stickView.setWidth(STICK_WIDTH);
        stickView.setHeight(STICK_HEIGHT);
        stickView.setBackgroundColor(Color.CYAN);

    }

    private void drawHelper(ImageEdge imageEdge, HelperInfo helperInfo) {
        Path path = new Path();
        Paint paint = new Paint();
        paint.setStrokeWidth(1);
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);

        for (int outlineIndex : imageEdge.getOutlinesOnly()) {
            int x = outlineIndex % width;
            int y = outlineIndex / width;

            if (path.isEmpty()) {
                path.moveTo(x, y);
            } else {
                path.lineTo(x, y);
            }
        }
        path.close();

        Bitmap bmOut = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmOut);
        canvas.drawPath(path, paint);

        path.reset();
        path.moveTo(helperInfo.getLeftMatchPoint().x, helperInfo.getLeftMatchPoint().y);
        path.lineTo(helperInfo.getLeftMatchPoint().x, helperInfo.getLowestCenterPoint().y);
        path.lineTo(helperInfo.getRightMatchPoint().x, helperInfo.getLowestCenterPoint().y);
        path.lineTo(helperInfo.getRightMatchPoint().x, helperInfo.getRightMatchPoint().y);
        path.close();
        canvas.drawPath(path, paint);

        canvas.drawRect(helperInfo.getStick(STICK_WIDTH, STICK_HEIGHT), paint);

        drawOutLineImage(bmOut);
    }

    private void drawOutLineImage(final Bitmap oriBitmapArray) {
        Disposable disposable = this.swordMan.getKnifeLine(oriBitmapArray, 0)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(imageEdges -> {

                    ImageEdge imageEdge = imageEdges.get(0);
                    Path path = new Path();
                    Paint paint = new Paint();
                    paint.setStrokeWidth(1);
                    paint.setColor(Color.WHITE);
                    paint.setStyle(Paint.Style.FILL_AND_STROKE);

                    for (int outlineIndex : imageEdge.getOutlinesOnly()) {
                        int x = outlineIndex % width;
                        int y = outlineIndex / width;

                        if (path.isEmpty()) {
                            path.moveTo(x, y);

                        } else {
                            path.lineTo(x, y);
                        }
                    }

                    path.close();

                    Bitmap bmOut = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(bmOut);
                    canvas.drawPath(path, paint);

                    if (Config.isDevelopVersion()) {
                        if (imageEdges.size() > 1) {
                            ImageEdge imageEdge2 = imageEdges.get(1);
                            paint.setStrokeWidth(1);
                            paint.setColor(Color.BLUE);
                            paint.setStyle(Paint.Style.FILL_AND_STROKE);

                            path.reset();

                            for (int outlineIndex : imageEdge2.getOutlinesOnly()) {
                                int x = outlineIndex % width;
                                int y = outlineIndex / width;

                                if (path.isEmpty()) {
                                    path.moveTo(x, y);

                                } else {
                                    path.lineTo(x, y);
                                }
                            }
                            canvas.drawPath(path, paint);
                        }
                    }

                    Bitmap withShadow = UIUtil.addShadow(bmOut, Color.parseColor("#b1b1b1"), 3, 2, 2); // 그림자 효과
                    overDrawBitmap(withShadow);
                });
        compositeDisposable.add(disposable);
    }

    private void overDrawBitmap(Bitmap bitmap) {
        knifeLineImageView.setVisibility(View.VISIBLE);
        BitmapDrawable bitmapDrawable = (BitmapDrawable) knifeLineImageView.getDrawable();
        if (bitmapDrawable != null) {
            Bitmap preBitmap = bitmapDrawable.getBitmap();
            if (preBitmap != null && !preBitmap.isRecycled()) {
                preBitmap.recycle();
            }
        }

        shineEffectImageView.setVisibility(View.VISIBLE);
        BitmapDrawable oldBitmap = (BitmapDrawable) shineEffectImageView.getDrawable();
        if (bitmapDrawable != null) {
            Bitmap preBitmap = oldBitmap.getBitmap();
            if (preBitmap != null && !preBitmap.isRecycled()) {
                preBitmap.recycle();
            }
        }

        knifeLineImageView.setImageBitmap(bitmap);
        shineEffectImageView.setImageBitmap(bitmap);

        multiFlyLayer.removeAllViews();
        multiFlyLayer.addView(knifeLineImageView);

        effectLayer.removeAllViews();
        effectLayer.addView(shineEffectImageView);

//        effectLayer.startShimmer();
        startEffect();
    }


    private void showTutorial() {
        SnapsTutorialAttribute attribute = new SnapsTutorialAttribute.Builder().setGifType(SnapsTutorialAttribute.GIF_TYPE.ACRYLIC_STAND_EDITOR).create();
        SnapsTutorialUtil.showGifView((Activity) mContext, attribute, () -> {
            fProgressDialog.dismiss();
            showPrice();
        });
    }

    class UsageGuideToolTip {
        private volatile CountDownTimer mCountDownTimer;

        public UsageGuideToolTip() {
            mCountDownTimer = null;
        }

        public void start() {
            if (mCountDownTimer != null) return;

            long time = MessageUtil.PriceToast.DEFAULT_DURATION_MILLISECONDS + 5000;
            mCountDownTimer = new CountDownTimer(time, time) {
                public void onTick(long millisUntilFinished) {
                }

                public void onFinish() {
                    View view = new View(mContext);
                    FrameLayout.LayoutParams layout = new FrameLayout.LayoutParams(lastHelperInfo.getHelperWidth(), STICK_HEIGHT);
                    view.setLayoutParams(layout);
                    view.setX(helperView.getX());
                    view.setY(helperView.getY());

                    bonusLayer.addView(view);

                    int margin = 95;
                    SnapsTutorialUtil.showTooltipAlways((Activity) mContext,
                            new SnapsTutorialAttribute.Builder().setViewPosition(SnapsTutorialConstants.eTUTORIAL_VIEW_POSITION.TOP)
                                    .setText("[TEST] 롱 터치해서 움직여봐")
                                    .setTargetView(view)
                                    .setTopMargin(UIUtil.convertDPtoPX(mContext, margin))
                                    .create());
                }
            };

            try {
                mCountDownTimer.start();
            } catch (Exception e) {
                Dlog.e(TAG, e);
            }
        }

        public void cancel() {
            if (mCountDownTimer == null) return;

            try {
                mCountDownTimer.cancel();
            } catch (Exception e) {
                Dlog.e(TAG, e);
            } finally {
                mCountDownTimer = null;
            }

            SnapsTutorialUtil.clearTooltip();
        }
    }

    private void showPrice() {
        if (!AcrylicStandEditor.sIsShowPrice) return;
        AcrylicStandEditor.sIsShowPrice = false;

        int pageWidth = (int) Float.parseFloat(_snapsPage.width);
        int pageHeight = (int) Float.parseFloat(_snapsPage.height);

        final int imageWidth = Math.round((float) (pageWidth - (IMAGEVIEW_LEFT_MARGIN + IMAGEVIEW_RIGHT_MARGIN)) / ZOOM_LEVEL);
        final int imageHeight = Math.round((float) (pageHeight - (IMAGEVIEW_TOP_MARGIN + IMAGEVIEW_BOTTOM_MARGIN)) / ZOOM_LEVEL);

        saveMMSize(imageWidth, imageHeight);

        final float discountRate = getDiscountRate();

        // @Marko 2020.06.29 아크릴 제품 가격 알려주는 API 에 글리터 추가 되므로 papercode 파라미터를 같이 넘겨야 한다.
        // 아크릴 스탠드는 아직 글리터가 제공되지 않으므로 160901 을 기본값으로 넣어준다.
        Flowable<AreaPriceEntity> entity = APIConnection.getInstance().getNewSnapsAPIService()
                .getProductAreaPrice("ACRYLIC_LIFESIZE_STAND", imageWidth, imageHeight, discountRate, "160901")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        Disposable disposable = entity.subscribe(areaPriceEntity -> {
                    String priceMsg = getPriceFormattedMessage(imageWidth, imageHeight, areaPriceEntity);
                    if (priceMsg.length() == 0) return;

                    priceToast.show((Activity) mContext, priceMsg, MessageUtil.PriceToast.DEFAULT_DURATION_MILLISECONDS);
                    // usageGuideToolTip.start();  //TODO::일단 주석 처리
                },

                throwable -> Dlog.e(TAG, throwable));
        compositeDisposable.add(disposable);
    }

    private float getDiscountRate() {
        SnapsTemplatePrice snapsTemplatePrice = SnapsTemplateManager.getInstance().getSnapsTemplate().priceList.get(0);
        float discountRate = 0f;
        try {
            discountRate = Float.parseFloat(snapsTemplatePrice.F_DISC_RATE);
        } catch (NumberFormatException e) {
            Dlog.e(TAG, e);
        }
        return discountRate;
    }

    private void saveMMSize(int pageMMWidth, int pageMMHeight) {
        SnapsProductOption snapsProductOption = SnapsTemplateManager.getInstance().getSnapsTemplate().getProductOption();
        snapsProductOption.set(SnapsProductOption.KEY_MM_WIDTH, Integer.toString(pageMMWidth));
        snapsProductOption.set(SnapsProductOption.KEY_MM_HEIGHT, Integer.toString(pageMMHeight));
    }

    private String getPriceFormattedMessage(int pageMMWidth, int pageMMHeight, AreaPriceEntity areaPriceEntity) {
        String resultMsg = "";

        if (areaPriceEntity == null) {
            return resultMsg;
        }

        float price = areaPriceEntity.getSellPrice();

        StringBuilder sb = new StringBuilder();
        sb.append(getContext().getString(R.string.acrylicStand_resized_fit_photo));
        sb.append("\n");
        sb.append(pageMMWidth).append(" x ").append(pageMMHeight);
        sb.append(" / ");
        sb.append(SnapsLanguageUtil.getCurrencyStr(price));

        resultMsg = sb.toString();
        return resultMsg;
    }


    // https://stackoverflow.com/questions/7919865/detecting-a-long-press-with-android
    final GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
        public void onLongPress(MotionEvent e) {
            isLongClick = true;
            hideEffect();
            helperView.setVisibility(View.VISIBLE);
            UIUtil.performWeakVibration(getContext(), 10);
        }
    });

    volatile boolean isLongClick = false;
    volatile float lastMoveX;
    volatile float longClickScaleFactor = 1.0f;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (lastImageEdge == null || lastHelperInfo == null) {
            return false;
        }

        if (!isHelperMoveable) {
            return false;
        }

        usageGuideToolTip.cancel();

        gestureDetector.onTouchEvent(event);

        int action = event.getAction();
        int pointCount = event.getPointerCount();
        if (pointCount > 1) {
            //zoom 동작에 헬퍼가 움직이지 않기 위해서
            return false;
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                longClickScaleFactor = getCurrentScaleFactor();
                //터치 이벤트 좌표를 캔버스 배율에 맞게 조정해서 값을 구한다.
                lastMoveX = helperView.getX() - (event.getRawX() / longClickScaleFactor);
                break;

            case MotionEvent.ACTION_MOVE:
                if (isLongClick) {
                    float minX = lastImageEdge.getFitImageRect().left;
                    float maxX = lastImageEdge.getFitImageRect().right - lastHelperInfo.getHelperWidth();

                    //터치 이벤트 좌표를 캔버스 배율에 맞게 조정해서 값을 구한다.
                    float correctX = (event.getRawX() / longClickScaleFactor) + lastMoveX;
                    correctX = Math.min(Math.max(correctX, minX), maxX);

                    helperView.setX(correctX);
                    helperView.setY(lastHelperInfo.getLowestCenterPoint().y);
                }
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (isLongClick) {
                    helperView.setVisibility(View.GONE);
                    onChangeHelperPosition(helperView.getSnapsControl().getIntX() + lastHelperInfo.getHelperWidth() / 2);
                    isLongClick = false;
                }
                break;
        }

        //zoom 했을때 스크롤 안되게
        return isLongClick;
    }

    private void onChangeHelperPosition(int centerX) {
        if (lastImageEdge == null || lastHelperInfo == null) {
            return;
        }

        HelperInfo newPositionHelperInfo = helperMaker.moveHelper(lastImageEdge, centerX, lastHelperInfo.getLowestCenterPoint().y, width, lastHelperInfo.getHelperWidth());

        drawHelper(lastImageEdge, newPositionHelperInfo);

        stickView.setX(newPositionHelperInfo.getLowestCenterPoint().x - STICK_WIDTH / 2);
        stickView.setY(lastHelperInfo.getLowestCenterPoint().y);

        lastHelperInfo = newPositionHelperInfo;
    }

    private void hideEffect() {
        effectLayer.stopShimmer();
        effectLayer.setVisibility(GONE);
    }

    private void startEffect() {
        effectLayer.startShimmer();
        effectLayer.setVisibility(VISIBLE);
    }


    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    protected void loadShadowLayer() {
    }

    @Override
    protected void loadPageLayer() {
    }

    @Override
    protected void loadBonusLayer() {
    }

    @Override
    protected void initMargin() {
    }

    @Override
    public boolean isPreventViewPagerScroll() {
        return true;
    }
}
