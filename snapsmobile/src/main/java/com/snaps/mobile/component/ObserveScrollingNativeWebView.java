package com.snaps.mobile.component;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Message;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JsResult;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.snaps.common.structure.SnapsHandler;
import com.snaps.common.utils.ISnapsHandler;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.image.Utils;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.system.SystemUtil;
import com.snaps.common.utils.thread.ATask;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.ui.menu.webinterface.ISnapsWebViewLoadListener;
import com.snaps.mobile.activity.ui.menu.webinterface.SnapsShouldOverrideUrlLoader;
import com.snaps.mobile.activity.webview.WebViewDialog;
import com.snaps.mobile.activity.webview.WebViewDialogOneBtn;
import com.snaps.mobile.interfaces.OnPageLoadListener;
import com.snaps.mobile.interfaces.OnPageScrollListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

public class ObserveScrollingNativeWebView extends ObserveScrollingWebView implements ISnapsHandler {
	private static final String TAG = ObserveScrollingNativeWebView.class.getSimpleName();

	public static boolean doingTouch = false;
	LayoutInflater mInflater = null;
	HashSet<ImpWebViewProcess> processes = new HashSet<ImpWebViewProcess>();
	boolean network_error = false;

	OnPageLoadListener onPageLoadListener = null;

	private OnLongClickListener originLongClickListener;

	private OnPageScrollListener pageScrollListener = null;

	int progressbarType = 0;
	Timer timer = null;
	TimerTask myTask = null;

	private int scrollPos = 0;

	boolean isPageFinshed = false;
	private boolean isLoaded = false;
	private JsResult jsResult;
	SnapsHandler handler = new SnapsHandler(this);

	private SnapsShouldOverrideUrlLoader snapsShouldOverrideUrlLoader = null;

	public ObserveScrollingNativeWebView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub

	}

	public ObserveScrollingNativeWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init(context, attrs);
	}

	public ObserveScrollingNativeWebView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public int getRealScrollPos() {
		return getScrollY();
	}

	public int getScrollPos() {
		return scrollPos;
	}

	public void setScrollPos(int pos) {
		scrollPos = pos;
	}

	public void setHorizontalProgressBar(ProgressBar pb) {
		this.progressbarType = 1;
		pb.bringToFront();
		if (this.toString().equals(pb.getTag())) {
			pb.setVisibility(isPageFinshed ? View.GONE : View.VISIBLE);
		}
	}

	public JsResult getJsResut() {
		return this.jsResult;
	}

	public int getProgressbarType() {
		return progressbarType;
	}

	public boolean isLoadedWebView() {
		return isLoaded;
	}

	public void setProgressbarType(int progressbarType) {
		this.progressbarType = progressbarType;
	}

	public void setOnPageLoadListener(OnPageLoadListener onPageLoadListener) {
		this.onPageLoadListener = onPageLoadListener;
	}

	public void setPageScrollListner(OnPageScrollListener listener) {
		this.pageScrollListener = listener;
	}

	public String getLastHistoryUrl() {
		String url = "";
		WebBackForwardList mWebBackForwardList = copyBackForwardList();
		if (mWebBackForwardList != null) {
			url = mWebBackForwardList.getItemAtIndex(mWebBackForwardList.getCurrentIndex() - 1).getUrl();
		}
		return url;
	}

	void init(Context context, AttributeSet attrs) {
		snapsShouldOverrideUrlLoader = new SnapsShouldOverrideUrlLoader((Activity) getContext(), SnapsShouldOverrideUrlLoader.WEB);
		snapsShouldOverrideUrlLoader.setSnapsWebViewLoadListener(new ISnapsWebViewLoadListener() {
			@Override
			public void onLoaded() {
				isLoaded = true;
			}
		});
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ProgressWebViewAttrs);

		if (a != null) {
			progressbarType = a.getInteger(R.styleable.ProgressWebViewAttrs_progressType, 0);
			a.recycle();
		}

		mInflater = LayoutInflater.from(getContext());

		getSettings().setDefaultTextEncodingName("UTF-8");

		//크롬 디버깅 기능 활성화..
		if (!Config.isRealServer() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			setWebContentsDebuggingEnabled(true);
		}

		WebSettings set = getSettings();

		set.setJavaScriptEnabled(true);
		set.setLoadsImagesAutomatically(true);
		// 캐쉬를 하지 않는다..
		set.setCacheMode(WebSettings.LOAD_NO_CACHE);
		set.setPluginState(WebSettings.PluginState.ON);
		set.setDomStorageEnabled(true);
		// 웹페이지를 화면사이즈에 맞춤.
		set.setLoadWithOverviewMode(true);
		set.setUseWideViewPort(true);
		// 줌컨트롤추가
		set.setBuiltInZoomControls(false);

		if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT_WATCH) {
			setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		} else {
			set.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
		}

		originLongClickListener = Utils.getOnLongClickListener(this);
		setWebViewLongClickable(false);

		setOnScrollListener(new OnPageScrollListener() {
			@Override
			public boolean onScrollChanged(int l, int t, int oldl, int oldt) {
				if (pageScrollListener != null) {
					pageScrollListener.onScrollChanged(0, t, 0, oldt);
				}
				return false;
			}

			@Override
			public boolean onScrollChanged(int dx, int dy) {
				return false;
			}
		});

		setWebViewClient(new WebViewClientClass());
		setWebChromeClient(new WebChromeClientClass());

		//addView(v);
		resumeTimers();
	}

	public void setWebViewLongClickable(boolean flag) {

		setLongClickable(flag);
		setHapticFeedbackEnabled(flag);
		setOnLongClickListener(flag ? originLongClickListener : new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				return true;
			}
		});
	}

	@Override
	protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
		requestDisallowInterceptTouchEvent(false);
		super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent e) {
		requestDisallowInterceptTouchEvent(true);
		return super.onInterceptTouchEvent(e);
	}

	@Override
	public boolean onTouchEvent(MotionEvent e) {

		return super.onTouchEvent(e);
	}

	public void addWebviewProcess(ImpWebViewProcess process) {
		processes.add(process);
	}

	class WebViewClientClass extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			final String COUPON_URL = "/mw/v3/coupon/coupon_register.jsp";
			final String EVENT_URL = "pageNum=P0008";

			setWebViewLongClickable(url.contains(COUPON_URL) || url.contains(EVENT_URL));

			if (snapsShouldOverrideUrlLoader != null) {
				snapsShouldOverrideUrlLoader.setProcesses(processes);
				snapsShouldOverrideUrlLoader.setObserveScrollingWebView(ObserveScrollingNativeWebView.this);

				return snapsShouldOverrideUrlLoader.shouldOverrideUrlLoading(view, url);
			}
			return true;
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);

			isPageFinshed = false;
			network_error = false;

			if (onPageLoadListener != null) {
				onPageLoadListener.onPageStarted(url);
			}
			if(!EndlessPagerForNativeScrollViewAdapter.isFirst) {
				Intent action = new Intent(Const_VALUE.WEBVIEW_START);
				getContext().sendBroadcast(action);
            }

		}

		@Override
		public void onPageFinished(WebView view, String url) {
			isPageFinshed = true;

			view.loadUrl("javascript:(function(){ document.body.style.paddingTop = '48px'})();");
			if (url.startsWith("http://trace.epost.go.kr")) {
				view.loadUrl("javascript:document.body.style.zoom = 2.3", SystemUtil.getWebviewVersionMapData(getContext()));
			}
			isLoaded = true;

			if (!network_error) {
				//		findViewById(R.id.la_network_error).setVisibility(View.GONE);
			}

			if (onPageLoadListener != null) {
				onPageLoadListener.onPageFinished(url);
			}

			if (isClearHistory) {
				view.clearHistory();
			}

			if(!EndlessPagerForNativeScrollViewAdapter.isFirst) {
				Intent action = new Intent(Const_VALUE.WEBVIEW_END);
				getContext().sendBroadcast(action);
			}
			EndlessPagerForNativeScrollViewAdapter.isFirst = true;
		}

		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
			// 스키마가 snapskr이면 넘김.
			network_error = true;
			try {
				//		findViewById(R.id.la_network_error).setVisibility(View.VISIBLE);
			} catch (Exception e) {
				Dlog.e(TAG, e);
			}

			if(!EndlessPagerForNativeScrollViewAdapter.isFirst) {
				Intent action = new Intent(Const_VALUE.WEBVIEW_FAIL);
				getContext().sendBroadcast(action);
			}
			isClearHistory = false;
		}
	}

	public WebView getWebView() {
		return this;
	}

	class WebChromeClientClass extends WebChromeClient {
		@Override
		public void onProgressChanged(final WebView view, int newProgress) {

			try {

				if (newProgress == 100) {// 페이지 종료
					ATask.executeVoidWithThreadPool(new ATask.OnTask() {

						@Override
						public void onPre() {
						}

						@Override
						public void onPost() {

							for (String c : afterCmd) {
								view.loadUrl(c, SystemUtil.getWebviewVersionMapData(getContext()));
								isLoaded = true;
							}

							afterCmd.clear();

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
		public boolean onJsAlert(WebView view, String url, final String message, final JsResult result) {
			jsResult = result;

			try {
				if (getContext() != null && !((Activity) getContext()).isFinishing()) {
					WebViewDialogOneBtn wdia = new WebViewDialogOneBtn(getContext(), message, result);
					wdia.setCancelable(false);
					wdia.show();

				} else {
					result.cancel();
					jsResult = null;
				}
			} catch (WindowManager.BadTokenException e) {
				Dlog.e(TAG, e);
			}

			return true;
		}

		@Override
		public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {

			try {
				if (getContext() != null) {
					WebViewDialog wdia = new WebViewDialog(getContext(), message, result);
					wdia.setCancelable(false);
					wdia.show();
				}
			} catch (WindowManager.BadTokenException e) {
				Dlog.e(TAG, e);
			}

			return true;
		}
	}

	public void loadUrl(String url) {
		isClearHistory = false;
		loadUrl(url, SystemUtil.getWebviewVersionMapData(getContext()));
		isLoaded = true;
	}

	boolean isClearHistory = false;

	public void loadUrl(String url, boolean isClearHistory) {
		this.isClearHistory = isClearHistory;
		loadUrl(url, SystemUtil.getWebviewVersionMapData(getContext()));
		isLoaded = true;

	}

	final int SEND_PROGRESSBAR_SHOW = 10;
	final int SEND_PROGRESSBAR_HIDE = 11;
	public void reFresh() {
		loadUrl("javascript:window.refresh();");
	}
	@Override
	public void handleMessage(Message msg) {
		switch (msg.what) {
			case SEND_PROGRESSBAR_SHOW:
//				if( progressbar != null ) progressbar.setVisibility(View.VISIBLE);
				break;

			case SEND_PROGRESSBAR_HIDE:
//				if( progressbar != null ) progressbar.setVisibility(View.GONE);
				break;
			default:
				break;
		}
	}

	// 웹뷰 cmd
	ArrayList<String> afterCmd = new ArrayList<String>();

	public void regWebCmd(String cmd) {
		if (isPageFinshed) {
			loadUrl(cmd, SystemUtil.getWebviewVersionMapData(getContext()));
			isLoaded = true;
		} else {
			afterCmd.add(cmd);
		}
	}
}
