package com.snaps.mobile.activity.selectimage.helper;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

/**
 * Created by ifunbae on 16. 4. 29..
 */
public interface OnItemClickListener {
    void onItemClick(RecyclerView recyclerView, View v, int position);
    void onItemLongClick(RecyclerView RecyclerView, View v, int position);
}
