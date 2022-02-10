package com.snaps.mobile.activity.google_style_image_selector.ui.sticky;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by ysjeong on 16. 8. 3..
 */
public class ImageSelectStickyControls {

    private View trayView;
    private RelativeLayout lyTitleBar;
    private TextView tvTitleBarText;
    private ImageView ivTitleBackBtn;
    private TextView tvTitleBackBtn;
    private TextView tvNextBtn;

    private ImageSelectStickyControls(StickyControlBuilder builder) {
        trayView = builder.trayView;
        lyTitleBar = builder.lyTitleBar;
        tvTitleBarText = builder.tvTitleBarText;
        ivTitleBackBtn = builder.ivTitleBackBtn;
        tvTitleBackBtn = builder.tvTitleBackBtn;
        tvNextBtn = builder.tvNextBtn;
    }

    public View getTrayView() {
        return trayView;
    }

    public RelativeLayout getLyTitleBar() {
        return lyTitleBar;
    }

    public TextView getTvTitleBarText() {
        return tvTitleBarText;
    }

    public ImageView getIvTitleBackBtn() {
        return ivTitleBackBtn;
    }

    public TextView getTvTitleBackBtn() {
        return tvTitleBackBtn;
    }

    public TextView getTvNextBtn() {
        return tvNextBtn;
    }

    public static class StickyControlBuilder {
        private View trayView;
        private RelativeLayout lyTitleBar;
        private TextView tvTitleBarText;
        private ImageView ivTitleBackBtn;
        private TextView tvTitleBackBtn;
        private TextView tvNextBtn;

        public StickyControlBuilder setTrayView(View trayView) {
            this.trayView = trayView;
            return this;
        }

        public StickyControlBuilder setLyTitleBar(RelativeLayout lyTitleBar) {
            this.lyTitleBar = lyTitleBar;
            return this;
        }

        public StickyControlBuilder setTvTitleBarText(TextView tvTitleBarText) {
            this.tvTitleBarText = tvTitleBarText;
            return this;
        }

        public StickyControlBuilder setIvTitleBackBtn(ImageView ivTitleBackBtn) {
            this.ivTitleBackBtn = ivTitleBackBtn;
            return this;
        }

        public StickyControlBuilder setTvTitleBackBtn(TextView tvTitleBackBtn) {
            this.tvTitleBackBtn = tvTitleBackBtn;
            return this;
        }

        public StickyControlBuilder setTvNextBtn(TextView tvNextBtn) {
            this.tvNextBtn = tvNextBtn;
            return this;
        }

        public ImageSelectStickyControls createControls() {
            return new ImageSelectStickyControls(this);
        }
    }
}
