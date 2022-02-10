package com.snaps.common.utils.ui;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_Template;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.net.http.HttpUtil;
import com.snaps.common.utils.net.xml.bean.Xml_UpdateInfo;
import com.snaps.common.utils.pref.Setting;

import java.io.File;

public class TemplateUtil {

	private boolean isPhotoPrint = false;

	public void initTemplate(Context context, Xml_UpdateInfo xmlUpdateinfo) {
//		String templatePrePath = Const_VALUE.PATH_PACKAGE(context, false);

//		if (templateProcess2(templatePrePath + Const_Template.FILEPATH_PHOTO_PRINT, Const_Template.photo_print_url)) {
//			// 업데이트가 되면 objec로 저장이 되었던 객체를 삭제한다.
//			context.deleteFile(Const_VALUE.PRINT_INFO_FILENAME);
//		}

		// 테마북
		// templateProcess(defInfo, Const_Template.FILEPATH_THEMEBOOK_TEMPLATE, Const_Template.themebook_soft_url, Const_VALUE.KEY_THEMEBOOK_V, null);

		// editor.commit();

		// xmlUpdateinfo.saveSharedPreferencesVersion(editor);

	}

	class DefInfo {
		public String templatePrePath;
		public SharedPreferences pref;
		public SharedPreferences.Editor editor;

		public DefInfo(String templatePrePath, SharedPreferences pref, Editor editor) {
			this.templatePrePath = templatePrePath;
			this.pref = pref;
			this.editor = editor;
		}
	}

	void templateProcess(DefInfo defInfo, String templatePath, String templateUrl, String versionPref, String version) {
		templatePath = defInfo.templatePrePath + templatePath;

		if (isPhotoPrint || (version == null || version != null && (defInfo.pref.getString(versionPref, "").compareTo(version) < 0 || !new File(templatePath).exists()))) {

			if (HttpUtil.saveUrlToFile(templateUrl, templatePath)) {
				if (version != null) {
					defInfo.editor.putString(versionPref, version);
                    defInfo.editor.commit();
				}
			}
		}
	}

	boolean templateProcess2(String templatePath, String templateUrl) {
		// 템플릿 파일이 있는지 확인 없으면 다운을 받는다.
		if (new File(templatePath).exists())
			return false;

		HttpUtil.saveUrlToFile(templateUrl, templatePath);
		return true;
	}

	public void downloadTemplete(Context context, String productCode, int kind) {
		// SharedPreferences pref = Setting.getSP(context);
		// SharedPreferences.Editor editor = pref.edit();
		String templatePrePath = Const_VALUE.PATH_PACKAGE(context, false);
		// DefInfo defInfo = new DefInfo(templatePrePath, pref, editor);

//		if (Config.isFacebook_book7(productCode)) {
//			// 페이스북북 템플릿 - TODO fb : 페북북 5,7인치 템플릿 다운로드
//			templateProcess2(templatePrePath + Const_Template.FILEPATH_FACEBOOK_BOOK7_TEMPLATE, Const_Template.facebook_book7_url);
//		} else if (Config.isFacebook_book5(productCode)) {
//			templateProcess2(templatePrePath + Const_Template.FILEPATH_FACEBOOK_BOOK5_TEMPLATE, Const_Template.facebook_book5_url);
//		} else if (Config.isSnapsKakaobook5(productCode)) {
//			// 카카오북 템플릿 - TODO kk : 카카오북 5,7인치 템플릿 다운로드
//			templateProcess2(templatePrePath + Const_Template.FILEPATH_KAKAOBOOK5_TEMPLATE, Const_Template.kakaobook5_url);
//		} else if (Config.isSnapsKakaobook7(productCode)) {
//			templateProcess2(templatePrePath + Const_Template.FILEPATH_KAKAOBOOK7_TEMPLATE, Const_Template.kakaobook7_url);
//		} else
		if (Config.isSnapsSticker()) {

			if (kind == 1) {
				templateProcess2(templatePrePath + Const_Template.FILEPATH_STICKET1_TEMPLATE, Const_Template.stikcer1_url);
			} else if (kind == 2) {
				templateProcess2(templatePrePath + Const_Template.FILEPATH_STICKET2_TEMPLATE, Const_Template.stikcer2_url);
			} else if (kind == 6) {
				// 스티커킷 템플릿
				templateProcess2(templatePrePath + Const_Template.FILEPATH_STICKET6_TEMPLATE, Const_Template.stikcer6_url);
			}

		}
//		else if (Config.isSnapsFBCard(productCode)) {
//			if (kind == 0) {
//				// 페이스북명함 템플릿
//				templateProcess2(templatePrePath + Const_Template.FILEPATH_FACEBOOK_NAMECARD_A_TEMPLATE, Const_Template.facebook_namecard_a_url);
//			} else {
//				templateProcess2(templatePrePath + Const_Template.FILEPATH_FACEBOOK_NAMECARD_B_TEMPLATE, Const_Template.facebook_namecard_b_url);
//			}
//		}

		// calendar_url

		// 콜라주 템플릿
		// templateProcess(defInfo, Const_Template.FILEPATH_COLLAGE5_TEMPLATE, Const_Template.collage5_url, Const_VALUE.KEY_COLLAGE5_TEMPLATE_V,
		// xmlUpdateinfo.collage_template);
		// templateProcess(defInfo, Const_Template.FILEPATH_COLLAGE7_TEMPLATE, Const_Template.collage7_url, Const_VALUE.KEY_COLLAGE7_TEMPLATE_V,
		// xmlUpdateinfo.collage7_template);
	}

	final String MAXPAGE_INFO_VERSION = "maxpageinfoversion";

	/***
	 * 맥스페이지 정보를 불러오는 함수..
	 * 
	 * @param activity
	 * @param maxPageVersion
	 * @return
	 */
//	public boolean checkMaxPageInfo(Context activity, String maxPageVersion) {
//		String maxpageVersion = Setting.getString(activity, MAXPAGE_INFO_VERSION);
//
//		// TODO 저장된 파일이 없거나 버전이 다를때. 지금은 updateinfo의 버전과 spine.xml 의 버전을 저장한 값을 비교하므로 항상 다시 받는다.
//		if (!maxpageVersion.equals(maxPageVersion) || !SnapsMaxPageInfo.isMaxpageInfoFile(activity.getApplicationContext())) {
//			String url = Config.isRealServer() ? SnapsAPI.GET_MAXPAGE_INFO : SnapsAPI.GET_MAXPAGE_INFO_T;
//			if (HttpUtil.saveUrlToFile(url, SnapsMaxPageInfo.getFilePath(activity.getApplicationContext()))) {
//				//Object로 저장을 한다
//				SnapsMaxPageInfo maxPageInfo = new SnapsMaxPageInfo(activity);
//				boolean isSuccess = maxPageInfo.loadInfo(activity);
//				if(!isSuccess) return false;
//				//버젼정보 저장.
//				String versionCode = new SnapsMaxPageInfo(activity).getVersionCode();
//				Setting.set(activity, MAXPAGE_INFO_VERSION, versionCode);
//
//
//			} else {// 맥스페이지 정보를 가져오지 못한경우 처리
//				return false;
//			}
//		}
//
//		return true;
//
//	}

	/**
	 * 홈 메뉴 선물 버튼에 N표시를 updateInfo.xml 에서 관리 한다.
	 * 
	 * @return
	 */
	public static final String PRESENT_INFO_PREV_VERSION = "eventinfoprevversion";
	public static final String PRESENT_INFO_CUR_VERSION = "eventinfocurversion";

	public static boolean checkPresentVersionInfo(Activity activity, String curVersion) {
		if (curVersion == null || curVersion.length() < 1)
			return true;

		String presentVersion = Setting.getString(activity, PRESENT_INFO_PREV_VERSION);
		if (!curVersion.equals(presentVersion)) {
			Setting.set(activity, PRESENT_INFO_CUR_VERSION, curVersion);
		}

		return true;
	}
}
