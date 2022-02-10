package com.snaps.mobile.component;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.webkit.WebView;

import com.snaps.mobile.interfaces.OnPageScrollListener;

public class ObserveScrollingWebView extends WebView {
	private OnPageScrollListener listener;
	private boolean isScrollable = false;

	public ObserveScrollingWebView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public ObserveScrollingWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public ObserveScrollingWebView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public void setOnScrollListener(OnPageScrollListener listener) {
		this.listener = listener;
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		if (listener != null) {
			listener.onScrollChanged(l, t, oldl, oldt);
		}
	}

	public int getMaxScrollPosition() {
		return computeVerticalScrollRange() - computeVerticalScrollExtent();
	}

	public boolean isScrollable() {
		return computeVerticalScrollRange() > computeVerticalScrollExtent();
	}

	public boolean isScrollAtTop() {
		return getScrollY() < 1;
	}

	public boolean canScrollHor(int direction) {
		final int offset = computeHorizontalScrollOffset();
		final int range = computeHorizontalScrollRange() - computeHorizontalScrollExtent();
		if (range == 0) {
			return false;
		}
		if (direction < 0) {
			return offset > 0;
		} else {
			return offset < range - 1;
		}
	}

	private Handler titleAnimationHandler;
	private Runnable titleAnimationRunnable;
	private boolean isEnableFling = false;

	public void setEnableFling(boolean enableFling) {
		isEnableFling = enableFling;
		titleAnimationHandler = null;
		titleAnimationRunnable = null;
	}

	public void setTitleAnimation(Handler titleAnimationHandler, Runnable titleAnimationRunnable) {
		this.titleAnimationHandler = titleAnimationHandler;
		this.titleAnimationRunnable = titleAnimationRunnable;
	}

}
