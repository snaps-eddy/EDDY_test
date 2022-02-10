package com.snaps.mobile.component;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Region;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.structure.photoprint.SnapsPhotoPrintItem;
import com.snaps.common.utils.imageloader.recoders.CropInfo;
import com.snaps.common.utils.imageloader.recoders.CropInfo.CORP_ORIENT;
import com.snaps.common.utils.log.Dlog;

import errorhandle.logger.Logg;

public class SnapsAreaView extends ImageView {
    private static final String TAG = SnapsAreaView.class.getSimpleName();
    // 인화영역 크기...
    RectF mClipRect = new RectF(); // 영역그리는 크기
    RectF mClipRect2 = new RectF(); // 영역 투명하게 그리는 크기...
    RectF mClipRect3 = new RectF(); // 불투명하게 그리는 크기...

    /************************************
     *
     ************************************/

    // 배경색상 설정..
    int mBackgroundColor;
    // 인화영역 색상..
    int mClipRectColor;
    int mLineSize = 3; // 인화영역 두께..

    Paint mPaint; // 인화영역 그리는 Paint
    boolean mIsTouch = true;
    PointF mStartPosition = new PointF();

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

    public void setmIsViewRotate(boolean mIsViewRotate) {
        this.mIsViewRotate = mIsViewRotate;
    }

    public SnapsAreaView(Context context) {
        super(context);
        init();
    }

    public SnapsAreaView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SnapsAreaView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
        init();
    }

    void init() {

        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        mBackgroundColor = Color.parseColor("#66000000");
        mClipRectColor = Color.parseColor("#FFEF4123");

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(mClipRectColor);
        mPaint.setStyle(Paint.Style.FILL);

        // px => dip
        float scale = getResources().getDisplayMetrics().density;
        mLineSize = (int) (mLineSize * scale + 0.5f);

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

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);

        // 이미지가 설정이 되면..

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

        float ratioX = (float) mScreenWidth / (float) originWidth;
        float ratioY = (float) mScreenHeight / (float) originHeight;

        // 스크린에 뿌려지는 이미지 크기를 구한다
        if (ratioX >= ratioY) {
            // _cropOrient = LEFT_RIGHT;
            mScreenImgHeight = mScreenHeight;
            mScreenImgWidth = originWidth * ratioY;
        } else {
            // _cropOrient = UP_DOWN;
            mScreenImgWidth = mScreenWidth;
            mScreenImgHeight = originHeight * ratioX;
        }

        if (originWidth > originHeight) {
            mRatio = mViewPortWRation;
        } else if (originWidth < originHeight) {
            mRatio = mViewPortHRation;
        } else if (originWidth == originHeight) {

            if (isRotate) {
                mRatio = mViewPortWRation;
            } else {
                mRatio = mViewPortHRation;
            }

        }

        // viewPort 이동방향을 설정한다.
        boolean isStandard = (mScreenImgWidth / mScreenImgHeight) > mRatio ? true : false;
        orientValue = isStandard ? CORP_ORIENT.WIDTH : CORP_ORIENT.HEIGHT;

        // viewport 크기 설정...
        final float imgCropWidth = isStandard ? (mScreenImgHeight * mRatio) : mScreenImgWidth;
        final float imgCropHeight = isStandard ? mScreenImgHeight : (mScreenImgWidth / mRatio);

        // 크롭영역 limit 설정
        if (orientValue == CORP_ORIENT.HEIGHT) {
            mOriginY = (int) (mScreenImgHeight - imgCropHeight) / 2;
            mMaxMove = (int) ((mScreenImgHeight - imgCropHeight) / 2);
            mMinY = mOriginY - mMaxMove;
            mMaxY = mOriginY + mMaxMove;
        } else {
            mOriginX = (int) (mScreenImgWidth - imgCropWidth) / 2;
            mMaxMove = (int) ((mScreenImgWidth - imgCropWidth) / 2);
        }

        // 크롭영역 위치를 조정한다.
        adjustCropRect(imgCropWidth, imgCropHeight);

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
        // 크롭영역을 가운데에 맞춘다.
        int x = (int) ((mScreenImgWidth - cWidth) / 2);
        int y = (int) Math.floor(((mScreenImgHeight - cHeight) / 2));

        // 기존 크롭 정보를 로드한다.
        // 정보가 있으면 적용을 한다.
        CropInfo cInfo = mImgData.CROP_INFO;

        if (cInfo != null && CropInfo.CORP_ORIENT.NONE != cInfo.cropOrient) {

            CORP_ORIENT tempOrient = cInfo.cropOrient;
            float tempFloat = 1.0f;

            int angle = mImgData.ROTATE_ANGLE / 90;

            if (angle == 1 || angle == 3) {
                if (!mIsViewRotate) {
                    tempOrient = changeOrientation(tempOrient);
                }
            }

            if (tempOrient == CORP_ORIENT.WIDTH) {
                if (angle == 0) {
                    ;
                } else if (angle == 1) {
                    if (!mIsViewRotate) {
                        tempFloat = -tempFloat;
                    }
                } else if (angle == 2) {
                    if (!mIsViewRotate) {
                        tempFloat = -tempFloat;
                    }
                } else if (angle == 3) {
                    // if (!mIsViewRotate)
                    // tempFloat = -tempFloat;
                }

            } else if (tempOrient == CORP_ORIENT.HEIGHT) {
                if (angle == 0) {
                    ;
                } else if (angle == 1) {
                    ;
                } else if (angle == 2) {
                    if (!mIsViewRotate) {
                        tempFloat = -tempFloat;
                    }
                } else if (angle == 3) {
                    if (!mIsViewRotate) {
                        tempFloat = -tempFloat;
                    }
                }
            }

            // 각도 계산을 해야한다.
            int cropSize = (int) (tempOrient == CORP_ORIENT.WIDTH ? mScreenImgWidth : mScreenImgHeight);
            float offsetValue = ((float) cropSize * ((float) cInfo.movePercent));

            if (tempOrient == CORP_ORIENT.WIDTH) {
                x -= (offsetValue * tempFloat);
            } else {
                y -= (offsetValue * tempFloat);
            }
        }

        // 좌표를 가지고 세로 그린다.
        setClipRect(x, y, cWidth, cHeight);
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
        // 인화영역 그리는 영역 (빨간 사각형..)
        mClipRect2 = new RectF(mClipRect);
        mClipRect2.inset(mLineSize, mLineSize);
        invalidate();
    }

    void offsetClipRect(float dx, float dy) {

        // 0보다작으면 안됨...
        // 뷰보다 크면 안됨..
        if (orientValue == CORP_ORIENT.HEIGHT) {

            if (mClipRect.top + dy <= mClipRect3.top) {
                mClipRect.offset(0, mClipRect3.top - mClipRect.top);
            } else if (mClipRect.bottom + dy > mClipRect3.bottom) {
                mClipRect.offset(0, mClipRect3.bottom - mClipRect.bottom);
            } else {
                mClipRect.offset(dx, dy);
            }
        } else {
            if (mClipRect.left + dx <= mClipRect3.left) {
                mClipRect.offset(mClipRect3.left - mClipRect.left, 0);
            } else if (mClipRect.right + dx > mClipRect3.right) {
                mClipRect.offset(mClipRect3.right - mClipRect.right, 0);
            } else {
                mClipRect.offset(dx, dy);
            }
        }

        mClipRect2 = new RectF(mClipRect);
        mClipRect2.inset(mLineSize, mLineSize);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        // 전체 배경영역 설정
        canvas.clipRect(mClipRect3);

        // 인화영역 설정...
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            canvas.clipOutRect(mClipRect2);
        }
        else {
            canvas.clipRect(mClipRect2, Region.Op.DIFFERENCE);
        }

        // 배경 그리기..
        canvas.drawColor(mBackgroundColor);
        // 인화영역 그리기..
        canvas.drawRect(mClipRect, mPaint);
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
    public boolean onTouchEvent(MotionEvent event) {
        if (mIsTouch) {

            if (!mClipRect.contains(event.getX(), event.getY())) {
                return super.onTouchEvent(event);
            }

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mStartPosition.x = event.getX();
                    mStartPosition.y = event.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    float moveX = 0;
                    float moveY = 0;
                    if (orientValue == CORP_ORIENT.WIDTH) {
                        moveX = mStartPosition.x - event.getX();
                        mStartPosition.x = event.getX();
                    } else {
                        moveY = mStartPosition.y - event.getY();
                        mStartPosition.y = event.getY();
                    }

                    offsetClipRect(-moveX, -moveY);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    break;

                default:
                    break;
            }

            return true;

        } else {
            return super.onTouchEvent(event);
        }
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
            } else if (angle == 3) {
                movePercent = -(float) moveY / mScreenImgHeight;
            } else if (angle == 2) {
                movePercent = -(float) moveY / mScreenImgHeight;
            }
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
            startPercent = (int) (((float) start / mScreenImgHeight) * 100f);
            endPercent = (int) (((float) end / mScreenImgHeight) * 100f);
        } else if (tempOrient == CORP_ORIENT.WIDTH) {
            float baseX = (mClipRect3.width() - mClipRect.width()) / 2.0f;
            float start = baseX - movePercent * mScreenImgWidth;
            float end = start + mClipRect.width();
            startPercent = (int) (((float) start / mScreenImgWidth) * 100f);
            endPercent = (int) (((float) end / mScreenImgWidth) * 100f);
        }

        if (angle == 1 || angle == 3) {
            tempOrient = changeOrientation(tempOrient);
        }

        return new CropInfo(tempOrient, movePercent, startPercent, endPercent);
    }

    CORP_ORIENT changeOrientation(CORP_ORIENT orientation) {
        if (CORP_ORIENT.WIDTH == orientation) {
            return CORP_ORIENT.HEIGHT;
        } else if (CORP_ORIENT.HEIGHT == orientation) {
            return CORP_ORIENT.WIDTH;
        } else {
            return orientation;
        }
    }
}
