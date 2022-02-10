package com.snaps.mobile.activity.themebook;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.image.ImageUtil;
import com.snaps.common.utils.imageloader.SnapsImageDownloader;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.pref.Setting;
import com.snaps.common.utils.system.ViewUnbindHelper;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.component.SnapsPhotoPrintView;
import com.snaps.mobile.component.SnapsTutorialView;

import java.util.ArrayList;

import errorhandle.CatchActivity;
import errorhandle.logger.Logg;
import errorhandle.logger.SnapsLogger;
import errorhandle.logger.model.SnapsLoggerClass;

public class CropImageActivity3 extends CatchActivity implements OnClickListener {
	private static final String TAG = CropImageActivity3.class.getSimpleName();
	ArrayList<MyPhotoSelectImageData> imageList;
	int imgIdx = 0;
	float f_Ratio = 0.0f;
	float fPhotoW_Ratio = 0.0f;
	float fPhotoH_Ratio = 0.0f;
	int screenWidth, screenHeight;
	ProgressBar progressImg;
	SnapsPhotoPrintView mPhotoPrintView;
	TextView mCropCountpage;

	final String TUTORIAL = "tutorial3";
	SnapsTutorialView mTutorialView = null;

	SnapsImageDownloader mDownloader;

	boolean mIsLoading = false;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SnapsLogger.appendClassTrackingLog(new SnapsLoggerClass<>(this));

		setContentView(R.layout.activity_cropimage3);
		progressImg = (ProgressBar) findViewById(R.id.progressImg);
		imageList = (ArrayList<MyPhotoSelectImageData>) getIntent().getSerializableExtra("data");
		imgIdx = getIntent().getIntExtra("dataindex", 0);
		fPhotoW_Ratio = 0.7f;
		fPhotoH_Ratio = 0.7f;

		screenWidth = UIUtil.getScreenWidth(this);
		screenHeight = UIUtil.getScreenHeight(this);

		mPhotoPrintView = (SnapsPhotoPrintView) findViewById(R.id.photoPrintView);
		TextView btn = (TextView) findViewById(R.id.btn_before);
		btn.setOnClickListener(this);
		btn = (TextView) findViewById(R.id.btn_next);
		btn.setOnClickListener(this);
		btn = (TextView) findViewById(R.id.btn_complete);
		btn.setOnClickListener(this);

		mCropCountpage = (TextView) findViewById(R.id.photo_Crop_count);

		mCropCountpage.setText(Integer.toString(imgIdx + 1) + " / " + Integer.toString(imageList.size()));
		int width = Math.min(512, UIUtil.getScreenWidth(getApplicationContext()));
		int height = Math.min(512, UIUtil.getScreenHeight(getApplicationContext()));

		mDownloader = new SnapsImageDownloader(width, height);

		// 튜토리얼 뷰를 띄운다.
		loadImg(); 
	}

	void showTutorial() {
		// 튜토리얼 뷰를 띄운다.
		if (Config.TEST_TUTORIAL || !Setting.getBoolean(getApplicationContext(), SnapsTutorialView.TUTORIAL3)) {
			mTutorialView = new SnapsTutorialView(getApplicationContext(), SnapsTutorialView.TUTORIAL3);
			mTutorialView.setTutorialImage(R.drawable.tut_3);
			addContentView(mTutorialView, new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT));
		}
	}

	boolean removeTutorial() {
		if (mTutorialView != null) {
			ViewGroup g = (ViewGroup) mTutorialView.getParent();
			if (g != null) {
				g.removeView(mTutorialView);
				mTutorialView = null;
				return true;
			}
		}
		mTutorialView = null;
		return false;
	}

	@Override
	public void onBackPressed() {
		if (removeTutorial())
			return;

		if (!mIsLoading) {// 로딩이 완료가 되었으면 해당 사진 크롭 정보를 저장한다.
			imageList.get(imgIdx).CROP_INFO = mPhotoPrintView.getCropInfo();
		}

		setResult(RESULT_OK, getIntent().putExtra("data", imageList));
		finish();

	}

	void loadImg() {
		final String imgUri = imageList.get(imgIdx).PATH;
		mPhotoPrintView.setAdjustClipBound(imageList.get(imgIdx), fPhotoW_Ratio, fPhotoH_Ratio, true);
		mDownloader.loadBitmap(imgUri, mPhotoPrintView, progressImg, imageList.get(imgIdx).ROTATE_ANGLE, new SnapsImageDownloader.OnLoadComplete() {

			@Override
			public void onComplete(int width, int height) {
				Dlog.d("onComplete() " + "path:" + imgUri + ", w:" + width + ", h:" + height);
				mPhotoPrintView.calculatorClipRect();
				mIsLoading = false;

			}

			@Override
			public void onFailedLoad() {
				MessageUtil.toast(CropImageActivity3.this, getString(R.string.image_loding_fail_text));//"이미지 로딩에 실패 했습니다.\n잠시후 다시 시도 바랍니다.");
				CropImageActivity3.this.finish();				
			}
		});
	}

	public void onClick(View v) {
		int id = v.getId();
		if (R.id.btn_before == id) {
			if (!mIsLoading) {
				imageList.get(imgIdx).CROP_INFO = mPhotoPrintView.getCropInfo();
				imgIdx--;
				if (imgIdx < 0) {
					imgIdx = 0;
					MessageUtil.toast(CropImageActivity3.this, R.string.PhotoPrintpage_start);
					return;
				}

				mIsLoading = true;
				loadImg();
			}

		} else if (R.id.btn_next == id) {
			if (!mIsLoading) {
				imageList.get(imgIdx).CROP_INFO = mPhotoPrintView.getCropInfo();
				imgIdx++;
				if (imgIdx > imageList.size() - 1) {
					imgIdx = imageList.size() - 1;
					MessageUtil.toast(CropImageActivity3.this, R.string.PhotoPrintpage_finish);
					return;
				}
				mIsLoading = true;
				loadImg();
			}
		} else if (R.id.btn_complete == id) {
			if (!mIsLoading) {
				imageList.get(imgIdx).CROP_INFO = mPhotoPrintView.getCropInfo();
			}
			setResult(RESULT_OK, getIntent().putExtra("data", imageList));
			finish();

		}

		mCropCountpage.setText(Integer.toString(imgIdx + 1) + " / " + Integer.toString(imageList.size()));
	}

	@Override
	protected void onDestroy() {
		ImageUtil.recycleBitmap(mPhotoPrintView);
		
		if(mTutorialView != null)
			mTutorialView.destroy();

		try {
			ViewUnbindHelper.unbindReferences(getWindow().getDecorView(), null, false);
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
		super.onDestroy();
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
