package com.snaps.mobile.activity.themebook.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.imageloader.ImageLoader;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.themebook.ThemeCategoryActivity;
import com.snaps.mobile.activity.themebook.holder.ThemeCategoryHolder;

import errorhandle.logger.Logg;

public class ThemeBookCategoryAdapter extends BaseAdapter {
	private static final String TAG = ThemeBookCategoryAdapter.class.getSimpleName();
	ThemeCategoryActivity cartegoryAct;
	LayoutInflater inflater;

	int getGridColumnWidth;
	int imageWidth;
	public ThemeCategoryHolder vh;

	public ThemeBookCategoryAdapter(Activity act) {
		inflater = LayoutInflater.from(act);
		this.cartegoryAct = (ThemeCategoryActivity) act;

		getGridColumnWidth = UIUtil.getGridColumnHeight(cartegoryAct, Const_VALUE.IMAGE_ALBUM_COLS, Const_VALUE.IMAGE_ALBUM_SPACING, Const_VALUE.IMAGE_ALBUM_SPACING);
		imageWidth = getGridColumnWidth - UIUtil.convertDPtoPX(cartegoryAct, Const_VALUE.IMAGE_ALBUM_FRAME);
		Dlog.d("ThemeBookCategoryAdapter() imageWidth:" + imageWidth);
//		imageLoader.setDiscCache(act);// ImageLoader의 Thumbnail 사이즈를 사진선택에 맞게 변경
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		int pos = position;// % coverAct.xmlThemeCover.bgList.size();
		try {
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.adapter_themebookcategory_item, parent, false);

				LinearLayout.LayoutParams mFrameLayoutParams = new LinearLayout.LayoutParams(getGridColumnWidth, (int) (getGridColumnWidth / 0.7f));
				RelativeLayout.LayoutParams mImageLayoutParams = new RelativeLayout.LayoutParams(imageWidth, (int) (imageWidth / 0.7f));
				mImageLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);

				RelativeLayout layoutImgFrame = (RelativeLayout) convertView.findViewById(R.id.layoutImgFrame);
				layoutImgFrame.setLayoutParams(mFrameLayoutParams);

				ImageView categoryAlbum = (ImageView) convertView.findViewById(R.id.imgCategoryAlbum);
				categoryAlbum.setLayoutParams(mImageLayoutParams);

				TextView categoryName = (TextView) convertView.findViewById(R.id.txtcategoryName);

				vh = new ThemeCategoryHolder(categoryAlbum, categoryName);
				convertView.setTag(vh);
			} else {
				vh = (ThemeCategoryHolder) convertView.getTag();
			}

			String imgUrl = cartegoryAct.xmlThemeCategory.bgList.get(pos).F_EIMG_PATH;

			ImageLoader.with(cartegoryAct).load(SnapsAPI.DOMAIN(false) + imgUrl).into(vh.imgCategoryAlbum);

			vh.imgCategoryName.setText(cartegoryAct.xmlThemeCategory.bgList.get(pos).F_CATEGORY_NAME);

		} catch (Exception e) {
			Dlog.e(TAG, e);
		}

		return convertView;
	}

	@Override
	public int getCount() {
		return cartegoryAct.xmlThemeCategory.bgList.size();
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
