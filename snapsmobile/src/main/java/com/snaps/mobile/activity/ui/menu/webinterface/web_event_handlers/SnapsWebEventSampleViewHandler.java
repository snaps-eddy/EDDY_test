package com.snaps.mobile.activity.ui.menu.webinterface.web_event_handlers;

import android.app.Activity;
import android.content.Intent;

import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.activity.board.MyArtworkDetailActivity;
import com.snaps.mobile.activity.ui.menu.webinterface.SnapsShouldOverrideUrlLoader;
import com.snaps.mobile.activity.webview.WebViewCmdGotoPage;

import errorhandle.logger.Logg;

/**
 * Created by ysjeong on 16. 8. 12..
 */
public class SnapsWebEventSampleViewHandler extends SnapsWebEventBaseHandler {
    private static final String TAG = SnapsWebEventSampleViewHandler.class.getSimpleName();
    public SnapsWebEventSampleViewHandler(Activity activity, SnapsShouldOverrideUrlLoader.SnapsShouldHandleData handleDatas) {
        super(activity, handleDatas);
    }

    @Override
    public boolean handleEvent() {
        String tempid = urlData.get("tempId");
        Intent intent = new Intent(activity, MyArtworkDetailActivity.class);
        intent.putExtra("saveexist", "no");
        intent.putExtra("tempId", tempid);
        intent.putExtra("sampleView", true);
        activity.startActivity(intent);

        WebViewCmdGotoPage.gotoPage(activity, handleDatas);
        return true;
    }

    @Override
    public void printClassName() {
        Dlog.d("printClassName() class Name:" + getClass().getName());
    }
}
