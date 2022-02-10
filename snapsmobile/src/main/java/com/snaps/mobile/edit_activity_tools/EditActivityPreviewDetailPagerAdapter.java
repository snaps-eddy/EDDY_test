package com.snaps.mobile.edit_activity_tools;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.snaps.common.utils.imageloader.ImageLoader;
import com.snaps.common.utils.system.ViewUnbindHelper;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;

import java.io.File;

public class EditActivityPreviewDetailPagerAdapter extends PagerAdapter {
	EditActivityPreviewActivity detailAct;

	int srcImgThumbSize = -1;

	public EditActivityPreviewDetailPagerAdapter(EditActivityPreviewActivity detailAct) {
		this.detailAct = detailAct;
		srcImgThumbSize = UIUtil.getScreenHeight(detailAct);// UIUtil.getCalcMyartworkWidth(detailAct);
	}

	@Override
	public Object instantiateItem(View pager, final int position) {// 뷰페이저에서 사용할 뷰객체 생성/등록
		if (detailAct == null) {
			return null;
		}
		// 페이징화면 생성
		LayoutInflater inflater = LayoutInflater.from(detailAct);
		View view = inflater.inflate(R.layout.activity_myartworkdetail_item, null);

		ImageView imgMyartworkDetail = (ImageView) view.findViewById(R.id.imgMyartworkDetail);

		if (detailAct.themeBookPageThumbnailPaths == null || detailAct.themeBookPageThumbnailPaths.size() <= position) {
			return view;
		}

		String filePath = detailAct.themeBookPageThumbnailPaths.get(position);
		ImageLoader.with(detailAct).load(new File(filePath)).into(imgMyartworkDetail);

		((ViewPager) pager).addView(view, 0); // 뷰 페이저에 추가
		return view;
	}

	@Override
	public int getCount() {
		return detailAct.themeBookPageThumbnailPaths.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

	@Override
	public void destroyItem(View pager, int position, Object view) {// 뷰 객체 삭제.
		((ViewPager) pager).removeView((View) view);
		ViewUnbindHelper.unbindReferences((View) view);
		view = null;
	}
}
