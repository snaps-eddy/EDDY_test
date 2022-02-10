package com.snaps.mobile.activity.themebook.smart_analysis_product.page_edit.data;

import android.widget.TextView;

public class SmartRecommendBookCoverInfo {
    private TextView pageCountView;
    private TextView photoCountView;

    private SmartRecommendBookCoverInfo(Builder builder) {
        this.pageCountView = builder.pageCountView;
        this.photoCountView = builder.photoCountView;
    }

    public TextView getPageCountView() {
        return pageCountView;
    }

    public TextView getPhotoCountView() {
        return photoCountView;
    }

    public static class Builder {
        private TextView pageCountView;
        private TextView photoCountView;

        public Builder setPageCountView(TextView pageCountView) {
            this.pageCountView = pageCountView;
            return this;
        }

        public Builder setPhotoCountView(TextView photoCountView) {
            this.photoCountView = photoCountView;
            return this;
        }

        public SmartRecommendBookCoverInfo create() {
            return new SmartRecommendBookCoverInfo(this);
        }
    }
}
