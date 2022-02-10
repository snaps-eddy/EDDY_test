package com.snaps.mobile.activity.photoprint.view;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.snaps.common.structure.photoprint.PhotoPrintListItemHolder;
import com.snaps.mobile.activity.photoprint.model.ScrollChangeListener;

/**
 * Created by songhw on 2017. 3. 13..
 */

public class ScrollObserveRecyclerView extends RecyclerView {
    private ScrollChangeListener listener;

    private int minScrollPosition = -1;

    private boolean doingSelectModeMinScroll = false;


    public ScrollObserveRecyclerView(Context context) {
        super(context);
    }

    public ScrollObserveRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollObserveRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setListener( ScrollChangeListener listener ) {
        this.listener = listener;
    }

    public void setMinScrollPosition( int value ) { minScrollPosition = value; }
    public void disableMinScrollPosition() { minScrollPosition = -1; }

    @Override
    public void computeScroll() {
        View firstView = getChildAt(0);
        if( firstView != null && minScrollPosition > 0 ) {
            if( ((PhotoPrintListItemHolder) getChildViewHolder(firstView)).isHeaderOrFooterView() && firstView.getBottom() > minScrollPosition ) {
                doingSelectModeMinScroll = true;
                scrollBy( 0, firstView.getBottom() - minScrollPosition );
            }
        }
        super.computeScroll();
    }

    public void setSelectModeMinScrollFalse() { doingSelectModeMinScroll = false; }

    public boolean isDoingSelectModeMinScroll() { return doingSelectModeMinScroll; }

    @Override
    public void onScrolled(int dx, int dy) {
        super.onScrolled(dx, 0);

        if( listener != null )
            listener.onScrolled( dx, dy );
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);

        if( listener != null )
            listener.onScrollStateChanged( state );
    }
}
