package com.snaps.mobile.activity.home.utils.push_handlers;

import android.app.Activity;

import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.activity.home.intent_strategies.HomeIntentDataHandlerFactory;
import com.snaps.mobile.activity.home.intent_strategies.HomeIntentDataImp;
import com.snaps.mobile.activity.home.model.HomeIntentHandleData;

/**
 * Created by ysjeong on 2017. 8. 24..
 */

public class SnapsPushHandlerForCMD extends SnapsBasePushHandler {
    private static final String TAG = SnapsPushHandlerForCMD.class.getSimpleName();
    public SnapsPushHandlerForCMD(Activity activity, SnapsPushHandleData pushHandleData) {
        super(activity, pushHandleData);
    }

    @Override
    public boolean performPushDataHandle() {
        try {
            HomeIntentHandleData handleData = new HomeIntentHandleData.Builder().setHomeUIHandler(getPushHandleData().getHomeUIHandler()).setIntent(getPushHandleData().getIntent()).create();
            HomeIntentDataImp homeIntentDataImp = HomeIntentDataHandlerFactory.createHomeIntentDataImpWithIntent(getActivity(), handleData);
            if (homeIntentDataImp != null) homeIntentDataImp.performGoToFunction();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        return false;
    }
}
