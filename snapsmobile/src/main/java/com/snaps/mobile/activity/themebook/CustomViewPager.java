package com.snaps.mobile.activity.themebook;

import android.content.*;
import android.graphics.*;
import androidx.viewpager.widget.ViewPager;
import android.view.*;


public class CustomViewPager extends ViewPager {

	private static Camera mCamera;

	public CustomViewPager(Context context) {
		super(context);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return super.onInterceptTouchEvent(arg0);
	}

	@Override
	public boolean onTouchEvent(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return super.onTouchEvent(arg0);
	}

	@Override
	public void setCurrentItem(int item, boolean smoothScroll) {
		// TODO Auto-generated method stub
		super.setCurrentItem(item, smoothScroll);
	}

	@Override
	public void setCurrentItem(int item) {
		// TODO Auto-generated method stub
		super.setCurrentItem(item);
	}

	@Override
	public void setOffscreenPageLimit(int limit) {
		// TODO Auto-generated method stub
		super.setOffscreenPageLimit(limit);
	}

	@Override
	public void setPageMargin(int marginPixels) {
		// TODO Auto-generated method stub
		super.setPageMargin(marginPixels);
	}

}