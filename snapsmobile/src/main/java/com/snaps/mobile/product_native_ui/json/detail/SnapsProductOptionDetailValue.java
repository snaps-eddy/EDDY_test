package com.snaps.mobile.product_native_ui.json.detail;

import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.LinkedTreeMap;
import com.snaps.mobile.product_native_ui.interfaces.ISnapsProductOptionCellConstants;
import com.snaps.mobile.product_native_ui.json.SnapsProductNativeUIBaseResultJson;
import com.snaps.mobile.product_native_ui.string_switch.SnapsProductOptionDetailValueParser;

/**
 * Created by ifunbae on 2016. 10. 14..
 */

public class SnapsProductOptionDetailValue extends SnapsProductNativeUIBaseResultJson {

    private static final long serialVersionUID = 33669967687888519L;
    @SerializedName(ISnapsProductOptionCellConstants.KEY_PRODUCT_SIZE)
    private String productSize;
    @SerializedName(ISnapsProductOptionCellConstants.KEY_PHOTO_SIZE)
    private String photoSize;
    @SerializedName(ISnapsProductOptionCellConstants.KEY_PRODUCT_MATERIAL)
    private String productMaterial;
    @SerializedName(ISnapsProductOptionCellConstants.KEY_PRODUCT_VOLUME)
    private String productVolumn;
    @SerializedName(ISnapsProductOptionCellConstants.KEY_FRAME_SIZE)
    private String frameSize;
    @SerializedName(ISnapsProductOptionCellConstants.KEY_USE_IMAGE_CNT)
    private String useImageCnt;
    @SerializedName(ISnapsProductOptionCellConstants.KEY_ENABLEPAGE)
    private String enablePage;
    @SerializedName(ISnapsProductOptionCellConstants.KEY_PAGE_PRICE)
    private String page_price;
    @SerializedName(ISnapsProductOptionCellConstants.KEY_THUMBNAIL)
    private int thumbnail;
    @SerializedName(ISnapsProductOptionCellConstants.KEY_PROD_FORM)
    private String prodForm;
    @SerializedName(ISnapsProductOptionCellConstants.KEY_LEATHER_COVER)
    private String leatherCover;

    public SnapsProductOptionDetailValue() {}

    public SnapsProductOptionDetailValue clone() {
        SnapsProductOptionDetailValue temp = new SnapsProductOptionDetailValue();
        temp.productSize = productSize;
        temp.photoSize = photoSize;
        temp.productMaterial = productMaterial;
        temp.productVolumn = productVolumn;
        temp.frameSize = frameSize;
        temp.useImageCnt = useImageCnt;
        temp.enablePage = enablePage;
        temp.page_price = page_price;
        temp.thumbnail = thumbnail;
        temp.leatherCover = leatherCover;
        temp.prodForm = prodForm;
        return temp;
    }

    public void performStrParsingFromMap(final LinkedTreeMap dataMap) {
        if (dataMap == null) return;
        SnapsProductOptionDetailValueParser<SnapsProductOptionDetailValue> snapsSwitch = new SnapsProductOptionDetailValueParser<>(this, dataMap);
        snapsSwitch.perform();
    }

    public String getProductSize() {
        return productSize;
    }

    public void setProductSize(String productSize) {
        this.productSize = productSize;
    }

    public String getUseImageCnt() {
        return useImageCnt;
    }

    public void setUseImageCnt(String useImageCnt) {
        this.useImageCnt = useImageCnt;
    }

    public String getEnablePage() {
        return enablePage;
    }

    public void setEnablePage(String enablePage) {
        this.enablePage = enablePage;
    }

    public String getPage_price() { return page_price; }

    public String getPhotoSize() { return photoSize; }

    public void setPhotoSize(String photoSize) { this.photoSize = photoSize; }

    public void setPage_price(String page_price) {
        this.page_price = page_price;
    }

    public int getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(int thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getLeatherCover() {
        return leatherCover;
    }

    public void setLeatherCover(String leatherCover) {
        this.leatherCover = leatherCover;
    }

    public String getProdForm() { return prodForm; }

    public void setProdForm(String prodForm) { this.prodForm = prodForm; }

    public String getFrameSize() { return frameSize; }

    public void setFrameSize(String frameSize) { this.frameSize = frameSize; }

    public String getProductMaterial() { return productMaterial; }

    public void setProductMaterial(String productMaterial) { this.productMaterial = productMaterial; }

    public String getProductVolumn() { return productVolumn; }

    public void setProductVolumn(String productVolumn) { this.productVolumn = productVolumn; }
}
