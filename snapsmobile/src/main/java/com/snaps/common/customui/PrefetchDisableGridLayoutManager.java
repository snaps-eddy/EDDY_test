package com.snaps.common.customui;

import android.content.Context;
import androidx.recyclerview.widget.GridLayoutManager;
import android.util.AttributeSet;

public class PrefetchDisableGridLayoutManager extends GridLayoutManager {
    public PrefetchDisableGridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public PrefetchDisableGridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
        init();
    }

    public PrefetchDisableGridLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
        init();
    }

    private void init() {
        setItemPrefetchEnabled(false);
    }
}
