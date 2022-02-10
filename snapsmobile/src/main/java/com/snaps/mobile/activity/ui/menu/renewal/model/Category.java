package com.snaps.mobile.activity.ui.menu.renewal.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by songhw on 2016. 7. 26..
 */
public class Category implements Parcelable, Serializable {
    private static final long serialVersionUID = -3940245288128219510L;
    private String title;
    private boolean isShowNewTag;
    private ArrayList<Menu> menuList;

    public Category( String title, boolean showNewTag, ArrayList<Menu> menuList ) {
        this.title = title;
        this.isShowNewTag = showNewTag;
        this.menuList = menuList;
    }

    public Category( JsonObject jsonObject ) {
        this.title = jsonObject.get( "title" ).getAsString();
        this.isShowNewTag = "true".equalsIgnoreCase(jsonObject.get("new").getAsString());

        JsonArray jsonArray = jsonObject.getAsJsonArray( "menus" );
        if( jsonArray != null ) {
            menuList = new ArrayList<Menu>();
            for( int i = 0; i < jsonArray.size(); ++i )
                menuList.add( new Menu(jsonArray.get(i).getAsJsonObject()) );
        }
    }

    protected Category(Parcel in) {
        title = in.readString();
        isShowNewTag = in.readByte() != 0;
        menuList = in.createTypedArrayList(Menu.CREATOR);
    }

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

    /**
     * getters
     */
    public String getTitle() { return this.title; }
    public boolean isShowNewTag() { return this.isShowNewTag; }
    public ArrayList<Menu> getMenuList() { return this.menuList; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeByte((byte) (isShowNewTag ? 1 : 0));
        dest.writeTypedList(menuList);
    }
}
