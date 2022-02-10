package com.snaps.mobile.product_native_ui.json.detail;

import com.google.gson.annotations.SerializedName;
import com.snaps.mobile.product_native_ui.json.SnapsProductNativeUIBaseResultJson;

/**
 * Created by ifunbae on 2016. 10. 14..
 */

public class SnapsProductOptionPriceValue extends SnapsProductNativeUIBaseResultJson {
    private static final long serialVersionUID = 874118666197515665L;
    @SerializedName("salePercent")
    private String salePerscent;
    @SerializedName("discountPrice")
    private String discountPrice;
    @SerializedName("price")
    private String price;

    public SnapsProductOptionPriceValue() {}

    public SnapsProductOptionPriceValue clone() {
        SnapsProductOptionPriceValue temp = new SnapsProductOptionPriceValue();
        temp.salePerscent = salePerscent;
        temp.discountPrice = discountPrice;
        temp.price = price;
        return temp;
    }

    public String getSalePerscent() {
        return salePerscent;
    }

    public void setSalePerscent(String salePerscent) {
        this.salePerscent = salePerscent;
    }

    public String getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(String discountPrice) {
        this.discountPrice = discountPrice;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
