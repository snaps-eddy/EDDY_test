package com.snaps.common.utils.ui;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.snaps.mobile.R;

public class CustomizeDialog extends Dialog {

    LinearLayout confirm, cancel;
    TextView confirmTxt, cancelTxt;

    ICustomDialogListener mListener;

    public CustomizeDialog(Context context) {
        super(context, R.style.TransparentProgressDialog);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    public CustomizeDialog(Context context, String msg, ICustomDialogListener lis) {
        super(context, R.style.TransparentProgressDialog);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        init(context, msg, null, R.string.confirm, R.string.cancel, lis, null);
    }

    public CustomizeDialog(Context context, String msg, int okbtnStrResId, int cancelbtnResId, ICustomDialogListener lis) {
        super(context, R.style.TransparentProgressDialog);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        init(context, msg, null, okbtnStrResId, cancelbtnResId, lis, null);
    }

    public CustomizeDialog(Context context, String msg, ICustomDialogListener lis, OnCancelListener cancelListenr) {
        super(context, R.style.TransparentProgressDialog);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        init(context, msg, null, R.string.confirm, R.string.cancel, lis, cancelListenr);
    }

    public CustomizeDialog(Context context, String msg, int okbtnStrResId, int cancelbtnResId, ICustomDialogListener lis, OnCancelListener cancelListenr) {
        super(context, R.style.TransparentProgressDialog);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        init(context, msg, null, okbtnStrResId, cancelbtnResId, lis, cancelListenr);
    }

    public CustomizeDialog(Context context, String msg, String subMsg, ICustomDialogListener lis) {
        super(context, R.style.TransparentProgressDialog);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        init(context, msg, subMsg, R.string.confirm, R.string.cancel, lis, null);
    }

    public CustomizeDialog(Context context, String msg, String subMsg, int okbtnStrResId, int cancelbtnResId, ICustomDialogListener lis) {
        super(context, R.style.TransparentProgressDialog);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        init(context, msg, subMsg, okbtnStrResId, cancelbtnResId, lis, null);
    }

    public CustomizeDialog(Context context, String msg, String subMsg, int okbtnStrResId, int cancelbtnResId, ICustomDialogListener lis, boolean isInit2) {
        super(context, R.style.TransparentProgressDialog);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (isInit2)
            init2(context, msg, subMsg, okbtnStrResId, cancelbtnResId, lis, null);
        else
            init(context, msg, subMsg, okbtnStrResId, cancelbtnResId, lis, null);
    }

    public CustomizeDialog(Context context, String msg, String subMsg, ICustomDialogListener lis, OnCancelListener cancelListenr) {
        super(context, R.style.TransparentProgressDialog);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        init(context, msg, subMsg, R.string.confirm, R.string.cancel, lis, cancelListenr);
    }

    public CustomizeDialog(Context context, String msg, String subMsg, int okbtnStrResId, int cancelbtnResId, ICustomDialogListener lis, OnCancelListener cancelListenr) {
        super(context, R.style.TransparentProgressDialog);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        init(context, msg, subMsg, okbtnStrResId, cancelbtnResId, lis, cancelListenr);
    }

    public void setmListener(ICustomDialogListener mListener) {
        this.mListener = mListener;
    }

    public void setOneBtnStyle() {
        if (cancel != null)
            cancel.setVisibility(View.GONE);
    }

    public void setOneBtnStyle(Context context, int okbtnStrResId) {
        if (cancel != null)
            cancel.setVisibility(View.GONE);

        if (confirmTxt != null && context != null) {
            confirmTxt.setText(context.getString(okbtnStrResId));
        }
    }

    private void init2(Context context, String msg, String subMsg, int okbtnStrResId, int cancelbtnResId, ICustomDialogListener lis, OnCancelListener cancelListener) {
        if (context == null)
            return;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog2);

        getWindow().setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        confirm = (LinearLayout) findViewById(R.id.llLike);
        TextView tvCancel = (TextView) findViewById(R.id.tvCancel);

        tvCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mListener != null)
                    mListener.onClick(ICustomDialogListener.CANCEL);
                dismiss();

            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mListener != null)
                    mListener.onClick(ICustomDialogListener.OK);
                dismiss();
            }
        });

        TextView tvDesc = (TextView) findViewById(R.id.custom_dialog_title);
        tvDesc.setText(msg);

        if (subMsg != null && subMsg.length() > 0) {
            TextView tvSubDesc = (TextView) findViewById(R.id.custom_dialog_sub_title);
            tvSubDesc.setVisibility(View.VISIBLE);
            tvSubDesc.setText(subMsg);
        }

        if (cancelListener != null) {
            setOnCancelListener(cancelListener);
        }

        mListener = lis;

        setCanceledOnTouchOutside(false);
    }

    private void init(Context context, String msg, String subMsg, int okbtnStrResId, int cancelbtnResId, ICustomDialogListener lis, OnCancelListener cancelListener) {
        if (context == null)
            return;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog);

        getWindow().setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        confirm = (LinearLayout) findViewById(R.id.custom_dialog_ok_btn);
        cancel = (LinearLayout) findViewById(R.id.custom_dialog_cancel_btn);

        confirmTxt = (TextView) findViewById(R.id.custom_dialog_ok_tv);
        cancelTxt = (TextView) findViewById(R.id.custom_dialog_cancel_tv);

        if (okbtnStrResId > 0)
            confirmTxt.setText(context.getString(okbtnStrResId));

        if (cancelbtnResId > 0)
            cancelTxt.setText(context.getString(cancelbtnResId));

        confirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mListener != null)
                    mListener.onClick(ICustomDialogListener.OK);
                dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mListener != null)
                    mListener.onClick(ICustomDialogListener.CANCEL);
                dismiss();

            }
        });

        TextView tvDesc = (TextView) findViewById(R.id.custom_dialog_title);
        tvDesc.setText(msg);

        if (subMsg != null && subMsg.length() > 0) {
            TextView tvSubDesc = (TextView) findViewById(R.id.custom_dialog_sub_title);
            tvSubDesc.setVisibility(View.VISIBLE);
            tvSubDesc.setText(subMsg);
        }

        if (cancelListener != null) {
            setOnCancelListener(cancelListener);
        }

        mListener = lis;

        setCanceledOnTouchOutside(false);
    }

    public void showSnsAlert() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_sns_alert_dialog);

        confirm = (LinearLayout) findViewById(R.id.custom_dialog_ok_btn);

        confirmTxt = (TextView) findViewById(R.id.custom_dialog_ok_tv);

        confirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mListener != null)
                    mListener.onClick(ICustomDialogListener.OK);
                dismiss();
            }
        });

        show();
    }

    public static void showCustomOneBtnDialog(Context context, int layoutResId, final ICustomDialogListener lis) throws Exception {
        final CustomizeDialog customizeDialog = new CustomizeDialog(context);

        customizeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customizeDialog.setContentView(layoutResId);

        View view = customizeDialog.findViewById(R.id.custom_dialog_confirm_btn);
        if (view != null) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (lis != null) {
                        lis.onClick(ICustomDialogListener.OK);
                        customizeDialog.dismiss();
                    }
                }
            });
        }

        customizeDialog.setCanceledOnTouchOutside(false);
        customizeDialog.setCancelable(false);
        customizeDialog.show();
    }
}
