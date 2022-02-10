package com.snaps.common.structure.photoprint.json;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by songhw on 2017. 3. 14..
 */

public class PhotoPrintJsonObjectTmplnfo implements Serializable, Parcelable {
    private static final long serialVersionUID = 2443697804418485430L;

    @SerializedName("F_PROD_CODE")
    private String F_PROD_CODE;
    @SerializedName("F_PROD_TYPE")
    private String F_PROD_TYPE;
    @SerializedName("F_PROD_NAME")
    private String F_PROD_NAME;
    @SerializedName("F_PROD_NICK_NAME")
    private String F_PROD_NICK_NAME;
    @SerializedName("F_PROD_SIZE")
    private String F_PROD_SIZE;
    @SerializedName("F_PAGE_MM_WIDTH")
    private String F_PAGE_MM_WIDTH;
    @SerializedName("F_PAGE_MM_HEIGHT")
    private String F_PAGE_MM_HEIGHT;
    @SerializedName("F_PAGE_PIXEL_WIDTH")
    private String F_PAGE_PIXEL_WIDTH;
    @SerializedName("F_PAGE_PIXEL_HEIGHT")
    private String F_PAGE_PIXEL_HEIGHT;
    @SerializedName("F_RES_MIN")
    private String F_RES_MIN;

    protected PhotoPrintJsonObjectTmplnfo(Parcel in) {
        F_PROD_CODE = in.readString();
        F_PROD_TYPE = in.readString();
        F_PROD_NAME = in.readString();
        F_PROD_NICK_NAME = in.readString();
        F_PROD_SIZE = in.readString();
        F_PAGE_MM_WIDTH = in.readString();
        F_PAGE_MM_HEIGHT = in.readString();
        F_PAGE_PIXEL_WIDTH = in.readString();
        F_PAGE_PIXEL_HEIGHT = in.readString();
        F_RES_MIN = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(F_PROD_CODE);
        dest.writeString(F_PROD_TYPE);
        dest.writeString(F_PROD_NAME);
        dest.writeString(F_PROD_NICK_NAME);
        dest.writeString(F_PROD_SIZE);
        dest.writeString(F_PAGE_MM_WIDTH);
        dest.writeString(F_PAGE_MM_HEIGHT);
        dest.writeString(F_PAGE_PIXEL_WIDTH);
        dest.writeString(F_PAGE_PIXEL_HEIGHT);
        dest.writeString(F_RES_MIN);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PhotoPrintJsonObjectTmplnfo> CREATOR = new Creator<PhotoPrintJsonObjectTmplnfo>() {
        @Override
        public PhotoPrintJsonObjectTmplnfo createFromParcel(Parcel in) {
            return new PhotoPrintJsonObjectTmplnfo(in);
        }

        @Override
        public PhotoPrintJsonObjectTmplnfo[] newArray(int size) {
            return new PhotoPrintJsonObjectTmplnfo[size];
        }
    };

    public String getF_PROD_CODE() {
        return F_PROD_CODE;
    }

    public String getF_PROD_TYPE() {
        return F_PROD_TYPE;
    }

    public String getF_PROD_NAME() {
        return F_PROD_NAME;
    }

    public String getF_PROD_NICK_NAME() {
        return F_PROD_NICK_NAME;
    }

    public String getF_PROD_SIZE() {
        return F_PROD_SIZE;
    }

    public String getF_PAGE_MM_WIDTH() {
        return F_PAGE_MM_WIDTH;
    }

    public String getF_PAGE_MM_HEIGHT() {
        return F_PAGE_MM_HEIGHT;
    }

    public String getF_PAGE_PIXEL_WIDTH() {
        return F_PAGE_PIXEL_WIDTH;
    }

    public String getF_PAGE_PIXEL_HEIGHT() {
        return F_PAGE_PIXEL_HEIGHT;
    }

    public String getF_RES_MIN() {
        return F_RES_MIN;
    }
}
