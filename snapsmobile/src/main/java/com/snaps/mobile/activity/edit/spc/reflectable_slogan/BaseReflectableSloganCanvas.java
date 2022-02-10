package com.snaps.mobile.activity.edit.spc.reflectable_slogan;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.snaps.common.image.OverprintBitmap;
import com.snaps.common.image.RxImageLoader;
import com.snaps.common.spc.SnapsPageCanvas;
import com.snaps.common.spc.view.ImageLoadView;
import com.snaps.common.spc.view.SnapsImageView;
import com.snaps.common.structure.SnapsProductOption;
import com.snaps.common.structure.SnapsTemplateManager;
import com.snaps.common.structure.control.SnapsClipartControl;
import com.snaps.common.structure.control.SnapsControl;
import com.snaps.common.structure.control.SnapsTextControl;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.text.SnapsTextToImageAttribute;
import com.snaps.common.text.SnapsTextToImageUtil;
import com.snaps.common.text.SnapsTextToImageView;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.constant.SnapsProductInfoManager;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.BitmapUtil;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.common.utils.ui.ViewIDGenerator;
import com.snaps.mobile.R;
import com.snaps.mobile.utils.shimmer_animation.ShimmerAnimationManager;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;

public abstract class BaseReflectableSloganCanvas extends SnapsPageCanvas {

    private static final String TAG = BaseReflectableSloganCanvas.class.getSimpleName();

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private String gradientSkinPath;
    private SnapsImageView shineEffectImageView = null;
    private Bitmap copiedTextToImage;
    private RxImageLoader rxImageLoader;
    private PublishSubject<OverprintBitmap> imageLoadStream = PublishSubject.create();

    public BaseReflectableSloganCanvas(Context context) {
        super(context);
        rxImageLoader = new RxImageLoader();

        SnapsProductOption snapsProductOption = SnapsTemplateManager.getInstance().getSnapsTemplate().getProductOption();
        String gradientColorCode = snapsProductOption.get(SnapsProductOption.KEY_GRADIENT_TYPE);
        gradientSkinPath = getGradientSkinPath(gradientColorCode);

        if (gradientSkinPath == null) {
            //Finish Editor !
        }
    }

    public abstract String getGradientSkinPath(String gradientColorCode);

    @Override
    public void setSnapsPage(SnapsPage page, int number, boolean isBg, String previewBgColor) {
        super.setSnapsPage(page, number, isBg, previewBgColor);

//        @Marko : 정식 출시 되지 않은 기능이라 일단 막는다.
//        int willOverprintClipartsCount = Observable.fromIterable(_snapsPage.getClipartControlList())
//                .cast(SnapsClipartControl.class)
//                .filter(SnapsClipartControl::isOverPrint)
//                .count()
//                .blockingGet()
//                .intValue();
        int willOverprintClipartsCount = 0;

        int willOverprintTextCount = Observable.fromIterable(_snapsPage.getTextControlList())
                .cast(SnapsTextControl.class)
                .filter(control -> control.format.isOverPrint())
                .count()
                .blockingGet()
                .intValue();

        if (willOverprintClipartsCount + willOverprintTextCount > 0) {
            compositeDisposable.add(imageLoadStream
                    .buffer(willOverprintClipartsCount + willOverprintTextCount)
                    .subscribe(this::makeEffectBitmap, throwable -> Dlog.e(TAG, throwable)));
        }

        effectLayer.setShimmer(ShimmerAnimationManager.getInstance().getSloganShimmer());
    }

    private void makeEffectBitmap(List<OverprintBitmap> overPrintObjects) {
        if (isThumbnailView()) {
            return;
        }

        if (copiedTextToImage == null) {
            copiedTextToImage = BitmapUtil.getInSampledBitmap(width, height, 1);
        } else {
            copiedTextToImage.eraseColor(Color.TRANSPARENT);
        }

        if (copiedTextToImage == null) {
            return;
        }

        copiedTextToImage.eraseColor(Color.WHITE);

        Bitmap mergedBitmap = BitmapUtil.getInSampledBitmap(width, height, 1);
        if (mergedBitmap == null) {
            return;
        }

        Canvas cvs = new Canvas(mergedBitmap);
        Paint paint = new Paint();

        for (OverprintBitmap overprintBitmap : overPrintObjects) {

            SnapsControl snapsControl = overprintBitmap.getSnapsControl();
            String rawAngle = overprintBitmap.getSnapsControl().angle;

            Bitmap bitmap = overprintBitmap.getBitmap();

            if (rawAngle != null && !rawAngle.isEmpty()) {
                Matrix matrix = new Matrix();
                matrix.postRotate(Float.parseFloat(rawAngle));

                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            }

            int left = snapsControl.getIntX();
            int top = snapsControl.getIntY();

            cvs.drawBitmap(bitmap, left, top, paint);
        }

        Canvas bgCanvas = new Canvas(copiedTextToImage);

        paint.setColor(Color.WHITE);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        bgCanvas.drawBitmap(mergedBitmap, 0, 0, paint);
        mergedBitmap.recycle();

        shineEffectImageView.setVisibility(View.VISIBLE);
        shineEffectImageView.setImageBitmap(copiedTextToImage);
        startEffect();
    }

    @Override
    protected void loadControlLayer() {
        super.loadControlLayer();

        FrameLayout.LayoutParams layout = new FrameLayout.LayoutParams(width, height);
        shineEffectImageView = new SnapsImageView(getContext());
        shineEffectImageView.setLayoutParams(layout);

        effectLayer.removeAllViews();
        effectLayer.addView(shineEffectImageView);
    }

    @Override
    protected void loadBonusLayer() {

        FrameLayout.LayoutParams layout = new FrameLayout.LayoutParams(width, height);
        SnapsImageView iv_saftyzone = new SnapsImageView(getContext());
        iv_saftyzone.setLayoutParams(layout);

        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        String pdCode = productInfoManager.getPROD_CODE();

        if (pdCode.equals(Const_PRODUCT.HOLOGRAPHY_SLOGAN_60X20) || pdCode.equals(Const_PRODUCT.REFLECTIVE_SLOGAN_60X20) || pdCode.equals(Const_PRODUCT.MAGICAL_REFLECTIVE_SLOGAN_60X20)) {
            iv_saftyzone.setImageResource(R.drawable.slogan_safety_zone_basic);
        } else {
            iv_saftyzone.setImageResource(R.drawable.slogan_safety_zone_mini);
        }

        bonusLayer.removeAllViews();
        bonusLayer.addView(iv_saftyzone);
    }

//    @Marko : 정식 출시 되지 않은 기능이라 일단 막는다.
//    @Override
//    protected void setClipartControl(final SnapsClipartControl clipartControl) {
//        ImageLoadView view = new ImageLoadView(this.getContext(), clipartControl);
//        view.setSnapsControl(clipartControl);
//
//        // angleclip적용
//        if (!clipartControl.angle.isEmpty()) {
//            view.setRotation(Float.parseFloat(clipartControl.angle));
//        }
//
//        float alpha = Float.parseFloat(clipartControl.alpha);
//        view.setAlpha(alpha);
//
//        controlLayer.addView(view);

//        String requestURL = SnapsAPI.DOMAIN() + clipartControl.resourceURL;
//        int requestWidth = clipartControl.getIntWidth();
//        int requestHeight = clipartControl.getIntHeight();

//        compositeDisposable.add(rxImageLoader.loadImageRx(getContext(), requestURL, requestWidth, requestHeight)
//                .zipWith(rxImageLoader.getBitmapSkin(getContext(), width, gradientSkinPath), (src, skin) -> overPrint(src, skin, clipartControl))
//                .observeOn(AndroidSchedulers.mainThread())
//                .doOnSuccess(view::setImageBitmap)
//                .map(bitmap -> new OverprintBitmap(bitmap, clipartControl))
//                .filter(bitmap -> clipartControl.isOverPrint())
//                .subscribe(overprintBitmap -> imageLoadStream.onNext(overprintBitmap)));
//    }

    @Override
    protected void setMutableTextControl(final SnapsControl control) {

        hideEffect();

        if (!(control instanceof SnapsTextControl)) {
            return;
        }

        SnapsTextControl textControl = (SnapsTextControl) control;
        if (textControl.text == null) {
            textControl.text = "";
        }

        final SnapsTextToImageView snapsTextToImageView = new SnapsTextToImageView(getContext(), textControl, width, height);
        snapsTextToImageView.setTag(textControl);
        snapsTextToImageView.getPlaceHolderTextView().setTag(textControl);

        if (isRealPagerView()) {
            int generatedId = ViewIDGenerator.generateViewId(textControl.getControlId());
            textControl.setControlId(generatedId);
            snapsTextToImageView.setId(generatedId);

            snapsTextToImageView.addClickEventListener(v -> {
                UIUtil.blockClickEvent(v);
                Intent intent = new Intent(Const_VALUE.CLICK_LAYOUT_ACTION);
                intent.putExtra("control_id", snapsTextToImageView.getId());
                intent.putExtra("dummy_control_id", v.getId());
                intent.putExtra("isEdit", false);
                intent.putExtra("viewInCover", _snapsPage.isCover() && _page == 0);

                getContext().sendBroadcast(intent);
            });

        } else if (isThumbnailView()) {
            snapsTextToImageView.setThumbnail(getThumbnailRatioX(), getThumbnailRatioY());
            TextView texView = snapsTextToImageView.getPlaceHolderTextView();
            if (texView != null) {
                texView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 1);
            }
        }

        controlLayer.addView(snapsTextToImageView);

        if (TextUtils.isEmpty(textControl.text)) {
            snapsTextToImageView.showEmptyTextView();
            return;
        }

        SnapsTextToImageAttribute attribute = snapsTextToImageView.getAttribute();

        final String requestURL = SnapsTextToImageUtil.createTextToImageUrlWithAttribute(attribute);
        int mImageScale = attribute.getImageScale();
        int requestWidth = attribute.getSnapsTextControl().getIntWidth();
        int requestHeight = attribute.getSnapsTextControl().getIntHeight();

        if (mImageScale > 1) {
            requestWidth *= mImageScale;
            requestHeight *= mImageScale;
        }

        compositeDisposable.add(rxImageLoader.loadImageRx(getContext(), requestURL, requestWidth, requestHeight)
                .zipWith(rxImageLoader.getBitmapSkin(getContext(), width, gradientSkinPath), (src, skin) -> overPrint(src, skin, textControl))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(snapsTextToImageView::loadTextImage)
                .map(bitmap -> new OverprintBitmap(bitmap, textControl))
                .filter(bitmap -> textControl.format.isOverPrint())
                .subscribe(overprintBitmap -> imageLoadStream.onNext(overprintBitmap)));
    }

    private void hideEffect() {
        if (effectLayer == null) {
            return;
        }

        effectLayer.stopShimmer();
        effectLayer.setVisibility(GONE);
    }

    private void startEffect() {
        if (isThumbnailView()) {
            return;
        }

        if (shineEffectImageView.getDrawable() == null) {
            return;
        }

        Dlog.d("Start Effect ! ");

        Disposable animationDisposable = ShimmerAnimationManager.getInstance().startShimmer(effectLayer, 4000);
        compositeDisposable.add(animationDisposable);
        effectLayer.setVisibility(VISIBLE);
    }

    private Bitmap overPrint(Bitmap src, Bitmap mask, SnapsControl snapsControl) {
        Bitmap emptyBitmap = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);
        Bitmap copiedBitmap = Bitmap.createBitmap(src); //원본 그대로 multiply 하면 캐시에도 적용됨.

        Canvas canvas = new Canvas(emptyBitmap);
        Paint paint = new Paint();
        canvas.drawBitmap(copiedBitmap, 0, 0, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
        canvas.drawBitmap(mask, -snapsControl.getIntX(), -snapsControl.getIntY(), paint);
        copiedBitmap.recycle();
        return emptyBitmap;
    }

    @Override
    public void onDestroyCanvas() {
        super.onDestroyCanvas();

        hideEffect();

        if (!compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
        if (copiedTextToImage != null && !copiedTextToImage.isRecycled()) {
            copiedTextToImage.recycle();
        }
    }

    @Override
    protected void loadShadowLayer() {
    }

    @Override
    protected void loadPageLayer() {
    }

    @Override
    protected void initMargin() {
    }

}
