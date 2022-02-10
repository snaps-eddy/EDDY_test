package com.snaps.mobile.activity.ui.menu.webinterface.web_event_handlers;

import android.app.Activity;

import errorhandle.logger.Logg;

import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.IntentUtil;
import com.snaps.mobile.activity.ui.menu.webinterface.SnapsShouldOverrideUrlLoader;

/**
 * Created by ysjeong on 16. 8. 12..
 */
public class SnapsWebEventGoDeliveryHandler extends SnapsWebEventBaseHandler {
    private static final String TAG = SnapsWebEventGoDeliveryHandler.class.getSimpleName();
    public SnapsWebEventGoDeliveryHandler(Activity activity, SnapsShouldOverrideUrlLoader.SnapsShouldHandleData handleDatas) {
        super(activity, handleDatas);
    }

    @Override
    public boolean handleEvent() {
        IntentUtil.sendToDeliveryActivity(activity);
        return true;
    }

    @Override
    public void printClassName() {
        Dlog.d("printClassName() class name:" + getClass().getName());
    }
}
