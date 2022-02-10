package com.snaps.mobile.activity.intro.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.snaps.common.push.PushManager;
import com.snaps.common.structure.SnapsHandler;
import com.snaps.common.trackers.SnapsAppsFlyer;
import com.snaps.common.utils.ISnapsHandler;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_EKEY;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.constant.Const_VALUES;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.net.CNetStatus;
import com.snaps.common.utils.net.xml.GetParsedXml;
import com.snaps.common.utils.net.xml.bean.Xml_SnapsLoginInfo;
import com.snaps.common.utils.pref.Setting;
import com.snaps.common.utils.system.SystemUtil;
import com.snaps.common.utils.thread.ATask;
import com.snaps.common.utils.ui.ICustomDialogListener;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.common.utils.ui.UI;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.diary.activities.SnapsDiaryMainActivity;
import com.snaps.mobile.activity.event.LoginEventActivtiy;
import com.snaps.mobile.activity.hamburger_menu.interfacies.ISnapsHamburgerMenuListener;
import com.snaps.mobile.activity.home.RenewalHomeActivity;
import com.snaps.mobile.activity.home.utils.SnapsLoginManager;
import com.snaps.mobile.activity.intro.IAfterLoginProcess;
import com.snaps.mobile.utils.pref.PrefUtil;

import errorhandle.logger.SnapsInterfaceLogDefaultHandler;


public class LoginFragment extends Fragment implements ISnapsHandler, View.OnFocusChangeListener {
    private static final String TAG = LoginFragment.class.getSimpleName();
    // layout
    EditText editLoginId;
    EditText editLoginPwd;

    ImageView editLoginIdUnderLine;
    ImageView editLoginPwdUnderLine;

    TextView mLoginBtn;

    boolean mIdOn = false;
    boolean mPwdOn = false;

    // data
    Xml_SnapsLoginInfo xmlSnapsLoginInfo;

    // 홈으로 이동..
    boolean isMoveHome = false;

    //햄버거 메뉴에서 로그인 함.
    boolean isFromHamburgerMenu = false;

    CheckBox chkboxPushAgree = null;

    SnapsHandler mHandler = new SnapsHandler(this);

    ISnapsHamburgerMenuListener menuClickListenter = null;

    public static LoginFragment newInstance(ISnapsHamburgerMenuListener listenter) {
        LoginFragment fragment = new LoginFragment();
        fragment.menuClickListenter = listenter;
        return fragment;
    }

    public LoginFragment() {
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromInputMethod(editLoginId.getWindowToken(), 0);
            imm.hideSoftInputFromInputMethod(editLoginPwd.getWindowToken(), 0);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_loginp_login, container, false);

        String userId = Setting.getString(getActivity(), Const_VALUE.KEY_SNAPS_USER_ID);

        editLoginId = (EditText) v.findViewById(R.id.editLoginId);
        editLoginPwd = (EditText) v.findViewById(R.id.editLoginPwd);
        mLoginBtn = (TextView) v.findViewById(R.id.btnLogin);

        editLoginIdUnderLine = (ImageView) v.findViewById(R.id.editLoginIdUnderLine);
        editLoginPwdUnderLine = (ImageView) v.findViewById(R.id.editLoginPwdUnderLine);

        chkboxPushAgree = (CheckBox) v.findViewById(R.id.chkbox_push_agree);

        editLoginId.setText(userId);

        editLoginId.setOnFocusChangeListener(this);
        editLoginPwd.setOnFocusChangeListener(this);

        UI.<TextView>findViewById(v, R.id.btnLogin).setOnClickListener(onClick);
        UI.<TextView>findViewById(v, R.id.btnPwdFind).setOnClickListener(onClick);
        UI.<TextView>findViewById(v, R.id.btnJoin).setOnClickListener(onClick);

        v.findViewById(R.id.fragment_loginp_login_back_iv).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                hideAllKeyboard();

                if (menuClickListenter != null)
                    menuClickListenter.onHamburgerMenuPostMsg(ISnapsHamburgerMenuListener.MSG_MOVE_TO_BACK);
            }
        });


        editLoginId.postDelayed(new Runnable() {

            @Override
            public void run() {
                try {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(editLoginId, 0);
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                }
            }
        }, 200);

        if (editLoginId.getText().toString().equals("")) {

        } else {
            mIdOn = true;
        }

        if (editLoginPwd.getText().toString().equals("")) {

        } else {
            mPwdOn = true;
        }

        editLoginId.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.length() > 0) {
                    mIdOn = true;

                } else {
                    mIdOn = false;
                }

                if (mIdOn && mPwdOn) {
                    mLoginBtn.setBackgroundResource(R.drawable.selector_red_btn);//setBackgroundColor(Color.parseColor("#e36a63"));
                } else {
                    mLoginBtn.setBackgroundResource(R.drawable.selector_black_btn);//setBackgroundColor(Color.parseColor("#191919"));
                }
            }
        });

        editLoginPwd.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    mPwdOn = true;
                } else {
                    mPwdOn = false;
                }

                if (mIdOn && mPwdOn) {
                    mLoginBtn.setBackgroundResource(R.drawable.selector_red_btn);
                } else {
                    mLoginBtn.setBackgroundResource(R.drawable.selector_black_btn);
                }
            }
        });

        try {
            isMoveHome = getActivity().getIntent().getBooleanExtra("moveHome", false);

            if (getArguments() != null) {
                isFromHamburgerMenu = getArguments().getBoolean("isFromHamburgerMenu", false);
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        return v;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        Activity activity = getActivity();
        if (activity != null && isAdded()) { //crash 대응
            if (hasFocus) {
                if (v.getId() == R.id.editLoginId) {
                    editLoginIdUnderLine.setBackgroundColor(Color.WHITE);
                    editLoginPwdUnderLine.setBackgroundColor(getResources().getColor(R.color.color_join_edit_text_normal));
                } else if (v.getId() == R.id.editLoginPwd) {
                    editLoginIdUnderLine.setBackgroundColor(getResources().getColor(R.color.color_join_edit_text_normal));
                    editLoginPwdUnderLine.setBackgroundColor(getResources().getColor(R.color.color_join_edit_text_pw_focus));
                }
            }
        }
    }

    OnClickListener onClick = new OnClickListener() {
        @Override
        public void onClick(View v) {

            hideAllKeyboard();

            Message msg = new Message();
            msg.arg1 = v.getId();

            mHandler.sendMessageDelayed(msg, 200);
        }
    };

    public void hideAllKeyboard() {
        try {
            UIUtil.hideKeyboard(getActivity(), editLoginId);
            UIUtil.hideKeyboard(getActivity(), editLoginPwd);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private void showPushAgreePopup(final String userNo) {

        // 이전에 한번 로그인 했던 사용자인 경우 스냅스 푸쉬 알림 서비스 안내 팝업을 띄우지 않는다.
        String saveUserNo = PrefUtil.getGCMAgreeUserno(getActivity());
        boolean isAlertPassed = false;
        if (!saveUserNo.equals("") && saveUserNo.equals(userNo))
            isAlertPassed = true;

        if (!isAlertPassed && chkboxPushAgree != null && chkboxPushAgree.isChecked()) {
            MessageUtil.showPushAgreeInfo(getActivity(), true, new ICustomDialogListener() {
                @Override
                public void onClick(byte clickedOk) {
                    //
                    PrefUtil.setGCMAgreeUserno(getActivity(), userNo);
                    goHomeActivity();

                }
            });
        } else if (chkboxPushAgree != null) { // 원래 코드는 만약을 위해 살리고, checkbox선택에 관한 값이 있을땐 그걸 기준으로 처리한다.
            Setting.set(getActivity(), Const_VALUE.KEY_GCM_PUSH_RECEIVE, chkboxPushAgree.isChecked());
            goHomeActivity();

            if (!chkboxPushAgree.isChecked()) {
                Setting.set(getActivity(), Const_VALUE.KEY_SAW_PUSH_AGREE_POPUP, false);
                Setting.set(getActivity(), Config.getBlockShowCurrentUserKey(getActivity()), false);
            }
        } else {
            String status = PrefUtil.getGCMAgreeUsernoStatus(getActivity());
            //같은 사용자이면 푸쉬정보 다시설정
            if (status.equals(userNo + "true")) {
                Setting.set(getActivity(), Const_VALUE.KEY_GCM_PUSH_RECEIVE, true);
            } else if (status.equals(userNo)) {
                Setting.set(getActivity(), Const_VALUE.KEY_GCM_PUSH_RECEIVE, false);
            }

            goHomeActivity();
        }
    }

    @Override
    public void handleMessage(Message msg) {
        int id = msg.arg1;
        if (id == R.id.btnLogin) {// 로그인
            if (mIdOn && mPwdOn) {
                String inputId = editLoginId.getText().toString().trim();
                String inputPwd = editLoginPwd.getText().toString().trim();
                if (StringUtil.isContainsEmptyText(inputId)) {
                    MessageUtil.alertnoTitleOneBtn(getActivity(), getString(R.string.is_contains_empty_text), new ICustomDialogListener() {
                        @Override
                        public void onClick(byte clickedOk) {
                            if (editLoginId != null)
                                editLoginId.requestFocus();
                        }
                    });
                } else if (StringUtil.isContainsEmptyText(inputPwd)) {
                    MessageUtil.alertnoTitleOneBtn(getActivity(), getString(R.string.is_contains_empty_text), new ICustomDialogListener() {
                        @Override
                        public void onClick(byte clickedOk) {
                            if (editLoginPwd != null)
                                editLoginPwd.requestFocus();
                        }
                    });
                } else
                    snapsLogin();
            } else {
                if (!mIdOn) {
                    MessageUtil.alertnoTitleOneBtn(getActivity(), getString(R.string.plz_input_id), new ICustomDialogListener() {
                        @Override
                        public void onClick(byte clickedOk) {
                            if (editLoginId != null)
                                editLoginId.requestFocus();
                        }
                    });
                } else if (!mPwdOn) {
                    MessageUtil.alertnoTitleOneBtn(getActivity(), getString(R.string.pwdreset_fail_empty), new ICustomDialogListener() {
                        @Override
                        public void onClick(byte clickedOk) {
                            if (editLoginPwd != null)
                                editLoginPwd.requestFocus();
                        }
                    });
                }
            }
        } else if (id == R.id.btnPwdFind) {// 비밀번호 찾기
            if (menuClickListenter != null) {
                menuClickListenter.onHamburgerMenuPostMsg(ISnapsHamburgerMenuListener.MSG_MOVE_TO_PWD_FIND);
            }
        } else if (id == R.id.btnJoin) {// 회원가입
            if (menuClickListenter != null) {
                menuClickListenter.onHamburgerMenuPostMsg(ISnapsHamburgerMenuListener.MSG_MOVE_TO_JOIN);
            }
        }
    }

    /**
     * 스냅스 로그인
     */
    void snapsLogin() {

        // 빈칸은 미리 체크한다.
        final String snapsUserId = editLoginId.getText().toString();
        final String snapsUserPwd = editLoginPwd.getText().toString();
        if ("".equals(snapsUserId) || "".equals(snapsUserPwd)) {// 빈칸체크
            MessageUtil.toast(getActivity(), R.string.login_validate);
            return;
        }

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromInputMethod(editLoginId.getWindowToken(), 0);
        imm.hideSoftInputFromInputMethod(editLoginPwd.getWindowToken(), 0);

        ATask.executeVoidDefProgress(getActivity(), new ATask.OnTask() {
            boolean isNetworkErr = false;
            boolean isServerNetworkErr = false;

            @Override
            public void onPre() {
            }

            @Override
            public void onBG() {
                CNetStatus netStatus = CNetStatus.getInstance();
                if (netStatus.isAliveNetwork(getActivity())) {
                    String snapsUserName1 = Setting.getString(getActivity(), Const_VALUE.KEY_SNAPS_USER_NAME1);
                    String snapsUserName2 = Setting.getString(getActivity(), Const_VALUE.KEY_SNAPS_USER_NAME2);
                    String snapsLoginType = Setting.getString(getActivity(), Const_VALUE.KEY_SNAPS_LOGIN_TYPE, Const_VALUES.SNAPSLOGIN_SNAPS);
                    xmlSnapsLoginInfo = GetParsedXml.snapsLogin(getActivity(), snapsUserId, snapsUserPwd, snapsUserName1, snapsUserName2, snapsLoginType);
                    if (xmlSnapsLoginInfo != null && xmlSnapsLoginInfo.isServerError)
                        isServerNetworkErr = true;
                } else {
                    isNetworkErr = true;
                }
            }

            @Override
            public void onPost() {
//				if (xmlSnapsLoginInfo != null) {// 로그인 성공 home으로 이동
                if ("true".equals(xmlSnapsLoginInfo.F_RETURN_CODE)) {// 로그인 성공 home으로 이동
                    Setting.set(getActivity(), Const_VALUE.KEY_SNAPS_USER_NO, xmlSnapsLoginInfo.F_USER_NO);// userno 저장
                    Setting.set(getActivity(), Const_VALUE.KEY_SNAPS_USER_ID, snapsUserId);
                    Setting.set(getActivity(), Const_VALUE.KEY_SNAPS_USER_PWD, snapsUserPwd);
                    Setting.set(getActivity(), Const_VALUE.KEY_SNAPS_USER_NAME, xmlSnapsLoginInfo.F_USER_NAME);
                    Setting.set(getActivity(), Const_VALUE.KEY_USER_INFO_USER_NAME, xmlSnapsLoginInfo.F_USER_NAME);
                    Setting.set(getActivity(), Const_VALUE.KEY_EVENT_DEVICE, xmlSnapsLoginInfo.F_DEVICE);
                    Setting.set(getActivity(), Const_VALUE.KEY_USER_AUTH, xmlSnapsLoginInfo.F_USER_AUTH);
                    Setting.set(getActivity(), Const_VALUE.KEY_USER_PHONENUMBER, xmlSnapsLoginInfo.F_USER_PHONENUMBER);
                    Setting.set(getActivity(), Const_VALUE.KEY_USER_INFO_GRADE_CODE, xmlSnapsLoginInfo.F_USER_LVL);
                    //배지 카운터를 서버에서 조회하도록 요청한다.
                    Setting.set(getActivity(), Const_VALUE.KEY_SHOULD_REQUEST_BADGE_COUNT, true);
                    //TODO  duckwon
                    SnapsAppsFlyer.setUserEmails(snapsUserId);
                    showPushAgreePopup(xmlSnapsLoginInfo.F_USER_NO);
                } else {
                    if (isServerNetworkErr) {
                        MessageUtil.toast(getActivity(), getString(R.string.login_fail_server_err_msg));
                    } else {
                        if (isNetworkErr) {
                            MessageUtil.toast(getActivity(), R.string.login_fail_because_network);
                        } else {
                            if ("bizMember".equals(xmlSnapsLoginInfo.F_RETURN_MSG)) {
                                MessageUtil.alertnoTitleOneBtn(getActivity(), getString(R.string.login_fail_bizmember), new ICustomDialogListener() {
                                    @Override
                                    public void onClick(byte clickedOk) {
                                        if (editLoginId != null)
                                            editLoginId.requestFocus();
                                    }
                                });
                            } else {
                                MessageUtil.alertnoTitleOneBtn(getActivity(), getString(R.string.login_fail_only), new ICustomDialogListener() {
                                    @Override
                                    public void onClick(byte clickedOk) {
                                        if (editLoginId != null)
                                            editLoginId.requestFocus();
                                    }
                                });
                            }

                        }
                    }
                }
            }
        });
    }

    private void checkKakaoEvent(String userId) {
        // 자동로그인이 성공을 하면 카카오톡 친구 추가 이벤트 등록 호출...
        String sendno = Config.getKAKAO_EVENT_SENDNO();// PrefUtil.getKakaoSenderNo(getOrderActivity());
        if (!sendno.equals("")) {
            Config.setKAKAO_EVENT_RESULT2(GetParsedXml.regKakaoInvite(userId, sendno, Config.getKAKAO_EVENT_CODE(), SystemUtil.getDeviceId(getActivity()), SnapsInterfaceLogDefaultHandler.createDefaultHandler()));
//            StickyStyleWebviewActivity.isEnablerefresh = true;
        }

        try {
            FragmentActivity activity = getActivity();
            if (activity != null && activity.getIntent() != null) {
                String kakaoLogin = activity.getIntent().getStringExtra("kakaologin");
                if (kakaoLogin != null && kakaoLogin.equals("true")) {
                    String reloadUrl = getActivity().getIntent().getStringExtra("reloadUrl");
                    if (reloadUrl != null) {
                        Intent action = new Intent(Const_VALUE.RELOAD_URL);
                        action.putExtra("reloadurl", reloadUrl);
                        getActivity().sendBroadcast(action);
                    }

//                    StickyStyleWebviewActivity.isEnablerefresh = true;
                }
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private int retryCount = -1;
    private Handler h;
    private Runnable goHomeActivityRunnable = new Runnable() {
        @Override
        public void run() {
            goHomeActivity();
        }
    };

    private void goHomeActivity() {
        if (getActivity() == null) { // getActivity가 null인 로그가 있어, 1초정도까지만 retry 해보자.
            retryCount = retryCount == -1 ? 0 : retryCount + 1;
            if (retryCount > 9) // retryCount가 10 이상이면 10번 이상 시도해 보았으므로, 그냥 에러 나오게 해서 죽이자.
                retryCount = -1;
            else {
                if (h == null)
                    h = new Handler();
                h.postDelayed(goHomeActivityRunnable, 100);
                return;
            }
        } else // activity가 null이 아니면 초기화.
            retryCount = -1;

        if (getActivity() == null || getActivity().isFinishing()) return;

        //로그인 정보 갱신
        Intent intent = new Intent(Const_VALUE.LOGIN_ACTION);
        getActivity().sendBroadcast(intent);

        //알림설정 저장정보 삭제,.
        PrefUtil.clearGCMAgreeUsernoStatus(getActivity());

        // push device 등록
        ATask.executeVoidDefProgress(getActivity(), new ATask.OnTask() {
            @Override
            public void onPre() {
            }

            @Override
            public void onBG() {
                String regId = Setting.getString(getActivity(), Const_VALUE.KEY_GCM_REGID);
                if (!"".equals(regId)) {
                    String userId = xmlSnapsLoginInfo.F_USER_NO;
                    String userName = xmlSnapsLoginInfo.F_USER_NAME;
//                    String appVer = SystemUtil.getAppVersion(getActivity());
//                    String deviceId = SystemUtil.getIMEI(getActivity());
//                    HttpReq.regPushDevice(Setting.getBoolean(getActivity(), Const_VALUE.KEY_GCM_PUSH_RECEIVE) ? regId : "", userId, userName, appVer, deviceId, SnapsInterfaceLogDefaultHandler.createDefaultHandler());
                    PushManager service = new PushManager(getActivity());
                    service.requestRegistPushDevice();

                    checkKakaoEvent(userId);

                    //crashlytics 로그 추가
                    if (Config.isRealServer()) {
//                        Crashlytics.setUserIdentifier(userId);
//                        Crashlytics.setUserName(userName);
                        FirebaseCrashlytics.getInstance().setUserId(userId);
                    }
                } else {
                    checkKakaoEvent(xmlSnapsLoginInfo.F_USER_NO);
                }
            }

            @Override
            public void onPost() {
                final String id = xmlSnapsLoginInfo.F_USER_ID;

                // 휴면 알럿 대상일
                if (Setting.getBoolean(getActivity(), Const_VALUE.KEY_SNAPS_REST_ID)) {
                    SnapsLoginManager.startLogInProcess(getActivity(), Const_VALUES.LOGIN_P_REST_ID);
                    doAfterRestIdCheck(id);
                    Setting.set(getActivity(), Const_VALUE.KEY_SNAPS_REST_ID, false); // 알럿대상 취소.
                } else
                    doAfterRestIdCheck(id);
            }
        });
    }

    private void doAfterRestIdCheck(String id) {
        // 스냅스 앱이고, 이벤트 기간이고, 쿠폰대상일때.
        if (Config.getCHANNEL_CODE() != null && Config.getCHANNEL_CODE().equals(Config.CHANNEL_SNAPS_KOR) && xmlSnapsLoginInfo.F_EVENT_TERM.equals("true") && xmlSnapsLoginInfo.F_COUPON.equals("true")) {
            Setting.set(getActivity(), Const_VALUE.KEY_EVENT_FILE_PATH, xmlSnapsLoginInfo.F_FILE_PATH); // 이벤트 파일 경로를 저장해두고.
            Setting.set(getActivity(), Const_VALUE.KEY_LAST_EVENT_ALERT_ID, id); // 마지막 대상으로 본인의 아이디를 저장.
            makeEventView(); // 이벤트 팝업을 띄움.
        }
        // 대상이 아니고 마지막 팝업 아이디가 본인이 아니면.
        else if (Config.getCHANNEL_CODE() != null && Config.getCHANNEL_CODE().equals(Config.CHANNEL_SNAPS_KOR) && xmlSnapsLoginInfo.F_EVENT_TERM.equals("true")
                && !id.equals(Setting.getString(getActivity(), Const_VALUE.KEY_LAST_EVENT_ALERT_ID))) {
            MessageUtil.alert(getActivity(), R.string.event_failed_msg_already_get_coupon, new ICustomDialogListener() {
                @Override
                public void onClick(byte clickedOk) {
                    getActivity().finish();
                }
            }); // 알럿을 띄우고.
            Setting.set(getActivity(), Const_VALUE.KEY_LAST_EVENT_ALERT_ID, id); // 마지막 대상으로 본인의 아이디를 저장.
        } else if (isFromHamburgerMenu) {
            if (menuClickListenter != null) {
                menuClickListenter.onHamburgerMenuPostMsg(ISnapsHamburgerMenuListener.MSG_COMPLATE_LOG_IN);
            }
        } else if (isMoveHome) {
            Intent homeIntent = new Intent(getActivity(), RenewalHomeActivity.class);
            homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            // homeIntent.setData(getIntent().getData());
            getActivity().startActivity(homeIntent);
            getActivity().finish();

        } else {

            String loginProcess = getActivity().getIntent().getStringExtra(Const_EKEY.LOGIN_PROCESS);
            if (Const_VALUES.LOGIN_P_RESULT.equals(loginProcess)) {
                getActivity().setResult(Activity.RESULT_OK);
            }
            // 팝업을 띄울 필요가 없는경우...
            getActivity().finish();
        }

        moveToActivity();
    }

    void moveToActivity() {
        try {
            if (getActivity() != null && getActivity() instanceof IAfterLoginProcess) {
                IAfterLoginProcess process = (IAfterLoginProcess) getActivity();
                int where = process.getMoveWhere();
                if (where == IAfterLoginProcess.MOVE_TO_DIARY_MAIN) {
                    Intent diaryItt = new Intent(getActivity(), SnapsDiaryMainActivity.class);
                    startActivity(diaryItt);
                }
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    void makeEventView() {
        Intent intent = new Intent(getActivity(), LoginEventActivtiy.class);
        startActivityForResult(intent, 999);
    }
}
