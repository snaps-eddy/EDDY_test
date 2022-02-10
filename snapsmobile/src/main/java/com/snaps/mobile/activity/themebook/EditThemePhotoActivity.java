package com.snaps.mobile.activity.themebook;

import android.graphics.Point;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.image.ImageDirectLoader;
import com.snaps.common.utils.image.ImageUtil;
import com.snaps.common.utils.imageloader.recoders.CropInfo;
import com.snaps.common.utils.imageloader.recoders.CropInfo.CORP_ORIENT;
import errorhandle.logger.Logg;

import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.system.ViewUnbindHelper;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;

import java.util.ArrayList;

import errorhandle.CatchActivity;

@SuppressWarnings({"deprecation", "unchecked"})
public class EditThemePhotoActivity extends CatchActivity {
	private static final String TAG = EditThemePhotoActivity.class.getSimpleName();
	ImageView imgOrigin;
	LinearLayout layoutCropRect;
	RelativeLayout.LayoutParams rectLP;
	ProgressBar progressImg;
	boolean nProgressIng = false;

	GestureDetector gesture;

	ArrayList<MyPhotoSelectImageData> imageList;
	float xValance;
	int imgIdx = 0;
	int screenWidth, screenHeight;
	float screenImgWidth = 0.0f, screenImgHeight = 0.0f;
	int originX, originY, maxMove, minX, maxX, minY, maxY;
	// 최대이동거리, 최저x, 최대x, 최저y, 최대y
	CORP_ORIENT orientValue = CORP_ORIENT.NONE;

	boolean isClick = true;

	// 스티커(6,2,1분할) 비율 넓이/높이
	float f_Ratio = 0.0f;
	float fPhotoW_Ratio = 0.0f;
	float fPhotoH_Ratio = 0.0f;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_theme_photo_edit);

		try {
			imageList = (ArrayList<MyPhotoSelectImageData>) getIntent().getSerializableExtra("data");
			imgIdx = getIntent().getIntExtra("dataindex", 0);
			fPhotoW_Ratio = getIntent().getFloatExtra("ratiow", 0.0f);
			fPhotoH_Ratio = getIntent().getFloatExtra("ratioh", 0.0f);

			gesture = new GestureDetector(onGesture);

			imgOrigin = (ImageView) findViewById(R.id.imgOrigin);
			layoutCropRect = (LinearLayout) findViewById(R.id.layoutCropRect);
			progressImg = (ProgressBar) findViewById(R.id.progressImg);

			screenWidth = UIUtil.getScreenWidth(this);
			screenHeight = UIUtil.getScreenHeight(this);
			Dlog.d("onCreate() screen w:" + screenWidth + ", h:" + screenHeight);

			rectLP = (RelativeLayout.LayoutParams) layoutCropRect.getLayoutParams();

			int stickerCountInPage = 0; // 페이지당 들어가는 스티커 갯수
			if (Config.getTMPL_CODE().equals(Config.TEMPLATE_STICKER_6)) {
				f_Ratio = Const_VALUE.STICKER_6_RATIO;
				stickerCountInPage = 6;
			} else if (Config.getTMPL_CODE().equals(Config.TEMPLATE_STICKER_2)) {
				stickerCountInPage = 2;
				f_Ratio = Const_VALUE.STICKER_2_RATIO;
			} else if (Config.getTMPL_CODE().equals(Config.TEMPLATE_STICKER_1)) {
				stickerCountInPage = 1;
				f_Ratio = Const_VALUE.STICKER_1_RATIO;
			}

			if (Config.isSnapsSticker()) {
				int imgCnt = imageList.size(); // 이미지 총갯수
				imgIdx = (imgIdx == 0) ? 0 : (imgIdx - 1) * stickerCountInPage % imgCnt;
			}
			loadImg();
		} catch (Exception e) {
			Dlog.e(TAG, e);
			finish();
		}

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return gesture.onTouchEvent(event);
	}

	public void onClick(View v) {

		if (isClick == true) {
			if (R.id.btnBack == v.getId()) {// 이전

				setResult(RESULT_OK, getIntent().putExtra("data", imageList));
				finish();

			} else if (R.id.btnCropComplete == v.getId()) {// 완료
				saveCropInfo();// 현재 사진 crop정보 저장

				setResult(RESULT_OK, getIntent().putExtra("data", imageList));
				finish();
			}

			else if (R.id.btnPrevImg == v.getId()) {// 이전사진
				saveCropInfo();// 현재 사진 crop정보 저장

				imgIdx--;
				if (imgIdx < 0) {
					imgIdx = 0;
					MessageUtil.toast(EditThemePhotoActivity.this, R.string.croppage_start);
					return;
				}

				loadImg();

				isClick = false;

			} else if (R.id.btnNextImg == v.getId()) {// 다음사진

				saveCropInfo();// 현재 사진 crop정보 저장
				imgIdx++;
				if (imgIdx >= imageList.size()) {
					imgIdx = imageList.size() - 1;
					MessageUtil.toast(EditThemePhotoActivity.this, R.string.croppage_finish);
					return;
				}

				loadImg();

				isClick = false;

			}
		} else {

		}

	}

	OnGestureListener onGesture = new OnGestureListener() {
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

			if (isClick == true) {
				if (orientValue == CORP_ORIENT.HEIGHT)
					moveRectangle(-(int) distanceY);
				else if (orientValue == CORP_ORIENT.WIDTH)
					moveRectangle(-(int) distanceX);
			} else {

			}

			return true;
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			return false;
		}

		@Override
		public void onShowPress(MotionEvent e) {
		}

		@Override
		public void onLongPress(MotionEvent e) {
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			return false;
		}

		@Override
		public boolean onDown(MotionEvent e) {
			return false;
		}
	};

	void saveCropInfo() {
		Dlog.d("saveCropInfo() x:" + layoutCropRect.getX() + ", y:" + layoutCropRect.getY()
				+ ", w:" + layoutCropRect.getWidth() + ", h:" + layoutCropRect.getHeight());

		// 스크린의 center와 크롭영역의 center값을 구해 얼마나 영역이 이동이 이루어 졌는지 구한다.
		// 스크린의 center를 구한다.
		Point screenCenter = new Point(screenWidth / 2, screenHeight / 2);
		// 크롭영역의 center를 구한다.
		Point cropRectCenter = new Point((int) (layoutCropRect.getX() + layoutCropRect.getWidth() / 2), (int) (layoutCropRect.getY() + layoutCropRect.getHeight() / 2));

		float movePercent = 0;
		int startPercent = 0;
		int endPercent = 0;
		if (orientValue == CORP_ORIENT.HEIGHT) {// 세로이동

			// 이동한 크기를 이동거리/이미지크기 비율로 저장을 한다.
			float moveY = screenCenter.y - cropRectCenter.y;
			movePercent = (float) moveY / screenImgHeight;
			// 크롭영역에 썸네일을 만들기 위해 설정을 함..
			// 기존 로직 사용
			int startY = (int) (layoutCropRect.getY() - (screenHeight - screenImgHeight) / 2.0f);

			startPercent = (int) (((float) startY / screenImgHeight) * 100f);
			endPercent = (int) (((float) (startY + layoutCropRect.getHeight()) / screenImgHeight) * 100f);
		} else if (orientValue == CORP_ORIENT.WIDTH) {// 가로이동

			float moveX = screenCenter.x - cropRectCenter.x;
			movePercent = (float) moveX / screenImgWidth;
			// 크롭영역에 썸네일을 만들기 위해 설정을 함..
			// 기존 로직 사용
			int startX = (int) (layoutCropRect.getX() - (screenWidth - screenImgWidth) / 2.0f);
			startPercent = (int) (((float) startX / screenImgWidth) * 100f);
			endPercent = (int) (((float) (startX + layoutCropRect.getWidth()) / screenImgWidth) * 100f);
		}

		CropInfo cropInfo = null;
		cropInfo = new CropInfo(orientValue, movePercent, startPercent, endPercent);
		Dlog.d("saveCropInfo() " + cropInfo.toString());
		imageList.get(imgIdx).CROP_INFO = cropInfo;
	}

	void loadImg() {
		final String imgUri = imageList.get(imgIdx).PATH;

		ImageDirectLoader.loadAllImage(this, imageList.get(imgIdx).ROTATE_ANGLE, imgUri, imgOrigin, screenWidth, progressImg, new ImageDirectLoader.OnLoadComplete() {
			@Override
			public void onComplete(int width, int height) {
				Dlog.d("loadImg() imgUri:" + imgUri + ", w:" + width + ", h:" + height);
				boxSizing(width, height);
			}

			@Override
			public void onFailedLoad() {
				MessageUtil.toast(EditThemePhotoActivity.this, getString(R.string.image_loding_fail_text));
				EditThemePhotoActivity.this.finish();				
			}
		});
	}

	// 이미지 로딩(사진로딩)
	// crop 영역 설정
	void boxSizing(final int imgWidth, final int imgHeight) {
		// 이미지가 가로에 맞춰야할지 세로에 맞춰야 할지 확
		float ratioX = (float) screenWidth / (float) imgWidth;
		float ratioY = (float) screenHeight / (float) imgHeight;

		Dlog.d("boxSizing() ratio x:" + ratioX + ", y:" + ratioY);

		// 스크린에 뿌려지는 이미지 크기를 구한다
		if (ratioX >= ratioY) {
			screenImgHeight = screenHeight;
			screenImgWidth = imgWidth * ratioY;
		} else {
			// _cropOrient = UP_DOWN;
			screenImgWidth = screenWidth;
			screenImgHeight = imgHeight * ratioX;
		}

		orientValue = CORP_ORIENT.NONE;
		// 이미지 기준은 true이면 가로가 기준 false이면 높이가 기준이 된다.
		// 즉 true이면 높이가 꽉채워지고 false이면 가로가 꽉채워진다.

		boolean isStandard = (screenImgWidth / screenImgHeight) > f_Ratio ? true : false;

		orientValue = isStandard ? CORP_ORIENT.WIDTH : CORP_ORIENT.HEIGHT;
		/*
		 * final float imgCropWidth = isStandard ? (screenImgHeight / f_Ratio) : screenImgWidth; final float imgCropHeight = isStandard ? screenImgHeight : (screenImgWidth * f_Ratio);
		 */
		final float imgCropWidth = isStandard ? (screenImgHeight * f_Ratio) : screenImgWidth;
		final float imgCropHeight = isStandard ? screenImgHeight : (screenImgWidth / f_Ratio);

		rectLP.width = (int) Math.ceil(imgCropWidth);
		rectLP.height = (int) Math.ceil(imgCropHeight);
		layoutCropRect.setLayoutParams(rectLP);

		// 크롭영역 limit 설정
		if (!isStandard) {
			originY = (int) (screenHeight - imgCropHeight) / 2;
			maxMove = (int) ((screenImgHeight - imgCropHeight) / 2);
			minY = originY - maxMove;
			maxY = originY + maxMove;
		} else {
			originX = (int) (screenWidth - imgCropWidth) / 2;
			maxMove = (int) ((screenImgWidth - imgCropWidth) / 2);
			minX = originX - maxMove;
			maxX = originX + maxMove;
		}

		// 크롭영역 위치를 조정한다.
		adjustCropRect(imgCropWidth, imgCropHeight);
	}

	/***
	 * 저장된 cropRect를 읽어와서 위치를 조절하는 함
	 */
	void adjustCropRect(float cWidth, float cHeight) {
		// 크롭영역을 가운데에 맞춘다.

		int x = (int) ((screenWidth - cWidth) / 2);
		int y = (int) Math.floor(((screenHeight - cHeight) / 2));

		// 기존 크롭 정보를 로드한다.
		// 정보가 있으면 적용을 한다.
		CropInfo cInfo = imageList.get(imgIdx).CROP_INFO;
		if (cInfo != null && CropInfo.CORP_ORIENT.NONE != cInfo.cropOrient) {
			int cropSize = (int) (cInfo.cropOrient == CORP_ORIENT.WIDTH ? screenImgWidth : screenImgHeight);
			float offsetValue = ((float) cropSize * ((float) cInfo.movePercent));

			if (cInfo.cropOrient == CORP_ORIENT.WIDTH)
				x -= offsetValue;
			else
				y -= offsetValue;
		}

		layoutCropRect.setX(x);
		layoutCropRect.setY(y);

		isClick = true;

	}

	void moveRectangle(int move) {
		if (orientValue == CORP_ORIENT.WIDTH) {// 좌우이동 Landscape
			int goX = (int) layoutCropRect.getX() + move;
			if (goX < minX)
				layoutCropRect.setX(minX);
			else if (goX > maxX)
				layoutCropRect.setX(maxX);
			else
				layoutCropRect.setX(goX);
		} else if (orientValue == CORP_ORIENT.HEIGHT) {// 상하이동 Portrait
			int goY = (int) layoutCropRect.getY() + move;
			if (goY < minY)
				layoutCropRect.setY(minY);
			else if (goY > maxY)
				layoutCropRect.setY(maxY);
			else
				layoutCropRect.setY(goY);
		}
	}

	@Override
	protected void onDestroy() {
		ImageUtil.recycleBitmap(imgOrigin);

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

