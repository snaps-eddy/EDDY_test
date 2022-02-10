package com.snaps.mobile.activity.google_style_image_selector.ui.custom;

import android.content.Context;
import android.graphics.PointF;

import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import android.util.AttributeSet;
import android.util.DisplayMetrics;

import com.snaps.common.customui.PrefetchDisableLinearLayoutManager;
import com.snaps.common.utils.log.Dlog;

/**
 * Created by ysjeong on 2016. 11. 30..
 */

public class SnapsCustomLinearLayoutManager extends PrefetchDisableLinearLayoutManager {
    private static final String TAG = SnapsCustomLinearLayoutManager.class.getSimpleName();
    private CenterSmoothScroller smoothScroller = null;

    public SnapsCustomLinearLayoutManager(Context context) {
        super(context);
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        if (smoothScroller == null)
            smoothScroller = new CenterSmoothScroller(recyclerView.getContext());
        smoothScroller.setTargetPosition(position);
        startSmoothScroll(smoothScroller);
    }

    public void scrollToCenterPosition(RecyclerView recyclerView, int position) {
        if (smoothScroller == null)
            smoothScroller = new CenterSmoothScroller(recyclerView.getContext());
        smoothScroller.setTargetPosition(position);
        startSmoothScroll(smoothScroller);
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            return super.scrollVerticallyBy(dy, recycler, state);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        return 0;
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private class CenterSmoothScroller extends LinearSmoothScroller {
        private static final float MILLISECONDS_PER_INCH = 15.f;

        CenterSmoothScroller(Context context) {
            super(context);
        }

        @Override
        public int calculateDtToFit(int viewStart, int viewEnd, int boxStart, int boxEnd, int snapPreference) {
            return (boxStart + (boxEnd - boxStart) / 2) - (viewStart + (viewEnd - viewStart) / 2);
        }

        @Override
        public PointF computeScrollVectorForPosition(int targetPosition) {
            return SnapsCustomLinearLayoutManager.this
                    .computeScrollVectorForPosition(targetPosition);
        }

        @Override
        protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
            // 스피드 값을 0으로 리턴하면 recyclerview 에서 exception 을 던진다.
            return MILLISECONDS_PER_INCH / displayMetrics.densityDpi;
        }

        @Override
        protected int getVerticalSnapPreference() {
            return SNAP_TO_START;
        }
    }

    @Override
    public boolean supportsPredictiveItemAnimations() {
        return false;
    }
}