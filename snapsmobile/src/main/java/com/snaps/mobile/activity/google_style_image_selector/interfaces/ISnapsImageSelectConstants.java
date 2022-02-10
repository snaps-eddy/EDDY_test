package com.snaps.mobile.activity.google_style_image_selector.interfaces;

/**
 * Created by ysjeong on 2016. 11. 23..
 */

public interface ISnapsImageSelectConstants {

    //튜토리얼이 보여진 날짜 저장
    String SETTING_KEY_TUTORIAL_PINCH_MOTION_SHOWN_DATE = "SETTING_KEY_TUTORIAL_PINCH_MOTION_SHOWN_DATE";
    String SETTING_KEY_SMART_RECOMMEND_BOOK_CHANGE_IMAGE_BY_DRAG_N_DROP = "SETTING_KEY_SMART_RECOMMEND_BOOK_CHANGE_IMAGE_BY_DRAG_N_DROP";
    String SETTING_KEY_SMART_RECOMMEND_BOOK_SWIPE_PAGE = "SETTING_KEY_SMART_RECOMMEND_BOOK_SWIPE_PAGE";

    //마지막으로 선택한 폴더의 ID 저장
    String SETTING_KEY_LAST_SELECTED_ALBUM_ID = "SETTING_KEY_LAST_SELECTED_ALBUM_ID";
    String SETTING_KEY_LAST_SELECTED_PHOTO_SOURCE_KIND = "SETTING_KEY_LAST_SELECTED_PHOTO_SOURCE_KIND";

    int INVALID_TRAY_CELL_ID = -999;

    //폰 사진 컬럼 갯수
    int COLUMN_COUNT_OF_UI_DEPTH_YEAR           = 8;
    int COLUMN_COUNT_OF_UI_DEPTH_MONTH          = 4;
    int COLUMN_COUNT_OF_UI_DEPTH_DAY            = 3;
    int COLUMN_COUNT_OF_UI_DEPTH_STAGGERED      = 2;

    //폰 사진 컬럼 갯수(가로 모드)
    int COLUMN_COUNT_OF_LANDSCAPE_UI_DEPTH_YEAR           = 14;
    int COLUMN_COUNT_OF_LANDSCAPE_UI_DEPTH_MONTH          = 7;
    int COLUMN_COUNT_OF_LANDSCAPE_UI_DEPTH_DAY            = 5;
    int COLUMN_COUNT_OF_LANDSCAPE_UI_DEPTH_STAGGERED      = 2;

    int INVALID_VALUE                        = -999;
    int IS_CONTAINS_PHOTO_ON_ALL_TRAY        = -1;

    int PHOTO_PRINT_MAX                     = 500;

    int TRANSPARENCY_PHOTO_CARD_MAX                     = 24;

    int REQCODE_IMGDETAILEDIT               = 1;
    int REQCODE_EDIT                        = 3;
    int REQCODE_PHOTOPRINT                  = 99;
    int REQCODE_DIARY_WRITE                 = 100;
    int REQCODE_TRAY_ALL_VIEW               = 1000;

    int REQCODE_GOOGLE_SIGN_IN              = 10000;
    int REQCODE_INPUT_TITLE                 = 10001;
    int REQCODE_GOOGLE_SCOPE                = 10002;

    int RESULT_CODE_TRAY_ALL_VIEW_EDITED    = 500001;

    int PHONE_ALL_PHOTO_CURSOR_ID           = -999999;

    String SINGLE_CHOOSE_IMAGE_KEY          = "single_choose_image_key";
    String GOOGLE_PHOTO_AUTO_BACKUP         = "Auto Backup";

    String GOOGLE_PHOTO_SIGN_IN_RESPONSE_TOKEN_KEY         = "google_photo_sign_in_response_token_key";
    String GOOGLE_PHOTO_SIGN_IN_RESPONSE_USE_ID_KEY         = "google_photo_sign_in_response_user_id_key";

    String GOOGLE_PHOTO_ALBUM_URL           = "https://picasaweb.google.com/data/feed/api/user/%s/albumid/%s";

    String PHONE_PHONO_QUERY_IMG_CNT        = "imgcnt";

    enum eTRAY_CELL_STATE {
        EMPTY,
        TEMPLATE,
        PHOTO_THUMBNAIL,
        PLUS_BUTTON,
        SECTION_TITLE,
        SECTION_LINE,
        EMPTY_DUMMY,
    }

    enum eGOOGLE_STYLE_DEPTH {
        DEPTH_YEAR,
        DEPTH_MONTH,
        DEPTH_DAY,
        DEPTH_STAGGERED
    }

    enum eGOOGLE_STYLE_HOLDER_TYPE {
        HOLDER_TYPE_YEAR_SECTION, //DEPTH_YEAR의 모양이 달라서 추가한 SECTION..
        HOLDER_TYPE_MONTH_SECTION, //DEPTH_YEAR의 모양이 달라서 추가한 SECTION..
        HOLDER_TYPE_COMMON_DATE_SECTION,
        HOLDER_TYPE_THUMBNAIL,
        HEADER
    }

    enum ePINCH_TARGET_TYPE {
        CURRENT,
        NEXT,
        PREV
    }

    enum eMOTION_STATE {
        IDLE,
        SCALE_NEXT_DEPTH,
        SCALE_PREV_DEPTH,
    }

    enum eANIMATION_OBJECT_TYPE { //Section...
        CURRENT,
        TARGET,
        BOTH
    }

    enum eTUTORIAL_TYPE {
        PHONE_FRAGMENT_PINCH_MOTION,
        SMART_RECOMMEND_BOOK_CHANGE_IMAGE_BY_DRAG_N_DROP,
        SMART_RECOMMEND_BOOK_SWIPE_PAGE
    }

    enum ePhotoSourceType {
        NONE,
        PHONE,
        FACEBOOK,
        GOOGLE_PHOTO,
        KAKAO_STORY,
        INSTAGRAM,
        SNAPS_STICKER
    }
}
