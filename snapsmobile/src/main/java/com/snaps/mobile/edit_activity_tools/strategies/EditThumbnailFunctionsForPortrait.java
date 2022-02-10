package com.snaps.mobile.edit_activity_tools.strategies;

import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.View;

import com.snaps.mobile.edit_activity_tools.customview.EditActivityThumbnailRecyclerView;
import com.snaps.mobile.edit_activity_tools.interfaces.IEditThumbnailBehaviorByOrientation;
import com.snaps.mobile.edit_activity_tools.utils.EditActivityThumbnailUtils;

/**
 * Created by ysjeong on 16. 5. 11..
 */
public class EditThumbnailFunctionsForPortrait implements IEditThumbnailBehaviorByOrientation {

    @Override
    public void scrollToIdx(EditActivityThumbnailRecyclerView recyclerView, int moveType, int itemTotalCnt, int childIdx) {
        if (recyclerView == null || recyclerView.getChildCount() < 1) return;

        childIdx = Math.min(itemTotalCnt - 1, Math.max(0, childIdx));

        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int firstItemPos = layoutManager.findFirstVisibleItemPosition();

        boolean isSmoothScroll = Math.abs(childIdx - firstItemPos) == 2 || Math.abs(childIdx - firstItemPos) == 0;
        if (isSmoothScroll && moveType == EditActivityThumbnailUtils.PAGE_MOVE_TYPE_NEXT) {
            recyclerView.smoothScrollToPosition(Math.min(itemTotalCnt - 1, childIdx + 1));
        } else if (isSmoothScroll && moveType == EditActivityThumbnailUtils.PAGE_MOVE_TYPE_PREV) {
            recyclerView.smoothScrollToPosition(Math.max(0, childIdx - 1));
        } else {
            View item = recyclerView.getChildAt(0);
            int offset = 0;
            if (item != null)
                offset = (recyclerView.getMeasuredWidth() / 2) - (recyclerView.getChildAt(0).getMeasuredWidth() / 2);
            else
                offset = (recyclerView.getMeasuredWidth() / 3);
            layoutManager.scrollToPositionWithOffset(childIdx, offset);
        }
    }
}
