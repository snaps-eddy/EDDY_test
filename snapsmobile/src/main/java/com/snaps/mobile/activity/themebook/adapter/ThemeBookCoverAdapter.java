package com.snaps.mobile.activity.themebook.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.imageloader.ImageLoader;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.themebook.ThemeCoverActivity;
import com.snaps.mobile.activity.themebook.holder.ThemeCoverHolder;

import errorhandle.logger.Logg;

public class ThemeBookCoverAdapter extends BaseAdapter {
	private static final String TAG = ThemeBookCoverAdapter.class.getSimpleName();
	static final int ALBUM_MAGIN_H = 36;
	static final int ALBUM_MAGIN_SPACING = 8;
	static final int ALBUM_MAGIN_V = 20;
	ThemeCoverActivity coverAct;
	LayoutInflater inflater;

	int getGridColumnWidth;
	int imageWidth;
	public ThemeCoverHolder vh;

	public ThemeBookCoverAdapter(Activity act) {
		inflater = LayoutInflater.from(act);
		this.coverAct = (ThemeCoverActivity) act;

		getGridColumnWidth = UIUtil.getGridColumnHeight(coverAct, Const_VALUE.IMAGE_ALBUM_COLS, UIUtil.convertDPtoPX(coverAct,Const_VALUE.IMAGE_ALBUM_SPACING),  UIUtil.convertDPtoPX(coverAct,Const_VALUE.IMAGE_ALBUM_SPACING));
		imageWidth = getGridColumnWidth - UIUtil.convertDPtoPX(coverAct, Const_VALUE.IMAGE_COVER_DESIGN_FRAME);
		Dlog.d("ThemeBookCoverAdapter() imageWidth:" + imageWidth);
//		imageLoader.setDiscCache(act);// ImageLoader의 Thumbnail 사이즈를 사진선택에 맞게 변경
	}
	
	public void setGridColumnWidth(boolean isLandscapeMode) {
		int colsNums = Const_VALUE.IMAGE_ALBUM_COLS;
		if(isLandscapeMode) {
			colsNums = 4;
			getGridColumnWidth = UIUtil.getGridColumnHeight(coverAct,
					colsNums, UIUtil.convertDPtoPX(coverAct,ALBUM_MAGIN_SPACING), UIUtil.convertDPtoPX(coverAct,ALBUM_MAGIN_H), isLandscapeMode);
		}else {
			getGridColumnWidth = UIUtil.getGridColumnHeight(coverAct,
					colsNums, UIUtil.convertDPtoPX(coverAct,ALBUM_MAGIN_V), UIUtil.convertDPtoPX(coverAct,ALBUM_MAGIN_SPACING), isLandscapeMode);

		}

		imageWidth = getGridColumnWidth - UIUtil.convertDPtoPX(coverAct, Const_VALUE.IMAGE_COVER_DESIGN_FRAME);
		Dlog.d("setGridColumnWidth() imageWidth:" + imageWidth);
//		imageLoader.setDiscCache(coverAct);// ImageLoader의 Thumbnail 사이즈를 사진선택에 맞게 변경
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		int pos = position;// % coverAct.xmlThemeCover.bgList.size();
		try {
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.adapter_themebookcover_item, parent, false);

				float widthRatio = 1.0f;
				float heightRatio = 1.0f;

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

//					mFrameLayoutParams = new LinearLayout.LayoutParams((int) (getGridColumnWidth * widthRatio), (int) (getGridColumnWidth / heightRatio));
					mFrameLayoutParams = new LinearLayout.LayoutParams((int) (getGridColumnWidth ), (int) (getGridColumnWidth ));
					mImageLayoutParams = new RelativeLayout.LayoutParams((int) (getGridColumnWidth ), (int) (getGridColumnWidth ));
					mSelectLayoutParams = new RelativeLayout.LayoutParams((int)(getGridColumnWidth ),(int) (getGridColumnWidth ));

				} else {
//					mFrameLayoutParams = new LinearLayout.LayoutParams(getGridColumnWidth, (int) (getGridColumnWidth / 0.7f));
					mFrameLayoutParams = new LinearLayout.LayoutParams(getGridColumnWidth, (int) (imageWidth / 0.7f));

					mImageLayoutParams = new RelativeLayout.LayoutParams(imageWidth, (int) (imageWidth / 0.7f));
					mSelectLayoutParams = new RelativeLayout.LayoutParams(getGridColumnWidth, (int) (imageWidth / 0.7f));
				}

				mImageLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);

				RelativeLayout layoutImgFrame = (RelativeLayout) convertView.findViewById(R.id.layoutImgFrame);
				layoutImgFrame.setLayoutParams(mFrameLayoutParams);

				ImageView coverAlbum = (ImageView) convertView.findViewById(R.id.imgCoverAlbum);
				coverAlbum.setLayoutParams(mImageLayoutParams);

				ImageView coverSelect = (ImageView) convertView.findViewById(R.id.img_select);
				ImageView outLine = (ImageView) convertView.findViewById(R.id.img_out_line);
				RelativeLayout selectlayout  =(RelativeLayout)convertView.findViewById(R.id.select_layout);
				mSelectLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
				selectlayout.setLayoutParams(mSelectLayoutParams);
				vh = new ThemeCoverHolder(coverAlbum, coverSelect,outLine,selectlayout, pos);
				convertView.setTag(vh);
			} else {
				vh = (ThemeCoverHolder) convertView.getTag();
				convertView.forceLayout();
			}

			// 커버에 체크를 표시를 해야할지 말지 설정..
			if (coverAct.xmlThemeCover.bgList.get(pos).F_IS_SELECT) {
				//vh.imgCoverAlbum.setAlpha(60);
				vh.imgCoverSelect.setVisibility(View.VISIBLE);
				vh.imgOutLine.setBackgroundResource(R.drawable.image_border_change_design_item_select);
				vh.selectLayout.setBackgroundColor(Color.parseColor("#66000000"));
			} else {
//				vh.imgCoverAlbum.setAlpha(255);
				vh.imgCoverSelect.setVisibility(View.INVISIBLE);
				vh.selectLayout.setBackgroundColor(Color.parseColor("#00000000"));
				vh.imgOutLine.setBackgroundResource(R.drawable.image_border_change_design_item_non_select);
			}

			//String imgUrl = coverAct.xmlThemeCover.bgList.get(pos).F_SSMPL_URL;
			String reSizeUrl = coverAct.xmlThemeCover.bgList.get(pos).F_RESIZE_320_URL;

			ImageLoader.with(coverAct).load(SnapsAPI.DOMAIN(false) + reSizeUrl).into(vh.imgCoverAlbum);
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}

		return convertView;
	}

	@Override
	public int getCount() {
		return coverAct.xmlThemeCover.bgList.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
}
