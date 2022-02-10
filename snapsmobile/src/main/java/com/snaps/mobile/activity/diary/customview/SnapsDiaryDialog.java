package com.snaps.mobile.activity.diary.customview;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.ICustomDialogListener;
import com.snaps.common.utils.ui.IListShapeDialogListener;
import com.snaps.mobile.R;

public class SnapsDiaryDialog extends Dialog {
    private static final String TAG = SnapsDiaryDialog.class.getSimpleName();
    LinearLayout confirm, cancel;
    TextView confirmTxt, cancelTxt;

    ICustomDialogListener mListener;

    private static SnapsDiaryDialog mDialog = null;

    private static void dissmissPrevDialog() {
        if(mDialog != null && mDialog.isShowing()) {
            Context con = ( (ContextWrapper) mDialog.getContext() ).getBaseContext();
            if( con != null && con instanceof Activity ) {
                if( ((Activity) con).isFinishing() ) return;
                else if( Build.VERSION.SDK_INT > 16 && ((Activity) con).isDestroyed() ) return;
            }
            mDialog.dismiss();
        }
    }

    public static void showListSelectPopup(Activity activity, IListShapeDialogListener listener) {
        dissmissPrevDialog();
        String[] arrItems = { activity.getString(R.string.diary_modify), activity.getString(R.string.diary_delete) };

        mDialog = new SnapsDiaryDialog(activity, arrItems, listener);
        mDialog.show();
    }

    public static void showDialogWithImpect(Activity activity,
                                            int messageResId,
                                            int impectMessageResId,
                                            String confirmBtnText,
                                            String cancelBtnText,
                                            ICustomDialogListener onClick) {
        if (activity == null)
            return;

        dissmissPrevDialog();

        try {
            mDialog = new SnapsDiaryDialog(activity, messageResId, impectMessageResId, confirmBtnText, cancelBtnText, onClick);
            mDialog.show();
        } catch (WindowManager.BadTokenException e) {
            Dlog.e(TAG, e);
        }
    }

    public static void showDialogOneBtn(Activity activity,
                                        String title,
                                        String message,
                                        ICustomDialogListener onClick) {
        if (activity == null)
            return;

        dissmissPrevDialog();

        try {
            mDialog = new SnapsDiaryDialog(activity, title, message, onClick);
            mDialog.setCancelable(false);
            mDialog.show();
        } catch (WindowManager.BadTokenException e) {
            Dlog.e(TAG, e);
        }
    }

    public static void showDialogIosContentsNotice(Activity activity) {
        if (activity == null)
            return;

        dissmissPrevDialog();

        try {
            mDialog = new SnapsDiaryDialog(activity);
            mDialog.setContentView(R.layout.diary_other_os_notice_dialog);
            ( (TextView) mDialog.findViewById(R.id.diary_popup_contents_1) ).setText(String.format(activity.getString(R.string.diary_other_os_notice_popup_contents_01), activity.getString(R.string.android_os_eng)));
            ( (TextView) mDialog.findViewById(R.id.diary_popup_contents_3) ).setText(String.format(activity.getString(R.string.diary_other_os_notice_popup_contents_03), activity.getString(R.string.ios_eng)));
            ( (TextView) mDialog.findViewById(R.id.diary_popup_contents_5) ).setText(String.format(activity.getString(R.string.diary_other_os_notice_popup_contents_05), activity.getString(R.string.android_os_eng)));
            mDialog.findViewById(R.id.custom_dialog_cancel_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialog.dismiss();
                }
            });

            mDialog.setCancelable(false);
            mDialog.show();
        } catch (WindowManager.BadTokenException e) {
            Dlog.e(TAG, e);
        }
    }

    public SnapsDiaryDialog(Context context, int msg, int impectMsg, String okBtnText, String cancelBtnText, ICustomDialogListener lis) {
        super(context, R.style.TransparentProgressDialog);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        init(context, msg, impectMsg, okBtnText, cancelBtnText, lis, null);
    }

    public SnapsDiaryDialog(Context context, String msg, String subMsg, ICustomDialogListener lis) {
        super(context, R.style.TransparentProgressDialog);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        init(context, msg, subMsg, lis, null);
    }

    public SnapsDiaryDialog(Context context, String[] arrItems, IListShapeDialogListener lis) {
        super(context, R.style.TransparentProgressDialog);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        init(context, arrItems, lis);
    }

    public SnapsDiaryDialog(Context context) {
        super(context, R.style.TransparentProgressDialog);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        init(context);
    }

    private void init(Context context) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    private void init(Context context, String[] arrItems, final IListShapeDialogListener lis) {
        if (context == null)
            return;

        if (arrItems == null) return;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog_list_shape);

        getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        Button btn00 = (Button) findViewById(R.id.custom_dialog_list_shape_btn_00);
        Button btn01 = (Button) findViewById(R.id.custom_dialog_list_shape_btn_01);

        btn00.setText(arrItems[0]);
        btn00.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lis != null) lis.onClick(0);
                SnapsDiaryDialog.this.dismiss();
            }
        });

        if (arrItems.length > 1) {
            findViewById(R.id.custom_dialog_list_line).setVisibility(View.VISIBLE);
            btn01.setVisibility(View.VISIBLE);
            btn01.setText(arrItems[1]);

            btn01.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (lis != null) lis.onClick(1);
                    SnapsDiaryDialog.this.dismiss();
                }
            });
        }
    }

    private void init(Context context, int msg, int impectMsg, String okBtnText, String cancelBtnText, ICustomDialogListener lis, OnCancelListener cancelListener) {
        if (context == null)
            return;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog);

        getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        confirm = (LinearLayout) findViewById(R.id.custom_dialog_ok_btn);
        cancel = (LinearLayout) findViewById(R.id.custom_dialog_cancel_btn);

        confirmTxt = (TextView) findViewById(R.id.custom_dialog_ok_tv);
        cancelTxt = (TextView) findViewById(R.id.custom_dialog_cancel_tv);

        confirmTxt.setText(okBtnText);
        cancelTxt.setText(cancelBtnText);

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
        tvDesc.setTextColor(Color.BLACK);
        if(impectMsg != 0) {
            setSpannableAppliedText(tvDesc, context.getString(msg), context.getString(impectMsg));
        } else {
            tvDesc.setText(context.getString(msg));
        }

        if (cancelListener != null) {
            setOnCancelListener(cancelListener);
        }

        mListener = lis;
    }

    private void setSpannableAppliedText(TextView textView, String orgText, String impectText) {
        if (orgText == null || impectText == null || !orgText.contains(impectText)) return;

        int impectTextStartIdx = orgText.indexOf(impectText);

        textView.setText("");
        final SpannableStringBuilder sp = new SpannableStringBuilder(orgText);
        sp.setSpan(new ForegroundColorSpan(Color.RED), impectTextStartIdx, impectTextStartIdx + impectText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.append(sp);
    }

    private void init(Context context, String msg, String subMsg, ICustomDialogListener lis, OnCancelListener cancelListener) {
        if (context == null)
            return;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog);

        getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        confirm = (LinearLayout) findViewById(R.id.custom_dialog_ok_btn);
        cancel = (LinearLayout) findViewById(R.id.custom_dialog_cancel_btn);

        confirmTxt = (TextView) findViewById(R.id.custom_dialog_ok_tv);
        cancelTxt = (TextView) findViewById(R.id.custom_dialog_cancel_tv);
        cancelTxt.setVisibility(View.GONE);
        cancel.setVisibility(View.GONE);

        confirmTxt.setText(R.string.confirm);

        confirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mListener != null)
                    mListener.onClick(ICustomDialogListener.OK);
                dismiss();
            }
        });

        TextView tvDesc = (TextView) findViewById(R.id.custom_dialog_title);
        if (msg != null && msg.length() > 0) {
            tvDesc.setTextColor(subMsg != null && subMsg.length() > 0 ? Color.RED : Color.BLACK);
            tvDesc.setText(msg);
        }

        confirmTxt.setTextColor(Color.BLACK);

        if (subMsg != null && subMsg.length() > 0) {
            TextView tvSubDesc = (TextView) findViewById(R.id.custom_dialog_sub_title);
            tvSubDesc.setVisibility(View.VISIBLE);
            tvSubDesc.setText(subMsg);
        }

        if (cancelListener != null) {
            setOnCancelListener(cancelListener);
        }

        mListener = lis;
    }
}
