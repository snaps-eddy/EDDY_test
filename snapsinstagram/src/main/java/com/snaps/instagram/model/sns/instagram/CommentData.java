package com.snaps.instagram.model.sns.instagram;

import com.snaps.common.utils.log.Dlog;
import com.snaps.instagram.utils.instagram.InstagramApp;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class CommentData {
	private static final String TAG = CommentData.class.getSimpleName();
	public String id, content;
	public UserData owner;
	public long createdLong;
	public Calendar createdDate;


	public CommentData( JSONObject jobj ) {
		try {
			id = jobj.has( "id" ) ? jobj.getString( "id" ) : "";
			content = jobj.has( "text" ) ? jobj.getString( "text" ) : "";
			owner = jobj.has( "from" ) ? new UserData( jobj.getJSONObject("from") ) : null;
			createdLong = jobj.has( "created_time" ) ? jobj.getLong( "created_time") : 0;
			createdDate = InstagramApp.getCalFromTimestamp( createdLong );
		} catch (JSONException e) { Dlog.e(TAG, e); }
	}
}
