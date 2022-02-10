package com.snaps.facebook.model.sns.facebook;

import com.snaps.common.utils.ui.ContextUtil;
import com.snaps.facebook.R;
import com.snaps.facebook.utils.sns.FacebookUtil.BookMaker;

import java.util.ArrayList;
import java.util.Calendar;

public class TimelineData {
	public static final int TYPE_STATUS = 0;
	public static final int TYPE_PHOTO = 1;
	public static final int TYPE_VIDEO = 2;
	public static final int TYPE_LINK = 3;
	public static final int TYPE_MESSAGE = 4;

	public int type = TYPE_STATUS;
	public int year, month, day, hour, min;
	public String dayOfWeek, hourStr;

	public int areaCount = 0; // 대략적으로 차지하는 공간을 저장. 4개가 1장(2페이지)이다.

	public PostData post;
	public FeedData feed;
	public long createTime;
	public Calendar createDate;

	public String content;
	public String imageUrl;

	public TimelineData() {

	}

	public FriendData getSharedPostOriginWriter() {
		if( post != null ) return post.getSharedPostOriginWriter();
		return null;
	}

	// story tag중에 application이 없으면 유저의 글을 공유한걸로 본다.
	public boolean isSharedFromUser() {
		if( post != null && post.storyTagList != null && post.storyTagList.size() > 0 ) {
			for( int i = 0; i < post.storyTagList.size(); ++i ) {
				if( "application".equals(post.storyTagList.get(i).rawType) ) return false;
			}
			return true;
		}
		return false;
	}

	public boolean isMemorySharePost() {
		return post != null && post.isMemorySharePost();
	}

	public Calendar getSharedPostCreatedTime() {
		if( post != null ) return post.originCreateDate;
		return null;
	}

	public String getSharedApplicationName() {
		String name = "";
		if( post != null && post.storyTagList != null && post.storyTagList.size() > 0 ) {
			for( int i = 0; i < post.storyTagList.size(); ++i ) {
				if( "application".equals(post.storyTagList.get(i).rawType) ) {
					name = post.storyTagList.get(i).name;
					break;
				}
			}
		}

		return name;
	}

	public boolean isSharedPost() {
		return post != null && post.isSharedPost();
	}

	public ArrayList<AttachmentData> getAttachments() {
		if( type == TYPE_MESSAGE ) return feed.attachmentList;
		else return post.attachmentList;
	}

	public String getMessage() {
		if( type == TYPE_MESSAGE ) return feed.message;
		else return post.message;
	}

	public String getId() {
		if( type == TYPE_MESSAGE ) return feed.id;
		else return post.id;
	}

	public int[] getSummary() {
		int[] result = new int[3];
		if( type != TimelineData.TYPE_MESSAGE ) result = post.summary;
		else result = feed.summary;
		return result;
	}

	public boolean hasSummaryData() {
		int[] summary = getSummary();
		for( int i = 0; i < summary.length; ++i ) {
			if( summary[i] > 0 ) return true;
		}
		return false;
	}

	public boolean isSharedPostWithoutWriter() {
		return post != null && post.isSharedPostWithoutWriter();
	}

	public boolean hasSharedAttachment() {
		return post != null && post.hasSharedAttachment();
	}

	public int getPoint() {
		int point = 0;
		int[] ary = getSummary();
		for( int i = 0; i < ary.length; ++i ) point += ary[i];
		return point;
	}

	public TimelineData( PostData post ) {
		type = post.type;
		this.post = post;
		createTime = post.createTime;
		createDate = post.createDate;

		imageUrl = post.getThumbUrl();
		if( post.message != null && post.message.length() > 0 ) content = post.message;

		init();
	}

	public TimelineData( FeedData feed ) {
		type = TYPE_MESSAGE;
		this.feed = feed;
		createTime = feed.createTime;
		createDate = feed.createDate;

		imageUrl = feed.getThumbUrl();
		if( feed.message != null && feed.message.length() > 0 ) content = feed.message;

		init();
	}

	private void init() {
		if( imageUrl == null ) imageUrl = "";

		year = createDate.get( Calendar.YEAR );
		month = createDate.get( Calendar.MONTH ) + 1;
		day = createDate.get( Calendar.DAY_OF_MONTH );
		dayOfWeek = ContextUtil.getString(R.string.day_of_week, "요일");

		switch( createDate.get(Calendar.DAY_OF_WEEK) ) {
			case Calendar.MONDAY: dayOfWeek = ContextUtil.getString(R.string.monday, "월요일"); break;
			case Calendar.TUESDAY: dayOfWeek = ContextUtil.getString(R.string.tuesday, "화요일"); break;
			case Calendar.WEDNESDAY: dayOfWeek = ContextUtil.getString(R.string.wednesday, "수요일"); break;
			case Calendar.THURSDAY: dayOfWeek = ContextUtil.getString(R.string.thursday, "목요일"); break;
			case Calendar.FRIDAY: dayOfWeek = ContextUtil.getString(R.string.friday, "금요일"); break;
			case Calendar.SATURDAY: dayOfWeek = ContextUtil.getString(R.string.saturday, "토요일"); break;
			case Calendar.SUNDAY: dayOfWeek = ContextUtil.getString(R.string.sunday, "일요일"); break;
		}
		hourStr = createDate.get( Calendar.AM_PM ) == Calendar.AM ? "am" : "pm";
		hour = createDate.get(Calendar.HOUR);
		min = createDate.get( Calendar.MINUTE );
	}

	public TimelineData clone() {
		TimelineData data = new TimelineData();
		data.type = type;
		data.year = year;
		data.month = month;
		data.day = day;
		data.hour = hour;
		data.min = min;
		data.dayOfWeek = dayOfWeek;
		data.hourStr = hourStr;
		data.areaCount = areaCount;
		data.post = post;
		data.feed = feed;
		data.createDate = createDate;
		data.content = content;
		data.imageUrl = imageUrl;
		return data;
	}

	public String getStory() {
		if( type != TYPE_MESSAGE ) return post.story;
		else return feed.story;
	}

	public String getFullPictureUrl() {
		if( type != TYPE_MESSAGE ) return post.fullPicture;
		else return feed.fullPicture;
	}

	public String getDescription() {
		if( type != TYPE_MESSAGE ) return post.description;
		else return feed.description;
	}

	public String getThumbnailUrl() {
		if( type != TYPE_MESSAGE ) return post.thumb;
		else return feed.thumb;
	}

	public ArrayList<LikeData> getLikeList() {
		if( type != TYPE_MESSAGE ) return post.likeList;
		else return feed.likeList;
	}

	public ArrayList<CommentData> getCommentList() {
		if( type != TYPE_MESSAGE ) return post.commentList;
		else return feed.commentList;
	}

	public int getShareCount() {
		if( type != TYPE_MESSAGE ) return post.summary[BookMaker.SHARE_COUNT_INDEX];
		else return feed.summary[BookMaker.SHARE_COUNT_INDEX];
	}

	public ArrayList<String> getAttachmentIdList() {
		ArrayList<String> list = new ArrayList<String>();

		if( type != TYPE_MESSAGE && post.attachmentList != null && post.attachmentList.size() > 0 )
			for( int i = 0; i < post.attachmentList.size(); ++i ) list.add( post.attachmentList.get(i).targetId );
		else if( type == TYPE_MESSAGE && feed.attachmentList != null && feed.attachmentList.size() > 0 )
			for( int i = 0; i < feed.attachmentList.size(); ++i ) list.add( feed.attachmentList.get(i).targetId );

		return list;
	}
}