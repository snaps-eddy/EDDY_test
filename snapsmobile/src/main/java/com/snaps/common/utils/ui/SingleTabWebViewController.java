package com.snaps.common.utils.ui;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.RelativeLayout;

import com.snaps.mobile.component.ObserveScrollingWebView;
import com.snaps.mobile.interfaces.OnPageScrollListener;

public class SingleTabWebViewController implements OnPageScrollListener {
	private RelativeLayout titleLayout;

	private boolean isTitleSlideEnable = false;

	public SingleTabWebViewController() {}
	
	public void setView( RelativeLayout titleLayout ) {
		this.titleLayout = titleLayout;
	}
	
	// 기존 웹뷰를 사용할 때만 사용하면 됩니다.
	public void setWebView( ObserveScrollingWebView wv ) {
		wv.setOnScrollListener(new OnPageScrollListener() {
			@Override
			public boolean onScrollChanged(int l, int t, int oldl, int oldt) {
				SingleTabWebViewController.this.onScrollChanged(0, t, 0, oldt);
				return false;
			}

            @Override
            public boolean onScrollChanged(int dx, int dy) { return false; }
        });
	}
	
	
	// 혹시 모르니 남겨두기.
	public void showTitle() {
		if( titleLayout.getVisibility() == View.VISIBLE ) return;
		
		AlphaAnimation aa = new AlphaAnimation( 0f, 1f );
		aa.setDuration( 10 );
		aa.setFillAfter( true );
		aa.setAnimationListener( new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				titleLayout.setVisibility( View.VISIBLE );
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {}
			
			@Override
			public void onAnimationEnd(Animation animation) {}
		});
		titleLayout.startAnimation( aa );
	}
	
	public void hideTitle() {
		if( titleLayout.getVisibility() == View.GONE ) return;
		
		AlphaAnimation aa = new AlphaAnimation( 1f, 0f );
		aa.setDuration( 1000 );
		aa.setFillAfter( true );
		aa.setAnimationListener( new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}
			
			@Override
			public void onAnimationRepeat(Animation animation) {}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				titleLayout.setVisibility( View.GONE );
			}
		});
		titleLayout.startAnimation(aa);
	}
	
	@Override
	public boolean onScrollChanged(int l, int t, int oldl, int oldt) {
	    return onScrollChanged( l - oldl, t - oldt );
	}

    @Override
    public boolean onScrollChanged(int dx, int dy) {
        if (!isTitleSlideEnable()) {
            return false;
        }

        RelativeLayout.LayoutParams rParams = (RelativeLayout.LayoutParams) titleLayout.getLayoutParams();
        int oldMargin = rParams.topMargin;
        int newMargin;
        rParams.topMargin -= dy;
        if( rParams.topMargin < -titleLayout.getHeight() ) rParams.topMargin = -titleLayout.getHeight();
        else if( rParams.topMargin > 0 ) rParams.topMargin = 0;
        newMargin = rParams.topMargin;

        boolean changed = oldMargin != newMargin;
        if( changed ) titleLayout.setLayoutParams( rParams );

        return changed;
    }

    public boolean isTitleSlideEnable() {
		return isTitleSlideEnable;
	}

	public void setIsTitleSlideEnable(boolean isTitleSlideEnable) {
		this.isTitleSlideEnable = isTitleSlideEnable;
	}
}
