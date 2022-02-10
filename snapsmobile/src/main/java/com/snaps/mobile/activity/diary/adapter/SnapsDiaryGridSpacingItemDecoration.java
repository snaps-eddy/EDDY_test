package com.snaps.mobile.activity.diary.adapter;

/**
 * Created by ysjeong on 16. 4. 19..
 */

import android.graphics.Rect;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

public class SnapsDiaryGridSpacingItemDecoration extends RecyclerView.ItemDecoration {
    private int space;

    public SnapsDiaryGridSpacingItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {

        int position = parent.getChildAdapterPosition(view); // item position

        if (position == 0) return;

        if (outRect.left != space && (position - 1) % 3 != 0)
            outRect.left = space;
        if (outRect.bottom != space)
            outRect.bottom = space;
    }
}
