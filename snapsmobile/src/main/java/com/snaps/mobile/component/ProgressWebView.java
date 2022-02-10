package com.snaps.mobile.component;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager.BadTokenException;
import android.webkit.JsResult;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.snaps.common.structure.SnapsHandler;
import com.snaps.common.utils.ISnapsHandler;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.image.Utils;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.system.SystemUtil;
import com.snaps.common.utils.thread.ATask;
import com.snaps.common.utils.thread.ATask.OnTask;
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

public class ProgressWebView extends RelativeLayout implements ISnapsHandler {
	private static final String TAG = ProgressWebView.class.getSimpleName();

	LayoutInflater mInflater = null;
	ObserveScrollingWebView webView = null;
	ProgressBar progressbar = null;
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
	
	public ProgressWebView(Context context) {
		super(context);
		init(context, null);
	}

	public ProgressWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public ProgressWebView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}
	
	public int getRealScrollPos() {
		return webView == null ? 0 : webView.getScrollY();
	}
	
	public int getScrollPos() {
		return scrollPos;
	}
	
	public void setScrollPos( int pos ) {
		this.scrollPos = pos;
	}
	
	public void setHorizontalProgressBar( ProgressBar pb ) {
		this.progressbarType = 1;
		this.progressbar = pb;
		pb.bringToFront();
		if( this.toString().equals(pb.getTag()) ) {
			pb.setVisibility( isPageFinshed ? View.GONE : View.VISIBLE );
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
	
	public boolean isScrollable() {
		return ( webView == null || webView.isScrollable() );
	}
	
	public void scrollTo( int x, int y ) {
		if( webView != null ) webView.scrollTo( x, y );
	}
	
	public int getWebViewScrollX() {
		return webView == null ? 0 : webView.getScrollX();
	}
	
	public int getWebViewScrollY() {
		return webView == null ? 0 : webView.getScrollY();
	}

	public void setProgressbarType(int progressbarType) {
		this.progressbarType = progressbarType;
	}

	public void setOnPageLoadListener(OnPageLoadListener onPageLoadListener) {
		this.onPageLoadListener = onPageLoadListener;
	}
	
	public void setPageScrollListner( OnPageScrollListener listener ) {
		this.pageScrollListener = listener;
	}
	
	public String getLastHistoryUrl() {
		String url = "";
		if( webView != null ) {
			WebBackForwardList mWebBackForwardList = webView.copyBackForwardList();
			if( mWebBackForwardList != null ) url = mWebBackForwardList.getItemAtIndex(mWebBackForwardList.getCurrentIndex()-1).getUrl();
		}
		
		return url;
	}

	void init(Context context, AttributeSet attrs) {
		snapsShouldOverrideUrlLoader = new SnapsShouldOverrideUrlLoader((Activity)getContext(), SnapsShouldOverrideUrlLoader.WEB);
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
		View v = mInflater.inflate(R.layout.custom_webview, null);
		webView = (ObserveScrollingWebView) v.findViewById(R.id.webview);
		webView.getSettings().setDefaultTextEncodingName( "UTF-8" );

		//크롬 디버깅 기능 활성화..
		if (!Config.isRealServer() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
			webView.setWebContentsDebuggingEnabled(true);

		if (progressbarType == 1) {
			progressbar = (ProgressBar) v.findViewById(R.id.progressBar);
		} else if (progressbarType == 0) {
			progressbar = (ProgressBar) v.findViewById(R.id.progressBar2);

			try {
				Drawable drawable = getResources().getDrawable(R.drawable.rotate_progress);
				progressbar.setIndeterminateDrawable(drawable);
			} catch (OutOfMemoryError e) { Dlog.e(TAG, e); }
		}

		WebSettings set = webView.getSettings();

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
			webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		} else {
			set.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
		}

        originLongClickListener = Utils.getOnLongClickListener( webView );
        setWebViewLongClickable( false );

		webView.setOnScrollListener(new OnPageScrollListener() {
			@Override
			public boolean onScrollChanged(int l, int t, int oldl, int oldt) {
				if( pageScrollListener != null ) pageScrollListener.onScrollChanged(0, t, 0, oldt);
				return false;	
			}

            @Override
            public boolean onScrollChanged(int dx, int dy) { return false; }
        });
		
		webView.setWebViewClient(new WebViewClientClass());
		webView.setWebChromeClient(new WebChromeClientClass());
		
		addView(v);
		webView.resumeTimers();

		// 네크워크 오류일때 retry
		findViewById(R.id.btn_retry).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				webView.reload();

			}
		});
	}

    public void setWebViewLongClickable( boolean flag ) {
        if( webView == null ) return;

        webView.setLongClickable(flag);
        webView.setHapticFeedbackEnabled(flag);
        webView.setOnLongClickListener( flag ? originLongClickListener : new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
    }

	public void addWebviewProcess(ImpWebViewProcess process) {
		processes.add(process);
	}
	
	class WebViewClientClass extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
            final String COUPON_URL = "/mw/v3/coupon/coupon_register.jsp";
            final String EVENT_URL = "pageNum=P0008";

            setWebViewLongClickable( url.contains(COUPON_URL) || url.contains(EVENT_URL) );

			if(snapsShouldOverrideUrlLoader != null) {
				snapsShouldOverrideUrlLoader.setProcesses(processes);
				snapsShouldOverrideUrlLoader.setObserveScrollingWebView(webView);
				
				return snapsShouldOverrideUrlLoader.shouldOverrideUrlLoading(view, url);
			}
			return true;
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);

			isPageFinshed = false;

			if( progressbar != null ) {
				if (progressbarType == 0) {
					ATask.executeVoid(new OnTask() {
	
						@Override
						public void onPre() {
						}
	
						@Override
						public void onPost() {
							if (!isPageFinshed)
								progressbar.setVisibility(View.VISIBLE);
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
	
				} else 
					progressbar.setVisibility(View.VISIBLE);
				// if (progressbarType == 1)
				progressbar.setProgress(0);
			}

			network_error = false;

			if (onPageLoadListener != null)
				onPageLoadListener.onPageStarted(url);

		}

		@Override
		public void onPageFinished(WebView view, String url) {
			isPageFinshed = true;
			
			if (url.startsWith("http://trace.epost.go.kr"))
				view.loadUrl("javascript:document.body.style.zoom = 2.3", SystemUtil.getWebviewVersionMapData(getContext()));
			isLoaded = true;

			if (!network_error) {
				findViewById(R.id.la_network_error).setVisibility(View.GONE);
			}

			if (onPageLoadListener != null)
				onPageLoadListener.onPageFinished(url);

			if (isClearHistory) {
				view.clearHistory();
			}
		}

		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
			// 스키마가 snapskr이면 넘김.
			network_error = true;
			try {
				findViewById(R.id.la_network_error).setVisibility(View.VISIBLE);
			} catch (Exception e) {
				Dlog.e(TAG, e);
			}

			isClearHistory = false;
		}
	}
	
	public WebView getWebView() {
		return webView;
	}

	class WebChromeClientClass extends WebChromeClient {
		@Override
		public void onProgressChanged(final WebView view, int newProgress) {
			if( progressbar == null ) return;
			try {
				progressbar.setProgress(newProgress);
				if (newProgress == 100) {// 페이지 종료
					ATask.executeVoidWithThreadPool(new OnTask() {

						@Override
						public void onPre() {}

						@Override
						public void onPost() {
							progressbar.setVisibility(View.GONE);

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
			ProgressWebView.this.jsResult = result;

			try {
				if( getContext() != null && !( (Activity)getContext() ).isFinishing() ) {
					WebViewDialogOneBtn wdia = new WebViewDialogOneBtn(getContext(), message, result);
					wdia.setCancelable(false);
					wdia.show();
					
				}
				else {
					result.cancel();
					ProgressWebView.this.jsResult = null;
				}
			} catch (BadTokenException e) {
				Dlog.e(TAG, e);
			}

			return true;
		}

		@Override
		public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {

			try {
				if(getContext() != null) {
					WebViewDialog wdia = new WebViewDialog(getContext(), message, result);
					wdia.setCancelable(false);
					wdia.show();
				}
			} catch (BadTokenException e) {
				Dlog.e(TAG, e);
			}

			return true;
		}
	}
	
	public void loadUrl(String url) {
		isClearHistory = false;
		webView.loadUrl(url, SystemUtil.getWebviewVersionMapData(getContext()));
		isLoaded = true;
	}
	
	boolean isClearHistory = false;
	public void loadUrl(String url, boolean isClearHistory) {
		this.isClearHistory = isClearHistory;
		webView.loadUrl(url, SystemUtil.getWebviewVersionMapData(getContext()));
		isLoaded = true;

	}

	public void reload() {
		webView.reload();
	}

	public boolean canGoBack() {
		return webView.canGoBack();
	}

	public void goBack() {
		webView.goBack();
	}

	public WebBackForwardList copyBackForwardList() {
		return webView.copyBackForwardList();
	}

	final int SEND_PROGRESSBAR_SHOW = 10;
	final int SEND_PROGRESSBAR_HIDE = 11;

	@Override
	public void handleMessage(Message msg) {
		switch (msg.what) {
			case SEND_PROGRESSBAR_SHOW :
				if( progressbar != null ) progressbar.setVisibility(View.VISIBLE);
				break;

			case SEND_PROGRESSBAR_HIDE :
				if( progressbar != null ) progressbar.setVisibility(View.GONE);
				break;
			default :
				break;
		}
	}

	// 웹뷰 cmd
	ArrayList<String> afterCmd = new ArrayList<String>();

	public void regWebCmd(String cmd) {
		if (isPageFinshed) {
			webView.loadUrl(cmd, SystemUtil.getWebviewVersionMapData(getContext()));
			isLoaded = true;
		} else
			afterCmd.add(cmd);
	}

	public boolean canScrollHor(int direction) {
		return webView.canScrollHor(direction);
	}

	public int getMaxScrollPosition() {
		return webView.getMaxScrollPosition();
	}

	public boolean isScrollAtTop() {
		return webView.isScrollAtTop();
	}

	private Handler titleAnimationHandler;
	private Runnable titleAnimationRunnable;

	private boolean isEnableFling = false;




	public void setEnableFling(boolean enableFling) {
		webView.setEnableFling(enableFling);
	}

	public void setTitleAnimation( Handler titleAnimationHandler, Runnable titleAnimationRunnable ) {
		webView.setTitleAnimation(titleAnimationHandler, titleAnimationRunnable);
	}

	public void setOnScrollListener(OnPageScrollListener onScrollListener ) {
		webView.setOnScrollListener(onScrollListener);
	}
}
