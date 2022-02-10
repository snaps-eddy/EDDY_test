package com.snaps.mobile.interfaces;

public interface OnPageScrollListener {
	boolean onScrollChanged( int l, int t, int oldl, int oldt );
    boolean onScrollChanged( int dx, int dy );
}
