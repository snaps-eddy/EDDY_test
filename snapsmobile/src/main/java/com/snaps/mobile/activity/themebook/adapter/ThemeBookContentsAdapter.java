package com.snaps.mobile.activity.themebook.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.imageloader.ImageLoader;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.themebook.ThemeContensActivity;
import com.snaps.mobile.activity.themebook.holder.ThemeContentsHolder;

import errorhandle.logger.Logg;

public class ThemeBookContentsAdapter extends BaseAdapter {
	private static final String TAG = ThemeBookContentsAdapter.class.getSimpleName();
	ThemeContensActivity contentsAct;
	LayoutInflater inflater;

	int getGridColumnWidth;
	int imageWidth;
	public ThemeContentsHolder vh;

	public ThemeBookContentsAdapter(Activity act) {
		inflater = LayoutInflater.from(act);
		this.contentsAct = (ThemeContensActivity) act;

		getGridColumnWidth = UIUtil.getGridColumnHeight(contentsAct, Const_VALUE.IMAGE_ALBUM_COLS, Const_VALUE.IMAGE_ALBUM_SPACING, Const_VALUE.IMAGE_ALBUM_SPACING);
		imageWidth = getGridColumnWidth - UIUtil.convertDPtoPX(contentsAct, Const_VALUE.IMAGE_ALBUM_FRAME_12);
		Dlog.d("ThemeBookContentsAdapter() imageWidth:" + imageWidth);
//		imageLoader.setDiscCache(act);// ImageLoader의 Thumbnail 사이즈를 사진선택에 맞게 변경
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		int pos = position;// % coverAct.xmlThemeCover.bgList.size();
		try {
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.adapter_themebookcontents_item, parent, false);

				LinearLayout.LayoutParams mFrameLayoutParams = new LinearLayout.LayoutParams(getGridColumnWidth, (int) (getGridColumnWidth / 0.7f));
				RelativeLayout.LayoutParams mImageLayoutParams = new RelativeLayout.LayoutParams(imageWidth, (int) (imageWidth / 0.7f));
				mImageLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);

				RelativeLayout layoutImgFrame = (RelativeLayout) convertView.findViewById(R.id.layoutImgFrame);
				layoutImgFrame.setLayoutParams(mFrameLayoutParams);

				ImageView coverAlbum = (ImageView) convertView.findViewById(R.id.imgContents);
				coverAlbum.setLayoutParams(mImageLayoutParams);

				ImageView coverSelect = (ImageView) convertView.findViewById(R.id.img_select);

				vh = new ThemeContentsHolder(coverAlbum, coverSelect, pos);
				convertView.setTag(vh);
			} else {
				vh = (ThemeContentsHolder) convertView.getTag();
			}

			// 커버에 체크를 표시를 해야할지 말지 설정..
			if (contentsAct.xmlThemeContents.bgList.get(pos).F_IS_SELECT) {
				vh.imgCoverAlbum.setAlpha(60);
				vh.imgCoverSelect.setVisibility(View.VISIBLE);
			} else {
				vh.imgCoverAlbum.setAlpha(255);
				vh.imgCoverSelect.setVisibility(View.INVISIBLE);
			}

			String imgUrl = contentsAct.xmlThemeContents.bgList.get(pos).F_DIMG_PATH;

			ImageLoader.with(contentsAct).load(SnapsAPI.DOMAIN(false) + imgUrl).into(vh.imgCoverAlbum);
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}

		return convertView;
	}

	@Override
	public int getCount() {
		return contentsAct.xmlThemeContents.bgList.size();
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
