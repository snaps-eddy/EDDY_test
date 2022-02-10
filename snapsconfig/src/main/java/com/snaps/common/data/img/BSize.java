package com.snaps.common.data.img;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by ifunbae on 2017. 1. 9..
 */

public class BSize implements Serializable, Parcelable {
    private static final long serialVersionUID = -5453563887061970184L;
    float width = 0;
    float height = 0;

    public BSize() {
        width = 0;
        height = 0;
    }

    public BSize(float width, float height){
        this.width = width;
        this.height = height;
    }

    public BSize(BSize bSize){
        if (bSize == null) return;
        this.width = bSize.width;
        this.height = bSize.height;
    }

    protected BSize(Parcel in) {
        width = in.readFloat();
        height = in.readFloat();
    }

    public boolean isValidSize() {
        return width > 0 && height > 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(width);
        dest.writeFloat(height);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BSize> CREATOR = new Creator<BSize>() {
        @Override
        public BSize createFromParcel(Parcel in) {
            return new BSize(in);
        }

        @Override
        public BSize[] newArray(int size) {
            return new BSize[size];
        }
    };

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void set(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void set(BSize size) {
        if (size == null) return;
        this.width = size.width;
        this.height = size.height;
    }
}
