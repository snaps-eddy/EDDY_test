package com.snaps.mobile.activity.webview;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_EKEY;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.net.xml.GetParsedXml;
import com.snaps.common.utils.pref.Setting;
import com.snaps.common.utils.system.SystemUtil;
import com.snaps.common.utils.thread.ATask;
import com.snaps.common.utils.thread.ATask.OnTask;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.home.RenewalHomeActivity;
import com.snaps.mobile.activity.ui.menu.webinterface.web_event_handlers.SnapsWebEventBaseHandler;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import errorhandle.logger.Logg;
import errorhandle.logger.SnapsInterfaceLogDefaultHandler;

public class PopupDialogCenterPush extends DialogFragment {
	private static final String TAG = PopupDialogCenterPush.class.getSimpleName();

	// 요구사항..
	// 받은 url을 호출한다.
	// 동영상을 플레이 할수 있도록 한다.
	// 스텍관리를 통해 뒤로가기를 할때. 팝업을 종료한다.

	// 파서 생성... cmd 검색..
	//

	// 정규식..
	// var Regexpexec = new RegExp("("+keyName+"=)(.*?)(?=&|$)").exec(value);

	com.snaps.mobile.component.ObserveScrollingWebView webview = null;

	ProgressBar progressbar = null;

	String openUrl = "";
	String type = "";
	String user_no = "";
	String close = "";
	String mFullurl = "";
	HashMap<String, String> hashmap;

	boolean today = false;

	boolean isFirstLoad = true;

	ImageView Pushcheck;

	public static PopupDialogCenterPush newInstance() {
		return new PopupDialogCenterPush();
	}

	public void setOpenUrl(String openUrl) {
		this.openUrl = openUrl;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setUserNo(String no) {
		this.user_no = no;
	}

	public void setclose(String close) {
		this.close = close;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setStyle(DialogFragment.STYLE_NO_TITLE, 0);

		isFirstLoad = true;

	}

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.dialog_popup_push_center_, container, false);
		webview = (com.snaps.mobile.component.ObserveScrollingWebView) v.findViewById(R.id.webview);
		progressbar = (ProgressBar) v.findViewById(R.id.progressBar);

		webview.getSettings().setJavaScriptEnabled(true);
		webview.getSettings().setPluginState(PluginState.ON);

		// 웹페이지를 화면사이즈에 맞춤.
		webview.getSettings().setLoadWithOverviewMode(true);
		webview.getSettings().setUseWideViewPort(true);
		// 줌컨트롤추가
		webview.getSettings().setBuiltInZoomControls(false);

		webview.setWebViewClient(new WebClient());
		webview.setWebChromeClient(new WebChromeClientClass());
		webview.setLongClickable(false);
		webview.getSettings().setSupportZoom(false);
		webview.getSettings().setDisplayZoomControls(false);
		webview.getSettings().setUseWideViewPort(false);
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

		progressbar.animate();
		webview.loadUrl(openUrl, SystemUtil.getWebviewVersionMapData(getActivity()));

		TextView pushclosetitle = (TextView) v.findViewById(R.id.push_bottom_close_title);
		pushclosetitle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (getToday()) {
					saveDate();
					dismiss();
				} else {
					dismiss();
				}

			}
		});

		ImageView Pushclose = (ImageView) v.findViewById(R.id.push_center_close);

		Pushclose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (getToday()) {
					saveDate();
					dismiss();
				} else {
					dismiss();
				}

			}
		});

		Pushcheck = (ImageView) v.findViewById(R.id.push_center_bottom_check);

		Pushcheck.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				todayCheck();
			}
		});

		TextView Pushtext = (TextView) v.findViewById(R.id.push_bottom_title);

		Pushtext.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				todayCheck();

			}
		});

		if (close.equals("N")) {
			Pushtext.setVisibility(View.INVISIBLE);
			Pushcheck.setVisibility(View.INVISIBLE);

		} else {
			Pushtext.setVisibility(View.VISIBLE);
			Pushcheck.setVisibility(View.VISIBLE);
		}

		return v;
	}

	public void todayCheck() {
		today = !today;

		if (today) {
			Pushcheck.setImageResource(R.drawable.push_inner_chk);
		} else {
			Pushcheck.setImageResource(R.drawable.push_inner_chk_none);
		}

		setToday(today);
	}

	public void setToday(boolean td) {
		today = td;
	}

	public boolean getToday() {
		return today;
	}

	public void saveDate() {
		// 현재 시간을 msec으로 구한다.
		long now = System.currentTimeMillis();

		// 현재 시간을 저장 한다.
		Date date = new Date(now);

		SimpleDateFormat CurYearFormat = new SimpleDateFormat("yyyy");
		SimpleDateFormat CurMonthFormat = new SimpleDateFormat("MM");
		SimpleDateFormat CurDayFormat = new SimpleDateFormat("dd");

		String strCurYear = CurYearFormat.format(date);
		String strCurMonth = CurMonthFormat.format(date);
		String strCurDay = CurDayFormat.format(date);

		Dlog.d("saveDate() year:" + strCurYear + ", month:" + strCurMonth + ", day:" + strCurDay);

		String dateToday = strCurYear + strCurMonth + strCurDay;

		Setting.set(getActivity(), Const_VALUE.PUSH_CENTER_CHECK_KEY, dateToday);
	}

	class WebClient extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			Dlog.d("shouldOverrideUrlLoading() url:" + url);

			mFullurl = url;

			hashmap = Config.ExtractWebURL(url);

			if (url.startsWith("snapsapp://")) {

				String schemeHost = SnapsWebEventBaseHandler.findHost(url);
				if (schemeHost == null || schemeHost.length() < 1) {
					if(hashmap != null && hashmap.containsKey(Const_EKEY.WEB_CMD_KEY)) {
						schemeHost = hashmap.get(Const_EKEY.WEB_CMD_KEY);
					}
				}

                if (schemeHost != null && schemeHost.length() > 0) {
                    if (schemeHost.equals("gotoPage")) {
                        requestPushLog();
                    } else if (schemeHost.equals("sendCmd")) {

                    }
                }
			}// 동영상인 경우 ..
			else if (url.endsWith(".mp4")) {
			}
			// return super.shouldOverrideUrlLoading(view, m_szWebViewUrl);
			return true;
		}

		@Override
		public void onPageFinished(WebView view, String url) {
		}
	}

	void requestPushLog() {

		if (Config.getCHANNEL_CODE().equals(Config.CHANNEL_SNAPS_KOR)) {

			ATask.executeVoid(new OnTask() {

				@Override
				public void onPre() {
					// TODO Auto-generated method stub

				}

				@Override
				public void onPost() {
					String target = hashmap.get("pageNum");
					Dlog.d("requestPushLog() target:" + target);

					Intent goHomeIntent = new Intent(getActivity(), RenewalHomeActivity.class);
					goHomeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					goHomeIntent.putExtra("gototarget", target);

					if (target.contains("P0006") || target.contains("P0002") || target.contains("P0007")) {
						goHomeIntent.putExtra("fullurl", mFullurl);

					}

					startActivity(goHomeIntent);
					dismiss();

				}

				@Override
				public void onBG() {
					// 서버에 리뷰 등록 api 호출...
					GetParsedXml.requestPushLog(user_no, type, SnapsInterfaceLogDefaultHandler.createDefaultHandler());

				}
			});
		}

	}

	class WebChromeClientClass extends WebChromeClient {
		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			try {
				if (newProgress == 100) {// 페이지 종료
					progressbar.clearAnimation();
					progressbar.setVisibility(View.GONE);
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

			if (isFirstLoad) {
				isFirstLoad = false;
			} else
				webview.reload();
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
