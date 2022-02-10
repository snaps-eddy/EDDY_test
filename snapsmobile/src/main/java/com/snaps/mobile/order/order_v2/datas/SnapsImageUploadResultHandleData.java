package com.snaps.mobile.order.order_v2.datas;

import android.app.Activity;

import com.snaps.common.structure.SnapsHandler;
import com.snaps.common.structure.SnapsTemplate;
import com.snaps.mobile.order.order_v2.interfacies.SnapsImageUploadListener;

public class SnapsImageUploadResultHandleData {
    private SnapsHandler snapsHandler;
    private Activity activity;
    private SnapsTemplate snapsTemplate;
    private SnapsImageUploadListener.eImageUploadState state;
    private SnapsImageUploadResultData uploadResultData;

    public SnapsImageUploadResultHandleData(Builder builder) {
        this.snapsHandler = builder.snapsHandler;
        this.activity = builder.activity;
        this.snapsTemplate = builder.snapsTemplate;
        this.state = builder.state;
        this.uploadResultData = builder.uploadResultData;
    }

    public SnapsHandler getSnapsHandler() {
        return snapsHandler;
    }

    public Activity getActivity() {
        return activity;
    }

    public SnapsTemplate getSnapsTemplate() {
        return snapsTemplate;
    }

    public SnapsImageUploadListener.eImageUploadState getState() {
        return state;
    }

    public SnapsImageUploadResultData getUploadResultData() {
        return uploadResultData;
    }

    public static class Builder {

        private SnapsHandler snapsHandler;
        private Activity activity;
        private SnapsTemplate snapsTemplate;
        private SnapsImageUploadListener.eImageUploadState state;
        private SnapsImageUploadResultData uploadResultData;

        public Builder setSnapsHandler(SnapsHandler snapsHandler) {
            this.snapsHandler = snapsHandler;
            return this;
        }

        public Builder setActivity(Activity activity) {
            this.activity = activity;
            return this;
        }

        public Builder setSnapsTemplate(SnapsTemplate snapsTemplate) {
            this.snapsTemplate = snapsTemplate;
            return this;
        }

        public Builder setState(SnapsImageUploadListener.eImageUploadState state) {
            this.state = state;
            return this;
        }

        public Builder setUploadResultData(SnapsImageUploadResultData uploadResultData) {
            this.uploadResultData = uploadResultData;
            return this;
        }

        public SnapsImageUploadResultHandleData create() {
            return new SnapsImageUploadResultHandleData(this);
        }
    }
}
