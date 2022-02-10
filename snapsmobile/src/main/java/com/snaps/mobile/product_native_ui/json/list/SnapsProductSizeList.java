package com.snaps.mobile.product_native_ui.json.list;

import com.google.gson.annotations.SerializedName;
import com.snaps.mobile.product_native_ui.json.SnapsProductNativeUIBaseResultJson;

import java.util.List;

/**
 * Created by ifunbae on 2016. 10. 14..
 */

public class SnapsProductSizeList extends SnapsProductNativeUIBaseResultJson {
    private static final long serialVersionUID = 1448120228242756470L;
    @SerializedName("list")
    private List<SnapsProductSizeItem> sizeList;

    public List<SnapsProductSizeItem> getSize() {
        return sizeList;
    }
}
