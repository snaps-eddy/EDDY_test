package com.snaps.common.storybook;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.snaps.common.storybook.StoryData.ImageInfo;

public interface IStoryDataStrategy {

	public enum eSTORY_DATA_SORT_TYPE {
		POPULAR, // 인기 있는 게시물
		DATE_LASTEST, // 날짜 순
		NORMAL // 그대로
	}

	public enum eSTORY_STATICS_TYPE {
		MONTH_TOTAL_CNT_TYPE_INTEGER, // 달수
		PHOTO_TOTAL_CNT_TYPE_INTEGER, // 사진수
		STORY_TOTAL_CNT_TYPE_INTEGER, // 스토리수
		REPLY_TOTAL_CNT_TYPE_INTEGER, // 댓글수
	}

	String getUserName();
	Calendar getUserBirthDayCalendar();
	ImageInfo getUserImageUrl();
	// ImageInfo getUserThumbImageUrl();
	ImageInfo getCoverBackgroundImage();
	String getStoryPeriod();
	void setStoryPeriod(String startDate, String endDate);
	int getFeelCommentFriendCount();
	int getCommentFriendCount();
	int getFeelTotalCount();
	int getCommentTotalCount();
	int getNoteStoryCount();
	int getPhotoStoryCount();
	int getPhotoNoteStoryCount();

	// public int getDataByInt(DataKeyName key);
	// String getDataByString(DataKeyName key);
	// public ArrayList<String> getDataByArray(DataKeyName key);

	public ArrayList<StoryData> getSortedStories(eSTORY_DATA_SORT_TYPE sortType);
	public Object getStoryStatics(eSTORY_STATICS_TYPE type);
	public void getStoryies(String startDate, String endDate, int commentCount, int photoCount, IOnStoryDataLoadListener listener);
	public StoryData getStory(int index);
	public int getStoryCount();
	public ArrayList<StoryData> getStories();
	public StoryData getBestStoryData();
	public void removeStories(List<String> removeList);
	public void requestStoriesDetail();
	public void setStoryDataLoadListener(IOnStoryDataLoadListener ls);

	/** 신규 추가 07/31 **/
	/***
	 * 이미지 데이터를 가져오는 함수..
	 * 
	 * @param snsproperty
	 * @return
	 */
	public ImageInfo getImageData(String snsproperty);

	/***
	 * 텍스트 데이터를 가져오는 함수..
	 * 
	 * @param snsproperty
	 * @param format
	 * @return
	 */
	public String getTextData(String snsproperty, String format);

	/***
	 * 친구 이미지 리스트를 가져오는 함수
	 * 느낌 친구 + 댓글친구.
	 * @return
	 */
	public ArrayList<String> getFrientImageList();

}
