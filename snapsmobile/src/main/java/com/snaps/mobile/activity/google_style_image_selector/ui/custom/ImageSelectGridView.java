package com.snaps.mobile.activity.google_style_image_selector.ui.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by ysjeong on 2016. 12. 5..
 */

public class ImageSelectGridView extends GridView{
    public ImageSelectGridView(Context context) {
        super(context);
    }

    public ImageSelectGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageSelectGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public int computeVerticalScrollOffset() {
        return super.computeVerticalScrollOffset();
    }
}
