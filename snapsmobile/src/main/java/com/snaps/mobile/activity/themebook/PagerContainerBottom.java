package com.snaps.mobile.activity.themebook;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class PagerContainerBottom extends FrameLayout implements ViewPager.OnPageChangeListener {

	boolean mNeedsRedraw = false;

	public PagerContainerBottom(Context context) {
		super(context);
		init();
	}

	public PagerContainerBottom(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public PagerContainerBottom(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	@SuppressLint("NewApi")
	private void init() {
	}

	@Override
	protected void onFinishInflate() {
	}

	private Point mCenter = new Point();
	private Point mInitialTouch = new Point();

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		mCenter.x = w / 2;
		mCenter.y = h / 2;
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		// Force the container to redraw on scrolling.
		// Without this the outer pages render initially and then stay static
		if (mNeedsRedraw)
			invalidate();
	}

	@Override
	public void onPageSelected(int position) {
	}

	@Override
	public void onPageScrollStateChanged(int state) {
		mNeedsRedraw = (state != ViewPager.SCROLL_STATE_IDLE);
	}

}
