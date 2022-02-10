package com.snaps.mobile.product_native_ui.json.detail;

import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.LinkedTreeMap;
import com.snaps.mobile.product_native_ui.json.SnapsProductNativeUIBaseResultJson;
import com.snaps.mobile.product_native_ui.string_switch.SnapsProductOptionPriceParser;

/**
 * Created by ifunbae on 2016. 10. 14..
 */

public class SnapsProductOptionPrice extends SnapsProductNativeUIBaseResultJson {
    private static final long serialVersionUID = 973702583215033881L;
    @SerializedName("name")
    private String name;
    @SerializedName("cellType")
    private String cellType;
    @SerializedName("attribute")
    private String attribute;
    @SerializedName("value")
    private SnapsProductOptionPriceValue values;
    @SerializedName("link")
    private String link;
    @SerializedName("linkText")
    private String linkText;
    @SerializedName("infoText")
    private String infoText;

    public SnapsProductOptionPrice() {}

    public SnapsProductOptionPrice clone() {
        SnapsProductOptionPrice temp = new SnapsProductOptionPrice();
        temp.name = name;
        temp.cellType = cellType;
        temp.attribute = attribute;
        temp.values = values == null ? null : values.clone();
        temp.link = link;
        temp.linkText = linkText;
        temp.infoText = infoText;
        return temp;
    }

    public void performStrParsingFromMap(final LinkedTreeMap dataMap) {
        if (dataMap == null) return;
        SnapsProductOptionPriceParser<SnapsProductOptionPrice> snapsSwitch = new SnapsProductOptionPriceParser<>(this, dataMap);
        snapsSwitch.perform();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCellType() {
        return cellType;
    }

    public void setCellType(String cellType) {
        this.cellType = cellType;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public SnapsProductOptionPriceValue getValues() {
        return values;
    }

    public void setValues(SnapsProductOptionPriceValue values) {
        this.values = values;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getLinkText() {
        return linkText;
    }

    public void setLinkText(String linkText) {
        this.linkText = linkText;
    }

    public void setInfoText(String infoText) {
        this.infoText = infoText;
    }

    public String getInfoText() {
        return infoText;
    }
}
