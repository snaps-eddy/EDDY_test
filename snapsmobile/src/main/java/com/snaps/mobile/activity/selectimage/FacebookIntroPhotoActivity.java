package com.snaps.mobile.activity.selectimage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.snaps.common.data.img.MyFacebookImageData;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_EKEY;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.IFacebook;
import com.snaps.common.utils.ui.IFacebook.OnPaging;
import com.snaps.common.utils.ui.SnsFactory;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.selectimage.adapter.ImageFacebookPhotoAdapter;
import com.snaps.mobile.activity.selectimage.adapter.viewholder.ImageDetailHolder;

import org.json.JSONArray;
import org.json.JSONObject;

import errorhandle.CatchFragmentActivity;
import font.FProgressDialog;

public class FacebookIntroPhotoActivity extends CatchFragmentActivity {
	private static final String TAG = FacebookIntroPhotoActivity.class.getSimpleName();

	static final int MAX_FACEBOOK_IMG_COUNT = 28;// 카카오스토리에서 한번에 최대로 읽을 수 있는 갯수

	GridView gridDetail;
	ImageFacebookPhotoAdapter facebookDetailAdapter;
	int pageIdx = 0;
	int reloadAfterPosition;

	ImageView backbtn;


	// config
	boolean isScrollTouch = false;
	boolean isLoading = false;
	boolean isMoreImg = true;

	private IFacebook facebook;
	
	String nextPageKey = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_imagefacebookdetail);
		backbtn = (ImageView) findViewById(R.id.btnfaceTopBack);

		if (Config.isFacebookService())
			facebook = SnsFactory.getInstance().queryInteface();

		// facebook.Init(this);

		backbtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
				overridePendingTransition(0, R.anim.uptodown);
			}
		});

		try {

			facebookDetailAdapter = new ImageFacebookPhotoAdapter(this);
			gridDetail = (GridView) findViewById(R.id.gridDetail);
			gridDetail.setAdapter(facebookDetailAdapter);
			OnScrollListener onScrollListener = new OnScrollListener() {
				@Override
				public void onScrollStateChanged(AbsListView view, int scrollState) {
					if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE)
						isScrollTouch = false;
					else
						isScrollTouch = true;
				}

				@Override
				public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
					boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount - 1;
					if (isScrollTouch && loadMore && !isLoading && isMoreImg) {
						isLoading = true;
						reloadAfterPosition = firstVisibleItem;
						facebookImgLoading(true, nextPageKey, -1, -1);// 기존 list에 추가적으로 로딩
					}
				}
			};
			gridDetail.setOnScrollListener(onScrollListener);
			gridDetail.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					final ImageDetailHolder vh = (ImageDetailHolder) view.getTag();
					Intent intent = getIntent().putExtra(Const_EKEY.FACEBOOK_INTRO_PHOTO, vh.imgData.PATH);
					setResult(RESULT_OK, intent);
					finish();

				}
			});

			facebookImgLoading(false, nextPageKey, -1, -1);// 기존 list에 추가적으로 로딩
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
	}

	@Override
	public void onResume() {
		super.onResume();

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		pageIdx = 0;
		isMoreImg = true;
	}

	void facebookImgLoading(final boolean isAddReload, String _nextPageKey, final int width, final int height) {
		final FProgressDialog pd = new FProgressDialog(this);
		pd.setMessage(getString(R.string.please_wait));
		pd.setCancelable(false);
		pd.show();

		OnPaging pageListener = new OnPaging() {

			@Override
			public void onPagingComplete(JSONObject jsonObj) {
				pd.dismiss();

				try {
					JSONArray dataArrays = jsonObj.optJSONArray("data");

					nextPageKey = null;
					if(!jsonObj.isNull("paging")) {
						JSONObject pagingObj = (JSONObject) jsonObj.get("paging");
						if(pagingObj != null && !pagingObj.isNull("next")) {
							Object nextObj = pagingObj.get("next");
							if(nextObj != null) {
								JSONObject cursorsObj = (JSONObject) pagingObj.get("cursors");
								if(cursorsObj != null) {
									Object nextKey = cursorsObj.get("after");
									if(nextKey != null)
										nextPageKey = (String) nextKey;							
								}
							}
						}
					}
					
					isMoreImg = nextPageKey != null && nextPageKey.length() > 0;//dataArrays.length() == MAX_FACEBOOK_IMG_COUNT;// 최대조회갯수와 같으면 더 있을것으로 봄.

					JSONObject arrayObject = null;
					for (int i = 0; i < dataArrays.length(); i++) {
						MyFacebookImageData image = new MyFacebookImageData();
						arrayObject = dataArrays.optJSONObject(i);

						image.ID = arrayObject.optString("id", "");
						image.createdAt = StringUtil.getFBDatetoLong(FacebookIntroPhotoActivity.this, 
								arrayObject.optString("created_time")); //2015-05-13T01:40:31+0000
						
						JSONArray imgArrays = arrayObject.optJSONArray("images");
						if(imgArrays != null && imgArrays.length() > 0) {
							JSONObject orgImgObj = imgArrays.optJSONObject(0);
							JSONObject thumbImgObj = imgArrays.optJSONObject(imgArrays.length()-1);
							
							if(orgImgObj != null) {
								image.ORIGIN_IMAGE_DATA = orgImgObj.getString("source");
								image.ORIGIN_IMAGE_WIDTH = orgImgObj.optString("width", "");
								image.ORIGIN_IMAGE_HEIGHT = orgImgObj.optString("height", "");
							}
							
							if(thumbImgObj != null) {
								image.THUMBNAIL_IMAGE_DATA = thumbImgObj.getString("source");
							}
						}
						
						if (width == -1 && height == -1) {
							facebookDetailAdapter.add(image);
						} else {
							try {
								if(width <= Integer.parseInt(image.ORIGIN_IMAGE_WIDTH) 
										&& height <= Integer.parseInt(image.ORIGIN_IMAGE_HEIGHT))
								{
									facebookDetailAdapter.add(image);
								}
							} catch (NumberFormatException e) {
								Dlog.e(TAG, e);
							}
						}
					}
					facebookDetailAdapter.notifyDataSetChanged();

					if (isAddReload)
						gridDetail.setSelection(reloadAfterPosition);// 추가로딩이면 최종 리스트의 마지막 지점만큼 스크롤
				} catch (Exception e) {
					Dlog.e(TAG, e);
				}

				isLoading = false;

			}
		};
		
		if (Config.isFacebookService()) 
		{
			if(facebook != null)
				facebook.facebookGetPhotos(this, _nextPageKey, MAX_FACEBOOK_IMG_COUNT, pageListener);
			nextPageKey = null;
		}
	}
}
