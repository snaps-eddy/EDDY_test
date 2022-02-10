package com.snaps.common.utils.ui;

import android.widget.RelativeLayout;

import com.snaps.mobile.component.ObserveScrollingScrollView;
import com.snaps.mobile.interfaces.OnPageScrollListener;

public class TitleSlideControllerWithScrollView implements OnPageScrollListener {
	private RelativeLayout titleLayout;
	private boolean isTitleSlideEnable = false;

	public TitleSlideControllerWithScrollView() {}
	
	public void setViews( RelativeLayout titleLayout, ObserveScrollingScrollView sv ) {
		this.titleLayout = titleLayout;
		sv.setOnScrollListener( this );
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
        rParams.topMargin -= dy;
        if( rParams.topMargin < -titleLayout.getHeight() ) rParams.topMargin = -titleLayout.getHeight();
        else if( rParams.topMargin > 0 ) rParams.topMargin = 0;

        boolean changed = oldMargin != rParams.topMargin;
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
