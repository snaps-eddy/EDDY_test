package com.snaps.mobile.activity.home.ui_strategies;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStripForSticky;
import com.snaps.common.model.WebViewPage;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.constant.SnapsConfigManager;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.net.xml.GetParsedXml;
import com.snaps.common.utils.net.xml.bean.Xml_MyBadge;
import com.snaps.common.utils.net.xml.bean.Xml_PushAllUser;
import com.snaps.common.utils.pref.Setting;
import com.snaps.common.utils.thread.ATask;
import com.snaps.common.utils.ui.ContextUtil;
import com.snaps.common.utils.ui.DataTransManager;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.common.utils.ui.TabStyleNativeScrollViewController;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.hamburger_menu.SnapsMenuManager;
import com.snaps.mobile.activity.home.model.HomeUIControl;
import com.snaps.mobile.activity.home.model.HomeUIData;
import com.snaps.mobile.activity.home.utils.SnapsEventHandler;
import com.snaps.mobile.activity.home.utils.SnapsLoginManager;
import com.snaps.mobile.activity.themebook.PhotobookCommonUtils;
import com.snaps.mobile.activity.ui.menu.renewal.MenuDataManager;
import com.snaps.mobile.activity.ui.menu.renewal.model.Category;
import com.snaps.mobile.activity.webview.PopupDialogCenterPush;
import com.snaps.mobile.activity.webview.PopupDialogPushFragment;
import com.snaps.mobile.component.CustomSensitivityViewPager;
import com.snaps.mobile.utils.pref.PrefUtil;
import com.snaps.mobile.utils.smart_snaps.SmartSnapsManager;
import com.snaps.mobile.utils.thirdparty.SnapsTPAppManager;

import java.util.List;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;

import errorhandle.logger.SnapsInterfaceLogDefaultHandler;
import errorhandle.logger.SnapsLogger;

public class HomeUIForSnapsRenewal extends HomeUIBase {
    private static final String TAG = HomeUIForSnapsRenewal.class.getSimpleName();
    public HomeUIForSnapsRenewal(Activity activity, SnapsEventHandler eventHandler) {
        super(activity, eventHandler);
    }

    @Override
    public void initialize() {
        createHomeUIControls();

        //initScrollViewController();

        initControlState();
    }

    private void initControlState() {
//        ImageView img0 = (ImageView) getActivity().findViewById(R.id.txtBadgeMenu1);
//        img0.setVisibility(View.GONE);

        HomeUIData homeUIData = getHomeUIData();
        homeUIData.set_cart_count(Setting.getInt(getActivity(), Const_VALUE.KEY_CART_COUNT));

        HomeUIControl homeUIControl = getHomeUIControl();
//        if (homeUIData.get_cart_count() == 0)
//            homeUIControl.getTxtCartBadge().setText("");
//        else {
//            if( homeUIData.get_cart_count() > 99 ) {
//                homeUIData.set_cart_count(99);
//            }
//            homeUIControl.getTxtCartBadge().setText(String.valueOf(homeUIData.get_cart_count()));
//        }

//        homeUIControl.getmHomeAlpha().setBackgroundColor(Color.argb(125, 0, 0, 0));
//        homeUIControl.getmHomeAlpha().bringToFront();

//        /** 일기는 글로벌을 지원 안 함.**/
//        if (!Config.useKorean()) {
//            View diaryLayout = getActivity().findViewById(R.id.img_diary_layout);
//            if (diaryLayout != null) {
//                diaryLayout.setVisibility(View.GONE);
//            }
//        }
    }

    @Override
    public void createHomeUIControls() {
        HomeUIControl homeUIControl = getHomeUIControl();
//        homeUIControl.setTxtCartBadge((TextView) getActivity().findViewById(R.id.txtCartBadge));
//        homeUIControl.setTxtPresentBadge((TextView) getActivity().findViewById(R.id.txt_present));
//        homeUIControl.setWvController(new TabStyleNativeScrollViewController((SnapsBaseFragmentActivity)getActivity()));
//        homeUIControl.setmHomeAlpha((View) getActivity().findViewById(R.id.alpha50));
        homeUIControl.setLinearLayoutWebViewProgress((LinearLayout) getActivity().findViewById(R.id.webviewProgress));
        homeUIControl.setImageViewWebViewProgress((ImageView) getActivity().findViewById(R.id.webViewProgressbar));
    }

    private void initScrollViewController() {
        // 컨트롤러 초기화.
        TabStyleNativeScrollViewController wvController = getHomeUIControl().getWvController();

        final MenuDataManager menuDataManager = MenuDataManager.getInstance();
        if (menuDataManager != null) {
            menuDataManager.createShouldOverrideUrlLoader(getActivity());
            menuDataManager.setLoginStatusListener(wvController);
            if (menuDataManager.getCategories() != null) {
                for (int i = 0; i < menuDataManager.getCategories().size(); ++i) {
                    final Category mainMenu = menuDataManager.getCategories().get(i);
                    if (mainMenu == null)
                        continue;

//                    WebViewPage webPage = new WebViewPage( mainMenu.getTitle(), "", mainMenu.isShowNewTag(), (Config.useKorean() && i == 0) );
                    WebViewPage webPage = new WebViewPage( mainMenu.getTitle(), "", mainMenu.isShowNewTag(), (isSupportHomeMenu() && i == 0) );
                    webPage.setMenuList(mainMenu.getMenuList());
                    wvController.addPage(webPage);
                }
            }

            // 각 뷰 추가 및 초기화.
            wvController.setViews((RelativeLayout) getActivity().findViewById(R.id.home_title_bar), (FrameLayout) getActivity().findViewById(R.id.header), (CustomSensitivityViewPager) getActivity().findViewById(R.id.pager));
            wvController.initialize( PagerSlidingTabStripForSticky.TAB_TYPE_AUTO );

            if (SnapsConfigManager.isAutoLaunchProductMakingMode()) {
                menuDataManager.performAnyUrlForTest();
            }
        }
    }

    private boolean isSupportHomeMenu() {
        return Config.useKorean() || isExistHomeValue();
    }

    private boolean isExistHomeValue() {
        MenuDataManager menuDataManager = MenuDataManager.getInstance();
        return menuDataManager != null && menuDataManager.isExistHomeMenu();
    }

    @Override
    public void checkInnerPopup() throws Exception {

        // jack@snaps.com PUSH 공지 제거 SnapsAPI.PUSH_INTERFACE()
        /*
        String todayStr = DateUtil.getTodayDate();
        if (todayStr.equals(Setting.getString(getActivity(), Const_VALUE.PUSH_FULL_CHECK_KEY))
                || todayStr.equals(Setting.getString(getActivity(), Const_VALUE.PUSH_CENTER_CHECK_KEY))) {
            return;
        } else {
            PushALLSearch();
        }
        */
        if (!SnapsTPAppManager.isThirdPartyApp(getActivity())) {
            if (Config.getKAKAO_EVENT_RESULT() == null) {
                if (!PrefUtil.getKakaoSenderNo(getActivity()).equals("")) {
                    Config.setKAKAO_EVENT_RESULT("login");
                }
            }
        }

    }

    public void PushALLSearch() {
        ATask.executeVoid(new ATask.OnTask() {
            private List<Xml_PushAllUser.PushAllUser> mPushAllList;
            @Override
            public void onPre() {}

            @Override
            public void onPost() {
                String pushUrl = "";
                if (mPushAllList != null && mPushAllList.get(0).F_STATUS.equals("ok")) {
                    pushUrl = StringUtil.getURLDecode(mPushAllList.get(0).F_TARGET_PATH, "euc-kr");
                    SnapsLogger.appendTextLog("inner push popup",pushUrl);
                    // 기본푸쉬
                    if (mPushAllList.get(0).F_RCV_TYPE.equals("221003")) {

                        middlePopup(pushUrl, mPushAllList.get(0).F_BRDCST_CODE, mPushAllList.get(0).F_CLOSE_YORN);

                    }// 전체레이어
                    else if (mPushAllList.get(0).F_RCV_TYPE.equals("221004")) {
                        fullPopup(pushUrl, mPushAllList.get(0).F_BRDCST_CODE, mPushAllList.get(0).F_CLOSE_YORN);
                    }
                }
            }

            @Override
            public void onBG() {
                Xml_PushAllUser alluser = GetParsedXml.PushAllUser(SnapsLoginManager.getUUserNo(getActivity()));
                mPushAllList = alluser.pushAllUserList;
            }
        });
    }

    private void middlePopup(String url, String type, String close) {
        try {
            FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
            Fragment prev = getActivity().getFragmentManager().findFragmentByTag("dialog");
            if (prev != null) {
                ft.remove(prev);
            }
            ft.addToBackStack(null);

            PopupDialogCenterPush pushPopupcenter = new PopupDialogCenterPush();
            pushPopupcenter.setOpenUrl(url);
            pushPopupcenter.setType(type);
            pushPopupcenter.setclose(close);
            pushPopupcenter.setUserNo(SnapsLoginManager.getUUserNo(getActivity()));
            pushPopupcenter.show(ft, "dialog");
        } catch (IllegalStateException e) {
            Dlog.e(TAG, e);
        }
    }

    private void fullPopup(String url, String type, String close) {
        try {
            FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
            Fragment prev = getActivity().getFragmentManager().findFragmentByTag("dialog");
            if (prev != null) {
                ft.remove(prev);
            }
            ft.addToBackStack(null);

            PopupDialogPushFragment pushPopupFragment = new PopupDialogPushFragment();
            pushPopupFragment.setOpenUrl(url);
            pushPopupFragment.setType(type);
            pushPopupFragment.setclose(close);
            pushPopupFragment.setUserNo(SnapsLoginManager.getUUserNo(getActivity()));
            pushPopupFragment.show(ft, "dialog");
        } catch (IllegalStateException e) {
            Dlog.e(TAG, e);
        }
    }

    @Override
    public void handleOnResume() {
        // ContextUtil의 context가 null인 경우가 있으므로, onResume시마다 다시 넣어줌.
        ContextUtil.setSubContext( getActivity() );
        Config.setIS_MAKE_RUNNING(false);
        setDeviceMaxBitmapSize();

        try {
            SnapsMenuManager.initPage();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        reloadPager();
        refreshCrm();
        reloadData();

//        SnapsAdbrix.startSession(getActivity());
//        if (SnapsLoginManager.isLogOn(getActivity())) {
//            IgawLiveOps.resume(getActivity());
//        }

        DataTransManager.releaseInstance();

        PhotobookCommonUtils.initProductEditInfo();
        DataTransManager.releaseCloneImageSelectDataHolder();
        SmartSnapsManager.finalizeInstance();
        //getHomeUIControl().getWvController().reFresh();
    }

    public void checkKakaoEvent() {
        if (getEventHandler() != null && !getEventHandler().isLaunchFromKakaoLink())
            getEventHandler().checkKakaoEvent();
    }

    private void setDeviceMaxBitmapSize() {
        if( Config.getDeviceMaxBitmapSize() < 1 ) {
            final int IMAGE_MAX_BITMAP_DIMENSION = 2048;

            EGL10 egl = (EGL10) EGLContext.getEGL();
            EGLDisplay display = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);

            int[] version = new int[2];
            egl.eglInitialize(display, version);

            int[] totalConfigurations = new int[1];
            egl.eglGetConfigs(display, null, 0, totalConfigurations);

            EGLConfig[] configurationsList = new EGLConfig[totalConfigurations[0]];
            egl.eglGetConfigs(display, configurationsList, totalConfigurations[0], totalConfigurations);

            int[] textureSize = new int[1];
            int maximumTextureSize = 0;

            for (int i = 0; i < totalConfigurations[0]; i++) {
                egl.eglGetConfigAttrib(display, configurationsList[i], EGL10.EGL_MAX_PBUFFER_WIDTH, textureSize);

                if (maximumTextureSize < textureSize[0])
                    maximumTextureSize = textureSize[0];
            }

            egl.eglTerminate(display);

            Config.setDeviceMaxBitmapSize( Math.max(maximumTextureSize, IMAGE_MAX_BITMAP_DIMENSION) );
        }
    }

    private void reloadPager() {
//        if (getHomeUIControl() == null) return;
//        TabStyleNativeScrollViewController controller = getHomeUIControl().getWvController();
//        if (controller != null) controller.reload();
    }

    private void refreshCrm() {
//        MenuDataManager menuDataManager = MenuDataManager.getInstance();
//        if( menuDataManager != null )
//            menuDataManager.refreshCrm( getActivity() );
    }

    private void reloadData() {
        HomeUIData homeUIData = getHomeUIData();
        if (homeUIData != null) {
            homeUIData.set_cart_count(Setting.getInt(getActivity(), Const_VALUE.KEY_CART_COUNT));
            homeUIData.set_coupon_count(Setting.getInt(getActivity(), Const_VALUE.KEY_COUPON_COUNT));
            homeUIData.set_myartwork_count(Setting.getInt(getActivity(), Const_VALUE.KEY_MYARTWORK_COUNT));
        }

        refreshDataAndUI();
    }

    @Override
    public void refreshDataAndUI() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor ed = prefs.edit();
        ed.putBoolean("touchcheck", false);
        ed.commit();

        if (!SnapsLoginManager.isLogOn(getActivity())) {// userNo가 있을때만 조회함.
            // 로그인이 안되어 있으면 뱃지는 0으로변경
            Setting.set(getActivity(), Const_VALUE.KEY_COUPON_COUNT, 0);
            Setting.set(getActivity(), Const_VALUE.KEY_CART_COUNT, 0); // cart count도 0으로.
            PrefUtil.saveCartCount(getActivity(), 0);

//            if (getHomeUIControl() != null) {
//                getHomeUIControl().getTxtCartBadge().setText("");
//            }
            return;
        }

        //getBadgeCount();
    }

    /**
     * 수동 로그인 또는 결제 완료 시점에서만 호출한다.
     */
    private void getBadgeCount() {
        ATask.executeVoid(new ATask.OnTask() {
            private Xml_MyBadge xmlBadgeCnt;
            @Override
            public void onPre() {
                xmlBadgeCnt = null;
            }

            @Override
            public void onBG() {

                boolean isNeedRequestBadgeCount = Setting.getBoolean(getActivity(), Const_VALUE.KEY_SHOULD_REQUEST_BADGE_COUNT);
                if (isNeedRequestBadgeCount) {
                    Setting.set(getActivity(), Const_VALUE.KEY_SHOULD_REQUEST_BADGE_COUNT, false);
                    xmlBadgeCnt = GetParsedXml.getMyBadge(SnapsTPAppManager.getBadgeCountParams(getActivity().getApplicationContext()), SnapsInterfaceLogDefaultHandler.createDefaultHandler());
                }
            }

            @Override
            public void onPost() {
                HomeUIControl homeUIControl = getHomeUIControl();
                HomeUIData homeUIData = getHomeUIData();
                if (homeUIControl == null || homeUIData == null) return;

                if (xmlBadgeCnt != null) {
                    try {
                        homeUIData.set_cart_count(Integer.valueOf(xmlBadgeCnt.F_CART_CNT));
                    } catch (NumberFormatException e) {
                        Dlog.e(TAG, e);
                    }

                    try {
                        int _coupon_count = Integer.valueOf(xmlBadgeCnt.F_CPN_CNT);
                        homeUIData.set_coupon_count(_coupon_count);
                        Setting.set(getActivity(), Const_VALUE.KEY_COUPON_COUNT, _coupon_count);
                    } catch (NumberFormatException e) {
                        Dlog.e(TAG, e);
                    }

                    try {
                        int  _myartwork_count = Integer.valueOf(xmlBadgeCnt.F_PROJ_CNT);
                        homeUIData.set_myartwork_count(_myartwork_count);
                        Setting.set(getActivity(), Const_VALUE.KEY_MYARTWORK_COUNT, _myartwork_count);
                    } catch (NumberFormatException e) {
                        Dlog.e(TAG, e);
                    }

                    try {
                        int _notice_count = Integer.valueOf(xmlBadgeCnt.F_PBL_CNT);
                        homeUIData.set_notice_count(_notice_count);
                    } catch (NumberFormatException e) {
                        Dlog.e(TAG, e);
                    }

                    PrefUtil.saveCartCount( getActivity(), homeUIData.get_cart_count() );
                } else {
                    homeUIData.set_cart_count(Setting.getInt(getActivity(), Const_VALUE.KEY_CART_COUNT));
                }

                TextView txtCartBadge = homeUIControl.getTxtCartBadge();
                if (homeUIData.get_cart_count() == 0) {
                    txtCartBadge.setText("");
                } else {
                    txtCartBadge.setVisibility(View.VISIBLE);
                    if( homeUIData.get_cart_count() > 99 ) {
                        homeUIData.set_cart_count(99);
                    }

                    txtCartBadge.setText(String.valueOf(homeUIData.get_cart_count()));
                }
            }
        });
    }

    @Override
    public void handleOnBackPressed(ISnapsFinishCheckListener snapsFinishCheckListener) {
        if (!isAppFinishCheckFlag()) {
            Toast appFinishToast = Toast.makeText(getActivity(), R.string.app_finish, Toast.LENGTH_SHORT);
            appFinishToast.show();
            setAppFinishToast(appFinishToast);
            setAppFinishCheckFlag(true);

            if (snapsFinishCheckListener != null)
                snapsFinishCheckListener.requestClearAppFinishCheckFlag();
        } else {
            if (getAppFinishToast() != null)
                getAppFinishToast().cancel();

            if (snapsFinishCheckListener != null)
                snapsFinishCheckListener.performSnapsFinish();
        }
    }

    @Override
    public void clearAppFinishCheckFlag() {
        setAppFinishCheckFlag(false);
    }
}
