package com.snaps.mobile.activity.diary.customview;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by ysjeong on 16. 4. 1..
 */
public class SnapsRecyclerView extends RecyclerView {

    private boolean isAddedScrollListener = false;

    private boolean isAddedViewTreeObserver = false;

    public SnapsRecyclerView(Context context) {
        super(context);
        init(context);
    }

    public SnapsRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SnapsRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {}

    public boolean isAddedScrollListener() {
        return isAddedScrollListener;
    }

    public void setIsAddedScrollListener(boolean isAddedScrollListener) {
        this.isAddedScrollListener = isAddedScrollListener;
    }

    public boolean isAddedViewTreeObserver() {
        return isAddedViewTreeObserver;
    }

    public void setIsAddedViewTreeObserver(boolean isAddedViewTreeObserver) {
        this.isAddedViewTreeObserver = isAddedViewTreeObserver;
    }
}
