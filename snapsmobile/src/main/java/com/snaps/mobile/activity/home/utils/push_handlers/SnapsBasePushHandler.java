package com.snaps.mobile.activity.home.utils.push_handlers;

import android.app.Activity;

/**
 * Created by ysjeong on 2017. 8. 24..
 */

public abstract class SnapsBasePushHandler implements ISnapsPushHandler {
    private Activity activity;
    private SnapsPushHandleData pushHandleData;

    public SnapsBasePushHandler(Activity activity, SnapsPushHandleData pushHandleData) {
        this.activity = activity;
        this.pushHandleData = pushHandleData;
    }

    public Activity getActivity() {
        return activity;
    }

    public SnapsPushHandleData getPushHandleData() {
        return pushHandleData;
    }
}
