package com.snaps.common.spc.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by ysjeong on 2017. 10. 19..
 */

public abstract class SnapsBaseCardTextView extends RelativeLayout {

    public SnapsBaseCardTextView(Context context) {
        super(context);
    }

    public SnapsBaseCardTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SnapsBaseCardTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public abstract void text(String text);
}
