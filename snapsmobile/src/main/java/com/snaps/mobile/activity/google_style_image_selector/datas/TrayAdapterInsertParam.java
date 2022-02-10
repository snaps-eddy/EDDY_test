package com.snaps.mobile.activity.google_style_image_selector.datas;

import com.snaps.common.data.img.MyPhotoSelectImageData;

public class TrayAdapterInsertParam {
    private String imageMapKey;
    private MyPhotoSelectImageData imageData;
    private boolean isArrayInsert = false;
    private boolean isArrayInsertAndLastItem = false;

    private TrayAdapterInsertParam(Builder builder) {
        this.imageMapKey = builder.imageMapKey;
        this.imageData = builder.imageData;
        this.isArrayInsert = builder.isArrayInsert;
        this.isArrayInsertAndLastItem = builder.isArrayInsertAndLastItem;
    }

    public String getImageMapKey() {
        return imageMapKey;
    }

    public MyPhotoSelectImageData getImageData() {
        return imageData;
    }

    public boolean isArrayInsert() {
        return isArrayInsert;
    }

    public boolean isArrayInsertAndLastItem() {
        return isArrayInsertAndLastItem;
    }

    public static class Builder {
        private String imageMapKey;
        private MyPhotoSelectImageData imageData;
        private boolean isArrayInsert = false;
        private boolean isArrayInsertAndLastItem = false;

        public Builder setImageMapKey(String imageMapKey) {
            this.imageMapKey = imageMapKey;
            return this;
        }

        public Builder setImageData(MyPhotoSelectImageData imageData) {
            this.imageData = imageData;
            return this;
        }

        public Builder setArrayInsert(boolean arrayInsert) {
            isArrayInsert = arrayInsert;
            return this;
        }

        public Builder setArrayInsertAndLastItem(boolean arrayInsertAndLastItem) {
            isArrayInsertAndLastItem = arrayInsertAndLastItem;
            return this;
        }

        public TrayAdapterInsertParam create() {
            return new TrayAdapterInsertParam(this);
        }
    }

}
