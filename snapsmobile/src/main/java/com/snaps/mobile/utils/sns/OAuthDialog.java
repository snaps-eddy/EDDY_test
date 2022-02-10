package com.snaps.mobile.utils.sns;

import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.R;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class OAuthDialog extends DialogFragment {
	private static final String TAG = OAuthDialog.class.getSimpleName();
	String OAUTH_URL,REDIRECT_URI,CLIENT_ID,OAUTH_SCOPE;

	public OAuthDialog(String oauthUrl, String redirectUrl,String clientId,String scope){
		OAUTH_URL = oauthUrl;
		REDIRECT_URI = redirectUrl;
		CLIENT_ID = clientId;
		OAUTH_SCOPE = scope;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.auth_dialog, container);
		WebView web = (WebView) view.findViewById(R.id.webv);
		web.getSettings().setJavaScriptEnabled(true);
		web.getSettings().setAppCacheEnabled(false);
		web.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		web.loadUrl(OAUTH_URL + "?redirect_uri=" + REDIRECT_URI + "&response_type=code&client_id=" + CLIENT_ID + "&scope=" + OAUTH_SCOPE);
		web.setWebViewClient(new WebViewClient() {

			boolean authComplete = false;
			Intent resultIntent = new Intent();

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);

			}

			String authCode;

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);

				if (url.contains("?code=") && authComplete != true) {
					Uri uri = Uri.parse(url);
					authCode = uri.getQueryParameter("code");
					Dlog.i(TAG, "onPageFinished() CODE:" + authCode);
					authComplete = true;
					resultIntent.putExtra("code", authCode);
					dismiss();
				} else if (url.contains("error=access_denied")) {
					Dlog.i(TAG, "onPageFinished() ACCESS_DENIED_HERE");
					resultIntent.putExtra("code", authCode);
					authComplete = true;
					dismiss();
				}
			}

		});
		
		return view;
	}

}
