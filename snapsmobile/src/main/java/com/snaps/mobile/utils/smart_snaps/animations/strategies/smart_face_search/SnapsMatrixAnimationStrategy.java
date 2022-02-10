package com.snaps.mobile.utils.smart_snaps.animations.strategies.smart_face_search;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.spc.view.CustomImageView;
import com.snaps.common.utils.imageloader.recoders.AdjustableCropInfo;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.system.ViewUnbindHelper;
import com.snaps.mobile.utils.smart_snaps.animations.SnapsMatrixAnimation;
import com.snaps.mobile.utils.ui.SnapsImageViewTarget;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by ysjeong on 2018. 1. 19..
 */

public abstract class SnapsMatrixAnimationStrategy {
    private static final String TAG = SnapsMatrixAnimationStrategy.class.getSimpleName();

    private Context context;
    private ProgressBar imageLoadProgress = null;
    private CustomImageView imageViewParent = null;

    private ImageView imageView;
    private MyPhotoSelectImageData imageData;

    private Matrix imageViewMatrix;
    private Matrix defaultMatrix;

    private long animationDuring = 0l;
    private float defaultOffsetX, defaultOffsetY;
    private float imageViewMeasuredWidth, imageViewMeasuredHeight;

    private Bitmap imageBitmap = null;

    private boolean isSuspend = false;
    private boolean isRealPagerView = false;
    private AtomicBoolean isActiveAnimation = new AtomicBoolean(false);

    private int controlWidth, controlHeight;

    protected SnapsImageViewTarget.ImageSearchCompletedListener imageSearchCompletedListener;

    public abstract void initMatrixWithImageView(ImageView view);
    public abstract void setDefaultMatrix();
    public abstract void performAnimation();
    public abstract void suspendAnimation();

    public void init(SnapsMatrixAnimation.SnapsMatrixAnimationAttribute matrixAnimationAttribute) {
        this.context = matrixAnimationAttribute.getContext();
        this.imageView = matrixAnimationAttribute.getIv();
        this.imageBitmap = matrixAnimationAttribute.getImageBitmap();
        this.imageData = matrixAnimationAttribute.getImageData();
        this.animationDuring = matrixAnimationAttribute.getAnimationDuring();
        this.isRealPagerView = matrixAnimationAttribute.isRealPagerView();
        this.controlWidth = matrixAnimationAttribute.getControlWidth();
        this.controlHeight = matrixAnimationAttribute.getControlHeight();
        this.imageSearchCompletedListener = matrixAnimationAttribute.getImageSearchCompletedListener();


        initMatrixWithImageView(imageView);

        initHook();

        setSuspend(false);
    }

    protected void initHook() {}

    public void setImageData(MyPhotoSelectImageData imageData) {
        this.imageData = imageData;
    }

    public boolean isActiveAnimation() {
        return isActiveAnimation.get();
    }

    public void setActiveAnimation(boolean activeAnimation) {
        isActiveAnimation.set(activeAnimation);
    }

    public void changeStrategy(SnapsMatrixAnimationStrategy prevStrategy) {
        if (prevStrategy == null) return;
        this.context = prevStrategy.context;
        this.imageView = prevStrategy.imageView;
        this.imageData = prevStrategy.imageData;
        this.animationDuring = prevStrategy.animationDuring;
        this.imageViewParent = prevStrategy.imageViewParent;
        this.imageLoadProgress = prevStrategy.imageLoadProgress;
        this.imageViewMeasuredWidth = prevStrategy.imageViewMeasuredWidth;
        this.imageViewMeasuredHeight = prevStrategy.imageViewMeasuredHeight;
        this.imageViewMatrix = prevStrategy.imageViewMatrix;
        this.defaultMatrix = prevStrategy.defaultMatrix;
        this.isRealPagerView = prevStrategy.isRealPagerView;
        this.imageBitmap = prevStrategy.imageBitmap;
    }

    void removeImageLoadProgress() {
        try {
            if (getImageViewParent() != null && getImageLoadProgress() != null) {
                getImageLoadProgress().setVisibility(View.GONE);
                getImageViewParent().removeView(getImageLoadProgress());

                try {
                    ViewUnbindHelper.unbindReferences(getImageLoadProgress(), null, false);
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                }

                setImageLoadProgress(null);
                setImageViewParent(null);
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    public long getAnimationDuring() {
        return animationDuring;
    }

    public void setAnimationDuring(long animationDuring) {
        this.animationDuring = animationDuring;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public AdjustableCropInfo getCropInfo() {
        return imageData != null && imageData.getCropInfo() instanceof AdjustableCropInfo ? (AdjustableCropInfo) imageData.getCropInfo() : null;
    }

    protected void handleSmartSnapsSearchFailed() {
        if (getImageView() != null) {
            getImageView().setScaleType(ImageView.ScaleType.CENTER_CROP);
            getImageView().postInvalidate();
        }
        removeImageLoadProgress();
        setActiveAnimation(false);
    }

    public float getImageViewMeasuredWidth() {
        return imageViewMeasuredWidth;
    }

    public void setImageViewMeasuredWidth(float imageViewMeasuredWidth) {
        this.imageViewMeasuredWidth = imageViewMeasuredWidth;
    }

    public float getImageViewMeasuredHeight() {
        return imageViewMeasuredHeight;
    }

    public void setImageViewMeasuredHeight(float imageViewMeasuredHeight) {
        this.imageViewMeasuredHeight = imageViewMeasuredHeight;
    }

    public Matrix getImageViewMatrix() {
        return imageViewMatrix;
    }

    public void setImageViewMatrix(Matrix imageViewMatrix) {
        this.imageViewMatrix = imageViewMatrix;
    }

    public Matrix getDefaultMatrix() {
        return defaultMatrix;
    }

    public void setDefaultMatrix(Matrix defaultMatrix) {
        this.defaultMatrix = defaultMatrix;
    }

    public float getDefaultOffsetX() {
        return defaultOffsetX;
    }

    public void setDefaultOffsetX(float defaultOffsetX) {
        this.defaultOffsetX = defaultOffsetX;
    }

    public float getDefaultOffsetY() {
        return defaultOffsetY;
    }

    public void setDefaultOffsetY(float defaultOffsetY) {
        this.defaultOffsetY = defaultOffsetY;
    }

    public boolean isSuspend() {
        return isSuspend;
    }

    public void setSuspend(boolean suspend) {
        isSuspend = suspend;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public ProgressBar getImageLoadProgress() {
        return imageLoadProgress;
    }

    public void setImageLoadProgress(ProgressBar imageLoadProgress) {
        this.imageLoadProgress = imageLoadProgress;
    }

    public CustomImageView getImageViewParent() {
        return imageViewParent;
    }

    public void setImageViewParent(CustomImageView parent) {
        this.imageViewParent = parent;
    }

    public boolean isRealPagerView() {
        return isRealPagerView;
    }

    public void setRealPagerView(boolean realPagerView) {
        isRealPagerView = realPagerView;
    }

    public Bitmap getImageBitmap() {
        return imageBitmap;
    }

    public int getControlWidth() {
        return controlWidth;
    }

    public int getControlHeight() {
        return controlHeight;
    }

    public void setImageSearchCompletedListener(SnapsImageViewTarget.ImageSearchCompletedListener imageSearchCompletedListener) {
        this.imageSearchCompletedListener = imageSearchCompletedListener;
    }
}
