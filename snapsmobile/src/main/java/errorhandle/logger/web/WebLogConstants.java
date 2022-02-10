package errorhandle.logger.web;

public interface WebLogConstants {

    String WEB_LOG_ANNIE_PREFIX = "/";

    enum eWebLogName {
        home_event_annie_click,
        home_event_annie_clickDirect,
        photobook_annie_view_click,
        photobook_annie_selectphoto_clickBack,
        photobook_annie_selectphotobackpopup_clickCancel,
        photobook_annie_selectphotobackpopup_clickOk,
        photobook_annie_selectphoto_clickTotal,
        photobook_annie_selectphoto_clickEnter,
        photobook_annie_selectphoto_clickRange,
        photobook_annie_make_clickCancel,
        photobook_annie_complete_processLayout_REQ,
        photobook_annie_complete_processLayout_RES,
        photobook_annie_make_clickBack,
        photobook_annie_make_clickSound,
        photobook_annie_complete_clickBack,
        photobook_annie_completeback_clickCancel,
        photobook_annie_completeback_clickConfirm,
        photobook_annie_complete_clickUp,
        photobook_annie_complete_clickPreview,
        photobook_annie_complete_widthPreview,
        photobook_annie_preview_page,
        photobook_annie_preview_clickBack,
        photobook_annie_preview_clickPage,
        photobook_annie_preview_clickMovecart,
        photobook_annie_preview_clickCancel,
        photobook_annie_preview_clickConfirm,
        photobook_annie_preview_clickContinue,
        photobook_annie_complete_clickCoverimg,
        photobook_annie_complete_clickCover,
        photobook_annie_editdetailcover_clickBack,
        photobook_annie_editdetailcoverback_clickCancel,
        photobook_annie_editdetailcoverback_clickConfirm,
        photobook_annie_editdetailcover_clickComplete,
        photobook_annie_editdetailcover_clickImg,
        photobook_annie_editdetailcover_clickEditphoto,
        photobook_annie_editdetailcover_updateImg_REQ,
        photobook_annie_editdetailcover_updateImg_RES,
        photobook_annie_editdetailcover_clickAddphoto,
        photobook_annie_editdetailcover_clickEditlayout,
        photobook_annie_editdetailcover_edittextEdittext,
        photobook_annie_editdetailcover_updateEdittext,
        photobook_annie_complete_clickIndeximg,
        photobook_annie_editdetailindex_clickBack,
        photobook_annie_editdetailindexback_clickCancel,
        photobook_annie_editdetailindexback_clickConfirm,
        photobook_annie_editdetailindex_clickComplete,
        photobook_annie_editdetailindex_clickImg,
        photobook_annie_editdetailindex_clickEditphoto,
        photobook_annie_editdetailindex_updateImg_REQ,
        photobook_annie_editdetailindex_updateImg_RES,
        photobook_annie_editdetailindex_clickAddphoto,
        photobook_annie_editdetailindex_clickEditlayout,
        photobook_annie_editdetailindex_clickEditbackground,
        photobook_annie_editdetailindex_updateBackground,
        photobook_annie_complete_clickPage,
        photobook_annie_editdetail_clickBack,
        photobook_annie_editdetail_clickComplete,
        photobook_annie_editdetail_swapImg,
        photobook_annie_editdetail_clickImg,
        photobook_annie_editdetail_clickEditphoto,
        photobook_annie_editdetail_updateImg_REQ,
        photobook_annie_editdetail_updateImg_RES,
        photobook_annie_editdetail_clickAddphoto,
        photobook_annie_editdetail_clickEditlayout,
        photobook_annie_editdetail_clickEditbackground,
        photobook_annie_editdetail_updateBackground,
        photobook_annie_complete_pressPage,
        photobook_annie_editpopup_clickEdit,
        photobook_annie_editpopup_clickDelete,
        photobook_annie_editpopup_updateDelete,
        photobook_annie_complete_scaleupPage,
        photobook_annie_editphoto_moveImg,
        photobook_annie_editphoto_rotateImg,
        photobook_annie_editphoto_scaleupImg,
        photobook_annie_editphoto_scaledownImg,
        photobook_annie_editphoto_clickCancel,
        photobook_annie_editphoto_clickEnter,
        photobook_annie_editphoto_clickBefore,
        photobook_annie_editphoto_clickNext,
        photobook_annie_editphoto_clickRotate,
        photobook_annie_editphoto_clickReset,
        photobook_annie_editphoto_clickFilter,
        photobook_annie_editphoto_updateFilter,
        photobook_annie_addphoto_clickBack,
        photobook_annie_addphoto_clickEnter,
        photobook_annie_addphoto_clickAlbum,
        photobook_annie_complete_clickCart,
        photobook_annie_completeconfirm_clickCancel,
        photobook_annie_completeconfirm_clickConfirm,
        photobook_annie_completenotice_clickContinue,
        photobook_annie_completenotice_clickMovecart,
        cart_annie_list_clickBack,
        cart_annie_list_clickIndex,
        cart_annie_list_clickBanner,
        cart_annie_list_clickProject,
        photobook_annie_recomplete_clickBack,
        cart_mobile_list_clickChange,
        cart_mobile_volume_clickCancel,
        cart_mobile_volume_updateComplete,
        cart_annie_list_clickProjopen,
        cart_annie_projopen_clickEdit,
        cart_annie_projopen_clickDelete,
        cart_annie_list_clickOrder,
        v1_product_click,
        v1_user_totalimg_exif,
        v1_user_addimg_exif
    }

    enum eWebLogInterfaceType {
        REQ("REQ"),
        RES("RES");

        private String value;

        eWebLogInterfaceType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    enum eWebLogMethodType {
        GET("GET"),
        POST("POST");

        private String value;

        eWebLogMethodType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    enum eWebLogLanguage {
        KR("ko-KR"),
        EN("en-ENG"),
        JP("jp-JPN"),
        CH("cn-CHN");

        private String value;

        eWebLogLanguage(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    enum eWebLogPayloadType {
        WHERE("where"),
        TITLE_TEXT("key_title_text"),
        BACKGROUND("background"),
        PHOTO_ALBUM("photo_album"),
        FILTER_CODE("filter_code"),
        TEMPLATE_CODE("temp_code"),
        IMG_PATH("img_path"),
        IMG_CNT("IMG_CNT"),
        PAGE("page"),
        AURA_XML("key_photobook_aura_xml"),
        PROJ_CODE("proj_code"),
        PROJ_CODE_LIST("proj_code_list"),
        PROJ_CNT("proj_cnt"),
        PREV_PROJ_CNT("prev_proj_cnt"),
        REQUEST_CONTENTS("key_request_contents"),
        RESPONSE_CONTENTS("key_response_contents"),
        PRODUCT_CLICK("product_click"),
        IMAGE_EXIF("IMAGE_EXIF");


        private String keyName;

        eWebLogPayloadType(String keyName) {
            this.keyName = keyName;
        }

        public String getKeyName() {
            return keyName;
        }
    }

    enum eWebLogPhotoAlbumType {
        PHONE("value_album_phone"),
        KAKAO_STORY("value_album_kakao"),
        FACEBOOK("value_album_facebook"),
        INSTARGRAM("value_album_instargram"),
        GOOGLE_PHOTO("value_album_google");

        private String value;

        eWebLogPhotoAlbumType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    enum eWebLogPhotoUploadCompleteWhere {
        FIRST("value_first"),
        SECOND("value_second");

        private String value;

        eWebLogPhotoUploadCompleteWhere(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
