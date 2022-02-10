package com.snaps.mobile.activity.google_style_image_selector.ui.custom;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;

import com.snaps.common.customui.PrefetchDisableGridLayoutManager;
import com.snaps.common.utils.log.Dlog;

/**
 * Created by ysjeong on 2017. 1. 16..
 */

public class CustomGridLayoutManager extends PrefetchDisableGridLayoutManager {
    private static final String TAG = CustomGridLayoutManager.class.getSimpleName();
    private boolean isScrollEnabled = true;
    public CustomGridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public CustomGridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    public CustomGridLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
    }

    public void setScrollEnabled(boolean flag) {
        this.isScrollEnabled = flag;
    }

    @Override
    public boolean canScrollVertically() {
        return isScrollEnabled && super.canScrollVertically();
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
    public void setSpanCount(int spanCount) {
        try {
            super.setSpanCount(spanCount);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    @Override
    public boolean supportsPredictiveItemAnimations() {
        return false;
    }
}
