package com.snaps.facebook.model.sns.facebook;

import com.snaps.common.utils.log.Dlog;
import com.snaps.facebook.utils.FBCommonUtil;
import com.snaps.facebook.utils.sns.FacebookUtil.BookMaker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class AlbumData {
	private static final String TAG = AlbumData.class.getSimpleName();
	public static final int TYPE_WALL = 0;
	public static final int TYPE_MOBILE = 1;
	public static final int TYPE_PROFILE = 2;
	public static final int TYPE_COVER = 3;
	public static final int TYPE_NORMAL = 4;
	
	public String id;
	public String fromId;
	public String fromName;
	public String thumb;
	public String fullPicture;
	public int width, height, type;
	public long createTime;
	public Calendar createDate;
	
	public int[] summary;
	
	public ArrayList<CommentData> commentList = new ArrayList<CommentData>();
	public ArrayList<LikeData> likeList = new ArrayList<LikeData>();
	public ArrayList<SharedPostData> sharedList = new ArrayList<SharedPostData>();
	
	
	public AlbumData( int type ) {
		this.type = type;
	}
	
	public AlbumData( String jsonString, int type ) {
		JSONObject jobj = null;
		try {
			jobj = new JSONObject( jsonString );
		} catch (JSONException e) {
			Dlog.e(TAG, e);
			new AlbumData( type );
		}
		
		new AlbumData( jobj, type );
	}
	
	public int getPoint() { return commentList.size() + likeList.size(); }
	
	public AlbumData( JSONObject jobj, int type ) {
		this.type = type;
		
		JSONArray tempAry;
		JSONObject tempObj;
		
		try {
			id = jobj.has("id") ? jobj.getString( "id" ) : "";
			if( jobj.has("from") ) {
				fromId = jobj.getJSONObject( "from" ).has("id") ? jobj.getJSONObject( "from" ).getString( "id" ) : "";
				fromName = jobj.getJSONObject( "from" ).has("name") ? jobj.getJSONObject( "from" ).getString( "name" ) : "";
			}
			else {
				fromId = "";
				fromName = "";
			}
			
			thumb = jobj.has("picture") ? jobj.getString( "picture" ) : "";
			
			createTime = jobj.getLong( "created_time" );
			createDate = FBCommonUtil.getCalFromFBTime( createTime );
			
			fullPicture = "";
			width = 0;
			height = 0;
			
			if( jobj.has("images") ) {
				tempAry = jobj.getJSONArray( "images" );
				int w, h;
				for( int i = 0; i < tempAry.length(); ++i ) {
					tempObj = tempAry.getJSONObject( i );
					if( tempObj.getInt("width") > width ) {
						width = tempObj.getInt( "width" );
						height = tempObj.getInt( "height" );
						fullPicture = tempObj.getString( "source" );
					}
				}
			}

			if( jobj.has("sharedposts") ) {
				JSONArray jary = jobj.getJSONObject( "sharedposts" ).getJSONArray( "data" );
				for( int i = 0; i < jary.length(); ++i )
					sharedList.add( new SharedPostData(jary.getJSONObject(i)) );
			}
			if( jobj.has("comments") ) {
				JSONArray jary = jobj.getJSONObject( "comments" ).getJSONArray( "data" );
				for( int i = 0; i < jary.length(); ++i )
					commentList.add( new CommentData(jary.getJSONObject(i), CommentData.TYPE_NORMAL) );
			}
			if( jobj.has("likes") ) {
				JSONArray jary = jobj.getJSONObject( "likes" ).getJSONArray( "data" );
				for( int i = 0; i < jary.length(); ++i )
					likeList.add( new LikeData(jary.getJSONObject(i)) );
			}
			
			summary = new int[3];
			summary[BookMaker.LIKE_COUNT_INDEX] = likeList.size();
			summary[BookMaker.COMMENT_COUNT_INDEX] = 0;
			for( int i = 0; i < commentList.size(); ++i ) summary[BookMaker.COMMENT_COUNT_INDEX] += commentList.get(i).getTotalCommentCount();
			summary[BookMaker.SHARE_COUNT_INDEX] = 0; // 사진 공유는 없음. 
		} catch (JSONException e) {
			Dlog.e(TAG, e);
		}
	}
}
