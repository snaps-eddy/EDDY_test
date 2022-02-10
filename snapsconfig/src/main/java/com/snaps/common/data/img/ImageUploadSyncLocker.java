package com.snaps.common.data.img;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by ysjeong on 2017. 4. 13..
 */

public class ImageUploadSyncLocker implements Parcelable, Serializable {

    private static final long serialVersionUID = 5663419841524039815L;

    public ImageUploadSyncLocker() {}
    protected ImageUploadSyncLocker(Parcel in) {
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ImageUploadSyncLocker> CREATOR = new Creator<ImageUploadSyncLocker>() {
        @Override
        public ImageUploadSyncLocker createFromParcel(Parcel in) {
            return new ImageUploadSyncLocker(in);
        }

        @Override
        public ImageUploadSyncLocker[] newArray(int size) {
            return new ImageUploadSyncLocker[size];
        }
    };
}
