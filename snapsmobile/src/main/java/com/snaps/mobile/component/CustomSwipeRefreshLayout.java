package com.snaps.mobile.component;

import android.content.Context;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.snaps.common.utils.log.Dlog;

/**
 * Created by songhw on 2016. 10. 17..
 */
public class CustomSwipeRefreshLayout extends SwipeRefreshLayout {
    private static final String TAG = CustomSwipeRefreshLayout.class.getSimpleName();

    public CustomSwipeRefreshLayout(Context context) {
        super(context);
    }

    public CustomSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        try {
            return super.onTouchEvent(ev);
        } catch( IllegalArgumentException e ) {
            Dlog.e(TAG, e);
            return true;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch( IllegalArgumentException e ) {
            Dlog.e(TAG, e);
            return true;
        }
    }
}
