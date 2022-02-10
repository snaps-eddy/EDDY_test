package com.snaps.mobile.activity.google_style_image_selector.ui.fragments.sns.photo_remove;

import com.snaps.common.storybook.IOnStoryDataLoadListener;
import com.snaps.common.storybook.StoryData;
import com.snaps.common.storybook.StoryData.ImageInfo;
import com.snaps.mobile.activity.book.SNSBookFragmentActivity;
import com.snaps.mobile.activity.book.StoryBookCommonUtil;
import com.snaps.mobile.activity.book.StoryBookDataManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class KakaobookBookPhotoRemoveFragment extends SNSBookRemoveStyleBaseFragment {
	private StoryBookDataManager storybookMan;

	private ArrayList<StoryData> m_arrStoryList = null;


	public KakaobookBookPhotoRemoveFragment() {}

	public static KakaobookBookPhotoRemoveFragment getInstance() {
		KakaobookBookPhotoRemoveFragment detailFrag = new KakaobookBookPhotoRemoveFragment();
		detailFrag.setType(SNSBookFragmentActivity.TYPE_KAKAO_STORY);
		return detailFrag;
	}

	@Override
	protected void setLoadIndex( int cursor, int dataCount ) {
		iStartIdx = Math.min((cursor * CNT), dataCount - 1);
		iEndIdx = Math.min((iStartIdx + (CNT - 1)), dataCount - 1);
		isMoreImg = iEndIdx < dataCount - 1;
	}

	@Override
	protected boolean addImageData() {
		LinkedList<StoryData> queData = new LinkedList<StoryData>();
		for(int idx = iStartIdx; idx <= iEndIdx; idx++) {
			StoryData data = m_arrStoryList.get(idx);
			if(data != null)
				queData.add(data);
		}

		if ( queData.isEmpty() )
			return false;

		while(!queData.isEmpty()) {
			StoryData data = queData.poll();
			
			if (data == null)
				continue;

			String thumbnailPath = null;
			String originalPath = null;
			String title = null;

			if(data.images != null && !data.images.isEmpty()) {
				ImageInfo imgInfo = data.images.get(0);
				thumbnailPath = imgInfo.small;
				originalPath = imgInfo.original;
				
				title = "";

				HashMap<String, String> contact = new HashMap<String, String>();
				contact.put(TAG_Image, originalPath);
				contact.put(TAG_Thumbnail, thumbnailPath);
				contact.put(TAG_Width, String.valueOf(imgInfo.getOriginWidth()));
				contact.put(TAG_Height, String.valueOf(imgInfo.getOriginHeight()));
				contact.put(TAG_NAME, title);
				contact.put(TAG_ID, data.id);

				if (m_arrAllDataList != null) {
					if (!isDuplicateData(m_arrAllDataList, contact)) {
						m_arrAllDataList.add(contact);
					}
				}
				
			} else {
				String content = data.content;
				if(content != null && content.trim().length() > 0) {
					
					title = content;
					
					HashMap<String, String> contact = new HashMap<String, String>();
					contact.put(TAG_Image, "content");
					contact.put(TAG_Thumbnail, "content");
					contact.put(TAG_Width, "0");
					contact.put(TAG_Height, "0");
					contact.put(TAG_NAME, title);
					contact.put(TAG_ID, data.id);
					
					if (m_arrAllDataList != null) {
						if (!isDuplicateData(m_arrAllDataList, contact)) {
							m_arrAllDataList.add(contact);
						}
					}
				}
			}
		}

		return true;
	}

	@Override
	protected int getDataCount() {
		int count = 0;
		if( m_arrStoryList != null ) count = m_arrStoryList.size();
		return count;
	}

	@Override
	protected boolean checkDataManager() {
		storybookMan = StoryBookDataManager.getInstance();
		return storybookMan != null;
	}

	@Override
	protected void getData() {
		storybookMan.getStoryies(storybookMan.getStartDate(),
				storybookMan.getEndDate(),
				storybookMan.getCommentCount(),
				storybookMan.getPhotoCount(),
				new IOnStoryDataLoadListener() {
					@Override
					public void onStoryProfileLoadComplete() {
					}

					@Override
					public void onStoryLoadStateUpdate(int type, int value) {
					}

					@Override
					public void onStoryDetailLoadComplete() {
					}

					@Override
					public void onStoryLoadFail(int errorCode) {
						if (imageSelectActivityV2 != null) {
							if (pd != null)
								pd.dismiss();
							StoryBookCommonUtil.showErrMsg(imageSelectActivityV2, imageSelectActivityV2.getApplicationContext(), errorCode);
							imageSelectActivityV2.finish();
						}
					}

					@SuppressWarnings("unchecked")
					@Override
					public void onStoryListLoadComplete(
							ArrayList<StoryData> list) {
						if (list != null && !list.isEmpty()) {

							m_arrStoryList = (ArrayList<StoryData>) list.clone();//dataManager.getSortedStories(eSTORY_DATA_SORT_TYPE.NORMAL);
							imageLoading(m_iCursor);
						} else {
							if (imageSelectActivityV2 != null) {
								if (pd != null)
									pd.dismiss();
								StoryBookCommonUtil.showErrMsg(imageSelectActivityV2, imageSelectActivityV2.getApplicationContext(), ERR_CODE_INVALID_PERIOD);
								imageSelectActivityV2.finish();
							}
						}
					}
				});
	}

	@Override
	public boolean isExistAlbumList() {
		return false;
	}
}
