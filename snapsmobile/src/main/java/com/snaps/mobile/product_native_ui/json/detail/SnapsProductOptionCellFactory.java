package com.snaps.mobile.product_native_ui.json.detail;

import com.google.gson.internal.LinkedTreeMap;

import java.util.List;

/**
 * Created by ifunbae on 2016. 10. 14..
 */

public class SnapsProductOptionCellFactory {

    public static SnapsProductOptionCell createCell(List<LinkedTreeMap> values, LinkedTreeMap myData) {
        return new SnapsProductOptionCell(values, myData);
    }

    public static SnapsProductOptionCell createCell(List<LinkedTreeMap> values, SnapsProductOption base) {
        return new SnapsProductOptionCell(values, base);
    }
}
