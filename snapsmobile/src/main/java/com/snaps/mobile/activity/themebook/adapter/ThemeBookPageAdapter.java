package com.snaps.mobile.activity.themebook.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.request.transition.Transition;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.imageloader.ImageLoader;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.edit.view.DialogDefaultProgress;
import com.snaps.mobile.activity.themebook.ThemeDesignListActivity;
import com.snaps.mobile.activity.themebook.holder.ThemeCoverHolder;
import com.snaps.mobile.utils.ui.SnapsImageViewTarget;

public class ThemeBookPageAdapter extends BaseAdapter {
	private static final String TAG = ThemeBookPageAdapter.class.getSimpleName();
	static final int PAGE_SPACING = 16;
	ThemeDesignListActivity coverAct;
	LayoutInflater inflater;

	int getGridColumnWidth;
	int imageWidth;
	public ThemeCoverHolder vh;

	private DialogDefaultProgress progress = null;

	private int loadComplateCount = 0;
	ThemeDesignListActivity.eDesignPhotoCnt designPhotoCnt = null;

	private boolean isLandscapeMode = false;

	public ThemeBookPageAdapter(Activity act, ThemeDesignListActivity.eDesignPhotoCnt designPhotoCnt) {
		inflater = LayoutInflater.from(act);
		this.coverAct = (ThemeDesignListActivity) act;

		loadComplateCount = 0;
		this.designPhotoCnt = designPhotoCnt;

		getGridColumnWidth = UIUtil.getGridColumnHeight(coverAct, Const_VALUE.IMAGE_ALBUM_COLS, Const_VALUE.IMAGE_PAGE_DESIGN_LIST_SPACING, Const_VALUE.IMAGE_PAGE_DESIGN_LIST_SPACING);
		imageWidth = getGridColumnWidth - UIUtil.convertDPtoPX(coverAct, Const_VALUE.IMAGE_COVER_DESIGN_FRAME);
		Dlog.d("ThemeBookPageAdapter() imageWidth:" + imageWidth);
//		imageLoader.setDiscCache(coverAct);// ImageLoader의 Thumbnail 사이즈를 사진선택에 맞게 변경
	}

	public void setGridColumnWidth() {
		int colsNums = Const_VALUE.IMAGE_ALBUM_COLS;
		if(isLandscapeMode) {
			colsNums = 3;
		}

		getGridColumnWidth = UIUtil.getGridColumnHeight(coverAct,
				colsNums, UIUtil.convertDPtoPX(coverAct,PAGE_SPACING),UIUtil.convertDPtoPX(coverAct,PAGE_SPACING), isLandscapeMode);
		imageWidth = getGridColumnWidth - UIUtil.convertDPtoPX(coverAct, Const_VALUE.IMAGE_COVER_DESIGN_FRAME);
		Dlog.d("setGridColumnWidth() imageWidth:" + imageWidth);
//		imageLoader.setDiscCache(coverAct);// ImageLoader의 Thumbnail 사이즈를 사진선택에 맞게 변경
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		try {
			if (convertView == null) {

				int layoutResId = R.layout.adapter_themebookpage_item;
				if (Const_PRODUCT.isPhotoCardProduct() || Const_PRODUCT.isNewWalletProduct())
					layoutResId = R.layout.adapter_photocard_change_design_item;

				convertView = inflater.inflate(layoutResId, parent, false);

				float widthRatio = 1.0f;
				float heightRatio = coverAct.mRatio;

				LinearLayout.LayoutParams mFrameLayoutParams = null;
				RelativeLayout.LayoutParams mImageLayoutParams = null;
				RelativeLayout.LayoutParams mSelectLayoutParams = null;

				if (Config.isSimplePhotoBook() || Config.isSimpleMakingBook()) {
					Dlog.d("getView() coverAct.mRatio:" + coverAct.mRatio);

					if (coverAct.mRatio > 100.f) {
						heightRatio = coverAct.mRatio - 101.f;
					} else {
						widthRatio = coverAct.mRatio;
					}

					mFrameLayoutParams = new LinearLayout.LayoutParams((int) (getGridColumnWidth), (int) (getGridColumnWidth / heightRatio));
					mImageLayoutParams = new RelativeLayout.LayoutParams((int)(getGridColumnWidth) , (int) (getGridColumnWidth / heightRatio));
					//아웃라인그릴때 width가 보이는 넓이보다 커서 마진값을 줬음
					mSelectLayoutParams = new RelativeLayout.LayoutParams((int)(getGridColumnWidth),(int) (getGridColumnWidth / heightRatio));

				} else {
					mFrameLayoutParams = new LinearLayout.LayoutParams(getGridColumnWidth, (int) (getGridColumnWidth / 0.7f));
					mImageLayoutParams = new RelativeLayout.LayoutParams(imageWidth, (int) (imageWidth / 0.7f));
					mSelectLayoutParams = new RelativeLayout.LayoutParams(imageWidth-UIUtil.convertDPtoPX(coverAct,isLandscapeMode() ? 8:10), (int) (imageWidth / 0.7f));
				}

				mImageLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);

				RelativeLayout layoutImgFrame = (RelativeLayout) convertView.findViewById(R.id.layoutImgFrame);
				layoutImgFrame.setLayoutParams(mFrameLayoutParams);

				ImageView coverAlbum = (ImageView) convertView.findViewById(R.id.imgCoverAlbum);
				coverAlbum.setLayoutParams(mImageLayoutParams);

				ImageView coverSelect = (ImageView) convertView.findViewById(R.id.img_select);

				ImageView outLine = null;

				outLine = (ImageView) convertView.findViewById(R.id.img_out_line);

				if (Const_PRODUCT.isPhotoCardProduct() || Const_PRODUCT.isNewWalletProduct()) {
					vh = new ThemeCoverHolder(coverAlbum, coverSelect, outLine, position);
				}else {
					RelativeLayout selectlayout  =(RelativeLayout)convertView.findViewById(R.id.select_layout);
					mSelectLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
					selectlayout.setLayoutParams(mSelectLayoutParams);
					vh = new ThemeCoverHolder(coverAlbum, coverSelect, outLine,selectlayout, position);
				}

				convertView.setTag(vh);
			} else {
				vh = (ThemeCoverHolder) convertView.getTag();
				convertView.forceLayout();
			}

			// 커버에 체크를 표시를 해야할지 말지 설정..
			if (coverAct.getDesignItem(designPhotoCnt, position).F_IS_SELECT) {
				vh.imgCoverSelect.setVisibility(View.VISIBLE);

				if (Const_PRODUCT.isPhotoCardProduct() || Const_PRODUCT.isNewWalletProduct()) {
					vh.imgOutLine.setBackgroundResource(isLandscapeMode() ? R.drawable.shape_image_border_change_design_item_select_for_landscape : R.drawable.shape_image_border_change_design_item_select);
				}else{
					vh.imgOutLine.setBackgroundResource(R.drawable.image_border_change_design_item_select);
					vh.selectLayout.setBackgroundColor(Color.parseColor("#66000000"));
				}
			} else {
				vh.imgCoverSelect.setVisibility(View.INVISIBLE);

				if (Const_PRODUCT.isPhotoCardProduct() || Const_PRODUCT.isNewWalletProduct()) {
					vh.imgOutLine.setBackgroundColor(0);
				}else{
					vh.selectLayout.setBackgroundColor(Color.parseColor("#00000000"));
					vh.imgOutLine.setBackgroundResource(R.drawable.image_border_change_design_item_non_select);
				}

			}

			String imgUrl = coverAct.getDesignItem(designPhotoCnt, position).F_SSMPL_URL;

            final String URL = SnapsAPI.DOMAIN(false) + imgUrl;
            SnapsImageViewTarget bitmapImageViewTarget = new SnapsImageViewTarget(coverAct, vh.imgCoverAlbum) {
                @Override
                public void onLoadStarted(@Nullable Drawable placeholder) {
                    super.onLoadStarted(placeholder);
                    setProgressVisible(true);
                }

                @Override
                public void onResourceReady(Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    super.onResourceReady(resource, transition);
                    int maxCount = Math.min(8, getCount());
                    if(++loadComplateCount >= maxCount)
                        setProgressVisible(false);
                }

                @Override
                public void onLoadFailed(@Nullable Drawable errorDrawable) {
                    super.onLoadFailed(errorDrawable);
                    setProgressVisible(false);
                }
            };

            ImageLoader.asyncDisplayImage(coverAct, URL, bitmapImageViewTarget);
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}

		return convertView;
	}

	public DialogDefaultProgress getProgress() {
		return progress;
	}

	public void setProgress(DialogDefaultProgress progress) {
		this.progress = progress;
	}

	@Override
	public int getCount() {
		if(coverAct != null && coverAct.getDesignListFromMap(designPhotoCnt) != null)
			return coverAct.getDesignListFromMap(designPhotoCnt).size();
		else
			return 0;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	private void setProgressVisible(boolean isShow) {
	}

//	@Override
//	public void onLoadingStarted() {
//		setProgressVisible(true);
//	}
//
//	@Override
//	public void onLoadingFailed(@Nullable Drawable errorDrawable) {
//		setProgressVisible(false);
//	}
//
//	@Override
//	public void onLoadingComplete(Bitmap resource, ImageView imageView) {
//		int maxCount = Math.min(8, getCount());
//		if(++loadComplateCount >= maxCount)
//			setProgressVisible(false);
//	}
//
//	@Override
//	public void onLoadingCancelled() {
//		setProgressVisible(false);
//	}

	public void destroy() {
		setProgressVisible(false);
	}

	public boolean isLandscapeMode() {
		return isLandscapeMode;
	}

	public void setLandscapeMode(boolean landscapeMode) {
		isLandscapeMode = landscapeMode;
	}
}
