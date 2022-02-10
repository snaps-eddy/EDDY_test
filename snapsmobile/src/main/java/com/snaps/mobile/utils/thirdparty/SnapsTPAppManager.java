package com.snaps.mobile.utils.thirdparty;

import android.content.Context;
import android.content.Intent;
import android.widget.ImageView;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.snaps.common.data.between.AccessData;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.pref.Setting;
import com.snaps.common.utils.system.SystemUtil;
import com.snaps.common.utils.ui.IFacebook;
import com.snaps.common.utils.ui.IKakao;
import com.snaps.common.utils.ui.IntentUtil;
import com.snaps.common.utils.ui.SnsFactory;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.hamburger_menu.SnapsMenuManager;
import com.snaps.mobile.activity.home.RenewalHomeActivity;
import com.snaps.mobile.activity.home.utils.SnapsLoginManager;
import com.snaps.mobile.activity.webview.DetailProductWebviewActivity;
import com.snaps.mobile.activity.webview.UIWebviewActivity;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import errorhandle.logger.Logg;

public class SnapsTPAppManager {
	private static final String TAG = SnapsTPAppManager.class.getSimpleName();

	private static AppType gAppType = null;

	public static enum AppType {
		SNAPS_MOBILE, SDK, BETWEEN, SNAPS_KOR0033
	}

	public static void initThirdPartyLibrary(Context context) {
		IKakao kakao = SnsFactory.getInstance().queryIntefaceKakao();
		if (kakao != null) {
//			 	Session.initializeSession(this,null, AuthType.KAKAO_STORY);
			kakao.initializeSession(context);
		}

		FacebookSdk.sdkInitialize(context);

		//Facebook developer 에서 추가해 달라는 내용이 있어서 추가 함.
		AppEventsLogger.activateApp(context);

		IFacebook facebook = SnsFactory.getInstance().queryInteface();
		if (facebook != null) {
			facebook.setContext(context);
		}
	}

	public static boolean isThirdPartyApp(Context con) {
		if (Config.isSnapsSDK2(con)) {
			gAppType = AppType.SDK;
		} else if (Config.isSnapsBitween(con)) {
			gAppType = AppType.BETWEEN;
		} else if (Config.getCHANNEL_CODE() != null && Config.getCHANNEL_CODE().equals("KOR0033")) {
			gAppType = AppType.SNAPS_KOR0033;
		} else {
			gAppType = AppType.SNAPS_MOBILE;
		}

		return gAppType != AppType.SNAPS_MOBILE && gAppType != AppType.SNAPS_KOR0033;
	}

	public static String getSnapsWebDomain(Context con, String urlPath,
			String query) {
		if (con == null) {
			return urlPath + ((query != null) ? ("&" + query) : "");
		}

		if (urlPath != null && !urlPath.startsWith("http")) {
			StringBuffer sbUrl = new StringBuffer();
			if (isThirdPartyApp(con)) {
				sbUrl.append(SnapsAPI.WEB_DOMAIN(urlPath, "", SnapsLoginManager.getUUserNo(con)));
			} else {
				sbUrl.append(SnapsAPI.WEB_DOMAIN(urlPath, SnapsLoginManager.getUUserNo(con), ""));
			}
			sbUrl.append("&snapsVer=").append(SystemUtil.getAppVersion(con));
			sbUrl.append(((query != null) ? ("&" + query) : ""));
			return sbUrl.toString();
		} else {
			return urlPath;
		}
	}

	public static AppType getAppType() {
		return gAppType;
	}

	public static void setConfigReal(Context con) {
		switch (getAppType()) {
			case SDK:
			case BETWEEN:
				Config.setRealServer(Config.isRealServer(con));
				break;
			default:
				break;
		}
	}

	/***
	 * 프로젝트 코드 파라미터를 가져오는 함수..
	 *
	 * @param con
	 * @return
	 */
	public static List<NameValuePair> getProjectCodeParams(Context con) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		//2017.06.19 projCode 발급 하는 데 UserNo 가 필요 없다고 하여 주석
		switch (getAppType()) {
			case SDK:
				params.add(new BasicNameValuePair("userno", SnapsLoginManager.getUUserNo(con)));
				break;
			case BETWEEN:
				AccessData data = Config.getBetweenAuthInfo(con);
				String hash = data.getHashCode();
				String token = "";
				try {
					// token = URLEncoder.encode(data.getAccessToken(), "utf-8");
					token = URLEncoder.encode(data.getSnapsToken(), "utf-8");
				} catch (UnsupportedEncodingException e) {
					Dlog.e(TAG, e);
				}

				params.add(new BasicNameValuePair("prmHash", hash));
				params.add(new BasicNameValuePair("prmToken", token));

				break;
			default: // mobile인 경우
				params.add(new BasicNameValuePair("userno", SnapsLoginManager.getUUserNo(con)));
				break;
		}

		return params;
	}

	public static String getBaseQuary(Context con) {
		return getBaseQuary(con, true);

	}

	public static String getBaseQuary(Context con, boolean isChnlCode) {
		String url = "";
		switch (getAppType()) {
			case SDK:
				url = String.format("f_uuser_id=%s", Config.getFF_UUSERID());
				break;
			case BETWEEN:
				// return String.format("f_uuser_id=%s&f_chnl_code=%s",
				// Config.FF_UUSERID, Config.CHANNEL_CODE);
				AccessData data = Config.getBetweenAuthInfo(con);
				String hash = data.getHashCode();
				String token = "";
				try {
					// token = URLEncoder.encode(data.getAccessToken(), "utf-8");
					token = URLEncoder.encode(data.getSnapsToken(), "utf-8");
				} catch (UnsupportedEncodingException e) {
					Dlog.e(TAG, e);
				}

				url = String.format("prmHash=%s&prmToken=%s&prmChnlCode=%s", hash, token, Config.getCHANNEL_CODE());
				break;
			default:
				url = String.format("f_user_id=%s", SnapsLoginManager.getUUserNo(con));
				break;

		}

		if (isChnlCode) {
			url += String.format("&f_chnl_code=%s", Config.getCHANNEL_CODE());
		}

		return url;

	}

	public static String getPaymentResultUrl(Context con, boolean result, String orderCode) {

		// String urlBody = SnapsAPI.WEB_DOMAIN() +
		// String.format("/mw/order/%s.jsp", result ? "complete" : "pay_fail");
		String urlBody = String.format("/mw/order/%s.jsp", result ? "complete"
				: "pay_fail");

		switch (getAppType()) {
			case SDK:
			case BETWEEN:
				return SnapsAPI.WEB_DOMAIN(urlBody, "", SnapsLoginManager.getUUserNo(con)) + String.format("&deviceFlag=mw&orderCode=%s", orderCode);
			// return String.format(SnapsAPI.WEB_DOMAIN() + "/mw/order/%s.jsp?deviceFlag=mw&orderCode=%s&f_uuser_id=%s&f_chnl_code=%s", result ?
			// "complete" : "pay_fail", orderCode, Config.FF_UUSERID, Config.CHANNEL_CODE);
			default:
				return SnapsAPI.WEB_DOMAIN(urlBody, SnapsLoginManager.getUUserNo(con), "") + String.format("&deviceFlag=mw&orderCode=%s", orderCode);
		}
	}

	public static void setImageResource(ImageView iv, int res) {
		switch (getAppType()) {
			case SDK:
			case BETWEEN:
				if (iv != null) {
					iv.setImageResource(res);
				}
				break;
			default:
				break;
		}
	}

	/***
	 * 주문배송 url를 구하는 함수..
	 *
	 * @param con
	 * @return
	 */
	public static String getOrderUrl(Context con) {
		switch (getAppType()) {
		case SDK:
			return SnapsAPI.WEB_DOMAIN("/mw/history", "",
					Config.getUUserID(con));
		case BETWEEN:
			String baseQuery = "&" + getBaseQuary(con, false);
			String url = SnapsAPI.WEB_DOMAIN("/mw/history", "", "");
			url += baseQuery;
			return url;
		default:
			return SnapsAPI.WEB_DOMAIN(SnapsAPI.ORDER_URL(), SnapsLoginManager.getUUserNo(con),
					"");

		}

	}

	public static void gotoQNA(Context con, String query) {
		switch (getAppType()) {
			case SDK:
			case BETWEEN:
				String qnaUrl;
				qnaUrl = SnapsAPI.QNA_URL() + SnapsLoginManager.getUUserNo(con) + "&" + query;

				Intent intent = DetailProductWebviewActivity.getIntent(con, con.getString(
//				Intent intent = WebviewActivity.getIntent(con, con.getString(
//						R.string.qna), UrlUtil.getTopMarginUrl(con, false, qnaUrl), true, SnapsMenuManager.eHAMBURGER_ACTIVITY.CUSTOMER);
						R.string.qna), qnaUrl, true, SnapsMenuManager.eHAMBURGER_ACTIVITY.CUSTOMER);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				IntentUtil.startActivity(con, intent);
				break;
			default:
				break;
		}
	}

	public static void goCustomerActivity(Context context) {
		String userNo = Setting.getString(context, Const_VALUE.KEY_SNAPS_USER_NO);

		String qnaUrl = SnapsAPI.QNA_URL() + userNo + "&f_chnl_code=" + Config.getCHANNEL_CODE();

		String titleStr = context.getString(R.string.customer_center);
		Intent intent = DetailProductWebviewActivity.getIntent(context, titleStr, qnaUrl, SnapsMenuManager.eHAMBURGER_ACTIVITY.CUSTOMER);

//        intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK );
		intent.putExtra("detailindex", "");
		context.startActivity(intent);

		SnapsMenuManager.requestFinishPrevActivity();
	}

	public static String getCouponUrl(Context con) {
		String url = "";
		switch (getAppType()) {
			case SDK:
				url = SnapsAPI.WEB_DOMAIN("/mw/v3/coupon/index.jsp", "", SnapsLoginManager.getUUserNo(con));
				break;
			case BETWEEN:
				String baseQuery = "&" + getBaseQuary(con);
				url = SnapsAPI.WEB_DOMAIN("/mw/v3/coupon/index.jsp", "", "");
				url += baseQuery;
				break;
			default:
				url = SnapsAPI.WEB_DOMAIN("/mw/v3/coupon/index.jsp", SnapsLoginManager.getUUserNo(con), "");
				break;
		}
		return url;
	}

	public static void gotoCartList(Context con, int _cart_count, String naviBarTitle, String query) {
		gotoCartList(con, _cart_count, naviBarTitle, query, false);
	}

	public static void gotoCartList(Context con, int _cart_count, String naviBarTitle, String query, boolean clearActivities) {
		Intent intent;
		if (clearActivities) {
			intent = new Intent(con, RenewalHomeActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra("goToCart", true);
			con.startActivity(intent);
			return;
		}

		String cartCount = Integer.toString(_cart_count);
		String url = getCartListUrl(con, naviBarTitle, cartCount);
		intent = RenewalHomeActivity.getIntent(con, con.getString(R.string.cart), url);

		Dlog.d("gotoCartList() Url:" + url);

		if (intent != null) {
			con.startActivity(intent);
		}
	}

	public static String getNoticeUrl(Context con) {
		String url = SnapsAPI.NOTICE_URL() + "?" + "f_chnl_code=" + Config.getCHANNEL_CODE() + "&osType=190002";
		switch (getAppType()) {
			case SDK:
				break;
			case BETWEEN:
				url = SnapsAPI.NOTICE_URL() + "?" + "f_chnl_code=" + Config.getCHANNEL_CODE() + "&" + getBaseQuary(con);
				break;
			default:
				break;
		}
		return url;
	}

	public static List<NameValuePair> getBadgeCountParams(Context con) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		switch (getAppType()) {
			case BETWEEN:
				AccessData data = Config.getBetweenAuthInfo(con);
				String hash = data.getHashCode();
				String token = "";
				try {
					// token = URLEncoder.encode(data.getAccessToken(), "utf-8");
					token = URLEncoder.encode(data.getSnapsToken(), "utf-8");
				} catch (UnsupportedEncodingException e) {
					Dlog.e(TAG, e);
				}

				params.add(new BasicNameValuePair("prmHash", hash));
				params.add(new BasicNameValuePair("prmToken", token));

				break;
			case SDK:
			default: // mobile인 경우
				params.add(new BasicNameValuePair("F_USER_ID", SnapsLoginManager.getUUserNo(con)));
				params.add(new BasicNameValuePair("f_os_type", "190002"));
				break;
		}

		return params;
	}

	public static String getCartListUrl(Context con, String naviBarTitle,
			String cartCount) {
		String url = "";
		switch (getAppType()) {
			case SDK:
				url = SnapsAPI.WEB_DOMAIN("/mw/v3/cart/index.jsp", "", SnapsLoginManager.getUUserNo(con)) + "&nextPage=MY_SESSION" + "&naviBarTitle=" + naviBarTitle + "&cartCount=" + cartCount;// + "&" + getBaseQuary(con, false);
				break;
			case BETWEEN:
				url = SnapsAPI.WEB_DOMAIN("/mw/v3/cart/index.jsp", "", "") + "&nextPage=MY_SESSION" + "&naviBarTitle=" + naviBarTitle + "&cartCount=" + cartCount + "&" + getBaseQuary(con, false);
				break;
			case SNAPS_KOR0033:
				url = SnapsAPI.WEB_DOMAIN(SnapsAPI.CART_URL(), "", SnapsLoginManager.getUUserNo(con)) + "&nextPage=MY_SESSION" + "&naviBarTitle=" + naviBarTitle + "&cartCount=" + cartCount + "&" + getBaseQuary(con, false);
				break;
			default:
				// TODO test code
//				url = SnapsAPI.WEB_DOMAIN(SnapsAPI.CART_URL, getUUserNo(con), "") + "&nextPage=MY_SESSION" + "&naviBarTitle=" + naviBarTitle + "&cartCount=" + cartCount + "&" + getBaseQuary(con, false);
				url = SnapsAPI.WEB_DOMAIN(SnapsAPI.CART_URL(), SnapsLoginManager.getUUserNo(con), "") + "&nextPage=MY_SESSION" + "&naviBarTitle=" + naviBarTitle + "&cartCount=" + cartCount + "&" + getBaseQuary(con, false);
				break;
		}

		return url;
	}

	public static String getChannelCode() {
		String channelCode = "";
		if (Config.isSnapsSDK() || Config.isSnapsBitween()) {
			channelCode = Config.getCHANNEL_CODE();
		}
//		else channelCode = Config.IS_REAL ? Config.CHANNEL_CODE : Config.CHANNEL_SNAPS_TEST;
		else {
			channelCode = Config.getCHANNEL_CODE();
		}

		return channelCode;
	}

	//추가인증 Y or N
	public static String isVerify(Context context) {
		return Setting.getString(context, Const_VALUE.KEY_USER_AUTH);
	}

	//추가인증 전화번호
	public static String getVerifyPhoneNumber(Context context) {
		return Setting.getString(context, Const_VALUE.KEY_USER_PHONENUMBER);
	}

}
