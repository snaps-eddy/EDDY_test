package com.snaps.mobile.component;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.interfaces.OnPageScrollListener;
import com.snaps.mobile.interfaces.OnStickyScrollTouchListener;

public class FrameLayoutForScrollObserve extends FrameLayout {
	private static final String TAG = FrameLayoutForScrollObserve.class.getSimpleName();

	protected Context context;

	protected ObserveScrollingScrollView scrollViewLayout;
	protected ObserveScrollingRecyclerView recyclerView;
	protected ObserveScrollingNativeWebView webView;
	protected OnPageScrollListener pageScrollListener = null;
	protected OnStickyScrollTouchListener onStickyScrollTouchListener;

	protected boolean isInitialized = false;

	protected int scrollPos;
	protected int screenW;

	public FrameLayoutForScrollObserve(Context context) {
		super(context);
		init(context, null);
	}

	public FrameLayoutForScrollObserve(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public FrameLayoutForScrollObserve(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}

	void init(Context context, AttributeSet attrs) {
		this.context = context;

		Point displaySize = new Point();
		((Activity) context).getWindowManager().getDefaultDisplay().getSize(displaySize);
		screenW = displaySize.x;
	}

	public void scrollTo(int x, int y) {
		if (scrollViewLayout != null) {
			scrollViewLayout.scrollTo(x, y);
		} else if (recyclerView != null && recyclerView.getRecyclerView() != null) {
			RecyclerView view = recyclerView.getRecyclerView();
			view.scrollBy(x - view.computeHorizontalScrollOffset(), y - view.computeVerticalScrollOffset());
		} else if (webView != null) {
			webView.scrollTo(x, y);
		}
	}

	public int getWebViewScrollX() {
		return scrollViewLayout != null ? scrollViewLayout.getScrollX() : recyclerView != null && recyclerView.getRecyclerView() != null ? recyclerView.getRecyclerView().computeHorizontalScrollOffset() : webView != null ? webView.getScrollX() : 0;
	}

	public int getWebViewScrollY() {
		return scrollViewLayout != null ? scrollViewLayout.getScrollY() : recyclerView != null && recyclerView.getRecyclerView() != null ? recyclerView.getRecyclerView().computeVerticalScrollOffset() : webView != null ? webView.getScrollY() : 0;
	}

	public boolean isScrollAtTopOrBottom() {
		if (recyclerView != null) {
			return recyclerView.isScrollAtTop() || recyclerView.isScrollAtBottom();
		} else if (scrollViewLayout != null) {
			return scrollViewLayout.isScrollAtTop() || scrollViewLayout.isScrollAtBottom();
		} else {
			return false;
		}
	}

	public int getMaxScrollPosition() {
		if (recyclerView != null) {
			return recyclerView.getMaxScrollPosition();
		} else if (scrollViewLayout != null) {
			return scrollViewLayout.getMaxScrollPosition();
		} else if (webView != null) {
			return webView.getMaxScrollPosition();
		} else {
			return 0;
		}
	}

	public boolean isScrollAtTop() {
		if (recyclerView != null) {
			return recyclerView.isScrollAtTop();
		} else if (scrollViewLayout != null) {
			return scrollViewLayout.isScrollAtTop();
		} else if (webView != null) {
			return webView.isScrollAtTop();
		} else {
			return false;
		}
	}

	public boolean isScrollAtBottom() {
		if (recyclerView != null) {
			return recyclerView.isScrollAtBottom();
		} else if (scrollViewLayout != null) {
			return scrollViewLayout.isScrollAtBottom();
		} else {
			return false;
		}
	}

	public void setScrollPos(int pos) {
		this.scrollPos = pos;
	}

	public int getScrollPos() {
		return scrollPos;
	}

	public void setPageScrollListner(OnPageScrollListener listener) {
		this.pageScrollListener = listener;
	}

	public void setOnStickyScrollTouchListener(OnStickyScrollTouchListener listener) {
		this.onStickyScrollTouchListener = listener;
	}

	public boolean isInitialized() {
		return isInitialized;
	}

	public void setInitialized(boolean flag) {
		isInitialized = flag;
	}

	public boolean isScrollable() {
		return (scrollViewLayout != null && scrollViewLayout.isScrollable()) || (recyclerView != null && recyclerView.isScrollable() || webView != null && webView.isScrollable());
	}

	protected int getDefaultTopMargin(boolean isSingleTab) {
		if (context == null) {
			return 0;
		}

		Resources res = context.getResources();
		try {
			if (isOddScreenDevice()) {
				return (int) (UIUtil.convertDPtoPX(context, UIUtil.getStatusBarHeight()) + (isSingleTab ? 0 : res.getDimension(R.dimen.snaps_sticky_viewpager_strip_height)));
			}
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}

		return (int) (res.getDimension(R.dimen.home_title_bar_height) + (isSingleTab ? 0 : res.getDimension(R.dimen.snaps_sticky_viewpager_strip_height)));
	}

	private boolean isOddScreenDevice() throws Exception {
		return Build.MODEL.contains("N950") || Build.MODEL.contains("N960");
	}

	protected int getDefaultTopMargin() {
		return getDefaultTopMargin(false);
	}
}
