package com.snaps.instagram.model.sns.instagram;

import com.snaps.common.utils.log.Dlog;
import com.snaps.instagram.utils.instagram.InstagramApp;
import com.snaps.instagram.utils.instagram.InstagramApp.BookMaker;
import com.snaps.instagram.utils.instagram.InstagramApp.BookMaker.CompleteListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class PostData {
	private static final String TAG = PostData.class.getSimpleName();
	public final static int TYPE_IMAGE = 0;
	public final static int TYPE_VIDEO = 1;

	public String id, caption, filter, link;

	public int type, likeCount, commentCount;
	public long createdLong;
	public Calendar createdDate;
	public boolean userLiked;
//	public String content;

	public UserData owner;
	public ImageData image;
	public VideoData video;
	public LocationData location;

	public CompleteListener listener;

	public HashMap<String, CommentData> commentMap;
	public ArrayList<CommentData> commentList;
	public ArrayList<UserData> likedUserList;
	public ArrayList<UserData> inPhotoUserList;
	public ArrayList<String> tags;

	public PostData clone() {
		PostData newIns = new PostData();
		newIns.id = this.id;
		newIns.caption = this.caption;
		newIns.filter = this.filter;
		newIns.link = this.link;
		newIns.type = this.type;
		newIns.likeCount = this.likeCount;
		newIns.commentCount = this.commentCount;
		newIns.createdLong = this.createdLong;
		newIns.createdDate = this.createdDate;
		newIns.userLiked = this.userLiked;
		newIns.owner = this.owner;
		newIns.image = this.image.clone();
		newIns.video = this.video;
		newIns.location = this.location;
		newIns.listener = this.listener;
		newIns.commentMap = this.commentMap;
		newIns.commentList = this.commentList;
		newIns.inPhotoUserList = this.inPhotoUserList;
		newIns.tags = this.tags;
		return newIns;
	}

	public PostData() {}

	public PostData( JSONObject jobj ) {
		try {
			JSONArray jary;
			JSONObject temp;

			id = jobj.has( "id" ) ? jobj.getString("id") : "";
			caption = InstagramApp.jsonNotNullCheck( jobj, "caption" ) && jobj.getJSONObject( "caption" ).has( "text" ) ? jobj.getJSONObject( "caption" ).getString("text") : "";
			filter = jobj.has( "filter" ) ? jobj.getString("filter") : "";
			link = jobj.has( "link" ) ? jobj.getString("link") : "";
			type = jobj.has( "type" ) && jobj.getString( "type" ).startsWith( "video" ) ? TYPE_VIDEO : TYPE_IMAGE;
			createdLong = jobj.has( "created_time" ) ? jobj.getLong("created_time") : 0;
			createdDate = InstagramApp.getCalFromTimestamp( createdLong );
			userLiked = jobj.has( "user_has_liked" ) && jobj.getBoolean( "user_has_liked" );

			owner = jobj.has( "user" ) ? new UserData( jobj.getJSONObject("user") ) : null;
			image = jobj.has("images") ? new ImageData( jobj.getJSONObject("images") ) : null;
			video = jobj.has("videos") ? new VideoData( jobj.getJSONObject("videos") ) : null;
			location = InstagramApp.jsonNotNullCheck(jobj, "location") ? new LocationData( jobj.getJSONObject("location") ) : null;

			commentList = new ArrayList<CommentData>();
			commentMap = new HashMap<String, CommentData>();
			commentCount = 0;
			CommentData comment;
			if( jobj.has("comments") ) {
				temp = jobj.getJSONObject( "comments" );
				commentCount = temp.has( "count" ) ? temp.getInt( "count" ) : 0;
				if( temp.has("data") ) {
					jary = temp.getJSONArray( "data" );
					for( int i = 0; i < jary.length(); ++i ) {
						comment = new CommentData( jary.getJSONObject(i) );
						commentMap.put( comment.id, comment );
					}
				}
			}

			likedUserList = new ArrayList<UserData>();
			likeCount = 0;
			if( jobj.has("likes") ) {
				temp = jobj.getJSONObject( "likes" );
				likeCount = temp.has( "count" ) ? temp.getInt( "count" ) : 0;
				if( temp.has("data") ) {
					jary = temp.getJSONArray( "data" );
					for( int i = 0; i < jary.length(); ++i ) likedUserList.add( new UserData(jary.getJSONObject(i)) );
				}
			}

			inPhotoUserList = new ArrayList<UserData>();
			if( jobj.has("users_in_photo") ) {
				jary = jobj.getJSONArray( "users_in_photo" );
				for( int i = 0; i < jary.length(); ++i ) {
					temp = jary.getJSONObject( i );
					if( temp.has("user") ) {
						UserData user = new UserData( temp.getJSONObject("user") );
						if( temp.has("position") ) {
							temp = temp.getJSONObject( "position" );
							user.setPosition( temp.getDouble("x"), temp.getDouble("y") );
						}
					}
				}
			}

			tags = new ArrayList<String>();
			if( jobj.has("tags") ) {
				jary = jobj.getJSONArray( "tags" );
				for( int i = 0; i < jary.length(); ++i )
					tags.add( InstagramApp.decodeString(jary.getString(i)) );
			}
		} catch (JSONException e) {
			Dlog.e(TAG, e);
		}
	}

	public void setCompleteListener( CompleteListener listener ) { this.listener = listener; }
	public void clearListener() {
		if( listener != null ) listener.onComplete(null);
		if( image != null && image.listener != null ) image.listener.onComplete( null );
	}
	public void makeCommentData() {
		if( commentMap == null || commentMap.size() == commentCount ) {
			if( commentMap != null ) {
				commentList = new ArrayList<CommentData>( commentMap.values() );
				Collections.sort( commentList, new NumberAscCompare() );
			}
			Collections.sort( commentList, new NumberAscCompare() );
			if( listener != null ) {
				listener.onComplete( this );
				listener = null;
			}
			return;
		}

		URL url = null;
		try {
			String urlString = BookMaker.getInstance().getCommentsUrl( id );
			JSONObject jobj;
			JSONArray jary;
			CommentData comment;
			url = new URL(urlString);

			InputStream inputStream = url.openConnection().getInputStream();
			String response = InstagramApp.streamToString(inputStream);
			if( response != null && response.length() > 0 ) {
				jobj = new JSONObject( response );
				if( jobj.has("meta") && jobj.getJSONObject("meta").has("code") && jobj.getJSONObject("meta").getInt("code") == 200 && jobj.has("data") ) {
					jary = jobj.getJSONArray( "data" );
					for( int i = 0; i < jary.length(); ++i ) {
						comment = new CommentData( jary.getJSONObject(i) );
						commentMap.put(comment.id, comment);

					}
				}
			}

			commentList = new ArrayList<CommentData>( commentMap.values() );
			Collections.sort( commentList, new NumberAscCompare() );

			if( listener != null ) {
				listener.onComplete( this );
				listener = null;
			}
		} catch (IndexOutOfBoundsException e) {
			Dlog.e(TAG, e);
		} catch (MalformedURLException e) {
			Dlog.e(TAG, e);
		} catch (IOException e) {
			Dlog.e(TAG, e);
		} catch (JSONException e) {
			Dlog.e(TAG, e);
		} catch (NullPointerException e) {
			Dlog.e(TAG, e);
		}
	}

	/**
	 * 댓글 생성순
	 *
	 */
	public static class NumberAscCompare implements Comparator<CommentData> {
		@Override
		public int compare(CommentData arg0, CommentData arg1) {
			return arg0.createdLong < arg1.createdLong ? -1 : arg0.createdLong > arg1.createdLong ? 1 : 0;
		}
	}
}