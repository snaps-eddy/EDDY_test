package com.snaps.instagram.utils.instagram;

import com.snaps.common.utils.log.Dlog;

import org.json.JSONException;
import org.json.JSONObject;


public class InstagramImageData {
	private static final String TAG = InstagramImageData.class.getSimpleName();
	public String id = "";
	public long createdTime;
//	public int[] thumbSize = new int[2];
	public int[] lowResSize = new int[2];
	public int[] standardResSize = new int[2];
//	public String thumbUrl = "";
	public String lowUrl = "";
	public String standardUrl = "";
	
	public InstagramImageData( String index, long createdTime, JSONObject jobj ) {
		try {
			id = index;
			this.createdTime = createdTime;
			JSONObject thumb, low, standard;
			thumb = jobj.getJSONObject( "thumbnail" );
			low = jobj.getJSONObject( "low_resolution" );
			standard = jobj.getJSONObject( "standard_resolution" );
//			thumbSize = new int[]{ thumb.getInt("width"), thumb.getInt( "height" ) };
//			thumbUrl = thumb.getString( "url" );
			lowResSize = new int[]{ low.getInt("width"), low.getInt( "height" ) };
			lowUrl = low.getString( "url" );
			standardResSize = new int[]{ standard.getInt("width"), standard.getInt( "height" ) };
			standardUrl = standard.getString( "url" );
		} catch (JSONException e) {
			Dlog.e(TAG, e);
		}
	}
}
