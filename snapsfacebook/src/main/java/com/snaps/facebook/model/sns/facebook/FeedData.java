package com.snaps.facebook.model.sns.facebook;

import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.net.http.HttpUtil;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.facebook.utils.FBCommonUtil;
import com.snaps.facebook.utils.sns.FacebookUtil.BookMaker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

public class FeedData {
	private static final String TAG = FeedData.class.getSimpleName();
	public int type = TimelineData.TYPE_STATUS;

	public String id;
	public String fromId;
	public String fromName;
	public String thumb;
	public String fullPicture;
	public String name;
	public String message;
	public String event;
	public String description;
	public String story;
	public long createTime;
	public Calendar createDate;

	public boolean fromSomeone = false;

	public int[] summary;

	public ArrayList<CommentData> commentList = new ArrayList<CommentData>();
	public ArrayList<LikeData> likeList = new ArrayList<LikeData>();
	public ArrayList<AttachmentData> attachmentList = new ArrayList<AttachmentData>();
	public ArrayList<StoryTag> storyTagList = new ArrayList<StoryTag>();

	public String albumId = "";

	public String getThumbUrl() {
		if( attachmentList != null && attachmentList.size() > 0 ) return attachmentList.get(0).thumbUrl;
		return fullPicture;
	}

	public boolean isTaggedWrite() {
		return storyTagList != null && storyTagList.size() > 0;
	}

	public boolean isPostedByQuestionApplication() {
		if( storyTagList != null && storyTagList.size() > 0 ) {
			for( int i = 0; i < storyTagList.size(); ++i ) {
				if( "application".equals(storyTagList.get(i).rawType) && storyTagList.get(i).name != null && storyTagList.get(i).name.length() > 0 && storyTagList.get(i).name.contains("questions") ) return true;
			}
		}
		return false;
	}

	public boolean isVideoPostWithoutThumbnail() {
		if( type == TimelineData.TYPE_VIDEO ) {
			if( StringUtil.isEmpty(fullPicture) && StringUtil.isEmpty(thumb) && (attachmentList == null || attachmentList.size() < 1) ) return true;
		}
		return false;
	}

	public FeedData() {

	}

	public FeedData( String jsonString ) {
		JSONObject jobj = null;
		try {
			jobj = new JSONObject( jsonString );
		} catch (JSONException e) {
			Dlog.e(TAG, e);
			new FeedData();
		}

		new FeedData( jobj );
	}

	public void calculateImageSize( final BookMaker maker, ArrayList<AsyncTask> taskList ) {
		if( attachmentList != null && attachmentList.size() == 1 && attachmentList.get(0).width < 1 && attachmentList.get(0).height < 1 ) {
			AsyncTask.execute( new Runnable() {
				@Override
				public void run() {
					setSize( attachmentList.get(0) );

					if( attachmentList.get(0).width < 1 || attachmentList.get(0).height < 1 ) {
						if (attachmentList.get(0).width == AttachmentData.INVALID_IMAGE_DIMENSION
								|| attachmentList.get(0).height == AttachmentData.INVALID_IMAGE_DIMENSION) {
							Rect imageRect = HttpUtil.getNetworkImageRect(attachmentList.get(0).imageUrl);
							attachmentList.get(0).width = imageRect.width();
							attachmentList.get(0).height = imageRect.height();
						} else {
							int[] size = getSizeFromFacebookImageUrl( attachmentList.get(0).imageUrl );
							if( size[0] < 1 || size[1] < 1 ) size = getSizeFromFacebookImageUrl( attachmentList.get(0).thumbUrl );

							attachmentList.get(0).width = size[0];
							attachmentList.get(0).height = size[1];
						}
					}
				}
			});
		}
	}

	private void setSize( AttachmentData attData ) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		Rect r = new Rect();
		try {
			BitmapFactory.decodeStream((InputStream)new URL(attData.imageUrl).getContent(), r, options);
			attData.height = options.outHeight;
			attData.width = options.outWidth;

		} catch (MalformedURLException e) {
			Dlog.e(TAG, e);
		} catch (IOException e) {
			Dlog.e(TAG, e);
		}
	}

	private int[] getSizeFromFacebookImageUrl( String url ) {
		int[] result = new int[2];

		String[] parsed = url.split( "/" );
		if( parsed != null && parsed.length > 0 ) {
			for( int i = 0; i < parsed.length; ++i ) {
				if( parsed[i].startsWith("s") && parsed[i].contains("x") && parsed[i].length() < 11 ) { // max s9999x9999 10
					parsed = parsed[i].substring( 1, parsed[i].length() ).split( "x" );
					try {
						result[0] = Integer.parseInt( parsed[0] );
						result[1] = Integer.parseInt( parsed[1] );
					} catch( NumberFormatException e ) {
						Dlog.e(TAG, e);
						continue;
					}
					break;
				}
			}
		}

		return result;
	}

	public GraphRequest refreshCommentAndLike( final BookMaker maker ) {
		if( albumId == null || albumId.length() < 1 ) return null;

		GraphRequest request = GraphRequest.newGraphPathRequest(AccessToken.getCurrentAccessToken(), albumId, new GraphRequest.Callback() {
			@Override
			public void onCompleted(GraphResponse response) {
				try {
					JSONObject jobj = response.getJSONObject();
					if(jobj == null) {
						maker.checkRefreshProcessDone( response.getRequest() );
						return;
					}

					commentList = new ArrayList<CommentData>();
					likeList = new ArrayList<LikeData>();
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

					refreshSummary();

					maker.checkRefreshProcessDone( response.getRequest() );
				} catch (JSONException e) {
					maker.checkRefreshProcessDone( response.getRequest() );
					Dlog.e(TAG, e);
				}
			}
		});

		Bundle parameters = new Bundle();
		parameters.putString( "fields", "likes.limit(100),comments.limit(100){comments.limit(100){created_time,id,from,message},message,created_time,from,id}" );
		parameters.putString("date_format", "U");
		request.setParameters(parameters);
		return request;
	}

	public FeedData( JSONObject jobj ) {
		String typeStr;
		try {
			typeStr = jobj.has( "type" ) ? jobj.getString( "type" ) : "";
			type = TimelineData.TYPE_STATUS;
			switch( typeStr ) {
				case "photo": type = TimelineData.TYPE_PHOTO; break;
				case "video": type = TimelineData.TYPE_VIDEO; break;
				case "link": type = TimelineData.TYPE_LINK; break;
			}

			if( jobj.has("from") ) {
				fromId = jobj.getJSONObject( "from" ).getString( "id" );
				fromName = jobj.getJSONObject( "from" ).getString( "name" );

				fromSomeone = !fromId.equals( BookMaker.getInstance().id );
			}
			id = jobj.has("id") ? jobj.getString( "id" ) : "";
			createTime = jobj.getLong( "created_time" );
			createDate = FBCommonUtil.getCalFromFBTime( createTime );
			fullPicture = jobj.has("full_picture") ? jobj.getString( "full_picture" ) : "";
			thumb = jobj.has("picture") ? jobj.getString( "picture" ) : "";
			name = jobj.has("name") ? jobj.getString( "name" ) : "";
			message = jobj.has("message") ? jobj.getString( "message" ) : "";
			event = jobj.has("event") ? jobj.getString( "event" ) : "";
			description = jobj.has("description") ? jobj.getString( "description" ) : "";
			int sharedCount = jobj.has( "shares" ) ? jobj.getJSONObject( "shares" ).getInt( "count" ) : 0;
			story = jobj.has("story") ? jobj.getString( "story" ) : "";

			if( (fullPicture == null || fullPicture.length() < 1) && thumb != null && thumb.length() > 0 ) fullPicture = thumb;
			if( (thumb == null || thumb.length() < 1) && fullPicture != null && fullPicture.length() > 0 ) thumb = fullPicture;

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

			if( jobj.has("attachments") ) {
				JSONArray jary = jobj.getJSONObject( "attachments" ).getJSONArray( "data" );
				for( int i = 0; i < jary.length(); ++i )
					attachmentList.addAll( AttachmentData.makeAttList(jary.getJSONObject(i), message, type, "") );
			}
			else if( fullPicture.length() > 0 && thumb.length() > 0 ) {
				AttachmentData tempAtt = new AttachmentData( fullPicture, thumb );
				attachmentList.add( tempAtt );
			}

			if( jobj.has("story_tags") ) {
				JSONArray jary = jobj.getJSONArray( "story_tags" );
				for( int i = 0; i < jary.length(); ++i ) {
					storyTagList.add( new StoryTag(jary.getJSONObject(i)) );
				}
			}

			if( attachmentList != null && attachmentList.size() > 0 ) {
				albumId = "";
				for( int i = 0; i < attachmentList.size(); ++i ) {
					if( attachmentList.get(i).albumId != null && attachmentList.get(i).albumId.length() > 0 ) {
						albumId = attachmentList.get(i).albumId;
						break;
					}
				}
			}
			refreshSummary( sharedCount );
		} catch (JSONException e) {
			Dlog.e(TAG, e);
		}
	}

	private void refreshSummary() {
		refreshSummary(summary[BookMaker.SHARE_COUNT_INDEX]);
	}
	private void refreshSummary( int sharedCount ) {
		summary = new int[3];
		summary[BookMaker.LIKE_COUNT_INDEX] = likeList.size();
		summary[BookMaker.COMMENT_COUNT_INDEX] = 0;
		for( int i = 0; i < commentList.size(); ++i ) summary[BookMaker.COMMENT_COUNT_INDEX] += commentList.get(i).getTotalCommentCount();
		summary[BookMaker.SHARE_COUNT_INDEX] = sharedCount;
	}
}