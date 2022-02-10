package com.snaps.mobile.activity.home.fragment;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.activity.ui.menu.renewal.model.Menu;
import com.snaps.mobile.component.FrameLayoutForScrollObserve;
import com.snaps.mobile.component.ScalableNativeLayout;
import com.snaps.mobile.component.SnapsWebviewProcess;

import java.util.ArrayList;

public class NativeScrollViewFragmentForMenuScrollableUI extends NativeFragmentForMenuScrollableUI {
	private SnapsWebviewProcess  webviewProcess;

	private ArrayList<Menu> menu;
	
    private boolean isHomeTab = false;
	
	public NativeScrollViewFragmentForMenuScrollableUI() {}

	public void setMenuList( ArrayList<Menu> menuList, boolean isHomeTab ) {
		this.menu = menuList;
        this.isHomeTab = isHomeTab;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

    @Override
    public void setLayout(FrameLayoutForScrollObserve wv) {
        this.scaleableLayout = (ScalableNativeLayout) wv;
        if( container != null && !attached ) attachView();
    }

    @Override
    public FrameLayoutForScrollObserve getLayout() {
        return scaleableLayout;
    }

    @Override
    public void attachView() {
        container.removeAllViews();
        if( scaleableLayout.getParent() != null ) ( (ViewGroup) scaleableLayout.getParent() ).removeAllViews();
        container.addView(scaleableLayout);

        if( !scaleableLayout.isInitialized() ) scaleableLayout.initUI(menu, isHomeTab);
        else UIUtil.reloadImage( scaleableLayout );

        if( onScrollViewCreateListener != null ) {
            if( scaleableLayout.isScrollable() )
                onScrollViewCreateListener.onNativeScrollViewCreated(scaleableLayout);
            else {
                scaleableLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (Build.VERSION.SDK_INT < 16)
                            scaleableLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        else
                            scaleableLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                        if (onScrollViewCreateListener != null) onScrollViewCreateListener.onNativeScrollViewCreated(scaleableLayout);
                    }
                });
            }
        }

        scaleableLayout.setOnStickyScrollTouchListener(onStickyScrollTouchListener);

        attached = true;
    }

    @Override
    public void dettachView() {
        if( scaleableLayout != null ) UIUtil.clearImage( getActivity(), scaleableLayout, true );

        if( container != null ) {
            container.removeAllViews();
            container.invalidate();
        }

        webviewProcess = null;
        onScrollViewCreateListener = null;
        attached = false;
    }

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup v = new RelativeLayout( getContext() );
		ViewGroup.LayoutParams params = new ViewGroup.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT );
		v.setLayoutParams( params );
		this.container = v;
		if( scaleableLayout != null && !attached ) attachView();
		return v;
	}

    @Override
    public void adjustScroll(int scrollHeight) {

    }
}
