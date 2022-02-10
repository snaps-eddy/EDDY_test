package com.snaps.mobile.activity.google_style_image_selector.ui.custom;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;

import com.snaps.common.customui.PrefetchDisableLinearLayoutManager;
import com.snaps.common.utils.log.Dlog;

/**
 * Created by ysjeong on 2017. 1. 16..
 */

public class CustomLinearLayoutManager extends PrefetchDisableLinearLayoutManager {
    private static final String TAG = CustomLinearLayoutManager.class.getSimpleName();
    private boolean isScrollEnabled = true;

    public CustomLinearLayoutManager(Context context) {
        super(context);
    }

    public CustomLinearLayoutManager(Context context, boolean isScrollEnabled) {
        super(context);
        this.isScrollEnabled = isScrollEnabled;
    }

    public CustomLinearLayoutManager(Context context, int orientation, boolean reverseLayout, boolean isScrollEnabled) {
        super(context, orientation, reverseLayout);
        this.isScrollEnabled = isScrollEnabled;
    }

    public CustomLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes, boolean isScrollEnabled) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.isScrollEnabled = isScrollEnabled;
    }

    public CustomLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
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
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }
}
