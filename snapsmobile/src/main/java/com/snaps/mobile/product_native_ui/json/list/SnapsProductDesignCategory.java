package com.snaps.mobile.product_native_ui.json.list;

import com.google.gson.annotations.SerializedName;
import com.snaps.mobile.product_native_ui.json.SnapsProductNativeUIBaseResultJson;

import java.util.List;

/**
 * Created by ifunbae on 2016. 10. 14..
 */

public class SnapsProductDesignCategory extends SnapsProductNativeUIBaseResultJson {
    private static final long serialVersionUID = 5906450028838886248L;
    @SerializedName("NEW")
    private boolean NEW;
    @SerializedName("categoryName")
    private String CATEGORY_NAME;
    @SerializedName("viewType")
    private String VIEW_TYPE;
    @SerializedName("item")
    private List<SnapsProductDesignItem> ITEMS;

    public boolean isNEW() {
        return NEW;
    }

    public void setNEW(boolean NEW) {
        this.NEW = NEW;
    }

    public String getCATEGORY_NAME() {
        return CATEGORY_NAME;
    }

    public void setCATEGORY_NAME(String CATEGORY_NAME) {
        this.CATEGORY_NAME = CATEGORY_NAME;
    }

    public void setVIEW_TYPE(String VIEW_TYPE) {
        this.VIEW_TYPE = VIEW_TYPE;
    }

    public String getVIEW_TYPE() {
        return VIEW_TYPE;
    }
    public List<SnapsProductDesignItem> getITEMS() {
        return ITEMS;
    }

    public void setITEMS(List<SnapsProductDesignItem> ITEMS) {
        this.ITEMS = ITEMS;
    }
}
