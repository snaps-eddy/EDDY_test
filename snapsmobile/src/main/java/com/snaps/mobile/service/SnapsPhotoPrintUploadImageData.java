package com.snaps.mobile.service;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.structure.photoprint.ImpUploadProject;

/**
 * Created by ysjeong on 2018. 1. 2..
 */

public class SnapsPhotoPrintUploadImageData {
    private int imageId;
    private int imageKind;
    private String fileName;
    private MyPhotoSelectImageData myPhotoSelectImageData;
    private ImpUploadProject project;

    private SnapsPhotoPrintUploadImageData(Builder builder) {
        this.imageId = builder.imageId;
        this.imageKind = builder.imageKind;
        this.fileName = builder.fileName;
        this.myPhotoSelectImageData = builder.myPhotoSelectImageData;
        this.project = builder.project;
    }

    public int getImageId() {
        return imageId;
    }

    public int getImageKind() {
        return imageKind;
    }

    public String getFileName() {
        return fileName;
    }

    public MyPhotoSelectImageData getMyPhotoSelectImageData() {
        return myPhotoSelectImageData;
    }

    public ImpUploadProject getProject() {
        return project;
    }

    public static class Builder {
        private int imageId;
        private int imageKind;
        private String fileName;
        private MyPhotoSelectImageData myPhotoSelectImageData;
        private ImpUploadProject project;

        public Builder setImageId(int imageId) {
            this.imageId = imageId;
            return this;
        }

        public Builder setImageKind(int imageKind) {
            this.imageKind = imageKind;
            return this;
        }

        public Builder setFileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public Builder setMyPhotoSelectImageData(MyPhotoSelectImageData myPhotoSelectImageData) {
            this.myPhotoSelectImageData = myPhotoSelectImageData;
            return this;
        }

        public Builder setProject(ImpUploadProject project) {
            this.project = project;
            return this;
        }

        public SnapsPhotoPrintUploadImageData create() {
            return new SnapsPhotoPrintUploadImageData(this);
        }
    }
}
