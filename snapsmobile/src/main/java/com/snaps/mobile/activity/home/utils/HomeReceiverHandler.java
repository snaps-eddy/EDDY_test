package com.snaps.mobile.activity.home.utils;

import android.app.Activity;
import android.content.IntentFilter;

import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.net.http.HttpReq;
import com.snaps.mobile.activity.hamburger_menu.SnapsMenuManager;
import com.snaps.mobile.activity.home.utils.push_handlers.ISnapsPushHandler;
import com.snaps.mobile.activity.home.utils.push_handlers.ISnapsPushHandlerIterator;
import com.snaps.mobile.activity.home.utils.push_handlers.SnapsPushHandleData;
import com.snaps.mobile.activity.home.utils.push_handlers.SnapsPushHandler;

import errorhandle.logger.SnapsInterfaceLogDefaultHandler;

/**
 * Created by ysjeong on 2017. 8. 17..
 */

public class HomeReceiverHandler {
    private static final String TAG = HomeReceiverHandler.class.getSimpleName();
    private Activity activity = null;
    private KakaoEventReceiver kakaoReceiver = null;
    private LoginReceiver loginReceiver = null;

    private HomeReceiverHandler(Builder builder) {
        this.activity = builder.activity;
        this.kakaoReceiver = builder.kakaoReceiver;
        this.loginReceiver = builder.loginReceiver;
    }

    public void initOnReceivedPush(Activity activity) {
        try {
            SnapsMenuManager.initPage();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        registerGCMInfoToSnapsServer(activity);
    }

    public void registerGCMInfoToSnapsServer(Activity activity) {
        if (activity == null) return;
        HttpReq.registerGCMInfoToSnapsServerOnBackground(activity, SnapsLoginManager.getUUserNo(activity), SnapsInterfaceLogDefaultHandler.createDefaultHandler());
    }

    public void registerReceivers() throws Exception {
        IntentFilter filter = new IntentFilter(Const_VALUE.INSTALL_REFERR);
        activity.registerReceiver(getKakaoReceiver(), filter);

        IntentFilter loginFilter = new IntentFilter(Const_VALUE.LOGIN_ACTION);
        loginFilter.addAction(Const_VALUE.WEBVIEW_START);
        loginFilter.addAction(Const_VALUE.WEBVIEW_END);
        loginFilter.addAction(Const_VALUE.WEBVIEW_FAIL);
        activity.registerReceiver(getLoginReceiver(), loginFilter);

    }

    public void unRegisterLoginReceiver() throws Exception {
        if (loginReceiver != null) {
            activity.unregisterReceiver(loginReceiver);
            loginReceiver = null;
        }
    }

    public void unRegisterKakaoReceiver() throws Exception {
        if (kakaoReceiver != null) {
            activity.unregisterReceiver(kakaoReceiver);
            kakaoReceiver = null;
        }
    }

    public KakaoEventReceiver getKakaoReceiver() {
        return kakaoReceiver;
    }

    public LoginReceiver getLoginReceiver() {
        return loginReceiver;
    }

    public boolean handleIntentDataOnReceivedPush(Activity activity, SnapsPushHandleData pushHandleData) throws Exception {
        ISnapsPushHandlerIterator iterator = SnapsPushHandler.createInstanceWithIntent(activity, pushHandleData);
        while (iterator.hasNext()) {
            ISnapsPushHandler pushHandler = iterator.next();
            if (pushHandler.performPushDataHandle()) return true;
        }

        iterator.releaseInstance();

        return false;
    }

    public static class Builder {
        private Activity activity = null;
        private KakaoEventReceiver kakaoReceiver = null;
        private LoginReceiver loginReceiver = null;

        public Builder(Activity activity) {
            this.activity = activity;
        }

        public Builder setActivity(Activity activity) {
            this.activity = activity;
            return this;
        }

        public Builder setKakaoReceiver(KakaoEventReceiver kakaoReceiver) {
            this.kakaoReceiver = kakaoReceiver;
            return this;
        }

        public Builder setLoginReceiver(LoginReceiver loginReceiver) {
            this.loginReceiver = loginReceiver;
            return this;
        }

        public HomeReceiverHandler create() {
            return new HomeReceiverHandler(this);
        }
    }
}
