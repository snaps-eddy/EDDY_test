package com.snaps.mobile.activity.ui.menu.webinterface.web_event_handlers;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import errorhandle.logger.Logg;

import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.activity.ui.menu.webinterface.SnapsShouldOverrideUrlLoader;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created by ysjeong on 16. 8. 12..
 */
public class SnapsWebEventOpenBrowserHandler extends SnapsWebEventBaseHandler {
    private static final String TAG = SnapsWebEventOpenBrowserHandler.class.getSimpleName();

    public SnapsWebEventOpenBrowserHandler(Activity activity, SnapsShouldOverrideUrlLoader.SnapsShouldHandleData handleDatas) {
        super(activity, handleDatas);
    }

    @Override
    public boolean handleEvent() {
        String openurl = urlData.get("url");
        if (!StringUtil.isEmpty(openurl)) {
            try {
                openurl = URLDecoder.decode(openurl, "utf-8");
            } catch (UnsupportedEncodingException e) {
                Dlog.e(TAG, e);
            }

            openBrowserWithUrl(openurl);
        }
        return true;
    }

    private void openBrowserWithUrl(String url) {
        if (activity == null || activity.isFinishing()) return;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        activity.startActivity(intent);
    }

    @Override
    public void printClassName() {
        Dlog.d("printClassName() class name:" + getClass().getName());
    }
}
