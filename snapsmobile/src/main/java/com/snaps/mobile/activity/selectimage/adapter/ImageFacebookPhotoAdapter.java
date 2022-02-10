package com.snaps.mobile.activity.selectimage.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.snaps.common.data.img.MyFacebookImageData;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.constant.Const_VALUES;
import com.snaps.common.utils.imageloader.ImageLoader;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.selectimage.FacebookIntroPhotoActivity;
import com.snaps.mobile.activity.selectimage.adapter.viewholder.ImageDetailHolder;

public class ImageFacebookPhotoAdapter extends ArrayAdapter<MyFacebookImageData> {
	private static final String TAG = ImageFacebookPhotoAdapter.class.getSimpleName();

	FacebookIntroPhotoActivity selectAct;
	LayoutInflater inflater;

	GridView.LayoutParams mImageViewLayoutParams;
	int _kakaoImageCount = 0;
	int getGridColumnWidth = 0;

	public ImageFacebookPhotoAdapter(FacebookIntroPhotoActivity facebookact) {
		super(facebookact, 0);

		selectAct = facebookact;
		inflater = LayoutInflater.from(selectAct);

		getGridColumnWidth = UIUtil.getGridColumnHeight(selectAct, Const_VALUE.IMAGE_GRID_COLS);
		mImageViewLayoutParams = new GridView.LayoutParams(getGridColumnWidth, getGridColumnWidth);
//		imageLoader.setDiscCache(facebookact);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		try {
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.fragment_imagedetail_item, parent, false);
				ImageView imgDetail = (ImageView) convertView.findViewById(R.id.imgDetail);
				ImageView imgChoiceBg = (ImageView) convertView.findViewById(R.id.imgChoiceBg);
				ImageView imgChoiceBgWhite = (ImageView) convertView.findViewById(R.id.imgChoiceBgWhite);
				ImageDetailHolder holder = new ImageDetailHolder(imgDetail, imgChoiceBg);
				holder.setWhiteBg(imgChoiceBgWhite);
				
				convertView.setTag(holder);
				convertView.setLayoutParams(mImageViewLayoutParams);
			}

			final ImageDetailHolder vh = (ImageDetailHolder) convertView.getTag();

			MyFacebookImageData imgData = getItem(position);// kakaoFrag.kakaoImageList.get(position);

			String thumbnailIimgUrl = imgData.THUMBNAIL_IMAGE_DATA;
			String imgUrl = imgData.ORIGIN_IMAGE_DATA;

			ImageLoader.with(selectAct).load(thumbnailIimgUrl).into(vh.imgDetail);

			String imgWidth = imgData.ORIGIN_IMAGE_WIDTH;
			String imgHeight = imgData.ORIGIN_IMAGE_HEIGHT;

			String mapKey = Const_VALUES.SELECT_FACEBOOK + "_" + imgData.ID;
			vh.setImgData(mapKey, Const_VALUES.SELECT_FACEBOOK, position, Uri.parse(imgUrl).getLastPathSegment(), imgUrl, thumbnailIimgUrl, imgWidth, imgHeight);
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}

		return convertView;
	}
}
