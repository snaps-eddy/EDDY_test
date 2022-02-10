package com.snaps.mobile.product_native_ui.ui.adapter;

/**
 * Created by ysjeong on 16. 4. 19..
 */

import android.content.Context;
import android.graphics.Rect;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.snaps.common.utils.ui.UIUtil;

public class SnapsProductListGridSpacingItemDecoration extends RecyclerView.ItemDecoration {
    private int spaceLeft;
    private int spaceRight;
    private int spaceCenter;

    public SnapsProductListGridSpacingItemDecoration(Context context) {
        spaceLeft = UIUtil.convertDPtoPX(context, 16);
        spaceCenter = UIUtil.convertDPtoPX(context, 8);
        spaceRight = UIUtil.convertDPtoPX(context, 16);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view); // item position
        if (position == 0) return;

        //왼쪽 아이템
        if ((position - 1) % 2 != 1) {
            if (outRect.left != spaceLeft)
                outRect.left = spaceLeft;
        } else { //오른쪽 아이템
            if (outRect.left != spaceCenter)
                outRect.left = spaceCenter;

            if (outRect.right != spaceRight)
                outRect.right = spaceRight;
        }
    }
}
