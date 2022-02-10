package com.snaps.mobile.activity.ui.menu.webinterface.web_event_handlers;

import android.app.Activity;

import com.snaps.common.utils.constant.Const_EKEY;
import com.snaps.common.utils.constant.Const_VALUE;
import errorhandle.logger.Logg;

import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.pref.Setting;
import com.snaps.mobile.activity.ui.menu.webinterface.SnapsShouldOverrideUrlLoader;
import com.snaps.mobile.activity.webview.WebViewCmdGotoPage;
import com.snaps.mobile.utils.pref.PrefUtil;

/**
 * Created by ysjeong on 16. 8. 12..
 */
public class SnapsWebEventCountHandler extends SnapsWebEventBaseHandler {
    private static final String TAG = SnapsWebEventCountHandler.class.getSimpleName();

    public SnapsWebEventCountHandler(Activity activity, SnapsShouldOverrideUrlLoader.SnapsShouldHandleData handleDatas) {
        super(activity, handleDatas);
    }

    @Override
    public boolean handleEvent() {
        try {
            String cartCount = urlData.get(Const_EKEY.WEB_CARTCOUNT_KEY);
            if (cartCount != null && cartCount.length() > 0) {
                PrefUtil.saveCartCount(activity, cartCount);
            }

            String couponCount = urlData.get(Const_EKEY.WEB_COUPON_COUNT_KEY);
            if (couponCount != null && couponCount.length() > 0)
                Setting.set(activity, Const_VALUE.KEY_COUPON_COUNT, Integer.parseInt(couponCount));
        } catch (NumberFormatException e) {
            Dlog.e(TAG, e);
        }

        WebViewCmdGotoPage.gotoPage(activity, handleDatas);
        return true;
    }

    @Override
    public void printClassName() {
        Dlog.d("printClassName() class name:" + getClass().getName());
    }
}
