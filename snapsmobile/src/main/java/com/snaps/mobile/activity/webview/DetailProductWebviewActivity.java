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
import com.snaps.common.utils.ui.DataTransManager;
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
import com.snaps.mobile.activity.home.fragment.HomeMenuBase;
import com.snaps.mobile.activity.home.fragment.HomeMenuBase.OnSlideMenuLitener;
import com.snaps.mobile.activity.ui.menu.renewal.MenuDataManager;
import com.snaps.mobile.activity.ui.menu.renewal.model.SubCategory;
import com.snaps.mobile.activity.ui.menu.webinterface.SnapsShouldOverrideUrlLoader;
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

public class DetailProductWebviewActivity extends SnapsBaseFragmentActivity
		implements OnPageLoadListener, OnSlideMenuLitener, GoHomeOpserver.OnGoHomeOpserver
{
	private static final String TAG = DetailProductWebviewActivity.class.getSimpleName();
	public final static String KEY_ANIMATE_TITLE = "need to animate title layout";
	public final static String KEY_ANIMATE_WHEN_FINISH = "need to animate when finish activity";
	public final static String KEY_WEBVIEW_TYPE = "webview_type";

	boolean isShow = false;

	ImageView mHomeBtn;
	ImageView mBackBtn;

	public int currentHome = 0;

	ProgressWebView progressWebview = null;
	static public boolean isEnablerefresh = false;

	private HomeMenuBase homeMenuGridFragment = null;
	private View mHomeAlpha;

	private boolean m_isHomeMenu;
	private boolean m_isPresentEvent;

	String m_orientation = null;

	protected SingleTabWebViewController wvController;

	SnapsWebViewReceiver receiver = null;

	String snsShareCallBack = "";

	String m_szWebViewUrl = "";

	private SnapsMenuManager.eHAMBURGER_ACTIVITY eWebViewType;

	public static Intent getIntent(Context context, String title, String url,
			boolean animateTitle, SnapsMenuManager.eHAMBURGER_ACTIVITY webViewType) {
		Intent intent = new Intent(context, DetailProductWebviewActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra(Const_EKEY.WEBVIEW_TITLE, title);
		intent.putExtra(KEY_ANIMATE_TITLE, animateTitle);
		intent.putExtra(KEY_ANIMATE_WHEN_FINISH, false);
		intent.putExtra(Const_EKEY.WEBVIEW_URL, url);
		intent.putExtra(KEY_WEBVIEW_TYPE, webViewType.ordinal());
		intent.putExtra(Const_EKEY.WEBVIEW_PRESENT_URL, false);
		return intent;
	}

	public static Intent getIntent(Context context, String title, String url,
			boolean animateTitle, int intentFlag, SnapsMenuManager.eHAMBURGER_ACTIVITY webViewType) {
		Intent intent = new Intent(context, DetailProductWebviewActivity.class);
		intent.addFlags(intentFlag);
		intent.putExtra(Const_EKEY.WEBVIEW_TITLE, title);
		intent.putExtra(KEY_ANIMATE_TITLE, animateTitle);
		intent.putExtra(KEY_ANIMATE_WHEN_FINISH, false);
		intent.putExtra(Const_EKEY.WEBVIEW_URL, url);
		intent.putExtra(KEY_WEBVIEW_TYPE, webViewType.ordinal());
		intent.putExtra(Const_EKEY.WEBVIEW_PRESENT_URL, false);
		return intent;
	}

	public static Intent getIntent(Context context, String title, String url, SnapsMenuManager.eHAMBURGER_ACTIVITY webViewType) {
		Intent intent = new Intent(context, DetailProductWebviewActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra(Const_EKEY.WEBVIEW_TITLE, title);
		intent.putExtra(KEY_ANIMATE_TITLE, true);
		intent.putExtra(KEY_ANIMATE_WHEN_FINISH, false);
		intent.putExtra(Const_EKEY.WEBVIEW_URL, url);
		intent.putExtra(Const_EKEY.WEBVIEW_PRESENT_URL, false);
		intent.putExtra(KEY_WEBVIEW_TYPE, webViewType.ordinal());
		return intent;
	}

	public static Intent getIntent(Context context, String title, String url,
			boolean animateTitle, boolean animateWhenFinish, SnapsMenuManager.eHAMBURGER_ACTIVITY webViewType) {
		Intent intent = new Intent(context, DetailProductWebviewActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra(Const_EKEY.WEBVIEW_TITLE, title);
		intent.putExtra(KEY_ANIMATE_TITLE, animateTitle);
		intent.putExtra(KEY_ANIMATE_WHEN_FINISH, animateWhenFinish);
		intent.putExtra(Const_EKEY.WEBVIEW_URL, url);
		intent.putExtra(KEY_WEBVIEW_TYPE, webViewType.ordinal());
		intent.putExtra(Const_EKEY.WEBVIEW_PRESENT_URL, false);
		return intent;
	}
	
	public static Intent getIntent(Context context, String title, String url,
			boolean animateTitle, boolean animateWhenFinish, int intentFlag, SnapsMenuManager.eHAMBURGER_ACTIVITY webViewType) {
		Intent intent = new Intent(context, DetailProductWebviewActivity.class);
		intent.addFlags(intentFlag);
		intent.putExtra(Const_EKEY.WEBVIEW_TITLE, title);
		intent.putExtra(KEY_ANIMATE_TITLE, animateTitle);
		intent.putExtra(KEY_ANIMATE_WHEN_FINISH, animateWhenFinish);
		intent.putExtra(Const_EKEY.WEBVIEW_URL, url);
		intent.putExtra(KEY_WEBVIEW_TYPE, webViewType.ordinal());
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

		int webTypeOrdinal = getIntent().getIntExtra(KEY_WEBVIEW_TYPE, -1);
		if (webTypeOrdinal >= 0) {
			eWebViewType = SnapsMenuManager.eHAMBURGER_ACTIVITY.values()[webTypeOrdinal];
		}

		m_orientation = getIntent().getStringExtra("orientation");
		boolean isExistTitleBar = true;
		if (m_orientation != null) {
			isExistTitleBar = false;
			if (m_orientation.equals("h")) {
				UIUtil.updateFullscreenStatus(this, false);
				// portrait
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

			} else if (m_orientation.equals("w")) {
				UIUtil.updateFullscreenStatus(this, true);
				// landscape
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			}
		}
		setContentView(R.layout.activity_theme_webview_detail);

		if (!isExistTitleBar) {
			findViewById(R.id.main_menu_bar).setVisibility(View.GONE);
		}

		isEnablerefresh = false;
		progressWebview = (ProgressWebView) findViewById(R.id.progressWebview);
		ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
		progressBar.setTag(progressWebview.toString());
		progressWebview.setHorizontalProgressBar(progressBar);

		boolean animateTitle = getIntent().getBooleanExtra(KEY_ANIMATE_TITLE,
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

					if (!Config.isSnapsSDK2(DetailProductWebviewActivity.this)) {
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

		//햄버거 메뉴 또는 i버튼의 유무 처리
		setTitleAreaControls();

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
			m_isPresentEvent = getIntent().getBooleanExtra(
					Const_EKEY.WEBVIEW_PRESENT_URL, false);
			DataTransManager dtMan = DataTransManager.getInstance();
			if (dtMan != null) {
				if (m_isPresentEvent)
					dtMan.setShownPresentPage(true);
			} else {
				DataTransManager.notifyAppFinish(this);
				return;
			}

			Dlog.d("onCreate() title:" + title + ", url:" + m_szWebViewUrl);

			// title
			TextView tvTitleText = UI.<TextView> findViewById(this, R.id.txtTitleText);
			FontUtil.applyTextViewTypeface(tvTitleText, FontUtil.eSnapsFonts.YOON_GOTHIC_760);
			tvTitleText.setText(title);

			title = StringUtil.getTitleAtUrl(m_szWebViewUrl, "naviBarTitle");
			if (title != null) {
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
            if( getIntent().getExtras().getBoolean(Const_EKEY.WEBVIEW_LONG_CLICKABLE, false) )
                progressWebview.setWebViewLongClickable( true );

			progressWebview.loadUrl(m_szWebViewUrl);
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}

		initSlidingMenu();

		GoHomeOpserver.addGoHomeListener(this);
	}

	private void setTitleAreaControls() {
		if (eWebViewType == null) return;

		boolean isVisibleInfoBtn = false;
		boolean isVisibleHamburgerBtn = false;

		switch (eWebViewType) {
			case COUPON:
				isVisibleHamburgerBtn = true;
				break;
			case MY_BENEFIT:
				isVisibleHamburgerBtn = true;
				break;
			case ORDER:
				isVisibleHamburgerBtn = true;
				break;
			case CUSTOMER:
				isVisibleHamburgerBtn = true;
				break;
			case NOTICE:
				ImageView btnTitleClose = (ImageView) findViewById(R.id.btnTitleClose);
				btnTitleClose.setVisibility(View.VISIBLE);
				btnTitleClose.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						finishActivity();
					}
				});

				if (mBackBtn != null)
					mBackBtn.setVisibility(View.GONE);
				if (mHomeBtn != null)
					mHomeBtn.setVisibility(View.GONE);
				break;
			case EVENT:
				String url = getIntent() != null ? getIntent().getStringExtra(Const_EKEY.WEBVIEW_URL) : null;
				String eventMainPageUrl = SnapsMenuManager.getEventMainPageUrl(DetailProductWebviewActivity.this);
				isVisibleHamburgerBtn = url != null && eventMainPageUrl != null && url.equalsIgnoreCase(eventMainPageUrl); //이벤트 서브 페이지는 햄버거 메뉴 노출 안 함.
				break;
			case PRODUCT_DETAIL_PAGE:
				isVisibleInfoBtn = Config.useKorean();
				break;
		}

		if (wvController != null) {
			wvController.setIsTitleSlideEnable(false);
		}

		ImageView infoBtn = (ImageView) findViewById(R.id.btnTitleInfo);
		ImageView hamburgerBtn = (ImageView) findViewById(R.id.btnTitleHamburgerMenu);

		hamburgerBtn.setVisibility(View.GONE);
//		if (isVisibleHamburgerBtn) {
//			hamburgerBtn.setVisibility(View.VISIBLE);
//			hamburgerBtn.setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					SnapsMenuManager.showHamburgerMenu(DetailProductWebviewActivity.this, eWebViewType);
//				}
//			});
//			hamburgerBtn.invalidate();
//		}

		if (isVisibleInfoBtn) {
			infoBtn.setVisibility(View.VISIBLE);
			infoBtn.setAlpha(1.f);
			infoBtn.setColorFilter(Color.rgb(0, 0, 0));
			infoBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					startProductInfoPage();
				}
			});
		}
	}

	private void startProductInfoPage() {
		MenuDataManager menuDataManager = MenuDataManager.getInstance();
		if (menuDataManager == null) return;

		SnapsMenuManager menuManager = SnapsMenuManager.getInstance();
		SubCategory subCategory = menuManager.getSubCategory();
		if (subCategory == null) return;

		String infoUrl = subCategory.getInfoUrl();

		SnapsShouldOverrideUrlLoader shouldOverrideUrlLoder = new SnapsShouldOverrideUrlLoader(this, SnapsShouldOverrideUrlLoader.WEB);
		shouldOverrideUrlLoder.shouldOverrideUrlLoading(infoUrl);
	}

	private void initSlidingMenu() {
	}

	void chageTitle(String title) {
		if (title != null && !title.equals("")) {
			UI.<TextView> findViewById(this, R.id.txtTitleText).setText(title);
		} else {

		}
	}

	void checkPremiumDesign(String url) {
		boolean isOption = url.contains("#");
		String yn = StringUtil.getTitleAtUrl(url, "F_OUTER_YORN");
		if (yn != null && yn.equals("Y") && !isOption) {
			findViewById(R.id.btnPremium).setVisibility(View.VISIBLE);
			findViewById(R.id.btnPremium).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					String openurl = "/mw/v3/store/information/info_premium.jsp";
					openurl = SnapsTPAppManager.getSnapsWebDomain(getApplicationContext(), openurl, SnapsTPAppManager.getBaseQuary(getApplicationContext(), false));
					Intent intent = PopupWebviewActivity.getIntent(DetailProductWebviewActivity.this, openurl);
					if (intent != null)
						DetailProductWebviewActivity.this.startActivity(intent);
				}
			});
		}else{
			findViewById(R.id.btnPremium).setVisibility(View.GONE);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		unregisterReceiver(receiver);
		receiver = null;
		/*
		 * if (webview != null) { webview.postDelayed(new Runnable() {
		 * 
		 * @Override public void run() { try { webview.destroy(); } catch
		 * (Exception ex) { } } }, 3000); }
		 */

		GoHomeOpserver.removeGoHomeListenrer(this);

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

		reloadIfMySnapsPage(); //마이 스냅스는 onResume 에서 무조건 페이지를 갱신 해 준다.
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public void onBackPressed() {
		// super.onBackPressed();

		if (progressWebview != null && progressWebview.getJsResut() != null) {
			progressWebview.getJsResut().cancel();
		}
		finishActivity();
	}

	private void reloadIfMySnapsPage() {
		if (Config.isNeedWebViewRefresh()) {
			Config.setNeedWebViewRefresh(false);
			if (progressWebview != null && progressWebview.getWebView() != null) {
				progressWebview.getWebView().reload();
			}
		}
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
			DataTransManager dtMan = DataTransManager.getInstance();
			if (dtMan != null) {
				if (!m_isPresentEvent && dtMan.isShownPresentPage()) {
					SnapsMenuManager.gotoPresentPage(this, null, null);
					dtMan.setShownPresentPage(false);
				} else {
					dtMan.setShownPresentPage(false);
				}
			}

			requestFinishActivity();
		}
	}

	protected void requestFinishActivity() {
		this.finish();
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
							MessageUtil.toast(DetailProductWebviewActivity.this, getString(R.string.failed_upload_plz_retry)); //"업로드에 실패 했습니다.\n잠시 후 다시 시도 바랍니다.");
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
			checkPremiumDesign(url);
			chageTitle(title);
		}
	}

	@Override
	public void onGoHome() {
		requestFinishActivity();
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
