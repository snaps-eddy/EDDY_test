package com.snaps.mobile.component;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.structure.photoprint.SnapsPhotoPrintItem;
import com.snaps.common.utils.imageloader.recoders.AdjustableCropInfo;
import com.snaps.common.utils.imageloader.recoders.CropInfo;
import com.snaps.common.utils.imageloader.recoders.CropInfo.CORP_ORIENT;

public class SnapsAdjustCropRangePhotoPrintView_backup_20141031 extends ImageView {

	private final float LIMIT_MAX_CROP_RATIO = 10.f; //크롭 영역은 원본 이미지의 일정 비율 이상 작게 설정하지 못한다.
	private final int SIZE_ADJUST_TOUCH_RECT_HALF_SIZE = 10; //크롭 손잡이 크기
	
	// 인화영역 크기...
	RectF mClipRect = new RectF(); // 영역그리는 크기
	RectF mClipRect3 = new RectF(); // 불투명하게 그리는 크기...
	
	//드래그 손잡이
	RectF mAdjustCropRect = new RectF(); 
	
	// 배경색상 설정..
	int mBackgroundColor;
	// 인화영역 색상..
	int mClipRectColor;
	int mLineSize = 3; // 인화영역 두께..

	Paint mPaint; // 인화영역 그리는 Paint
	boolean mIsTouch = true;
	boolean mIsAdjustCropSize = false;
	PointF mPrePos = new PointF();

	float mViewPortWRation = 0.0f;
	float mViewPortHRation = 0.0f;

	CORP_ORIENT orientValue = CORP_ORIENT.NONE;
	int mOriginX, mOriginY, mMaxMove, mMinX, mMaxX, mMinY, mMaxY;
	float mRatio = 0.0f;
	// view전체크기
	int mScreenWidth, mScreenHeight;
	// 화면에 뿌려지는 이미지 크기...
	float mScreenImgWidth = 0.0f, mScreenImgHeight = 0.0f;

	int mOriginImgWidth, mOriginImgHeight;

	MyPhotoSelectImageData mImgData;
	SnapsPhotoPrintItem mItem;

	boolean mIsViewRotate = false;
	
	float m_fRatioX = 0.f, m_fRatioY = 0.f;
	
	//크롭되는 사각형의 가로, 세
	float m_fImgCropWidth  = 0.f;
	float m_fImgCropHeight = 0.f;
	
	float m_fCropStartXPt = 0.f;
	float m_fCropStartYPt = 0.f;
	
	boolean m_isCropXScale = false;//mClipRect.width() < mClipRect.height();
	float m_fCropRatio = 0.f;//isXScale ? mClipRect.width() / mClipRect.height() : mClipRect.height() / mClipRect.width();
	float fixSize = 0.f;
	
	Context mContext;

	public void setmIsViewRotate(boolean mIsViewRotate) {
		this.mIsViewRotate = mIsViewRotate;
	}

	public SnapsAdjustCropRangePhotoPrintView_backup_20141031(Context context) {
		super(context);
		init(context);
	}

	public SnapsAdjustCropRangePhotoPrintView_backup_20141031(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public SnapsAdjustCropRangePhotoPrintView_backup_20141031(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init(context);
	}

	void init(Context context) {

		mContext = context;
		
		setLayerType(View.LAYER_TYPE_SOFTWARE, null);

		mBackgroundColor = Color.parseColor("#66000000");
		mClipRectColor = Color.parseColor("#FFEF4123");

		// px => dip
		float scale = getResources().getDisplayMetrics().density;
		mLineSize = (int) (mLineSize * scale + 0.5f);

		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setColor(mClipRectColor);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeWidth(mLineSize); 

		setWillNotDraw(false);
	}

	/***
	 * 라인 크기를 설정하는 함수
	 * 
	 * @param width
	 */
	public void setLineBorderWidth(int width) {
		float scale = getResources().getDisplayMetrics().density;
		mLineSize = (int) (width * scale + 0.5f);
	}

	/***
	 * 인화영역 설정 뷰의 크기를 조정한다.
	 * 
	 * @param imgWidth
	 * @param imgHeight
	 * @param cInfo
	 * @param viewPortRation
	 * @param isTouch
	 */
	public void setAdjustClipBound(MyPhotoSelectImageData imgData, float viewPortWRation, float viewPortHRation, boolean isTouch) {
		mImgData = imgData;
		mViewPortWRation = viewPortWRation;
		mViewPortHRation = viewPortHRation;
		mIsTouch = isTouch;

		mOriginImgWidth = Integer.parseInt(mImgData.F_IMG_WIDTH);
		mOriginImgHeight = Integer.parseInt(mImgData.F_IMG_HEIGHT);

	}

	/***
	 * 인화영역 설정하는 함수...
	 */
	public void calculatorClipRect() {
		// 이미지 크기를 가지고 뷰의 크기를 조정한다.
		// 이미지가 가로에 맞춰야할지 세로에 맞춰야 할지 확

		int originWidth = mOriginImgWidth;
		int originHeight = mOriginImgHeight;

		// exif에 이미지의 기본 회전정보
		boolean isRotate = false;
		if (!mIsViewRotate && ((mImgData.ROTATE_ANGLE == 90) || (mImgData.ROTATE_ANGLE == 270))) {
			int temp = originWidth;
			originWidth = originHeight;
			originHeight = temp;
			isRotate = true;
		}

		m_fRatioX = (float) mScreenWidth / (float) originWidth;
		m_fRatioY = (float) mScreenHeight / (float) originHeight;

		// 스크린에 뿌려지는 이미지 크기를 구한다
		if (m_fRatioX >= m_fRatioY) {
			mScreenImgHeight = mScreenHeight;
			mScreenImgWidth = originWidth * m_fRatioY;
		} else {
			mScreenImgWidth = mScreenWidth;
			mScreenImgHeight = originHeight * m_fRatioX;
		}

		if (originWidth > originHeight) {
			mRatio = mViewPortWRation;
		} else if (originWidth < originHeight) {
			mRatio = mViewPortHRation;
		} else if (originWidth == originHeight) {

			if (isRotate)
				mRatio = mViewPortWRation;
			else
				mRatio = mViewPortHRation;

		}

		// viewPort 이동방향을 설정한다.
		boolean isStandard = (mScreenImgWidth / mScreenImgHeight) > mRatio ? true : false;
		orientValue = isStandard ? CORP_ORIENT.WIDTH : CORP_ORIENT.HEIGHT;

		// viewport 크기 설정...
		m_fImgCropWidth = isStandard ? (mScreenImgHeight * mRatio) : mScreenImgWidth;
		m_fImgCropHeight = isStandard ? mScreenImgHeight : (mScreenImgWidth / mRatio);
		
		// 크롭영역 limit 설정
		if (orientValue == CORP_ORIENT.HEIGHT) {
			mOriginY = (int) (mScreenImgHeight - m_fImgCropHeight) / 2;
			mMaxMove = (int) ((mScreenImgHeight - m_fImgCropHeight) / 2);
			mMinY = mOriginY - mMaxMove;
			mMaxY = mOriginY + mMaxMove;
		} else {
			mOriginX = (int) (mScreenImgWidth - m_fImgCropWidth) / 2;
			mMaxMove = (int) ((mScreenImgWidth - m_fImgCropWidth) / 2);
		}

		// 크롭영역 위치를 조정한다.
		adjustCropRect(m_fImgCropWidth, m_fImgCropHeight);
	}

	/***
	 * width와 heigt를 바꾸는 함수..
	 */
	void changeWidthHeight() {
		float temp = mScreenImgHeight;
		mScreenImgHeight = mScreenImgWidth;
		mScreenImgWidth = temp;
	}

	void adjustCropRect(float cWidth, float cHeight) {
		
		// 기존 크롭 정보를 로드한다.
		// 정보가 있으면 적용을 한다.
		if(mImgData.isAdjustableCropMode) {
			// 좌표를 가지고 세로 그린다.
			return;
		}
		
		// 크롭영역을 가운데에 맞춘다.
		int x = (int) ((mScreenImgWidth - cWidth) / 2);
		int y = (int) Math.floor(((mScreenImgHeight - cHeight) / 2));
		
		// 좌표를 가지고 세로 그린다.
		setClipRect(x, y , cWidth, cHeight);
		// requestLayout();
		invalidate();
	}

	void setClipRect(float left, float top, float cropWidth, float cropHeight) {
		// 불투명하게 그릴영
		if (mScreenImgWidth == mScreenWidth) {
			mClipRect3 = new RectF(0, (mScreenHeight - mScreenImgHeight) / 2.0f, mScreenImgWidth, mScreenImgHeight + (mScreenHeight - mScreenImgHeight) / 2.0f);
		} else {
			mClipRect3 = new RectF((mScreenWidth - mScreenImgWidth) / 2.0f, 0, mScreenImgWidth + (mScreenWidth - mScreenImgWidth) / 2.0f, mScreenImgHeight);
		}

		// 인화영역
		mClipRect.set(left + mClipRect3.left, top + mClipRect3.top, cropWidth + left + mClipRect3.left, top + cropHeight + mClipRect3.top);
		
		m_fCropStartXPt = mClipRect.left;
		m_fCropStartYPt = mClipRect.top;
		
		// 인화영역 그리는 영역 (빨간 사각형..)

		m_isCropXScale = mClipRect.width() < mClipRect.height();
		m_fCropRatio = m_isCropXScale ? mClipRect.width() / mClipRect.height() : mClipRect.height() / mClipRect.width();
		
		fixSize = m_isCropXScale ? mScreenImgWidth - cropWidth : mScreenImgHeight - cropHeight;
		
		//드래그 손잡이 영역
		mAdjustCropRect = new RectF((mClipRect.left - (mLineSize*4)),
				mClipRect.top - (mLineSize*4),
				mClipRect.left + (mLineSize*4),
				mClipRect.top + (mLineSize*4));
		
		invalidate();
	}
	
	private float getRectArea(RectF rect)
	{
		if(rect == null) return 0;
		
		return rect.width() * rect.height();
	}

	void offsetClipRect(float evtX, float evtY, float moveX, float moveY) {

		if(mIsAdjustCropSize) //크롭 박스 크기를 조절하는 손잡이를 잡았을 때
		{
			RectF tempRect = new RectF();
			tempRect.set(mClipRect);
			
			boolean isCollapse = moveX > 0 && moveY > 0;
			boolean isEnlarge = moveX < 0 && moveY < 0;
			
			float move = (float) Math.sqrt(Math.pow(Math.abs(moveX), 2) + Math.pow(Math.abs(moveY), 2));
			float x = mClipRect.left;
			float y = mClipRect.top;
			
			if(isCollapse) {
				x = (mClipRect.left - (m_isCropXScale ? (float)move * m_fCropRatio : move));
				y = (mClipRect.top - (!m_isCropXScale ? (float)move * m_fCropRatio : move));
			} else if(isEnlarge) {
				x = (mClipRect.left + (m_isCropXScale ? (float)move * m_fCropRatio : move));
				y = (mClipRect.top + (!m_isCropXScale ? (float)move * m_fCropRatio : move));
			} 
			
			if(x <= mClipRect3.left || y <= mClipRect3.top) {
				mClipRect.set(tempRect);
			} else {
				mClipRect.set(x, y,
						mClipRect.right, mClipRect.bottom);
				
				float fBaseImgArea = getRectArea(mClipRect3);
				float fClipArea = getRectArea(mClipRect);
				
				if(fClipArea <= fBaseImgArea/LIMIT_MAX_CROP_RATIO)
					mClipRect.set(tempRect);
			}
			
			tempRect = null;
		}
		else
		{
			if (mClipRect.top + moveY <= mClipRect3.top) {
				mClipRect.offset(0, mClipRect3.top - mClipRect.top);
			} else if (mClipRect.bottom + moveY > mClipRect3.bottom) {
				mClipRect.offset(0, mClipRect3.bottom - mClipRect.bottom);
			} else {
				mClipRect.offset(moveX, moveY);
			}
			
			if (mClipRect.left + moveX <= mClipRect3.left) {
				mClipRect.offset(mClipRect3.left - mClipRect.left, 0);
			} else if (mClipRect.right + moveX > mClipRect3.right) {
				mClipRect.offset(mClipRect3.right - mClipRect.right, 0);
			} else {
				mClipRect.offset(moveX, moveY);
			}
		}

		//드래그 손잡이 영역
		mAdjustCropRect = new RectF((mClipRect.left - (mLineSize*4)),
				mClipRect.top - (mLineSize*4),
				mClipRect.left + (mLineSize*4),
				mClipRect.top + (mLineSize*4));
		
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.save();
		// 배경 그리기..
		canvas.drawColor(mBackgroundColor);
		// 인화영역 그리기..
		canvas.drawRect(mClipRect, mPaint);
		// 드래그 영역 그리
		canvas.drawRect(mAdjustCropRect, mPaint);
		
		canvas.restore();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		int width;
		int height;

		// Measure Width
		if (widthMode == MeasureSpec.EXACTLY) {
			// Must be this size
			width = widthSize;
		} else if (widthMode == MeasureSpec.AT_MOST) {
			// Can't be bigger than...
			width = Math.min(0, widthSize);
		} else {
			// Be whatever you want
			width = 0;
		}
		// Measure Height
		if (heightMode == MeasureSpec.EXACTLY) {
			// Must be this size
			height = heightSize;
		} else if (heightMode == MeasureSpec.AT_MOST) {
			// Can't be bigger than...
			height = Math.min(0, heightSize);
		} else {
			// Be whatever you want
			height = 0;
		}

		mScreenWidth = width;
		mScreenHeight = height;
		
		setMeasuredDimension(width, height);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		calculatorClipRect();
		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	protected void onWindowVisibilityChanged(int visibility) {
		super.onWindowVisibilityChanged(visibility);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mIsTouch) {

			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				mPrePos.x = event.getX();
				mPrePos.y = event.getY();
				
				RectF touchRect = new RectF(mPrePos.x - SIZE_ADJUST_TOUCH_RECT_HALF_SIZE,
						mPrePos.y - SIZE_ADJUST_TOUCH_RECT_HALF_SIZE,
						mPrePos.x + SIZE_ADJUST_TOUCH_RECT_HALF_SIZE,
						mPrePos.y + SIZE_ADJUST_TOUCH_RECT_HALF_SIZE);
				
				mIsAdjustCropSize = false;
				
				if(mAdjustCropRect.intersect(touchRect))
					mIsAdjustCropSize = true;
				else if (!mClipRect.contains(event.getX(), event.getY()))
					return super.onTouchEvent(event);
				
				break;
			case MotionEvent.ACTION_MOVE:
				
				int x = (int) event.getX();
				int y = (int) event.getY();
				
				if(!mClipRect3.contains(x, y))
					return super.onTouchEvent(event);
				
				float moveX = 0;
				float moveY = 0;
				
				moveX = mPrePos.x - x;
				mPrePos.x = event.getX();
				moveY = mPrePos.y - y;
				mPrePos.y = event.getY();
				
				if(mIsAdjustCropSize)
				{
					offsetClipRect(x, y, moveX, moveY);
					return super.onTouchEvent(event);
				}
				
				offsetClipRect(x, y, -moveX, -moveY);
				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_OUTSIDE:
				fixInvalidRectPosition();
				break;

			default:
				break;
			}

			return true;

		} else
			return super.onTouchEvent(event);
	}
	
	private void fixInvalidRectPosition() {

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				if(mContext instanceof Activity){
					((Activity)mContext).runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if(mClipRect == null || mClipRect3 == null) return;
							
							if (mClipRect.top < mClipRect3.top) {
								mClipRect.offset(0, mClipRect3.top - mClipRect.top);
							} else if (mClipRect.bottom > mClipRect3.bottom) {
								mClipRect.offset(0, mClipRect3.bottom - mClipRect.bottom);
							} else if (mClipRect.left < mClipRect3.left) {
								mClipRect.offset(mClipRect3.left - mClipRect.left, 0);
							} else if (mClipRect.right > mClipRect3.right) {
								mClipRect.offset(mClipRect3.right - mClipRect.right, 0);
							} 
							
							//드래그 손잡이 영역
							mAdjustCropRect = new RectF((mClipRect.left - (mLineSize*4)),
									mClipRect.top - (mLineSize*4),
									mClipRect.left + (mLineSize*4),
									mClipRect.top + (mLineSize*4));
							
							invalidate();							
						}
					});
				}
			}
		}, 10);
	}

	/***
	 * 크롭정보를 반환하는 함수..
	 * 
	 * @return
	 */
	public CropInfo getCropInfo() {
		// 스크린의 center와 크롭영역의 center값을 구해 얼마나 영역이 이동이 이루어 졌는지 구한다.
		// 스크린의 center를 구한다.
		PointF screenCenter = new PointF(mScreenWidth / 2.f, mScreenHeight / 2.f);
		// 크롭영역의 center를 구한다.
		PointF cropRectCenter = new PointF((mClipRect.left + mClipRect.width() / 2.0f), (mClipRect.top + mClipRect.height() / 2.0f));

		float movePercent = 0;
		float moveY = 0.0f;
		float moveX = 0.0f;

		CORP_ORIENT tempOrient = orientValue;

		if (tempOrient == CORP_ORIENT.HEIGHT) {// 세로이동
			// 이동한 크기를 이동거리/이미지크기 비율로 저장을 한다.
			moveY = screenCenter.y - cropRectCenter.y;
		} else if (tempOrient == CORP_ORIENT.WIDTH) {// 가로이동
			moveX = screenCenter.x - cropRectCenter.x;
		}

		int angle = mImgData.ROTATE_ANGLE / 90;

		if (orientValue == CORP_ORIENT.WIDTH) {
			if (angle == 0) {
				movePercent = (float) moveX / mScreenImgWidth;
			} else if (angle == 1) {
				movePercent = -(float) moveX / mScreenImgWidth;
			} else if (angle == 2) {
				movePercent = -(float) moveX / mScreenImgWidth;
			} else if (angle == 3) {
				movePercent = (float) moveX / mScreenImgWidth;
			}

		} else if (orientValue == CORP_ORIENT.HEIGHT) {
			if (angle == 1) {
				movePercent = (float) moveY / mScreenImgHeight;
			} else if (angle == 0) {
				movePercent = (float) moveY / mScreenImgHeight;
			} else if (angle == 3)
				movePercent = -(float) moveY / mScreenImgHeight;
			else if (angle == 2)
				movePercent = -(float) moveY / mScreenImgHeight;
		}

		return getCropInfo(orientValue, angle, movePercent);
	}
	
	CropInfo getCropInfo(CORP_ORIENT orientation, int angle, float movePercent) {

		int startPercent = 0;
		int endPercent = 0;
		CORP_ORIENT tempOrient = orientation;

		if (tempOrient == CORP_ORIENT.HEIGHT) {
			float baseY = (mClipRect3.height() - mClipRect.height()) / 2.0f;
			float start = baseY - movePercent * mScreenImgHeight;
			float end = start + mClipRect.height();
			startPercent = (int) (((float) start / mScreenImgHeight) * 100.f);
			endPercent = (int) (((float) end / mScreenImgHeight) * 100.f);
		} else if (tempOrient == CORP_ORIENT.WIDTH) {
			float baseX = (mClipRect3.width() - mClipRect.width()) / 2.0f;
			float start = baseX - movePercent * mScreenImgWidth;
			float end = start + mClipRect.width();
			startPercent = (int) (((float) start / mScreenImgWidth) * 100.f);
			endPercent = (int) (((float) end / mScreenImgWidth) * 100.f);
		}

		if (angle == 1 || angle == 3) {
			tempOrient = changeOrientation(tempOrient);
		}

		return new CropInfo(tempOrient, movePercent, startPercent, endPercent);
	}
	
	public AdjustableCropInfo getAdjustCropInfo()
	{
		if(mClipRect == null) return null;
		
		float fRat = orientValue == CORP_ORIENT.WIDTH ? m_fRatioX : m_fRatioY;
		
		float x = Math.max(0, (int)(mClipRect.left - mClipRect3.left));
		float y = Math.max(0, (int)(mClipRect.top - mClipRect3.top));
		float w = mClipRect.width();
		float h = mClipRect.height();

		return null;   //TODO
	}

	CORP_ORIENT changeOrientation(CORP_ORIENT orientation) {
		if (CORP_ORIENT.WIDTH == orientation)
			return CORP_ORIENT.HEIGHT;
		else if (CORP_ORIENT.HEIGHT == orientation)
			return CORP_ORIENT.WIDTH;
		else
			return orientation;
	}
}
