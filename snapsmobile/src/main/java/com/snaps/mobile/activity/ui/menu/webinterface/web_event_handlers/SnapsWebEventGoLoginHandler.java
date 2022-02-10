package com.snaps.mobile.activity.ui.menu.webinterface.web_event_handlers;

import android.app.Activity;
import android.os.Bundle;

import com.snaps.common.utils.constant.Const_VALUES;
import errorhandle.logger.Logg;

import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.activity.home.utils.SnapsLoginManager;
import com.snaps.mobile.activity.ui.menu.webinterface.SnapsShouldOverrideUrlLoader;
import com.snaps.mobile.activity.webview.WebViewCmdGotoPage;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created by ysjeong on 16. 8. 12..
 */
public class SnapsWebEventGoLoginHandler extends SnapsWebEventBaseHandler {
    private static final String TAG = SnapsWebEventGoLoginHandler.class.getSimpleName();
    public SnapsWebEventGoLoginHandler(Activity activity, SnapsShouldOverrideUrlLoader.SnapsShouldHandleData handleDatas) {
        super(activity, handleDatas);
    }

    @Override
    public boolean handleEvent() {
        String optionUrl = urlData.get("optionUrl");
        try {
            optionUrl = URLDecoder.decode(optionUrl, "utf-8");
        } catch (UnsupportedEncodingException e) {
            Dlog.e(TAG, e);
        }

        Bundle bundle = new Bundle();
        bundle.putString("kakaologin", "true");
        if (optionUrl != null)
            bundle.putString("reloadUrl", optionUrl);

        SnapsLoginManager.startLogInProcess(activity, Const_VALUES.LOGIN_P_LOGIN, bundle);

        WebViewCmdGotoPage.gotoPage(activity, handleDatas);
        return true;
    }

    @Override
    public void printClassName() {
        Dlog.d("printClassName() class name:" + getClass().getName());
    }
}
