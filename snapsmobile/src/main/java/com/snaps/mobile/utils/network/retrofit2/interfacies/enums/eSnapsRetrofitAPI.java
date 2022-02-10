package com.snaps.mobile.utils.network.retrofit2.interfacies.enums;

import com.snaps.mobile.utils.network.retrofit2.genetator.googlephoto.GooglePhotoNetworkHandlerGetAlbum;
import com.snaps.mobile.utils.network.retrofit2.genetator.googlephoto.GooglePhotoNetworkHandlerGetMediaList;
import com.snaps.mobile.utils.network.retrofit2.genetator.googlephoto.GooglePhotoNetworkHandlerGetPhotoList;

public enum eSnapsRetrofitAPI {

    //-------------------------   구글포토   ----------------------------------
    GET_GOOGLE_PHOTO_ALBUM(GooglePhotoNetworkHandlerGetAlbum.class),
    GET_GOOGLE_PHOTO_LIST(GooglePhotoNetworkHandlerGetPhotoList.class),
    GET_GOOGLE_MEDIA_LIST(GooglePhotoNetworkHandlerGetMediaList.class);

    //-------------------------   공통   ----------------------------------

//    /**
//     * full url request:
//     *  request: FULL_URL
//     *  response: SnapsNetworkAPIResponseString
//     */
//    GET_DYNAMIC_URL_RETURN_STRING(SnapsNetworkGenerateGetDynamicUrlReturnString.class),
//
//
//
//
//
//    //------------------------- 회원 정보 ----------------------------------
//
//    /**
//     *  로그인:
//     *  request: id, password,
//     *  response: SnapsNetworkAPIResponseExecuteLogin
//     */
//    LOGIN(SnapsNetworkGenerateExecuteLogin.class),
//
//    /**
//     *  로그아웃:
//     *  request: none,
//     *  response: SnapsNetworkAPIBaseResponse
//     */
//    LOGOUT(SnapsNetworkGenerateExecuteLogOut.class),
//
//    /**
//     * 회원가입:
//     *  request: id, pw, userName
//     *  response: SnapsNetworkAPIResponsePostUser
//     */
//    JOIN(SnapsNetworkGenerateJoin.class),
//
//    /**
//     * 회원가입 아이디 중복 체크:
//     *  request: SnapsNetworkAPIBodyExistence
//     *  response: SnapsNetworkAPIResponsePostUser
//     */
//    CHECK_DUPLICATE_ID_ON_JOIN_(SnapsNetworkGenerateCheckExistenceIdOnJoin.class),
//
//    /**
//     * 회원정보:
//     *  request: none
//     *  response: SnapsNetworkAPIBaseResponse
//     */
//    GET_USER_INFO(SnapsNetworkGenerateUserInfo.class),
//
//    /**
//     * 회원인증(문자전송):
//     *  request: (HashMap) cellPhoneNumber-value
//     *  response: SnapsNetworkAPIBaseResponse
//     */
//    CERTIFICATE_USER_SEND_MMS(SnapsNetworkGenerateCertificateUserSendMMS.class),
//
//    /**
//     * 회원인증(문자검증):
//     *  request: authKey, (HashMap) cellPhoneNumber-value
//     *  response: SnapsNetworkAPIBaseResponse
//     */
//    CERTIFICATE_USER_CONFIRM_MMS(SnapsNetworkGenerateCertificateUserConfirmMMS.class),
//
//    /**
//     * 회원탈퇴:
//     *  request: none
//     *  response: SnapsNetworkAPIBaseResponse
//     */
//    WITHDRAW(SnapsNetworkGenerateWithdraw.class),
//
//
//
//
//
//
//
//    //-------------------------  편집기  ----------------------------------
//
//    /**
//     * 프로젝트 코드 발급:
//     *  request: none
//     *  response: SnapsNetworkAPIResponsePostProject
//     */
//    GET_PROJECT_CODE(SnapsNetworkGeneratePostProject.class),
//
//    /**
//     * 상품 가격 정보 조회:
//     *  request: templateCode, backType, paperCode, frameType
//     *  response: SnapsNetworkAPIResponseGetProductPrice
//     */
//    GET_PRODUCT_PRICE(SnapsNetworkGenerateGetProductPrice.class),
//
//    /**
//     * 사진 인화 가격 조회:
//     *  request: none
//     *  response: SnapsNetworkAPIResponseGetProductPrintPrice
//     */
//    GET_PHOTO_PRINT_PRICE(SnapsNetworkGenerateGetProductPrintPrice.class),
//
//    /**
//     * 포토카드, 스퀘어프린트 등 디자인 가져오기:
//     *  request: templateCode, pageType, designerId, side, sort
//     *  response: SnapsNetworkAPIResponseGetProductDesign
//     */
//    GET_PAGE_DESIGN(SnapsNetworkGenerateGetProductDesign.class),
//
//    /**
//     * 템플릿 조회:
//     *  request: backgroundCode, templateId
//     *  response: ??//SnapsNetworkAPIResponseGetTemplateXml
//     */
//    GET_TEMPLATE_XML(SnapsNetworkGenerateGetTemplateXml.class),
//
//    /**
//     * 상품 및 템플릿 정보 조회:
//     *  request: templateCode, glossyType, paperCode
//     *  response: SnapsNetworkAPIResponseGetProductTemplate
//     */
//    GET_PRODUCT_TEMPLATE_INFO(SnapsNetworkGenerateGetProductTemplate.class),
//
//    /**
//     * save xml 조회:
//     *  request: none
//     *  response: String
//     */
//    GET_SAVE_XML(SnapsNetworkGenerateGetProjectXml.class),
//
//    /**
//     * 원본 사진 업로드:
//     *  request: SnapsNetworkAPIBodyPostOrgFile
//     *  response: SnapsNetworkAPIResponsePostProjectFile
//     */
//    UPLOAD_ORG_FILE(SnapsNetworkGeneratePostProjectOrgFile.class),
//
//    /**
//     * 썸네일 사진 업로드:
//     *  request: SnapsNetworkAPIBodyPostThumbnailFile
//     *  response: SnapsNetworkAPIResponsePostProjectFile
//     */
//    UPLOAD_THUMB_FILE(SnapsNetworkGeneratePostProjectThumbFile.class),
//
//    /**
//     * SNS 원본 업로드:
//     *  request: SnapsNetworkAPIBodyPostSNSOrgFile
//     *  response: SnapsNetworkAPIResponsePostProjectFile
//     */
//    UPLOAD_SNS_ORG_FILE(SnapsNetworkGeneratePostProjectSNSOrgFile.class),
//
//    /**
//     * SNS 썸네일 업로드 :
//     *  request: SnapsNetworkAPIBodyPostSNSThumbnailFile
//     *  response: SnapsNetworkAPIResponsePostProjectFile
//     */
//    UPLOAD_SNS_THUMB_FILE(SnapsNetworkGeneratePostProjectSNSThumbFile.class),
//
//    /**
//     * 휴일 정보 가져오기:
//     *  request: none
//     *  response: SnapsNetworkAPIResponseGetHolidayList
//     */
//    GET_HOLY_DAY_LIST(SnapsNetworkGenerateGetHolidayList.class),
//
//    /**
//     * 인공지능 포토북 레이아웃 추천:
//     *  request: SnapsNetworkAPIBodyPostLayout
//     *  response: String
//     */
//    GET_RECOMMEND_BOOK_XML(SnapsNetworkGeneratePostLayout.class),
//
//    /**
//     * 인공지능 포토북 커버 조회:
//     *  request: projectCode, templateCode
//     *  response: String
//     */
//    GET_RECOMMEND_BOOK_COVER(SnapsNetworkGenerateGetCover.class),
//
//    /**
//     * 인공지능 포토북 BG  조회:
//     *  request: none
//     *  response: SnapsNetworkAPIResponseLayoutList
//     */
//    GET_RECOMMEND_BOOK_BG(SnapsNetworkGenerateGetBG.class),
//
//    /**
//     * 인공지능 포토북 레이아웃 리스트 조회:
//     *  request: eSnapsPageResourceRequestType
//     *  response:
//     */
//    GET_RECOMMEND_BOOK_LAYOUT_LIST(SnapsNetworkGenerateGetLayoutList.class),
//
//
//    /**
//     * 인공지능 포토북 페이지 디자인 템플릿 조회:
//     *  request: templateId
//     *  response:
//     */
//    GET_RECOMMEND_BOOK_PAGE_TEMPLATE(SnapsNetworkGenerateGetPageTemplate.class),
//
//    /**
//     * 인공지능 포토북 템플릿 조회:
//     *  request: ,
//     *  response:
//     */
//    GET_RECOMMEND_BOOK_TEMPLATE_XML(SnapsNetworkGenerateGetTemplateXml.class),
//
//    /**
//     * 장바구니 저장:
//     *  request: SnapsNetworkAPIBodyPostProjectSave
//     *  response: SnapsNetworkAPIResponsePostProject
//     */
//    UPLOAD_PROJECT(SnapsNetworkGeneratePostProjectAlbum.class),
//
//    /**
//     * 포토북 커버 XML 조회:
//     *  request: productCode, paperCode, glossyType, backgroundCode
//     *  response: SnapsNetworkAPIResponseGetTemplateXml
//     */
////    GET_COVER_TEMPLATE_XML(SnapsNetworkGenerateGetCoverTemplateXml.class),
//
//    /**
//     * 사진 인화 업로드:
//     *  request: SnapsNetworkAPIBodyPostProjectPrint
//     *  response: SnapsNetworkAPIResponsePostProject
//     */
//    UPLOAD_PHOTO_PRINT_PROJECT(SnapsNetworkGeneratePostProjectPrint.class),
//
//
//
//
//
//
//    //------------------------- 상품 상세 (너무 많아지면 분리하자.) ----------------------------------
//
//    /**
//     * 카테고리별 디자인 리스트(심플포토북 전용)
//     */
//    PRODUCT_SORT_TYPE(null), //FIXME...아직 작업 전...
//
//
//
//
//    //--------------------------- 기타 ------------------------------------
//
//    /**
//     * 앱 구동시 필수 호출 인터페이스
//     */
//    HELLO(SnapsNetworkGenerateGetHello.class),
//
//    REGISTER_DEVICE(SnapsNetworkGeneratePostDevice.class),
//
//    API_TEST(SnapsNetworkAPITest.class);

    private Class<?> generateClass;

    eSnapsRetrofitAPI(Class<?> cl) {
        this.generateClass = cl;
    }

    public Class<?> getGenerateClass() {
        return generateClass;
    }

    /*
    TEST_GET,
    TEST_POST,
    TEST_POST_MULTIPART,
    TEST_PHOTO_BOOK_TEMPLATE,
    */
}
