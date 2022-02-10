package com.snaps.mobile.edit_activity_tools.interfaces;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by ysjeong on 16. 5. 30..
 */
public interface IEditThumbnailItemTouchHelperAdapter {


    enum Type{LIST,GRID}

    /**
     * Called when an item has been dragged far enough to trigger a move. This is called every time
     * an item is shifted, and <strong>not</strong> at the end of a "drop" event.<br/>
     * <br/>
     * Implementations should call {@link RecyclerView.Adapter#notifyItemMoved(int, int)} after
     * adjusting the underlying data to reflect this move.
     *
     * @param fromPosition The start position of the moved item.
     * @param toPosition   Then resolved position of the moved item.
     * @return True if the item was moved to the new adapter position.
     *
     * @see RecyclerView#getAdapterPositionFor(RecyclerView.ViewHolder)
     * @see RecyclerView.ViewHolder#getAdapterPosition()
     */
    boolean onItemMove(int fromPosition, int toPosition);


    /**
     * Called when an item has been dismissed by a swipe.<br/>
     * <br/>
     * Implementations should call {@link RecyclerView.Adapter#notifyItemRemoved(int)} after
     * adjusting the underlying data to reflect this removal.
     *
     * @param position The position of the item dismissed.
     *
     * @see RecyclerView#getAdapterPositionFor(RecyclerView.ViewHolder)
     * @see RecyclerView.ViewHolder#getAdapterPosition()
     */
    void onItemDismiss(int position);

    /**
     * EditActivityThumbItemTouchHelperCallback의 메서드를 callback으로 보내 주기 위해
     * @param viewHolder
     * @param actionState
     */
    void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState);
}
