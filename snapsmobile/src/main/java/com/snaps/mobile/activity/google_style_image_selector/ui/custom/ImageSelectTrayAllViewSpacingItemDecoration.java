package com.snaps.mobile.activity.google_style_image_selector.ui.custom;

/**
 * Created by ysjeong on 16. 4. 19..
 */

import android.content.Context;
import android.graphics.Rect;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.snaps.common.utils.ui.UIUtil;

public class ImageSelectTrayAllViewSpacingItemDecoration extends RecyclerView.ItemDecoration {
    private int lastBottomSpace;

    public ImageSelectTrayAllViewSpacingItemDecoration(Context context) {
        lastBottomSpace = UIUtil.convertDPtoPX(context, 80);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view); // item position

        if (position == state.getItemCount() - 1) {
            if (outRect.bottom != lastBottomSpace)
                outRect.bottom = lastBottomSpace;
        }
    }
}
