package com.snaps.mobile.activity.ui.menu.webinterface.web_event_handlers;

import android.app.Activity;
import android.content.Intent;

import errorhandle.logger.Logg;

import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.activity.home.RenewalHomeActivity;
import com.snaps.mobile.activity.ui.menu.webinterface.SnapsShouldOverrideUrlLoader;
import com.snaps.mobile.activity.webview.WebViewCmdGotoPage;
import com.snaps.mobile.utils.pref.PrefUtil;

/**
 * Created by ysjeong on 16. 8. 12..
 */
public class SnapsWebEventAppFinishHandler extends SnapsWebEventBaseHandler {
    private static final String TAG = SnapsWebEventAppFinishHandler.class.getSimpleName();
    public SnapsWebEventAppFinishHandler(Activity activity, SnapsShouldOverrideUrlLoader.SnapsShouldHandleData handleDatas) {
        super(activity, handleDatas);
    }

    @Override
    public boolean handleEvent() {
        Intent ittLogin = new Intent(activity, RenewalHomeActivity.class);
        PrefUtil.clearUserInfo(activity, true);// 모든정보 초기화
        activity.startActivity(ittLogin);

        if (activity != null && !(activity instanceof RenewalHomeActivity))
            activity.finish();

        WebViewCmdGotoPage.gotoPage(activity, handleDatas);
        return true;
    }

    @Override
    public void printClassName() {
        Dlog.d("printClassName() class name:" + getClass().getName());
    }
}
