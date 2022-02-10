package com.snaps.mobile.product_native_ui.json.detail;

import com.google.gson.annotations.SerializedName;
import com.snaps.mobile.product_native_ui.json.SnapsProductNativeUIBaseResultJson;

import java.util.List;

/**
 * Created by ifunbae on 2016. 10. 14..
 */

public class SnapsProductRoot extends SnapsProductNativeUIBaseResultJson {
    private static final long serialVersionUID = 1201155400436179339L;
    @SerializedName("thumnail")
    private SnapsProductThumbnail thumnail;
    @SerializedName("normalOption")
    private List<SnapsProductNormalOption> normalOptionList;
    @SerializedName("productOption")
    private SnapsProductOption productOption;
    @SerializedName("detail")
    private SnapsProductDetail detail;
    @SerializedName("premium")
    private SnapsProductPremium premium;

    private SnapsProductOptionCell productOptionControl;

    public SnapsProductThumbnail getThumnail() {
        return thumnail;
    }

    public void setThumnail(SnapsProductThumbnail thumnail) {
        this.thumnail = thumnail;
    }

    public List<SnapsProductNormalOption> getNormalOptionList() {
        return normalOptionList;
    }

    public void setNormalOptionList(List<SnapsProductNormalOption> normalOptionList) {
        this.normalOptionList = normalOptionList;
    }

    public SnapsProductOption getProductOption() {
        return productOption;
    }

    public void setProductOption(SnapsProductOption productOption) {
        this.productOption = productOption;
    }

    public SnapsProductDetail getDetail() {
        return detail;
    }

    public void setDetail(SnapsProductDetail detail) {
        this.detail = detail;
    }

    public SnapsProductPremium getPremium() {
        return premium;
    }

    public void setPremium(SnapsProductPremium premium) {
        this.premium = premium;
    }

    public SnapsProductOptionBaseCell getProductOptionControl() {
        return productOptionControl;
    }

    public void setProductOptionControl(SnapsProductOptionCell productOptionControl) {
        this.productOptionControl = productOptionControl;
    }

    public void createProductOptionControls() {
        SnapsProductOption option = getProductOption();
        if (option == null) return;

        setProductOptionControl(SnapsProductOptionCellFactory.createCell(option.getValues(), option));
    }
}
