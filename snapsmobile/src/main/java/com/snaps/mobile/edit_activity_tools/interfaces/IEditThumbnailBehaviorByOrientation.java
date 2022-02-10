package com.snaps.mobile.edit_activity_tools.interfaces;

import com.snaps.mobile.edit_activity_tools.customview.EditActivityThumbnailRecyclerView;

/**
 * Created by ysjeong on 16. 5. 11..
 */
public interface IEditThumbnailBehaviorByOrientation {
	void scrollToIdx(EditActivityThumbnailRecyclerView recyclerView, int moveType, int itemTotalCnt, int childIdx);
}
