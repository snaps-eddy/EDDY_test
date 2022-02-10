package com.snaps.mobile.activity.youtube;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.R;

import errorhandle.CatchActivity;
import errorhandle.logger.SnapsLogger;
import errorhandle.logger.model.SnapsLoggerClass;

/**
 * Created by songhw on 2016. 5. 13..
 */
public class SnapsYoutubeActivity extends CatchActivity {
    public static final String VIDEO_ID_INTENT_EXTRA = "video_id_intent_extra";

    private View mContentView, mCustomView;
    private FrameLayout mCustomViewContainer;
    private WebChromeClient.CustomViewCallback mCustomViewCallback;


    private String videoId;

    private WebView myWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SnapsLogger.appendClassTrackingLog(new SnapsLoggerClass<>(this));

        videoId = getIntent().getStringExtra( VIDEO_ID_INTENT_EXTRA );
        if( StringUtil.isEmpty(videoId) ) finish();

        setContentView(R.layout.activity_youtube_player);

        myWebView = (WebView)findViewById(R.id.webview);

        SnapsYoutubeWebView mWebChromeClient = new SnapsYoutubeWebView();
        myWebView.setWebChromeClient(mWebChromeClient);
        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });

        WebSettings webSettings = myWebView.getSettings();
        webSettings.setPluginState(WebSettings.PluginState.ON);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setJavaScriptEnabled(true);

        myWebView.loadUrl("https://www.youtube.com/embed/" + videoId);
    }

    /**
     * Created by songhw on 2016. 5. 13..
     */
    public class SnapsYoutubeWebView extends WebChromeClient {
        private FrameLayout.LayoutParams LayoutParameters = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            if (mContentView != null) {
                callback.onCustomViewHidden();
                return;
            }
            mContentView = findViewById(R.id.content_view);
            mContentView.setVisibility(View.GONE);
            mCustomViewContainer = new FrameLayout( SnapsYoutubeActivity.this );
            mCustomViewContainer.setLayoutParams(LayoutParameters);
            mCustomViewContainer.setBackgroundResource(android.R.color.black);
            view.setLayoutParams(LayoutParameters);
            mCustomViewContainer.addView(view);
            mCustomView = view;
            mCustomViewCallback = callback;
            mCustomViewContainer.setVisibility(View.VISIBLE);
            setContentView(mCustomViewContainer);
        }

        @Override
        public void onHideCustomView() {
            if (mCustomView == null) {
                return;
            } else {
                mCustomView.setVisibility(View.GONE);
                mCustomViewContainer.removeView(mCustomView);
                mCustomView = null;
                mCustomViewContainer.setVisibility(View.GONE);
                mCustomViewCallback.onCustomViewHidden();
                mContentView.setVisibility(View.VISIBLE);
                setContentView(mContentView);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if( myWebView != null ) {
            myWebView.resumeTimers();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if( myWebView != null ) {
            myWebView.pauseTimers();
        }
    }

    @Override
    protected void onDestroy() {
        if( myWebView != null ) {
            myWebView.destroy();
            myWebView = null;
        }

        super.onDestroy();
    }
}
