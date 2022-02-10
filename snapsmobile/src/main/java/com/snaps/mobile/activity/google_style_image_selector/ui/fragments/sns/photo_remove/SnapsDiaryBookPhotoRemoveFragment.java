package com.snaps.mobile.activity.google_style_image_selector.ui.fragments.sns.photo_remove;

import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.book.SNSBookFragmentActivity;
import com.snaps.mobile.activity.diary.SnapsDiaryDataManager;
import com.snaps.mobile.activity.diary.SnapsDiaryInterfaceUtil;
import com.snaps.mobile.activity.diary.json.SnapsDiaryListItemJson;
import com.snaps.mobile.activity.diary.json.SnapsDiaryListJson;
import com.snaps.mobile.activity.diary.recoder.SnapsDiaryListInfo;
import com.snaps.mobile.activity.diary.recoder.SnapsDiaryListItem;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class SnapsDiaryBookPhotoRemoveFragment extends SNSBookRemoveStyleBaseFragment {

	public SnapsDiaryBookPhotoRemoveFragment() {}

	public static SnapsDiaryBookPhotoRemoveFragment getInstance() {
		SnapsDiaryBookPhotoRemoveFragment detailFrag = new SnapsDiaryBookPhotoRemoveFragment();
		detailFrag.setType(SNSBookFragmentActivity.TYPE_DIARY);
		return detailFrag;
	}

	@Override
	protected void setLoadIndex( int cursor, int dataCount ) {

		if (imageSelectActivityV2 != null)
			imageSelectActivityV2.setMaxImageCount(dataCount);

		iStartIdx = Math.min((cursor * CNT), dataCount - 1);
		iEndIdx = Math.min((iStartIdx + (CNT - 1)), dataCount - 1);
		isMoreImg = iEndIdx < dataCount - 1;
	}


	@Override
	protected boolean addImageData() {
		SnapsDiaryListInfo listInfo = SnapsDiaryDataManager.getInstance().getPublishListInfo();
		if ( listInfo == null || listInfo.getArrDiaryList() == null || listInfo.getArrDiaryList().isEmpty() ) return false;

		LinkedList<SnapsDiaryListItem> queData = new LinkedList<SnapsDiaryListItem>();
		for (int idx = iStartIdx; idx < iEndIdx + 1 ; idx++) {
			SnapsDiaryListItem data = listInfo.getArrDiaryList().get(idx);
			if (data != null)
				queData.add(data);
		}


		if (queData == null || queData.isEmpty())
			return false;

		while(!queData.isEmpty()) {
			SnapsDiaryListItem data = queData.poll();
			
			if (data == null)
				continue;

			String thumbnailPath = data.getThumbnailUrl();;
			String title = data.getDiaryNo();
			String osType = data.getOsType();

			HashMap<String, String> contact = new HashMap<String, String>();
			contact.put( TAG_Thumbnail, thumbnailPath );
			contact.put( TAG_NAME, title );
			contact.put( TAG_OS_TYPE, osType);

			if (m_arrAllDataList != null) {
				contact.put( TAG_ID, "" + m_arrAllDataList.size() );
				m_arrAllDataList.add( contact );
			}
		}
		
		return true;
	}

	@Override
	protected int getDataCount() {
		SnapsDiaryListInfo listInfo = SnapsDiaryDataManager.getInstance().getPublishListInfo();
		int count = 0;
		if( listInfo != null && listInfo.getArrDiaryList() != null ) count = listInfo.getArrDiaryList().size();
		return count;
	}

	@Override
	protected boolean checkDataManager() {
		return true;
	}

	@Override
	protected void getData() {
		SnapsDiaryInterfaceUtil.getDiaryList( getActivity(), SnapsDiaryDataManager.getInstance().getPageInfo(true), new SnapsDiaryInterfaceUtil.ISnapsDiaryInterfaceCallback() {
			@Override
			public void onPreperation() {}

			@Override
			public void onResult(boolean result, Object resultObj) {
				if(imageSelectActivityV2 != null) {
					if (pd != null)
						pd.dismiss();

					boolean noData = resultObj == null;
					List<SnapsDiaryListItemJson> arrJsonResult = null;
					if( !noData ) {
						SnapsDiaryListJson listJson = (SnapsDiaryListJson) resultObj;
						arrJsonResult = listJson.getDiaryList();
						if( arrJsonResult == null || arrJsonResult.isEmpty() ) noData = true;
					}

					if( noData ) {
						imageSelectActivityV2.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								MessageUtil.toast(imageSelectActivityV2, getString(R.string.snaps_diary_empty_post_error_message_2));
								imageSelectActivityV2.finish();
							}
						});
						return;
					}

					SnapsDiaryListInfo listInfo = new SnapsDiaryListInfo();
					listInfo.addDiaryList(arrJsonResult);
					SnapsDiaryDataManager.getInstance().setPublishListInfo( listInfo );

					imageSelectActivityV2.runOnUiThread(new Runnable() {
						@Override
						public void run() { imageLoading(m_iCursor); }
					} );
				}
			}
		});
	}

	@Override
	public boolean isExistAlbumList() {
		return false;
	}
}
