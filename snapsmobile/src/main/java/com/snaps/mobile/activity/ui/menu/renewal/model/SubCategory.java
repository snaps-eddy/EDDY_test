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
public class SubCategory implements Serializable, Parcelable {
    private static final long serialVersionUID = 7471111770320833132L;
    private String comment, arrangeType, title, topic, stickyImage, infoUrl, nextPageUrl;
    private ArrayList<Item> items;

    public SubCategory( String title, String infoUrl ) { // TODO temp constructor
        this.title = title;
        this.infoUrl = infoUrl;
    }

    public SubCategory( JsonObject jsonObject ) {
        this.comment = jsonObject.has( "comment" ) ? jsonObject.get( "comment" ).getAsString() : "";
        this.arrangeType = jsonObject.has( "arrange" ) ? jsonObject.get( "arrange" ).getAsString() : "";
        this.title = jsonObject.has( "title" ) ? jsonObject.get( "title" ).getAsString() : "";
        this.topic = jsonObject.has( "topic" ) ? jsonObject.get( "topic" ).getAsString() : "";
        this.stickyImage = jsonObject.has( "stickyImage" ) ? jsonObject.get( "stickyImage" ).getAsString() : "";
        this.infoUrl = jsonObject.has( "infoUrl" ) ? jsonObject.get( "infoUrl" ).getAsString() : "";
        this.nextPageUrl = jsonObject.has( "pageUrl" ) ? jsonObject.get( "pageUrl" ).getAsString() : "";

        items = new ArrayList<Item>();
        JsonArray jsonArray = jsonObject.has( "items" ) ? jsonObject.get( "items" ).getAsJsonArray() : null;
        if( jsonArray != null && jsonArray.size() > 0 ) {
            for( int i = 0; i < jsonArray.size(); ++i )
                items.add( new Item(jsonArray.get(i).getAsJsonObject()) );
        }
    }

    public static final Creator<SubCategory> CREATOR = new Creator<SubCategory>() {
        @Override
        public SubCategory createFromParcel(Parcel in) {
            return new SubCategory(in);
        }

        @Override
        public SubCategory[] newArray(int size) {
            return new SubCategory[size];
        }
    };

    public boolean isMultiSubMenu() {
        return items != null && items.size() > 1;
    }

    /**
     * getters
     */
    public String getComment() { return this.comment; }
    public String getArrangeType() { return this.arrangeType; }
    public String getTitle() { return title; }
    public String getTopic() { return topic; }
    public String getStickyImage() { return stickyImage; }
    public String getInfoUrl() { return infoUrl; }
    public String getNextPageUrl() { return nextPageUrl; }
    public ArrayList<Item> getItems() { return this.items; }
    public boolean isFixArrangeType() {
        return getArrangeType() != null && getArrangeType().equalsIgnoreCase("fix");
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(comment);
        dest.writeString(arrangeType);
        dest.writeString(title);
        dest.writeString(topic);
        dest.writeString(stickyImage);
        dest.writeString(infoUrl);
        dest.writeString(nextPageUrl);

        if (items != null)
            dest.writeTypedList(items);
    }

    protected SubCategory(Parcel in) {
        comment = in.readString();
        arrangeType = in.readString();
        title = in.readString();
        topic = in.readString();
        stickyImage = in.readString();
        infoUrl = in.readString();
        nextPageUrl = in.readString();

        if (items != null)
            in.readTypedList(items, Item.CREATOR);
    }
}
