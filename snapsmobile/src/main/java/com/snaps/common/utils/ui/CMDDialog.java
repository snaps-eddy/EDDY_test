package com.snaps.common.utils.ui;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.snaps.mobile.R;

public class CMDDialog extends Dialog {

    LinearLayout confirm, cancel;
    TextView confirmTxt;

    ICustomDialogListener mListener;

    public CMDDialog(Context context) {
        super(context, R.style.TransparentProgressDialog);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    public CMDDialog(Context context, String msg, ICustomDialogListener lis) {
        super(context, R.style.TransparentProgressDialog);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        init(context, msg, null, R.string.confirm, R.string.cancel, lis, null);
    }

    public CMDDialog(Context context, String msg, int okbtnStrResId, int cancelbtnResId, ICustomDialogListener lis) {
        super(context, R.style.TransparentProgressDialog);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        init(context, msg, null, okbtnStrResId, cancelbtnResId, lis, null);
    }

    public CMDDialog(Context context, String msg, ICustomDialogListener lis, OnCancelListener cancelListenr) {
        super(context, R.style.TransparentProgressDialog);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        init(context, msg, null, R.string.confirm, R.string.cancel, lis, cancelListenr);
    }

    public CMDDialog(Context context, String msg, int okbtnStrResId, int cancelbtnResId, ICustomDialogListener lis, OnCancelListener cancelListenr) {
        super(context, R.style.TransparentProgressDialog);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        init(context, msg, null, okbtnStrResId, cancelbtnResId, lis, cancelListenr);
    }

    public CMDDialog(Context context, String msg, String subMsg, ICustomDialogListener lis) {
        super(context, R.style.TransparentProgressDialog);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        init(context, msg, subMsg, R.string.confirm, R.string.cancel, lis, null);
    }

    public CMDDialog(Context context, String msg, String subMsg, int okbtnStrResId, int cancelbtnResId, ICustomDialogListener lis) {
        super(context, R.style.TransparentProgressDialog);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        init(context, msg, subMsg, okbtnStrResId, cancelbtnResId, lis, null);
    }

    public CMDDialog(Context context, String msg, String subMsg, int okbtnStrResId, int cancelbtnResId, ICustomDialogListener lis, boolean isInit2) {
        super(context, R.style.TransparentProgressDialog);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        init(context, msg, subMsg, okbtnStrResId, cancelbtnResId, lis, null);
    }

    public CMDDialog(Context context, String msg, String subMsg, ICustomDialogListener lis, OnCancelListener cancelListenr) {
        super(context, R.style.TransparentProgressDialog);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        init(context, msg, subMsg, R.string.confirm, R.string.cancel, lis, cancelListenr);
    }

    public CMDDialog(Context context, String msg, String subMsg, int okbtnStrResId, int cancelbtnResId, ICustomDialogListener lis, OnCancelListener cancelListenr) {
        super(context, R.style.TransparentProgressDialog);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        init(context, msg, subMsg, okbtnStrResId, cancelbtnResId, lis, cancelListenr);
    }

    private void init(Context context, String msg, String subMsg, int okbtnStrResId, int cancelbtnResId, ICustomDialogListener lis, OnCancelListener cancelListener) {
        if (context == null)
            return;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.cmd_dialog);

        getWindow().setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        confirm = findViewById(R.id.cmd_dialog_ok_btn);
        confirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mListener != null)
                    mListener.onClick(ICustomDialogListener.OK);
                dismiss();
            }
        });

        View ftpBtn = findViewById(R.id.cmd_dialog_ftp_btn);
        ftpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null)
                    mListener.onClick(ICustomDialogListener.CANCEL);
                dismiss();
            }
        });

        EditText tvDesc = (EditText) findViewById(R.id.cmd_dialog_title);
        tvDesc.setText(msg);

        if (cancelListener != null) {
            setOnCancelListener(cancelListener);
        }

        mListener = lis;

        setCanceledOnTouchOutside(false);
    }

    public void setmListener(ICustomDialogListener mListener) {
        this.mListener = mListener;
    }
}
