package com.snaps.common.utils.constant;

/**
 * 복수 상수값들
 *
 * @author crjung
 */
public class Const_VALUES {
    // -------------------- Color Code --------------------
    public static final String COLOR_CODE_TRANSPARENCY = "145015";
    public static final String COLOR_CODE_WHITE = "145016";
    public static final String COLOR_CODE_BLACK = "145017";
    public static final String COLOR_CODE_NAVY = "145018";
    public static final String COLOR_CODE_SKYBLUE = "145019";
    public static final String COLOR_CODE_MINT = "145020";
    public static final String COLOR_CODE_YELLOW = "145021";
    public static final String COLOR_CODE_PINK = "145022";
    public static final String COLOR_CODE_LAVENDER = "145023";
    public static final String COLOR_CODE_RED_GRADIENT = "145024";
    public static final String COLOR_CODE_BLUE_GRADIENT = "145025";
    public static final String COLOR_CODE_GREEN_GRADIENT = "145026";
    public static final String COLOR_CODE_YELLOW_GRADIENT = "145027";

    public static final String COLOR_CODE_TINCASE_SILVER = "145030";
    public static final String COLOR_CODE_TINCASE_WHITE = "145031";
    public static final String COLOR_CODE_TINCASE_BLACK = "145032";


//    public static final String COLOR_CODE_ACRYLIC_CLEAR = "160901";
    public static final String COLOR_CODE_ACRYLIC_GLITTER_HOLOGRAM = "160902";
    public static final String COLOR_CODE_ACRYLIC_GLITTER_GOLD = "160903";
    public static final String COLOR_CODE_ACRYLIC_GLITTER_BLUE_GREEN = "160904";
    public static final String COLOR_CODE_ACRYLIC_GLITTER_DEEP_PINK = "160905";
    public static final String COLOR_CODE_ACRYLIC_GLITTER_PURPLE = "160906";

    // 원래 컬러코드값이 와야하는데 홀로그램 반사 슬로건과 반사슬로건의 글자 그라디언트 정보 전달을 위해 사용되는 코드.
    public static final String COLOR_CODE_REFLECT = "145028";
    public static final String COLOR_CODE_HOLOGRAPHY = "145029";


    // -------------------- KT 북 --------------------
    public static final int KT_BOOK_COVER_TITLE_MAX_LENGTH = 23;
    public static final int KT_BOOK_TITLE_OR_PAGE_TEXT_MAX_LINE = 5;
    public static final int KT_BOOK_TITLE_OR_PAGE_TEXT_MAX_LENGTH = 195;

    public static final String KT_BOOK_TITLE_OR_PAGE_TEXT_MAX_LINE_TOAST_MSG = "최대 입력 가능한 글자 수 또는 영역을 초과하였습니다.";
    public static final String KT_BOOK_TITLE_OR_PAGE_TEXT_MAX_LENGTH_TOAST_MSG = "최대 입력 가능한 글자 수 또는 영역을 초과하였습니다.";
    public static final String KT_BOOK_BACK_KEY_MSG = "편집중인 상품을 저장하지 않고\n종료하시겠습니까?";

    public static final String KT_BOOK_TITLE_DESC_MSG = "* 책등과 표지에 들어갈 제목을 입력해주세요. \n* 특수문자를 제외한 "
            + KT_BOOK_COVER_TITLE_MAX_LENGTH + "자리까지 입력이 가능합니다.";

    public static final String KT_BOOK_INPUT_TEXT_HINT_TEXT = "텍스트를 입력하려면 터치하세요";

    public static final String KT_BOOK_EDITOR_TUTORIAL_TEXT = "텍스트 영역을 터치해서 내용을 입력할 수 있어요!\n(입력하지 않으면, 아무것도 인쇄되지 않아요.)";


    // -------------------- 이미지 선택종류 --------------------
    public static final int SELECT_GOOGLEPHOTO = 8;
    public static final int SELECT_INSTAGRAM = 7;
    public static final int SELECT_PINKPONG = 6;
    public static final int SELECT_KAKAO = 5;
    public static final int SELECT_FACEBOOK = 0;
    public static final int SELECT_PHONE = 1;
    public static final int SELECT_SNAPS = 2;
    public static final int SELECT_SDK_CUSTOMER = 3;
    public static final int SELECT_BETWEEN = 4;
    public static final int SELECT_EMPTY = 100;
    // SELECT_SDK_CUSTOMER
    public static final int SELECT_CONTENT = 10;
    public static final int SELECT_UPLOAD = 11;

    // -------------------- ImageFetcher 처리종류 --------------------
    public static final int IMG_FETCH_FILE = 1;
    public static final int IMG_FETCH_URL = 2;

    // -------------------- 배송방법종류 --------------------
    /**
     * 택배
     */
    public static final int DELIVER_DELIVERY = 0;
    /**
     * 일반우편
     */
    public static final int DELIVER_POST = 1;
    /**
     * 묶음배송
     */
    public static final int DELIVER_BUNDLE = 2;
    /**
     * 퀵서비스
     */
    public static final int DELIVER_QUICK = 3;

    // -------------------- 작품/장바구니 삭제/저장 구분 flag --------------------
    /**
     * 삭제
     */
    public static final String PROJ_DEL = "del";
    /**
     * 주문결제 삭제
     */
    public static final String PROJ_ORDER_DEL = "ordel";
    /**
     * 저장
     */
    public static final String PROJ_SAVE = "save";

    // -------------------- 주문자/받는이 구분 --------------------
    /**
     * 주문자
     */
    public static final int DELIVER_SENDER = 0;
    /**
     * 받는이
     */
    public static final int DELIVER_RECEIVER = 1;

    public static final int SOCIAL_FACEBOOK = 0;
    public static final int SOCIAL_TWITTER = 1;
    public static final int SOCIAL_METODAY = 2;
    public static final int SOCIAL_NAVER_BLOG = 3;
    public static final int SOCIAL_NAVER_CAFE = 4;
    public static final int SOCIAL_DAUM_CAFE = 5;
    public static final int SOCIAL_DIRECT_INPUT = 6;

    // -------------------- 주문상태 구분 --------------------
    /**
     * 접수준비(014002)
     */
    public static final String ORD_STATUS1_READY = "014002";
    /**
     * 결제완료(014003)
     */
    public static final String ORD_STATUS1_COMP = "014003";
    /**
     * 인화보류(014004)
     */
    public static final String ORD_STATUS2_REMAKE = "014004";
    /**
     * 주문처리중(014006)
     */
    public static final String ORD_STATUS3_ING = "014006";
    /**
     * 발송완료(014008)
     */
    public static final String ORD_STATUS4_SENDED = "014008";
    /**
     * 거래완료(014009)
     */
    public static final String ORD_STATUS5_END = "014009";

    // -------------------- 작품상태 구분 --------------------
    /**
     * 작품(146000)
     */
    public static final String MYART_STATUS0_ART = "146000";
    /**
     * 장바구니(146001)
     */
    public static final String MYART_STATUS1_CART1 = "146001";
    /**
     * 장바구니(146002)
     */
    public static final String MYART_STATUS2_CART2 = "146002";
    /**
     * 결재완료(146003)
     */
    public static final String MYART_STATUS3_STTL = "146003";

    // // -------------------- 상품구분 --------------------
    // /** 콜라주(00800600080001) */
    // public static final String PRODUCT0_COLLAGE = "00800600080001";
    // /** 스티커(00802100010001) */
    // public static final String PRODUCT1_STICKER = "00802100010001";
    // /** 명함(00800900050003) */
    // public static final String PRODUCT2_NAMECARD = "00800900050003";

    public static final String CART_STEP = "cart_step";

    // ----------- Snaps앱용 -----------
    // -------------------- 로그인 구분 --------------------
    /**
     * SNAPS(189999)
     */
    public static final String SNAPSLOGIN_SNAPS = "189006"; // 189999 => 189006 수
    /**
     * TWITTER(189003)
     */
    public static final String SNAPSLOGIN_TWITTER = "189003";
    /**
     * FACEBOOK(189002)
     */
    public static final String SNAPSLOGIN_FACEBOOK = "189002";

    // -------------------- 회원가입/수정 여부 --------------------
    /**
     * INSERT(189999)
     */
    public static final String SNAPSJOIN_INSERT = "I";
    /**
     * MODIFY(189003)
     */
    public static final String SNAPSJOIN_MODIFY = "M";
    // ----------- ----------- -----------

    // -------------------- 로그인 프로세스 구분 --------------------
    /**
     * 로그인
     */
    public static final String LOGIN_P_LOGIN = "login";
    /**
     * 회원가입
     */
    public static final String LOGIN_P_JOIN = "join";
    /**
     * 회원탈퇴
     */
    public static final String LOGIN_P_RETIRE = "retire";
    /**
     * 비밀번호 재설정
     */
    public static final String LOGIN_P_PWDRESET = "pwdreset";
    /**
     * 비밀번호 찾기.
     */
    public static final String LOGIN_P_PWDFIND = "pwdfind";
    /**
     * 로그인 후 result 반환
     */
    public static final String LOGIN_P_RESULT = "login_result";
    /**
     * 추가인증
     */
    public static final String LOGIN_P_VERRIFY = "verrify";
    /**
     * 추가인증 팝업
     */
    public static final String LOGIN_P_VERRIFY_POPUP = "verrify_popup";
    /**
     * 휴면계정 해제
     */
    public static final String LOGIN_P_REST_ID = "rest_id";
    // ----------- ----------- -----------

    // -------------------- 이벤트 응모상태 구분 --------------------
    /**
     * 공유안함
     */
    public static final String EVENT_STATUS_NOSHARE = "N";
    /**
     * 공유함
     */
    public static final String EVENT_STATUS_SHARE = "Y";
    /**
     * 당첨
     */
    public static final String EVENT_STATUS_WINNER = "winner";
    /**
     * 꽝
     */
    public static final String EVENT_STATUS_LOSER = "loser";
    // ----------- ----------- -----------

    // -------------------- 이벤트 공유타입 구분 --------------------
    /**
     * 카카오톡
     */
    public static final String EVENT_SHARE_TALK = "talk";
    /**
     * 카카오스토리
     */
    public static final String EVENT_SHARE_STORY = "story";
    /**
     * 주문
     */
    public static final String EVENT_SHARE_ORDER = "order";
    // ----------- ----------- -----------

    // -------------------- 제휴코드 구분 --------------------
    /**
     * 카카오톡
     */
    public static final String HPPN_TYPE_KAKOTALK = "kakao";
    // ----------- ----------- -----------

    // -------------------- 결제방법 구분 --------------------
    /**
     * 결제방법-신용카드
     */
    public static final String STTL_MTHD_CREDITCARD = "012001";
    /**
     * 결제방법-핸드폰결제
     */
    public static final String STTL_MTHD_PHONE = "012002";
    // ----------- ----------- -----------

    // -------------------- 쿠폰종류 구분 --------------------
    /**
     * 쿠폰-티켓몬스터
     */
    public static final String CPN_TYPE_TMON = "timon";
    /**
     * 쿠폰-스냅스
     */
    public static final String CPN_TYPE_SNAPS = "snaps";
    // ----------- ----------- -----------

    // -------------------- 로컬 이미지 로드 타입 --------------------
    /**
     * QRCode
     */
    public static final int QRCODE_TYPE = 100;
    /**
     * spine
     */
    public static final int SPINE_TYPE = 101;
    /**
     * no print
     */
    public static final int NOPRINT_TYPE = 102;
    /**
     * book stick
     */
    public static final int SPINE_IMAGE_TYPE = 103;
    // ----------- ----------- -----------

    public static final String EXTRAS_HAMBURGER_MENU_ACT = "extras_hamburger_menu_act";
    public static final String EXTRAS_HAMBURGER_MENU_FRG = "extras_hamburger_menu_frg";
    public static final String EXTRAS_HAMBURGER_MENU_START_ANIM_IN = "extras_hamburger_menu_start_anim_in";
    public static final String EXTRAS_HAMBURGER_MENU_START_ANIM_OUT = "extras_hamburger_menu_start_anim_out";

    public static final int HOME_MENU_ID_CS_LIST = 9000;
    public static final int HOME_MENU_ID_AUTO_SELECT_PHOTO_FOR_TEST = 9001;
    public static final int HOME_MENU_ID_DELETE_ALL_INTERNAL_MEMORY = 9002;
    public static final int HOME_MENU_ID_FORCE_CRASH = 9003;
    public static final int HOME_MENU_ID_FORCE_SERVER = 9004;
    public static final int HOME_MENU_ID_SET_DEVELOP_URL = 9005;
    public static final int HOME_MENU_ID_DELETE_DEVELOP_URL = 9006;
    public static final int HOME_MENU_ID_USE_DRAW_SMART_SNAPS_SEARCH_AREA = 9007;
    public static final int HOME_MENU_ID_CREATE_ALL_UPLOAD_IMAGE_HISTORY = 9008;
    public static final int HOME_MENU_ID_DELETE_UPLOAD_IMAGE_HISTORY = 9009;
}
