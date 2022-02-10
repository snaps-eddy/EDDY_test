package com.snaps.facebook.model.sns.facebook;

import com.snaps.common.utils.log.Dlog;
import com.snaps.facebook.utils.FBCommonUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class ImageData {
	private static final String TAG = ImageData.class.getSimpleName();
	public static final int TYPE_NORMAL = 0;
	public static final int TYPE_PROFILE = 1;
	public static final int TYPE_COVER = 2;
	public static final int TYPE_WALL = 3;
	public static final int TYPE_MOBILE = 4;
	
	public int type = TYPE_NORMAL;
	
	public String id;
	public String url;
	public long createTime;
	public Calendar createDate;
	
	public ImageData() {
		
	}
	
	public ImageData( String jsonString ) {
		JSONObject jobj = null;
		try {
			jobj = new JSONObject( jsonString );
		} catch (JSONException e) {
			Dlog.e(TAG, e);
			new ImageData();
		}
		
		new ImageData( jobj );
	}
	
	public ImageData( JSONObject jobj ) {
		String typeStr;
		try {
			typeStr = jobj.getString( "type" );
			type = TYPE_NORMAL;
			switch( typeStr ) {
				case "wall": type = TYPE_WALL; break;
				case "cover": type = TYPE_COVER; break;
				case "profile": type = TYPE_PROFILE; break;
				case "mobile": type = TYPE_MOBILE; break;
			}
			id = jobj.getString( "id" );
			createTime = jobj.getLong( "created_time" );
			createDate = FBCommonUtil.getCalFromFBTime( createTime );
			url = jobj.getJSONObject( "picture" ).getJSONObject( "data" ).getString( "url" );
		} catch (JSONException e) {
			Dlog.e(TAG, e);
		}
	}

}
