package com.snaps.facebook.model.sns.facebook;

import com.snaps.common.utils.ui.StringUtil;
import com.snaps.facebook.utils.sns.FacebookUtil;
import com.snaps.facebook.utils.sns.FacebookUtil.BookMaker;

import java.util.ArrayList;
import java.util.Calendar;

public class ChapterData {
	public int chapterIndex = 0;
	public int pageIndex = 0;
	public int pageCounts = 0;
	public int areaCount = 0;
	public int[] response;
	public ArrayList<TimelineData> timelines;

	public ChapterData() {
		timelines = new ArrayList<TimelineData>();
	}

	public String getChapterIndexStr() {
		return chapterIndex < 10 ? "0" + chapterIndex : "" + chapterIndex;
	}
	public String getStartMonthStr( boolean isCapital ) {
		String yearStr = StringUtil.getMonthStr( ( timelines != null && timelines.size() > 0 ) ? timelines.get(0).createDate.get(Calendar.MONTH) : -1, false);
		int intYear = timelines.get(0).createDate.get( Calendar.YEAR );
		return yearStr.length() > 0 && isCapital ? yearStr.toUpperCase() : yearStr;
	}
	public String getEndMonthStr( boolean isCapital ) {
		String yearStr = StringUtil.getMonthStr( ( timelines != null && timelines.size() > 0 ) ? timelines.get(timelines.size() - 1).createDate.get(Calendar.MONTH) : -1, false );
		int intYear = timelines.get(0).createDate.get( Calendar.YEAR );
		return yearStr.length() > 0 && isCapital ? yearStr.toUpperCase() : yearStr;
	}

	public int getStartYear() {
		return ( timelines != null && timelines.size() > 0 ) ? timelines.get(0).year : -1;
	}
	public int getStartMonth() {
		return ( timelines != null && timelines.size() > 0 ) ? timelines.get(0).month : -1;
	}
	public int getEndYear() {
		return ( timelines != null && timelines.size() > 0 ) ? timelines.get( timelines.size() - 1 ).year : -1;
	}
	public int getEndMonth() {
		return ( timelines != null && timelines.size() > 0 ) ? timelines.get( timelines.size() - 1 ).month : -1;
	}
	public ArrayList<TimelineData> getTimeLines() {
		return timelines;
	}
	public TimelineData getTimeLine(int index) {
		if(timelines == null || timelines.size() <= index) return null;
		return timelines.get(index);
	}
	public TimelineData getFirstTimeline() {
		if( timelines == null ) return null;
		return timelines.get(0);
	}
	public TimelineData getLastTimeline() {
		if( timelines == null ) return null;
		return timelines.get( timelines.size() - 1 ) ;
	}
	public int[] getSummary() {
		int[] response = new int[3];

		for( int i = 0; i < timelines.size(); ++i )
			response = FacebookUtil.combineAry( response, timelines.get(i).getSummary() );

		return response;
	}
	public int[][] getPostCounts() {
		int[] postCount = new int[4];
		int[] sharedCount = new int[3];
		int[] messageCount = new int[]{0};

		TimelineData temp;
		boolean isShared;
		for( int index = 0; index < timelines.size(); ++index ) {
			temp = timelines.get( index );

			int type = temp.type;

			if( temp.feed != null ) {
				if( type == TimelineData.TYPE_MESSAGE && temp.feed != null && temp.feed.fromSomeone )
					messageCount[0] ++;
			}
			else if( !temp.post.sharedFromSomeone && temp.post.summary[BookMaker.SHARE_COUNT_INDEX] > 0 ) postCount[BookMaker.POST_SHARED_COUNT_INDEX] ++;
			else {
				isShared = temp.isSharedPost() || temp.isSharedPostWithoutWriter() ||  temp.hasSharedAttachment();

				switch( temp.post.type ) {
					case TimelineData.TYPE_LINK:
					case TimelineData.TYPE_STATUS:
						if( isShared ) sharedCount[BookMaker.SHARED_WRITE_COUNT_INDEX] ++;
						else postCount[BookMaker.POST_WRITE_COUNT_INDEX] ++;
						break;
					case TimelineData.TYPE_PHOTO:
						if( isShared ) sharedCount[BookMaker.SHARED_PHOTO_COUNT_INDEX] ++;
						else postCount[BookMaker.POST_PHOTO_COUNT_INDEX] ++;
						break;
					case TimelineData.TYPE_VIDEO:
						if( isShared ) sharedCount[BookMaker.SHARED_VIDEO_COUNT_INDEX] ++;
						else postCount[BookMaker.POST_VIDEO_COUNT_INDEX] ++;
						break;
				}
			}
		}

		return new int[][]{ postCount, sharedCount, messageCount };
	}

	public void refreshCount() { refreshCount( true ); }
	public void refreshCount( boolean doPageCountRefresh ) {
		areaCount = 0;
		if( timelines != null )
			for( int i = 0; i < timelines.size(); ++i )
				areaCount += timelines.get(i).areaCount;
		if( doPageCountRefresh ) pageCounts = areaCount / 4; // 대략적인 값.		
	}
}
