package com.snaps.mobile.utils.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import androidx.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.data.smart_snaps.SmartSnapsImgInfo;
import com.snaps.common.data.smart_snaps.interfacies.ISmartSnapImgDataAnimationState;
import com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants;
import com.snaps.common.spc.view.CustomImageView;
import com.snaps.common.structure.control.SnapsLayoutControl;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.imageloader.CropUtil;
import com.snaps.common.utils.imageloader.ISnapsImageViewTarget;
import com.snaps.common.utils.imageloader.ImageLoader;
import com.snaps.common.utils.imageloader.SnapsCustomTargets;
import com.snaps.common.utils.imageloader.filters.ImageEffectBitmap;
import com.snaps.common.utils.imageloader.filters.ImageFilters;
import com.snaps.common.utils.imageloader.recoders.AdjustableCropInfo;
import com.snaps.common.utils.imageloader.recoders.BaseCropInfo;
import com.snaps.common.utils.imageloader.recoders.EffectFilerMaker;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.BitmapUtil;
import com.snaps.common.utils.ui.ContextUtil;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.activity.common.SnapsEditActivity;
import com.snaps.mobile.activity.common.interfacies.SnapsProductEditorAPI;
import com.snaps.mobile.activity.themebook.PhotobookCommonUtils;
import com.snaps.mobile.component.image_edit_componet.MatrixUtil;
import com.snaps.mobile.utils.smart_snaps.SmartSnapsManager;
import com.snaps.mobile.utils.smart_snaps.SmartSnapsUtil;
import com.snaps.mobile.utils.smart_snaps.animations.SnapsMatrixAnimation;
import com.snaps.mobile.utils.smart_snaps.animations.strategies.smart_face_search.SnapsMatrixSearchFailedAnimation;
import com.snaps.mobile.utils.smart_snaps.animations.strategies.smart_face_search.SnapsMatrixShakeAnimation;

import java.io.File;

import static com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants.DEFAULT_MATRIX_ANIMATION_TIME;
import static com.snaps.common.utils.constant.Const_VALUES.SELECT_SNAPS;


/**
 * Created by ifunbae on 2017. 1. 18..
 */

public class SnapsImageViewTarget extends ImageViewTarget<Bitmap> implements ISmartSnapImgDataAnimationState, ISnapsImageViewTarget {
    private static final String TAG = SnapsImageViewTarget.class.getSimpleName();
    private Context context = null;
    private SnapsImageViewTargetParams snapsImageViewTargetParams = null;
    private SnapsMatrixAnimation snapsMatrixAnimation = null;
    private ImageSearchCompletedListener imageSearchCompletedListener = null;
    private boolean ismaskLoding = false;

    public SnapsImageViewTarget(Context context, ImageView view) {
        super(view);
        this.context = context;
    }

//    public SnapsImageViewTarget(Context context, ImageView view, @NonNull SnapsImageViewTargetParams snapsImageViewTargetParams) {
//        super(view);
//        this.context = context;
//        this.snapsImageViewTargetParams = snapsImageViewTargetParams;
//        /**
//         * @Marko
//         * 밑에 있는 생성자는 matrix animation 을 자동으로 타기 때문에 acrylic 제품 용 으로 추가함.
//         * (객체 생성만 해줬을 뿐인데 어디선가 이 플래그 가지고 매트릭스 애니메이션을 태우는거 같다.)
//         */
//    }

    public SnapsImageViewTarget(Context context, @NonNull SnapsImageViewTargetParams snapsImageViewTargetParams) {
        super(snapsImageViewTargetParams.getView());
        this.context = context;
        this.snapsImageViewTargetParams = snapsImageViewTargetParams;
        this.snapsMatrixAnimation = new SnapsMatrixAnimation();
    }

    public SnapsImageViewTarget(Context context, @NonNull SnapsImageViewTargetParams snapsImageViewTargetParams, ImageSearchCompletedListener imageSearchCompletedListener) {
        super(snapsImageViewTargetParams.getView());
        this.context = context;
        this.snapsImageViewTargetParams = snapsImageViewTargetParams;
        this.snapsMatrixAnimation = new SnapsMatrixAnimation();
        this.imageSearchCompletedListener = imageSearchCompletedListener;
    }

    @Override
    protected void setResource(Bitmap resource) {
        if (!BitmapUtil.isUseAbleBitmap(resource)) return;

        suspendAnimation();

        try {
            resource = preProcessingResource(resource);

            if (hasMask()) {
                setResourceWithBitmapAfterLoadMask(resource);
            } else {
                setResourceWithBitmap(resource, false);
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private void setResourceWithBitmapAfterLoadMask(final Bitmap resource) {
        try {
            hideImageView();

            ImageLoader.with(context).load(getMaskUrl()).override(getControlWidth(), getControlHeight()).into(new SnapsCustomTargets<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap maskBitmap, Transition transition) {
                    try {
                        setMaskBitmapToCustomImageView(maskBitmap);

                        setResourceWithBitmap(resource, true);
                    } catch (Exception e) {
                        Dlog.e(TAG, e);
                    }

                    if (!shouldApplyMatrixValue())
                        showImageView();
                }
            });
        } catch (Exception e) {
            Dlog.e(TAG, e);
            showImageView();
        }
    }

    private Bitmap resizeMaskBitmapIfDiffDimension(Bitmap maskBitmap) {
        if (view == null || getControlWidth() < 1 || getControlHeight() < 1 || !BitmapUtil.isUseAbleBitmap(maskBitmap))
            return maskBitmap;
        if (maskBitmap.getWidth() != getControlWidth() || maskBitmap.getHeight() != getControlHeight()) {
            try {
                return CropUtil.getInSampledScaleBitmap(maskBitmap, getControlWidth(), getControlHeight());
            } catch (OutOfMemoryError e) {
                Dlog.e(TAG, e);
            }
        }
        return maskBitmap;
    }

    private void setMaskBitmapToCustomImageView(Bitmap maskBitmap) {
        try {
            CustomImageView customImageView = (CustomImageView) view.getParent();
            if (customImageView != null) {

                maskBitmap = resizeMaskBitmapIfDiffDimension(maskBitmap);

                customImageView.setMaskBitmap(maskBitmap);
                customImageView.postInvalidate();
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private void setResourceWithBitmap(Bitmap resource, boolean mask) throws Exception {
        if (SmartSnapsManager.isSupportSmartSnapsProduct()) {
            if (isWaitForSmartSnapsAnimation()) {
                setImageBitmapOnImageView(resource);
                initializeSmartSnapsAnimation(resource);
            } else if (Config.useDrawSmartSnapsImageArea()) {
                setImageBitmapOnImageView(SmartSnapsUtil.drawSmartSnapsImageArea(resource, getImageData()));
            } else {
                if (!SmartSnapsManager.isSmartAreaSearching()) {
                    if (imageSearchCompletedListener != null) {
                        imageSearchCompletedListener.onfinished();
                    }
                }
                setImageBitmapOnImageView(resource);
            }
        } else {
            if (imageSearchCompletedListener != null) {
                imageSearchCompletedListener.onfinished();
            }
            setImageBitmapOnImageView(resource);
        }
        applyMatrixValueOnImageView(resource);
    }

    private void setImageBitmapOnImageView(Bitmap resource) throws Exception {
        view.setScaleType(getImageViewScaleTypeByLoadType());
        view.setImageBitmap(resource);
        if (!Const_PRODUCT.isDIYStickerProduct() && !Const_PRODUCT.isAcrylicKeyringProduct()) {
            if (snapsImageViewTargetParams != null) {
                SnapsPage snapsPage = snapsImageViewTargetParams.getSnapsPage();
                if (snapsPage != null) {
                    int imageCount = snapsPage.getImageCountOnPage();
                    if (imageCount <= 0) {
                        if (imageSearchCompletedListener != null) {
                            imageSearchCompletedListener.onfinished();
                        }
                    }
                }
            }
        }
    }

    private ImageView.ScaleType getImageViewScaleTypeByLoadType() {
        if (Const_PRODUCT.isNewKakaoBook() && snapsImageViewTargetParams.getLayoutControl() != null) {
            return getLoadType() == SELECT_SNAPS || snapsImageViewTargetParams.getLayoutControl().isImageFull ? ImageView.ScaleType.CENTER_INSIDE : ImageView.ScaleType.CENTER_CROP;
        } else if (Const_PRODUCT.isFreeSizeProduct()) {
            return ImageView.ScaleType.MATRIX;
        } else {
//            return getLoadType() == SELECT_SNAPS ? ImageView.ScaleType.CENTER_INSIDE : ImageView.ScaleType.CENTER_CROP;
            return getLoadType() == SELECT_SNAPS ? ImageView.ScaleType.FIT_XY: ImageView.ScaleType.CENTER_CROP; //Ben 클립아트 스케일 방법 변경(2019년에 스펙 변경되었다고...)
        }

    }

    private Bitmap preProcessingResource(Bitmap resource) throws Exception {
        //TODO  특이한 케이스로 아래 로직이 안 먹히는 고객이 있어서 반응을 보기 위해 전 코드로 롤백시켰다..만약, 반응이 잠잠하다면 다시 코드를 원복하자.
        //거의 희박한 가능성으로 flipped된 이미지가 존재하기 때문에, 부하를 줄이기 위해 포기 함.
//        resource = CropUtil.getFlippedBitmap(getImagePath(), resource);
        //rotateTransformation에서 처리 하도록 수정함.
        if (getRotate() != 0)
            resource = CropUtil.getRotateImage(resource, getRotate());

        return processEffectFilter(resource, getCropInfo());
    }

    private void initializeSmartSnapsAnimation(Bitmap imageBitmap) {
        if (snapsMatrixAnimation != null && !snapsMatrixAnimation.isInitialized()) {
            SnapsMatrixAnimation.SnapsMatrixAnimationAttribute matrixAnimationAttribute = new SnapsMatrixAnimation.SnapsMatrixAnimationAttribute.Builder()
                    .setContext(context)
                    .setIv(view)
                    .setImageBitmap(imageBitmap)
                    .setImageData(getImageData())
                    .setControlWidth(getControlWidth())
                    .setControlHeight(getControlHeight())
                    .setAnimationDuring(DEFAULT_MATRIX_ANIMATION_TIME)
                    .setRealPagerView(isRealPagerView())
                    .setImageSearchCompletedListener(imageSearchCompletedListener)
                    .create();

            snapsMatrixAnimation.initAnimation(matrixAnimationAttribute);
        }
    }

    private void applyMatrixValueOnImageView(final Bitmap resource) {
        if (!shouldApplyMatrixValue()) {
//            if (imageSearchCompletedListener != null)
//                imageSearchCompletedListener.onfinished();
            return;
        }

        hideImageView();

        view.setScaleType(ImageView.ScaleType.MATRIX);
        view.post(new Runnable() {
            @Override
            public void run() {
                if (isWaitForSmartSnapsAnimation()) {
                    initializeSmartSnapsAnimation(resource);
                    return;
                }

                try {
                    if (!shouldApplyMatrixValue()) {
                        view.setScaleType(getImageViewScaleTypeByLoadType());
                        showImageView();
                        return;
                    }

                    Matrix matrix = MatrixUtil.getMatrixAppliedEditInfo(resource, (AdjustableCropInfo) getCropInfo(), view, getControlWidth(), getControlHeight());
                    view.setImageMatrix(matrix);
                    view.postInvalidate();

                    showImageView();

                } catch (Exception e) {
                    Dlog.e(TAG, e);
                }
            }
        });
    }

    private void hideImageView() {
        if (!hasMask() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            view.setForeground(new ColorDrawable(Color.argb(255, 255, 255, 255)));
        } else {
            view.setVisibility(View.INVISIBLE);
        }
    }

    private void showImageView() {
        if (!hasMask() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            view.setForeground(null);
        } else {
            view.setVisibility(View.VISIBLE);
        }

//        if(imageSearchCompletedListener != null) {
//            imageSearchCompletedListener.onfinished();
//        }
    }

    private boolean shouldApplyMatrixValue() {
        return !(isWaitForSmartSnapsAnimation() || getCropInfo() == null || !(getCropInfo() instanceof AdjustableCropInfo) || getControlWidth() <= 0 || getControlHeight() <= 0);
    }

    @Override
    public void onRequestedAnimation() {
        try {
            if (Config.useDrawSmartSnapsImageArea()) {
                setImageBitmapOnImageView(SmartSnapsUtil.getAppliedDrawSmartSnapsSearchedArea(view, getImageData()));
            }

            startSmartSnapsAnimation();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    @Override
    public boolean isActiveAnimation() {
        return snapsMatrixAnimation != null && snapsMatrixAnimation.isActiveAnimation();
    }

    @Override
    public void setAnimationStateToStart() {
        if (snapsMatrixAnimation != null)
            snapsMatrixAnimation.setActiveAnimation(true);
    }

    private void startSmartSnapsAnimation() throws Exception {
        fixSmartSnapsAreaInfoForThumbnail();

        if (isFailedSmartFaceSearchWithImgData(getImageData())) {
            snapsMatrixAnimation.changeAnimationStrategy(Config.useDrawSmartSnapsImageArea() ? new SnapsMatrixShakeAnimation() : new SnapsMatrixSearchFailedAnimation(imageSearchCompletedListener));
        }

        snapsMatrixAnimation.startAnimation();
    }

    private void fixSmartSnapsAreaInfoForThumbnail() throws Exception {
        if (!SmartSnapsManager.isFirstSmartAreaSearching() || isRealPagerView() || getLayoutControl() == null)
            return;
        SmartSnapsUtil.fixLayoutControlCropAreaBySmartSnapsAreaInfo((Activity) context, getLayoutControl());
    }

    private boolean isFailedSmartFaceSearchWithImgData(MyPhotoSelectImageData imageData) {
        return imageData == null || !imageData.isFindSmartSnapsFaceArea();
    }

    @Override
    public void suspendAnimation() {
        if (!SmartSnapsManager.isSupportSmartSnapsProduct()) return;

        if (snapsMatrixAnimation != null) {
            snapsMatrixAnimation.suspendAnimation();
        }
    }

    private boolean isWaitForSmartSnapsAnimation() {
        if (!SmartSnapsManager.isSupportSmartSnapsProduct() || !SmartSnapsManager.isSmartAreaSearching() || getImageData() == null || getImageData().getSmartSnapsImgInfo() == null || getImageData().isEditedImage())
            return false;

        if (shouldThumbnailAnimation()) return true;

        try {
            SmartSnapsImgInfo smartSnapsImgInfo = getImageData().getSmartSnapsImgInfo();
            return smartSnapsImgInfo.getSmartSnapsImgState() == SmartSnapsConstants.eSmartSnapsImgState.READY
                    || smartSnapsImgInfo.getSmartSnapsImgState() == SmartSnapsConstants.eSmartSnapsImgState.RECEIVE_SMART_SNAPS_INFO;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean shouldThumbnailAnimation() {
        if (!SmartSnapsManager.isFirstSmartAreaSearching() || isRealPagerView() || getCropInfo() == null || !(getCropInfo() instanceof AdjustableCropInfo))
            return false;
        AdjustableCropInfo cInfo = (AdjustableCropInfo) getCropInfo();
        AdjustableCropInfo.CropImageRect imgRect = cInfo.getImgRect();
        return imgRect == null || imgRect.width < 1 || imgRect.height < 1;
    }

    private BaseCropInfo getCropInfo() {
        if (snapsImageViewTargetParams == null) return null;
        MyPhotoSelectImageData imageData = snapsImageViewTargetParams.getImageData();
        return imageData != null ? imageData.getCropInfo() : null;
    }

    @Override
    public MyPhotoSelectImageData getImageData() {
        return snapsImageViewTargetParams != null ? snapsImageViewTargetParams.getImageData() : null;
    }

    private SnapsLayoutControl getLayoutControl() {
        return snapsImageViewTargetParams != null ? snapsImageViewTargetParams.getLayoutControl() : null;
    }

    private boolean isRealPagerView() {
        return snapsImageViewTargetParams != null && snapsImageViewTargetParams.isRealPagerView();
    }

    private boolean hasMask() {
        if (snapsImageViewTargetParams == null) return false;
        SnapsLayoutControl layoutControl = snapsImageViewTargetParams.getLayoutControl();
        return layoutControl != null && !StringUtil.isEmpty(layoutControl.mask);
    }

    private String getMaskUrl() {
        if (snapsImageViewTargetParams == null || context == null || !hasMask()) return null;
        SnapsLayoutControl layoutControl = snapsImageViewTargetParams.getLayoutControl();
        return PhotobookCommonUtils.getMaskUrlWithMaskResName(context, layoutControl.mask);
    }

    private String getImagePath() {
        return snapsImageViewTargetParams != null ? snapsImageViewTargetParams.getUri() : null;
    }

    @Override
    public int getRotate() {
        return snapsImageViewTargetParams != null ? snapsImageViewTargetParams.getRotate() : 0;
    }

    private int getLoadType() {
        return snapsImageViewTargetParams != null ? snapsImageViewTargetParams.getLoadType() : -1;
    }

    private int getControlWidth() {
        return view != null && view.getWidth() > 0 ? view.getWidth() : (getLayoutControl() != null ? getLayoutControl().getIntWidth() : 0);
    }

    private int getControlHeight() {
        return view != null && view.getHeight() > 0 ? view.getHeight() : (getLayoutControl() != null ? getLayoutControl().getIntHeight() : 0);
    }

    //필터 적용 된 캐시가 없다면 생성 한다. 이거 포토북 로딩하는 시점에 템플릿 다 뒤져서 없으면 생성 해 놓는 방식으로 바꿔야 할 것 같다..
    private Bitmap processEffectFilter(Bitmap bitmap, BaseCropInfo cropInfo) throws Exception {
        if (cropInfo == null || cropInfo.getEffectFilerMaker() == null)
            return bitmap; //필터 파일이 없을 때만 EffectFilerMaker가 null이 아니다

        EffectFilerMaker effectFilerMaker = cropInfo.getEffectFilerMaker();
        MyPhotoSelectImageData imgData = effectFilerMaker.getImageData();

        if (imgData == null || imgData.ADJ_CROP_INFO == null || !imgData.ADJ_CROP_INFO.shouldCreateFilter() || bitmap == null || bitmap.isRecycled())
            return bitmap;

        /**
         * 이미지 필터를 적용 한 사진은 캐시를 가지고 있다.(매번 필터를 적용하면 버벅이니까.)
         * 그런데 만약 캐시 이미지가 지워졌다면 다시 생성하는 로직이다.
         */
        try {
            String effectPath = imgData.EFFECT_PATH;
            if (effectPath == null || effectPath.length() < 1)
                effectPath = ImageFilters.getExportFileName(ContextUtil.getContext(), imgData.F_IMG_NAME, imgData.F_IMG_SQNC, imgData.EFFECT_TYPE);
            File effectFile = new File(effectPath);
            if (effectFile.exists() && effectFile.length() > 0) {
                imgData.ADJ_CROP_INFO.setShouldCreateFilter(false);
                imgData.EFFECT_PATH = effectPath;
                imgData.EFFECT_THUMBNAIL_PATH = effectPath;
                imgData.setTriedRecoveryEffectFilterFile(true);

                refreshUIWithDelay(1000);
                return ImageLoader.loadImageSyncFromUri(context, effectPath, 0);
            }

            ImageEffectBitmap.EffectType effectType = ImageEffectBitmap.convertEffectStrToEnumType(imgData.EFFECT_TYPE);

            Bitmap bmEffect = ImageFilters.getEffectAppliedBitmap(ContextUtil.getContext(), effectType, bitmap);
            if (bmEffect != null && !bmEffect.isRecycled()) {
//				Logg.y("created filter file");

                int angle = imgData.ROTATE_ANGLE;
                Bitmap bmRotate = null;
                if (angle > 0) {
                    bmRotate = CropUtil.getRotateImage(bmEffect, angle);
                    if (bmRotate != null && bmEffect != bmRotate && !bmEffect.isRecycled()) {
                        bmEffect.recycle();
                        bmEffect = null;
                    }
                } else {
                    bmRotate = bmEffect;
                }

                String exportPath = ImageFilters.getAppliedEffectImgFilePath(ContextUtil.getContext(), bmRotate, effectPath, false);
                if (exportPath != null && exportPath.length() > 0) {
                    if (bmRotate != null && !bmRotate.isRecycled()) {
                        bmRotate.recycle();
                        bmRotate = null;
                    }

                    imgData.ADJ_CROP_INFO.setShouldCreateFilter(false);
                    imgData.EFFECT_PATH = exportPath;
                    imgData.EFFECT_THUMBNAIL_PATH = exportPath;
                    imgData.setTriedRecoveryEffectFilterFile(true);

                    Bitmap resultBitmap = ImageLoader.loadImageSyncFromUri(context, exportPath, 0);
                    refreshUIWithDelay(1000);
                    return resultBitmap;
                }
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        return bitmap;
    }

    private void refreshUIWithDelay(long delay) { //커버의 경우 효과 사진 캐시 생성 후 화면 갱신이 잘 안되는 증상이 확인 되어서 강제로 갱신처리를 한다...
        if (!isRealPagerView() || getLayoutControl() == null || getLayoutControl().getPageIndex() > 0)
            return;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    if (context != null && context instanceof SnapsEditActivity) {
                        SnapsEditActivity editActivity = (SnapsEditActivity) context;
                        SnapsProductEditorAPI editorAPI = editActivity.getProductEditorAPI();
                        editorAPI.refreshUI();
                    }
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                }
            }
        }, delay);
    }

    public interface ImageSearchCompletedListener {
        void onfinished();
    }
}
