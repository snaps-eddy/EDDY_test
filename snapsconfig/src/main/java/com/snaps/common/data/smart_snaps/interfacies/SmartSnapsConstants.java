package com.snaps.common.data.smart_snaps.interfacies;

import com.snaps.common.R;
import com.snaps.common.utils.ui.StringUtil;

import static com.snaps.common.utils.constant.Const_PRODUCT.PRODUCT_SMART_SNAPS_ANALYSIS_PHOTO_BOOK_BABY;
import static com.snaps.common.utils.constant.Const_PRODUCT.PRODUCT_SMART_SNAPS_ANALYSIS_PHOTO_BOOK_COUPLE;
import static com.snaps.common.utils.constant.Const_PRODUCT.PRODUCT_SMART_SNAPS_ANALYSIS_PHOTO_BOOK_FAMILY;
import static com.snaps.common.utils.constant.Const_PRODUCT.PRODUCT_SMART_SNAPS_ANALYSIS_PHOTO_BOOK_TRAVEL;

/**
 * Created by ysjeong on 2018. 1. 16..
 */

public class SmartSnapsConstants {

    public static final boolean IS_USE_SMART_SNAPS_FUNCTION = true;

    public static final String URL_RECOMMEND_BOOK_INFO_PAGE = "snapsapp://openAppPopup?openUrl=/mw/v3/store/information/info_bookAIphoto.jsp?clssCode=KOR0031002011000&sclsscode=001001019000&mclsscode=001001019001&prodCode=00800600130001&prodName=AI%20포토북%20애니&paperCode=001001019000&fromType=main";

    public static final String EXTRAS_KEY_PAGE_EDIT_REQUEST_DATA = "extras_key_page_edit_request_data";
    public static final String EXTRAS_KEY_LAST_EDITED_PAGE_REQUEST_DATA = "extras_key_last_edited_index";

    public static final String SMART_RECOMMEND_BOOK_GLOSSY_TYPE = "M";

    public static final int SMART_SNAPS_ANALYSIS_PHOTO_BOOK_MIN_PHOTO_COUNT = 23;
    public static final int SMART_SNAPS_ANALYSIS_PHOTO_BOOK_MAX_PHOTO_COUNT = 150;

    public static long DEFAULT_MATRIX_ANIMATION_TIME = 300;

    public static long TRANSPARENCY_PHOTO_CARD_ANIMATION_TIME = 600;

    public static long WAIT_FOR_ANIMATE_PREPARE_TIME = 200;
    public static long WAIT_FOR_ANIMATE_PREPARE_TIME_FOR_CALENDAR = 600;

    public static final long MAX_SMART_SNAPS_PAGING_WAIT_TIME = 15000; //페이징이 넘어가면서 업로드흘 할때 너무 오래 걸리면 이만큼만 기다려 준다.

    public static final long LIMIT_OF_PAGE_DESIGN_LIST_SIZE = 20;

    public static final int REQUEST_CODE_PAGE_EDIT = 1000;

    public static final int RESULT_CODE_EDITED = 1;
    public static final int RESULT_CODE_CANCEL = -1;

    public enum eSmartSnapsImageSelectType {
        NONE,
        SMART_CHOICE,
        NORMAL_CHOICE,
        SMART_RECOMMEND_BOOK_PRODUCT
    }

    public enum eSmartSnapsImgState {
        NONE,
        READY,
        RECEIVE_SMART_SNAPS_INFO,
        FINISH_ANIMATION
    }

    public enum eSmartSnapsProgressType {
        NONE,
        FIST_LOAD,
        ADD_PAGE,
        CHANGE_DESIGN,
        CHANGE_PHOTO
    }

    public enum eSmartSnapsAnalysisTaskType {
        GET_PROJECT_CODE(2),
        UPLOAD_THUMBNAILS(73),
        GET_RECOMMEND_TEMPLATE(20),
        FIT_CENTER_FACE(1),
        GET_COVER_TEMPLATE(1),
        GET_PAGE_TEMPLATE(1),
        GET_PAGE_BG_RES(2);

        private int progressWeight = 0;
        eSmartSnapsAnalysisTaskType(int weight) {
            this.progressWeight = weight;
        }

        public int getProgressWeight() {
            return progressWeight;
        }
    }

    public enum eSmartAnalysisPhotoBookThemeType {
        TRAVEL("1"),
        COUPLE("2"),
        FAMILY("3"),
        BABY("4");

        public static eSmartAnalysisPhotoBookThemeType createByThemeCode(String code) {
            if (StringUtil.isEmpty(code)) return null;
            else if (code.equalsIgnoreCase("1")) return TRAVEL;
            else if (code.equalsIgnoreCase("2")) return COUPLE;
            else if (code.equalsIgnoreCase("3")) return FAMILY;
            else if (code.equalsIgnoreCase("4")) return BABY;
            return null;
        }

        private String themeCode = "";
        eSmartAnalysisPhotoBookThemeType(String themeCode) {
            this.themeCode = themeCode;
        }

        public String getThemeCode() {
            return themeCode;
        }

        public String getTemplateCode() {
            switch (this) {
                case TRAVEL: return PRODUCT_SMART_SNAPS_ANALYSIS_PHOTO_BOOK_TRAVEL;
                case COUPLE: return PRODUCT_SMART_SNAPS_ANALYSIS_PHOTO_BOOK_COUPLE;
                case FAMILY: return PRODUCT_SMART_SNAPS_ANALYSIS_PHOTO_BOOK_FAMILY;
                case BABY: return PRODUCT_SMART_SNAPS_ANALYSIS_PHOTO_BOOK_BABY;
            }
            return null;
        }

        public int getTemplateTextResId() {
            switch (this) {
                case TRAVEL: return R.string.smart_snaps_theme_travel;
                case COUPLE: return R.string.smart_snaps_theme_couple;
                case FAMILY: return R.string.smart_snaps_theme_famliy;
                case BABY: return R.string.smart_snaps_theme_baby;
            }
            return 0;
        }

        public String getWebLogCode() {
            switch (this) {
                case TRAVEL: return "value_ani_theme_travel";
                case COUPLE: return "value_ani_theme_couple";
                case FAMILY: return "value_ani_theme_family";
                case BABY: return "value_ani_theme_baby";
            }
            return "";
        }
    }

    public enum eSmartSnapsAnalysisProductEditListHolderType {
        HOLDER_TYPE_COVER,
        HOLDER_TYPE_PAGE,
        HOLDER_TYPE_DUMMY
    }

    public enum eSmartSnapsAnalysisImagePageType {
        PAGE("page"),
        COVER("cover"),
        TITLE("title");

        private String type = "";
        eSmartSnapsAnalysisImagePageType(String type) {
            this.type = type;
        }

        public String getTypeStr() {
            return type;
        }
    }

    public enum eEditorBottomFragment {
        NONE(""),
        SELECT_LAYOUT_FRAGMENT("eEditorBottomFragment_SELECT_LAYOUT_FRAGMENT"),
        SELECT_BG_FRAGMENT("eEditorBottomFragment_SELECT_BG_FRAGMENT"),
        SELECT_PHOTO_FRAGMENT("eEditorBottomFragment_SELECT_LAYOUT_FRAGMENT"),
        EDIT_TITLE("eEditorBottomFragment_EDIT_TITLE");

        private String fragmentTag = "";
        eEditorBottomFragment(String tag) {
            this.fragmentTag = tag;
        }

        public String getFragmentTag() {
            return fragmentTag;
        }
    }

    public enum ePinchZoomPivotX {
        LEFT,
        CENTER,
        RIGHT
    }
}
