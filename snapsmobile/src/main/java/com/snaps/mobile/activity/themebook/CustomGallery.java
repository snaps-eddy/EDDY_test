package com.snaps.mobile.activity.themebook;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Gallery;

public class CustomGallery extends Gallery {

	public CustomGallery(Context context) {
		this(context, null);
	}

	public CustomGallery(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CustomGallery(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

}