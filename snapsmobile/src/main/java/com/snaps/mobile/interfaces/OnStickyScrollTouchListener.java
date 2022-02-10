package com.snaps.mobile.interfaces;

import android.view.MotionEvent;

import com.snaps.mobile.component.ObserveScrollingNativeWebView;
import com.snaps.mobile.component.ObserveScrollingRecyclerView;
import com.snaps.mobile.component.ObserveScrollingScrollView;
import com.snaps.mobile.component.ObserveScrollingWebView;
import com.snaps.mobile.component.ProgressWebView;

public interface OnStickyScrollTouchListener {
	void onStickyScrollTouch(MotionEvent e, ObserveScrollingScrollView scrollView);
    void onStickyScrollTouch(MotionEvent e, ObserveScrollingRecyclerView recyclerView);
    void onStickyScrollTouch(MotionEvent e, ObserveScrollingNativeWebView webView);
}
