package com.snaps.mobile.activity.ui.menu.webinterface.web_event_handlers;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by ysjeong on 2016. 10. 28..
 */

public class SnapsProductListParams implements Parcelable, Serializable {

    private static final long serialVersionUID = 3909156166192721580L;

    private String title = null;
    private String clssCode = null;
    private String mclssCode = null;
    private String sclssCode = null;
    private String templateCode = null;
    private String detailInterfaceUrl = "";
    private String prodCode = null;
    private String infoUrl = null;
    private boolean isOuter = false; //프리미엄 표시 여부
    private boolean isProductSubList = false; //달력, 사진인화 쪽에서 서브 리스트로 넘어가는 경우
    private boolean isPhotobook = false; // photobook 예외처리

    protected SnapsProductListParams(Parcel in) {
        title = in.readString();
        clssCode = in.readString();
        mclssCode = in.readString();
        sclssCode = in.readString();
        templateCode = in.readString();
        detailInterfaceUrl = in.readString();
        prodCode = in.readString();
        infoUrl = in.readString();
        isOuter = in.readByte() != 0;
        isProductSubList = in.readByte() != 0;
        isPhotobook = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(clssCode);
        dest.writeString(mclssCode);
        dest.writeString(sclssCode);
        dest.writeString(templateCode);
        dest.writeString(detailInterfaceUrl);
        dest.writeString(prodCode);
        dest.writeString(infoUrl);
        dest.writeByte((byte) (isOuter ? 1 : 0));
        dest.writeByte((byte) (isProductSubList ? 1 : 0));
        dest.writeByte((byte) (isPhotobook ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SnapsProductListParams> CREATOR = new Creator<SnapsProductListParams>() {
        @Override
        public SnapsProductListParams createFromParcel(Parcel in) {
            return new SnapsProductListParams(in);
        }

        @Override
        public SnapsProductListParams[] newArray(int size) {
            return new SnapsProductListParams[size];
        }
    };

    public String getProdCode() {
        return prodCode;
    }

    public void setProdCode(String prodCode) {
        this.prodCode = prodCode;
    }

    public String getClssCode() {
        return clssCode;
    }

    public void setClssCode(String clssCode) {
        this.clssCode = clssCode;
    }

    public String getMclssCode() {
        return mclssCode;
    }

    public void setMclssCode(String mclssCode) {
        this.mclssCode = mclssCode;
    }

    public String getSclssCode() {
        return sclssCode;
    }

    public void setSclssCode(String sclssCode) {
        this.sclssCode = sclssCode;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    public boolean isOuter() {
        return isOuter;
    }

    public void setOuter(boolean outer) {
        isOuter = outer;
    }

    public boolean isProductSubList() {
        return isProductSubList;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetailInterfaceUrl() { return detailInterfaceUrl; }

    public void setDetailInterfaceUrl(String detailInterfaceUrl) { this.detailInterfaceUrl = detailInterfaceUrl; }

    public boolean isPhotobook() { return isPhotobook; }

    public void setPhotobook(boolean photobook) { isPhotobook = photobook; }

    public void setProductSubList(String isSubList) {
        isProductSubList = isSubList != null && isSubList.equalsIgnoreCase("true");
    }

    public String getInfoUrl() { return infoUrl; }

    public void setInfoUrl(String infoUrl) { this.infoUrl = infoUrl; }

    public SnapsProductListParams() {}
}
