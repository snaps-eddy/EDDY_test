package com.snaps.mobile.activity.themebook.smart_analysis_product.page_edit.data;

import android.widget.ImageView;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.structure.control.SnapsLayoutControl;

public class SmartRecommendBookEditDragImageInfo {
    private MyPhotoSelectImageData imageData;
    private ImageView view;
    private boolean isSwapping = false;
    private SnapsLayoutControl longClickedLayoutControl = null;

    private SmartRecommendBookEditDragImageInfo(Builder builder) {
        this.imageData = builder.imageData;
        this.view = builder.view;
        this.isSwapping = builder.isSwapping;
        this.longClickedLayoutControl = builder.longClickedLayoutControl;
    }

    public SnapsLayoutControl getLongClickedLayoutControl() {
        return longClickedLayoutControl;
    }

    public MyPhotoSelectImageData getImageData() {
        return imageData;
    }

    public ImageView getView() {
        return view;
    }

    public boolean isSwapping() {
        return isSwapping;
    }

    public static class Builder {
        private MyPhotoSelectImageData imageData;
        private ImageView view;
        private boolean isSwapping = false;
        private SnapsLayoutControl longClickedLayoutControl = null;

        public Builder setImageData(MyPhotoSelectImageData imageData) {
            this.imageData = imageData;
            return this;
        }

        public Builder setView(ImageView view) {
            this.view = view;
            return this;
        }

        public Builder setSwapping() {
            isSwapping = true;
            return this;
        }

        public Builder setLongClickedLayoutControl(SnapsLayoutControl longClickedLayoutControl) {
            this.longClickedLayoutControl = longClickedLayoutControl;
            return this;
        }

        public SmartRecommendBookEditDragImageInfo create() {
            return new SmartRecommendBookEditDragImageInfo(this);
        }
    }
}
