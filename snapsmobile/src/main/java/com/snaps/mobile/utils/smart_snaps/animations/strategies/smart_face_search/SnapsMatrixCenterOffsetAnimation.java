//package com.snaps.mobile.utils.smart_snaps.animations.strategies;
//
//import android.animation.ValueAnimator;
//import android.graphics.Matrix;
//import android.graphics.drawable.Drawable;
//import android.view.View;
//import android.view.animation.DecelerateInterpolator;
//import android.widget.ImageView;
//
//import com.snaps.common.utils.imageloader.recoders.AdjustableCropInfo;
//
///**
// * Created by ysjeong on 2018. 1. 19..
// */
//
//public class SnapsMatrixCenterOffsetAnimation extends SnapsMatrixAnimationStrategy {
//
//    @Override
//    public void initMatrixWithImageView(ImageView view) {
//        if (view == null) return;
//
//        Matrix imageViewMatrix = view.getImageMatrix();
//
//        Drawable viewDrawable = view.getDrawable();
//        if (viewDrawable == null) return;
//
//        float drawableWidth = viewDrawable.getIntrinsicWidth();
//        float drawableHeight = viewDrawable.getIntrinsicHeight();
//        float imageViewMeasuredWidth = view.getUserSelectWidth();
//        float imageViewMeasuredHeight = view.getHeight();
//
//        setImageViewMeasuredWidth(imageViewMeasuredWidth);
//        setImageViewMeasuredHeight(imageViewMeasuredHeight);
//
//        float scale;
//
//        if (imageViewMeasuredWidth <= 0 || imageViewMeasuredHeight <= 0 || drawableWidth <= 0 || drawableHeight <= 0) return;
//
//        float clipRectRatio = imageViewMeasuredWidth / imageViewMeasuredHeight;
//        float imageRatio = drawableWidth / drawableHeight;
//
//        if (imageRatio > clipRectRatio) {
//            scale = imageViewMeasuredHeight / drawableHeight;
//        } else {
//            scale = imageViewMeasuredWidth / drawableWidth;
//        }
//
//        imageViewMatrix.setScale(scale, scale);
//
//        setDefaultMatrix(new Matrix());
//
//        //center 맞추기
//        drawableHeight *= scale;
//        drawableWidth *= scale;
//
//        getDefaultMatrix().setScale(scale, scale);
//
//        float offsetX = (drawableWidth - imageViewMeasuredWidth) / 2;
//        float offsetY = (drawableHeight - imageViewMeasuredHeight) / 2;
//
//        getDefaultMatrix().postTranslate(-offsetX, -offsetY);
//
//        imageViewMatrix.postTranslate(-offsetX, -offsetY);
//
//        view.setImageMatrix(imageViewMatrix);
//        view.postInvalidate();
//
//        view.setVisibility(View.VISIBLE);
//    }
//
//    @Override
//    public void setDefaultMatrix() {}
//
//    @Override
//    public void performAnimation() {
//        if (isSuspend() || getCropInfo() == null || getImageViewMeasuredWidth() <= 0 || getImageViewMeasuredHeight() <= 0) return;
//
//        ValueAnimator viewParamsAnimator = ValueAnimator.ofFloat(0.f, 1f);
//        viewParamsAnimator.setDuration(getAnimationDuring());
//        viewParamsAnimator.setInterpolator(new DecelerateInterpolator());
//        viewParamsAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                try {
//                    float value = (float) animation.getAnimatedValue();
//
//                    //이동 계산
//                    AdjustableCropInfo.CropImageRect imgRect = getCropInfo().getImgRect();
//
//                    setImageViewMatrix(new Matrix());
//                    getImageViewMatrix().set(getDefaultMatrix());
//
//                    //이동 적용
//                    float moveX = 0;
//                    float moveY = 0;
//                    if (imgRect != null) {
//                        moveX = imgRect.movedX * value;
//                        moveY = imgRect.movedY * value;
//                    }
//
//                    getImageViewMatrix().postTranslate(moveX, moveY);
//                    getImageView().setImageMatrix(getImageViewMatrix());
//                    getImageView().postInvalidate();
//                } catch (Exception e) { Dlog.e(TAG, e); }
//            }
//        });
//
//        viewParamsAnimator.start();
//    }
//
//    @Override
//    public void suspendAnimation() {
//        setSuspend(true);
//    }
//}
