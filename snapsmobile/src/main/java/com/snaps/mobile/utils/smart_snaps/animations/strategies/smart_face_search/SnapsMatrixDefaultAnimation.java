package com.snaps.mobile.utils.smart_snaps.animations.strategies.smart_face_search;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.Matrix;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.snaps.common.spc.view.CustomImageView;
import com.snaps.common.structure.SnapsHandler;
import com.snaps.common.utils.ISnapsHandler;
import com.snaps.common.utils.imageloader.recoders.AdjustableCropInfo;
import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.utils.smart_snaps.SmartSnapsManager;

/**
 * Created by ysjeong on 2018. 1. 19..
 */

public class SnapsMatrixDefaultAnimation extends SnapsMatrixAnimationStrategy implements ISnapsHandler {
    private static final String TAG = SnapsMatrixDefaultAnimation.class.getSimpleName();

    private static long MAX_PROGRESS_SHOWING_TIME = 10000;

    private SnapsHandler snapsHandler = null;

    @Override
    protected void initHook() {
        super.initHook();
        snapsHandler = new SnapsHandler(this);
    }

    @Override
    public void initMatrixWithImageView(ImageView view) {
        if (view == null) return;

        if (getImageBitmap() == null) return;

        float drawableWidth = getImageBitmap().getWidth();
        float drawableHeight = getImageBitmap().getHeight();
        float imageViewMeasuredWidth = view.getWidth();
        float imageViewMeasuredHeight = view.getHeight();

        if (imageViewMeasuredWidth <= 0)
            imageViewMeasuredWidth = getControlWidth();

        if (imageViewMeasuredHeight <= 0)
            imageViewMeasuredHeight = getControlHeight();

        setImageViewMeasuredWidth(imageViewMeasuredWidth);
        setImageViewMeasuredHeight(imageViewMeasuredHeight);

        float scale;

        if (imageViewMeasuredWidth <= 0 || imageViewMeasuredHeight <= 0 || drawableWidth <= 0 || drawableHeight <= 0) return;

        float clipRectRatio = imageViewMeasuredWidth / imageViewMeasuredHeight;
        float imageRatio = drawableWidth / drawableHeight;

        if (imageRatio > clipRectRatio) {
            scale = imageViewMeasuredHeight / drawableHeight;
        } else {
            scale = imageViewMeasuredWidth / drawableWidth;
        }

        setDefaultMatrix(new Matrix());

        //center 맞추기
        drawableHeight *= scale;
        drawableWidth *= scale;

        getDefaultMatrix().setScale(scale, scale);

        float offsetX = (drawableWidth - imageViewMeasuredWidth) / 2;
        float offsetY = (drawableHeight - imageViewMeasuredHeight) / 2;

        getDefaultMatrix().postTranslate(-offsetX, -offsetY);

        createImageLoadProgressWithImageView(view);
    }

    private void createImageLoadProgressWithImageView(ImageView view) {
        if (!isRealPagerView() || !SmartSnapsManager.isFirstSmartAreaSearching()) return;

        if (view.getParent() != null && view.getParent() instanceof CustomImageView) {
            setImageViewParent((CustomImageView) view.getParent());

            CustomImageView customImageView = getImageViewParent();
            ProgressBar imageLoadProgress = customImageView.createImageLoadProgressWithImageView(getImageViewMeasuredWidth(), getImageViewMeasuredHeight());
            setImageLoadProgress(imageLoadProgress);
            checkProgressDismiss(imageLoadProgress);
        }
    }

    //가끔 프로그래스가 사라지지 않는 경우가 확인 되어서 꽁수로...
    private void checkProgressDismiss(final ProgressBar imageLoadProgress) {
        if (imageLoadProgress == null || !isRealPagerView()) return;

        if (SmartSnapsManager.isFirstSmartAreaSearching()) {
            SmartSnapsManager smartSnapsManager = SmartSnapsManager.getInstance();
            smartSnapsManager.putSmartSnapsSearchingProgress(getImageViewParent(), imageLoadProgress);
        } else {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (getImageViewParent() != null) {
                        try {
                            getImageViewParent().removeView(imageLoadProgress);
                        } catch (Exception e) { Dlog.e(TAG, e); }
                    }
                }
            }, MAX_PROGRESS_SHOWING_TIME);
        }
    }

    @Override
    public void setDefaultMatrix() {
        if (getImageView() != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getImageView().setForeground(null);
            }
        }
    }

    @Override
    public void performAnimation() {
        if (isSuspend() || getCropInfo() == null) {
            handleSmartSnapsSearchFailed();
            return;
        }

        if (getImageViewMeasuredWidth() <= 0 || getImageViewMeasuredHeight() <= 0) {
            initMatrixWithImageView(getImageView());
        }

        removeImageLoadProgress();

        getImageView().setScaleType(ImageView.ScaleType.MATRIX);
        getImageView().setImageMatrix(getDefaultMatrix());

        if (isRealPagerView()) {
            startAnimation();
        } else {
            if (snapsHandler != null) snapsHandler.sendEmptyMessageDelayed(HANDLE_MSG_APPLY_MATRIX_WITHOUT_ANIM, getAnimationDuring());
        }
    }

    private void startAnimation() {
        ValueAnimator viewParamsAnimator = ValueAnimator.ofFloat(0.f, 1f);
        viewParamsAnimator.setDuration(getAnimationDuring());
        viewParamsAnimator.setInterpolator(new AccelerateInterpolator());
        viewParamsAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                try {
                    float value = (float) animation.getAnimatedValue();

                    //이동 계산
                    AdjustableCropInfo.CropImageRect imgRect = getCropInfo().getImgRect();

                    setImageViewMatrix(new Matrix());
                    getImageViewMatrix().set(getDefaultMatrix());

                    //이동 적용
                    float moveX = 0;
                    float moveY = 0;
                    if (imgRect != null) {
                        moveX = imgRect.movedX * value;
                        moveY = imgRect.movedY * value;
                        getImageViewMatrix().postTranslate(moveX, moveY);

                        getImageView().postInvalidate();
                        getImageView().setImageMatrix(getImageViewMatrix());
                    }
                } catch (Exception e) { Dlog.e(TAG, e); }
            }
        });

        viewParamsAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {
                setActiveAnimation(false);
                removeImageLoadProgress();
                if (imageSearchCompletedListener != null)
                    imageSearchCompletedListener.onfinished();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                setActiveAnimation(false);
                if (imageSearchCompletedListener != null)
                    imageSearchCompletedListener.onfinished();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });

        viewParamsAnimator.start();
    }

    private void applyMatrixWithoutAnimation() throws Exception {
        //이동 계산
        AdjustableCropInfo.CropImageRect imgRect = getCropInfo().getImgRect();

        setImageViewMatrix(new Matrix());
        getImageViewMatrix().set(getDefaultMatrix());

        //이동 적용
        float moveX = 0;
        float moveY = 0;
        if (imgRect != null) {
            moveX = imgRect.movedX;
            moveY = imgRect.movedY;
            getImageViewMatrix().postTranslate(moveX, moveY);
            getImageView().setImageMatrix(getImageViewMatrix());
            getImageView().postInvalidate();
        }
    }

    private static final int HANDLE_MSG_APPLY_MATRIX_WITHOUT_ANIM = 0;

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case HANDLE_MSG_APPLY_MATRIX_WITHOUT_ANIM :
                try {
                    applyMatrixWithoutAnimation();
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                }
                break;
        }
    }

    @Override
    public void suspendAnimation() {
        setSuspend(true);
        setActiveAnimation(false);
        removeImageLoadProgress();
    }
}
