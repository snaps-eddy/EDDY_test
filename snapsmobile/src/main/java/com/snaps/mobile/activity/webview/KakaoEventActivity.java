package com.snaps.mobile.activity.webview;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;

import com.snaps.common.structure.SnapsHandler;
import com.snaps.common.utils.ISnapsHandler;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_EKEY;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.constant.Const_VALUES;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.pref.Setting;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.hamburger_menu.SnapsMenuManager;
import com.snaps.mobile.activity.home.KakaoDialogFragment2;
import com.snaps.mobile.activity.home.utils.SnapsLoginManager;
import com.snaps.mobile.interfaces.ImpKakaoDialogListener;
import com.snaps.mobile.utils.pref.PrefUtil;

import errorhandle.logger.Logg;
import errorhandle.logger.SnapsLogger;
import errorhandle.logger.model.SnapsLoggerClass;

public class KakaoEventActivity extends DetailProductWebviewActivity implements ImpKakaoDialogListener, ISnapsHandler {
	private static final String TAG = KakaoEventActivity.class.getSimpleName();

	public static Intent getIntent(Context context, String title, String url, boolean isPresentPage) {
		Intent intent = new Intent(context, KakaoEventActivity.class);
		intent.putExtra(Const_EKEY.WEBVIEW_TITLE, title);
		intent.putExtra(Const_EKEY.WEBVIEW_URL, url);
		intent.putExtra(Const_EKEY.WEBVIEW_PRESENT_URL, isPresentPage);
		intent.putExtra(KEY_WEBVIEW_TYPE, SnapsMenuManager.eHAMBURGER_ACTIVITY.EVENT.ordinal());
        intent.putExtra( Const_EKEY.WEBVIEW_LONG_CLICKABLE, false );
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
		return intent;
	}

    public static Intent getIntent(Context context, String title, String url, boolean isPresentPage, boolean setLongClickEnable) {
        Intent intent = new Intent(context, KakaoEventActivity.class);
        intent.putExtra(Const_EKEY.WEBVIEW_TITLE, title);
        intent.putExtra(Const_EKEY.WEBVIEW_URL, url);
        intent.putExtra(Const_EKEY.WEBVIEW_PRESENT_URL, isPresentPage);
        intent.putExtra(KEY_WEBVIEW_TYPE, SnapsMenuManager.eHAMBURGER_ACTIVITY.EVENT.ordinal());
        intent.putExtra( Const_EKEY.WEBVIEW_LONG_CLICKABLE, setLongClickEnable );
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return intent;
    }

	String reLoadUrl = null;
	SnapsWebViewReceiver receiver = null;

	SnapsHandler snapsHandler = null;

	public static String LOGIN_AFTER_CMD = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SnapsLogger.appendClassTrackingLog(new SnapsLoggerClass<>(this));
		SnapsLogger.appendTextLog(getLogForDebug());

		reLoadUrl = null;
		LOGIN_AFTER_CMD = "";

		snapsHandler = new SnapsHandler(this);

		IntentFilter filter = new IntentFilter(Const_VALUE.RELOAD_URL);
		receiver = new SnapsWebViewReceiver();
		registerReceiver(receiver, filter);

		IntentFilter filterkakao = new IntentFilter(Const_VALUE.KAKAOLOING_ACTION);
		registerReceiver(receiver, filterkakao);
	}

	private String getLogForDebug() {
		try {
			String title = getIntent().getStringExtra(Const_EKEY.WEBVIEW_TITLE);
			String webViewUrl = getIntent().getStringExtra(Const_EKEY.WEBVIEW_URL);
			String orientation = getIntent().getStringExtra("orientation");
			String detailIndex = getIntent().getStringExtra("detailindex");

			StringBuilder builder = new StringBuilder();
			builder.append("title : ").append(title).append(", ");
			builder.append("webViewUrl : ").append(webViewUrl).append(", ");
			builder.append("orientation : ").append(orientation).append(", ");
			builder.append("detailIndex : ").append(detailIndex);
			return builder.toString();
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
		return "";
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		unregisterReceiver(receiver);
		receiver = null;

//			PrefUtil.clearKakaoEvent(this);
	}

	@Override
	protected void requestFinishActivity() {
		super.requestFinishActivity();
		PrefUtil.clearKakaoEvent(this);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();

		PrefUtil.clearKakaoEvent(this);
	}

	@Override
	protected void onResume() {
		try {
			super.onResume();
			if (isEnablerefresh) {
				isEnablerefresh = false;
			}

			// 결과값이 있거나.. 카카오 데이터 정보가 있는경우 팝업을 띄어준다.
			if (!Config.getKAKAO_EVENT_SENDNO().equals("") && Config.getKAKAO_EVENT_RESULT2() != null) {
				if (snapsHandler != null) {
					snapsHandler.sendEmptyMessageDelayed(HANDLE_MSG_SHOW_EVENT_DLG, 300);
				}
			}
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	void showEventDlg() {
		SnapsLogger.appendTextLog("KakaoEvent Point", "3");
		if( (Build.VERSION.SDK_INT  > 16 && isDestroyed()) || isFinishing() ) return;
		SnapsLogger.appendTextLog("KakaoEvent Point", "4");

		if (Config.getKAKAO_EVENT_RESULT2() != null) {
			if (Config.getKAKAO_EVENT_RESULT2().equals("devicedual")) {
				WebViewDialogOneBtn wdia = new WebViewDialogOneBtn(this, "", getResources().getString(R.string.kakao_event_error_dualdevice));
				wdia.setCancelable(false);
				wdia.show();
			} else if (Config.getKAKAO_EVENT_RESULT2().equals("success")) {

				String msg = getResources().getString(R.string.kakao_event_suceess);
				String msg2 = getResources().getString(R.string.kakao_coupon_expiration);

				SpannableString sText = new SpannableString(msg + msg2);
				sText.setSpan(new RelativeSizeSpan(0.7f), msg.length(), msg.length() + msg2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				WebViewDialogOneBtn wdia = new WebViewDialogOneBtn(this, "", sText);
				wdia.setCancelable(false);
				wdia.show();

			} else if (Config.getKAKAO_EVENT_RESULT2().equals("dual")) {
				WebViewDialogOneBtn wdia = new WebViewDialogOneBtn(this, "", getResources().getString(R.string.kakao_event_error_dual));
				wdia.setCancelable(false);
				wdia.show();
			} else if (Config.getKAKAO_EVENT_RESULT2().equals("same")) {
				WebViewDialogOneBtn wdia = new WebViewDialogOneBtn(this, "", getResources().getString(R.string.kakao_event_error_dual));
				wdia.setCancelable(false);
				wdia.show();

			} else if (Config.getKAKAO_EVENT_RESULT2().equals("expr")) {
				WebViewDialogOneBtn wdia = new WebViewDialogOneBtn(this, "", getResources().getString(R.string.event_finished));
				wdia.setCancelable(false);
				wdia.show();

			} else if (Config.getKAKAO_EVENT_RESULT2().equals("login")) {
				KakaoDialogFragment2 dlg = null;
				dlg = KakaoDialogFragment2.newInstance();
				dlg.setListener(this);
				getFragmentManager().beginTransaction().add(dlg, "kakao").commitAllowingStateLoss();
			} else if (Config.getKAKAO_EVENT_RESULT2().equals("fail")) {

			} else if (Config.getKAKAO_EVENT_RESULT2().equals("false")) {

			}
		}

		// 확인이 되면 결과값 초기화..
//		Config.KAKAO_EVENT_SENDNO = null;
		Config.setKAKAO_EVENT_RESULT2(null);

	}
	@Override
	public void OnClickListenr(int clickKinds) {
		if (clickKinds == 10) { // 로그인 화
//			Intent intent = new Intent(this, LoginProcessActivity.class);
//			intent.putExtra(Const_EKEY.LOGIN_PROCESS, Const_VALUES.LOGIN_P_LOGIN);
//			IntentUtil.startActivity(this, intent);
			SnapsLoginManager.startLogInProcess(this, Const_VALUES.LOGIN_P_LOGIN);
		} else if (clickKinds == 20) { // 회원가입 화면..
//			Intent intent = new Intent(this, LoginProcessActivity.class);
//			intent.putExtra(Const_EKEY.LOGIN_PROCESS, Const_VALUES.LOGIN_P_JOIN);
//			IntentUtil.startActivity(this, intent);
			SnapsLoginManager.startLogInProcess(this, Const_VALUES.LOGIN_P_JOIN);
		}

	}

	class SnapsWebViewReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();

			if (action != null && TextUtils.equals(action, Const_VALUE.RELOAD_URL)) {
				reLoadUrl = intent.getStringExtra("reloadurl");

				SnapsLogger.appendTextLog("SnapsWebViewReceiver reLoadUrl", reLoadUrl);

				String userNo = Setting.getString(context, Const_VALUE.KEY_SNAPS_USER_NO);
				String loadurl = reLoadUrl;// == null ? "/mw/event/friend_invite.jsp" : reLoadUrl;
				String url = SnapsAPI.WEB_DOMAIN(loadurl, userNo, "");
				progressWebview.loadUrl(url, true);
			} else if (action != null && TextUtils.equals(action, Const_VALUE.KAKAOLOING_ACTION)) {

				SnapsLogger.appendTextLog("SnapsWebViewReceiver KAKAOLOING_ACTION", "?");

				// progressWebview.loadUrl(m_szWebViewUrl, true);

				if (!LOGIN_AFTER_CMD.equals("")) {
					progressWebview.loadUrl(LOGIN_AFTER_CMD);
				}
			}
		}

	}

	private static final int HANDLE_MSG_SHOW_EVENT_DLG = 0;

	@Override
	public void handleMessage(Message msg) {
		switch (msg.what) {
			case HANDLE_MSG_SHOW_EVENT_DLG:
				showEventDlg();
				break;
		}
	}
}
