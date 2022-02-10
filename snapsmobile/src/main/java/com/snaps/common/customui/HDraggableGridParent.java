package com.snaps.common.customui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.snaps.common.customui.dragdrop.DragController;
import com.snaps.common.customui.dragdrop.DropTarget;

import java.util.List;

public abstract class HDraggableGridParent extends ViewGroup implements DropTarget, View.OnTouchListener, View.OnClickListener, View.OnLongClickListener {

	protected Context mContext = null;

	public HDraggableGridParent(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}

	public interface OnRearrangeListener {
		public abstract void onRearrange(int oldIndex, int newIndex);
	}

	public interface OnDragViewClickListener {
		public abstract void onDragViewClick(View view, int position);
	}

	public interface OnDragScrollState {
		public abstract void onDragScrollChanged(int state);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
	}

	public abstract void scrollToIdx(int moveType, int childIdx);

	public abstract void setChildDataList(List<String> childDataList);

	public abstract void setOnRearrangeListener(OnRearrangeListener l);

	public abstract void setOnDragViewClickListener(OnDragViewClickListener l);

	public abstract void setDragController(DragController mDragController);

	public abstract void setSetPadding(int setPadding);

	public abstract void setMaxPage(int maxPage);

	public abstract int getMinPage();

	public abstract void setMinPage(int minPage);

	public abstract int getSpinePage();

	public abstract void setSpinePage(int spinePage);

	public abstract boolean isLimitPageCount();

	public abstract void removeChildDataList(int index);

	public abstract void addChildDataList(int index, String pageIdx);

	public abstract void waitFinishScroll(OnDragScrollState listener);

	public abstract void stopHandler();

	public abstract void setSetchildSize(int count);
}
