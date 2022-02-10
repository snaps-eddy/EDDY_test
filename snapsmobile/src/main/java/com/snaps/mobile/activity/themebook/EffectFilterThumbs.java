package com.snaps.mobile.activity.themebook;

import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by ysjeong on 2017. 9. 4..
 */
public class EffectFilterThumbs {
    private int idx;
    private ImageView imgView;
    private ImageView outline;
    private ProgressBar progress;
    private TextView name;

    private EffectFilterThumbs(Builder builder) {
        this.idx = builder.idx;
        this.imgView = builder.imgView;
        this.outline = builder.outline;
        this.progress = builder.progress;
        this.name = builder.name;
    }

    public int getIdx() {
        return idx;
    }

    public ImageView getImgView() {
        return imgView;
    }

    public ImageView getOutline() {
        return outline;
    }

    public ProgressBar getProgress() {
        return progress;
    }

    public TextView getName() {
        return name;
    }

    public static class Builder {
        private int idx;
        private ImageView imgView;
        private ImageView outline;
        private ProgressBar progress;
        private TextView name;

        public Builder setIdx(int idx) {
            this.idx = idx;
            return this;
        }

        public Builder setImgView(ImageView imgView) {
            this.imgView = imgView;
            return this;
        }

        public Builder setOutline(ImageView outline) {
            this.outline = outline;
            return this;
        }

        public Builder setProgress(ProgressBar progress) {
            this.progress = progress;
            return this;
        }

        public Builder setName(TextView name) {
            this.name = name;
            return this;
        }

        public EffectFilterThumbs create() {
            return new EffectFilterThumbs(this);
        }
    }
}