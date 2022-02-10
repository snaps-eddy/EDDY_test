package com.snaps.instagram.model.sns.instagram;

import com.snaps.common.utils.log.Dlog;
import com.snaps.instagram.utils.instagram.InstagramApp;

import org.json.JSONException;
import org.json.JSONObject;

public class UserData {
	private static final String TAG = UserData.class.getSimpleName();
	public String id, name, fullname;
	public ImageData profile; // 작성자인 경우만 있는 데이터.
	public double[] position; // 태그된 유저데이터에만.
	public int follow, follower, media; 

	public UserData() {}
	
	public UserData( JSONObject jobj ) {
		try {
			id = jobj.has( "id" ) ? jobj.getString( "id" ) : "";
			name = jobj.has( "username" ) ? jobj.getString("username") : "";
			fullname = jobj.has( "full_name" ) ? jobj.getString( "full_name" ) : "";//InstagramApp.decodeString( jobj.getString("full_name") ) : "";
			String profileUrl = jobj.has( "profile_picture" ) ? jobj.getString( "profile_picture" ) : "";
			if( profileUrl != null && profileUrl.length() > 0 ) {
				profile = new ImageData( profileUrl );
			}
			if( jobj.has("counts") ) {
				jobj = jobj.getJSONObject( "counts" );
				media = jobj.has( "media" ) ? jobj.getInt( "media" ) : 0;
				follow = jobj.has( "follows" ) ? jobj.getInt( "follows" ) : 0;
				follower = jobj.has( "followed_by" ) ? jobj.getInt( "followed_by" ) : 0;
			}
		} catch (JSONException e) {
			Dlog.e(TAG, e);
		}
	}
	
	public void setPosition( double x, double y ) {
		position = new double[]{ x, y };
	}
}
