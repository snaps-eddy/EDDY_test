package com.snaps.mobile.utils.pref;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.net.xml.bean.Xml_UpdateInfo;
import com.snaps.common.utils.pref.Setting;
import com.snaps.common.utils.ui.IntentUtil;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.mobile.component.SnapsTutorialView;
import com.snaps.mobile.utils.thirdparty.SnapsTPAppManager;

import errorhandle.logger.SnapsLogger;

public class PrefUtil {
	private static final String TAG = PrefUtil.class.getSimpleName();
	static String KAKAO_FRIENDID = "kakao_friendid";
	static String KAKAO_EVENTCODE = "kakao_evnetcode";
	static String KAKAO_FRIEND_DEVICEID = "kakako_deviceid";

	/**
	 * 사용자 관련 데이터 초기화
	 *
	 * @param context
	 */
	public static void clearUserInfo(Context context, boolean isLogout) {
		if (!SnapsTPAppManager.isThirdPartyApp(context)) {
			SnapsLogger.appendTextLog("clearUserInfo");
			SharedPreferences sp = Setting.getSP(context);
			if (sp == null) {
				return;
			}

			SharedPreferences.Editor editor = sp.edit();
			if (editor == null) {
				return;
			}

			editor.putString(Const_VALUE.KEY_SNAPS_USER_NO, "");
			editor.putString(Const_VALUE.KEY_USER_INFO_USER_NAME, "");

			if (isLogout) {
				editor.putString(Const_VALUE.KEY_SNAPS_USER_ID, "");
				editor.putString(Const_VALUE.KEY_SNAPS_USER_PWD, "");
				editor.putString(Const_VALUE.KEY_USER_AUTH, "");
				editor.putString(Const_VALUE.KEY_USER_PHONENUMBER, "");
				editor.putInt(Const_VALUE.KEY_CART_COUNT, 0);
				editor.putBoolean(Const_VALUE.KEY_SNAPS_AI, false);
				editor.putBoolean(Const_VALUE.KEY_SNAPS_AI_TOS_AGREEMENT, false);
			}

			editor.putString(Const_VALUE.KEY_SNAPS_USER_NAME1, "");
			editor.putString(Const_VALUE.KEY_SNAPS_USER_NAME2, "");
			editor.putString(Const_VALUE.KEY_SNAPS_USER_NAME, "");
			editor.putInt(Const_VALUE.KEY_CART_COUNT, 0);
			editor.putInt(Const_VALUE.KEY_MYARTWORK_COUNT, 0);

			editor.commit();

			if (context instanceof Activity) {
				IntentUtil.badgeUpdate(0, (Activity) context);
			}
		} else {
			Config.setUUserID2("", context);
		}
	}

	/**
	 * Template 버전정보 초기화
	 *
	 * @param context
	 */
	public static void initTemplateVersion(Context context, Xml_UpdateInfo info) {
		SharedPreferences sp = Setting.getSP(context);
		if (sp == null) {
			return;
		}

		SharedPreferences.Editor editor = sp.edit();
		if (editor == null) {
			return;
		}

		// 20140904
		/*
		 * <facebook_book5_template version="0.3"/> <facebook_book7_template version="0.2"/> <sticker_template version="0.1"/> <sticker2_template version="0.1"/> <sticker1_template version="0.1"/>
		 * <collage_template version="0.1"/> <collage7_template version="0.1"/> <facebook_cardA_template version="0.1"/> <facebook_cardB_template version="0.2"/> <kakao_book5_template version="0.1"/>
		 * <kakao_book7_template version="0.1"/> <photo_print version="0.5"/>
		 */

		editor.putBoolean(Const_VALUE.KEY_SEND_GOOGLE_ANALYTICS_DATA, info.isEnableGoogleAnalytics());
		editor.apply();
	}

	/***
	 * 카카오 이벤트 정보 저장
	 *
	 * @param context
	 * @param friendId
	 * @param eventcode
	 */
	public static void saveKakaoEvent(Context context, String friendId, String eventcode, String deviceId) {
		SnapsLogger.appendTextLog("saveKakaoEvent");
		SharedPreferences sp = Setting.getSP(context);
		if (sp == null) {
			return;
		}

		SharedPreferences.Editor editor = sp.edit();
		if (editor == null) {
			return;
		}

		editor.putString(KAKAO_FRIENDID, friendId);
		editor.putString(KAKAO_EVENTCODE, eventcode);
		editor.putString(KAKAO_FRIEND_DEVICEID, deviceId);
		editor.commit();

		lastKakaoEventSaveTime = System.currentTimeMillis();
	}

	/***
	 * 카카오 친구초대 이벤트 정보 삭제...
	 *
	 * @param context
	 */
	public static long lastKakaoEventSaveTime = 0l;

	public static void clearKakaoEvent(Context context) {
		SnapsLogger.appendTextLog("clearKakaoEvent");
		SharedPreferences sp = Setting.getSP(context);
		if (sp == null) {
			return;
		}

		SharedPreferences.Editor editor = sp.edit();
		if (editor == null) {
			return;
		}

		editor.remove(KAKAO_FRIENDID);
		editor.remove(KAKAO_EVENTCODE);
		editor.remove(KAKAO_FRIEND_DEVICEID);
		editor.commit();
	}

	public static String getKakaoSenderNo(Context context) {
		return Setting.getString(context, KAKAO_FRIENDID);
	}

	public static String getKakaoDeviceID(Context context) {
		return Setting.getString(context, KAKAO_FRIEND_DEVICEID);
	}

	public static String getKakaoEventCode(Context context) {
		return Setting.getString(context, KAKAO_EVENTCODE);
	}

	/**
	 * 푸쉬동의한 사용자 번호 가져오는 함수
	 *
	 * @param context
	 * @return
	 */
	public static String getGCMAgreeUserno(Context context) {
		return Setting.getString(context, Const_VALUE.GCM_AGREE_USERNO);

	}

	public static void setGCMAgreeUserno(Context context, String userNo) {
		Setting.set(context, Const_VALUE.GCM_AGREE_USERNO, userNo);

	}

	public static String getGCMAgreeUsernoStatus(Context context) {
		return Setting.getString(context, Const_VALUE.GCM_AGREE_USERNO_STATUS);

	}

	public static void setGCMAgreeUsernoStatus(Context context, String status) {
		Setting.set(context, Const_VALUE.GCM_AGREE_USERNO_STATUS, status);

	}

	/***
	 * 카카오 친구초대 이벤트 정보 삭제...
	 *
	 * @param context
	 */
	public static void clearGCMAgreeUsernoStatus(Context context) {
		SnapsLogger.appendTextLog("clearGCMAgreeUsernoStatus");
		SharedPreferences sp = Setting.getSP(context);
		if (sp == null) {
			return;
		}

		SharedPreferences.Editor editor = sp.edit();
		if (editor == null) {
			return;
		}

		editor.remove(Const_VALUE.GCM_AGREE_USERNO_STATUS);
		editor.commit();
	}

	static String ENABLE_GOOGLEPHOTO = "enable_googlephoto";

	public static void setEnableGooglePhoto(Context context, boolean isEnable) {
		Setting.set(context, ENABLE_GOOGLEPHOTO, isEnable);
	}

	public static boolean getGooglePhotoEnable(Context context) {
		return Setting.getBoolean(context, ENABLE_GOOGLEPHOTO);
	}

	static String SNAPS_APP_VERSION = "snaps_app_version";

	static public void setSnapsAppVersion(Context context, String appVersion) {
		Setting.set(context, SNAPS_APP_VERSION, appVersion);
	}

	static public String getSnapsAppVersion(Context context) {
		return Setting.getString(context, SNAPS_APP_VERSION);
	}

	static String GOOGLEPHOTO_TOKEN = "googlephoto_token";

	static public void setGooglePhotoAcccessToken(Context context, String token) {
		Setting.set(context, GOOGLEPHOTO_TOKEN, token);
	}

	static public String getGooglePhotoAcccessToken(Context context) {
		return Setting.getString(context, GOOGLEPHOTO_TOKEN);
	}

	static public void removeGooglePhotoAcccessToken(Context context) {
		SnapsLogger.appendTextLog("removeGooglePhotoAcccessToken");
		SharedPreferences sp = Setting.getSP(context);
		if (sp == null) {
			return;
		}

		SharedPreferences.Editor editor = sp.edit();
		if (editor == null) {
			return;
		}

		editor.remove(GOOGLEPHOTO_TOKEN);
		editor.commit();
	}

	static String GOOGLEPHOTO_AUTHCODE = "googlephoto_authcode";

	static public void setGooglePhotoAuthCode(Context context, String token) {
		Setting.set(context, GOOGLEPHOTO_AUTHCODE, token);
	}

	static public String getGooglePhotoAuthCode(Context context) {
		return Setting.getString(context, GOOGLEPHOTO_AUTHCODE);
	}

	static public void removeGooglePhotoAuthCode(Context context) {
		SharedPreferences sp = Setting.getSP(context);
		if (sp == null) {
			return;
		}

		SharedPreferences.Editor editor = sp.edit();
		if (editor == null) {
			return;
		}

		editor.remove(GOOGLEPHOTO_AUTHCODE);
		editor.commit();
	}

	static String GOOGLEPHOTO_NAME = "googlephoto_name";

	static public void setGooglePhotoName(Context context, String token) {
		Setting.set(context, GOOGLEPHOTO_NAME, token);
	}

	static public String getGooglePhotoName(Context context) {
		return Setting.getString(context, GOOGLEPHOTO_NAME);
	}

	static public void removeGooglePhotoName(Context context) {
		SnapsLogger.appendTextLog("removeGooglePhotoName");
		SharedPreferences sp = Setting.getSP(context);
		if (sp == null) {
			return;
		}

		SharedPreferences.Editor editor = sp.edit();
		if (editor == null) {
			return;
		}

		editor.remove(GOOGLEPHOTO_NAME);
		editor.commit();
	}

	static String GOOGLEPHOTO_REFRESHTOKEN = "googlephoto_refreshtoken";

	static public void setGooglePhotoRefreshToken(Context context, String token) {
		Setting.set(context, GOOGLEPHOTO_REFRESHTOKEN, token);
	}

	static public String getGooglePhotoRefreshToken(Context context) {
		return Setting.getString(context, GOOGLEPHOTO_REFRESHTOKEN);
	}

	static public void removeGooglePhotoRefreshToken(Context context) {
		SnapsLogger.appendTextLog("removeGooglePhotoRefreshToken");
		SharedPreferences sp = Setting.getSP(context);
		if (sp == null) {
			return;
		}

		SharedPreferences.Editor editor = sp.edit();
		if (editor == null) {
			return;
		}

		editor.remove(GOOGLEPHOTO_REFRESHTOKEN);
		editor.commit();
	}

	private final static String NEED_TO_SHOW_SNS_BOOK_ALERT = "need_to_show_sns_book_alert";

	public static boolean showSnsBookAlert(Context context) { // 한번만 알럿 띄우도록.
		SnapsLogger.appendTextLog("showSnsBookAlert");
		SharedPreferences sp = Setting.getSP(context);
		if (sp == null) {
			return false;
		}

		boolean result = sp.getBoolean(NEED_TO_SHOW_SNS_BOOK_ALERT, true);
		Setting.set(context, NEED_TO_SHOW_SNS_BOOK_ALERT, false);
		return result;
	}

	private final static String NEED_TO_SHOW_DIARY_OTHER_OS_NOTICE_ALERT = "need_to_show_diary_other_os_notice_alert";

	public static boolean showDiaryOtherOsNoticeAlert(Context context) { // 한번만 알럿 띄우도록.
		SnapsLogger.appendTextLog("showDiaryOtherOsNoticeAlert");
		SharedPreferences sp = Setting.getSP(context);
		if (sp == null) {
			return false;
		}

		boolean result = sp.getBoolean(NEED_TO_SHOW_DIARY_OTHER_OS_NOTICE_ALERT, true);
		Setting.set(context, NEED_TO_SHOW_DIARY_OTHER_OS_NOTICE_ALERT, false);
		return result;
	}

	public static boolean isNeedCheckDiaryOtherOsNoticeAlert(Context context) {
		SnapsLogger.appendTextLog("isNeedCheckDiaryOtherOsNoticeAlert");
		SharedPreferences sp = Setting.getSP(context);
		return sp != null && sp.getBoolean(NEED_TO_SHOW_DIARY_OTHER_OS_NOTICE_ALERT, true);
	}

	public static void initSnsBookAlert(Context context) {
		Setting.set(context, NEED_TO_SHOW_SNS_BOOK_ALERT, true);
	}

	public static void initPhotoPrintTutorial(Context context) {
		Setting.set(context, SnapsTutorialView.TUTORIAL_PHOTO_PRINT, false);
	}

	/**
	 * 튜토리얼이나 알럿은 앱 설치 후 1번만 보여주게 되어 있는데,
	 * 매번 보여 줄 필요가 있는 것들은 저장 된 값을 초기화 시켜 준다.
	 */
	public static void initAlwaysShouldShowAlert(Context context) {
		initSnsBookAlert(context); //SNS 알럿 껏다 켜면 다시 보이게 해 달라고 기획팀 요청 함.

		initPhotoPrintTutorial(context); //사진 인화 튜토리얼도..
	}

	/***
	 * 장바구니 카운트 저장.
	 *
	 * @param activity
	 * @param cartCount
	 */
	public static void saveCartCount(Activity activity, String cartCount) {
		try {
			int cartCnt = Integer.parseInt(cartCount);
			saveCartCount(activity, cartCnt);
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
	}

	public static void saveCartCount(Activity activity, int cartCount) {
		Setting.set(activity, Const_VALUE.KEY_CART_COUNT, cartCount);
//		IntentUtil.badgeUpdate(cartCount, activity);
	}

	public static void deleteAllPreferencesForDebug(Context context) throws Exception {
		SnapsLogger.appendTextLog("deleteAllPreferencesForDebug");
		SharedPreferences sp = Setting.getSP(context);
		if (sp == null) {
			return;
		}

		SharedPreferences.Editor editor = sp.edit();
		if (editor == null) return;

		editor.clear();
		editor.apply();
		MessageUtil.toast(context, "deleted all internal memories.");
	}
}
