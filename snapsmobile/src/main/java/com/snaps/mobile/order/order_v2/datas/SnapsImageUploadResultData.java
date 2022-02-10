package com.snaps.mobile.order.order_v2.datas;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.structure.SnapsDelImage;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderConstants;

/**
 * Created by ysjeong on 2017. 4. 4..
 */

public class SnapsImageUploadResultData {
    private int totalImgCnt = 0;
    private int finishedCnt = 0;
    private SnapsOrderConstants.eSnapsOrderUploadResultMsg uploadResultMsg;
    private MyPhotoSelectImageData imageData;
    private SnapsDelImage delImage;
    private String detailMsg = "";

    private SnapsImageUploadResultData(Builder builder) {
        if (builder == null) return;

        totalImgCnt = builder.totalImgCnt;
        finishedCnt = builder.finishedCnt;
        uploadResultMsg = builder.errMsg;
        imageData = builder.imageData;
        delImage = builder.delImage;
        detailMsg = builder.detailMsg;
    }

    public String getDetailMsg() {
        return detailMsg;
    }

    public void setUploadResultMsg(SnapsOrderConstants.eSnapsOrderUploadResultMsg uploadResultMsg) {
        this.uploadResultMsg = uploadResultMsg;
    }

    public void setFinishedCnt(int finishedCnt) {
        this.finishedCnt = finishedCnt;
    }

    public void setTotalImgCnt(int totalImgCnt) {
        this.totalImgCnt = totalImgCnt;
    }

    public SnapsOrderConstants.eSnapsOrderUploadResultMsg getUploadResultMsg() {
        return uploadResultMsg;
    }

    public int getTotalImgCnt() {
        return totalImgCnt;
    }

    public int getFinishedCnt() {
        return finishedCnt;
    }

    public MyPhotoSelectImageData getImageData() {
        return imageData;
    }

    public SnapsDelImage getDelImage() {
        return delImage;
    }

    public static class Builder {
        private int totalImgCnt = 0;
        private int finishedCnt = 0;
        private SnapsOrderConstants.eSnapsOrderUploadResultMsg errMsg;
        private MyPhotoSelectImageData imageData;
        private SnapsDelImage delImage;
        private String detailMsg = "";

        public Builder setDetailMsg(String detailMsg) {
            this.detailMsg = detailMsg;
            return this;
        }

        public Builder setErrMsg(SnapsOrderConstants.eSnapsOrderUploadResultMsg errMsg) {
            this.errMsg = errMsg;
            return this;
        }

        public Builder setTotalImgCnt(int totalImgCnt) {
            this.totalImgCnt = totalImgCnt;
            return this;
        }

        public Builder setFinishedCnt(int finishedCnt) {
            this.finishedCnt = finishedCnt;
            return this;
        }

        public Builder setImageData(MyPhotoSelectImageData imageData) {
            this.imageData = imageData;
            return this;
        }

        public Builder setDelImage(SnapsDelImage delImage) {
            this.delImage = delImage;
            return this;
        }

        public SnapsImageUploadResultData create() {
            return new SnapsImageUploadResultData(this);
        }
    }
}
