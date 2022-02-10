package com.snaps.mobile.kr;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.os.AsyncTask;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import com.snaps.common.trackers.SnapsAppsFlyer;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.pref.Setting;

import java.io.IOException;

import errorhandle.logger.Logg;

/**
 * Created by ysjeong on 2017. 8. 16..
 */

public class SnapsGCMUtil {
    private static final String TAG = SnapsGCMUtil.class.getSimpleName();
    public static void registerGCM(Context context) {
        try {
            String test;
            if (checkPlayServices(context)) {
                getRegId(context);
            }
//            GCMRegistrar.checkDevice(context);
//            GCMRegistrar.checkManifest(context);
//
//            String regId = GCMRegistrar.getRegistrationId(context);
////            Logg.d("Application-regId:" + regId);
//
//            if ("".equals(regId)) {
//                GCMRegistrar.register(context, Const_VALUE.GCM_PROJECT_NUMBER);
//            } else {
//                //FIXME FCM으로 마이그레이션 하기 전까지는 실행할 때 마다 키를 갱신 해 줌.
//                GCMRegistrar.unregister(context);
//                GCMRegistrar.register(context, Const_VALUE.GCM_PROJECT_NUMBER);
//            }

        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }
    private static boolean checkPlayServices(Context context) {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            return false;
        }
        return true;
    }

    private static void getRegId(final Context context) {
//        new AsyncTask<Void,Void,Void>(){
//
//            @Override
//            protected Void doInBackground(Void... voids) {
//                try {
////                    GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
////                    String regId = gcm.register(Const_VALUE.GCM_PROJECT_NUMBER);
////                   // Setting.set(context, Const_VALUE.KEY_GCM_REGID, regId);
////                    Logg.d("Application-regId:" + regId);
////                    //TODO duckwon
////                    try{
////                        if(regId!=null){
////                            SnapsAppsFlyer.setUpdateUninstallToken(context,regId);
////                        }
////                    }catch (Throwable e){
////                        Dlog.e(TAG, e);
////                    }
////                }catch (IOException e) {
////                    Dlog.e(TAG, e);
////                }
////                return null;
//            }
//        }.execute();
    }
}
