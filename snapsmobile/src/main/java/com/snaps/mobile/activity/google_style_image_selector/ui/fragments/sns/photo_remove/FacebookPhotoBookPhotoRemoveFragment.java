package com.snaps.mobile.activity.google_style_image_selector.ui.fragments.sns.photo_remove;

import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.facebook.model.sns.facebook.TimelineData;
import com.snaps.facebook.utils.sns.FacebookUtil.BookMaker;
import com.snaps.facebook.utils.sns.FacebookUtil.ProcessListener;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.book.SNSBookFragmentActivity;

import java.util.HashMap;
import java.util.LinkedList;

public class FacebookPhotoBookPhotoRemoveFragment extends SNSBookRemoveStyleBaseFragment {
	private static final String TAG = FacebookPhotoBookPhotoRemoveFragment.class.getSimpleName();

	public FacebookPhotoBookPhotoRemoveFragment() {}

	public static FacebookPhotoBookPhotoRemoveFragment getInstance() {
		FacebookPhotoBookPhotoRemoveFragment detailFrag = new FacebookPhotoBookPhotoRemoveFragment();
		detailFrag.setType(SNSBookFragmentActivity.TYPE_FACEBOOK_PHOTOBOOK);
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
		BookMaker maker = BookMaker.getInstance();

		if (maker.timelines == null || maker.timelines.isEmpty()) return false;

		LinkedList<TimelineData> queData = new LinkedList<TimelineData>();
		for(int idx = iStartIdx; idx <= iEndIdx; idx++) {
			TimelineData data = maker.timelines.get(idx);
			if(data != null)
				queData.add(data);
		}

		if (queData == null || queData.isEmpty())
			return false;

		while(!queData.isEmpty()) {
			TimelineData data = queData.poll();

			if ( data == null ) continue;

			String thumbnailPath = null;
			String title = null;

			if( data.imageUrl != null && !data.imageUrl.isEmpty() ) {
				thumbnailPath = data.imageUrl;
				title = "";
			} else if( data.content != null && data.content.trim().length() > 0 ) {
				thumbnailPath = "";
				title = data.content;
			}

			HashMap<String, String> contact = new HashMap<String, String>();
			contact.put( TAG_Thumbnail, thumbnailPath );
			contact.put( TAG_NAME, title );

			if (m_arrAllDataList != null) {
				contact.put( TAG_ID, "" + m_arrAllDataList.size() );
				m_arrAllDataList.add( contact );
			}
		}

		return true;
	}

	@Override
	protected int getDataCount() {
		BookMaker maker = BookMaker.getInstance();
		int count = 0;
		if( maker.timelines != null ) count = maker.timelines.size();
		return count;
	}

	@Override
	protected boolean checkDataManager() { return true; }

	@Override
	protected void getData() {
		try {
			final BookMaker maker = BookMaker.getInstance();
			maker.execute(imageSelectActivityV2, new ProcessListener() {
				@Override
				public void onFail(final Object result) {
					if (imageSelectActivityV2 != null) {
						if (pd != null)
							pd.dismiss();
						imageSelectActivityV2.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (maker.timelines == null || maker.timelines.size() < 1) {
									MessageUtil.toast(imageSelectActivityV2, getString(R.string.facebook_photobook_error_no_post_exist) + " (" + result.toString() + ")");
									imageSelectActivityV2.finish();
									return;
								}
								MessageUtil.toast(imageSelectActivityV2, result.toString());
								imageSelectActivityV2.finish();
							}
						});
					}
				}

				@Override
				public void onError(final Object result) {
					if (imageSelectActivityV2 != null) {
						if (pd != null)
							pd.dismiss();

						imageSelectActivityV2.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (maker.timelines == null || maker.timelines.size() < 1) {
									MessageUtil.toast(imageSelectActivityV2, getString(R.string.facebook_photobook_error_no_post_exist) + " (" + result.toString() + ")");
									imageSelectActivityV2.finish();
									return;
								}
								MessageUtil.toast(imageSelectActivityV2, result.toString());
								imageSelectActivityV2.finish();
							}
						});
					}
				}

				@Override
				public void onComplete(Object result) {
					if (imageSelectActivityV2 != null) {
						if (pd != null)
							pd.dismiss();
						maker.makeTimelineList(false);

						imageSelectActivityV2.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (maker.timelines == null || maker.timelines.size() < 1) {
									MessageUtil.toast(imageSelectActivityV2, getString(R.string.facebook_photobook_error_no_post_exist));
									imageSelectActivityV2.finish();
									return;
								}
								imageLoading(m_iCursor);
							}
						});
					}
				}
			});
		} catch (Exception e) {
			Dlog.e(TAG, e);
			MessageUtil.toast(imageSelectActivityV2, e.toString());
			imageSelectActivityV2.finish();
		}
	}

	@Override
	public boolean isExistAlbumList() {
		return false;
	}
}