package com.snaps.facebook.model.sns.facebook;

import android.os.Bundle;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.facebook.utils.FBCommonUtil;
import com.snaps.facebook.utils.sns.FacebookUtil.BookMaker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class PostData {
	private static final String TAG = PostData.class.getSimpleName();
	public int type = TimelineData.TYPE_STATUS;

	public String id;
	public String name;
	public String thumb;
	public String fullPicture;
	public String targetUrl;
	public String message;
	public String story;
	public String description;
	public long originCreateTime;
	public Calendar originCreateDate;
	public long createTime;
	public Calendar createDate;
	public boolean sharedFromSomeone = false;
	public boolean sharedFromMine = false;

	public int[] summary;

	public ArrayList<CommentData> commentList = new ArrayList<CommentData>();
	public ArrayList<LikeData> likeList = new ArrayList<LikeData>();
	public ArrayList<AttachmentData> attachmentList = new ArrayList<AttachmentData>();
	public ArrayList<StoryTag> storyTagList = new ArrayList<StoryTag>();


	public String albumId = "";


	public PostData() {

	}

	public FriendData getSharedPostOriginWriter() {
		BookMaker maker = BookMaker.getInstance();

		String id = getSharedPostOriginWriterId();
		if( id != null && id.length() > 0 ) return maker.friendsMap.get( id );
		return null;
	}

	public String getSharedPostOriginWriterId() {
		BookMaker maker = BookMaker.getInstance();

		// 포스트이고, 스토리에 {본인이름}님이 ~~~를 공유했습니다 라고 포함되어 있고, story태그에서 원글의 게시자 정보를 찾을 수 있을 때 처리.
		// post에서 공유한 글인지, 내 글인지 구분할만한 요소가 story의 text뿐이라 조건문이 지저분함.
		String id = "";
		if( story != null && story.contains(maker.name + "님이") && (story.contains("공유했습니다.") || story.contains("게시물을 올렸습니다.")) && storyTagList != null && storyTagList.size() > 0 ) {
			for( int i = 0; i < storyTagList.size(); ++i ) {
				if( !storyTagList.get(i).id.equals(maker.id) ) {
					id = storyTagList.get(i).id;
					break;
				}
			}
			return id;
		}
		return "";
	}

	public boolean hasSharedAttachment() {
		if( attachmentList != null && attachmentList.size() > 0 ) {
			for( int i = 0; i < attachmentList.size(); ++i ) {
				if( attachmentList.get(i).rawType.startsWith("share") ) return true;
			}
		}
		return false;
	}

	public boolean isSharedPost() {
		return getSharedPostOriginWriter() != null || getSharedPostOriginWriterId().length() > 0;
	}

	public boolean isMemorySharePost() {
		return !StringUtil.isEmpty( story ) && story.contains( "추억을 공유했습니다" );
	}

	public boolean isSharedPostWithoutWriter() {
		for( int i = 0; i < attachmentList.size(); ++i ) {
			if( attachmentList.get(i).rawType != null && attachmentList.get(i).rawType.contains("share") ) return true;
		}

		return false;
	}

	public boolean isVideoPostWithoutThumbnail() {
		if( type == TimelineData.TYPE_VIDEO ) {
			if( StringUtil.isEmpty(fullPicture) && StringUtil.isEmpty(thumb) && (attachmentList == null || attachmentList.size() < 1) ) return true;
		}
		return false;
	}

	public boolean isPostedByQuestionApplication() {
		if( storyTagList != null && storyTagList.size() > 0 ) {
			for( int i = 0; i < storyTagList.size(); ++i ) {
				if( "application".equals(storyTagList.get(i).rawType) && storyTagList.get(i).name != null && storyTagList.get(i).name.length() > 0 && storyTagList.get(i).name.contains("questions") ) return true;
			}
		}
		return false;
	}

	public String getThumbUrl() {
		if( attachmentList != null && attachmentList.size() > 0 ) return attachmentList.get(0).thumbUrl;
		return fullPicture;
	}

	public GraphRequest getSharedWriteCreatedTime( final BookMaker maker ) {
		if( attachmentList == null || attachmentList.size() < 1 ) return null;
		final String targetId = attachmentList.get(0).targetId;
		if( targetId == null || targetId.length() < 1 ) return null;

		GraphRequest request = GraphRequest.newGraphPathRequest(AccessToken.getCurrentAccessToken(), targetId, new GraphRequest.Callback() {
			@Override
			public void onCompleted(GraphResponse response) {
				try {
					JSONObject jobj = response.getJSONObject();
					if(jobj == null) {
						maker.checkRefreshProcessDone( response.getRequest() );
						return;
					}

					originCreateTime = jobj.has( "created_time" ) ? jobj.getLong( "created_time" ) : 0;
					originCreateDate = originCreateTime > 0 ? FBCommonUtil.getCalFromFBTime( originCreateTime ) : null;

					maker.checkRefreshProcessDone( response.getRequest() );
				} catch (JSONException e) {
					maker.checkRefreshProcessDone( response.getRequest() );
					Dlog.e(TAG, e);
				}
			}
		});

		Bundle parameters = new Bundle();
		parameters.putString( "fields", "created_time,name,id" );
		parameters.putString("date_format", "U");
		request.setParameters(parameters);
		return request;
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

	public PostData( String jsonString ) {
		JSONObject jobj = null;
		try {
			jobj = new JSONObject( jsonString );
		} catch (JSONException e) {
			Dlog.e(TAG, e);
			new PostData();
		}

		new PostData( jobj );
	}

	public PostData( JSONObject jobj ) {
		String typeStr;
		try {
			typeStr = jobj.has("type") ? jobj.getString( "type" ) : "";
			type = TimelineData.TYPE_STATUS;
			switch( typeStr ) {
				case "photo": type = TimelineData.TYPE_PHOTO; break;
				case "link": type = TimelineData.TYPE_LINK; break;
			}
			if( typeStr.startsWith("video") ) type = TimelineData.TYPE_VIDEO;

			id = jobj.has("id") ? jobj.getString( "id" ) : "";
			name = jobj.has("name") ? jobj.getString( "name" ) : "";
			thumb = jobj.has("picture") ? jobj.getString( "picture" ) : "";
			fullPicture = jobj.has("full_picture") ? jobj.getString("full_picture") : "";
			createTime = jobj.has( "created_time" ) ? jobj.getLong("created_time") : 0;
			createDate = createTime > 0 ? FBCommonUtil.getCalFromFBTime(createTime) : null;
			message = jobj.has("message") ? jobj.getString("message") : "";
			story = jobj.has("story") ? jobj.getString( "story" ) : "";
			description = jobj.has("description") ? jobj.getString( "description" ) : "";

			// 이전에 있던 note기능으로 포스트 게시했을 경우 예외처리.
			if( !StringUtil.isEmpty(story) && story.contains("노트를 게시했습니다") ) {
				JSONObject tempJobj;
				JSONArray tempJary;
				if( jobj.has("attachments") ) {
					tempJobj = jobj.getJSONObject( "attachments" );
					if( tempJobj.has("data") ) {
						tempJary = tempJobj.getJSONArray( "data" );
						if( tempJary != null && tempJary.length() > 0 ) {
							tempJobj = tempJary.getJSONObject(0);
							if( tempJobj.has("description") && tempJobj.getString("description").length() > 0 ) {
								message += "\n" + tempJobj.getString("description");
							}
						}
					}
				}
			}
			// Youtube에서 동영상 올렸을 경우 type이 status로 오니까 바꿔줘야함.
			else if( !StringUtil.isEmpty(story) && story.contains("YouTube에서 게시물을 올렸습니다") ) {
				JSONObject tempJobj;
				JSONArray tempJary;
				if( jobj.has("attachments") ) {
					tempJobj = jobj.getJSONObject( "attachments" );
					if( tempJobj.has("data") ) {
						tempJary = tempJobj.getJSONArray( "data" );
						if( tempJary != null && tempJary.length() > 0 ) {
							tempJobj = tempJary.getJSONObject(0);
							if( tempJobj.has("type") && tempJobj.getString("type").startsWith("video") ) {
								type = TimelineData.TYPE_VIDEO;
							}
						}
					}
				}
			}

			int sharedCount = jobj.has( "shares" ) ? jobj.getJSONObject( "shares" ).getInt( "count" ) : 0;

			String fromId = jobj.has( "from" ) ? jobj.getJSONObject( "from" ).getString( "id" ) : "";
			if( jobj.has("story_tags") && fromId.length() > 0 ) {
				JSONArray jary = jobj.getJSONArray( "story_tags" );
				if( jary.length() > 0 && !fromId.equals(jary.getJSONObject(0).getString("id")) && jary.getJSONObject(0).getString("name").length() > 0 )
					sharedFromSomeone = true;
				sharedFromMine = !sharedFromSomeone;
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
			if( jobj.has("attachments") ) {
				JSONArray jary = jobj.getJSONObject( "attachments" ).getJSONArray( "data" );
				for( int i = 0; i < jary.length(); ++i ) {
					ArrayList<AttachmentData> tempList = AttachmentData.makeAttList(jary.getJSONObject(i), message, type, "");
					attachmentList.addAll( tempList );
				}
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
		refreshSummary( summary[BookMaker.SHARE_COUNT_INDEX] );
	}
	private void refreshSummary( int sharedCount ) {
		summary = new int[3];
		summary[BookMaker.LIKE_COUNT_INDEX] = likeList.size();
		summary[BookMaker.COMMENT_COUNT_INDEX] = 0;
		for( int i = 0; i < commentList.size(); ++i ) summary[BookMaker.COMMENT_COUNT_INDEX] += commentList.get(i).getTotalCommentCount();
		summary[BookMaker.SHARE_COUNT_INDEX] = sharedCount;
	}
}