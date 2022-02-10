package com.snaps.common.utils.constant;

/**
 * Activity start 시 넘기는 extra 데이터의 key들
 * 
 * @author crjung
 *
 */
public class Const_EKEY {
	/** 쿠폰선택 시 key */
	public static final String COUPON_SELECT = "COUPON_SELECT";
	/** 쿠폰적용 시 장바구니의 projcode key */
	public static final String CART_PROJCODE = "cartprojcode";
	/** 쿠폰적용 시 장바구니의 수량 key */
	public static final String CART_CNT = "cartcnt";
	/** 쿠폰적용 시 장바구니의 ordercode key */
	public static final String CART_ORDERCODE = "cartordercode";

	public static final String FACEBOOK_INTRO_PHOTO = "facebookintrophoto";

	/** 사진선택 시 사진종류 key */
	public static final String PHOTO_KIND = "PHOTO_KIND";

	/** 폰사진폴더선택 시 폴더 Id key */
	public static final String PHONE_FOLDER_ID = "phoneFolderId";

	/** Snaps 폴더선택 시 폴더 Id key */
	public static final String SNAPS_CATEGORY_CODE = "snapsCategoryCode";

	public static final String CLEAR_CACHE = "clearcache";

	/** 사진편집화면으로 넘길 전체 ImgData List */
	public static final String IMG_DATA_KEYLIST = "imgdata_keylist";
	/** 사진편집화면으로 넘길 전체 ImgData Map */
	public static final String IMG_DATA_MAP = "imgdata_map";
	/** 선택한 이미지의 position */
	public static final String IMG_DATA_POSITION = "imgdata_position";
	/** 선택한 이미지의 path */
	public static final String IMG_DATA_FILE_PATH = "imgdata_file_path";
	/** 선택한 이미지의 적용 효과 타입 */
	public static final String IMG_EFFECT_TYPE = "imgdata_effect_type";
	/** 선택한 이미지의 각도 */
	public static final String IMG_ANGLE_VALUE = "imgdata_angle_value";

	/** 내작품함의 주문결제함 여부 */
	public static final String MYART_IS_ORDER = "myart_isorder";
	/** 내작품함의 projcode */
	public static final String MYART_PROJCODE = "myart_projcode";
	/** 내작품함의 prodcode */
	public static final String MYART_PRODCODE = "myart_prodcode";
	/** 내작품함의 결재여부 */
	public static final String MYART_BAG_STAT = "myart_bagstat";

	/** 배송정보입력에서 팝업창의 선택된 idx */
	public static final String SELECT_IDX = "selectidx";
	/** 배송정보입력에서 주문자,받는이 ? idx */
	public static final String WHO_IDX = "whoidx";

	/** 배송정보입력에서 우편번호 검색어 */
	public static final String POST_SEARCH = "postsearch";

	/** 개월수 선택 검색어 */
	public static final String MONTH_SEARCH = "monthsearch";

	/** 주문결과 */
	public static final String ORDER_RESULT = "orderresult";

	/** 주문상세의 주문코드 */
	public static final String MYORDER_ORDER_CODE = "order_code";

	/** 이전 액티비티가 종료된 후 현재 액티비티도 종료할것인지? */
	public static final String IS_FINISH = "isfinish";
	/** 이전 액티비티 종료 후 현재 액티비티의 데이터를 재로딩 할 것인지? */
	public static final String IS_RELOAD = "isreload";

	/** 카카오결제 pay_id */
	public static final String KAKAO_PAY_ID = "pay_id";
	/** 카카오결제 checkout_hash */
	public static final String KAKAO_CHECKOUT_HASH = "checkout_hash";
	
	/** URL TYPE **/
	public static final String URL_TYPE_DELIVERY = "delivery";

	/** webview title */
	public static final String WEBVIEW_TITLE = "webview_title";
	/** webview url */
	public static final String WEBVIEW_URL = "webview_url";
	/** pre url */
	public static final String WEBVIEW_PRESENT_URL = "webview_present_url";
    /** webview longclickable */
    public static final String WEBVIEW_LONG_CLICKABLE = "webview_long_clickable";
	/** webview event code */
	public static final String WEBVIEW_EVENTCODE = "webview_event_code";

	/** 카카오 로그아웃, 연결해제 시 메인 액티비티 종료를 위한 key */
	public static final String HOME_FIFNISH = "homefinish";

	/** home에서 선택한 상품종류를 사진선택쪽으로 넘기기위한 key */
	public static final String HOME_SELECT_PRODUCT = "homeselectproduct";

	/** home에서 선택한 상품종류를 사진선택쪽으로 넘기기위한 key */
	public static final String HOME_SELECT_PRODUCT_CODE = "homeselectproductcode";

	/** home에서 선택한 상품종류를 사진선택쪽으로 넘기기위한 key */
	public static final String HOME_SELECT_KIND = "homeselectkind";

	/** home에서 선택한 상품종류를 사진선택쪽으로 넘기기위한 key */
	public static final String THEME_SELECT_TEMPLE = "themeselecttemple";
	
	/** 화면 회전 관련 key */
	public static final String SCREEN_ORIENTATION_STATE_CHANGE = "screen_ori_change";

	/** home에서 선택한 종이 타입 사진선택쪽으로 넘기기위한 key */
	public static final String HOME_SELECT_PAPER_TYPE = "themeselectpapertype";

	/** 푸시수신 시 앱실행 구분용 key */
	public static final String PUSH_RUN = "pushrun";

	/** 쿠폰적용 시 적용할 상품코드를 위한 key */
	public static final String COUPON_PRODCODE = "coupon_prodcode";

	/** 이미지 선택 화면에서 인텐트 정보 key */
	public static final String IMAGE_SELECT_INTENT_DATA_KEY = "image_sel_intent_data_key";

	// ----------- Snaps앱용 -----------
	/** 로그인 프로세스 구분을 위한 key */
	public static final String LOGIN_PROCESS = "loginprocess";

	public static final String LOGIN_AFTER_WHERE = "loginpAfterWhere";

	/** web Cmd key */
	public static final String WEB_CMD_KEY = "cmd";
	
	/** web Close Cmd key */
	public static final String WEB_CLOSE_CMD_KEY = "closeCmd";

	/** web ProdCode key */
	public static final String WEB_PRODCODE_KEY = "productCode";

	/** web ProdCode key */
	public static final String WEB_TEMPLE_KEY = "prmTmplCode";

	public static final String WEB_TEMPLE_CODE = "templateCode";

	public static final String WEB_SMART_YN = "smartYN";

	/** web frame ID key */
	public static final String WEB_FRAME_KEY = "frameid";

	/** web frame ID key */
	public static final String WEB_FRAME_TYPE_KEY = "frametype";

	/** web kind key */
	public static final String WEB_KIND_KEY = "kind";

	/** web paper code key */
	public static final String WEB_PAPER_CODE = "paperCode";

	/** web PROJCODE key */
	public static final String WEB_PROJCODE_KEY = "prjCode";

	/** web UNITPRICE key */
	public static final String WEB_UNITPRICE_KEY = "unitPrice";

	/** web PAGE key */
	public static final String WEB_PAGE_KEY = "pageName";

	/** web OrderCode key */
	public static final String WEB_ORDER_CODE_KEY = "orderCode";
	
	/** web ProdCode key */
	public static final String WEB_CARD_CNT_KEY = "prmAddCnt";

	/** web SttleMethod key */
	public static final String WEB_STTLEMETHOD_KEY = "sttleMethod";

	/** web naviBarTitle key */
	public static final String WEB_NAVIBARTITLE_KEY = "naviBarTitle";
	
	/**  */
	public static final String WEB_URL_TYPE_KEY = "urlType";

	/** web cartcount key */
	public static final String WEB_CARTCOUNT_KEY = "cartCnt";

	public static final String WEB_COUPON_COUNT_KEY = "couponCnt";

	/** web calendar key */
	public static final String WEB_CALENDAR_YEAR_KEY = "year";

	public static final String WEB_CALENDAR_MONTH_KEY = "month";

	/** web PROJCODE key */
	public static final String WEB_START_DATE_KEY = "startDate";
	public static final String WEB_END_DATE_KEY = "endDate";
	public static final String WEB_COMMENT_CNT_KEY = "commentCnt";
	public static final String WEB_ANSWER_CNT_KEY = "answerCnt";
	public static final String WEB_PHOTO_CNT_KEY = "photoCnt";
	public static final String WEB_POST_CNT_KEY = "postCnt";
	public static final String WEB_TITLE_KEY = "title";

	public static final String WEB_LINKURL = "linkUrl";
	public static final String WEB_DESCRIPTION = "desc";
	public static final String WEB_IMAGE_URL = "imgUrl";

	public static final String WEB_RECOMMEND_SEQ_KEY= "recommendSeq";


	public static final String DIARY_INFO_CHANGE = "diary_info_change";
	public static final String DIARY_REQUEST_PROFILE_PHOTO_SELECT = "diary_profile_photo";

	public static final String DIARY_DATA = "diary_data";
	public static final String DIARY_XML_PATH = "diary_xml_path";
	public static final String DIARY_IS_MODIFY_MODE = "diary_is_modify_mode";

	public static final String NATIVE_UI_SIZE_TYPE = "native_ui_size_type";
	public static final String NATIVE_UI_PARAMS = "native_ui_params";

	/** newYearsCard 인텐트 **/
	public static final String NEW_YEARS_CARD_MAX_COUNT = "new_years_card_max_count";
	public static final String NEW_YEARS_CARD_MODE = "new_years_card_mode";
	public static final String NEW_YEARS_CARD_SELECT_TEMPLETE_CODE = "new_years_card_mode";
}
