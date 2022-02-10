package com.snaps.common.data.img;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by ysjeong on 2017. 4. 13..
 */

public class SmartSnapsImgUploadSyncLocker implements Parcelable, Serializable {

    private static final long serialVersionUID = 856548519962542894L;

    public SmartSnapsImgUploadSyncLocker() {}
    protected SmartSnapsImgUploadSyncLocker(Parcel in) {
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SmartSnapsImgUploadSyncLocker> CREATOR = new Creator<SmartSnapsImgUploadSyncLocker>() {
        @Override
        public SmartSnapsImgUploadSyncLocker createFromParcel(Parcel in) {
            return new SmartSnapsImgUploadSyncLocker(in);
        }

        @Override
        public SmartSnapsImgUploadSyncLocker[] newArray(int size) {
            return new SmartSnapsImgUploadSyncLocker[size];
        }
    };
}
