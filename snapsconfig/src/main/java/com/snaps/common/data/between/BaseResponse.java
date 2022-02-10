package com.snaps.common.data.between;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.snaps.common.utils.log.Dlog;

import java.io.Serializable;

public class BaseResponse implements Serializable {
	private static final String TAG = BaseResponse.class.getSimpleName();
	private static final long serialVersionUID = -1749026439321708520L;
	
	public static BaseResponse deserializeResponse(String jsonData, Class<?> clazz) {
		try {
			Gson gson = new Gson();
			BaseResponse response = (BaseResponse) gson.fromJson(jsonData,
					clazz);
			return response;
		} catch (JsonSyntaxException e) {
			Dlog.e(TAG, e);
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
	
		return null;
	}
	
	public static boolean parseBool(String str) {
		if (str == null)
			return false;
		return str.equalsIgnoreCase("y") || str.equalsIgnoreCase("true");
	}
}
