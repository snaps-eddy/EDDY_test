package com.snaps.mobile.activity.home.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by songhw on 2016. 7. 12..
 */
public class PageDataSet implements Parcelable {
    public int w;
    public int h;
    public ArrayList<TextData> textList;
    public ArrayList<ImageData> imageList;

    public PageDataSet(Parcel in) {
        w = in.readInt();
        h = in.readInt();
        textList = in.createTypedArrayList(TextData.CREATOR);
        imageList = in.createTypedArrayList(ImageData.CREATOR);
    }

    public PageDataSet( int width, int height ) {
        w = width;
        h = height;
        textList = new ArrayList<TextData>();
        imageList = new ArrayList<ImageData>();
    }

    public static final Creator<PageDataSet> CREATOR = new Creator<PageDataSet>() {
        @Override
        public PageDataSet createFromParcel(Parcel in) {
            return new PageDataSet(in);
        }

        @Override
        public PageDataSet[] newArray(int size) {
            return new PageDataSet[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt( w );
        dest.writeInt( h );
        dest.writeParcelableArray( (TextData[]) textList.toArray(), flags );
        dest.writeParcelableArray( (ImageData[])imageList.toArray(), flags );
    }


}
