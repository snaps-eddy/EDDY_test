package com.snaps.mobile.component;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

import com.snaps.common.utils.imageloader.recoders.AdjustableCropInfo.CropImageRect;
import com.snaps.common.utils.imageloader.recoders.CropInfo.CORP_ORIENT;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.diary.SnapsDiaryDataManager;
import com.snaps.mobile.component.image_edit_componet.OrientedBoundingBox;

public class SnapsMultiTouchImageView extends ImageView implements
		OnTouchListener {

	private final float MIN_SCALE_RATIO = .5f;
	private final float MAX_SCALE_RATIO = 2f;
	private Context mCon = null;
	private boolean isEdited = false;
	private boolean m_isScaleable = false;
	private long m_lPrevToastTime = 0l;
	private boolean m_isOnTouch = false;
	private boolean m_isEditable = false;
	
	public SnapsMultiTouchImageView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		 mCon = context;
		init();
	}

	public SnapsMultiTouchImageView(Context context) {
		this(context, null);
		 mCon = context;
		init();
	}

	public SnapsMultiTouchImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		 mCon = context;
		init();
	}

	public interface MatrixListener {
		void notifyMatrix(Path path, Matrix matrix);
	}

	//편집 정보가 잘못 되었을 때, 초기화하고 다시 로딩 시키기 위해
	public interface EditorInitializeListener {
		void initEditedContents();
	}

	public MatrixListener matrixListener = null;

	public boolean isEditable() {
		return m_isEditable;
	}

	public void setEditable(boolean m_isEditable) {
		this.m_isEditable = m_isEditable;
	}

	public boolean isValidArea() {
		return validRange();
	}

	public void setMatrixListener(MatrixListener lis) {
		matrixListener = lis;
	}
	
	public void setScaleable(boolean able) {
		m_isScaleable = able;
	}
	
	public boolean isScaleable() {
		return m_isScaleable;
	}
	
	public boolean isOnTouch() {
		return m_isOnTouch;
	}

	public void setOnTouch(boolean m_isOnTouch) {
		this.m_isOnTouch = m_isOnTouch;
	}

	protected Matrix matrix = new Matrix();
	protected Matrix savedMatrix = new Matrix();
	protected Matrix tempMatrix = new Matrix();
	protected Matrix scaledMatrix = new Matrix();
	protected Matrix originMatrix = new Matrix(); //최초 초기값
	protected Matrix mTouchDownMatrix = new Matrix(); //화면을 클릭하는 순간의 값
	protected Matrix mLastAllowMatrix = new Matrix(); //화면을 클릭하는 순간의 값

	public CORP_ORIENT orientValue = CORP_ORIENT.NONE;
	
	public static final int NONE = 0;
	public static final int DRAG = 1;
	public static final int ZOOM = 2;
	public int mode = NONE;
	
	private PointF start = new PointF();

	public RectF clipRectRange = null;

	public Path mBoundsPath = new Path();
	public Path mTouchDownPath = new Path();
	public Path mLastAllowPath = new Path();
	
	private Region mRangeCheckRegion;

	private MatrixBounds originalBounds = null;

	protected ImgRectAttribute mCurImgRect = new ImgRectAttribute(); //현재 이미지를 컨트롤 하기 위함.
	protected ImgRectAttribute mTotalImgRect = new ImgRectAttribute(); //모든 이동 궤적이나 스케일이 합산된 상태
	protected ImgRectAttribute mTouchdownImgRect = new ImgRectAttribute(); //터치 다운한 상태를 저장.
	protected ImgRectAttribute mLastAllowImgRect = new ImgRectAttribute(); //터치 다운한 상태를 저장.
	
	protected float m_fTempWidth, m_fTempHeight;
	protected float m_fResWidth, m_fResHeight;
	
	private float oldDragDist = 1f;
	private float curDragDist = 0f;
	private float newRot = 0f;
	private float[] lastEvent = null;

	protected int mLineSize = 3; // 인화영역 두께..

	private boolean m_isAction;
	private boolean m_isChangedMatrixValue = false;
	private boolean m_isActionPointerUp = false;
	protected boolean m_isScaledOrRotated = false; // 처음에 사용자가 기본 위치만 옮기고 싶어 할 수
													// 있기 때문에, 회전이나 확대가 적용 된 이후
													// 부터 자유 이동이 가능케 한다.
	private float m_fPrevRotateAngle = 0.f;
	
	private long m_lInitLockTime = 0l;
	
	public void init() {
		setOnTouchListener(this);
		
		setEdited(false);
	}

	public boolean isEdited() {
		return isEdited;
	}

	public void setEdited(boolean isEdited) {
		this.isEdited = isEdited;
	}

	public void setClipRectRange(RectF rect) {
		clipRectRange = new RectF(rect);
	}

	private void saveTouchDownInfo() {
		mTouchDownMatrix.set(matrix);
		mTouchdownImgRect.setMovedX(mTotalImgRect.getMovedX());
		mTouchdownImgRect.setMovedY(mTotalImgRect.getMovedY());
		mTouchdownImgRect.setWidth(mCurImgRect.getWidth());
		mTouchdownImgRect.setHeight(mCurImgRect.getHeight());

		mTouchDownPath.set(mBoundsPath);
		
		mLastAllowMatrix.set(matrix);
		mLastAllowImgRect.setMovedX(mTotalImgRect.getMovedX());
		mLastAllowImgRect.setMovedY(mTotalImgRect.getMovedY());
		mLastAllowImgRect.setWidth(mCurImgRect.getWidth());
		mLastAllowImgRect.setHeight(mCurImgRect.getHeight());
		
		mLastAllowPath.set(mBoundsPath);
	}
	
	private Matrix getClipLineMatchedMatrix(int moveX, int moveY) {
		Matrix fixedMatrix = new Matrix(matrix);
		RectF rect = getMatrixRect();
		if (!m_isScaledOrRotated) {
			if (orientValue == CORP_ORIENT.HEIGHT) {
				if(moveY > 0 && rect.top + moveY >= clipRectRange.top)
					moveY = (int) (clipRectRange.top - rect.top) - 4; //4를 빼주는 이유는 가끔 선 밖으로 나가게 되는 오류가 확인 되기 때문에..
				else if(moveY < 0 && rect.bottom + moveY <= clipRectRange.bottom)
					moveY = (int) (clipRectRange.bottom - rect.bottom) + 4;
			} else {
				if(moveX > 0 && rect.left + moveX >= clipRectRange.left)
					moveX = (int) (clipRectRange.left - rect.left) - 4;
				else if(moveX < 0 && rect.right + moveX <= clipRectRange.right)
					moveX = (int) (clipRectRange.right - rect.right) + 4;
			}
			
			fixedMatrix.postTranslate(moveX, moveY);
		}
		return fixedMatrix;
	}
	
	protected void checkResolution(float scale, float angle) {}
	
	protected boolean isShowingProgress() { return false; }

	public boolean onTouch(View v, MotionEvent event) {

		if(System.currentTimeMillis() - m_lInitLockTime < 200 || isShowingProgress() || !isEditable()) return false;
		
		m_isAction = false;

		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			
			setOnTouch(true);
			
			saveTouchDownInfo();

			mCurImgRect.setMovedX(0.f);
			mCurImgRect.setMovedY(0.f);
			savedMatrix.set(matrix);
			start.set(event.getX(), event.getY());
			mode = DRAG;
			lastEvent = null;
			m_isChangedMatrixValue = false;
			m_isActionPointerUp = false;
			mCurImgRect.setScale(1.f);
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			if(event.getPointerCount() <= 2) {
				oldDragDist = spacing(event);
				if (oldDragDist > 10f) {
					savedMatrix.set(matrix);
					mode = ZOOM;
				}
				lastEvent = new float[4];
				lastEvent[0] = event.getX(0);
				lastEvent[1] = event.getX(1);
				lastEvent[2] = event.getY(0);
				lastEvent[3] = event.getY(1);
				curDragDist = rotation(event);
			}
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			setOnTouch(false);
			mode = NONE;
			lastEvent = null;
			if (m_isChangedMatrixValue) {
				
				if (!validRange()) {
					m_lInitLockTime = System.currentTimeMillis();

					if(!SnapsDiaryDataManager.isAliveSnapsDiaryService())
						showToastMsg(R.string.invalid_image_range_msg);
					
					initPos();
					return false;
				}
				
				mTotalImgRect.addMovedX(mCurImgRect.getMovedX());
				mTotalImgRect.addMovedY(mCurImgRect.getMovedY());

				mTotalImgRect.setWidth(mCurImgRect.getWidth());
				mTotalImgRect.setHeight(mCurImgRect.getHeight());
			}
			m_isActionPointerUp = false;

			checkResolution(getScale(), getAngle());
			
			return true;
		case MotionEvent.ACTION_POINTER_UP:
			mode = NONE;
			lastEvent = null;
			
			m_fTempWidth = mCurImgRect.getWidth();
			m_fTempHeight = mCurImgRect.getHeight();
			
			m_isActionPointerUp = true;
			break;
		case MotionEvent.ACTION_MOVE:
			if (mode == DRAG) {
				int moveX = (int) (event.getX() - start.x);
				int moveY = (int) (event.getY() - start.y);
			
				if (!m_isScaledOrRotated) {
					if (orientValue == CORP_ORIENT.HEIGHT) {
						moveX = 0;
					} else {
						moveY = 0;
					}
				}
				
				mCurImgRect.setMovedX(moveX);
				mCurImgRect.setMovedY(moveY);
				tempMatrix.set(getClipLineMatchedMatrix(moveX, moveY));
				
				matrix.set(savedMatrix);
				matrix.postTranslate(mCurImgRect.getMovedX(), mCurImgRect.getMovedY());
				m_isAction = true;
				
				checkImageRectLimitLine();

			} else if (mode == ZOOM) {
				if (m_isActionPointerUp) break;

				if (lastEvent != null && event.getPointerCount() == 2) {
					float newDist = spacing(event);
	
					boolean isScaleable = false;
					// Scale
					if (newDist > 10f) {
						float newScaleValue = newDist / oldDragDist;
						
						isScaleable = isValidScale(newScaleValue);
						PointF center = getCurCenter();
						
						if(isScaleable){
							m_isAction = true;
							
							matrix.set(savedMatrix);
							mCurImgRect.setScale(newScaleValue);
							matrix.postScale(mCurImgRect.getScale(), mCurImgRect.getScale(), center.x,
									center.y);
						} 
						else
						{
							showToastMsg(R.string.cannot_zooming_more);
							
							scaledMatrix.set(savedMatrix);
							scaledMatrix.postScale(mCurImgRect.getScale(), mCurImgRect.getScale(), center.x,
									center.y);
						}
					}

					newRot = rotation(event);
					float r = newRot - curDragDist;
					
					if(!isScaleable) {
						if(Math.abs(m_fPrevRotateAngle - r) < .1)
							break;
						else
							matrix.set(scaledMatrix);
					}
					
					m_isAction = true;
					PointF center = getCurCenter();
					matrix.postRotate(r, center.x, center.y);
					m_fPrevRotateAngle = r;
				}
			}
			break;
		}

		if (matrixListener != null) {
			
			if (lastEvent != null && event.getPointerCount() > 2)
				m_isAction = false;
				
			if (m_isAction) {
				if (m_isActionPointerUp) {
					mCurImgRect.setWidth(m_fTempWidth * mCurImgRect.getScale());
					mCurImgRect.setHeight(m_fTempHeight * mCurImgRect.getScale());
				} else {
					mCurImgRect.setWidth(mTouchdownImgRect.getWidth() * mCurImgRect.getScale());
					mCurImgRect.setHeight(mTouchdownImgRect.getHeight() * mCurImgRect.getScale());
				}

				PointF center = getCurCenter();
				OrientedBoundingBox bow = new OrientedBoundingBox(-getAngle(),
						center.x, center.y, mCurImgRect.getWidth(),
						mCurImgRect.getHeight());
				mBoundsPath = bow.toPath();

				saveAllowMatrix();
				
				matrixListener.notifyMatrix(mBoundsPath, matrix);
				
				m_isChangedMatrixValue = true;
				
				setEdited(true);
			}
		}

		return true;
	}
	
	protected void saveAllowMatrix() {
		if(validRange()) {
			mLastAllowImgRect.setMovedX(mTotalImgRect.getMovedX() + mCurImgRect.getMovedX());
			mLastAllowImgRect.setMovedY(mTotalImgRect.getMovedY() + mCurImgRect.getMovedY());
			mLastAllowImgRect.setWidth(mCurImgRect.getWidth());
			mLastAllowImgRect.setHeight(mCurImgRect.getHeight());
			
			mLastAllowMatrix.set(matrix);
			
			mLastAllowPath.set(mBoundsPath);
		}
	}
	
	//스케일링이나, 로테이트가 되지 않은 상태에서 이동을 해 클립영역을 벗어나지 못하게 하는 함수
	private void checkImageRectLimitLine() {
		if(!m_isScaledOrRotated) {
			RectF rect = getMatrixRect();
			if (orientValue == CORP_ORIENT.HEIGHT) {
				if(rect.top > clipRectRange.top){
					matrix.set(tempMatrix);
					mCurImgRect.setMovedY(getYValueFromMatrix(tempMatrix) - getYValueFromMatrix(savedMatrix));
				}
				else if(rect.bottom < clipRectRange.bottom){
					matrix.set(tempMatrix);
					mCurImgRect.setMovedY(getYValueFromMatrix(tempMatrix) - getYValueFromMatrix(savedMatrix));
				}
			}
			else {
				if(rect.left > clipRectRange.left){
					matrix.set(tempMatrix);
					mCurImgRect.setMovedX(getXValueFromMatrix(tempMatrix) - getXValueFromMatrix(savedMatrix));
				}
				else if(rect.right < clipRectRange.right){
					matrix.set(tempMatrix);
					mCurImgRect.setMovedX(getXValueFromMatrix(tempMatrix) - getXValueFromMatrix(savedMatrix));
				}
			}
		}
	}

	public PointF getCurCenter() {
		return new PointF((mCurImgRect.getCenterX() + mTotalImgRect.getMovedX()) + mCurImgRect.getMovedX(),
				(mCurImgRect.getCenterY() + mTotalImgRect.getMovedY()) + mCurImgRect.getMovedY());
	}
	
	public void initImgRectValues() {
		if(mCurImgRect != null)
			mCurImgRect.clear();
		if(mTotalImgRect != null)
			mTotalImgRect.clear();
		if(mTouchdownImgRect != null)
			mTouchdownImgRect.clear();
		
		m_isScaledOrRotated = false;
	}

	public boolean validRange() {
		// 이미지가 무조건 클립 영역보다는 크거나 같아야 한다.
		if (clipRectRange == null)
			return false;

		if (mBoundsPath == null)
			return true;

		if(mRangeCheckRegion == null)
			mRangeCheckRegion = new Region();

		int w = this.getMeasuredWidth();
		int h = this.getMeasuredHeight();

		mRangeCheckRegion.setPath(mBoundsPath, new Region(0, 0, w + 100, h + 100));

		boolean valid = mRangeCheckRegion.contains((int) clipRectRange.left,
				(int) clipRectRange.top)
				&& mRangeCheckRegion.contains((int) clipRectRange.right,
						(int) clipRectRange.top)
				&& mRangeCheckRegion.contains((int) clipRectRange.left,
						(int) clipRectRange.bottom)
				&& mRangeCheckRegion.contains((int) clipRectRange.right,
						(int) clipRectRange.bottom);

		//FIXME 클립 영역하고 정확하게 일치 하지가 않는다..

		if(valid && getScale() != 1.f)
			m_isScaledOrRotated = true;

		return m_isScaledOrRotated ? valid : (getScale() == 1.f && getAngle() % 90 == 0);
	}
	
	private void showToastMsg(int resId) {
		if(mCon == null || System.currentTimeMillis() - m_lPrevToastTime < 2000) return;
		
		m_lPrevToastTime = System.currentTimeMillis();
		MessageUtil.toast(mCon, resId, Gravity.CENTER);
	}
	
	private boolean isValidScale(float newScaleValue) {
		
		float calculatedScale = (getScale() - 1) + newScaleValue;
		return calculatedScale > MIN_SCALE_RATIO && calculatedScale < MAX_SCALE_RATIO;
	}
	
	public float getMoveX() {
		return getMeasuredMatrixValue(2);
	}

	public float getMoveY() {
		return getMeasuredMatrixValue(5);
	}

	public float getScaleX() {
		return getMeasuredMatrixValue(0);
	}

	public float getScaleY() {
		return getMeasuredMatrixValue(4);
	}

	public float getScale() {
		if (originalBounds == null || mCurImgRect == null) return 1.f;

		float originWidth = originalBounds.getWidth();
		float curWidth = mCurImgRect.getWidth();
		return curWidth / originWidth;
	}

	public float getAngle() {
		float[] values = new float[9];
		matrix.getValues(values);
		return Math.round(Math.atan2(values[Matrix.MSKEW_X],
				values[Matrix.MSCALE_X]) * (180 / Math.PI));
	}

	public void initPos() {
		if(!adjustPosByLastAllowMatrix()) {
			mTotalImgRect.setMovedX(mTouchdownImgRect.getMovedX());
			mTotalImgRect.setMovedY(mTouchdownImgRect.getMovedY());
			mCurImgRect.setWidth(mTouchdownImgRect.getWidth());
			mCurImgRect.setHeight(mTouchdownImgRect.getHeight());
			
			matrix.set(mTouchDownMatrix);
			mBoundsPath.set(mTouchDownPath);
			
			if (matrixListener != null)
				matrixListener.notifyMatrix(mTouchDownPath, mTouchDownMatrix);
		}
		
		if (matrixListener != null)
			matrixListener.notifyMatrix(mLastAllowPath, mLastAllowMatrix);
	}
	
	private boolean adjustPosByLastAllowMatrix() {
		
		mTotalImgRect.setMovedX(mLastAllowImgRect.getMovedX());
		mTotalImgRect.setMovedY(mLastAllowImgRect.getMovedY());
		mCurImgRect.setWidth(mLastAllowImgRect.getWidth());
		mCurImgRect.setHeight(mLastAllowImgRect.getHeight());
		
		matrix.set(mLastAllowMatrix);
		mBoundsPath.set(mLastAllowPath);
		
		return validRange();
	}

	public void fitToScreen(Bitmap bm) {
		if(bm == null || bm.isRecycled()) return;
		int w = this.getMeasuredWidth();
		int h = this.getMeasuredHeight();

		RectF drawableRect = new RectF(0, 0, bm.getWidth(), bm.getHeight());
		RectF viewRect = new RectF(0, 0, w, h);
		matrix.setRectToRect(drawableRect, viewRect, Matrix.ScaleToFit.CENTER);
		setImageMatrix(matrix);

		float[] values = new float[9];
		matrix.getValues(values);
		originMatrix.set(matrix);

		m_fResWidth = getWidthFromMatrix(originMatrix, this) / values[0];
		m_fResHeight = getHeightFromMatrix(originMatrix, this) / values[4];

		RectF rect = getMatrixRect();
		originalBounds = new MatrixBounds(rect.left, rect.top, rect.right,
				rect.top, rect.right, rect.bottom, rect.left, rect.bottom);

		mBoundsPath = new Path();
		mBoundsPath.moveTo(originalBounds.LT.x, originalBounds.LT.y);
		mBoundsPath.lineTo(originalBounds.RT.x, originalBounds.RT.y);
		mBoundsPath.lineTo(originalBounds.RB.x, originalBounds.RB.y);
		mBoundsPath.lineTo(originalBounds.LB.x, originalBounds.LB.y);
		
		//Path 초기화.
		initImgRectValues();

		mCurImgRect.setCenterX(rect.left + (rect.width() / 2));
		mCurImgRect.setCenterY(rect.top + (rect.height() / 2));

		mCurImgRect.setWidth(rect.width());
		mCurImgRect.setHeight(rect.height());

		mTotalImgRect.setWidth(mCurImgRect.getWidth());	
		mTotalImgRect.setHeight(mCurImgRect.getHeight());

		if (matrixListener != null)
			matrixListener.notifyMatrix(mBoundsPath, matrix);
		
		setVisibility(View.VISIBLE);
	}

	public RectF getMatrixRect() {
		float l = getXValueFromMatrix(matrix);
		float t = getYValueFromMatrix(matrix);

		float r = l + getWidthFromMatrix(matrix, this);
		float b = t + getHeightFromMatrix(matrix, this);

		return new RectF(l, t, r, b);
	}

	public float getMeasuredMatrixValue(int type) {

		float[] values = new float[9];
		matrix.getValues(values);

		float[] initValues = new float[9];
		originMatrix.getValues(initValues);
		
		float value = values[type] - initValues[type];

		return type == 0 || type == 4 ? value + 1 : value;
	}

	/**
	 * Determine the space between the first two fingers
	 */
	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return  (float)Math.sqrt(x * x + y * y);
	}

	/**
	 * Calculate the degree to be rotated by.
	 * 
	 * @param event
	 * @return Degrees
	 */
	private float rotation(MotionEvent event) {
		double delta_x = (event.getX(0) - event.getX(1));
		double delta_y = (event.getY(0) - event.getY(1));
		double radians = Math.atan2(delta_y, delta_x);
		return (float) Math.toDegrees(radians);
	}

	public float getXValueFromMatrix(Matrix matrix) {

		float[] values = new float[9];
		matrix.getValues(values);
		float globalX = values[2];

		return globalX;
	}

	public float getYValueFromMatrix(Matrix matrix) {

		float[] values = new float[9];
		matrix.getValues(values);
		float globalY = values[5];

		return globalY;
	}

	public float getWidthFromMatrix(Matrix matrix, ImageView imageview) {

		float[] values = new float[9];
		matrix.getValues(values);

		Drawable d = getDrawable();
		int imageWidth = d.getIntrinsicWidth();
		float scaleWidth = imageWidth * values[0];
		return scaleWidth;
	}

	public float getHeightFromMatrix(Matrix matrix, ImageView imageview) {

		float[] values = new float[9];
		matrix.getValues(values);

		Drawable d = getDrawable();
		int imageHeight = d.getIntrinsicHeight();
		float scaleHeight = imageHeight * values[4];

		return scaleHeight;
	}

	public static class MatrixBounds {
		public PointF LT = new PointF();
		public PointF RT = new PointF();
		public PointF RB = new PointF();
		public PointF LB = new PointF();

		public MatrixBounds() {
		}

		public MatrixBounds(MatrixBounds bounds) {
			set(bounds.LT.x, bounds.LT.y, bounds.RT.x, bounds.RT.y,
					bounds.RB.x, bounds.RB.y, bounds.LB.x, bounds.LB.y);
		}

		public MatrixBounds(float ltX, float ltY, float rtX, float rtY,
				float rbX, float rbY, float lbX, float lbY) {
			set(ltX, ltY, rtX, rtY, rbX, rbY, lbX, lbY);
		}

		public void set(float ltX, float ltY, float rtX, float rtY, float rbX,
				float rbY, float lbX, float lbY) {
			LT.x = ltX;
			LT.y = ltY;
			RT.x = rtX;
			RT.y = rtY;
			RB.x = rbX;
			RB.y = rbY;
			LB.x = lbX;
			LB.y = lbY;
		}

		public float[] getCenter() {
			return new float[] { ((getRight() - getLeft()) / 2),
					((getBottom() - getTop()) / 2) };
		}

		public float getWidth() {
			return getRight() - getLeft();
		}

		public float getHeight() {
			return getBottom() - getTop();
		}

		public float getLeft() {
			return LB.x;
		}

		public float getTop() {
			return LT.y;
		}

		public float getRight() {
			return RT.x;
		}

		public float getBottom() {
			return RB.y;
		}
	}
	
	protected class ImgRectAttribute
	{
		private float movedX, movedY, width, height, centerX, centerY, scale;

		public void clear() {
			movedX = 0.f;
			movedY = 0.f;
			width = 0.f;
			height = 0.f;
			centerX = 0.f;
			centerY = 0.f;
			scale = 0.f;
		}
		
		public void set(CropImageRect attr) {
			movedX = attr.movedX;
			movedY = attr.movedY;
			width = attr.width;
			height = attr.height;
			centerX = attr.centerX;
			centerY = attr.centerY;
			scale = attr.scaleX;
		}
		
		public void addMovedX(float movedX) {
			this.movedX += movedX;
		}
		
		public float getMovedX() {
			return movedX;
		}

		public void setMovedX(float movedX) {
			this.movedX = movedX;
		}

		public float getMovedY() {
			return movedY;
		}
		
		public void addMovedY(float movedY) {
			this.movedY += movedY;
		}

		public void setMovedY(float movedY) {
			this.movedY = movedY;
		}

		public float getWidth() {
			return width;
		}

		public void setWidth(float width) {
			this.width = width;
		}

		public float getHeight() {
			return height;
		}

		public void setHeight(float height) {
			this.height = height;
		}

		public float getCenterX() {
			return centerX;
		}

		public void setCenterX(float centerX) {
			this.centerX = centerX;
		}

		public float getCenterY() {
			return centerY;
		}

		public void setCenterY(float centerY) {
			this.centerY = centerY;
		}

		public float getScale() {
			return scale;
		}

		public void setScale(float scale) {
			this.scale = scale;
		}
	}
}
