package com.snaps.mobile.utils.sns;

import android.os.Bundle;

import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.net.http.HttpUtil;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import errorhandle.logger.SnapsInterfaceLogDefaultHandler;

public class FBHttpUtil {
	private static final String TAG = FBHttpUtil.class.getSimpleName();
	static final String FB_GRAPH_PREFIX = "https://graph.facebook.com";
	
	static List<NameValuePair> bundleToQuery(Bundle params) {
		Iterator<String> it = params.keySet().iterator();
		
		List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		while (it.hasNext()) {
			String key = it.next();
			postParameters.add(new BasicNameValuePair(key, params.getString(key)));
		}
		return postParameters;
	}
	
	public static JSONObject graphPath(String accessToken, String gPath, Bundle params) {
		JSONObject jObj = null;
		try {
			params.putString("access_token", accessToken);
			List<NameValuePair> postParameters = bundleToQuery(params);
			
			String fullUrl = FB_GRAPH_PREFIX + gPath;
			String result = HttpUtil.connectPost(fullUrl, postParameters, SnapsInterfaceLogDefaultHandler.createDefaultHandler());
			return new JSONObject(result);
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
		return jObj;
	}
	
	public static JSONObject fql(String accessToken, String query) {
		JSONObject jObj = null;
		try {
			Bundle params = new Bundle();
			params.putString("access_token", accessToken);
			params.putString("q", query);
			List<NameValuePair> postParameters = bundleToQuery(params);
			
			String fullUrl = FB_GRAPH_PREFIX + "/fql";
			String result = HttpUtil.connectGet(fullUrl , postParameters, SnapsInterfaceLogDefaultHandler.createDefaultHandler());
			return new JSONObject(result);
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
		return jObj;
	}
}
