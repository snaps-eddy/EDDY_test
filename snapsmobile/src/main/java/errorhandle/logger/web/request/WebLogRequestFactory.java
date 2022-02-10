package errorhandle.logger.web.request;

import errorhandle.logger.web.WebLogConstants;
import errorhandle.logger.web.request.interfacies.WebLogRequestInfo;
import errorhandle.logger.web.request.uri.ani.WebLogURIForRecommendBook;

import static errorhandle.logger.web.WebLogConstants.eWebLogInterfaceType.RES;
import static errorhandle.logger.web.WebLogConstants.eWebLogPayloadType.AURA_XML;
import static errorhandle.logger.web.WebLogConstants.eWebLogPayloadType.BACKGROUND;
import static errorhandle.logger.web.WebLogConstants.eWebLogPayloadType.FILTER_CODE;
import static errorhandle.logger.web.WebLogConstants.eWebLogPayloadType.IMAGE_EXIF;
import static errorhandle.logger.web.WebLogConstants.eWebLogPayloadType.IMG_CNT;
import static errorhandle.logger.web.WebLogConstants.eWebLogPayloadType.IMG_PATH;
import static errorhandle.logger.web.WebLogConstants.eWebLogPayloadType.PAGE;
import static errorhandle.logger.web.WebLogConstants.eWebLogPayloadType.PHOTO_ALBUM;
import static errorhandle.logger.web.WebLogConstants.eWebLogPayloadType.PREV_PROJ_CNT;
import static errorhandle.logger.web.WebLogConstants.eWebLogPayloadType.PRODUCT_CLICK;
import static errorhandle.logger.web.WebLogConstants.eWebLogPayloadType.PROJ_CNT;
import static errorhandle.logger.web.WebLogConstants.eWebLogPayloadType.PROJ_CODE;
import static errorhandle.logger.web.WebLogConstants.eWebLogPayloadType.PROJ_CODE_LIST;
import static errorhandle.logger.web.WebLogConstants.eWebLogPayloadType.REQUEST_CONTENTS;
import static errorhandle.logger.web.WebLogConstants.eWebLogPayloadType.RESPONSE_CONTENTS;
import static errorhandle.logger.web.WebLogConstants.eWebLogPayloadType.TEMPLATE_CODE;
import static errorhandle.logger.web.WebLogConstants.eWebLogPayloadType.TITLE_TEXT;
import static errorhandle.logger.web.WebLogConstants.eWebLogPayloadType.WHERE;

public class WebLogRequestFactory {
    public static WebLogRequestInfo createURIWithLogName(WebLogConstants.eWebLogName logName) throws Exception {
        WebLogURIForRecommendBook webLogURIForRecommendBook = WebLogURIForRecommendBook.createURI(logName);

        switch (logName) {
            case home_event_annie_click:
                webLogURIForRecommendBook.addLogDescription("초기화면\t홈 메뉴\t인공지능 포토북 애니 클릭").forceSendLogMessage();
                break;
            case home_event_annie_clickDirect:
                webLogURIForRecommendBook.addLogDescription("초기화면\t인공지능 포토북 애니 이벤트 화면\t애니 바로가기 클릭");
                break;
            case photobook_annie_view_click:
                webLogURIForRecommendBook.addLogDescription("초기화면\t포토북 메뉴\t인공지능 포토북 애니 클릭");
                break;
            case photobook_annie_selectphoto_clickBack:
                webLogURIForRecommendBook.addLogDescription("홈\t사진 선택 화면\t뒤로가기 버튼 클릭");
                break;
            case photobook_annie_selectphotobackpopup_clickCancel:
                webLogURIForRecommendBook.addLogDescription("사진 선택 화면\t사진선택화면_뒤로가기 팝업\t취소 버튼 클릭");
                break;
            case photobook_annie_selectphotobackpopup_clickOk:
                webLogURIForRecommendBook.addLogDescription("사진 선택 화면\t사진선택화면_뒤로가기 팝업\t확인 버튼 클릭");
                break;
            case photobook_annie_selectphoto_clickTotal:
                webLogURIForRecommendBook.addLogDescription("홈\t사진 선택 화면\t선택 된 사진 전체보기 클릭");
                break;
            case photobook_annie_selectphoto_clickEnter:
                webLogURIForRecommendBook.addLogDescription("홈\t사진 선택 화면\t확인 버튼 클릭").addRequirePayloads(WHERE);
                break;
            case photobook_annie_selectphoto_clickRange:
                webLogURIForRecommendBook.addLogDescription("홈\t사진 선택 화면\t모든사진 클릭");
                break;
            case photobook_annie_make_clickCancel:
                webLogURIForRecommendBook.addLogDescription("사진 선택 화면\t포토북 구성중 화면\t취소하기 버튼 클릭").addRequirePayloads(PROJ_CODE);
                break;
            case photobook_annie_complete_processLayout_REQ:
                webLogURIForRecommendBook.addLogDescription("사진 선택 화면\t포토북 구성중 화면\t레이아웃 추천 분석(REQ)").addRequirePayloads(PROJ_CODE, REQUEST_CONTENTS);
                break;
            case photobook_annie_complete_processLayout_RES:
                webLogURIForRecommendBook.addLogDescription("사진 선택 화면\t포토북 구성중 화면\t레이아웃 추천 분석(RES)").addInterfaceType(RES).addRequirePayloads(PROJ_CODE, RESPONSE_CONTENTS);
                break;
            case photobook_annie_make_clickBack:
                webLogURIForRecommendBook.addLogDescription("사진 선택 화면\t포토북 구성중 화면\t뒤로가기 버튼 클릭").addRequirePayloads(PROJ_CODE);
                break;
            case photobook_annie_make_clickSound:
                webLogURIForRecommendBook.addLogDescription("사진 선택 화면\t포토북 구성중 화면\t사운드 버튼 클릭").addRequirePayloads(PROJ_CODE);
                break;
            case photobook_annie_complete_clickBack:
                webLogURIForRecommendBook.addLogDescription("포토북 구성중 화면\t포토북 구성 완성 화면\t뒤로가기 버튼 클릭").addRequirePayloads(PROJ_CODE);
                break;
            case photobook_annie_completeback_clickCancel:
                webLogURIForRecommendBook.addLogDescription("포토북 구성 완성 화면\t뒤로가기 진행 확인 팝업\t취소 버튼 클릭").addRequirePayloads(PROJ_CODE);
                break;
            case photobook_annie_completeback_clickConfirm:
                webLogURIForRecommendBook.addLogDescription("포토북 구성 완성 화면\t뒤로가기 진행 확인 팝업\t확인 버튼 클릭").addRequirePayloads(PROJ_CODE);
                break;
            case photobook_annie_complete_clickUp:
                webLogURIForRecommendBook.addLogDescription("포토북 구성중 화면\t포토북 구성 완성 화면\t맨위로 올리기 아이콘 클릭").addRequirePayloads(PROJ_CODE);
                break;
            case photobook_annie_complete_clickPreview:
                webLogURIForRecommendBook.addLogDescription("포토북 구성중 화면\t포토북 구성 완성 화면\t크게보기 클릭").addRequirePayloads(PROJ_CODE);
                break;
            case photobook_annie_complete_widthPreview:
                webLogURIForRecommendBook.addLogDescription("포토북 구성중 화면\t포토북 구성 완성 화면\t화면 눕혀서 크게보기 전환").addRequirePayloads(PROJ_CODE);
                break;
            case photobook_annie_preview_page:
                webLogURIForRecommendBook.addLogDescription("포토북 구성 완성 화면\t크게보기 화면\t크게보기 페이지 넘기기").addRequirePayloads(PROJ_CODE, PAGE);
                break;
            case photobook_annie_preview_clickBack:
                webLogURIForRecommendBook.addLogDescription("포토북 구성 완성 화면\t크게보기 화면\t뒤로가기 버튼 클릭").addRequirePayloads(PROJ_CODE);
                break;
            case photobook_annie_preview_clickPage:
                webLogURIForRecommendBook.addLogDescription("포토북 구성 완성 화면\t크게보기 화면\t페이지 이미지 클릭").addRequirePayloads(PROJ_CODE);
                break;
            case photobook_annie_preview_clickMovecart:
                webLogURIForRecommendBook.addLogDescription("포토북 구성 완성 화면\t크게보기 화면\t장바구니 버튼 클릭").addRequirePayloads(PROJ_CODE);
                break;
            case photobook_annie_preview_clickCancel:
                webLogURIForRecommendBook.addLogDescription("포토북 구성 완성 화면\t장바구니 저장 진행 확인 팝업\t취소 버튼 클릭").addRequirePayloads(PROJ_CODE);
                break;
            case photobook_annie_preview_clickConfirm:
                webLogURIForRecommendBook.addLogDescription("포토북 구성 완성 화면\t장바구니 저장 진행 확인 팝업\t저장 버튼 클릭").addRequirePayloads(PROJ_CODE, AURA_XML);
                break;
            case photobook_annie_preview_clickContinue:
                webLogURIForRecommendBook.addLogDescription("포토북 구성 완성 화면\t장바구니 저장 완료 알림 팝업\t계속편집하기 버튼 클릭").addRequirePayloads(PROJ_CODE);
                break;
            case photobook_annie_complete_clickCoverimg:
                webLogURIForRecommendBook.addLogDescription("포토북 구성중 화면\t포토북 구성 완성 화면\t커버 이미지 클릭").addRequirePayloads(PROJ_CODE);
                break;
            case photobook_annie_complete_clickCover:
                webLogURIForRecommendBook.addLogDescription("포토북 구성중 화면\t포토북 구성 완성 화면\t'커버 편집하기' 버튼 클릭").addRequirePayloads(PROJ_CODE);
                break;
            case photobook_annie_editdetailcover_clickBack:
                webLogURIForRecommendBook.addLogDescription("포토북 구성 완성 화면, 크게보기 화면\t상세편집 화면[커버]\t뒤로가기 버튼 클릭").addRequirePayloads(PROJ_CODE, PAGE);
                break;
            case photobook_annie_editdetailcoverback_clickCancel:
                webLogURIForRecommendBook.addLogDescription("상세편집 화면[커버]\t뒤로가기 진행 확인 팝업\t취소 버튼 클릭").addRequirePayloads(PROJ_CODE, PAGE);
                break;
            case photobook_annie_editdetailcoverback_clickConfirm:
                webLogURIForRecommendBook.addLogDescription("상세편집 화면[커버]\t뒤로가기 진행 확인 팝업\t확인 버튼 클릭").addRequirePayloads(PROJ_CODE, PAGE);
                break;
            case photobook_annie_editdetailcover_clickComplete:
                webLogURIForRecommendBook.addLogDescription("포토북 구성 완성 화면, 크게보기 화면\t상세편집 화면[커버]\t완료 클릭").addRequirePayloads(PROJ_CODE, PAGE, TEMPLATE_CODE, IMG_PATH, TITLE_TEXT);
                break;
            case photobook_annie_editdetailcover_clickImg:
                webLogURIForRecommendBook.addLogDescription("포토북 구성 완성 화면, 크게보기 화면\t상세편집 화면[커버]\t커버 사진(이미지) 클릭").addRequirePayloads(PROJ_CODE, PAGE, TEMPLATE_CODE, IMG_PATH);
                break;
            case photobook_annie_editdetailcover_clickEditphoto:
                webLogURIForRecommendBook.addLogDescription("포토북 구성 완성 화면, 크게보기 화면\t상세편집 화면[커버]\t'사진' 버튼 클릭(하단)").addRequirePayloads(PROJ_CODE, PAGE, TEMPLATE_CODE);
                break;
            case photobook_annie_editdetailcover_updateImg_REQ:
                webLogURIForRecommendBook.addLogDescription("포토북 구성 완성 화면, 크게보기 화면\t상세편집 화면[커버]\t사진 변경 수정(REQ)").addRequirePayloads(PROJ_CODE, PAGE, TEMPLATE_CODE, IMG_PATH);
                break;
            case photobook_annie_editdetailcover_updateImg_RES:
                webLogURIForRecommendBook.addLogDescription("포토북 구성 완성 화면, 크게보기 화면\t상세편집 화면[커버]\t사진 변경 수정(RES)").addInterfaceType(RES).addRequirePayloads(PROJ_CODE, PAGE, TEMPLATE_CODE, IMG_PATH);
                break;
            case photobook_annie_editdetailcover_clickAddphoto:
                webLogURIForRecommendBook.addLogDescription("포토북 구성 완성 화면, 크게보기 화면\t상세편집 화면[커버]\t사진 추가 등록 버튼(+) 클릭").addRequirePayloads(PROJ_CODE, PAGE, TEMPLATE_CODE);
                break;
            case photobook_annie_editdetailcover_clickEditlayout:
                webLogURIForRecommendBook.addLogDescription("포토북 구성 완성 화면, 크게보기 화면\t상세편집 화면[커버]\t'구성바꾸기' 버튼 클릭(하단)").addRequirePayloads(PROJ_CODE, PAGE, TEMPLATE_CODE);
                break;
            case photobook_annie_editdetailcover_edittextEdittext:
                webLogURIForRecommendBook.addLogDescription("포토북 구성 완성 화면, 크게보기 화면\t상세편집 화면[커버]\t'책등입력' 버튼 클릭(하단)").addRequirePayloads(PROJ_CODE, PAGE, TITLE_TEXT);
                break;
            case photobook_annie_editdetailcover_updateEdittext:
                webLogURIForRecommendBook.addLogDescription("상세편집 화면[커버]\t책등입력 페이지\t'제목 입력'+완료 행동값").addRequirePayloads(PROJ_CODE, PAGE, TITLE_TEXT);
                break;
            case photobook_annie_complete_clickIndeximg:
                webLogURIForRecommendBook.addLogDescription("포토북 구성중 화면\t포토북 구성 완성 화면\t속지 이미지 클릭").addRequirePayloads(PROJ_CODE, PAGE);
                break;
            case photobook_annie_editdetailindex_clickBack:
                webLogURIForRecommendBook.addLogDescription("포토북 구성 완성 화면, 크게보기 화면\t상세편집 화면[속지]\t뒤로가기 버튼 클릭").addRequirePayloads(PROJ_CODE, PAGE);
                break;
            case photobook_annie_editdetailindexback_clickCancel:
                webLogURIForRecommendBook.addLogDescription("상세편집 화면[속지]\t뒤로가기 진행 확인 팝업\t취소 버튼 클릭").addRequirePayloads(PROJ_CODE, PAGE);
                break;
            case photobook_annie_editdetailindexback_clickConfirm:
                webLogURIForRecommendBook.addLogDescription("상세편집 화면[속지]\t뒤로가기 진행 확인 팝업\t확인 버튼 클릭").addRequirePayloads(PROJ_CODE, PAGE);
                break;
            case photobook_annie_editdetailindex_clickComplete:
                webLogURIForRecommendBook.addLogDescription("포토북 구성 완성 화면, 크게보기 화면\t상세편집 화면[속지]\t완료 클릭").addRequirePayloads(PROJ_CODE, PAGE, TEMPLATE_CODE, IMG_PATH, BACKGROUND);
                break;
            case photobook_annie_editdetailindex_clickImg:
                webLogURIForRecommendBook.addLogDescription("포토북 구성 완성 화면, 크게보기 화면\t상세편집 화면[속지]\t속지 사진(이미지) 클릭").addRequirePayloads(PROJ_CODE, PAGE, TEMPLATE_CODE, IMG_PATH);
                break;
            case photobook_annie_editdetailindex_clickEditphoto:
                webLogURIForRecommendBook.addLogDescription("포토북 구성 완성 화면, 크게보기 화면\t상세편집 화면[속지]\t'사진' 버튼 클릭(하단)").addRequirePayloads(PROJ_CODE, PAGE, TEMPLATE_CODE);
                break;
            case photobook_annie_editdetailindex_updateImg_REQ:
                webLogURIForRecommendBook.addLogDescription("포토북 구성 완성 화면, 크게보기 화면\t상세편집 화면[속지]\t사진 변경 수정(REQ)").addRequirePayloads(PROJ_CODE, PAGE, TEMPLATE_CODE, IMG_PATH);
                break;
            case photobook_annie_editdetailindex_updateImg_RES:
                webLogURIForRecommendBook.addLogDescription("포토북 구성 완성 화면, 크게보기 화면\t상세편집 화면[속지]\t사진 변경 수정(RES)").addInterfaceType(RES).addRequirePayloads(PROJ_CODE, PAGE, TEMPLATE_CODE, IMG_PATH);
                break;
            case photobook_annie_editdetailindex_clickAddphoto:
                webLogURIForRecommendBook.addLogDescription("포토북 구성 완성 화면, 크게보기 화면\t상세편집 화면[속지]\t사진 추가 등록 버튼 클릭").addRequirePayloads(PROJ_CODE, PAGE, TEMPLATE_CODE);
                break;
            case photobook_annie_editdetailindex_clickEditlayout:
                webLogURIForRecommendBook.addLogDescription("포토북 구성 완성 화면, 크게보기 화면\t상세편집 화면[속지]\t'구성바꾸기' 버튼 클릭(하단)").addRequirePayloads(PROJ_CODE, PAGE, TEMPLATE_CODE);
                break;
            case photobook_annie_editdetailindex_clickEditbackground:
                webLogURIForRecommendBook.addLogDescription("포토북 구성 완성 화면, 크게보기 화면\t상세편집 화면[속지]\t배경 편집 버튼 클릭(하단)").addRequirePayloads(PROJ_CODE, PAGE, TEMPLATE_CODE);
                break;
            case photobook_annie_editdetailindex_updateBackground:
                webLogURIForRecommendBook.addLogDescription("포토북 구성 완성 화면, 크게보기 화면\t상세편집 화면[속지]\t배경 변경 수정").addRequirePayloads(PROJ_CODE, PAGE, TEMPLATE_CODE, BACKGROUND);
                break;
            case photobook_annie_complete_clickPage:
                webLogURIForRecommendBook.addLogDescription("포토북 구성중 화면\t포토북 구성 완성 화면\t각 페이지 이미지(편집하기) 클릭").addRequirePayloads(PROJ_CODE, PAGE);
                break;
            case photobook_annie_editdetail_clickBack:
                webLogURIForRecommendBook.addLogDescription("포토북 구성 완성 화면, 크게보기 화면\t상세편집 화면[기타페이지]\t뒤로가기 버튼 클릭").addRequirePayloads(PROJ_CODE, PAGE);
                break;
            case photobook_annie_editdetail_clickComplete:
                webLogURIForRecommendBook.addLogDescription("포토북 구성 완성 화면, 크게보기 화면\t상세편집 화면[기타페이지]\t완료 클릭").addRequirePayloads(PROJ_CODE, PAGE, TEMPLATE_CODE, IMG_PATH, BACKGROUND);
                break;
            case photobook_annie_editdetail_swapImg:
                webLogURIForRecommendBook.addLogDescription("포토북 구성 완성 화면, 크게보기 화면\t상세편집 화면[기타페이지]\t사진 맞교체(swap)").addRequirePayloads(PROJ_CODE, PAGE, IMG_PATH);
                break;
            case photobook_annie_editdetail_clickImg:
                webLogURIForRecommendBook.addLogDescription("포토북 구성 완성 화면, 크게보기 화면\t상세편집 화면[기타페이지]\t페이지 내부 이미지 클릭").addRequirePayloads(PROJ_CODE, PAGE, TEMPLATE_CODE, IMG_PATH);
                break;
            case photobook_annie_editdetail_clickEditphoto:
                webLogURIForRecommendBook.addLogDescription("포토북 구성 완성 화면, 크게보기 화면\t상세편집 화면[기타페이지]\t사진 편집 버튼 클릭(하단)").addRequirePayloads(PROJ_CODE, PAGE, TEMPLATE_CODE);
                break;
            case photobook_annie_editdetail_updateImg_REQ:
                webLogURIForRecommendBook.addLogDescription("포토북 구성 완성 화면, 크게보기 화면\t상세편집 화면[기타페이지]\t사진 변경 수정(REQ)").addRequirePayloads(PROJ_CODE, PAGE, TEMPLATE_CODE, IMG_PATH);
                break;
            case photobook_annie_editdetail_updateImg_RES:
                webLogURIForRecommendBook.addLogDescription("포토북 구성 완성 화면, 크게보기 화면\t상세편집 화면[기타페이지]\t사진 변경 수정(RES)").addInterfaceType(RES).addRequirePayloads(PROJ_CODE, PAGE, TEMPLATE_CODE, IMG_PATH);
                break;
            case photobook_annie_editdetail_clickAddphoto:
                webLogURIForRecommendBook.addLogDescription("포토북 구성 완성 화면, 크게보기 화면\t상세편집 화면[기타페이지]\t사진 추가 등록 버튼 클릭").addRequirePayloads(PROJ_CODE, PAGE, TEMPLATE_CODE);
                break;
            case photobook_annie_editdetail_clickEditlayout:
                webLogURIForRecommendBook.addLogDescription("포토북 구성 완성 화면, 크게보기 화면\t상세편집 화면[기타페이지]\t'구성바꾸기' 버튼 클릭(하단)").addRequirePayloads(PROJ_CODE, PAGE, TEMPLATE_CODE);
                break;
            case photobook_annie_editdetail_clickEditbackground:
                webLogURIForRecommendBook.addLogDescription("포토북 구성 완성 화면, 크게보기 화면\t상세편집 화면[기타페이지]\t배경 편집 버튼 클릭(하단)").addRequirePayloads(PROJ_CODE, PAGE, TEMPLATE_CODE);
                break;
            case photobook_annie_editdetail_updateBackground:
                webLogURIForRecommendBook.addLogDescription("포토북 구성 완성 화면, 크게보기 화면\t상세편집 화면[기타페이지]\t배경 변경 수정").addRequirePayloads(PROJ_CODE, PAGE, TEMPLATE_CODE, BACKGROUND);
                break;
            case photobook_annie_complete_pressPage:
                webLogURIForRecommendBook.addLogDescription("포토북 구성중 화면\t포토북 구성 완성 화면\t각 페이지 오래누르기").addRequirePayloads(PROJ_CODE, PAGE);
                break;
            case photobook_annie_editpopup_clickEdit:
                webLogURIForRecommendBook.addLogDescription("포토북 구성 완성 화면\t페이지 편집 삭제 팝업\t편집 버튼 누르기").addRequirePayloads(PROJ_CODE, PAGE);
                break;
            case photobook_annie_editpopup_clickDelete:
                webLogURIForRecommendBook.addLogDescription("포토북 구성 완성 화면\t페이지 편집 삭제 팝업\t삭제").addRequirePayloads(PROJ_CODE, PAGE, IMG_CNT);
                break;
            case photobook_annie_editpopup_updateDelete:
                webLogURIForRecommendBook.addLogDescription("포토북 구성 완성 화면\t페이지 편집 삭제 팝업\t삭제 완료").addRequirePayloads(PROJ_CODE, PAGE, IMG_CNT, IMG_PATH);
                break;
            case photobook_annie_complete_scaleupPage:
                webLogURIForRecommendBook.addLogDescription("포토북 구성중 화면\t포토북 구성 완성 화면\t각 페이지 확대").addRequirePayloads(PROJ_CODE, PAGE);
                break;
            case photobook_annie_editphoto_moveImg:
                webLogURIForRecommendBook.addLogDescription("상세편집 화면[커버],상세편집 화면[속지],상세편집 화면[기타페이지]\t사진편집 화면\t이미지 이동").addRequirePayloads(PROJ_CODE, PAGE, TEMPLATE_CODE, IMG_PATH);
                break;
            case photobook_annie_editphoto_rotateImg:
                webLogURIForRecommendBook.addLogDescription("상세편집 화면[커버],상세편집 화면[속지],상세편집 화면[기타페이지]\t사진편집 화면\t이미지 회전").addRequirePayloads(PROJ_CODE, PAGE, TEMPLATE_CODE, IMG_PATH);
                break;
            case photobook_annie_editphoto_scaleupImg:
                webLogURIForRecommendBook.addLogDescription("상세편집 화면[커버],상세편집 화면[속지],상세편집 화면[기타페이지]\t사진편집 화면\t이미지 확대").addRequirePayloads(PROJ_CODE, PAGE, TEMPLATE_CODE, IMG_PATH);
                break;
            case photobook_annie_editphoto_scaledownImg:
                webLogURIForRecommendBook.addLogDescription("상세편집 화면[커버],상세편집 화면[속지],상세편집 화면[기타페이지]\t사진편집 화면\t이미지 축소").addRequirePayloads(PROJ_CODE, PAGE, TEMPLATE_CODE, IMG_PATH);
                break;
            case photobook_annie_editphoto_clickCancel:
                webLogURIForRecommendBook.addLogDescription("상세편집 화면[커버],상세편집 화면[속지],상세편집 화면[기타페이지]\t사진편집 화면\t취소 버튼 클릭").addRequirePayloads(PROJ_CODE, PAGE, TEMPLATE_CODE, IMG_PATH);
                break;
            case photobook_annie_editphoto_clickEnter:
                webLogURIForRecommendBook.addLogDescription("상세편집 화면[커버],상세편집 화면[속지],상세편집 화면[기타페이지]\t사진편집 화면\t확인 버튼 클릭").addRequirePayloads(PROJ_CODE, PAGE, TEMPLATE_CODE, IMG_PATH);
                break;
            case photobook_annie_editphoto_clickBefore:
                webLogURIForRecommendBook.addLogDescription("상세편집 화면[커버],상세편집 화면[속지],상세편집 화면[기타페이지]\t사진편집 화면\t이전사진 버튼 클릭").addRequirePayloads(PROJ_CODE, PAGE, TEMPLATE_CODE, IMG_PATH);
                break;
            case photobook_annie_editphoto_clickNext:
                webLogURIForRecommendBook.addLogDescription("상세편집 화면[커버],상세편집 화면[속지],상세편집 화면[기타페이지]\t사진편집 화면\t다음사진 버튼 클릭").addRequirePayloads(PROJ_CODE, PAGE, TEMPLATE_CODE, IMG_PATH);
                break;
            case photobook_annie_editphoto_clickRotate:
                webLogURIForRecommendBook.addLogDescription("상세편집 화면[커버],상세편집 화면[속지],상세편집 화면[기타페이지]\t사진편집 화면\t회전 버튼 클릭").addRequirePayloads(PROJ_CODE, PAGE, TEMPLATE_CODE, IMG_PATH);
                break;
            case photobook_annie_editphoto_clickReset:
                webLogURIForRecommendBook.addLogDescription("상세편집 화면[커버],상세편집 화면[속지],상세편집 화면[기타페이지]\t사진편집 화면\t리셋 버튼 클릭").addRequirePayloads(PROJ_CODE, PAGE, TEMPLATE_CODE, IMG_PATH);
                break;
            case photobook_annie_editphoto_clickFilter:
                webLogURIForRecommendBook.addLogDescription("상세편집 화면[커버],상세편집 화면[속지],상세편집 화면[기타페이지]\t사진편집 화면\t필터 버튼 클릭").addRequirePayloads(PROJ_CODE, PAGE, TEMPLATE_CODE, IMG_PATH);
                break;
            case photobook_annie_editphoto_updateFilter:
                webLogURIForRecommendBook.addLogDescription("상세편집 화면[커버],상세편집 화면[속지],상세편집 화면[기타페이지]\t사진편집 화면\t필터 변경").addRequirePayloads(PROJ_CODE, PAGE, TEMPLATE_CODE, IMG_PATH, FILTER_CODE);
                break;
            case photobook_annie_addphoto_clickBack:
                webLogURIForRecommendBook.addLogDescription("상세편집 화면[커버],상세편집 화면[속지],상세편집 화면[기타페이지]\t사진 추가 앨범 선택 화면\t뒤로가기 버튼 클릭").addRequirePayloads(PROJ_CODE);
                break;
            case photobook_annie_addphoto_clickEnter:
                webLogURIForRecommendBook.addLogDescription("상세편집 화면[커버],상세편집 화면[속지],상세편집 화면[기타페이지]\t사진 추가 앨범 선택 화면\t확인 버튼 클릭").addRequirePayloads(PROJ_CODE);
                break;
            case photobook_annie_addphoto_clickAlbum:
                webLogURIForRecommendBook.addLogDescription("상세편집 화면[커버],상세편집 화면[속지],상세편집 화면[기타페이지]\t사진 추가 앨범 선택 화면\t휴대폰 사진 버튼 클릭").addRequirePayloads(PROJ_CODE, PHOTO_ALBUM);
                break;
            case photobook_annie_complete_clickCart:
                webLogURIForRecommendBook.addLogDescription("포토북 구성중 화면\t포토북 구성 완성 화면\t장바구니에 담기 버튼 클릭").addRequirePayloads(PROJ_CODE);
                break;
            case photobook_annie_completeconfirm_clickCancel:
                webLogURIForRecommendBook.addLogDescription("포토북 구성 완성 화면\t장바구니 저장 진행 확인 팝업\t취소 버튼 클릭").addRequirePayloads(PROJ_CODE);
                break;
            case photobook_annie_completeconfirm_clickConfirm:
                webLogURIForRecommendBook.addLogDescription("포토북 구성 완성 화면\t장바구니 저장 진행 확인 팝업\t저장 버튼 클릭").addRequirePayloads(PROJ_CODE, AURA_XML);
                break;
            case photobook_annie_completenotice_clickContinue:
                webLogURIForRecommendBook.addLogDescription("포토북 구성 완성 화면\t장바구니 저장 완료 알림 팝업\t계속편집하기 버튼 클릭").addRequirePayloads(PROJ_CODE);
                break;
            case photobook_annie_completenotice_clickMovecart:
                webLogURIForRecommendBook.addLogDescription("포토북 구성 완성 화면\t장바구니 저장 완료 알림 팝업\t장바구니로 이동 버튼 클릭").addRequirePayloads(PROJ_CODE);
                break;
            case cart_annie_list_clickBack:
                webLogURIForRecommendBook.addLogDescription("장바구니 저장 완료 알림 팝업, 홈, 크게보기 화면\t장바구니 화면\t뒤로가기 버튼 클릭");
                break;
            case cart_annie_list_clickIndex:
                webLogURIForRecommendBook.addLogDescription("장바구니 저장 완료 알림 팝업, 홈, 크게보기 화면\t장바구니 화면\t우측상단 목록버튼 클릭");
                break;
            case cart_annie_list_clickBanner:
                webLogURIForRecommendBook.addLogDescription("장바구니 저장 완료 알림 팝업, 홈, 크게보기 화면\t장바구니 화면\t상단 배너 클릭");
                break;
            case cart_annie_list_clickProject:
                webLogURIForRecommendBook.addLogDescription("장바구니 저장 완료 알림 팝업, 홈, 크게보기 화면\t장바구니 화면\t프로젝트 개체 클릭").addRequirePayloads(PROJ_CODE);
                break;
            case photobook_annie_recomplete_clickBack:
                webLogURIForRecommendBook.addLogDescription("장바구니 화면\t(재)포토북 구성 완성 화면\t뒤로가기 버튼 클릭").addRequirePayloads(PROJ_CODE);
                break;
            case cart_mobile_list_clickChange:
                webLogURIForRecommendBook.addLogDescription("장바구니 저장 완료 알림 팝업, 홈, 크게보기 화면\t장바구니 화면\t수량 '변경 ▶' 클릭(mobile 상품)").addRequirePayloads(PROJ_CODE, PROJ_CNT);
                break;
            case cart_mobile_volume_clickCancel:
                webLogURIForRecommendBook.addLogDescription("장바구니 화면\t수량변경 팝업 화면\t취소 버튼(X) 클릭").addRequirePayloads(PROJ_CODE);
                break;
            case cart_mobile_volume_updateComplete:
                webLogURIForRecommendBook.addLogDescription("장바구니 화면\t수량변경 팝업 화면\t수량 변경 완료").addRequirePayloads(PROJ_CODE, PROJ_CNT, PREV_PROJ_CNT);
                break;
            case cart_annie_list_clickProjopen:
                webLogURIForRecommendBook.addLogDescription("장바구니 저장 완료 알림 팝업, 홈, 크게보기 화면\t장바구니 화면\t프로젝트 ▽ 버튼 누르기").addRequirePayloads(PROJ_CODE);
                break;
            case cart_annie_projopen_clickEdit:
                webLogURIForRecommendBook.addLogDescription("장바구니 화면\t프로젝트 ▽ 팝업 화면\t편집버튼 클릭").addRequirePayloads(PROJ_CODE);
                break;
            case cart_annie_projopen_clickDelete:
                webLogURIForRecommendBook.addLogDescription("장바구니 화면\t프로젝트 ▽ 팝업 화면\t삭제버튼 클릭").addRequirePayloads(PROJ_CODE);
                break;
            case cart_annie_list_clickOrder:
                webLogURIForRecommendBook.addLogDescription("장바구니 저장 완료 알림 팝업, 홈, 크게보기 화면\t장바구니 화면\t주문하기 버튼 클릭").addRequirePayloads(PROJ_CODE_LIST);
            case v1_product_click:
                webLogURIForRecommendBook.addLogDescription("상품클릭").addRequirePayloads(PRODUCT_CLICK).setShouldSendLogMessage(true);
                break;
            case v1_user_totalimg_exif:
                webLogURIForRecommendBook.addLogDescription("이미지올리기").addRequirePayloads(IMAGE_EXIF, IMG_CNT).setShouldSendLogMessage(true);
                break;
        }

        return webLogURIForRecommendBook;
    }
}
