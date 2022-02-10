package com.snaps.mobile.activity.home.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by songhw on 2016. 7. 12..
 */
public class TextData implements Parcelable {
    public String text;
    public int x, y, w, h, z = -1;
    public int ori;

    public TextData() {}

    protected TextData(Parcel in) {
        text = in.readString();
        x = in.readInt();
        y = in.readInt();
        w = in.readInt();
        h = in.readInt();
        z = in.readInt();
        ori = in.readInt();
    }

    public static final Creator<TextData> CREATOR = new Creator<TextData>() {
        @Override
        public TextData createFromParcel(Parcel in) {
            return new TextData(in);
        }

        @Override
        public TextData[] newArray(int size) {
            return new TextData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString( text );
        dest.writeInt( x );
        dest.writeInt( y );
        dest.writeInt( w );
        dest.writeInt( h );
        dest.writeInt( z );
        dest.writeInt( ori );
    }
}
