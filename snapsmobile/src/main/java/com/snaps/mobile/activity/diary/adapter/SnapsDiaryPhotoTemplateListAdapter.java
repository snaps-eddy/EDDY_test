package com.snaps.mobile.activity.diary.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.request.transition.Transition;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.imageloader.ImageLoader;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.diary.activities.SnapsDiarySelectPhotoTemplateActivity;
import com.snaps.mobile.activity.edit.view.DialogDefaultProgress;
import com.snaps.mobile.utils.ui.SnapsImageViewTarget;

public class SnapsDiaryPhotoTemplateListAdapter extends BaseAdapter {
	private static final String TAG = SnapsDiaryPhotoTemplateListAdapter.class.getSimpleName();
	SnapsDiarySelectPhotoTemplateActivity coverAct;
	LayoutInflater inflater;

	int getGridColumnWidth;
	int imageWidth;
	public DesignHolder vh;

	private DialogDefaultProgress progress = null;

	private int loadComplateCount = 0;

	public SnapsDiaryPhotoTemplateListAdapter(Activity act) {
		inflater = LayoutInflater.from(act);
		this.coverAct = (SnapsDiarySelectPhotoTemplateActivity) act;

		loadComplateCount = 0;

		getGridColumnWidth = UIUtil.getGridColumnHeight(coverAct, SnapsDiaryBaseAdapter.GRID_COLUMN_COUNT, Const_VALUE.IMAGE_PAGE_DESIGN_LIST_SPACING, Const_VALUE.IMAGE_PAGE_DESIGN_LIST_SPACING);
		imageWidth = getGridColumnWidth - UIUtil.convertDPtoPX(coverAct, Const_VALUE.IMAGE_COVER_DESIGN_FRAME);
		Dlog.d("SnapsDiaryPhotoTemplateListAdapter() imageWidth:" + imageWidth);
//		imageLoader.setDiscCache(coverAct);// ImageLoader의 Thumbnail 사이즈를 사진선택에 맞게 변경
	}
	
	public void setGridColumnWidth(boolean isLandscapeMode) {
		getGridColumnWidth = UIUtil.getGridColumnHeight(coverAct,
				SnapsDiaryBaseAdapter.GRID_COLUMN_COUNT, 20, 20, isLandscapeMode);
		Dlog.d("setGridColumnWidth() imageWidth:" + imageWidth);
//		imageLoader.setDiscCache(coverAct);// ImageLoader의 Thumbnail 사이즈를 사진선택에 맞게 변경
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		try {
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.snaps_diary_photo_template_item, parent, false);

				RelativeLayout.LayoutParams mFrameLayoutParams = new RelativeLayout.LayoutParams(getGridColumnWidth, getGridColumnWidth);

				RelativeLayout layoutImgFrame = (RelativeLayout) convertView.findViewById(R.id.layoutImgFrame);
				layoutImgFrame.setLayoutParams(mFrameLayoutParams);

				RelativeLayout layoutCoverBg = (RelativeLayout) convertView.findViewById(R.id.imgCoverBg);

				final ImageView coverAlbum = (ImageView) convertView.findViewById(R.id.imgCoverAlbum);

				ImageView coverSelect = (ImageView) convertView.findViewById(R.id.img_select);

				vh = new DesignHolder(layoutCoverBg, coverAlbum, coverSelect, position);
				convertView.setTag(vh);

				String imgUrl = coverAct.getTemplateItem(position).F_SSMPL_URL;

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
						if (coverAlbum != null) {
							coverAlbum.setScaleType(ImageView.ScaleType.FIT_XY);
						}
					}

					@Override
					public void onLoadFailed(@Nullable Drawable errorDrawable) {
						super.onLoadFailed(errorDrawable);
						setProgressVisible(false);
					}
				};

				ImageLoader.asyncDisplayImage(coverAct, URL, bitmapImageViewTarget);
			} else {
				vh = (DesignHolder) convertView.getTag();
				convertView.forceLayout();
			}

			// 커버에 체크를 표시를 해야할지 말지 설정..
			if (coverAct.getTemplateItem(position).F_IS_SELECT) {
				vh.imgCoverBg.setBackgroundColor(coverAct.getResources().getColor(R.color.color_diary_template_seleted));
				vh.imgCoverSelect.setVisibility(View.VISIBLE);
			} else {
				vh.imgCoverBg.setBackgroundColor(coverAct.getResources().getColor(R.color.color_diary_template_normal));
				vh.imgCoverSelect.setVisibility(View.INVISIBLE);
			}
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
		if(coverAct != null && coverAct.getDesignList() != null)
			return coverAct.getDesignList().size();
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
	
	public void destroy() {
		setProgressVisible(false);
	}

	public static class DesignHolder {
		public RelativeLayout imgCoverBg;
		public ImageView imgCoverAlbum;
		public ImageView imgCoverSelect;
		public int index;

		public DesignHolder(RelativeLayout imgCoverBg, ImageView imgCoverAlbum, ImageView imgCoverSelect, int index) {
			this.imgCoverBg = imgCoverBg;
			this.imgCoverAlbum = imgCoverAlbum;
			this.imgCoverSelect = imgCoverSelect;
			this.index = index;
		}

	}

}
