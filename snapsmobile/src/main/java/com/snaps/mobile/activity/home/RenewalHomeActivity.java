package com.snaps.mobile.activity.home;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.facebook.appevents.AppEventsConstants;
import com.facebook.appevents.AppEventsLogger;
import com.snaps.common.data.interfaces.ISnapsApplication;
import com.snaps.common.deeplink.DeeplinkService;
import com.snaps.common.push.PushManager;
import com.snaps.common.structure.SnapsHandler;
import com.snaps.common.structure.SnapsTemplateManager;
import com.snaps.common.utils.ISnapsHandler;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_EKEY;
import com.snaps.common.utils.constant.Const_VALUE;
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
import com.snaps.common.utils.ui.ICustomDialogListener;
import com.snaps.common.utils.ui.IFacebook;
import com.snaps.common.utils.ui.IntentUtil;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.common.utils.ui.SystemIntentUtil;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.kakao.utils.share.SNSShareUtil;
import com.snaps.mobile.BuildConfig;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.google_style_image_selector.utils.ImageSelectManager;
import com.snaps.mobile.activity.hamburger_menu.SnapsMenuManager;
import com.snaps.mobile.activity.home.fragment.GoHomeOpserver;
import com.snaps.mobile.activity.home.interfacies.ISnapsHomeActStateChangeListener;
import com.snaps.mobile.activity.home.model.RenewalHomeWebViewInterface;
import com.snaps.mobile.activity.home.ui_strategies.HomeUIBase;
import com.snaps.mobile.activity.home.ui_strategies.HomeUIFactory;
import com.snaps.mobile.activity.home.ui_strategies.HomeUIHandler;
import com.snaps.mobile.activity.home.utils.HomeReceiverHandler;
import com.snaps.mobile.activity.home.utils.HomeThirdPartyLibHandler;
import com.snaps.mobile.activity.home.utils.KakaoEventReceiver;
import com.snaps.mobile.activity.home.utils.LoginReceiver;
import com.snaps.mobile.activity.home.utils.SnapsEventHandler;
import com.snaps.mobile.activity.home.utils.SnapsLoginManager;
import com.snaps.mobile.activity.home.utils.push_handlers.SnapsPushHandleData;
import com.snaps.mobile.activity.setting.SnapsSettingActivity;
import com.snaps.mobile.activity.themebook.SmartRecommendAIMakingActivity;
import com.snaps.mobile.activity.ui.menu.renewal.MenuDataManager;
import com.snaps.mobile.activity.ui.menu.webinterface.SnapsShouldOverrideUrlLoader;
import com.snaps.mobile.activity.ui.menu.webinterface.SnapsWebEventHandlerFactory;
import com.snaps.mobile.activity.webview.IUIWebViewActBridge;
import com.snaps.mobile.activity.webview.WebViewDialog;
import com.snaps.mobile.activity.webview.WebViewDialogOneBtn;
import com.snaps.mobile.autosave.AutoSaveManager;
import com.snaps.mobile.base.SnapsBaseFragmentActivity;
import com.snaps.mobile.cseditor.CSEditorContract;
import com.snaps.mobile.cseditor.api.GetProjectDetailIntractorImpl;
import com.snaps.mobile.cseditor.api.response.ResponseGetProjectDetail;
import com.snaps.mobile.cseditor.model.SnapsSchemeURL;
import com.snaps.mobile.cseditor.view.CSEditorHomeActivity;
import com.snaps.mobile.order.order_v2.util.org_image_upload.upload_fail_handle.SnapsUploadFailedImageDataCollector;
import com.snaps.mobile.service.SnapsPhotoUploader;
import com.snaps.mobile.service.ai.DeviceManager;
import com.snaps.mobile.utils.kakao.KakaoStoryPostingEventor;
import com.snaps.mobile.utils.pref.PrefUtil;
import com.snaps.mobile.utils.select_product_junction.SnapsSelectProductJunctionFactory;
import com.snaps.mobile.utils.select_product_junction.interfaces.ISnapsProductLauncher;
import com.snaps.mobile.utils.smart_snaps.SmartSnapsManager;
import com.snaps.mobile.utils.sns.googlephoto.GooglePhotoUtil;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import errorhandle.CatchSyncker;
import errorhandle.logger.SnapsLogger;

import static com.snaps.mobile.autosave.IAutoSaveConstants.SETTING_KEY_AUTO_SAVE_RECOVERY_TRY_COUNT;

public class RenewalHomeActivity extends SnapsBaseFragmentActivity implements RenewalHomeContract.View, ISnapsHandler, IUIWebViewActBridge {
    private static final String TAG = RenewalHomeActivity.class.getSimpleName();
    private static final int INPUT_FILE_REQUEST_CODE_LOLLIPOP = 8888;
    private HomeReceiverHandler homeReceiverHandler = null;
    private HomeUIHandler homeUIHandler = null;
    private SnapsEventHandler eventHandler = null;
    private HomeThirdPartyLibHandler homeThirdPartyLibHandler = null;
    private SnapsHandler snapsHandler = null;
    private ISnapsHomeActStateChangeListener homeActStateChangeListener = null;

    private long lOnNewIntentTime = 0;
    private boolean isFirstLoaded = false;

    private WebView mWebView;
    private WebView mChildView = null;

    private SnapsShouldOverrideUrlLoader urlLoaderForCart = null;
    private SnapsShouldOverrideUrlLoader urlLoader = null;
    boolean isProductEditMode = false;
    private ValueCallback<Uri[]> mSelectFilePathCallback; //이미지 첨부
    private String mCameraPhotoPath;

    // 이니q시스 페이이지인지 아닌지 false 아닌걸로...
    boolean mIsINISIS_Page = false;

    boolean mIsPaymentComplete = false;
    // 결제
    boolean network_error = false;

    boolean isBlockBackKey = false;

    private SharedPreferenceRepository spManager;
    private RenewalHomePresenter mPresenter;

    private BroadcastReceiver mProjectCodeReceiver = null; //adb broadcast

    public static Intent getIntent(Context context, String title, String url) {
        Intent intent = new Intent(context, RenewalHomeActivity.class);
        intent.putExtra(Const_EKEY.WEBVIEW_TITLE, title);
        intent.setData(Uri.parse(Config.getPaymentScheme(context) + "://payment"));
        intent.putExtra(Const_EKEY.WEBVIEW_URL, url);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        disableAutofill();

        setContentView(R.layout.activity_home_renewal);

        spManager = new SharedPreferenceRepository(this);

        mPresenter = new RenewalHomePresenter(spManager, new DeviceManager(this), new PushManager(this));
        mPresenter.setContext_ForDebug(getApplicationContext());
        mPresenter.setView(this);

        initWebView();
        initialize();

        performBaseTasksOnAppLaunched();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.createInstance(this);
        }

        /*
        if (SyncPhotoServiceManager.getInstance().isRunning() == false) {
            //AI 사진 동기화 리모트 서비스 시작
            //앱 백그라운드 상태로 1분 이상 있으면 OS가 서비스를 종료시킨다. (오레오 이상 버전)
            SyncPhotoServiceManager.getInstance().startService(getApplicationContext(), true, 0);
        }
        */

        registerProjectCodeReceiver();
    }

    // ADB command 이용한 프로젝트 열기 (https://snaps1.atlassian.net/wiki/spaces/RNDMOBILE/pages/1018757291/ADB+command)
    private void registerProjectCodeReceiver() {
        if (!Config.isDevelopVersion()) return;

        // "adb shell am broadcast -a com.snaps.mobile.kr.ktcs --es prjcode " + projectCode

        if (mProjectCodeReceiver != null) return;

        final String BROADCAST_MESSAGE = "com.snaps.mobile.kr.ktcs";
        final IntentFilter theFilter = new IntentFilter();
        theFilter.addAction(BROADCAST_MESSAGE);

        mProjectCodeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (!intent.getAction().equals(BROADCAST_MESSAGE)) return;

                String projectCode = intent.getExtras().getString("prjcode");
                Toast.makeText(context, "Project Code:" + projectCode, Toast.LENGTH_SHORT).show();

                GetProjectDetailIntractorImpl intractor = new GetProjectDetailIntractorImpl();
                intractor.requestGetProjectDetail(projectCode, new CSEditorContract.GetProjectDetailIntractor.OnFinishedListener() {
                    @Override
                    public void onFinished(ResponseGetProjectDetail projectDetail) {
                        if (projectDetail != null) {
                            makeResultIntent(projectDetail.getProjectCode(), projectDetail.getProductCode(), projectDetail.getTemplateCode());
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                    }

                    public void makeResultIntent(String projectCode, String productCode, String templateCode) {
                        /**
                         * 사진 인화는 template code 가 비어있다.
                         */
                        if (projectCode.isEmpty() || productCode.isEmpty()) {
                            return;
                        }

                        SnapsSchemeURL url = new SnapsSchemeURL(projectCode, productCode, templateCode);
                        String impliedURL = url.getImpliedURL();

                        Intent returnIntent = new Intent();
                        returnIntent.putExtra(CSEditorContract.CS_EDITOR_RESULT_SCHEME, impliedURL);

                        saveCurrentProjectSchema(impliedURL);

                        loadCartProductPage(impliedURL);
                    }

                    private void saveCurrentProjectSchema(String impliedURL) {
                        Setting.set(getApplicationContext(), CSEditorContract.KEY_CURRENT_PROJECT_SCHEME, impliedURL);
                    }
                });
            }
        };

        registerReceiver(mProjectCodeReceiver, theFilter);
    }

    private void unregisterProjectCodeReceiver() {
        if (!Config.isDevelopVersion()) return;

        if (mProjectCodeReceiver != null) {
            unregisterReceiver(mProjectCodeReceiver);
        }
    }


    //삼성 단말기에서
    //아래 익셉션이 간혹 발생하는 상황이다. (갤럭시 버그)
    //Fatal Exception: java.lang.NullPointerException
    //Attempt to invoke virtual method 'boolean com.android.server.autofill.RemoteFillService$PendingRequest.cancel()' on a null object reference
    //
    // https://stackoverflow.com/questions/45731372/disabling-android-o-auto-fill-service-for-an-application
    private void disableAutofill() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                getWindow().getDecorView().setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS);
            } catch (Throwable t) {
                Dlog.e(TAG, t);
            }
        }
    }

    private void initWebView() {
        //웹뷰 디버깅 - 크롬 브라우저에서 주소창에 chrome://inspect 입력
        if (BuildConfig.DEBUG) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                WebView.setWebContentsDebuggingEnabled(true);
            }
        }

        mWebView = findViewById(R.id.snaps_home_webView);

        //단말 해상도 및 시스템 설정에서 폰트 사이즈의 영향을 받지 않도록 TextZoom Level 고정, Min sdk 가 15 이기 때문에 if status 삭제.
        mWebView.getSettings().setTextZoom(100);

        mWebView.getSettings().setJavaScriptEnabled(true);

        mWebView.getSettings().setPluginState(WebSettings.PluginState.ON);

        // 몇몇 단말기에서 주소 클릭이 안된다고 해서 넣은 코드..해결이 될지는 모르겠음.
        mWebView.getSettings().setDomStorageEnabled(true);

        // 평균적으로 킷캣 이상에서는 하드웨어 가속이 성능이 좋음.
        mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.getSettings().setSupportMultipleWindows(true);

        // // 웹페이지를 화면사이즈에 맞춤.
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(true);
        // 줌컨트롤추가
        mWebView.getSettings().setBuiltInZoomControls(false);

        if (BuildConfig.DEBUG) {
            mWebView.setLongClickable(false);
            mWebView.setOnLongClickListener(v -> {
                if (mWebView.getUrl().endsWith("snaps.com/member/my")) {
                    //로그인 화면에서 아이디,비번 붙여넣기가 안되서 불편해서 수정
                    return false;
                }

                if (Config.isDevelopVersion() && BuildConfig.DEBUG) {
                    startActivityForResult(new Intent(this, CSEditorHomeActivity.class), CSEditorHomeActivity.CS_EDITOR_HOME_REQUEST_CODE);
                }
                return true;
            });
        }

        // 캐쉬를 하지 않는다..
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        // 삼성 카드 요구 사항
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                mWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

                CookieManager cookieMan = CookieManager.getInstance();
                cookieMan.setAcceptCookie(true);
                cookieMan.setAcceptThirdPartyCookies(mWebView, true);
            } catch (Exception e) {
                Dlog.e(TAG, e);
            }
        }
        // end 삼성 카드 요구 사항

        RenewalHomeWebViewInterface mWebBridgeInterface = new RenewalHomeWebViewInterface(mPresenter);
        mWebView.addJavascriptInterface(mWebBridgeInterface, "HybridApp");
        mWebView.setWebViewClient(new CustomWebViewClient());
        mWebView.setWebChromeClient(new WebChromeClientClass());

        mPresenter.onInitWebView(SystemUtil.getDeviceId(this));
        UIUtil.applyLanguage(this);
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

        homeUIHandler = HomeUIFactory.createHomeUIRenewalWithEventHandler(this, eventHandler);

        homeReceiverHandler = new HomeReceiverHandler.Builder(this)
                .setKakaoReceiver(KakaoEventReceiver.createInstanceWithEventHandler(eventHandler))
                .setLoginReceiver(new LoginReceiver(homeUIHandler))
                .create();

        homeThirdPartyLibHandler = new HomeThirdPartyLibHandler(this);

        urlLoaderForCart = SnapsShouldOverrideUrlLoader.createInstanceForCart(this, this);

        urlLoader = new SnapsShouldOverrideUrlLoader(this, homeThirdPartyLibHandler.getFacebook(), homeThirdPartyLibHandler.getKakao());
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

    @Override
    protected void onResume() {
        Dlog.d(Dlog.UI_MACRO, "WEBVIEW");
        if (SystemUtil.isActivityFinishing(this)) {
            SnapsLogger.sendTextLog("RenewalHomeActivity/onResume", "IllegalArgumentException called.");
        }

        super.onResume();
        Config.setIS_MAKE_RUNNING(false);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.getInstance().startSync();
        }

        isProductEditMode = false;


        if (homeActStateChangeListener != null) {
            homeActStateChangeListener.onHomeActResume();
        }

        if (homeUIHandler != null) {
            homeUIHandler.handleOnResume();
        }

        tryAutoLogInOnFirstAppLoaded();
    }

    private void tryAutoLogInOnFirstAppLoaded() {
        final boolean shouldFromAutoLogin = eventHandler != null && eventHandler.isLaunchFromKakaoLink(); //카카오링크로 들어오면, 무조건 자동 로그인을 시도해 본다.(카카오이벤트 결과값에 따라 UI가 다르다.)
        if (!shouldFromAutoLogin && (!isFirstLoaded || SnapsLoginManager.isEmptyLoginHistory(this))) {
            return;
        }

        isFirstLoaded = false;

        SnapsLoginManager.getUserInfo(this, success -> {
            if (!success) {
                CNetStatus netStatus = CNetStatus.getInstance();
                boolean isNetworkErr = !netStatus.isAliveNetwork(RenewalHomeActivity.this);
                if (isNetworkErr) {
                    MessageUtil.toast(RenewalHomeActivity.this, R.string.login_fail_because_network);
                }
//                else {
//                    if (!SnapsLoginManager.isEmptyLoginHistory(RenewalHomeActivity.this)) {
////                        MessageUtil.toast(RenewalHomeActivity.this, R.string.login_fail_only);
//                    }
//                }
            }

            if (eventHandler != null && eventHandler.isLaunchFromKakaoLink()) {
                eventHandler.setLaunchFromKakaoLink(false);
                eventHandler.checkKakaoEvent();
            }
        });
    }

    /**
     * RenewalHomeContract.View 구현코드
     * All WebView methods must be called on the same thread.
     */
    @Override
    public void reloadWebPage() {
        if (mWebView == null) {
            return;
        }
        runOnUiThread(() -> mWebView.reload());
    }

    @Override
    public void loadWebPage(String url) {
        if (mWebView == null) {
            return;
        }
        runOnUiThread(() -> mWebView.loadUrl(url));
    }

    @Override
    public void loadWebPage(String url, long delay) {
        if (mWebView == null) {
            return;
        }
        runOnUiThread(() -> mWebView.postDelayed(() -> mWebView.loadUrl(url), delay));
    }

    @Override
    public void loadCartProductPage(String snapsURL) {
        if (urlLoaderForCart == null) {
            return;
        }
        runOnUiThread(() -> urlLoaderForCart.shouldOverrideUrlLoading(mWebView, snapsURL, null));
    }

    @Override
    public void loadProductPage(String snapsURL) {
        if (urlLoader == null) {
            return;
        }
        runOnUiThread(() -> urlLoader.shouldOverrideUrlLoading(mWebView, snapsURL, null));
    }

    @Override
    public void startDiaryListActivity() {
        runOnUiThread(() -> SnapsMenuManager.gotoDiaryList(this));
    }

    @Override
    public void startSettingActivity() {
        runOnUiThread(() -> {
            Intent intent = new Intent(this, SnapsSettingActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            IntentUtil.startActivity(this, intent);
        });
    }

    @Override
    public void syncWebViewCookie() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.getInstance().sync();
        } else {
            CookieManager.getInstance().flush();
        }
    }

    @Override
    public void startSmartRecommendAIMakingActivity() {
        runOnUiThread(() -> {
            Intent intent = new Intent(this, SmartRecommendAIMakingActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });
    }

    @Override
    public void startSelfAIPhotoBook() {
        String prodKey = Config.getPROD_CODE();
//        ISnapsProductLauncher launcher = SnapsSelectProductJunctionFactory.createProductLauncher(prodKey);
        ISnapsProductLauncher launcher = SnapsSelectProductJunctionFactory.getInstance().createProductLauncher(prodKey);

        if (launcher != null) {
            launcher.startMakeProduct(this, null);
        }

        // SnapsWebEventSelectProductHandler.java 에서 가져옴
        AppEventsLogger logger = AppEventsLogger.newLogger(this);
        Bundle params = new Bundle();
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, Config.getPROD_CODE());
        logger.logEvent(AppEventsConstants.EVENT_NAME_VIEWED_CONTENT, 0, params);
    }

    @Override
    public void putUserInfo(String param) {
        SnapsLoginManager.getInstance().putUserInfo(this, param);
    }

    @Override
    public void deleteUserInfo() {
        SnapsLoginManager.getInstance().deleteUserInfo(this);
    }

    @Override
    public void showToast(String message) {
        runOnUiThread(() -> Toast.makeText(this, message, Toast.LENGTH_SHORT).show());
    }

    @Override
    public void showAlertDialog(int messageId) {
        runOnUiThread(() -> MessageUtil.alert(this, messageId));
    }

    @Override
    public void executeDoubleClickBackbutton() {
        if (homeUIHandler != null) {
            homeUIHandler.handleOnBackPressed(new HomeUIBase.ISnapsFinishCheckListener() {
                @Override
                public void performSnapsFinish() {
                    RenewalHomeActivity.super.onBackPressed();

                    Setting.set(RenewalHomeActivity.this, SETTING_KEY_AUTO_SAVE_RECOVERY_TRY_COUNT, 0);

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

    @Override
    public void executeWebViewGoBack() {
        mWebView.goBack();
    }

    @Override
    public void showPushServiceConfirmDialog(String permissionName) {
        mPresenter.onShowPushServiceConfirmDialog();

        runOnUiThread(() -> MessageUtil.showPushAgreeInfo(this, true, clickedOk -> {
            String userNo = spManager.getString(Const_VALUE.KEY_SNAPS_USER_NO);
            PrefUtil.setGCMAgreeUserno(this, userNo);
            mPresenter.onPermissionDialogButtonClicked(permissionName, true);
        }));
    }

    @Override
    public void showSetFilePermissionInfo(String permissionName) {
        runOnUiThread(() -> MessageUtil.alert(this,
                getString(R.string.need_to_permission_accept_for_get_phone_pictures),
                "", R.string.cancel, R.string.confirm_move_to_setting,
                false, clickedOk -> {
                    if (clickedOk == ICustomDialogListener.OK) {
                        SystemIntentUtil.showSystemSetting(this);
                    }
                    mPresenter.onPermissionDialogButtonClicked(permissionName, false);
                }));
    }

    //2020.01.04 Ben 배경 제거 서비스 파일 저장 관련
    @Override
    public void showSetFileWritePermissionInfo() {
        runOnUiThread(() -> MessageUtil.alert(this,
                getString(R.string.need_to_permission_accept_for_get_phone_pictures),
                "", R.string.cancel, R.string.confirm_move_to_setting,
                false, clickedOk -> {
                    if (clickedOk == ICustomDialogListener.OK) {
                        SystemIntentUtil.showSystemSetting(this);
                    }
                    mPresenter.onFileWritePermissionDialogButtonClicked(false);
                }));
    }

    //2020.01.04 Ben 배경 제거 서비스 파일 저장 관련
    @Override
    public void rescanMediaScanner(File file) {
        MediaScannerConnection.scanFile(getApplicationContext(), new String[]{file.getAbsolutePath()}, null,
                (path, uri) -> {
                    if (path == null || uri == null) {
                        Dlog.e(TAG, "rescanMediaScanner() onScanCompleted [path == null || uri == null]");
                        return;
                    }
                    Dlog.i(TAG, "rescanMediaScanner() onScanCompleted : " + uri.getPath());
                    runOnUiThread(() -> Toast.makeText(getApplicationContext(), getString(R.string.eraser_background_file_save), Toast.LENGTH_SHORT).show());
                });
    }

    /**
     * RenewalHomeContract.View 구현코드 End.
     */

    public class CustomWebViewClient extends WebViewClient {

        @Override
        public void onReceivedError(final WebView view, int errorCode, String description, String failingUrl) {
            network_error = true;
            isBlockBackKey = false;
            super.onReceivedError(view, errorCode, description, failingUrl);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return super.shouldOverrideUrlLoading(view, request);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            //전화 걸기
            if (url.startsWith("tel:")) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse(url));
                startActivity(intent);
                return true;
            }
            return urlLoaderForCart != null && urlLoaderForCart.shouldOverrideUrlLoading(view, url);

        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            isBlockBackKey = false;
            syncWebViewCookie();
            super.onPageFinished(view, url);
        }
    }

    class WebChromeClientClass extends WebChromeClient {

        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            WebViewDialogOneBtn wdia = new WebViewDialogOneBtn(RenewalHomeActivity.this, message, result);
            wdia.setCancelable(false);
            if (!RenewalHomeActivity.this.isFinishing()) wdia.show();

            return true;
        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
            WebViewDialog wdia = new WebViewDialog(RenewalHomeActivity.this, message, result);
            wdia.setCancelable(false);
            if (!RenewalHomeActivity.this.isFinishing()) wdia.show();

            return true;
        }

        // For Android Version 5.0+
        // 이하 버전은 openFileChooser를 구현해야 하는데 그냥 지원안한다.
        public boolean onShowFileChooser(
                WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            if (mSelectFilePathCallback != null) {
                mSelectFilePathCallback.onReceiveValue(null);
            }
            mSelectFilePathCallback = filePathCallback;
            imageChooser();
            return true;
        }

        private void imageChooser() {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                try {
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmssSSS").format(new Date());
                    File photoFile = File.createTempFile(
                            ".JPEG_" + timeStamp + "_",
                            ".jpg",
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    );
                    mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
                    takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                } catch (IOException ex) {
                    takePictureIntent = null;
                }
            }

            Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
            contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
            contentSelectionIntent.setType("image/*");

            Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
            chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
            //chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
            if (takePictureIntent != null) {
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{takePictureIntent});
            }

            startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE_LOLLIPOP);
        }

        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {

            if (isDialog == false) {
                WebView newWebView = new WebView(RenewalHomeActivity.this);
                WebView.WebViewTransport transport
                        = (WebView.WebViewTransport) resultMsg.obj;
                transport.setWebView(newWebView);
                resultMsg.sendToTarget();

                newWebView.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        try {

                            Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                            browserIntent.setData(Uri.parse(url));
                            startActivity(browserIntent);

                        } catch (ActivityNotFoundException anfe) {

                            if (url.startsWith("supertoss://")) {
                                Intent excepIntent = new Intent(Intent.ACTION_VIEW);
                                excepIntent.setData(Uri.parse("market://details?id=viva.republica.toss"));
                                RenewalHomeActivity.this.startActivity(excepIntent);
                                return true;
                            }

                        } catch (Exception e) {
                            Dlog.e(TAG, e);
                        }
                        return false;
                    }
                });

                return true;
            }

            mWebView.removeAllViews();

            mChildView = new WebView(RenewalHomeActivity.this);
            mChildView.getSettings().setJavaScriptEnabled(true);

            RenewalHomeWebViewInterface mWebBridgeInterface = new RenewalHomeWebViewInterface(mPresenter);
            mChildView.addJavascriptInterface(mWebBridgeInterface, "HybridApp");

            mChildView.getSettings().setPluginState(WebSettings.PluginState.ON);

            // 몇몇 단말기에서 주소 클릭이 안된다고 해서 넣은 코드..해결이 될지는 모르겠음.
            mChildView.getSettings().setDomStorageEnabled(true);

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT_WATCH) {
                mChildView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            }

            mChildView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            mChildView.getSettings().setSupportMultipleWindows(false);

            // // 웹페이지를 화면사이즈에 맞춤.
            mChildView.getSettings().setLoadWithOverviewMode(true);
            mChildView.getSettings().setUseWideViewPort(true);
            // 줌컨트롤추가
            mChildView.getSettings().setBuiltInZoomControls(false);

            if (BuildConfig.DEBUG) {
                mChildView.setLongClickable(false);
                mChildView.setOnLongClickListener(v -> true);
            }

            mChildView.getSettings().setTextZoom(100);

            // 캐쉬를 하지 않는다..
            mChildView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
            mChildView.setWebViewClient(new CustomWebViewClient());
            mChildView.setWebChromeClient(new WebChromeClient() {
                @Override
                public void onCloseWindow(WebView window) {
                    super.onCloseWindow(window);
                    window.setVisibility(View.GONE);
                    mWebView.removeView(window);
                    mChildView = null;
                }

                @Override
                public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                    WebViewDialogOneBtn wdia = new WebViewDialogOneBtn(RenewalHomeActivity.this, message, result);
                    wdia.setCancelable(false);
                    if (!RenewalHomeActivity.this.isFinishing()) wdia.show();

                    return true;
                }

                @Override
                public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
                    WebViewDialog wdia = new WebViewDialog(RenewalHomeActivity.this, message, result);
                    wdia.setCancelable(false);
                    if (!RenewalHomeActivity.this.isFinishing()) wdia.show();

                    return true;
                }

            });

            mChildView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mWebView.addView(mChildView);
            WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
            transport.setWebView(mChildView);
            resultMsg.sendToTarget();

            return true;
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

//            IntentUtil.putExtraDataIfHostIsExecGoto(intent);
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

//    private void initIgawLiveOps() {
//        try {
//            homeThirdPartyLibHandler.initIgawLiveOps(this::processIntentDataOnReceivePush);
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

    @Override
    protected void onPause() {
        super.onPause();

        // 아래 활성화 시키면 카카오 계정 연결 로그인 페이지가 나타나지 않음
//        if (!isProductEditMode) {
//            if (mWebView != null) {
//                mWebView.pauseTimers();
//            }
//        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.getInstance().stopSync();
        } else {
            CookieManager.getInstance().flush();
        }

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
    public void onRestart() {
        super.onRestart();
        if (mWebView != null) {
            mPresenter.onRestart();
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

    public void enableProductEditState() {
        isProductEditMode = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //CS 처리용 프로젝트 코드 리시버 종료
        unregisterProjectCodeReceiver();

        //AI 사진 동기화 리모트 서비스 종료
        /*
        SyncPhotoServiceManager.getInstance().stopService();
        */

        if (mWebView != null) {
            mWebView.postDelayed(() -> {
                try {
                    mWebView.destroy();
                } catch (Exception ex) {
                    Dlog.e(TAG, ex);
                }
            }, 3000);
        }

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

        SnapsLogger.appendTextLog("RenewalHomeActivity", "onRestoreInstanceState was called.");

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


        //이미지 첨부
        if (requestCode == INPUT_FILE_REQUEST_CODE_LOLLIPOP) {
            if (resultCode == RESULT_OK) {
                if (mSelectFilePathCallback == null) {
                    super.onActivityResult(requestCode, resultCode, data);
                    return;
                }
                Uri[] results = new Uri[]{getResultUri(data)};

                mSelectFilePathCallback.onReceiveValue(results);
                mSelectFilePathCallback = null;
            } else {
                if (mSelectFilePathCallback != null) {
                    mSelectFilePathCallback.onReceiveValue(null);
                }
                mSelectFilePathCallback = null;
                super.onActivityResult(requestCode, resultCode, data);
            }
        }

        if (requestCode == CSEditorHomeActivity.CS_EDITOR_HOME_REQUEST_CODE) {
            if (!Config.isDevelopVersion()) {
                return;
            }
            if (data == null) {
                return;
            }

            String schemeTestURL = data.getStringExtra(CSEditorContract.TEST_SCHEME_TEST_URL);
            if (schemeTestURL != null) {
                loadProductPage(schemeTestURL);
                return;
            }

            String selectProductScheme = data.getStringExtra(CSEditorContract.SELECT_PRODUCT_SCHEME);
            if (selectProductScheme != null) {
                mPresenter.onSelectProduct(selectProductScheme, false);
                return;
            }

            String editorSchema = data.getStringExtra(CSEditorContract.CS_EDITOR_RESULT_SCHEME);
            if (editorSchema != null) {
                loadCartProductPage(editorSchema);
            }
        }
    }

    private Uri getResultUri(Intent data) {
        Uri result = null;
        if (data == null || TextUtils.isEmpty(data.getDataString())) {
            // If there is not data, then we may have taken a photo
            if (mCameraPhotoPath != null) {
                result = Uri.parse(mCameraPhotoPath);
            }
        } else {
            result = Uri.parse(data.getDataString());
        }
        return result;
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent.hasExtra("targetUrl")) {
            String targetUrl = intent.getStringExtra("targetUrl");
            String prmChannelCode = intent.getStringExtra("prmchnlcode");
            String deviceChannelCode = getString(R.string.channel_code);
            this.mPresenter.onGetExternalURL(targetUrl, prmChannelCode, deviceChannelCode);
            return;
        }

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

        if (mChildView != null) {
            mChildView.goBack();

        } else {
            mPresenter.onBackPressed(mWebView.getOriginalUrl(), mWebView.canGoBack());
        }
    }

    public void onClick(View v) {
        UIUtil.blockClickEvent(v, UIUtil.DEFAULT_CLICK_BLOCK_TIME);

        int id = v.getId();
        if (id == R.id.txtBadgeMenu || id == R.id.txtCartBadge || id == R.id.txtBadgeMenuLy) {
            SnapsMenuManager.goToCartList(RenewalHomeActivity.this, homeUIHandler);
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
    public static final int HANDLE_MSG_GOTO_CART = 3;


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
            case HANDLE_MSG_GOTO_CART:
                mPresenter.onMessageGoToCart(mWebView.getUrl());
                break;
        }
    }

    public HomeUIHandler getHomeUIHandler() {
        return homeUIHandler;
    }


    @Override
    public View getBtnEdit() {
        return null;
    }

    @Override
    public View getBtnComplete() {
        return null;
    }

    @Override
    public KakaoStoryPostingEventor getKakaoStoryPostingEventor() {
        return null;
    }

    @Override
    public String getSnsShareCallBack() {
        return null;
    }

    @Override
    public void setSnsShareCallBack(String str) {

    }

    @Override
    public void shouldOverrideUrlLoading(WebView view, String url) {
        if (urlLoaderForCart != null)
            urlLoaderForCart.shouldOverrideUrlLoading(view, url);
    }

    @Override
    public boolean isINISIS_Page() {
        return mIsINISIS_Page;
    }

    @Override
    public void setIsPaymentComplete(boolean flag) {
        mIsPaymentComplete = flag;
    }
}
