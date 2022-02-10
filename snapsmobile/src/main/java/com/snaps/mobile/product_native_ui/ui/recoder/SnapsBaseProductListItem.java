package com.snaps.mobile.product_native_ui.ui.recoder;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class SnapsBaseProductListItem implements Parcelable, Serializable {
    private static final long serialVersionUID = -2226916461945986669L;

    public static final int ITEM_TYPE_SORT_HEADER = 0;
    public static final int ITEM_TYPE_ITEM = 1;
    public static final int ITEM_TYPE_DUMMY = 2;

    protected int itemType = ITEM_TYPE_ITEM;
    private boolean select = false;

    protected SnapsBaseProductListItem(Parcel in) {
        itemType = in.readInt();
        select = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(itemType);
        dest.writeByte((byte) (select ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SnapsBaseProductListItem> CREATOR = new Creator<SnapsBaseProductListItem>() {
        @Override
        public SnapsBaseProductListItem createFromParcel(Parcel in) {
            return new SnapsBaseProductListItem(in);
        }

        @Override
        public SnapsBaseProductListItem[] newArray(int size) {
            return new SnapsBaseProductListItem[size];
        }
    };

    public boolean isHeader() {
        return itemType == ITEM_TYPE_SORT_HEADER;
    }



    public SnapsBaseProductListItem() {
        this.itemType = ITEM_TYPE_ITEM;
    }

    public SnapsBaseProductListItem(int type) {
        this.itemType = type;
    }

    public boolean isDummyItem() {
        return itemType == ITEM_TYPE_DUMMY;
    }

    public int getItemType() {
        return itemType;
    }


    public void setSelect(boolean select) {
        this.select = select;
    }
    public boolean isSelect() {
        return select;
    }


}
