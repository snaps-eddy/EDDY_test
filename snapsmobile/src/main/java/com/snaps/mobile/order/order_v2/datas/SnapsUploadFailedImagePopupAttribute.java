package com.snaps.mobile.order.order_v2.datas;

import android.app.Activity;

/**
 * Created by ysjeong on 2017. 4. 27..
 */

public class SnapsUploadFailedImagePopupAttribute {
    private Activity activity;
    private String projCode;
    private boolean isLandscapeMode;
    private boolean isPhotoPrint;

    private SnapsUploadFailedImagePopupAttribute(UploadFailedImagePopupBuilder builder) {
        activity = builder.activity;
        projCode = builder.projCode;
        isLandscapeMode = builder.isLandscapeMode;
        isPhotoPrint = builder.isPhotoPrint;
    }

    public boolean isPhotoPrint() {
        return isPhotoPrint;
    }

    public Activity getActivity() {
        return activity;
    }

    public String getProjCode() {
        return projCode;
    }

    public boolean isLandscapeMode() {
        return isLandscapeMode;
    }

    public static class UploadFailedImagePopupBuilder {
        private Activity activity;
        private String projCode;
        private boolean isLandscapeMode;
        private boolean isPhotoPrint;

        public UploadFailedImagePopupBuilder setPhotoPrint(boolean photoPrint) {
            isPhotoPrint = photoPrint;
            return this;
        }

        public UploadFailedImagePopupBuilder setActivity(Activity activity) {
            this.activity = activity;
            return this;
        }

        public UploadFailedImagePopupBuilder setProjCode(String projCode) {
            this.projCode = projCode;
            return this;
        }

        public UploadFailedImagePopupBuilder setLandscapeMode(boolean landscapeMode) {
            isLandscapeMode = landscapeMode;
            return this;
        }

        public SnapsUploadFailedImagePopupAttribute create() {
            return new SnapsUploadFailedImagePopupAttribute(this);
        }
    }
}
