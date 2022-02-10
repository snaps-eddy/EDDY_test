package com.snaps.common.utils.ui;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by ifunbae on 2017. 1. 9..
 */

public class BRect implements Serializable, Parcelable {
    private static final long serialVersionUID = 1277106876935567476L;
    public int left, top, right, bottom;

    public final int width() {
        return right - left;
    }

    public final int height() {
        return bottom - top;
    }

    public final int centerX() {
        return (left + right) >> 1;
    }

    public final int centerY() {
        return (top + bottom) >> 1;
    }

    public BRect() {}

    protected BRect(Parcel in) {
        left = in.readInt();
        top = in.readInt();
        right = in.readInt();
        bottom = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(left);
        dest.writeInt(top);
        dest.writeInt(right);
        dest.writeInt(bottom);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BRect> CREATOR = new Creator<BRect>() {
        @Override
        public BRect createFromParcel(Parcel in) {
            return new BRect(in);
        }

        @Override
        public BRect[] newArray(int size) {
            return new BRect[size];
        }
    };

    public void set(int left, int top, int right, int bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }
}
