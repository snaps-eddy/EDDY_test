package com.snaps.common.push;

import android.content.Context;
import android.os.AsyncTask;

import com.google.firebase.messaging.RemoteMessage;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.net.http.HttpReq;
import com.snaps.common.utils.net.http.HttpUtil;
import com.snaps.common.utils.pref.Setting;
import com.snaps.common.utils.system.SystemUtil;
import com.snaps.mobile.BuildConfig;
import com.snaps.mobile.service.ai.DeviceManager;
import com.snaps.mobile.service.ai.SyncPhotoServiceManager;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.Map;

import errorhandle.logger.Logg;
import errorhandle.logger.SnapsInterfaceLogDefaultHandler;

public class PushManager {
    private static final String TAG = PushManager.class.getSimpleName();
    private static final String GCM_KEY_MESSAGE = "message";
    private static final String GCM_KEY_POP_MESSAGE = "popMessage";
    private static final String GCM_KEY_TITLE = "title";
    private static final String GCM_KEY_RECEIVE_TYPE = "rcvType";
    private static final String GCM_KEY_IMAGE_PATH = "imgPath";
    private static final String GCM_KEY_IMAGE_INCLUDED = "imgInclude";
    private static final String GCM_KEY_TARGET_URL = "targetUrl";
    private static final String GCM_KEY_TARGET_FULL_URL = "targetFullUrl";
    private static final String GCM_KEY_IMAGE_OT = "imgOt";
    private static final String GCM_KEY_CHANNEL_CODE = "prmchnlcode";

    private static final String GCM_NOTICE = "221001";
    private static final String GCM_PUSH = "221002";
    private static final String GCM_PUSHNOITCE = "221005";
    private static final String GCM_AI_ANALYSIS_COMPLETED = "221006";
    private static final String GCM_SILENT_PUSH = "221999";

    private Context context;

    public PushManager(Context context) {
        this.context = context;
    }

    public void onNewToken(String newToken) {
        Setting.set(context, Const_VALUE.KEY_GCM_REGID, newToken);
    }

    public void onMessage(Map<String, String> messageDataMap, DeviceManager deviceManager, PushNotificationService pushNotificationService) {
        if (messageDataMap == null) {
            Dlog.w(TAG, "Ignored message, MessageDataMap is null.");
            return;
        }

        boolean isAllowShowPushMessage = Setting.getBoolean(context, Const_VALUE.KEY_GCM_PUSH_RECEIVE, false);
        if (!isAllowShowPushMessage) {
            Dlog.w(TAG, "Device not allow push message.");
            return;
        }

        GCMContent parsedMessage = parseRemoteMessage(messageDataMap);
        String receiveType = parsedMessage.getReceiveType();
        if (receiveType == null || receiveType.trim().length() < 1) {
            Dlog.w(TAG, "Failed parsing message data.");
            return;
        }

        notifyMessage(receiveType, parsedMessage, deviceManager, pushNotificationService);
    }

    private GCMContent parseRemoteMessage(Map<String, String> messageDataMap) {
        Setting.set(context, Const_VALUE.KEY_BROADCAST_CODE, messageDataMap.get("brdcstCode"));
        Setting.set(context, Const_VALUE.KEY_RESEND_NO, messageDataMap.get("resendNo"));

        String message = messageDataMap.get(GCM_KEY_MESSAGE);
        String title = messageDataMap.get(GCM_KEY_TITLE);
        String receiveType = messageDataMap.get(GCM_KEY_RECEIVE_TYPE);
        String imagePath = messageDataMap.get(GCM_KEY_IMAGE_PATH);
        String imageOt = messageDataMap.get(GCM_KEY_IMAGE_OT);
        String imageIncluded = messageDataMap.get(GCM_KEY_IMAGE_INCLUDED);

        String targetPath = messageDataMap.get(GCM_KEY_TARGET_URL);
        String targetfullPath = messageDataMap.get(GCM_KEY_TARGET_FULL_URL);
        String popMessage = messageDataMap.get(GCM_KEY_POP_MESSAGE);
        String prmchnlcode = messageDataMap.get(GCM_KEY_CHANNEL_CODE);

        GCMContent.Builder contentBuilder = new GCMContent.Builder();
        contentBuilder.msg(message)
                .title(title)
                .receiveType(receiveType)
                .bigImgPath(imagePath)
                .imgOt(imageOt)
                .imageIncluded(imageIncluded)
                .target(targetPath)
                .fullTarget(targetfullPath)
                .popMessage(popMessage)
                .prmchnlcode(prmchnlcode);

        return contentBuilder.create();
    }

    private void notifyMessage(String receiveType, GCMContent parsedMessage, DeviceManager deviceManager, PushNotificationService pushNotificationService) {
        switch (receiveType) {
            case GCM_NOTICE: {
                pushNotificationService.notifyInStatusBar(parsedMessage, true);
                break;
            }
            case GCM_PUSH: {
                pushNotificationService.notifyWakeup(parsedMessage, true);
                break;
            }
            case GCM_PUSHNOITCE: {
                pushNotificationService.notifyInStatusBar(parsedMessage, true);
                pushNotificationService.notifyWakeup(parsedMessage, true);
                break;
            }
            case GCM_AI_ANALYSIS_COMPLETED: {
                if (deviceManager.isAppForeground()) {
                    pushNotificationService.showToast(parsedMessage.getPopMessage());
                } else {
                    pushNotificationService.notifyInStatusBar(parsedMessage, true);
                }
                break;
            }
            case GCM_SILENT_PUSH: {

                if (Config.isDevelopVersion() && BuildConfig.DEBUG) {
                    pushNotificationService.showToast("Received silent push message");
                }

                /*
                if (deviceManager.isAppForeground() == false && SyncPhotoServiceManager.getInstance().isRunning() == false) {
                    //사진 동기화 서비스 시작 (15초 동안 실행된다.)
                    SyncPhotoServiceManager.getInstance().startService(context, false, 15);
                }
                */
                break;
            }
            default:
                Dlog.w(TAG, "Disable handle message!");
                break;
        }
    }

    public void requestRegistPushDevice() {
        final String regId = Setting.getString(context, Const_VALUE.KEY_GCM_REGID, "");
        final String userNo = Setting.getString(context, Const_VALUE.KEY_SNAPS_USER_NO);
        final String userName = Setting.getString(context, Const_VALUE.KEY_SNAPS_USER_NAME);
        final String appVersion = SystemUtil.getAppVersion(context);
        final String deviceID = SystemUtil.getDeviceId(context);
        final boolean isAgreeGetPush = Setting.getBoolean(context, Const_VALUE.KEY_GCM_PUSH_RECEIVE);

        AsyncTask.execute(() -> HttpReq.regPushDevice(isAgreeGetPush ? regId : "", userNo, userName,
                appVersion, deviceID, SnapsInterfaceLogDefaultHandler.createDefaultHandler()));
    }

    public void requestPushReceived(boolean whenClickNoti) {
        ArrayList<NameValuePair> params;
        params = new ArrayList<>();
        params.add(new BasicNameValuePair("deviceNo", Setting.getString(context, Const_VALUE.KEY_GCM_REGID)));
        params.add(new BasicNameValuePair("userNo", Setting.getString(context, Const_VALUE.KEY_SNAPS_USER_NO)));
        params.add(new BasicNameValuePair("brdcstCode", Setting.getString(context, Const_VALUE.KEY_BROADCAST_CODE)));
        params.add(new BasicNameValuePair("resendNo", Setting.getString(context, Const_VALUE.KEY_RESEND_NO)));
        params.add(new BasicNameValuePair("rcvYorn", "Y"));
        params.add(new BasicNameValuePair("openYorn", whenClickNoti ? "Y" : "N"));

        AsyncTask.execute(() -> HttpUtil.connectGet(SnapsAPI.PUSH_RECEIVE_INTERFACE(), params,
                SnapsInterfaceLogDefaultHandler.createDefaultHandler()));
    }
}
