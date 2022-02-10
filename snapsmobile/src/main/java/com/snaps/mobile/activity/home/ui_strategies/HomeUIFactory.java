package com.snaps.mobile.activity.home.ui_strategies;

import android.app.Activity;

import com.snaps.mobile.activity.home.utils.SnapsEventHandler;

/**
 * Created by ysjeong on 2017. 8. 17..
 */

public class HomeUIFactory {
    public static HomeUIHandler createHomeUIWithEventHandler(Activity activity, SnapsEventHandler eventHandler) {
        //TODO  비트윈하고 카카오 스냅스앱을 모두 지워 버렸다.
        //TODO  추후, 지원이 필요하다면 재개발을 진행하세요.
        return new HomeUIForSnaps(activity, eventHandler);
    }

    public static HomeUIHandler createHomeUIRenewalWithEventHandler(Activity activity, SnapsEventHandler eventHandler) {
        //TODO  비트윈하고 카카오 스냅스앱을 모두 지워 버렸다.
        //TODO  추후, 지원이 필요하다면 재개발을 진행하세요.
        return new HomeUIForSnapsRenewal(activity, eventHandler);
    }
}
