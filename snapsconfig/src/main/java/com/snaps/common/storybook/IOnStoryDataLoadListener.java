package com.snaps.common.storybook;

import java.util.ArrayList;

public interface IOnStoryDataLoadListener {
	
	public static final int PROGRESS_TYPE_GET_STORY_USER_INFO 		= 0;
	public static final int PROGRESS_TYPE_GET_STORY_LIST_INFO 		= 1;
	public static final int PROGRESS_TYPE_GET_STORY_DETAIL_INFO 	= 2;
	
	public static final int ERR_CODE_INVALID_PERIOD 				= 1001;
	public static final int ERR_CODE_FAILED_GET_USER_PROFILE 		= 1002;
	public static final int ERR_CODE_FAILED_GET_STORY_LIST 			= 1003;
	public static final int ERR_CODE_FAILED_GET_STORY_DETAIL 		= 1004;
	
	void onStoryProfileLoadComplete();
	void onStoryListLoadComplete(ArrayList<com.snaps.common.storybook.StoryData> list);
	void onStoryDetailLoadComplete();
	void onStoryLoadStateUpdate(int type, int value);
	void onStoryLoadFail(int errorCode);
}
