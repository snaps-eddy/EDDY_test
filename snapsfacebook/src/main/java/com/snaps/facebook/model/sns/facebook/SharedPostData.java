package com.snaps.facebook.model.sns.facebook;

import com.snaps.common.utils.log.Dlog;

import org.json.JSONException;
import org.json.JSONObject;

public class SharedPostData {
	private static final String TAG = SharedPostData.class.getSimpleName();
	public String id;
	public String createdTime;
	public String story;
	
	public SharedPostData() {
		
	}
	
	public SharedPostData( String jsonString ) {
		JSONObject jobj = null;
		try {
			jobj = new JSONObject( jsonString );
		} catch (JSONException e) {
			Dlog.e(TAG, e);
			new SharedPostData();
		}
		
		new SharedPostData( jobj );
	}
	
	public SharedPostData( JSONObject jobj ) {
		try {
			id = jobj.getString( "id" );
			createdTime = jobj.has( "created_time" ) ? jobj.getString( "created_time" ) : "";
			story = jobj.has( "story" ) ? jobj.getString( "story" ) : "";
		} catch (JSONException e) {
			Dlog.e(TAG, e);
		}
	}

}
