package com.snaps.common.data.img;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by ifunbae on 2017. 1. 9..
 */

public class BPoint implements Serializable, Parcelable {

    int x = 0, y = 0;

    public BPoint() {
        x = 0;
        y = 0;
    }

    public BPoint(int x, int y){
        this.x = x;
        this.y = y;
    }

    protected BPoint(Parcel in) {
        x = in.readInt();
        y = in.readInt();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(x);
        dest.writeInt(y);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BPoint> CREATOR = new Creator<BPoint>() {
        @Override
        public BPoint createFromParcel(Parcel in) {
            return new BPoint(in);
        }

        @Override
        public BPoint[] newArray(int size) {
            return new BPoint[size];
        }
    };

    public void set(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void set(BPoint point) {
        if (point == null) return;
        this.x = point.x;
        this.y = point.y;
    }
}
