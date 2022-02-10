package com.snaps.mobile.kr;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import androidx.core.app.ActivityCompat;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.snaps.common.data.interfaces.ISnapsApplication;
import com.snaps.common.push.PushManager;
import com.snaps.common.trackers.SnapsAppsFlyer;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.net.CNetStatus;
import com.snaps.common.utils.net.http.HttpUtil;
import com.snaps.common.utils.net.xml.bean.Xml_UpdateInfo;
import com.snaps.common.utils.pref.Setting;
import com.snaps.common.utils.system.ViewUnbindHelper;
import com.snaps.common.utils.ui.FontUtil;
import com.snaps.common.utils.ui.ICustomDialogListener;
import com.snaps.common.utils.ui.IntentUtil;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.common.utils.ui.SnapsLanguageUtil;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.activity.hamburger_menu.SnapsMenuManager;
import com.snaps.mobile.activity.home.RenewalHomeActivity;
import com.snaps.mobile.utils.pref.PrefUtil;
import com.snaps.common.utils.ISnapsHandler;
import com.snaps.common.structure.SnapsHandler;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Locale;

import bolts.AppLinks;
import errorhandle.CatchActivity;

public class SplashActivity extends CatchActivity implements ISnapsHandler, ActivityCompat.OnRequestPermissionsResultCallback {
    private static final String TAG = SplashActivity.class.getSimpleName();
    public static final int REQUEST_STORAGE_PERMISSION_CODE = 9999;

    private SnapsIntroHandler introHandler = null;    //updateInfo를 받고 로그인 등 splash화면에서 핵심적인 동작들을 담당한다

    private SnapsHandler splashHandler = null;

    private SplashPopupHandler splashPopupHandler = null;

    private ImageView progressBar = null;

    private boolean isLaunchedHomeActivity = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().hasCategory(Intent.CATEGORY_LAUNCHER)
                && Intent.ACTION_MAIN.equals(getIntent().getAction()) && !isTaskRoot()) {
            finish();
            return;
        }

        initApplication();

        setContentView(R.layout.activity_splash);

        initDefaultSetting();

        initSplashUI();

        startIntroProcess();

        changeBGColorBasedOnProductFlavors();
        showToastServerInfo();

        //일기 서비스 종료 대응
        /*
        if (SnapsDiaryMainActivity.IS_END_OF_SERVICE) {
            SnapsDiaryMainActivity.downloadNoticeDialogImg(getApplicationContext());
            NTPClient.checkEnableDiaryNewOrEdit();
        }
        */
    }

    private void initApplication() {
        isLaunchedHomeActivity = false;
        ((ISnapsApplication) getApplication()).requestInitApplication();
    }

    private void initDefaultSetting() {
        createRequireUtils();

        initBasicPreferences();

        createSplashHandlers();

        checkAppLinkData();
    }

    private void initSplashUI() {
        setIntroMessage(R.string.constructing_font_data);

        startLogImageAnimation();

        initProgressBar();
    }

    private void checkAppLinkData() {
        Uri targetUrl = AppLinks.getTargetUrlFromInboundIntent(this, getIntent());
        if (targetUrl != null) { //TODO  왜 무조건 null이니..?
            putExtraAppLinkDatas(getIntent(), targetUrl);
        }
    }

    private void putExtraAppLinkDatas(Intent intent, Uri targetUri) {
        if (intent == null || targetUri == null || targetUri.toString() == null || targetUri.toString().length() < 1)
            return;
        try {
            intent.putExtra("gototarget", "P0008");
            String targetFullUrl = "snapsapp://optionUrl=" + URLEncoder.encode(targetUri.toString(), "utf-8");
            intent.putExtra("fullurl", targetFullUrl);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private void createSplashHandlers() {
        splashHandler = new SnapsHandler(this);

        introHandler = SnapsIntroHandler.createInstanceWithSplashHandler(this, splashHandler);

        splashPopupHandler = SplashPopupHandler.createInstanceWithSplashHandler(this, splashHandler);
    }

    private void initBasicPreferences() {
        Setting.set(this, Const_VALUE.KEY_SNAPS_REST_ID, false); // 휴면계정값 초기화.

        PrefUtil.initAlwaysShouldShowAlert(this);

        checkKakaoEventIntentData();
    }

    private void checkKakaoEventIntentData() {
        clearPrevKakaoEventHistory();

        String kakaoSchema = Config.getKAKAO_SCHEMA();
        if (getIntent() != null && getIntent().getDataString() != null && getIntent().getDataString().startsWith(kakaoSchema)) {
            String url = getIntent().getDataString();
            url = URLDecoder.decode(url);

            String sendNo = StringUtil.getTitleAtUrl(url, "sendno");
            String deviceId = "", eventCode = "";
            PrefUtil.saveKakaoEvent(this, sendNo, eventCode, deviceId);
        }

        if (!PrefUtil.getKakaoSenderNo(this).equals("")) {
            Config.setKAKAO_EVENT_RESULT("login");
        }

        Config.setKAKAO_SCHEMA(this.getResources().getString(R.string.kakao_scheme));
    }

    private void clearPrevKakaoEventHistory() {
        PrefUtil.clearKakaoEvent(this);
        Config.setKAKAO_EVENT_RESULT(null);
    }

    private void createRequireUtils() {
        SnapsMenuManager.createInstance();

        FontUtil fontUtil = FontUtil.getInstance();
        fontUtil.createShouldDownloadFontCountArr(this);
    }

    private void setIntroMessage(int msgResId) {
        if (FontUtil.isShouldDownloadFont()) {
            TextView introText = (TextView) findViewById(R.id.tv_intro);
            if (introText != null)
                introText.setText(getString(msgResId));
        }
    }

    private void initProgressBar() {
        progressBar = (ImageView) findViewById(R.id.bottom_line_progress);
        progressBar.setPivotX(0f);
        progressBar.setVisibility(FontUtil.isShouldDownloadFont() ? View.VISIBLE : View.INVISIBLE);
        updateProgress(0);
    }

    private void updateProgress(int progress) {
        final float CURRENT_PROGRESS = (float) progress / 100.f;
        SplashActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressBar != null)
                    progressBar.setScaleX(CURRENT_PROGRESS);
                if (CURRENT_PROGRESS > 0.99f) {
                    if (splashHandler != null)
                        splashHandler.sendEmptyMessage(HANDLE_MOVE_HOME);
                }
            }
        });
    }

    private void startLogImageAnimation() {
        ImageView logo = (ImageView) findViewById(R.id.logo);
        AnimationDrawable frameAnimation = (AnimationDrawable) logo.getDrawable();
        frameAnimation.start();
    }

    private void startIntroProcess() {
        if (!CNetStatus.isEnableAppLaunchNetworkState(this)) {
            cancelDialog();
            return;
        }

        introHandler.setFontDownloadProgressListener(fontDownloadProgressListener);
        introHandler.downloadUpdateInfo();
    }

    private HttpUtil.DownloadProgressListener fontDownloadProgressListener = new HttpUtil.DownloadProgressListener() {
        @Override
        public void updateProgress(long current, long total) {
            if (!FontUtil.isShouldDownloadFont()) return;

            int totalDownloadFontCount = FontUtil.getTotalDownloadFontCount();
            int currentDownloadFontCount = FontUtil.getCurrentDownloadFontCount();

            float currentPercent = (float) current / (float) total;
            float oneFilePercent = 0.99f / (float) totalDownloadFontCount;
            SplashActivity.this.updateProgress((int) ((currentDownloadFontCount * oneFilePercent + currentPercent * oneFilePercent) * 100));
        }
    };

    private void initMainUI() {
        if (splashPopupHandler.shouldShowAccessAppPopup()) {
            splashPopupHandler.showAccessAppPopup();
            return;
        }

        if (splashPopupHandler.shouldShowSystemPermissionPopup()) {
            splashPopupHandler.showSystemPermissionPopup();
            return;
        }

        if (splashPopupHandler.shouldShowPushAgreePopup()) {
            splashPopupHandler.showPushAgreePopup();
            return;
        }

        if (introHandler != null)
            introHandler.initMenuData();

        PushManager pushManager = new PushManager(this);
        pushManager.requestRegistPushDevice();

        //다운로드 받은 폰트 로딩
        //원본 코드는 menuDataManager.init 할때 했음
        FontUtil.loadDownloadFonts(getApplicationContext());
    }

    private void startHomeActivity() {
        if (isLaunchedHomeActivity) return;
        isLaunchedHomeActivity = true;

        Intent homeIntent = new Intent(SplashActivity.this, RenewalHomeActivity.class);
        homeIntent.setData(getIntent().getData());

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            homeIntent.putExtras(bundle);
        }

        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(homeIntent);
        finish();
    }

    private void setAppsFlyerSetting() {
        try {
            SnapsAppsFlyer.setSDKinit(getApplication());
            SnapsAppsFlyer.setCurrency(StringUtil.getAppsFlyerCurrencyCode(this));
//            SnapsAppsFlyer.setUninstallTracking(Const_VALUE.GCM_PROJECT_NUMBER);
            SnapsAppsFlyer.setDeepLink(this);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    public void cancelDialog() {
        MessageUtil.alert(this, R.string.network_status_check, false, new ICustomDialogListener() {
            @Override
            public void onClick(byte clickedOk) {
                finish();
            }
        });
    }

    public void updateDialog(final String storeUrl) {
        MessageUtil.alert(this, R.string.app_update_title, R.string.app_update, false, new ICustomDialogListener() {
            @Override
            public void onClick(byte clickedOk) {
                IntentUtil.moveUrl(SplashActivity.this, storeUrl);
                finish();
            }
        });
    }

    /***
     * 맥스 페이지 정보를 가져오지 않은경우 메세지 처리..
     */
    public void maxPageWarnningDialog() {
        MessageUtil.alert(this, R.string.maxpageinfo_download_fail, false, new ICustomDialogListener() {
            @Override
            public void onClick(byte clickedOk) {
                finish();
            }
        });
    }

    public void versionCheckFailDialog() {
        MessageUtil.alert(this, R.string.version_check_fail, false, new ICustomDialogListener() {
            @Override
            public void onClick(byte clickedOk) {
                finish();
            }
        });
    }

    public void isNotEnoughStorageSpaceDialog() {
        MessageUtil.alert(this, R.string.not_enough_storage_space_to_launch, false, new ICustomDialogListener() {
            @Override
            public void onClick(byte clickedOk) {
                finish();
            }
        });
    }

    private void showEmergencyNotice() {
        if (introHandler != null) {
            Xml_UpdateInfo updateInfo = introHandler.getXmlUpdateinfo();
            if (updateInfo != null) {
                noticeDialog(updateInfo.getNoticeMsg().replace("\\n", "\n"), updateInfo.getNoticeUrl());
            }
        }
    }

    public void noticeDialog(String msg, final String url) {
        MessageUtil.alert(SplashActivity.this, msg, false, new ICustomDialogListener() {
            @Override
            public void onClick(byte clickedOk) {
                if (!StringUtil.isEmpty(url)) IntentUtil.moveUrl(SplashActivity.this, url);
                finish();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_STORAGE_PERMISSION_CODE) {
            splashHandler.sendEmptyMessage(HANDLE_INIT_MAIN_UI);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent e) {
        return false;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        finish();
    }

    @Override
    protected void onDestroy() {
        try {
            ViewUnbindHelper.unbindReferences(getWindow().getDecorView());
            System.gc();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        super.onDestroy();
    }

    // handler 처리구분값
    public static final int HANDLE_MOVE_MARKET = 0;
    public static final int HANDLE_SHOW_NOTICE = 1;
    public static final int HANDLE_MOVE_HOME = 2;
    public static final int HANDLE_MAXPAGE_WARNNING = 3;
    public static final int HANDLE_VERSION_WARNNING = 4;// 버젼확인 실패..
    public static final int HANDLE_NOT_ENOUGH_STORAGE_SPACE = 5; // 저장 공간 부족..

    public static final int HANDLE_INIT_MAIN_UI = 200;
    public static final int HANDLE_COMPLETE_PROGRESS = 202;
    public static final int HANDLE_INIT_APPS_FLYER = 203;

    public static final int HANDLE_REQUEST_APP_FINISH = 1000;

    @Override
    public void handleMessage(Message msg) {
        try {
            switch (msg.what) {
                case HANDLE_INIT_APPS_FLYER:
                    setAppsFlyerSetting();
                    break;
                case HANDLE_COMPLETE_PROGRESS:
                    updateProgress(100);
                    break;
                case HANDLE_REQUEST_APP_FINISH:
                    finish();
                    break;
                case HANDLE_INIT_MAIN_UI:
                    initMainUI();
                    break;
                case HANDLE_MOVE_MARKET:// 마켓으로 이동
                    updateDialog(SnapsAPI.PLAY_STORE_UPDATE_URL);
                    break;
                case HANDLE_SHOW_NOTICE:// 긴급 공지 띄우기
                    showEmergencyNotice();
                    break;
                case HANDLE_MOVE_HOME:// 홈으로 이동
                    startHomeActivity();
                    break;
                case HANDLE_MAXPAGE_WARNNING:// 긴급 공지 띄우기
                    maxPageWarnningDialog();
                    break;
                case HANDLE_VERSION_WARNNING: // 버젼확인 실패..
                    versionCheckFailDialog();
                    break;
                case HANDLE_NOT_ENOUGH_STORAGE_SPACE: // 저장 공간 부족
                    isNotEnoughStorageSpaceDialog();
                    break;
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }


    private void changeBGColorBasedOnProductFlavors() {
        ImageView bgImgView = findViewById(R.id.imgIntroRandom);
        if (Config.isDevelopVersion()) {
            bgImgView.setColorFilter(Color.DKGRAY);
        }
    }


    private void showToastServerInfo() {
        if (Config.isDevelopVersion() == false) {
            return;
        }

        String currentLang = Setting.getString(getApplicationContext(), Const_VALUE.KEY_APPLIED_LANGUAGE, Locale.getDefault().getLanguage());
        if (!SnapsLanguageUtil.isServiceableLanguage(currentLang)) {
            currentLang = Locale.ENGLISH.getLanguage();  //그냥 한국어!!!
        }
        if (StringUtil.isEmpty(currentLang)) {
            currentLang = Locale.getDefault().getLanguage();
        }
        String msg = "WEB : " + SnapsAPI.FRONTEND_DOMAIN(currentLang, false) + "\n";
        msg += "API : " + SnapsAPI.DOMAIN();

        Toast toast = Toast.makeText(this, msg, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP | Gravity.LEFT, 10, 10);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            GradientDrawable gd = new GradientDrawable();
            gd.setColor(Color.GRAY);
            gd.setCornerRadius(5);
            gd.setStroke(3, Color.WHITE);
            View toastView = toast.getView();
            toastView.setBackground(gd);

            TextView toastMessage = (TextView) toastView.findViewById(android.R.id.message);
            toastMessage.setPadding(20, 0, 15, 0);
        }

        toast.show();
    }
}
