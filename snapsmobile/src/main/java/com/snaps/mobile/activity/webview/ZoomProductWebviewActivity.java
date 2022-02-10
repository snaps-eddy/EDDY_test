package com.snaps.mobile.activity.webview;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
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
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.DataTransManager;
import com.snaps.common.utils.ui.FontUtil;
import com.snaps.common.utils.ui.IFacebook;
import com.snaps.common.utils.ui.IKakao;
import com.snaps.common.utils.ui.SingleTabWebViewController;
import com.snaps.common.utils.ui.SnsFactory;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.common.utils.ui.UI;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.kakao.utils.share.SNSShareUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.hamburger_menu.SnapsMenuManager;
import com.snaps.mobile.activity.home.fragment.GoHomeOpserver;
import com.snaps.mobile.activity.home.fragment.GoHomeOpserver.OnGoHomeOpserver;
import com.snaps.mobile.activity.home.fragment.HomeMenuBase;
import com.snaps.mobile.activity.home.fragment.HomeMenuBase.OnSlideMenuLitener;
import com.snaps.mobile.base.SnapsBaseFragmentActivity;
import com.snaps.mobile.component.ProgressWebView;
import com.snaps.mobile.component.SnapsWebviewProcess;
import com.snaps.mobile.interfaces.OnPageLoadListener;
import com.snaps.mobile.utils.thirdparty.SnapsTPAppManager;

import errorhandle.logger.Logg;
import errorhandle.logger.SnapsLogger;
import errorhandle.logger.model.SnapsLoggerClass;


public class ZoomProductWebviewActivity extends SnapsBaseFragmentActivity implements OnPageLoadListener, OnSlideMenuLitener, OnGoHomeOpserver {
	private static final String TAG = ZoomProductWebviewActivity.class.getSimpleName();

	public final static String KEY_ANIMATE_TITLE = "need to animate title layout";
	public final static String KEY_ANIMATE_WHEN_FINISH = "need to animate when finish activity";

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

	public static Intent getIntent( Context context, String title, String url, boolean animateTitle ) {
		Intent intent = new Intent(context, ZoomProductWebviewActivity.class);
		intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
		intent.putExtra(Const_EKEY.WEBVIEW_TITLE, title);
		intent.putExtra(KEY_ANIMATE_TITLE, animateTitle);
		intent.putExtra(KEY_ANIMATE_WHEN_FINISH, false);
		intent.putExtra(Const_EKEY.WEBVIEW_URL, url);
		return intent;
	}
	
	public static Intent getIntent( Context context, String title, String url ) {
		Intent intent = new Intent(context, ZoomProductWebviewActivity.class);
		intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
		intent.putExtra(Const_EKEY.WEBVIEW_TITLE, title);
		intent.putExtra(KEY_ANIMATE_TITLE, true);
		intent.putExtra(KEY_ANIMATE_WHEN_FINISH, false);
		intent.putExtra(Const_EKEY.WEBVIEW_URL, url);
		return intent;
	}
	
	public static Intent getIntent( Context context, String title, String url, boolean animateTitle, boolean animateWhenFinish ) {
		Intent intent = new Intent(context, ZoomProductWebviewActivity.class);
		intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
		intent.putExtra(Const_EKEY.WEBVIEW_TITLE, title);
		intent.putExtra(KEY_ANIMATE_TITLE, animateTitle);
		intent.putExtra(KEY_ANIMATE_WHEN_FINISH, animateWhenFinish);
		intent.putExtra(Const_EKEY.WEBVIEW_URL, url);
		return intent;
	}

	private IKakao kakao = null;
	private IFacebook facebook = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
			getWindow().setFlags(
					WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
					WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
		}

		super.onCreate(savedInstanceState);

		SnapsLogger.appendClassTrackingLog(new SnapsLoggerClass<>(this));

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

		try {
			setContentView(R.layout.activity_theme_webview_detail);
		} catch (OutOfMemoryError e) {
			Dlog.e(TAG, e);
			finishActivity();
			return;
		}

		if (!isExistTitleBar) {
			findViewById(R.id.main_menu_bar).setVisibility(View.GONE);
		}

		isEnablerefresh = false;
		progressWebview = (ProgressWebView) findViewById( R.id.progressWebview );
		ProgressBar progressBar = (ProgressBar) findViewById( R.id.progressBar );

		try {
			Drawable drawable = getResources().getDrawable(R.drawable.smart_page_simple_progress);
			progressBar.setIndeterminateDrawable(drawable);
		} catch (OutOfMemoryError e) { Dlog.e(TAG, e); }

		progressBar.setTag( progressWebview.toString() );
		progressWebview.setHorizontalProgressBar( progressBar );
		
		boolean animateTitle = getIntent().getBooleanExtra( KEY_ANIMATE_TITLE, true );
		if( animateTitle ) {
			wvController = new SingleTabWebViewController();
			wvController.setView( (RelativeLayout)findViewById(R.id.main_menu_bar) );
			progressWebview.setPageScrollListner( wvController );
			progressWebview.setOnPageLoadListener( this );
		}

		RelativeLayout.LayoutParams layoutParams =  (RelativeLayout.LayoutParams) progressWebview.getLayoutParams();
		layoutParams.topMargin = 0;
		progressWebview.setLayoutParams(layoutParams);

		mHomeBtn = (ImageView) findViewById(R.id.btnTitleLeft);
		mBackBtn = (ImageView) findViewById(R.id.btnTitleLeftBack);

		mHomeAlpha = (View) findViewById(R.id.alpha50);
		mHomeAlpha.setBackgroundColor(Color.argb(125, 0, 0, 0));
		mHomeAlpha.bringToFront();

		mHomeAlpha.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (event.getAction() == MotionEvent.ACTION_DOWN)

					if (!Config.isSnapsSDK2(ZoomProductWebviewActivity.this)) {
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
			String url = getIntent().getStringExtra(Const_EKEY.WEBVIEW_URL);
			m_isPresentEvent = getIntent().getBooleanExtra(Const_EKEY.WEBVIEW_PRESENT_URL, false);
			DataTransManager dtMan = DataTransManager.getInstance();
			if (dtMan != null) {
				if (m_isPresentEvent)
					dtMan.setShownPresentPage(true);
			} else {
				DataTransManager.notifyAppFinish(this);
				return;
			}

			Dlog.d("onCreate() title:" + title + ", url:" + url);

			// title
			TextView tvTitleText = UI.<TextView> findViewById(this, R.id.txtTitleText);
			FontUtil.applyTextViewTypeface(tvTitleText, FontUtil.eSnapsFonts.YOON_GOTHIC_760);
			tvTitleText.setText(title);

			title = StringUtil.getTitleAtUrl(url, "naviBarTitle");
			if (title != null) {
				chageTitle(title);
			}

			// 공지사항인 경우..
			String webIndex = getIntent().getExtras().getString("detailindex");

			// detailindex값이 없는경우 배송조회가 된다.
			if (webIndex != null && !webIndex.equals("")) {
				url += "&seq=" + webIndex;
			}
			
			//디바이스 정보를 넣어준다.
			url = StringUtil.addUrlParameter(url, "deviceModel=" + Build.MODEL);

			SnapsWebviewProcess sp = new SnapsWebviewProcess(this, facebook, kakao);
			progressWebview.addWebviewProcess(sp);

			progressWebview.setOnPageLoadListener(this);
			progressWebview.loadUrl(url);

		} catch (Exception e) {
			Dlog.e(TAG, e);
		}

		initSlidingMenu();

		GoHomeOpserver.addGoHomeListener(this);
	}

	private void initSlidingMenu() {
	}

	void chageTitle(String title) {
		if (title != null && !title.equals("")) {
			UI.<TextView> findViewById(this, R.id.txtTitleText).setText(title);
		} else {

		}

		checkHomeKey();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		/*
		 * if (webview != null) { webview.postDelayed(new Runnable() {
		 * 
		 * @Override public void run() { try { webview.destroy(); } catch (Exception ex) { } } }, 3000); }
		 */
		if (progressWebview != null && progressWebview.getWebView() != null) {
			progressWebview.getWebView().destroy();
		}

		GoHomeOpserver.removeGoHomeListenrer(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (progressWebview != null && progressWebview.getWebView() != null) {
			progressWebview.getWebView().resumeTimers();
		}
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
		if( progressWebview != null && progressWebview.getJsResut() != null ) {
			progressWebview.getJsResut().cancel();
		}
		finishActivity();
	}

	private void checkHomeKey() {
			String title = UI.<TextView> findViewById(this, R.id.txtTitleText).getText().toString();
			final String[] arHomeMenuTitles = {getString(R.string.store), getString(R.string.cart), getString(R.string.order_and_delivery), getString(R.string.manage_coupons)};

			m_isHomeMenu = false;

			if (!m_isPresentEvent) {
				for (String t : arHomeMenuTitles) {
					if (t.equals(title)) {
						m_isHomeMenu = true;
						break;
					}
				}
			}

			if (m_isHomeMenu) {
				mHomeBtn.setImageResource(R.drawable.btn_top_menu);
				mHomeBtn.setVisibility(View.VISIBLE);
				mBackBtn.setVisibility(View.INVISIBLE);
				RelativeLayout backBtnLy = (RelativeLayout) findViewById(R.id.btnTitleLeftBackLy);
				backBtnLy.setVisibility(View.INVISIBLE);
				findViewById(R.id.btnTitleLeftLy).setVisibility(View.VISIBLE);
				findViewById(R.id.btnTitleLeftBackLy).setVisibility(View.INVISIBLE);
			} else {
				mHomeBtn.setVisibility(View.INVISIBLE);
				mBackBtn.setVisibility(View.VISIBLE);
				findViewById(R.id.btnTitleLeftBackLy).setVisibility(View.VISIBLE);
				findViewById(R.id.btnTitleLeftLy).setVisibility(View.INVISIBLE);
			}
	}

	void finishActivity() {
		if (progressWebview.canGoBack()) {
			String title = getIntent().getStringExtra(Const_EKEY.WEBVIEW_TITLE);
			if (title != null) chageTitle(title);
			progressWebview.goBack();

		} else {			
			DataTransManager dtMan = DataTransManager.getInstance();
			if (dtMan != null) {
				if (!m_isPresentEvent && dtMan.isShownPresentPage()) {
					SnapsMenuManager.gotoPresentPage(this, null, null);
					return;
				}
				dtMan.setShownPresentPage(false);
			} else {
				DataTransManager.notifyAppFinish(this);
				return;
			}
			
			finish();
			if( getIntent().getBooleanExtra(KEY_ANIMATE_WHEN_FINISH, false) ) overridePendingTransition( R.anim.slide_in_from_left, R.anim.slide_out_to_right );
		}
	}

	public void onClick(View v) {
		if (v.getId() == R.id.btnTitleLeftBack || (v.getId() == R.id.btnTitleLeftBackLy))
			finishActivity();
		else if (v.getId() == R.id.btnTitleLeft || (v.getId() == R.id.btnTitleLeftLy)) {
			if( Config.isSnapsSDK2(this) ) return;
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
		
		if(!SnapsTPAppManager.isThirdPartyApp(this))
			SNSShareUtil.postActivityForResult(requestCode, resultCode, data);
	}

	@Override
	public void onPageStarted(String url) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageFinished(String url) {
		String title = StringUtil.getTitleAtUrl(url, "naviBarTitle");
		if( title == null || title.length() < 1 ) title = getIntent().getStringExtra( Const_EKEY.WEBVIEW_TITLE );
		if (title != null) {
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
}
