package com.snaps.common.customui;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.util.AttributeSet;

public class PrefetchDisableLinearLayoutManager extends LinearLayoutManager {

    public PrefetchDisableLinearLayoutManager(Context context) {
        super(context);
        init();
    }

    public PrefetchDisableLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
        init();
    }

    public PrefetchDisableLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setItemPrefetchEnabled(false);
    }
}
