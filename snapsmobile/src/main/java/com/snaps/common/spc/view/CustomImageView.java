package com.snaps.common.spc.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.snaps.common.data.img.ExifUtil;
import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.imp.iSnapsPageCanvasInterface;
import com.snaps.common.structure.control.SnapsControl;
import com.snaps.common.structure.control.SnapsLayoutControl;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.BitmapUtil;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.common.utils.ui.ViewIDGenerator;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.diary.SnapsDiaryDataManager;
import com.snaps.mobile.activity.themebook.PhotobookCommonUtils;
import com.snaps.mobile.autosave.AutoSaveManager;
import com.snaps.mobile.component.ColorBorderView;
import com.snaps.mobile.component.MaskImageView;
import com.snaps.mobile.interfaces.ISnapsControl;
import com.snaps.mobile.interfaces.ImpMaskImageViewListener;
import com.snaps.mobile.utils.smart_snaps.SmartSnapsManager;

import errorhandle.logger.Logg;

public class CustomImageView extends ViewGroup implements ImpMaskImageViewListener {
	private static final String TAG = CustomImageView.class.getSimpleName();

	private MaskImageView imageview = null;

	private SnapsLayoutControl layout = null;
	private float radius = 0;
	final Path path = new Path();
	String pageType;

	private iSnapsPageCanvasInterface _callback = null;

	private boolean isThumbnail = false;
	private boolean isPreview = false;

	private Bitmap oriBitmap = null;
	private Bitmap maskBitmap = null;

	private ProgressBar imageLoadProgress = null;

	public interface OnImageLoadListener {
		void onImageLoad(CustomImageView imageView);
	}

	protected OnImageLoadListener mOnImageLoadListner;

	public void setOnImageLoadListener(OnImageLoadListener i) {
		mOnImageLoadListner = i;
	}

	public boolean isThumbnail() {
		return isThumbnail;
	}

	public void setIsThumbnail(boolean isThumbnail) {
		this.isThumbnail = isThumbnail;
	}

	public boolean isPreview() {
		return isPreview;
	}

	public void setIsPreview(boolean isPreview) {
		this.isPreview = isPreview;
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
	}

	@Override
	public void draw(Canvas canvas) {
		try {
			if (!path.isEmpty()) {
				canvas.clipPath(path);
			}

			super.draw(canvas);

			if (BitmapUtil.isUseAbleBitmap(maskBitmap)) {
				Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
				paint.setAntiAlias(true);
				paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
				canvas.drawBitmap(maskBitmap, 0, 0, paint);
				paint.setXfermode(null);
			}
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
	}

	public CustomImageView(Context context) {
		super(context);

		setLayerType(View.LAYER_TYPE_SOFTWARE, null);
	}

	/***
	 * 
	 * @param type
	 * @param context
	 * @param layout
	 * @param callback
	 */
	public CustomImageView(String type, Context context, SnapsLayoutControl layout, iSnapsPageCanvasInterface callback) {
		super(context);
		this._callback = callback;
		init(type, context, layout);
	}

	/***
	 * Custom Image View 생성.
	 * 
	 * @param type
	 * @param context
	 * @param layout
	 */
	public CustomImageView(String type, Context context, SnapsLayoutControl layout) {
		super(context);
		init(type, context, layout);

	}

	public void init(String type, final Context context, SnapsLayoutControl _layout) {

		setLayerType(View.LAYER_TYPE_SOFTWARE, null);

		pageType = type;
		this.layout = _layout;

		int x = 0;
		int y = 0;
		int width = 0;
		int height = 0;

		/**
		 * 일기는 편집화면, 리스트 화면을 선명하게 해 달라는 이슈가 있어서,
		 * scaled된 edSize로 보여준다.
		 */
		if (SnapsDiaryDataManager.isAliveSnapsDiaryService()) {
			x = layout.getScaledX();
			y = layout.getScaledY();
			width = (int) Float.parseFloat(layout.getScaledWidth());
			height = (int) Float.parseFloat(layout.getScaledHeight());
		} else {
			x = layout.getX();
			y = (int) Float.parseFloat(layout.y);
			width = (int) Float.parseFloat(this.layout.width);
			height = (int) Float.parseFloat(this.layout.height);
		}

		ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(width, height);
		params.setMargins(x, y, 0, 0);
		this.setLayoutParams(new FrameLayout.LayoutParams(params));

		ViewGroup.MarginLayoutParams imageParams = new ViewGroup.MarginLayoutParams(params.width, params.height);

		imageview = new MaskImageView(getContext());

		if (_callback != null /* && layout.imgData != null */) {
			if (!isThumbnail() && !isPreview()) {
				AutoSaveManager saveMan = AutoSaveManager.getInstance();
				if (saveMan != null && saveMan.isRecoveryMode()) {
//					layout.setControlId(-1);
				}
				int generatedId = ViewIDGenerator.generateViewId(layout.getControlId());
				layout.setControlId(generatedId);
				imageview.setId(generatedId);
			}
		} else if (PhotobookCommonUtils.shouldForceSetControlIdProduct()) {
			int generatedId = ViewIDGenerator.generateViewId(layout.getControlId());
			layout.setControlId(generatedId);
			imageview.setId(generatedId);
		}

		imageview.setImgListener(this);

		imageview.setLayoutParams(imageParams);

		if (layout.maskType.equalsIgnoreCase("image") || (layout.imgData != null && layout.imgData.KIND == 2)
				|| (Config.isSimpleMakingBook() && layout.imgData != null && !"cover".equalsIgnoreCase(pageType)) || layout.isImageFull) {

			// 이미지 풀
			imageview.setScaleType(ImageView.ScaleType.FIT_CENTER);
		} else {
			// 페이지 풀
			imageview.setScaleType(ImageView.ScaleType.CENTER_CROP);

			if (!this.layout.maskRadius.equalsIgnoreCase("")) {
				radius = Float.valueOf(this.layout.maskRadius);
			}
		}

		if (radius > 0) {
			path.addRoundRect(new RectF(0, 0, Integer.parseInt(this.layout.width), Integer.parseInt(this.layout.height)), radius, radius, Direction.CW);
		}

		this.addView(imageview);
		Dlog.d("init() pageType:" + pageType);

		if (!"".equals(layout.bgColor)) {
			if (!layout.mask.isEmpty() || layout.isImageFull) { // 마스크가 있는경우 배경을 투명으로 설정한다. 색상을 넣으면...이상..
				this.setBackgroundColor(Color.TRANSPARENT);
			} else {
				this.setBackgroundColor(Color.parseColor("#" + layout.bgColor));
			}

		}

		boolean isEnableClick = true;
		if ((Const_PRODUCT.isSNSBook()) && !layout.isSnsBookCover)
			isEnableClick = false;

		if (isThumbnail() || isPreview())
			isEnableClick = false;

		//CS 대응
		if (Config.isDevelopVersion()) {
			if (isEnableClick == false) {
				if (Const_PRODUCT.isSnapsDiary()) {
					//일기 커버 페이지 아닌 경우도 이미지 편집 가능 하도록
					isEnableClick = true;
				}
			}
		}

		if (isEnableClick && _callback != null && layout.type.equals("browse_file")) {
			imageview.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// action 보내기.
					sendLayoutClickAction(context, v, false);
					//CS 대응
					if (Config.isDevelopVersion()) {
						logImageData(v);
					}
				}
			});

			imageview.setOnTouchListener(new OnTouchListener() {
				private View actionDownView = null;
				@SuppressLint("ClickableViewAccessibility")
				@Override
				public boolean onTouch(View view, MotionEvent motionEvent) {
					actionDownView = view;
					if (gestureDetector != null)
						gestureDetector.onTouchEvent(motionEvent);
					return false;
				}

				private GestureDetector gestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {
					public void onLongPress(MotionEvent e) {
						if (actionDownView != null)
							sendLayoutClickAction(getContext(), actionDownView, true);
					}
				});
			});
		}

		if (layout.bordersinglecolortype.equals("1")) {
			addBorderView(layout);
		}

		if ( layout != null && layout.qrCodeUrl != null && layout.qrCodeUrl.length() > 0) {
			addQRCode(layout);
		}
	}

	//CS 대응
	private void logImageData(View v) {
		if (v instanceof MaskImageView == false) return;

		MaskImageView maskImageView = (MaskImageView)v;

		SnapsControl snapsControl = maskImageView.getSnapsControl();
		if (snapsControl == null) return;
		if (snapsControl instanceof SnapsLayoutControl == false) return;

		SnapsLayoutControl snapsLayoutControl = (SnapsLayoutControl)snapsControl;
		MyPhotoSelectImageData myPhotoSelectImageData = snapsLayoutControl.imgData;
		if (myPhotoSelectImageData == null) return;

		String domain = SnapsAPI.DOMAIN();
		StringBuilder sb = new StringBuilder();

		sb.append("\n");

		sb.append("IMAGE_ID : ").append(myPhotoSelectImageData.IMAGE_ID).append("\n");

		if (myPhotoSelectImageData.THUMBNAIL_PATH != null && myPhotoSelectImageData.THUMBNAIL_PATH.length() > 0) {
            sb.append("THUMBNAIL_PATH : ").append(domain).append(myPhotoSelectImageData.THUMBNAIL_PATH).append("\n");
        }

        if (myPhotoSelectImageData.ORIGINAL_PATH != null && myPhotoSelectImageData.ORIGINAL_PATH.length() > 0) {
            sb.append("ORIGINAL_PATH : ").append(domain).append(myPhotoSelectImageData.ORIGINAL_PATH).append("\n");
        }

		sb.append("PATH : ").append(myPhotoSelectImageData.PATH).append("\n");
		sb.append("LOCAL_THUMBNAIL_PATH : ").append(myPhotoSelectImageData.LOCAL_THUMBNAIL_PATH).append("\n");

		if (myPhotoSelectImageData.EFFECT_PATH != null && myPhotoSelectImageData.EFFECT_PATH.length() > 0) {
			sb.append("EFFECT_PATH : ").append(myPhotoSelectImageData.EFFECT_PATH).append("\n");
		}

		if (myPhotoSelectImageData.EFFECT_THUMBNAIL_PATH != null && myPhotoSelectImageData.EFFECT_THUMBNAIL_PATH.length() > 0) {
			sb.append("EFFECT_THUMBNAIL_PATH : ").append(myPhotoSelectImageData.EFFECT_THUMBNAIL_PATH).append("\n");
		}

		sb.append("ORIGINAL W H : ").append(myPhotoSelectImageData.F_IMG_WIDTH).append(" x ");
		sb.append(myPhotoSelectImageData.F_IMG_HEIGHT).append("\n");

		ExifUtil.SnapsExifInfo snapsExifInfo = myPhotoSelectImageData.getExifInfo();
		if (snapsExifInfo != null) {
			sb.append("Exif OrientationTag : ").append(snapsExifInfo.getOrientationTag()).append("\n");
		}

		sb.append("Layout X Y W H : ").append(snapsLayoutControl.x).append(", ").append(snapsLayoutControl.y).append(", ");
		sb.append(snapsLayoutControl.width).append(", ").append(snapsLayoutControl.height).append("\n");

		sb.append("ROTATE_ANGLE : ").append(myPhotoSelectImageData.ROTATE_ANGLE).append("\n");
		sb.append("ROTATE_ANGLE_THUMB : ").append(myPhotoSelectImageData.ROTATE_ANGLE_THUMB).append("\n");
		sb.append("FREE_ANGLE : ").append(myPhotoSelectImageData.FREE_ANGLE).append("\n");

		Dlog.d(Dlog.PRE_FIX_CS + sb.toString());
	}


	private boolean isExistImageData() {
		if (imageview == null) return false;
		Object obj = imageview.getSnapsControl();
		if (obj != null && obj instanceof SnapsLayoutControl) {
//							SnapsLayoutControl press_control = (SnapsLayoutControl) imageview.getTag();
			SnapsLayoutControl press_control = (SnapsLayoutControl) obj;
			return press_control.imgData != null;
		}
		return true;
	}

	private void sendLayoutClickAction(Context context, View v, boolean isLongClick) {
		if (context == null || v == null || isThumbnail() || isPreview() || layout.imgData == null) return;

		boolean isEdited = isExistImageData();

		Intent intent = new Intent(Const_VALUE.CLICK_LAYOUT_ACTION);
		intent.putExtra("control_id", v.getId());
		intent.putExtra("isEdit", false);
		intent.putExtra("pageType", pageType);
		intent.putExtra("isLongClick", isLongClick);
		if (layout != null) {
			if (Const_PRODUCT.isSNSBook()) {
				//CS 대응
				//브로드캐스트 수신하는 곳에서 isEditableImg 값을 검사한다.
				boolean isEditableImg = layout.isSnsBookCover;
				if (Config.isDevelopVersion()) {
					isEditableImg = true;
				}
				intent.putExtra("isEditableImg", isEditableImg);
			}

			intent.putExtra("isEdited", isEdited);
		}

		context.sendBroadcast(intent);
	}

	public ProgressBar createImageLoadProgressWithImageView(float width, float height) {
		if (isThumbnail || !SmartSnapsManager.isSupportSmartSnapsProduct() || !SmartSnapsManager.isSmartAreaSearching()) return null;

		if (imageLoadProgress != null)
			removeView(imageLoadProgress);

		imageLoadProgress = new ProgressBar(getContext());
		Drawable drawable = getContext().getResources().getDrawable(R.drawable.smart_page_simple_progress);
		imageLoadProgress.setIndeterminateDrawable(drawable);
		imageLoadProgress.setIndeterminate(true);

		final int IMAGE_LOAD_PROGRESS_DIMENSION = UIUtil.convertDPtoPX(getContext(), 8);

		ViewGroup.MarginLayoutParams progressParams = new FrameLayout.LayoutParams(IMAGE_LOAD_PROGRESS_DIMENSION, IMAGE_LOAD_PROGRESS_DIMENSION);
		progressParams.leftMargin = (int) (((width) / 2) - (IMAGE_LOAD_PROGRESS_DIMENSION / 2));
		progressParams.topMargin = (int) (((height) / 2) - (IMAGE_LOAD_PROGRESS_DIMENSION / 2));
		imageLoadProgress.setLayoutParams(progressParams);
		imageLoadProgress.requestLayout();

		this.addView(imageLoadProgress);
		imageLoadProgress.bringToFront();
		return imageLoadProgress;
	}

	/***
	 * 사진틀 추가..
	 * 
	 * @param control
	 */
	void addBorderView(SnapsControl control) {
		ColorBorderView border = new ColorBorderView(getContext());

		ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(Integer.parseInt(control.width), Integer.parseInt(control.height));
		border.setLayoutParams(new FrameLayout.LayoutParams(params));
		border.setBorderWidth((SnapsLayoutControl) control);

		this.addView(border);
	}

	/**
	 * ImageView 반환
	 * @return
	 */
	public ISnapsControl getImageView() {
		return imageview;
	}

	/**
	 * 
	 * 시퀀스 이미지 로드.
	 * 
	 * @param seq
	 */
	public void setImageSeq(String[] seq) {
		this.layout.imgYear = seq[2];
		this.layout.imgSeq = seq[3];
		this.layout.imgWidth = seq[4];
		this.layout.imgHeight = seq[5];
		// seq[ 6 ]; 회전 여부.
		this.layout.oriPath = seq[7];
		this.layout.thumPath = seq[8];
		this.layout.tinyPath = seq[9];
		this.layout.realFileName = seq[10];
		this.layout.imagePath = seq[10];

		this.layout.local = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View#onMeasure(int, int)
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		try {
			final int count = getChildCount();
			int maxHeight = 0;
			int maxWidth = 0;

			for (int i = 0; i < count; i++) {
				final View child = getChildAt(i);

				if (child.getVisibility() != GONE) {
					measureChild(child, widthMeasureSpec, heightMeasureSpec);
				}
			}

			maxWidth += getPaddingLeft() + getPaddingRight();
			maxHeight += getPaddingTop() + getPaddingBottom();

			Drawable drawble = getBackground();

			if (drawble != null) {
				maxHeight = Math.max(maxHeight, drawble.getMinimumHeight());
				maxWidth = Math.max(maxWidth, drawble.getMinimumWidth());
			}

			setMeasuredDimension(resolveSize(maxWidth, widthMeasureSpec), resolveSize(maxHeight, heightMeasureSpec));
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {

		final int count = getChildCount();

		final int parentLeft = this.getPaddingLeft();
		final int parentTop = this.getPaddingTop();

		ViewGroup.MarginLayoutParams marginParams = null;

		for (int i = 0; i < count; i++) {
			final View child = getChildAt(i);

			if (child.getVisibility() != GONE) {
				final LayoutParams lp = (LayoutParams) child.getLayoutParams();

				final int width = Math.max(lp.width, child.getMeasuredWidth());
				final int height = Math.max(lp.height, child.getMeasuredHeight());

				int childLeft = parentLeft + child.getPaddingLeft();
				int childTop = parentTop + child.getPaddingTop();

				if (lp instanceof ViewGroup.MarginLayoutParams) {
					marginParams = (ViewGroup.MarginLayoutParams) lp;

					childLeft += marginParams.leftMargin;
					childTop += marginParams.topMargin;
				}

				child.layout(childLeft, childTop, childLeft + width, childTop + height);
			}
		}
	}

	@Override
	public void completeLoadBitmap(Bitmap bitmap) {
//		BitmapUtil.bitmapRecycle(oriBitmap);
		oriBitmap = bitmap;
	}

	/**
	 * 동영상인 경우 qrcode를 추가한다.
	 */
	public void addQRCode(SnapsLayoutControl data) {
		int barcodeSize = 20;
		ImageView qrCodeImage = new ImageView(getContext());
		ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(barcodeSize,barcodeSize);
		params.leftMargin = data.getIntWidth() - barcodeSize - 10;
		params.topMargin = data.getIntHeight() - barcodeSize - 10;
		qrCodeImage.setLayoutParams(new FrameLayout.LayoutParams(params));
		qrCodeImage.setBackgroundResource(R.drawable.qrcode);
		addView(qrCodeImage);
	}

	public void releaseInstance() {
//		ImageLoader.clear(getContext(), imageview);
		imageLoadProgress = null;
	}

	public void setMaskBitmap(Bitmap maskBitmap) {
		this.maskBitmap = maskBitmap;
		setLayerType(LAYER_TYPE_HARDWARE, null);
	}

	public boolean isImagePng() {
		return layout.imagePath.contains(".png");
	}

	public SnapsLayoutControl getLayoutControl() {
		return layout;
	}
}
