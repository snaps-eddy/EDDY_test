package com.snaps.mobile.product_native_ui.ui.recoder;

import android.os.Parcel;
import android.os.Parcelable;

import com.snaps.mobile.product_native_ui.json.list.SnapsProductSizeItem;

import java.io.Serializable;

public class SnapsProductPriceListItem extends SnapsBaseProductListItem implements Parcelable, Serializable {
    private static final long serialVersionUID = -2226916461945986669L;

    private String cmd = null;
    private String thumbnail = null;
    private String orgPrice = null;
    private String name = null;
    private String size = null;
    private String discountPrice = null;

    public SnapsProductPriceListItem(SnapsProductSizeItem item) {
        if (item == null) return;
        itemType = ITEM_TYPE_ITEM;
        setCmd(item.getCMD());
        setThumbnail(item.getTHUMBNAIL());
        setOrgPrice(item.getORIGINALPRICE());
        setName(item.getNAME());
        setSize(item.getSIZE());
        setDiscountPrice(item.getDISCOUNTPRICE());
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getOrgPrice() {
        return orgPrice;
    }

    public void setOrgPrice(String orgPrice) {
        this.orgPrice = orgPrice;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(String discountPrice) {
        this.discountPrice = discountPrice;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {// !! Parcel 객체에 write 하는
        dest.writeString(cmd);
        dest.writeString(thumbnail);
        dest.writeString(orgPrice);
        dest.writeString(name);
        dest.writeString(size);
        dest.writeString(discountPrice);
    }

    private void readFromParcel(Parcel in) {
        cmd = in.readString();
        thumbnail = in.readString();
        orgPrice = in.readString();
        name = in.readString();
        size = in.readString();
        discountPrice = in.readString();
    }

    public SnapsProductPriceListItem(Parcel in) {
        readFromParcel(in);
    }

    public SnapsProductPriceListItem(int type) {
        itemType = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @SuppressWarnings("rawtypes")
    public static final Creator CREATOR = new Creator() {

        @Override
        public SnapsProductPriceListItem createFromParcel(Parcel in) {
            return new SnapsProductPriceListItem(in);
        }

        @Override
        public SnapsProductPriceListItem[] newArray(int size) {
            return new SnapsProductPriceListItem[size];
        }
    };
}
