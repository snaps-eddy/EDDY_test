package com.snaps.common.http;

import com.google.gson.annotations.SerializedName;

public class AreaPriceEntity {

    @SerializedName("price")
    private float price;

    @SerializedName("sellPrice")
    private float sellPrice;

    @SerializedName("discountRate")
    private float discountRate;

    public float getPrice() {
        return price;
    }

    public float getSellPrice() {
        return sellPrice;
    }

    public float getDiscountRate() {
        return discountRate;
    }
}
