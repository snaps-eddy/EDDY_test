package com.snaps.mobile.activity.ui.menu.renewal.viewpager;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.lsjwzh.widget.recyclerviewpager.LoopRecyclerViewPager;

/**
 * Created by songhw on 2016. 8. 3..
 */
public class TouchCustomLoopRecyclerViewPager extends LoopRecyclerViewPager {
    public static boolean doingTouch = false;
    public static boolean disablePaging = false;

    private boolean isViewAlive = true;
    private int delaySecond = -1;

    public TouchCustomLoopRecyclerViewPager(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public TouchCustomLoopRecyclerViewPager(Context context) {
        super(context);
    }

    public TouchCustomLoopRecyclerViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setAutoRolling( int delaySecond ) {
        this.delaySecond = delaySecond;
        startRolling();
    }

    private void startRolling() {
        handler = new Handler();
        handler.postDelayed(doRolling, delaySecond * 1000);
    }

    private void stopRolling() {
        if( handler != null ) handler.removeCallbacks( doRolling );
        handler = null;
    }

    private void showNextItem() {
        smoothScrollToPosition(getCurrentPosition() + 1);
    }

    private Handler handler;
    private Runnable doRolling = new Runnable() {
        @Override
        public void run() {
            if( handler != null && isViewAlive && delaySecond > 0 ) {
                showNextItem();
                handler.postDelayed( this, delaySecond * 1000 );
            }
        }
    };

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        doingTouch = e.getAction() == MotionEvent.ACTION_DOWN || e.getAction() == MotionEvent.ACTION_MOVE;
        if( disablePaging ) return false;

        return super.onInterceptTouchEvent(e);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        doingTouch = e.getAction() == MotionEvent.ACTION_DOWN || e.getAction() == MotionEvent.ACTION_MOVE;
        stopRolling();
        if( !doingTouch ) startRolling();
        return super.onTouchEvent(e);
    }

    @Override
    protected void onAttachedToWindow() {
        stopRolling();
        isViewAlive = true;
        startRolling();
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        isViewAlive = false;
        stopRolling();
        super.onDetachedFromWindow();
    }
}
