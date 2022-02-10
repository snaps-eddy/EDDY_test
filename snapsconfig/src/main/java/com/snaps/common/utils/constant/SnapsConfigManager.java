package com.snaps.common.utils.constant;

import android.content.Context;

import com.snaps.common.BuildConfig;
import com.snaps.common.utils.pref.Setting;
import com.snaps.common.utils.ui.ContextUtil;
import com.snaps.common.utils.ui.StringUtil;

import static com.snaps.common.utils.constant.Const_VALUE.KEY_DEVELOP_URL_PATH;

/**
 * Created by ysjeong on 16. 6. 8..
 * <p>
 * 설정에 관련된 전역 변수들.
 * 직접 사용해도 상관은 없지만, 될 수 있으며면 Config를 통해 사용하세요.
 */
public class SnapsConfigManager {

    private volatile static SnapsConfigManager gInstance = null;

    //	/** SnapsMobile SDK 사용 유무 관련 **/
    private boolean IS_COMPLETE = false;

    private boolean IS_SDK = false;

    private boolean IS_BETWEEN_VERSION = false;

    private String strPageKey = "";

    private String strGroupURL = "";

    // QA 서버..
    private boolean IS_QA = false;

    private boolean isEdit = false;

    // FIXME v2 / v3 혼용일 떄
    private boolean IS_USE_INDEX_V3 = false;

    private String KAKAO_SCHEMA = "";

    private boolean APP_PROCESS = false;

    private String KAKAO_EVENT_RESULT = null;

    // KakaoEventActivity에서만 사용
    private String KAKAO_EVENT_RESULT2 = null;
    private String KAKAO_EVENT_SENDNO = "";
    private String KAKAO_EVENT_DEVICEID = "";
    private String KAKAO_EVENT_CODE = "";

    private String titleLogoURL = "";

    private String imageRequestURL = "";

    private String customerLogoURL = "";

    private String customerName = "";

    private String titleBackground = "";

    private String FF_UUSERID = "";

    private boolean IS_FACEBOOK = false;

    private String SDK_PAYMENET_SCHEME = "";

    private boolean IS_OVER_LENTH_CARD_MSG = false;

    private boolean IS_MAKE_RUNNING = false;

    /**
     * 인터페이스 서버 구분 [true : 운영서버, false : 운영서버 아님.]
     */
    private boolean IS_REAL = true;

    private boolean IS_DEVELOP_VERSION = false;

    private boolean IS_CS_VERSION = false;

    //디자인너 아이디
//	static String DESIGN_ID = "";

    /**
     * 로컬 저장 PATH
     **/
    private String THUMB_PATH = "";
    private String PROJECT_FILE_PATH = "";
    private String EFFECT_APPLIED_IMG_SAVE_PATH = "";

    private String UI_MENU_JSON_PATH = "";

    private String CHANNEL_CODE = "KOR0031";
    private String PC_CHANNEL_CODE = "KOR0000";

    /**
     * 로깅여부
     */
    private boolean DEBUG_LOGG = false;
    private boolean FILE_LOGG = false;

    /**
     * app version정보
     */
    private String APP_VERSION = "";
    /**
     * 테스트 주소정보 사용여부
     */
    private boolean IS_TEMPADDR = false;

    private String currentClassName = "";

    private String SHOW_GRADE_BUTTON = "true";

    private int DEVICE_MAX_BITMAP_SIZE = 0;

    //웹뷰를 강제로 리로드하기 위한 꼼수
    private boolean isNeedWebViewRefresh = false;

    private boolean useDrawSmartSnapsSearchArea = false;

    private boolean useDrawUndefinedFontSearchArea = false;

    private int orgImageAutoSelectCount = 0;
    private static final int DEFAULT_ORG_IMAGE_AUTO_SELECT_COUNT = 23;

    public static String AUTO_SELECT_DIRECTORY_NAME = "";//"testOripq";

    private boolean isWQHDResolutionDevice = false;

    private String orderExceptionDesc = "";

    private boolean isAliveRecommendBookActivity = false; //templateCode로 구분해야 하는데, 2.5에서 추가되면서 구분 하기가 애매해졌다.
    private static String sBackendAPIBase;
    private static String sFrontendAPIBase;

    private SnapsConfigManager() {
    }

    public static void createInstance() {
        if (gInstance == null) {
            synchronized (SnapsConfigManager.class) {
                if (gInstance == null) {
                    gInstance = new SnapsConfigManager();
                }
            }
        }
    }

    public static SnapsConfigManager getInstance() {
        if (gInstance == null) {
            createInstance();
        }
        return gInstance;
    }

    public static void finalizeInstance() {
        if (gInstance != null) {
            gInstance.cleanConfigInfo();
            gInstance = null;
        }
    }

    public void cleanConfigInfo() {
        setAPP_PROCESS(false);
        setAPP_VERSION("");
        setCHANNEL_CODE("");
        setCustomerLogoURL("");
        setCustomerName("");
        setDEBUG_LOGG(false);
        setEFFECT_APPLIED_IMG_SAVE_PATH("");
        setFF_UUSERID("");
        setFILE_LOGG(false);
        setImageRequestURL("");
        setIS_BETWEEN_VERSION(false);
        setIS_COMPLETE(false);
        setIS_FACEBOOK(false);
        setIS_MAKE_RUNNING(false);
        setIS_OVER_LENTH_CARD_MSG(false);
        setIS_QA(false);
        setIS_REAL(true);
        setIS_SDK(false);
        setIS_TEMPADDR(false);
        setIS_USE_INDEX_V3(false);
        setIsEdit(false);
        setKAKAO_EVENT_CODE("");
        setKAKAO_EVENT_DEVICEID("");
        setKAKAO_EVENT_RESULT("");
        setKAKAO_EVENT_RESULT2("");
        setKAKAO_EVENT_SENDNO("");
        setKAKAO_SCHEMA("");
        setPC_CHANNEL_CODE("");
//        setPROJECT_FILE_PATH("");
        setSDK_PAYMENET_SCHEME("");
        setStrGroupURL("");
        setStrPageKey("");
//        setTHUMB_PATH("");
        setTitleBackground("");
        setTitleLogoURL("");
        setUI_MENU_JSON_PATH("");
        setCurrentClassName("");
        setSHOW_GRADE_BUTTON("true");
        setDeviceMaxBitmapSize(0);
        setOrgImageAutoSelectCount(0);
        setUseDrawSmartSnapsSearchArea(false);
        setUseDrawUndefinedFontSearchArea(false);
        setOrderExceptionDesc("");
        setWQHDResolutionDevice(false);
    }

    public boolean isUseDrawUndefinedFontSearchArea() {
        return useDrawUndefinedFontSearchArea;
    }

    public void setUseDrawUndefinedFontSearchArea(boolean useDrawUndefinedFontSearchArea) {
        this.useDrawUndefinedFontSearchArea = useDrawUndefinedFontSearchArea;
    }

    public boolean isUseDrawSmartSnapsSearchArea() {
        return useDrawSmartSnapsSearchArea;
    }

    public void setUseDrawSmartSnapsSearchArea(boolean useDrawSmartSnapsSearchArea) {
        this.useDrawSmartSnapsSearchArea = useDrawSmartSnapsSearchArea;
    }

    public int getOrgImageAutoSelectCount() {
        if (isAutoLaunchProductMakingMode()) {
            return DEFAULT_ORG_IMAGE_AUTO_SELECT_COUNT;
        }
        return orgImageAutoSelectCount;
    }

    public void setOrgImageAutoSelectCount(int orgImageAutoSelectCount) {
        this.orgImageAutoSelectCount = orgImageAutoSelectCount;
    }

    public void setDeviceMaxBitmapSize(int size) {
        DEVICE_MAX_BITMAP_SIZE = size;
    }

    public int getDeviceMaxBitmapSize() {
        return DEVICE_MAX_BITMAP_SIZE;
    }

    public boolean IS_COMPLETE() {
        return IS_COMPLETE;
    }

    public void setIS_COMPLETE(boolean IS_COMPLETE) {
        this.IS_COMPLETE = IS_COMPLETE;
    }

    public boolean IS_SDK() {
        return IS_SDK;
    }

    public void setIS_SDK(boolean IS_SDK) {
        this.IS_SDK = IS_SDK;
    }

    public boolean isWQHDResolutionDevice() {
        return isWQHDResolutionDevice;
    }

    public void setWQHDResolutionDevice(boolean isWQHDResolutionDevice) {
        this.isWQHDResolutionDevice = isWQHDResolutionDevice;
    }

    public boolean IS_BETWEEN_VERSION() {
        return IS_BETWEEN_VERSION;
    }

    public void setIS_BETWEEN_VERSION(boolean IS_BETWEEN_VERSION) {
        this.IS_BETWEEN_VERSION = IS_BETWEEN_VERSION;
    }

    public String getOrderExceptionDesc() {
        return orderExceptionDesc;
    }

    public void setOrderExceptionDesc(String orderExceptionDesc) {
        this.orderExceptionDesc = orderExceptionDesc;
    }

    public String getStrPageKey() {
        return strPageKey;
    }

    public void setStrPageKey(String strPageKey) {
        this.strPageKey = strPageKey;
    }

    public String getStrGroupURL() {
        return strGroupURL;
    }

    public void setStrGroupURL(String strGroupURL) {
        this.strGroupURL = strGroupURL;
    }

    public boolean IS_QA() {
        return IS_QA;
    }

    public void setIS_QA(boolean IS_QA) {
        this.IS_QA = IS_QA;
    }

    public boolean isEdit() {
        return isEdit;
    }

    public void setIsEdit(boolean isEdit) {
        this.isEdit = isEdit;
    }

    public boolean IS_USE_INDEX_V3() {
        return IS_USE_INDEX_V3;
    }

    public void setIS_USE_INDEX_V3(boolean IS_USE_INDEX_V3) {
        this.IS_USE_INDEX_V3 = IS_USE_INDEX_V3;
    }

    public String getKAKAO_SCHEMA() {
        return KAKAO_SCHEMA;
    }

    public void setKAKAO_SCHEMA(String KAKAO_SCHEMA) {
        this.KAKAO_SCHEMA = KAKAO_SCHEMA;
    }

    public boolean isAPP_PROCESS() {
        return APP_PROCESS;
    }

    public void setAPP_PROCESS(boolean APP_PROCESS) {
        this.APP_PROCESS = APP_PROCESS;
    }

    public boolean isDEVELOP_VERSION() {
        return IS_DEVELOP_VERSION;
    }

    public SnapsConfigManager setDEVELOP_VERSION(boolean IS_DEVELOP_VERSION) {
        this.IS_DEVELOP_VERSION = IS_DEVELOP_VERSION;
        return this;
    }

    public String getKAKAO_EVENT_RESULT() {
        return KAKAO_EVENT_RESULT;
    }

    public void setKAKAO_EVENT_RESULT(String KAKAO_EVENT_RESULT) {
        this.KAKAO_EVENT_RESULT = KAKAO_EVENT_RESULT;
    }

    public String getKAKAO_EVENT_RESULT2() {
        return KAKAO_EVENT_RESULT2;
    }

    public void setKAKAO_EVENT_RESULT2(String KAKAO_EVENT_RESULT2) {
        this.KAKAO_EVENT_RESULT2 = KAKAO_EVENT_RESULT2;
    }

    public String getKAKAO_EVENT_SENDNO() {
        return KAKAO_EVENT_SENDNO;
    }

    public void setKAKAO_EVENT_SENDNO(String KAKAO_EVENT_SENDNO) {
        this.KAKAO_EVENT_SENDNO = KAKAO_EVENT_SENDNO;
    }

    public String getKAKAO_EVENT_DEVICEID() {
        return KAKAO_EVENT_DEVICEID;
    }

    public void setKAKAO_EVENT_DEVICEID(String KAKAO_EVENT_DEVICEID) {
        this.KAKAO_EVENT_DEVICEID = KAKAO_EVENT_DEVICEID;
    }

    public String getKAKAO_EVENT_CODE() {
        return KAKAO_EVENT_CODE;
    }

    public void setKAKAO_EVENT_CODE(String KAKAO_EVENT_CODE) {
        this.KAKAO_EVENT_CODE = KAKAO_EVENT_CODE;
    }

    public String getTitleLogoURL() {
        return titleLogoURL;
    }

    public void setTitleLogoURL(String titleLogoURL) {
        this.titleLogoURL = titleLogoURL;
    }

    public String getImageRequestURL() {
        return imageRequestURL;
    }

    public void setImageRequestURL(String imageRequestURL) {
        this.imageRequestURL = imageRequestURL;
    }

    public String getCustomerLogoURL() {
        return customerLogoURL;
    }

    public void setCustomerLogoURL(String customerLogoURL) {
        this.customerLogoURL = customerLogoURL;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getTitleBackground() {
        return titleBackground;
    }

    public void setTitleBackground(String titleBackground) {
        this.titleBackground = titleBackground;
    }

    public String getFF_UUSERID() {
        return FF_UUSERID;
    }

    public void setFF_UUSERID(String FF_UUSERID) {
        this.FF_UUSERID = FF_UUSERID;
    }

    public boolean IS_FACEBOOK() {
        return IS_FACEBOOK;
    }

    public void setIS_FACEBOOK(boolean IS_FACEBOOK) {
        this.IS_FACEBOOK = IS_FACEBOOK;
    }

    public String getSDK_PAYMENET_SCHEME() {
        return SDK_PAYMENET_SCHEME;
    }

    public void setSDK_PAYMENET_SCHEME(String SDK_PAYMENET_SCHEME) {
        this.SDK_PAYMENET_SCHEME = SDK_PAYMENET_SCHEME;
    }

    public boolean IS_OVER_LENTH_CARD_MSG() {
        return IS_OVER_LENTH_CARD_MSG;
    }

    public void setIS_OVER_LENTH_CARD_MSG(boolean IS_OVER_LENTH_CARD_MSG) {
        this.IS_OVER_LENTH_CARD_MSG = IS_OVER_LENTH_CARD_MSG;
    }

    public boolean IS_MAKE_RUNNING() {
        return IS_MAKE_RUNNING;
    }

    public void setIS_MAKE_RUNNING(boolean IS_MAKE_RUNNING) {
        this.IS_MAKE_RUNNING = IS_MAKE_RUNNING;
    }

    public boolean IS_REAL() {
        return IS_REAL;
    }

    public void setIS_REAL(boolean IS_REAL) {
        this.IS_REAL = IS_REAL;
    }

    public String getEFFECT_APPLIED_IMG_SAVE_PATH() {
        return EFFECT_APPLIED_IMG_SAVE_PATH;
    }

    public void setEFFECT_APPLIED_IMG_SAVE_PATH(String EFFECT_APPLIED_IMG_SAVE_PATH) {
        this.EFFECT_APPLIED_IMG_SAVE_PATH = EFFECT_APPLIED_IMG_SAVE_PATH;
    }

    public String getUI_MENU_JSON_PATH() {
        return UI_MENU_JSON_PATH;
    }

    public void setUI_MENU_JSON_PATH(String UI_MENU_JSON_PATH) {
        this.UI_MENU_JSON_PATH = UI_MENU_JSON_PATH;
    }

    public String getCHANNEL_CODE() {
        return CHANNEL_CODE;
    }

    public void setCHANNEL_CODE(String CHANNEL_CODE) {
        this.CHANNEL_CODE = CHANNEL_CODE;
    }

    public String getPC_CHANNEL_CODE() {
        return PC_CHANNEL_CODE;
    }

    public void setPC_CHANNEL_CODE(String PC_CHANNEL_CODE) {
        this.PC_CHANNEL_CODE = PC_CHANNEL_CODE;
    }

    public boolean isDEBUG_LOGG() {
        return DEBUG_LOGG;
    }

    public void setDEBUG_LOGG(boolean DEBUG_LOGG) {
        this.DEBUG_LOGG = DEBUG_LOGG;
    }

    public boolean isFILE_LOGG() {
        return FILE_LOGG;
    }

    public void setFILE_LOGG(boolean FILE_LOGG) {
        this.FILE_LOGG = FILE_LOGG;
    }

    public String getAPP_VERSION() {
        return APP_VERSION;
    }

    public void setAPP_VERSION(String APP_VERSION) {
        this.APP_VERSION = APP_VERSION;
    }

    public boolean IS_TEMPADDR() {
        return IS_TEMPADDR;
    }

    public void setIS_TEMPADDR(boolean IS_TEMPADDR) {
        this.IS_TEMPADDR = IS_TEMPADDR;
    }

    public String getCurrentClassName() {
        return currentClassName;
    }

    public void setCurrentClassName(String currentClassName) {
        this.currentClassName = currentClassName;
    }

    public String getSHOW_GRADE_BUTTON() {
        return SHOW_GRADE_BUTTON;
    }

    public void setSHOW_GRADE_BUTTON(String SHOW_GRADE_BUTTON) {
        this.SHOW_GRADE_BUTTON = SHOW_GRADE_BUTTON;
    }

    public boolean isNeedWebViewRefresh() {
        return isNeedWebViewRefresh;
    }

    public void setIsNeedWebViewRefresh(boolean isNeedWebViewRefresh) {
        this.isNeedWebViewRefresh = isNeedWebViewRefresh;
    }

    public boolean isAliveRecommendBookActivity() {
        return isAliveRecommendBookActivity;
    }

    public void setAliveRecommendBookActivity(boolean aliveRecommendBookActivity) {
        isAliveRecommendBookActivity = aliveRecommendBookActivity;
    }

    public static boolean isAutoLaunchProductMakingMode() {
        if (!Config.isDevelopVersion()) {
            return false;
        }

        Context context = ContextUtil.getContext();
        if (context == null) {
            return false;
        }

        String developUrl = Setting.getString(context, KEY_DEVELOP_URL_PATH);
        return !StringUtil.isEmpty(developUrl);
    }

    public void setBackendAPIBase(String backendAPIBase) {
        sBackendAPIBase = backendAPIBase;
    }

    public String getBackendAPIBase() {
        return sBackendAPIBase;
    }

    public void setFrontendAPIBase(String frontendAPIBase) {
        sFrontendAPIBase = frontendAPIBase;
    }

    public String getFrontendAPIBase() {
        return sFrontendAPIBase;
    }
}
