package com.snaps.mobile.activity.selectimage;

import android.os.Bundle;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.ImageView;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.utils.constant.Const_EKEY;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.system.ViewUnbindHelper;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.selectimage.adapter.ImageDetailPagerAdapter2;

import java.util.ArrayList;
import java.util.HashMap;

import errorhandle.CatchFragmentActivity;
import errorhandle.logger.SnapsLogger;
import errorhandle.logger.model.SnapsLoggerClass;

public class ImageDetailEditActivity extends CatchFragmentActivity {
	private static final String TAG = ImageDetailEditActivity.class.getSimpleName();

	ViewPager vpagerImageDetail;

	// data
	public ArrayList<String> selectImgKeyList = new ArrayList<String>();
	public HashMap<String, MyPhotoSelectImageData> selectImgMap = new HashMap<String, MyPhotoSelectImageData>();
	ImageDetailPagerAdapter2 pagerAdapter;

	boolean isChange = false;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SnapsLogger.appendClassTrackingLog(new SnapsLoggerClass<>(this));

		setContentView(R.layout.activity_imagedetailedit_);

		selectImgKeyList = getIntent().getStringArrayListExtra(Const_EKEY.IMG_DATA_KEYLIST);
		selectImgMap = (HashMap<String, MyPhotoSelectImageData>) getIntent().getSerializableExtra(Const_EKEY.IMG_DATA_MAP);

		int currPos = getIntent().getIntExtra(Const_EKEY.IMG_DATA_POSITION, 0);
		
		ImageView rotate = (ImageView)findViewById(R.id.btnEditRotate);
		

		vpagerImageDetail = (ViewPager) findViewById(R.id.vpagerImageDetail);
		vpagerImageDetail.setOffscreenPageLimit(1);

		pagerAdapter = new ImageDetailPagerAdapter2(this);

		vpagerImageDetail.setAdapter(pagerAdapter);
		vpagerImageDetail.setCurrentItem(currPos);

		vpagerImageDetail.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {

				if (arg0 == 0) {
					MessageUtil.toast(ImageDetailEditActivity.this, R.string.PhotoPrintpage_start);
				} else if (arg0 == selectImgKeyList.size() - 1) {
					MessageUtil.toast(ImageDetailEditActivity.this, R.string.PhotoPrintpage_finish);
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
	}
	@Override
	public void onBackPressed() {
		editFinish();
	}
	@Override
	protected void onDestroy() {

		try {
			ViewUnbindHelper.unbindReferences(getWindow().getDecorView());
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}

		super.onDestroy();
	}

	public MyPhotoSelectImageData getImgData(int position) {
		return selectImgMap.get(selectImgKeyList.get(position));
	}

	public void onClick(View v) {
		if (v.getId() == R.id.btnEditBack) {// ??????
			editFinish();
		} else if (v.getId() == R.id.btnEditRotate) {// ??????
			isChange = true;

			int currItem = vpagerImageDetail.getCurrentItem();
			MyPhotoSelectImageData data = getImgData(currItem);

			int angle = data.ROTATE_ANGLE + 90;
			int thumbAngle = data.ROTATE_ANGLE_THUMB + 90;

			if (angle >= 360)
				angle = 0;

			// ???????????? -1?????? ????????? 360?????? 350 ?????? ???...
			if (thumbAngle >= 350)
				thumbAngle = 0;
			
			// ????????? 89??? ????????? ??????????????????...
			if(thumbAngle>80 && thumbAngle<=90)
				thumbAngle = 90;

			data.ROTATE_ANGLE = angle;
			data.ROTATE_ANGLE_THUMB = thumbAngle;

			pagerAdapter.notifyDataSetChanged();
		} else if (v.getId() == R.id.btnEditDelete) {// ??????
			isChange = true;
			int currItem = vpagerImageDetail.getCurrentItem();

			if (selectImgKeyList != null && selectImgKeyList.size() > currItem) {
				String key = selectImgKeyList.get(currItem);
				selectImgKeyList.remove(key);
				selectImgMap.remove(key);

				if (selectImgKeyList.size() == 0)// ??????????????? ?????? ??????
					editFinish();
				else if (currItem > 0)// currItem??? 0?????? ?????? ????????? ?????? ?????? ????????? ??????~
					// currItem??? 0??????, ???????????? ??????????????? ????????? 0??????~
					--currItem;

				pagerAdapter.notifyDataSetChanged();
				vpagerImageDetail.setCurrentItem(currItem);
			}
		}
	}
	void editFinish() {
		if (isChange) {
			getIntent().putStringArrayListExtra(Const_EKEY.IMG_DATA_KEYLIST, selectImgKeyList);
			getIntent().putExtra(Const_EKEY.IMG_DATA_MAP, selectImgMap);
			setResult(RESULT_OK, getIntent());
		}
		finish();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}
}
