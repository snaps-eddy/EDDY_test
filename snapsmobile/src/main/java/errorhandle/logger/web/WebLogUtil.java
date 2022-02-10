package errorhandle.logger.web;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.log.SnapsInterfaceLogListener;
import com.snaps.common.utils.net.CNetStatus;
import com.snaps.common.utils.net.http.HttpUtil;
import com.snaps.common.utils.system.DateUtil;
import com.snaps.common.utils.system.SystemUtil;
import com.snaps.common.utils.ui.ContextUtil;
import com.snaps.mobile.activity.home.utils.SnapsLoginManager;

import org.json.JSONException;
import org.json.JSONObject;

import errorhandle.SnapsAssert;
import errorhandle.logger.web.request.interfacies.WebLogRequestInfo;
import errorhandle.logger.web.request.payload.WebLogPayload;

public class WebLogUtil {
    private static final String TAG = WebLogUtil.class.getSimpleName();
    public static JSONObject createWebLogParamJsonObj(WebLogRequestInfo requestInfo) throws Exception {
        JSONObject baseRequestParams = createBaseWebLogParamJsonObj();
        baseRequestParams = appendWebLogParamJsonObjWithRequestInfo(baseRequestParams, requestInfo);
        return baseRequestParams;
    }

    private static JSONObject createBaseWebLogParamJsonObj() {
        Context context = ContextUtil.getContext();
        JSONObject jObjectData = new JSONObject();
        try {
            jObjectData.put("accept-language", getWebLogLanguageCode());
            jObjectData.put("x-snaps-token", SystemUtil.getDeviceId(context));
            jObjectData.put("x-snaps-channel", getWebLogChannelValue());
            jObjectData.put("app-version", Config.getAPP_VERSION());
            jObjectData.put("device-info", Build.MODEL);
            jObjectData.put("link", getWebLogNetworkStateValue(context));
            jObjectData.put("userNo", SnapsLoginManager.getUUserNo(context));
        } catch (JSONException e) {
            Dlog.e(TAG, e);
        }
        return jObjectData;
    }

    private static String getWebLogLanguageCode() {
        if (Config.useEnglish()) {
            return WebLogConstants.eWebLogLanguage.EN.getValue();
        } else if (Config.useJapanese()) {
            return WebLogConstants.eWebLogLanguage.JP.getValue();
        } else if (Config.useChinese()) {
            return WebLogConstants.eWebLogLanguage.CH.getValue();
        }

        return WebLogConstants.eWebLogLanguage.KR.getValue();
    }

    private static JSONObject appendWebLogParamJsonObjWithRequestInfo(JSONObject jsonObject, WebLogRequestInfo requestInfo) {
        if (jsonObject == null || requestInfo == null) return jsonObject;

        try {
            jsonObject.put("uri", requestInfo.getURI());

            WebLogConstants.eWebLogInterfaceType interfaceType = requestInfo.getInterfaceType();
            if (interfaceType == null) {
                SnapsAssert.assertTrue(false);
            } else {
                jsonObject.put("type", interfaceType.getValue()); //or RES //필수
            }

            WebLogConstants.eWebLogMethodType methodType = requestInfo.getMethodType();
            if (methodType == null) {
                SnapsAssert.assertTrue(false);
            } else {
                jsonObject.put("method", methodType.getValue()); //or RES //필수
            }

            jsonObject.put("event_time", getCurrentTimeByWebLogFormat()); //or RES //필수

            WebLogPayload payload = requestInfo.getPayload();
            if (payload != null) {
                jsonObject.put("payload", payload.getPayloadJsonStr());
            } else {
                jsonObject.put("payload", "{}");
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        return jsonObject;
    }

    private static String getWebLogChannelValue() {
        return "ANDROID/" + Build.VERSION.RELEASE;
    }

    private static String getWebLogNetworkStateValue(Context context) {
        CNetStatus net_status = CNetStatus.getInstance();
        switch (net_status.getNetType(context)) {
            case CNetStatus.NET_TYPE_NONE:
                return "";
            case CNetStatus.NET_TYPE_WIFI:
                return "WIFI";
            default:
                return getNetworkClass(context);

        }
    }

    private static String getNetworkClass(Context context) {
        TelephonyManager mTelephonyManager = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);
        int networkType = mTelephonyManager.getNetworkType();
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return "3G";
            case TelephonyManager.NETWORK_TYPE_LTE:
                return "LTE";
            default:
                return "";
        }
    }

    private static String getCurrentTimeByWebLogFormat() {
//        YYYYMMDDHHMMSS.SSS   20170301 212830.243
        return DateUtil.getTodayDateWithFormat("yyyyMMddHHmmss.FFF");
    }

    public static int sendSnapsWebLogWithJsonParams(JSONObject paramStr, SnapsInterfaceLogListener interfaceLogListener) throws Exception {
        if (paramStr == null) return -1;
        return HttpUtil.requestJsonReturnResponseCode(SnapsAPI.POST_API_WEB_LOG(), paramStr.toString(), interfaceLogListener);
    }
}
