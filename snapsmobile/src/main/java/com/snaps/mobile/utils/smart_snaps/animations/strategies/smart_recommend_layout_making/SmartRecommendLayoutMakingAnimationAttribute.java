package com.snaps.mobile.utils.smart_snaps.animations.strategies.smart_recommend_layout_making;

import android.app.Activity;
import android.widget.ImageView;

public class SmartRecommendLayoutMakingAnimationAttribute {
    private Activity activity;
    private long animationTime;
    private ImageView imageView;
    private SmartRecommendLayoutMakingAnimationFactory.eSmartRecommendLayoutMakingAnimationType prevAnimation;

    private SmartRecommendLayoutMakingAnimationAttribute(Builder builder) {
        this.activity = builder.activity;
        this.animationTime = builder.animationTime;
        this.imageView = builder.imageView;
        this.prevAnimation = builder.prevAnimation;
    }

    public Activity getActivity() {
        return activity;
    }

    public long getAnimationTime() {
        return animationTime;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public SmartRecommendLayoutMakingAnimationFactory.eSmartRecommendLayoutMakingAnimationType getPrevAnimation() {
        return prevAnimation;
    }

    public static class Builder {
        private Activity activity;
        private long animationTime;
        private ImageView imageView;
        private SmartRecommendLayoutMakingAnimationFactory.eSmartRecommendLayoutMakingAnimationType prevAnimation;

        public Builder(Activity act) {
            this.activity = act;
        }

        public Builder setPrevAnimation(SmartRecommendLayoutMakingAnimationFactory.eSmartRecommendLayoutMakingAnimationType prevAnimation) {
            this.prevAnimation = prevAnimation;
            return this;
        }

        public Builder setAnimationTime(long animationTime) {
            this.animationTime = animationTime;
            return this;
        }

        public Builder setImageView(ImageView imageView) {
            this.imageView = imageView;
            return this;
        }

        public SmartRecommendLayoutMakingAnimationAttribute create() {
            return new SmartRecommendLayoutMakingAnimationAttribute(this);
        }
    }
}
