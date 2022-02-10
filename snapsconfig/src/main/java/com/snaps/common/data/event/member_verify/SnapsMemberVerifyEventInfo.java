package com.snaps.common.data.event.member_verify;

import java.util.LinkedHashMap;

public class SnapsMemberVerifyEventInfo {
    private LinkedHashMap<String, String> coupons = null;
    private String title;
    private String authPopImage;
    private boolean shouldShowCouponUI = false;

    public LinkedHashMap<String, String> getCoupons() {
        return coupons;
    }

    public void setCoupons(LinkedHashMap<String, String> coupons) {
        this.coupons = coupons;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthPopImage() {
        return authPopImage;
    }

    public void setAuthPopImage(String authPopImage) {
        this.authPopImage = authPopImage;
    }

    public boolean shouldShowCouponUI() {
        return shouldShowCouponUI;
    }

    public void setShouldShowCouponUI(boolean shouldShowCouponUI) {
        this.shouldShowCouponUI = shouldShowCouponUI;
    }
}
