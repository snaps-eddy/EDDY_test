package com.snaps.common.utils.ui;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.snaps.mobile.R;
import com.snaps.mobile.activity.edit.fragment.dialog.DialogInputNameFragment;

public class PassportPhotoSaveDialog extends Dialog {

    LinearLayout confirm, cancel;
    TextView confirmTxt, cancelTxt;

    DialogInputNameFragment.IDialogInputNameClickListener dialogInputNameClickListener;

    public PassportPhotoSaveDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    public PassportPhotoSaveDialog(Context context, int themeResId) {
        super(context, themeResId);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    public PassportPhotoSaveDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    public PassportPhotoSaveDialog(Context context, DialogInputNameFragment.IDialogInputNameClickListener customDialogListener) {
        super(context, R.style.TransparentProgressDialog);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        init(context, customDialogListener);
    }

    public DialogInputNameFragment.IDialogInputNameClickListener getDialogInputNameClickListener() {
        return dialogInputNameClickListener;
    }

    public void setDialogInputNameClickListener(DialogInputNameFragment.IDialogInputNameClickListener dialogInputNameClickListener) {
        this.dialogInputNameClickListener = dialogInputNameClickListener;
    }

    private void init(Context context, DialogInputNameFragment.IDialogInputNameClickListener lis) {
        if (context == null)
            return;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.passport_photo_save_confirm_dialog);

        if (getWindow() != null)
            getWindow().setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        confirm = (LinearLayout) findViewById(R.id.custom_dialog_ok_btn);
        cancel = (LinearLayout) findViewById(R.id.custom_dialog_cancel_btn);

        confirmTxt = (TextView) findViewById(R.id.custom_dialog_ok_tv);
        cancelTxt = (TextView) findViewById(R.id.custom_dialog_cancel_tv);

        setDialogInputNameClickListener(lis);

        confirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (getDialogInputNameClickListener() != null)
                    getDialogInputNameClickListener().onClick(true);
                dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (getDialogInputNameClickListener() != null)
                    getDialogInputNameClickListener().onClick(false);
                dismiss();

            }
        });

        setCanceledOnTouchOutside(false);
    }

    public void setLowResolutionPhotoDescVisible(boolean isShow) {
        View descView = findViewById(R.id.passport_photo_save_confirm_dialog_resolution_desc_tv);
        if (descView != null) {
            descView.setVisibility(isShow ? View.VISIBLE : View.GONE);
        }
    }
}
