package com.snaps.common.utils.net.xml.bean;

public class Xml_UpdateInfo {
    private String appVersion, noticeVersion, noticeMsg, noticeUrl, instargramLogin = null;
    private boolean doingFriendEvent, enableAdbrix, enableGoogleAnalytics, enableAppsFlyer, openDailybookBeta, imageCache, isUsePhoneCertification;

    /**
     * getters
     */
    public String getAppVersion() { return appVersion; }
    public String getNoticeVersion() { return noticeVersion; }
    public String getNoticeMsg() { return noticeMsg; }
    public String getNoticeUrl() { return noticeUrl; }
    public String getInstargramLogin() { return instargramLogin; }
    public boolean isDoingFriendEvent() { return doingFriendEvent; }
    public boolean isEnableAdbrix() { return enableAdbrix; }
    public boolean isEnableGoogleAnalytics() { return enableGoogleAnalytics; }
    public boolean isEnableAppsFlyer() { return enableAppsFlyer;}
    public boolean isOpenDailybookBeta() { return openDailybookBeta; }
    public boolean isImageCache() { return imageCache; }
    public boolean isUsePhoneCertification() {
        return isUsePhoneCertification;
    }

    /**
     * setters
     */
    public void setNoticeUrl(String noticeUrl) { this.noticeUrl = noticeUrl; }
    public void setAppVersion(String appVersion) { this.appVersion = appVersion; }
    public void setNoticeVersion(String noticeVersion) { this.noticeVersion = noticeVersion; }
    public void setNoticeMsg(String noticeMsg) { this.noticeMsg = noticeMsg; }
    public void setInstargramLogin(String instargramLogin) { this.instargramLogin = instargramLogin; }
    public void setDoingFriendEvent(boolean doFriendEvent) { this.doingFriendEvent = doFriendEvent; }
    public void setEnableAdbrix(boolean enableAdbrix) { this.enableAdbrix = enableAdbrix; }
    public void setEnableGoogleAnalytics(boolean enableGoogleAnalytics) { this.enableGoogleAnalytics = enableGoogleAnalytics; }
    public void setEnableAppsFlyer(boolean enableAppsFlyer) { this.enableAppsFlyer = enableAppsFlyer;}
    public void setOpenDailybookBeta(boolean openDailybookBeta) { this.openDailybookBeta = openDailybookBeta; }
    public void setImageCache(boolean imageCache) { this.imageCache = imageCache; }
    public void setUsePhoneCertification(boolean usePhoneCertification) {
        isUsePhoneCertification = usePhoneCertification;
    }
}

// backup
//public class Xml_UpdateInfo {
//	public String iphone_version;
//	public String iphone_appstore_url;
//	public String android_version;
//	public String android_playstore_url;
//	public String android_notice_url;
//	public String android_notice_content;
//	public String android_notice_version;
//
//	// 템플릿
//	public String photo_print;
//	public String facebook_book5_template;
//	public String facebook_book7_template;
//	public String kakaobook5_template;
//	public String kakaobook7_template;
//	public String sticker_template;
//	public String sticker2_template;
//	public String sticker1_template;
//	public String collage_template;
//	public String collage7_template;
//	public String namecard_template;
//	public String facebook_cardA_template;
//	public String facebook_cardB_template;
//
//	// 공유 메시지
//	public String posting_project;
//	public String share_project;
//	public String invite_message;
//
//	//UI Menu 구성
//	public String ui_menu_info_version;
//
//	// 이벤트 정보
//	public String event_posting_url;
//	public String event_posting_homeimgurl;
//	public String event_posting_mainimgurl;
//	public String event_posting_code;
//	public String event_posting_text;
//	public String event_order_url;
//	public String event_order_homeimgurl;
//	public String event_order_mainimgurl;
//	public String event_order_code;
//	public String event_order_text;
//
//	public String maxPage_version;
//
//	public String present_version;
//
//	public String pinkponEventOpen = "false";
//
//	public String kakaoEvent = "false";
//
//	public String customerUrl;
//
//	public boolean enableAdbrix = false;
//
//
//	// <event_posting url="http://117.52.102.177/kakao/event/20130709_open/index.jsp"
//	// homeImgUrl="http://m.snaps.kr/mobile/snapsforkakao/event/home_event_banner.png"
//	// mainImgUrl="http://m.snaps.kr/mobile/snapsforkakao/event/pop_event_img.png"
//	// eventCode="0041">이벤트 참여하시고 선물받아 가세요.</event_posting>
//	// <event_order url="" homeImgUrl="" mainImgUrl="" eventCode=""/>
//
//	// <posting_project>내가 만든 스냅스 %@에요!</posting_project>
//	// <share_project>스냅스에서 만든 작품을 공유했습니다.\n지금 확인하시면 %@님의 작품을 보실 수 있습니다.</share_project>
//	// <invite_message>
//	// 휴대폰사진 인화할 필요없어! 콜라주하니까 좋아~\n사진만 넣으면 콜라주로 디자인된 포토북이 바로 만들어진다구.\n\n%@님이 소개하신 콜라주가 궁금하시면 보러오세요.
//	// </invite_message>
//
//	/**
//	 * kakao용 updateinfo
//	 */
//	// public Xml_UpdateInfo(String iphone_version, String iphone_appstore_url, String android_version, String android_playstore_url, String
//	// android_notice_url, String android_notice_msg, String android_notice_version,
//	// String kakaobook5_template, String kakaobook7_template,
//	// String sticker_template, String collage_template, String namecard_template,
//	// String posting_project, String share_project, String invite_message, String event_posting_url, String event_posting_homeimgurl, String
//	// event_posting_mainimgurl, String event_posting_code, String event_posting_text, String event_order_url, String event_order_homeimgurl, String
//	// event_order_mainimgurl, String event_order_code, String event_order_text) {
//	// this.iphone_version = iphone_version;
//	// this.iphone_appstore_url = iphone_appstore_url;
//	// this.android_version = android_version;
//	// this.android_playstore_url = android_playstore_url;
//	// this.android_notice_url = android_notice_url;
//	// this.android_notice_msg = android_notice_msg;
//	// this.android_notice_version = android_notice_version;
//	// this.kakaobook5_template = kakaobook5_template;
//	// this.kakaobook7_template = kakaobook7_template;
//	// this.sticker_template = sticker_template;
//	// this.collage_template = collage_template;
//	// this.namecard_template = namecard_template;
//	// this.posting_project = posting_project;
//	// this.share_project = share_project;
//	// this.invite_message = invite_message;
//	// this.event_posting_url = event_posting_url;
//	// this.event_posting_homeimgurl = event_posting_homeimgurl;
//	// this.event_posting_mainimgurl = event_posting_mainimgurl;
//	// this.event_posting_code = event_posting_code;
//	// this.event_posting_text = event_posting_text;
//	// this.event_order_url = event_order_url;
//	// this.event_order_homeimgurl = event_order_homeimgurl;
//	// this.event_order_mainimgurl = event_order_mainimgurl;
//	// this.event_order_code = event_order_code;
//	// this.event_order_text = event_order_text;
//	// }
//
//	public Xml_UpdateInfo() {
//
//	}
//
//	public void setEnableAdbrix(boolean enableAdbrix) {
//		this.enableAdbrix = enableAdbrix;
//	}
//
//	/**
//	 * 다국어용 updateinfo
//	 */
//	public Xml_UpdateInfo(String iphone_version, String iphone_appstore_url, String android_version, String android_playstore_url, String android_notice_url, String android_notice_content, String android_notice_version, String photo_print, String kakaobook5_template, String kakaobook7_template,
//			String facebook_book5_template, String facebook_book7_template, String sticker_template, String sticker2_template, String sticker1_template, String collage_template, String collage7_template, String facebook_cardA_template, String facebook_cardB_template, String posting_project,
//			String share_project, String invite_message, String event_posting_url, String event_posting_homeimgurl, String event_posting_mainimgurl, String event_posting_code, String event_posting_text, String event_order_url, String event_order_homeimgurl, String event_order_mainimgurl,
//			String event_order_code, String event_order_text) {
//
//		this.iphone_version = iphone_version;
//		this.iphone_appstore_url = iphone_appstore_url;
//		this.android_version = android_version;
//		this.android_playstore_url = android_playstore_url;
//		this.android_notice_url = android_notice_url;
//		this.android_notice_content = android_notice_content;
//		this.android_notice_version = android_notice_version;
//		this.photo_print = photo_print;
//		this.kakaobook5_template = kakaobook5_template;
//		this.kakaobook7_template = kakaobook7_template;
//		this.facebook_book5_template = facebook_book5_template;
//		this.facebook_book7_template = facebook_book7_template;
//		this.sticker_template = sticker_template;
//		this.sticker2_template = sticker2_template;
//		this.sticker1_template = sticker1_template;
//		this.collage_template = collage_template;
//		this.collage7_template = collage7_template;
//		this.facebook_cardA_template = facebook_cardA_template;
//		this.facebook_cardB_template = facebook_cardB_template;
//		this.posting_project = posting_project;
//		this.share_project = share_project;
//		this.invite_message = invite_message;
//		this.event_posting_url = event_posting_url;
//		this.event_posting_homeimgurl = event_posting_homeimgurl;
//		this.event_posting_mainimgurl = event_posting_mainimgurl;
//		this.event_posting_code = event_posting_code;
//		this.event_posting_text = event_posting_text;
//		this.event_order_url = event_order_url;
//		this.event_order_homeimgurl = event_order_homeimgurl;
//		this.event_order_mainimgurl = event_order_mainimgurl;
//		this.event_order_code = event_order_code;
//		this.event_order_text = event_order_text;
//	}
//
//	public Xml_UpdateInfo(String iphone_version, String iphone_appstore_url, String android_version, String android_playstore_url, String maxPage_version, String kakaoEvent, String MenuInfo) {
//		this.iphone_version = iphone_version;
//		this.iphone_appstore_url = iphone_appstore_url;
//		this.android_version = android_version;
//		this.android_playstore_url = android_playstore_url;
//		this.maxPage_version = maxPage_version;
//		this.kakaoEvent = kakaoEvent;
//		this.ui_menu_info_version = MenuInfo;
//	}
//
//	@Override
//	public String toString() {
//		return "Xml_UpdateInfo [iphone_version=" + iphone_version + ", iphone_appstore_url=" + iphone_appstore_url + ", android_version=" + android_version + ", android_playstore_url=" + android_playstore_url + ", android_notice_url=" + android_notice_url + ", android_notice_msg="
//				+ android_notice_content + ", android_notice_version=" + android_notice_version + ", photo_print=" + photo_print + ", facebook_book5_template=" + facebook_book5_template + ", facebook_book7_template=" + facebook_book7_template + ", kakaobook5_template=" + kakaobook5_template
//				+ ", kakaobook7_template=" + kakaobook7_template + ", sticker_template=" + sticker_template + ", sticker2_template=" + sticker2_template + ", sticker1_template=" + sticker1_template + ", collage_template=" + collage_template + ", collage7_template=" + collage7_template
//				+ ", namecard_template=" + namecard_template + ", facebook_cardA_template=" + facebook_cardA_template + ", facebook_cardB_template=" + facebook_cardB_template + ", posting_project=" + posting_project + ", share_project=" + share_project + ", invite_message=" + invite_message
//				+ ", event_posting_url=" + event_posting_url + ", event_posting_homeimgurl=" + event_posting_homeimgurl + ", event_posting_mainimgurl=" + event_posting_mainimgurl + ", event_posting_code=" + event_posting_code + ", event_posting_text=" + event_posting_text + ", event_order_url="
//				+ event_order_url + ", event_order_homeimgurl=" + event_order_homeimgurl + ", event_order_mainimgurl=" + event_order_mainimgurl + ", event_order_code=" + event_order_code + ", event_order_text=" + event_order_text + ", ui_menu_info_version=" + ui_menu_info_version + "]";
//	}
//
//	public void setPresentVersion(String version) {
//		this.present_version = version;
//	}
//
//	public void setCustomerUrl(String url) {
//		this.customerUrl = url;
//	}
//
//    public void setNotice( String version, String content, String url ) {
//        android_notice_version = version;
//        android_notice_content = content;
//        android_notice_url = url;
//    }
//
//	/***
//	 * updateInfo에서 템플릿 버젼만 저장하는 함수.
//	 *
//	 * @param editor
//	 */
//	public void saveSharedPreferencesVersion(SharedPreferences.Editor editor) {
//		editor.putString("updateInfo_" + Const_VALUE.KEY_FACEBOOK_BOOK5_TEMPLATE_V, facebook_book5_template);
//		editor.putString("updateInfo_" + Const_VALUE.KEY_FACEBOOK_BOOK7_TEMPLATE_V, facebook_book7_template);
//		editor.putString("updateInfo_" + Const_VALUE.KEY_KAKAOBOOK5_TEMPLATE_V, kakaobook5_template);
//		editor.putString("updateInfo_" + Const_VALUE.KEY_KAKAOBOOK7_TEMPLATE_V, kakaobook7_template);
//		editor.putString("updateInfo_" + Const_VALUE.KEY_STICKET6_TEMPLATE_V, sticker_template);
//		editor.putString("updateInfo_" + Const_VALUE.KEY_STICKET2_TEMPLATE_V, sticker2_template);
//		editor.putString("updateInfo_" + Const_VALUE.KEY_STICKET1_TEMPLATE_V, sticker1_template);
//		// editor.putString("updateInfo_collage_template", collage_template);
//		// editor.putString("updateInfo_collage7_template", collage7_template);
//		// editor.putString("updateInfo_namecard_template", namecard_template);
//		editor.putString("updateInfo_" + Const_VALUE.KEY_FACEBOOK_CARDA_TEMPLATE_V, facebook_cardA_template);
//		editor.putString("updateInfo_" + Const_VALUE.KEY_FACEBOOK_CARDB_TEMPLATE_V, facebook_cardB_template);
//
//		editor.commit();
//	}
//}
