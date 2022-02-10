package com.snaps.mobile.activity.edit;

import android.content.Context;
import android.graphics.Point;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.utils.custom_layouts.InterceptTouchableViewPager;

public class PagerContainer extends FrameLayout implements ViewPager.OnPageChangeListener {
	private static final String TAG = PagerContainer.class.getSimpleName();
	private InterceptTouchableViewPager mPager;
	boolean mNeedsRedraw = false;
	
	public PagerContainer(Context context) {
		super(context);
		init();
	}

	public PagerContainer(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public PagerContainer(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		if(Config.isSnapsSticker()) {
			setClipChildren(false);
			setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}
	}

	@Override
	protected void onFinishInflate() {
		try {
			for( int i = 0; i < ((ViewGroup) this).getChildCount(); ++i ) {
				if( this.getChildAt(i) instanceof InterceptTouchableViewPager ) {
					mPager = (InterceptTouchableViewPager) this.getChildAt(i);
					mPager.setOnPageChangeListener(this);
					break;
				}
			}
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
	}

	private Point mCenter = new Point();
	private Point mInitialTouch = new Point();

	public InterceptTouchableViewPager getViewPager() {
		return mPager;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		mCenter.x = w / 2;
		mCenter.y = h / 2;
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
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