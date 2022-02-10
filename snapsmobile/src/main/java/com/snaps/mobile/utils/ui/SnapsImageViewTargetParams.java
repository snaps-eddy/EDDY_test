package com.snaps.mobile.utils.ui;

import android.widget.ImageView;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.structure.control.SnapsLayoutControl;
import com.snaps.common.structure.page.SnapsPage;

/**
 * Created by ysjeong on 2018. 1. 18..
 */

public class SnapsImageViewTargetParams {
    private SnapsLayoutControl layoutControl;
    private ImageView view;
    private MyPhotoSelectImageData imageData;
    private int rotate;
    private String uri;
    private boolean isRealPagerView;
    private int loadType;
    private SnapsPage snapsPage;

    private SnapsImageViewTargetParams(Builder builder) {
        this.layoutControl = builder.layoutControl;
        this.view = builder.view;
        this.imageData = builder.imageData;
        this.rotate = builder.rotate;
        this.uri = builder.uri;
        this.isRealPagerView = builder.isRealPagerView;
        this.loadType = builder.loadType;
        this.snapsPage = builder.snapsPage;
    }

    public SnapsPage getSnapsPage() {
        return snapsPage;
    }

    public int getLoadType() {
        return loadType;
    }

    public SnapsLayoutControl getLayoutControl() {
        return layoutControl;
    }

    public ImageView getView() {
        return view;
    }

    public MyPhotoSelectImageData getImageData() {
        return imageData;
    }

    public int getRotate() {
        return rotate;
    }

    public String getUri() {
        return uri;
    }

    public boolean isRealPagerView() {
        return isRealPagerView;
    }

    public static class Builder {
        private SnapsPage snapsPage;
        private SnapsLayoutControl layoutControl;
        private ImageView view;
        private MyPhotoSelectImageData imageData;
        private int rotate;
        private String uri;
        private boolean isRealPagerView;
        private int loadType;

        public Builder setSnapsPage(SnapsPage snapsPage) {
            this.snapsPage = snapsPage;
            return this;
        }

        public Builder setLoadType(int loadType) {
            this.loadType = loadType;
            return this;
        }

        public Builder setLayoutControl(SnapsLayoutControl layoutControl) {
            this.layoutControl = layoutControl;
            return this;
        }

        public Builder setView(ImageView view) {
            this.view = view;
            return this;
        }

        public Builder setImageData(MyPhotoSelectImageData imageData) {
            this.imageData = imageData;
            return this;
        }

        public Builder setRotate(int rotate) {
            this.rotate = rotate;
            return this;
        }

        public Builder setUri(String uri) {
            this.uri = uri;
            return this;
        }

        public Builder setRealPagerView(boolean realPagerView) {
            isRealPagerView = realPagerView;
            return this;
        }

        public SnapsImageViewTargetParams create() {
            return new SnapsImageViewTargetParams(this);
        }
    }
}
