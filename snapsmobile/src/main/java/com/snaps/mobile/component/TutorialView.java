package com.snaps.mobile.component;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Region;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import com.snaps.common.utils.imageloader.CropUtil;
import com.snaps.mobile.R;

public class TutorialView extends View {

    Bitmap mBitmap = null;
    // view전체크기
    int mScreenWidth, mScreenHeight;
    Context mContext;
    // 구멍을 뚤을지 말지 설정...
    boolean mIsClipping = true;
    int mBackgroundColor;
    private Path mPath;

    // 원의 반지름
    final float CIRCLE_RADIUSHDPI = 36.f;
    final float CIRCLE_RADIUSXHDPI = 56.f;

    // 비트맵 기준으로 탑 마진..
    final float CIRCLE_TOPMARGINHDPI = 118.f;
    final float CIRCLE_TOPMARGINXHDPI = 175.5f;

    int mBitmapTopMarginDPI = 75;
    float mCircleRadius;

    float mBitmapLeftMargin = 0;

    public TutorialView(Context context) {
        super(context);
        init(context);
    }

    public TutorialView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TutorialView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    /***
     * 초기화하는 함수..
     */
    void init(Context context) {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mContext = context;
        mBackgroundColor = Color.parseColor("#99000000");
        // bitmap을 로드 한다.
        mBitmap = CropUtil.getInSampledDecodeBitmapFromResource(context.getResources(), R.drawable.tut_1);//BitmapFactory.decodeResource(context.getResources(), R.drawable.tut_1);
        mPath = new Path();
    }

    PointF mPoint = new PointF();
    // 비트맵이 그려질때 상단 마진..
    float mBitmapTopMarginPX = 0;

    /***
     * 원을 그릴영역을 계산하는 함수...
     */
    void calculatorCicle() {
        if (!mIsClipping)
            return;

        // 원에 위치를 계산한다.
        int bWidth = mBitmap.getWidth();
        // 이미지 넓이가 화면 넓이보다 작은경우 이미지를 중앙에 그리기 위해 마진을 준다.
        if (mScreenWidth > bWidth)
            mBitmapLeftMargin = (mScreenWidth - bWidth) / 2.f;

        mPoint.x = bWidth / 2.f + mBitmapLeftMargin;

        float aa = 0;
        float scale = getResources().getDisplayMetrics().density;
        // xhdpi
        if (bWidth >= 720) {
            aa = bWidth / 720.f;
            mCircleRadius = CIRCLE_RADIUSXHDPI * aa;
            mPoint.y = mBitmapTopMarginPX + CIRCLE_TOPMARGINXHDPI * aa;
            mBitmapTopMarginPX = (int) (70.f * scale + 0.5f);
        }// hdpi
        else {
            aa = bWidth / 480.f;
            mCircleRadius = CIRCLE_RADIUSHDPI * aa;
            mPoint.y = mBitmapTopMarginPX + CIRCLE_TOPMARGINHDPI * aa;
            mBitmapTopMarginPX = (int) (70.f * scale + 0.5f);
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();
        // 클립영역 설정...
        // 전체영역
        canvas.clipRect(0, 0, mScreenWidth, mScreenHeight);
        // 원을 그려보자..!!
        if (mIsClipping) {
            mPath.reset();
            mPath.addCircle(mPoint.x, mPoint.y, mCircleRadius, Path.Direction.CCW);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                canvas.clipOutPath(mPath);
            }
            else {
                canvas.clipPath(mPath, Region.Op.DIFFERENCE);
            }
        }
        // 배경 그리기..
        canvas.drawColor(mBackgroundColor);
        // 비트맴을 그린다.
        canvas.drawBitmap(mBitmap, mBitmapLeftMargin, mBitmapTopMarginPX, null);

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
        calculatorCicle();
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onAttachedToWindow() {
        // TODO Auto-generated method stub
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mBitmap != null && !mBitmap.isRecycled()) {
            mBitmap.recycle();
            mBitmap = null;
        }
    }

}
