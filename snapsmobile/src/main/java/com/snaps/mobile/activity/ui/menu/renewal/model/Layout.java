package com.snaps.mobile.activity.ui.menu.renewal.model;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by songhw on 2016. 7. 26..
 */
public class Layout implements Serializable, Parcelable {
    private static final long serialVersionUID = 1121714916400839925L;
    private int[] size;
    private int bgColor = 0;
    private String click;
    private ArrayList<LayoutObject> objectList;
    private String[] objectListJsonStr;
    private int[] objectTypeList;

    public Layout( int width, int height, int bgColor, String click, ArrayList<LayoutObject> objectList ) {
        size = new int[2];
        size[0] = width;
        size[1] = height;
        this.bgColor = bgColor;
        this.click = click;
        this.objectList = objectList;
    }

    public Layout( JsonObject jsonObject ) {
        JsonObject tempObject;
        JsonArray tempArray;

        size = new int[2];
        tempArray = jsonObject.get( "size" ).getAsJsonArray();
        size[0] = tempArray.get(0).getAsInt();
        size[1] = tempArray.get(1).getAsInt();

        bgColor = Color.parseColor(jsonObject.get("bgColor").getAsString().trim());

        click = jsonObject.has( "click" ) ? jsonObject.get( "click" ).getAsString() : "";

        tempArray = jsonObject.get( "object" ).getAsJsonArray();
        String type;
        if( tempArray != null && tempArray.size() > 0 ) {
            objectList = new ArrayList<LayoutObject>();
            for( int i = 0; i < tempArray.size(); ++i ) {
                tempObject = tempArray.get(i).getAsJsonObject();
                type = tempObject.get( "type" ).getAsString();
                if( "prod_price".equals(type) ) objectList.add( new ProductPrice(tempArray.get(i).getAsJsonObject()) );
                else if( "prod_new".equals(type) ) objectList.add( new Image (tempArray.get(i).getAsJsonObject()) );
                else if( "image".equals(type) ) objectList.add( new Image(tempArray.get(i).getAsJsonObject()) );
                else if( "text".equals(type) ) objectList.add( new Text(tempArray.get(i).getAsJsonObject(), LayoutObject.TYPE_TEXT) );
                else if( "percent".equals(type) ) objectList.add( new Text(tempArray.get(i).getAsJsonObject(), LayoutObject.TYPE_PERCENT) );
                else if( "discount".equals(type) ) objectList.add( new Text(tempArray.get(i).getAsJsonObject(), LayoutObject.TYPE_DISCOUNT) );
                else if( "price".equals(type) ) objectList.add( new Text(tempArray.get(i).getAsJsonObject(), LayoutObject.TYPE_PRICE) );
                else if( "pageControl".equals(type) ) objectList.add( new PageControl(tempArray.get(i).getAsJsonObject()) );
            }
        }
    }

    protected Layout(Parcel in) {
        size = in.createIntArray();
        bgColor = in.readInt();
        click = in.readString();
        objectList = in.createTypedArrayList(LayoutObject.CREATOR);
        objectListJsonStr = in.createStringArray();
        objectTypeList = in.createIntArray();
    }

    public static final Creator<Layout> CREATOR = new Creator<Layout>() {
        @Override
        public Layout createFromParcel(Parcel in) {
            return new Layout(in);
        }

        @Override
        public Layout[] newArray(int size) {
            return new Layout[size];
        }
    };

    public Layout clone() {
        ArrayList<LayoutObject> objects = new ArrayList<LayoutObject>();
        int type;
        if( objectList != null ) {
            for (int i = 0; i < objectList.size(); ++i) {
                type = objectList.get(i).type;
                if( type == LayoutObject.TYPE_PROD_PRICE ) objectList.add( ((ProductPrice) objectList.get(i)).clone() );
                else if( type == LayoutObject.TYPE_IMAGE || type == LayoutObject.TYPE_PROD_NEW ) objectList.add( ((Image) objectList.get(i)).clone() );
                else if( type == LayoutObject.TYPE_TEXT ) objectList.add( ((Text) objectList.get(i)).clone() );
                else if( type == LayoutObject.TYPE_PAGE_CONTROL ) objectList.add( ((PageControl) objectList.get(i)).clone() );
            }
        }

        Layout layout = new Layout( size[0], size[1], bgColor, click, objects );
        layout.objectListJsonStr = objectListJsonStr;
        layout.objectTypeList = objectTypeList;
        return layout;
    }

    public void toJson() {
        if( objectList == null || objectList.size() < 1 ) return;

        objectListJsonStr = new String[ objectList.size() ];
        objectTypeList = new int[ objectList.size() ];
        Gson gson = new Gson();
        int type;
        for( int i = 0; i < objectList.size(); ++i ) {
            type = objectList.get(i).getType();
            objectTypeList[i] = type;
            switch (type) {
                case LayoutObject.TYPE_TEXT :
                    objectListJsonStr[i] = gson.toJson( objectList.get(i), Text.class );
                    break;
                case LayoutObject.TYPE_IMAGE :
                case LayoutObject.TYPE_PROD_NEW :
                    objectListJsonStr[i] = gson.toJson( objectList.get(i), Image.class );
                    break;
                case LayoutObject.TYPE_PAGE_CONTROL :
                    objectListJsonStr[i] = gson.toJson( objectList.get(i), PageControl.class );
                    break;
                case LayoutObject.TYPE_PROD_PRICE :
                    objectListJsonStr[i] = gson.toJson( objectList.get(i), ProductPrice.class );
                    break;
            }
        }
    }

    public void fromJson() {
        if( objectListJsonStr == null || objectTypeList == null || objectListJsonStr.length != objectTypeList.length ) return;
        objectList = new ArrayList<LayoutObject>();
        Gson gson = new Gson();
        int type;
        for( int i = 0; i < objectTypeList.length; ++i ) {
            type = objectTypeList[i];
            switch (type) {
                case LayoutObject.TYPE_TEXT :
                    objectList.add( gson.fromJson(objectListJsonStr[i], Text.class) );
                    break;
                case LayoutObject.TYPE_IMAGE :
                case LayoutObject.TYPE_PROD_NEW :
                    objectList.add( gson.fromJson(objectListJsonStr[i], Image.class) );
                    break;
                case LayoutObject.TYPE_PAGE_CONTROL :
                    objectList.add( gson.fromJson(objectListJsonStr[i], PageControl.class) );
                    break;
                case LayoutObject.TYPE_PROD_PRICE :
                    objectList.add( gson.fromJson(objectListJsonStr[i], ProductPrice.class) );
                    break;
            }
        }
    }

    /**
     * getters
     */
    public int[] getSize() { return this.size; }
    public int getBgColor() { return this.bgColor; }
    public String getClick() { return this.click; }
    public ArrayList<LayoutObject> getObjectList() { return this.objectList; }

    /**
     * setter
     */
    public void setClick( String click ) { this.click = click; }
    public void setBgColor( int color ) { this.bgColor = color; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeIntArray(size);
        dest.writeInt(bgColor);
        dest.writeString(click);
        dest.writeTypedList(objectList);
        dest.writeStringArray(objectListJsonStr);
        dest.writeIntArray(objectTypeList);
    }
}
