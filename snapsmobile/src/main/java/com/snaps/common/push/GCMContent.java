package com.snaps.common.push;

import androidx.annotation.Nullable;

public class GCMContent {

    private String msg;
    private String title;
    private String target;
    private String bigImgPath;
    private String fullTarget;
    private String imgOt;
    private String imageIncluded;
    private String receiveType;
    private String popMessage;
    private String prmchnlcode;

    private GCMContent(Builder builder) {
        this.msg = builder.msg;
        this.title = builder.title;
        this.target = builder.target;
        this.bigImgPath = builder.bigImgPath;
        this.fullTarget = builder.fullTarget;
        this.imgOt = builder.imgOt;
        this.imageIncluded = builder.imageIncluded;
        this.receiveType = builder.receiveType;
        this.popMessage = builder.popMessage;
        this.prmchnlcode = builder.prmchnlcode;
    }

    public String getMsg() {
        return msg;
    }

    public String getTitle() {
        return title;
    }

    public String getTarget() {
        return target;
    }

    public String getBigImgPath() {
        return bigImgPath;
    }

    public String getFullTarget() {
        return fullTarget;
    }

    public String getImageIncluded() {
        return imageIncluded;
    }

    public int getImgOt() {
        return Integer.valueOf(imgOt);
    }

    public String getReceiveType() {
        return receiveType;
    }

    public String getPopMessage() {
        return popMessage;
    }

    public String getPrmchnlcode() {
        return prmchnlcode;
    }

    public static class Builder {

        private String msg;
        private String title;
        private String target;
        private String bigImgPath;
        private String fullTarget;
        private String imgOt;
        private String imageIncluded;
        private String receiveType;
        private String popMessage;
        private String prmchnlcode;

        public Builder msg(String msg) {
            this.msg = msg;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder target(String target) {
            this.target = target;
            return this;
        }

        public Builder bigImgPath(String bigImgPath) {
            this.bigImgPath = bigImgPath;
            return this;
        }

        public Builder fullTarget(String fullTarget) {
            this.fullTarget = fullTarget;
            return this;
        }

        public Builder imgOt(@Nullable String _imgOt) {
            this.imgOt = _imgOt == null ? "0" : _imgOt;
            return this;
        }

        public Builder imageIncluded(String imageIncluded) {
            this.imageIncluded = imageIncluded;
            return this;
        }

        public Builder receiveType(String receiveType) {
            this.receiveType = receiveType;
            return this;
        }

        public Builder popMessage(String popMessage) {
            this.popMessage = popMessage;
            return this;
        }

        public Builder prmchnlcode(String prmchnlcode) {
            this.prmchnlcode = prmchnlcode;
            return this;
        }

        public GCMContent create() {
            return new GCMContent(this);
        }
    }
}
