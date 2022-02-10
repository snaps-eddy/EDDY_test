package com.snaps.mobile.activity.google_style_image_selector.interfaces;

import androidx.recyclerview.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

/**
 * Created by ysjeong on 2017. 1. 11..
 */

public interface IUIPinchMotionCallback {
    boolean processUIByScaleGesture(ScaleGestureDetector detector);
    boolean onRecyclerViewItemTouchEvent(RecyclerView recyclerView, MotionEvent event);
    boolean scaleNextDepth(float scaleFactor);
    boolean scalePrevDepth(float scaleFactor);
}
