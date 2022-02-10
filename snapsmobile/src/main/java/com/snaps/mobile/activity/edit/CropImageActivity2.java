package com.snaps.mobile.activity.edit;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_VALUES;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.file.FileUtil;
import com.snaps.common.utils.image.ImageUtil;
import com.snaps.common.utils.imageloader.SnapsImageDownloader;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.pref.Setting;
import com.snaps.common.utils.system.ViewUnbindHelper;
import com.snaps.common.utils.ui.DataTransManager;
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

public class CropImageActivity2 extends CatchActivity implements OnClickListener {
	private static final String TAG = CropImageActivity2.class.getSimpleName();
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cropimage2);

		SnapsLogger.appendClassTrackingLog(new SnapsLoggerClass<>(this));

		progressImg = (ProgressBar) findViewById(R.id.progressImg);
		DataTransManager dataTransManager = DataTransManager.getInstance();
		if (dataTransManager != null) {
			imageList = dataTransManager.getTempPhotoImageDataList();
		}

		imgIdx = getIntent().getIntExtra("dataindex", 0);
		fPhotoW_Ratio = getIntent().getFloatExtra("ratiow", 0.0f);
		fPhotoH_Ratio = getIntent().getFloatExtra("ratioh", 0.0f);

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
		if (imageList == null || imageList.size() <= imgIdx) return;
		String imgUri = imageList.get(imgIdx).PATH;
		mPhotoPrintView.setAdjustClipBound(imageList.get(imgIdx), fPhotoW_Ratio, fPhotoH_Ratio, true);
		if (imageList.get(imgIdx).KIND == Const_VALUES.SELECT_UPLOAD) {
			if (FileUtil.isExistFile(imageList.get(imgIdx).ORIGINAL_PATH))
				imgUri = imageList.get(imgIdx).ORIGINAL_PATH;
			else if(imageList.get(imgIdx).getSafetyThumbnailPath() != null)
				imgUri = SnapsAPI.DOMAIN() + imageList.get(imgIdx).getSafetyThumbnailPath();
			else
				imgUri = SnapsAPI.DOMAIN() + imgUri;
		}

		final String imageUri = imgUri;
		mDownloader.loadBitmap(imgUri, mPhotoPrintView, progressImg, imageList.get(imgIdx).ROTATE_ANGLE, new SnapsImageDownloader.OnLoadComplete() {

			@Override
			public void onComplete(int width, int height) {
				Dlog.d("loadImg() imgUri:" + imageUri + ", w:" + width + ", h:" + height);
				mPhotoPrintView.calculatorClipRect();
				mIsLoading = false;

			}

			@Override
			public void onFailedLoad() {
				MessageUtil.toast(CropImageActivity2.this, getString(R.string.image_loding_fail_text));
				CropImageActivity2.this.finish();
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
					MessageUtil.toast(CropImageActivity2.this, R.string.PhotoPrintpage_start);
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
					MessageUtil.toast(CropImageActivity2.this, R.string.PhotoPrintpage_finish);
					return;
				}
				mIsLoading = true;
				loadImg();
			}
		} else if (R.id.btn_complete == id) {
			if (!mIsLoading) {
				imageList.get(imgIdx).CROP_INFO = mPhotoPrintView.getCropInfo();
			}
			DataTransManager.getInstance().setPhotoImageDataList( imageList );
			setResult(RESULT_OK, getIntent());
			finish();

		}

		mCropCountpage.setText(Integer.toString(imgIdx + 1) + " / " + Integer.toString(imageList.size()));
	}

	@Override
	protected void onDestroy() {
		ImageUtil.recycleBitmap(mPhotoPrintView);
		if (mTutorialView != null)
			mTutorialView.destroy();

		try {
			ViewUnbindHelper.unbindReferences(getWindow().getDecorView(), null, false);
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
		super.onDestroy();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}
}
