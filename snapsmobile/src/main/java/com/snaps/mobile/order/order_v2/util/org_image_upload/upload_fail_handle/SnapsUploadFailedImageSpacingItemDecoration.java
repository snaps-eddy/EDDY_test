package com.snaps.mobile.order.order_v2.util.org_image_upload.upload_fail_handle;

/**
 * Created by ysjeong on 16. 4. 19..
 */

import android.content.Context;
import android.graphics.Rect;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.snaps.mobile.R;

public class SnapsUploadFailedImageSpacingItemDecoration extends RecyclerView.ItemDecoration {
    private int spaceTop;

    private int columnCount;

    public SnapsUploadFailedImageSpacingItemDecoration(Context context, int margin, int columnCount) {
        init(context, margin, columnCount);
    }

    private void init(Context context, int topSpace, int columnCount) {
        this.spaceTop = topSpace >= 0 ? topSpace : (int) context.getResources().getDimension(R.dimen.upload_failed_image_adapter_margin);
        this.columnCount = columnCount;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {

        int position = parent.getChildAdapterPosition(view); // item position

        if (view == null) return;
        GridLayoutManager.LayoutParams layoutParams = (GridLayoutManager.LayoutParams) view.getLayoutParams();

        int topMargin = position < columnCount ?  0 : spaceTop;

        if (layoutParams.getSpanIndex() == 0) {
            outRect.set(0, topMargin, 0, 0);
        } else {
            outRect.set(spaceTop, topMargin, 0, 0);
        }
    }
}
