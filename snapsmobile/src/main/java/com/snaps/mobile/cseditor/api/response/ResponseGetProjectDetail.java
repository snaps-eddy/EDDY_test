package com.snaps.mobile.cseditor.api.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResponseGetProjectDetail {

//    {"projectCode":"20060717000341","productCode":"00800500020001","userNo":"","xmlPath":"","templateCode":"045001000003"}

    @SerializedName("projectCode")
    @Expose
    private String projectCode;

    @SerializedName("productCode")
    @Expose
    private String productCode;

    @SerializedName("templateCode")
    @Expose
    private String templateCode;

    @SerializedName("xmlPath")
    @Expose
    private String xmlPath;

    public String getProjectCode() {
        return projectCode;
    }

    public String getProductCode() {
        return productCode;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public String getXmlPath() {
        return xmlPath;
    }
}
