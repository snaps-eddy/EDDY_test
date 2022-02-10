package com.snaps.mobile.activity.ui.menu.webinterface.web_event_handlers;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.activity.ui.menu.webinterface.SnapsShouldOverrideUrlLoader;
import com.snaps.mobile.activity.webview.WebViewCmdGotoPage;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;

import errorhandle.logger.Logg;

/**
 * Created by ysjeong on 16. 8. 12..
 */
public class SnapsWebEventOpenIntentHandler extends SnapsWebEventBaseHandler {
    private static final String TAG = SnapsWebEventOpenIntentHandler.class.getSimpleName();
    public SnapsWebEventOpenIntentHandler(Activity activity, SnapsShouldOverrideUrlLoader.SnapsShouldHandleData handleDatas) {
        super(activity, handleDatas);
    }

    @Override
    public boolean handleEvent() {
        openIntent(urlData);

        WebViewCmdGotoPage.gotoPage(activity, handleDatas);
        return true;
    }

    void openIntent(final HashMap<String, String> urlData) {
        String url = urlData.get("openUrl");
        if (url != null) {
            try {
                if (url != null)
                    url = URLDecoder.decode(url, "utf-8");
            } catch (UnsupportedEncodingException e) {
                Dlog.e(TAG, e);
            }
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        activity.startActivity(intent);
    }

    @Override
    public void printClassName() {
        Dlog.d("printClassName() class name:" + getClass().getName());
    }
}
