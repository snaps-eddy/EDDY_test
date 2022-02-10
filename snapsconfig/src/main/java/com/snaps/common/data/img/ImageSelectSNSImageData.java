package com.snaps.common.data.img;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ysjeong on 2016. 12. 22..
 */

public class ImageSelectSNSImageData implements Parcelable {

    private String id = "";
    private String orgImageUrl = "";
    private String thumbnailImageUrl = "";
    private String content = "";
    private String strCreateAt = "";
    private long lCreateAt;

    private String realId = "";
    private String mineType ="";
    //원본이미지 넓이, 높이 추가...
    private String orgImageWidth = "";
    private String orgImageHeight = "";

    public ImageSelectSNSImageData() {}

    protected ImageSelectSNSImageData(Parcel in) {
        id = in.readString();
        orgImageUrl = in.readString();
        thumbnailImageUrl = in.readString();
        content = in.readString();
        strCreateAt = in.readString();
        lCreateAt = in.readLong();
        realId = in.readString();
        orgImageWidth = in.readString();
        orgImageHeight = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(orgImageUrl);
        dest.writeString(thumbnailImageUrl);
        dest.writeString(content);
        dest.writeString(strCreateAt);
        dest.writeLong(lCreateAt);
        dest.writeString(realId);
        dest.writeString(orgImageWidth);
        dest.writeString(orgImageHeight);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ImageSelectSNSImageData> CREATOR = new Creator<ImageSelectSNSImageData>() {
        @Override
        public ImageSelectSNSImageData createFromParcel(Parcel in) {
            return new ImageSelectSNSImageData(in);
        }

        @Override
        public ImageSelectSNSImageData[] newArray(int size) {
            return new ImageSelectSNSImageData[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrgImageUrl() {
        return orgImageUrl;
    }

    public void setOrgImageUrl(String orgImageUrl) {
        this.orgImageUrl = orgImageUrl;
    }

    public String getThumbnailImageUrl() {
        return thumbnailImageUrl;
    }

    public void setThumbnailImageUrl(String thumbnailImageUrl) {
        this.thumbnailImageUrl = thumbnailImageUrl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStrCreateAt() {
        return strCreateAt;
    }

    public void setStrCreateAt(String strCreateAt) {
        this.strCreateAt = strCreateAt;
    }

    public long getlCreateAt() {
        return lCreateAt;
    }

    public void setlCreateAt(long lCreateAt) {
        this.lCreateAt = lCreateAt;
    }

    public String getRealId() {
        return realId;
    }

    public void setRealId(String realId) {
        this.realId = realId;
    }

    public String getOrgImageWidth() {
        return orgImageWidth;
    }

    public void setOrgImageWidth(String orgImageWidth) {
        this.orgImageWidth = orgImageWidth;
    }

    public String getOrgImageHeight() {
        return orgImageHeight;
    }

    public void setOrgImageHeight(String orgImageHeight) {
        this.orgImageHeight = orgImageHeight;
    }

    public String getMineType() {
        return mineType;
    }

    public void setMineType(String mineType) {
        this.mineType = mineType;
    }
}
