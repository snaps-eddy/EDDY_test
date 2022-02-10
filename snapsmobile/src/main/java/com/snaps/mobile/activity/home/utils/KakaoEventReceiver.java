package com.snaps.mobile.activity.home.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.snaps.common.utils.constant.Config;
import errorhandle.logger.Logg;

import errorhandle.logger.SnapsLogger;

/**
 * Created by ysjeong on 2017. 8. 17..
 */

public class KakaoEventReceiver extends BroadcastReceiver {

    private SnapsEventHandler eventHandler = null;

    private KakaoEventReceiver(SnapsEventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }

    public static KakaoEventReceiver createInstanceWithEventHandler(SnapsEventHandler eventHandler) {
        return new KakaoEventReceiver(eventHandler);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && intent.getDataString() == null) {
            String sendno = intent.getStringExtra("sendno");
            if (sendno != null && Config.getKAKAO_EVENT_RESULT() == null) {
                Config.setKAKAO_EVENT_RESULT("login");

                SnapsLogger.appendTextLog("on kakao receiver", sendno);
                if (eventHandler != null)
                    eventHandler.gotoKakaoEventPage(null);
            }
        }
    }
}
