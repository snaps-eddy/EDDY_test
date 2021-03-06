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

    // Command ??????.
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
     * ????????? ???????????? url
     */
    public static String AGREEMENT_URL_MOBILE() {
        return WEB_DOMAIN() + "/mw/v3/rules/service.jsp?lang=";
    }

    /**
     * ????????? ???????????????????????? url
     */
    public static String PRIVACY_URL_MOBILE() {
        return WEB_DOMAIN() + "/mw/v3/rules/privacy.jsp?lang=";
    }

    /**
     * ???????????? url
     */
    public static String NOTICE_URL() {
        return WEB_DOMAIN() + "/mw/v3/site/snaps/notice/index.jsp";
    }

    /**
     * ???????????? url
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
     * [Mobile ???????????????] ??? ??????, ??????????????? ??????, ???????????? ????????? ?????????
     */
    public static String GET_UPDATEINFO_MOBILE() {
        return DOMAIN() + "/servlet/Command.do?part=mobile.SmartPhotoforSDK&cmd=getJosnUpdateInfo&nextPage=getJosnUpdateInfo&prmChnlCode=%s&prmTest="; // &prmTest=N"; // renewal
    }

    /**
     * ????????? ????????? ????????? ??????
     */
    public static String GET_API_THEMEBOOK_ADD_PAGE() {
        return SERVLET_COMMAND_NORMAL() + PART_SMART_PHOTO_LITE + "&cmd=getMultiFormList&prmPageType=page" + "&prmChnlCode=" + Config.getCHANNEL_CODE();
    }

    /**
     * ????????? ?????? ????????? ??????
     */
    public static String GET_API_THEMEBOOK_COVER() {
        return SERVLET_COMMAND_NORMAL() + PART_SMART_PHOTO_LITE + "&cmd=getMultiFormList&prmPageType=cover" + "&prmChnlCode=" + Config.getCHANNEL_CODE();
    }

    /**
     * ????????? categoty
     */
    public static String GET_API_THEMEBOOK_CATEGORY() {
        return SERVLET_COMMAND_NORMAL() + PART_MOBILE_GET + "&cmd=ED_ST&nextPage=ED_ST&F_SID=ED_ST&F_CHNL_CODE=KOR0005&F_MODE=category&F_CLSS_CODE=039027";
    }

    /**
     * ????????? contents
     */
    public static String GET_API_THEMEBOOK_CONTENTS() {
        return SERVLET_COMMAND_NORMAL() + PART_MOBILE_GET + "&cmd=ED_ST&nextPage=ED_ST&F_SID=ED_ST&F_CHNL_CODE=KOR0005&f_clss_code=039027";
    }

    /**
     * ?????? ????????? ????????? ??????
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
     * ?????? ???????????? ??????
     */
    public static String GET_API_PROJECT_CODE() {
        return SERVLET_COMMAND_NORMAL() + PART_SMART_PHOTO_LITE + "&cmd=getProjCode&nextPage=getProjcode&prmChnlCode=" + Config.getCHANNEL_CODE();
    }

    /**
     * ?????? ?????? ??????..
     */
    public static String GET_API_VERIFY_PROJECT_CODE() {
        return SERVLET_COMMAND_NORMAL() + PART_SMART_PHOTO_CS + "&cmd=getIsAfterOrderEdit";
    }

    /**
     * ????????? ?????????.
     */
    public static String GET_API_TEMPLATE() {
        return SERVLET_COMMAND_NORMAL() + PART_SMART_PHOTO_LITE + "&cmd=getTmplScriptRsrcMulti&nextPage=getTmplScriptRsrcMulti&prmChnlCode=" + Config.getCHANNEL_CODE()
                + "&prmdsplcode=&categorycode=";
    }

    /**
     * ?????? ????????? ?????????. ?????? ???[?????????-A5] / TBA5MT_1u / 045021001065 ?????? ???[?????????-A6] / TBA6MT_1u / 045021001066 ?????? ???-???????????? / FTBA6MT_1u / 045021001067
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
     * ??????, ???????????? ?????????
     */
    // TODO : ????????? ????????? test ??????.
    public static String GET_API_MOBILE_TMPLRSRC_LIST() {
        return SERVLET_COMMAND_NORMAL() + PART_SMART_PHOTO_LITE + "&cmd=getMobileTmplRsrcList&nextPage=getMobileTmplRsrcList&prmChnlCode="
                + Config.getCHANNEL_CODE();
    }

    /**
     * ???????????? ??????
     */
    public static String POST_API_INSERT_ERRORLOG() {
        return SERVLET_COMMAND_NORMAL() + PART_MOBILE_SET + "&cmd=insertErrLog&nextPage=insertErrLog&F_CHNL_CODE=" + Config.getCHANNEL_CODE();
    }

    /**
     * ????????? ??????
     */
    public static String POST_API_EVENT_APPLY() {
        return SERVLET_COMMAND_NORMAL() + PART_MOBILE_SET + "&cmd=EV_APPLY&nextPage=EV_APPLY&F_SID=EV_APPLY&F_CHNL_CODE=" + Config.getCHANNEL_CODE();
    }

    /**
     * ????????? ????????????
     */
    public static String POST_API_EVENT_GIFT() {
        return (Config.isRealServer() ? SERVLET_COMMAND_SECURE() : SERVLET_COMMAND_NORMAL()) + PART_MOBILE_SET + "&cmd=EV_GIFT&nextPage=EV_GIFT&F_SID=EV_GIFT&F_CHNL_CODE="
                + Config.getCHANNEL_CODE();
    }

    /**
     * ????????? ?????? ??? ????????????
     */
    public static String POST_API_PUSH_INFO() {
        return SERVLET_COMMAND_NORMAL() + PART_MOBILE_SET + "&cmd=PS_SEND&nextPage=PS_SEND&F_SID=PS_SEND&F_CHNL_CODE=" + Config.getCHANNEL_CODE();
    }

    // ---------------------------------------------------- ???????????? API
    // ----------------------------------------------------

    /**
     * snaps ????????????
     */
    public static String POST_API_SNAPS_JOIN() {
        return (Config.isRealServer() ? SERVLET_COMMAND_SECURE() : SERVLET_COMMAND_NORMAL())
                + PART_MOBILE_SET
                + "&cmd=MY_JOIN&nextPage=MY_JOIN&F_SID=MY_JOIN&F_CHNL_CODE="
                + Config.getCHANNEL_CODE();
    }

    public static String getSnapsLoginPostApi() { // POST_API_SNAPS_LOGIN?????? KOR0030??? ???????????? ???????????? ????????? ??????
        StringBuilder sb = new StringBuilder();
        sb.append(Config.isRealServer() ? SERVLET_COMMAND_SECURE() : SERVLET_COMMAND_NORMAL());
        sb.append(PART_MOBILE_GET).append("&cmd=MY_LOGIN&nextPage=MY_LOGIN&F_SID=MY_LOGIN&F_CHNL_CODE=");
        sb.append(Config.getCHANNEL_CODE());
        return sb.toString();
    }

    /**
     * snaps ????????????
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
     * snaps ???????????? ?????????
     *
     * @params F_USER_ID ????????????
     * @params F_TMP_PASS ????????????
     * @params F_NEW_PASS ???????????????
     */
    public static String POST_API_SNAPS_PWDRESET() {
        return (Config.isRealServer() ? SERVLET_COMMAND_SECURE() : SERVLET_COMMAND_NORMAL())
                + PART_MOBILE_SET
                + "&cmd=MY_PASS&nextPage=MY_PASS&F_SID=MY_PASS&F_CHNL_CODE="
                + Config.getCHANNEL_CODE();
    }

    /**
     * snaps ??????(????????????)
     *
     * @params F_MODE (validate(????????????), proc(??????))
     * @params f_user_id
     * @params f_user_pwd (??????????????? ?????? ??????)
     */
    public static String POST_API_SNAPS_RETIRE() {
        return (Config.isRealServer() ? SERVLET_COMMAND_SECURE() : SERVLET_COMMAND_NORMAL())
                + PART_MOBILE_SET
                + "&cmd=MY_LEAVE&nextPage=MY_LEAVE&F_SID=MY_LEAVE&F_CHNL_CODE="
                + Config.getCHANNEL_CODE();
    }

    // ---------------------------------------------------- ???????????? API
    // ----------------------------------------------------

    /**
     * snaps ????????? ?????? ?????????.
     *
     * @params F_CATE_CODE
     */
    public static String GET_API_IMAGE_LIST_STICKER() {
        return SERVLET_COMMAND_NORMAL() + PART_MOBILE_GET + "&cmd=ED_ST&nextPage=ED_ST&F_SID=ED_ST&F_CHNL_CODE=" + Config.getCHANNEL_CODE();
    }

    /**
     * snaps ????????? ?????? ?????????.
     */
    public static String GET_API_IMAGE_ALBUMLIST_STICKER() {
        return SERVLET_COMMAND_NORMAL() + PART_MOBILE_GET + "&cmd=ED_ST&nextPage=ED_ST&F_SID=ED_ST&F_MODE=category&F_CHNL_CODE=" + Config.getCHANNEL_CODE();
    }

    /**
     *
     * ?????? ????????? ?????? API
     */

    /**
     * ????????? ?????????
     *
     * @params F_USER_ID ?????? ?????????.
     * @params f_hppn_type ????????????
     */
    public static String GET_API_MY_PROJECT_LIST() {
        return SERVLET_COMMAND_NORMAL() + PART_MOBILE_GET + "&cmd=MY_PRJ&nextPage=MY_PRJ&F_SID=MY_PRJ&F_CHNL_CODE=" + Config.getCHANNEL_CODE();
    }

    /**
     * ?????? ????????????
     *
     * @params F_PROJ_CODE ?????? ??????
     */
    public static String GET_API_MY_PROJECT_DETAIL() {
        return SERVLET_COMMAND_NORMAL() + PART_MOBILE_GET + "&cmd=MY_DTL&nextPage=MY_DTL&F_SID=MY_DTL&F_CHNL_CODE=" + Config.getCHANNEL_CODE();
    }

    /**
     * ????????? ???????????? ?????? , ????????? ??????????????? ??????.
     *
     * @return RETURN_MSG ??????????????? ( ???????????? / ??????????????? )
     * @params F_USER_ID ?????? ID
     * @params F_FLAG ?????? / ?????? ( del / save )
     * @params F_PROJ_CODE ???????????? ??????.
     * @params F_BAG_ STAT ?????????
     */
    public static String POST_API_MY_PROJECT_OPTION_FLAG() {
        return SERVLET_COMMAND_NORMAL() + PART_MOBILE_SET + "&cmd=MY_PRJ&nextPage=MY_PRJ&F_SID=MY_PRJ" + "&F_CHNL_CODE=" + Config.getCHANNEL_CODE();
    }

    /**
     * ??????????????? ?????? ??????????????? ??????
     *
     * @params F_USER_ID ?????? ID
     * @params F_PROJ_CODE ???????????? ??????.
     */
    public static String POST_API_MY_PROJECT_CART_READD() {
        return SERVLET_COMMAND_NORMAL() + PART_MOBILE_SET + "&cmd=MY_REORDER&nextPage=MY_REORDER&F_SID=MY_REORDER" + "&f_chnl_code="
                + Config.getCHANNEL_CODE();
    }

    /**
     * ???????????? ?????????
     *
     * @params F_PAGE_NO ???????????????
     */
    public static String GET_API_NOTICE() {
        return SERVLET_COMMAND_NORMAL() + PART_MOBILE_GET + "&cmd=MY_PBL&nextPage=MY_PBL&F_SID=MY_PBL&F_CHNL_CODE=" + Config.getCHANNEL_CODE();
    }

    /**
     * ???????????? ??????
     *
     * @params F_USER_ID ??????ID
     * @params F_CONTENTS ?????? ??????
     */
    public static String POST_API_QUESTION_COMMNET() {
        return SERVLET_COMMAND_NORMAL() + PART_MOBILE_SET + "&cmd=MY_C3&nextPage=MY_C3&F_SID=MY_C3&F_CHNL_CODE=" + Config.getCHANNEL_CODE();
    }

    /**
     * ???????????? ?????????
     *
     * @params F_USER_ID ???????????????
     * @params F_PAGE_NO ????????? ??????.
     */
    public static String GET_API_QUESTION_LIST() {
        return SERVLET_COMMAND_NORMAL() + PART_MOBILE_GET + "&cmd=MY_C1&nextPage=MY_C1&F_SID=MY_C1&F_CHNL_CODE=" + Config.getCHANNEL_CODE();
    }

    /**
     * ?????? ??????
     */
    public static String SET_COUPON() {
        return SERVLET_COMMAND_NORMAL() + PART_MOBILE_SET + "&cmd=MY_CPN&nextPage=MY_CPN&F_SID=MY_CPN&F_CHNL_CODE=" + Config.getCHANNEL_CODE();
    }

    /**
     * ????????? ?????? ?????? ???????????? ?????? ?????? ???
     */
    public static String COUPON_KIND_INFO() {
        return SERVLET_COMMAND_NORMAL() + PART_MOBILE_GET + "&cmd=MY_CPN&nextPage=MY_CPN&f_mode=clss&F_CHNL_CODE=" + Config.getCHANNEL_CODE();
    }

    // ?????? ?????????...
    // http://117.52.102.177/servlet/Command.do?part=mobile.SetData&cmd=MY_CPN&nextPage=MY_CPN&F_SID=MY_CPN&F_CHNL_CODE=KOR0031&F_USER_ID=308559&F_PROJ_CODE=193001&f_mode=delete
    public static String COUPON_INFO_RESET() {
        return SERVLET_COMMAND_NORMAL() + PART_MOBILE_SET + "&cmd=MY_CPN&nextPage=MY_CPN&F_SID=MY_CPN&f_mode=delete&F_CHNL_CODE=" + Config.getCHANNEL_CODE();
    }

    // ---------------------------------------------------- ???????????? API
    // ----------------------------------------------------

    /**
     * ???????????? ?????????
     *
     * @params F_USER_ID ???????????????
     * @params F_PAGE_NO ????????? ??????
     */
    public static String GET_API_ORDER_LIST() {
        return SERVLET_COMMAND_NORMAL() + PART_MOBILE_GET + "&cmd=MY_DVY&nextPage=MY_DVY&F_SID=MY_DVY&F_CHNL_CODE=" + Config.getCHANNEL_CODE();
    }

    /**
     * ???????????? ????????????
     *
     * @params F_ORDER_CODE ????????????
     * @params F_USER_ID ???????????????
     */
    public static String GET_API_ORDER_DETAIL() {
        return SERVLET_COMMAND_NORMAL() + PART_MOBILE_GET + "&cmd=MY_ODL&nextPage=MY_ODL&F_SID=MY_ODL&F_CHNL_CODE=" + Config.getCHANNEL_CODE();
    }

    /**
     * ????????? ????????????
     *
     * @params F_USER_ID ???????????????
     * @params F_PROJ_CODES ??????????????????,??????
     */
    public static String POST_API_KAKAO_BILL_REQ() {
        return SERVLET_COMMAND_SECURE() + PART_MOBILE_SET + "&cmd=MY_STTL&nextPage=MY_STTL&F_SID=MY_STTL&F_CHNL_CODE=" + Config.getCHANNEL_CODE();
    }

    /**
     * ????????? ???????????? ????????? ???????????? ?????? ????????????
     *
     * @params F_USER_ID ???????????????
     * @params F_PROJ_CODE ??????????????????,??????
     * @params F_ORDER_CODE ????????????
     */
    public static String POST_API_SNAPS_BILL_REQ() {
        return SERVLET_COMMAND_SECURE() + PART_MOBILE_SET + "&cmd=MY_ORD&nextPage=MY_ORD&F_SID=MY_ORD&F_CHNL_CODE=" + Config.getCHANNEL_CODE();
    }

    /**
     * ?????????,???????????? ??????
     *
     * @params F_ORDER_CODE ????????????,...........
     */
    public static String POST_API_ORDER_ADDR_SAVE() {
        return SERVLET_COMMAND_SECURE() + PART_MOBILE_SET + "&cmd=MY_ADR&nextPage=MY_ADR&F_SID=MY_ADR&F_CHNL_CODE=" + Config.getCHANNEL_CODE();
    }

    /**
     * ????????? ???????????? ??????
     *
     * @params checkout_ticket ???????????????
     * @params pay_id ??????id
     * @params checkout_hash hash
     */
    public static String POST_API_KAKAO_BILL_CONFIRM() {
        return SERVLET_COMMAND_SECURE() + PART_MOBILE_SET + "&cmd=MY_CONFIRM&nextPage=MY_CONFIRM&F_SID=MY_CONFIRM&F_CHNL_CODE=" + Config.getCHANNEL_CODE();
    }

    /**
     * ?????? ??????
     *
     * @params F_ORDER_CODE ????????????
     */
    public static String POST_API_ORDER_CANCEL() {
        return SERVLET_COMMAND_NORMAL() + PART_MOBILE_SET + "&cmd=MY_CANCEL&nextPage=MY_CANCEL&F_SID=MY_CANCEL&F_CHNL_CODE=" + Config.getCHANNEL_CODE();
    }

    // ---------------------------------------------------- ???????????? API
    // ----------------------------------------------------

    /**
     * ????????????, ???????????? ?????? ??????
     */
    public static String POST_API_BADGE_COUNT() {
        return SERVLET_COMMAND_NORMAL() + PART_MOBILE_GET + "&cmd=MY_BADGE&nextPage=MY_BADGE&F_SID=MY_BADGE&F_CHNL_CODE=" + Config.getCHANNEL_CODE();
    }

    /**
     * ??? ????????????
     */
    public static String GET_API_HOME_PRICE() {
        return SERVLET_COMMAND_NORMAL() + PART_MOBILE_GET + "&cmd=HM_INFO&nextPage=HM_INFO&F_SID=HM_INFO&F_CHNL_CODE=" + Config.getCHANNEL_CODE();
    }
    // --- ???????????? ?????? ?????? ---

    /**
     * ???????????? ??????
     *
     * @params F_dong ?????????(?????????)
     */
    public static String GET_API_MY_ZIP() {
        return SERVLET_COMMAND_NORMAL() + PART_MOBILE_GET + "&cmd=MY_ZIP&nextPage=MY_ZIP&F_SID=MY_ZIP&F_CHNL_CODE=" + Config.getCHANNEL_CODE();
    }

    ;

    /**
     * ??????????????? ?????????
     *
     * @params F_USER_ID ???????????????
     */
    public static String GET_API_MY_DLEIVERY_ORDER() {
        return SERVLET_COMMAND_NORMAL() + PART_MOBILE_GET + "&cmd=MY_HIS&nextPage=MY_HIS&F_SID=MY_HIS&F_CHNL_CODE=" + Config.getCHANNEL_CODE();
    }

    ;

    /**
     * ????????? ???????????? ??????
     *
     * @params f_device_no ????????????
     * @params f_other_id ???UUID
     * @params f_user_no ???????????????NO
     * @params f_uuser_id ???????????????ID
     * @params f_user_name ?????????
     * @params f_os_type OS Type
     * @params f_os_ver Os ??????
     * @params f_app_ver ?????????
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

    // ------------------------------------------------------------ ????????? ????????? ?????????
    // ???????????? ?????? api
    // ------------------------------------------------------------//

    // save.xml receive ?????? ??????????????? ??????..
    public static String GET_API_SAVE_XML() {
        return SERVLET_COMMAND_NORMAL() + PART_SMART_PHOTO_LITE + "&cmd=getXmlPathNew2" + "&prmChnlCode=" + Config.getCHANNEL_CODE();
    }

    public static String GET_API_PHOTO_PRINT() {
        return SERVLET_COMMAND_NORMAL() + PART_MOBILE_GET + "&cmd=PD_PRICE&nextPage=PD_PRICE" + "&F_CHNL_CODE=" + Config.getCHANNEL_CODE();
    }

    // ???????????? ???????????? ?????? imgseq ?????? ?????? ??????...
    public static String GET_API_SNS_IMAGE_IMGSEQ() {
        return SERVLET_COMMAND_NORMAL() + PART_SMART_PHOTO_LITE + "&cmd=setOuterImgPathSave&nextPage=setOuterImgPathSave&prmOrgnCode=140003";
    }

    // ?????? ?????? ?????????
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

    // ???????????? ???????????? ????????? ??????
    // (???????????? : 315001 - ????????????, 315002 :????????????)
    public static String REG_KAKAO_INVITE_EVENT() {
        return SERVLET_COMMAND_NORMAL() + "part=mobile.SetData&cmd=setEventGoodWill&nextPage=setEventGoodWill";
    }

    // ?????? ?????? ??????
    public static String GET_API_PRODUCT_PRICE_URL() {
        return SERVLET_COMMAND_NORMAL() + PART_SMART_PHOTO_LITE + "&cmd=getProjPrice&nextPage=getProjPrice";
    }

    // FIXME ????????????, ???????????? ???????????? ?????? ???, v3???...
    public static String CART_URL() {
        return "/mw/v3/cart/index.jsp"; // "index_v3.jsp";
    }

    public static String ORDER_URL() {
        return "/mw/v3/history";// "/mw/history/index_v3.jsp";
    }

    public static String BENEFIT_URL() {
        return "/mw/v3/mysnaps/benefits.jsp?";
    }

    //?????? ????????? ????????? ??????
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