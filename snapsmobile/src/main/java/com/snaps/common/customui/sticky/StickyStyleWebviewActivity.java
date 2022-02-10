package com.snaps.common.customui.sticky;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStripForSticky;
import com.snaps.common.model.WebViewPage;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_EKEY;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.pref.Setting;
import com.snaps.common.utils.ui.DataTransManager;
import com.snaps.common.utils.ui.IFacebook;
import com.snaps.common.utils.ui.IKakao;
import com.snaps.common.utils.ui.SnsFactory;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.common.utils.ui.UI;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.kakao.utils.share.SNSShareUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.hamburger_menu.SnapsMenuManager;
import com.snaps.mobile.activity.home.RenewalHomeActivity;
import com.snaps.mobile.activity.home.fragment.GoHomeOpserver;
import com.snaps.mobile.activity.home.fragment.HomeMenuBase;
import com.snaps.mobile.activity.home.fragment.HomeMenuBase.OnSlideMenuLitener;
import com.snaps.mobile.activity.ui.menu.json.UINavigatorProducts;
import com.snaps.mobile.activity.ui.menu.renewal.MenuDataManager;
import com.snaps.mobile.activity.ui.menu.renewal.model.Item;
import com.snaps.mobile.activity.ui.menu.renewal.model.SubCategory;
import com.snaps.mobile.activity.ui.menu.webinterface.SnapsShouldOverrideUrlLoader;
import com.snaps.mobile.base.SnapsBaseFragmentActivity;
import com.snaps.mobile.component.CustomSensitivityViewPager;
import com.snaps.mobile.component.ProgressWebView;
import com.snaps.mobile.component.SnapsWebviewProcess;
import com.snaps.mobile.interfaces.OnPageLoadListener;
import com.snaps.mobile.utils.thirdparty.SnapsTPAppManager;

import java.util.ArrayList;
import java.util.HashMap;

import errorhandle.logger.Logg;
import errorhandle.logger.SnapsLogger;
import errorhandle.logger.model.SnapsLoggerClass;

@Deprecated
public class StickyStyleWebviewActivity extends SnapsBaseFragmentActivity implements OnPageLoadListener, OnSlideMenuLitener, GoHomeOpserver.OnGoHomeOpserver, SnapsStickyLayout.ISnapsStickyClickListener {
    private static final String TAG = StickyStyleWebviewActivity.class.getSimpleName();

    private static String SIMPLE_MAKING_GUIDE_SHOWN = "simple_making_guide_shown";

    boolean isShow = false;

    ImageView mHomeBtn;
    ImageView mBackBtn;

    public int currentHome = 0;

    static public boolean isEnablerefresh = false;

    private HomeMenuBase homeMenuGridFragment = null;
//    private SlidingMenu slidingMenu = null;
    private View mHomeAlpha;

    private boolean m_isHomeMenu;
    private boolean m_isPresentEvent;
    private boolean m_isFromHomeActivity;

    String m_orientation = null;

    protected StickyStyleWebViewController wvController;
    private SnapsStickyLayout stickyLayout;

//    public static Intent getIntent(Context context, String title, String url) {
//        Intent intent = new Intent(context, StickyStyleWebviewActivity.class);
//        intent.putExtra(Const_EKEY.WEBVIEW_TITLE, title);
//        intent.putExtra(Const_EKEY.WEBVIEW_URL, url);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        return intent;
//    }

    private IKakao kakao = null;
    private IFacebook facebook = null;

    private UINavigatorProducts products = null;

    @SuppressLint("InflateParams")
    @Override
    public void onCreate(Bundle savedInstanceState) {

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                    WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        }

        super.onCreate(savedInstanceState);

        SnapsLogger.appendClassTrackingLog(new SnapsLoggerClass<>(this));

        checkOrientationState();

        setContentView(R.layout.activity_sticky_style_webview);

//        if (!isExistTitleBar) {
//            findViewById(R.id.home_title_bar).setVisibility(View.GONE);
//        }
//        if (findViewById(R.id.TopLine) != null)
//            findViewById(R.id.TopLine).setVisibility(View.GONE);

        isEnablerefresh = false;

//        mHomeBtn = (ImageView) findViewById(R.id.btnTitleLeft);
        mBackBtn = (ImageView) findViewById(R.id.btnTitleLeftBack);

        checkHomeKey();

        if (!SnapsTPAppManager.isThirdPartyApp(this)) {
            if (Config.isFacebookService()) {
                facebook = SnsFactory.getInstance().queryInteface();
                facebook.init(this);
            }
            // ////////////////////////////////
            kakao = SnsFactory.getInstance().queryIntefaceKakao();

            // /////////////////////////////
        }

//		if (SnapsTPAppManager.isThirdPartyApp(this))
//			SnapsTPAppManager.setImageResource(mHomeBtn, R.drawable.btn_top_back);

        try {
            String title = getIntent().getStringExtra(Const_EKEY.WEBVIEW_TITLE);
            String url = getIntent().getStringExtra(Const_EKEY.WEBVIEW_URL);
            m_isPresentEvent = getIntent().getBooleanExtra(Const_EKEY.WEBVIEW_PRESENT_URL, false);
            DataTransManager dtMan = DataTransManager.getInstance();
            if (dtMan != null) {
                if (m_isPresentEvent)
                    dtMan.setShownPresentPage(true);
            } else {
                DataTransManager.notifyAppFinish(this);
                return;
            }

            Dlog.d("onCreate() title:" + title + ", url:" + url);

            // title
//            UI.<TextView>findViewById(this, R.id.snpas_sticky_id_title_bar_text).setText(title);
            UI.<TextView>findViewById(this, R.id.snpas_sticky_id_title_bar_text).setText(title);

            title = StringUtil.getTitleAtUrl(url, "naviBarTitle");
            if (title != null) {
                chageTitle(title);
            }

            // 공지사항인 경우..
            String webIndex = getIntent().getExtras().getString("detailindex");

            // detailindex값이 없는경우 배송조회가 된다.
            if (webIndex != null && !webIndex.equals("")) {
                url += "&seq=" + webIndex;
            }

            //디바이스 정보를 넣어준다.
            url = StringUtil.addUrlParameter(url, "deviceModel=" + Build.MODEL);
            if (!Config.isRealServer()) {
                url += "&testapp=T";
            }

            /**
             * Naver style UI 적용.
             */
            // 컨트롤러 초기화.
            wvController = new StickyStyleWebViewController(this); // activity에서 progress webview를 사용하는 경 전역변수로.

            //하위 메뉴가 많아서 스트립 메뉴가 스크롤되어야 하는 형태.
            boolean isExpandedType = false;

            //서브 메뉴가 여러개의 메뉴가 있는지..
            boolean isMultiSubMenu = false;

            // 페이지 데이터 파싱 및 추가.
            final SnapsMenuManager menuMan = SnapsMenuManager.getInstance();
            if (menuMan != null) {

//                SubCategory subCategory = menuMan.getSubCategory();
//                if (subCategory == null)
//                    subCategory = findSubcategoryByUrl(url);
                SubCategory subCategory = MenuDataManager.findSubcategoryByUrl(url);
                if (subCategory == null)
                    subCategory = menuMan.getSubCategory();

                WebViewPage webViewPage = null;
                boolean pageAdded = false;

                if (subCategory != null) {

                    isExpandedType = !subCategory.isFixArrangeType();

                    wvController.setStickyInfo(getStickInfo(subCategory));

                    chageTitle(subCategory.getTitle());

                    if (url != null && !url.startsWith("http")) {
                        String userNo = Setting.getString(this, Const_VALUE.KEY_SNAPS_USER_NO);
                        menuMan.setSubPageBaseUrl(SnapsAPI.WEB_DOMAIN(url, userNo, ""));
                    } else
                        menuMan.setSubPageBaseUrl(url);

                    ArrayList<Item> subRes = subCategory.getItems();

                    if (subCategory.isMultiSubMenu() && !isForceSingleTabMenu(url)) { //하위 메뉴가 2개 이상
                        isMultiSubMenu = true;
                        for (Item subMenu : subRes) {
                            if (subMenu == null) continue;

                            webViewPage = new WebViewPage(subMenu.getName(),
                                    (menuMan.getSubPageBaseUrl() + "&" + subMenu.getData()),
                                    subMenu.isShowNewTag());

                            wvController.addPage(webViewPage);
                            pageAdded = true;
                        }
                    } else if (subRes != null && !subRes.isEmpty()) {
                        webViewPage = new WebViewPage(subRes.get(0).getName(),
                                menuMan.getSubPageBaseUrl(),
                                subRes.get(0).isShowNewTag());

                        wvController.addPage(webViewPage);
                        pageAdded = true;
                    }
                }
                // 바로 상세페이지로 가는경우는 url 그대로 전달.
                if( !pageAdded ) {
                    if( url != null && !url.startsWith("http") ) {
                        String userNo = Setting.getString(this, Const_VALUE.KEY_SNAPS_USER_NO);
                        url = SnapsAPI.WEB_DOMAIN(url, userNo, "");
                    }
                    webViewPage = new WebViewPage(title, url, false);
                    wvController.addPage(webViewPage);
                }
            }

            stickyLayout = (SnapsStickyLayout) findViewById(R.id.snpas_sticky_id_sticky_layout);
            CustomSensitivityViewPager viewPager = (CustomSensitivityViewPager) findViewById(R.id.snpas_sticky_id_viewpager);
            PagerSlidingTabStripForSticky tabStrip = (PagerSlidingTabStripForSticky) findViewById(R.id.snpas_sticky_id_tab_strip);

            LinearLayout imageLayout = (LinearLayout) findViewById(R.id.snpas_sticky_id_image_layout);
            RelativeLayout titleLayout = (RelativeLayout) findViewById(R.id.snpas_sticky_id_title_layout);
            TextView titleTextView = (TextView) findViewById(R.id.snpas_sticky_id_title_bar_text);
            TextView stickyTitle = (TextView) findViewById(R.id.snpas_sticky_id_sticky_area_title);
            TextView stickyDesc = (TextView) findViewById(R.id.snpas_sticky_id_sticky_area_desc);
            TextView stickyInfo = (TextView) findViewById(R.id.snpas_sticky_id_sticky_area_info);
            TextView stickyInfoFake = (TextView) findViewById(R.id.snpas_sticky_id_sticky_area_info_fake);
            ImageView backImage  = (ImageView) findViewById(R.id.snpas_sticky_id_title_bar_back);
            ImageView menuBtn  = (ImageView) findViewById(R.id.snpas_sticky_id_title_bar_menu);
            ImageView infoIcon  = (ImageView) findViewById(R.id.snpas_sticky_id_title_bar_info);
            ImageView stickyImage  = (ImageView) findViewById(R.id.snpas_sticky_id_sticky_area_image);
            ImageView gradLeft  = (ImageView) findViewById(R.id.snpas_sticky_id_tab_strip_grad_left);
            ImageView gradRight  = (ImageView) findViewById(R.id.snpas_sticky_id_tab_strip_grad_right);

            if (isMultiSubMenu) {
                gradLeft.setVisibility(View.VISIBLE);
                gradRight.setVisibility(View.VISIBLE);
            }

//            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);

            stickyLayout.setOnStickyClickListener(this);

            StickyControls stickyControls = new StickyControls.StickyControlBuilder()
                    .setViewPager(viewPager)
                    .setPagerSlidingTabStripForSticky(tabStrip)
                    .setImageLayout(imageLayout)
                    .setTitleLayout(titleLayout)
                    .setTitleText(titleTextView)
                    .setStickyTitle(stickyTitle)
                    .setStickyDesc(stickyDesc)
                    .setStickyInfo(stickyInfo)
                    .setStickyInfoFake(stickyInfoFake)
                    .setBackKey(backImage)
                    .setMenuKey(menuBtn)
                    .setInfoIcon(infoIcon)
                    .setMainImage(stickyImage)
                    .createControls();

            wvController.setStickyControls(stickyLayout, stickyControls);

            // 하드코딩으로 상품 구분할때 F_PROD_CODE는 바뀔수 있으므로 F_SCLSS_CODE로 구분.
//            wvController.initialize((url.contains("F_SCLSS_CODE=001007005000") || url.contains("F_SCLSS_CODE=001002016000")) ? PagerSlidingTabStripForSticky.TAB_TYPE_EXPANDED : PagerSlidingTabStripForSticky.TAB_TYPE_DEFAULT);
//            wvController.initialize(isExpandedType ? PagerSlidingTabStripForSticky.TAB_TYPE_EXPANDED : PagerSlidingTabStripForSticky.TAB_TYPE_DEFAULT);
            wvController.initialize(isExpandedType ? PagerSlidingTabStripForSticky.TAB_TYPE_AUTO : PagerSlidingTabStripForSticky.TAB_TYPE_DEFAULT);
            wvController.setDrawPagerStripUnderline(true);
            /**
             * 여기까지.
             */

            final SnapsWebviewProcess sp = new SnapsWebviewProcess(this, facebook, kakao);
            wvController.setWebViewProcess(sp);
//옛날 간편만들기 튜토리얼
//            Setting.set(this, SIMPLE_MAKING_GUIDE_SHOWN, true); // FIXME 간편만들기 가이드 안나오도록. 나중에 살릴지 모르니 코드 남겨둠.
//            if (url != null && url.contains("F_SCLSS_CODE=001001006000") && !Setting.getBoolean(this, SIMPLE_MAKING_GUIDE_SHOWN) && !SnapsTPAppManager.isThirdPartyApp(this)) {
//                LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                final View view = layoutInflater.inflate(R.layout.simple_making_book_guide, null);
//                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
//                view.setOnTouchListener(new View.OnTouchListener() {
//                    @Override
//                    public boolean onTouch(View v, MotionEvent event) {
//                        return true;
//                    }
//                });
//                view.findViewById(R.id.btn_02).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Setting.set(StickyStyleWebviewActivity.this, SIMPLE_MAKING_GUIDE_SHOWN, true);
//                        ((ViewGroup) view.getParent()).removeView(view);
//                    }
//                });
//                view.findViewById(R.id.btn_01).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Setting.set(StickyStyleWebviewActivity.this, SIMPLE_MAKING_GUIDE_SHOWN, true);
//
//                        // FIXME FCLSSCODE나 MCLSSCODE 필요하면 추가
//                        // 포토북 참조 snapsapp://cmd=openAppPopup&openUrl=%2Fmw%2Fstore%2Finformation%2Finfo_bookSimplephoto.jsp%3Fsclsscode%3D001001006000%26mclsscode%3D001001006008
//                        sp.shouldOverrideUrlLoading(null, "snapsapp://openAppPopup?openUrl=%2Fmw%2Fv3%2Fstore%2Finformation%2Finfo_easyBook.jsp");
//                        ((ViewGroup) view.getParent()).removeView(view);
//                    }
//                });
//
//                addContentView(view, params);
//            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        if (!Config.isSnapsSDK2(this)) initSlidingMenu();

        GoHomeOpserver.addGoHomeListener(this);
    }

    private SnapsMenuManager.UIStickyInfo getStickInfo(SubCategory subCategory) {
        SnapsMenuManager.UIStickyInfo stickyInfo = new SnapsMenuManager.UIStickyInfo();
		stickyInfo.setArrange(subCategory.getArrangeType());
		stickyInfo.setArrItems(subCategory.getItems());
		stickyInfo.setInfoUrl(subCategory.getInfoUrl());
		stickyInfo.setNextPageUrl(subCategory.getNextPageUrl());
		stickyInfo.setStickyImage(subCategory.getStickyImage());
		stickyInfo.setTitle(subCategory.getTitle());
		stickyInfo.setTopic(subCategory.getTopic());
        return stickyInfo;
    }

    private boolean isForceSingleTabMenu(String url) {
        boolean flag = false;
        if (url != null && url.length() > 0) {
            try {
                HashMap<String, String> params = StringUtil.parseUrl(url);
                if (params == null) return false;

                if (params.containsKey("F_PROD_CODE")) {
                    String prodCode = params.get("F_PROD_CODE");
                    if (Const_PRODUCT.isCardProduct(prodCode)) {
                        if (params.containsKey("clss_name")) {
                            String sClssName = params.get("clss_name");
                            if (sClssName != null && sClssName.equals(getString(R.string.season))) ;
                            else flag = true;
                        }
                    }
                }
            } catch (Exception e) {
                Dlog.e(TAG, e);
                return false;
            }
        }
        return flag;
    }


    private void initSlidingMenu() {

//        homeMenuGridFragment = new HomeMenuGridFragmentRenewal(this);
//
//        // configure the SlidingMenu
//        slidingMenu = new SlidingMenu(this);
//        slidingMenu.setMode(SlidingMenu.LEFT);
//        slidingMenu.setBehindScrollScale(0.0f);
//        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
//        slidingMenu.setTouchModeBehind(SlidingMenu.TOUCHMODE_NONE);
//        slidingMenu.setShadowWidthRes(R.dimen.shadow_width);
//        slidingMenu.setShadowDrawable(R.drawable.sticker_shadow);
//
//        int slidWidth = (int) (UIUtil.getScreenWidth(this) * 0.85f);
//
//        slidingMenu.setBehindOffset(UIUtil.getScreenWidth(this) - slidWidth);
//        slidingMenu.setFadeDegree(0.0f);
//        slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
//        slidingMenu.setMenu(R.layout.menu_frame);
//        // slidingMenu.setBackgroundResource(R.drawable.bg_left_menu_pattern);
//
//        FragmentUtil.replce(R.id.menu_frame, this, homeMenuGridFragment);
    }

    public void chageTitle(String title) {
        if (title != null && !title.equals("")) {
            UI.<TextView>findViewById(this, R.id.snpas_sticky_id_title_bar_text).setText(title);
        } else {

        }

        checkHomeKey();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        /*
		 * if (webview != null) { webview.postDelayed(new Runnable() {
		 *
		 * @Override public void run() { try { webview.destroy(); } catch (Exception ex) { } } }, 3000); }
		 */

        if (wvController != null) {
            if (wvController.getCurrentProgressWebView() != null && wvController.getCurrentProgressWebView().getWebView() != null)
                wvController.getCurrentProgressWebView().getWebView().destroy();

            if (wvController.getSingleProgressWebview() != null && wvController.getSingleProgressWebview().getWebView() != null) {
                wvController.getSingleProgressWebview().getWebView().destroy();
            }
        }

        if (stickyLayout != null) {
            stickyLayout.releaseInstances();
            stickyLayout = null;
        }

        GoHomeOpserver.removeGoHomeListenrer(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (wvController != null) {
            if (wvController.getCurrentProgressWebView() != null && wvController.getCurrentProgressWebView().getWebView() != null)
                wvController.getCurrentProgressWebView().getWebView().resumeTimers();

            if (wvController.getSingleProgressWebview() != null && wvController.getSingleProgressWebview().getWebView() != null) {
                wvController.getSingleProgressWebview().getWebView().resumeTimers();
            }

            wvController.reload();
        }

        if (stickyLayout != null)
            stickyLayout.loadMainImage();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (wvController != null) {
            if (wvController.getCurrentProgressWebView() != null && wvController.getCurrentProgressWebView().getWebView() != null) {
                wvController.getCurrentProgressWebView().getWebView().pauseTimers();
            }

            if (wvController.getSingleProgressWebview() != null && wvController.getSingleProgressWebview().getWebView() != null) {
                wvController.getSingleProgressWebview().getWebView().pauseTimers();
            }
        }

        if (stickyLayout != null)
            stickyLayout.releaseMainImage();
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();

//        if (slidingMenu != null && slidingMenu.isMenuShowing()) {
//            slidingMenu.showContent();
//            mHomeAlpha.setVisibility(View.GONE);
//            if (Config.isSnapsBitween()) {
//                ImageView menuImg = (ImageView) findViewById(R.id.btnTopMenuAlpha);
//                if (menuImg != null)
//                    menuImg.setVisibility(View.GONE);
//            }
//            return;
//        }

        finishActivity();
    }

    private void checkHomeKey() {
       
//        String title = UI.<TextView>findViewById(this, R.id.snpas_sticky_id_title_bar_text).getText().toString();
        String title = UI.<TextView>findViewById(this, R.id.snpas_sticky_id_title_bar_text).getText().toString();
        final String[] arHomeMenuTitles = {getString(R.string.store), getString(R.string.cart), getString(R.string.order_and_delivery), getString(R.string.manage_coupons)};

        m_isHomeMenu = false;

        if (!m_isPresentEvent) {
            for (String t : arHomeMenuTitles) {
                if (t.equals(title)) {
                    m_isHomeMenu = true;
                    break;
                }
            }
        }

        //FIXME
//        if (m_isHomeMenu) {
//            mHomeBtn.setImageResource(R.drawable.btn_top_menu);
//            mHomeBtn.setVisibility(View.VISIBLE);
//            mBackBtn.setVisibility(View.INVISIBLE);
//            RelativeLayout backBtnLy = (RelativeLayout) findViewById(R.id.btnTitleLeftBackLy);
//            backBtnLy.setVisibility(View.INVISIBLE);
//            findViewById(R.id.btnTitleLeftLy).setVisibility(View.VISIBLE);
//            findViewById(R.id.btnTitleLeftBackLy).setVisibility(View.INVISIBLE);
//        } else {
//            mHomeBtn.setVisibility(View.INVISIBLE);
//            mBackBtn.setVisibility(View.VISIBLE);
//            findViewById(R.id.btnTitleLeftBackLy).setVisibility(View.VISIBLE);
//            findViewById(R.id.btnTitleLeftLy).setVisibility(View.INVISIBLE);
//        }
    }

    void finishActivity() {
        ProgressWebView progressWebView = wvController.getCurrentProgressWebView();
        if (progressWebView != null && progressWebView.canGoBack()) {
            String title = StringUtil.getTitleAtUrl(progressWebView.getLastHistoryUrl(), "naviBarTitle");
            if (title != null) chageTitle(title);
            progressWebView.goBack();
        } else {
            DataTransManager dtMan = DataTransManager.getInstance();
            if (dtMan != null) {
                if (!m_isPresentEvent && dtMan.isShownPresentPage()) {
                    SnapsMenuManager.gotoPresentPage(this, null, null);
                    return;
                }
                dtMan.setShownPresentPage(false);
            } else {
                DataTransManager.notifyAppFinish(this);
                return;
            }

            if (m_orientation != null) {
                finish();
            } else {
                if (m_isFromHomeActivity) {
                    Intent intent = new Intent(this, RenewalHomeActivity.class);
                    intent.putExtra("gototarget", "home");
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }

                finish();
            }
        }
    }

    public void onClick(View v) {
        if (v.getId() == R.id.btnTitleLeftBack || (v.getId() == R.id.btnTitleLeftBackLy))
            finishActivity();
        else if (v.getId() == R.id.btnTitleLeft || (v.getId() == R.id.btnTitleLeftLy)) {
//            if (slidingMenu.isMenuShowing()) {
//                slidingMenu.showContent();
//                if (mHomeAlpha != null) mHomeAlpha.setVisibility(View.GONE);
//                if (Config.isSnapsBitween()) {
//                    ImageView menuImg = (ImageView) findViewById(R.id.btnTopMenuAlpha);
//                    if (menuImg != null)
//                        menuImg.setVisibility(View.GONE);
//                }
//            } else {
                homeMenuGridFragment.setCartCount();
//                slidingMenu.showMenu();
                if (mHomeAlpha != null) mHomeAlpha.setVisibility(View.VISIBLE);
                if (Config.isSnapsBitween()) {
                    ImageView menuImg = (ImageView) findViewById(R.id.btnTopMenuAlpha);
                    if (menuImg != null)
                        menuImg.setVisibility(View.VISIBLE);
                }
//            }
        }
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        if (!SnapsTPAppManager.isThirdPartyApp(this) && facebook != null)
            facebook.addCallback();
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();

        if (!SnapsTPAppManager.isThirdPartyApp(this) && facebook != null)
            facebook.removeCallback();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (!SnapsTPAppManager.isThirdPartyApp(this))
            SNSShareUtil.postActivityForResult(requestCode, resultCode, data);

        if (facebook != null)
            facebook.onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    public void onPageStarted(String url) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPageFinished(String url) {
        String title = StringUtil.getTitleAtUrl(url, "naviBarTitle");
        if (title != null) {
            chageTitle(title);
        }
    }

    @Override
    public void onGoHome() {
        finish();
    }

    @Override
    public void onCloseMenu() {
//        if (slidingMenu.isMenuShowing()) {
//            slidingMenu.showContent();
//            if (mHomeAlpha != null) mHomeAlpha.setVisibility(View.GONE);
//            if (Config.isSnapsBitween()) {
//                // ImageView menuImg = (ImageView) findViewById(R.id.btnTopMenuAlpha);
//                // if (menuImg != null)
//                // menuImg.setVisibility(View.GONE);
//            }
//        }
    }

    private void checkOrientationState() {
        m_orientation = getIntent().getStringExtra("orientation");
        m_isFromHomeActivity = getIntent().getBooleanExtra("fromHomeActivity", true);

        boolean isExistTitleBar = true;
        if (m_orientation != null) {
            isExistTitleBar = false;
            if (m_orientation.equals("h")) {
                UIUtil.updateFullscreenStatus(this, false);
                // portrait
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            } else if (m_orientation.equals("w")) {
                UIUtil.updateFullscreenStatus(this, true);
                // landscape
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        }
    }

    private void showProductInfoPage(SnapsMenuManager.UIStickyInfo stickyInfo) {
        if (stickyInfo == null) return;

        SnapsShouldOverrideUrlLoader urlLoder = new SnapsShouldOverrideUrlLoader(this, SnapsShouldOverrideUrlLoader.WEB);
        urlLoder.shouldOverrideUrlLoading(stickyInfo.getInfoUrl());
    }

    @Override
    public void onSnapsStickyViewClicked(int what, SnapsMenuManager.UIStickyInfo stickyInfo) {
        switch (what) {
            case SnapsStickyLayout.ISnapsStickyClickListener.STICKY_CONTROL_ID_STICKY_AREA_INFO:
                showProductInfoPage(stickyInfo);
                break;
            case SnapsStickyLayout.ISnapsStickyClickListener.STICKY_CONTROL_ID_TITLE_BAR_BACK_KEY:
                onBackPressed();
                break;
            case SnapsStickyLayout.ISnapsStickyClickListener.STICKY_CONTROL_ID_TITLE_BAR_INFO:
                showProductInfoPage(stickyInfo);
                break;
            case SnapsStickyLayout.ISnapsStickyClickListener.STICKY_CONTROL_ID_TITLE_BAR_MENU:
                SnapsMenuManager.showHamburgerMenu(this, SnapsMenuManager.eHAMBURGER_ACTIVITY.ETC);
                break;
        }
    }
}
