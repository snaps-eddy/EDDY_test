package com.snaps.instagram.utils.instagram;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.snaps.common.utils.log.Dlog;


public class InstagramDialog extends Dialog {
	static final float[] DIMENSIONS_LANDSCAPE = { 460, 260 };
	static final float[] DIMENSIONS_PORTRAIT = { 280, 420 };
	static final FrameLayout.LayoutParams FILL = new FrameLayout.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
	static final int MARGIN = 4;
	static final int PADDING = 2;
	private String mUrl;
	private OAuthDialogListener mListener;
	private ProgressDialog mSpinner;
	private WebView mWebView;
	private LinearLayout mContent;
	private TextView mTitle;
	private static final String TAG = "Instagram-WebView";
	private InstagramSession mSession;
	
	public InstagramDialog(Context context, String url, OAuthDialogListener listener) {
		super(context);
		mUrl = url;
		mListener = listener;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mSession = new InstagramSession(getContext());
		mSpinner = new ProgressDialog(getContext());
		mSpinner.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mSpinner.setMessage("Loading...");
		mContent = new LinearLayout(getContext());
		mContent.setOrientation(LinearLayout.VERTICAL);
		setUpTitle();
		setUpWebView();
		Display display = getWindow().getWindowManager().getDefaultDisplay();
		final float scale = getContext().getResources().getDisplayMetrics().density;
		float[] dimensions = (display.getWidth() < display.getHeight()) ? DIMENSIONS_PORTRAIT : DIMENSIONS_LANDSCAPE;
//		addContentView(mContent, new FrameLayout.LayoutParams( (int) (dimensions[0] * scale + 0.5f), (int) (dimensions[1] * scale + 0.5f)));
		addContentView(mContent, new FrameLayout.LayoutParams(display.getWidth() - (int) (scale * 5f), display.getHeight() - (int) (scale * 5f)));

//		CookieSyncManager.createInstance(getContext());
//		CookieManager cookieManager = CookieManager.getInstance();
//		cookieManager.removeAllCookie();
	} 
	
	private void setUpTitle() { 
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mTitle = new TextView(getContext());
		mTitle.setText("Instagram");
		mTitle.setTextColor(Color.WHITE);
		mTitle.setTypeface(Typeface.DEFAULT_BOLD);
		mTitle.setBackgroundColor(Color.BLACK);
		mTitle.setPadding(MARGIN + PADDING, MARGIN, MARGIN, MARGIN); mContent.addView(mTitle);
	}
	
	@SuppressLint("SetJavaScriptEnabled")
	private void setUpWebView() { 
		mWebView = new WebView(getContext());
		mWebView.setVerticalScrollBarEnabled(false);
		mWebView.setHorizontalScrollBarEnabled(false);
		mWebView.setWebViewClient(new OAuthWebViewClient());
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setDomStorageEnabled(true);
		mWebView.loadUrl(mUrl);
		mWebView.setLayoutParams(FILL);
		mContent.addView(mWebView);
	} 
	
	private class OAuthWebViewClient extends WebViewClient { 
		@Override public boolean shouldOverrideUrlLoading(WebView view, String url) { 
			Dlog.d("shouldOverrideUrlLoading() Redirecting URL " + url);
			if ( url.startsWith(InstagramApp.mCallbackUrl) ) {
				String urls[] = url.split("=");
				mListener.onComplete(urls[1]);
				InstagramDialog.this.dismiss();
				return true;
			} else if( null != mSession.getLoginPath() && url.equals(mSession.getLoginPath()) ) {
				view.loadUrl(mUrl);
				return true;
			}
			return false;
		} 
		
		@Override 
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) { 
			Dlog.e(TAG, "onReceivedError() Page error: " + description);
			super.onReceivedError(view, errorCode, description, failingUrl);
			mListener.onError(description); InstagramDialog.this.dismiss();
		}

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Dlog.d("onPageStarted() Loading URL: " + url);
            super.onPageStarted(view, url, favicon);
            if( mSpinner != null && mSpinner.getOwnerActivity() != null
                    && !mSpinner.getOwnerActivity().isFinishing() ) {
//				Context con = ( (ContextWrapper) mSpinner.getContext() ).getBaseContext();
//                if( con != null && )
//				if( con != null && con instanceof Activity && (((Activity) con).isFinishing() || ((Activity) con).isDestroyed()) ) return;
                if( Build.VERSION.SDK_INT >= 17 && mSpinner.getOwnerActivity().isDestroyed() );
                else
                    mSpinner.show();
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished( view, url );
//            super.onPageFinished(
            String title = mWebView.getTitle();
            if (title != null && title.length() > 0) {
                mTitle.setText(title);
                if( mSpinner != null && mSpinner.isShowing() && mSpinner.getOwnerActivity() != null
                        && !mSpinner.getOwnerActivity().isFinishing() ) {
//				if( mSpinner != null && mSpinner.isShowing() ) {
//					Context con = ( (ContextWrapper) mSpinner.getContext() ).getBaseContext();
//					if( con != null && con instanceof Activity && (((Activity) con).isFinishing() || ((Activity) con).isDestroyed()) ) return;
                    if( Build.VERSION.SDK_INT >= 17 && mSpinner.getOwnerActivity().isDestroyed() );
                    else
                        mSpinner.dismiss();
                }
            }
        }
	}
}
		

