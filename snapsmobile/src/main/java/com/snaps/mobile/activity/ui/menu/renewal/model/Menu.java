package com.snaps.mobile.activity.ui.menu.renewal.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonObject;

import java.io.Serializable;

/**
 * Created by songhw on 2016. 7. 26..
 */
public class Menu implements Serializable, Parcelable{
    private static final long serialVersionUID = -2770149469468282625L;

    private String layerId, dataId, comment;

    public Menu( JsonObject jsonObject ) {
        layerId = jsonObject.has( "layer" ) ? jsonObject.get( "layer" ).getAsString() : "";
        dataId = jsonObject.has( "data" ) ? jsonObject.get( "data" ).getAsString() : "";
        comment = jsonObject.has( "comment" ) ? jsonObject.get( "comment" ).getAsString(): "";

    }

    public Menu( String layerId, String dataId ) {
        this.layerId = layerId;
        this.dataId = dataId;
    }

    protected Menu(Parcel in) {
        layerId = in.readString();
        dataId = in.readString();
        comment = in.readString();
    }

    public Menu clone() {
        return new Menu( layerId, dataId );
    }

    /**
     * getters
     */
    public String getLayerId() { return this.layerId; }
    public String getDataId() { return this.dataId; }
    public String getComment() { return this.comment; }

    public static final Creator<Menu> CREATOR = new Creator<Menu>() {
        @Override
        public Menu createFromParcel(Parcel in) {
            return new Menu(in);
        }

        @Override
        public Menu[] newArray(int size) {
            return new Menu[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(layerId);
        dest.writeString(dataId);
        dest.writeString(comment);
    }
}
