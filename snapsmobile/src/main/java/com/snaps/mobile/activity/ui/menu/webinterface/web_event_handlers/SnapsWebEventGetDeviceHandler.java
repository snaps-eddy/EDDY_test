package com.snaps.mobile.activity.ui.menu.webinterface.web_event_handlers;

import android.app.Activity;
import android.webkit.WebView;

import errorhandle.logger.Logg;

import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.system.SystemUtil;
import com.snaps.mobile.activity.ui.menu.webinterface.SnapsShouldOverrideUrlLoader;
import com.snaps.mobile.activity.webview.WebViewCmdGotoPage;

import java.util.HashMap;

/**
 * Created by ysjeong on 16. 8. 12..
 */
public class SnapsWebEventGetDeviceHandler extends SnapsWebEventBaseHandler {
    private static final String TAG = SnapsWebEventGetDeviceHandler.class.getSimpleName();
    public SnapsWebEventGetDeviceHandler(Activity activity, SnapsShouldOverrideUrlLoader.SnapsShouldHandleData handleDatas) {
        super(activity, handleDatas);
    }

    @Override
    public boolean handleEvent() {

        postDeviceId(handleDatas.getWebview(), urlData);

        WebViewCmdGotoPage.gotoPage(activity, handleDatas);
        return true;
    }

    private void postDeviceId(WebView view, HashMap<String, String> urlData) {
        if(activity == null) return;

        String callbackParams = urlData.get("callback");
        if(callbackParams == null || callbackParams.length() < 1) return;

        String deviceId = SystemUtil.getDeviceId(activity);
        String url = String.format("javascript:%s(\"%s\")", callbackParams, deviceId);
        sendWebCommand(view, url);
    }

    @Override
    public void printClassName() {
        Dlog.d("printClassName() class name:" + getClass().getName());
    }
}
