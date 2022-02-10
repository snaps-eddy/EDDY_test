package com.snaps.mobile.activity.ui.menu.webinterface.web_event_handlers;

import android.app.Activity;
import android.content.Intent;

import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.activity.ui.menu.webinterface.SnapsShouldOverrideUrlLoader;
import com.snaps.mobile.activity.webview.WebViewCmdGotoPage;
import com.snaps.mobile.activity.youtube.SnapsYoutubeActivity;

import errorhandle.logger.Logg;

/**
 * Created by ysjeong on 16. 8. 12..
 */
public class SnapsWebEventPlayYoutubeHandler extends SnapsWebEventBaseHandler {
    private static final String TAG = SnapsWebEventPlayYoutubeHandler.class.getSimpleName();
    public SnapsWebEventPlayYoutubeHandler(Activity activity, SnapsShouldOverrideUrlLoader.SnapsShouldHandleData handleDatas) {
        super(activity, handleDatas);
    }

    @Override
    public boolean handleEvent() {
        String vodID = urlData.get("vodId");

        Intent intent = new Intent( activity, SnapsYoutubeActivity.class );
        intent.putExtra( SnapsYoutubeActivity.VIDEO_ID_INTENT_EXTRA, vodID );
        activity.startActivity(intent);

        WebViewCmdGotoPage.gotoPage(activity, handleDatas);
        return true;
    }

    @Override
    public void printClassName() {
        Dlog.d("printClassName() class name:" + getClass().getName());
    }
}
