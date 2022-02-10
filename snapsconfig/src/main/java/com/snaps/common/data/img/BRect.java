package com.snaps.common.data.img;

import android.os.Parcel;
import android.os.Parcelable;

import com.snaps.common.utils.ui.StringUtil;

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

    public BRect(int l, int t, int r, int b) {
        set(l, t, r, b);
    }

    public BRect(BRect rect) {
        if (rect == null) return;
        set(rect.left, rect.top, rect.right, rect.bottom);
    }

    public boolean isValidRect() {
        return width() > 0 && height() > 0;
    }

    protected BRect(Parcel in) {
        left = in.readInt();
        top = in.readInt();
        right = in.readInt();
        bottom = in.readInt();
    }

    public void offset(int dx, int dy) {
        left += dx;
        top += dy;
        right += dx;
        bottom += dy;
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

    public void set(BRect rect) {
        if (rect == null) return;
        this.left = rect.left;
        this.top = rect.top;
        this.right = rect.right;
        this.bottom = rect.bottom;
    }

    public boolean contains(int x, int y) {
        return left <= x && right >= x
                && top <= y && bottom >= y;
    }

    public static BRect createBRectWithRcStr(String rcStr) {
        if (StringUtil.isEmpty(rcStr)) return new BRect();
        String[] rc = rcStr.replace(" ", "|").split("\\|");
        int x = (int) Float.parseFloat(rc[0]);
        int y = (int) Float.parseFloat(rc[1]);
        int w = (int) Float.parseFloat(rc[2]);
        int h = (int) Float.parseFloat(rc[3]);
        return new BRect(x, y, x+w, y+h);
    }
}
