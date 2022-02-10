package com.snaps.mobile.component;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.ScrollView;

import com.snaps.mobile.interfaces.OnPageScrollListener;

public class ObserveScrollingScrollView extends ScrollView {
	private OnPageScrollListener listener;
	private boolean isEnableFling = false;

	public ObserveScrollingScrollView(Context context) {
		super(context);
		init();
	}

	public ObserveScrollingScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ObserveScrollingScrollView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		isEnableFling = true;
		setVerticalScrollBarEnabled(false);
		setHorizontalScrollBarEnabled(false);
	}
	
	public void setOnScrollListener( OnPageScrollListener listener ) {
		this.listener = listener;
	}
	
	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		if( listener != null ) listener.onScrollChanged(l, t, oldl, oldt);
	}

    private Handler titleAnimationHandler;
    private Runnable titleAnimationRunnable;
	@Override
	public void fling(int velocityY) {
        if( titleAnimationHandler != null && titleAnimationRunnable != null ) {
            titleAnimationHandler.removeCallbacks( titleAnimationRunnable );
            titleAnimationHandler = null;
            titleAnimationRunnable = null;
        }

		if (!isEnableFling) return;
		super.fling(velocityY);
	}

	public void setEnableFling(boolean enableFling) {
		isEnableFling = enableFling;
        titleAnimationHandler = null;
        titleAnimationRunnable = null;
	}

    public void setTitleAnimation( Handler titleAnimationHandler, Runnable titleAnimationRunnable ) {
        this.titleAnimationHandler = titleAnimationHandler;
        this.titleAnimationRunnable = titleAnimationRunnable;
    }

    public int getMaxScrollPosition() {
        return computeVerticalScrollRange() - computeVerticalScrollExtent();
    }

	public boolean isScrollable() {
		return computeVerticalScrollRange() > computeVerticalScrollExtent();
	}

    public boolean isScrollAtBottom() {
        return computeVerticalScrollRange() - ( computeVerticalScrollExtent() + getScrollY() ) < 1;
    }

    public boolean isScrollAtTop() {
        return getScrollY() < 1;
    }


}
