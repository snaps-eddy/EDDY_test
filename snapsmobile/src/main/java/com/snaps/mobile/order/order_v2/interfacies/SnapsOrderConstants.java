package com.snaps.mobile.order.order_v2.interfacies;

/**
 * Created by ysjeong on 2017. 3. 28..
 */

public interface SnapsOrderConstants {
    enum eSnapsOrderType {
        ORDER_TYPE_PREPARATION,
        ORDER_TYPE_GET_PROJECT_CODE,
        ORDER_TYPE_VERIFY_PROJECT_CODE,
        ORDER_TYPE_UPLOAD_ORG_IMAGE,
        ORDER_TYPE_UPLOAD_THUMB_IMAGE,
        ORDER_TYPE_MAKE_PAGE_THUMBNAILS,
        ORDER_TYPE_UPLOAD_MAIN_THUMBNAIL,
        ORDER_TYPE_UPLOAD_PAGE_THUMBNAILS,
        ORDER_TYPE_MAKE_XML,
        ORDER_TYPE_UPLOAD_XML,
        ORDER_TYPE_INSPECT_REQUIRED_TO_ORDER,

        ORDER_TYPE_GET_DIARY_SEQ_CODE,
        ORDER_TYPE_CHECK_DIARY_MISSION_STATE,
        ORDER_TYPE_UPLOAD_DIARY_THUMBNAIL,
        ORDER_TYPE_MAKE_DIARY_XML,
        ORDER_TYPE_UPLOAD_DIARY_XML,
    }

    enum eSnapsOrderUploadResultMsg {
        UPLOAD_READIED_BACKGROUND_IMG_UPLOAD,
        UPLOAD_FAILED_CAUSE_ORG_IMAGE_DATA_IS_NULL,
        UPLOAD_FAILED_CAUSE_RETURN_VALUE_ERR,
        UPLOAD_FAILED_CAUSE_THUMBNAIL_RETURN_VALUE_ERR,
        UPLOAD_FAILED_CAUSE_SUSPENDED,
        UPLOAD_FAILED_CAUSE_OVER_ARR_IDX,
        UPLOAD_FAILED_CAUSE_EXCEPTION,
        UPLOAD_SKIPPED,
        UPLOAD_FAILED_CAUSE_NOT_SUPPORT_NETWORK_STATE,
        UPLOAD_FAILED_CAUSE_NOT_EXIST_PROJECT_CODE,
        UPLOAD_FAILED_CAUSE_NOT_EXIST_DIARY_SEQ,
        UPLOAD_FAILED_CAUSE_NOT_EXIST_IMAGE
    }

    enum NETWORK_CHECK_RESULT {
        ERROR,
        SUCCESS,
        FAILED_CAUSE_IS_NOT_ALLOW_CELLULAR,
        FAILED_CAUSE_IS_NOT_CONNECTED,
        FAILED_CAUSE_DENIED
    }

    /**
     * 1.저해상도 사진이 포함되어 있는가.
     * 2.사진이 모두 찼는가
     * 3.주문 수량 조건이 충족하는가
     */
    enum eSaveCartConditionCheckResult {
        SUCCESS,
        EXCEPTION,
        FAILED_CAUSE_CONTAIN_LOW_RESOLUTION_PHOTO,
        FAILED_CAUSE_IS_LACK_QUANTITY,
        FAILED_CAUSE_EXIST_EMPTY_IMAGE_CONTROL
    }

    String SETTING_VALUE_USE_CELLULAR_CONFIRM_DATE = "setting_value_use_cellular_confirm_date";
    String SETTING_VALUE_USE_CELLULAR_DENIED_DATE = "setting_value_use_cellular_denied_date";

    String EXCEPTION_MSG_TASK_UNKNOWN = "snaps order exception : task unknown.";
    String EXCEPTION_MSG_NETWORK_ERROR = "snaps order exception : network error.";
    String EXCEPTION_MSG_UNKNOWN_HOST_ERROR = "snaps order exception : unknown host error.";

    String ORDER_STAT_CODE = "146001";

    int ORDER_PREPARE_INSPECT_RESULT_OK = 100;
    int ORDER_PREPARE_INSPECT_RESULT_DATA_ERROR = 500;
    int ORDER_PREPARE_INSPECT_RESULT_NOT_LOGGED_IN = 501;
    int ORDER_PREPARE_INSPECT_RESULT_NOT_EXIST_PROJECT_CODE = 502;
    int ORDER_PREPARE_INSPECT_RESULT_NOT_PHOTO_REPLENISHMENT = 503; //사진이 모두 채워지지 않은 상태
    int ORDER_PREPARE_INSPECT_RESULT_NOT_PRINTABLE_PHOTO_EXIST = 504; //인화 불가 사진
    int ORDER_PREPARE_INSPECT_RESULT_NOT_EXIST_DIARY_SEQ = 505;
    int ORDER_PREPARE_INSPECT_RESULT_NOT_EXIST_DIARY_IMAGE = 506;
    int ORDER_PREPARE_INSPECT_RESULT_NOT_CONNECT_NETWORK = 507;
    int ORDER_PREPARE_INSPECT_RESULT_NOT_ENOUGH_STORAGE_SPACE = 508;
    int ORDER_PREPARE_INSPECT_RESULT_IS_NOT_EXIST_PROJECT_FILE_FOLDER = 509;
    int ORDER_PREPARE_INSPECT_RESULT_NOT_FULL_REQUIRE_PAGE = 510; //페이지가 모자란 상태

    int ORG_IMG_UPLOAD_START = 0;
    int ORG_IMG_UPLOAD_RESULT_TYPE_SUCCESS = 10;
    int ORG_IMG_UPLOAD_RESULT_TYPE_FAIL = 11;
    int ORG_IMG_UPLOAD_RESULT_TYPE_ALL_TASK_FINISHED = 100;

    int LOGIN_REQUSTCODE = 2000;

//    int DELAY_TIME_FOR_BACKGROUND_IMG_UPLOAD_PREPARE = 5000; //와이 파이가 연결되면 해당 시간 이후부터 원본 이미지를 업로드 하기 시작한다. (버벅임을 방지하기 위함이다.)
    int DELAY_TIME_FOR_BACKGROUND_IMG_UPLOAD_PREPARE = 100; //와이 파이가 연결되면 해당 시간 이후부터 원본 이미지를 업로드 하기 시작한다. (버벅임을 방지하기 위함이다.)
}
