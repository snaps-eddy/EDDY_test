package com.snaps.common.structure.photoprint;

import android.graphics.Rect;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

/**
 * Created by songhw on 2017. 3. 13..
 */

public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

    private int left, right, top, bottom, header, footer;

    public GridSpacingItemDecoration( int leftSpacing, int rightSpacing, int topSpacing, int bottomSpacing, int header, int footer ) {
        left = leftSpacing;
        right = rightSpacing;
        top = topSpacing;
        bottom = bottomSpacing;
        this.header = header;
        this.footer = footer;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left = left;
        outRect.right = right;
        outRect.top = top;
        outRect.bottom = bottom;
    }
}