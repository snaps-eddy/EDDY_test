package com.snaps.facebook.model.sns.facebook;

import com.snaps.common.utils.log.Dlog;

import org.json.JSONException;
import org.json.JSONObject;

public class LikeData {
	private static final String TAG = LikeData.class.getSimpleName();
	public String id;
	public String name;
	public String profileUrl;
	public String nextPage;
	
	public LikeData() {
		
	}
	
	public LikeData( String jsonString ) {
		JSONObject jobj = null;
		try {
			jobj = new JSONObject( jsonString );
		} catch (JSONException e) {
			Dlog.e(TAG, e);
			new LikeData();
		}
		
		new LikeData( jobj );
	}
	
	public LikeData( JSONObject jobj ) {
		try {
			id = jobj.getString( "id" );
			name = jobj.getString( "name" );
			profileUrl = jobj.has( "picture" ) ? jobj.getJSONObject( "picture" ).getJSONObject( "data" ).getString( "url" ) : "";
			if( jobj.has("paging") && jobj.getJSONObject("paging").has("cursors") && jobj.getJSONObject("paging").getJSONObject("cursors").has("next") )
				nextPage = jobj.getJSONObject( "paging" ).getJSONObject( "cursors" ).getString( "next" );
			else nextPage = "";
		} catch (JSONException e) {
			Dlog.e(TAG, e);
		}
	}

}
