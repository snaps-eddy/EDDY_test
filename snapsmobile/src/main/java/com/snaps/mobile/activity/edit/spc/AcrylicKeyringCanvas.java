package com.snaps.mobile.activity.edit.spc;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
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
import com.snaps.common.image.RxImageLoader;
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
import com.snaps.common.utils.constant.Const_VALUES;
import com.snaps.common.utils.image.ResolutionUtil;
import com.snaps.common.utils.imageloader.ImageLoader;
import com.snaps.common.utils.imageloader.recoders.AdjustableCropInfo;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.BitmapUtil;
import com.snaps.common.utils.ui.DynamicProductDimensions;
import com.snaps.common.utils.ui.DynamicProductImageSizeConverter;
import com.snaps.common.utils.ui.ImageEdge;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.common.utils.ui.SnapsLanguageUtil;
import com.snaps.common.utils.ui.SwordMan;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.common.products.single_page_product.AcrylicKeyringEditor;
import com.snaps.mobile.activity.edit.skin.SnapsSkinConstants;
import com.snaps.mobile.tutorial.SnapsTutorialAttribute;
import com.snaps.mobile.tutorial.SnapsTutorialConstants;
import com.snaps.mobile.tutorial.new_tooltip_tutorial.SnapsTutorialUtil;
import com.snaps.mobile.utils.custom_layouts.ARelativeLayoutParams;
import com.snaps.mobile.utils.shimmer_animation.ShimmerAnimationManager;
import com.snaps.mobile.utils.ui.SnapsImageViewTarget;

import java.util.ArrayList;
import java.util.List;

import font.FProgressDialog;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class AcrylicKeyringCanvas extends SnapsPageCanvas implements View.OnTouchListener {

    private static final String TAG = AcrylicKeyringCanvas.class.getSimpleName();

    private static final boolean IS_SHOW_GUIDE_LINE_FOR_DEBUG = false;
    public static final int INNER_KEY_HOLE_DIAMETER = 3;
    public static final int KNIFE_LINE_THICKNESS = 2;

    private final int mmWidthUserSelected;
    private final int mmHeightUserSelected;
    private final int zoomLevel;

    // mm to Pixel
    private final int keyholeRadiusPixel;
    private final int keyholeDiameterPixel;
    private final int innerHoleRadiusPixel;
    private final int spaceKeyholePathPixel;
    private final int spaceSceneAndImageViewPixel;
    private final int thicknessKnifeLinePixel;

    private volatile FProgressDialog fProgressDialog;
    private MessageUtil.PriceToast priceToast;
    private UsageGuideToolTip usageGuideToolTip;

    private String textureCode;
    private Bitmap textureSkin;
    private String textureSkinRemotePath;

    public AcrylicKeyringCanvas(Context context) {
        super(context);
        SnapsProductOption snapsProductOption = SnapsTemplateManager.getInstance().getSnapsTemplate().getProductOption();
        mmWidthUserSelected = (int) Float.parseFloat(snapsProductOption.get(SnapsProductOption.KEY_USER_SELECTED_MM_WIDTH));
        mmHeightUserSelected = (int) Float.parseFloat(snapsProductOption.get(SnapsProductOption.KEY_USER_SELECTED_MM_HEIGHT));
        zoomLevel = (int) Float.parseFloat(snapsProductOption.get(SnapsProductOption.KEY_ZOOM_LEVEL));
        int keyholeDiameter = (int) Float.parseFloat(snapsProductOption.get(SnapsProductOption.KEY_KEY_HOLE_DIAMETER));
        int sceneMarginPixel = (int) Float.parseFloat(snapsProductOption.get(SnapsProductOption.KEY_MARGIN_PX));

        keyholeDiameterPixel = keyholeDiameter * zoomLevel;
        keyholeRadiusPixel = Math.round(keyholeDiameter * zoomLevel * 0.5f);

        innerHoleRadiusPixel = Math.round(INNER_KEY_HOLE_DIAMETER * zoomLevel * 0.5f);
        spaceKeyholePathPixel = keyholeRadiusPixel;
        spaceSceneAndImageViewPixel = keyholeDiameter * zoomLevel + sceneMarginPixel;

        thicknessKnifeLinePixel = KNIFE_LINE_THICKNESS * zoomLevel;

        fProgressDialog = new FProgressDialog(mContext, false);
        fProgressDialog.setCancelable(false);

        usageGuideToolTip = new UsageGuideToolTip();
        priceToast = new MessageUtil.PriceToast();

        textureCode = snapsProductOption.get(SnapsProductOption.KEY_KEYRING_TEXTURE_TYPE);
        textureSkinRemotePath = getRemoteTextureImagePath(textureCode);

        rxImageLoader = new RxImageLoader();

        if (textureSkinRemotePath == null) {
            Dlog.e(TAG, "Not found skin path!! ");
        }
    }

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private SnapsMovableImageView keyholeView;
    private SnapsImageView knifeLineImageView = null;
    private SnapsImageView shineEffectImageView = null;
    private SnapsImageView keyHolePathImageViewForDebug = null;
    private KeyHolePath keyHolePath = null;
    private Bitmap layoutLayerCaptureBitmap;
    private SwordMan swordMan = new SwordMan();
    private ImageEdge lastImageEdge;
    private RxImageLoader rxImageLoader;

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

        this.setFocusable(true);
        this.setFocusableInTouchMode(true);
        this.setClickable(true);
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
                        .setTutorialId(SnapsTutorialConstants.eTUTORIAL_ID.TUTORIAL_ID_TOOLTIP_NAME_ACRYLIC_KEYRING_TOUCH_UPLOAD_PNG_FILE)
                        .setTargetView(view)
                        .create());
            }
        }

        FrameLayout.LayoutParams layout = new FrameLayout.LayoutParams(width, height);
        knifeLineImageView = new SnapsImageView(getContext());
        knifeLineImageView.setLayoutParams(layout);

        shineEffectImageView = new SnapsImageView(getContext());
        shineEffectImageView.setLayoutParams(layout);

        Bitmap bitmap = Bitmap.createBitmap(keyholeDiameterPixel, keyholeDiameterPixel, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.parseColor("#e8625a"));
        canvas.drawCircle(keyholeRadiusPixel, keyholeRadiusPixel, keyholeRadiusPixel, paint);

        punchHole(keyholeRadiusPixel, keyholeRadiusPixel, paint, canvas);

        int size = controlLayer.getChildCount();
        for (int i = 0; i < size; i++) {
            View view = controlLayer.getChildAt(i);
            if (view instanceof SnapsMovableImageView) {
                keyholeView = (SnapsMovableImageView) view;
                keyholeView.setTag("Key Hole");
                keyholeView.setWidth(keyholeDiameterPixel);
                keyholeView.setHeight(keyholeDiameterPixel);
                keyholeView.setImageBitmap(bitmap);
                keyholeView.setOnLongClickListener(v -> false);
                keyholeView.setBackgroundColor(Color.TRANSPARENT);
                keyholeView.setVisibility(View.GONE);
                break;
            }
        }

        keyHolePath = new AcrylicKeyringCanvas.KeyHolePath();

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
        final String URL = url;
        SnapsImageViewTarget bitmapImageViewTarget = new SnapsImageViewTarget(getContext(), imageView) {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                super.onResourceReady(resource, transition);

                try {
                    if (isRealPagerView()) {
                        calculateImageCoordinate(resource, view, loadType, URL, rotate);
                    }
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                } finally {
                    if (Looper.myLooper() == Looper.getMainLooper() && view != null) {
                        view.post(() -> subImageLoadCheckCount());
                    } else {
                        subImageLoadCheckCount();
                    }
                }

                getEdgeRect(resource, imageView, layoutControl, imgData);
                isLoadedShadowLayer = true;
            }
        };
        ImageLoader.asyncDisplayImageCenterInside(mContext, imgData, url, bitmapImageViewTarget, mmWidthUserSelected * zoomLevel, mmHeightUserSelected * zoomLevel);

        fProgressDialog.show();
    }

    private void getEdgeRect(Bitmap bitmap, ImageView imageView, SnapsLayoutControl layoutControl, MyPhotoSelectImageData imgData) {
        Rect imageRect = swordMan.extractImageRect(bitmap);

        int bWidth = Math.abs(imageRect.width()) + 1;
        int bHeight = Math.abs(imageRect.height()) + 1;

        DynamicProductImageSizeConverter converter = new DynamicProductImageSizeConverter();
        DynamicProductDimensions dimensions = converter.getFitImageDimensions(bWidth, bHeight, mmWidthUserSelected * zoomLevel, mmHeightUserSelected * zoomLevel);

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

        modifySceneSize(dimensions.getWidth() + spaceSceneAndImageViewPixel * 2, dimensions.getHeight() + spaceSceneAndImageViewPixel * 2, layoutControl);
        mHandler.sendEmptyMessageDelayed(MSG_LOAD_DIY_STICKER_BITMAP, 100);
    }

    private void modifySceneSize(int mWidth, int mHeight, SnapsLayoutControl layoutControl) {

        RelativeLayout.LayoutParams layout = new RelativeLayout.LayoutParams(this.getLayoutParams());
        initMargin();

        layout.width = mWidth + leftMargin + rightMargin;
        layout.height = mHeight + topMargin + bottomMargin;

        getSnapsPage().info.F_PAGE_MM_WIDTH = String.valueOf(Math.round((float) layout.width / (float) zoomLevel));
        getSnapsPage().info.F_PAGE_MM_HEIGHT = String.valueOf(Math.round((float) layout.height / (float) zoomLevel));

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

        int pixelWidth = Math.min(width, mWidth);
        int pixelHeight = Math.min(height, mHeight);

        //변경된 데이터 저장
        _snapsPage.width = Integer.toString(pixelWidth);
        _snapsPage.height = Integer.toString(pixelHeight);

        checkNoPrintImage((float) pixelWidth / (float) zoomLevel, pixelWidth, layoutControl);
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

                //키홀이 움직일 경로 만들기
                Bitmap capturedImage = captureTargetImage();
                createKeyHolePath(capturedImage);
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private Bitmap captureTargetImage() {
        try {
            if (layoutLayerCaptureBitmap == null) {
                layoutLayerCaptureBitmap = BitmapUtil.getInSampledBitmap(width, height, 321);
            } else {
                layoutLayerCaptureBitmap.eraseColor(Color.TRANSPARENT);
            }

            Canvas cvs = new Canvas(layoutLayerCaptureBitmap);
            layoutLayer.draw(cvs);
            return layoutLayerCaptureBitmap;

        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        return null;
    }

    private void createKeyHolePath(final Bitmap oriBitmapArray) {

        Disposable disposable = rxImageLoader.getBitmapSkin(getContext(), 720, textureSkinRemotePath)
                .subscribeOn(Schedulers.io())
                .onErrorReturn(e -> {
                    Dlog.e(TAG, e);
                    Bitmap bitmap = Bitmap.createBitmap(720, 720, Bitmap.Config.ARGB_8888);
                    bitmap.eraseColor(Color.WHITE);
                    return bitmap;
                })
                .flatMap(skin -> {
                    this.textureSkin = skin;
                    return this.swordMan.getKnifeLine(oriBitmapArray, spaceKeyholePathPixel);
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(imageEdges -> {

                    ImageEdge imageEdge = imageEdges.get(0);

                    keyHolePath.clearData();
                    keyHolePath.setData(imageEdge.getEdgePixels(), width, height, imageEdge.getLabelingId());

                    extractImageOutline(oriBitmapArray);
                    showTutorial();
                }, throwable -> Dlog.e(TAG, throwable));

        compositeDisposable.add(disposable);
    }

    private void extractImageOutline(final Bitmap oriBitmapArray) {
        Disposable disposable = this.swordMan.getKnifeLine(oriBitmapArray, thicknessKnifeLinePixel)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(imageEdges -> {

                    ImageEdge imageEdge = imageEdges.get(0);

                    int[] locationOnScreen = new int[2];
                    getLocationOnScreen(locationOnScreen);
                    int canvasOffsetX = locationOnScreen[0];
                    int canvasOffsetY = locationOnScreen[1];
                    multiFlyLayer.getLocationOnScreen(locationOnScreen);
                    int offsetKeyHoleX = locationOnScreen[0] - canvasOffsetX;
                    int offsetKeyHoleY = locationOnScreen[1] - canvasOffsetY;

                    int keyholeX = keyholeView.getSnapsControl().getIntX();
                    int keyholeY = keyholeView.getSnapsControl().getIntY();

                    Point point;

                    if (keyholeX == 0 && keyholeY == 0) {
                        keyHolePath.setOffsetImageView(offsetKeyHoleX, offsetKeyHoleY);
                        int x = UIUtil.getCurrentScreenWidth(mContext) / 2;
                        point = keyHolePath.getMinDistancePoint(x, 0);
                        setKeyholeLocation(point.x, point.y);

                    } else {
                        point = keyHolePath.getMinDistancePoint(keyholeX + keyholeRadiusPixel, keyholeY + keyholeRadiusPixel);
                        setKeyholeLocation(point.x, point.y);
                        keyHolePath.setOffsetImageView(offsetKeyHoleX, offsetKeyHoleY);
                    }

                    drawEdgeAndKeyHole(imageEdge, point.x, point.y);

                    lastImageEdge = imageEdge;
                });
        compositeDisposable.add(disposable);
    }

    private synchronized void setKeyholeLocation(int keyHoleCenterX, int keyHoleCenterY) {
        keyholeView.setX(keyHoleCenterX - keyholeRadiusPixel);
        keyholeView.setY(keyHoleCenterY - keyholeRadiusPixel);
    }

    private void drawEdgeAndKeyHole(ImageEdge imageEdge, int keyholeCenterX, int keyholeCenterY) {
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

        canvas.drawCircle(keyholeCenterX, keyholeCenterY, keyholeRadiusPixel, paint);
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

                    int keyHoleCenterX = keyholeView.getSnapsControl().getIntX() + keyholeRadiusPixel;
                    int keyHoleCenterY = keyholeView.getSnapsControl().getIntY() + keyholeRadiusPixel;
                    punchHole(keyHoleCenterX, keyHoleCenterY, paint, canvas);

                    applyShimmerEffect(bmOut);

                    if (textureSkin == null) {
                        Bitmap withShadow = UIUtil.addShadow(bmOut, Color.parseColor("#b1b1b1"), 3, 2, 2); // 그림자 효과
                        setKnifeLineBitmap(withShadow);

                    } else {
                        Bitmap glitterAppliedKnifeLine = bmOut.copy(bmOut.getConfig(), bmOut.isMutable());
                        Canvas glitterApplyingCanvas = new Canvas(glitterAppliedKnifeLine);
                        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
                        glitterApplyingCanvas.drawBitmap(textureSkin, 0, 0, paint);

                        Bitmap withShadow = UIUtil.addShadow(glitterAppliedKnifeLine, Color.parseColor("#b1b1b1"), 3, 2, 2); // 그림자 효과
                        setKnifeLineBitmap(withShadow);
                    }
                });
        compositeDisposable.add(disposable);
    }

    private void setKnifeLineBitmap(Bitmap bitmap) {
        knifeLineImageView.setVisibility(View.VISIBLE);
        BitmapDrawable bitmapDrawable = (BitmapDrawable) knifeLineImageView.getDrawable();
        if (bitmapDrawable != null) {
            Bitmap preBitmap = bitmapDrawable.getBitmap();
            if (preBitmap != null && !preBitmap.isRecycled()) {
                preBitmap.recycle();
            }
        }

        knifeLineImageView.setImageBitmap(bitmap);

        multiFlyLayer.removeAllViews();
        multiFlyLayer.addView(knifeLineImageView);
    }

    private void applyShimmerEffect(Bitmap bitmap) {
        shineEffectImageView.setVisibility(View.VISIBLE);
        BitmapDrawable oldBitmap = (BitmapDrawable) shineEffectImageView.getDrawable();
        if (oldBitmap != null) {
            Bitmap preBitmap = oldBitmap.getBitmap();
            if (preBitmap != null && !preBitmap.isRecycled()) {
                preBitmap.recycle();
            }
        }
        shineEffectImageView.setImageBitmap(bitmap);

        effectLayer.removeAllViews();
        effectLayer.addView(shineEffectImageView);

        startEffect();
    }

    private void punchHole(int centerX, int centerY, Paint paint, Canvas canvas) {
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        paint.setAntiAlias(true);

        canvas.drawCircle(centerX, centerY, innerHoleRadiusPixel, paint);
    }


    private void showTutorial() {
        SnapsTutorialAttribute attribute = new SnapsTutorialAttribute.Builder().setGifType(SnapsTutorialAttribute.GIF_TYPE.ACRYLIC_KEYING_EDITOR).create();
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
                    View view = new View((Activity) mContext);
                    FrameLayout.LayoutParams layout = new FrameLayout.LayoutParams(keyholeRadiusPixel, keyholeRadiusPixel);
                    view.setLayoutParams(layout);
                    view.setX(keyholeView.getX() + keyholeRadiusPixel / 2);
                    view.setY(keyholeView.getY() + keyholeRadiusPixel / 2);

                    bonusLayer.addView(view);

                    int margin = -125;
                    SnapsTutorialUtil.showTooltipAlways((Activity) mContext,
                            new SnapsTutorialAttribute.Builder().setViewPosition(SnapsTutorialConstants.eTUTORIAL_VIEW_POSITION.BOTTOM)
                                    .setText("[TEST] 롱 터치해서 움직여봐")
                                    .setTargetView(view)
                                    .setTopMargin(UIUtil.convertDPtoPX((Activity) mContext, margin))
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
        if (!AcrylicKeyringEditor.sIsShowPrice) return;
        AcrylicKeyringEditor.sIsShowPrice = false;

        int pageWidth = (int) Float.parseFloat(_snapsPage.width);
        int pageHeight = (int) Float.parseFloat(_snapsPage.height);

        final int imageWidth = Math.round((float) (pageWidth - (2 * spaceSceneAndImageViewPixel)) / zoomLevel);
        final int imageHeight = Math.round((float) (pageHeight - (2 * spaceSceneAndImageViewPixel)) / zoomLevel);

        saveMMSize(imageWidth, imageHeight);

        final float discountRate = getDiscountRate();

        // @Marko 2020.06.29 아크릴 제품 가격 알려주는 API 에 글리터 추가 되므로 papercode 파라미터를 같이 넘겨야 한다.
        // 아크릴 스탠드는 아직 글리터가 제공되지 않으므로 160901 을 기본값으로 넣어준다.
        Flowable<AreaPriceEntity> entity = APIConnection.getInstance().getNewSnapsAPIService()
                .getProductAreaPrice("ACRYLIC_KEYRING", imageWidth, imageHeight, discountRate, textureCode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        Disposable disposable = entity.subscribe(
                areaPriceEntity -> {
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
        sb.append(getContext().getString(R.string.acrylickeyring_resized_fit_photo));
        sb.append("\n");
        sb.append(pageMMWidth).append(" x ").append(pageMMHeight);
        sb.append(" / ");
        sb.append(SnapsLanguageUtil.getCurrencyStr(price));
        sb.append(" ").append("(").append("1").append(getContext().getString(R.string.quantities)).append(")");

        resultMsg = sb.toString();
        return resultMsg;
    }

    volatile boolean isLongClick = false;

    // https://stackoverflow.com/questions/7919865/detecting-a-long-press-with-android
    final GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
        public void onLongPress(MotionEvent e) {
            isLongClick = true;
            hideEffect();
            keyholeView.setVisibility(View.VISIBLE);
            Point point = keyHolePath.getMinDistancePoint((int) e.getX(), (int) e.getY());
            if (point != null) {
                setKeyholeLocation(point.x, point.y);
                UIUtil.performWeakVibration(getContext(), 10);
            }

        }
    });

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (keyHolePath == null || !keyHolePath.isSetData()) {
            return false;
        }

        usageGuideToolTip.cancel();

        gestureDetector.onTouchEvent(event);

        int action = event.getAction();
        int pointCount = event.getPointerCount();
        if (pointCount > 1) {
            //zoom 동작에 키링이 움직이지 않기 위해서
            return false;
        }

        Point point;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                break;

            case MotionEvent.ACTION_MOVE:
                if (isLongClick) {
                    point = keyHolePath.getMinDistancePoint((int) event.getX(), (int) event.getY());
                    if (point != null) {
                        setKeyholeLocation(point.x, point.y);
                    }
                }
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (isLongClick) {
                    keyholeView.setVisibility(View.GONE);
                    onChangeKeyholePosition(keyholeView.getSnapsControl().getIntX() + keyholeRadiusPixel, keyholeView.getSnapsControl().getIntY() + keyholeRadiusPixel);
                    isLongClick = false;
                }
                break;
        }

        if (isLongClick) {
            //zoom 했을때 스크롤 안되게
            return true;
        }

        return false;
    }

    private void onChangeKeyholePosition(int centerX, int centerY) {
        drawEdgeAndKeyHole(lastImageEdge, centerX, centerY);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
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

    @Override
    public void setBgColor(int color) {
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
    }

    @Override
    protected void initMargin() {
    }

    @Override
    public boolean isPreventViewPagerScroll() {
        return true;
    }


    static class KeyHolePath {
        private int mOffsetX;
        private int mOffsetY;
        private List<Integer> mListX;
        private List<Integer> mListY;


        public KeyHolePath() {
            mListX = new ArrayList<>();
            mListY = new ArrayList<>();
        }

        public void clearData() {
            mListX.clear();
            mListY.clear();
        }

        public boolean isSetData() {
            return mListX.size() > 0;
        }

        public void setData(int[] outline, int w, int h, int lineColor) {
            for (int y = 0; y < h; y++) {
                int offsetHeight = y * w;
                for (int x = 0; x < w; x++) {
                    int offset = offsetHeight + x;
                    if (outline[offset] == lineColor) {
                        mListX.add(x);
                        mListY.add(y);
                    }
                }
            }
        }

        public void setOffsetImageView(int x, int y) {
            mOffsetX = x;
            mOffsetY = y;
        }

        public Point getOffsetImageView() {
            return new Point(mOffsetX, mOffsetY);
        }


        public Point getMinDistancePoint(int x, int y) {
            if (mListX.size() == 0) return null;

            int offsetX = x - mOffsetX;
            int offsetY = y - mOffsetY;
            int minDistance = Integer.MAX_VALUE;
            int foundIndex = Integer.MIN_VALUE;
            int size = mListX.size();
            for (int i = 0; i < size; i++) {
                int xx = mListX.get(i);
                int yy = mListY.get(i);
                int xxx = Math.abs(offsetX - xx) * 2;
                int yyy = Math.abs(offsetY - yy) * 2;
                int distance = xxx + yyy;
                if (minDistance > distance) {
                    minDistance = distance;
                    foundIndex = i;
                }
            }

            if (foundIndex == Integer.MIN_VALUE) return null;

            int xxx = mListX.get(foundIndex);
            int yyy = mListY.get(foundIndex);
            Point point = new Point(xxx, yyy);
            //Dlog.d("getMinDistance() point:" + point);
            return point;
        }
    }

    private void hideEffect() {
        effectLayer.stopShimmer();
        effectLayer.setVisibility(GONE);
    }

    private void startEffect() {
        effectLayer.startShimmer();
        effectLayer.setVisibility(VISIBLE);
    }

    private String getRemoteTextureImagePath(String paperCode) {
        if (paperCode == null) {
            return null;
        }

        switch (paperCode) {
            case Const_VALUES.COLOR_CODE_ACRYLIC_GLITTER_HOLOGRAM:
                return SnapsSkinConstants.ACRYLIC_KEYRING_TEXTURE_HOLOGRAM;

            case Const_VALUES.COLOR_CODE_ACRYLIC_GLITTER_GOLD:
                return SnapsSkinConstants.ACRYLIC_KEYRING_TEXTURE_GOLD;

            case Const_VALUES.COLOR_CODE_ACRYLIC_GLITTER_BLUE_GREEN:
                return SnapsSkinConstants.ACRYLIC_KEYRING_TEXTURE_BLUE_GREEN;

            case Const_VALUES.COLOR_CODE_ACRYLIC_GLITTER_DEEP_PINK:
                return SnapsSkinConstants.ACRYLIC_KEYRING_TEXTURE_DEEP_PINK;

            case Const_VALUES.COLOR_CODE_ACRYLIC_GLITTER_PURPLE:
                return SnapsSkinConstants.ACRYLIC_KEYRING_TEXTURE_PURPLE;

            default:
                return SnapsSkinConstants.ACRYLIC_KEYRING_TEXTURE_CLEAR;
        }
    }

}