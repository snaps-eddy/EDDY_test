package com.snaps.mobile.activity.webview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.JsResult;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebHistoryItem;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_EKEY;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.system.SystemUtil;
import com.snaps.common.utils.thread.ATask;
import com.snaps.common.utils.thread.ATask.OnTask;
import com.snaps.common.utils.ui.ProgressController;
import com.snaps.common.utils.ui.SingleTabWebViewController;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.kakao.utils.share.SNSShareUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.hamburger_menu.SnapsMenuManager;
import com.snaps.mobile.activity.home.fragment.GoHomeOpserver;
import com.snaps.mobile.activity.home.fragment.GoHomeOpserver.OnGoHomeOpserver;
import com.snaps.mobile.activity.home.fragment.HomeMenuBase.OnSlideMenuLitener;
import com.snaps.mobile.activity.photoprint.NewPhotoPrintListActivity;
import com.snaps.mobile.activity.ui.menu.webinterface.SnapsShouldOverrideUrlLoader;
import com.snaps.mobile.activity.ui.menu.webinterface.web_event_handlers.SnapsWebEventCartProductLoadHandler;
import com.snaps.mobile.base.SnapsBaseFragmentActivity;
import com.snaps.mobile.utils.kakao.KakaoStoryPostingEventor;
import com.snaps.mobile.utils.thirdparty.SnapsTPAppManager;

import java.util.HashMap;

import errorhandle.logger.Logg;
import errorhandle.logger.SnapsLogger;
import errorhandle.logger.model.SnapsLoggerClass;
import font.FProgressDialog;

import static com.snaps.mobile.activity.ui.menu.webinterface.web_event_handlers.SnapsWebEventBaseHandler.CALL_URL;
import static com.snaps.mobile.activity.ui.menu.webinterface.web_event_handlers.SnapsWebEventBaseHandler.CART_PRODUCT_LOAD_URL;
import static com.snaps.mobile.activity.ui.menu.webinterface.web_event_handlers.SnapsWebEventCartUrlHandler.CART_REQUEST_CODE_PAYMENT;

@SuppressLint("NewApi")
public class UIWebviewActivity extends SnapsBaseFragmentActivity implements OnSlideMenuLitener, OnGoHomeOpserver, IUIWebViewActBridge {
	private static final String TAG = UIWebviewActivity.class.getSimpleName();

	private static final String NAVER_PAY_STTL_CODE = "012019";

	com.snaps.mobile.component.ObserveScrollingWebView webview;
	FProgressDialog pd;

	TextView mBtnEdit;
	TextView mBtnComplete;
	TextView mTxtTitleText;
	ImageView mImageHome;
	ImageView hamburgerBtn;

	// 이니q시스 페이이지인지 아닌지 false 아닌걸로...
	boolean mIsINISIS_Page = false;

	boolean mIsPaymentComplete = false;

	// 결제
	boolean network_error = false;
	boolean isPageFinshed = false;

	boolean isBlockBackKey = false;

	private View mHomeAlpha;

	KakaoStoryPostingEventor eventor = null;
	String snsShareCallBack = "";
	boolean isProductEditMode = false;

	private SnapsShouldOverrideUrlLoader snapsShouldOverrideUrlLoader = null;

	public static Intent getIntent(Context context, String title, String url) {
		Intent intent = new Intent(context, UIWebviewActivity.class);
		intent.putExtra(Const_EKEY.WEBVIEW_TITLE, title);
		intent.setData(Uri.parse(Config.getPaymentScheme(context) + "://payment"));
		intent.putExtra(Const_EKEY.WEBVIEW_URL, url);
		return intent;
	}

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public void onCreate(Bundle savedInstanceState) {

		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
			getWindow().setFlags(
					WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
					WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
		}

		super.onCreate(savedInstanceState);

		SnapsLogger.appendClassTrackingLog(new SnapsLoggerClass<>(this));

		setContentView(R.layout.activity_uiwebview_);

		if (findViewById(R.id.TopLine) != null)
			findViewById(R.id.TopLine).setVisibility(View.GONE);

		if (!Config.isSnapsSDK2(this))
			eventor = new KakaoStoryPostingEventor(this);

		Config.setIS_MAKE_RUNNING(false);

		snapsShouldOverrideUrlLoader = SnapsShouldOverrideUrlLoader.createInstanceForCart(this, this);

		isBlockBackKey = false;

		mBtnEdit = (TextView) findViewById(R.id.btnTopEdit);
		mBtnComplete = (TextView) findViewById(R.id.btnTopEditComplete);

		mTxtTitleText = (TextView) findViewById(R.id.txtTitleText);
		mImageHome = (ImageView) findViewById(R.id.btnTitleLeft);

		mHomeAlpha = (View) findViewById(R.id.alpha50);
		mHomeAlpha.setBackgroundColor(Color.argb(125, 0, 0, 0));
		mHomeAlpha.bringToFront();

		mHomeAlpha.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (event.getAction() == MotionEvent.ACTION_DOWN)

					if (!Config.isSnapsSDK2(UIWebviewActivity.this)) {
						onCloseMenu();
						return true;
					} else {
						DisplayMetrics outMetrics = new DisplayMetrics();
						getWindowManager().getDefaultDisplay().getMetrics(outMetrics);

						float menuHeight;
						menuHeight = 90 * outMetrics.density;

						if (event.getY() > menuHeight) {

							onCloseMenu();
							return true;
						}
						return false;

					}

				return true;

			}
		});

		if (SnapsTPAppManager.isThirdPartyApp(this)) {
			SnapsTPAppManager.setImageResource(mImageHome, R.drawable.btn_prev);
			mImageHome.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					finishActivity();
				}
			});
		} else
			mImageHome.setImageResource(R.drawable.btn_top_menu);

		try {

			String defaultTitle = getIntent().getStringExtra(Const_EKEY.WEBVIEW_TITLE);
			if(defaultTitle != null && defaultTitle.length() > 0)
				mTxtTitleText.setText(defaultTitle);

			String url = "";

			url = getIntent().getStringExtra(CALL_URL);

			if (url == null || url.length() == 0)
				url = getIntent().getStringExtra(Const_EKEY.WEBVIEW_URL);
			else { // 이니시스 페이지에서는 편집버튼이 없어야 한다.
				mTxtTitleText.setText(getString(R.string.payment));
				mBtnEdit.setVisibility(View.GONE);
				mBtnComplete.setVisibility(View.GONE);
				mIsINISIS_Page = true;
			}

			if (url.contains("/mw/history") && Config.isSnapsBitween(this)) { // 주문/배송 페이지에도 편집버튼 없도록.
				mBtnEdit.setVisibility(View.GONE);
				mBtnComplete.setVisibility(View.GONE);
			}

			pd = new FProgressDialog(this);
			pd.setMessage(getString(R.string.please_wait));
			pd.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {

				}
			});
			pd.setCancelable(true);

			webview = (com.snaps.mobile.component.ObserveScrollingWebView) findViewById(R.id.webview);
			webview.getSettings().setJavaScriptEnabled(true);

			webview.getSettings().setPluginState(PluginState.ON);

			// 몇몇 단말기에서 주소 클릭이 안된다고 해서 넣은 코드..해결이 될지는 모르겠음.
			webview.getSettings().setDomStorageEnabled(true);

			if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT_WATCH) {
				webview.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
			}

			webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
			webview.getSettings().setSupportMultipleWindows(false);

			// // 웹페이지를 화면사이즈에 맞춤.
			webview.getSettings().setLoadWithOverviewMode(true);
			webview.getSettings().setUseWideViewPort(true);
			// 줌컨트롤추가
			webview.getSettings().setBuiltInZoomControls(false);

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

			// 삼성 카드 요구 사항
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				try {
					webview.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

					CookieManager cookieMan = CookieManager.getInstance();
					cookieMan.setAcceptCookie(true);
					cookieMan.setAcceptThirdPartyCookies(webview, true);
				} catch (Exception e) {
					Dlog.e(TAG, e);
				}
			}
			// end 삼성 카드 요구 사항
			hamburgerBtn = (ImageView) findViewById(R.id.btnTitleHamburgerMenu);
			//hamburgerBtn.setVisibility(defaultTitle != null && defaultTitle.equalsIgnoreCase(getString(R.string.cart)) ? View.VISIBLE : View.GONE);
			hamburgerBtn.setVisibility(View.GONE);
			hamburgerBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					SnapsMenuManager.showHamburgerMenu(UIWebviewActivity.this, SnapsMenuManager.eHAMBURGER_ACTIVITY.CART);
				}
			});

			changeTitle(url);

			ProgressBar progressBar = new ProgressController(this).initialize();
			webview.setWebViewClient(new WebViewClientClass());
			webview.setWebChromeClient(new WebChromeClientClass(progressBar));

			SingleTabWebViewController wvController = new SingleTabWebViewController();
			wvController.setView((RelativeLayout) findViewById(R.id.main_menu_bar));
			wvController.setWebView(webview);

			//네이버 페이는 상단바가 가려져서 예외 처리
			if (url.contains("f_sttl_mthd=" + NAVER_PAY_STTL_CODE))
				wvController.hideTitle();

			webview.loadUrl(url, SystemUtil.getWebviewVersionMapData(this));

			initSlidingMenu();

			checkProductEditByPushEvent();

		} catch (Exception e) {
			Dlog.e(TAG, e);
		}

		GoHomeOpserver.addGoHomeListener(this);
	}

	private void checkProductEditByPushEvent() throws Exception {
		String cartLoadUrl = getIntent().getStringExtra(CART_PRODUCT_LOAD_URL);
		if (StringUtil.isEmpty(cartLoadUrl) || snapsShouldOverrideUrlLoader == null) return;
		SnapsShouldOverrideUrlLoader.SnapsShouldHandleData handleDatas = new SnapsShouldOverrideUrlLoader.SnapsShouldHandleData();
		handleDatas.setUrl(cartLoadUrl);

		SnapsWebEventCartProductLoadHandler cartProductLoadHandler = new SnapsWebEventCartProductLoadHandler(this, handleDatas);
		cartProductLoadHandler.handleEvent();
	}

	@Override
	public View getBtnEdit() {
		return mBtnEdit;
	}

	@Override
	public View getBtnComplete() {
		return mBtnComplete;
	}

	@Override
	public KakaoStoryPostingEventor getKakaoStoryPostingEventor() {
		return eventor;
	}

	@Override
	public String getSnsShareCallBack() {
		return snsShareCallBack;
	}

	@Override
	public void setSnsShareCallBack(String str) {
		snsShareCallBack = str;
	}

	@Override
	public void shouldOverrideUrlLoading(WebView view, String url) {
		if (snapsShouldOverrideUrlLoader != null)
			snapsShouldOverrideUrlLoader.shouldOverrideUrlLoading(view, url);
	}

	@Override
	public boolean isINISIS_Page() {
		return mIsINISIS_Page;
	}

	@Override
	public void setIsPaymentComplete(boolean flag) {
		mIsPaymentComplete = flag;
	}

	private void initSlidingMenu() {
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		Config.setIS_MAKE_RUNNING(false);

		isProductEditMode = false;

		if (webview != null) {
			webview.resumeTimers();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (!isProductEditMode) {
			if (webview != null) {
				webview.pauseTimers();
			}
		}
	}

	public void enableProductEditState() {
		isProductEditMode = true;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (eventor != null) {
			eventor.release();
			eventor = null;
		}
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

		GoHomeOpserver.removeGoHomeListenrer(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		switch (requestCode) {
			case CART_REQUEST_CODE_PAYMENT:
				if (resultCode == RESULT_OK) {
					String returnValue = data.getStringExtra("returnValue");

					String params = String.format("javascript:appJs.exec({\"cmd\":\"paymentFinish\",\"response\":\"%s\"})", returnValue);
					webview.loadUrl(params, SystemUtil.getWebviewVersionMapData(this));
					// 결제가 완료가 되면 back key를 눌렀을때 홈으로 가도록 한다.
					mIsPaymentComplete = true;
				}
				break;

			default:
				// 인화수량 변경을 한경우...장바구니 화면을 refresh 한다.
				if (resultCode == NewPhotoPrintListActivity.REQUEST_CODE_PHOTOPRINT_CHANGE || resultCode == NewPhotoPrintListActivity.REQUEST_CODE_PHOTOPRINT_CHANGE) {
					String params = String.format("javascript:appJs.exec({\"cmd\":\"refresh\"})");
					webview.loadUrl(params, SystemUtil.getWebviewVersionMapData(this));
					isHistoryClean = true;
				}
				break;
		}

		if(!SnapsTPAppManager.isThirdPartyApp(this))
			SNSShareUtil.postActivityForResult(requestCode, resultCode, data);
	}

	@Override
	public void onBackPressed() {
		finishActivity();
	}

	private void checkHomeKey() {
		if (mImageHome != null) {
			mImageHome.setImageResource(R.drawable.btn_prev);
		}
	}

	void finishActivity() {
		if (isBlockBackKey) return;

		if (!mIsPaymentComplete && webview.canGoBack()) {
			getPageIndex();
			webview.goBack();
		} else {
			//결제 화면에서 뒤로 가면 모든 액티비티가 종료되는 현상 때문에 홈으로 안 가고 finish를 한다.
			finish();
		}
	}

	public void changeTitle(String url) {
		try {
			HashMap<String, String> hashmap = StringUtil.parseUrl(url);

			Dlog.d("changeTitle() url:" + url);

			if (hashmap.containsKey(Const_EKEY.WEB_NAVIBARTITLE_KEY)) {
				String title = hashmap.get(Const_EKEY.WEB_NAVIBARTITLE_KEY);
				mTxtTitleText.setText(title);

//				if (hamburgerBtn != null) {
//					hamburgerBtn.setVisibility(title.equals(getString(R.string.cart)) ? View.VISIBLE : View.GONE);
//				}

				if (getString(R.string.cart).equals(hashmap.get(Const_EKEY.WEB_NAVIBARTITLE_KEY)) || getString(R.string.order_complete).equals(hashmap.get(Const_EKEY.WEB_NAVIBARTITLE_KEY))) {
					mBtnEdit.setVisibility(View.GONE);
					mBtnComplete.setVisibility(View.GONE);
				} else {
					mBtnComplete.setVisibility(View.GONE);
					mBtnEdit.setVisibility(View.GONE);
				}
			}

		} catch (Exception e) {
			Dlog.e(TAG, e);
		}

		checkHomeKey();
	}

	/***
	 * 현재 페이지 이전 url를 가져와서 타이틀를 변경한다.
	 */
	void getPageIndex() {
		WebBackForwardList a = webview.copyBackForwardList();

		int idx = a.getCurrentIndex();
		WebHistoryItem h = a.getItemAtIndex(idx - 1);

		String preUrl = h.getOriginalUrl();
		Dlog.d("getPageIndex() preUrl:" + preUrl);
		changeTitle(preUrl);

	}

	public void onClick(View v) {
		if (v.getId() == R.id.btnTitleLeft || v.getId() == R.id.btnTitleLeftLy) {
			finishActivity();
		}

		else if (v.getId() == R.id.btnTopEdit) {
			webview.loadUrl("javascript:appJs.exec({\"cmd\":\"modify\"})", SystemUtil.getWebviewVersionMapData(this));

			mBtnEdit.setVisibility(View.GONE);
			mBtnComplete.setVisibility(View.VISIBLE);
		}

		else if (v.getId() == R.id.btnTopEditComplete) {
			webview.loadUrl("javascript:appJs.exec({\"cmd\":\"modify\"})", SystemUtil.getWebviewVersionMapData(this));

			mBtnComplete.setVisibility(View.GONE);
			mBtnEdit.setVisibility(View.VISIBLE);

		}
	}

	class WebViewClientClass extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(final WebView view, String url) {
			changeTitle(url);
			return snapsShouldOverrideUrlLoader != null && snapsShouldOverrideUrlLoader.shouldOverrideUrlLoading(view, url);
		}

		// 페이지 시작
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			isPageFinshed = false;

			ATask.executeVoid(new OnTask() {

				@Override
				public void onPre() {}

				@Override
				public void onPost() {
					if (!isPageFinshed)
						findViewById(R.id.progressBar2).setVisibility(View.VISIBLE);
				}

				@Override
				public void onBG() {
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						Dlog.e(TAG, e);
					}
				}
			});

			if (!isPageFinshed)
				findViewById(R.id.progressBar2).setVisibility(View.VISIBLE);

			network_error = false;

			if (shouldBlockBacKeyWithUrl(url))
				isBlockBackKey = true;
		}

		private boolean shouldBlockBacKeyWithUrl(String url) {
			return !StringUtil.isEmpty(url) && !url.startsWith(SnapsAPI.DOMAIN());
		}

		// 페이지 로딩시
		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
			network_error = true;
			try {
				findViewById(R.id.la_network_error).setVisibility(View.VISIBLE);
			} catch (Exception e) {
				Dlog.e(TAG, e);
			}

			isBlockBackKey = false;
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			isPageFinshed = true;

			HashMap<String, String> hashmap = StringUtil.parseUrl(url);

			if (hashmap != null && hashmap.containsKey(Const_EKEY.WEB_NAVIBARTITLE_KEY)) {
				String title = hashmap.get(Const_EKEY.WEB_NAVIBARTITLE_KEY);
				if (title != null)
					mTxtTitleText.setText(title);

				if (getString(R.string.cart).equals(hashmap.get(Const_EKEY.WEB_NAVIBARTITLE_KEY))) {

					if ("0".equals(hashmap.get(Const_EKEY.WEB_CARTCOUNT_KEY))) {
						mBtnEdit.setVisibility(View.GONE);

					} else {
						mBtnEdit.setVisibility(View.GONE);
					}
				}
			}

			if (!network_error) {
				findViewById(R.id.la_network_error).setVisibility(View.GONE);
			}

			if (isHistoryClean) {
				isHistoryClean = false;
				view.clearHistory();
			}

			isBlockBackKey = false;
		}
	}

	boolean isHistoryClean = false;

	class WebChromeClientClass extends WebChromeClient {
		private ProgressBar progressBar;

		WebChromeClientClass(ProgressBar progressBar) {
			this.progressBar = progressBar;
		}

		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			try {
				if (progressBar != null)
					progressBar.setProgress(newProgress);

				if (newProgress == 100) {// 페이지 종료
					if (progressBar != null)
						progressBar.setVisibility(View.GONE);
					findViewById(R.id.progressBar2).setVisibility(View.GONE);
				}

				Dlog.d("onProgressChanged() : " + newProgress);
			} catch (Exception e) {
				Dlog.e(TAG, e);
			}
		}

		@Override
		public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
			WebViewDialogOneBtn wdia = new WebViewDialogOneBtn(UIWebviewActivity.this, message, result);
			wdia.setCancelable(false);
			if( !UIWebviewActivity.this.isFinishing() ) wdia.show();

			return true;
		}

		@Override
		public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
			WebViewDialog wdia = new WebViewDialog(UIWebviewActivity.this, message, result);
			wdia.setCancelable(false);
			if( !UIWebviewActivity.this.isFinishing() ) wdia.show();

			return true;
		}
	}

	@Override
	public void onCloseMenu() {
	}

	@Override
	public void onGoHome() {
		finish();
	}
}
