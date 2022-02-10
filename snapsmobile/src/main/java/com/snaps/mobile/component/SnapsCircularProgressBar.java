/*
 * Copyright 2013 Leon Cheng
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.snaps.mobile.component;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ProgressBar;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;

public class SnapsCircularProgressBar extends ProgressBar {
    private static final String TAG = "SnapsCircularProgressBar";

    private static final int STROKE_WIDTH = 5;

    private String mTitle = "";

    private int mStrokeWidth = STROKE_WIDTH;

    private final RectF mCircleBounds = new RectF();

    private final Paint mProgressColorPaint = new Paint();
    private final Paint mBackgroundColorPaint = new Paint();
    private final Paint mTitlePaint = new Paint();
    private final Paint mPercentPaint = new Paint();

    private boolean mHasShadow = true;
    private int mShadowColor = Color.BLACK;
    private Context mContext = null;

    public interface ProgressAnimationListener {
        public void onAnimationStart();

        public void onAnimationFinish();

        public void onAnimationProgress(int progress);
    }

    public SnapsCircularProgressBar(Context context) {
        super(context);
        mContext = context;
        init(null, 0);
    }

    public SnapsCircularProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(attrs, 0);
    }

    public SnapsCircularProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        init(attrs, defStyle);
    }

    public void init(AttributeSet attrs, int style) {
        //so that shadow shows up properly for lines and arcs
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        TypedArray a = getContext().obtainStyledAttributes(attrs,
                R.styleable.SnapsCircularProgressBar, style, 0);

        String color;
        Resources res = getResources();

        this.mHasShadow = a.getBoolean(R.styleable.SnapsCircularProgressBar_hasShadow, true);

        color = a.getString(R.styleable.SnapsCircularProgressBar_progressColor);
        if (color == null) {
            mProgressColorPaint.setColor(res.getColor(R.color.circular_progress_default_progress));
        } else {
//			mProgressColorPaint.setColor(Color.parseColor(color));
            mProgressColorPaint.setColor(Color.parseColor("#e36a63"));


        }
        mBackgroundColorPaint.setColor(0x1AFFFFFF);

        color = a.getString(R.styleable.SnapsCircularProgressBar_titleColor);
        if (color == null) {
            mTitlePaint.setColor(res.getColor(R.color.circular_progress_default_title));
            mPercentPaint.setColor(res.getColor(R.color.circular_progress_default_title));
        } else {
            mTitlePaint.setColor(Color.parseColor(color));
            mPercentPaint.setColor(Color.parseColor(color));
        }

        String t = a.getString(R.styleable.SnapsCircularProgressBar_progresstitle);
        if (t != null)
            mTitle = t;

        mStrokeWidth = a.getInt(R.styleable.SnapsCircularProgressBar_lineWidth, STROKE_WIDTH);
        mStrokeWidth = UIUtil.convertDPtoPX(getContext(), 2);
        a.recycle();


        mProgressColorPaint.setAntiAlias(true);
        mProgressColorPaint.setStyle(Paint.Style.STROKE);
        mProgressColorPaint.setStrokeWidth(mStrokeWidth);

        mBackgroundColorPaint.setAntiAlias(true);
        mBackgroundColorPaint.setStyle(Paint.Style.STROKE);
        mBackgroundColorPaint.setStrokeWidth(mStrokeWidth);

        mTitlePaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics()));
        mTitlePaint.setStyle(Style.FILL_AND_STROKE);
        mTitlePaint.setAntiAlias(true);
        mTitlePaint.setShadowLayer(0.1f, 0, 1, Color.GRAY);

        mPercentPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics()));
        mPercentPaint.setStyle(Style.FILL_AND_STROKE);
        mPercentPaint.setAntiAlias(true);
        mPercentPaint.setShadowLayer(0.1f, 0, 1, Color.GRAY);

    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        canvas.drawArc(mCircleBounds, 0, 360, false, mBackgroundColorPaint);

        int prog = getProgress();
        float scale = getMax() > 0 ? (float) prog / getMax() * 360 : 0;

        if (mHasShadow)
            mProgressColorPaint.setShadowLayer(3, 0, 1, mShadowColor);
        canvas.drawArc(mCircleBounds, 270, scale, false, mProgressColorPaint);

        if (!TextUtils.isEmpty(mTitle)) {
            int textW0 = (int) mTitlePaint.measureText("0");
            int textW00 = (int) mTitlePaint.measureText("00");
            int xPos = (int) (getMeasuredWidth() / 2);
            if (mTitle.length() > 2) xPos = xPos - textW00 / 2 - textW0;
            else if (mTitle.length() > 1) xPos -= textW00 / 2;
            int yPos = (int) (getMeasuredHeight() / 2);

            float titleHeight = Math.abs(mTitlePaint.descent() + mTitlePaint.ascent());
            float perHeight = Math.abs(mPercentPaint.descent() + mPercentPaint.ascent());

            yPos += titleHeight / 2;
            canvas.drawText(mTitle, xPos, yPos, mTitlePaint);

            xPos = getMeasuredWidth() / 2 + textW0 + (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1.3f, getResources().getDisplayMetrics());
            yPos = (int) (((getMeasuredHeight() / 2) - (titleHeight / 2)) + perHeight);
            canvas.drawText("%", xPos, yPos, mPercentPaint);

        }

        super.onDraw(canvas);
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        final int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        final int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int min = Math.min(width, height);
        setMeasuredDimension(min + 2 * STROKE_WIDTH, min + 2 * STROKE_WIDTH);

        mCircleBounds.set(STROKE_WIDTH, STROKE_WIDTH, min + STROKE_WIDTH, min + STROKE_WIDTH);
    }

    @Override
    public synchronized void setProgress(int progress) {
        super.setProgress(progress);

        // the setProgress super will not change the details of the progress bar
        // anymore so we need to force an update to redraw the progress bar
        invalidate();
    }

    public void animateProgressTo(final int start, final int end, final ProgressAnimationListener listener) {
        if (start != 0)
            setProgress(start);

        final ObjectAnimator progressBarAnimator = ObjectAnimator.ofFloat(this, "animateProgress", start, end);
        progressBarAnimator.setDuration(200);
        progressBarAnimator.setInterpolator(new LinearInterpolator());

        progressBarAnimator.addListener(new AnimatorListener() {
            @Override
            public void onAnimationCancel(final Animator animation) {
            }

            @Override
            public void onAnimationEnd(final Animator animation) {
                SnapsCircularProgressBar.this.setProgress(end);
                if (listener != null)
                    listener.onAnimationFinish();
            }

            @Override
            public void onAnimationRepeat(final Animator animation) {
            }

            @Override
            public void onAnimationStart(final Animator animation) {
                if (listener != null)
                    listener.onAnimationStart();
            }
        });

        progressBarAnimator.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(final ValueAnimator animation) {
                int progress = ((Float) animation.getAnimatedValue()).intValue();
                if (progress != SnapsCircularProgressBar.this.getProgress()) {
                    //Log.d(TAG, progress + "");
                    SnapsCircularProgressBar.this.setProgress(progress);
                    if (listener != null)
                        listener.onAnimationProgress(progress);
                }
            }
        });
        progressBarAnimator.start();
    }

    public synchronized void setTitle(String title) {
        this.mTitle = title;
        invalidate();
    }

    public synchronized void setSubTitle(String subtitle) {
        invalidate();
    }

    public synchronized void setSubTitleColor(int color) {
        invalidate();
    }

    public synchronized void setTitleColor(int color) {
        mTitlePaint.setColor(color);
        invalidate();
    }

    public synchronized void setHasShadow(boolean flag) {
        this.mHasShadow = flag;
        invalidate();
    }

    public synchronized void setShadow(int color) {
        this.mShadowColor = color;
        invalidate();
    }

    public String getTitle() {
        return mTitle;
    }

    public boolean getHasShadow() {
        return mHasShadow;
    }
}
