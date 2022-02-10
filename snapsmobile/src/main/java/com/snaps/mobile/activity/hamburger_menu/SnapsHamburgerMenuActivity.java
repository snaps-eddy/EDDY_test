package com.snaps.mobile.activity.hamburger_menu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_EKEY;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.constant.Const_VALUES;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.pref.Setting;
import com.snaps.common.utils.ui.DataTransManager;
import com.snaps.common.utils.ui.FragmentUtil;
import com.snaps.common.utils.ui.IntentUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.diary.activities.SnapsDiaryMainActivity;
import com.snaps.mobile.activity.hamburger_menu.interfacies.ISnapsHamburgerMenuListener;
import com.snaps.mobile.activity.home.RenewalHomeActivity;
import com.snaps.mobile.activity.home.fragment.GoHomeOpserver;
import com.snaps.mobile.activity.home.utils.SnapsLoginManager;
import com.snaps.mobile.activity.intro.IAfterLoginProcess;
import com.snaps.mobile.activity.intro.fragment.JoinFragment;
import com.snaps.mobile.activity.intro.fragment.LoginFragment;
import com.snaps.mobile.activity.intro.fragment.PwdFindFragment;
import com.snaps.mobile.activity.intro.fragment.PwdResetFragment;
import com.snaps.mobile.activity.intro.fragment.RestIdFragment;
import com.snaps.mobile.activity.intro.fragment.RetireFragment;
import com.snaps.mobile.activity.intro.fragment.VerifyPhoneFragment;
import com.snaps.mobile.activity.intro.fragment.VerifyPhonePopupFragment;
import com.snaps.mobile.activity.setting.SnapsSettingActivity;
import com.snaps.mobile.activity.ui.menu.renewal.MenuDataManager;
import com.snaps.mobile.activity.webview.DetailProductWebviewActivity;
import com.snaps.mobile.utils.thirdparty.SnapsTPAppManager;
import com.snaps.common.utils.ISnapsHandler;
import com.snaps.common.structure.SnapsHandler;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import errorhandle.CatchFragmentActivity;
import errorhandle.logger.SnapsLogger;
import errorhandle.logger.model.SnapsLoggerClass;

/**
 * Created by ysjeong on 16. 8. 8..
 */
public class SnapsHamburgerMenuActivity extends CatchFragmentActivity implements ISnapsHamburgerMenuListener, ISnapsHandler, IAfterLoginProcess {
    private static final String TAG = SnapsHamburgerMenuActivity.class.getSimpleName();
    private SnapsHamburgerMenuFragment mMenuFragment    = null;

    private SnapsHandler mHandler                       = null;

    private SnapsMenuManager.eHAMBURGER_ACTIVITY eCurrentActivity = null;
    private SnapsMenuManager.eHAMBUGER_FRAGMENT eCurrentFragment = null;

    private int m_iMoveWhere = MOVE_TO_HOME;

    private SnapsMenuManager.eHAMBUGER_FRAGMENT eFirstFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SnapsLogger.appendClassTrackingLog(new SnapsLoggerClass<>(this));

        setContentView(R.layout.activity_hamburger_menu);

        int ordinalActivity = getIntent().getIntExtra(Const_VALUES.EXTRAS_HAMBURGER_MENU_ACT, -1);
        if (ordinalActivity >= 0 && ordinalActivity < SnapsMenuManager.eHAMBURGER_ACTIVITY.values().length) {
            eCurrentActivity = SnapsMenuManager.eHAMBURGER_ACTIVITY.values()[ordinalActivity];
        }

        int ordinalFragment = getIntent().getIntExtra(Const_VALUES.EXTRAS_HAMBURGER_MENU_FRG, -1);
        if (ordinalFragment >= 0 && ordinalFragment < SnapsMenuManager.eHAMBUGER_FRAGMENT.values().length) {
            eCurrentFragment = SnapsMenuManager.eHAMBUGER_FRAGMENT.values()[ordinalFragment];
        } else {
            eCurrentFragment = SnapsMenuManager.eHAMBUGER_FRAGMENT.MAIN_MENU;
        }

        eFirstFragment = eCurrentFragment;

        m_iMoveWhere = getIntent().getIntExtra(Const_EKEY.LOGIN_AFTER_WHERE, MOVE_TO_HOME);

        mHandler = new SnapsHandler(this);

        replaceCurrentFragment();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * Fragments내의 메뉴를 선택하거나, Fragment 내에서 화면 전환이 필요할 때 호출된다.
     * @param msgWhat
     */
    @Override
    public void onHamburgerMenuPostMsg(int msgWhat) {
        switch (msgWhat) {
            case ISnapsHamburgerMenuListener.MSG_COMPLATE_REST_ID://추가인증 팝업 완료
                if(eCurrentFragment.equals(SnapsMenuManager.eHAMBUGER_FRAGMENT.REST_ID)) {
                    finishActivity();
                }else {
                    RestIdFragment restIdFragment = RestIdFragment.newInstance(this);
                    FragmentUtil.replce(R.id.activity_hamburger_frame, this, restIdFragment, null, R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
                    eCurrentFragment = SnapsMenuManager.eHAMBUGER_FRAGMENT.REST_ID;
                }
                break;
            case ISnapsHamburgerMenuListener.MSG_COMPLATE_VERIFI_POPUP://추가인증 팝업 완료
                LoginFragment loginFragment2 = LoginFragment.newInstance(this);
                FragmentUtil.replce(R.id.activity_hamburger_frame, this, loginFragment2, null, R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
                eCurrentFragment = SnapsMenuManager.eHAMBUGER_FRAGMENT.LOG_IN;
                break;
            case ISnapsHamburgerMenuListener.MSG_COMPLATE_JOIN_EVEVT://회원가입후 추가인증 이벤트로 가기
                VerifyPhonePopupFragment verifyPhoneFragment = VerifyPhonePopupFragment.newInstance(this);
                FragmentUtil.replce(R.id.activity_hamburger_frame, this, verifyPhoneFragment, null, R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
                eCurrentFragment = SnapsMenuManager.eHAMBUGER_FRAGMENT.VERIFY_PHONE;
                break;
            case ISnapsHamburgerMenuListener.MSG_COMPLATE_JOIN: //회원 가입 완료
                LoginFragment loginFragment = LoginFragment.newInstance(this);
                FragmentUtil.replce(R.id.activity_hamburger_frame, this, loginFragment, null, R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
                eCurrentFragment = SnapsMenuManager.eHAMBUGER_FRAGMENT.LOG_IN;
                break;
            case ISnapsHamburgerMenuListener.MSG_COMPLATE_LOG_IN: //로그인 완료
                if (eFirstFragment.equals(SnapsMenuManager.eHAMBUGER_FRAGMENT.MAIN_MENU)) {
                    replaceMainMenuFragment(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
                }
                else
                    finishActivity();
                break;
            case ISnapsHamburgerMenuListener.MSG_COMPLATE_RETIRE : //탈퇴 완료
                if (eFirstFragment.equals(SnapsMenuManager.eHAMBUGER_FRAGMENT.MAIN_MENU)) {
                    replaceMainMenuFragment(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
                } else {
                    finishActivity();
                }
                break;
            case ISnapsHamburgerMenuListener.MSG_COMPLATE_PWD_RESET : //비밀번호 변경 완료
                finishActivity();
                break;
            case ISnapsHamburgerMenuListener.MSG_MOVE_TO_LOG_IN: //로그인 화면으로 이동
                replaceLoginFragment(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
                break;
            case ISnapsHamburgerMenuListener.MSG_MOVE_TO_JOIN: //회원 가입 화면으로 이동
                replaceJoinFragment(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
                break;
            case ISnapsHamburgerMenuListener.MSG_MOVE_TO_BACK: //뒤로 가기
                goBack();
                break;
            case ISnapsHamburgerMenuListener.MSG_MOVE_TO_COMPLETED_VERIFY:
            case ISnapsHamburgerMenuListener.MSG_COMPLATE_VERIFI://추가인증 완료
            case ISnapsHamburgerMenuListener.MSG_MOVE_TO_HOME: //로그인 체크 필요 없이 메뉴 이동
            case ISnapsHamburgerMenuListener.MSG_MOVE_TO_EVENT:
            case ISnapsHamburgerMenuListener.MSG_MOVE_TO_SETTING:
            case ISnapsHamburgerMenuListener.MSG_MOVE_TO_NOTICE:
            case ISnapsHamburgerMenuListener.MSG_MOVE_TO_CUSTOMER:
                startActivityAfterClosedMenu(msgWhat);
                break;
            case ISnapsHamburgerMenuListener.MSG_MOVE_TO_PWD_FIND:
                replacePwdFindFragment(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
                break;

            case ISnapsHamburgerMenuListener.MSG_MOVE_TO_COUPON: //로그인 체크 후 메뉴 이동
            case ISnapsHamburgerMenuListener.MSG_MOVE_TO_CART:
            case ISnapsHamburgerMenuListener.MSG_MOVE_TO_DIARY:
            case ISnapsHamburgerMenuListener.MSG_MOVE_TO_ORDER:
            case ISnapsHamburgerMenuListener.MSG_MOVE_TO_MY_SNAPS: //FIXME 아이콘 변경해야함.
                String userNo = Setting.getString(this, Const_VALUE.KEY_SNAPS_USER_NO);
                if (userNo.length() > 0)
                    startActivityAfterClosedMenu(msgWhat);
                else
                    replaceLoginFragment(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        goBack();
    }

    private void goBack() {
        if (eFirstFragment.equals(eCurrentFragment)) {
            finishActivity();
            return;
        }

        switch (eCurrentFragment) {
            case JOIN:
                replaceLoginFragment(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
                break;
            case LOG_IN:
                replaceMainMenuFragment(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
                break;
            case PWD_FIND:
                replaceLoginFragment(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
                break;
//            case PWD_RESET:
//                replaceMainMenuFragment(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right); //FIXME 맞나?
//                break;
//            case RETIRE:
//                replaceMainMenuFragment(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right); //FIXME 맞나?
//                break;
            case VERIFY_PHONE:
                String userNo=  SnapsLoginManager.getUUserNo(this);
                if(TextUtils.isEmpty(userNo))
                    replaceLoginFragment(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
                else
                    finishActivity();

                break;
            case MAIN_MENU:
            default:
                finishActivity();
                break;
        }
    }

    /**
     * 이벤트 화면으로 이
     */
    private void startActivityAfterClosedMenu(int type) {
        finishActivity();

        Message msg = new Message();
        msg.arg1 = type;
        msg.what = MOVE_ACTIVITY_AFTER_HAMBURGER_MENU_CLOSED;
        int animTime = (int) (getResources().getInteger(R.integer.anim_time_fade_in_out) * .55f); //애니메이션이 완전히 종료 되기 전에 좀 더 빨리 넘어가게 한다.

        mHandler.sendMessageDelayed(msg, animTime); //애니메이션이 끝나는 시점.
    }

    public void finishActivity() {
        finish();

        if (eFirstFragment.equals(SnapsMenuManager.eHAMBUGER_FRAGMENT.MAIN_MENU))
            overridePendingTransition(0, R.anim.anim_fade_out);
        else
            overridePendingTransition(0, R.anim.anim_fade_out);
    }

    private void goCustomerActivity() {
        sendPageEventTracker( R.string.action_faq );

        SnapsTPAppManager.goCustomerActivity(this);
    }

    private void goEventActivity() {
        SnapsMenuManager.gotoPresentPage(this, null, null);

        SnapsMenuManager.requestFinishPrevActivity();
    }

    private void goDiaryActivity() {
        sendPageEventTracker(R.string.action_diary);

        Intent diaryItt = new Intent(this, SnapsDiaryMainActivity.class);
        startActivity(diaryItt);

        SnapsMenuManager.requestFinishPrevActivity();
    }

    private void goSettingActivity() {
        sendPageEventTracker( R.string.action_setting );

        Intent intent = new Intent(this, SnapsSettingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        IntentUtil.startActivity(this, intent);

        SnapsMenuManager.requestFinishPrevActivity();
    }

    private String getCartListText() {
        String naviBarTitle = "";
        try {
            naviBarTitle = URLEncoder.encode(getString(R.string.cart), "utf-8");
        } catch (UnsupportedEncodingException e) {
            Dlog.e(TAG, e);
        }
        return naviBarTitle;
    }

    private void goNoticeActivity() {
        DataTransManager dataTransManager = DataTransManager.getInstance();
        if (dataTransManager != null) {
            dataTransManager.setShownPresentPage(false);
        }

        sendPageEventTracker(R.string.action_notice);

        Setting.set(this, Const_VALUE.KEY_NOTICE_OLD_VERSION, Setting.getInt(this, Const_VALUE.KEY_NOTICE_NEW_VERSION));

        String noticeIdx = null;
        MenuDataManager menuDataManager = MenuDataManager.getInstance();
        if (menuDataManager != null && menuDataManager.getNoticeItem() != null)
            noticeIdx = menuDataManager.getNoticeItem().getSeq();

        SnapsMenuManager.goToNoticeList(this, SnapsTPAppManager.getNoticeUrl(this), noticeIdx);

        //공지사항은 팝업 컨셉이라고 하니, stack에 쌓는다.
//        SnapsMenuManager.requestFinishPrevActivity();
    }

    private void goCouponActivity() {
        sendPageEventTracker(R.string.action_coupon);

        SnapsMenuManager.gotoCouponAct(this, SnapsTPAppManager.getCouponUrl(this));

        SnapsMenuManager.requestFinishPrevActivity();
    }

    private void goHomeLogin(){
        Intent intent = new Intent(this, RenewalHomeActivity.class);
        intent.putExtra("verifyComplete", true);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void goCartActivity() {
        sendPageEventTracker( R.string.action_cart_page );
        // 장바구니로 가기...
        int _cart_count = Setting.getInt(this, Const_VALUE.KEY_CART_COUNT);
        String query = SnapsTPAppManager.getBaseQuary(this);

        if (SnapsTPAppManager.isThirdPartyApp(this)) {
            String naviBarTitle = getCartListText();
            SnapsTPAppManager.gotoCartList(this, _cart_count, naviBarTitle, query);
        } else {
            SnapsTPAppManager.gotoCartList(this, _cart_count, getString(R.string.cart), query);
        }

        SnapsMenuManager.requestFinishPrevActivity();
    }

    private void goMySnapsActivity() {
//        String openurl = SnapsAPI.WEB_DOMAIN() + SnapsAPI.MY_GRADE_URL + "type=mySnaps&f_user_no=" + Setting.getString(this, Const_VALUE.KEY_SNAPS_USER_NO) + "&prmChnlCode=" + Config.getCHANNEL_CODE() + "&f_chnl_code=" + Config.getCHANNEL_CODE();
        if (!Config.useKorean()) return;

        String openurl = SnapsAPI.WEB_DOMAIN() + SnapsAPI.BENEFIT_URL() + "f_user_no=" + Setting.getString(this, Const_VALUE.KEY_SNAPS_USER_NO);

        openurl = SnapsTPAppManager.getSnapsWebDomain(this, openurl, SnapsTPAppManager.getBaseQuary(this, false));

        Intent intent = DetailProductWebviewActivity.getIntent(this, getString(R.string.benefit_title), openurl, SnapsMenuManager.eHAMBURGER_ACTIVITY.MY_BENEFIT);
        startActivity(intent);

        SnapsMenuManager.requestFinishPrevActivity();
    }

    private void goOrderDeliveryActivity() {
        String url = SnapsAPI.WEB_DOMAIN(SnapsAPI.ORDER_URL(), SnapsLoginManager.getUUserNo(this), "");
        Intent intent = DetailProductWebviewActivity.getIntent(this, this.getString(R.string.order_and_delivery), url, true, SnapsMenuManager.eHAMBURGER_ACTIVITY.ORDER);
        startActivity(intent);

        SnapsMenuManager.requestFinishPrevActivity();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 999) {
            // 로그인 이벤트 화면이 띄어지고 닫히는경우
            String loginProcess = getIntent().getStringExtra(Const_EKEY.LOGIN_PROCESS);
            if (Const_VALUES.LOGIN_P_RESULT.equals(loginProcess)) {
                setResult(Activity.RESULT_OK);
            }
            finish();
        }
    }

    /**
     * 액티비티를 완전히 닫고 나서 액티비티 이동 처리
     */
    public static final int MOVE_ACTIVITY_AFTER_HAMBURGER_MENU_CLOSED = 100;

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case MOVE_ACTIVITY_AFTER_HAMBURGER_MENU_CLOSED:
                int type = msg.arg1;
                switch (type) {
                    case ISnapsHamburgerMenuListener.MSG_MOVE_TO_HOME:
                        if (eCurrentActivity != null && eCurrentActivity.equals(SnapsMenuManager.eHAMBURGER_ACTIVITY.HOME))
                            return;
                        GoHomeOpserver.notifyGoHome(this);
                        break;
                    case ISnapsHamburgerMenuListener.MSG_MOVE_TO_EVENT :
                        if (eCurrentActivity != null && eCurrentActivity.equals(SnapsMenuManager.eHAMBURGER_ACTIVITY.EVENT))
                            return;
                        goEventActivity();
                        break;
                    case ISnapsHamburgerMenuListener.MSG_MOVE_TO_DIARY:
                        if (eCurrentActivity != null && eCurrentActivity.equals(SnapsMenuManager.eHAMBURGER_ACTIVITY.DIARY))
                            return;
                        goDiaryActivity();
                        break;
                    case ISnapsHamburgerMenuListener.MSG_MOVE_TO_CUSTOMER:
                        if (eCurrentActivity != null && eCurrentActivity.equals(SnapsMenuManager.eHAMBURGER_ACTIVITY.CUSTOMER))
                            return;
                        goCustomerActivity();
                        break;
                    case ISnapsHamburgerMenuListener.MSG_MOVE_TO_SETTING:
                        if (eCurrentActivity != null && eCurrentActivity.equals(SnapsMenuManager.eHAMBURGER_ACTIVITY.SETTING))
                            return;
                        goSettingActivity();
                        break;
                    case ISnapsHamburgerMenuListener.MSG_MOVE_TO_ORDER:
                        if (eCurrentActivity != null && eCurrentActivity.equals(SnapsMenuManager.eHAMBURGER_ACTIVITY.ORDER))
                            return;
                        goOrderDeliveryActivity();
                        break;
                    case ISnapsHamburgerMenuListener.MSG_MOVE_TO_MY_SNAPS:
                        if (eCurrentActivity != null && eCurrentActivity.equals(SnapsMenuManager.eHAMBURGER_ACTIVITY.MY_BENEFIT))
                            return;
                        goMySnapsActivity();
                        break;
                    case ISnapsHamburgerMenuListener.MSG_MOVE_TO_CART:
                        if (eCurrentActivity != null && eCurrentActivity.equals(SnapsMenuManager.eHAMBURGER_ACTIVITY.CART))
                            return;
                        goCartActivity();
                        break;
                    case ISnapsHamburgerMenuListener.MSG_MOVE_TO_COUPON:
                        if (eCurrentActivity != null && eCurrentActivity.equals(SnapsMenuManager.eHAMBURGER_ACTIVITY.COUPON))
                            return;
                        goCouponActivity();
                        break;
                    case ISnapsHamburgerMenuListener.MSG_MOVE_TO_NOTICE:
                        if (eCurrentActivity != null && eCurrentActivity.equals(SnapsMenuManager.eHAMBURGER_ACTIVITY.NOTICE))
                            return;
                        goNoticeActivity();
                        break;
                    case ISnapsHamburgerMenuListener.MSG_COMPLATE_VERIFI:
                        goHomeLogin();
                        break;
                    case ISnapsHamburgerMenuListener.MSG_MOVE_TO_COMPLETED_VERIFY:
                        finishActivity();
                        break;
                }
            break;
        }
    }

    private void replaceMainMenuFragment(int animIn, int animOut) {
        if (mMenuFragment == null)
            mMenuFragment = SnapsHamburgerMenuFragment.newInstance(this, eCurrentActivity);
        FragmentUtil.replce(R.id.activity_hamburger_frame, this, mMenuFragment, null, animIn, animOut);

        eCurrentFragment = SnapsMenuManager.eHAMBUGER_FRAGMENT.MAIN_MENU;
    }

    private void replaceLoginFragment(int animIn, int animOut) {
        LoginFragment loginFragment = LoginFragment.newInstance(this);

        Bundle bundle = new Bundle();
        bundle.putBoolean("isFromHamburgerMenu", eFirstFragment != null && eFirstFragment.equals(SnapsMenuManager.eHAMBUGER_FRAGMENT.MAIN_MENU));
        loginFragment.setArguments(bundle);

        FragmentUtil.replce(R.id.activity_hamburger_frame, this, loginFragment, null, animIn, animOut);

        eCurrentFragment = SnapsMenuManager.eHAMBUGER_FRAGMENT.LOG_IN;
    }

    private void replaceJoinFragment(int animIn, int animOut) {
        FragmentUtil.replce(R.id.activity_hamburger_frame, this, JoinFragment.newInstance(this), null, animIn, animOut);

        eCurrentFragment = SnapsMenuManager.eHAMBUGER_FRAGMENT.JOIN;
    }

    private void replaceVerifyPhoneFragment(int animIn, int animOut) {
        FragmentUtil.replce(R.id.activity_hamburger_frame, this, VerifyPhoneFragment.newInstance(this), null, animIn, animOut);

        eCurrentFragment = SnapsMenuManager.eHAMBUGER_FRAGMENT.VERIFY_PHONE;
    }
    private void replaceVerifyPhonePopupFragment(int animIn, int animOut) {
        FragmentUtil.replce(R.id.activity_hamburger_frame, this, VerifyPhonePopupFragment.newInstance(this), null, animIn, animOut);

        eCurrentFragment = SnapsMenuManager.eHAMBUGER_FRAGMENT.VERIFY_PHONE_POPUP;
    }

    private void replaceRestIdFragment(int animIn, int animOut) {
        FragmentUtil.replce(R.id.activity_hamburger_frame, this, RestIdFragment.newInstance(this), null, animIn, animOut);

        eCurrentFragment = SnapsMenuManager.eHAMBUGER_FRAGMENT.REST_ID;
    }

    private void replaceRetireFragment(int animIn, int animOut) {
        FragmentUtil.replce(R.id.activity_hamburger_frame, this, RetireFragment.newInstance(this), null, animIn, animOut);

        eCurrentFragment = SnapsMenuManager.eHAMBUGER_FRAGMENT.RETIRE;
    }

    private void replacePwdResetFragment(int animIn, int animOut) {
        FragmentUtil.replce(R.id.activity_hamburger_frame, this, PwdResetFragment.newInstance(this), null, animIn, animOut);

        eCurrentFragment = SnapsMenuManager.eHAMBUGER_FRAGMENT.PWD_RESET;
    }

    private void replacePwdFindFragment(int animIn, int animOut) {
        FragmentUtil.replce(R.id.activity_hamburger_frame, this, PwdFindFragment.newInstance(this), null, animIn, animOut);

        eCurrentFragment = SnapsMenuManager.eHAMBUGER_FRAGMENT.PWD_FIND;
    }

//    FragmentUtil.replce(R.id.frameMain, this, new LoginFragment());
//} else if (Const_VALUES.LOGIN_P_JOIN.equals(loginProcess)) {// 회원가입부터
//        FragmentUtil.replce(R.id.frameMain, this, new JoinFragment());
//        } else if (Const_VALUES.LOGIN_P_RETIRE.equals(loginProcess)) {// 회원탈퇴
//        FragmentUtil.replce(R.id.frameMain, this, new RetireFragment());
//        } else if (Const_VALUES.LOGIN_P_PWDRESET.equals(loginProcess)) {// 회원탈퇴
//        FragmentUtil.replce(R.id.frameMain, this, new PwdResetFragment());
//        } else if (Const_VALUES.LOGIN_P_PWDFIND.equals(loginProcess)) {// 비번 찾
//        FragmentUtil.replce(R.id.frameMain, this, new PwdFindFragment());


    @Override
    public int getMoveWhere() {
        return m_iMoveWhere;
    }

    private void sendPageEventTracker( int stringResId ) {
        if( SnapsTPAppManager.isThirdPartyApp(this) ) return;

//        int titleResId = -1;
//
//        if( v.equals(mLoginBtn) ) titleResId = R.string.menu_login;
//        else if( v.equals(mMenuHome) ) titleResId = R.string.menu_store;
//        else if( v.equals(mCartLayout) ) titleResId = R.string.menu_cart;
//        else if( v.equals(mDiaryLayout) ) titleResId = R.string.menu_diary;
//        else if( v.equals(mOrderLayout) ) titleResId = R.string.menu_order;
//        else if( v.equals(mCouponLayout) ) titleResId = R.string.menu_coupon;
//        else if( v.equals(mInviteLayout) ) titleResId = R.string.menu_invite;
//        else if( v.equals(mNoticeLayout) ) titleResId = R.string.menu_notice;
//        else if( v.equals(mQnaLayout) ) titleResId = R.string.menu_faq;
//        else if( v.equals(mSetttingLayout) ) titleResId = R.string.;

        if( stringResId > 0 ) {
            String trackerTitle = getString( stringResId );
            if( trackerTitle.length() > 0 ) sendPageEvent( trackerTitle );
        }
    }

    private void replaceCurrentFragment() {
        int animIn = getIntent().getIntExtra(Const_VALUES.EXTRAS_HAMBURGER_MENU_START_ANIM_IN, R.anim.anim_fade_in);
        int animOut = getIntent().getIntExtra(Const_VALUES.EXTRAS_HAMBURGER_MENU_START_ANIM_OUT, 0);

        switch (eCurrentFragment) {
            case MAIN_MENU:
                replaceMainMenuFragment(animIn, animOut);
                break;
            case LOG_IN:
                replaceLoginFragment(animIn, animOut);
                break;
            case JOIN:
                replaceJoinFragment(animIn, animOut);
                break;
            case RETIRE:
                replaceRetireFragment(animIn, animOut);
                break;
            case PWD_RESET:
                replacePwdResetFragment(animIn, animOut);
                break;
            case PWD_FIND:
                replacePwdFindFragment(animIn, animOut);
                break;
            case VERIFY_PHONE:
                replaceVerifyPhonePopupFragment(animIn,animOut);
                break;
            case VERIFY_PHONE_POPUP:
                replaceVerifyPhonePopupFragment(animIn,animOut);
                break;
            case REST_ID:
                replaceRestIdFragment(animIn,animOut);
                break;
        }
    }
}
