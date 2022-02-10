package com.snaps.mobile.component;

import android.content.Context;
import android.os.Handler;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;

import com.snaps.mobile.activity.diary.customview.SnapsSuperRecyclerView;
import com.snaps.mobile.interfaces.OnPageScrollListener;

public class ObserveScrollingRecyclerView extends SnapsSuperRecyclerView {
	private OnPageScrollListener listener;
	private boolean isEnableFling = false;

	public ObserveScrollingRecyclerView(Context context) {
		super(context);
		init();
	}

	public ObserveScrollingRecyclerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ObserveScrollingRecyclerView(Context context, AttributeSet attrs,
                                        int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		isEnableFling = true;
		setVerticalScrollBarEnabled(false);
		setHorizontalScrollBarEnabled(false);

        getRecyclerView().addOnScrollListener( scrollListener );
	}
	
	public void setOnScrollListener( OnPageScrollListener listener ) {
		this.listener = listener;
	}

    RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if( listener != null ) listener.onScrollChanged( dx, dy );
        }
    };
	
	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		if( listener != null ) listener.onScrollChanged(l, t, oldl, oldt);
	}

    private Handler titleAnimationHandler;
    private Runnable titleAnimationRunnable;

    public void setEnableFling(boolean enableFling) {
        isEnableFling = enableFling;
        titleAnimationHandler = null;
        titleAnimationRunnable = null;
    }

    public void setTitleAnimation( Handler titleAnimationHandler, Runnable titleAnimationRunnable ) {
        this.titleAnimationHandler = titleAnimationHandler;
        this.titleAnimationRunnable = titleAnimationRunnable;
    }

    public int getMaxScrollPosition() {
        return computeVerticalScrollRange() - computeVerticalScrollExtent();
    }

	public boolean isScrollable() {
        RecyclerView recyclerView = getRecyclerView();
		return recyclerView != null && recyclerView.computeVerticalScrollRange() > recyclerView.computeVerticalScrollExtent();
	}

    public boolean isScrollAtBottom() {
        RecyclerView recyclerView = getRecyclerView();
        return recyclerView != null && recyclerView.computeVerticalScrollRange() - ( recyclerView.computeVerticalScrollExtent() + recyclerView.getScrollY() ) < 1;
    }

    public boolean isScrollAtTop() {
        return getScrollY() < 1;
    }
}
