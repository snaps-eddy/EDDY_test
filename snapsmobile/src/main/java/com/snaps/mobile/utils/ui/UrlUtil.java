package com.snaps.mobile.utils.ui;


import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.ui.menu.webinterface.ISnapsWebEventCMDConstants;
import com.snaps.mobile.activity.ui.menu.webinterface.web_event_handlers.SnapsWebEventBaseHandler;

import org.apache.http.message.BasicNameValuePair;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class UrlUtil {
    private static final String TAG = UrlUtil.class.getSimpleName();
    public static String getIntentDataFromFullUrl(@NonNull Intent intent, String key, boolean isDecode) {
        String fullUrl = getFullUrlFromIntent(intent);
        if (fullUrl != null) {
            try {
                String data = Config.ExtractPushWebURL(fullUrl).get(key);
                return isDecode ? URLDecoder.decode(data, "utf-8") : data;
            } catch (Exception e) {
                Dlog.e(TAG, e);
            }
        }
        return null;
    }

    public static String getFullUrlFromIntent(@NonNull Intent intent) {
        Bundle bundle = intent.getExtras();
        return bundle != null ? bundle.getString("fullurl") : null;
    }

	public static String getTopMarginUrl(Context context, boolean haveTabs, String baseUrl ) {
		int marginDp = 0;
		Resources res = context.getResources();

		marginDp += res.getDimension( R.dimen.home_title_bar_height ) / res.getDisplayMetrics().density;
		if( haveTabs ) marginDp += res.getDimension( R.dimen.home_menu_bar_height ) / res.getDisplayMetrics().density;

		boolean hasParam = baseUrl.contains( "?" );
		return baseUrl + (hasParam ? "&" : "?") + "appMarginTop=" + marginDp;
	}

	// TODO home 웹뷰 상단 스페이스 좁은거 맞추기 위한 메소드.
	public static String getHomeTopMarginUrl( Context context, String baseUrl ) {
		int marginDp = 0;
		Resources res = context.getResources();

		marginDp += res.getDimension( R.dimen.home_title_bar_height ) / res.getDisplayMetrics().density;
		marginDp += res.getDimension( R.dimen.home_menu_bar_height ) / res.getDisplayMetrics().density;
		marginDp += res.getDimension( R.dimen.home_space_fix_margin ) / res.getDisplayMetrics().density;

		boolean hasParam = baseUrl.contains( "?" );
		return baseUrl + (hasParam ? "&" : "?") + "appMarginTop=" + marginDp;
	}

	public static boolean isPhotoPrintProduct( String url ) {
		for (String s : Config.PRODUCT_PHOTOPRINT_PRODCODE) {
			if ( url.contains("F_PROD_CODE=" + s) )
				return true;
		}

		return false;
	}

    public static Uri replaceUriParameter(Uri uri, ArrayList<BasicNameValuePair> newParams ) {
        final Set<String> params = uri.getQueryParameterNames();
        final Uri.Builder newUri = uri.buildUpon().clearQuery();

        HashMap<String, String> newParamMap = new HashMap<String, String>();
        for( BasicNameValuePair item : newParams )
            newParamMap.put( item.getName(), item.getValue() );

        for (String param : params) {
            String value;
            if ( newParamMap.containsKey(param) ) {
                value = newParamMap.get( param );
                newParamMap.remove( param );
            }
            else
                value = uri.getQueryParameter(param);

            newUri.appendQueryParameter( param, value );
        }

        for( String key : newParamMap.keySet() )
            newUri.appendQueryParameter( key, newParamMap.get(key) );

        return newUri.build();
    }

    public static String getParameterFromUrl(String url, String findParam) {
        if (StringUtil.isEmpty(url)) return url;
        try {
            HashMap<String, String> params = UrlUtil.getParameters( url );
            if (params != null && params.containsKey(findParam)) {
                return params.get(findParam);
            }
        } catch (Exception e) { Dlog.e(TAG, e); }
        return null;
    }

    public static HashMap<String, String> getParameters( String url ) {
        if (StringUtil.isEmpty(url)) return null;

        Uri uri = Uri.parse( url );
        final Set<String> params = uri.getQueryParameterNames();
        if (params == null) return null;

        HashMap<String, String> map = new HashMap<String, String>();
        for (String param : params) {
            if (param == null) continue;
            map.put( param, uri.getQueryParameter(param) );
        }

        return map;
    }

    //네이티브로 전환하면서 제품 리스트 / 상세 화면으로 이동하는 기능이 동작하지 않아, 하드 코딩 처리
    public static String convertNativeURLIfOldUrl(String url) {
        if (url == null) return null;
        final String[] arOldUrl = {
                SnapsWebEventBaseHandler.DETAIL_PRODUCT_SCHEME,
                SnapsWebEventBaseHandler.SIZE_PRODUCT_SCHEME,
                SnapsWebEventBaseHandler.SEARCH_PRODUCT_SCHEME
        };

        boolean isOldUrl = false;
        for (String oldUrl : arOldUrl) {
            if (url.startsWith(oldUrl)) {
                isOldUrl = true;
                break;
            }
        }

        if (isOldUrl) {
            HashMap<String, String> mapParams = getExtractWebURL(url);
            String convertUrl = convertNativeURLFromHashMap(mapParams, url);
            if (convertUrl != null) return convertUrl;
        }

        return url;
    }

    private static String convertNativeURLFromHashMap(HashMap<String, String> mapData, String url) {
        if (mapData == null || mapData.isEmpty() || url == null) return null;

        StringBuilder builder = new StringBuilder();

        String scheme = "";
        if (url.startsWith(SnapsWebEventBaseHandler.SEARCH_PRODUCT_SCHEME))
            scheme = ISnapsWebEventCMDConstants.SNAPS_SCHEME_TYPE_LIST;
        else if (url.startsWith(SnapsWebEventBaseHandler.SIZE_PRODUCT_SCHEME))
            scheme = ISnapsWebEventCMDConstants.SNAPS_SCHEME_TYPE_SIZE;
        else if (url.startsWith(SnapsWebEventBaseHandler.DETAIL_PRODUCT_SCHEME))
            scheme = ISnapsWebEventCMDConstants.SNAPS_SCHEME_TYPE_DETAIL;

        builder.append(SnapsWebEventBaseHandler.SCHMA)
               .append(scheme).append("?");

        int idx = 0;
        for (String key : mapData.keySet()) {
            builder.append(key).append("=").append(mapData.get(key));
            if (++idx < mapData.size()) {
                builder.append("&");
            }
        }

        return builder.toString();
    }

    public static HashMap<String, String> getExtractWebURL(String url) {
        if (url == null || !url.contains("?")) return null;

        HashMap<String, String> hashmap = new HashMap<String, String>();
        String params = url.substring(url.indexOf("?") + 1, url.length());

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
        return hashmap;
    }
}
