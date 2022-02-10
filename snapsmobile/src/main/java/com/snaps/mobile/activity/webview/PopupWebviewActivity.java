package com.snaps.mobile.activity.webview;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_EKEY;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.file.FileUtil;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.pref.Setting;
import com.snaps.common.utils.ui.FontUtil;
import com.snaps.common.utils.ui.IFacebook;
import com.snaps.common.utils.ui.IKakao;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.common.utils.ui.SingleTabWebViewController;
import com.snaps.common.utils.ui.SnsFactory;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.common.utils.ui.UI;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.kakao.utils.share.SNSShareUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.hamburger_menu.SnapsMenuManager;
import com.snaps.mobile.activity.home.fragment.GoHomeOpserver;
import com.snaps.mobile.activity.home.fragment.HomeMenuBase.OnSlideMenuLitener;
import com.snaps.mobile.base.SnapsBaseFragmentActivity;
import com.snaps.mobile.component.ProgressWebView;
import com.snaps.mobile.component.SnapsWebviewProcess;
import com.snaps.mobile.interfaces.OnFileUploadListener;
import com.snaps.mobile.interfaces.OnPageLoadListener;
import com.snaps.mobile.utils.thirdparty.SnapsTPAppManager;
import com.snaps.mobile.utils.ui.UrlUtil;

import errorhandle.logger.Logg;
import errorhandle.logger.SnapsLogger;
import errorhandle.logger.model.SnapsLoggerClass;

public class PopupWebviewActivity extends SnapsBaseFragmentActivity
		implements OnPageLoadListener, OnSlideMenuLitener, GoHomeOpserver.OnGoHomeOpserver {
	private static final String TAG = PopupWebviewActivity.class.getSimpleName();

	public interface IPopupDialogFragmentCallback {
        void onReceived(String msg);
    }

	private static IPopupDialogFragmentCallback popupDialogFragmentCallback = null;

    private SnapsWebViewReceiver receiver = null;

    private ProgressWebView progressWebview = null;

    private SingleTabWebViewController wvController;

    private ImageView mHomeBtn;

    private ImageView mBackBtn;

    private View mHomeAlpha;

    private String m_szWebViewUrl = "";

    private boolean isFinishedActivity = false;

	public static Intent getIntent(Context context, String url) {
		return getIntent(context, null, url);
	}

	public static Intent getIntent(Context context, PopupWebviewActivity.IPopupDialogFragmentCallback callback, String url) {
		popupDialogFragmentCallback = callback;

		Intent intent = new Intent(context, PopupWebviewActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(DetailProductWebviewActivity.KEY_ANIMATE_TITLE, true);
		intent.putExtra(DetailProductWebviewActivity.KEY_ANIMATE_WHEN_FINISH, false);
		intent.putExtra(Const_EKEY.WEBVIEW_URL, url);
		intent.putExtra(DetailProductWebviewActivity.KEY_WEBVIEW_TYPE, SnapsMenuManager.eHAMBURGER_ACTIVITY.ETC.ordinal());
		intent.putExtra(Const_EKEY.WEBVIEW_PRESENT_URL, false);
		return intent;
	}

	private IKakao kakao = null;
	private IFacebook facebook = null;
	private SnapsWebviewProcess sp = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
			getWindow().setFlags(
					WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
					WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
		}

		super.onCreate(savedInstanceState);

		SnapsLogger.appendClassTrackingLog(new SnapsLoggerClass<>(this));

		IntentFilter filter = new IntentFilter(Const_VALUE.RELOAD_URL);
		receiver = new SnapsWebViewReceiver();
		registerReceiver(receiver, filter);

        UIUtil.updateFullscreenStatus(this, false);
        // portrait
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_theme_webview_detail);

        findViewById(R.id.main_menu_bar).setVisibility(View.GONE);

		progressWebview = (ProgressWebView) findViewById(R.id.progressWebview);
		ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
		progressBar.setTag(progressWebview.toString());
		progressWebview.setHorizontalProgressBar(progressBar);

		boolean animateTitle = getIntent().getBooleanExtra(DetailProductWebviewActivity.KEY_ANIMATE_TITLE,
				true);
		if (animateTitle) {
			wvController = new SingleTabWebViewController();
			wvController
					.setView((RelativeLayout) findViewById(R.id.main_menu_bar));
			progressWebview.setPageScrollListner(wvController);
			progressWebview.setOnPageLoadListener(this);
		}

		mHomeBtn = (ImageView) findViewById(R.id.btnTitleLeft);
		mBackBtn = (ImageView) findViewById(R.id.btnTitleLeftBack);

		mHomeAlpha = (View) findViewById(R.id.alpha50);
		mHomeAlpha.setBackgroundColor(Color.argb(125, 0, 0, 0));
		mHomeAlpha.bringToFront();

		mHomeAlpha.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (event.getAction() == MotionEvent.ACTION_DOWN)

					if (!Config.isSnapsSDK2(PopupWebviewActivity.this)) {
						onCloseMenu();
						return true;
					} else {
						DisplayMetrics outMetrics = new DisplayMetrics();
						getWindowManager().getDefaultDisplay().getMetrics(
								outMetrics);

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

		checkHomeKey();

		if (!SnapsTPAppManager.isThirdPartyApp(this)) {
			if (Config.isFacebookService()) {
				facebook = SnsFactory.getInstance().queryInteface();
				facebook.init(this);
			}
			kakao = SnsFactory.getInstance().queryIntefaceKakao();
		}

		try {
			String title = getIntent().getStringExtra(Const_EKEY.WEBVIEW_TITLE);
			m_szWebViewUrl = getIntent().getStringExtra(Const_EKEY.WEBVIEW_URL);
			Dlog.d("onCreate() title:" + title + ", url:" + m_szWebViewUrl);

			// title
			TextView tvTitleText = UI.<TextView> findViewById(this, R.id.txtTitleText);
			FontUtil.applyTextViewTypeface(tvTitleText, FontUtil.eSnapsFonts.YOON_GOTHIC_760);
			tvTitleText.setText(title);

			title = StringUtil.getTitleAtUrl(m_szWebViewUrl, "naviBarTitle");
			if (title != null) {

				if (title.length() > 0) {
					findViewById(R.id.main_menu_bar).setVisibility(View.VISIBLE);
				}

				chageTitle(title);
			}

			// 공지사항인 경우..
			String webIndex = getIntent().getExtras().getString("detailindex");

			// detailindex값이 없는경우 배송조회가 된다.
			if (webIndex != null && !webIndex.equals("")) {
				m_szWebViewUrl += "&seq=" + webIndex;
			}

			// 디바이스 정보를 넣어준다.
			m_szWebViewUrl = StringUtil.addUrlParameter(m_szWebViewUrl, "deviceModel=" + Build.MODEL);

			sp = new SnapsWebviewProcess(this, facebook, kakao);
			if (UrlUtil.isPhotoPrintProduct(m_szWebViewUrl))
				sp.initPhotoPrint();
			progressWebview.addWebviewProcess(sp);

			progressWebview.setOnPageLoadListener(this);
			progressWebview.loadUrl(m_szWebViewUrl);

			SnapsLogger.appendTextLog("PopupWebViewActivity Url : ", m_szWebViewUrl);

		} catch (Exception e) {
			Dlog.e(TAG, e);
		}

		SnapsMenuManager menuManager = SnapsMenuManager.getInstance();
        if (menuManager != null) {
            menuManager.setPopupWebviewActivity(this);
        }

		GoHomeOpserver.addGoHomeListener(this);
	}

	public boolean isFinished() {
		return isFinishedActivity;
	}

	void chageTitle(String title) {
		if (title != null && !title.equals("")) {
			UI.<TextView> findViewById(this, R.id.txtTitleText).setText(title);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		unregisterReceiver(receiver);
		receiver = null;

		GoHomeOpserver.removeGoHomeListenrer(this);

		isFinishedActivity = true;

		if (progressWebview != null && progressWebview.getWebView() != null) {
			progressWebview.getWebView().destroy();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (progressWebview != null && progressWebview.getWebView() != null) {
			progressWebview.getWebView().resumeTimers();
		}

		isFinishedActivity = false;
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (progressWebview != null && progressWebview.getWebView() != null) {
			progressWebview.getWebView().pauseTimers();
		}
	}

	@Override
	public void onBackPressed() {
		if (progressWebview != null && progressWebview.getJsResut() != null) {
			progressWebview.getJsResut().cancel();
		}

		finishActivity();
	}

	private void checkHomeKey() {
		mHomeBtn.setVisibility(View.INVISIBLE);
		mBackBtn.setVisibility(View.VISIBLE);
		findViewById(R.id.btnTitleLeftBackLy).setVisibility(View.VISIBLE);
		findViewById(R.id.btnTitleLeftLy).setVisibility(View.INVISIBLE);
	}

	void finishActivity() {
		if (progressWebview.canGoBack()) {
			String title = getIntent().getStringExtra(Const_EKEY.WEBVIEW_TITLE);
			if (title != null)
				chageTitle(title);
			progressWebview.goBack();

		} else {
			finish();
		}
	}

	public void onClick(View v) {
		if (v.getId() == R.id.btnTitleLeftBack
				|| (v.getId() == R.id.btnTitleLeftBackLy))
			finishActivity();
		else if (v.getId() == R.id.btnTitleLeft
				|| (v.getId() == R.id.btnTitleLeftLy)) {
			finishActivity();
		}
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		if (!SnapsTPAppManager.isThirdPartyApp(this) && facebook != null)
			facebook.addCallback();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();

		if (!SnapsTPAppManager.isThirdPartyApp(this) && facebook != null)
			facebook.removeCallback();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		//파일 첨부에서 선택한 사진을 서버로 전송한다.
		if(requestCode == WebviewActivity.REQUEST_CODE_OPEN_GALLERY) {
			if(data != null) {
				Uri uri = data.getData();
				FileUtil.sendAttachFile(this, uri, new OnFileUploadListener() {
					@Override
					public void onFinished(String url) {
						if(url == null || url.length() < 1) {
							MessageUtil.toast(PopupWebviewActivity.this, getString(R.string.failed_upload_plz_retry)); //"업로드에 실패 했습니다.\n잠시 후 다시 시도 바랍니다.");
							return;
						}

						String cmd = sp.getFileAttachCMD(url);
						if(cmd != null && progressWebview != null)
							progressWebview.loadUrl(cmd);
					}
				});
			}
		}
		super.onActivityResult(requestCode, resultCode, data);

		if(!SnapsTPAppManager.isThirdPartyApp(this))
			SNSShareUtil.postActivityForResult(requestCode, resultCode, data);

		if (facebook != null)
			facebook.onActivityResult(this, requestCode, resultCode, data);
	}



	@Override
	public void onPageStarted(String url) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageFinished(String url) {
		String title = StringUtil.getTitleAtUrl(url, "naviBarTitle");
		if (title == null || title.length() < 1)
			title = getIntent().getStringExtra(Const_EKEY.WEBVIEW_TITLE);
		if (title != null) {
			//프리미엄 아이콘 확인.
			chageTitle(title);
		}
	}

	@Override
	public void onGoHome() {
		finish();
	}

	@Override
	public void onCloseMenu() {
	}

	class SnapsWebViewReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			try {
				final String action = intent.getAction();

				if (action != null
						&& TextUtils.equals(action, Const_VALUE.RELOAD_URL)) {
					String userNo = Setting.getString(context,
							Const_VALUE.KEY_SNAPS_USER_NO);
					String loadurl = intent.getStringExtra("reloadurl");// ==
																		// null
																		// ?
																		// "/mw/event/friend_invite.jsp"
																		// :
																		// reLoadUrl;
					String url = SnapsAPI.WEB_DOMAIN(loadurl, userNo, "");
					if (progressWebview != null)
						progressWebview.loadUrl(url);
				}
			} catch (Exception e) {
				Dlog.e(TAG, e);
			}
		}
	}
}
