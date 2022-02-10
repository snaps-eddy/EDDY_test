package com.snaps.mobile.utils.custom_layouts;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.RelativeLayout;

public class ARelativeLayoutParams extends RelativeLayout.LayoutParams {

	public ARelativeLayoutParams(
			android.widget.RelativeLayout.LayoutParams source) {
		super((android.view.ViewGroup.LayoutParams)source);
	}

	public ARelativeLayoutParams(MarginLayoutParams source) {
		super(source);
	}

	public ARelativeLayoutParams(android.view.ViewGroup.LayoutParams source) {
		super(source);
	}

	public ARelativeLayoutParams(int w, int h) {
		super(w, h);
	}

	public ARelativeLayoutParams(Context c, AttributeSet attrs) {
		super(c, attrs);
	}
}
