package com.snaps.common.utils.constant;

import android.content.Context;
import android.graphics.Typeface;

import com.snaps.common.utils.system.SystemUtil;

import java.util.List;
import java.util.Map;

/**
 * 단일 상수값들
 *
 * @author crjung
 */
public class Const_VALUE {
    public static final String FIRST_LAUNCH_PROCESS_DONE = "first_launch_process_done";

    public static final String PUSH_FULL_CHECK_KEY = "fullpushkey";
    public static final String PUSH_CENTER_CHECK_KEY = "centerpushkey";
    public static final String ALL_USER = "alluser";
    public static final String CRM_USER = "crmuser";
    public static final String EVNET_USER = "eventuser";
    public static final String LEVEL_USER = "leveluser";

    public static final int REQ_CODE_PERMISSION = 200;

    public static final String APP_VERSION = "app_version";

    // -------------------- Bitmap 자르기 위한 비율 정보 --------------------

    public static final float FACEBOOK_7in_Top = 0.014f; // 5.f / 544.f;
    public static final float FACEBOOK_7in_LR = 0.05f; // 20.f / 793.f;
    public static final float FACEBOOK_7in_Bottom = 0.014f; // 5.f / 544.f;

    public static final float COLLAGE_7in_Top = 0.0223f; // 15.f / 465.f;
    public static final float COLLAGE_7in_LR = 0.045f; // 40.f / 885.f;
    public static final float COLLAGE_7in_Bottom = 0.0223f; // 15.f / 465.f;

    // -------------------- UI 관련 정보 --------------------
    /**
     * 이미지 그리드 컬럼갯수
     */
    public static final int IMAGE_GRID_COLS = 3;
    /**
     * 이미지 그리드 컬럼갯수
     */
    public static final int IMAGE_TRAY_COLS = 4;
    /**
     * 이미지 그리드 컬럼갯수
     */
    public static final int IMAGE_GRID_COLS_LANDSCAPE = 5;
    /**
     * 이미지 그리드 아이템 간 간격(px)
     */
    public static final int IMAGE_GRID_SPACING = 15;
    /**
     * 이미지 그리드 아이템 간 간격(px)
     */
    public static final int IMAGE_GRID_SPACING_TOP = 15;
    /**
     * 이미지 앨범 리스트 컬럼갯수
     */
    public static final int IMAGE_ALBUM_COLS = 2;
    /**
     * 이미지 앨범 아이템 간 간격(px)
     */
    public static final int IMAGE_ALBUM_SPACING = 30;
    /**
     * 이미지 페이지 디자인 아이템 간 간격(px)
     */
    public static final int IMAGE_PAGE_DESIGN_LIST_SPACING = 10;
    /**
     * 이미지 앨범 frame 틀 간격(dp) 좌우포함
     */
//	public static final int IMAGE_ALBUM_FRAME = Config.IS_SUPPORT_EDITOR_LANDSCAPE_MODE ? 12 : 20;
    public static final int IMAGE_ALBUM_FRAME = 20;
    public static final int IMAGE_ALBUM_FRAME_12 = 12;
    public static final int IMAGE_COVER_DESIGN_FRAME = 10;
    /**
     * 스냅스 앨범 리스트 컬럼갯수
     */
    public static final int IMAGE_SNAPS_ALBUM_COLS = 3;
    /**
     * 카카오북 리스트 컬럼갯수
     */
    public static final int IMAGE_GRID_COLS_FOR_KAKAOBOOK = 3;

    // -------------------- UI 관련 정보 --------------------

    /**
     * 스티커킷 6분할 crop 시 height 대비 width ratio
     */
    public static final float STICKER_6_RATIO = 1.0f;
    /**
     * 스티커킷 2분할 crop 시 height 대비 width ratio
     */
    public static final float STICKER_2_RATIO = 0.75f;
    /**
     * 스티커킷 1분할 crop 시 height 대비 width ratio
     */
    public static final float STICKER_1_RATIO = 1.57f;

    /**
     * 콜라주 페이지 세로 대비 가로 비율
     */
    public static final float COLLAGE_RATIO = 802f / 420f;
    /**
     * 페북북 페이지 세로 대비 가로 비율
     */
    public static final float FBBOOK_RATIO = 868f / 608f;

    // -------------------- UI 관련 데이터 정보 --------------------
    /**
     * 페북북 5inch 최대갯수
     */
    public static final int MAX_IMAGE_FBBOOK_5 = 30;
    /**
     * 페북북 7inch 최대갯수
     */
    public static final int MAX_IMAGE_FBBOOK_7 = 38;
    /**
     * 카카오북 5inch 최대갯수
     */
    public static final int MAX_IMAGE_KAKAOBOOK_5 = 30;
    /**
     * 카카오북 7inch 최대갯수
     */
    public static final int MAX_IMAGE_KAKAOBOOK_7 = 38;
    /**
     * 카카오북 5inch 내용 최대라인수
     */
    public static final int MAX_LINES_KAKAOBOOK_5 = 12;
    /**
     * 카카오북 7inch 내용 최대라인수
     */
    public static final int MAX_LINES_KAKAOBOOK_7 = 19;
    /**
     * 콜라주 최대갯수
     */
    public static final int MAX_IMAGE_COLLAGE = 20;
    /**
     * 간편만들기 최대갯수
     */
    public static final int MAX_IMAGE_SIMPLE_MAKING_BOOK = 21;
    /**
     * 스티커킷 6개 최대갯수
     */
    public static final int MAX_IMAGE_STICKER_6 = 90;
    /**
     * 스티커킷 2개 최대갯수
     */
    public static final int MAX_IMAGE_STICKER_2 = 30;
    /**
     * 스티커킷 1개 최대갯수
     */
    public static final int MAX_IMAGE_STICKER_1 = 15;

    /**
     * 심플북 최대갯수
     */
    public static final int MAX_IMAGE_SIMPLE_PHOTO_BOOK = 200;

    // /** 심플북 최소갯수 */
    // public static final int MIN_IMAGE_SIMPLE_PHOTO_BOOK = 30;

    /**
     * 커스텀폰트 관련
     */
    public static Typeface SNAPS_TYPEFACE;
    public static Typeface SNAPS_TYPEFACE_YG033;
    public static Typeface SNAPS_TYPEFACE_YG032;
    public static Typeface SNAPS_TYPEFACE_SOMANG;

    public static Typeface SNAPS_TYPEFACE_CALENDAR;
    public static Typeface SNAPS_TYPEFACE_TEXTLIST;

    public static Typeface SNAPS_TYPEFACE_TEXTLISTS[];

    public static String SNAPS_TYPEFACE_NAME;
    public static String SNAPS_TYPEFACE_NAME2;

    public static String SNAPS_TYPEFACE_MONTH;
    public static String SNAPS_TYPEFACE_MONTH_TITLE;
    public static String SNAPS_TYPEFACE_YEAR;
    public static String SNAPS_TYPEFACE_DAY;
//	public static String SNAPS_TYPEFACE_DAY_TITLE;

    public static Map<String, Typeface> sTypefaceMap = null;
    public static List<String> sNotDefineFontList = null;       //CS 대응

    public static final void SNAPS_TYPEFACE_INIT(Context context) {
//		SNAPS_TYPEFACE = Typeface.createFromAsset(context.getAssets(), "Snaps_YG032.ttf.mp3");
//		SNAPS_TYPEFACE_YG033 = Typeface.createFromAsset(context.getAssets(), "SNAPS_YGO33.TTF.mp3");
//		SNAPS_TYPEFACE_SOMANG = Typeface.createFromAsset(context.getAssets(), "snaps_Somang2M.ttf.mp3");
    }

    // -------------------- Preference 관련 데이터 정보 --------------------
    // ----------- Snaps앱용 -----------
    /**
     * 숫자로 구성된 스냅스 아이디를 preference에 저장할 key 값
     */
    public static final String KEY_SNAPS_USER_NO = "snapsuserno";
    /**
     * 이메일로 구성된 스냅스 아이디를 preference에 저장할 key 값
     */
    public static final String KEY_SNAPS_USER_ID = "snapsuserid";
    /**
     * 스냅스 이름 preference에 저장할 key 값
     */
    public static final String KEY_SNAPS_USER_NAME = "snapsusername";
    /**
     * 스냅스 이름1 preference에 저장할 key 값
     */
    public static final String KEY_SNAPS_USER_NAME1 = "snapsusername1";
    /**
     * 스냅스 이름2 preference에 저장할 key 값
     */
    public static final String KEY_SNAPS_USER_NAME2 = "snapsusername2";
    /**
     * 스냅스 패스워드 preference에 저장할 key 값
     */
    public static final String KEY_SNAPS_USER_PWD = "snapsuserpwd";
    /**
     * 스냅스 로그인구분 preference에 저장할 key 값
     */
    public static final String KEY_SNAPS_LOGIN_TYPE = "snapslogintype";
    /**
     * 스냅스 로그인구분 휴면계정 여부
     */
    public static final String KEY_SNAPS_REST_ID = "snapsrestid";
    public static final String VALUE_SNAPS_REST_ID = "344002";

    /**
     * facebook 아이디를 preference에 저장할 key 값
     */
    public static final String KEY_FACEBOOK_ID = "facebookid";
    /**
     * facebook 이름을 preference에 저장할 key 값
     */
    public static final String KEY_FACEBOOK_NAME = "facebookname";
    /**
     * facebook 프로필이미지 url을 preference에 저장할 key 값
     */
    public static final String KEY_FACEBOOK_PROFILE_URL = "facebookprofileurl";
    /**
     * facebook 배경이미지 url을 preference에 저장할 key 값
     */
    public static final String KEY_FACEBOOK_COVER_URL = "facebookcoverurl";
    /**
     * facebook link url을 preference에 저장할 key 값
     */
    public static final String KEY_FACEBOOK_LINK_URL = "facebooklinkurl";

    /**
     * instagrm book의 tutorial을 보여줬는지 체크할 key 값
     */
    public static final String KEY_INSTAGRAM_BOOK_TUTORIAL = "instagram_book_tutorial";

    /**
     * facebook 아이디를 preference에 저장할 key 값
     */
    public static final String KEY_KAKAO_ID = "kakaoid";
    /**
     * facebook 이름을 preference에 저장할 key 값
     */
    public static final String KEY_KAKAO_NAME = "kakaoname";
    /**
     * facebook 프로필이미지 url을 preference에 저장할 key 값
     */
    public static final String KEY_KAKAO_PROFILE_URL = "kakaoprofileurl";
    /**
     * facebook 배경이미지 url을 preference에 저장할 key 값
     */
    public static final String KEY_KAKAO_COVER_URL = "kakaocoverurl";
    /**
     * facebook link url을 preference에 저장할 key 값
     */
    public static final String KEY_KAKAO_LINK_URL = "kakaolinkurl";

    /**
     * 공유 이벤트 정보를 저장할 key 값
     */
    public static final String KEY_EVENT_POSTING_URL = "event_posting_url";
    public static final String KEY_EVENT_POSTING_HOMEIMGURL = "event_posting_homeimgurl";
    public static final String KEY_EVENT_POSTING_MAINIMGURL = "event_posting_mainimgurl";
    public static final String KEY_EVENT_POSTING_CODE = "event_posting_code";
    public static final String KEY_EVENT_POSTING_TEXT = "event_posting_text";
    /**
     * 주문 이벤트 정보를 저장할 key 값
     */
    public static final String KEY_EVENT_ORDER_URL = "event_order_url";
    public static final String KEY_EVENT_ORDER_HOMEIMGURL = "event_order_homeimgurl";
    public static final String KEY_EVENT_ORDER_MAINIMGURL = "event_order_mainimgurl";
    public static final String KEY_EVENT_ORDER_CODE = "event_order_code";
    public static final String KEY_EVENT_ORDER_TEXT = "event_order_text";
    /**
     * 이벤트 코드별 다시보지 않기 값을 저장할 key 값
     */
    public static final String KEY_EVENT_NOSHOW = "event_noshow_";// + code
    public static final String KEY_EVENT_DEVICE = "event_device";// + code

    public static final String KEY_USER_AUTH = "user_auth";// 추가인증
    public static final String KEY_USER_PHONENUMBER = "user_phonenumber";//추가인증
    // ----------- ----------- -----------
    public static final String KEY_UI_MENU_JSON_VERSION = "ui_menu_json_ver";

    public static final String KEY_UI_MENU_RES_VERSION = "ui_menu_res_ver";

    /**
     * 푸쉬 정보를 저장할 key 값
     */
    public static final String KEY_BROADCAST_CODE = "brdcstCode";
    public static final String KEY_RESEND_NO = "resendNo";

    /**
     * 숫자로 구성된 카카오톡 아이디를 preference에 저장할 key 값
     */
    public static final String KEY_KAKAOTALK_USER_ID = "kakaouserid";
    /**
     * 카카오톡 본인이름을 preference에 저장할 key 값
     */
    public static final String KEY_KAKAOTALK_MYNAME = "kakaomyname";
    /**
     * 카카오톡 프로필 썸네일 이미지 url을 preference에 저장할 key 값
     */
    public static final String KEY_KAKAOTALK_PROFILE_THUMBNAIL_URL = "kakaouprofilethumbnailurl";
    /**
     * 카카오톡 프로필이미지 url을 preference에 저장할 key 값
     */
    public static final String KEY_KAKAOTALK_PROFILE_URL = "kakaouprofileurl";
    /**
     * 카카오톡 배경이미지 url을 preference에 저장할 key 값
     */
    public static final String KEY_KAKAOTALK_BGIMAGE_URL = "bgimageurl";

    /**
     * 카카오톡 공유 메시지를 저장할 key 값
     */
    public static final String KEY_KAKAOTALK_SHARE_MSG = "kakaotalk_share_msg";
    /**
     * 카카오톡 초대 메시지를 저장할 key 값
     */
    public static final String KEY_KAKAOTALK_INVITE_MSG = "kakaotalk_invite_msg";
    /**
     * 카카오스토리 포스팅 메시지를 저장할 key 값
     */
    public static final String KEY_KAKAOSTORY_POST_MSG = "kakaostory_post_msg";

    // SnapsAPI에 상수로 등록하였음.
//	/** iphone_appstore_url 저장할 key 값 */
//	public static final String KEY_IPHONE_APPSTROE_URL = "iphone_appstore_url";
//	/** android_playstore_url 저장할 key 값 */
//	public static final String KEY_ANDROID_PLAYSTORE_URL = "android_playstore_url";

    /**
     * FACEBOOK_BOOK5 template version을 preference에 저장할 key 값
     */
    public static final String KEY_FACEBOOK_BOOK5_TEMPLATE_V = "facebook_book5_template_v";
    /**
     * FACEBOOK_BOOK7 template version을 preference에 저장할 key 값
     */
    public static final String KEY_FACEBOOK_BOOK7_TEMPLATE_V = "facebook_book7_template_v";

    /**
     * photo_print version을 preference에 저장할 key 값
     */
    public static final String KEY_PHOTO_PRINT_V = "photo_print_v";

    /**
     * themebook version을 preference에 저장할 key 값
     */
    public static final String KEY_THEMEBOOK_V = "themebook_v";

    /**
     * kakaobook5 template version을 preference에 저장할 key 값
     */
    public static final String KEY_KAKAOBOOK5_TEMPLATE_V = "kakaobook5_template_v";
    /**
     * kakaobook7 template version을 preference에 저장할 key 값
     */
    public static final String KEY_KAKAOBOOK7_TEMPLATE_V = "kakaobook7_template_v";

    /**
     * sticker6 template version을 preference에 저장할 key 값
     */
    public static final String KEY_STICKET6_TEMPLATE_V = "sticker6_template_v";
    /**
     * sticker6 template version을 preference에 저장할 key 값
     */
    public static final String KEY_STICKET2_TEMPLATE_V = "sticker2_template_v";
    /**
     * sticker6 template version을 preference에 저장할 key 값
     */
    public static final String KEY_STICKET1_TEMPLATE_V = "sticker1_template_v";

    /**
     * collage5 template version을 preference에 저장할 key 값
     */
    public static final String KEY_COLLAGE5_TEMPLATE_V = "collage5_template_v";
    /**
     * collage7 template version을 preference에 저장할 key 값
     */
    public static final String KEY_COLLAGE7_TEMPLATE_V = "collage7_template_v";

    /**
     * namecard template version을 preference에 저장할 key 값
     */
    public static final String KEY_NAMECARD_TEMPLATE_V = "namecard_template_v";

    /**
     * facebook_cardA template version을 preference에 저장할 key 값
     */
    public static final String KEY_FACEBOOK_CARDA_TEMPLATE_V = "facebook_cardA_v";
    /**
     * facebook_cardB template version을 preference에 저장할 key 값
     */
    public static final String KEY_FACEBOOK_CARDB_TEMPLATE_V = "facebook_cardB_v";

    /**
     * 장바구니 갯수를 저장할 key 값
     */
    public static final String KEY_CART_COUNT = "cartcount";
    /**
     * 쿠폰 갯수를 저장할 key 값
     */
    public static final String KEY_COUPON_COUNT = "couponcount";
    /**
     * 내작품함 갯수를 저장할 key 값
     */
    public static final String KEY_MYARTWORK_COUNT = "myartworkcount";

    /**
     * 공지사항 key 값
     */
    public static final String KEY_NOTICE_OLD_VERSION = "noticecountold";

    /**
     * 공지사항 key 값
     */
    public static final String KEY_NOTICE_NEW_VERSION = "noticecountnew";

    /**
     * 홈화면 가격정보를 저장할 key 값
     */
    public static final String KEY_COLLAGE_PRICE = "collage_price";
    public static final String KEY_COLLAGE_MAX_PAGE = "collage_max_page";
    public static final String KEY_COLLAGE_DLVR_MTHD = "collage_dlvr_mthd";
    public static final String KEY_STICKERKIT_PRICE = "stickerkit_price";
    public static final String KEY_STICKERKIT_MAX_PAGE = "stickerkit_max_page";
    public static final String KEY_STICKERKIT_DLVR_MTHD = "stickerkit_dlvr_mthd";
    public static final String KEY_NAMECARD_PRICE = "namecard_price";
    public static final String KEY_NAMECARD_MAX_PAGE = "namecard_max_page";
    public static final String KEY_NAMECARD_DLVR_MTHD = "namecard_dlvr_mthd";

    public static final String KEY_SELL_PRICE = "SELL_PRICE";
    public static final String KEY_ORG_PRICE = "ORG_PRICE";
    public static final String KEY_MAX_PAGE = "KEY_MAX_PAGE";
    public static final String KEY_DLVR_MTHD = "KEY_DLVR_MTHD";
    public static final String KEY_DLVR_DAY = "KEY_DLVR_DAY";
    public static final String KEY_DISC_RATE = "DISC_RATE";

    /**
     * 명함 팝업여부 key 값
     */
    public static final String KEY_NAMECARD_NOTICE = "namecard_notice";

    /**
     * GCM Regid key 값
     */
    public static final String KEY_GCM_REGID = "gcm_regid";

    /**
     * GCM push 수신여부 key 값
     */
    public static final String KEY_GCM_PUSH_RECEIVE = "gcm_push_receive";

    /**
     * 스냅스 AI 설정 on/off key 값
     */
    public static final String KEY_SNAPS_AI = "snaps_ai";
    public static final String KEY_SNAPS_AI_TOS_AGREEMENT = "snaps_ai_tos_agreement";
    public static final String KEY_SNAPS_AI_ALLOW_UPLOAD_MOBILE_NET = "snaps_ai_allow_upload_mobile_net";

    /**
     * google analyrics 전송 flag
     */
    public static final String KEY_SEND_GOOGLE_ANALYTICS_DATA = "send_google_analyrics_data";

    /**
     * 최종 이벤트 중복 알럿을 띄운 아이디 저장
     */
    public static final String KEY_LAST_EVENT_ALERT_ID = "last_event_alert_id";

    /**
     * 자동로그인 사용자가 설치이벤트 대상일 경우를 고려한 변수 3가지 저장
     */
    public static final String KEY_EVENT_TERM = "last_event_term";
    public static final String KEY_EVENT_COUPON = "last_event_coupon";
    public static final String KEY_EVENT_FILE_PATH = "last_event_file_path";

    /**
     * 사용자 정보 key 값
     */
    public static final String KEY_USER_INFO_USER_NAME = "user_info_username";
    public static final String KEY_USER_INFO_GRADE_CODE = "user_info_grade_code";
    public static final String KEY_USER_INFO_EVT_DESC = "user_info_evt_desc";

    /**
     * adbrix intent key 값
     */
    public static final String KEY_ADBRIX_INTENT = "adbrix_intent_value";

    /**
     * 푸쉬 동의 관련
     **/
    public static final String KEY_SAW_PUSH_AGREE_POPUP = "saw_push_agree_popup";

    public static final String KEY_SAW_BETWEEN_PUSH_AGREE_POPUP = "saw_betweeb_push_agree_popup";

    public static final String KEY_BLOCK_SHOW_AGREE_POPUP_USER = "block_show_agree_popup_user";

    public static final String KEY_SAW_ACCESS_APP_POPUP = "saw_access_app_popup";

    /**
     * 배지 카운터를 필요할때만 조회하게 하기 위해
     */
    public static final String KEY_SHOULD_REQUEST_BADGE_COUNT = "should_request_badge_count";

    /**
     * 고객센터 파일첨부 관련...
     **/
    public static final String KEY_FILE_ATTACH_CALLBACK_MSG = "file_attach_callback_msg";

    /**
     * 이미지 선택 화면 UI 유형
     **/
    public static final String KEY_IMAGE_SELECT_UI_TYPE = "key_image_select_ui_type";

    /**
     * 편집 화면에서 템플릿을 다운받아야하는 경우
     **/
    public static final String KEY_IS_REQUIRES_TEMPLATE_DOWN_AT_EDIT_ACT = "is_requires_template_download_at_edit";

    /**
     * 사진 선택
     **/
    public static final String KEY_IMAGE_SELECT_FRG_TYPE = "image_select_frg_type";

    public static final String KEY_IS_REAL_SERVER_FOR_DEVELOP = "key_is_real_server_for_develop";

    public static final String KEY_DEVELOP_URL_SETTING_MODE = "key_develop_url_setting_mode";
    public static final String KEY_DEVELOP_URL_PATH = "key_develop_url_path";

    /**
     * 포토 카드 수량 변경 튜토리얼 보여진 시간
     **/
    public static final String KEY_SHOWN_TIME_FOR_PHOTO_CARD_QUANTITY_TUTORIAL = "shown_time_for_photo_card_quantity_tutorial";
    public static final String TUTORIAL_ID_TOOLTIP_PHOTO_CARD_LONG_CLICK_DELETE = "shown_time_for_photo_card_long_click_delete_tutorial";
    public static final String TUTORIAL_ID_TOOLTIP_PHOTO_CARD_CHANGE_DESIGN = "shown_time_for_photo_card_change_design_tutorial";
    public static final String TUTORIAL_ID_TOOLTIP_WALLET_PHOTO_CHANGE_DESIGN = "shown_time_for_wallet_photo_change_design_tutorial";

    /**
     * 애니 메인화면 핀치 시도 여부
     **/
    public static final String KEY_USER_HAD_PINCH_ZOOM_ON_MAIN_LIST = "key_user_had_pinch_zoom_on_main_list";

    /**
     * Device Id
     */
    public static final String KEY_DEVICE_ID = "device_id";

    // -------------------- 파일경로 관련 정보 --------------------
    /**
     * 파일경로를 URI로 변경할때 앞에 붙일 prefix
     */
    public static final String MEDIA_FILEPATH_PREFIX = "file://";
    /**
     * URL 원본 이미지의 sampling 이미지 파일캐쉬 저장 경로
     */
    static final String PATH_SDCARD_SRC_SAMPLING = "/src_sampling/";

    public static final String PATH_SDCARD_PS(Context context) {
        return Config.getExternalCacheDir(context) + PATH_SDCARD_SRC_SAMPLING;
    }

    /**
     * 패키지의 root 경로
     */
    public static final String PATH_PACKAGE(Context context, boolean isCache) {
        if (context == null) return null;
        if (isCache) {
            if (Config.getExternalCacheDir(context) == null) return null;
            return Config.getExternalCacheDir(context).getAbsolutePath();
        } else {
            if (Config.getExternalFilesDir(context, null) == null) return null;
            return Config.getExternalFilesDir(context, null).getAbsolutePath();
        }
    }

    /**
     * 사진선택 상세보기 시 상세 이미지 disk cache 경로
     */
    static String PATH_IMAGESELECT_DETAIL = null;

    public static final String PATH_IMAGESELECT_DETAIL(Context context) {
        if (PATH_IMAGESELECT_DETAIL == null)
            PATH_IMAGESELECT_DETAIL = Config.getExternalCacheDir(context) + "/imageselect/";
        return PATH_IMAGESELECT_DETAIL;
    }

    /**
     * 내 작품함 상세보기 시 상세 이미지 disk cache 경로
     */
    public static final String PATH_MYARTWORK_DETAIL(Context context, String projCode) {
        return Config.getExternalCacheDir(context) + "/myartwork/" + projCode + "/";
    }

    /**
     * 내 작품함 상세보기 카카오스토리 공유용 이미지 경로
     */
    static String PATH_MYARTWORK_KAKAOSTORY_SHARE = null;

    public static final String PATH_MYARTWORK_KAKAOSTORY_SHARE(Context context) {
        if (PATH_MYARTWORK_KAKAOSTORY_SHARE == null)
            PATH_MYARTWORK_KAKAOSTORY_SHARE = Config.getExternalCacheDir(context) + "/myartwork/kakaostory.jpg";
        return PATH_MYARTWORK_KAKAOSTORY_SHARE;
    }

    // -------------------- 기타 정보 --------------------
    /**
     * 카카오 결제 연령제한 값
     */
    public static final int BILL_AGE_LIMIT = 15;

    // -------------------- 카카오스토리 친구초대 이벤트 관련 ----------------------
    /**
     * 카카오스토리 보낸사람 UserID
     */
    public static String SENDER_USER_ID = null;

    // -------------------- GCM 관련정보 ----------------------
    /**
     * GCM Project Number
     */
    //snapsmarketing@gmail.com
    public static final String GCM_PROJECT_NUMBER = "539296492700";

    //snaps.developer@gmail.com
    public static final String GCM_PROJECT_NUMBER_FOR_BETWEEN = "547893227060";

    /**
     * 서버로 기기정보 등록 시 안드로이드 OS TYPE 값
     */
    public static final String GCM_ANDROID_OS_TYPE = "190002";

    public static String QNA_EMAIL() {
        if (SystemUtil.isKorean())
            return "snaps@snaps.kr";
        else if (SystemUtil.isJapan())
            return "support@snaps.com";
        else
            return "snaps@snaps.kr";
    }

    // -------------------- App Scheme 정보 ----------------------
    /**
     * 모바일웹에서 비번 변경 후 앱실행 Scheme
     */
    public static final String APPSCHEME_CHGEDPWD = "snapscom://changedpassword";

    // API 버젼 - 쿠폰 리스트 가져오기 변경으로 인한..
    public static final String API_VERSION = "1.0";

    // 언어값 저장 키.
    public static final String KEY_LANGUAGE = "pref_key_language";
    // 유저가 직접 선택한 언어값 저장 키.
    public static final String KEY_SELECTED_LANGUAGE = "pref_key_selected_language";
    // 유저가 직접 선택하여 적용된 언어값 저장 키.
    public static final String KEY_APPLIED_LANGUAGE = "pref_key_applied_language";
    // 유저의 의해 언어설정이 변경 되었는가
    public static final String KEY_IS_USER_CHANGE_LANGUAGE = "pref_key_is_user_change_language";

    // 이벤트 팝업 표시 여부...
    public static final String SHOW_EVENT = "show_event_popup";

    public static final String APPVERSION = "appversion";

    // 테마북
    public static final String NONE_TYPE = "none";
    public static final String CONTENT_TYPE = "content";
    public static final String TEXT_TYPE = "text";
    public static final String USERIMAGE_TYPE = "user_image";

    // 레이아웃 클릭관련 리시버 및 데이터 키 정의
    public static final String TEXT_TO_IMAGE_ACTION = "com.snaps.kr.text_to_image.msg";
    public static final String CLICK_LAYOUT_ACTION = "com.snaps.kr.layout.click";
    public static final String RESET_LAYOUT_ACTION = "com.snaps.kr.layout.reset";

    public static final String REQUEST_START_APP = "REQUEST_START_APP";

    public static final int PLUS_BUTTON_WIDTH = 60; //50 잘 안 눌려져서 살짝 키움..
    public static final int PLUS_BUTTON_HEIGHT = 60;

    public static final int CARD_EMPTY_TEXT_HEIGHT = 100;

    public static final int PHOTO_BOOK_GALLERY_HORIZONTAL_VIEW_WIDTH = 120;
    public static final int PHOTO_BOOK_GALLERY_HORIZONTAL_VIEW_HEIGHT = 80;
    public static final int PHOTO_CARD_GALLERY_VIEW_HEIGHT = 106;
    public static final int NEW_YEARS_CARD_GALLERY_VIEW_WDITH = 10;
    public static final int NEW_YEARS_CARD_GALLERY_VIEW_HEIGHT = 86;

    public static final int QRCODE_RIGHT_MARGIN = 60;
    public static final int QRCODE_BOTTOM_MARGIN = 80;

    public static final int CALENDAR_SKIN_BOTTOM_MARGIN = 20;

    public static final String INSTALL_REFERR = "com.snaps.installreferrer";

    public static final String RELOAD_URL = "com.snaps.action.reloadurl";

    public static final String KAKAO_EVENT_OPEN = "kakao_event_open";

    public static final String TOKEN_INVAILD_ERROR = "tokenInvalid";

    public static final String KAKAOLOING_ACTION = "kakaologin_action";

    public static final String USE_USER_CERTIFICATION = "use_user_certification";

    public static final String FACEBOOK_CHANGE_NAME_ACTION = "facebook_change_name_action";

    public static final String INSTARGRAM_LOGIN = "instargram_login";

    public static final String SNAPS_ACTIVITY_TRACKER_ACTION = "snaps_activity_tracker_name_action";
    public static final String SNAPS_EVENT_TRACKER_ACTION = "snaps_event_tracker_action";
    public static final String SNAPS_CAMPAIGN_TRACKER_ACTION = "snaps_campaign_tracker_action";

    //구글 애드워즈 관련..
    public static final String GOOGLE_ADWORDS_CONVERSION_ID = "943955751";
    public static final String GOOGLE_ADWORDS_CONVERSION_LABEL = "gvoPCLz6qV8Qp76OwgM";
    public static final String GOOGLE_ADWORDS_CONVERSION_VALUE = "1000.00";


    //사진인화 object저장명..  -> Const_Template.FILEPATH_PHOTO_PRINT에 object로 저장함.
//	public static final String PRINT_INFO_FILENAME = "printInfo";

    //푸쉬법령 동의한 사용장 아이디
    public static final String GCM_AGREE_USERNO = "gcm_agree_userno";
    public static final String GCM_AGREE_USERNO_STATUS = "gcm_agree_userno_status";

    /**
     * MenuData 저장한 최종 app version
     */
    public static final String APP_VERSION_FOR_MENU_DATA = "app_version_for_menu_data";

    //KIDS NOTE API
    public static final String KIDS_NOTE_API_GRANT_TYPE = "password";
    public static final String KIDS_NOTE_API_PASSWORD = "111111";
    public static final String KIDS_NOTE_API_URL = "https://openapi-dev.kidsnote.com";
    public static final String KIDS_NOTE_CLIENT_ID = "rhUvyqpXxJYjkLuyS3JqPI5M8I5RPU92Hzi6C4IX";
    public static final String KIDS_NOTE_SECRET = "TuQXAF8beJ2d0Xx7wA8HaYOSx04vPQg9Wi36fwriMwWtvps25xFVvdCoM8BkSAHsyLQvNWXDyT7dPMTkF1ej5K81lL33RuyTBAbQ0uOyf43JFIvyd8Y0PBTfYjshl5Q9";
    public static final String LOGIN_ACTION = "com.snaps.kr.login.complete";

    public static final String WEBVIEW_START = "com.snaps.kr.webview.start";
    public static final String WEBVIEW_END = "com.snaps.kr.webview.end";

    public static final String WEBVIEW_FAIL = "com.snaps.kr.webview.fail";


    public static final String PROJECT_CODE_TOKEN_INVALID = "tokenInvaild";

    public static final String SMART_SNAPS_DAY_CHECK = "smart_snaps_day_check";

    public static final String HAS_SEEN_WEB_TUTORIAL = "has_seen_web_tutorial";
}
