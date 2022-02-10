package com.snaps.mobile.product_native_ui.ui.recoder;

import android.os.Parcel;
import android.os.Parcelable;

import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.product_native_ui.json.list.SnapsProductDesignItem;

import java.io.Serializable;

public class SnapsProductGridShapeListItem extends SnapsBaseProductListItem implements Parcelable, Serializable {
    private static final String TAG = SnapsProductGridShapeListItem.class.getSimpleName();
    private static final long serialVersionUID = -2226916461945986669L;

    private String thumbnail = null;
    private String largeThumbnail = null;
    private String contents = null;

    private String regDate = null;
    private String tmplCode = null;
    private String mClssCode = null;
    private String designId = null;
    private String tmplName = null;
    private int popularNumber = 999;
    private String prodCode = null;
    private String isOuterYN = null;
    private String isSimplePhotobookYN = null;

    public SnapsProductGridShapeListItem(SnapsProductDesignItem item) {
        if (item == null) return;
        itemType = ITEM_TYPE_ITEM;
        setThumbnail(item.getF_MOSMPL_URL());
        setLargeThumbnail(item.getF_LARGE_THUMBNAIL_URL());
        setContents(item.getF_TMPL_NAME());
        setRegDate(item.getF_REG_DATE());
        setTmplCode(item.getF_TMPL_CODE());
        setmClssCode(item.getF_MCLSS_CODE());
        setDesignId(item.getF_DESIGNER_ID());
        setTmplName(item.getF_TMPL_NAME());
        setPopularNumber(item.getROWNUM1());
        setProdCode(item.getF_PROD_CODE());
        setIsOuterYN(item.getF_OUTER_YORN());
        setIsSimplePhotobookYN(item.getSIMPLE_PHOTOBOOK());
    }

    public SnapsProductGridShapeListItem(int type) {
        itemType = type;
    }

    public String getLargeThumbnail() {
        return largeThumbnail;
    }

    public void setLargeThumbnail(String largeThumbnail) {
        this.largeThumbnail = largeThumbnail;
    }

    public String getRegDate() {
        return regDate;
    }

    public int getRegDateInteger() {
        if (regDate == null || regDate.length() < 1) return 0;
        try {
            return Integer.parseInt(regDate);
        } catch (NumberFormatException e) { Dlog.e(TAG, e); }
        return 0;
    }

    public void setRegDate(String regDate) {
        this.regDate = regDate;
    }

    public String getTmplCode() {
        return tmplCode;
    }

    public void setTmplCode(String tmplCode) {
        this.tmplCode = tmplCode;
    }

    public String getmClssCode() {
        return mClssCode;
    }

    public void setmClssCode(String mClssCode) {
        this.mClssCode = mClssCode;
    }

    public String getDesignId() {
        return designId;
    }

    public void setDesignId(String designId) {
        this.designId = designId;
    }

    public String getTmplName() {
        return tmplName;
    }

    public void setTmplName(String tmplName) {
        this.tmplName = tmplName;
    }

    public int getPopularNumber() {
        return popularNumber;
    }

    public void setPopularNumber(int popularNumber) {
        this.popularNumber = popularNumber;
    }

    public String getProdCode() {
        return prodCode;
    }

    public void setProdCode(String prodCode) {
        this.prodCode = prodCode;
    }

    public String getIsOuterYN() {
        return isOuterYN;
    }

    public void setIsOuterYN(String isOuterYN) {
        this.isOuterYN = isOuterYN;
    }

    public String getIsSimplePhotobookYN() {
        return isSimplePhotobookYN;
    }

    public void setIsSimplePhotobookYN(String isSimplePhotobookYN) {
        this.isSimplePhotobookYN = isSimplePhotobookYN;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public boolean isPremium() {
        return getIsOuterYN() != null && getIsOuterYN().equalsIgnoreCase("Y");
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {// !! Parcel 객체에 write 하는
        dest.writeString(thumbnail);
        dest.writeString(largeThumbnail);
        dest.writeString(contents);
        dest.writeString(regDate);
        dest.writeString(tmplCode);
        dest.writeString(mClssCode);
        dest.writeString(designId);
        dest.writeString(tmplName);
        dest.writeString(prodCode);
        dest.writeString(isOuterYN);
        dest.writeString(isSimplePhotobookYN);
        dest.writeInt(popularNumber);
    }

    private void readFromParcel(Parcel in) {
        thumbnail = in.readString();
        largeThumbnail = in.readString();
        contents = in.readString();
        regDate = in.readString();
        tmplCode = in.readString();
        mClssCode = in.readString();
        designId = in.readString();
        tmplName = in.readString();
        prodCode = in.readString();
        isOuterYN = in.readString();
        isSimplePhotobookYN = in.readString();
        popularNumber = in.readInt();
    }

    public SnapsProductGridShapeListItem(Parcel in) {
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @SuppressWarnings("rawtypes")
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        @Override
        public SnapsProductGridShapeListItem createFromParcel(Parcel in) {
            return new SnapsProductGridShapeListItem(in);
        }

        @Override
        public SnapsProductGridShapeListItem[] newArray(int size) {
            return new SnapsProductGridShapeListItem[size];
        }
    };
}
