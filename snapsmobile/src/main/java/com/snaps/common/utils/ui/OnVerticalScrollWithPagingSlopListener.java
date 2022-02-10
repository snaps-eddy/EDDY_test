package com.snaps.common.utils.ui;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.ViewConfiguration;

import com.snaps.common.utils.log.Dlog;

public abstract class OnVerticalScrollWithPagingSlopListener extends OnVerticalScrollListener {
	private static final String TAG = OnVerticalScrollWithPagingSlopListener.class.getSimpleName();

	private int pagingTouchSlop = 0;

	private int moveY;

	public OnVerticalScrollWithPagingSlopListener(Context context) {
		try {
			pagingTouchSlop = ViewConfiguration.get(context).getScaledPagingTouchSlop();//UIUtil.convertDPtoPX(context, ViewConfiguration.get(context).getScaledPagingTouchSlop());
		} catch (Exception e) {
			Dlog.e(TAG, e);
			pagingTouchSlop = 50;
		}
	}

	@Override
	public final void onScrollStateChanged(RecyclerView recyclerView, int newState) {
		super.onScrollStateChanged(recyclerView, newState);

		if (newState == RecyclerView.SCROLL_STATE_IDLE) {
			moveY = 0;
		}
	}

	@Override
	public final void onScrolledUp(int dy) {
		moveY = moveY < 0 ? moveY + dy : dy;
		if (moveY < -pagingTouchSlop) {
			onScrolledUp();
		}
	}

	@Override
	public final void onScrolledDown(int dy) {
		moveY = moveY > 0 ? moveY + dy : dy;
		if (moveY > pagingTouchSlop) {
			onScrolledDown();
		}
	}
}
