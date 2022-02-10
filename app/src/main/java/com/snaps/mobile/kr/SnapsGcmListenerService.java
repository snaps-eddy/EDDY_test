package com.snaps.mobile.kr;
//
//import android.os.Bundle;
//
//import com.google.android.gms.gcm.GcmListenerService;
//import com.snaps.common.utils.constant.Config;
//import com.snaps.common.utils.constant.Const_VALUE;
//import com.snaps.common.utils.constant.SnapsAPI;
//import com.snaps.common.utils.pref.Setting;
//import com.snaps.mobile.utils.ui.NotifyUtil;
//
//import errorhandle.logger.Logg;
//import errorhandle.logger.SnapsLogger;
//
//public class SnapsGcmListenerService extends GcmListenerService{
//
//    @Override
//    public void onMessageReceived(String from, Bundle data) {
//
//        boolean pushReceive = Setting.getBoolean(this, Const_VALUE.KEY_GCM_PUSH_RECEIVE, true);
//        if (!pushReceive) return;
//
//        if( data != null ) {
//            Setting.set( getApplicationContext(), Const_VALUE.KEY_BROADCAST_CODE, data.getString("brdcstCode") );
//            Setting.set( getApplicationContext(), Const_VALUE.KEY_RESEND_NO, data.getString("resendNo") );
//        }
//
//        NotifyUtil.requestPushReceived( getApplicationContext(), false );
//
//        try {
//            if (data != null) {
//
//                // processList(context);
//
//                String set = data.toString();
//
//                Logg.d(" set set " + set);
//                String msg = data.getString(Const_VALUE.GCM_MSG_KEY);
//                String msgTitle = data.getString(Const_VALUE.GCM_TITLE_KEY);
//
//                String rcvTypr = data.getString(Const_VALUE.GCM_RCV_KEY);
//                String imagePath = data.getString(Const_VALUE.GCM_IMG_PATH);
//                String targetPath = data.getString(Const_VALUE.GCM_TARGET_PATH);
//                String imageinclude = data.getString(Const_VALUE.GCM_IMAGE_EXIST);
//                String targetfullPath = data.getString("targetFullUrl");
//
//                // processList(context);
//
//                if (msg != null) {
//                    // Logg.d("msg:" + msg);
//                    // Logg.d("rcvTypr:" + rcvTypr);
//                    // Logg.d("targetPath:" + targetPath);
//                    // Logg.d("imagePath:" + imagePath);
//                    // Logg.d("imageinclude:" + imageinclude);
//                    // Logg.d("targetfullPath:" + targetfullPath);
//
//                    if (!Config.IS_MAKE_RUNNING()) {
//                        if (rcvTypr.equals(Const_VALUE.GCM_NOTICE)) {
////							NotifyUtil.notify(context, msg, msgTitle, targetPath, true, targetfullPath);
//                            NotifyUtil.GCMContents contents = new NotifyUtil.GCMContents();
//                            contents.setTitle(msgTitle);
//                            contents.setMsg(msg);
//                            contents.setTarget(targetPath);
//                            contents.setFullTarget(targetfullPath);
//                            contents.setBigImgPath(SnapsAPI.DOMAIN() + imagePath);
//                            NotifyUtil.notify(getApplicationContext(), contents, true);
//
//                        } else if (rcvTypr.equals(Const_VALUE.GCM_PUSH)) {
//                            NotifyUtil.wakePush(getApplicationContext(), msg, msgTitle, imagePath, targetPath, true, imageinclude, targetfullPath);
//                        } else if (rcvTypr.equals(Const_VALUE.GCM_PUSHNOITCE)) {
////							NotifyUtil.notify(context, msg, msgTitle, targetPath, true, targetfullPath);
//                            NotifyUtil.GCMContents contents = new NotifyUtil.GCMContents();
//                            contents.setTitle(msgTitle);
//                            contents.setMsg(msg);
//                            contents.setTarget(targetPath);
//                            contents.setFullTarget(targetfullPath);
//                            contents.setBigImgPath(SnapsAPI.DOMAIN() + imagePath);
//                            NotifyUtil.notify(getApplicationContext(), contents, true);
//                            NotifyUtil.wakePush(getApplicationContext(), msg, msgTitle, imagePath, targetPath, true, imageinclude, targetfullPath);
//                        }
//                    }
//
//                }
//            }
//        } catch (Exception e) {
//            Dlog.e(TAG, e);
//            SnapsLogger.sendLogException("SnapsGcmListenerService/onMessage", e);
//        }
//    }
//
//
//
//}
