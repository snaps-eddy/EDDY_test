package com.snaps.mobile.activity.ui.menu.renewal.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by songhw on 2016. 9. 23..
 */
public class CustomPair implements Serializable, Parcelable {
    private static final long serialVersionUID = 1392588791623447258L;
    public String key, valueStr;
    public SubCategory subCategory;
    public Layout layout;
    public Value value;

    public CustomPair(String key, Object obj) {
        this.key = key;
        if( obj instanceof SubCategory ) subCategory = (SubCategory) obj;
        if( obj instanceof Layout ) layout = (Layout) obj;
        if( obj instanceof Value ) value = (Value) obj;
    }

    protected CustomPair(Parcel in) {
        key = in.readString();
        valueStr = in.readString();
        subCategory = in.readParcelable(SubCategory.class.getClassLoader());
        layout = in.readParcelable(Layout.class.getClassLoader());
        value = in.readParcelable(Value.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(key);
        dest.writeString(valueStr);
        dest.writeParcelable(subCategory, flags);
        dest.writeParcelable(layout, flags);
        dest.writeParcelable(value, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CustomPair> CREATOR = new Creator<CustomPair>() {
        @Override
        public CustomPair createFromParcel(Parcel in) {
            return new CustomPair(in);
        }

        @Override
        public CustomPair[] newArray(int size) {
            return new CustomPair[size];
        }
    };
}
