package com.snaps.mobile.utils.kakao;

import android.app.Activity;
import android.content.Context;

//import com.kakao.kakaolink.v2.KakaoLinkResponse;
//import com.kakao.kakaolink.v2.KakaoLinkService;
//import com.kakao.message.template.ButtonObject;
//import com.kakao.message.template.ContentObject;
//import com.kakao.message.template.FeedTemplate;
//import com.kakao.message.template.LinkObject;
//import com.kakao.message.template.TemplateParams;
//import com.kakao.network.ErrorResult;
//import com.kakao.network.callback.ResponseCallback;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.pref.Setting;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.R;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import errorhandle.logger.Logg;

public class SnapsKakaoUtil {

//    public static boolean feedTemplateWithUrlData(Activity activity, SnapsSnsInviteParams snapsSnsInviteParams) {
//        if (activity == null || snapsSnsInviteParams == null) return false;
//
//        if (snapsSnsInviteParams.getExcuteParam() != null) {
//            return feedTemplateWithExecuteParam(activity, snapsSnsInviteParams);
//        }
//
//        return feedTemplateSelectiveWithExecute(activity, snapsSnsInviteParams);
//    }
//
//    private static boolean feedTemplateWithExecuteParam(Activity activity, SnapsSnsInviteParams snapsSnsInviteParams) {
//        FeedTemplate params = FeedTemplate
//                .newBuilder(ContentObject.newBuilder(snapsSnsInviteParams.getText(),
//                        snapsSnsInviteParams.getImgUrl(),
//                        LinkObject.newBuilder().setWebUrl(snapsSnsInviteParams.getOpenurl())
//                                .setMobileWebUrl(snapsSnsInviteParams.getOpenurl()).build())
////                        .setDescrption(snapsSnsInviteParams.getText())
//                        .setImageWidth(Integer.parseInt(snapsSnsInviteParams.getImgWidth()))
//                        .setImageHeight(Integer.parseInt(snapsSnsInviteParams.getImgHeight()))
//                        .build())
//                .addButton(new ButtonObject(snapsSnsInviteParams.getUrlText(), LinkObject.newBuilder()
//                        .setWebUrl(snapsSnsInviteParams.getOpenurl())
//                        .setMobileWebUrl(snapsSnsInviteParams.getOpenurl())
//                        .setAndroidExecutionParams(snapsSnsInviteParams.getExcuteParam())
//                        .setIosExecutionParams(snapsSnsInviteParams.getExcuteParam())
//                        .build()))
//                .build();
//        return sendDefault(activity, params);
//    }
//
//    private static boolean sendDefault(Activity activity, TemplateParams templateParams) {
//        try {
//            KakaoLinkService.getInstance().sendDefault(activity, templateParams, new ResponseCallback<KakaoLinkResponse>() {
//                @Override
//                public void onFailure(ErrorResult errorResult) {
//                    Logg.y(errorResult.toString());
//                }
//
//                @Override
//                public void onSuccess(KakaoLinkResponse result) {
//                    Logg.y("success kakao link.");
//                }
//            });
//            return true;
//        } catch (Exception e) { Dlog.e(TAG, e); }
//        return false;
//    }
//
//    private static boolean feedTemplateSelectiveWithExecute(Activity activity, SnapsSnsInviteParams snapsSnsInviteParams) {
//        ContentObject contentObject = ContentObject.newBuilder(snapsSnsInviteParams.getText(),
//                snapsSnsInviteParams.getImgUrl(),
//                LinkObject.newBuilder().setWebUrl(snapsSnsInviteParams.getOpenurl())
//                        .setMobileWebUrl(snapsSnsInviteParams.getOpenurl()).build())
////                .setDescrption(snapsSnsInviteParams.getText())
//                .setImageWidth(Integer.parseInt(snapsSnsInviteParams.getImgWidth()))
//                .setImageHeight(Integer.parseInt(snapsSnsInviteParams.getImgHeight()))
//                .build();
//
//        FeedTemplate.Builder feedBuilder = new FeedTemplate.Builder(contentObject);
//        if (!StringUtil.isEmpty(snapsSnsInviteParams.getUrlText())) {
//            if (snapsSnsInviteParams.getIsRunapp() != null && snapsSnsInviteParams.getIsRunapp().equalsIgnoreCase("true")) {
//                feedBuilder.addButton(new ButtonObject(snapsSnsInviteParams.getUrlText(), LinkObject.newBuilder()
//                        .setWebUrl(snapsSnsInviteParams.getOpenurl())
//                        .setMobileWebUrl(snapsSnsInviteParams.getOpenurl())
//                        .setAndroidExecutionParams(snapsSnsInviteParams.getExcuteParam())
//                        .setIosExecutionParams(snapsSnsInviteParams.getExcuteParam())
//                        .build()));
//            } else {
//                feedBuilder.addButton(new ButtonObject(snapsSnsInviteParams.getUrlText(), LinkObject.newBuilder()
//                        .setWebUrl(snapsSnsInviteParams.getOpenurl())
//                        .setMobileWebUrl(snapsSnsInviteParams.getOpenurl())
//                        .build()));
//            }
//        }
//        return sendDefault(activity, feedBuilder.build());
//    }
//
//    /**
//     * 카카오톡 공유
//     */
//    public static void kakaoTalkPost(Activity act, String projCode) {
//        try {
//            // Recommended: Use application context for parameter.
//            KakaoLink kakaoLink = KakaoLink.getLink(act.getApplicationContext());
//
//            // check, intent is available.
//            if (!kakaoLink.isAvailableIntent()) {
//                alert(act, act.getResources().getString(R.string.kakaotalk_install_fail));
//                return;
//            }
//
//            ArrayList<Map<String, String>> metaInfoArray = null;
//
//            if (!Config.getPROJ_CODE().equalsIgnoreCase(""))
//                metaInfoArray = getKakaoPostMyProject(act);
//            else
//                metaInfoArray = getKakaoPostApps(act);
//
//            String shareString = "";
//            String shareMsg = Setting.getString(act, Const_VALUE.KEY_KAKAOTALK_SHARE_MSG);
//            String myName = Setting.getString(act, Const_VALUE.KEY_KAKAOTALK_MYNAME);
//            if ("".equals(shareMsg)) {// 공유메시지 없는 경우
//                shareString = String.format(act.getString(R.string.kakaotalk_share_message), myName);
//            } else {// 있는 경우
//                shareString = shareMsg.replaceAll("%@", myName);
//                Logg.d("shareString:" + shareString);
//            }
//
//            kakaoLink.openKakaoAppLink(act, SnapsAPI.DOMAIN(), // link url
//                    shareString, act.getPackageName(), act.getPackageManager().getPackageInfo(act.getPackageName(), 0).versionName, act.getResources().getString(R.string.snaps), // [title]
//                    "UTF-8", metaInfoArray);
//        } catch (Exception e) {
//            Dlog.e(TAG, e);
//        }
//    }
//
//    static void alert(Context context, String message) {
//        MessageUtil.alert(context, context.getString(R.string.app_name), message);
//    }
//
//    /**
//     *
//     * KaKao Post Apps
//     * @param context
//     * @return
//     */
//    public static ArrayList<Map<String,String>> getKakaoPostApps ( Context context ) {
//
//        ArrayList<Map<String, String>> metaInfoArray = new ArrayList<Map<String, String>>();
//
//        // excute url은 kakao + client_id + :// 과 같은 형식으로 만들어집니다.
//        // 카카오톡에서 이 앱을 실행시키기 위해서 AndroidManifest.xml에 custom scheme을 설정해줍니다.
//        // 추가적인 액션에 대해 exe?key1=value1&key2=value2 의 형식으로 excuteurl을 설정해주면됩니다.
//
//        Map<String, String> metaInfoAndroid = new Hashtable<String, String>(1);
//        metaInfoAndroid.put("os", "android");
//        metaInfoAndroid.put("devicetype", "phone");
//        metaInfoAndroid.put("installurl", SnapsAPI.PLAY_STORE_UPDATE_URL);
//        metaInfoAndroid.put("executeurl", KakaoConst.CLIENT_REDIRECT_URI );
//
//        Map<String, String> metaInfoIOS = new Hashtable<String, String>(1);
//        metaInfoIOS.put("os", "ios");
//        metaInfoIOS.put("devicetype", "phone");
//        metaInfoIOS.put("installurl", SnapsAPI.APP_STORE_UPDATE_URL);
//        metaInfoIOS.put("executeurl", KakaoConst.CLIENT_REDIRECT_URI);
//
//        // add to array
//        metaInfoArray.add(metaInfoAndroid);
//        metaInfoArray.add(metaInfoIOS);
//
//        return metaInfoArray;
//    }
//
//
//    /**
//     *
//     * Kakao Post Project
//     * @param context
//     * @return
//     */
//    public static ArrayList<Map<String,String>> getKakaoPostMyProject (Context context ) {
//        ArrayList<Map<String, String>> metaInfoArray = new ArrayList<Map<String, String>>();
//
//        // If application is support Android platform.
//        Map<String, String> metaInfoAndroid = new Hashtable<String, String>(1);
//        metaInfoAndroid.put("os", "android");
//        metaInfoAndroid.put("devicetype", "phone");
//        metaInfoAndroid.put("installurl", SnapsAPI.PLAY_STORE_UPDATE_URL);
//        metaInfoAndroid.put("executeurl", "snapsmobilekr" + "://sharekakaotalk?prdcode=" + Config.getPROD_CODE() + "&prjcode=" + Config.getPROJ_CODE());
//
//        // If application is support ios platform.
//        Map<String, String> metaInfoIOS = new Hashtable<String, String>(1);
//        metaInfoIOS.put("os", "ios");
//        metaInfoIOS.put("devicetype", "phone");
//        metaInfoIOS.put("installurl", SnapsAPI.APP_STORE_UPDATE_URL);
//        metaInfoIOS.put("executeurl", "snapsmobilekr" + "://sharekakaotalk?prdcode=" + Config.getPROD_CODE() + "&prjcode=" + Config.getPROJ_CODE());
//
//        // add to array
//        metaInfoArray.add(metaInfoAndroid);
//        metaInfoArray.add(metaInfoIOS);
//
//        return metaInfoArray;
//    }
}
