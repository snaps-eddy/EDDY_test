package com.snaps.mobile.product_native_ui.json.detail;

import com.google.gson.annotations.SerializedName;
import com.snaps.mobile.product_native_ui.json.SnapsProductNativeUIBaseResultJson;

import java.util.List;

/**
 * Created by ifunbae on 2016. 10. 14..
 */

public class SnapsProductThumbnailItem extends SnapsProductNativeUIBaseResultJson {

    private static final long serialVersionUID = 5727532376069678405L;
    @SerializedName("comment")
    private String comment;

    @SerializedName("prodForm")
    private String prodForm;

    @SerializedName("size")
    private List<Integer> size;

    @SerializedName("items")
    private List<String> items;

    @SerializedName("skin")
    private List<String> skin;

    @SerializedName("leatherCover")
    private List<?> leatherCover;

    @SerializedName("zoomUrl")
    private String zoomUrl;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<Integer> getSize() {
        return size;
    }

    public void setSize(List<Integer> size) {
        this.size = size;
    }

    public List<String> getItems() {
        return items;
    }

    public void setItems(List<String> items) {
        this.items = items;
    }

    public List<String> getSkin() {
        return skin;
    }

    public void setSkin(List<String> skin) {
        this.skin = skin;
    }

    public String getZoomUrl() {
        return zoomUrl;
    }

    public void setZoomUrl(String zoomUrl) {
        this.zoomUrl = zoomUrl;
    }

    public List<?> getLeatherCover() {
        return leatherCover;
    }

    public void setLeatherCover(List<?> leatherCover) {
        this.leatherCover = leatherCover;
    }

    public String getProdForm() { return prodForm; }

    public void setProdForm(String prodForm) { this.prodForm = prodForm; }
}
