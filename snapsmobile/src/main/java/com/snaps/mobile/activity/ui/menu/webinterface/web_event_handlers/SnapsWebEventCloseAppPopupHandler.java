package com.snaps.mobile.activity.ui.menu.webinterface.web_event_handlers;

import android.app.Activity;

import com.snaps.common.utils.constant.Config;
import errorhandle.logger.Logg;

import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.activity.hamburger_menu.SnapsMenuManager;
import com.snaps.mobile.activity.home.RenewalHomeActivity;
import com.snaps.mobile.activity.ui.menu.webinterface.SnapsShouldOverrideUrlLoader;
import com.snaps.mobile.activity.webview.PopupWebviewActivity;

/**
 * Created by ysjeong on 16. 8. 12..
 */
public class SnapsWebEventCloseAppPopupHandler extends SnapsWebEventBaseHandler {
    private static final String TAG = SnapsWebEventCloseAppPopupHandler.class.getSimpleName();
    public SnapsWebEventCloseAppPopupHandler(Activity activity, SnapsShouldOverrideUrlLoader.SnapsShouldHandleData handleDatas) {
        super(activity, handleDatas);
    }

    @Override
    public boolean handleEvent() {

        //웹 화면 리프리시가 필요하다고 해서 만든 꼼수.
        if (urlData != null) {
            String refresh = urlData.get("refresh");
            if (refresh != null && refresh.equalsIgnoreCase("true")) {
                Config.setNeedWebViewRefresh(true);
            }
        }

        boolean isPopupDialogFragment = false;

        SnapsMenuManager menuManager = SnapsMenuManager.getInstance();
        if (menuManager != null) {
            PopupWebviewActivity popupWebviewActivity = menuManager.getPopupWebViewActivity();
            if (popupWebviewActivity != null && !popupWebviewActivity.isFinished()) {
                popupWebviewActivity.finish();
                isPopupDialogFragment = true;
            }
        }

        if (!isPopupDialogFragment) {
            if (activity != null && !(activity instanceof RenewalHomeActivity)) {
                activity.finish();
            }
        }
        return true;
    }

    @Override
    public void printClassName() {
        Dlog.d("printClassName() class name:" + getClass().getName());
    }
}
