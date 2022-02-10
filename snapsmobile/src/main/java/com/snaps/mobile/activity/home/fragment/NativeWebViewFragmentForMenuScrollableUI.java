package com.snaps.mobile.activity.home.fragment;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import com.snaps.mobile.component.EndlessPagerForNativeScrollViewAdapter;
import com.snaps.mobile.component.FrameLayoutForScrollObserve;
import com.snaps.mobile.component.ScalableWebViewLayout;

public class NativeWebViewFragmentForMenuScrollableUI extends NativeFragmentForMenuScrollableUI{
    public NativeWebViewFragmentForMenuScrollableUI() {}
    private String url;;

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup v = new RelativeLayout( getContext() );
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT );
        v.setLayoutParams( params );
        this.container = v;
        if( scalableWebViewLayout != null && !attached ) attachView();
        return v;
    }

    @Override
    public void setLayout(FrameLayoutForScrollObserve wv) {
        scalableWebViewLayout = (ScalableWebViewLayout) wv;
        if( container != null && !attached ) attachView();
    }

    @Override
    public FrameLayoutForScrollObserve getLayout() {
        return scalableWebViewLayout;
    }

    @Override
    public void attachView() {
        if(scalableWebViewLayout == null) return;

        container.removeAllViews();

        if( scalableWebViewLayout.getParent() != null ) ( (ViewGroup) scalableWebViewLayout.getParent() ).removeAllViews();
        container.addView( scalableWebViewLayout );

        if( !scalableWebViewLayout.isInitialized() )
            scalableWebViewLayout.initUI(url);

        if( onScrollViewCreateListener != null ) {
            if( scalableWebViewLayout.isScrollable() )
                onScrollViewCreateListener.onNativeScrollViewCreated( scalableWebViewLayout );
            else {
                scalableWebViewLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (Build.VERSION.SDK_INT < 16)
                            scalableWebViewLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        else
                            scalableWebViewLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                        if (onScrollViewCreateListener != null) onScrollViewCreateListener.onNativeScrollViewCreated( scalableWebViewLayout );
                    }
                });
            }
        }
        scalableWebViewLayout.setOnStickyScrollTouchListener( onStickyScrollTouchListener );
        attached = true;

    }

    @Override
    public void dettachView() {
        if( container != null ) {
            container.removeAllViews();
            container.invalidate();
        }

        attached = false;

    }

    public void reloadUrl(String url) {
        scalableWebViewLayout.reLoad(url);
    }

    public void reFresh() {
        if(scalableWebViewLayout == null) return;
        scalableWebViewLayout.reFresh();
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if(menuVisible && EndlessPagerForNativeScrollViewAdapter.isFirst) {
            reFresh();
        }
    }

    @Override
    public void adjustScroll(int scrollHeight) {}
}
