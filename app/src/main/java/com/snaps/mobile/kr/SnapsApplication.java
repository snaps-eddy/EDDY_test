package com.snaps.mobile.kr;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;

import androidx.multidex.MultiDex;

import com.bumptech.glide.Glide;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.snaps.common.data.interfaces.ISnapsApplication;
import com.snaps.common.http.APIConnection;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.imageloader.ImageLoader;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.pref.Setting;
import com.snaps.common.utils.ui.ContextUtil;
import com.snaps.common.utils.ui.CrashlyticsBridge;
import com.snaps.common.utils.ui.SnapsAppVersionUtil;
import com.snaps.common.utils.ui.SnapsLanguageUtil;
import com.snaps.kakao.utils.kakao.KaKaoUtil;
import com.snaps.mobile.component.SnapsBroadcastReceiver;
import com.snaps.mobile.component.SnapsBroadcastReceiver.ImpSnapsBroadcastReceiver;
import com.snaps.mobile.utils.network.ip.SnapsIPManager;
import com.snaps.mobile.utils.pref.PrefUtil;
import com.snaps.mobile.utils.thirdparty.SnapsTPAppManager;

import java.util.Locale;

import dagger.hilt.android.HiltAndroidApp;
import errorhandle.CatchExceptionHandler;
import errorhandle.logger.SnapsSettingLoggerHandler;

@HiltAndroidApp
public class SnapsApplication extends Application implements ImpSnapsBroadcastReceiver, ISnapsApplication {
    private static final String TAG = SnapsApplication.class.getSimpleName();
    private SnapsThirdPartyTrackers thirdPartyTrackers = null;
    private static SnapsApplication gInstance;

    public static SnapsApplication getGlobalContext() {
        return gInstance;
    }

    //앱 종료 후 재 시작하면, Application onCreate를 다시 안 타기 때문에 SplashActivity.java 에서 초기화 한 다.
    @Override
    public void requestInitApplication() {
        gInstance = this;

        ContextUtil.createInstance(this);

        CrashlyticsBridge.createInstance(SnapsSettingLoggerHandler.createLoggerBridgeHandler());

        SnapsLanguageUtil.checkLanguageState(this);

        SnapsAppVersionUtil.checkAppVersion(this);

        setDefaultAppConfig();

        SnapsTPAppManager.initThirdPartyLibrary(this);

        SnapsGCMUtil.registerGCM(this);

        initThirdPartyTrackers();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        KaKaoUtil.initSDK(this);

        initServer();

        setIPAddress();
    }

    @Override
    public String getLauncherActivityName() {
        return SplashActivity.class.getCanonicalName();
    }

    private void initFabric() {
        // Debug 모드에선 크래시로그 수집하지 않도록
        // https://stackoverflow.com/questions/16986753/how-to-disable-crashlytics-during-development
        boolean isRealServer = "real".equalsIgnoreCase(BuildConfig.FLAVOR_api);
        boolean isEnable = isRealServer && !BuildConfig.DEBUG;
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(isEnable);
    }

    @Override
    public Context getSnapsApplication() {
        return getGlobalContext();
    }

    private void initServer() {
        boolean isRealServer = "real".equalsIgnoreCase(BuildConfig.FLAVOR_api);
        String backendAPIBase = BuildConfig.BACK_END_BASE_URL;
        String frontendAPIBase = BuildConfig.FRONT_END_BASE_URL;
        Config.initServer(this, isRealServer, backendAPIBase, frontendAPIBase);

        String currentLang = Setting.getString(this, Const_VALUE.KEY_APPLIED_LANGUAGE, Locale.getDefault().getLanguage());
        if (!SnapsLanguageUtil.isServiceableLanguage(currentLang)) {
            currentLang = Locale.ENGLISH.getLanguage();
        }

        APIConnection.getInstance().init(currentLang);
    }

    private void setDefaultAppConfig() {
        Config.initTempAddr(false);// 임시주소정보 사용여부
        Config.setIS_QA(false);
        Config.initConfig(getString(R.string.channel_code));

        initServer();

        Config.setUseDrawUndefinedFontSearchArea(BuildConfig.DEBUG);  //정의 되지 않은 폰트 영역 표시
        Config.setUseDrawSmartSnapsSearchArea(false);    //얼굴 인식 영역 표시
        Config.initLog(BuildConfig.DEBUG);// Logging 설정
        Config.useSnapsSDK2(false, this);
        Config.useFacebookService(true);
        Config.setPaymentScheme("snapskr", this);
        Config.useV3Index(true);

        boolean isDevelopVersion = "develop".equalsIgnoreCase(BuildConfig.FLAVOR_products);
        Config.setDevelopVersion(isDevelopVersion);

        PrefUtil.setEnableGooglePhoto(getApplicationContext(), true);     //구글 포토 사용 유무 설정..

        Thread.setDefaultUncaughtExceptionHandler(new CatchExceptionHandler(this));  //오류 로그 추적

        initFabric(); //crashlytics
    }

    private void initThirdPartyTrackers() {
        SnapsThirdPartyTrackers.registerAdWords(this);

//        SnapsThirdPartyTrackers.registerIgawAutoSessionTracker(this);

        try {
            thirdPartyTrackers = SnapsThirdPartyTrackers.createInstanceWithRegisterReceiver(this, this);
            thirdPartyTrackers.initGoogleAnalyticsTracker(this);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    @Override
    public void requestGCMRegistrarDestroy() {
        try {
//            GCMRegistrar.onDestroy(this);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

        ImageLoader.clearMemory(this);
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);

        Glide glide = Glide.get(this);
        if (glide != null) {
            glide.trimMemory(level);
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        // 등록이된 receiver 제거.
        unregisterReceiver();
    }

    @Override
    public void onReceiveData(Context context, Intent intent) {
        handleTrackerReceiveData(context, intent);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        SnapsLanguageUtil.handleChangedConfiguration(this, newConfig);

        super.onConfigurationChanged(newConfig);
    }

    private void handleTrackerReceiveData(Context context, Intent intent) {
        try {
            thirdPartyTrackers.handleReceiveData(context, intent);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private SnapsBroadcastReceiver getTrackerReceiver() throws Exception {
        return thirdPartyTrackers.getReceiver();
    }

    private void unregisterReceiver() {
        try {
            unregisterReceiver(getTrackerReceiver());
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private void setIPAddress() {
        SnapsIPManager.getInstance().setIPAddress();
    }
}
