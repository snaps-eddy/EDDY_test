package com.snaps.common.utils.ui;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.TextView;

import com.snaps.mobile.R;
import com.snaps.mobile.activity.edit.fragment.dialog.DialogInputNameFragment;

public class AccessAppDialog extends Dialog {

    private View confirm = null;

    DialogInputNameFragment.IDialogInputNameClickListener dialogInputNameClickListener;

    public AccessAppDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    public AccessAppDialog(Context context, int themeResId) {
        super(context, themeResId);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    public AccessAppDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    public AccessAppDialog(Context context, DialogInputNameFragment.IDialogInputNameClickListener customDialogListener) {
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
        setContentView(R.layout.access_app_dialog);

        if (getWindow() != null)
            getWindow().setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        confirm = findViewById(R.id.access_app_dialog_ok_tv);

        setDialogInputNameClickListener(lis);

        confirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (getDialogInputNameClickListener() != null)
                    getDialogInputNameClickListener().onClick(true);
                dismiss();
            }
        });

        setCanceledOnTouchOutside(false);
    }
}
