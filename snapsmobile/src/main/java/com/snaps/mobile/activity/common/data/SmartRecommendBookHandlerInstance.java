package com.snaps.mobile.activity.common.data;

import com.snaps.mobile.activity.common.interfacies.SnapsEditActExternalConnectionBridge;

public class SmartRecommendBookHandlerInstance {
    private SnapsEditActExternalConnectionBridge externalConnectionBridge = null;
    private SnapsProductEditInfo snapsProductEditInfo = null;

    private SmartRecommendBookHandlerInstance(Builder builder) {
        this.externalConnectionBridge = builder.externalConnectionBridge;
        this.snapsProductEditInfo = builder.snapsProductEditInfo;
    }

    public SnapsEditActExternalConnectionBridge getExternalConnectionBridge() {
        return externalConnectionBridge;
    }

    public SnapsProductEditInfo getSnapsProductEditInfo() {
        return snapsProductEditInfo;
    }

    public static class Builder {
        private SnapsEditActExternalConnectionBridge externalConnectionBridge = null;
        private SnapsProductEditInfo snapsProductEditInfo = null;

        public Builder setExternalConnectionBridge(SnapsEditActExternalConnectionBridge externalConnectionBridge) {
            this.externalConnectionBridge = externalConnectionBridge;
            return this;
        }

        public Builder setSnapsProductEditInfo(SnapsProductEditInfo snapsProductEditInfo) {
            this.snapsProductEditInfo = snapsProductEditInfo;
            return this;
        }

        public SmartRecommendBookHandlerInstance create() {
            return new SmartRecommendBookHandlerInstance(this);
        }
    }
}
