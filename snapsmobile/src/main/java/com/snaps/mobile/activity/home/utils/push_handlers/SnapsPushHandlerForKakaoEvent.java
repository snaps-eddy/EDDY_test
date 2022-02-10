package com.snaps.mobile.activity.home.utils.push_handlers;

import android.app.Activity;

import com.snaps.mobile.activity.hamburger_menu.SnapsMenuManager;
import com.snaps.common.structure.SnapsHandler;
import com.snaps.mobile.activity.home.RenewalHomeActivity;

/**
 * Created by ysjeong on 2017. 8. 24..
 */

public class SnapsPushHandlerForKakaoEvent extends SnapsBasePushHandler {
    public SnapsPushHandlerForKakaoEvent(Activity activity, SnapsPushHandleData pushHandleData) {
        super(activity, pushHandleData);
    }

    @Override
    public boolean performPushDataHandle() {
        if (SnapsMenuManager.goToKakaoEventPageIfGetKakaoIntent(getPushHandleData().getIntent(), getPushHandleData().getEventHandler())) {
            SnapsHandler handler = getPushHandleData().getSnapsHandler();
            if (handler != null) {
                handler.sendEmptyMessage(RenewalHomeActivity.HANDLE_MSG_UNREGISTER_KAKAO_RECEIVER);
            }
            return true;
        }
        return false;
    }
}
