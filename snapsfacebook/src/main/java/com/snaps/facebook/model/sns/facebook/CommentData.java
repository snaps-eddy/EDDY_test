package com.snaps.facebook.model.sns.facebook;

import com.snaps.common.utils.log.Dlog;
import com.snaps.facebook.utils.FBCommonUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class CommentData {
	private static final String TAG = CommentData.class.getSimpleName();
	public static final int TYPE_NORMAL = 0;
	public static final int TYPE_SUB = 1;
	
	public int type = TYPE_NORMAL;
	
	public String id;
	public String fromId;
	public String fromName;
	public String message;
	public String nextPage;
	
	public long createTime;
	public Calendar createDate;
	
	public ArrayList<CommentData> subCommentList = new ArrayList<CommentData>();
	public ArrayList<com.snaps.facebook.model.sns.facebook.LikeData> likeList = new ArrayList<LikeData>();
	
	public CommentData( int typeStr ) {
		
	}
	
	public CommentData( String jsonString, int typeStr ) {
		JSONObject jobj = null;
		try {
			jobj = new JSONObject( jsonString );
		} catch (JSONException e) {
			Dlog.e(TAG, e);
			new CommentData( typeStr );
		}
		
		new CommentData( jobj, typeStr );
	}
	
	public CommentData( JSONObject jobj, int typeStr ) {
		try {
			type = typeStr == TYPE_SUB ? typeStr : TYPE_NORMAL;
			
			id = jobj.getString( "id" );
			fromId = jobj.has( "from" ) ? jobj.getJSONObject( "from" ).getString( "id" ) : "";
			fromName = jobj.has( "from" ) ? jobj.getJSONObject( "from" ).getString( "name" ) : "";
			message = jobj.has( "message" ) ? jobj.getString( "message" ) : "";
			if( jobj.has("paging") && jobj.getJSONObject("paging").has("cursors") && jobj.getJSONObject("paging").getJSONObject("cursors").has("next") )
				nextPage = jobj.getJSONObject( "paging" ).getJSONObject( "cursors" ).getString( "next" );
			else nextPage = "";
			
			createTime = jobj.getLong( "created_time" );
			createDate = FBCommonUtil.getCalFromFBTime( createTime );
			
			if( jobj.has("comments") ) {
				JSONArray jary = jobj.getJSONObject( "comments" ).getJSONArray( "data" );
				for( int i = 0; i < jary.length(); ++i ) {
					subCommentList.add( new CommentData(jary.getJSONObject(i), CommentData.TYPE_SUB) );
				}
			}
			if( jobj.has("likes") ) {
				JSONArray jary = jobj.getJSONObject( "likes" ).getJSONArray( "data" );
				for( int i = 0; i < jary.length(); ++i )
					likeList.add( new com.snaps.facebook.model.sns.facebook.LikeData(jary.getJSONObject(i)) );
			}
		} catch (JSONException e) {
			Dlog.e(TAG, e);
		}
	}
	
	public int getTotalCommentCount() {
		int count = 1;
		if( subCommentList != null ) count += subCommentList.size(); 
		return count;
	}

}
