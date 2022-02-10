package com.snaps.mobile.activity.ui.menu.webinterface.web_event_handlers;

import android.app.Activity;
import android.content.Intent;

import com.snaps.common.utils.constant.Const_EKEY;
import com.snaps.common.utils.constant.SnapsAPI;
import errorhandle.logger.Logg;

import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.activity.hamburger_menu.SnapsMenuManager;
import com.snaps.mobile.activity.home.utils.SnapsLoginManager;
import com.snaps.mobile.activity.ui.menu.webinterface.SnapsShouldOverrideUrlLoader;
import com.snaps.mobile.activity.webview.DetailProductWebviewActivity;
import com.snaps.mobile.activity.webview.WebViewCmdGotoPage;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created by ysjeong on 16. 8. 12..
 */
public class SnapsWebStackPageHandler extends SnapsWebEventBaseHandler {
    private static final String TAG = SnapsWebStackPageHandler.class.getSimpleName();

    public SnapsWebStackPageHandler(Activity activity, SnapsShouldOverrideUrlLoader.SnapsShouldHandleData handleDatas) {
        super(activity, handleDatas);
    }

    @Override
    public boolean handleEvent() {
        String openurl = urlData.get("optionUrl");

        if (openurl != null) {
            try {
                openurl = URLDecoder.decode(openurl, "utf-8");
                if (!openurl.startsWith("http")) {
                    openurl = SnapsAPI.WEB_DOMAIN(openurl, SnapsLoginManager.getUUserNo(activity), "");
                }
            } catch (UnsupportedEncodingException e) {
                Dlog.e(TAG, e);
            }

            String title = "";
            if (openurl.startsWith("http")) {
                try {
                    String naviTitle = urlData.get(Const_EKEY.WEB_NAVIBARTITLE_KEY);
                    if(naviTitle != null)
                        title = URLDecoder.decode(naviTitle, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    Dlog.e(TAG, e);
                }
            }

            Intent intent = DetailProductWebviewActivity.getIntent(activity, title, openurl, true, Intent.FLAG_ACTIVITY_NEW_TASK, SnapsMenuManager.eHAMBURGER_ACTIVITY.ETC);

            if (intent != null)
                activity.startActivity(intent);
        }

        WebViewCmdGotoPage.gotoPage(activity, handleDatas);
        return true;
    }

    @Override
    public void printClassName() {
        Dlog.d("printClassName() class Name:" + getClass().getName());
    }
}
