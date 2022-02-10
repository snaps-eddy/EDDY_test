package com.snaps.mobile.activity.diary.interfaces;

import com.snaps.mobile.activity.diary.recoder.SnapsDiaryListItem;

/**
 * Created by ysjeong on 16. 3. 14..
 */
public interface IOnSnapsDiaryItemSelectedListener {
    void onDiaryItemSelected(final SnapsDiaryListItem item, final int position, final boolean isDetailView);
}
