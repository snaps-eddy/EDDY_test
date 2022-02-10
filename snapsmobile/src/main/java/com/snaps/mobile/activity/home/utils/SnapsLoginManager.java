package com.snaps.mobile.activity.home.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.snaps.common.trackers.SnapsAppsFlyer;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.constant.Const_VALUES;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.net.CNetStatus;
import com.snaps.common.utils.net.xml.GetParsedXml;
import com.snaps.common.utils.net.xml.bean.Xml_SnapsLoginInfo;
import com.snaps.common.utils.net.xml.bean.Xml_UserInfo;
import com.snaps.common.utils.pref.Setting;
import com.snaps.common.utils.system.SystemUtil;
import com.snaps.common.utils.thread.ATask;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.activity.hamburger_menu.SnapsHamburgerMenuActivity;
import com.snaps.mobile.activity.hamburger_menu.SnapsMenuManager;
import com.snaps.mobile.utils.pref.PrefUtil;

import org.json.JSONObject;

import errorhandle.logger.Logg;
import errorhandle.logger.SnapsInterfaceLogDefaultHandler;

/**
 * Created by ysjeong on 2017. 8. 22..
 */

public class SnapsLoginManager {
    private static final String TAG = SnapsLoginManager.class.getSimpleName();

    public interface OnGetUserInfoListener {
        void onGetUserInfo(boolean success);
    }

    private static volatile SnapsLoginManager gInstance = null;

    private Xml_SnapsLoginInfo xmlSnapsLoginInfo;

    private Xml_UserInfo.UserInfoData userInfo;

    private SnapsLoginManager() {
    }

    public static SnapsLoginManager getInstance() {
        if (gInstance == null)
            createInstance();

        return gInstance;
    }

    public static void createInstance() {
        if (gInstance == null) {
            synchronized (SnapsLoginManager.class) {
                if (gInstance == null) {
                    gInstance = new SnapsLoginManager();
                }
            }
        }
    }

    /**
     * 액비비티가 종료될 때 꼭 호출해 줄 것.
     */
    public static void finalizeInstance() {
        try {
            releaseAllInstances();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private static void releaseAllInstances() throws Exception {
        if (getInstance() == null) return;
        getInstance().xmlSnapsLoginInfo = null;
        getInstance().userInfo = null;
        gInstance = null;
    }

    public static String getUserName(Context context, int maxLength) {
        String name = "";
        try {
            name = Setting.getString(context, Const_VALUE.KEY_USER_INFO_USER_NAME);
            if (name == null || name.trim().length() < 1) {
                name = Setting.getString(context, Const_VALUE.KEY_SNAPS_USER_NAME);
            }
            if (name != null && name.length() > maxLength)
                name = name.substring(0, maxLength) + "..";
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        return name;
    }

    public static boolean isLogOn(Context context) {
        return !StringUtil.isEmpty(getUUserNo(context));
    }

    public static String getUUserNo(Context con) {
        return Setting.getString(con, Const_VALUE.KEY_SNAPS_USER_NO, "");
    }

    public static String getUserId(Context con) {
        return Setting.getString(con, Const_VALUE.KEY_SNAPS_USER_ID, "");
    }

    public static boolean isEmptyLoginHistory(Context context) {
        String snapsUserId = Setting.getString(context, Const_VALUE.KEY_SNAPS_USER_ID);
        String snapsUserPwd = Setting.getString(context, Const_VALUE.KEY_SNAPS_USER_PWD);
        return "".equals(snapsUserId) || "".equals(snapsUserPwd);
    }

    public static void getUserInfo(final Activity activity, final OnGetUserInfoListener snapsLoginFinishListener) {
        String userNo = Setting.getString(activity, Const_VALUE.KEY_SNAPS_USER_NO);
        final String snapsUserId = Setting.getString(activity, Const_VALUE.KEY_SNAPS_USER_ID);
        final String snapsUserPwd = Setting.getString(activity, Const_VALUE.KEY_SNAPS_USER_PWD);
        Dlog.d("getUserInfo() userNo:" + userNo + ", ID:" + snapsUserId + ", PW:" + snapsUserPwd);

        boolean isAutoLogin = !"".equals(userNo) && !"".equals(snapsUserId) && !"".equals(snapsUserPwd); // 회원가입 한 상태이고, 전에 로그인을 한 적이 없다면 자동 로그인 안함.
        if (isAutoLogin) {
            requestGetUserInfo(activity, snapsLoginFinishListener);
        } else {
            PrefUtil.clearUserInfo(activity, false);

//            registerPushInfo(activity);   //로그인 성공 하든 말든 푸쉬 서비스에 등록

            if (snapsLoginFinishListener != null) {
                snapsLoginFinishListener.onGetUserInfo(false);
            }
        }
    }

    private static void requestGetUserInfo(final Activity activity, final OnGetUserInfoListener snapsLoginFinishListener) {
        ATask.executeVoidDefProgress(activity, new ATask.OnTask() {
            boolean isSuccessLogin = false;
            String snapsUserId = Setting.getString(activity, Const_VALUE.KEY_SNAPS_USER_ID);
            String snapsUserPwd = Setting.getString(activity, Const_VALUE.KEY_SNAPS_USER_PWD);

            @Override
            public void onPre() {
            }

            @Override
            public void onBG() {
                CNetStatus netStatus = CNetStatus.getInstance();
                if (netStatus.isAliveNetwork(activity)) {
                    getInstance().xmlSnapsLoginInfo = tryLoginPrevAccountInfo();

                    isSuccessLogin = getInstance().xmlSnapsLoginInfo != null && getInstance().xmlSnapsLoginInfo.F_USER_NO != null;
                    if (isSuccessLogin) {
                        getInstance().userInfo = createUserInfoByLoginInfo();
                        saveUserInfo();

                        savePreferencesByLoginInfo();

                        setCrashlyticsUserAccountLog();
                    }
                }
            }

            @Override
            public void onPost() {
                if (isSuccessLogin) { // 로그인 성공 home으로 이동
                    registKakaoSenderNo();

//                    registerPushInfo(activity);
                } else {
//                    PrefUtil.clearUserInfo(activity, false);

                    if (snapsLoginFinishListener != null) {
                        snapsLoginFinishListener.onGetUserInfo(isSuccessLogin);
                    }
                }
            }

            // push device 등록
            private void registKakaoSenderNo() {
                ATask.executeVoidWithThreadPool(new ATask.OnTask() {
                    @Override
                    public void onPre() {
                    }

                    @Override
                    public void onBG() {
                        String userId = getInstance().xmlSnapsLoginInfo.F_USER_NO;

                        // 자동로그인이 성공을 하면 카카오톡 친구 추가 이벤트 등록 호출...
                        String sendno = PrefUtil.getKakaoSenderNo(activity);
                        // String deviceId = PrefUtil.getKakaoDeviceID(SplashActivity.this);
                        // String eventCode = PrefUtil.getKakaoEventCode(SplashActivity.this);
                        if (!sendno.equals("")) {
                            // Config.KAKAO_EVENT_RESULT = GetParsedXml.regKakaoInvite(SplashActivity.this, userId);
                            Config.setKAKAO_EVENT_RESULT(GetParsedXml.regKakaoInvite(userId, sendno, PrefUtil.getKakaoEventCode(activity), SystemUtil.getDeviceId(activity), SnapsInterfaceLogDefaultHandler.createDefaultHandler()));
                        }
                    }

                    @Override
                    public void onPost() {
                        if (snapsLoginFinishListener != null) {
                            snapsLoginFinishListener.onGetUserInfo(isSuccessLogin);
                        }
                    }
                });
            }

            private Xml_SnapsLoginInfo tryLoginPrevAccountInfo() {
                String snapsUserName1 = Setting.getString(activity, Const_VALUE.KEY_SNAPS_USER_NAME1);
                String snapsUserName2 = Setting.getString(activity, Const_VALUE.KEY_SNAPS_USER_NAME2);
                String snapsLoginType = Setting.getString(activity, Const_VALUE.KEY_SNAPS_LOGIN_TYPE, Const_VALUES.SNAPSLOGIN_SNAPS);
                return GetParsedXml.snapsLogin(activity, snapsUserId, snapsUserPwd, snapsUserName1, snapsUserName2, snapsLoginType);
            }

            private void savePreferencesByLoginInfo() {
                Setting.set(activity, Const_VALUE.KEY_EVENT_TERM, "true".equalsIgnoreCase(getInstance().xmlSnapsLoginInfo.F_EVENT_TERM));
                Setting.set(activity, Const_VALUE.KEY_EVENT_COUPON, "true".equalsIgnoreCase(getInstance().xmlSnapsLoginInfo.F_COUPON));
                Setting.set(activity, Const_VALUE.KEY_EVENT_FILE_PATH, getInstance().xmlSnapsLoginInfo.F_FILE_PATH);

                Setting.set(activity, Const_VALUE.KEY_SNAPS_USER_NO, getInstance().xmlSnapsLoginInfo.F_USER_NO);// userno
                // 저장
                Setting.set(activity, Const_VALUE.KEY_SNAPS_USER_NAME, getInstance().xmlSnapsLoginInfo.F_USER_NAME);
                Setting.set(activity, Const_VALUE.KEY_USER_AUTH, getInstance().xmlSnapsLoginInfo.F_USER_AUTH);
                Setting.set(activity, Const_VALUE.KEY_USER_PHONENUMBER, getInstance().xmlSnapsLoginInfo.F_USER_PHONENUMBER);
                Setting.set(activity, Const_VALUE.KEY_SNAPS_AI, getInstance().xmlSnapsLoginInfo.F_USER_AI_SYNC);
                Setting.set(activity, Const_VALUE.KEY_SNAPS_AI_TOS_AGREEMENT, getInstance().xmlSnapsLoginInfo.F_USER_AI_TOS_AGREE);

                Setting.set(activity, Const_VALUE.KEY_SNAPS_USER_ID, snapsUserId);
                Setting.set(activity, Const_VALUE.KEY_SNAPS_USER_PWD, snapsUserPwd);

                // 이벤트 쿠폰을 받은 적이 있는 지
                Setting.set(activity, Const_VALUE.KEY_EVENT_DEVICE, getInstance().xmlSnapsLoginInfo.F_DEVICE);
            }

            private Xml_UserInfo.UserInfoData createUserInfoByLoginInfo() {
                //굳이 인터페이스 통할 필요가 없어서 수정 함.
                String userNo = getInstance().xmlSnapsLoginInfo.F_USER_NO;
                String id = getInstance().xmlSnapsLoginInfo.F_USER_ID;
                String userName = getInstance().xmlSnapsLoginInfo.F_USER_NAME;
                String userLvName = "";//jsonObject.getString("F_USER_LVL_NAME"); 디자인 리뉴얼로 필요 없어졌다..
                String userDesc = "";//jsonObject.getString("F_COUPON_DESC"); 디자인 리뉴얼로 필요 없어졌다..
                String userLv = getInstance().xmlSnapsLoginInfo.F_USER_LVL;
                return new Xml_UserInfo.UserInfoData(userNo, id, userName, userLvName, userDesc, userLv);
            }

            private void setCrashlyticsUserAccountLog() {
                if (Config.isRealServer()) {
//                    Crashlytics.setUserIdentifier(getInstance().xmlSnapsLoginInfo.F_USER_ID);
//                    Crashlytics.setUserName(getInstance().xmlSnapsLoginInfo.F_USER_NAME);
                    FirebaseCrashlytics.getInstance().setUserId(getInstance().xmlSnapsLoginInfo.F_USER_ID);
                }
            }

            private void saveUserInfo() {
                Setting.set(activity, Const_VALUE.KEY_USER_INFO_USER_NAME, getInstance().userInfo.F_USER_NAME);
                Setting.set(activity, Const_VALUE.KEY_USER_INFO_GRADE_CODE, getInstance().userInfo.F_USER_LVL);
                Setting.set(activity, Const_VALUE.KEY_USER_INFO_EVT_DESC, getInstance().userInfo.F_COUPON_DESC);
            }
        });
    }

    public Xml_SnapsLoginInfo getXmlSnapsLoginInfo() {
        return xmlSnapsLoginInfo;
    }

    public void setXmlSnapsLoginInfo(Xml_SnapsLoginInfo xmlSnapsLoginInfo) {
        this.xmlSnapsLoginInfo = xmlSnapsLoginInfo;
    }

    public Xml_UserInfo.UserInfoData getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(Xml_UserInfo.UserInfoData userInfo) {
        this.userInfo = userInfo;
    }

    public static boolean tryLogInIfChangedPwdScheme(Activity activity, String dataString) throws Exception {
        if (Const_VALUE.APPSCHEME_CHGEDPWD.equals(dataString) && "".equals(getUUserNo(activity))) {
            SnapsLoginManager.startLogInProcess(activity, Const_VALUES.LOGIN_P_LOGIN);
            return true;
        }
        return false;
    }

    public static boolean tryLogInIfVerifyCompleteWithIntent(Activity activity, Intent intent) throws Exception {
        //비로그인 추가인증 시 로그인화면 띄우기
        boolean isGoLogin = intent.getBooleanExtra("verifyComplete", false);
        if (isGoLogin) {
            SnapsLoginManager.startLogInProcess(activity, Const_VALUES.LOGIN_P_LOGIN);
            return true;
        }
        return false;
    }

    public static void startLogInProcess(Activity activity, String loginProcess) {
        startLogInProcess(activity, loginProcess, null);
    }

    public static void startLogInProcess(Activity activity, String loginProcess, Bundle bundle) {
        startLogInProcess(activity, loginProcess, bundle, -1);
    }

    public static void startLogInProcess(Activity activity, String loginProcess, Bundle bundle, int activityForResultCode) {
        if (activity == null) return;

        if (bundle == null) bundle = new Bundle();

        SnapsMenuManager.eHAMBUGER_FRAGMENT where = null;
        if (Const_VALUES.LOGIN_P_LOGIN.equals(loginProcess) || Const_VALUES.LOGIN_P_RESULT.equals(loginProcess)) {// 로그인부터
            where = SnapsMenuManager.eHAMBUGER_FRAGMENT.LOG_IN;
        } else if (Const_VALUES.LOGIN_P_JOIN.equals(loginProcess)) {// 회원가입부터
            where = SnapsMenuManager.eHAMBUGER_FRAGMENT.JOIN;
        } else if (Const_VALUES.LOGIN_P_RETIRE.equals(loginProcess)) {// 회원탈퇴
            where = SnapsMenuManager.eHAMBUGER_FRAGMENT.RETIRE;
        } else if (Const_VALUES.LOGIN_P_PWDRESET.equals(loginProcess)) {// 회원탈퇴
            where = SnapsMenuManager.eHAMBUGER_FRAGMENT.PWD_RESET;
        } else if (Const_VALUES.LOGIN_P_PWDFIND.equals(loginProcess)) {// 비번 찾
            where = SnapsMenuManager.eHAMBUGER_FRAGMENT.PWD_FIND;
        } else if (Const_VALUES.LOGIN_P_VERRIFY.equals(loginProcess)) {//추가인증
            where = SnapsMenuManager.eHAMBUGER_FRAGMENT.VERIFY_PHONE;
        } else if (Const_VALUES.LOGIN_P_VERRIFY_POPUP.equals(loginProcess)) {//추가인증
            where = SnapsMenuManager.eHAMBUGER_FRAGMENT.VERIFY_PHONE_POPUP;
        } else if (Const_VALUES.LOGIN_P_REST_ID.equals(loginProcess)) {//휴먼계정 해제
            where = SnapsMenuManager.eHAMBUGER_FRAGMENT.REST_ID;
        }

        Intent intent = new Intent(activity, SnapsHamburgerMenuActivity.class);

        bundle.putInt(Const_VALUES.EXTRAS_HAMBURGER_MENU_FRG, where != null ? where.ordinal() : -1);
//		bundle.putInt(Const_VALUES.EXTRAS_HAMBURGER_MENU_START_ANIM_IN, R.anim.anim_fade_in);
        bundle.putInt(Const_VALUES.EXTRAS_HAMBURGER_MENU_START_ANIM_IN, 0);
        bundle.putInt(Const_VALUES.EXTRAS_HAMBURGER_MENU_START_ANIM_OUT, 0);

        intent.putExtras(bundle);

        if (activityForResultCode > -1)
            activity.startActivityForResult(intent, activityForResultCode);
        else
            activity.startActivity(intent);
    }

    /**
     * @param param {"data":{"isAITermsAgree":true,"isUseAI":true,"token":"eyJhbGciOiJIUzI1NiJ9.eyJjb2RlIjoiMDAxLkUuMi4wIiwidXNlck5vIjoiMzcwMzQyNDAiLCJleHAiOjE1NjM0MzcxODN9.Htn37VPgvhAl9KirZJMcoVC1XX8lmd-kpU_32eB_Z3A","userNo":37034240,"userId":"ben@snaps.com","userName":"김인규","userLevel":"NEW","snsType":"EMAIL","type":"SNAPS","isResetRestStatus":false,"duplicationList":[],"eventIdx":null,"crmPopCode":null,"userPassword":"bin12230"}}
     */
    public void putUserInfo(Context context, String param) {
        if (param == null || param.length() < 1) {
            return;
        }

        try {
            JSONObject jsonObject = new JSONObject(param);
            JSONObject dataObject = jsonObject.getJSONObject("data");
            String userId = dataObject.getString("userId");
            String userName = dataObject.getString("userName");
            String userPassword = dataObject.getString("userPassword");
            long userNo = dataObject.getLong("userNo");
            boolean isAITermsAgree = dataObject.getBoolean("isAITermsAgree");
            boolean isUseAI = dataObject.getBoolean("isUseAI");
            boolean isUseLTE = dataObject.getBoolean("isUseLTE");

            Setting.set(context, Const_VALUE.KEY_SNAPS_USER_ID, userId);
            Setting.set(context, Const_VALUE.KEY_SNAPS_USER_PWD, userPassword);
            Setting.set(context, Const_VALUE.KEY_SNAPS_USER_NAME, userName);
            Setting.set(context, Const_VALUE.KEY_SNAPS_USER_NO, String.valueOf(userNo));
            Setting.set(context, Const_VALUE.KEY_SNAPS_AI_TOS_AGREEMENT, isAITermsAgree);
            Setting.set(context, Const_VALUE.KEY_SNAPS_AI, isUseAI);
            Setting.set(context, Const_VALUE.KEY_SNAPS_AI_ALLOW_UPLOAD_MOBILE_NET, isUseLTE);

            SnapsAppsFlyer.setUserEmails(userId);

        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    public void deleteUserInfo(Context context) {
        PrefUtil.clearUserInfo(context, true);
    }
}
