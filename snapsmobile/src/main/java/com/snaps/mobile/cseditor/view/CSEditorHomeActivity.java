package com.snaps.mobile.cseditor.view;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.jakewharton.rxbinding3.view.RxView;
import com.jakewharton.rxbinding3.widget.RxCompoundButton;
import com.jakewharton.rxbinding3.widget.RxTextView;
import com.snaps.common.utils.constant.Config;
import com.snaps.mobile.R;
import com.snaps.mobile.cseditor.CSEditorContract;
import com.snaps.mobile.cseditor.CSEditorHomePresenter;
import com.snaps.mobile.cseditor.api.GetProjectDetailIntractorImpl;

import java.util.concurrent.TimeUnit;

import font.FProgressDialog;
import io.reactivex.disposables.CompositeDisposable;

public class CSEditorHomeActivity extends AppCompatActivity implements CSEditorContract.View {

    public static final int CS_EDITOR_HOME_REQUEST_CODE = 3432;

    private CSEditorHomePresenter mPresenter;
    private EditText et_projectcode, et_scheme;
    private FProgressDialog progressDialog;
    private CheckBox cb_use_smart_search;
    private CheckBox cb_use_undefined_font_search;
    private Button btn_get_project_detail, btn_go_to_scheme;
    private CompositeDisposable disposable = new CompositeDisposable();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!Config.isDevelopVersion()) {
            finish();
            return;
        }

        setContentView(R.layout.activity_cseditorhome);
        mPresenter = new CSEditorHomePresenter(getApplicationContext(), new GetProjectDetailIntractorImpl());
        mPresenter.setView(this);

        if (Build.DEVICE.equals("generic_x86")) {
            Button btn_paste_project_code = findViewById(R.id.btn_paste_project_code);
            btn_paste_project_code.setVisibility(View.VISIBLE);
            btn_paste_project_code.setOnClickListener(v -> pasteProduceCode(btn_paste_project_code));
        }

        btn_get_project_detail = findViewById(R.id.btn_get_project_detail);
        disposable.add(RxView.clicks(btn_get_project_detail).throttleFirst(1500L, TimeUnit.MILLISECONDS).subscribe(unit -> mPresenter.onClickGetProjectDetail(et_projectcode.getText().toString())));

        et_projectcode = findViewById(R.id.et_projectcode);
        disposable.add(RxTextView.textChanges(et_projectcode).subscribe(charSequence -> {
            if (charSequence.length() > 13) {
                btn_get_project_detail.setEnabled(true);
                btn_get_project_detail.setBackgroundColor(Color.parseColor("#000000"));
            } else {
                btn_get_project_detail.setEnabled(false);
                btn_get_project_detail.setBackgroundColor(getResources().getColor(R.color.light_grey));
            }
        }));

        btn_go_to_scheme = findViewById(R.id.btn_go_to_scheme);
        btn_go_to_scheme.setOnClickListener(v -> mPresenter.onClickGoToScheme(et_scheme.getText().toString()));

        et_scheme = findViewById(R.id.et_scheme);
        disposable.add(RxTextView.textChanges(et_scheme).subscribe(charSequence -> {
            if (charSequence.length() > 0) {
                btn_go_to_scheme.setEnabled(true);
                btn_go_to_scheme.setBackgroundColor(Color.parseColor("#000000"));
            } else {
                btn_go_to_scheme.setEnabled(false);
                btn_go_to_scheme.setBackgroundColor(getResources().getColor(R.color.light_grey));
            }
        }));

        Button btn_make_product = findViewById(R.id.btn_make_product);
        btn_make_product.setOnClickListener(v -> mPresenter.onClickMakeProduct());

        cb_use_smart_search = findViewById(R.id.cb_use_smart_search);
        cb_use_smart_search.setChecked(Config.isUseDrawSmartSnapsSearchArea());
        disposable.add(RxCompoundButton.checkedChanges(cb_use_smart_search).subscribe(aBoolean -> mPresenter.onChangeUseSmartSearch(aBoolean)));

        cb_use_undefined_font_search = findViewById(R.id.cb_use_undefined_font_search);
        cb_use_undefined_font_search.setChecked(Config.isUseDrawUndefinedFontSearchArea());
        disposable.add(RxCompoundButton.checkedChanges(cb_use_undefined_font_search).subscribe(aBoolean -> mPresenter.onChangeUseUndefinedFontSearch(aBoolean)));

        mPresenter.onViewReady();
    }

    @Override
    public void finishActivity(Intent returnIntent) {
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void showProgressBar() {
        hideProgressBar();
        progressDialog = new FProgressDialog(this);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    public void hideProgressBar() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public void setLastProjectData(String lastProjectCode) {
        et_projectcode.setText(lastProjectCode);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.disposable.clear();
    }

    @Override
    public void showProductList(CharSequence[] productLabels) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setSingleChoiceItems(productLabels, -1, (dialog, which) -> {
            dialog.dismiss();
            mPresenter.onChooseProduct(which);
        });

        builder.setCancelable(true);
        builder.create().show();
    }

    private boolean pasteProduceCode(Button button) {
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (!clipboardManager.hasPrimaryClip()) {
            showBadProduceCodeEffect(button);
            return false;
        }
        if (!clipboardManager.getPrimaryClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
            showBadProduceCodeEffect(button);
            return false;
        }

        ClipData.Item item = clipboardManager.getPrimaryClip().getItemAt(0);
        String projectCode = item.getText().toString().trim();
        if (projectCode.length() != 14) {
            showBadProduceCodeEffect(button);
            return false;
        }
        try {
            Long.parseLong(projectCode);
        } catch (NumberFormatException e) {
            showBadProduceCodeEffect(button);
            return false;
        }

        et_projectcode.setText(projectCode);

        ScaleAnimation scaleAnimation = new ScaleAnimation(1.25f, 1f, 1.25f, 1f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(300);
        scaleAnimation.setFillAfter(true);
        et_projectcode.startAnimation(scaleAnimation);

        return true;
    }

    private void showBadProduceCodeEffect(Button button) {
        //et_projectcode.setText("");

        int colorFrom = getResources().getColor(R.color.red);
        int colorTo = getResources().getColor(R.color.black);
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(500);
        colorAnimation.addUpdateListener(animator -> {
            button.setBackgroundColor((int) animator.getAnimatedValue());
        });
        colorAnimation.start();

        ObjectAnimator
                .ofFloat(button, "translationX", 0, 25, -25, 25, -25, 15, -15, 6, -6, 0)
                .setDuration(500)
                .start();
    }
}
