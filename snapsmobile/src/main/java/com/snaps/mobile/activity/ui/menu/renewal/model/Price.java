package com.snaps.mobile.activity.ui.menu.renewal.model;

import com.google.gson.JsonObject;

/**
 * Created by songhw on 2016. 7. 26..
 */
public class Price {
    private double price, salePrice;
    private int salePercent;
    private boolean isSale, salePercentIsUptoValue;


    public Price( JsonObject jsonObject ) {
        price = jsonObject.has( "PRICE" ) ? jsonObject.get( "PRICE" ).getAsDouble() : 0;
        salePrice = jsonObject.has( "SALEPRICE" ) ? jsonObject.get( "SALEPRICE" ).getAsDouble() : 0;
        salePercent = jsonObject.has( "SALEIMG" ) ? jsonObject.get( "SALEIMG" ).getAsInt() : 0;
        isSale = jsonObject.has( "ISSALE" ) ? jsonObject.get( "ISSALE" ).getAsBoolean() : false;
        salePercentIsUptoValue = jsonObject.has( "SALEIMGISUPTO" ) ? jsonObject.get( "SALEIMGISUPTO" ).getAsBoolean() : false;
    }

    /**
     * getters
     */
    public double getPrice() { return price; }
    public double getSalePrice() { return salePrice; }
    public int getSalePercent() { return salePercent; }
    public boolean isSale() { return isSale; }
    public boolean isSalePercentIsUptoValue() { return salePercentIsUptoValue; }
}
