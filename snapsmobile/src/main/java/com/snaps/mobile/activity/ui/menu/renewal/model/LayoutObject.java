package com.snaps.mobile.activity.ui.menu.renewal.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.Serializable;

/**
 * Created by songhw on 2016. 7. 27..
 */
public class LayoutObject implements Serializable, Parcelable {
    public static final int TYPE_IMAGE               = 0;
    public static final int TYPE_TEXT                = 1;
    public static final int TYPE_PAGE_CONTROL        = 2;
    public static final int TYPE_PROD_PRICE          = 3;
    public static final int TYPE_PROD_NEW            = 5;
    public static final int TYPE_PERCENT             = 6;
    public static final int TYPE_DISCOUNT            = 7;
    public static final int TYPE_PRICE               = 8;
    private static final long serialVersionUID = 1369820718490424926L;

    protected int type;
    protected int[] rect;

    protected String value;

    public LayoutObject() {}

    protected LayoutObject(Parcel in) {
        type = in.readInt();
        rect = in.createIntArray();
        value = in.readString();
    }

    public static final Creator<LayoutObject> CREATOR = new Creator<LayoutObject>() {
        @Override
        public LayoutObject createFromParcel(Parcel in) {
            return new LayoutObject(in);
        }

        @Override
        public LayoutObject[] newArray(int size) {
            return new LayoutObject[size];
        }
    };

    protected void setRectFromJson( JsonObject object ) {
        if( !object.has("rect") ) return;

        rect = new int[4];
        JsonArray array = object.get( "rect" ).getAsJsonArray();
        if( array != null && array.size() > 3 ) {
            for( int i = 0; i < 4; ++i )
                rect[i] = array.get(i).getAsInt();
        }
    }

    protected void setValueFromJson( JsonObject object ) {
        if( !object.has( "value") ) return;
        value = object.get( "value" ).getAsString();
    }

    protected LayoutObject clone() {
        LayoutObject instance = new LayoutObject();
        instance.type = type;
        instance.rect = rect;
        instance.value = value;
        return instance;
    }

    public int[] getRect() { return this.rect; }
    public int getType() { return this.type; }
    public String getValue() { return this.value; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(type);
        dest.writeIntArray(rect);
        dest.writeString(value);
    }
}
