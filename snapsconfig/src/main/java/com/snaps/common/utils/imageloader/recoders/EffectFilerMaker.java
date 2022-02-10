package com.snaps.common.utils.imageloader.recoders;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.snaps.common.data.img.MyPhotoSelectImageData;

import java.io.Serializable;

/**
 * Created by ysjeong on 16. 6. 29..
 */
public class EffectFilerMaker implements Parcelable, Serializable {
    private Context context = null;
    private MyPhotoSelectImageData imageData = null;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public MyPhotoSelectImageData getImageData() {
        return imageData;
    }

    public void setImageData(MyPhotoSelectImageData imageData) {
        this.imageData = imageData;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {// !! Parcel 객체에 write 하는
        dest.writeParcelable(imageData, 0);
    }

    private void readFromParcel(Parcel in) {// !! Parcel 객체에 write 하는 순서는 read
        imageData = in.readParcelable(MyPhotoSelectImageData.class.getClassLoader());
    }

    public EffectFilerMaker() {}

    public EffectFilerMaker(Parcel in) {
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @SuppressWarnings("rawtypes")
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        @Override
        public EffectFilerMaker createFromParcel(Parcel in) {
            return new EffectFilerMaker(in);
        }

        @Override
        public EffectFilerMaker[] newArray(int size) {
            return new EffectFilerMaker[size];
        }
    };
}
