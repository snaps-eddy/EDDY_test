package com.snaps.mobile.activity.themebook;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_EKEY;
import com.snaps.common.utils.constant.Const_VALUES;
import com.snaps.common.utils.constant.SnapsConfigManager;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.ICustomDialogListener;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.google_style_image_selector.activities.ImageSelectActivityV2;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectIntentData;
import com.snaps.mobile.activity.home.fragment.GoHomeOpserver;

import java.util.regex.Pattern;

import errorhandle.CatchActivity;
import errorhandle.logger.Logg;
import errorhandle.logger.SnapsLogger;
import errorhandle.logger.model.SnapsLoggerClass;

public class ThemeTitleActivity extends CatchActivity implements GoHomeOpserver.OnGoHomeOpserver {

    private static final String TAG = ThemeTitleActivity.class.getSimpleName();

    public static final String KEY_NEXT_LANDING_ACTION = "whereis";
    public static final String NEXT_LANDING_ACTION_EDIT = "edit";
    public static final String NEXT_LANDING_ACTION_RECOMMEND_PHOTO_BOOK = "recommend_photo_book";
    public static final String NEXT_LANDING_ACTION_SIMPLE_PHOTO_BOOK = "simplephoto_book";
    public static final String NEXT_LANDING_ACTION_SIMPLE_MAKING_PHOTO_BOOK = "simple_making_book";
    public static final String NEXT_LANDING_ACTION_KT_BOOK = "kt_book";


    TextView mNextBtn;
    TextView mNextBottomBtn;
    ImageView mPreBtn;
    TextView mThemeTitle;

    EditText mEditTxt;
    String txtThemeTitle = "";
    String mWhereis = "";

    TextView tv_title_message;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SnapsLogger.appendClassTrackingLog(new SnapsLoggerClass<>(this));

        boolean isScreenModeChange = false;
        if (getIntent() != null) {
            isScreenModeChange = getIntent().getBooleanExtra(Const_EKEY.SCREEN_ORIENTATION_STATE_CHANGE, false);
        }

        boolean isLandscapeMode = isScreenModeChange ? UIUtil.fixCurrentOrientationAndReturnBoolLandScape(this) : UIUtil.fixOrientation(this, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        if (isLandscapeMode) {
            UIUtil.updateFullscreenStatus(this, true);
        } else {
            UIUtil.updateFullscreenStatus(this, false);
        }

        setContentView(R.layout.activity_theme_title);

        mThemeTitle = findViewById(R.id.ThemeTitleText);
        mThemeTitle.setText(getString(R.string.enter_title));
        mNextBtn = findViewById(R.id.ThemebtnTopNext);
        mNextBtn.setText(getString(R.string.next));
        mNextBtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
        mNextBottomBtn = findViewById(R.id.activity_theme_title_next_btn);
        mNextBottomBtn.setText(getString(R.string.next));
        mPreBtn = findViewById(R.id.ThemeTitleLeft);
        RelativeLayout preBtnLy = findViewById(R.id.ThemeTitleLeftLy);

        mEditTxt = findViewById(R.id.title_edit);
        mWhereis = getIntent().getStringExtra(KEY_NEXT_LANDING_ACTION);

        tv_title_message = findViewById(R.id.title_message);

        if (mWhereis.equals(NEXT_LANDING_ACTION_EDIT)) {
            mNextBtn.setText(getString(R.string.done));
            mNextBottomBtn.setText(getString(R.string.done));

            mEditTxt.setText(Config.getPROJ_NAME());

            mEditTxt.setSelection(Config.getPROJ_NAME().length());

        } else if (mWhereis.equals(NEXT_LANDING_ACTION_RECOMMEND_PHOTO_BOOK)) {
            mPreBtn.setVisibility(View.VISIBLE);
            mNextBtn.setText(getString(R.string.next));
            mNextBottomBtn.setText(getString(R.string.next));

            if (tv_title_message != null) {
                tv_title_message.setText(R.string.input_title_desc_for_recommend_photobook);
            }

            if (SnapsConfigManager.isAutoLaunchProductMakingMode()) {
                performNextStepWithFilteredTitle("test");
            }
        } else { // 편집이 아니면 무조건 다음..으로 표시
            mPreBtn.setVisibility(View.VISIBLE);
            mNextBtn.setText(getString(R.string.next));
            mNextBottomBtn.setText(getString(R.string.next));
            String templateCode = getIntent().getStringExtra(Const_EKEY.THEME_SELECT_TEMPLE);
            String productCode = getIntent().getStringExtra(Const_EKEY.HOME_SELECT_PRODUCT_CODE);
            // 템플릿 코드와 프로덕트 코드를 Config에 저장한다.
            Config.setPROD_CODE(productCode);
            Config.setTMPL_CODE(templateCode);
            Dlog.d("onCreate() templateCode:" + templateCode + ", productCode:" + productCode);
        }

        mPreBtn.setOnClickListener(v -> onBackPressed());

        preBtnLy.setOnClickListener(v -> onBackPressed());

        //KT 북
        int maxTitleLength = getResources().getInteger(R.integer.photo_book_title_text_max_length);
        if (Config.isKTBook()) {
            maxTitleLength = Const_VALUES.KT_BOOK_COVER_TITLE_MAX_LENGTH;
            tv_title_message.setText(Const_VALUES.KT_BOOK_TITLE_DESC_MSG); //메시지 변경
        }

        mEditTxt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxTitleLength)});

        mEditTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                changeNextBtnsStatus();
            }
        });

        mNextBtn.setOnClickListener(onNextKeyClickedListener);
        mNextBottomBtn.setOnClickListener(onNextKeyClickedListener);

        GoHomeOpserver.addGoHomeListener(this);

        changeNextBtnsStatus();
    }

    //KT 북
    private void changeNextBtnsStatus() {
        if (Config.isKTBook()) {
            String title = mEditTxt.getText().toString();
            final String filterString = StringUtil.getFilterString(title);

            if (filterString.length() > 0) {
                if (mNextBottomBtn.isClickable()) {
                    return;
                }

                mNextBottomBtn.setClickable(true);
                mNextBottomBtn.setBackgroundColor(Color.parseColor("#E36A63"));

                mNextBtn.setTextColor(Color.BLACK);
                mNextBtn.setClickable(true);
            } else {
                if (mNextBottomBtn.isClickable() == false) {
                    return;
                }

                mNextBottomBtn.setClickable(false);
                mNextBottomBtn.setBackgroundColor(Color.parseColor("#999999"));

                mNextBtn.setTextColor(Color.parseColor("#999999"));
                mNextBtn.setClickable(false);
            }
        }
    }

    private OnClickListener onNextKeyClickedListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            UIUtil.blockClickEvent(v);
            String title = mEditTxt.getText().toString();
            final String filterString = StringUtil.getFilterString(title);

            if (title.length() != filterString.length()) {
                MessageUtil.alert(ThemeTitleActivity.this, getString(R.string.emoji_delete_msg), false, clickedOk -> performNextStepWithFilteredTitle(filterString));
            } else {
                performNextStepWithFilteredTitle(filterString);
            }
        }
    };

    private void performNextStepWithFilteredTitle(String title) {
        // 라인피드 제거.. 제목은 무조건 한줄로만 가능함...
        title = title.replace("\n", "");
        title = title.replace("\r", "");

        Config.setPROJ_NAME(title);

        if (mWhereis == null) {
            doDefaultNextAction(title);
            finish();
            return;
        }

        switch (mWhereis) {
            case NEXT_LANDING_ACTION_SIMPLE_PHOTO_BOOK:
                if (Config.useKorean()) {
                    Intent intent = new Intent(ThemeTitleActivity.this, SmartSnapsTypeSelectActivity.class);
                    startActivity(intent);

                } else {
                    Intent intent = new Intent(ThemeTitleActivity.this, ImageSelectActivityV2.class);
                    ImageSelectIntentData intentDatas = new ImageSelectIntentData.Builder()
                            .setHomeSelectProduct(Config.SELECT_SIMPLEPHOTO_BOOK)
                            .setHomeSelectProductCode(Config.getPROD_CODE())
                            .setHomeSelectKind("").create();

                    Bundle bundle = new Bundle();
                    bundle.putSerializable(Const_EKEY.IMAGE_SELECT_INTENT_DATA_KEY, intentDatas);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                break;

            case NEXT_LANDING_ACTION_SIMPLE_MAKING_PHOTO_BOOK: {
                Intent intent = new Intent(ThemeTitleActivity.this, ImageSelectActivityV2.class);
                ImageSelectIntentData intentDatas = new ImageSelectIntentData.Builder()
                        .setHomeSelectProduct(Config.SELECT_SIMPLE_MAKING_BOOK)
                        .setHomeSelectProductCode(Config.getPROD_CODE())
                        .setHomeSelectKind("").create();

                Bundle bundle = new Bundle();
                bundle.putSerializable(Const_EKEY.IMAGE_SELECT_INTENT_DATA_KEY, intentDatas);
                intent.putExtras(bundle);
                startActivity(intent);
                break;

            }
            case NEXT_LANDING_ACTION_RECOMMEND_PHOTO_BOOK:
                setResult(RESULT_OK);
                break;

            case NEXT_LANDING_ACTION_KT_BOOK: {
                Intent intent = new Intent(ThemeTitleActivity.this, ImageSelectActivityV2.class);
                ImageSelectIntentData intentDatas = new ImageSelectIntentData.Builder()
                        .setHomeSelectProduct(Config.SELECT_KT_BOOK)
                        .setHomeSelectProductCode(Config.getPROD_CODE())
                        .setHomeSelectKind("").create();

                Bundle bundle = new Bundle();
                bundle.putSerializable(Const_EKEY.IMAGE_SELECT_INTENT_DATA_KEY, intentDatas);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            }
            default:
                doDefaultNextAction(title);
                break;
        }
        finish();
    }

    private void doDefaultNextAction(String title) {
        Intent data = new Intent();
        data.putExtra("contentText", title);
        setResult(RESULT_OK, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onGoHome() {
        finish();
    }

    /***
     * 영문 숫자 한글만 입력이 되도록 필터 생성...
     */
    public InputFilter filterAlphaNumHan = new InputFilter() {
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Pattern ps = Pattern.compile("^[a-zA-Z0-9가-힣ㄱ-ㅎㅏ-ㅣ\u318D\u119E\u11A2\u2022\u2025a\u00B7\uFE55\\s]+$");

            if (!ps.matcher(source).matches()) {
                return "";
            }
            return null;
        }
    };

    /**
     * 이모티콘등 제거하는 함수..
     *
     * @return
     */
    String getFilterString(String plainText) {
        String fillter = plainText.replaceAll("[\u2600-\u26ff\ud83d\ude00-\ud83d\ude4f\uefff\u318D\u119E\u11A2]", "");

        return fillter;

    }
}
