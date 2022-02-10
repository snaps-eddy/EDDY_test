package com.snaps.mobile.activity.webview;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.appsflyer.AppsFlyerLib;
import com.snaps.common.trackers.SnapsAppsFlyer;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.pref.Setting;
import com.snaps.common.utils.system.SystemUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.SnapsBaseActivity;
import com.snaps.mobile.activity.home.RenewalHomeActivity;
import com.snaps.mobile.activity.ui.menu.webinterface.SnapsShouldOverrideUrlLoader;
import com.snaps.mobile.component.ObserveScrollingWebView;
import com.snaps.mobile.utils.kakao.KakaoStoryPostingEventor;
import com.snaps.mobile.utils.thirdparty.SnapsTPAppManager;

import errorhandle.logger.Logg;
import errorhandle.logger.SnapsLogger;
import errorhandle.logger.model.SnapsLoggerClass;

public class PaymentResultActivity extends SnapsBaseActivity {
	private static final String TAG = PaymentResultActivity.class.getSimpleName();

	ObserveScrollingWebView webview;
	TextView mBtnEdit;
	TextView mBtnComplete;
	TextView mTxtTitleText;
	ImageView mImageHome;
	KakaoStoryPostingEventor eventor = null;
	SnapsShouldOverrideUrlLoader snapsShouldOverrideUrlLoder = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		SnapsLogger.appendClassTrackingLog(new SnapsLoggerClass<>(this));
		SnapsAppsFlyer.setDeepLink(this);
		setContentView(R.layout.activity_uiwebview_);
		eventor = new KakaoStoryPostingEventor(this);

		mBtnEdit = (TextView) findViewById(R.id.btnTopEdit);
		mBtnComplete = (TextView) findViewById(R.id.btnTopEditComplete);

		mTxtTitleText = (TextView) findViewById(R.id.txtTitleText);
		mImageHome = (ImageView) findViewById(R.id.btnTitleLeft);
		mImageHome.setVisibility(View.GONE);

		snapsShouldOverrideUrlLoder = new SnapsShouldOverrideUrlLoader(PaymentResultActivity.this, SnapsShouldOverrideUrlLoader.WEB);

		try {
			mTxtTitleText.setText(getString(R.string.complete_payment));//"결제완료");
			mBtnEdit.setVisibility(View.GONE);
			mBtnComplete.setVisibility(View.VISIBLE);

			String url = "";

			webview = (com.snaps.mobile.component.ObserveScrollingWebView) findViewById(R.id.webview);
			webview.getSettings().setJavaScriptEnabled(true);
			webview.getSettings().setPluginState(PluginState.ON);

			// 웹페이지를 화면사이즈에 맞춤.
			webview.getSettings().setLoadWithOverviewMode(true);
			webview.getSettings().setUseWideViewPort(true);
			// 줌컨트롤추가
			webview.getSettings().setBuiltInZoomControls(false);

			webview.setWebViewClient(new WebViewClientClass());
			webview.setWebChromeClient(new WebChromeClientClass());
			webview.setLongClickable(false);
			webview.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					// TODO Auto-generated method stub
					return true;
				}
			});

			// 캐쉬를 하지 않는다..
			webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

			if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT_WATCH) {
				webview.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
			}

			if (getIntent() != null && getIntent().getDataString() != null && getIntent().getDataString().startsWith(Config.getPaymentScheme(getApplicationContext()))) {
				try {
					Dlog.d("onCreate() date:" + getIntent().getDataString());
					// 이니시스 ISP : success, fail
					if (getIntent().getData() != null) {
						String status = getIntent().getData().getQueryParameter("status");
						boolean result = "success".equals(status);
						url = createResultUrl(result, "");
						mTxtTitleText.setText(getString(R.string.order_complete));//"주문완료");

					}
				} catch (Exception e) {
					Dlog.e(TAG, e);
				}
			}

			webview.loadUrl(url, SystemUtil.getWebviewVersionMapData(this));

		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
		setDeepLinking();
	}

	private void setDeepLinking(){
		try {
			AppsFlyerLib.getInstance().sendDeepLinkData(this);
		}catch (Exception e){
			Dlog.e(TAG, e);
		}
	}
	String createResultUrl(boolean result, String resultCode) {
		String orderCode = Setting.getString(getApplicationContext(), "ordercode", "");
		String url = "";

		// 채널코드 uuserid
		url = SnapsTPAppManager.getPaymentResultUrl(this, result, orderCode);
		Dlog.d("createResultUrl() url:" + url);
		return url;
	}

	@Override
	public void onBackPressed() {
		finishActivity();
	}

	@Override
	protected void onDestroy() {
		if (eventor != null) {
			eventor.release();
			eventor = null;
		}

		if( webview != null ) {
			webview.destroy();
		}

		super.onDestroy();
	}

	final String WEBCMD_SCHMA = "snapsapp://";

	class WebViewClientClass extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (snapsShouldOverrideUrlLoder != null)
				snapsShouldOverrideUrlLoder.shouldOverrideUrlLoading(url);

			return true;
		}

		@Override
		public void onPageFinished(WebView view, String url) {
		}
	}

	class WebChromeClientClass extends WebChromeClient {
		@Override
		public void onProgressChanged(WebView view, int newProgress) {
		}

	}

	public void onClick(View v) {
		if (v.getId() == R.id.btnTopEditComplete) {
			finishActivity();
		}
	}

	void finishActivity() {

		//배지 카운터를 서버에서 조회하도록 요청한다.
		Setting.set(this, Const_VALUE.KEY_SHOULD_REQUEST_BADGE_COUNT, true);

		Intent intent = new Intent(this, RenewalHomeActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		finish();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if( webview != null ) {
			webview.resumeTimers();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if( webview != null ) {
			webview.pauseTimers();
		}
	}
}
