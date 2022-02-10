package com.snaps.facebook.model.sns.facebook;

import com.snaps.common.utils.log.Dlog;

import org.json.JSONException;
import org.json.JSONObject;

public class StoryTag {
	private static final String TAG = StoryTag.class.getSimpleName();
	public String id;
	public String name;
	public String rawType;
	
	public StoryTag() {
		
	}
	
	public StoryTag( String jsonString ) {
		JSONObject jobj = null;
		try {
			jobj = new JSONObject( jsonString );
			new StoryTag( jobj );
		} catch (JSONException e) {
			Dlog.e(TAG, e);
			new StoryTag();
		}		
	}
	
	public StoryTag( JSONObject jobj ) {
		try {
			id = jobj.has( "id" ) ? jobj.getString( "id" ) : "";
			name = jobj.has( "name" ) ? jobj.getString( "name" ) : "";
			rawType = jobj.has( "type" ) ? jobj.getString( "type" ) : "";
		} catch (JSONException e) {
			Dlog.e(TAG, e);
			new StoryTag();
		}
		
	}
}
