package com.snaps.mobile.activity.intro.fragment;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.appevents.AppEventsConstants;
import com.facebook.appevents.AppEventsLogger;
import com.snaps.common.trackers.SnapsAppsFlyer;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.constant.Const_VALUES;
import com.snaps.common.utils.constant.ISnapsConfigConstants;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.log.SnapsInterfaceLogListener;
import com.snaps.common.utils.net.http.HttpReq;
import com.snaps.common.utils.net.xml.XmlResult;
import com.snaps.common.utils.pref.Setting;
import com.snaps.common.utils.thread.ATask;
import com.snaps.common.utils.ui.ICustomDialogListener;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.common.utils.ui.UI;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.hamburger_menu.interfacies.ISnapsHamburgerMenuListener;

import errorhandle.CatchFragmentActivity;
import errorhandle.logger.SnapsInterfaceLogDefaultHandler;

public class JoinFragment extends Fragment implements View.OnFocusChangeListener {
    private static final String TAG = JoinFragment.class.getSimpleName();

    private static final String PARAMS_LANG_KR = "kr";
    private static final String PARAMS_LANG_EN = "en";
    private static final String PARAMS_LANG_JP = "jp";
    private static final String PARAMS_LANG_CH = "ch";

    // layout
    EditText editJoinName;
    EditText editJoinId;
    EditText editJoinRewriteId;
    EditText editJoinPwd;

    ImageView editJoinNameUnderLine;
    ImageView editJoinIdUnderLine;
    ImageView editJoinRewriteIdUnderLine;
    ImageView editJoinPwdUnderLine;

    TextView mLoginBtn;
    TextView mCheckBtn;

    TextView mSendTxt;
    TextView mAgreeTxt;

    boolean mNameOn = false;

    boolean mEmailOn = false;
    boolean mEmailRewriteOn = false;

    boolean mPwdOn = false;

    boolean mIsAgree = false;

    // data
    String joinResult;

    ISnapsHamburgerMenuListener menuClickListenter = null;

    public static JoinFragment newInstance(ISnapsHamburgerMenuListener listenter) {
        JoinFragment fragment = new JoinFragment();
        fragment.menuClickListenter = listenter;
        return fragment;
    }

    public JoinFragment() {
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private String getLangStrValue() {
        String chnCode = Config.getCHANNEL_CODE();
        if (chnCode == null) return PARAMS_LANG_KR;

        if (chnCode.equalsIgnoreCase(ISnapsConfigConstants.CHANNEL_SNAPS_GLOBAL_ENG))
            return PARAMS_LANG_EN;
        else if (chnCode.equalsIgnoreCase(ISnapsConfigConstants.CHANNEL_SNAPS_GLOBAL_JPN))
            return PARAMS_LANG_JP;
        else if (chnCode.equalsIgnoreCase(ISnapsConfigConstants.CHANNEL_SNAPS_GLOBAL_CHN))
            return PARAMS_LANG_CH;
        return PARAMS_LANG_KR;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_loginp_join, container, false);

        (getActivity()).setTitle(R.string.signup);

        editJoinName = (EditText) v.findViewById(R.id.editJoinName);
        editJoinId = (EditText) v.findViewById(R.id.editJoinId);
        editJoinRewriteId = (EditText) v.findViewById(R.id.editJoinRewriteId);
        editJoinPwd = (EditText) v.findViewById(R.id.editJoinPwd);
        mCheckBtn = (TextView) v.findViewById(R.id.JoinAgreementCheck);
        mLoginBtn = (TextView) v.findViewById(R.id.Join_btn);

        mSendTxt = (TextView) v.findViewById(R.id.txtSendEmailAgreement);

        editJoinNameUnderLine = (ImageView) v.findViewById(R.id.editJoinNameUnderLine);
        editJoinIdUnderLine = (ImageView) v.findViewById(R.id.editJoinIdUnderLine);
        editJoinPwdUnderLine = (ImageView) v.findViewById(R.id.editJoinPwdUnderLine);
        editJoinRewriteIdUnderLine = (ImageView) v.findViewById(R.id.editJoinRewriteIdUnderLine);

        editJoinName.setOnFocusChangeListener(this);
        editJoinId.setOnFocusChangeListener(this);
        editJoinRewriteId.setOnFocusChangeListener(this);
        editJoinPwd.setOnFocusChangeListener(this);

        v.findViewById(R.id.fragment_loginp_login_back_iv).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (menuClickListenter != null)
                    menuClickListenter.onHamburgerMenuPostMsg(ISnapsHamburgerMenuListener.MSG_MOVE_TO_BACK);
            }
        });

        TextView txtJoinAgreement = (TextView) v.findViewById(R.id.txtJoinAgreement);
        String agreementText = getString(R.string.join_agreement);
        String termsText = getString(R.string.terms);
        String privacyText = getString(R.string.privacy_statement);

        String agreementUrl = SnapsAPI.AGREEMENT_URL_MOBILE() + getLangStrValue();
        String privacyUrl = SnapsAPI.PRIVACY_URL_MOBILE() + getLangStrValue();

        if (Config.useKorean()) {
            agreementText = agreementText.replace(termsText, getAHrefText(termsText, agreementUrl));
            agreementText = agreementText.replace(privacyText, getAHrefText(privacyText, privacyUrl));
        } else {
            termsText = getAHrefText(termsText, agreementUrl);
            privacyText = getAHrefText(privacyText, privacyUrl);
            agreementText = (agreementText + " (" + termsText + ", " + privacyText + ")");
        }

        txtJoinAgreement.setMovementMethod(LinkMovementMethod.getInstance());
        txtJoinAgreement.setClickable(true);
        txtJoinAgreement.setText(Html.fromHtml(agreementText));
        txtJoinAgreement.setLinkTextColor(Color.parseColor("#999999"));

        UI.<TextView>findViewById(v, R.id.Join_btn).setOnClickListener(onClick);

        editJoinName.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.length() > 0) {
                    mNameOn = true;

                } else {
                    mNameOn = false;
                }

                allCheckItem();
            }
        });

        editJoinId.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    mEmailOn = true;
                } else {
                    mEmailOn = false;
                }

                allCheckItem();
            }
        });

        editJoinRewriteId.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.length() > 0) {
                    mEmailRewriteOn = true;

                } else {
                    mEmailRewriteOn = false;
                }

                allCheckItem();
            }
        });

        editJoinPwd.addTextChangedListener(new TextWatcher() {

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

                allCheckItem();
            }
        });

        mCheckBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                mIsAgree = !mIsAgree;

                if (mIsAgree) {
                    mCheckBtn.setBackgroundResource(R.drawable.img_push_agree_chkbox_on);
                } else {
                    mCheckBtn.setBackgroundResource(R.drawable.img_push_agree_chkbox_off);
                }

                allCheckItem();

                editJoinNameUnderLine.setBackgroundColor(getResources().getColor(R.color.color_join_edit_text_normal));
                editJoinIdUnderLine.setBackgroundColor(getResources().getColor(R.color.color_join_edit_text_normal));
                editJoinRewriteIdUnderLine.setBackgroundColor(getResources().getColor(R.color.color_join_edit_text_normal));
                editJoinPwdUnderLine.setBackgroundColor(getResources().getColor(R.color.color_join_edit_text_normal));

            }
        });

        return v;
    }

    private String getAHrefText(String text, String url) {
        StringBuilder builder = new StringBuilder();
        builder.append("<a href=").append(url).append(">").append(text).append("</a>");
        return builder.toString();
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        Activity activity = getActivity();
        if (activity != null && isAdded()) { //crash 대응
            if (hasFocus) {
                if (v.getId() == R.id.editJoinName) {
                    editJoinNameUnderLine.setBackgroundColor(Color.WHITE);
                    editJoinIdUnderLine.setBackgroundColor(getResources().getColor(R.color.color_join_edit_text_normal));
                    editJoinRewriteIdUnderLine.setBackgroundColor(getResources().getColor(R.color.color_join_edit_text_normal));
                    editJoinPwdUnderLine.setBackgroundColor(getResources().getColor(R.color.color_join_edit_text_normal));
                } else if (v.getId() == R.id.editJoinId) {
                    editJoinNameUnderLine.setBackgroundColor(getResources().getColor(R.color.color_join_edit_text_normal));
                    editJoinIdUnderLine.setBackgroundColor(Color.WHITE);
                    editJoinRewriteIdUnderLine.setBackgroundColor(getResources().getColor(R.color.color_join_edit_text_normal));
                    editJoinPwdUnderLine.setBackgroundColor(getResources().getColor(R.color.color_join_edit_text_normal));
                } else if (v.getId() == R.id.editJoinRewriteId) {
                    editJoinNameUnderLine.setBackgroundColor(getResources().getColor(R.color.color_join_edit_text_normal));
                    editJoinIdUnderLine.setBackgroundColor(getResources().getColor(R.color.color_join_edit_text_normal));
                    editJoinRewriteIdUnderLine.setBackgroundColor(Color.WHITE);
                    editJoinPwdUnderLine.setBackgroundColor(getResources().getColor(R.color.color_join_edit_text_normal));
                } else if (v.getId() == R.id.editJoinPwd) {
                    editJoinNameUnderLine.setBackgroundColor(getResources().getColor(R.color.color_join_edit_text_normal));
                    editJoinIdUnderLine.setBackgroundColor(getResources().getColor(R.color.color_join_edit_text_normal));
                    editJoinRewriteIdUnderLine.setBackgroundColor(getResources().getColor(R.color.color_join_edit_text_normal));
                    editJoinPwdUnderLine.setBackgroundColor(getResources().getColor(R.color.color_join_edit_text_pw_focus));
                }
            }
        }
    }

    public void allCheckItem() {
        if (mNameOn && mPwdOn && mEmailOn && mEmailRewriteOn && mIsAgree) {
            mLoginBtn.setBackgroundResource(R.drawable.selector_red_btn);
        } else {
            mLoginBtn.setBackgroundResource(R.drawable.selector_black_btn);
        }
    }

    OnClickListener onClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.Join_btn) {// 가입하기
                if (mNameOn && mPwdOn && mEmailOn && mEmailRewriteOn && mIsAgree) {
                    snapsJoin();
                } else {
                    if (!mNameOn) {
                        MessageUtil.alertnoTitleOneBtn(getActivity(), getString(R.string.plz_input_name), new ICustomDialogListener() {
                            @Override
                            public void onClick(byte clickedOk) {
                                editJoinName.requestFocus();
                            }
                        });
                    } else if (!mEmailOn) {
                        MessageUtil.alertnoTitleOneBtn(getActivity(), getString(R.string.plz_input_email), new ICustomDialogListener() {
                            @Override
                            public void onClick(byte clickedOk) {
                                editJoinId.requestFocus();
                            }
                        });
                    } else if (!mEmailRewriteOn) {
                        MessageUtil.alertnoTitleOneBtn(getActivity(), getString(R.string.plz_input_re_email), new ICustomDialogListener() {
                            @Override
                            public void onClick(byte clickedOk) {
                                editJoinRewriteId.requestFocus();
                            }
                        });
                    } else if (!mPwdOn) {
                        MessageUtil.alertnoTitleOneBtn(getActivity(), getString(R.string.pwdreset_fail_empty), new ICustomDialogListener() {
                            @Override
                            public void onClick(byte clickedOk) {
                                editJoinPwd.requestFocus();
                            }
                        });
                    } else if (!mIsAgree) {
                        MessageUtil.alertnoTitleOneBtn(getActivity(), getString(R.string.plz_agree_terms), new ICustomDialogListener() {
                            @Override
                            public void onClick(byte clickedOk) {
                                mCheckBtn.requestFocus();
                            }
                        });
                    }
                }
            }
        }
    };

    /**
     * 스냅스 회원가입
     */
    void snapsJoin() {
        //이름에 앞/뒤 공백은 제거하되, 중간에 스페이스 사용은 가능하다.
        final String joinName = editJoinName.getText().toString().trim();

        final String joinId = (editJoinId.getText().toString()).trim();
        final String joinRewrite = editJoinRewriteId.getText().toString().trim();

        final String joinPwd = editJoinPwd.getText().toString().trim();

        // 여길 탈 수가 없다..하지만, 만에 하나 모르니, 그냥 남겨 둠..
        if ("".equals(joinId) || "".equals(joinName) || "".equals(joinPwd) || "".equals(joinRewrite)) {// 빈칸체크
            MessageUtil.toast(getActivity(), R.string.login_validate);
            return;
        }

        //이메일에 대문자 및 공백 문자가 들어갈 수 없음.
        if (StringUtil.isContainsUppercaseOrEmptyText(joinId)) {
            MessageUtil.alertnoTitleOneBtn(getActivity(), getString(R.string.email_is_contains_invalid_char), new ICustomDialogListener() {
                @Override
                public void onClick(byte clickedOk) {
                    editJoinId.requestFocus();
                }
            });
            return;
        }

        if (!StringUtil.isValidEmail(joinId)) {// 이메일체크
            MessageUtil.alertnoTitleOneBtn(getActivity(), getString(R.string.login_validate_id), new ICustomDialogListener() {
                @Override
                public void onClick(byte clickedOk) {
                    editJoinId.requestFocus();
                }
            });
            return;
        }
        if (!joinId.equals(joinRewrite)) {// 이메일체크,이메일체크확인 일치여부
            MessageUtil.alertnoTitleOneBtn(getActivity(), getString(R.string.login_validate_email_notsame), new ICustomDialogListener() {
                @Override
                public void onClick(byte clickedOk) {
                    editJoinRewriteId.requestFocus();
                }
            });
            return;
        }

        if (!StringUtil.isValidPwd(6, 15, joinPwd)) {// 패스워드체크
            MessageUtil.alertnoTitleOneBtn(getActivity(), getString(R.string.login_validate_pwd), new ICustomDialogListener() {
                @Override
                public void onClick(byte clickedOk) {
                    editJoinPwd.requestFocus();
                }
            });
            return;
        }

        //한글 빼고 모두 허용.
        if (StringUtil.isContainLanguageChar(joinPwd)) { // 패스워드체크
            MessageUtil.alertnoTitleOneBtn(getActivity(), getString(R.string.failed_join_cause_contains_not_valid_char), new ICustomDialogListener() {
                @Override
                public void onClick(byte clickedOk) {
                    editJoinPwd.requestFocus();
                }
            });
            return;
        }

        ATask.executeVoidDefProgress(getActivity(), new ATask.OnTask() {
            @Override
            public void onPre() {
            }

            @Override
            public void onBG() {
                String snapsLoginType = Setting.getString(getActivity(), Const_VALUE.KEY_SNAPS_LOGIN_TYPE, Const_VALUES.SNAPSLOGIN_SNAPS);
                joinResult = HttpReq.snapsJoin(Const_VALUES.SNAPSJOIN_INSERT, joinId, joinName, joinPwd, snapsLoginType, SnapsInterfaceLogDefaultHandler.createDefaultHandler());
            }

            @Override
            public void onPost() {
                String result = "";
                String event = "";
                try {
                    result = new XmlResult(joinResult).get("RETURN_CODE");
                    event = new XmlResult(joinResult).get("EVENT_FLAG");
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                }
                if ("true".equals(result)) {// 가입 성공 home으로 이동
                    Setting.set(getActivity(), Const_VALUE.KEY_SNAPS_USER_NO, "");
                    Setting.set(getActivity(), Const_VALUE.KEY_SNAPS_USER_ID, joinId);
                    Setting.set(getActivity(), Const_VALUE.KEY_SNAPS_USER_PWD, joinPwd);
                    Setting.set(getActivity(), Const_VALUE.KEY_SNAPS_USER_NAME, joinName);

                    showPopup(joinId, joinName, event, SnapsInterfaceLogDefaultHandler.createDefaultHandler());

                } else if ("false".equals(result)) {// 가입 실패
                    MessageUtil.toast(getActivity(), R.string.join_fail);
                } else if ("leave".equals(result)) {// 탈퇴 아이디
                    MessageUtil.alert(getActivity(), R.string.join_retire);
                } else if ("exist".equals(result)) {// 중복 아이디
                    MessageUtil.alert(getActivity(), R.string.join_dup);
                }
            }
        });
    }

    public void showPopup(final String USER_ID, final String USER_NAME, final String event, final SnapsInterfaceLogListener interfaceLogListener) {
        try {
            ATask.executeBooleanDefProgress(getContext(), new ATask.OnTaskResult() {
                @Override
                public void onPre() {
                }

                @Override
                public boolean onBG() {
                    try {
                        SnapsAppsFlyer.setJoinComplete(JoinFragment.this.getContext().getApplicationContext());
                        AppEventsLogger logger = AppEventsLogger.newLogger(getActivity());
                        Bundle params = new Bundle();
                        params.putString(AppEventsConstants.EVENT_PARAM_REGISTRATION_METHOD, getString(R.string.join_complete));
                        logger.logEvent(AppEventsConstants.EVENT_NAME_COMPLETED_REGISTRATION, 0, params);
                    } catch (Exception e) {
                        Dlog.e(TAG, e);
                        if (interfaceLogListener != null)
                            interfaceLogListener.onSnapsInterfaceException(e);

                    }
                    return false;
                }

                @Override
                public void onPost(boolean result) {
                    // GA 회원가입 완료.
                    if (getActivity() instanceof CatchFragmentActivity)
                        ((CatchFragmentActivity) getActivity()).sendActionEvent(getString(R.string.join_complete));
//                    SnapsAdbrix.joinComplete();
                    if ("Y".equals(event)) {
                        if (menuClickListenter != null)
                            menuClickListenter.onHamburgerMenuPostMsg(ISnapsHamburgerMenuListener.MSG_COMPLATE_JOIN_EVEVT);
                    } else {
                        MessageUtil.alertnoTitleOneBtn(getActivity(), String.format(getString(R.string.join_celebration_message), USER_NAME), new ICustomDialogListener() {
                            @Override
                            public void onClick(byte clickedOk) {
                                if (menuClickListenter != null)
                                    menuClickListenter.onHamburgerMenuPostMsg(ISnapsHamburgerMenuListener.MSG_COMPLATE_JOIN);
                            }
                        });
                    }
                }
            });
        } catch (IllegalStateException e) {
            Dlog.e(TAG, e);
        }
    }
}
