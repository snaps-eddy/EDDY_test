package com.snaps.facebook.model.sns.facebook;

import com.snaps.common.utils.log.Dlog;

import org.json.JSONException;
import org.json.JSONObject;

public class FriendData {
	private static final String TAG = FriendData.class.getSimpleName();
	public String id;
	public String name;
	public String thumbUrl, originUrl;
	public int[] thumbSize = new int[2];
	public int[] originSize = new int[2];
	public int likeCount;
	public int commentCount;
	
	public FriendData( String id, String name ) {
		this.id = id;
		this.name = name;
		
		likeCount = 0;
		commentCount = 0;
	}
	
	public void setOriginProfile( JSONObject originObj ) {
		try {
			originUrl = originObj.has( "url" ) ? originObj.getString( "url" ) : "";
			
			if( originObj.has("width") && originObj.has("height") ) {
				originSize[0] = originObj.getInt( "width" );
				originSize[1] = originObj.getInt( "height" );
			}
		} catch (JSONException e) {
			Dlog.e(TAG, e);
		}		
	}
	
	public void setThumbProfile( JSONObject thumbObj ) {
		try {
			thumbUrl = thumbObj.has( "url" ) ? thumbObj.getString( "url" ) : "";
			
			if( thumbObj.has("width") && thumbObj.has("height") ) {
				thumbSize[0] = thumbObj.getInt( "width" );
				thumbSize[1] = thumbObj.getInt( "height" );
			}
		} catch (JSONException e) {
			Dlog.e(TAG, e);
		}		
	}
}
