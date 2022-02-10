package com.snaps.mobile.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.activity.home.fragment.FragmentViewPager;
import com.snaps.mobile.activity.home.fragment.NativeWebViewFragmentForMenuScrollableUI;
import com.snaps.mobile.activity.ui.menu.renewal.MenuDataManager;

public class CustomSensitivityViewPager extends FragmentViewPager {
    private static final String TAG = CustomSensitivityViewPager.class.getSimpleName();

	private final int MIN_DISTANCE = 10;;

	public CustomSensitivityViewPager(Context context) {
		super(context);
		init();
	}

	public CustomSensitivityViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	@SuppressWarnings("rawtypes")
	private void init() {}

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (MenuDataManager.viewPageHorizontallyScrollable()) {
            try {
                return super.onInterceptTouchEvent( ev );
            } catch (IllegalArgumentException e) { Dlog.e(TAG, e); }
        }
        return false;
    }

    @Override
    public void scrollBy(int x, int y) {
        if( MenuDataManager.viewPageHorizontallyScrollable() )
            super.scrollBy(x, y);
    }

    @Override
    public void scrollTo(int x, int y) {
        if( MenuDataManager.viewPageHorizontallyScrollable() )
            super.scrollTo(x, y);
    }

}
