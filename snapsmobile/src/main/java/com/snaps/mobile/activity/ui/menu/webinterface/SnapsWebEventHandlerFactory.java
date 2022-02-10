package com.snaps.mobile.activity.ui.menu.webinterface;

import android.app.Activity;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_EKEY;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.activity.home.utils.SnapsLoginManager;
import com.snaps.mobile.activity.ui.menu.webinterface.web_event_handlers.SnapsWebEventBaseHandler;
import com.snaps.mobile.activity.ui.menu.webinterface.web_event_handlers.SnapsWebEventDefaultHandler;
import com.snaps.mobile.activity.ui.menu.webinterface.web_event_handlers.SnapsWebEventHttpHandler;
import com.snaps.mobile.activity.ui.menu.webinterface.web_event_handlers.SnapsWebEventThirdPartySchemaHandler;
import com.snaps.mobile.activity.webview.KakaoEventActivity;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;

/**
 * Created by ysjeong on 16. 8. 12..
 */
public class SnapsWebEventHandlerFactory {
    private static final String TAG = SnapsWebEventHandlerFactory.class.getSimpleName();
    private static SnapsWebEventHandlerStrSwicher webEventHandlerStrSwicher = null;

    public static SnapsWebEventBaseHandler createWebEventHandler(Activity activity, SnapsShouldOverrideUrlLoader.SnapsShouldHandleData handleDatas) {
        if (handleDatas == null) return null;

        String url = handleDatas.getUrl();
        if (url == null || url.length() < 1) return null;

        KakaoEventActivity.LOGIN_AFTER_CMD = "";

        //menuData json에서 받아온 url은 스냅스 domain을 붙여서 사용한다.
        if (handleDatas.isNativeUI() && !url.startsWith(SnapsWebEventBaseHandler.SCHMA)) {
            url = SnapsAPI.WEB_DOMAIN(url, SnapsLoginManager.getUUserNo(activity), "");
        }

        //스냅스 스키마(snapsApp://)
        if (url.startsWith(SnapsWebEventBaseHandler.SCHMA))
            return createSnapsSchemeTypeWebEventHandler(activity, handleDatas);
        else if (isThirdPartyAppSchema(url))
            return createThirdPartySchemeTypeWebEventHandler(activity, handleDatas);

        //일반 httpURL
        return createNormalTypeWebEventHandler(activity, handleDatas);
    }

    private static boolean isThirdPartyAppSchema(String url) {
        if (StringUtil.isEmpty(url) || url.startsWith("http") || !url.contains(":") || url.startsWith(SnapsWebEventBaseHandler.SCHMA)) return false;
        return !StringUtil.isEmpty(SnapsWebEventThirdPartySchemaHandler.getThirdPartySchemaWithUrl(url));
    }

    private static void createWebEventHandlerStrSwitcher(Activity activty, SnapsShouldOverrideUrlLoader.SnapsShouldHandleData handleDatas) {
        if (webEventHandlerStrSwicher == null)
            webEventHandlerStrSwicher = new SnapsWebEventHandlerStrSwicher();

        webEventHandlerStrSwicher.setActivity(activty);
        webEventHandlerStrSwicher.setHandleDatas(handleDatas);
    }

    public static void finalizeHandler() {
        if (webEventHandlerStrSwicher != null) {
            webEventHandlerStrSwicher.clear();
            webEventHandlerStrSwicher = null;
        }
    }

    private static SnapsWebEventBaseHandler createThirdPartySchemeTypeWebEventHandler(Activity activity, SnapsShouldOverrideUrlLoader.SnapsShouldHandleData handleDatas) {
        if (handleDatas != null) {
            String url = handleDatas.getUrl();
            handleDatas.setThirdPartySchema(SnapsWebEventThirdPartySchemaHandler.getThirdPartySchemaWithUrl(url));
        }

        return new SnapsWebEventThirdPartySchemaHandler(activity, handleDatas);
    }

    private static SnapsWebEventBaseHandler createSnapsSchemeTypeWebEventHandler(Activity activity, SnapsShouldOverrideUrlLoader.SnapsShouldHandleData handleDatas) {
        if (handleDatas == null) return null;

        String url = handleDatas.getUrl();
        if (url == null || url.length() < 1 || !url.startsWith(SnapsWebEventBaseHandler.SCHMA)) return null;

        String schemeHost = "";

        /**
         * 모바일 리뉴얼을 하면서 url scheme 형식을 snapsapp://host?body 형식으로 변경했다.
         */
        if (Config.isOldStyleCmdKey(url)) {
            HashMap<String, String> hashmap = Config.ExtractWebURL(url);
            if(hashmap != null && hashmap.containsKey(Const_EKEY.WEB_CMD_KEY)) {
                schemeHost = hashmap.get(Const_EKEY.WEB_CMD_KEY);
            }
        } else {
            schemeHost = SnapsWebEventBaseHandler.findHost(url);

            // TODO 임시로 기존 snapsapp://cmd=...로 시작되는 url도 동작하도록
            if( StringUtil.isEmpty(schemeHost) && url.contains("cmd=") ) {
                if( url.contains("&") ) schemeHost = url.split("&")[0].split("=")[1];
                else schemeHost = url.split( "=" )[1];
            }
        }

        if (schemeHost != null && schemeHost.length() > 0) {
            handleDatas.setHost(schemeHost);

            if( "detail".equalsIgnoreCase(schemeHost) )
                try {
                    handleDatas.setUrl( URLDecoder.decode(handleDatas.getUrl(), "UTF-8") );
                } catch (UnsupportedEncodingException e) {
                    Dlog.e(TAG, e);
                }

            //If else의 퍼포먼스 향상을 위해 Map에 case들을 올려 놓고 사용하는 방식으로 변경 함.
            createWebEventHandlerStrSwitcher(activity, handleDatas);

            SnapsWebEventBaseHandler handler = null;
            if (webEventHandlerStrSwicher != null)
                handler = webEventHandlerStrSwicher.getHandler(schemeHost);

            if (handler == null)
                handler = new SnapsWebEventDefaultHandler(activity, handleDatas);

            return handler;
        } else {
            return new SnapsWebEventDefaultHandler(activity, handleDatas);
        }
    }

    private static SnapsWebEventBaseHandler createNormalTypeWebEventHandler(Activity activity, SnapsShouldOverrideUrlLoader.SnapsShouldHandleData urlDatas) {
        if (urlDatas == null) return null;

        String url = urlDatas.getUrl();
        if (url == null || url.length() < 1) return null;

        return new SnapsWebEventHttpHandler(activity, urlDatas);
    }
}
