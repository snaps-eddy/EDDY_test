package com.snaps.mobile.product_native_ui.json.detail;

import com.google.gson.annotations.SerializedName;
import com.snaps.mobile.product_native_ui.json.SnapsProductNativeUIBaseResultJson;

import java.util.List;

/**
 * Created by ifunbae on 2016. 10. 14..
 */

public class SnapsProductDetailItem extends SnapsProductNativeUIBaseResultJson {
    private static final long serialVersionUID = -9020805025220371464L;
    @SerializedName("name")
    private String name;
    @SerializedName("cellType")
    private String cellType;
    @SerializedName("attribute")
    private String attribute;
    @SerializedName("linkText")
    private String linkText;
    @SerializedName("linkUrl")
    private String linkUrl;
    @SerializedName("value")
    private String value;
    @SerializedName("values")
    private List<String> values;

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

    public String getLinkText() {
        return linkText;
    }

    public void setLinkText(String linkText) {
        this.linkText = linkText;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<String> getValues() { return values; }
}
