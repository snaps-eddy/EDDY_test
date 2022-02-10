package com.snaps.mobile.utils.custom_layouts;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.FrameLayout;

public class AFrameLayoutParams extends FrameLayout.LayoutParams {

	public AFrameLayoutParams(
			android.widget.FrameLayout.LayoutParams source) {
		super((android.view.ViewGroup.LayoutParams) source);
	}

	public AFrameLayoutParams(MarginLayoutParams source) {
		super(source);
	}

	public AFrameLayoutParams(android.view.ViewGroup.LayoutParams source) {
		super(source);
	}

	public AFrameLayoutParams(int w, int h) {
		super(w, h);
	}

	public AFrameLayoutParams(Context c, AttributeSet attrs) {
		super(c, attrs);
	}
}
