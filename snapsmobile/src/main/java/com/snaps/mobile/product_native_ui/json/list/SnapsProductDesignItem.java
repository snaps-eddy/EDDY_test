package com.snaps.mobile.product_native_ui.json.list;

import com.google.gson.annotations.SerializedName;
import com.snaps.mobile.product_native_ui.json.SnapsProductNativeUIBaseResultJson;

/**
 * Created by ifunbae on 2016. 10. 14..
 */

public class SnapsProductDesignItem extends SnapsProductNativeUIBaseResultJson {
    private static final long serialVersionUID = 9191925960898359176L;
    @SerializedName("regDate")
    private String F_REG_DATE;
    @SerializedName("tmplCode")
    private String F_TMPL_CODE;
    @SerializedName("img320Url")
    private String F_MOSMPL_URL;
    @SerializedName("img656Url")
    private String F_LARGE_THUMBNAIL_URL;
    @SerializedName("mclssCode")
    private String F_MCLSS_CODE;
    @SerializedName("designerId")
    private String F_DESIGNER_ID;
    @SerializedName("tmplName")
    private String F_TMPL_NAME;
    @SerializedName("no")
    private int ROWNUM1;
    @SerializedName("outerYorn")
    private String F_OUTER_YORN;
    @SerializedName("prodCode")
    private String F_PROD_CODE;
    @SerializedName("simplePhotobook")
    private String SIMPLE_PHOTOBOOK;

    public String getF_LARGE_THUMBNAIL_URL() {
        return F_LARGE_THUMBNAIL_URL;
    }

    public void setF_LARGE_THUMBNAIL_URL(String f_LARGE_THUMBNAIL_URL) {
        F_LARGE_THUMBNAIL_URL = f_LARGE_THUMBNAIL_URL;
    }

    public String getF_REG_DATE() {
        return F_REG_DATE;
    }

    public void setF_REG_DATE(String f_REG_DATE) {
        F_REG_DATE = f_REG_DATE;
    }

    public String getF_TMPL_CODE() {
        return F_TMPL_CODE;
    }

    public void setF_TMPL_CODE(String f_TMPL_CODE) {
        F_TMPL_CODE = f_TMPL_CODE;
    }

    public String getF_MOSMPL_URL() {
        return F_MOSMPL_URL;
    }

    public void setF_MOSMPL_URL(String f_MOSMPL_URL) {
        F_MOSMPL_URL = f_MOSMPL_URL;
    }

    public String getF_MCLSS_CODE() {
        return F_MCLSS_CODE;
    }

    public void setF_MCLSS_CODE(String f_MCLSS_CODE) {
        F_MCLSS_CODE = f_MCLSS_CODE;
    }

    public String getF_DESIGNER_ID() {
        return F_DESIGNER_ID;
    }

    public void setF_DESIGNER_ID(String f_DESIGNER_ID) {
        F_DESIGNER_ID = f_DESIGNER_ID;
    }

    public String getF_TMPL_NAME() {
        return F_TMPL_NAME;
    }

    public void setF_TMPL_NAME(String f_TMPL_NAME) {
        F_TMPL_NAME = f_TMPL_NAME;
    }

    public int getROWNUM1() {
        return ROWNUM1;
    }

    public void setROWNUM1(int ROWNUM1) {
        this.ROWNUM1 = ROWNUM1;
    }

    public String getF_OUTER_YORN() {
        return F_OUTER_YORN;
    }

    public void setF_OUTER_YORN(String f_OUTER_YORN) {
        F_OUTER_YORN = f_OUTER_YORN;
    }

    public String getF_PROD_CODE() {
        return F_PROD_CODE;
    }

    public void setF_PROD_CODE(String f_PROD_CODE) {
        F_PROD_CODE = f_PROD_CODE;
    }

    public String getSIMPLE_PHOTOBOOK() {
        return SIMPLE_PHOTOBOOK;
    }

    public void setSIMPLE_PHOTOBOOK(String SIMPLE_PHOTOBOOK) {
        this.SIMPLE_PHOTOBOOK = SIMPLE_PHOTOBOOK;
    }
}
