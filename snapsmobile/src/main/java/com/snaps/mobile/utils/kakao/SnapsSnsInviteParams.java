package com.snaps.mobile.utils.kakao;

public class SnapsSnsInviteParams {
    private String text = null;
    private String openurl = null;
    private String urlText = null;
    private String imgUrl = null;
    private String imgWidth = null;
    private String imgHeight = null;
    private String isRunapp = null;
    private String eventcode = null;
    private String excuteParam = null;

    private SnapsSnsInviteParams(Builder builder) {
        this.text = builder.text;
        this.openurl = builder.openurl;
        this.urlText = builder.urlText;
        this.imgUrl = builder.imgUrl;
        this.imgWidth = builder.imgWidth;
        this.imgHeight = builder.imgHeight;
        this.isRunapp = builder.isRunapp;
        this.eventcode = builder.eventcode;
        this.excuteParam = builder.excuteParam;
    }

    public String getText() {
        return text;
    }

    public String getOpenurl() {
        return openurl;
    }

    public String getUrlText() {
        return urlText;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getImgWidth() {
        return imgWidth;
    }

    public String getImgHeight() {
        return imgHeight;
    }

    public String getIsRunapp() {
        return isRunapp;
    }

    public String getEventcode() {
        return eventcode;
    }

    public String getExcuteParam() {
        return excuteParam;
    }

    public static class Builder {
        private String text = null;
        private String openurl = null;
        private String urlText = null;
        private String imgUrl = null;
        private String imgWidth = null;
        private String imgHeight = null;
        private String isRunapp = null;
        private String eventcode = null;
        private String excuteParam = null;

        public Builder setText(String text) {
            this.text = text;
            return this;
        }

        public Builder setOpenurl(String openurl) {
            this.openurl = openurl;
            return this;
        }

        public Builder setUrlText(String urlText) {
            this.urlText = urlText;
            return this;
        }

        public Builder setImgUrl(String imgUrl) {
            this.imgUrl = imgUrl;
            return this;
        }

        public Builder setImgWidth(String imgWidth) {
            this.imgWidth = imgWidth;
            return this;
        }

        public Builder setImgHeight(String imgHeight) {
            this.imgHeight = imgHeight;
            return this;
        }

        public Builder setIsRunapp(String isRunapp) {
            this.isRunapp = isRunapp;
            return this;
        }

        public Builder setEventcode(String eventcode) {
            this.eventcode = eventcode;
            return this;
        }

        public Builder setExcuteParam(String excuteParam) {
            this.excuteParam = excuteParam;
            return this;
        }

        public SnapsSnsInviteParams create() {
            return new SnapsSnsInviteParams(this);
        }
    }
}
