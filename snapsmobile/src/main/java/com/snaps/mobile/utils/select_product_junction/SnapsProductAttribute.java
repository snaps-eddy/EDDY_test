package com.snaps.mobile.utils.select_product_junction;

import com.snaps.common.utils.ui.IFacebook;
import com.snaps.common.utils.ui.IKakao;
import com.snaps.mobile.activity.photoprint.PhotoPrintProductInfo;
import com.snaps.mobile.activity.ui.menu.webinterface.SnapsShouldOverrideUrlLoader;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ysjeong on 2016. 11. 24..
 */

public class SnapsProductAttribute {
    private String prodKey;
    private HashMap<String, String> urlData;
    private ArrayList<PhotoPrintProductInfo> photoPrintDataList;
    private IKakao kakao;
    private IFacebook facebook;
    private SnapsShouldOverrideUrlLoader.SnapsShouldHandleData handleDatas;

    private SnapsProductAttribute(Builder builder) {
        this.prodKey = builder.prodKey;
        this.urlData = builder.urlData;
        this.photoPrintDataList = builder.photoPrintProductInfos;
        this.kakao = builder.kakao;
        this.facebook = builder.facebook;
        this.handleDatas = builder.handleDatas;
    }

    public SnapsShouldOverrideUrlLoader.SnapsShouldHandleData getHandleDatas() {
        return handleDatas;
    }

    public IKakao getKakao() {
        return kakao;
    }

    public IFacebook getFacebook() {
        return facebook;
    }

    public String getProdKey() {
        return prodKey;
    }

    public HashMap<String, String> getUrlData() {
        return urlData;
    }

    public ArrayList<PhotoPrintProductInfo> getPhotoPrintDataList() {
        return photoPrintDataList;
    }

    public void setPhotoPrintDataList(ArrayList<PhotoPrintProductInfo> photoPrintDataList) {
        this.photoPrintDataList = photoPrintDataList;
    }

    public static class Builder {
        private String prodKey;
        private HashMap<String, String> urlData;
        private ArrayList<PhotoPrintProductInfo> photoPrintProductInfos;
        private IKakao kakao;
        private IFacebook facebook;
        private SnapsShouldOverrideUrlLoader.SnapsShouldHandleData handleDatas;

        public Builder setHandleDatas(SnapsShouldOverrideUrlLoader.SnapsShouldHandleData handleDatas) {
            this.handleDatas = handleDatas;
            return this;
        }

        public Builder setProdKey(String prodKey) {
            this.prodKey = prodKey;
            return this;
        }

        public Builder setUrlData(HashMap<String, String> urlData) {
            this.urlData = urlData;
            return this;
        }

        public Builder setPhotoPrintDataList(ArrayList<PhotoPrintProductInfo> mData) {
            this.photoPrintProductInfos = mData;
            return this;
        }

        public Builder setKakao(IKakao kakao) {
            this.kakao = kakao;
            return this;
        }

        public Builder setFacebook(IFacebook facebook) {
            this.facebook = facebook;
            return this;
        }

        public SnapsProductAttribute create() {
            return new SnapsProductAttribute(this);
        }
    }
}
