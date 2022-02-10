package com.snaps.common.utils.ui;

import androidx.recyclerview.widget.RecyclerView;
import android.widget.AbsListView;

import com.snaps.common.utils.log.Dlog;

public abstract class OnVerticalScrollListener extends RecyclerView.OnScrollListener {
    private static final String TAG = OnVerticalScrollListener.class.getSimpleName();

    @Override
    public final void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        try {
            if (!recyclerView.canScrollVertically(-1)) {
                onScrolledToTop();
            } else if (!recyclerView.canScrollVertically(1)) {
                onScrolledToBottom();
            }
            if (dy < 0) {
                onScrolledUp(dy);
            } else if (dy > 0) {
                onScrolledDown(dy);
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);

        if (newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
            onScrollStop();
        }
    }

    public void onScrolledUp(int dy) {
        onScrolledUp();
    }

    public void onScrolledDown(int dy) {
        onScrolledDown();
    }

    public void onScrolledUp() {}

    public void onScrolledDown() {}

    public void onScrolledToTop() {}

    public void onScrolledToBottom() {}

    public void onScrollStop() {}
}