package com.snaps.mobile.activity.home.fragment;

import android.content.Context;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;

public class FragmentViewPager extends ViewPager {
	public FragmentViewPager(Context context) {
		super(context);
	}

	
	
	public FragmentViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * @param containerViewId the ViewPager this adapter is being supplied to
	 * @param id pass in getItemId(position) as this is whats used internally in this class
	 * @return the tag used for this pages fragment
	 */
	public static String makeFragmentName(int containerViewId, long id) {
	    return "android:switcher:" + containerViewId + ":" + id;
	}
}
