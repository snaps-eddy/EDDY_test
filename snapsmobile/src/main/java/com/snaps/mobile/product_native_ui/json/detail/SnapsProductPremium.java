package com.snaps.mobile.product_native_ui.json.detail;

import com.google.gson.annotations.SerializedName;
import com.snaps.mobile.product_native_ui.json.SnapsProductNativeUIBaseResultJson;

import java.util.List;

/**
 * Created by ifunbae on 2016. 10. 14..
 */

public class SnapsProductPremium extends SnapsProductNativeUIBaseResultJson {
    private static final long serialVersionUID = 1818080524615564672L;
    @SerializedName("items")
    private List<SnapsProductPremiumItem> items;

    public List<SnapsProductPremiumItem> getItems() {
        return items;
    }

    public void setItems(List<SnapsProductPremiumItem> items) {
        this.items = items;
    }
}
