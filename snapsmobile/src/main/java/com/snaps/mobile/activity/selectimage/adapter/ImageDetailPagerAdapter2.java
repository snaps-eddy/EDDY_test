package com.snaps.mobile.activity.selectimage.adapter;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.utils.imageloader.SnapsImageDownloader;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.selectimage.ImageDetailEditActivity;

public class ImageDetailPagerAdapter2 extends PagerAdapter {
	static final int ROTATE_ANGLE = 90;

	ImageDetailEditActivity detailAct;

	int srcImgThumbSizeW;
	int srcImgThumbSizeH;

	SnapsImageDownloader mDownloader;

	public ImageDetailPagerAdapter2(ImageDetailEditActivity detailAct) {
		this.detailAct = detailAct;
		srcImgThumbSizeW = Math.min(512,UIUtil.getScreenWidth(detailAct));
						
		srcImgThumbSizeH = Math.min(512, UIUtil.getScreenHeight(detailAct));

		mDownloader = new SnapsImageDownloader(srcImgThumbSizeW, srcImgThumbSizeH);

	}

	@Override
	public int getItemPosition(Object object) {
		// TODO Auto-generated method stub
		return POSITION_NONE;
	}

	@Override
	public Object instantiateItem(View pager, final int position) {// 뷰페이저에서 사용할
																	// 뷰객체 생성/등록
		// 페이징화면 생성
		LayoutInflater inflater = LayoutInflater.from(detailAct);
		View view = inflater.inflate(R.layout.activity_imagedetailedit_item, null);

		final MyPhotoSelectImageData imgData = detailAct.getImgData(position);
		final ImageView imgDetailEdit = (ImageView) view.findViewById(R.id.imgDetailEdit);
		final ProgressBar progressImg = (ProgressBar) view.findViewById(R.id.progressImg);

		mDownloader.loadBitmap(imgData.PATH, imgDetailEdit, progressImg, imgData.ROTATE_ANGLE);
		
		((ViewPager) pager).addView(view, 0); // 뷰 페이저에 추가
		return view;
	}

	@Override
	public int getCount() {
		return detailAct.selectImgKeyList.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

	@Override
	public void destroyItem(View pager, int position, Object view) {// 뷰 객체 삭제.
		((ViewPager) pager).removeView((View) view);
		view = null;
	}

}
