package com.snaps.mobile.activity.ui.menu.renewal.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonObject;

import java.io.Serializable;

/**
 * Created by songhw on 2016. 7. 26..
 */
public class Item implements Serializable, Parcelable {
    private static final long serialVersionUID = 8986678883734325499L;
    private String name, data;
    private boolean isShowNewTag;

    public Item( JsonObject jsonObject ) {
        name = jsonObject.get( "name" ).getAsString();
        data = jsonObject.get( "data" ).getAsString();
        isShowNewTag = "y".equalsIgnoreCase( jsonObject.get("menu_new").getAsString() );
    }

    protected Item(Parcel in) {
        name = in.readString();
        data = in.readString();
        isShowNewTag = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(data);
        dest.writeByte((byte) (isShowNewTag ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Item> CREATOR = new Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

    /**
     * getters
     */
    public String getName() { return this.name; }
    public String getData() { return this.data; }
    public boolean isShowNewTag() { return this.isShowNewTag; }

}
