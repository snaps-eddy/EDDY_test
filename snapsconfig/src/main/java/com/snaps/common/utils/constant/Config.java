package com.snaps.common.utils.constant;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.snaps.common.data.between.AccessData;
import com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.log.SnapsInterfaceLogListener;
import com.snaps.common.utils.net.http.HttpUtil;
import com.snaps.common.utils.net.xml.XmlResult;
import com.snaps.common.utils.pref.Setting;
import com.snaps.common.utils.ui.ContextUtil;
import com.snaps.common.utils.ui.StringUtil;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import static com.snaps.common.utils.constant.Const_PRODUCT.PRODUCT_SMART_SNAPS_ANALYSIS_PHOTO_BOOK_ETC;


/**
 * 설정에 관련된 변수는 SnapsConfigManager
 * 제품 제작에 관련된 변수는 SnapsProductInfoManager 에 정의해 주세요.
 */
public abstract class Config implements ISnapsConfigConstants {
    private static final String TAG = Config.class.getSimpleName();

    public static File getExternalCacheDir(Context context) {
        if (context == null) return null;
        File cacheDir = context.getExternalCacheDir();
        return switchExternalFileDirIfDevelopVersion(cacheDir);
    }

    public static File getExternalFilesDir(Context context, String path) {
        if (context == null) return null;
        File cacheDir = context.getExternalFilesDir(path);
        return switchExternalFileDirIfDevelopVersion(cacheDir);
    }

    private static File switchExternalFileDirIfDevelopVersion(File cacheDir) {
        if (!isDevelopVersion() || cacheDir == null) return cacheDir;
        String absolutePath = cacheDir.getAbsolutePath();
        absolutePath = absolutePath.replace("snaps.mobile.kr/", "snaps.mobile.kr.develop/");
        return new File(absolutePath);
    }

    public static boolean isActiveImageAutoSelectFunction() {
        return (isDevelopVersion() || !isRealServer()) && getOrgImageAutoSelectCount() > 0;
    }

    public static void setOrderExceptionDesc(String exceptionDesc) {
        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        configManager.setOrderExceptionDesc(exceptionDesc);
    }

    public static String getOrderExceptionDesc() {
        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        return configManager.getOrderExceptionDesc();
    }

    public static void setUseDrawUndefinedFontSearchArea(boolean use) {
        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        configManager.setUseDrawUndefinedFontSearchArea(use);
    }

    public static boolean isUseDrawUndefinedFontSearchArea() {
        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        return configManager.isUseDrawUndefinedFontSearchArea();
    }

    public static boolean useDrawUndefinedFontSearchArea() {
        if (!isDevelopVersion() && isRealServer()) return false;
        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        return configManager.isUseDrawUndefinedFontSearchArea();
    }

    public static void setUseDrawSmartSnapsSearchArea(boolean use) {
        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        configManager.setUseDrawSmartSnapsSearchArea(use);
    }

    public static boolean isUseDrawSmartSnapsSearchArea() {
        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        return configManager.isUseDrawSmartSnapsSearchArea();
    }

    public static boolean useDrawSmartSnapsImageArea() {
        if (!isDevelopVersion() && isRealServer()) return false;
        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        return configManager.isUseDrawSmartSnapsSearchArea();
    }

    public static void setOrgImageAutoSelectCount(int count) {
        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        configManager.setOrgImageAutoSelectCount(count);
    }

    public static int getOrgImageAutoSelectCount() {
        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        return configManager.getOrgImageAutoSelectCount();
    }

    public static void setNeedWebViewRefresh(boolean flag) {
        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        configManager.setIsNeedWebViewRefresh(flag);
    }

    public static boolean isNeedWebViewRefresh() {
        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        return configManager.isNeedWebViewRefresh();
    }

    public static void useV3Index(boolean flag) {
        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        configManager.setIS_USE_INDEX_V3(flag);
    }

    public static boolean isV3Index() {
        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        return configManager.IS_USE_INDEX_V3();
    }

    public static boolean isIS_QA() {
        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        return configManager.IS_QA();
    }

    public static boolean isRealServer() {
        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        return configManager.IS_REAL();
    }

    public static void setRealServer(boolean flag) {
        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        configManager.setIS_REAL(flag);
    }

    public static void setDeviceMaxBitmapSize(int size) {
        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        configManager.setDeviceMaxBitmapSize(size);
    }

    public static int getDeviceMaxBitmapSize() {
        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        return configManager.getDeviceMaxBitmapSize();
    }

    /***
     * QA서버 설정.. true이면 QA서버로 설정이됨..
     *
     * @param iS_QA
     */
    public static void setIS_QA(boolean iS_QA) {
        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        configManager.setIS_QA(iS_QA);
    }

    public static void setSnapsSDKAlbumPageyKey(String pageKey, Context context) {
        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        configManager.setStrPageKey(pageKey);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("SNAPS_SDK_PAGE_KEY", pageKey);

        editor.commit();
    }

    public static void setComplete(boolean bComplete, Context context) {

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("SNAPS_COMPLETE", bComplete);
        editor.commit();

    }

    public static boolean isComplete(Context context) {

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);

        boolean ret = false;
        ret = pref.getBoolean("SNAPS_COMPLETE", ret);

        return ret;
    }

    public static void setSnapsSDKAlbumGroup(Context context, String url) {
        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        configManager.setStrGroupURL(url);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("SNAPS_SDK_GROUP", url);

        editor.commit();
    }

    public static String getSnapsSDKAlbumGroup(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);

        String ret = "";
        ret = pref.getString("SNAPS_SDK_GROUP", ret);

        return ret;
    }

    public static void setSnapsSDKAccessToken(Context context, String token) {

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("SNAPS_SDK_ACCESS_TOKEN", token);

        editor.commit();
    }

    public static String getSnapsSDKAccessToken(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);

        String ret = "";
        ret = pref.getString("SNAPS_SDK_ACCESS_TOKEN", ret);

        return ret;
    }

    @SuppressLint("NewApi")
    public static boolean isSnapsSDKAlbumGroup(Context context) {

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);

        String ret = "";
        ret = pref.getString("SNAPS_SDK_GROUP", ret);

        if (ret.isEmpty())
            return false;
        else
            return true;
    }

    public static String getSnapsSDKAlbumPageyKey(Context context) {

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);

        String ret = "";
        ret = pref.getString("SNAPS_SDK_PAGE_KEY", ret);

        return ret;
    }

    public static void useSnapsSDK(boolean isSDK) {
        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        configManager.setIS_SDK(isSDK);
    }

    public static void useSnapsSDK2(boolean isSDK, Context context) {
        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        configManager.setIS_SDK(isSDK);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("SNAPS_SDK", isSDK);

        editor.commit();

        if (isSDK) {
            configManager.setAPP_VERSION("SnapsSDK1.0");
            Setting.set(context, Const_VALUE.APPVERSION, configManager.getAPP_VERSION());

        }
    }

    public static boolean isSnapsSDK() {
        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        return configManager.IS_SDK();
    }

    public static boolean isSnapsSDK2(Context context) {

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);

        boolean ret = false;
        ret = pref.getBoolean("SNAPS_SDK", false);
        return ret;
    }

    public static void useSnapsBetween(boolean isBetween) {
        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        configManager.setIS_BETWEEN_VERSION(isBetween);
    }

    public static void useSnapsBetween(boolean isBetween, Context context) {
        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        configManager.setIS_BETWEEN_VERSION(isBetween);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("SNAPS_BETWEEN", isBetween);

        editor.commit();

        if (isBetween) {
            configManager.setAPP_VERSION("SnapsBetween1.0");
            Setting.set(context, Const_VALUE.APPVERSION, configManager.getAPP_VERSION());

        }
    }

    public static void setBetweenAuthInfo(Context context, AccessData accessData) {
        if (accessData == null)
            return;

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("SNAPS_BETWEEN_ACCESS_TOKEN", accessData.getAccessToken());
        editor.putString("SNAPS_BETWEEN_EXP", accessData.getExpiresIn());
        editor.putString("SNAPS_BETWEEN_SCOPE", accessData.getScope());
        editor.putString("SNAPS_BETWEEN_USER_ID", accessData.getUserId());
        editor.putString("SNAPS_BETWEEN_REL_ID", accessData.getRelationshipId());
        editor.putString("SNAPS_BETWEEN_REFRESH_TOKEN", accessData.getRefreshToken());
        editor.putString("SNAPS_BETWEEN_HASH", accessData.getHashCode());
        editor.putString("SNAPS_BETWEEN_S_TOKEN", accessData.getSnapsToken());
        editor.commit();
    }

    public static AccessData getBetweenAuthInfo(Context context) {

        AccessData accessData = new AccessData();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);

        String token = pref.getString("SNAPS_BETWEEN_ACCESS_TOKEN", null);
        String exp = pref.getString("SNAPS_BETWEEN_EXP", null);
        String scope = pref.getString("SNAPS_BETWEEN_SCOPE", null);
        String userId = pref.getString("SNAPS_BETWEEN_USER_ID", null);
        String RelId = pref.getString("SNAPS_BETWEEN_REL_ID", null);
        String refreshToken = pref.getString("SNAPS_BETWEEN_REFRESH_TOKEN", null);
        String hash = pref.getString("SNAPS_BETWEEN_HASH", null);
        String sToken = pref.getString("SNAPS_BETWEEN_S_TOKEN", null);

        if (token == null)
            return null;

        accessData.setAccessToken(token);
        accessData.setExpiresIn(exp);
        accessData.setScope(scope);
        accessData.setUserId(userId);
        accessData.setRelationshipId(RelId);
        accessData.setRefreshToken(refreshToken);
        accessData.setHashCode(hash);
        accessData.setSnapsToken(sToken);

        return accessData;
    }

    public static boolean isValidBetweenAuthLastRefreshedTime(Context context) {

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);

        long savedTime = pref.getLong("SNAPS_BETWEEN_AUTH_REF_TIME", System.currentTimeMillis());

        return System.currentTimeMillis() - savedTime < (60000 * 60 * 24) * 3; // 갱신한지 3일 이내인지
    }

    public static void setBetweenAuthLastRefreshedTime(Context context, long time) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putLong("SNAPS_BETWEEN_AUTH_REF_TIME", time);

        editor.commit();
    }

    public static boolean isSnapsBitween() {
        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        return configManager.IS_BETWEEN_VERSION();
    }

    public static boolean isSnapsBitween(Context context) {

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);

        boolean ret = false;
        ret = pref.getBoolean("SNAPS_BETWEEN", false);
        return ret;
    }

    public static void setSnapsBetweenRegisterUser(Context context, boolean register) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("SNAPS_BETWEEN_REGISTER", register);

        editor.commit();
    }

    public static boolean isSnapsBetweenRegisterUser(Context context) {

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);

        boolean ret = false;
        ret = pref.getBoolean("SNAPS_BETWEEN_REGISTER", false);
        return ret;
    }

    public static void setUUserID(String ID) {
        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        configManager.setFF_UUSERID(ID);
    }

    public static String getUUserID(Context context) {

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);

        String ret = "";
        ret = pref.getString("SNAPS_UUSERID", ret);
        return ret;
    }

    public static void setUUserID2(String ID, Context context) {
        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        configManager.setFF_UUSERID(ID);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("SNAPS_UUSERID", ID);

        editor.commit();
    }

    public static void setTitleLogoURL(String url) {
        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        configManager.setTitleLogoURL(url);
    }

    public static String getTitleLogURL() {
        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        return configManager.getTitleLogoURL();
    }

    public static String getTitleBackground() {
        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        return configManager.getTitleBackground();
    }

    public static String getCustomerLogoURL() {
        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        return configManager.getCustomerLogoURL();
    }

    public static String getCustomerName() {
        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        return configManager.getCustomerName();
    }

    public static void setSDKAlbumRequestURL(String url) {
        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        configManager.setImageRequestURL(url);
    }

    public static void setCustomerLogoURL(String url) {
        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        configManager.setCustomerLogoURL(url);
    }

    public static void setCustomerName(String name) {
        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        configManager.setCustomerName(name);
    }

    public static boolean isFacebookService() {
        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        return configManager.IS_FACEBOOK();
    }

    public static void setPaymentScheme(String scheme, Context context) {
        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        configManager.setSDK_PAYMENET_SCHEME(scheme);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("SNAPS_SDK_PAYMENT_SCHEME", scheme);

        editor.commit();
    }

    public static String getPaymentScheme(Context context) {

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);

        String ret = "";
        ret = pref.getString("SNAPS_SDK_PAYMENT_SCHEME", ret);

        return ret;
    }

    public static void useFacebookService(boolean useFacebook) {
        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        configManager.setIS_FACEBOOK(useFacebook);
    }

    public static void loadManagerXML(String url, SnapsInterfaceLogListener interfaceLogListener) {

        try {
            String result = HttpUtil.connectGet(url, interfaceLogListener);

            XmlResult xmlResult = new XmlResult(result);

            SnapsConfigManager configManager = SnapsConfigManager.getInstance();
            configManager.setCustomerName(xmlResult.get("customer"));
            configManager.setTitleLogoURL(xmlResult.get("titlelogo"));
            configManager.setCustomerLogoURL(xmlResult.get("servicelogo"));
            configManager.setTitleBackground(xmlResult.get("titlebackground"));

            xmlResult.close();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    /**
     * @param context
     * @param isRealServer
     * @param backendAPIBase
     * @param frontendAPIBase
     */
    public static void initServer(Context context, boolean isRealServer, String backendAPIBase, String frontendAPIBase) {
        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        configManager.setIS_REAL(isRealServer);
        configManager.setBackendAPIBase(backendAPIBase);
        configManager.setFrontendAPIBase(frontendAPIBase);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();

        editor.putBoolean("SNAPS_SERVER", isRealServer);

        editor.apply();
    }

    public static boolean isRealServer(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getBoolean("SNAPS_SERVER", false);
    }

    public static void initLog(boolean isLogging) {
        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        configManager.setDEBUG_LOGG(isLogging);
    }

    public static void initAppVersion(String appVersion) {
        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        configManager.setAPP_VERSION(appVersion);
    }

    public static void initTempAddr(boolean tempAddr) {
        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        configManager.setIS_TEMPADDR(tempAddr);
    }

    /**
     * 초기 채널 설정.
     *
     * @param code
     */
    public static void initConfig(final String code) {
        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        configManager.setCHANNEL_CODE(code);
    }

    public static String getCHANNEL_CODE() {
        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        return configManager.getCHANNEL_CODE();
    }

    public static String getPC_CHANNEL_CODE() {
        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        return configManager.getPC_CHANNEL_CODE();
    }

    /**
     * 현재 선택된 prodcode가 파라미터로 전달된 코드와 같은지 확인
     *
     * @param prodCode
     * @return
     */
    public static boolean isProdCode(String prodCode) {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        String pdCode = productInfoManager.getPROD_CODE();
        return pdCode != null && pdCode.equalsIgnoreCase(prodCode);
    }

    public static boolean isOldStyleCmdKey(String url) {
        return url != null && url.startsWith("snapsapp://cmd");
    }

    /**
     * 현재 선택된 prodcode가 파라미터로 전달된 코드와 같은지 확인
     *
     * @param url
     * @return
     */

    public static HashMap<String, String> ExtractWebURL(String url) {
        if (url == null) return null;

        String SCHMA = "snapsapp://";

        HashMap<String, String> hashmap = new HashMap<String, String>();

        if (url.startsWith(SCHMA)) {

            String params = "";

            //snapsapp://scheme?body 형식으로 변경되어서 추가한 코드
            if (isOldStyleCmdKey(url)) {
                params = url.substring(SCHMA.length(), url.length());
            } else {
                //신형식..
                if (url.contains("?")) {
                    params = url.substring(url.indexOf("?") + 1, url.length());
                } else {
                    params = url.substring(SCHMA.length(), url.length());
                }
            }

            params = params.replace("?", "&");
            String[] arParams1 = params.split("&");

            for (String find : arParams1) {
                String[] jsonparam = find.split("=");
                String key = null, value = null;
                if (jsonparam != null && jsonparam.length > 1) {
                    key = jsonparam[0];
                    value = jsonparam[1];
                }

                if (key != null && key.length() > 0 && value != null && value.length() > 0)
                    hashmap.put(key, value);
            }
        }
        return hashmap;
    }


    /***
     * 단순히 & = 를 가지고 값을 파싱하는 함수
     * @param url
     * @return
     */
    public static HashMap<String, String> ExtractPushWebURL(String url) {
        if (url == null) return null;

        String SCHMA = "snapsapp://";

        HashMap<String, String> hashmap = new HashMap<String, String>();

        if (url.startsWith(SCHMA)) {

            String params = url.substring(SCHMA.length(), url.length());
            url.replace("?", "&");

            String[] arParams1 = params.split("&");

            for (String find : arParams1) {
                String[] jsonparam = find.split("=");
                String key = null, value = null;
                if (jsonparam != null && jsonparam.length > 1) {
                    key = jsonparam[0];
                    value = jsonparam[1];
                }

                if (key != null && key.length() > 0 && value != null && value.length() > 0)
                    hashmap.put(key, value);
            }
        }
        return hashmap;
    }

//    public static HashMap<String, String> parseUrl(String url) {
//        HashMap<String, String> hashmap = new HashMap<String, String>();
//
//        Uri uri = Uri.parse(url);
//
//        if (uri == null)
//            return hashmap;
//
//        String scheme = uri.getScheme(); // ex) "snapsapp://"
//
//        hashmap.put("scheme", scheme);
//
//        String params = uri.getQuery();
//
//        if (params == null)
//            return hashmap;
//
//        String[] arParams1 = params.split("&");
//
//        for (String find : arParams1) {
//            String[] jsonparam = find.split("=");
//
//            if (jsonparam != null && jsonparam.length > 0) {
//                if (jsonparam[0].equalsIgnoreCase("fullurl")) {
//                    String value = find.substring(find.indexOf(jsonparam[0]) + jsonparam[0].length() + 1);
//                    hashmap.put(jsonparam[0], value);
//                    continue;
//                }
//
//                if (jsonparam.length == 2)
//                    hashmap.put(jsonparam[0], jsonparam[1]);
//                else if (jsonparam.length == 1)
//                    hashmap.put(jsonparam[0], "");
//            }
//        }
//
//        return hashmap;
//    }

    /**
     * 현재 상품 사진인 유무
     *
     * @return
     */
    public static boolean isSnapsPhotoPrint() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        String pdCode = productInfoManager.getPROD_CODE();
        if (pdCode == null) return false;

        for (String s : PRODUCT_PHOTOPRINT_PRODCODE) {
            if (s.equalsIgnoreCase(pdCode))
                return true;
        }

        return false;
    }

    /***
     * 언어 체크
     * @return
     */
    public static boolean useKorean() {
        return getCHANNEL_CODE().startsWith("KOR");
    }

    public static boolean useEnglish() {
        return getCHANNEL_CODE().startsWith("ENG");
    }

    public static boolean useJapanese() {
        return getCHANNEL_CODE().startsWith("JPN");
    }

    public static boolean useChinese() {
        return getCHANNEL_CODE().startsWith("CHN");
    }

    /***
     * 현재 선택이 사진인화이고 KR버젼인경우...
     *
     * @return
     */
    public static boolean isSnapsPhotoPrint_kr() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        String pdCode = productInfoManager.getPROD_CODE();

        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        if (configManager.getCHANNEL_CODE() != null && configManager.getCHANNEL_CODE().equalsIgnoreCase(Config.CHANNEL_SNAPS_KOR) && Config.PRODUCT_PHOTOPRINT.equalsIgnoreCase(pdCode))
            return true;

        return false;
    }

    public static boolean isSnapsPhotoPrint(String prodCode) {
        for (String s : PRODUCT_PHOTOPRINT_PRODCODE) {
            if (s.equalsIgnoreCase(prodCode))
                return true;
        }

        return false;
    }

    public static boolean isIdentifyPhotoPrint() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        String pdCode = productInfoManager.getPROD_CODE();

        for (String s : PRODUCT_IDENTIFY_PHOTOPRINT_PRODCODE) {
            if (s.equalsIgnoreCase(pdCode))
                return true;
        }

        return false;
    }

    public static boolean isIdentifyPhotoPrint(String prodCode) {
        for (String s : PRODUCT_IDENTIFY_PHOTOPRINT_PRODCODE) {
            if (s.equalsIgnoreCase(prodCode))
                return true;
        }

        return false;
    }

    public static int getIdentifyPhotoCount() {
        return getTMPL_CODE() != null && getTMPL_CODE().equalsIgnoreCase(ISnapsConfigConstants.IDENTIFY_PHOTO_TEMPLATE_CODE_NORMAL) ? 3 : 8;
    }

    public static boolean isPassportPhoto() {
        return isIdentifyPhotoPrint() && getTMPL_CODE() != null && getTMPL_CODE().equalsIgnoreCase(ISnapsConfigConstants.IDENTIFY_PHOTO_TEMPLATE_CODE_PASSPORT);
    }

    public static boolean shouldShowPassportImageEditGuide() {
        return Config.useKorean() && isPassportPhoto();
    }

    /***
     * 플러스 버튼을 추가해야할지 판단하는 함수.
     *
     * @return
     */
    public static boolean isCheckPlusButton() {
        // 심플포톱북만 추가를 한다.
        if (isSimplePhotoBook() || isSimpleMakingBook())
            return true;
        return false;
    }

    /***
     * Config에 저장된 ProductCode을 가지고 테마북인지 판단하는 함수.
     *
     * @return
     */
    public static boolean isThemeBook() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        String pdCode = productInfoManager.getPROD_CODE();
        if (pdCode == null) return false;
        if (Config.PRODUCT_THEMEBOOK_A5.equalsIgnoreCase(pdCode))
            return true;

        if (Config.PRODUCT_THEMEBOOK_A6.equalsIgnoreCase(pdCode))
            return true;

        if (Config.PRODUCT_THEMEBOOK_HARD.equalsIgnoreCase(pdCode))
            return true;

        return false;
    }

    public static boolean isFanBook() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        String pdCode = productInfoManager.getPROD_CODE();

        if (pdCode != null && pdCode.startsWith(PRODUCT_FANBOOK_GROUP))
            return true;
        return false;
    }

    public static boolean isWoodBlockCalendar() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        String pdCode = productInfoManager.getPROD_CODE();
        return isWoodBlockCalendar(pdCode);
    }

    public static boolean isWoodBlockCalendar(String productCode) {
        if (productCode.equalsIgnoreCase(Const_ThumbNail.PRODUCT_WOOD_BLOCK_CALENDAR))
            return true;

        return false;
    }

    public static boolean isCalendar() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        String pdCode = productInfoManager.getPROD_CODE();
        return isCalendar(pdCode);
    }

    public static boolean isCalendar(String productCode) {
        for (String s : PRODUCT_CALENDARS) {
            if (s.equalsIgnoreCase(productCode))
                return true;
        }

        // 벽걸이, 스케즐러 추가로 추가.
        if (productCode.startsWith(PRODUCT_WALL_CALENDAR) || productCode.startsWith(PRODUCT_SCHEDULE_CALENDAR))
            return true;

        if (productCode.equalsIgnoreCase(Const_ThumbNail.PRODUCT_WOOD_BLOCK_CALENDAR))
            return true;

        return false;
    }

    public static boolean isCalendarMini(String productCode) {

        if (Config.PRODUCT_CALENDAR_MINI.equalsIgnoreCase(productCode))
            return true;

        if (Config.PRODUCT_CALENDAR_VERTICAL.equalsIgnoreCase(productCode))
            return true;

        return false;
    }

    public static boolean isCalendarWide(String productCode) {

        if (Config.PRODUCT_CALENDAR_WIDE.equalsIgnoreCase(productCode))
            return true;

        return false;
    }

    public static boolean isCalendarLarge(String productCode) {

        if (Config.PRODUCT_CALENDAR_LARGE.equalsIgnoreCase(productCode))
            return true;

        return false;
    }

    public static boolean isCalendarNormal(String productCode) {

        if (Config.PRODUCT_CALENDAR.equalsIgnoreCase(productCode))
            return true;

        return false;
    }

    public static boolean isCalendarVert(String productCode) {

        if (Config.PRODUCT_CALENDAR_VERTICAL.equalsIgnoreCase(productCode))
            return true;

        return false;
    }

    public static boolean isCalendarNormalVert(String productCode) {

        if (Config.PRODUCT_CALENDAR_VERTICAL2.equalsIgnoreCase(productCode))
            return true;

        return false;
    }

    public static boolean isCalenderWall(String productCode) {
        // 벽걸이, 스케즐러 추가로 추가.
        if (productCode.startsWith(PRODUCT_WALL_CALENDAR))
            return true;

        return false;
    }

    public static boolean isCalenderSchedule(String productCode) {
        // 스케즐러 추가로 추가.
        if (productCode.startsWith(PRODUCT_SCHEDULE_CALENDAR))
            return true;

        return false;
    }

    // 00800700060002
    // 00800700030003

    /***
     * ProductCode을 가지고 테마북인지 판단하는 함수.
     *
     * @param productCode
     * @return
     */
    public static boolean isThemeBook(String productCode) {
        if (Config.PRODUCT_THEMEBOOK_A5.equalsIgnoreCase(productCode))
            return true;

        if (Config.PRODUCT_THEMEBOOK_A6.equalsIgnoreCase(productCode))
            return true;

        if (Config.PRODUCT_THEMEBOOK_HARD.equalsIgnoreCase(productCode))
            return true;

        return false;
    }

    /**
     * 현재 상품 페북북 유무 Facebook_book: old, facebook_photobook : current
     *
     * @return
     */
    public static boolean isNewKakaoBook() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        String pdCode = productInfoManager.getPROD_CODE();
        if (pdCode == null) return false;

        if (Config.PRODUCT_NEW_KAKAKO_STORYBOOK_HARD.equalsIgnoreCase(pdCode) || Config.PRODUCT_NEW_KAKAKO_STORYBOOK_SOFT.equalsIgnoreCase(pdCode))
            return true;
        return false;
    }

    public static boolean isNewKakaoBook(String prodCode) {
        if (Config.PRODUCT_NEW_KAKAKO_STORYBOOK_HARD.equalsIgnoreCase(prodCode) || Config.PRODUCT_NEW_KAKAKO_STORYBOOK_SOFT.equalsIgnoreCase(prodCode))
            return true;
        return false;
    }

    public static boolean isFacebook_Photobook() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        String pdCode = productInfoManager.getPROD_CODE();
        if (pdCode == null) return false;

        if (Config.PRODUCT_FACEBOOK_PHOTOBOOK_HARD.equalsIgnoreCase(pdCode) || Config.PRODUCT_FACEBOOK_PHOTOBOOK_SOFT.equalsIgnoreCase(pdCode))
            return true;
        return false;
    }

    public static boolean isFacebook_Photobook(String prodCode) {
        if (Config.PRODUCT_FACEBOOK_PHOTOBOOK_HARD.equalsIgnoreCase(prodCode) || Config.PRODUCT_FACEBOOK_PHOTOBOOK_SOFT.equalsIgnoreCase(prodCode))
            return true;
        return false;
    }

    public static boolean isSnapsDiary() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        String pdCode = productInfoManager.getPROD_CODE();
        if (pdCode == null) return false;

        if (Config.PRODUCT_SNAPS_DIARY_HARD.equalsIgnoreCase(pdCode) || Config.PRODUCT_SNAPS_DIARY_SOFT.equalsIgnoreCase(pdCode))
            return true;
        return false;
    }

    public static boolean isSnapsDiary(String prodCode) {
        if (Config.PRODUCT_SNAPS_DIARY_HARD.equalsIgnoreCase(prodCode) || Config.PRODUCT_SNAPS_DIARY_SOFT.equalsIgnoreCase(prodCode))
            return true;
        return false;
    }

    public static boolean isSNSBook() {
        return isNewKakaoBook() || isFacebook_Photobook() || isSnapsDiary();
    }

    /*
    public static boolean isSNSBook(String prodCode) {
        return isNewKakaoBook(prodCode) || isFacebook_Photobook(prodCode) || isSnapsDiary(prodCode);
    }
    */

    public static boolean isSNSPhoto(int productKind) {
        switch (productKind) {
            case Const_VALUES.SELECT_KAKAO:
            case Const_VALUES.SELECT_FACEBOOK:
            case Const_VALUES.SELECT_SDK_CUSTOMER:
            case Const_VALUES.SELECT_BETWEEN:
            case Const_VALUES.SELECT_INSTAGRAM:
            case Const_VALUES.SELECT_GOOGLEPHOTO:
                return true;
        }
        return false;
    }


    /**
     * 현재 상품 스티커 유무
     *
     * @return
     */
    public static boolean isSnapsSticker() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        String pdCode = productInfoManager.getPROD_CODE();
        if (pdCode == null) return false;

        if (Config.PRODUCT_STICKER.equalsIgnoreCase(pdCode))
            return true;
        return false;
    }

    public static boolean isSnapsSticker(String prodCode) {
        if (Config.PRODUCT_STICKER.equalsIgnoreCase(prodCode))
            return true;
        return false;
    }


    /***
     * 현재 상품이 심플포토북 유무
     *
     * @return
     */
    public static boolean isSimplePhotoBook() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        String pdCode = productInfoManager.getPROD_CODE();
        if (pdCode == null) return false;

        if (isSimpleMakingBook())
            return false; // 간편만들기 레이플렛북이 심플포토북을 타지 않기 위해.

        //6*6 사이즈 적용.
        //for (String s : PRODUCT_SIMPLE_PHOTOBOOK) {
        if (pdCode.startsWith(PRODUCT_SIMPLE_PHOTOBOOK))
            return true;
        //}

        for (String s : PRODUCT_LAY_FLATBOOK) {
            if (pdCode.startsWith(s))
                return true;
        }

        if (isFanBook()) {
            return true;
        }

        if (isKTBook(pdCode)) {
            return true;
        }

//		if (pdCode.startsWith(PRODUCT_LEATHER_BOOK))
//			return true;

        return false;
    }

    /***
     * 현재 상품이 간편만들기인지.
     *
     * @return
     */
    public static boolean isSimpleMakingBook() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        String pdCode = productInfoManager.getPROD_CODE();
        if (pdCode == null) return false;

        for (String s : PRODUCT_SIMPLE_MAKING_BOOK) {
            if (pdCode.startsWith(s))
                return true;
        }

        return false;
    }

    /**
     * KT Book 체크는 템플릿 코드로 한다.
     * SnapsProductInfoManager 에 상품 정보가 저장 안되어있을 경우 제대로 동작 안한다.
     * 이 함수는 편집기 내부에서 쓰길 권장.
     * 그 전에 분기처리가 필요할 경우에는 템플릿 코드와 프로덕트 코드를 어디선가 얻어서 체크.
     *
     * @return
     */
    public static boolean isKTBook() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        String productCode = productInfoManager.getPROD_CODE();
//        String templateCode = productInfoManager.getTMPL_CODE();
        if (productCode == null) {
            return false;
        }
        return isKTBook(productCode);
    }

    /**
     * @return
     */
    public static boolean isKTBook(String productCode) {
        if (productCode == null) {
            return false;
        }
        return productCode.equals("00800600190032");
    }

    public static boolean isSimpleMakingBook(String prodCode) {
        for (String s : PRODUCT_SIMPLE_MAKING_BOOK) {
            if (prodCode.startsWith(s))
                return true;
        }

        return false;
    }

    public static boolean isFanBook(String pdCode) {
        if (pdCode != null && pdCode.startsWith(PRODUCT_FANBOOK_GROUP))
            return true;

        return false;
    }

    // 포토북 형식인데, 커버가 없는 제품..
    public static boolean isNotCoverPhotoBook() {
        return isCalendar() || Const_PRODUCT.isPackageProduct() || Const_PRODUCT.isCardProduct() || Const_PRODUCT.isPhotoCardProduct() || Const_PRODUCT.isNewWalletProduct();
    }

    public static boolean isPhotobooks() {
        return isSimplePhotoBook() || isSimpleMakingBook() || isLayFlatBook() || isSmartSnapsRecommendLayoutPhotoBook();
    }

    /***
     * 현재 상품이 심플포토북 유무
     *
     * @param productCode
     * @return
     */
    public static boolean isSimplePhotoBook(String productCode) {
        if (isSimpleMakingBook(productCode))
            return false;

        //6*6 적용.
//		for (String s : PRODUCT_SIMPLE_PHOTOBOOK) {
        if (productCode.startsWith(PRODUCT_SIMPLE_PHOTOBOOK))
            return true;
//		}

        for (String s : PRODUCT_LAY_FLATBOOK) {
            if (productCode.startsWith(s))
                return true;
        }
        if (isFanBook(productCode)) {
            return true;
        }

        if (isKTBook(productCode)) {
            return true;
        }
//		if (productCode.startsWith(PRODUCT_LEATHER_BOOK))
//			return true;

        return false;
    }

    public static boolean isLayFlatBook() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        String pdCode = productInfoManager.getPROD_CODE();
        if (pdCode == null) return false;

        for (String s : PRODUCT_LAY_FLATBOOK) {
            if (pdCode.startsWith(s))
                return true;
        }

        return false;
    }

    public static boolean isSmartSnapsRecommendLayoutPhotoBook() {
        if (isAliveRecommendBookActivity()) return true;
        String templateCode = Config.getTMPL_CODE();
        if (StringUtil.isEmpty(templateCode)) return false;
        return isSmartSnapsRecommendLayoutPhotoBook(templateCode);
    }

    public static boolean isSmartSnapsRecommendLayoutPhotoBook(String templateCode) {
        if (PRODUCT_SMART_SNAPS_ANALYSIS_PHOTO_BOOK_ETC.equalsIgnoreCase(templateCode)) return true;
        SmartSnapsConstants.eSmartAnalysisPhotoBookThemeType[] themeTypes = SmartSnapsConstants.eSmartAnalysisPhotoBookThemeType.values();
        for (SmartSnapsConstants.eSmartAnalysisPhotoBookThemeType themeType : themeTypes) {
            if (!StringUtil.isEmpty(templateCode) && templateCode.equalsIgnoreCase(themeType.getTemplateCode()))
                return true;
        }

        return false;
    }

    //2021년 버전 자동 완성 포토북
    public static boolean isSmartSnapsRecommendLayoutPhotoBookVer2021(String templateCode) {
        if (templateCode.equals(Const_PRODUCT.PRODUCT_SMART_SNAPS_ANALYSIS_PHOTO_BOOK_TRAVEL_VER_2021)) return true;
        if (templateCode.equals(Const_PRODUCT.PRODUCT_SMART_SNAPS_ANALYSIS_PHOTO_BOOK_BABY_VER_2021)) return true;
        if (templateCode.equals(Const_PRODUCT.PRODUCT_SMART_SNAPS_ANALYSIS_PHOTO_BOOK_COUPLE_VER_2021)) return true;
        if (templateCode.equals(Const_PRODUCT.PRODUCT_SMART_SNAPS_ANALYSIS_PHOTO_BOOK_FAMILY_VER_2021)) return true;
        if (templateCode.equals(Const_PRODUCT.PRODUCT_SMART_SNAPS_ANALYSIS_PHOTO_BOOK_ETC_VER_2021)) return true;
        return false;
    }

//	public static boolean isLeatherCoverBook() {
//		for (String s : PRODUCT_LEATHERBOOK) {
//			if (s.equalsIgnoreCase(pdCode))
//				return true;
//		}
//
//		return false;
//	}

    public static String getKAKAO_SCHEMA() {
        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        return configManager.getKAKAO_SCHEMA();
    }

    public static void setKAKAO_SCHEMA(String kAKAO_SCHEMA) {
        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        configManager.setKAKAO_SCHEMA(kAKAO_SCHEMA);
    }

    // 편집화면 하단에 썸네일이 있는 제품들..
    public static boolean isExistThumbnailEditView() {
        if (Config.isThemeBook() || Config.isSimplePhotoBook() || Config.isSimpleMakingBook() || Const_PRODUCT.isLayFlatBook() || Config.isCalendar() || Const_PRODUCT.isCardProduct()
                || Const_PRODUCT.isPackageProduct() || Const_PRODUCT.isPhotoCardProduct() || Const_PRODUCT.isNewWalletProduct() || Const_PRODUCT.isStikerGroupProduct()
                || Const_PRODUCT.isAccordionCardProduct() || Const_PRODUCT.isPosterGroupProduct() || Const_PRODUCT.isBabyNameStikerGroupProduct() || Const_PRODUCT.isLongPhotoStickerProduct())
            return true;
        return false;
    }

    public static boolean isValidProjCode() {
//		return Config.getPROJ_CODE() != null
//				&& Config.getPROJ_CODE().length() > 0
//				&& !Config.getPROJ_CODE().equals("tokenInvaild") && Config.getPROJ_CODE() != null && !Config.getPROJ_CODE().equals("false");
        return isValidProjCodeWithStringCode(Config.getPROJ_CODE());
    }

    public static boolean isValidProjCodeWithStringCode(String projectCode) {
//		return projectCode != null
//				&& projectCode.trim().length() > 0
//				&& !projectCode.equals("tokenInvaild") && !projectCode.equals("false");

        if (StringUtil.isEmpty(projectCode)) return false;

        try {
            Long.parseLong(projectCode);
            return true;
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        return false;
    }

    public static String getPROJ_CODE() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        return productInfoManager.getPROJ_CODE();
    }

    public static void setPROJ_CODE(String pROJ_CODE) {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        productInfoManager.setPROJ_CODE(pROJ_CODE);
    }

    public static String getPROD_CODE() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        return productInfoManager.getPROD_CODE();
    }

    public static void setPROD_CODE(String pROD_CODE) {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        productInfoManager.setPROD_CODE(pROD_CODE);
    }

    public static String getTMPL_CODE() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        return productInfoManager.getTMPL_CODE();
    }

    public static void setTMPL_CODE(String tMPL_CODE) {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        productInfoManager.setTMPL_CODE(tMPL_CODE);
    }

    public static String getPROD_NAME() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        return productInfoManager.getPROD_NAME();
    }

    public static void setPROD_NAME(String pROD_NAME) {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        productInfoManager.setPROD_NAME(pROD_NAME);
    }

    public static String getPROJ_NAME() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        return productInfoManager.getPROJ_NAME();
    }

    public static void setPROJ_NAME(String pROJ_NAME) {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        productInfoManager.setPROJ_NAME(pROJ_NAME);
    }

    public static String getPROJ_UTYPE() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        return productInfoManager.getPROJ_UTYPE();
    }

    public static void setPROJ_UTYPE(String pROJ_UTYPE) {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        productInfoManager.setPROJ_UTYPE(pROJ_UTYPE);
    }

    public static String getTMPL_COVER() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        return productInfoManager.getTMPL_COVER();
    }

    public static void setTMPL_COVER(String tMPL_COVER) {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        productInfoManager.setTMPL_COVER(tMPL_COVER);
    }

    public static String getTMPL_COVER_TITLE() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        return productInfoManager.getTMPL_COVER_TITLE();
    }

    public static void setTMPL_COVER_TITLE(String tMPL_COVER_TITLE) {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        productInfoManager.setTMPL_COVER_TITLE(tMPL_COVER_TITLE);
    }

    public static String getPAPER_CODE() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        return productInfoManager.getPAPER_CODE();
    }

    public static void setPAPER_CODE(String pAPER_CODE) {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        productInfoManager.setPAPER_CODE(pAPER_CODE);
    }

    public static String getQUANTITY() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        return productInfoManager.getQUANTITY();
    }

    public static void setQUANTITY(String QUANTITY) {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        productInfoManager.setQUANTITY(QUANTITY);
    }

    public static String getCARD_QUANTITY() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        return productInfoManager.getCARD_QUANTITY();
    }

    public static void setCARD_QUANTITY(String cARD_QUANTITY) {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        productInfoManager.setCARD_QUANTITY(cARD_QUANTITY);
    }

    public static boolean isFromCart() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        return productInfoManager.isFromCart();
    }

    public static void setFromCart(boolean isFromCart) {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        productInfoManager.setFromCart(isFromCart);
    }

    public static String getFRAME_TYPE() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        return productInfoManager.getFRAME_TYPE();
    }

    public static void setFRAME_TYPE(String fRAME_TYPE) {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        productInfoManager.setFRAME_TYPE(fRAME_TYPE);
    }

    public static String getBACK_TYPE() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        return productInfoManager.getBACK_TYPE();
    }

    public static void setBACK_TYPE(String bACK_TYPE) {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        productInfoManager.setBACK_TYPE(bACK_TYPE);
    }

    public static String getFRAME_ID() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        return productInfoManager.getFRAME_ID();
    }

    public static void setFRAME_ID(String fRAME_ID) {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        productInfoManager.setFRAME_ID(fRAME_ID);
    }

    public static String getNOTE_PAPER_CODE() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        return productInfoManager.getNOTE_PAPER_CODE();
    }

    public static void setNOTE_PAPER_CODE(String nOTE_PAPER_CODE) {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        productInfoManager.setNOTE_PAPER_CODE(nOTE_PAPER_CODE);
    }

    public static String getDesignId() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        return productInfoManager.getDESIGN_ID();
    }

    public static void setDesignId(String designId) {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        productInfoManager.setDESIGN_ID(designId);
    }


    public static String getGLOSSY_TYPE() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        return productInfoManager.getGLOSSY_TYPE();
    }

    public static void setGLOSSY_TYPE(String gLOSSY_TYPE) {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        productInfoManager.setGLOSSY_TYPE(gLOSSY_TYPE);
    }

    public static void cleanProductInfo() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        productInfoManager.cleanProductInfo();
    }

    public static boolean isBetweenVersion() {
        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        return configManager.IS_BETWEEN_VERSION();
    }

    public static void setAppProcess(boolean flag) {
        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        configManager.setAPP_PROCESS(flag);
    }

    public static boolean isAppProcess() {
        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        return configManager.isAPP_PROCESS();
    }

    public static void setDevelopVersion(boolean flag) {
        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        configManager.setDEVELOP_VERSION(flag);
    }

    public static boolean isDevelopVersion() {
        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        return configManager.isDEVELOP_VERSION();
    }

    public static void setWQHDResolutionDevice(boolean flag) {
        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        configManager.setWQHDResolutionDevice(flag);
    }

    public static boolean isWQHDResolutionDevice() {
        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        return configManager.isWQHDResolutionDevice();
    }

    public static String getKAKAO_EVENT_RESULT() {
        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        return configManager.getKAKAO_EVENT_RESULT();
    }

    public static void setKAKAO_EVENT_RESULT(String str) {
        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        configManager.setKAKAO_EVENT_RESULT(str);
    }

    public static void setKAKAO_EVENT_RESULT2(String str) {
        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        configManager.setKAKAO_EVENT_RESULT2(str);
    }

    public static String getKAKAO_EVENT_RESULT2() {
        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        return configManager.getKAKAO_EVENT_RESULT2();
    }

    public static void setKAKAO_EVENT_CODE(String str) {
        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        configManager.setKAKAO_EVENT_CODE(str);
    }

    public static String getKAKAO_EVENT_CODE() {
        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        return configManager.getKAKAO_EVENT_CODE();
    }

    public static void setKAKAO_EVENT_SENDNO(String str) {
        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        configManager.setKAKAO_EVENT_SENDNO(str);
    }

    public static String getKAKAO_EVENT_SENDNO() {
        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        return configManager.getKAKAO_EVENT_SENDNO();
    }

    public static void setKAKAO_EVENT_DEVICEID(String str) {
        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        configManager.setKAKAO_EVENT_DEVICEID(str);
    }

    public static void setIS_OVER_LENTH_CARD_MSG(boolean flag) {
        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        configManager.setIS_OVER_LENTH_CARD_MSG(flag);
    }

    public static boolean IS_OVER_LENTH_CARD_MSG() {
        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        return configManager.IS_OVER_LENTH_CARD_MSG();
    }

    public static void setIS_MAKE_RUNNING(boolean flag) {
        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        configManager.setIS_MAKE_RUNNING(flag);
    }

    public static boolean IS_MAKE_RUNNING() {
        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        return configManager.IS_MAKE_RUNNING();
    }

    public static void setFF_UUSERID(String str) {
        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        configManager.setFF_UUSERID(str);
    }

    public static String getFF_UUSERID() {
        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        return configManager.getFF_UUSERID();
    }

    public static String getYEAR_KEY() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        return productInfoManager.getYEAR_KEY();
    }

    public static String getSQNC_KEY() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        return productInfoManager.getSQNC_KEY();
    }

    public static void setYEAR_KEY(String str) {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        productInfoManager.setYEAR_KEY(str);
    }

    public static void setSQNC_KEY(String str) {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        productInfoManager.setSQNC_KEY(str);
    }

    public static boolean checkServiceUploadFileDir() throws Exception {
        File projectFileDir = getServiceUploadFileDir();
        return (projectFileDir != null && projectFileDir.exists() && projectFileDir.isDirectory()) || (projectFileDir != null && projectFileDir.mkdirs());
    }

    public static File getServiceUploadFileDir() throws Exception {
        Context context = ContextUtil.getContext();
        if (context == null) return null;
        return new File(getExternalCacheDir(context), SERVICE_UPLOAD_FILE_PATH);
    }

    public static File getSERVICE_UPLOAD_FILE(String fileName) throws Exception {
        boolean isExistDir = checkServiceUploadFileDir();
        if (!isExistDir) {
            throw new IOException("failed to make project File dir");
        }

        return new File(getServiceUploadFileDir(), fileName);
    }

    public static boolean checkProjectFileDir() throws Exception {
        File projectFileDir = getProjectFileDir();
        return (projectFileDir != null && projectFileDir.exists() && projectFileDir.isDirectory()) || (projectFileDir != null && projectFileDir.mkdirs());
    }

    public static File getProjectFileDir() throws Exception {
        Context context = ContextUtil.getContext();
        if (context == null) return null;
        return new File(getExternalCacheDir(context), PROJECT_FILE_PATH);
    }

    public static File getPROJECT_FILE(String fileName) throws Exception {
        boolean isExistDir = checkProjectFileDir();
        if (!isExistDir) {
            throw new IOException("failed to make project File dir");
        }

        return new File(getProjectFileDir(), fileName);
    }

    public static boolean checkThumbnailFileDir() throws Exception {
        File thumbnailFileDir = getThumbnailFileDir();
        return (thumbnailFileDir != null && thumbnailFileDir.exists() && thumbnailFileDir.isDirectory()) || (thumbnailFileDir != null && thumbnailFileDir.mkdirs());
    }

    public static File getThumbnailFileDir() throws Exception {
        Context context = ContextUtil.getContext();
        if (context == null) return null;
        return new File(getExternalCacheDir(context), THUMBNAIL_SAVE_PATH);
    }

    public static File getTHUMB_PATH(String fileName) throws Exception {
        boolean isExistDir = checkThumbnailFileDir();
        if (!isExistDir) {
            throw new IOException("failed to make thumbnail File dir");
        }

        return new File(getThumbnailFileDir(), fileName);
    }

    public static boolean checkServiceThumbnailFileDir() throws Exception {
        File thumbnailFileDir = getServiceThumbnailFileDir();
        return (thumbnailFileDir != null && thumbnailFileDir.exists() && thumbnailFileDir.isDirectory()) || (thumbnailFileDir != null && thumbnailFileDir.mkdirs());
    }

    public static File getServiceThumbnailFileDir() throws Exception {
        Context context = ContextUtil.getContext();
        if (context == null) return null;
        return new File(getExternalCacheDir(context), SERVICE_THUMBNAIL_SAVE_PATH);
    }

    public static File getSERVICE_THUMB_PATH(String fileName) throws Exception {
        boolean isExistDir = checkServiceThumbnailFileDir();
        if (!isExistDir) {
            throw new IOException("failed to make service thumbnail File dir");
        }

        return new File(getServiceThumbnailFileDir(), fileName);
    }

    public static boolean checkServiceThumbnailSimpleFileDir() throws Exception {
        File thumbnailFileDir = getServiceThumbnailSimpleFileDir();
        return (thumbnailFileDir != null && thumbnailFileDir.exists() && thumbnailFileDir.isDirectory()) || (thumbnailFileDir != null && thumbnailFileDir.mkdirs());
    }

    public static File getServiceThumbnailSimpleFileDir() throws Exception {
        Context context = ContextUtil.getContext();
        if (context == null) return null;
        return new File(getExternalCacheDir(context), SERVICE_THUMBNAIL_SIMPLE_SAVE_PATH);
    }

    public static File getSERVICE_THUMB_SIMPLE_PATH(String fileName) throws Exception {
        boolean isExistDir = checkServiceThumbnailSimpleFileDir();
        if (!isExistDir) {
            throw new IOException("failed to make service thumbnail File dir");
        }

        return new File(getServiceThumbnailSimpleFileDir(), fileName);
    }

//	public static String getPROJECT_FILE_PATH() {
//		SnapsConfigManager configManager = SnapsConfigManager.getInstance();
//		return configManager.getPROJECT_FILE_PATH();
//	}
//
//	public static void setPROJECT_FILE_PATH(String str) {
//		SnapsConfigManager configManager = SnapsConfigManager.getInstance();
//		configManager.setPROJECT_FILE_PATH(str);
//	}

    public static String getUSER_COVER_COLOR() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        return productInfoManager.getUserCoverColor();
    }

    public static void setUSER_COVER_COLOR(String str) {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        productInfoManager.setUserCoverColor(str);
    }

//	public static void setTHUMB_PATH(String str) {
//		SnapsConfigManager configManager = SnapsConfigManager.getInstance();
//		configManager.setTHUMB_PATH(str);
//	}
//
//	public static String getTHUMB_PATH() {
//		SnapsConfigManager configManager = SnapsConfigManager.getInstance();
//		return configManager.getTHUMB_PATH();
//	}

    public static void setEFFECT_APPLIED_IMG_SAVE_PATH(String str) {
        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        configManager.setEFFECT_APPLIED_IMG_SAVE_PATH(str);
    }

    public static String getEFFECT_APPLIED_IMG_SAVE_PATH() {
        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        return configManager.getEFFECT_APPLIED_IMG_SAVE_PATH();
    }

    public static void setUI_MENU_JSON_PATH(String str) {
        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        configManager.setUI_MENU_JSON_PATH(str);
    }

    public static String getUI_MENU_JSON_PATH() {
        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        return configManager.getUI_MENU_JSON_PATH();
    }

    public static String getAPP_VERSION() {
        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        return configManager.getAPP_VERSION();
    }

    public static String getCurrentClassName() {
        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        return configManager.getCurrentClassName();
    }

    public static void setCurrentClassName(String str) {
        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        configManager.setCurrentClassName(str);
    }

    public static String getSHOW_GRADE_BUTTON() {
        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        return configManager.getSHOW_GRADE_BUTTON();
    }

    public static void setSHOW_GRADE_BUTTON(String str) {
        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        configManager.setSHOW_GRADE_BUTTON(str);
    }

    public static boolean isDEBUG_LOGG() {
        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        return configManager.isDEBUG_LOGG();
    }

    public static boolean isFILE_LOGG() {
        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        return configManager.isFILE_LOGG();
    }

    public static String getBlockShowCurrentUserKey(Context context) {
        String userNo = Setting.getString(context, Const_VALUE.KEY_SNAPS_USER_NO);
        return Const_VALUE.KEY_BLOCK_SHOW_AGREE_POPUP_USER + userNo;
    }

    public static boolean isBlockShowCurrentUserPushAgreePopup(Context context) {
        String userNo = Setting.getString(context, Const_VALUE.KEY_SNAPS_USER_NO);
        return !StringUtil.isEmpty(userNo) && Setting.getBoolean(context, getBlockShowCurrentUserKey(context), false);
    }

    public static boolean isFabricUse = false;

    public static void setIsFabricUse(boolean use) {
        isFabricUse = use;
    }

    public static boolean isFabricUse() {
        return isFabricUse;
    }

    public static boolean isEditScreenSave = false;

    public static void setIsBabyNameStickerEditScreen(boolean isEditScreen) {
        isEditScreenSave = isEditScreen;
    }

    public static boolean isBabyNameStickerEditScreen() {
        return isEditScreenSave;
    }

    public static boolean isAliveRecommendBookActivity() {
        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        return configManager.isAliveRecommendBookActivity();
    }

    public static void setAliveRecommendBookActivity(boolean aliveRecommendBookActivity) {
        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        configManager.setAliveRecommendBookActivity(aliveRecommendBookActivity);
        setAI_SELFAI_EDITTING(aliveRecommendBookActivity);
    }


    // 추천AI, 셀프AI 관련 정보
    public static boolean getAI_IS_SELFAI() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        return productInfoManager.getAI_IS_SELFAI();
    }

    public static void setAI_IS_SELFAI(boolean self) {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        productInfoManager.setAI_IS_SELFAI(self);
    }

    public static boolean getAI_IS_RECOMMENDAI() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        return productInfoManager.getAI_IS_RECOMMENDAI();
    }

    public static void setAI_IS_RECOMMENDAI(boolean recommend) {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        productInfoManager.setAI_IS_RECOMMENDAI(recommend);
    }

    public static String getAI_RECOMMENDREQ() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        return productInfoManager.getAI_RECOMMENDREQ();
    }

    public static void setAI_RECOMMENDREQ(String value) {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        productInfoManager.setAI_RECOMMENDREQ(value);
    }

    public static String getAI_SEARCHTYPE() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        return productInfoManager.getAI_SEARCHTYPE();
    }

    public static void setAI_SEARCHTYPE(String value) {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        productInfoManager.setAI_SEARCHTYPE(value);
    }

    public static String getAI_SEARCHVALUE() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        return productInfoManager.getAI_SEARCHVALUE();
    }

    public static void setAI_SEARCHVALUE(String value) {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        productInfoManager.setAI_SEARCHVALUE(value);
    }

    public static String getAI_SEARCHDATE() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        return productInfoManager.getAI_SEARCHDATE();
    }

    public static void setAI_SEARCHDATE(String value) {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        productInfoManager.setAI_SEARCHDATE(value);
    }

    public static void setAI_SELFAI_EDITTING(boolean edit) {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        productInfoManager.setAI_SELFAI_EDITTING(edit);
    }

    public static boolean getAI_SELFAI_EDITTING() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        return productInfoManager.getAI_SELFAI_EDITTING();
    }

    public static String getBackendAPIBase() {
        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        return configManager.getBackendAPIBase();
    }

    public static String getFrontEndAPIBase(String parsedLanguageCode) {
        SnapsConfigManager configManager = SnapsConfigManager.getInstance();
        return String.format(configManager.getFrontendAPIBase(), parsedLanguageCode);
    }
}
