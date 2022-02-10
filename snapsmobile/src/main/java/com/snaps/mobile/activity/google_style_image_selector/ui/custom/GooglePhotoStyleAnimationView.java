package com.snaps.mobile.activity.google_style_image_selector.ui.custom;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectAnimationFinishListener;

/**
 * Created by ysjeong on 2017. 1. 11..
 */

public class GooglePhotoStyleAnimationView {

    public static final long ANIM_TIME = 400;

    private Context context = null;

    private FrameLayout containerLayout = null;

    private View currentHolderView = null;
    private View targetHolderView = null;

    private ImageView currentImageView = null;
    private ImageView targetImageView = null;

    private Rect currentOffset = null;
    private Rect targetOffset = null;

    private float pivotY = 0.f;

    private boolean isBothSections = false;
    private boolean isCurrentViewSection = false;
    private boolean isTargetViewSection = false;

    private int row = 0;

    private boolean isDummyStartView = false;
    private boolean isDummyTargetView = false;
    private boolean isSuspended = false;
    private boolean isSelected = false;

    private GooglePhotoStyleAnimationView(Builder builder) {
        if (builder == null) return;

        this.context = builder.context;
        this.containerLayout = builder.containerLayout;
        this.currentHolderView = builder.currentHolderView;
        this.targetHolderView = builder.targetHolderView;
        this.currentOffset = builder.currentOffset;
        this.targetOffset = builder.targetOffset;
        this.pivotY = builder.pivotY;
        this.isBothSections = builder.isTwoSections;
        this.isCurrentViewSection = builder.isCurrentViewSection;
        this.isTargetViewSection = builder.isTargetViewSection;
        this.currentImageView = builder.currentImageView;
        this.targetImageView = builder.targetImageView;
        this.row = builder.row;
        this.isDummyTargetView = builder.isDummyTargetView;
        this.isDummyStartView = builder.isDummyStartView;
        this.isSelected = builder.isSelected;

        containerLayout = new FrameLayout(this.context);
        FrameLayout.LayoutParams containerParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        containerLayout.setLayoutParams(containerParams);

        if (currentHolderView != null)
            containerLayout.addView(currentHolderView);
        if (targetHolderView != null)
            containerLayout.addView(targetHolderView);
    }

    public void setSuspended(boolean suspended) {
        isSuspended = suspended;
    }

    public void startAnimation(final IImageSelectAnimationFinishListener listener, final boolean isYearUIDepth) {
        if (containerLayout == null) return;

        final Rect startRect = getCurrentOffset();
        final Rect targetRect = getTargetOffset();

        long animationTime = ANIM_TIME;

        if (isYearUIDepth)
            animationTime *= 1.3f;

        ValueAnimator viewParamsAnimator = ValueAnimator.ofFloat(0.f, 1f);
        viewParamsAnimator.setDuration(animationTime);
        viewParamsAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (isSuspended) return;

                float value = (float) animation.getAnimatedValue();
                if (listener != null)
                    listener.onProgressAnimation(value);

                float moveX = (targetRect.left - startRect.left) * value;
                float moveY = (targetRect.top - startRect.top) * value;

                float addWidth = (targetRect.width() - startRect.width()) * value;
                float addHeight = (targetRect.height() - startRect.height()) * value;

                int scaledWidth = (int) (startRect.width() + addWidth);
                int scaledHeight = (int) (startRect.height() + addHeight);

                if (containerLayout != null) {

                    containerLayout.setPivotY(pivotY);

                    containerLayout.setX(startRect.left + moveX);
                    containerLayout.setY(startRect.top + moveY);

                    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) containerLayout.getLayoutParams();
                    layoutParams.width = scaledWidth;
                    layoutParams.height = scaledHeight;
                    containerLayout.setLayoutParams(layoutParams);
                }

                if (isBothSections()) { //둘 다 섹션(섹션끼리 변하는 형태)
                    currentHolderView.setAlpha(.5f - (value / 2));
                    targetHolderView.setAlpha(value);
                } else if (isCurrentViewSection()) { //현재는 섹션인데 다음 UI가 사진인 형태
                    currentHolderView.setAlpha(.5f - (value / 2));
                } else if (isTargetViewSection()) {
                    targetHolderView.setAlpha(value);
                } else { //사진 끼리
                    if (currentHolderView != null) {
                        View viewCurrentImageView = isSelected && !isYearUIDepth ? currentHolderView : currentImageView;
                        if (isDummyStartView()) {
                            viewCurrentImageView.setAlpha(value);
                        } else {
                            viewCurrentImageView.setAlpha(1f - value);
                        }

                        RelativeLayout.LayoutParams currentImageViewLayoutParams = (RelativeLayout.LayoutParams) currentImageView.getLayoutParams();
                        currentImageViewLayoutParams.width = scaledWidth;
                        currentImageViewLayoutParams.height = scaledHeight;
                        currentImageView.setLayoutParams(currentImageViewLayoutParams);
                    }

                    if (targetHolderView != null) {
                        View viewTargetImageView = isSelected && !isYearUIDepth ? targetHolderView : targetImageView;
                        if (isDummyTargetView()) {
                            viewTargetImageView.setAlpha(1f - value);
                        } else {
                            viewTargetImageView.setAlpha(value);
                        }

                        RelativeLayout.LayoutParams targetImageViewLayoutParams = (RelativeLayout.LayoutParams) targetImageView.getLayoutParams();
                        targetImageViewLayoutParams.width = scaledWidth;
                        targetImageViewLayoutParams.height = scaledHeight;
                        targetImageView.setLayoutParams(targetImageViewLayoutParams);
                    }
                }
            }
        });

        viewParamsAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {
                if (isSuspended) return;

                if (listener != null)
                    listener.onFinishAnimation();
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });

        viewParamsAnimator.start();
    }

    public View getCurrentHolderView() {
        return currentHolderView;
    }

    public View getTargetHolderView() {
        return targetHolderView;
    }

    public boolean isDummyStartView() {
        return isDummyStartView;
    }

    public boolean isDummyTargetView() {
        return isDummyTargetView;
    }

    public View getCurrentImageView() {
        return currentImageView;
    }

    public View getTargetImageView() {
        return targetImageView;
    }

    public boolean isCurrentViewSection() {
        return isCurrentViewSection;
    }

    public boolean isTargetViewSection() {
        return isTargetViewSection;
    }

    public boolean isBothSections() {
        return isBothSections;
    }

    public float getPivotY() {
        return pivotY;
    }

    public FrameLayout getContainerLayout() {
        return containerLayout;
    }

    public Rect getCurrentOffset() {
        return currentOffset;
    }

    public Rect getTargetOffset() {
        return targetOffset;
    }

    public int getRow() {
        return row;
    }

    public static class Builder {
        private Context context = null;
        private FrameLayout containerLayout = null;

        private View currentHolderView = null;
        private View targetHolderView = null;

        private ImageView currentImageView = null;
        private ImageView targetImageView = null;

        private String currentImagePath = null;
        private String targetImagePath = null;

        int currentImageSize = 0;
        int targetImageSize = 0;

        private Rect currentOffset = null;
        private Rect targetOffset = null;

        private float pivotY = 0.f;

        private boolean isTwoSections = false;
        private boolean isCurrentViewSection = false;
        private boolean isTargetViewSection = false;

        private int row = 0;

        private boolean isDummyTargetView = false;
        private boolean isDummyStartView = false;

        private boolean isSelected = false;

        public Builder(Context context) {
            if (context == null) return;
            this.context = context;
        }

        public Builder setSelected(boolean selected) {
            isSelected = selected;
            return this;
        }

        public Builder setDummyStartView(boolean dummyStartView) {
            isDummyStartView = dummyStartView;
            return this;
        }

        public Builder setDummyTargetView(boolean dummyTargetView) {
            isDummyTargetView = dummyTargetView;
            return this;
        }

        public Builder setCurrentImageSize(int currentImageSize) {
            this.currentImageSize = currentImageSize;
            return this;
        }

        public Builder setTargetImageSize(int targetImageSize) {
            this.targetImageSize = targetImageSize;
            return this;
        }

        public Builder setRow(int row) {
            this.row = row;
            return this;
        }

        public Builder setCurrentImageView(ImageView currentImageView) {
            this.currentImageView = currentImageView;
            return this;
        }

        public Builder setTargetImageView(ImageView targetImageView) {
            this.targetImageView = targetImageView;
            return this;
        }

        public Builder setTargetViewSection(boolean targetViewSection) {
            isTargetViewSection = targetViewSection;
            return this;
        }

        public Builder setCurrentViewSection(boolean currentViewSection) {
            isCurrentViewSection = currentViewSection;
            return this;
        }

        public Builder setTwoSections(boolean twoSections) {
            isTwoSections = twoSections;
            return this;
        }

        public Builder setCurrentHolderView(View currentImageView) {
            this.currentHolderView = currentImageView;
            return this;
        }

        public Builder setTargetHolderView(View targetImageView) {
            this.targetHolderView = targetImageView;
            return this;
        }

        public Builder setCurrentImagePath(String currentImagePath) {
            this.currentImagePath = currentImagePath;
            return this;
        }

        public Builder setTargetImagePath(String targetImagePath) {
            this.targetImagePath = targetImagePath;
            return this;
        }

        public Builder setCurrentOffset(Rect currentOffset) {
            this.currentOffset = currentOffset;
            return this;
        }

        public Builder setTargetOffset(Rect targetOffset) {
            this.targetOffset = targetOffset;
            return this;
        }

        public Builder setPivotY(float pivotY) {
            this.pivotY = pivotY;
            return this;
        }

        public GooglePhotoStyleAnimationView create() {
            return new GooglePhotoStyleAnimationView(this);
        }
    }
}
