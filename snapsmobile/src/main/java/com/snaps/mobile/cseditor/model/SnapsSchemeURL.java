package com.snaps.mobile.cseditor.model;

import androidx.annotation.Nullable;

import com.snaps.common.utils.constant.Config;

import java.util.HashMap;

public class SnapsSchemeURL {

    private String projectCode;

    private String productCode;

    private String templateCode;

    public SnapsSchemeURL(String rawSchemeURL) {
        HashMap<String, String> map = Config.ExtractWebURL(rawSchemeURL);
        this.projectCode = map.get("prjCode");
        this.productCode = map.get("productCode");
        this.templateCode = map.get("templateCode");
    }

    public SnapsSchemeURL(String projectCode, String productCode, String templateCode) {
        this.projectCode = projectCode;
        this.productCode = productCode;
        this.templateCode = templateCode;
    }

    @Nullable
    public String getProjectCode() {
        return projectCode;
    }

    @Nullable
    public String getProductCode() {
        return productCode;
    }

    @Nullable
    public String getTemplateCode() {
        return templateCode;
    }

    public String getImpliedURL() {
        return "snapsapp://preview?dumy=&productCode=" + productCode +
                "&prjCode=" + projectCode +
                "&templateCode=" + templateCode +
                "&smartYN=Y&unitPrice=0";
    }
}
