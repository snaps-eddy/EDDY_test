package com.snaps.common.structure.vo;

import com.google.gson.annotations.SerializedName;

public class AccessoriesOption {

    @SerializedName("productCode")
    String productCode;

    @SerializedName("templateCode")
    String templateCode;

    @SerializedName("quantity")
    int quantity;

    public String getProductCode() {
        return productCode;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public int getQuantity() {
        return quantity;
    }


}
