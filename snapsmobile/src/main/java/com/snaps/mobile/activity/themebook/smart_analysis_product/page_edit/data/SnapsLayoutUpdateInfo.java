package com.snaps.mobile.activity.themebook.smart_analysis_product.page_edit.data;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.structure.control.SnapsLayoutControl;

public class SnapsLayoutUpdateInfo {
    private SnapsTemplate snapsTemplate;
    private MyPhotoSelectImageData newImageData;
    private SnapsLayoutControl layoutControl;
    private boolean shouldSmartSnapsFitAnimation;

    private SnapsLayoutUpdateInfo(Builder builder) {
        this.snapsTemplate = builder.snapsTemplate;
        this.newImageData = builder.newImageData;
        this.layoutControl = builder.layoutControl;
        this.shouldSmartSnapsFitAnimation = builder.shouldSmartSnapsFitAnimation;
    }

    public SnapsTemplate getSnapsTemplate() {
        return snapsTemplate;
    }

    public MyPhotoSelectImageData getNewImageData() {
        return newImageData;
    }

    public SnapsLayoutControl getLayoutControl() {
        return layoutControl;
    }

    public boolean shouldSmartSnapsFitAnimation() {
        return shouldSmartSnapsFitAnimation;
    }

    public static class Builder {
        private SnapsTemplate snapsTemplate;
        private MyPhotoSelectImageData newImageData;
        private SnapsLayoutControl layoutControl;
        private boolean shouldSmartSnapsFitAnimation;

        public Builder setShouldSmartSnapsFitAnimation(boolean shouldSmartSnapsFitAnimation) {
            this.shouldSmartSnapsFitAnimation = shouldSmartSnapsFitAnimation;
            return this;
        }

        public Builder setSnapsTemplate(SnapsTemplate snapsTemplate) {
            this.snapsTemplate = snapsTemplate;
            return this;
        }

        public Builder setNewImageData(MyPhotoSelectImageData newImageData) {
            this.newImageData = newImageData;
            return this;
        }

        public Builder setLayoutControl(SnapsLayoutControl layoutControl) {
            this.layoutControl = layoutControl;
            return this;
        }

        public SnapsLayoutUpdateInfo create() {
            return new SnapsLayoutUpdateInfo(this);
        }
    }
}
