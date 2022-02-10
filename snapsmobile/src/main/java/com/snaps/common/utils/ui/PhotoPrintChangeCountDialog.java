package com.snaps.common.utils.ui;

import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.R;

import font.FTextView;

public class PhotoPrintChangeCountDialog extends Dialog {
    private static final String TAG = PhotoPrintChangeCountDialog.class.getSimpleName();

    public interface IPhotoPrintChangeCountDialogListener {
        public static byte CANCEL = 0;
        public static byte OK = 1;
        void onClick(byte clickedOk, int count);
    }

    private IPhotoPrintChangeCountDialogListener mListener;

    private EditText mEtCount;
    private Context mContext;

    public PhotoPrintChangeCountDialog(Context context) {
        super(context, R.style.TransparentProgressDialog);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        init();
        mContext = context;
    }

    public void setListener(IPhotoPrintChangeCountDialogListener mListener) {
        this.mListener = mListener;
    }

    public void showDialog(int defaultCount) {
        if (mEtCount == null) return;
        mEtCount.setText(defaultCount + "");
        mEtCount.selectAll();

        mEtCount.postDelayed(new Runnable() {

            @Override
            public void run() {
                try {
                    InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(mEtCount, 0);
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                }
            }
        }, 200);

        show();
    }

    public void init() {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.photo_print_change_count_dialog);

        ImageView ivClose = (ImageView)findViewById(R.id.photo_print_change_count_dialog_close_iv);
        mEtCount = (EditText)findViewById(R.id.photo_print_change_count_dialog_count_et);
        FTextView tvApply = (FTextView)findViewById(R.id.photo_print_change_count_dialog_apply_tv);

        mEtCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {}

            @Override
            public void afterTextChanged(Editable s) {
                if(s == null || s.toString() == null) return;
                if(s.toString().startsWith("0") || s.toString().trim().length() < 1) {
                    mEtCount.setText("1");
                    mEtCount.selectAll();
                }
            }
        });

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mContext != null && mEtCount != null)
                    UIUtil.hideKeyboard(mContext, mEtCount);

                if (mListener != null){
                    mListener.onClick(ICustomDialogListener.CANCEL, 0);
                    dismiss();
                }
            }
        });

        tvApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mContext != null && mEtCount != null)
                    UIUtil.hideKeyboard(mContext, mEtCount);

                int result = 1;
                if (mEtCount != null) {
                    String count = mEtCount.getText().toString();
                    try {
                        result = Math.max(1, Integer.parseInt(count));
                    } catch (NumberFormatException e) { Dlog.e(TAG, e); }
                }

                if (mListener != null){
                    mListener.onClick(ICustomDialogListener.OK, result);
                    dismiss();
                }
            }
        });
    }
}
