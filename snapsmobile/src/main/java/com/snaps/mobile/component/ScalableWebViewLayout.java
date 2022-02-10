package com.snaps.mobile.component;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import com.snaps.mobile.R;
import com.snaps.mobile.interfaces.OnPageScrollListener;

public class ScalableWebViewLayout extends FrameLayoutForScrollObserve {

	private boolean isHomeItem = false;

	public ScalableWebViewLayout(Context context) {
		super(context);
	}

	public ScalableWebViewLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ScalableWebViewLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void initUI(String url) {

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.custom_snaps_native_webview, null);
		scrollViewLayout = (ObserveScrollingScrollView) v.findViewById(R.id.snaps_native_main_scrollview);
		webView = (ObserveScrollingNativeWebView) v.findViewById(R.id.custom_snaps_native_web_view);
		webView.setOnScrollListener(new OnPageScrollListener() {
			@Override
			public boolean onScrollChanged(int l, int t, int oldl, int oldt) {
				if (pageScrollListener != null) {
					pageScrollListener.onScrollChanged(0, t, 0, oldt);
				}
				return false;
			}

			@Override
			public boolean onScrollChanged(int dx, int dy) {
				return false;
			}
		});

		webView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (onStickyScrollTouchListener != null) {
					onStickyScrollTouchListener.onStickyScrollTouch(event, webView);
				}
				return false;
			}
		});

		if (!webView.isLoadedWebView()) {
			webView.loadUrl(url);
		} else {



		}
		addView(v);

		setInitialized(true);
	}
	
    public void reLoad(String url) {
        webView.loadUrl( url );
    }

    public void reFresh() {
		if(webView == null) return;
        webView.reFresh();
    }

    public boolean isHome() { return this.isHomeItem; }


	public boolean canScrollHor(int direction) {
		return webView.canScrollHor(direction);
	}
}
