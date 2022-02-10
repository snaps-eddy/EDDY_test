package com.snaps.mobile.product_native_ui.json.list;

import com.google.gson.annotations.SerializedName;
import com.snaps.mobile.product_native_ui.json.SnapsProductNativeUIBaseResultJson;

/**
 * Created by ifunbae on 2016. 10. 14..
 */

public class SnapsProductSizeItem extends SnapsProductNativeUIBaseResultJson {
    private static final long serialVersionUID = -5595548677500199112L;
    @SerializedName("cmd")
    private String CMD;
    @SerializedName("thumbnail")
    private String THUMBNAIL;
    @SerializedName("originalPrice")
    private String ORIGINALPRICE;
    @SerializedName("name")
    private String NAME;
    @SerializedName("size")
    private String SIZE;
    @SerializedName("discountPrice")
    private String DISCOUNTPRICE;

    private String parentClassCode;

    public String getParentClassCode() { return parentClassCode; }

    public void setParentClassCode(String parentClassCode) { this.parentClassCode = parentClassCode; }

    public String getCMD() {
        return CMD;
    }

    public void setCMD(String CMD) {
        this.CMD = CMD;
    }

    public String getTHUMBNAIL() {
        return THUMBNAIL;
    }

    public void setTHUMBNAIL(String THUMBNAIL) {
        this.THUMBNAIL = THUMBNAIL;
    }

    public String getORIGINALPRICE() {
        return ORIGINALPRICE;
    }

    public void setORIGINALPRICE(String ORIGINALPRICE) {
        this.ORIGINALPRICE = ORIGINALPRICE;
    }

    public String getNAME() {
        return NAME;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public String getSIZE() {
        return SIZE;
    }

    public void setSIZE(String SIZE) {
        this.SIZE = SIZE;
    }

    public String getDISCOUNTPRICE() {
        return DISCOUNTPRICE;
    }

    public void setDISCOUNTPRICE(String DISCOUNTPRICE) {
        this.DISCOUNTPRICE = DISCOUNTPRICE;
    }
}
