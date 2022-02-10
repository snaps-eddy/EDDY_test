package com.snaps.mobile.kr;

import android.content.Context;

import com.google.firebase.messaging.RemoteMessage;
import com.snaps.common.push.PushNotificationService;
import com.snaps.common.push.PushManager;
import com.snaps.common.trackers.SnapsAppsFlyer;
import com.snaps.mobile.service.ai.DeviceManager;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().containsKey("af-uinstall-tracking")) {
            return;
        }

        Context context = getApplicationContext();

        PushManager pushService = new PushManager(context);
        DeviceManager deviceManager = new DeviceManager(context);
        PushNotificationService pushNotificationService = new PushNotificationService(context);

        pushService.onMessage(remoteMessage.getData(), deviceManager, pushNotificationService);
    }

    @Override
    public void onNewToken(String newToken) {
        super.onNewToken(newToken);

        SnapsAppsFlyer.setUpdateUninstallToken(getApplicationContext(), newToken);

        PushManager pushService = new PushManager(getApplicationContext());
        pushService.onNewToken(newToken);
    }

}
