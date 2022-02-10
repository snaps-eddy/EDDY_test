package com.snaps.common.utils.constant;

import android.os.Build;
import android.util.Log;

import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.StringUtil;

public class SnapsAPI {
    private static final String TAG = SnapsAPI.class.getSimpleName();
    public final static String AI_SYNC_PHOTO_REAL_DOMAIN = "http://saida-m.snaps.com/";
    public final static String AI_SYNC_PHOTO_TEST_DOMAIN = "http://stg-ai.snaps.com/";

    private static final String TEST_NATIVE_INTERFACE_DOMAIN = "https://dev-mapi.snaps.com";
    private static final String REAL_NATIVE_INTERFACE_DOMAIN = "http://clientapi.snaps.com";

    public static final String URL_TEXT_TO_IMAGE_DOMAIN = "https://text2.snaps.com";

    public static final String CRM_WEBVIEW_REAL_DOMAIN = "https://home.snaps.com"; //117.52.102.216 ~ 225

    private static final String URL_WEB_LOG_DOMAIN_REAL = "http://logz.snaps.com:12509/snaps.mobile";
    private static final String URL_WEB_LOG_DOMAIN_TEST = "http://logz.snaps.com:12509/dev.snaps.mobile";

    public static final String PLAY_STORE_UPDATE_URL = "market://details?id=com.snaps.mobile.kr";
    public static final String APP_STORE_UPDATE_URL = "https://itunes.apple.com/kr/app/seunaebseu-hyudaeponsajingwa/id692816867?l=ko&amp;ls=1&amp;mt=8";

    private static final String PART_SMART_PHOTO_CS = "part=mall.smartphoto.SmartPhotoCSInterface";
    private static final String PART_SMART_PHOTO = "part=mall.smartphoto.SmartPhotoInterface";
    private static final String PART_SMART_PHOTO_LITE = "part=mall.smartphotolite.SmartPhotoLiteInterface";
    private static final String PART_MOBILE_GET = "part=mobile.GetData";
    private static final String PART_MOBILE_SET = "part=mobile.SetData";
    private static final String PART_DIARY_INTERFACE = "part=mall.applInterface.DiaryInterface";
    private static final String PART_SAMRT_SNAPS_LAYOUT_RECOMMEND_INTERFACE = "part=mall.smartsnaps.SmartSnapsInterface";
    private static final String PART_SAMRT_SNAPS_LAYOUT_RECOMMEND_V2_INTERFACE = "part=mall.smartsnaps.SmartSnapsV2Interface";
    private static final String PART_RECOMMEND_PHOTOBOOK_INTERFACE = "part=mall.smartsnaps.RecommendPhotoBookInterface";
    private static final String NATIVE_UI_INTERFACE_LIST_PART = "/list";
    private static final String NATIVE_UI_INTERFACE_SUB_LIST_PART = "/product/list";
    private static final String NATIVE_UI_INTERFACE_DETAIL_PART = "/detail";

    public static final String NEW_BILLING_KR_INIPAY_URL = "/mw/v3/order/sttlRequest.jsp";

    /**
     * Domain Setting
     */
    public static String DOMAIN() {
        return DOMAIN(false);
    }

    public static String DOMAIN(boolean isSecure) {
        return (isSecure ? "https://" : "http://") + Config.getBackendAPIBase();
    }

    public static String FRONTEND_DOMAIN(String languageCode, boolean isCart) {
        String parsedLanguageCode = StringUtil.converLanguageCodeToCountryCode(languageCode);
        return "https://" + Config.getFrontEndAPIBase(parsedLanguageCode) + (isCart ? "/member/cart" : "");
    }

    public static String WEB_DOMAIN(boolean isThirdpartyapp, String urlBody, String userid) {
        if (isThirdpartyapp) {
            return WEB_DOMAIN(urlBody, "", userid);
        } else {
            return WEB_DOMAIN(urlBody, userid, "");
        }
    }

    public static String WEB_DOMAIN(String urlBody, String userno, String uuserid) {
        String body = urlBody + (urlBody.contains("?") ? (urlBody.endsWith("?") ? "" : "&") : "?");
        body += String.format("f_chnl_code=%s", Config.getCHANNEL_CODE());

        if (!uuserid.equals(""))
            body += String.format("&f_uuser_id=%s", uuserid);

        if (!userno.equals(""))
            body += String.format("&f_user_no=%s", userno);

        return DOMAIN() + body;
    }

    public static String WEB_DOMAIN() {
        return DOMAIN(false);
    }

    // Command 설정.
    private static String SERVLET_COMMAND_NORMAL() {
        return DOMAIN() + "/servlet/Command.do?";
    }

    private static String SERVLET_COMMAND_SECURE() {
        return DOMAIN(true) + "/servlet/Command.do?";

    }

    private static String NATIVE_UI_INTERFACE_PATH() {
        return (Config.isRealServer() ? REAL_NATIVE_INTERFACE_DOMAIN : TEST_NATIVE_INTERFACE_DOMAIN) + "/app/v3";
    }

    public static String MENU_FONT_DOWNLOAD_PATH() {
        return SnapsAPI.DOMAIN() + "/Upload/Data1/Resource/mobileUIFont/";
    }

    public static String getUiMenuData(String categoryVer, String subCategoryVer, String layoutVer, String homeValueVer, String menuValueVer, String photoPrintVer, String spineInfoVer) {
        return SERVLET_COMMAND_NORMAL() + "part=mobile.GetData&cmd=getConfigInfo&nextPage=getConfigInfo&prmChnlCode=" + SnapsConfigManager.getInstance().getCHANNEL_CODE() + "&prmOsType=190002&prmVersion=" +
                categoryVer + "," + subCategoryVer + "," + layoutVer + "," + homeValueVer + "," + menuValueVer + "," + photoPrintVer + "," + spineInfoVer +
//                "&prmTest=" + (Config.isRealServer() ? "N" : "Y");
                "&prmTest=N";
    }

    /**
     * 다국어 이용약관 url
     */
    public static String AGREEMENT_URL_MOBILE() {
        return WEB_DOMAIN() + "/mw/v3/rules/service.jsp?lang=";
    }

    /**
     * 다국어 개인정보취급방침 url
     */
    public static String PRIVACY_URL_MOBILE() {
        return WEB_DOMAIN() + "/mw/v3/rules/privacy.jsp?lang=";
    }

    /**
     * 공지사항 url
     */
    public static String NOTICE_URL() {
        return WEB_DOMAIN() + "/mw/v3/site/snaps/notice/index.jsp";
    }

    /**
     * 이용문의 url
     */
    public static String QNA_URL() {
        return WEB_DOMAIN() + "/mw/v3/cs/index.jsp?f_user_no=";
    }

    public static String PRODUCT_PHOTOPRINT_URL() {
        return DOMAIN() + "/page/app_photoprint.jsp";
    }

    public static String GET_HOME_CRM_INFO_URL() {
        return DOMAIN() + "/servlet/Command.do?part=mobile.GetData&cmd=getCrmUserInfo&nextPage=getCrmUserInfo&prmUserNo=";
    }

    public static String GET_HOME_DELIVERY_INFO_URL() {
        return DOMAIN() + "/servlet/Command.do?part=mobile.GetData&cmd=getNewDlvrInfoList&nextPage=getNewDlvrInfoList";
    }

    /**
     * [Mobile 다국어버전] 앱 버전, 템플릿들의 버전, 긴급공지 데이터 조회용
     */
    public static String GET_UPDATEINFO_MOBILE() {
        return DOMAIN() + "/servlet/Command.do?part=mobile.SmartPhotoforSDK&cmd=getJosnUpdateInfo&nextPage=getJosnUpdateInfo&prmChnlCode=%s&prmTest="; // &prmTest=N"; // renewal
    }

    /**
     * 포토북 페이지 리소스 호출
     */
    public static String GET_API_THEMEBOOK_ADD_PAGE() {
        return SERVLET_COMMAND_NORMAL() + PART_SMART_PHOTO_LITE + "&cmd=getMultiFormList&prmPageType=page" + "&prmChnlCode=" + Config.getCHANNEL_CODE();
    }

    /**
     * 테마북 커버 리소스 호출
     */
    public static String GET_API_THEMEBOOK_COVER() {
        return SERVLET_COMMAND_NORMAL() + PART_SMART_PHOTO_LITE + "&cmd=getMultiFormList&prmPageType=cover" + "&prmChnlCode=" + Config.getCHANNEL_CODE();
    }

    /**
     * 테마북 categoty
     */
    public static String GET_API_THEMEBOOK_CATEGORY() {
        return SERVLET_COMMAND_NORMAL() + PART_MOBILE_GET + "&cmd=ED_ST&nextPage=ED_ST&F_SID=ED_ST&F_CHNL_CODE=KOR0005&F_MODE=category&F_CLSS_CODE=039027";
    }

    /**
     * 테마북 contents
     */
    public static String GET_API_THEMEBOOK_CONTENTS() {
        return SERVLET_COMMAND_NORMAL() + PART_MOBILE_GET + "&cmd=ED_ST&nextPage=ED_ST&F_SID=ED_ST&F_CHNL_CODE=KOR0005&f_clss_code=039027";
    }

    /**
     * 상품 리소스 이미지 호출
     */
    public static String GET_API_RESOURCE_IMAGE() {
        return SERVLET_COMMAND_NORMAL() + PART_SMART_PHOTO_LITE + "&cmd=getResource&nextPage=getResource&rtype=design" + "&prmChnlCode=" + Config.getCHANNEL_CODE();
    }

    public static String GET_API_MASK_IMAGE() {
        return DOMAIN() + "/servlet/Command.do?" + PART_SMART_PHOTO_LITE + "&cmd=getResource&nextPage=getResource&rtype=design" + "&prmChnlCode=" + Config.getCHANNEL_CODE();
    }


    public static String GET_API_CALENDAR_TITLE_DAY() {
        return SERVLET_COMMAND_NORMAL() + PART_SMART_PHOTO_LITE + "&cmd=getTitleDayXml&nextPage=getTitleDayXml";
    }

    /**
     * 상품 프로젝트 코드
     */
    public static String GET_API_PROJECT_CODE() {
        return SERVLET_COMMAND_NORMAL() + PART_SMART_PHOTO_LITE + "&cmd=getProjCode&nextPage=getProjcode&prmChnlCode=" + Config.getCHANNEL_CODE();
    }

    /**
     * 주문 번호 인증..
     */
    public static String GET_API_VERIFY_PROJECT_CODE() {
        return SERVLET_COMMAND_NORMAL() + PART_SMART_PHOTO_CS + "&cmd=getIsAfterOrderEdit";
    }

    /**
     * 템플릿 데이터.
     */
    public static String GET_API_TEMPLATE() {
        return SERVLET_COMMAND_NORMAL() + PART_SMART_PHOTO_LITE + "&cmd=getTmplScriptRsrcMulti&nextPage=getTmplScriptRsrcMulti&prmChnlCode=" + Config.getCHANNEL_CODE()
                + "&prmdsplcode=&categorycode=";
    }

    /**
     * 멀티 템플릿 데이터. 테마 북[소프트-A5] / TBA5MT_1u / 045021001065 테마 북[소프트-A6] / TBA6MT_1u / 045021001066 테마 북-무료출판 / FTBA6MT_1u / 045021001067
     */
    public static String GET_API_MULTITEMPLATE() {
        return SERVLET_COMMAND_NORMAL() + PART_SMART_PHOTO_LITE + "&cmd=getCalendarTmplMulti&nextPage=getCalendarTmplMulti&prmChnlCode=" + Config.getCHANNEL_CODE();
    }

    public static String GET_API_MULTIPLEDATATEMPLATE() {
        return SERVLET_COMMAND_NORMAL() + PART_SMART_PHOTO_LITE + "&cmd=getCalendarTmplMultipleData&nextPage=getCalendarTmplMulti&prmChnlCode=" + Config.getCHANNEL_CODE();
    }

    public static String GET_API_NORMALTEMPLATE() {
        return SERVLET_COMMAND_NORMAL() + PART_SMART_PHOTO_LITE + "&cmd=getTmplScriptRsrc&nextPage=getCalendarTmplMulti&prmChnlCode=" + Config.getCHANNEL_CODE();
    }

    /**
     * 배경, 레이아웃 리스트
     */
    // TODO : 실서버 반영시 test 제거.
    public static String GET_API_MOBILE_TMPLRSRC_LIST() {
        return SERVLET_COMMAND_NORMAL() + PART_SMART_PHOTO_LITE + "&cmd=getMobileTmplRsrcList&nextPage=getMobileTmplRsrcList&prmChnlCode="
                + Config.getCHANNEL_CODE();
    }

    /**
     * 에러로그 전송
     */
    public static String POST_API_INSERT_ERRORLOG() {
        return SERVLET_COMMAND_NORMAL() + PART_MOBILE_SET + "&cmd=insertErrLog&nextPage=insertErrLog&F_CHNL_CODE=" + Config.getCHANNEL_CODE();
    }

    /**
     * 이벤트 응모
     */
    public static String POST_API_EVENT_APPLY() {
        return SERVLET_COMMAND_NORMAL() + PART_MOBILE_SET + "&cmd=EV_APPLY&nextPage=EV_APPLY&F_SID=EV_APPLY&F_CHNL_CODE=" + Config.getCHANNEL_CODE();
    }

    /**
     * 이벤트 결과확인
     */
    public static String POST_API_EVENT_GIFT() {
        return (Config.isRealServer() ? SERVLET_COMMAND_SECURE() : SERVLET_COMMAND_NORMAL()) + PART_MOBILE_SET + "&cmd=EV_GIFT&nextPage=EV_GIFT&F_SID=EV_GIFT&F_CHNL_CODE="
                + Config.getCHANNEL_CODE();
    }

    /**
     * 푸쉬로 실행 시 통계기록
     */
    public static String POST_API_PUSH_INFO() {
        return SERVLET_COMMAND_NORMAL() + PART_MOBILE_SET + "&cmd=PS_SEND&nextPage=PS_SEND&F_SID=PS_SEND&F_CHNL_CODE=" + Config.getCHANNEL_CODE();
    }

    // ---------------------------------------------------- 회원관련 API
    // ----------------------------------------------------

    /**
     * snaps 회원가입
     */
    public static String POST_API_SNAPS_JOIN() {
        return (Config.isRealServer() ? SERVLET_COMMAND_SECURE() : SERVLET_COMMAND_NORMAL())
                + PART_MOBILE_SET
                + "&cmd=MY_JOIN&nextPage=MY_JOIN&F_SID=MY_JOIN&F_CHNL_CODE="
                + Config.getCHANNEL_CODE();
    }

    public static String getSnapsLoginPostApi() { // POST_API_SNAPS_LOGIN값에 KOR0030이 들어가는 케이스가 있어서 수정
        StringBuilder sb = new StringBuilder();
        sb.append(Config.isRealServer() ? SERVLET_COMMAND_SECURE() : SERVLET_COMMAND_NORMAL());
        sb.append(PART_MOBILE_GET).append("&cmd=MY_LOGIN&nextPage=MY_LOGIN&F_SID=MY_LOGIN&F_CHNL_CODE=");
        sb.append(Config.getCHANNEL_CODE());
        return sb.toString();
    }

    /**
     * snaps 비번찾기
     *
     * @params F_USER_EMAIL
     */
    public static String POST_API_SNAPS_PWDFIND() {
        return (Config.isRealServer() ? SERVLET_COMMAND_SECURE() : SERVLET_COMMAND_NORMAL())
                + PART_MOBILE_GET
                + "&cmd=MY_FIND&nextPage=MY_FIND&F_SID=MY_FIND&F_CHNL_CODE="
                + Config.getCHANNEL_CODE();
    }

    /**
     * snaps 비밀번호 재설정
     *
     * @params F_USER_ID 회원번호
     * @params F_TMP_PASS 임시비번
     * @params F_NEW_PASS 재설정비번
     */
    public static String POST_API_SNAPS_PWDRESET() {
        return (Config.isRealServer() ? SERVLET_COMMAND_SECURE() : SERVLET_COMMAND_NORMAL())
                + PART_MOBILE_SET
                + "&cmd=MY_PASS&nextPage=MY_PASS&F_SID=MY_PASS&F_CHNL_CODE="
                + Config.getCHANNEL_CODE();
    }

    /**
     * snaps 탈퇴(회원확인)
     *
     * @params F_MODE (validate(회원확인), proc(탈퇴))
     * @params f_user_id
     * @params f_user_pwd (회원확인할 때만 입력)
     */
    public static String POST_API_SNAPS_RETIRE() {
        return (Config.isRealServer() ? SERVLET_COMMAND_SECURE() : SERVLET_COMMAND_NORMAL())
                + PART_MOBILE_SET
                + "&cmd=MY_LEAVE&nextPage=MY_LEAVE&F_SID=MY_LEAVE&F_CHNL_CODE="
                + Config.getCHANNEL_CODE();
    }

    // ---------------------------------------------------- 회원관련 API
    // ----------------------------------------------------

    /**
     * snaps 스티커 상세 리스트.
     *
     * @params F_CATE_CODE
     */
    public static String GET_API_IMAGE_LIST_STICKER() {
        return SERVLET_COMMAND_NORMAL() + PART_MOBILE_GET + "&cmd=ED_ST&nextPage=ED_ST&F_SID=ED_ST&F_CHNL_CODE=" + Config.getCHANNEL_CODE();
    }

    /**
     * snaps 스티커 앨범 리스트.
     */
    public static String GET_API_IMAGE_ALBUMLIST_STICKER() {
        return SERVLET_COMMAND_NORMAL() + PART_MOBILE_GET + "&cmd=ED_ST&nextPage=ED_ST&F_SID=ED_ST&F_MODE=category&F_CHNL_CODE=" + Config.getCHANNEL_CODE();
    }

    /**
     *
     * 메뉴 데이터 요청 API
     */

    /**
     * 작품함 리스트
     *
     * @params F_USER_ID 유저 아이디.
     * @params f_hppn_type 제휴코드
     */
    public static String GET_API_MY_PROJECT_LIST() {
        return SERVLET_COMMAND_NORMAL() + PART_MOBILE_GET + "&cmd=MY_PRJ&nextPage=MY_PRJ&F_SID=MY_PRJ&F_CHNL_CODE=" + Config.getCHANNEL_CODE();
    }

    /**
     * 작품 상세보기
     *
     * @params F_PROJ_CODE 상품 코드
     */
    public static String GET_API_MY_PROJECT_DETAIL() {
        return SERVLET_COMMAND_NORMAL() + PART_MOBILE_GET + "&cmd=MY_DTL&nextPage=MY_DTL&F_SID=MY_DTL&F_CHNL_CODE=" + Config.getCHANNEL_CODE();
    }

    /**
     * 작품함 장바구니 삭제 , 작품함 장바구니로 저장.
     *
     * @return RETURN_MSG 응답메세지 ( 삭제완료 / 오류메세지 )
     * @params F_USER_ID 유저 ID
     * @params F_FLAG 삭제 / 저장 ( del / save )
     * @params F_PROJ_CODE 프로젝트 코드.
     * @params F_BAG_ STAT 구분값
     */
    public static String POST_API_MY_PROJECT_OPTION_FLAG() {
        return SERVLET_COMMAND_NORMAL() + PART_MOBILE_SET + "&cmd=MY_PRJ&nextPage=MY_PRJ&F_SID=MY_PRJ" + "&F_CHNL_CODE=" + Config.getCHANNEL_CODE();
    }

    /**
     * 결재완료된 작품 장바구니에 추가
     *
     * @params F_USER_ID 유저 ID
     * @params F_PROJ_CODE 프로젝트 코드.
     */
    public static String POST_API_MY_PROJECT_CART_READD() {
        return SERVLET_COMMAND_NORMAL() + PART_MOBILE_SET + "&cmd=MY_REORDER&nextPage=MY_REORDER&F_SID=MY_REORDER" + "&f_chnl_code="
                + Config.getCHANNEL_CODE();
    }

    /**
     * 공지사항 리스트
     *
     * @params F_PAGE_NO 페이지번호
     */
    public static String GET_API_NOTICE() {
        return SERVLET_COMMAND_NORMAL() + PART_MOBILE_GET + "&cmd=MY_PBL&nextPage=MY_PBL&F_SID=MY_PBL&F_CHNL_CODE=" + Config.getCHANNEL_CODE();
    }

    /**
     * 이용문의 등록
     *
     * @params F_USER_ID 회원ID
     * @params F_CONTENTS 문의 댓글
     */
    public static String POST_API_QUESTION_COMMNET() {
        return SERVLET_COMMAND_NORMAL() + PART_MOBILE_SET + "&cmd=MY_C3&nextPage=MY_C3&F_SID=MY_C3&F_CHNL_CODE=" + Config.getCHANNEL_CODE();
    }

    /**
     * 이용문의 리스트
     *
     * @params F_USER_ID 회원아이디
     * @params F_PAGE_NO 페이지 번호.
     */
    public static String GET_API_QUESTION_LIST() {
        return SERVLET_COMMAND_NORMAL() + PART_MOBILE_GET + "&cmd=MY_C1&nextPage=MY_C1&F_SID=MY_C1&F_CHNL_CODE=" + Config.getCHANNEL_CODE();
    }

    /**
     * 쿠폰 등록
     */
    public static String SET_COUPON() {
        return SERVLET_COMMAND_NORMAL() + PART_MOBILE_SET + "&cmd=MY_CPN&nextPage=MY_CPN&F_SID=MY_CPN&F_CHNL_CODE=" + Config.getCHANNEL_CODE();
    }

    /**
     * 제휴사 쿠폰 정보 가져오기 쿠폰 분류 정
     */
    public static String COUPON_KIND_INFO() {
        return SERVLET_COMMAND_NORMAL() + PART_MOBILE_GET + "&cmd=MY_CPN&nextPage=MY_CPN&f_mode=clss&F_CHNL_CODE=" + Config.getCHANNEL_CODE();
    }

    // 쿠폰 초기화...
    // http://117.52.102.177/servlet/Command.do?part=mobile.SetData&cmd=MY_CPN&nextPage=MY_CPN&F_SID=MY_CPN&F_CHNL_CODE=KOR0031&F_USER_ID=308559&F_PROJ_CODE=193001&f_mode=delete
    public static String COUPON_INFO_RESET() {
        return SERVLET_COMMAND_NORMAL() + PART_MOBILE_SET + "&cmd=MY_CPN&nextPage=MY_CPN&F_SID=MY_CPN&f_mode=delete&F_CHNL_CODE=" + Config.getCHANNEL_CODE();
    }

    // ---------------------------------------------------- 주문관련 API
    // ----------------------------------------------------

    /**
     * 주문배송 리스트
     *
     * @params F_USER_ID 회원아이디
     * @params F_PAGE_NO 페이지 번호
     */
    public static String GET_API_ORDER_LIST() {
        return SERVLET_COMMAND_NORMAL() + PART_MOBILE_GET + "&cmd=MY_DVY&nextPage=MY_DVY&F_SID=MY_DVY&F_CHNL_CODE=" + Config.getCHANNEL_CODE();
    }

    /**
     * 주문배송 상세정보
     *
     * @params F_ORDER_CODE 주문코드
     * @params F_USER_ID 회원아이디
     */
    public static String GET_API_ORDER_DETAIL() {
        return SERVLET_COMMAND_NORMAL() + PART_MOBILE_GET + "&cmd=MY_ODL&nextPage=MY_ODL&F_SID=MY_ODL&F_CHNL_CODE=" + Config.getCHANNEL_CODE();
    }

    /**
     * 카카오 주문요청
     *
     * @params F_USER_ID 회원아이디
     * @params F_PROJ_CODES 프로젝트코드,수량
     */
    public static String POST_API_KAKAO_BILL_REQ() {
        return SERVLET_COMMAND_SECURE() + PART_MOBILE_SET + "&cmd=MY_STTL&nextPage=MY_STTL&F_SID=MY_STTL&F_CHNL_CODE=" + Config.getCHANNEL_CODE();
    }

    /**
     * 스냅스 주문요청 스냅스 프로젝트 수량 업데이트
     *
     * @params F_USER_ID 회원아이디
     * @params F_PROJ_CODE 프로젝트코드,수량
     * @params F_ORDER_CODE 주문코드
     */
    public static String POST_API_SNAPS_BILL_REQ() {
        return SERVLET_COMMAND_SECURE() + PART_MOBILE_SET + "&cmd=MY_ORD&nextPage=MY_ORD&F_SID=MY_ORD&F_CHNL_CODE=" + Config.getCHANNEL_CODE();
    }

    /**
     * 배송지,가격정보 저장
     *
     * @params F_ORDER_CODE 주문코드,...........
     */
    public static String POST_API_ORDER_ADDR_SAVE() {
        return SERVLET_COMMAND_SECURE() + PART_MOBILE_SET + "&cmd=MY_ADR&nextPage=MY_ADR&F_SID=MY_ADR&F_CHNL_CODE=" + Config.getCHANNEL_CODE();
    }

    /**
     * 카카오 결재내역 확인
     *
     * @params checkout_ticket 카카오티켓
     * @params pay_id 결제id
     * @params checkout_hash hash
     */
    public static String POST_API_KAKAO_BILL_CONFIRM() {
        return SERVLET_COMMAND_SECURE() + PART_MOBILE_SET + "&cmd=MY_CONFIRM&nextPage=MY_CONFIRM&F_SID=MY_CONFIRM&F_CHNL_CODE=" + Config.getCHANNEL_CODE();
    }

    /**
     * 주문 취소
     *
     * @params F_ORDER_CODE 주문코드
     */
    public static String POST_API_ORDER_CANCEL() {
        return SERVLET_COMMAND_NORMAL() + PART_MOBILE_SET + "&cmd=MY_CANCEL&nextPage=MY_CANCEL&F_SID=MY_CANCEL&F_CHNL_CODE=" + Config.getCHANNEL_CODE();
    }

    // ---------------------------------------------------- 주문관련 API
    // ----------------------------------------------------

    /**
     * 내작품함, 장바구니 갯수 조회
     */
    public static String POST_API_BADGE_COUNT() {
        return SERVLET_COMMAND_NORMAL() + PART_MOBILE_GET + "&cmd=MY_BADGE&nextPage=MY_BADGE&F_SID=MY_BADGE&F_CHNL_CODE=" + Config.getCHANNEL_CODE();
    }

    /**
     * 홈 가격정보
     */
    public static String GET_API_HOME_PRICE() {
        return SERVLET_COMMAND_NORMAL() + PART_MOBILE_GET + "&cmd=HM_INFO&nextPage=HM_INFO&F_SID=HM_INFO&F_CHNL_CODE=" + Config.getCHANNEL_CODE();
    }
    // --- 배송정보 입력 관련 ---

    /**
     * 우편번호 검색
     *
     * @params F_dong 검색어(동이름)
     */
    public static String GET_API_MY_ZIP() {
        return SERVLET_COMMAND_NORMAL() + PART_MOBILE_GET + "&cmd=MY_ZIP&nextPage=MY_ZIP&F_SID=MY_ZIP&F_CHNL_CODE=" + Config.getCHANNEL_CODE();
    }

    ;

    /**
     * 최근배송지 리스트
     *
     * @params F_USER_ID 유저아이디
     */
    public static String GET_API_MY_DLEIVERY_ORDER() {
        return SERVLET_COMMAND_NORMAL() + PART_MOBILE_GET + "&cmd=MY_HIS&nextPage=MY_HIS&F_SID=MY_HIS&F_CHNL_CODE=" + Config.getCHANNEL_CODE();
    }

    ;

    /**
     * 카카오 결재내역 확인
     *
     * @params f_device_no 기기정보
     * @params f_other_id 앱UUID
     * @params f_user_no 스냅스회원NO
     * @params f_uuser_id 카카오회원ID
     * @params f_user_name 회원명
     * @params f_os_type OS Type
     * @params f_os_ver Os 버전
     * @params f_app_ver 앱버전
     */
    public static String POST_API_PUSH_DEV() {
        String url = SERVLET_COMMAND_NORMAL() + PART_MOBILE_SET + "&cmd=CM_DEV&nextPage=CM_DEV&F_SID=CM_DEV&F_CHNL_CODE=" + Config.getCHANNEL_CODE();
        Dlog.d("POST_API_PUSH_DEV() url:" + url);
        return url;
    }

    public static String PUSH_INTERFACE() {
        return SERVLET_COMMAND_NORMAL() + PART_MOBILE_GET + "&cmd=CRM_BROADCAST&nextPage=CRM_BROADCAST&F_CHNL_CODE=" + Config.getCHANNEL_CODE();
    }

    public static String PUSH_RECEIVE_INTERFACE() {
        return SERVLET_COMMAND_NORMAL() + "part=mobile.SetTaskData&cmd=updateAppPushRcvStatus&nextPage=updateAppPushRcvStatus";
    }

    public static String PUSH_SEND_INTERFACE() {
        return SERVLET_COMMAND_NORMAL() + PART_MOBILE_SET + "&cmd=CRM_LOG&nextPage=CRM_LOG";
    }

    public static String KAKAO_TRNASFER_ID_INTERFACE() {
        return SERVLET_COMMAND_NORMAL() + PART_MOBILE_GET + "&cmd=TR_PROJ&nextPage=TR_PROJ&F_CHNL_CODE=" + Config.getCHANNEL_CODE();
    }

    public static String KAKAO_TRNASFER_DATA_INTERFACE() {
        return SERVLET_COMMAND_NORMAL() + PART_MOBILE_SET + "&cmd=TR_KAKAO&nextPage=TR_KAKAO&F_CHNL_CODE=" + Config.getCHANNEL_CODE();
    }

    // ------------------------------------------------------------ 카카오 스토리 연동시
    // 사용하지 않는 api
    // ------------------------------------------------------------//

    // save.xml receive 현재 사진인화만 사용..
    public static String GET_API_SAVE_XML() {
        return SERVLET_COMMAND_NORMAL() + PART_SMART_PHOTO_LITE + "&cmd=getXmlPathNew2" + "&prmChnlCode=" + Config.getCHANNEL_CODE();
    }

    public static String GET_API_PHOTO_PRINT() {
        return SERVLET_COMMAND_NORMAL() + PART_MOBILE_GET + "&cmd=PD_PRICE&nextPage=PD_PRICE" + "&F_CHNL_CODE=" + Config.getCHANNEL_CODE();
    }

    // 사진인화 페이스북 사진 imgseq 미리 따는 코드...
    public static String GET_API_SNS_IMAGE_IMGSEQ() {
        return SERVLET_COMMAND_NORMAL() + PART_SMART_PHOTO_LITE + "&cmd=setOuterImgPathSave&nextPage=setOuterImgPathSave&prmOrgnCode=140003";
    }

    // 리뷰 쓰기 이벤트
    public static String SET_API_REVIEWEVENT() {
        return SERVLET_COMMAND_NORMAL() + PART_MOBILE_SET + "&cmd=EV_APPLY&nextPage=EV_APPLY&F_SID=EV_APPLY&f_board_type=000068&F_CHNL_CODE=" + Config.getCHANNEL_CODE();
    }

    public static String SET_API_SAMPLEVIEW() {
        return SERVLET_COMMAND_NORMAL() + "part=mall.gallery.GalleryManager&cmd=getViewXml&f_view_mode=";
    }

    public static String POST_API_APP_LAUNCH_COUNT() {
        return SERVLET_COMMAND_NORMAL() + PART_SMART_PHOTO + "&cmd=setAppLaunchCount&nextPage=aaa&prmChnlCode=" + Config.getCHANNEL_CODE() + "&prmAppType=mobile"
                + "&prmOsType=190002&prmOsVer=" + Build.VERSION.RELEASE;
    }

    public static String POST_API_WEB_LOG() {
        return Config.isRealServer() ? URL_WEB_LOG_DOMAIN_REAL : URL_WEB_LOG_DOMAIN_TEST;
    }

    //
    public static String CALENDAR_TITLE_INFO() {
        return GET_API_CALENDAR_TITLE_DAY();
    }

    public static String GET_API_COUPON_APPLYLIST() {
        return SERVLET_COMMAND_NORMAL() + "part=mobile.SetData&cmd=MY_CPN&nextPage=MY_CPN&F_SID=MY_CPN" + "&f_mode=prod";
    }

    // 카카오톡 친구초대 이벤트 등록
    // (등록상태 : 315001 - 임시등록, 315002 :정상등록)
    public static String REG_KAKAO_INVITE_EVENT() {
        return SERVLET_COMMAND_NORMAL() + "part=mobile.SetData&cmd=setEventGoodWill&nextPage=setEventGoodWill";
    }

    // 상품 가격 조회
    public static String GET_API_PRODUCT_PRICE_URL() {
        return SERVLET_COMMAND_NORMAL() + PART_SMART_PHOTO_LITE + "&cmd=getProjPrice&nextPage=getProjPrice";
    }

    // FIXME 장바구니, 주문배송 미리보기 지원 시, v3로...
    public static String CART_URL() {
        return "/mw/v3/cart/index.jsp"; // "index_v3.jsp";
    }

    public static String ORDER_URL() {
        return "/mw/v3/history";// "/mw/history/index_v3.jsp";
    }

    public static String BENEFIT_URL() {
        return "/mw/v3/mysnaps/benefits.jsp?";
    }

    //일기 업로드 시퀀스 요청
    public static String GET_API_DIARY_SEQUENCE() {
        return SERVLET_COMMAND_NORMAL() + PART_DIARY_INTERFACE + "&cmd=getDiaryNo";
    }

    public static String GET_API_DIARY_CHANGE_MISSION_STATE() {
        return SERVLET_COMMAND_NORMAL() + PART_DIARY_INTERFACE + "&cmd=missionControl";
    }

    public static String GET_API_DIARY_READ_MISSION_STATE() {
        return SERVLET_COMMAND_NORMAL() + PART_DIARY_INTERFACE + "&cmd=getMissionInfo";
    }

    public static String GET_API_DIARY_LIST() {
        return SERVLET_COMMAND_NORMAL() + PART_DIARY_INTERFACE + "&cmd=getDiaryList";
    }

    public static String GET_API_DIARY_DELETE() {
        return SERVLET_COMMAND_NORMAL() + PART_DIARY_INTERFACE + "&cmd=deleteDiaryInfo";
    }

    public static String GET_API_DIARY_READ_USER_PROFILE_THUMBNAIL() {
        return SERVLET_COMMAND_NORMAL() + PART_DIARY_INTERFACE + "&cmd=getUserDetail";
    }

    public static String GET_API_DIARY_CHECK_MISSION_VALID() {
        return SERVLET_COMMAND_NORMAL() + PART_DIARY_INTERFACE + "&cmd=saveFirstWriteDate";
    }

    public static String GET_API_DIARY_COUNT_SAME_DATE() {
        return SERVLET_COMMAND_NORMAL() + PART_DIARY_INTERFACE + "&cmd=getPreDiaryForDayCnt";
    }

    public static String DIARY_HELP_PAGE_URL() {
        return "snapsapp://openAppPopup?openUrl=/mw/v3/store/information/info_diary_list.jsp?sclsscode=001001018000";
    }

    //saveErr Log
    public static String SAVELOG_URL() {
        return DOMAIN() + "/servlet/Command.do?part=mobile.SetData&cmd=setExceptionLog&nextPage=setExceptionLog";
    }

    public static String GET_NOTICE_INFO_URL() {
        return DOMAIN() + "/servlet/Command.do?part=mobile.GetData&cmd=getNoticeInfo&nextPage=getNoticeInfo";
    }

    //native ui list
    public static String GET_API_PRODUCT_LIST() {
        return NATIVE_UI_INTERFACE_PATH() + NATIVE_UI_INTERFACE_LIST_PART;
    }

    //native ui size
    public static String GET_API_PRODUCT_SUB_LIST() {
        return NATIVE_UI_INTERFACE_PATH() + NATIVE_UI_INTERFACE_SUB_LIST_PART;
    }

    //native ui detail
    public static String GET_API_PRODUCT_DETAIL() {
        return NATIVE_UI_INTERFACE_PATH() + NATIVE_UI_INTERFACE_DETAIL_PART;
    }

    public static String POST_API_SMART_SNAPS_GET_RECOMMEND_TEMPALTE_URL() {
        return SERVLET_COMMAND_NORMAL() + PART_SAMRT_SNAPS_LAYOUT_RECOMMEND_V2_INTERFACE + "&cmd=getLayoutRecommend";
    }

    public static String GET_API_SMART_SNAPS_GET_RECOMMEND_COVER_URL() {
        return SERVLET_COMMAND_NORMAL() + PART_SAMRT_SNAPS_LAYOUT_RECOMMEND_V2_INTERFACE + "&cmd=getCoverRecommend";
    }

    public static String GET_API_SMART_SNAPS_LAYOUT_LIST_URL() {
        return SERVLET_COMMAND_NORMAL() + PART_SAMRT_SNAPS_LAYOUT_RECOMMEND_INTERFACE + "&cmd=getLayoutList";
    }

    public static String GET_API_SMART_SNAPS_BG_RES_LIST_URL() {
        return SERVLET_COMMAND_NORMAL() + PART_SAMRT_SNAPS_LAYOUT_RECOMMEND_INTERFACE + "&cmd=getBackgroundList";
    }

    public static String GET_API_SMART_SNAPS_LAYOUT_XML_URL() {
        return SERVLET_COMMAND_NORMAL() + PART_SAMRT_SNAPS_LAYOUT_RECOMMEND_INTERFACE + "&cmd=getLayoutXML";
    }

    public static String GET_API_RECOMMEND_PHOTOBOOK_GET_STORY_IMAGE_LIST_URL() {
        return SERVLET_COMMAND_NORMAL() + PART_RECOMMEND_PHOTOBOOK_INTERFACE + "&cmd=getStoryImageList";
    }

    public static String GET_API_RECOMMEND_PHOTOBOOK_GET_RECOMMEND_PRODUCT_URL() {
        return SERVLET_COMMAND_NORMAL() + PART_RECOMMEND_PHOTOBOOK_INTERFACE + "&cmd=getRecommendProduct";
    }

    public static String GET_API_RECOMMEND_PHOTOBOOK_GET_FACE_DETECTION_URL() {
        return SERVLET_COMMAND_NORMAL() + PART_RECOMMEND_PHOTOBOOK_INTERFACE + "&cmd=getFaceDetection";
    }

}