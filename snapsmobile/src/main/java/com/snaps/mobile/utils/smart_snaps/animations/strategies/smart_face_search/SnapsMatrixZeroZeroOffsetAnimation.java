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
//public class SnapsMatrixZeroZeroOffsetAnimation extends SnapsMatrixAnimationStrategy {
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
////        drawableHeight *= scale;
////        drawableWidth *= scale;
//
//        imageViewMatrix.setScale(scale, scale);
//
//        view.setImageMatrix(imageViewMatrix);
//        view.postInvalidate();
//
//        view.setVisibility(View.VISIBLE);
//    }
//
//    @Override
//    public void setDefaultMatrix() {
//        if (getCropInfo() == null || getImageView() == null) {
//            return;
//        }
//
////        imageViewMatrix = imageView.getImageMatrix();
//
//        setDefaultMatrix(new Matrix());
//
//        Drawable viewDrawable = getImageView().getDrawable();
//        if (viewDrawable == null) return;
//
//        float drawableWidth = viewDrawable.getIntrinsicWidth();
//        float drawableHeight = viewDrawable.getIntrinsicHeight();
//        setImageViewMeasuredWidth(getImageView().getUserSelectWidth());
//        setImageViewMeasuredHeight(getImageView().getHeight());
//        float scale;
//
//        if (getImageViewMeasuredWidth() <= 0 || getImageViewMeasuredHeight() <= 0 || drawableWidth <= 0 || drawableHeight <= 0) return;
//
//        float clipRectRatio = getImageViewMeasuredWidth() / getImageViewMeasuredHeight();
//        float imageRatio = drawableWidth / drawableHeight;
//
//        if (imageRatio > clipRectRatio) {
//            scale = getImageViewMeasuredHeight() / drawableHeight;
//        } else {
//            scale = getImageViewMeasuredWidth() / drawableWidth;
//        }
//
//        drawableHeight *= scale;
//        drawableWidth *= scale;
//
//        getDefaultMatrix().setScale(scale, scale);
//
//        float offsetX = (drawableWidth - getImageViewMeasuredWidth()) / 2;
//        float offsetY = (drawableHeight - getImageViewMeasuredHeight()) / 2;
////        defaultMatrix.postTranslate(-offsetX, -offsetY);
//        setDefaultOffsetX(-offsetX);
//        setDefaultOffsetY(-offsetY);
//
////        imageView.setVisibility(View.VISIBLE);
//    }
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
////                    AdjustableCropInfo.CropImageRect clipRect = getCropInfo().getClipRect();
//                    AdjustableCropInfo.CropImageRect imgRect = getCropInfo().getImgRect();
//
////                    float clipWidthRatio = 1.f;
////                    float clipHeightRatio = 1.f;
////
////                    if (clipRect != null) {
////                        clipWidthRatio = clipRect.width / getImageViewMeasuredWidth();
////                        clipHeightRatio = clipRect.height / getImageViewMeasuredHeight();
////                    }
//
//                    setImageViewMatrix(new Matrix());
//                    getImageViewMatrix().set(getDefaultMatrix());
//
//                    //이동 적용
//                    float moveX = getDefaultOffsetX();
//                    float moveY = getDefaultOffsetY();
//                    if (imgRect != null) {
//                        moveX += imgRect.movedX;
//                        moveY += imgRect.movedY;
//                    }
//                    moveX *= value;
//                    moveY *= value;
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
