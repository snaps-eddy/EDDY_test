package com.snaps.common.trackers;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.net.UrlQuerySanitizer;
import android.util.Log;

import com.appsflyer.AFInAppEventParameterName;
import com.appsflyer.AFInAppEventType;
import com.appsflyer.AppsFlyerConversionListener;
import com.appsflyer.AppsFlyerLib;
import com.appsflyer.AppsFlyerProperties;
import com.snaps.common.BuildConfig;
import com.snaps.common.utils.ui.StringUtil;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kimduckwon on 2017. 8. 7..
 */

public class SnapsAppsFlyer {

    private static boolean isUse = false;

    private static final String APPSFLYER_KEY = "8mx5nfJZBkXWcBh4pgS3H";

    private static WeakReference<Context> sWeakRefContext = null;

    public static void setEnable(boolean flag) {
        isUse = flag;
    }

    public static final String TAG = SnapsAppsFlyer.class.getSimpleName();

    /**
     * SDK초기화
     *
     * @param application
     * @return
     */
    public static boolean setSDKinit(Application application) {
        if (!isUse) {
            return false;
        }

        sWeakRefContext = new WeakReference<>(application.getApplicationContext());
        AppsFlyerConversionListener conversionListener = new AppsFlyerConversionListener() {
            @Override
            public void onConversionDataSuccess(Map<String, Object> conversionData) {
                for (String attrName : conversionData.keySet()) {
                    Log.d(TAG, "attribute: " + attrName + " = " + conversionData.get(attrName));
                }
            }

            @Override
            public void onConversionDataFail(String errorMessage) {
                Log.d(TAG, "error getting conversion data: " + errorMessage);
            }

            @Override
            public void onAppOpenAttribution(Map<String, String> conversionData) {
                for (String attrName : conversionData.keySet()) {
                    Log.d(TAG, "attribute: " + attrName + " = " + conversionData.get(attrName));
                }
            }

            @Override
            public void onAttributionFailure(String errorMessage) {
                Log.d(TAG, "error onAttributionFailure : " + errorMessage);
            }
        };

        if (BuildConfig.DEBUG) {
            AppsFlyerLib.getInstance().setDebugLog(true);
        }
        AppsFlyerLib.getInstance().init(APPSFLYER_KEY, conversionListener, application);
        AppsFlyerLib.getInstance().startTracking(application);
        return true;
    }

    /**
     * 화페설정
     *
     * @param currencyCode
     * @return
     */
    public static boolean setCurrency(String currencyCode) {
        if (!isUse) {
            return false;
        }

        AppsFlyerLib.getInstance().setCurrencyCode(currencyCode);
        return true;
    }

    /**
     * 유저 이메일 설정
     *
     * @param email
     * @return
     */
    public static boolean setUserEmails(String email) {
        if (!isUse) {
            return false;
        }

        AppsFlyerLib.getInstance().setUserEmails(AppsFlyerProperties.EmailsCryptType.NONE, email);
        return true;
    }

    /**
     * 딥링크 등록
     *
     * @param activity
     * @return
     */
    public static boolean setDeepLink(Activity activity) {
        if (!isUse) {
            return false;
        }

        AppsFlyerLib.getInstance().sendDeepLinkData(activity);
        return true;
    }

    /**
     * 앱언인스트롤을 위한 gcmId 업데이트
     *
     * @param applicationContext
     * @param refreshedToken
     * @return
     */
    public static boolean setUpdateUninstallToken(Context applicationContext, String refreshedToken) {
        if (!isUse) {
            return false;
        }

        AppsFlyerLib.getInstance().updateServerUninstallToken(applicationContext, refreshedToken);
        return true;
    }

//	/**
//	 * 앱언인스트롤 등록
//	 *
//	 * @param senderId
//	 * @return
//	 */
//	public static boolean setUninstallTracking(String senderId) {
//		if (!isUse) {
//			return false;
//		}
//
//		AppsFlyerLib.getInstance().updateServerUninstallToken();
//		return true;
//	}


    public static boolean setInappEvent(String totalPrice, String orderCode) {
        if (!isUse) {
            return false;
        }

        if (sWeakRefContext == null) return false;
        Context context = sWeakRefContext.get();
        if (context == null) {
            //뭐 어쩔수 없다.....
            return false;
        }

        String currencyCode = StringUtil.getAppsFlyerCurrencyCode(context);
        setCurrency(currencyCode);

        if (totalPrice == null || totalPrice.length() == 0) return false;
        if (orderCode == null || orderCode.length() == 0) return false;

        double totalPriceRemoveComma = Double.parseDouble(totalPrice.replaceAll(",", ""));

        Map<String, Object> eventValue = new HashMap<String, Object>();
        eventValue.put(AFInAppEventParameterName.REVENUE, totalPriceRemoveComma);
        eventValue.put(AFInAppEventParameterName.CONTENT_TYPE, "category_a");
        eventValue.put(AFInAppEventParameterName.CONTENT_ID, orderCode);
        eventValue.put(AFInAppEventParameterName.CURRENCY, currencyCode);

        AppsFlyerLib.getInstance().trackEvent(context, AFInAppEventType.PURCHASE, eventValue);
        return true;
    }

    /**
     * 인앱 매출 이벤트
     *
     * @param applicationContext
     * @param url
     * @return
     */
    public static boolean setInappEvent(Context applicationContext, String url) {
        if (!isUse) {
            return false;
        }

        UrlQuerySanitizer urlQuerySanitizer = new UrlQuerySanitizer(url);
        String amount = urlQuerySanitizer.getValue("totalPrice");
        String orderId = urlQuerySanitizer.getValue("orderCode");
        String sttlCrrnc = urlQuerySanitizer.getValue("sttlCrrnc");

        double dAmount = Double.parseDouble(amount.replaceAll(",", ""));

        Map<String, Object> eventValue = new HashMap<String, Object>();
        eventValue.put(AFInAppEventParameterName.REVENUE, dAmount);
        eventValue.put(AFInAppEventParameterName.CONTENT_TYPE, "category_a");
        eventValue.put(AFInAppEventParameterName.CONTENT_ID, orderId);
        eventValue.put(AFInAppEventParameterName.CURRENCY, StringUtil.getAppsFlyerCurrencyCode(applicationContext));

        AppsFlyerLib.getInstance().trackEvent(applicationContext, AFInAppEventType.PURCHASE, eventValue);

        return true;
    }

    public static boolean setJoinComplete() {
        if (!isUse) {
            return false;
        }

        if (sWeakRefContext == null) return false;
        Context context = sWeakRefContext.get();
        if (context == null) {
            //뭐 어쩔수 없다.....
            return false;
        }

        Map<String, Object> eventValue = new HashMap<String, Object>();
        AppsFlyerLib.getInstance().trackEvent(context, AFInAppEventType.COMPLETE_REGISTRATION, eventValue);
        return true;
    }

    /**
     * 회원가입 완료
     *
     * @param applicationContext
     * @return
     */
    public static boolean setJoinComplete(Context applicationContext) {
        if (!isUse) {
            return false;
        }

        Map<String, Object> eventValue = new HashMap<String, Object>();

        AppsFlyerLib.getInstance().trackEvent(applicationContext, AFInAppEventType.COMPLETE_REGISTRATION, eventValue);

        return true;
    }

}

