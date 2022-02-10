package com.snaps.mobile.activity.themebook;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Message;

import androidx.core.content.ContextCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.snaps.common.structure.SnapsHandler;
import com.snaps.common.structure.control.TextFormat;
import com.snaps.common.utils.ISnapsHandler;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.constant.Const_VALUES;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.thread.ATask;
import com.snaps.common.utils.ui.FontUtil;
import com.snaps.common.utils.ui.ICustomDialogListener;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.card.SnapsTextOptions;
import com.snaps.mobile.activity.edit.view.custom_progress.SnapsTimerProgressView;
import com.snaps.mobile.activity.edit.view.custom_progress.SnapsTimerProgressViewFactory;
import com.snaps.mobile.activity.themebook.holder.IPhotobookCommonConstants.SnapsTextAlign;
import com.snaps.mobile.component.SnapsEditText;
import com.snaps.mobile.component.SnapsTextWriteColorPickerView;
import com.snaps.mobile.tutorial.SnapsTutorialAttribute;
import com.snaps.mobile.tutorial.SnapsTutorialConstants;
import com.snaps.mobile.tutorial.new_tooltip_tutorial.SnapsTutorialUtil;

import errorhandle.CatchFragmentActivity;
import errorhandle.logger.SnapsLogger;
import errorhandle.logger.model.SnapsLoggerClass;

public class SnapsTextWriteActivity extends CatchFragmentActivity implements OnClickListener, ISnapsHandler, SnapsTextWriteColorPickerView.ISnapsTextWriteColorPickerListener {
    private static final String TAG = SnapsTextWriteActivity.class.getSimpleName();
    private static int MAX_LENGTH_OF_COVER_TITLE = 25;

    private SnapsEditText editTextView;

    private ImageView alignLeftButton;
    private ImageView alignRightButton;
    private ImageView alignCenterButton;
    private SnapsTextAlign currentAlign = SnapsTextAlign.ALIGN_CENTER;
    private boolean isNeedSetTextAlign = true; //힌트는 Gravity를 적용하지 않기 위해

    private com.snaps.mobile.component.SnapsTextWriteColorPickerView colorPickerView = null;
    private View footerForHideKeyboard = null, colorPickerLayout = null;

    private View colorPickerBtn = null, keyboardBtn = null;
    private int keyboardHeight;
    private int previousHeightDifference = 0;
    private PopupWindow popupWindow = null;
    private boolean isKeyBoardVisible = false;
    private boolean isFromLandscapeMode = false;

    private String currentFontColor = null, baseFontColor = null;
    private SnapsHandler snapsHandler = null;

    private float hintTextSize = 0, writeTextSize = 0;
    private boolean isCoverTitleEdit = false;
    private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        SnapsLogger.appendClassTrackingLog(new SnapsLoggerClass<>(this));

        setContentView(R.layout.snaps_text_write_activity);

        mContext = getApplicationContext();

        if (UIUtil.getCurrentScreenWidth(mContext) > UIUtil.getCurrentScreenHeight(mContext)) {
            RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.layout_edittext);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) relativeLayout.getLayoutParams();
            layoutParams.setMargins(layoutParams.leftMargin, 0, layoutParams.rightMargin, 0);
            relativeLayout.setLayoutParams(layoutParams);
        }

        init();

        //KT 북
        if (Config.isKTBook()) {
            getWindow().getDecorView().setBackgroundColor(Color.WHITE);
        }
    }

    private void checkCoverTitleEdit() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            isCoverTitleEdit = bundle.getBoolean("is_cover_title_edit");
        }
    }

    private void init() {
        try {
            MAX_LENGTH_OF_COVER_TITLE = getResources().getInteger(R.integer.photo_book_title_text_max_length);

            checkCoverTitleEdit();

            SnapsTextOptions textOptions = loadSnapsTextOption();
            initInstanceWithTextOption(textOptions);

            initControls();

            initKeyboardLayout();

            //font download 가능성이 있으니 async로 처리 한다
            loadWrittenTextAfterApplyFontTypeFace(textOptions);
        } catch (Exception e) {
            Dlog.e(TAG, e);
            MessageUtil.toast(this, R.string.failed_read_to_menu_file);
            finish();
        }
    }

    private void initControls() throws Exception {
        initTitleBar();

        initEditText();

        initBottomMenu();
    }

    private void initBottomMenu() {
        if (!isCoverTitleEdit) {
            View bottomMenuLayout = findViewById(R.id.snaps_text_write_option_layout);
            if (bottomMenuLayout != null) {
                bottomMenuLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    private void initTitleBar() throws Exception {
        TextView titleTextView = (TextView) findViewById(R.id.ThemeTitleText);
        if (titleTextView != null) {
            String titleText = isCoverTitleEdit ? getString(R.string.enter_title) : getString(R.string.write);
            titleTextView.setText(titleText);
            titleTextView.setTextColor(Color.WHITE);
        }

        TextView completeButton = (TextView) findViewById(R.id.ThemebtnTopNext);
        if (completeButton != null) {
            String completeText = isCoverTitleEdit ? getString(R.string.done) : getString(R.string.confirm);
            completeButton.setText(completeText);
            completeButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    complete();
                }
            });

            completeButton.setTextColor(Color.WHITE);

            if (isCoverTitleEdit) {
                completeButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
            }
        }

        ImageView backButton = (ImageView) findViewById(R.id.ThemeTitleLeft);
        if (backButton != null) {
            backButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackKeyPressed();
                }
            });

            backButton.setColorFilter(Color.rgb(255, 255, 255));
        }

        if (findViewById(R.id.ThemeTitleLeftLy) != null) {
            findViewById(R.id.ThemeTitleLeftLy).setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    onBackKeyPressed();
                }
            });
        }

        //KT 북
        if (Config.isKTBook()) {
            titleTextView.setTextColor(Color.BLACK);
            completeButton.setTextColor(Color.BLACK);
            backButton.setColorFilter(Color.BLACK);
        }
    }

    private void onBackKeyPressed() {
        MessageUtil.alertnoTitleTwoBtn(this, getString(R.string.text_write_back_key_pressed_confirm_msg), new ICustomDialogListener() {
            @Override
            public void onClick(byte clickedOk) {
                if (clickedOk == ICustomDialogListener.OK) {
                    hideKeyboard();
                    finish();
                }
            }
        });
    }

    private void initEditText() throws Exception {
        editTextView = (SnapsEditText) findViewById(R.id.content_edit);
        //KT 북
        //원본 코드가 커서를 white로 설정해서 흰색 바탕은 경우 커서가 안보인다.
        //그래서 View를 하나더 만듬
        if (Config.isKTBook()) {
            editTextView.setVisibility(View.GONE);
            editTextView = (SnapsEditText) findViewById(R.id.content_edit_kt);
            editTextView.setVisibility(View.VISIBLE);
        }

        String titleText = isCoverTitleEdit ? getString(R.string.hint_plz_input_cover_title) : getString(R.string.initial_text);
        editTextView.setHint(titleText);
        editTextView.addTextChangedListener(new TextWatcher() {
            String previousString = "";

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                previousString = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    editTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, writeTextSize);
                    if (isCoverTitleEdit) {
                        editTextView.setGravity(Gravity.CENTER);
                    } else {
                        if (isNeedSetTextAlign) {
                            isNeedSetTextAlign = false;
                            setTextAlign(currentAlign);
                        }
                    }
                } else {
                    editTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, hintTextSize);
                    isNeedSetTextAlign = true;
                    editTextView.setGravity(Gravity.CENTER);
                }

                //KT 북
                //https://bbulog.tistory.com/1
                if (Config.isKTBook()) {
                    if (editTextView.getLineCount() > Const_VALUES.KT_BOOK_TITLE_OR_PAGE_TEXT_MAX_LINE) {
                        editTextView.removeTextChangedListener(this);	//재귀 호출 방지
                        editTextView.setText(previousString);
                        editTextView.setSelection(editTextView.length());
                        editTextView.addTextChangedListener(this);

                        MessageUtil.toast(mContext, Const_VALUES.KT_BOOK_TITLE_OR_PAGE_TEXT_MAX_LINE_TOAST_MSG);
                    }

                    if (editTextView.getText().length() > Const_VALUES.KT_BOOK_TITLE_OR_PAGE_TEXT_MAX_LENGTH) {
                        editTextView.removeTextChangedListener(this);	//재귀 호출 방지
                        editTextView.setText(previousString);
                        editTextView.setSelection(editTextView.length());
                        editTextView.addTextChangedListener(this);

                        MessageUtil.toast(mContext, Const_VALUES.KT_BOOK_TITLE_OR_PAGE_TEXT_MAX_LENGTH_TOAST_MSG);
                    }
                }
            }
        });

        if (isCoverTitleEdit) {
            InputFilter[] FilterArray = new InputFilter[1];
            FilterArray[0] = new InputFilter.LengthFilter(MAX_LENGTH_OF_COVER_TITLE);
            editTextView.setFilters(FilterArray);

            editTextView.setSingleLine();
        }

        editTextView.requestFocus();

        notifyTextColorWithFontColor(currentFontColor);

//		if (isCoverTitleEdit) {
//			setTextAlign(SnapsTextAlign.ALIGN_CENTER);
//		}
    }

    @Override
    public void onSelectTextColor(String color) {
        currentFontColor = color;

        notifyTextColorWithFontColor(color);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (snapsHandler != null) {

            showSoftKeyboard();

            showWriteTutorialWithDelay(1000);
        }
    }

    private void showSoftKeyboard() {
        if (isFromLandscapeMode) {
            snapsHandler.sendEmptyMessageDelayed(HANDLE_MSG_SHOW_KEYBOARD, 500);
        } else {
            if (editTextView != null) {
                UIUtil.showKeyboard(this, editTextView);
            }
        }
    }

    private void showWriteTutorialWithDelay(long delay) {
        snapsHandler.sendEmptyMessageDelayed(HANDLE_MSG_SHOW_TEXT_WRITE_TUTORIAL, 1000);
    }

    private void showTextWriteTutorial() {
        if (isCoverTitleEdit) return;
        try {
            View optionView = findViewById(R.id.text_write_option_align_layout);
            if (optionView == null) return;
            SnapsTutorialUtil.showTooltip(this, new SnapsTutorialAttribute.Builder().setViewPosition(SnapsTutorialConstants.eTUTORIAL_VIEW_POSITION.BOTTOM_NOT_TAIL)
                    .setText(getString(R.string.text_write_activity_tutorial_msg))
                    .setTargetView(optionView)
                    .setTopMargin(UIUtil.convertDPtoPX(this, -28))
                    .create());
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private void initInstanceWithTextOption(SnapsTextOptions options) {
        snapsHandler = new SnapsHandler(this);

        hintTextSize = 13;
        writeTextSize = 17;

        if (options != null) {
            isFromLandscapeMode = options.isFromLandscapeMode();

            TextFormat textFormat = options.getTextFormat();
            if (textFormat != null) {
                currentFontColor = textFormat.fontColor;
                baseFontColor = textFormat.baseFontColor;
                if (StringUtil.isEmpty(baseFontColor)) {
                    textFormat.baseFontColor = currentFontColor;
                    baseFontColor = currentFontColor;
                }

                try {
                    if (Const_PRODUCT.isBabyNameStikerGroupProduct()) {
                        writeTextSize = Math.min(36, Math.max(23, Float.parseFloat(textFormat.fontSize)));
                    } else {
                        writeTextSize = Math.min(36, Math.max((isCoverTitleEdit ? 17 : 13), Float.parseFloat(textFormat.fontSize)));
                    }

                } catch (NumberFormatException e) {
                    Dlog.e(TAG, e);
                }
            }
        }
    }

    private void notifyTextColorWithFontColor(String color) {
        currentFontColor = color;
        if (StringUtil.isEmpty(currentFontColor) || editTextView == null) return;
        try {
            int parseColor = Color.parseColor(("#" + currentFontColor));
            editTextView.setTextColor(parseColor);

            if (colorPickerBtn != null)
                colorPickerBtn.setBackgroundColor(parseColor);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private void initKeyboardLayout() {
        footerForHideKeyboard = findViewById(R.id.snaps_text_write_option_layout_footer_for_hide_keyboard);

        colorPickerLayout = getLayoutInflater().inflate(R.layout.snaps_text_write_option_color_picker_layout, null);
        View baseColorBgView = colorPickerLayout.findViewById(R.id.snaps_text_write_option_color_picker_base_color_view);
        View baseColorSelector = colorPickerLayout.findViewById(R.id.snaps_text_write_option_color_picker_base_color_selector);
        colorPickerView = (SnapsTextWriteColorPickerView) colorPickerLayout.findViewById(R.id.snaps_text_write_option_color_picker_view);
        colorPickerView.setBaseColorBgView(baseColorBgView);
        colorPickerView.initBaseColorSelectorWithBaseColor(baseColorSelector, baseFontColor);
        colorPickerView.setColorPickerListener(this);
        colorPickerView.selectPrevColor(currentFontColor);

        if (!isCoverTitleEdit) {
            LinearLayout lyAlign = (LinearLayout) findViewById(R.id.text_write_option_align_layout);
            lyAlign.setVisibility(View.VISIBLE);
        }

        alignLeftButton = (ImageView) findViewById(R.id.text_write_option_text_align_left);
        alignCenterButton = (ImageView) findViewById(R.id.text_write_option_text_align_center);
        alignRightButton = (ImageView) findViewById(R.id.text_write_option_text_align_right);

        alignLeftButton.setOnClickListener(this);
        alignRightButton.setOnClickListener(this);
        alignCenterButton.setOnClickListener(this);

        changeKeyboardHeight((int) getResources().getDimension(R.dimen.keyboard_height));

        colorPickerBtn = findViewById(R.id.text_write_option_layout_color_pick_color_show_btn);
        colorPickerBtn.setBackgroundColor(Color.parseColor("#" + currentFontColor));
        colorPickerBtn.setOnClickListener(this);

        //글자색 변경 가능/불가를 외부에서 설정하고 싶지만 그러면 수정이 너무 많네... ㅡㅡ;
        //그래서 아래와 같이
        //매지컬 반사 슬로건, 홀로그램 슬로건, 반사 슬로건은 글자색을 변경하지 못하게 한다.
        if (Const_PRODUCT.isMagicalReflectiveSloganProduct() ||
                Const_PRODUCT.isHolographySloganProduct() ||
                Const_PRODUCT.isReflectiveSloganProduct())
        {
            colorPickerBtn.setVisibility(View.GONE);
        }

        keyboardBtn = findViewById(R.id.text_write_option_layout_keyboard_iv);
        keyboardBtn.setOnClickListener(this);

        createPopUpView();

        checkKeyboardHeight();
    }

    private void hideColorPickerView() throws Exception {
        if (popupWindow.isShowing())
            popupWindow.dismiss();
    }

    private void showColorPickerView() throws Exception {
        if (!popupWindow.isShowing()) {

            popupWindow.setHeight(keyboardHeight);

            if (isKeyBoardVisible) {
                footerForHideKeyboard.setVisibility(LinearLayout.GONE);
            } else {
                footerForHideKeyboard.setVisibility(LinearLayout.VISIBLE);
            }

            View parentLayout = findViewById(R.id.snaps_text_write_parent_layout);
            popupWindow.showAtLocation(parentLayout, Gravity.BOTTOM, 0, 0);

            showKeyboardBtn();
        }
    }

    private void showKeyboardBtn() {
        if (keyboardBtn != null) {
            keyboardBtn.setVisibility(View.VISIBLE);
        }
    }

    private void hideKeyboardBtn() {
        if (keyboardBtn != null) {
            keyboardBtn.setVisibility(View.GONE);
        }
    }

    private void changeKeyboardHeight(int height) {
        if (height < 100 || colorPickerLayout == null) return;
        keyboardHeight = height;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, keyboardHeight);
        footerForHideKeyboard.setLayoutParams(params);
    }

    private SnapsTextOptions loadSnapsTextOption() throws Exception {
        Bundle bundle = getIntent().getExtras();
        return bundle != null ? (SnapsTextOptions) bundle.getParcelable("snapsTextOption") : null;
    }

    private void loadWrittenTextAfterApplyFontTypeFace(final SnapsTextOptions textOptions) throws Exception {
        if (textOptions == null || editTextView == null) return;

        ATask.executeVoidWithThreadPool(new ATask.OnTask() {
            private Typeface typeface = null;

            @Override
            public void onPre() {
                SnapsTimerProgressView.showProgress(SnapsTextWriteActivity.this,
                        SnapsTimerProgressViewFactory.eTimerProgressType.PROGRESS_TYPE_LOADING,
                        getString(R.string.font_downloading_msg));
            }

            @Override
            public void onBG() {
                TextFormat format = textOptions.getTextFormat();
                if (format != null)
                    typeface = FontUtil.getFontTypeface(SnapsTextWriteActivity.this, format.fontFace);
            }

            @Override
            public void onPost() {
                try {
                    SnapsTimerProgressView.destroyProgressView();

                    loadPrevText();

                    if (typeface != null) {
                        editTextView.setTypeface(typeface);
                    } else {
                        editTextView.setTypeface(Const_VALUE.SNAPS_TYPEFACE_YG033);
                    }

                    applyPrevTextAlignWithTextOption(textOptions);
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                }
            }
        });
    }

    private void loadPrevText() throws Exception {
        String edit_txt = "";
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            edit_txt = bundle.getString("written_text");
        }
        if (!StringUtil.isEmpty(edit_txt)) {
            editTextView.setText(edit_txt);
            editTextView.setSelection(edit_txt.length());
        }
    }

    private void applyPrevTextAlignWithTextOption(SnapsTextOptions snapsTextOption) throws Exception {
        if (snapsTextOption == null) return;
        setTextAlign(SnapsTextAlign.values()[snapsTextOption.getAlignOrdinal()]);
    }

    private boolean isOverTextLength(String text) {
        if (StringUtil.isEmpty(text)) return false;
        return isCoverTitleEdit && text.length() > MAX_LENGTH_OF_COVER_TITLE;
    }

    private void complete() {
        String writtenText = editTextView.getText().toString();
        writtenText = StringUtil.trimOnlySuffix(writtenText);

        if (isOverTextLength(writtenText)) {
            MessageUtil.toast(this, getString(R.string.input_title_desc));
            return;
        }

        final String filterString = StringUtil.getFilterString(writtenText);

        if (writtenText.length() != filterString.length()) {
            MessageUtil.alert(SnapsTextWriteActivity.this, getString(R.string.emoji_delete_msg), false, new ICustomDialogListener() {
                @Override
                public void onClick(byte clickedOk) {
                    finishActivityWithIntentInfo(filterString);
                }
            });
        } else {
            finishActivityWithIntentInfo(filterString);
        }
    }

    private void finishActivityWithIntentInfo(String resultText) {
        hideKeyboard();

        Intent data = new Intent();
        if (currentAlign != null)
            data.putExtra("snapsTextAlign", currentAlign.ordinal());
        data.putExtra("contentText", resultText);

        data.putExtra("fontColor", currentFontColor);

        setResult(RESULT_OK, data);
        finish();
    }

    private void createPopUpView() {
        popupWindow = new PopupWindow(colorPickerLayout, ViewGroup.LayoutParams.MATCH_PARENT, keyboardHeight, false);

        popupWindow.setOnDismissListener(() -> {
            if (footerForHideKeyboard != null)
                footerForHideKeyboard.setVisibility(LinearLayout.GONE);
        });
    }

    private void checkKeyboardHeight() {
        final View parentLayout = findViewById(R.id.snaps_text_write_parent_layout);
        if (parentLayout == null) return;

        parentLayout.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        try {
                            Rect r = new Rect();
                            parentLayout.getWindowVisibleDisplayFrame(r);

                            int screenHeight = parentLayout.getRootView()
                                    .getHeight();
                            int heightDifference = screenHeight - (r.bottom);

                            if (previousHeightDifference - heightDifference > 50) {
                                popupWindow.dismiss();
                            }

                            previousHeightDifference = heightDifference;
                            if (heightDifference > 100) {
                                isKeyBoardVisible = true;
                                changeKeyboardHeight(heightDifference);

                            } else {
                                isKeyBoardVisible = false;
                            }
                        } catch (Exception e) {
                            Dlog.e(TAG, e);
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        try {
            if (id == R.id.text_write_option_text_align_left) {
                setTextAlign(SnapsTextAlign.ALIGN_LEFT);
            } else if (id == R.id.text_write_option_text_align_center) {
                setTextAlign(SnapsTextAlign.ALIGN_CENTER);
            } else if (id == R.id.text_write_option_text_align_right) {
                setTextAlign(SnapsTextAlign.ALIGN_RIGHT);
            } else if (id == R.id.text_write_option_layout_keyboard_iv) {
                hideColorPickerView();

                hideKeyboardBtn();
            } else if (id == R.id.text_write_option_layout_color_pick_color_show_btn) {
                showColorPickerView();
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private void setTextAlign(SnapsTextAlign align) {
        try {
            currentAlign = align;

            if (isCoverTitleEdit) {
                editTextView.setGravity(Gravity.CENTER);
            } else {
                switch (align) {
                    case ALIGN_CENTER:
                        alignLeftButton.setImageResource(R.drawable.icon_btn_left_off);
                        alignCenterButton.setImageResource(R.drawable.icon_btn_center_on);
                        alignRightButton.setImageResource(R.drawable.icon_btn_right_off);
                        editTextView.setGravity(Gravity.CENTER);
                        break;
                    case ALIGN_RIGHT:
                        alignLeftButton.setImageResource(R.drawable.icon_btn_left_off);
                        alignCenterButton.setImageResource(R.drawable.icon_btn_center_off);
                        alignRightButton.setImageResource(R.drawable.icon_btn_right_on);
                        editTextView.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
                        break;
                    default:
                    case ALIGN_LEFT:
                        alignLeftButton.setImageResource(R.drawable.icon_btn_left_on);
                        alignCenterButton.setImageResource(R.drawable.icon_btn_center_off);
                        alignRightButton.setImageResource(R.drawable.icon_btn_right_off);
                        editTextView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
                        break;
                }
            }

            if (editTextView.getText() == null || editTextView.getText().length() < 1) {
                editTextView.setGravity(Gravity.CENTER);
                isNeedSetTextAlign = true;
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (popupWindow.isShowing()) {
                popupWindow.dismiss();
                return false;
            } else {
                onBackKeyPressed();
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStop() {
        super.onStop();
        hideKeyboard();
    }

    private void hideKeyboard() {
        try {
            if (editTextView != null) {
                UIUtil.hideKeyboard(this, editTextView);
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private static final int HANDLE_MSG_SHOW_KEYBOARD = 0;
    private static final int HANDLE_MSG_SHOW_TEXT_WRITE_TUTORIAL = 1;

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case HANDLE_MSG_SHOW_KEYBOARD:
                if (!isKeyBoardVisible && editTextView != null) {
                    UIUtil.showKeyboardForced(this, editTextView);
                }
                break;
            case HANDLE_MSG_SHOW_TEXT_WRITE_TUTORIAL:
                showTextWriteTutorial();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (popupWindow != null) {
            popupWindow.dismiss();
            popupWindow = null;
        }
    }
}
