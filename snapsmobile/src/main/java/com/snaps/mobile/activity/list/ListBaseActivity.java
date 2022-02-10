package com.snaps.mobile.activity.list;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStripForSticky;
import com.snaps.common.model.NativeProductListPage;
import com.snaps.common.utils.constant.Const_EKEY;
import com.snaps.common.utils.pref.Setting;
import com.snaps.common.utils.ui.IFacebook;
import com.snaps.common.utils.ui.IKakao;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.common.utils.ui.TabStyleNativeScrollViewController;
import com.snaps.common.utils.ui.UI;
import com.snaps.kakao.utils.share.SNSShareUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.hamburger_menu.SnapsMenuManager;
import com.snaps.mobile.activity.home.fragment.GoHomeOpserver;
import com.snaps.mobile.activity.ui.menu.renewal.model.Item;
import com.snaps.mobile.activity.ui.menu.renewal.model.SubCategory;
import com.snaps.mobile.activity.ui.menu.webinterface.SnapsShouldOverrideUrlLoader;
import com.snaps.mobile.activity.ui.menu.webinterface.web_event_handlers.SnapsProductListParams;
import com.snaps.mobile.base.SnapsBaseFragmentActivity;
import com.snaps.mobile.component.CustomSensitivityViewPager;
import com.snaps.mobile.component.SnapsNativeListViewProcess;
import com.snaps.mobile.product_native_ui.json.SnapsProductNativeUIBaseResultJson;
import com.snaps.mobile.product_native_ui.json.list.SnapsProductDesignCategory;
import com.snaps.mobile.product_native_ui.json.list.SnapsProductDesignList;
import com.snaps.mobile.product_native_ui.json.list.SnapsProductSizeList;
import com.snaps.mobile.product_native_ui.util.SnapsNativeUIManager;
import com.snaps.mobile.product_native_ui.util.SnapsProductNativeUIUtil;
import com.snaps.mobile.utils.thirdparty.SnapsTPAppManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import errorhandle.logger.SnapsLogger;
import errorhandle.logger.model.SnapsLoggerClass;
import errorhandle.logger.web.WebLogConstants;
import errorhandle.logger.web.request.WebLogRequestBuilder;

public class ListBaseActivity extends SnapsBaseFragmentActivity implements GoHomeOpserver.OnGoHomeOpserver, SnapsProductNativeUIUtil.ISnapsProductNativeUIInterfaceCallback {
    protected static String SIMPLE_MAKING_GUIDE_SHOWN = "simple_making_guide_shown";

    protected TabStyleNativeScrollViewController wvController;

    protected SubCategory subCategory;

    protected IKakao kakao = null;
    protected IFacebook facebook = null;

    protected boolean m_isSizeTypeUI = false; //사진인화, 달력 등은 사이즈 선택 화면이다.

    protected View lyNetworkErr = null;

    protected SnapsProductListParams productListParams;

    protected boolean isMultiSubMenu = false;

    @SuppressLint("InflateParams")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        //Kitkat에서 하드웨어 가속을 사용하면 오류가 발생함.
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                    WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        }

        super.onCreate(savedInstanceState);

        SnapsLogger.appendClassTrackingLog(new SnapsLoggerClass<>(this));

        setContentView(R.layout.activity_list);

        lyNetworkErr = findViewById(R.id.ly_sticky_network_err_parent);

        Intent getItt = getIntent();
        if (getItt != null) {
            Bundle bundle = getItt.getExtras();
            if(bundle != null) {
                bundle.setClassLoader(SnapsProductListParams.class.getClassLoader());

                String title = bundle.getString(Const_EKEY.WEBVIEW_TITLE);
                chageTitle( title );

                productListParams = (SnapsProductListParams) bundle.getSerializable(Const_EKEY.NATIVE_UI_PARAMS);

                m_isSizeTypeUI = bundle.getBoolean(Const_EKEY.NATIVE_UI_SIZE_TYPE, false);
            }
        }

        if (subCategory == null) {
            final SnapsMenuManager menuMan = SnapsMenuManager.getInstance();
            if (menuMan != null) {
                subCategory = menuMan.getSubCategory();
            }
        }

        if (subCategory == null) {
            finishActivity();
            return;
        }

        isMultiSubMenu = subCategory.isMultiSubMenu();

        initUI();

        initNetworkErrUI(); //네트워크 오류 UI 초기화

        initSnapsNativeListProcess();

        loadProductList();

        GoHomeOpserver.addGoHomeListener(this);
    }

    private void initUI() {
        if (subCategory == null) return;

        wvController = new TabStyleNativeScrollViewController(this);

        ImageView gradLeft = (ImageView) findViewById(R.id.snpas_sticky_id_tab_strip_grad_left);
        ImageView gradRight = (ImageView) findViewById(R.id.snpas_sticky_id_tab_strip_grad_right);

        ImageView btnInfo = (ImageView) findViewById(R.id.btnTitleInfo);
        ImageView btnHamburger = (ImageView) findViewById(R.id.btnTitleHamburgerMenu);

        boolean showInfoButton = !StringUtil.isEmpty( subCategory.getInfoUrl() );
        btnInfo.setVisibility( showInfoButton ? View.VISIBLE : View.GONE );
        btnHamburger.setVisibility( View.GONE );

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) btnInfo.getLayoutParams();
        params.addRule( RelativeLayout.ALIGN_PARENT_RIGHT, 1 );
        //params.addRule( RelativeLayout.LEFT_OF, R.id.btnTitleHamburgerMenu );
        btnInfo.setLayoutParams( params );

        btnInfo.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProductInfoPage( subCategory.getInfoUrl() );
            }
        } );
        btnHamburger.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SnapsMenuManager.showHamburgerMenu( ListBaseActivity.this, SnapsMenuManager.eHAMBURGER_ACTIVITY.ETC );
            }
        } );

        if (isMultiSubMenu) {
            gradLeft.setVisibility(View.VISIBLE);
            gradRight.setVisibility(View.VISIBLE);
        }

        if (subCategory.isMultiSubMenu()) {
            ArrayList<Item> subRes = subCategory.getItems();
            for (Item subMenu : subRes) {
                if (subMenu == null) continue;

                NativeProductListPage listPage = new NativeProductListPage(false, subMenu.getName(),
                        subMenu.isShowNewTag());

                wvController.addPage(listPage);
            }
        }
        wvController.setViews((RelativeLayout) findViewById(R.id.snpas_sticky_id_full_screen_title), (FrameLayout) findViewById(R.id.header), (CustomSensitivityViewPager) findViewById(R.id.pager));
    }

    private void initNetworkErrUI() {
        if (lyNetworkErr == null) return;

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) lyNetworkErr.getLayoutParams();
        int singleFragmentTypeOffset = (int) getResources().getDimension(R.dimen.snaps_sticky_strip_offset_y); //200dp (상단 큰 이미지 영역)
        int multiTypeOffset = singleFragmentTypeOffset + (int) getResources().getDimension(R.dimen.snaps_sticky_viewpager_strip_height); //48dp (상단 큰 이미지 영역 + 스트립)
        layoutParams.topMargin = isMultiSubMenu ? multiTypeOffset : singleFragmentTypeOffset;

        lyNetworkErr.setLayoutParams(layoutParams);
    }

    private void setNetworkErrUI(boolean isError) {
        if (lyNetworkErr == null) return;
        lyNetworkErr.setVisibility(isError ? View.VISIBLE : View.GONE);

        PagerSlidingTabStripForSticky tabStrip = (PagerSlidingTabStripForSticky) findViewById(R.id.snpas_sticky_id_tab_strip);
        if (tabStrip == null) return;
        if (isError) {
            tabStrip.setStripClickable(false);
        } else {
            tabStrip.setStripClickable(true);
        }
    }

    /*
     * 인터페이스를 통한 ProductList 조회가 완료되었을 때 호출 된다.
     */
    @Override
    public void onNativeProductInfoInterfaceResult(boolean result, SnapsProductNativeUIBaseResultJson resultObj) {
        if (result) {
            setNetworkErrUI(false);

            initProductList(resultObj);

            //showSimpleMakingBookTutorial();

            if( wvController != null ) {
                boolean isExpandedType = !subCategory.isFixArrangeType();   //하위 메뉴가 많아서 스트립 메뉴가 스크롤되어야 하는 형태.
                wvController.initialize( isExpandedType ? PagerSlidingTabStripForSticky.TAB_TYPE_AUTO : PagerSlidingTabStripForSticky.TAB_TYPE_DEFAULT );
            }
        } else {
            setNetworkErrUI(true);
        }
    }

    private void loadProductList() {
        //결과는 onNativeProductInfoInterfaceResult 에서 처리 한다.
        SnapsProductNativeUIUtil.requestProductList(this, m_isSizeTypeUI, productListParams, this);

    }


    private void initProductList(SnapsProductNativeUIBaseResultJson jsonObj) {
        if (jsonObj == null || subCategory == null || wvController == null) {
            finishActivity();
            return;
        }

        chageTitle(subCategory.getTitle());

        NativeProductListPage listPage;

        wvController.clearPage();

        if (jsonObj instanceof SnapsProductDesignList) {
            SnapsProductDesignList productList = (SnapsProductDesignList) jsonObj;

            if( subCategory != null && !subCategory.getItems().isEmpty() ) { // 순서를 interface의 결과 값이 아닌 menudata의 subcategory의 순서로 변경.
                Map<String, SnapsProductDesignCategory> map = productList.getProductMap();
                String key;
                SnapsProductDesignCategory category;
                for( Item item : subCategory.getItems() ) {
                    key = item.getName();
                    if( !map.containsKey(key) ) continue;

                    category = map.get( key );
                    listPage = new NativeProductListPage( category );
                    listPage.setTitle( category.getCATEGORY_NAME() );
                    listPage.setIsBadgeExist( category.isNEW() || item.isShowNewTag());

                    wvController.addPage(listPage);
                }
            }
            else { // subcategory items가 없을때는 기존 방식.
                List<SnapsProductDesignCategory> categories = productList.getProductList();
                if (categories != null) {
                    for (SnapsProductDesignCategory category : categories) {
                        if (category == null) continue;

                        listPage = new NativeProductListPage(category);
                        listPage.setTitle(category.getCATEGORY_NAME());
                        listPage.setIsBadgeExist(category.isNEW());

                        wvController.addPage(listPage);
                    }
                }
            }
        } else if (jsonObj instanceof SnapsProductSizeList) {
            SnapsProductSizeList sizeList = (SnapsProductSizeList) jsonObj;
            listPage = new NativeProductListPage(sizeList);
            listPage.setTitle(subCategory.getTitle());
            wvController.addPage(listPage);
        }

        // 하드코딩으로 상품 구분할때 F_PROD_CODE는 바뀔수 있으므로 F_SCLSS_CODE로 구분.
        wvController.initialize();

        wvController.reload();
    }

    private void initSnapsNativeListProcess() {
        SnapsNativeUIManager menuDataManager = SnapsNativeUIManager.getInstance();
        if (menuDataManager != null) {
            menuDataManager.setCurrentProductListParams(productListParams);
            menuDataManager.initNativeListViewProcess(this);
        }
    }

    private void showSimpleMakingBookTutorial() {
        String clssCode = null;
        if (productListParams != null) {
            clssCode = productListParams.getClssCode();
        }

        if (clssCode != null && clssCode.equalsIgnoreCase("KOR0031002001000") && !Setting.getBoolean(this, SIMPLE_MAKING_GUIDE_SHOWN) && !SnapsTPAppManager.isThirdPartyApp(this)) {
            if (!Setting.getBoolean(this, SIMPLE_MAKING_GUIDE_SHOWN) && !SnapsTPAppManager.isThirdPartyApp(this)) {
                LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View view = layoutInflater.inflate(R.layout.simple_making_book_guide, null);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                view.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return true;
                    }
                });
                view.findViewById(R.id.btn_02).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Setting.set(ListBaseActivity.this, SIMPLE_MAKING_GUIDE_SHOWN, true);
                        ((ViewGroup) view.getParent()).removeView(view);
                    }
                });
                view.findViewById(R.id.btn_01).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Setting.set(ListBaseActivity.this, SIMPLE_MAKING_GUIDE_SHOWN, true);

                        SnapsNativeUIManager dataManager = SnapsNativeUIManager.getInstance();
                        if (dataManager != null) {
                            SnapsNativeListViewProcess nativeListViewProcess = dataManager.getNativeListViewProcess(ListBaseActivity.this);
                            if (nativeListViewProcess != null) {
                                nativeListViewProcess.shouldOverrideUrlLoading(null, "snapsapp://openAppPopup?openUrl=%2Fmw%2Fv3%2Fstore%2Finformation%2Finfo_easyBook.jsp");
                            }
                        }

                        ((ViewGroup) view.getParent()).removeView(view);
                    }
                });

                addContentView(view, params);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        setNetworkErrUI(false);

        GoHomeOpserver.removeGoHomeListenrer(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (wvController != null) {
            wvController.reload();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        finishActivity();
    }

    void finishActivity() {
        finish();
    }

    public void onClick(View v) {
        if (v.getId() == R.id.btnTitleLeftBack || (v.getId() == R.id.btnTitleLeftBackLy)
                || v.getId() == R.id.btnTitleLeft || (v.getId() == R.id.btnTitleLeftLy))
            finishActivity();
        else if (v.getId() == R.id.btn_sticky_network_err_retry) {
            if (lyNetworkErr != null) {
                lyNetworkErr.setVisibility(View.GONE);
            }

            loadProductList();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!SnapsTPAppManager.isThirdPartyApp(this) && facebook != null)
            facebook.addCallback();
    }

    @Override
    protected void onStop() {
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
    public void onGoHome() {
        finish();
    }

    private void showProductInfoPage( String url ) {
        if ( StringUtil.isEmpty(url) ) return;

        SnapsShouldOverrideUrlLoader urlLoder = new SnapsShouldOverrideUrlLoader(this, SnapsShouldOverrideUrlLoader.WEB);
        urlLoder.shouldOverrideUrlLoading( url );
    }

    protected void chageTitle(String title) {
        if (title != null && !title.equals("")) {
            UI.<TextView>findViewById(this, R.id.txtTitleText).setText(title);
        }
    }
}
