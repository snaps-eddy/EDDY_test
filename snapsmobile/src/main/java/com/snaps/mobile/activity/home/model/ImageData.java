package com.snaps.mobile.activity.home.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by songhw on 2016. 7. 12..
 */
public class ImageData implements Parcelable {
    public int x, y, w, h, z = -1;

    public ImageData() {}

    protected ImageData(Parcel in) {
        x = in.readInt();
        y = in.readInt();
        w = in.readInt();
        h = in.readInt();
        z = in.readInt();
    }

    public static final Creator<ImageData> CREATOR = new Creator<ImageData>() {
        @Override
        public ImageData createFromParcel(Parcel in) {
            return new ImageData(in);
        }

        @Override
        public ImageData[] newArray(int size) {
            return new ImageData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt( x );
        dest.writeInt( y );
        dest.writeInt( w );
        dest.writeInt( h );
        dest.writeInt( z );
    }
}
