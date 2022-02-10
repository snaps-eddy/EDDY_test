package com.snaps.mobile.activity.home.utils.push_handlers;

import android.app.Activity;

import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.activity.home.utils.SnapsLoginManager;

/**
 * Created by ysjeong on 2017. 8. 24..
 */

public class SnapsPushHandlerForLogin extends SnapsBasePushHandler {
    private static final String TAG = SnapsPushHandlerForLogin.class.getSimpleName();
    public SnapsPushHandlerForLogin(Activity activity, SnapsPushHandleData pushHandleData) {
        super(activity, pushHandleData);
    }

    @Override
    public boolean performPushDataHandle() {
        try {
            return SnapsLoginManager.tryLogInIfChangedPwdScheme(getActivity(), getPushHandleData().getIntent().getDataString())
                    || SnapsLoginManager.tryLogInIfVerifyCompleteWithIntent(getActivity(), getPushHandleData().getIntent());
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        return false;
    }
}
