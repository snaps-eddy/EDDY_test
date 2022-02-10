package com.snaps.mobile.activity.google_style_image_selector.ui.custom;

/**
 * Created by ysjeong on 16. 4. 19..
 */

import android.content.Context;
import android.graphics.Rect;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.snaps.mobile.R;

public class ImageSelectFragmentPhotoBaseSpacingItemDecoration extends RecyclerView.ItemDecoration {
    private int spaceTop;
    private int spanCount = 0;

    public ImageSelectFragmentPhotoBaseSpacingItemDecoration(Context context, int margin) {
        init(context, margin);
    }

    public ImageSelectFragmentPhotoBaseSpacingItemDecoration(Context context, int margin, int spanCount) {
        this.spanCount = spanCount;
        init(context, margin);
    }

    private void init(Context context, int topSpace) {
        this.spaceTop = topSpace >= 0 ? topSpace : (int) context.getResources().getDimension(R.dimen.image_select_fragment_base_margin);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {

        int position = parent.getChildAdapterPosition(view); // item position
        if (position == 0) return;

        if (view == null) return;

        /**
         * StaggeredLayout일 경우 적용
         */
        GridLayoutManager.LayoutParams layoutParams = (GridLayoutManager.LayoutParams) view.getLayoutParams();
        if(spanCount == 0) {

            if (layoutParams.getSpanIndex() == 0) {
                outRect.set(0, spaceTop, 0, 0);
            } else {
                outRect.set(spaceTop, spaceTop, 0, 0);
            }
        }
        else {
            outRect.top = spaceTop;
            if (layoutParams.getSpanSize() == 1) {
                if (layoutParams.getSpanIndex() == 0) {
                    outRect.right = spaceTop / 2;
                } else if (layoutParams.getSpanIndex() == spanCount - 1) {
                    outRect.left = spaceTop / 2;
                } else {
                    outRect.right = spaceTop / 2;
                    outRect.left = spaceTop / 2;
                }
            }
        }
    }
}
