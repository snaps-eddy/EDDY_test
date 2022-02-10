package com.snaps.mobile.activity.ui.menu.renewal.model;

import android.graphics.Color;

import com.google.gson.JsonObject;

/**
 * Created by songhw on 2016. 7. 27..
 */
public class Image extends LayoutObject {
    private String click;
    private int bgColor = 0;


    public Image( String click, int bgColor, int[] rect, String value ) {
        type = TYPE_IMAGE;
        this.bgColor = bgColor;
        this.click = click;
        this.rect = rect;
        this.value = value;
    }

    public Image( JsonObject jsonObject ) {
        type = TYPE_IMAGE;

        setRectFromJson( jsonObject );
        setValueFromJson(jsonObject);

        click = jsonObject.has( "click" ) ? jsonObject.get( "click" ).getAsString() : "";
        bgColor = jsonObject.has( "bgColor" ) ? Color.parseColor( jsonObject.get("bgColor").getAsString() ) : 0;
    }

    public Image clone() {
        Image image = (Image) super.clone();
        image.type = TYPE_IMAGE;
        image.click = click;
        return image;
    }

    /**
     * getter
     */
    public String getClick() { return this.click; }
    public int getBgColor() { return this.bgColor; }
}
