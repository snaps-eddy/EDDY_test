package com.snaps.common.structure.photoprint;

import com.snaps.mobile.activity.photoprint.model.PhotoPrintData;

import java.io.File;

/**
 * Created by ysjeong on 2018. 1. 11..
 */

public class SnapsXmlMakeResult {

    private Exception exception = null;
    private boolean isSuccess = false;
    private File xmlFile = null;
    private PhotoPrintData photoPrintData = null;

    private SnapsXmlMakeResult(Builder builder) {
        this.exception = builder.exception;
        this.isSuccess = builder.isSuccess;
        this.xmlFile = builder.xmlFile;
        this.photoPrintData = builder.photoPrintData;
    }

    public Exception getException() {
        return exception;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public File getXmlFile() {
        return xmlFile;
    }

    public PhotoPrintData getPhotoPrintData() {
        return photoPrintData;
    }

    public static class Builder {
        private Exception exception = null;
        private boolean isSuccess = false;
        private File xmlFile = null;
        private PhotoPrintData photoPrintData = null;

        public Builder setException(Exception exception) {
            this.exception = exception;
            return this;
        }

        public Builder setSuccess(boolean success) {
            isSuccess = success;
            return this;
        }

        public Builder setXmlFile(File xmlFile) {
            this.xmlFile = xmlFile;
            return this;
        }

        public Builder setPhotoPrintData(PhotoPrintData photoPrintData) {
            this.photoPrintData = photoPrintData;
            return this;
        }

        public SnapsXmlMakeResult create() {
            return new SnapsXmlMakeResult(this);
        }
    }
}
