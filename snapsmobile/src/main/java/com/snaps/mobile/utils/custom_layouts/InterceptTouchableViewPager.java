package com.snaps.mobile.utils.custom_layouts;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import android.content.Context;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.snaps.common.utils.log.Dlog;

public class InterceptTouchableViewPager extends ViewPager {
    private static final String TAG = InterceptTouchableViewPager.class.getSimpleName();

    private boolean paging = true;
    private Set<ZoomableRelativeLayout> canvasSet = new HashSet<>();
    private boolean preventViewPagerScroll = false;

    public InterceptTouchableViewPager(Context context) {
        super(context);
    }

    public InterceptTouchableViewPager(Context context,AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        if (preventViewPagerScroll) {
            return false;
        }

        if (paging) {
            try {
                if (e.getPointerCount() == 1)
                    return super.onInterceptTouchEvent(e);
            } catch (IllegalArgumentException e2) { //android bug 가끔 pointer index -1나와서 예외 처리..
                Dlog.e(TAG, e2);
            }
        }

        return false;
    }

    public void setPaging(boolean p) {
        paging = p;
    }

    public Set<ZoomableRelativeLayout> getCanvasSet() {
        return canvasSet;
    }

    public void addCanvas(ZoomableRelativeLayout canvas) {
        if (canvasSet == null) return;
        canvasSet.add(canvas);
    }

    public void initCanvasMatrix() {
        if (canvasSet == null) return;

        Iterator<ZoomableRelativeLayout> itorator = canvasSet.iterator();
        while (itorator != null && itorator.hasNext()) {
            try {
                ZoomableRelativeLayout canvas = itorator.next();
                if (canvas != null) {
                    canvas.initLocation();
                }
            } catch (Exception e) {
                Dlog.e(TAG, e);
            }
        }
    }

    public void setPreventViewPagerScroll(boolean useSinglePage) {
        this.preventViewPagerScroll = useSinglePage;
    }
}