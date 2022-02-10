package com.snaps.mobile.activity.ui.menu.webinterface.web_event_handlers;

import android.app.Activity;
import android.content.Intent;

import com.snaps.common.utils.constant.Const_EKEY;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.pref.Setting;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.home.RenewalHomeActivity;
import com.snaps.mobile.activity.ui.menu.webinterface.SnapsShouldOverrideUrlLoader;
import com.snaps.mobile.activity.webview.UIWebviewActivity;
import com.snaps.mobile.activity.webview.WebViewCmdGotoPage;

import errorhandle.logger.Logg;

import static com.snaps.mobile.activity.ui.menu.webinterface.web_event_handlers.SnapsWebEventCartUrlHandler.CART_REQUEST_CODE_PAYMENT;

/**
 * Created by ysjeong on 16. 8. 12..
 */
public class SnapsWebEventPresentPaymentHandler extends SnapsWebEventBaseHandler {
    private static final String TAG = SnapsWebEventPresentPaymentHandler.class.getSimpleName();
    public SnapsWebEventPresentPaymentHandler(Activity activity, SnapsShouldOverrideUrlLoader.SnapsShouldHandleData handleDatas) {
        super(activity, handleDatas);
    }

    @Override
    public boolean handleEvent() {
        Intent intent = RenewalHomeActivity.getIntent(activity, activity.getString(R.string.payment_short), url);
        // 호출할 url를 만들어서 보낸다.
        String orderCode = urlData.get(Const_EKEY.WEB_ORDER_CODE_KEY);
        String sttlemethod = urlData.get(Const_EKEY.WEB_STTLEMETHOD_KEY);

        intent.putExtra(CALL_URL, createPaymentUrl(orderCode, sttlemethod));

        // 주문번호를 저장한다.
        Setting.set(activity.getApplicationContext(), "ordercode", orderCode);

        activity.startActivityForResult(intent, CART_REQUEST_CODE_PAYMENT);

        WebViewCmdGotoPage.gotoPage(activity, handleDatas);
        return true;
    }

    @Override
    public void printClassName() {
        Dlog.d("printClassName() class Name:" + getClass().getName());
    }
}
