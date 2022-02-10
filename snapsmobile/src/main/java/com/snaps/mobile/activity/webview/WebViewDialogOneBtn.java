package com.snaps.mobile.activity.webview;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.SpannableString;
import android.view.View;
import android.view.Window;
import android.webkit.JsResult;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.snaps.mobile.R;

public class WebViewDialogOneBtn extends Dialog implements android.view.View.OnClickListener {

    private TextView WebContents;

    private TextView mTitleView;

    private LinearLayout mBtnAgree;

    private String mTitle = null;
    private String mContent = null;
    private SpannableString mSpanContent = null;
    private JsResult mResult = null;

    public WebViewDialogOneBtn(Context context, String title, String content) {
        super(context);
        this.mTitle = title;
        this.mContent = content;
    }

    public WebViewDialogOneBtn(Context context, String title, SpannableString content) {
        super(context);
        this.mTitle = title;
        this.mSpanContent = content;
    }

    public WebViewDialogOneBtn(Context context, String content, JsResult result) {
        super(context);
        this.mContent = content;
        this.mResult = result;
    }

    public WebViewDialogOneBtn(Context context, String content) {
        super(context);
        this.mContent = content;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_custom_webview_one_btn);

        WebContents = (TextView) findViewById(R.id.webMessage);

        mBtnAgree = (LinearLayout) findViewById(R.id.btnDelAgree);

        setWebMessage(mContent);

        mBtnAgree.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (mResult != null)
                    mResult.confirm();
                dismiss();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mResult != null)
            mResult.confirm();
        dismiss();
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {

    }

    void setWebMessage(String content) {

        if (mSpanContent != null) {
            WebContents.setText(mSpanContent);
            return;
        }

        WebContents.setText(mContent);
    }

}
