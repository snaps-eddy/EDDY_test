package com.snaps.mobile.product_native_ui.json.detail;

import com.google.gson.annotations.SerializedName;
import com.snaps.mobile.product_native_ui.json.SnapsProductNativeUIBaseResultJson;

import java.util.List;

/**
 * Created by ifunbae on 2016. 10. 14..
 */

public class SnapsProductDetail extends SnapsProductNativeUIBaseResultJson {
    private static final long serialVersionUID = 7593324132289148844L;
    @SerializedName("title")
    private String title;
    @SerializedName("items")
    private List<SnapsProductDetailItem> items;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<SnapsProductDetailItem> getItems() {
        return items;
    }

    public void setItems(List<SnapsProductDetailItem> items) {
        this.items = items;
    }
}
