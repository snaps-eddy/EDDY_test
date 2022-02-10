package com.snaps.mobile.product_native_ui.json.detail;

import com.google.gson.annotations.SerializedName;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.product_native_ui.json.SnapsProductNativeUIBaseResultJson;

/**
 * Created by ifunbae on 2016. 10. 14..
 */

public class SnapsProductNormalOptionItemValue extends SnapsProductNativeUIBaseResultJson {
    private static final long serialVersionUID = -1527159037262775216L;
    @SerializedName("name")
    private String name;
    @SerializedName("value")
    private String value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) { this.value = value; }

    public boolean isEmpty() {
        return StringUtil.isEmpty( name ) && StringUtil.isEmpty( value );
    }
}
