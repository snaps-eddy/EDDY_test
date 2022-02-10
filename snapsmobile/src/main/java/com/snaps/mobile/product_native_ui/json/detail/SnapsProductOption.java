package com.snaps.mobile.product_native_ui.json.detail;

import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.LinkedTreeMap;
import com.snaps.mobile.product_native_ui.json.SnapsProductNativeUIBaseResultJson;

import java.util.List;

/**
 * Created by ifunbae on 2016. 10. 14..
 */

public class SnapsProductOption extends SnapsProductNativeUIBaseResultJson {
    private static final long serialVersionUID = 7703641599923275939L;
    @SerializedName("title")
    private String title;
    @SerializedName("type")
    private String type;
    @SerializedName("name")
    private String name;
    @SerializedName("default_index")
    private String defalut_index;
    @SerializedName("cellType")
    private String cellType;
    @SerializedName("prodForm")
    private String prodForm;
    @SerializedName("innerPaperKind")
    private String innerPaperKind;
    @SerializedName("parameter")
    private String parameter;

    @SerializedName("value")
    private List<LinkedTreeMap> values;

    @SerializedName("max")
    private int max;
    @SerializedName("min")
    private int min;

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public List<LinkedTreeMap> getValues() {
        return values;
    }

    public void setValues(List<LinkedTreeMap> values) {
        this.values = values;
    }

    public String getDefalut_index() {
        return defalut_index;
    }

    public void setDefalut_index(String defalut_index) {
        this.defalut_index = defalut_index;
    }

    public String getParameter() { return parameter; }

    public void setParameter(String parameter) { this.parameter = parameter; }

    public String getInnerPaperKind() { return innerPaperKind; }

    public void setInnerPaperKind(String innerPaperKind) { this.innerPaperKind = innerPaperKind; }
}
