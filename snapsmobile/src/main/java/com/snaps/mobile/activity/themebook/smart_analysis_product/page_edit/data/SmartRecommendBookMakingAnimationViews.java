package com.snaps.mobile.activity.themebook.smart_analysis_product.page_edit.data;

import android.widget.ImageView;
import android.widget.TextView;

public class SmartRecommendBookMakingAnimationViews {
    private ImageView centerViewA;
    private ImageView centerViewB;
    private ImageView memoriesImage;
    private TextView userNameView;
    private TextView descTextViewA;
    private TextView descTextViewB;

    private SmartRecommendBookMakingAnimationViews(Builder builder) {
        this.centerViewA = builder.centerViewA;
        this.centerViewB = builder.centerViewB;
        this.memoriesImage = builder.memoriesImage;
        this.userNameView = builder.userNameView;
        this.descTextViewA = builder.descTextViewA;
        this.descTextViewB = builder.descTextViewB;
    }

    public ImageView getCenterViewA() {
        return centerViewA;
    }

    public ImageView getCenterViewB() {
        return centerViewB;
    }

    public ImageView getMemoriesImage() {
        return memoriesImage;
    }

    public TextView getUserNameView() {
        return userNameView;
    }

    public TextView getDescTextViewA() {
        return descTextViewA;
    }

    public TextView getDescTextViewB() {
        return descTextViewB;
    }

    public static class Builder {
        private ImageView centerViewA;
        private ImageView centerViewB;
        private ImageView memoriesImage;
        private TextView userNameView;
        private TextView descTextViewA;
        private TextView descTextViewB;

        public Builder setCenterViewA(ImageView centerViewA) {
            this.centerViewA = centerViewA;
            return this;
        }

        public Builder setCenterViewB(ImageView centerViewB) {
            this.centerViewB = centerViewB;
            return this;
        }

        public Builder setMemoriesImage(ImageView memoriesImage) {
            this.memoriesImage = memoriesImage;
            return this;
        }

        public Builder setUserNameView(TextView userNameView) {
            this.userNameView = userNameView;
            return this;
        }

        public Builder setDescTextViewA(TextView descTextViewA) {
            this.descTextViewA = descTextViewA;
            return this;
        }

        public Builder setDescTextViewB(TextView descTextViewB) {
            this.descTextViewB = descTextViewB;
            return this;
        }

        public SmartRecommendBookMakingAnimationViews create() {
            return new SmartRecommendBookMakingAnimationViews(this);
        }
    }
}
