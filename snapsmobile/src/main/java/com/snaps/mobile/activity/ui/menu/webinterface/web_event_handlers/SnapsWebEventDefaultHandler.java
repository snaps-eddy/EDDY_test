package com.snaps.mobile.activity.ui.menu.webinterface.web_event_handlers;

import android.app.Activity;

import errorhandle.logger.Logg;

import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.activity.ui.menu.webinterface.SnapsShouldOverrideUrlLoader;
import com.snaps.mobile.activity.webview.WebViewCmdGotoPage;

/**
 * Created by ysjeong on 16. 8. 12..
 */
public class SnapsWebEventDefaultHandler extends SnapsWebEventBaseHandler {
    private static final String TAG = SnapsWebEventDefaultHandler.class.getSimpleName();
    public SnapsWebEventDefaultHandler(Activity activity, SnapsShouldOverrideUrlLoader.SnapsShouldHandleData handleDatas) {
        super(activity, handleDatas);
    }

    @Override
    public boolean handleEvent() {
        WebViewCmdGotoPage.gotoPage(activity, handleDatas);
        return true;
    }

    @Override
    public void printClassName() {
        Dlog.d("printClassName() class name:" + getClass().getName());
    }
}
