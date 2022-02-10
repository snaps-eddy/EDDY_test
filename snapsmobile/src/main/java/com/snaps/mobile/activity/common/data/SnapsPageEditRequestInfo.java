package com.snaps.mobile.activity.common.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class SnapsPageEditRequestInfo implements Serializable, Parcelable {
    private static final long serialVersionUID = 6907787560540666515L;
    private boolean isCover = false;
    private int pageIndex = -1;
    private int coverTemplateIndex = 0;
    private int maskCount = 0;
    private String currentPageMultiformId = null;
    private String baseMultiformId = null;
    private String currentPageBGId = null;
    private String basePageBGId = null;

    private SnapsPageEditRequestInfo(Builder builder) {
        this.isCover = builder.isCover;
        this.pageIndex = builder.pageIndex;
        this.coverTemplateIndex = builder.coverTemplateIndex;
        this.currentPageBGId = builder.currentPageBGId;
        this.basePageBGId = builder.basePageBGId;
    }

    protected SnapsPageEditRequestInfo(Parcel in) {
        isCover = in.readByte() != 0;
        pageIndex = in.readInt();
        coverTemplateIndex = in.readInt();
        maskCount = in.readInt();
        currentPageMultiformId = in.readString();
        baseMultiformId = in.readString();
        currentPageBGId = in.readString();
        basePageBGId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (isCover ? 1 : 0));
        dest.writeInt(pageIndex);
        dest.writeInt(coverTemplateIndex);
        dest.writeInt(maskCount);
        dest.writeString(currentPageMultiformId);
        dest.writeString(baseMultiformId);
        dest.writeString(currentPageBGId);
        dest.writeString(basePageBGId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SnapsPageEditRequestInfo> CREATOR = new Creator<SnapsPageEditRequestInfo>() {
        @Override
        public SnapsPageEditRequestInfo createFromParcel(Parcel in) {
            return new SnapsPageEditRequestInfo(in);
        }

        @Override
        public SnapsPageEditRequestInfo[] newArray(int size) {
            return new SnapsPageEditRequestInfo[size];
        }
    };

    public String getCurrentPageBGId() {
        return currentPageBGId;
    }

    public String getBasePageBGId() {
        return basePageBGId;
    }

    public int getMaskCount() {
        return maskCount;
    }

    public void setMaskCount(int maskCount) {
        this.maskCount = maskCount;
    }

    public boolean isCover() {
        return isCover;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public boolean isTitlePage() {
        return pageIndex == 1;
    }

    public int getCoverTemplateIndex() {
        return coverTemplateIndex;
    }

    public String getCurrentPageMultiformId() {
        return currentPageMultiformId;
    }

    public void setCurrentPageMultiformId(String currentPageMultiformId) {
        this.currentPageMultiformId = currentPageMultiformId;
    }

    public void setCurrentPageBGId(String currentPageBGId) {
        this.currentPageBGId = currentPageBGId;
    }

    public void setBasePageBGId(String basePageBGId) {
        this.basePageBGId = basePageBGId;
    }

    public String getBaseMultiformId() {
        return baseMultiformId;
    }

    public void setBaseMultiformId(String baseMultiformId) {
        this.baseMultiformId = baseMultiformId;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public static class Builder {
        private boolean isCover = false;
        private int pageIndex = -1;
        private int coverTemplateIndex = 0;
        private String currentPageBGId = null;
        private String basePageBGId = null;

        public Builder setCurrentPageBGId(String currentPageBGId) {
            this.currentPageBGId = currentPageBGId;
            return this;
        }

        public Builder setBasePageBGId(String basePageBGId) {
            this.basePageBGId = basePageBGId;
            return this;
        }

        public Builder setCover(boolean cover) {
            isCover = cover;
            return this;
        }

        public Builder setPageIndex(int pageIndex) {
            this.pageIndex = pageIndex;
            return this;
        }

        public Builder setCoverTemplateIndex(int coverTemplateIndex) {
            this.coverTemplateIndex = coverTemplateIndex;
            return this;
        }

        public SnapsPageEditRequestInfo create() {
            return new SnapsPageEditRequestInfo(this);
        }
    }
}
