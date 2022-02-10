package com.snaps.mobile.product_native_ui.json.detail;

/**
 * Created by ifunbae on 2016. 10. 14..
 */

public class SnapsProductOptionCommonValue {
    private String name;
    private String prodForm;
    private String cmd;
    private int max;
    private int min;
    private SnapsProductOptionDetailValue detailValue;
    private SnapsProductOptionPrice price;

    private SnapsProductOptionBaseCell childControl;

    public SnapsProductOptionCommonValue() {}

    public SnapsProductOptionCommonValue clone() {
        SnapsProductOptionCommonValue temp = new SnapsProductOptionCommonValue();
        temp.name = name;
        temp.prodForm = prodForm;
        temp.cmd = cmd;
        temp.max = max;
        temp.min = min;
        temp.detailValue = detailValue == null ? null : detailValue.clone();
        temp.price = price == null ? null : price;
        return temp;
    }

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

    public SnapsProductOptionPrice getPrice() {
        return price;
    }

    public void setPrice(SnapsProductOptionPrice price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) { this.cmd = cmd; }

    public String getProdForm() { return prodForm; }

    public void setProdForm(String prodForm) { this.prodForm = prodForm; }

    public SnapsProductOptionDetailValue getDetailValue() {
        return detailValue;
    }

    public void setDetailValue(SnapsProductOptionDetailValue detailValue) {
        this.detailValue = detailValue;
    }

    public void setChildControl(SnapsProductOptionBaseCell child) {
        childControl = child;
    }

    public boolean hasChild() {
        return childControl != null;
    }

    public SnapsProductOptionBaseCell getChildControl() {
        return childControl;
    }
}
