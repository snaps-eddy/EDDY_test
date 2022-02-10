package com.snaps.common.data.smart_snaps;

/**
 * Created by ysjeong on 2018. 2. 21..
 */

public class SmartSnapsSaveXmlImageInfo {
    private int pageIdx = 0;
    private String orientation = null;
    private String imgAnalysis = null;

    private SmartSnapsSaveXmlImageInfo(Builder builder) {
        pageIdx = builder.pageIdx;
        orientation = builder.orientation;
        imgAnalysis = builder.imgAnalysis;
    }

    public int getPageIdx() {
        return pageIdx;
    }

    public String getOrientation() {
        return orientation;
    }

    public String getImgAnalysis() {
        return imgAnalysis;
    }

    public static class Builder {
        private int pageIdx = 0;
        private String orientation = null;
        private String imgAnalysis = null;

        public Builder setPageIdx(int pageIdx) {
            this.pageIdx = pageIdx;
            return this;
        }

        public Builder setOrientation(String orientation) {
            this.orientation = orientation;
            return this;
        }

        public Builder setImgAnalysis(String imgAnalysis) {
            this.imgAnalysis = imgAnalysis;
            return this;
        }

        public SmartSnapsSaveXmlImageInfo create() {
            return new SmartSnapsSaveXmlImageInfo(this);
        }
    }

}
