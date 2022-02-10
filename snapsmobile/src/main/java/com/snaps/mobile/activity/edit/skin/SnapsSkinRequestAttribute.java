package com.snaps.mobile.activity.edit.skin;

import android.content.Context;
import android.view.View;

/**
 * Created by ysjeong on 2017. 6. 29..
 */

public class SnapsSkinRequestAttribute {

    private Context context;
    private String resourceFileName;
    private View skinBackgroundView;
    private SnapsSkinUtil.SnapsSkinLoadListener skinLoadListener;
    private int requestWidth;
    private int requestHeight;

    private SnapsSkinRequestAttribute(Builder builder) {
        this.context = builder.context;
        this.resourceFileName = builder.resourceFileName;
        this.skinBackgroundView = builder.skinBackgroundView;
        this.skinLoadListener = builder.skinLoadListener;
        this.requestWidth = builder.requestWidth;
        this.requestHeight = builder.requestHeight;
    }

    public Context getContext() {
        return context;
    }

    public String getResourceFileName() {
        return resourceFileName;
    }

    public View getSkinBackgroundView() {
        return skinBackgroundView;
    }

    public SnapsSkinUtil.SnapsSkinLoadListener getSkinLoadListener() {
        return skinLoadListener;
    }

    public int getRequestWidth() {
        return requestWidth;
    }

    public int getRequestHeight() {
        return requestHeight;
    }

    public static class Builder {
        private Context context;
        private String resourceFileName;
        private View skinBackgroundView;
        private SnapsSkinUtil.SnapsSkinLoadListener skinLoadListener;
        private int requestWidth;
        private int requestHeight;

        public Builder setSkinLoadListener(SnapsSkinUtil.SnapsSkinLoadListener skinLoadListener) {
            this.skinLoadListener = skinLoadListener;
            return this;
        }

        public Builder setContext(Context context) {
            this.context = context;
            return this;
        }

        public Builder setResourceFileName(String resourceFileName) {
            this.resourceFileName = resourceFileName;
            return this;
        }

        public Builder setSkinBackgroundView(View skinBackgroundView) {
            this.skinBackgroundView = skinBackgroundView;
            return this;
        }

        public Builder setRequestWidth(int requestWidth) {
            this.requestWidth = requestWidth;
            return this;
        }

        public Builder setRequestHeight(int requestHeight) {
            this.requestHeight = requestHeight;
            return this;
        }

        public SnapsSkinRequestAttribute create() {
            return new SnapsSkinRequestAttribute(this);
        }
    }
}
