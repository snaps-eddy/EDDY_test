package com.snaps.mobile.activity.cartorder;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.snaps.common.structure.photoprint.SnapsPhotoPrintItem;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.imageloader.SnapsImageDownloader;
import com.snaps.common.utils.imageloader.recoders.CropInfo;
import com.snaps.common.utils.imageloader.recoders.CropInfo.CORP_ORIENT;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.system.ViewUnbindHelper;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.SnapsBaseActivity;
import com.snaps.mobile.utils.thirdparty.SnapsTPAppManager;

import errorhandle.logger.SnapsLogger;
import errorhandle.logger.model.SnapsLoggerClass;


public class PhotoPrintDetailActivity extends SnapsBaseActivity {
	private static final String TAG = PhotoPrintDetailActivity.class.getSimpleName();
	int screenWidth, screenHeight;
	ProgressBar progressImg;
	ImageView mImageView;
	SnapsPhotoPrintItem mItem;
	SnapsImageDownloader mDownloader;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		SnapsLogger.appendClassTrackingLog(new SnapsLoggerClass<>(this));

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photoprint_detail);

		screenWidth = UIUtil.getScreenWidth(this);
		screenHeight = UIUtil.getScreenWidth(this);
		progressImg = (ProgressBar) findViewById(R.id.progressImg);

		mItem = (SnapsPhotoPrintItem) getIntent().getExtras().getParcelable("photoprintItem");
		mImageView = (ImageView) findViewById(R.id.imgOrigin);

		
		mDownloader = new SnapsImageDownloader(screenWidth, screenWidth);

		
		TextView complete = (TextView) findViewById(R.id.btnBack);
		complete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();

			}
		});

		loadImg();

	}

	void loadImg() {

		float movePercent = 0.0f;
		int startPercent = 0;
		int endPercent = 0;

		if(SnapsTPAppManager.isThirdPartyApp(this))
			SnapsTPAppManager.setConfigReal(this);
		
		CORP_ORIENT orientValue = CORP_ORIENT.NONE;

		if (mItem.mOffsetX.equals("0") && mItem.mOffsetY.equals("0.0000")) {
			orientValue = CORP_ORIENT.HEIGHT;

		} else if (mItem.mOffsetX.equals("0.0000") && mItem.mOffsetY.equals("0")) {
			orientValue = CORP_ORIENT.WIDTH;
		}

		else if (mItem.mOffsetX.equals("0") && !mItem.mOffsetY.equals("0.0000")) {
			orientValue = CORP_ORIENT.HEIGHT;

		} else if (!mItem.mOffsetX.equals("0.0000") && mItem.mOffsetY.equals("0")) {
			orientValue = CORP_ORIENT.WIDTH;
		}

		startPercent = (int) (Float.parseFloat(mItem.mTrimPos) * 100.f);
		endPercent = (int) (Float.parseFloat(mItem.mEndPos) * 100.f);

		CropInfo cropInfo = new CropInfo(orientValue, movePercent, startPercent, endPercent);
		mImageView.setImageBitmap(null);

		if (mItem.isFaceBookImage()) {
			mDownloader.loadCropBitmap(mItem.mOrgPath, mImageView, progressImg, mItem.mAngle, cropInfo);

		} else {
			mDownloader.loadCropBitmap(SnapsAPI.DOMAIN(false) + mItem.mOrgPath, mImageView, progressImg, mItem.mAngle, cropInfo);
		}
		// 원본 이미지를 로드하기 위해 width에 -1로 입력한다.

	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
		try {
			ViewUnbindHelper.unbindReferences(getWindow().getDecorView(), null, false);
			System.gc();
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}

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
