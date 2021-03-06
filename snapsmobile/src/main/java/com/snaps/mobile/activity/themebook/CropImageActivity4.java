package com.snaps.mobile.activity.themebook;

import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_VALUES;
import com.snaps.common.utils.constant.SnapsAPI;
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
import errorhandle.logger.SnapsLogger;
import errorhandle.logger.model.SnapsLoggerClass;

@SuppressWarnings({"deprecation", "unchecked"})
public class CropImageActivity4 extends CatchActivity {
	private static final String TAG = CropImageActivity4.class.getSimpleName();
	ImageView imgOrigin;
	LinearLayout layoutCropRect;
	RelativeLayout.LayoutParams rectLP;
	ProgressBar progressImg;
	boolean nProgressIng = false;

	GestureDetector gesture;

	ArrayList<MyPhotoSelectImageData> imageList;
	float xValance;
	int imgIdx = 0;
	float mScreenWidth, mScreenHeight;
	float screenImgWidth = 0.0f, screenImgHeight = 0.0f;
	float originX, originY, maxMove, minX, maxX, minY, maxY;

	// ??????????????????, ??????x, ??????x, ??????y, ??????y
	CORP_ORIENT orientValue = CORP_ORIENT.NONE;

	boolean isClick = true;

	// ?????????(6,2,1??????) ?????? ??????/??????
	float f_Ratio = 0.0f;
	float fPhotoW_Ratio = 0.0f;
	float fPhotoH_Ratio = 0.0f;

	TextView mThemeTitle;
	TextView mCompleteBtn;

	TextView mIndex;

	ImageView mBackBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		SnapsLogger.appendClassTrackingLog(new SnapsLoggerClass<>(this));

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cropimage4);

		ImageView iv = (ImageView) findViewById(R.id.ThemeTitleLeft);
		iv.setImageResource(R.drawable.close);

		try {
			mThemeTitle = (TextView) findViewById(R.id.ThemeTitleText);
			mThemeTitle.setText(getString(R.string.photo_modify_text));//"?????? ??????");
			mCompleteBtn = (TextView) findViewById(R.id.ThemebtnTopNext);
			mCompleteBtn.setText(getString(R.string.confirm));//"??????");

			mIndex = (TextView) findViewById(R.id.photo_Crop_count);

			mCompleteBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					saveCropInfo();// ?????? ?????? crop?????? ??????

					setResult(RESULT_OK, getIntent().putExtra("data", imageList));
					finish();

				}
			});

			mBackBtn = (ImageView) findViewById(R.id.ThemeTitleLeft);
			mBackBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					finish();

				}
			});
			
			if(findViewById(R.id.ThemeTitleLeftLy) != null) {
				findViewById(R.id.ThemeTitleLeftLy).setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						finish();
					}
				});
			}

			imageList = (ArrayList<MyPhotoSelectImageData>) getIntent().getSerializableExtra("data");
			imgIdx = getIntent().getIntExtra("dataIndex", 0);
			fPhotoW_Ratio = getIntent().getFloatExtra("ratiow", 0.0f);
			fPhotoH_Ratio = getIntent().getFloatExtra("ratioh", 0.0f);

			gesture = new GestureDetector(onGesture);

			imgOrigin = (ImageView) findViewById(R.id.imgOrigin);

			layoutCropRect = (LinearLayout) findViewById(R.id.layoutCropRect);

			progressImg = (ProgressBar) findViewById(R.id.progressImg);

			mScreenWidth = UIUtil.getScreenWidth(this);
			mScreenHeight = UIUtil.getScreenHeight(this);

			Dlog.d("screen w:" + mScreenWidth + ", h:" + mScreenHeight);

			mIndex.setText(Integer.toString(imgIdx + 1) + " / " + Integer.toString(imageList.size()));

			rectLP = (RelativeLayout.LayoutParams) layoutCropRect.getLayoutParams();

			f_Ratio = 0.7f;

			loadImg();
		} catch (Exception e) {
			Dlog.e(TAG, e);
			finish();
		}

		findViewById(R.id.btn_before1).setOnTouchListener(preUIChagner);
		findViewById(R.id.btn_before2).setOnTouchListener(preUIChagner);

		findViewById(R.id.btn_next1).setOnTouchListener(nextUIChagner);
		findViewById(R.id.btn_next2).setOnTouchListener(nextUIChagner);

	}

	OnTouchListener preUIChagner = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				((TextView) findViewById(R.id.btn_before1)).setTextColor(getResources().getColor(R.color.light_red));
			}
			if (event.getAction() == MotionEvent.ACTION_UP) {
				((TextView) findViewById(R.id.btn_before1)).setTextColor(Color.BLACK);
			}

			return false;
		}
	};

	OnTouchListener nextUIChagner = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				((TextView) findViewById(R.id.btn_next1)).setTextColor(getResources().getColor(R.color.light_red));
			}
			if (event.getAction() == MotionEvent.ACTION_UP) {
				((TextView) findViewById(R.id.btn_next1)).setTextColor(Color.BLACK);
			}

			return false;
		}
	};

	Rect outRect = new Rect();

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN :

				break;
			case MotionEvent.ACTION_MOVE :
				break;
			case MotionEvent.ACTION_UP :
			case MotionEvent.ACTION_CANCEL :

				break;

			default :
				break;
		}

		return gesture.onTouchEvent(event);
	}

	void setIndexText() {
		mIndex.setText(Integer.toString(imgIdx + 1) + " / " + Integer.toString(imageList.size()));
	}

	public void onClick(View v) {

		if (isClick == true) {
			if (R.id.btn_before1 == v.getId() || R.id.btn_before2 == v.getId() || R.id.btn_before == v.getId()) {// ????????????
				saveCropInfo();// ?????? ?????? crop?????? ??????

				imgIdx--;
				if (imgIdx < 0) {
					imgIdx = 0;
					MessageUtil.toast(CropImageActivity4.this, getString(R.string.PhotoPrintpage_start));
					return;
				}

				loadImg();

				isClick = false;

			} else if (R.id.btn_next1 == v.getId() || R.id.btn_next2 == v.getId() || R.id.btn_next == v.getId()) {// ????????????

				saveCropInfo();// ?????? ?????? crop?????? ??????
				imgIdx++;
				if (imgIdx >= imageList.size()) {
					imgIdx = imageList.size() - 1;
					MessageUtil.toast(CropImageActivity4.this, getString(R.string.PhotoPrintpage_finish));
					return;
				}

				loadImg();

				isClick = false;

			} else if (R.id.bottom_view == v.getId()) {

				MyPhotoSelectImageData data = imageList.get(imgIdx);

				int angle = data.ROTATE_ANGLE + 90;
				int thumbAngle = data.ROTATE_ANGLE_THUMB + 90;

				if (angle >= 360)
					angle = 0;

				// ???????????? -1?????? ????????? 360?????? 350 ?????? ???...
				if (thumbAngle >= 350)
					thumbAngle = 0;

				// ????????? 89??? ????????? ??????????????????...
				if (thumbAngle > 80 && thumbAngle <= 90)
					thumbAngle = 90;

				data.ROTATE_ANGLE = angle;
				data.ROTATE_ANGLE_THUMB = thumbAngle;

				// ????????? ?????? movePercent??? ????????? ??????.
				data.CROP_INFO.movePercent = 0;

				loadImg();
			}
		}
	}

	OnGestureListener onGesture = new OnGestureListener() {
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

			if (isClick == true) {
				if (orientValue == CORP_ORIENT.HEIGHT)
					moveRectangle(-distanceY);
				else if (orientValue == CORP_ORIENT.WIDTH)
					moveRectangle(-distanceX);
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

		// ???????????? center??? ??????????????? center?????? ?????? ????????? ????????? ????????? ????????? ????????? ?????????.
		// ????????? ???(?????????)??? center??? ?????????.
		PointF screenCenter = new PointF(imgOrigin.getX() + imgOrigin.getWidth() / 2.f, imgOrigin.getY() + imgOrigin.getHeight() / 2.f);
		// ??????????????? center??? ?????????.
		PointF cropRectCenter = new PointF((layoutCropRect.getX() + layoutCropRect.getWidth() / 2.f), (layoutCropRect.getY() + layoutCropRect.getHeight() / 2.f));

		float movePercent = 0;
		int startPercent = 0;
		int endPercent = 0;
		if (orientValue == CORP_ORIENT.HEIGHT) {// ????????????

			// ????????? ????????? ????????????/??????????????? ????????? ????????? ??????.
			float moveY = screenCenter.y - cropRectCenter.y;
			movePercent = moveY / (float) screenImgHeight;
			// ??????????????? ???????????? ????????? ?????? ????????? ???..
			// ?????? ?????? ??????

			float startY = layoutCropRect.getY() - imgOrigin.getY() - (imgOrigin.getHeight() - screenImgHeight) / 2.f;

			startPercent = (int) (((float) startY / screenImgHeight) * 1000.f);
			endPercent = (int) (((float) (startY + layoutCropRect.getHeight()) / screenImgHeight) * 1000.f);
		} else if (orientValue == CORP_ORIENT.WIDTH) {// ????????????

			float moveX = screenCenter.x - cropRectCenter.x;
			movePercent = moveX / (float) screenImgWidth;
			// ??????????????? ???????????? ????????? ?????? ????????? ???..
			// ?????? ?????? ??????

			float startX = layoutCropRect.getX() - imgOrigin.getX() - (imgOrigin.getWidth() - screenImgWidth) / 2.f;
			startPercent = (int) ((startX / screenImgWidth) * 1000.f);
			endPercent = (int) (((float) (startX + layoutCropRect.getWidth()) / screenImgWidth) * 1000.f);
		}

		CropInfo cropInfo = null;
		// if (Math.abs(movePercent) > 0) {
		cropInfo = new CropInfo(orientValue, movePercent, startPercent, endPercent);
		cropInfo.CROP_ACCURACY = 1000.f;
		Dlog.d(cropInfo.toString());

		// ?????? ?????? ??????...
		imageList.get(imgIdx).isModify = 0;
		// crop?????? ??????
		imageList.get(imgIdx).CROP_INFO = cropInfo;

	}

	void loadImg() {

		// ????????? ???????????? ????????????.
		setIndexText();
		String imgUri = "";

		int angle = 0;

		if (Config.isSNSPhoto(imageList.get(imgIdx).KIND)){
			imgUri = imageList.get(imgIdx).PATH;
			angle = imageList.get(imgIdx).ROTATE_ANGLE;
		} else if (imageList.get(imgIdx).KIND == Const_VALUES.SELECT_PHONE) {
			imgUri = imageList.get(imgIdx).PATH;
			angle = imageList.get(imgIdx).ROTATE_ANGLE;
		} else if (imageList.get(imgIdx).KIND == Const_VALUES.SELECT_UPLOAD) {
			imgUri = SnapsAPI.DOMAIN(false) + imageList.get(imgIdx).getSafetyThumbnailPath();
			angle = imageList.get(imgIdx).ROTATE_ANGLE;
		}

		f_Ratio = (float) imageList.get(imgIdx).cropRatio;

		final String imageUri = imgUri;
		ImageDirectLoader.loadAllImage(this, angle, imgUri, imgOrigin, (int) mScreenWidth, progressImg, new ImageDirectLoader.OnLoadComplete() {
			@Override
			public void onComplete(int width, int height) {
				Dlog.d("onComplete() " + "path:" + imageUri + ", w:" + width + ", h:" + height);
				boxSizing(width, height);
			}

			@Override
			public void onFailedLoad() {
				MessageUtil.toast(CropImageActivity4.this, getString(R.string.image_loding_fail_text));
				CropImageActivity4.this.finish();			
			}
		});
	}

	// ????????? ??????(????????????)
	// crop ?????? ??????
	void boxSizing(final int imgWidth, final int imgHeight) {
		mScreenWidth = imgOrigin.getWidth();
		mScreenHeight = imgOrigin.getHeight();

		// ???????????? ????????? ??????????????? ????????? ????????? ?????? ???
		float ratioX = (float) mScreenWidth / (float) imgWidth;
		float ratioY = (float) mScreenHeight / (float) imgHeight;

		// ???????????? ???????????? ????????? ????????? ?????????
		if (ratioX >= ratioY) {
			screenImgHeight = mScreenHeight;
			screenImgWidth = imgWidth * ratioY;
		} else {
			screenImgWidth = mScreenWidth;
			screenImgHeight = imgHeight * ratioX;
		}

		Dlog.d("boxSizing() origin image w:" + mScreenWidth + ", h:" + mScreenHeight);
		Dlog.d("boxSizing() ratio x:" + ratioX + ", y:" + ratioY);
		Dlog.d("boxSizing() resize image w:" + screenImgWidth + ", h:" + screenImgHeight);

		orientValue = CORP_ORIENT.NONE;

		boolean isStandard = (screenImgWidth / screenImgHeight) > f_Ratio ? true : false;

		orientValue = isStandard ? CORP_ORIENT.WIDTH : CORP_ORIENT.HEIGHT;

		final float imgCropWidth = isStandard ? (screenImgHeight * f_Ratio) : screenImgWidth;
		final float imgCropHeight = isStandard ? screenImgHeight : (screenImgWidth / f_Ratio);

		rectLP.width = (int) Math.ceil(imgCropWidth);
		rectLP.height = (int) Math.ceil(imgCropHeight);
		layoutCropRect.setLayoutParams(rectLP);

		// ???????????? limit ??????
		if (isStandard) {// CORP_ORIENT.WIDTH
			originX = (mScreenWidth - imgCropWidth) / 2.f + imgOrigin.getX();
			maxMove = (screenImgWidth - imgCropWidth) / 2.f;
			minX = originX - maxMove;
			maxX = originX + maxMove;
		} else {
			originY = (mScreenHeight - imgCropHeight) / 2.f + imgOrigin.getY();
			maxMove = (screenImgHeight - imgCropHeight) / 2.f;
			minY = originY - maxMove;
			maxY = originY + maxMove;

		}

		// ???????????? ????????? ????????????.
		adjustCropRect(imgCropWidth, imgCropHeight);
	}

	/***
	 * ????????? cropRect??? ???????????? ????????? ???????????? ???
	 */
	void adjustCropRect(float cWidth, float cHeight) {
		// ??????????????? ???????????? ?????????.
		Dlog.d("adjustCropRect() w:" + cWidth + ", h:" + cHeight);

		float x = ((mScreenWidth - cWidth) / 2.f) + imgOrigin.getX();
		float y = ((mScreenHeight - cHeight) / 2.f) + imgOrigin.getY();

		// ?????? ?????? ????????? ????????????.
		// ????????? ????????? ????????? ??????.
		CropInfo cInfo = imageList.get(imgIdx).CROP_INFO;
		if (cInfo != null && CropInfo.CORP_ORIENT.NONE != cInfo.cropOrient) {
			float cropSize = cInfo.cropOrient == CORP_ORIENT.WIDTH ? screenImgWidth : screenImgHeight;
			float offsetValue = cropSize * cInfo.movePercent;

			if (cInfo.cropOrient == CORP_ORIENT.WIDTH) {
				x -= offsetValue;
			} else {
				y -= offsetValue;
			}
		}

		layoutCropRect.setX(x);
		layoutCropRect.setY(y);

		isClick = true;

	}

	void moveRectangle(float move) {
		if (orientValue == CORP_ORIENT.WIDTH) {// ???????????? Landscape
			float goX = layoutCropRect.getX() + move;
			if (goX < minX)
				layoutCropRect.setX(minX);
			else if (goX > maxX)
				layoutCropRect.setX(maxX);
			else
				layoutCropRect.setX(goX);
		} else if (orientValue == CORP_ORIENT.HEIGHT) {// ???????????? Portrait
			float goY = layoutCropRect.getY() + move;
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
		imageList.clear();
		imageList = null;

		ImageUtil.recycleBitmap(imgOrigin);
		try {
			ViewUnbindHelper.unbindReferences(getWindow().getDecorView(), null, false);
			// System.gc();
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
