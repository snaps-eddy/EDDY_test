package com.snaps.mobile.activity.webview;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.WindowManager;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.snaps.common.utils.constant.Const_EKEY;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.file.FileUtil;
import errorhandle.logger.Logg;

import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.pref.Setting;
import com.snaps.common.utils.system.SystemUtil;
import com.snaps.common.utils.thread.ATask;
import com.snaps.common.utils.thread.ATask.OnTask;
import com.snaps.common.utils.ui.FontUtil;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.common.utils.ui.SingleTabWebViewController;
import com.snaps.common.utils.ui.UI;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.ui.menu.webinterface.SnapsShouldOverrideUrlLoader;
import com.snaps.mobile.interfaces.OnFileUploadListener;
import com.snaps.mobile.utils.thirdparty.SnapsTPAppManager;

import java.util.HashMap;

import errorhandle.CatchFragmentActivity;
import errorhandle.logger.SnapsLogger;
import errorhandle.logger.model.SnapsLoggerClass;

public class WebviewActivity extends CatchFragmentActivity {
	private static final String TAG = WebviewActivity.class.getSimpleName();
	
	public static final int REQUEST_CODE_OPEN_GALLERY = 10001;
	
	private final static String KEY_USE_TITLE_SCROLL = "key use title scroll";
	
	com.snaps.mobile.component.ObserveScrollingWebView webview;
	private ProgressBar progressBar;
	String fileAttachCallBack = "";

	// 뒤로가기가 가능하지 여부 설정.. false:뒤로가기 없음.. true이면 뒤로가기 있음..
	boolean isEnableBack = false;

	SnapsWebViewReceiver receiver = null;
	String reLoadUrl = null;
	
	String urlType = null;

	SnapsShouldOverrideUrlLoader snapsShouldOverrideUrlLoder = null;
	
	public static Intent getIntent(Context context, String title, String url) {
		Intent intent = new Intent(context, WebviewActivity.class);
		intent.putExtra(Const_EKEY.WEBVIEW_TITLE, title);
		intent.putExtra(Const_EKEY.WEBVIEW_URL, url);
		intent.putExtra( KEY_USE_TITLE_SCROLL, true );
		return intent;
	}
	
	public static Intent getIntent( Context context, String title, String url, boolean useTitleScroll ) {
		Intent intent = new Intent(context, WebviewActivity.class);
		intent.putExtra(Const_EKEY.WEBVIEW_TITLE, title);
		intent.putExtra(Const_EKEY.WEBVIEW_URL, url);
		intent.putExtra( KEY_USE_TITLE_SCROLL, useTitleScroll );
		return intent;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
			getWindow().setFlags(
					WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
					WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
		}

		super.onCreate(savedInstanceState);

		SnapsLogger.appendClassTrackingLog(new SnapsLoggerClass<>(this));

		setContentView(R.layout.activity_webview_);

		ImageView mImageHome = (ImageView) findViewById(R.id.btnTitleLeft);

		snapsShouldOverrideUrlLoder = new SnapsShouldOverrideUrlLoader(WebviewActivity.this, SnapsShouldOverrideUrlLoader.WEB);

		if(SnapsTPAppManager.isThirdPartyApp(this))
			SnapsTPAppManager.setImageResource(mImageHome, R.drawable.btn_prev);

		try {
			String title = getIntent().getStringExtra(Const_EKEY.WEBVIEW_TITLE);
			String url = getIntent().getStringExtra(Const_EKEY.WEBVIEW_URL);
			boolean useTitleScroll = getIntent().getBooleanExtra( KEY_USE_TITLE_SCROLL, true );
			
			String webIndex = getIntent().getExtras().getString("detailindex");

			// detailindex값이 없는경우 배송조회가 된다.
			if (webIndex != null && !webIndex.equals("")) {
				url += "&seq=" + webIndex;
			}

			Dlog.d("onCreate() title:" + title + ", url:" + url);

			urlType = getIntent().getStringExtra("urlType");
			
			isEnableBack = getIntent().getBooleanExtra("isEnableBack", false);

			if (isEnableBack) {
				ImageView leftBtn = (ImageView) findViewById(R.id.btnTitleLeft);
				leftBtn.setImageResource(R.drawable.btn_prev);
			}

			// title
			TextView tvTitleText = UI.<TextView> findViewById(this, R.id.txtTitleText);
			FontUtil.applyTextViewTypeface(tvTitleText, FontUtil.eSnapsFonts.YOON_GOTHIC_760);
			tvTitleText.setText(title);

			webview = (com.snaps.mobile.component.ObserveScrollingWebView) findViewById(R.id.webview);
			webview.getSettings().setJavaScriptEnabled(true);
			webview.getSettings().setPluginState(PluginState.ON);

			// 웹페이지를 화면사이즈에 맞춤.
			webview.getSettings().setLoadWithOverviewMode(true);
			webview.getSettings().setUseWideViewPort(true);
			// 줌컨트롤추가
			webview.getSettings().setBuiltInZoomControls(true);
			if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT_WATCH) {
				webview.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
			}

			// 캐쉬를 하지 않는다..
			webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
			webview.setLongClickable(false);
			webview.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					// TODO Auto-generated method stub
					return true;
				}
			});
			
			IntentFilter filter = new IntentFilter(Const_VALUE.RELOAD_URL);
			receiver = new SnapsWebViewReceiver();
			registerReceiver(receiver, filter);
			
			if( useTitleScroll ) {
				SingleTabWebViewController wvController = new SingleTabWebViewController();
				wvController.setView( (RelativeLayout)findViewById(R.id.title_layout) );
				wvController.setWebView( webview );
			}
			
			Resources res = getResources();

			progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
			progressBar.setBackgroundColor(res.getColor(android.R.color.transparent));
			progressBar.setMax(100);
			progressBar.setProgressDrawable(res.getDrawable(R.drawable.progressbar_webview));
			RelativeLayout.LayoutParams rParams = new RelativeLayout.LayoutParams( RelativeLayout.LayoutParams.MATCH_PARENT, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, res.getDisplayMetrics()) );
			rParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			progressBar.setLayoutParams(rParams);
			progressBar.setVisibility(View.GONE);

			( (RelativeLayout) findViewById(R.id.root_layout) ).addView( progressBar );
			
			webview.setWebViewClient(new WebViewClientClass());
			webview.setWebChromeClient(new WebChromeClientClass());

			webview.loadUrl( url, SystemUtil.getWebviewVersionMapData(WebviewActivity.this) );

		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//파일 첨부에서 선택한 사진을 서버로 전송한다.
		if(requestCode == REQUEST_CODE_OPEN_GALLERY) {
			if(data != null) {
				Uri uri = data.getData();
				FileUtil.sendAttachFile(this, uri, new OnFileUploadListener() {
					@Override
					public void onFinished(String url) {
						if(url == null || url.length() < 1) {
							MessageUtil.toast(WebviewActivity.this, getString(R.string.failed_upload_plz_retry));
							return;
						}
						
						String cmd = getFileAttachCMD(url);
						if(cmd != null && webview != null)
							webview.loadUrl(cmd);
					}
				});
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		
		unregisterReceiver(receiver);
		receiver = null;
		
		if (webview != null) {
			webview.postDelayed(new Runnable() {
				@Override
				public void run() {
					try {
						webview.destroy();
					} catch (Exception ex) {
						Dlog.e(TAG, ex);
					}
				}
			}, 3000);
		}
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    switch (keyCode) {
	    case KeyEvent.KEYCODE_BACK:
	    	if(urlType != null && urlType.equals(Const_EKEY.URL_TYPE_DELIVERY)) {
	    		return super.onKeyDown(keyCode, event);
	    	}
	    	
	    	if (!isEnableBack) {
				if (webview != null && webview.canGoBack()) {
					webview.goBack();
				} else 			
					goToHome();
				return true;
			}
	    }
	    return super.onKeyDown(keyCode, event);
	}

	void goToHome() {
		finish();
	}

	public void onClick(View v) {
		if (v.getId() == R.id.btnTitleLeft || v.getId() == R.id.btnTitleLeftLy) {
			
			if(urlType != null && urlType.equals(Const_EKEY.URL_TYPE_DELIVERY)) {
				finish();
				return;
	    	}
			
			if (webview != null && webview.canGoBack()) {
				webview.goBack();
			} else {
				if (!isEnableBack)
					goToHome();
				else
					finish();
			}
		}
	}

	class WebViewClientClass extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (snapsShouldOverrideUrlLoder != null)
				snapsShouldOverrideUrlLoder.shouldOverrideUrlLoading(webview, url);
			return true;
		}

		// 페이지 시작
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			if (progressBar != null) {
				progressBar.setVisibility(View.VISIBLE);
				progressBar.setProgress(0);
			}
		}

		// 페이지 로딩시
		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
			try {
				MessageUtil.toast(WebviewActivity.this, R.string.loading_fail);
			} catch (Exception e) {
				Dlog.e(TAG, e);
			}
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			// TODO Auto-generated method stub

			if (url.startsWith("http://trace.epost.go.kr"))
				view.loadUrl("javascript:document.body.style.zoom = 2.3", SystemUtil.getWebviewVersionMapData(WebviewActivity.this));
		}
	}
	
	/**
	 * 첨부파일 전송을 위해 겔러리를 열어 줌.
	 */
	private void openGallery(HashMap<String, String> urlData) {
		fileAttachCallBack = urlData.get("callback");
		
		FileUtil.callGallery(WebviewActivity.this);
	}
	
	public String getFileAttachCMD(String url) {
		if(fileAttachCallBack == null || fileAttachCallBack.length() < 1) return null;
		return String.format("javascript:%s(\"%s\")", fileAttachCallBack, url);
	}

	class WebChromeClientClass extends WebChromeClient {
		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			if (progressBar == null)
				return;
			try {
				progressBar.setProgress(newProgress);
				if (newProgress == 100) {// 페이지 종료
					ATask.executeVoid(new OnTask() {

						@Override
						public void onPre() {

						}

						@Override
						public void onPost() {
							progressBar.setVisibility(View.GONE);

						}

						@Override
						public void onBG() {
							try {
								Thread.sleep(500);
							} catch (InterruptedException e) {
								Dlog.e(TAG, e);
							}
						}
					});
				}
			} catch (Exception e) {
				Dlog.e(TAG, e);
			}
		}

		@Override
		public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
			// TODO Auto-generated method stub

			WebViewDialogOneBtn wdia = new WebViewDialogOneBtn(WebviewActivity.this, message, result);
			wdia.setCancelable(false);
			wdia.show();

			return true;

		}

		@Override
		public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {

			WebViewDialog wdia = new WebViewDialog(WebviewActivity.this, message, result);
			wdia.setCancelable(false);
			wdia.show();

			return true;
		}
	}
	
	class SnapsWebViewReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			try {
				final String action = intent.getAction();

				if (action != null && TextUtils.equals(action, Const_VALUE.RELOAD_URL)) {
					reLoadUrl = intent.getStringExtra("reloadurl");

					String userNo = Setting.getString(context, Const_VALUE.KEY_SNAPS_USER_NO);
					String loadurl = reLoadUrl;// == null ? "/mw/event/friend_invite.jsp" : reLoadUrl;
					String url = SnapsAPI.WEB_DOMAIN(loadurl, userNo, "");
					webview.loadUrl(url);
				} 
			} catch (Exception e) {
				Dlog.e(TAG, e);
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if( webview != null ) {
			webview.resumeTimers();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if( webview != null ) {
			webview.pauseTimers();
		}
	}
}
