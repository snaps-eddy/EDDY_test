package com.snaps.mobile.activity.setting;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;

import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.util.Pair;
import android.util.TypedValue;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.snaps.common.data.interfaces.ISnapsApplication;
import com.snaps.common.push.PushManager;
import com.snaps.common.structure.SnapsHandler;
import com.snaps.common.utils.ISnapsHandler;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.constant.Const_VALUES;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.pref.Setting;
import com.snaps.common.utils.system.SystemUtil;
import com.snaps.common.utils.ui.FontUtil;
import com.snaps.common.utils.ui.ICustomDialogListener;
import com.snaps.common.utils.ui.IFacebook;
import com.snaps.common.utils.ui.IFacebook.OnFBComplete;
import com.snaps.common.utils.ui.IKakao;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.common.utils.ui.SnsFactory;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.common.utils.ui.TitleSlideControllerWithScrollView;
import com.snaps.common.utils.ui.UI;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.facebook.utils.sns.FacebookUtil;
import com.snaps.instagram.utils.instagram.Const;
import com.snaps.instagram.utils.instagram.InstagramApp;
import com.snaps.instagram.utils.instagram.InstagramApp.OAuthAuthenticationListener;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.edit.view.custom_progress.SnapsTimerProgressView;
import com.snaps.mobile.activity.edit.view.custom_progress.SnapsTimerProgressViewFactory;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.ISnapsImageSelectConstants;
import com.snaps.mobile.activity.home.fragment.GoHomeOpserver;
import com.snaps.mobile.activity.home.utils.SnapsLoginManager;
import com.snaps.mobile.component.ObserveScrollingScrollView;
import com.snaps.mobile.component.SnapsBroadcastReceiver;
import com.snaps.mobile.component.SnapsBroadcastReceiver.ImpSnapsBroadcastReceiver;
import com.snaps.mobile.service.SnapsUploadState;
import com.snaps.mobile.service.SnapsUploadState.UploadState;
import com.snaps.mobile.utils.pref.PrefUtil;
import com.snaps.mobile.utils.select_product_junction.junctions.SnapsSelectProductJunctionForSimplePhotoBook;
import com.snaps.mobile.utils.sns.GoogleSignInActivity;
import com.snaps.mobile.utils.sns.googlephoto.GoogleAPITokenInfo;
import com.snaps.mobile.utils.sns.googlephoto.GooglePhotoUtil;
import com.snaps.mobile.utils.sns.googlephoto.interfacies.GooglePhotoAPIListener;
import com.snaps.mobile.utils.sns.googlephoto.interfacies.GooglePhotoAPIResult;
import com.snaps.mobile.utils.thirdparty.SnapsTPAppManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import errorhandle.SnapsAssert;
import errorhandle.logger.SnapsLogger;
import errorhandle.logger.model.SnapsLoggerClass;

public class SnapsSettingActivity extends AppCompatActivity implements ImpSnapsBroadcastReceiver, ISnapsHandler, GoHomeOpserver.OnGoHomeOpserver {
    private static final String TAG = SnapsSettingActivity.class.getSimpleName();

    LinearLayout lyKakao;
    LinearLayout lyFacebook;
    LinearLayout lyInstagram;

    TextView txtUserId;
    TextView btnSettingLogout;
    TextView btnSettingPwdReset;
    TextView btnSettingRetire;
    TextView btnFacebookConnect;
    TextView btnKakaoConnect;
    ImageButton btnPushReceive;
    //LinearLayout layoutSnapsAISetting;
    //ImageButton btnSnapsAI;
    TextView txtFBName;
    TextView txtKakaoName;
    ImageView iconInstagram;
    TextView textInstagramName;
    TextView btnInstagramConnect;

    //ImageButton btn_snaps_ai_with_lte;
    //LinearLayout snapsai_use_lte_setting_whole_layout;

    String userNo;

    boolean pushReceive = true;
    //boolean snapsAI = false;
    //boolean snapsAIWithLTE = false;

    private LanguageStringSet[] languageStringSets;

    IFacebook facebook;
    IKakao kakao;
    InstagramApp insta;

    protected SnapsBroadcastReceiver receiver = null;

    SnapsHandler mHandler = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        UIUtil.applyLanguage(this);
        super.onCreate(savedInstanceState);

        SnapsLogger.appendClassTrackingLog(new SnapsLoggerClass<>(this));

        if (!SnapsTPAppManager.isThirdPartyApp(this)) {
            setContentView(R.layout.activity_snapssetting);

            if (Config.isFacebookService()) {
                facebook = SnsFactory.getInstance().queryInteface();
                facebook.init(this);
            }

            kakao = SnsFactory.getInstance().queryIntefaceKakao();

            mHandler = new SnapsHandler(this);

            insta = new InstagramApp(this, Const.CLIENT_ID, Const.CLIENT_SECRET, Const.REDIRECT_URI);
            insta.setListener(new OAuthAuthenticationListener() {
                @Override
                public void onSuccess() {
                    mHandler.sendEmptyMessageDelayed(REFRESH_INSTAGRAM, 1000);
                }

                @Override
                public void onFail(String error) {
                    MessageUtil.toast(SnapsSettingActivity.this, getString(R.string.instagram_login_fail_msg));
                }
            });

            // title
            TextView tvTitleText = UI.<TextView>findViewById(this, R.id.txtTitleText);
            FontUtil.applyTextViewTypeface(tvTitleText, FontUtil.eSnapsFonts.YOON_GOTHIC_760);
            tvTitleText.setText(R.string.setting);

            txtUserId = findViewById(R.id.txtUserId);
            btnSettingLogout = findViewById(R.id.btnSettingLogout);
            btnSettingPwdReset = findViewById(R.id.btnSettingPwdReset);
            btnSettingRetire = findViewById(R.id.btnSettingRetire);
            txtFBName = findViewById(R.id.txtFBName);
            txtKakaoName = findViewById(R.id.txtkakaoName);
            textInstagramName = findViewById(R.id.txt_instagram_name);

            btnFacebookConnect = findViewById(R.id.btnFacebookConnect);
            btnKakaoConnect = findViewById(R.id.btnkakaoConnect);
            btnInstagramConnect = findViewById(R.id.btn_instagram_connect);

            lyFacebook = UI.findViewById(this, R.id.activity_snapssetting_facebook_ly);
            lyKakao = UI.findViewById(this, R.id.activity_snapssetting_kakao_story_ly);
            lyInstagram = UI.findViewById(this, R.id.activity_snapssetting_instagram_ly);

            btnPushReceive = UI.findViewById(this, R.id.btnPushReceive);
//			btnPushReceiveSub = UI.<ImageView> findViewById(this, R.id.btnPushReceive_sub);

            //btnSnapsAI = UI.findViewById(this, R.id.btnSnapsAI);
            //btn_snaps_ai_with_lte = UI.findViewById(this, R.id.btn_snaps_ai_with_lte);

            iconInstagram = UI.findViewById(this, R.id.icon_instagram);
            //iconInstagram.setImageResource(R.drawable.icon_insta);

            String currentLang = Setting.getString(this, Const_VALUE.KEY_APPLIED_LANGUAGE);
            if (StringUtil.isEmpty(currentLang)) {
                currentLang = Locale.getDefault().getLanguage();
            }

            LanguageStringSet languageStringSet = new LanguageStringSet(this, currentLang);
            ((TextView) findViewById(R.id.current_lang_text)).setText(languageStringSet.getLanguageString());
            (findViewById(R.id.modify_lang_button)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showLanguageChangeDialog();
                }
            });

            //layoutSnapsAISetting = UI.findViewById(this, R.id.snapsai_setting_whole_layout);
            //snapsai_use_lte_setting_whole_layout = UI.findViewById(this, R.id.snapsai_use_lte_setting_whole_layout);

            //boolean isAgreeAITerms = Setting.getBoolean(this, Const_VALUE.KEY_SNAPS_AI_TOS_AGREEMENT, false);
            //layoutSnapsAISetting.setVisibility(Config.useKorean() && isAgreeAITerms ? View.VISIBLE : View.GONE);
            //snapsai_use_lte_setting_whole_layout.setVisibility(Config.useKorean() && isAgreeAITerms && snapsAI ? View.VISIBLE : View.GONE);

            if (!Config.useKorean()) {
                lyKakao.setVisibility(View.GONE);
                (findViewById(R.id.activity_snapssetting_sns_divide_line)).setVisibility(View.GONE);
            }

            pushReceive = Setting.getBoolean(this, Const_VALUE.KEY_GCM_PUSH_RECEIVE, false);

            //snapsAI = Setting.getBoolean(this, Const_VALUE.KEY_SNAPS_AI, false);
            //snapsAIWithLTE = Setting.getBoolean(this, Const_VALUE.KEY_SNAPS_AI_ALLOW_UPLOAD_MOBILE_NET, false);

            if (PrefUtil.getGooglePhotoEnable(getApplicationContext())) {
                LinearLayout lyGooglePhoto = UI.<LinearLayout>findViewById(this, R.id.activity_snapssetting_googlephoto_ly);
                lyGooglePhoto.setVisibility(View.VISIBLE);

                View underline = UI.<LinearLayout>findViewById(this, R.id.activity_snapssetting_sns_divide_line4);
                if (underline != null) {
                    underline.setVisibility(View.VISIBLE);
                }

                if (GoogleAPITokenInfo.isValidAccessToken()) {
                    String userName = PrefUtil.getGooglePhotoName(this);
                    ((TextView) findViewById(R.id.txt_google_photo_name)).setText(userName);
                    ((TextView) findViewById(R.id.btn_google_photo_connect)).setText(R.string.disconnect);
                } else {
                    ((TextView) findViewById(R.id.btn_google_photo_connect)).setText("");
                }
            }

            setPushImg();
            //setSnapsAIImg();
            //setSnapsAIWithLTEImg();

            setUserCertificationUIState();

        } else {
            setContentView(R.layout.activity_between_setting);
            UI.<TextView>findViewById(this, R.id.txtTitleText).setText(R.string.setting);// title

            txtUserId = findViewById(R.id.txtUserId);

            btnSettingLogout = findViewById(R.id.btnSettingLogout);

            btnPushReceive = UI.findViewById(this, R.id.btnPushReceive);
            //btnSnapsAI = UI.findViewById(this, R.id.btnSnapsAI);
            //btn_snaps_ai_with_lte = UI.findViewById(this, R.id.btn_snaps_ai_with_lte);

            pushReceive = Setting.getBoolean(this, Const_VALUE.KEY_GCM_PUSH_RECEIVE, false);
            //snapsAI = Setting.getBoolean(this, Const_VALUE.KEY_SNAPS_AI, false);
            //snapsAIWithLTE = Setting.getBoolean(this, Const_VALUE.KEY_SNAPS_AI_ALLOW_UPLOAD_MOBILE_NET, false);
            if (Config.isSnapsBitween(this)) {
                setPushImg(); // 비트윈도 스냅스와 같은 방식으로 변경됨.
                //setSnapsAIImg();
                //setSnapsAIWithLTEImg();
            } else {
                btnPushReceive.setImageResource(pushReceive ? R.drawable.icon_between_menu_ck : R.drawable.icon_between_menu_ck_off); // SDK에는 없는거 같은데 없으면 필요없는 라인.
                //btnSnapsAI.setImageResource(snapsAI ? R.drawable.icon_between_menu_ck : R.drawable.icon_between_menu_ck_off);
                //btn_snaps_ai_with_lte.setImageResource(snapsAIWithLTE ? R.drawable.icon_between_menu_ck : R.drawable.icon_between_menu_ck_off);
            }
        }

        if (Config.isRealServer()) {
            UI.<TextView>findViewById(this, R.id.txtSettingVersion).setText("ver " + SystemUtil.getAppVersion(this));
        } else {
            UI.<TextView>findViewById(this, R.id.txtSettingVersion).setText("테스트버젼 " + SystemUtil.getAppVersion(this));
        }

        TitleSlideControllerWithScrollView titleSlideControllerWithScrollView = new TitleSlideControllerWithScrollView();
        titleSlideControllerWithScrollView.setIsTitleSlideEnable(false);
        titleSlideControllerWithScrollView.setViews((RelativeLayout) findViewById(R.id.title_layout), (ObserveScrollingScrollView) findViewById(R.id.scroll_view));

        // 리시버 등록....
        IntentFilter filter = new IntentFilter(Const_VALUE.FACEBOOK_CHANGE_NAME_ACTION);
        receiver = new SnapsBroadcastReceiver();
        receiver.setImpRecevice(this);
        registerReceiver(receiver, filter);

        initGooglePhotoUtil();

        GoHomeOpserver.addGoHomeListener(this);

        setActiveAiPhotoBookDebugUI();
    }

    //개발용
    private void setActiveAiPhotoBookDebugUI() {
        if (Config.isDevelopVersion()) {
            findViewById(R.id.layout_ai_photobook_debug).setVisibility(View.VISIBLE);
            processSelectAiPhotoBookProduct();
            processNetErrLogButton();
        }
    }

    private void processSelectAiPhotoBookProduct() {
        final List<Pair<String, String>> productList = new ArrayList();
        productList.add(Pair.create("선택 안함", ""));
        productList.add(Pair.create("6X6_HARD", "00800600130031"));
        productList.add(Pair.create("6X6_SOFT", "00800600130032"));
        productList.add(Pair.create("8X8_HARD", "00800600130001"));
        productList.add(Pair.create("8X8_SOFT", "00800600130003"));
        productList.add(Pair.create("10X10_HARD", "00800600130002"));
        productList.add(Pair.create("5X7_HARD", "00800600130019"));
        productList.add(Pair.create("5X7_SOFT", "00800600130022"));
        productList.add(Pair.create("8X10_HARD", "00800600130017"));
        productList.add(Pair.create("A4_HARD", "00800600130007"));
        productList.add(Pair.create("A4_SOFT", "00800600130008"));

        String[] productNames = new String[productList.size()];
        Map<String, Integer> productCodeToIndexMap = new HashMap<>();
        Map<String, String> productCodeToNameMap = new HashMap<>();
        for (int i = 0; i < productList.size(); i++) {
            Pair<String, String> info = productList.get(i);
            productNames[i] = info.first;
            productCodeToIndexMap.put(info.second, i);
            productCodeToNameMap.put(info.second, info.first);
        }

        final SharedPreferences sp = getSharedPreferences(
                SnapsSelectProductJunctionForSimplePhotoBook.AI_PHOTO_BOOK_PREFERENCES_NAME_FOR_DEBUG, Context.MODE_PRIVATE);
        final String keyProductCode = SnapsSelectProductJunctionForSimplePhotoBook.AI_PHOTO_BOOK_KEY_PRODUCT_CODE_FOR_DEBUG;

        Button btn = findViewById(R.id.btn_show_select_ai_photo_book_product);
        btn.setText(productCodeToNameMap.get(sp.getString(keyProductCode, "")));
        btn.setOnClickListener(v -> {
            int preSelectedIndex = productCodeToIndexMap.get(sp.getString(keyProductCode, ""));
            AlertDialog.Builder dialog = new AlertDialog.Builder(SnapsSettingActivity.this);
            dialog.setTitle("골라봐");
            dialog.setSingleChoiceItems(productNames, preSelectedIndex, (dialog1, which) -> {
                String selectProduceCode = productList.get(which).second;
                sp.edit().putString(keyProductCode, selectProduceCode).apply();
                btn.setText(productList.get(which).first);
                dialog1.dismiss();
            });
            dialog.create().show();
        });
    }


    //개발용
    private void processNetErrLogButton() {
        Button btn = findViewById(R.id.btn_show_net_err_log);
        btn.setOnClickListener(v -> {
            //NetworkErrorLog.kt 파일 참고 할 것
            SharedPreferences sp = getApplicationContext()
                    .getSharedPreferences("network_error_log", Context.MODE_PRIVATE);  //network_error_log <- 하드 코딩
            final String logText = sp.getString("log", "").replaceAll("\n", "\n\n"); //log <- 하드 코딩

            final String userNo = Setting.getString(getApplicationContext(), Const_VALUE.KEY_SNAPS_USER_NO);
            Dlog.e(TAG, "userNo:" + userNo);
            Dlog.e(TAG, " \n" + logText);

            AlertDialog.Builder alert = new AlertDialog.Builder(SnapsSettingActivity.this);
            alert.setTitle("userNo:" + userNo);

            EditText editText = new EditText(SnapsSettingActivity.this);
            editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            editText.setText(logText);
            editText.setFocusable(false);

            ScrollView scroll = new ScrollView(SnapsSettingActivity.this);
            scroll.setScrollbarFadingEnabled(false);
            scroll.setLayoutParams(
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT
                    )
            );
            scroll.addView(editText);

            alert.setView(scroll);
            alert.setPositiveButton("닫기", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });

            alert.setNegativeButton("전체 복사", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    String text = "userNo:" + userNo + "\n\n" + logText;
                    ClipboardManager clipboardManager = (ClipboardManager) getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clipData = ClipData.newPlainText("snaps_network_error_log", text);
                    clipboardManager.setPrimaryClip(clipData);
                    Toast.makeText(getApplicationContext(), "클립 보드에 복사 성공", Toast.LENGTH_SHORT).show();
                }
            });

            alert.show();
        });
    }

    private void setUserCertificationUIState() {
        String currentLang = Setting.getString(this, Const_VALUE.KEY_APPLIED_LANGUAGE);
        userNo = SnapsLoginManager.getUUserNo(this);
        if ("ko".equals(currentLang) || "".equals(currentLang)) {
            View certificationLayout = findViewById(R.id.activity_snapssetting_certification_layout);
            if (TextUtils.isEmpty(userNo)) {
                if (certificationLayout != null) {
                    certificationLayout.setVisibility(View.GONE);
                }
            } else {
                if (certificationLayout != null) {
                    certificationLayout.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void initGooglePhotoUtil() {
        try {
            GooglePhotoUtil.initGoogleSign(this, connectionResult -> Dlog.d("initGooglePhotoUtil() onConnection failed to google sign in"));
        } catch (Exception e) {
            Dlog.e(TAG, e);
            SnapsAssert.assertException(this, e);
        }
    }

    private void initLanguageStringSets() {
        languageStringSets = new LanguageStringSet[3];
//        languageStringSets = new LanguageStringSet[2];
        languageStringSets[0] = new LanguageStringSet(this, LanguageStringSet.LANG_KOREAN);
        languageStringSets[1] = new LanguageStringSet(this, LanguageStringSet.LANG_JAPANESE);
        languageStringSets[2] = new LanguageStringSet(this, LanguageStringSet.LANG_ENGLISH);
    }

    private void selectLanguageItem(int selected) {
        String currentLang = Setting.getString(this, Const_VALUE.KEY_APPLIED_LANGUAGE);
        if (StringUtil.isEmpty(currentLang)) {
            currentLang = Locale.getDefault().getLanguage();
        }

        final String tarLang = languageStringSets[selected].getLanguageKey();
        if (tarLang.equalsIgnoreCase(currentLang)) {
            return;
        }

        MessageUtil.alertTwoButton(this, getString(R.string.language_change_title), getString(R.string.language_change_detail), true, new ICustomDialogListener() {
            @Override
            public void onClick(byte clickedOk) {
                if (clickedOk == ICustomDialogListener.OK) {
                    Setting.set(SnapsSettingActivity.this, Const_VALUE.KEY_SELECTED_LANGUAGE, tarLang);

                    Intent intent = new Intent();
                    String mPackageName = getPackageName();
                    String mClass = ((ISnapsApplication) getApplication()).getLauncherActivityName();
                    intent.setComponent(new ComponentName(mPackageName, mClass));
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else if (clickedOk == ICustomDialogListener.CANCEL) {
                    ;
                }
            }
        });
    }

    private void showLanguageChangeDialog() {
        if (languageStringSets == null) {
            initLanguageStringSets();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String[] itemStrs = new String[languageStringSets.length];
        String currentLang = Setting.getString(this, Const_VALUE.KEY_APPLIED_LANGUAGE);
        if (StringUtil.isEmpty(currentLang)) {
            currentLang = Locale.getDefault().getLanguage();
        }
        int selectedIndex = 0;
        for (int i = 0; i < languageStringSets.length; ++i) {
            itemStrs[i] = languageStringSets[i].getLanguageListString();
            if (languageStringSets[i].getLanguageKey().equalsIgnoreCase(currentLang)) {
                selectedIndex = i;
            }
        }

        builder.setSingleChoiceItems(itemStrs, selectedIndex, (dialog, which) -> {
            dialog.dismiss();
            selectLanguageItem(which);
        });
        builder.setCancelable(true);

        final AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                try {
                    ListView listView = alertDialog.getListView();
                    int count = listView.getCount();  //getChildCount()가 아니고
                    for (int i = 0; i < count; i++) {
                        View view = listView.getChildAt(i);
                        if (view instanceof TextView) {
                            FontUtil.applyTextViewTypeface((TextView) view, FontUtil.eSnapsFonts.YOON_GOTHIC_740);
                        }
                    }
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                }
            }
        });

        alertDialog.show();
    }

    private void setPushImg() {
        if (btnPushReceive == null) {
            return;
        }

        if (pushReceive) {
            btnPushReceive.setImageResource(R.drawable.img_switch_on);
        } else {
            btnPushReceive.setImageResource(R.drawable.img_switch_off);
        }
    }

    /*
    private void setSnapsAIImg() {
        if (btnSnapsAI == null) {
            return;
        }

        if (snapsAI) {
            btnSnapsAI.setImageResource(R.drawable.img_switch_on);
            snapsai_use_lte_setting_whole_layout.setVisibility(View.VISIBLE);
        } else {
            btnSnapsAI.setImageResource(R.drawable.img_switch_off);
            snapsai_use_lte_setting_whole_layout.setVisibility(View.GONE);
        }
    }
    */

    /*
    private void setSnapsAIWithLTEImg() {
        if (btn_snaps_ai_with_lte == null) {
            return;
        }

        if (snapsAIWithLTE) {
            btn_snaps_ai_with_lte.setImageResource(R.drawable.img_switch_on);
        } else {
            btn_snaps_ai_with_lte.setImageResource(R.drawable.img_switch_off);
        }
    }
    */

    @Override
    public void onStart() {
        super.onStart();
        if (facebook != null) {
            facebook.addCallback();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (facebook != null) {
            facebook.removeCallback();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ISnapsImageSelectConstants.REQCODE_GOOGLE_SIGN_IN:
                if (resultCode == RESULT_OK) {
                    ((TextView) findViewById(R.id.txt_google_photo_name)).setText(PrefUtil.getGooglePhotoName(this));
                    ((TextView) findViewById(R.id.btn_google_photo_connect)).setText(R.string.disconnect);
                } else {
                    MessageUtil.toast(this, R.string.failed_google_sign_in);
                }
                break;
            default:
                if (facebook != null) {
                    facebook.onActivityResult(this, requestCode, resultCode, data);
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (facebook != null) {
            setFaceBookSetting();
        }

        mHandler.sendEmptyMessageDelayed(REFRESH_KAKAO, 1000);

        mHandler.sendEmptyMessageDelayed(REFRESH_INSTAGRAM, 1000);

        pushReceive = Setting.getBoolean(this, Const_VALUE.KEY_GCM_PUSH_RECEIVE, false);
        setPushImg();

        //snapsAI = Setting.getBoolean(this, Const_VALUE.KEY_SNAPS_AI, false);
        //setSnapsAIImg();

        //snapsAIWithLTE = Setting.getBoolean(this, Const_VALUE.KEY_SNAPS_AI_ALLOW_UPLOAD_MOBILE_NET, false);
        //setSnapsAIWithLTEImg();

        userNo = SnapsLoginManager.getUUserNo(this);

        loginLogout();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    void setFaceBookSetting() {
        mHandler.sendEmptyMessageDelayed(REFRESH_FACEBOOK, 1000);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    // 로그인, 로그아웃 UI 처리
    void loginLogout() {
        if ("".equals(userNo)) {// 로그아웃 상태
            if (SnapsTPAppManager.isThirdPartyApp(this)) {
                btnSettingLogout.setText(R.string.login);
                txtUserId.setVisibility(View.GONE);// userid 비우기
                btnSettingLogout.setVisibility(View.VISIBLE);
            } else {
                btnSettingLogout.setVisibility(View.GONE);
                txtUserId.setText(R.string.login);
                txtUserId.setVisibility(View.VISIBLE);
            }

            if (btnSettingPwdReset != null) {
                btnSettingPwdReset.setText(R.string.signup);// 회원가입
            }
            if (btnSettingRetire != null) {
                btnSettingRetire.setText(R.string.find_password);
            }

        } else {// 로그인 상태
            btnSettingLogout.setText(R.string.logout);
            btnSettingLogout.setVisibility(View.VISIBLE);
            txtUserId.setVisibility(View.VISIBLE);
            if (!SnapsTPAppManager.isThirdPartyApp(this)) {
                txtUserId.setText(Setting.getString(this, Const_VALUE.KEY_SNAPS_USER_ID));// userid
                // txtUserId.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
                if (btnSettingPwdReset != null) {
                    btnSettingPwdReset.setText(R.string.change_password);// 비번재설정
                }
                if (btnSettingRetire != null) {
                    btnSettingRetire.setText(R.string.delete_account);
                }

            } else {
                txtUserId.setText(SnapsLoginManager.getUUserNo(this));// userid
            }
        }

        /*
        if (layoutSnapsAISetting != null) {
            boolean isAgreeAITerms = Setting.getBoolean(this, Const_VALUE.KEY_SNAPS_AI_TOS_AGREEMENT, false);
            layoutSnapsAISetting.setVisibility(Config.useKorean() && isAgreeAITerms ? View.VISIBLE : View.GONE);
            snapsai_use_lte_setting_whole_layout.setVisibility(Config.useKorean() && isAgreeAITerms && snapsAI ? View.VISIBLE : View.GONE);
        }
        */

//        if (Config.isCSVersion()) {
//            layoutSnapsAISetting.setVisibility(View.GONE);
//            snapsai_use_lte_setting_whole_layout.setVisibility(View.GONE);
//        }
//
//        if (Config.isSnapsBitween(this)) {
//            TextView tvDesc = (TextView) findViewById(R.id.activity_between_setting_login_out_desc_tv);
//            if (tvDesc != null) {
//                tvDesc.setText(userNo != null && userNo.length() > 0 ? R.string.setting_service_logout_desc : R.string.setting_service_login_desc);
//            }
//
//            if (userNo == null || userNo.isEmpty()) {
//                finish();
//            }
//        }
        setUserCertificationUIState();
    }

    void logoutStep() {

        // 로그아웃시 푸쉬상태저장.
        String userId = Setting.getString(this, Const_VALUE.KEY_SNAPS_USER_NO);
        if (!userId.equals("")) {
            String status = userId + (pushReceive ? "true" : "");
            PrefUtil.setGCMAgreeUsernoStatus(this, status);
        }

        PrefUtil.clearUserInfo(this, true);// 모든정보 초기화
        userNo = "";
        // 설치이벤트 초기화.
        Setting.set(this, Const_VALUE.KEY_EVENT_COUPON, false);
        Setting.set(this, Const_VALUE.KEY_EVENT_TERM, false);
        CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(this);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
        loginLogout();
    }

    public void onClick(View v) {
        if (v.getId() == R.id.btnTitleLeft || v.getId() == R.id.btnTitleLeftLy) {
            finish();
        } else if (v.getId() == R.id.laylogin) {// 로그아웃, 로그인

            if ("".equals(userNo)) {// 로그아웃 상태면 로그인 화면으로
                SnapsLoginManager.startLogInProcess(this, Const_VALUES.LOGIN_P_LOGIN);
            } else {
                // 사진 업로드 중이면 로그아웃을 하지 않는다.
                if (SnapsUploadState.getInstance().getmState() != UploadState.UPLOADING) {
                    LogOutDialog wdia = new LogOutDialog(SnapsSettingActivity.this, SnapsSettingActivity.this, String.format(getString(R.string.logout_check)));
                    wdia.setCancelable(false);
                    wdia.show();

                } else {
                    MessageUtil.alert(this, R.string.photoprint_notice_logout);
                }
            }
        } else if (v.getId() == R.id.laypassword) {// 회원가입 or 비번재설정
            if ("".equals(userNo)) {// 회원가입
                SnapsLoginManager.startLogInProcess(this, Const_VALUES.LOGIN_P_JOIN);
            } else {// 비번재설정
                SnapsLoginManager.startLogInProcess(this, Const_VALUES.LOGIN_P_PWDRESET);
            }
        } else if (v.getId() == R.id.layretire) {// 회원탈퇴

            if ("".equals(userNo)) {
                SnapsLoginManager.startLogInProcess(this, Const_VALUES.LOGIN_P_PWDFIND);
            } else {
                SnapsLoginManager.startLogInProcess(this, Const_VALUES.LOGIN_P_RETIRE);
            }
        } else if (v.getId() == R.id.btnPushReceive) { // 알림설정

            pushReceive = !pushReceive;
            setPushImg();

            Setting.set(this, Const_VALUE.KEY_GCM_PUSH_RECEIVE, pushReceive);
            final String regId = Setting.getString(SnapsSettingActivity.this, Const_VALUE.KEY_GCM_REGID);
            final String deviceID = SystemUtil.getDeviceId(this);
//            if (!"".equals(regId)) {
//                AsyncTask.execute(() -> HttpReq.regPushDevice(
//                        pushReceive ? regId : "",
//                        Setting.getString(SnapsSettingActivity.this, Const_VALUE.KEY_SNAPS_USER_NO),
//                        Setting.getString(SnapsSettingActivity.this, Const_VALUE.KEY_SNAPS_USER_NAME),
//                        SystemUtil.getAppVersion(SnapsSettingActivity.this), deviceID, SnapsInterfaceLogDefaultHandler.createDefaultHandler()));
//            }
            PushManager service = new PushManager(this);
            service.requestRegistPushDevice();

            MessageUtil.showPushAgreeInfo(this, pushReceive, null);

            /*
        } else if (v.getId() == R.id.btnSnapsAI) { // 스냅스AI

            snapsAI = !snapsAI;
            setSnapsAIImg();

            if (SnapsLoginManager.isLogOn(this) == false) {
                return;
            }

            Setting.set(this, Const_VALUE.KEY_SNAPS_AI, snapsAI);

            AsyncTask.execute(() -> {
                String result = HttpReq.requestPutAIUse(Setting.getString(SnapsSettingActivity.this, Const_VALUE.KEY_SNAPS_USER_NO), snapsAI, SnapsInterfaceLogDefaultHandler.createDefaultHandler());
                Dlog.d("requestPutAIUse result:" + result);
            });


        } else if (v.getId() == R.id.btn_snaps_ai_with_lte) {

            snapsAIWithLTE = !snapsAIWithLTE;
            setSnapsAIWithLTEImg();

            if (SnapsLoginManager.isLogOn(this) == false) {
                return;
            }

            Setting.set(this, Const_VALUE.KEY_SNAPS_AI_ALLOW_UPLOAD_MOBILE_NET, snapsAIWithLTE);
            AsyncTask.execute(() -> {
                String result = HttpReq.requestPutAISyncWithLTE(Setting.getString(SnapsSettingActivity.this, Const_VALUE.KEY_SNAPS_USER_NO), snapsAIWithLTE, SnapsInterfaceLogDefaultHandler.createDefaultHandler());
                Dlog.d("requestPutAISyncWithLTE result:" + result);
            });


            */
        } else if (v.getId() == R.id.btnFacebookConnect || v.getId() == R.id.activity_snapssetting_facebook_ly) {
            if (facebook != null) {
                // if (FacebookUtil.isLogin()) {// 연결해제
                if (facebook.isFacebookLogin()) {// 연결해제
                    // FacebookUtil.logout();
                    facebook.facebookLogout();
                    // btnFacebookConnect.setText(R.string.setting_connect);
                    btnFacebookConnect.setText("");
                    MessageUtil.toast(getApplicationContext(), R.string.setting_disconnected);
                    if (txtFBName != null) {
                        txtFBName.setText(getString(R.string.facebook_login));
                    }
                } else {// 연결하기
                    if (!Config.IS_SUPPORT_FACEBOOK) {
                        MessageUtil.toast(this, R.string.facebook_not_support_msg);
                        return;
                    }

                    WebView webview = new WebView(this);
                    webview.resumeTimers();

                    // FacebookUtil.loginChk(this, new OnFBComplete()
                    facebook.facebookLoginChk(this, new OnFBComplete() {
                        @Override
                        public void onFBComplete(String result) {
                            Dlog.d("onClick() onFBComplete() result:" + result);

                            FacebookUtil.getProfileData(SnapsSettingActivity.this);
                            //
                            // String fbName = Setting.getString(SnapsSettingActivity.this, Const_VALUE.KEY_FACEBOOK_NAME);
                            // if (fbName != null && fbName.length() > 0) {
                            // txtFBName.setText(fbName);
                            // }
                        }
                    });
                }
            }
        } else if (v.getId() == R.id.btnkakaoConnect || v.getId() == R.id.activity_snapssetting_kakao_story_ly) {
            if (kakao != null) {
                // if (KaKaoUtil.isLogin())
                if (kakao.isKakaoLogin()) {// 연결해제
                    // KaKaoUtil.onClickLogout();
                    kakao.onKakaoClickLogout();
                    // btnKakaoConnect.setText(R.string.setting_connect);
                    btnKakaoConnect.setText("");
                    MessageUtil.toast(getApplicationContext(), getString(R.string.kakaostory_disconnected));
                    txtKakaoName.setText(getString(R.string.kakaostory_login));
                } else {// 연결하기
                    /*
                     * Intent intent = new Intent(SnapsSettingActivity.this, KakaoLoginActivity.class); startActivity(intent);
                     */
                    kakao.startKakaoLoginActivity(SnapsSettingActivity.this);
                }
            }
        } else if (v.getId() == R.id.btn_instagram_connect || v.getId() == R.id.activity_snapssetting_instagram_ly) {
            if (insta != null) {
                if (insta.getId() != null && insta.getId().length() > 0) { // 로그인 되어 있으면 연결해제.
                    insta.resetAccessToken();
                    btnInstagramConnect.setText("");
                    MessageUtil.toast(getApplicationContext(), getString(R.string.instagram_disconnected));
                    textInstagramName.setText(getString(R.string.instagram_login));
                } else {
                    if (!Config.IS_SUPPORT_INSTAGRAM) {
                        MessageUtil.toast(this, R.string.instagram_not_support_msg);
                        return;
                    }

                    insta.authorize();
                }
            }
        } else if (v.getId() == R.id.activity_snapssetting_googlephoto_ly) {
            // 로그인이 되어 있는 경우
            if (GoogleAPITokenInfo.isValidAccessToken()) {
                try {
                    GooglePhotoUtil.signOut(new GooglePhotoAPIListener() {
                        @Override
                        public void onPrepare() {
                            SnapsTimerProgressView.showProgress(SnapsSettingActivity.this, SnapsTimerProgressViewFactory.eTimerProgressType.PROGRESS_TYPE_LOADING);
                        }

                        @Override
                        public void onGooglePhotoAPIResult(boolean isSuccess, GooglePhotoAPIResult resultObj) {
                            SnapsTimerProgressView.destroyProgressView();
                        }
                    });
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                    SnapsTimerProgressView.destroyProgressView();
                }

                GoogleAPITokenInfo.deleteGoogleAllAuthInfo(SnapsSettingActivity.this);
                GoogleAPITokenInfo.deleteGoogleAllAuthInfo(SnapsSettingActivity.this);
                ((TextView) findViewById(R.id.txt_google_photo_name)).setText(R.string.google_photo_login);
                ((TextView) findViewById(R.id.btn_google_photo_connect)).setText("");
            } else {// 로그아웃인 경우
                Intent itt = new Intent(this, GoogleSignInActivity.class);
                startActivityForResult(itt, ISnapsImageSelectConstants.REQCODE_GOOGLE_SIGN_IN);
            }
        } else if (v.getId() == R.id.activity_snapssetting_certification_layout) {
            SnapsLoginManager.startLogInProcess(this, Const_VALUES.LOGIN_P_VERRIFY);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
        GoHomeOpserver.removeGoHomeListenrer(this);
    }

    @Override
    public void onGoHome() {
        finish();
    }

    @Override
    public void onReceiveData(Context context, Intent intent) {
        if (intent.getAction() == Const_VALUE.FACEBOOK_CHANGE_NAME_ACTION) {
            setFaceBookSetting();
        }
    }

    private final int REFRESH_FACEBOOK = 0;
    private final int REFRESH_KAKAO = 1;
    private final int REFRESH_INSTAGRAM = 2;
    private final int REFRESH_GOOGLE_PHOTO = 3;

    @Override
    public void handleMessage(Message msg) {
        try {
            switch (msg.what) {
                case REFRESH_FACEBOOK:
                    if (facebook != null && facebook.isFacebookLogin()) {
                        String fbName = Setting.getString(SnapsSettingActivity.this, Const_VALUE.KEY_FACEBOOK_NAME);
                        txtFBName.setText(fbName);
                        btnFacebookConnect.setText(R.string.disconnect);
                        if (fbName == null || fbName.length() < 1) {
                            FacebookUtil.getProfileData(SnapsSettingActivity.this);
                        }
                    } else {
                        txtFBName.setText(R.string.facebook_login);
                        btnFacebookConnect.setText("");
                    }
                    break;
                case REFRESH_KAKAO:
                    if (kakao != null) {
                        if (kakao.isKakaoLogin()) {
                            String kakaoName = Setting.getString(SnapsSettingActivity.this, Const_VALUE.KEY_KAKAO_NAME);
                            txtKakaoName.setText(kakaoName);
                            btnKakaoConnect.setText(R.string.disconnect);
                        } else {
                            txtKakaoName.setText(getString(R.string.kakaostory_login));
                            btnKakaoConnect.setText("");
                        }
                    }
                    break;
                case REFRESH_INSTAGRAM:
                    if (insta != null) {
                        if (insta.getId() != null && insta.getId().length() > 0) {
                            textInstagramName.setText(insta.getUserName());
                            btnInstagramConnect.setText(R.string.disconnect);
                        } else {
                            textInstagramName.setText(getString(R.string.instagram_login));
                            btnInstagramConnect.setText("");
                        }
                    }
                    break;
                case REFRESH_GOOGLE_PHOTO:
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private class LanguageStringSet {
        public static final String LANG_KOREAN = "ko";
        public static final String LANG_JAPANESE = "ja";
        public static final String LANG_ENGLISH = "en";

        private Context context;
        private String languageKey;

        public LanguageStringSet(Context context, String languageKey) {
            this.context = context;
            this.languageKey = languageKey;
        }

        public String getLanguageKey() {
            return languageKey;
        }

        public String getLanguageString() {
            int strResId = R.string.language_string_korean;
            switch (languageKey) {
                case LANG_KOREAN:
                    strResId = R.string.language_string_korean;
                    break;
                case LANG_ENGLISH:
                    strResId = R.string.language_string_english;
                    break;
                case LANG_JAPANESE:
                    strResId = R.string.language_string_japanese;
                    break;
            }

            return context.getString(strResId);
        }

        public String getLanguageListString() {
            String systemLang = Locale.getDefault().getLanguage();

            String listString = getLanguageString();
//            if( systemLang.equalsIgnoreCase(languageKey) ) // 없어졌음.
//                listString += " (" + context.getString( R.string.language_system_default ) + ")";

            return listString;
        }
    }
}
