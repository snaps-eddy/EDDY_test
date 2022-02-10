package com.snaps.mobile.activity.ui.menu.webinterface.web_event_handlers;

import android.app.Activity;

import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.activity.ui.menu.webinterface.SnapsShouldOverrideUrlLoader;
import com.snaps.mobile.utils.thirdparty.SnapsTPAppManager;

import errorhandle.logger.Logg;

/**
 * Created by ysjeong on 16. 8. 12..
 */
public class SnapsWebEventGoCustomerActHandler extends SnapsWebEventBaseHandler {
    private static final String TAG = SnapsWebEventGoCustomerActHandler.class.getSimpleName();
    public SnapsWebEventGoCustomerActHandler(Activity activity, SnapsShouldOverrideUrlLoader.SnapsShouldHandleData handleDatas) {
        super(activity, handleDatas);
    }

    @Override
    public boolean handleEvent() {
        SnapsTPAppManager.goCustomerActivity(activity);
        return true;
    }

    @Override
    public void printClassName() {
        Dlog.d("printClassName() class name:" + getClass().getName());
    }
}
