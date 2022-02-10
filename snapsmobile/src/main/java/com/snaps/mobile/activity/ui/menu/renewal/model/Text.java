package com.snaps.mobile.activity.ui.menu.renewal.model;

import android.graphics.Color;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.activity.ui.menu.renewal.MenuDataManager;

/**
 * Created by songhw on 2016. 7. 27..
 */
public class Text extends LayoutObject {
    protected int size;
    protected int space;
    protected int[] color;
    protected int underline;
    protected String font;
    protected String align;
    protected ProductPrice[] additions;

    public Text( JsonObject jsonObject, int type ) {
        this.type = type;

        setRectFromJson(jsonObject);
        setValueFromJson( jsonObject );
        setAttributeFromJson( jsonObject );

        if( jsonObject.has("addition") ) {
            JsonArray additionJsonAry = jsonObject.get("addition").getAsJsonArray();
            int additionLength = additionJsonAry.size();
            additions = new ProductPrice[additionLength];
            JsonObject additionJson;
            String addtionType;
            for( int i = 0; i < additionLength; ++i ) {
                additionJson = additionJsonAry.get(i).getAsJsonObject();
                addtionType = additionJson.get("type").getAsString();
                additions[i] = new ProductPrice( additionJson );

                if( "percent".equals(addtionType) ) additions[i].type = LayoutObject.TYPE_PERCENT;
                else if( "discount".equals(addtionType) ) additions[i].type = LayoutObject.TYPE_DISCOUNT;
                else if( "price".equals(addtionType) ) additions[i].type = LayoutObject.TYPE_PRICE;
            }
        }
    }

    public void setAttributeFromJson(JsonObject jsonObject) {
        JsonObject object = jsonObject.get( "attribute" ).getAsJsonObject();

        if( object.has("color") ) {
            String colorStr = object.get( "color" ).getAsString();
            if( !StringUtil.isEmpty(colorStr) ) {
                if (colorStr.contains(MenuDataManager.SEPARATOR_STRING.replace("\\",""))) {
                    String[] colorStrAry = colorStr.split(MenuDataManager.SEPARATOR_STRING);
                    color = new int[colorStrAry.length];
                    for (int i = 0; i < colorStrAry.length; ++i) {
                        if( !colorStrAry[i].contains("#") ) colorStrAry[i] = "#" + colorStrAry[i];
                        color[i] = Color.parseColor(colorStrAry[i]);
                    }
                }
                else
                    color = new int[]{Color.parseColor(colorStr)};
            }
        }

        font = object.has( "font" ) ? object.get( "font" ).getAsString() : "";
        size = object.has( "size" ) ? object.get( "size" ).getAsInt() : 0;
        align = object.has( "align" ) ? object.get( "align" ).getAsString() : "";
        underline = object.has( "underLine" ) ? Color.parseColor(object.get("underLine").getAsString()) : 0;
        space = object.has( "space" ) ? object.get( "space" ).getAsInt() : 0;
    }

    public Text clone() {
        Text instance = (Text) super.clone();
        instance.size = size;
        instance.space = space;
        instance.color = color;
        instance.underline = underline;
        instance.font = font;
        instance.align = align;
        instance.additions = additions.clone();
        return instance;
    }

    public int getSpace() { return space; }
    public ProductPrice[] getAdditions() { return additions; }
    public int getSize() { return size; }
    public int[] getColor() { return color; }
    public int getUnderline() { return underline; }
    public String getFont() { return font; }
    public String getAlign() { return align; }
}
