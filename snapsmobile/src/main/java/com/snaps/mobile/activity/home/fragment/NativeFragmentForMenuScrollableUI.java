package com.snaps.mobile.activity.home.fragment;

import android.os.Bundle;
import android.view.ViewGroup;

import com.kmshack.newsstand.ScrollTabHolderFragment;
import com.snaps.mobile.component.FrameLayoutForScrollObserve;
import com.snaps.mobile.component.ScalableNativeLayout;
import com.snaps.mobile.component.ScalableWebViewLayout;
import com.snaps.mobile.interfaces.OnNativeScrollViewCreateListener;
import com.snaps.mobile.interfaces.OnStickyScrollTouchListener;
import com.snaps.mobile.product_native_ui.ui.SnapsProductListView;

public abstract class NativeFragmentForMenuScrollableUI extends ScrollTabHolderFragment {
	protected ScalableNativeLayout scaleableLayout;
    protected SnapsProductListView snapsProductListView;
    protected ScalableWebViewLayout scalableWebViewLayout;

    protected OnNativeScrollViewCreateListener onScrollViewCreateListener;
    protected OnStickyScrollTouchListener onStickyScrollTouchListener;

    protected ViewGroup container;

    protected boolean attached = false;

	public NativeFragmentForMenuScrollableUI() {}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public abstract void setLayout( FrameLayoutForScrollObserve wv );
	public abstract FrameLayoutForScrollObserve getLayout();
	
	@Override
	public void onDestroy() {
		super.onDestroy();
        if (attached)
            dettachView();
	}

    public void setNativeScrollViewCreateListener( OnNativeScrollViewCreateListener listener ) {
        this.onScrollViewCreateListener = listener;
    }

    public void setOnStickyScrollTouchListener( OnStickyScrollTouchListener listener ) {
        this.onStickyScrollTouchListener = listener;
    }

    public abstract void attachView();
    public abstract void dettachView();
}
