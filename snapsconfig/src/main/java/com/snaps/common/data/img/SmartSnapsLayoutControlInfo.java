package com.snaps.common.data.img;

import android.os.Parcel;
import android.os.Parcelable;

import com.snaps.common.utils.ui.StringUtil;

import java.io.Serializable;


/**
 * Created by ysjeong on 2017. 9. 8..
 */

public class SmartSnapsLayoutControlInfo implements Parcelable, Serializable {

    private static final long serialVersionUID = 2641497468063846035L;
    private BSize imageSize;
    private BRect clipRect;
    private MyPhotoSelectImageData imageData;

    public SmartSnapsLayoutControlInfo(Builder builder) {
        this.imageSize = builder.imageSize;
        this.clipRect = builder.clipRect;
        this.imageData = builder.imageData;
    }

    protected SmartSnapsLayoutControlInfo(Parcel in) {
        imageSize = in.readParcelable(BSize.class.getClassLoader());
        clipRect = in.readParcelable(BRect.class.getClassLoader());
        imageData = in.readParcelable(MyPhotoSelectImageData.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(imageSize, flags);
        dest.writeParcelable(clipRect, flags);
        dest.writeParcelable(imageData, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SmartSnapsLayoutControlInfo> CREATOR = new Creator<SmartSnapsLayoutControlInfo>() {
        @Override
        public SmartSnapsLayoutControlInfo createFromParcel(Parcel in) {
            return new SmartSnapsLayoutControlInfo(in);
        }

        @Override
        public SmartSnapsLayoutControlInfo[] newArray(int size) {
            return new SmartSnapsLayoutControlInfo[size];
        }
    };

    public static class Builder {
        private BSize imageSize;
        private BRect clipRect;
        private MyPhotoSelectImageData imageData;

        public Builder setImageSize(BSize imageSize) {
            this.imageSize = imageSize;
            return this;
        }

        public Builder setClipRectInfo(String clipRectInfo) throws Exception {
            if (StringUtil.isEmpty(clipRectInfo)) return this;
            setClipRect(BRect.createBRectWithRcStr(clipRectInfo));
            return this;
        }

        public Builder setClipRect(BRect clipRect) {
            this.clipRect = clipRect;
            return this;
        }

        public Builder setImageData(MyPhotoSelectImageData imageData) {
            this.imageData = imageData;
            return this;
        }

        public SmartSnapsLayoutControlInfo create() {
            return new SmartSnapsLayoutControlInfo(this);
        }
    }

    public BRect getClipRect() {
        return clipRect;
    }

    public BSize getImageSize() {
        return imageSize;
    }

    public MyPhotoSelectImageData getImageData() {
        return imageData;
    }
}
