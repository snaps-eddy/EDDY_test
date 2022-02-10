package com.snaps.mobile.activity.webview;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.JsResult;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.snaps.mobile.R;

public class WebViewDialog extends Dialog implements android.view.View.OnClickListener {

    private TextView WebContents;

    private TextView mTitleView;

    private LinearLayout mBtnCancel;
    private LinearLayout mBtnAgree;

    private String mTitle;
    private String mContent;
    private JsResult mResult;

    public WebViewDialog(Context context) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        // TODO Auto-generated constructor stub
    }

    public WebViewDialog(Context context, String title, String content) {
        super(context);
        this.mTitle = title;
        this.mContent = content;
    }

    public WebViewDialog(Context context, String content, JsResult result) {
        super(context);
        this.mContent = content;
        this.mResult = result;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_custom_webview);

        WebContents = (TextView) findViewById(R.id.webMessage);

        mBtnCancel = (LinearLayout) findViewById(R.id.btnDelCancel);
        mBtnAgree = (LinearLayout) findViewById(R.id.btnDelOk);

        setWebMessage(mContent);

        mBtnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                mResult.cancel();
                dismiss();

            }
        });

        mBtnAgree.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                mResult.confirm();
                dismiss();
            }
        });
    }

    @Override
    public void onBackPressed() {
        mResult.cancel();
        dismiss();
        super.onBackPressed();
    }



    @Override
    public void onClick(View v) {

    }

    void setWebMessage(String content) {
        WebContents.setText(content);
    }

}
