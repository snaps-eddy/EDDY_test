package com.snaps.mobile.activity.ui.menu.webinterface.web_event_handlers;

import android.app.Activity;
import android.webkit.WebView;

import errorhandle.logger.Logg;

import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.activity.ui.menu.webinterface.SnapsShouldOverrideUrlLoader;
import com.snaps.mobile.activity.webview.KakaoEventActivity;
import com.snaps.mobile.activity.webview.WebViewCmdGotoPage;

import java.util.HashMap;

/**
 * Created by ysjeong on 16. 8. 12..
 */
public class SnapsWebEventKakaoStoryCheckHandler extends SnapsWebEventBaseHandler {
    private static final String TAG = SnapsWebEventKakaoStoryCheckHandler.class.getSimpleName();
    public SnapsWebEventKakaoStoryCheckHandler(Activity activity, SnapsShouldOverrideUrlLoader.SnapsShouldHandleData handleDatas) {
        super(activity, handleDatas);
    }

    @Override
    public boolean handleEvent() {
        checkKakaoStoryLogin(handleDatas.getWebview(), urlData);

        WebViewCmdGotoPage.gotoPage(activity, handleDatas);
        return true;
    }

    void checkKakaoStoryLogin(final WebView webView, final HashMap<String, String> urlData) {

        String callback = urlData.get("callback");

        String cmdText = "";

        if (kakao == null) {
            cmdText = String.format("javascript:%s(\"%s\")", callback, "1");
            sendWebCommand(webView, cmdText);
        }

        if (kakao.isKakaoLogin()) {
            cmdText = String.format("javascript:%s(\"%s\")", callback, "0");
            sendWebCommand(webView, cmdText);
        }

        else {
            kakao.startKakaoLoginActivity(activity);
            KakaoEventActivity.LOGIN_AFTER_CMD = String.format("javascript:%s(\"%s\")", callback, "0");
        }
    }

    @Override
    public void printClassName() {
        Dlog.d("printClassName() class name:" + getClass().getName());
    }
}
