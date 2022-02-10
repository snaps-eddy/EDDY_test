package com.snaps.mobile.activity.hamburger_menu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_EKEY;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.constant.Const_VALUES;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.pref.Setting;
import com.snaps.common.utils.ui.DataTransManager;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.diary.activities.SnapsDiaryMainActivity;
import com.snaps.mobile.activity.home.fragment.GoHomeOpserver;
import com.snaps.mobile.activity.home.ui_strategies.HomeUIHandler;
import com.snaps.mobile.activity.home.utils.SnapsEventHandler;
import com.snaps.mobile.activity.home.utils.SnapsLoginManager;
import com.snaps.mobile.activity.intro.IAfterLoginProcess;
import com.snaps.mobile.activity.ui.menu.renewal.model.Item;
import com.snaps.mobile.activity.ui.menu.renewal.model.SubCategory;
import com.snaps.mobile.activity.webview.DetailProductWebviewActivity;
import com.snaps.mobile.activity.webview.KakaoEventActivity;
import com.snaps.mobile.activity.webview.PopupWebviewActivity;
import com.snaps.mobile.activity.webview.WebViewDialogOneBtn;
import com.snaps.mobile.autosave.AutoSaveManager;
import com.snaps.mobile.utils.pref.PrefUtil;
import com.snaps.mobile.utils.thirdparty.SnapsTPAppManager;

import java.util.ArrayList;

import errorhandle.SnapsAssert;

public class SnapsMenuManager {
	private static final String TAG = SnapsMenuManager.class.getSimpleName();
	public static final String URL_EVENT_PAGE_MAIN = "/mw/v3/event/index.jsp";

	private static volatile SnapsMenuManager gInstance = null;

	private SubCategory subCategory = null;
	private UIStickyInfo stickyInfo = null;
	private String subPageBaseUrl = null;
	private PopupWebviewActivity popupWebviewActivity = null;

	public enum eHAMBURGER_ACTIVITY {
		HOME,
		EVENT,
		DIARY,
		CUSTOMER,
		SETTING,
		MY_BENEFIT,
		ORDER,
		CART,
		COUPON,
		NOTICE,
		PRODUCT_DETAIL_PAGE,
		ETC
	}

	public enum eHAMBUGER_FRAGMENT {
		MAIN_MENU,
		LOG_IN,
		JOIN,
		RETIRE,
		PWD_RESET,
		PWD_FIND,
		VERIFY_PHONE,
		VERIFY_PHONE_POPUP,
		REST_ID
	}

	public static void createInstance() {
		if (gInstance ==  null) {
			synchronized (SnapsMenuManager.class) {
				if (gInstance ==  null) {
					gInstance = new SnapsMenuManager();
				}
			}
		}
	}

	public static SnapsMenuManager getInstance() {
		if(gInstance ==  null)
			createInstance();

		return gInstance;
	}

	public static void dismissDialogFragment() {
		PopupWebviewActivity dialogFragment = getInstance().getPopupWebViewActivity();
		if (dialogFragment != null && !dialogFragment.isFinishing()) {
			dialogFragment.finish();
			getInstance().setPopupWebviewActivity(null);
		}
	}

	/***
	 * 상품이 꼬이는 경우 해결코드.. 포토북 => 래이플랫북..
	 */
	public static void initPage() throws Exception {
		//자동 복구 중에 해당 메서드가 호출 된다면, 복구 된 정보가 모두 삭제 된다.
		AutoSaveManager saveManager = AutoSaveManager.getInstance();
		if (saveManager != null && saveManager.isRecoveryMode()) return;

		//Config.setPAPER_CODE("");
		Config.cleanProductInfo();

		SnapsMenuManager menuMan = SnapsMenuManager.getInstance();
		if(menuMan != null) {
			menuMan.setSubCategory(null);
		}
	}

	public static void finalizeInstance() {
		gInstance = null;
	}

	private SnapsMenuManager() {}

	public static void showHamburgerMenu(Activity activity,	eHAMBURGER_ACTIVITY where) {
		if (activity == null) return;

		Intent intent = new Intent(activity, SnapsHamburgerMenuActivity.class);
		intent.putExtra(Const_VALUES.EXTRAS_HAMBURGER_MENU_ACT, where != null ? where.ordinal() : -1);

		activity.startActivity(intent);
	}

	/***
	 * 주문배송 화면으로 이동을 한다.
	 *
	 * @param con
	 */
	public static void gotoOrderDelivery(Context con) {
		String url = SnapsTPAppManager.getOrderUrl(con);
		Intent intent = DetailProductWebviewActivity.getIntent(con, con.getString(R.string.order_and_delivery), url, SnapsMenuManager.eHAMBURGER_ACTIVITY.ORDER);
		con.startActivity(intent);
	}


	public static void gotoCouponAct(Context con, String url) {
		Intent intent = DetailProductWebviewActivity.getIntent(con, con.getString(R.string.manage_coupons), url, true, SnapsMenuManager.eHAMBURGER_ACTIVITY.COUPON);
		con.startActivity(intent);
	}

	/***
	 * 공지사항 화면으로 이동하게 하는 함수..
	 *
	 */
	public static void goToNoticeList(Context con, String url, String index) {
		Intent intent = DetailProductWebviewActivity.getIntent(con, con.getString(R.string.notice), url, true, Intent.FLAG_ACTIVITY_NEW_TASK, SnapsMenuManager.eHAMBURGER_ACTIVITY.NOTICE);

		if (index != null)
			intent.putExtra("detailindex", index);

		if (intent != null)
			con.startActivity(intent);

	}

	public static void goToCartList(Activity activity, HomeUIHandler homeUIHandler) {
		try {
			sendPageEventTracker( activity, R.string.action_cart_page );

			int _cart_count = homeUIHandler.getHomeUIData().get_cart_count();
			if ("".equals(SnapsLoginManager.getUUserNo(activity))) {
				final String snapsUserId = Setting.getString(activity, Const_VALUE.KEY_SNAPS_USER_ID);
				final String snapsUserPwd = Setting.getString(activity, Const_VALUE.KEY_SNAPS_USER_PWD);

				if (!snapsUserId.equals("") && !snapsUserPwd.equals("")) {
					SnapsTPAppManager.gotoCartList(activity, _cart_count, activity.getString(R.string.cart), "");
				} else {
					SnapsLoginManager.startLogInProcess(activity, Const_VALUES.LOGIN_P_LOGIN);
				}
			} else {
				SnapsTPAppManager.gotoCartList(activity, _cart_count, activity.getString(R.string.cart), "");
			}
		} catch (Exception e) {
			Dlog.e(TAG, e);
			SnapsAssert.assertException(activity, e);
		}
	}

	/***
	 * 일기화면으로 이동하기...
	 */
	public static void gotoDiaryList(Activity activity) {
		sendPageEventTracker( activity, R.string.action_diary );

		if (!SnapsLoginManager.isLogOn(activity)) {
			Bundle bundle = new Bundle();
			bundle.putInt(Const_EKEY.LOGIN_AFTER_WHERE, IAfterLoginProcess.MOVE_TO_DIARY_MAIN);
			SnapsLoginManager.startLogInProcess(activity, Const_VALUES.LOGIN_P_LOGIN, bundle);
			return;
		}

		Intent diaryItt = new Intent(activity, SnapsDiaryMainActivity.class);
        diaryItt.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		activity.startActivity(diaryItt);
	}

	public static void gotoPresentPage(Context con, String optionUrl, String title) {
		String url = "";
		if (optionUrl == null)
			url = getEventMainPageUrl(con);
		else {
			if (optionUrl.startsWith("http")) {
				url = optionUrl;
			} else {
				url = SnapsAPI.WEB_DOMAIN(SnapsTPAppManager.isThirdPartyApp(con), optionUrl, SnapsLoginManager.getUUserNo(con));
			}
		}
//		url = UrlUtil.getTopMarginUrl( con, false, url );

		String naviTitle = title == null ? con.getString(R.string.event_title_text) : title;

		Intent intent = KakaoEventActivity.getIntent(con, naviTitle, url, true);
		con.startActivity(intent);
	}

	public static String getEventMainPageUrl(Context context) {
		return SnapsAPI.WEB_DOMAIN(SnapsTPAppManager.isThirdPartyApp(context), URL_EVENT_PAGE_MAIN, SnapsLoginManager.getUUserNo(context));
	}

	private static void sendPageEventTracker( Activity activity, int stringResId ) {
		String str = activity.getString( stringResId );
		if( !StringUtil.isEmpty(str) )
			sendPageEvent( activity, str );
	}

	private static void sendPageEvent( Activity activity, String message ) {
		if( SnapsTPAppManager.isThirdPartyApp(activity) ) return;

		Intent intent = new Intent(Const_VALUE.SNAPS_EVENT_TRACKER_ACTION);

		intent.putExtra("event_category", "페이지 이동");
		intent.putExtra("event_action", message);

		activity.sendBroadcast(intent);
	}

//	public static void gotoKakaoEventPage(Activity activity, String optionUrl) {
//		gotoKakaoEventPage(activity, optionUrl, null);
//	}

	public static boolean gotoKakaoEventPage(Activity activity, String optionUrl, String title) {
		if (!isKakaoEvent(activity))
			return false;

		// 이벤트 데이터 설정...
		Config.setKAKAO_EVENT_RESULT2(Config.getKAKAO_EVENT_RESULT());
		Config.setKAKAO_EVENT_SENDNO(PrefUtil.getKakaoSenderNo(activity));
		Config.setKAKAO_EVENT_CODE(PrefUtil.getKakaoEventCode(activity));
		Config.setKAKAO_EVENT_DEVICEID(PrefUtil.getKakaoDeviceID(activity));

		String url = "";
		if (optionUrl == null)
			url = SnapsAPI.WEB_DOMAIN(SnapsTPAppManager.isThirdPartyApp(activity), "/mw/event/friend_invite.jsp", SnapsLoginManager.getUUserNo(activity));
		else {
			if (optionUrl.startsWith("http")) {
				url = optionUrl;

				String body = (optionUrl.contains("?") ? (optionUrl.endsWith("?") ? "" : "&") : "?");
				if (SnapsLoginManager.isLogOn(activity)) {
					body += String.format("f_chnl_code=%s", Config.getCHANNEL_CODE());
					body += String.format("&f_user_no=%s", SnapsLoginManager.getUUserNo(activity));
					url += body;
				}
			} else {
				url = SnapsAPI.WEB_DOMAIN(SnapsTPAppManager.isThirdPartyApp(activity), optionUrl, SnapsLoginManager.getUUserNo(activity));
			}
		}

		String naviTitle = title == null ? activity.getString(R.string.event_title_text) : title;

		startKakaEventActivity(activity, naviTitle, url);

		// 카카오 데이터 초기화
		Config.setKAKAO_EVENT_CODE(null);
		Setting.set(activity, Const_VALUE.KAKAO_EVENT_OPEN, "");

		return true;
	}

	private static boolean isKakaoEvent(Activity activity) {
		String kakaoOpen = Setting.getString(activity, Const_VALUE.KAKAO_EVENT_OPEN);
		// 이벤트 중이 아닌데 카카오 이벤트가 들어오는 경우..
		if (kakaoOpen != null && kakaoOpen.equals("false") && Config.getKAKAO_EVENT_RESULT() != null && !Config.getKAKAO_EVENT_RESULT().equals("")) {
			WebViewDialogOneBtn wdia = new WebViewDialogOneBtn(activity, activity.getString(R.string.event_finished));
			wdia.setCancelable(false);
			wdia.show();

			return false;
		}

		return true;
	}

	private static void startKakaEventActivity(final Activity activity, final String natititle, final String url) {
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				if( (Build.VERSION.SDK_INT  > 16 && activity.isDestroyed()) || activity.isFinishing() ) return;
				Intent intent = KakaoEventActivity.getIntent(activity, natititle, url, false, true);
				activity.startActivity(intent);
			}
		}, 500);
	}

	public static boolean goToKakaoEventPageIfGetKakaoIntent(Intent intent, @NonNull SnapsEventHandler eventHandler) {
		boolean isKakao = intent.getBooleanExtra("goKakaoEvent", false);
		if (isKakao || Config.getKAKAO_EVENT_RESULT() != null) {
			String url = intent.getStringExtra("eventUrl");
			String navitTitle = intent.getStringExtra("naviTitle");
			return eventHandler.gotoKakaoEventPage(url, navitTitle);
		}
		return false;
	}

	public static void requestFinishPrevActivity() {
		DataTransManager dataTransManager = DataTransManager.getInstance();
		if (dataTransManager != null) {
			dataTransManager.setShownPresentPage(false);
		}

		GoHomeOpserver.notifyGoHome();
	}

	public SubCategory getSubCategory() {
		return subCategory;
	}

	public void setSubCategory(SubCategory subCategory) {
		this.subCategory = subCategory;
	}

	public UIStickyInfo getStickyInfo() {
		return stickyInfo;
	}

	public void setStickyInfo(UIStickyInfo stickyInfo) {
		this.stickyInfo = stickyInfo;
	}

	public String getSubPageBaseUrl() {
		return subPageBaseUrl;
	}

	public void setSubPageBaseUrl(String subPageBaseUrl) {
		this.subPageBaseUrl = subPageBaseUrl;
	}

	public PopupWebviewActivity getPopupWebViewActivity() {
		return popupWebviewActivity;
	}

	public void setPopupWebviewActivity(PopupWebviewActivity popupWebviewActivity) {
		this.popupWebviewActivity = popupWebviewActivity;
	}

	public static class UIStickyInfo {
		private String arrange;
		private String title;
		private String topic;
		private String stickyImage;
		private String infoUrl;
		private String nextPageUrl;
		private ArrayList<Item> arrItems;

		public void clear() {
			arrange = "";
			title = "";
			topic = "";
			stickyImage = "";
			infoUrl = "";
			nextPageUrl = "";

			if (arrItems != null) {
				arrItems.clear();
				arrItems = null;
			}
		}

		public String getArrange() {
			return arrange;
		}

		public void setArrange(String arrange) {
			this.arrange = arrange;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getTopic() {
			return topic;
		}

		public void setTopic(String topic) {
			this.topic = topic;
		}

		public String getStickyImage() {
			return stickyImage;
		}

		public void setStickyImage(String stickyImage) {
			this.stickyImage = stickyImage;
		}

		public String getInfoUrl() {
			return infoUrl;
		}

		public void setInfoUrl(String infoUrl) {
			this.infoUrl = infoUrl;
		}

		public String getNextPageUrl() {
			return nextPageUrl;
		}

		public void setNextPageUrl(String nextPageUrl) {
			this.nextPageUrl = nextPageUrl;
		}

		public ArrayList<Item> getArrItems() {
			return arrItems;
		}

		public void setArrItems(ArrayList<Item> arrItems) {
			this.arrItems = arrItems;
		}
	}

	public enum eMemberGrade {
		NEW("003001"),
		GOLD("003002"),
		VIP("003003"),
		VVIP("003005"),
		THE_FIRST("003014");

		String gradeCode = "";
		eMemberGrade(String gradeCode) {
			this.gradeCode = gradeCode;
		}

		private String getGradeCode() {
			return gradeCode;
		}

		public static eMemberGrade getGrade(String grade) {
			if(grade == null)
				return eMemberGrade.NEW;
			else if(grade.equalsIgnoreCase(GOLD.getGradeCode()))
				return eMemberGrade.GOLD;
			else if(grade.equalsIgnoreCase(VIP.getGradeCode()))
				return eMemberGrade.VIP;
			else if(grade.equalsIgnoreCase(VVIP.getGradeCode()))
				return eMemberGrade.VVIP;
			else if(grade.equalsIgnoreCase(THE_FIRST.getGradeCode()))
				return eMemberGrade.THE_FIRST;
			else
				return eMemberGrade.NEW;
		}

		Drawable getResource(Context context) throws PackageManager.NameNotFoundException {
			if (context == null) return null;
			Context resContext = context.createPackageContext(context.getPackageName(), 0);
			Resources res = resContext.getResources();

			int id = res.getIdentifier("icon_grade_" + getGradeCode(), "drawable", context.getPackageName());
			if (id == 0) {
				return res.getDrawable(R.drawable.icon_grade_new);
			} else
				return res.getDrawable(id);
		}
	}
}
