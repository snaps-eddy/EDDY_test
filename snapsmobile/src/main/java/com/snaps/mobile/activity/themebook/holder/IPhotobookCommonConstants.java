package com.snaps.mobile.activity.themebook.holder;

/**
 * Created by ysjeong on 16. 6. 22..
 */
public interface IPhotobookCommonConstants {

    int MIN_PAGE_COUNT_OF_PHOTO_BOOK = 21;

    int HANDLER_MSG_UPLOAD_ORG_IMAGES = 900001;
    int HANDLER_MSG_UPLOAD_THUMB_IMAGES = 900002;

    int HANDLER_MSG_SHOW_TUTORIAL_FOR_SMART_RECOMMEND_BOOK_COVER = 910000;
    int HANDLER_MSG_INIT_CANVAS_MATRIX                           = 910001;
    int HANDLER_MSG_UNLOCK_ORIENTATION_SENSOR = 910002;
    int HANDLER_MSG_CHANGE_TITLE_TEXT = 910003;

    String SNAPS_BROADCAST_BUNDLE_EXTRA_KEY_MSG_WHAT = "SNAPS_BROADCAST_BUNDLE_EXTRA_KEY_MSG_WHAT";
    String SNAPS_BROADCAST_BUNDLE_EXTRA_KEY_PAGE_INDEX = "SNAPS_BROADCAST_BUNDLE_EXTRA_KEY_PAGE_INDEX";

    int SNAPS_BROADCAST_BUNDLE_VALUE_TEXT_TO_IMAGE_SHOW_OVER_AREA_MSG = 1001;
    int SNAPS_BROADCAST_BUNDLE_VALUE_TEXT_TO_IMAGE_SHOW_TEXT_SERVER_NETWORK_ERR_MSG = 1002;

    int EDIT_PHOTO = 1;
    int EDIT_CONTENTS = 2;
    int EDIT_TEXT = 3;

    String TUTORIAL1 = "tutorial1";
    String TUTORIAL2 = "tutorial2";

    String TUTORIAL_ETC = "tutorial_etc";

    int REQ_PHOTO = 10;
    int REQ_CONTENT = 12;
    int REQ_MODIFY = 13;
    int REQ_EDIT_TEXT = 14;
    int REQ_INSERT_PHOTO = 15;

    int REQ_COVER_PHOTO = 20;
    int REQ_COVER_TEXT = 21;
    int REQ_COVER_CHANGE = 22;
    int REQ_ADD_PAGE = 23;
    int REQ_CHANGE_PAGE = 24;

    int REQ_PREVIEW = 30;
    int REQ_PRODUCT_ADD_PAGE = 40;
    int REQ_PRODUCT_CHANGE_PAGE = 41;
    int REQ_NAME_STICKER_EDIT_TEXT = 41;

    int RESULT_CODE_EDIT = 110001;

    String EXTRAS_KEY_PAGE_INDEX = "extras_key_page_index";
    String EXTRAS_KEY_SCREEN_ORIENTATION_ACT_INFO = "extras_key_screen_orientation_act_info";
    String EXTRAS_KEY_ACTIVE_ROTATION_SENSOR = "extras_key_active_rotation_sensor";

    public enum SnapsTextAlign {
        ALIGN_LEFT("left"),
        ALIGN_CENTER("center"),
        ALIGN_RIGHT("right");
        private final String str;
        private SnapsTextAlign(final String str) {
            this.str = str;
        }
        public String getStr() {
            return str;
        }
    }

    enum eImageDataRequestType {
        ONLY_COVER,
        ALL,
        ALL_EXCEPT_COVER
    }
}
