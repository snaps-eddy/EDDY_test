package com.snaps.mobile.activity.home.model;

import android.util.Log;
import android.webkit.JavascriptInterface;

import com.google.gson.Gson;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.activity.home.RenewalHomeContract;

import java.util.HashMap;
import java.util.Map;

import errorhandle.logger.Logg;

public class RenewalHomeWebViewInterface {
    private static final String TAG = RenewalHomeWebViewInterface.class.getSimpleName();
    private RenewalHomeContract.OnCalledJavascript mListener;

    public RenewalHomeWebViewInterface(RenewalHomeContract.OnCalledJavascript listener) {
        this.mListener = listener;
    }

    @JavascriptInterface
    public void selectProduct(String param) {
        Dlog.d("JAVASCRIPT selectProduct() param:" + param);
        mListener.onSelectProduct(param, false);
    }

    @JavascriptInterface
    public void preview(String param) {
        Dlog.d("JAVASCRIPT preview() param:" + param);
        if (Config.isDevelopVersion()) {
            try {
                Gson gson = new Gson();
                Map<String,Object> map = new HashMap();
                map = gson.fromJson(param, map.getClass());
                final String projectCode = "prjCode";
                if (map.containsKey(projectCode)) {
                    Dlog.d(Dlog.CS_TOOL_TOY, "PROJECT_CODE:" + map.get(projectCode));
                }
            }catch (Exception e) {}
        }

        mListener.onSelectProduct(param, true);
    }

    @JavascriptInterface
    public void gotoDailybook() {
        Dlog.d("JAVASCRIPT gotoDailybook()");
        mListener.onGoToDailybook();
    }

    @JavascriptInterface
    public void gotoSetting() {
        Dlog.d("JAVASCRIPT gotoSetting()");
        mListener.onGoToSetting();
    }

    @JavascriptInterface
    public void setUserInfo(String param) {
        Dlog.d("JAVASCRIPT setUserInfo() param:" + param);
        mListener.onSetUserInfo(param);
    }

    @JavascriptInterface
    public void removeUserInfo() {
        Dlog.d("JAVASCRIPT removeUserInfo()");
        mListener.onRemoveUserInfo();
    }

    @JavascriptInterface
    public void recommandAI(String param) {
        Dlog.d("JAVASCRIPT recommandAI() param:" + param);
        mListener.onRecommandAI(param);
    }

    @JavascriptInterface
    public void selfAI(String param) {
        Dlog.d("JAVASCRIPT selfAI() param:" + param);
        mListener.onSelfAI(param);
    }

    @JavascriptInterface
    public void setAISync() {
        Dlog.d("JAVASCRIPT setAISync()");
        mListener.onSetAISync();
    }

    @JavascriptInterface
    public void setAISyncWithLTE(String param) {
        Dlog.d("JAVASCRIPT setAISyncWithLTE() param:" + param);
        mListener.onSetAISyncWithLTE(param);
    }

    @JavascriptInterface
    public void checkDevicePermission(String param) {
        Dlog.d("JAVASCRIPT checkDevicePermission() param:" + param);
        mListener.onCheckDevicePermission(param);
    }

    @JavascriptInterface
    public void requestDevicePermission(String param) {
        Dlog.d("JAVASCRIPT requestDevicePermission() param:" + param);
        mListener.onRequestDevicePermission(param);
    }

    @JavascriptInterface
    public void setLogConversion(String param) {
        Dlog.d("JAVASCRIPT setLogConversion() param:" + param);
        mListener.onSetLogConversion(param);
    }

    @JavascriptInterface
    public void setBadgeCount(String param) {
//     Dlog.d("JAVASCRIPT setBadgeCount() param:" + param);
        // ?????? ????????? ???????????? ????????? ???????????? ????????? ?????? ????????????????????? ?????? ????????? ????????? ????????? ???????????? ????????????.
    }

    @JavascriptInterface
    public void setBackgroundColor(String param) {
//      Dlog.d("JAVASCRIPT setBackgroundColor() param:" + param);
        // ?????? ????????? ???????????? ????????? ???????????? ????????? ?????? ????????????????????? ?????? ????????? ????????? ????????? ???????????? ????????????.
    }

    //2020.01.04 Ben ?????? ?????? ????????? ?????? ?????? ??????
    @JavascriptInterface
    public void checkPermission(String param) {
        Dlog.d("JAVASCRIPT checkPermission() param:" + param);
        mListener.onCheckPermission(param);
    }

    //2020.01.04 Ben ?????? ?????? ????????? ?????? ?????? ??????
    @JavascriptInterface
    public void eraserFileSave(String param) {
        Dlog.d("JAVASCRIPT onEraserFileSave() param:" + param);
        mListener.onEraserFileSave(param);
    }
}
