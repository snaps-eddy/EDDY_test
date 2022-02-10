package com.snaps.mobile.activity.home.model;

/**
 * Created by ysjeong on 2017. 8. 17..
 */

public class HomeUIData {
    private int _cart_count = 0;
    private int _coupon_count = 0;
    private int _myartwork_count = 0;
    private int _notice_count = 0;
    private int currentHome = 0;

    public int get_cart_count() {
        return _cart_count;
    }

    public void set_cart_count(int _cart_count) {
        this._cart_count = _cart_count;
    }

    public int get_coupon_count() {
        return _coupon_count;
    }

    public void set_coupon_count(int _coupon_count) {
        this._coupon_count = _coupon_count;
    }

    public int get_myartwork_count() {
        return _myartwork_count;
    }

    public void set_myartwork_count(int _myartwork_count) {
        this._myartwork_count = _myartwork_count;
    }

    public int get_notice_count() {
        return _notice_count;
    }

    public void set_notice_count(int _notice_count) {
        this._notice_count = _notice_count;
    }

    public int getCurrentHome() {
        return currentHome;
    }

    public void setCurrentHome(int currentHome) {
        this.currentHome = currentHome;
    }
}
