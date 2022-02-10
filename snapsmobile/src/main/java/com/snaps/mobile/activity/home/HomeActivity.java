package com.snaps.mobile.activity.home;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import androidx.core.app.ActivityCompat;
import android.view.View;

import com.snaps.common.data.interfaces.ISnapsApplication;
import com.snaps.common.deeplink.DeeplinkService;
import com.snaps.common.structure.SnapsHandler;
import com.snaps.common.structure.SnapsTemplateManager;
import com.snaps.common.utils.ISnapsHandler;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.SnapsConfigManager;
import com.snaps.common.utils.constant.SnapsProductInfoManager;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.net.CNetStatus;
import com.snaps.common.utils.pref.Setting;
import com.snaps.common.utils.sound.SnapsSoundPlayer;
import com.snaps.common.utils.system.SystemUtil;
import com.snaps.common.utils.system.ViewUnbindHelper;
import com.snaps.common.utils.ui.ContextUtil;
import com.snaps.common.utils.ui.DataTransManager;
import com.snaps.common.utils.ui.FontUtil;
import com.snaps.common.utils.ui.IFacebook;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.kakao.utils.share.SNSShareUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.google_style_image_selector.utils.ImageSelectManager;
import com.snaps.mobile.activity.hamburger_menu.SnapsMenuManager;
import com.snaps.mobile.activity.home.fragment.GoHomeOpserver;
import com.snaps.mobile.activity.home.interfacies.ISnapsHomeActStateChangeListener;
import com.snaps.mobile.activity.home.ui_strategies.HomeUIBase;
import com.snaps.mobile.activity.home.ui_strategies.HomeUIFactory;
import com.snaps.mobile.activity.home.ui_strategies.HomeUIHandler;
import com.snaps.mobile.activity.home.utils.HomeDevelopMenuHandler;
import com.snaps.mobile.activity.home.utils.HomeReceiverHandler;
import com.snaps.mobile.activity.home.utils.HomeThirdPartyLibHandler;
import com.snaps.mobile.activity.home.utils.KakaoEventReceiver;
import com.snaps.mobile.activity.home.utils.LoginReceiver;
import com.snaps.mobile.activity.home.utils.SnapsEventHandler;
import com.snaps.mobile.activity.home.utils.SnapsLoginManager;
import com.snaps.mobile.activity.home.utils.push_handlers.SnapsPushHandleData;
import com.snaps.mobile.activity.ui.menu.renewal.MenuDataManager;
import com.snaps.mobile.activity.ui.menu.webinterface.SnapsWebEventHandlerFactory;
import com.snaps.mobile.autosave.AutoSaveManager;
import com.snaps.mobile.base.SnapsBaseFragmentActivity;
import com.snaps.mobile.order.order_v2.util.org_image_upload.upload_fail_handle.SnapsUploadFailedImageDataCollector;
import com.snaps.mobile.service.SnapsPhotoUploader;
import com.snaps.mobile.utils.smart_snaps.SmartSnapsManager;
import com.snaps.mobile.utils.sns.googlephoto.GooglePhotoUtil;

import errorhandle.CatchSyncker;
import errorhandle.logger.SnapsLogger;

import static com.snaps.mobile.autosave.IAutoSaveConstants.SETTING_KEY_AUTO_SAVE_RECOVERY_TRY_COUNT;

public class HomeActivity extends SnapsBaseFragmentActivity implements ISnapsHandler {
    private static final String TAG = HomeActivity.class.getSimpleName();
    private HomeReceiverHandler homeReceiverHandler = null;
    private HomeUIHandler homeUIHandler = null;
    private SnapsEventHandler eventHandler = null;
    private HomeThirdPartyLibHandler homeThirdPartyLibHandler = null;
    private SnapsHandler snapsHandler = null;
    private HomeDevelopMenuHandler homeDevelopMenuHandler = null;
    private ISnapsHomeActStateChangeListener homeActStateChangeListener = null;

    private long lOnNewIntentTime = 0;
    private boolean isFirstLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        initialize();

        performBaseTasksOnAppLaunched();
    }

    private void initialize() {
        createHandlers();

        registerReceivers();

        setDefaultPreferences();

        initUtils();

        initHomeUIControls();

//        initIgawLiveOps();

        registerListeners();

        registerGCMInfoToSnapsServer();

        isFirstLoaded = true;
    }

    private void createHandlers() {
        snapsHandler = new SnapsHandler(this);

        eventHandler = new SnapsEventHandler(this);

        homeUIHandler = HomeUIFactory.createHomeUIWithEventHandler(this, eventHandler);

        homeReceiverHandler = new HomeReceiverHandler.Builder(this)
                .setKakaoReceiver(KakaoEventReceiver.createInstanceWithEventHandler(eventHandler))
                .setLoginReceiver(new LoginReceiver(homeUIHandler))
                .create();

        homeThirdPartyLibHandler = new HomeThirdPartyLibHandler(this);
    }

    private void registerReceivers() {
        try {
            homeReceiverHandler.registerReceivers();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private void setDefaultPreferences() {
        Config.setAppProcess(true);
    }

    private void initHomeUIControls() {
        homeUIHandler.initialize();
    }

    private void registerListeners() {
        setHomeActStateChangeListener();
    }

    private void registerGCMInfoToSnapsServer() {
        if (homeReceiverHandler != null) {
            homeReceiverHandler.registerGCMInfoToSnapsServer(this);
        }
    }

    private void setHomeActStateChangeListener() {
        homeActStateChangeListener = MenuDataManager.getInstance();
    }

    private void performBaseTasksOnAppLaunched() {
        handleReceivedIntentData(getIntent());

        checkInstallEvent();

        AutoSaveManager.checkAutoSavedFiles();

        checkSnapsInnerPush();

        startPhotoPrintService();
    }

    private void handleReceivedIntentData(Intent intent) {
        try {
            if (isFromKakaoLink(intent)) {
                if (eventHandler != null) {
                    eventHandler.setLaunchFromKakaoLink(true);
                }
                return; //로그인 하고 나서 처리 하도록 한다.
            }

            DeeplinkService deeplinkService = new DeeplinkService();
            deeplinkService.putExtraDataIfHostIsExecGoto(intent);

            SnapsPushHandleData pushHandleData = new SnapsPushHandleData.Builder().setSnapsHandler(snapsHandler).setEventHandler(eventHandler).setHomeUIHandler(homeUIHandler).setIntent(intent).create();
            homeReceiverHandler.handleIntentDataOnReceivedPush(this, pushHandleData);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private boolean isFromKakaoLink(Intent intent) {
        if (intent == null) {
            return false;
        }

        String dataStr = intent.getDataString();
        return !StringUtil.isEmpty(dataStr) && dataStr.contains(Config.getKAKAO_SCHEMA());
    }

    private void checkInstallEvent() {
        try {
            eventHandler.checkInstallEvent();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private void checkSnapsInnerPush() {
        try {
            homeUIHandler.checkInnerPopup();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private void startPhotoPrintService() {
        SnapsPhotoUploader.getInstance(getApplicationContext());
    }

    private void checkDevelopVersion() {
        if (!Config.isDevelopVersion()) {
            return;
        }

        View menuBtn = findViewById(R.id.activity_home_develop_menu_btn);
        if (menuBtn != null) {
            menuBtn.setVisibility(View.VISIBLE);
            menuBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDevelopMenu();
                }
            });
        }
    }

    private void showDevelopMenu() {
        if (homeDevelopMenuHandler == null) {
            homeDevelopMenuHandler = new HomeDevelopMenuHandler(this);
        }
        homeDevelopMenuHandler.showMenu();
    }

//    private void initIgawLiveOps() {
//        try {
//            homeThirdPartyLibHandler.initIgawLiveOps(new LiveOpsDeepLinkEventListener() {
//                @Override
//                public void onReceiveDeeplinkData(String uri) {
//                    processIntentDataOnReceivePush(uri);
//                }
//            });
//        } catch (Exception e) {
//            Dlog.e(TAG, e);
//        }
//    }

    private Intent convertUriStrToIntent(String uriStr) throws Exception {
        if (StringUtil.isEmpty(uriStr)) {
            return null;
        }
        Uri uri = Uri.parse(uriStr);
        Intent itt = new Intent();
        itt.setData(uri);
        return itt;
    }

    // 초기화가 필요한 매니저들을 초기화.
    private void initUtils() {
        // 데이터 전송용
        DataTransManager.createInstance(this);

        // 공통적으로 리소스에 접근할 필요가 있을 경우
        ContextUtil.setSubContext(this);

        // 에러 관리
        CatchSyncker.createInstance();
        CatchSyncker.getInstance().setMainActivity(this);

        // 자동 저장
        AutoSaveManager.createInstance(this);

        // service 실행
        SnapsPhotoUploader.getInstance(getApplicationContext());
    }

    @Override
    protected void onResume() {
        if (SystemUtil.isActivityFinishing(this)) {
            SnapsLogger.sendTextLog("HomeActivity/onResume", "IllegalArgumentException called.");
        }

        super.onResume();
        if (homeActStateChangeListener != null) {
            homeActStateChangeListener.onHomeActResume();
        }

        if (homeUIHandler != null) {
            homeUIHandler.handleOnResume();
        }

        tryAutoLogInOnFirstAppLoaded();

        checkDevelopVersion();
    }

    private void tryAutoLogInOnFirstAppLoaded() {
        final boolean shouldFromAutoLogin = eventHandler != null && eventHandler.isLaunchFromKakaoLink(); //카카오링크로 들어오면, 무조건 자동 로그인을 시도해 본다.(카카오이벤트 결과값에 따라 UI가 다르다.)
        if (!shouldFromAutoLogin && (!isFirstLoaded || SnapsLoginManager.isEmptyLoginHistory(this))) {
            return;
        }

        isFirstLoaded = false;

        SnapsLoginManager.getUserInfo(this, new SnapsLoginManager.OnGetUserInfoListener() {
            @Override
            public void onGetUserInfo(boolean success) {

                if (!success) {
                    CNetStatus netStatus = CNetStatus.getInstance();
                    boolean isNetworkErr = !netStatus.isAliveNetwork(HomeActivity.this);
                    if (isNetworkErr) {
                        MessageUtil.toast(HomeActivity.this, R.string.login_fail_because_network);
                    } else {
                        if (!SnapsLoginManager.isEmptyLoginHistory(HomeActivity.this)) {
                            MessageUtil.toast(HomeActivity.this, R.string.login_fail_only);
                        }
                    }
                }

                if (eventHandler != null && eventHandler.isLaunchFromKakaoLink()) {
                    eventHandler.setLaunchFromKakaoLink(false);
                    eventHandler.checkKakaoEvent();
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

//        SnapsAdbrix.endSession();

        if (homeActStateChangeListener != null) {
            homeActStateChangeListener.onHomeActPause();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Config.isFacebookService()) {
            if (homeThirdPartyLibHandler != null) {
                IFacebook facebook = homeThirdPartyLibHandler.getFacebook();
                if (facebook != null) {
                    facebook.addCallback();
                }
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (snapsHandler != null) {
            snapsHandler.removeMessages(HANDLE_MSG_CLOSE_FLAG);
        }

        if (Config.isFacebookService()) {
            IFacebook facebook = homeThirdPartyLibHandler.getFacebook();
            if (facebook != null) {
                facebook.removeCallback();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        finalizeApplication();
    }

    private void finalizeApplication() {
        try {
            GooglePhotoUtil.finalizeInstance();

            initKakaoEventSenderUserId();

            unRegisterAllReceivers();

            finalizeUtils();

            SystemUtil.removeNotification(this);

            ViewUnbindHelper.unbindReferences(getWindow().getDecorView());

            ((ISnapsApplication) getApplication()).requestGCMRegistrarDestroy();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private void initKakaoEventSenderUserId() {
        if (eventHandler != null) {
            eventHandler.initKakaoEventSenderUserId();
        }
    }

    private void unRegisterAllReceivers() {
        unRegisterKakaoReceiver();
        unRegisterLoginReceiver();
    }

    private void unRegisterKakaoReceiver() {
        try {
            homeReceiverHandler.unRegisterKakaoReceiver();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private void unRegisterLoginReceiver() {
        try {
            homeReceiverHandler.unRegisterLoginReceiver();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    // 초기화 했던 매니저들의 메모리 해제.
    private void finalizeUtils() {
        FontUtil.finalizeInstance();

        GoHomeOpserver.removeAllGoHomeListenrer();

        DataTransManager.releaseInstance();

        ContextUtil.finalizeInstance();

        CatchSyncker.finalizeInstance();

        SnapsMenuManager.finalizeInstance();

        AutoSaveManager.finalizeInstance();

        SnapsConfigManager.finalizeInstance();

        SnapsProductInfoManager.finalizeInstance();

        MenuDataManager.finalizeInstance();

        SnapsWebEventHandlerFactory.finalizeHandler();

        SnapsTemplateManager.finalizeInstance();

        SnapsUploadFailedImageDataCollector.finalizeInstance();

        SnapsLoginManager.finalizeInstance();

        SmartSnapsManager.finalizeInstance();

        ImageSelectManager.finalizeInstance();

        SnapsSoundPlayer.finalizeInstance();
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        SnapsLogger.appendTextLog("HomeActivity", "onRestoreInstanceState was called.");

        forceAppFinish();
    }

    //!!! 강제로 앱을 종료 시킬 때 사용. 강제 종료 되었을때, 모든 프로세스를 종료 시키기 위해서만 사용.
    public void forceAppFinish() {
        try {
            GoHomeOpserver.notifyGoHome();

            moveTaskToBack(true);
            ActivityCompat.finishAffinity(this);

            //강제로 프로세스를 죽이면, 에러 로그 수집이 잘 안된다....
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (snapsHandler != null) {
                        snapsHandler.sendEmptyMessageDelayed(HANDLE_MSG_FORCE_APP_KILL, 1000);
                    }
                }
            });
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (Config.isFacebookService()) {
            if (homeThirdPartyLibHandler != null) {
                IFacebook facebook = homeThirdPartyLibHandler.getFacebook();
                if (facebook != null) {
                    facebook.saveInstance(outState);
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        SNSShareUtil.postActivityForResult(requestCode, resultCode, data);

        if (Config.isFacebookService()) {
            IFacebook facebook = homeThirdPartyLibHandler.getFacebook();
            if (facebook != null) {
                facebook.onActivityResult(this, requestCode, resultCode, data);
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (System.currentTimeMillis() - lOnNewIntentTime > 1500) {
            lOnNewIntentTime = System.currentTimeMillis();
            processIntentDataOnReceivePush(intent);
        }

        //FIXME...앱이 켜져 있을 때, 카톡 등으로 실행되면 변수 초기화가 안된다. 이에 따른 문제가 없는 지 전체적으로 점검을 할 필요가 있다.
        initEventInfo();
    }

    private void initEventInfo() {
        if (eventHandler != null) {
            eventHandler.initCheckKakaoEvent();
        }
    }

    private void processIntentDataOnReceivePush(Intent intent) {
        try {
            initOnReceivedPush();

            handleReceivedIntentData(intent);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private void initOnReceivedPush() {
        if (homeReceiverHandler != null) {
            homeReceiverHandler.initOnReceivedPush(this);
        }
    }

    private void processIntentDataOnReceivePush(String uriStr) {
        try {
            Intent intent = convertUriStrToIntent(uriStr);
            if (intent != null) {
                handleReceivedIntentData(intent);
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    @Override
    public void onBackPressed() {
        if (SnapsUploadFailedImageDataCollector.isShowingUploadFailPopup()) {
            return;
        }

        if (eventHandler != null) {
            eventHandler.removeEventView();
        }

        if (homeUIHandler != null) {
            homeUIHandler.handleOnBackPressed(new HomeUIBase.ISnapsFinishCheckListener() {
                @Override
                public void performSnapsFinish() {
                    HomeActivity.super.onBackPressed();

                    Setting.set(HomeActivity.this, SETTING_KEY_AUTO_SAVE_RECOVERY_TRY_COUNT, 0);

                    forceAppFinish();
                }

                @Override
                public void requestClearAppFinishCheckFlag() {
                    if (snapsHandler != null) {
                        snapsHandler.sendEmptyMessageDelayed(HANDLE_MSG_CLOSE_FLAG, 2000);
                    }
                }
            });
        }
    }

    public void onClick(View v) {
        UIUtil.blockClickEvent(v, UIUtil.DEFAULT_CLICK_BLOCK_TIME);

        int id = v.getId();
        if (id == R.id.txtBadgeMenu || id == R.id.txtCartBadge || id == R.id.txtBadgeMenuLy) {
            SnapsMenuManager.goToCartList(HomeActivity.this, homeUIHandler);
        } else if (id == R.id.img_present) {
            SnapsMenuManager.gotoPresentPage(this, null, null);
        } else if (id == R.id.img_diary_layout) {
            SnapsMenuManager.gotoDiaryList(this);
        } else if (id == R.id.btnTopMenu || id == R.id.btnTopMenuLy) {
            SnapsMenuManager.showHamburgerMenu(this, SnapsMenuManager.eHAMBURGER_ACTIVITY.HOME);
        }
    }

    public static final int HANDLE_MSG_UNREGISTER_KAKAO_RECEIVER = 0;
    private static final int HANDLE_MSG_CLOSE_FLAG = 1;
    private static final int HANDLE_MSG_FORCE_APP_KILL = 2;

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case HANDLE_MSG_CLOSE_FLAG:
                if (homeUIHandler != null) {
                    homeUIHandler.clearAppFinishCheckFlag();
                }
                break;
            case HANDLE_MSG_FORCE_APP_KILL:
                android.os.Process.killProcess(android.os.Process.myPid());
                break;
            case HANDLE_MSG_UNREGISTER_KAKAO_RECEIVER:
                unRegisterKakaoReceiver();
                break;
        }
    }

    public HomeUIHandler getHomeUIHandler() {
        return homeUIHandler;
    }
}

