package com.snaps.mobile.activity.ui.menu.renewal.view;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by songhw on 2016. 8. 5..
 */
public class TouchCustomRecyclerView extends RecyclerView {
    public static boolean doingTouch = false;

    public TouchCustomRecyclerView(Context context) {
        super(context);
    }

    public TouchCustomRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TouchCustomRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        doingTouch = e.getAction() == MotionEvent.ACTION_DOWN || e.getAction() == MotionEvent.ACTION_MOVE;
        return super.onInterceptTouchEvent(e);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        doingTouch = e.getAction() == MotionEvent.ACTION_DOWN || e.getAction() == MotionEvent.ACTION_MOVE;
        return super.onTouchEvent(e);
    }
}
