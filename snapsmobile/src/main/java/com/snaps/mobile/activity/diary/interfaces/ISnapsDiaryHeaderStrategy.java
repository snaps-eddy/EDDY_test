package com.snaps.mobile.activity.diary.interfaces;

import androidx.recyclerview.widget.RecyclerView;
import android.view.ViewGroup;

/**
 * Created by ysjeong on 16. 3. 31..
 */
public interface ISnapsDiaryHeaderStrategy {
    void setHeaderInfo(final RecyclerView.ViewHolder holder);
    RecyclerView.ViewHolder getViewHolder(ViewGroup parent);
    void destoryView();
    void refreshThumbnail();
}
