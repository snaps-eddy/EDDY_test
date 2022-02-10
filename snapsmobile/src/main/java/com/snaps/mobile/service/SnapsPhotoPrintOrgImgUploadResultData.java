package com.snaps.mobile.service;

import com.snaps.common.structure.SnapsDelImage;

/**
 * Created by ysjeong on 2017. 4. 4..
 */

public class SnapsPhotoPrintOrgImgUploadResultData {
    private int totalImgCnt = 0;
    private int finishedCnt = 0;
    private SnapsPhotoPrintUploadImageData imageData;
    private SnapsDelImage delImage;

    public static SnapsPhotoPrintOrgImgUploadResultData createDefaultInstance() {
        return new Builder().create();
    }

    private SnapsPhotoPrintOrgImgUploadResultData(Builder builder) {
        if (builder == null) return;

        totalImgCnt = builder.totalImgCnt;
        finishedCnt = builder.finishedCnt;
        imageData = builder.imageData;
        delImage = builder.delImage;
    }

    public void setFinishedCnt(int finishedCnt) {
        this.finishedCnt = finishedCnt;
    }

    public void setTotalImgCnt(int totalImgCnt) {
        this.totalImgCnt = totalImgCnt;
    }

    public int getTotalImgCnt() {
        return totalImgCnt;
    }

    public int getFinishedCnt() {
        return finishedCnt;
    }

    public SnapsPhotoPrintUploadImageData getImageData() {
        return imageData;
    }

    public SnapsDelImage getDelImage() {
        return delImage;
    }

    public static class Builder {
        private int totalImgCnt = 0;
        private int finishedCnt = 0;
        private SnapsPhotoPrintUploadImageData imageData;
        private SnapsDelImage delImage;

        public Builder setTotalImgCnt(int totalImgCnt) {
            this.totalImgCnt = totalImgCnt;
            return this;
        }

        public Builder setFinishedCnt(int finishedCnt) {
            this.finishedCnt = finishedCnt;
            return this;
        }

        public Builder setImageData(SnapsPhotoPrintUploadImageData imageData) {
            this.imageData = imageData;
            return this;
        }

        public Builder setDelImage(SnapsDelImage delImage) {
            this.delImage = delImage;
            return this;
        }

        public SnapsPhotoPrintOrgImgUploadResultData create() {
            return new SnapsPhotoPrintOrgImgUploadResultData(this);
        }
    }
}
