package com.snaps.common.utils.constant;

/**
 * Created by ysjeong on 16. 6. 9..
 */
public interface ISnapsConfigConstants {

    //디버깅용..
    boolean IS_USE_ERR_ASSERT = Config.isDevelopVersion() && true;

    /** 이벤 여부 */
    boolean IS_PHOTOPRINT_SELL = false;

    /** 튜토리얼 테스트 **/
    boolean TEST_TUTORIAL = false;

    /** 폰 SDK Info **/
    int ANDROID_VERSION = android.os.Build.VERSION.SDK_INT;

    /**
     *  !!!!!!!!!!!!!!  메인 화면 리소스를 업데이트 할 때마다, 버전코드를 올려주어야 최초 설치자들이 매번 리소스를 받지 않음. !!!!!!!!!!!!!!!!!!!!!!!!!
     */
    String ASSET_MAIN_UI_RESOURCE_VERSION = "1.0.0";

    // 페이스북 기능 지원 여부
    boolean IS_SUPPORT_FACEBOOK = false;

    // 인스타그램 기능 지원 여부
    boolean IS_SUPPORT_INSTAGRAM = false;

    // 카카오 사진 인화 재편집 기능 FIXME 검증이 되지 않아서, 일단 false
    boolean IS_ACTIVE_KAKAO_PIC_FIX_FUNCTION = false;

    String SNAPS_EXEC_GOTO_HOST = "exec_goto";

    // Home 화면에서 선택한 상품
    /** 페북북 선택 */
    int SELECT_FBBOOK = 0;
    /** 스티커킷 선택 */
    int SELECT_STICKER = 1;
    /** 콜라주 선택 */
    int SELECT_COLLAGE = 2;
    /** 카카오명함 선택 */
    int SELECT_NAMECARD = 3;
    /** 사진인화 */
    int SELECT_PHOTO_PRINT = 4;
    /** 테마북 */
    int SELECT_SINGLE_CHOOSE_TYPE = 5;
    /** 카카오북 선택 */
    int SELECT_KAKAOBOOK = 6;
    /** 심플포토북 */
    int SELECT_SIMPLEPHOTO_BOOK = 7;

    /** Calendar */
    int SELECT_CALENDAR = 8;

    /** 마블액자 */
    int SELECT_MARVEL_FRAME = 10;

    /** 메탈 액자 */
    int SELECT_METAL_FRAME = 11;

    /** 원목 액자 */
    int SELECT_WOOD_FRAME = 12;

    /** 원목 액자 */
    int SELECT_FRAME = 20;

    /** 카카오북 리뉴얼 버전 */
    int SELECT_NEW_KAKAOBOOK = 21;

    /** 우드블럭, 스퀘어, 떼부지, 플라로이드 킷트 */
    int SELECT_PACKAGE_KIT = 22;

    /** 간편만들기 */
    int SELECT_SIMPLE_MAKING_BOOK = 23;

    /** 카드류 */
    int SELECT_CARD = 24;

    /** Facebook Photobook */
    int SELECT_FACEBOOK_PHOTOBOOK = 25;

    /** 일기 쓸 때 사진 선택 */
    int SELECT_SNAPS_DIARY = 27;

    /** 제외할 일기 선택 */
    int SELECT_SNAPS_REMOVE_DIARY = 28;

    /** 포토카드 */
    int SELECT_PHOTO_CARD = 29;


    /** (신규)지갑용사진 */
    int SELECT_WALLET_PHOTO = 30;

    /** 증명 사진 */
    int SELECT_SNAPS_IDENTIFY_PHOTO = 31;

    /** 연하장 */
    int SELECT_SNAPS_NEW_YEARS_CARD = 32;

    int SELECT_SMART_SIMPLEPHOTO_BOOK = 33;

    /** 아코디언 카드 */
    int SELECT_ACCORDION_CARD = 34;
    /** 포스터 */
    int SELECT_POSTER = 35;
    /** 포스터 */
    int SELECT_SLOGAN = 36;

    int SELECT_SMART_ANALYSIS_PHOTO_BOOK = 37;

    int SELECT_MULTI_CHOOSE_TYPE = 38;

    /**투명 포토카드 */
    int SELECT_TRANSPARENCY_PHOTO_CARD = 39;

    /** 우드 블럭 Calendar */
    int SELECT_WOOD_BLOCK_CALENDAR = 40;

    /**DIY 스티커 */
    int SELECT_DIY_STICKER = 41;

    /** 스티커 그룹 */
    int SELECT_NEW_STICKER = 42;

    /** 스마트톡 */
    int SELECT_SMART_TALK= 42;

    /** KT BOOK */
    int SELECT_KT_BOOK = 43;

    /** Legacy Phone Case */
    int SELECT_PHONE_CASE = 44;

    /** New Phone Case */
    int SELECT_NEW_PHONE_CASE = 45;

    /** Seal Sticker */
    int SELECT_SEAL_STICKER = 46;

    /** Application Info **/
    String APPLICATION_NAME = "Android_Kakao";
    String PC_CHANNEL_CODE = "KOR0000";

    String CHANNEL_KAKAO = "KOR0031";
    String CHANNEL_SNAPS_KOR = "KOR0031";
    String CHANNEL_SNAPS_KOR_MEMBER = "KOR0001";

    String CHANNEL_SNAPS_GLOBAL_JPN = "JPN0031";
    String CHANNEL_SNAPS_GLOBAL_ENG = "ENG0031";
    String CHANNEL_SNAPS_GLOBAL_CHN = "CHN0031";


    String CHANNEL_SNAPS_JPN = "JPN1031";
    String CHANNEL_SNAPS_JPN_MEMBER = "JPN1000";
//    public static final String CHANNEL_SNAPS_ENG = "KOR0030";

    String CHANNEL_SNAPS_TEST = "KOR0031";// "KOR0031"; 웹팀 요청으로 테스트 버전일 때, 스토어쪽 채널코드를 33번으로 맞춤..2015 11 05 caesar


    /** Product Info **/
    String PRODUCT_PHOTOPRINT = "00800100010001";

    String PRODUCT_NEW_KAKAKO_STORYBOOK_SOFT = "00800600100009";
    String PRODUCT_NEW_KAKAKO_STORYBOOK_HARD = "00800600100008";

    //	// 페이스북5in
//	public static final String PRODUCT_FACEBOOK_BOOK_5 = "00800600100004";
//	// 페이스북7in
//	public static final String PRODUCT_FACEBOOK_BOOK_7 = "00800600100003";
//	// 카카오북5in
//	public static final String PRODUCT_KAKAOBOOK_5 = "00800600100002";
//	// 카카오북7in
//	public static final String PRODUCT_KAKAOBOOK_7 = "00800600100001";
//	// 콜라주북5in
//	public static final String PRODUCT_COLLAGE_5 = "00800600080001";
//	// 콜라주북7in
//	public static final String PRODUCT_COLLAGE_7 = "00800600080002";
    // 스티커 킷 6장,2장,1장 동일
    String PRODUCT_STICKER = "00802100010001";
    // 카카오 명함
//	public static final String PRODUCT_BUSINESS_CARD = "00800900050003";
    // 페이스북명함
//	public static final String PRODUCT_FACEBOOK_NAMECARD = "00800900050004";

    String[] PRODUCT_PHOTOPRINT_PRODCODE = { "00800100010001", "00800100010023", "00800100010002", "00800100010003", "00800100010005", "00800100010006", "00800100010011",
            "00800100010009", "00800100010010" };

    // 증명사진
    String PRODUCT_IDENTIFY_PHOTO = "00800500050001";
    String[] PRODUCT_IDENTIFY_PHOTOPRINT_PRODCODE = { "00800500050001" };//"00800500050001", "00800500050001" };

    // 페이스북 포토북
    String PRODUCT_FACEBOOK_PHOTOBOOK_HARD = "00800600100010";
    String PRODUCT_FACEBOOK_PHOTOBOOK_SOFT = "00800600100011";

    // 일기서비스
    String PRODUCT_SNAPS_DIARY_HARD = "00800600070007";
    String PRODUCT_SNAPS_DIARY_SOFT = "00800600070008";

    // 간편만들기
    String[] PRODUCT_SIMPLE_MAKING_BOOK = { "0080060017", "0080060018" };
    // 테마북 소프트 커버...
    String PRODUCT_THEMEBOOK_A5 = "00800600100006";
    // 테마북 A6...
    String PRODUCT_THEMEBOOK_A6 = "00800600100007";
    // 테마북 하드 커버...
    String PRODUCT_THEMEBOOK_HARD = "00800600100005";

    //팬북
    String PRODUCT_FANBOOK_HARD_A5 = "00800600210033";
    String PRODUCT_FANBOOK_HARD_A4 = "00800600210007";
    String PRODUCT_FANBOOK_SOFT_A5 = "00800600210034";
    String PRODUCT_FANBOOK_SOFT_A4 = "00800600210008";
    String PRODUCT_FANBOOK_GROUP = "0080060021";

    // 캘린더
    String PRODUCT_CALENDAR = "00800700030003";

    // 캘린더
    String PRODUCT_CALENDAR_VERTICAL = "00800700050004";
    // 캘린더
    String PRODUCT_CALENDAR_MINI = "00800700050003";
    // 캘린더
    String PRODUCT_CALENDAR_WIDE = "00800700060002";

    String PRODUCT_CALENDAR_LARGE = "00800700030004";

    String PRODUCT_CALENDAR_VERTICAL2 = "00800700030005";

    // 심플포포토북
    // 00800600130001 심플포토북-하드커버[8 X 8]
    // 00800600130001 심플포토북-하드커버[10 X 10]
    // 00800600130003 심플포토북-소프트커버[8 X 8]
    // 00800600130007 심플포토북-하드커버[A4]
    // 00800600130008 심플포토북-소프트커버[A4]
    // 00800600130017 심플포토북-하드커버[8 X 10]
    // 00800600130019 심플포토북-하드커버[5 X 7]
    // 00800600130022 심플포토북-소프트커버[5 X 7]
    // 00800600130023 심플포토북-하드커버[A5]
    // 00800600130024 심플포토북-소프트[A5]
    // 00800600130026 심플포토북-하드커버[5 X 5]
//	public static final String PRODUCT_SIMPLE_PHOTOBOOK[] = { "00800600130001", "00800600130002", "00800600130003", "00800600130007", "00800600130008", "00800600130017", "00800600130019",
//			"00800600130022", "00800600130023", "00800600130024", "00800600130026" };
    String PRODUCT_SIMPLE_PHOTOBOOK = "0080060013";

    String[] PRODUCT_CALENDARS = { "00800700030003", "00800700050004", "00800700050003", "00800700060002", "00800700030004", "00800700030005" };
    // 벽걸이달력..
    String PRODUCT_WALL_CALENDAR = "0080070004"; // 0080070004????//벽걸이달력
    // 스케즐러..
    String PRODUCT_SCHEDULE_CALENDAR = "0080070007"; // 0080070004????//벽걸이달력

    /** 앱별 상품 템플릿갯수 */
    // 카카오앱 - 콜라주5 카스북5,7 카스명함, 스티커킷6
    int TEMPLATE_COUNT_KAKAO = 5;
    // 스냅스 KR앱 - 콜라주5,7 페북북5,7 페북명함A,B, 스티커킷1,2,6, 카카오5,7,사진인
    int TEMPLATE_COUNT_KR = 12;
    // 스냅스 .com앱 - 콜라주5,7 페북북5,7 페북명함A,B, 스티커킷1,2,6
    int TEMPLATE_COUNT_JA = 9;

    /** Product Template Code Info **/
    String TEMPLATE_FACEBOOK_BOOK_7 = "045006003582";
    String TEMPLATE_FACEBOOK_BOOK_5 = "045006003583";
    String TEMPLATE_KAKAOBOOK_5 = "045006003362";
    String TEMPLATE_KAKAOBOOK_7 = "045006003383";
    String TEMPLATE_STICKER_6 = "045006002891";
    String TEMPLATE_STICKER_2 = "045006003354";
    String TEMPLATE_STICKER_1 = "045006003353";
    String TEMPLATE_COLLAGE_5 = "045006002893";
    String TEMPLATE_COLLAGE_7 = "045006003574";
    String TEMPLATE_NAMECARD = "045006002892";
    String TEMPLATE_FACEBOOK_NAMECARD_A = "045006003580";
    String TEMPLATE_FACEBOOK_NAMECARD_B = "045006003581";

    String TEMPLATE_PHOTO_PRINT = "045006003581";

    String TEMPLATE_THEMEBOOK = "045021001023";

    // public static final String TEMPLATE_CALENDAR = "046001017046";

    String IDENTIFY_PHOTO_TEMPLATE_CODE_PASSPORT = "045001000171";
    String IDENTIFY_PHOTO_TEMPLATE_CODE_NORMAL = "045001000169";
    String IDENTIFY_PHOTO_TEMPLATE_CODE_HALF = "045001000170";

    /** 프로젝트 썸네일 사이즈 **/
    int THUMB_SIZE = 200;

    int THUMB_SIZE_FOR_SNAPS_DIARY = 720;

    /** Resource Item Type Data **/
    /** 페북북,카카오북,스티커킷에만 필요함. */
    String RESOURCE_ITEM_TYPE_CODE = "159001";
    String RESOURCE_TITLE_ITEM_TYPE_CODE = "159002";

    String RESOURCE_TMPL_CODE_BACKGROUND = "045003";
    String RESOURCE_TMPL_CODE_COVER = "045004";
    String RESOURCE_TMPL_CODE_MULTITEMPLATE = "045006";

    // 주문코드.
    String ORDR_STAT_SAVE_CODE = "146000";
    String ORDR_STAT_ORDER_CODE = "146001";

    /** 기본 배경 색상 리스트 **/
    String[] BASE_COLOR_LIST = { "00adef", "f68028", "ffde00", "99ca3b", "231f20" };

//    public static final int[] STICKER_MARGIN_LIST = { 24, 16, 28, 36 };
int[] STICKER_MARGIN_LIST = { 2, 2, 2, 2 };
    int[] ACCORDION_CARD_MARGIN_LIST = { 12, 4, 12, 4 };
    int[] COLLAGE_MARGIN_LIST = { 30, 28, 30, 28 };// 책넘기기용
    // 마진 //
    // { 32
    // , 14
    // , 38
    // , 42
    // };//
    // 기존 마진
    int[] NAMECARD_MARGIN_LIST = { 13, 10, 14, 16 };
    int[] KAKAOBOOK5_MARGIN_LIST = { 15, 3, 15, 23 };
    int[] KAKAOBOOK7_MARGIN_LIST = { 10, 0, 10, 39 };
    int[] KAKAOBOOK7_PAGE_MARGIN_LIST = { 8, 0, 8, 38 };
    int[] KAKAOBOOK7_PAGEINSIDE_MARGIN_LIST = { 31, 0, 31, 0 };
    // public static final String KAKAOBOOK7_PAGE_LINE_HEIGHT = "8";
    int KAKAOBOOK7_TEXT_MAXLINE = 19;
    int KAKAOBOOK5_TEXT_MAXLINE = 12;
    int FB_BOOK_TEXT_MAXLINE = 5;
    int KAKAOBOOK7_PAGEINSIDE_WIDTH = 813;
    int KAKAOBOOK7_PAGEINSIDE_HEIGHT = 583;
    int KAKAOBOOK7_COVER_WIDTH = 868;
    int KAKAOBOOK7_COVER_HEIGHT = 608;

    int[] SMART_RECOMMEND_BOOK_MARGIN_LIST = { 10, 6, 10, 19 };

    int[] THEMEBOOK_MARGIN_LIST = { 10, 6, 10, 19 };
    int[] THEMEBOOK_HARDCOVER_MARGIN_LIST = { 10, 4, 10, 21 };
    // public static final int[] THEMEBOOK_SOFT_MARGIN_LIST = { 9, 5, 9, 24 };
    int[] THEMEBOOK_SOFT_MARGIN_LIST = { 8, 5, 8, 24 };

    int[] CARD_MARGIN_LIST = { 0, 0, 0, 0 };// {8, 5, 8, 24};

    int[] SNS_BOOK_SOFT_MARGIN_LIST = { 10, 6, 10, 24 };
    int[] SNS_BOOK_HARD_MARGIN_LIST = { 2, 0, 2, 14 };
    int[] SNS_BOOK_HARDCOVER_MARGIN_LIST = { 10, 4, 10, 21 };

    // 수치가 작아질수록 뷰는 커짐.
    int LANDSCAPE_PAGE_MARGIN_FOR_SIMPLE_BOOK_A4 = 60;
    int LANDSCAPE_PAGE_MARGIN_FOR_THEME_BOOK = 50;
    int LANDSCAPE_PAGE_MARGIN_FOR_POLAROID = 80; // ok
    int LANDSCAPE_PAGE_MARGIN_FOR_PHONE_CASE = 100;// 80; //ok
    int LANDSCAPE_PAGE_MARGIN_FOR_DESIGN_NOTE = 60;
    int LANDSCAPE_PAGE_MARGIN_FOR_MOUSE_PAD = 80; // ok
    int LANDSCAPE_PAGE_MARGIN_FOR_FRAME = 60;
    int LANDSCAPE_PAGE_MARGIN_FOR_CALENDER = 5;

    int[] CALENDARA_NORMAL_HORIZONTAL_MARGIN_LIST = { 10, 0, 0, 58 };
    int[] CALENDARA_WIDE_MARGIN_LIST = { 10, 0, 0, 63 };
    int[] CALENDARA_MINI_MARGIN_LIST = { 10, 0, 0, 46 };
    int[] CALENDARA_NORMAL_VERTICAL_MARGIN_LIST = { 10, 0, 0, 60 };
    int[] CALENDARA_WALL_MARGIN_LIST = { 0, 20, 0, 0 };
    int[] WOOD_FRAME_BORDER_PADDING = { 15, 5 };

    int LANDSCAPE_PAGE_MARGIN_FOR_CALENDER_VERTICAL = 60;
    int LANDSCAPE_PAGE_MARGIN_FOR_TUMBLR = 80;
    int LANDSCAPE_PAGE_MARGIN_FOR_CARD = 50;


    /**
     * UI 메뉴 JSON 파일
     */
    String UI_MENU_JSON_FILE_NAME = "mainstore.json";

    String DIARY_BASE_TEMPLATE_FILE_NAME = "snapsDiaryBaseTemplate.xml";

    String SNAPS_BASE_TEMPLATE_FILE_NAME = "snapsDefaultTemplate.xml";

    String UI_PRICE_JSON_FILE_NAME = "store_price.json";

    //6*6 적용..
    String[] PRODUCT_LAY_FLATBOOK = { "0080060015", "0080060018" };
    //public static final String PRODUCT_LEATHER_BOOK = "0080060013";
//	static final String[] PRODUCT_LEATHERBOOK = { "00800600130023", "00800600130024", "00800600130025", "00800600130026", "00800600130027", "00800600130028", "00800600150002", "00800600150006",
//			"00800600150008", "00800600150010" };

    int PRODUCT_THUMBNAIL_OFFSET_LENGTH = 800;

    String SERVICE_THUMBNAIL_SAVE_PATH  = "service_thumbnail";

    String SERVICE_THUMBNAIL_SIMPLE_SAVE_PATH= "service_thumbnail_simple";

    String THUMBNAIL_SAVE_PATH = "photo_thumbnails";

    String SERVICE_UPLOAD_FILE_PATH = "service_uploadfiles";

    String PROJECT_FILE_PATH = "project_files";

    String SAVE_XML_FILE_NAME = "save.xml";
    String AURA_ORDER_XML_FILE_NAME = "auraOrder.xml";
    String OPTION_XML_FILE_NAME = "prjOption.xml";
}
